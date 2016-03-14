/*
 *  Copyright 2006 The National Library of New Zealand
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.webcurator.core.targets;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Hibernate;
import org.springframework.context.MessageSource;
import org.webcurator.auth.AuthorityManager;
import org.webcurator.core.common.EnvironmentFactory;
import org.webcurator.core.common.WCTTreeSet;
import org.webcurator.core.exceptions.WCTRuntimeException;
import org.webcurator.core.notification.InTrayManager;
import org.webcurator.core.notification.MessageType;
import org.webcurator.core.permissionmapping.PermissionMappingStrategy;
import org.webcurator.core.scheduler.TargetInstanceManager;
import org.webcurator.core.util.Auditor;
import org.webcurator.core.util.AuthUtil;
import org.webcurator.core.util.DateUtils;
import org.webcurator.domain.AnnotationDAO;
import org.webcurator.domain.Pagination;
import org.webcurator.domain.SiteDAO;
import org.webcurator.domain.TargetDAO;
import org.webcurator.domain.TargetInstanceDAO;
import org.webcurator.domain.model.auth.Agency;
import org.webcurator.domain.model.auth.Privilege;
import org.webcurator.domain.model.auth.User;
import org.webcurator.domain.model.core.AbstractTarget;
import org.webcurator.domain.model.core.Annotation;
import org.webcurator.domain.model.core.BusinessObjectFactory;
import org.webcurator.domain.model.core.GroupMember;
import org.webcurator.domain.model.core.Permission;
import org.webcurator.domain.model.core.Schedule;
import org.webcurator.domain.model.core.Seed;
import org.webcurator.domain.model.core.Target;
import org.webcurator.domain.model.core.TargetGroup;
import org.webcurator.domain.model.core.TargetInstance;
import org.webcurator.domain.model.dto.AbstractTargetDTO;
import org.webcurator.domain.model.dto.GroupMemberDTO;
import org.webcurator.domain.model.dto.GroupMemberDTO.SAVE_STATE;
import org.webcurator.domain.model.dto.PermissionSeedDTO;
import org.webcurator.ui.common.Constants;
import org.webcurator.ui.target.TargetEditorContext;

/**
 * The implementation of the TargetManager interface.
 * 
 * @see TargetManager
 * @author bbeaumont
 */
public class TargetManagerImpl implements TargetManager {
	/** The logger for the Target Manager */
	private static Log log = LogFactory.getLog(TargetManagerImpl.class);
	/** The DAO for saving targets. */
	private TargetDAO targetDao = null;;
	/** The DAO for loading sites & permissions */
	private SiteDAO siteDao = null;
	/** The DAO for loading/saving annotations */
	private AnnotationDAO annotationDAO = null;
	/** The DAO for loading/saving Target Instances */
	private TargetInstanceDAO targetInstanceDao = null;
	/** The Authority Manager */
	private AuthorityManager authMgr = null;
	/** The TargetInstanceManager */
	private TargetInstanceManager instanceManager = null;
	/** The IntrayManager */
	private InTrayManager intrayManager = null;
	/** The Auditor */
	private Auditor auditor;
	/** Message Source */
	private MessageSource messageSource;
	/** Business object factory */
	private BusinessObjectFactory businessObjectFactory;

	/** The list of valid sub-group parent types */
	private WCTTreeSet subGroupParentTypesList = null;

	/** Whether to send notifications when group membership is updated. */
	private boolean sendGroupUpdateNotifications = true;
	private boolean allowMultiplePrimarySeeds = true;

	private String subGroupTypeName = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.webcurator.core.targets.TargetManager#getAllowMultiplePrimarySeeds()
	 */
	public boolean getAllowMultiplePrimarySeeds() {
		return allowMultiplePrimarySeeds;
	}

	public void setAllowMultiplePrimarySeeds(boolean allowMultiplePrimarySeeds) {
		this.allowMultiplePrimarySeeds = allowMultiplePrimarySeeds;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.webcurator.core.targets.TargetManager#save(org.webcurator.domain. model.core.Target)
	 */
	public void save(Target aTarget, List<GroupMemberDTO> parents) {

		boolean newSchedulesAddedByNonOwner = false;
		boolean wasHarvestNowSelected = aTarget.isHarvestNow();

		// If dirty and the current state is approved, we need to change the
		// state back to nominated.
		if (aTarget.isDirty() && aTarget.getOriginalState() == Target.STATE_APPROVED
				&& !authMgr.hasPrivilege(aTarget, Privilege.APPROVE_TARGET)) {
			log.debug("Target state changed to nominated due to changes");
			aTarget.changeState(Target.STATE_NOMINATED);
		}

		// Track a change into the approved state.
		if ((aTarget.getOriginalState() == Target.STATE_PENDING || aTarget.getOriginalState() == Target.STATE_REINSTATED || aTarget
				.getOriginalState() == Target.STATE_NOMINATED) && aTarget.getState() == Target.STATE_APPROVED) {
			aTarget.setSelectionDate(new Date());
		}

		int numActiveTIsPrevious = targetInstanceDao.countActiveTIsForTarget(aTarget.getOid());

		// Deal with removed schedules
		for (Schedule schedule : aTarget.getRemovedSchedules()) {
			targetInstanceDao.deleteScheduledInstances(schedule);
			schedule.setTargetInstances(new HashSet<TargetInstance>());
			targetInstanceDao.save(schedule);
		}

		// Deal with new schedules
		List<Schedule> newSchedules = new LinkedList<Schedule>();
		for (Schedule schedule : aTarget.getSchedules()) {
			if(schedule.isNew()) {
				// Record the schedule for auditing after save.
				newSchedules.add(schedule);

				// Flag that this schedule is going to be processed (only used when Target saved in Annotations tab)
				schedule.setSavedInThisSession(true);
				
				// Send a notification if the schedule owner is not the same as
				// the owner of the target.
				if (!schedule.getOwningUser().equals(aTarget.getOwningUser())) {
					newSchedulesAddedByNonOwner = true;
				}
			}
			else {
				// If this schedule is not new and has already been saved via adding an annotation then it is in a buggy state.
				if(schedule.isSavedInThisSession()){
					targetInstanceDao.deleteScheduledInstances(schedule);
					schedule.setTargetInstances(new HashSet<TargetInstance>());
					targetInstanceDao.save(schedule);
				}
			}
		}
		
		if(aTarget.getState()==Target.STATE_COMPLETED && newSchedules.size()!=0) {
			aTarget.changeState(Target.STATE_APPROVED);
		}
		
		// Load the original target so we can do auditing afterwards.
		AbstractTargetDTO originalTarget = null;
		if (!aTarget.isNew()) {
			originalTarget = targetDao.loadAbstractTargetDTO(aTarget.getOid());
			// targetDao.evict(originalTarget);
		}

		/* ---------------------------------------------------------------- */
		/* Deal with the scheduling state changes */
		/* ---------------------------------------------------------------- */

		// Get the original parents
		Set<AbstractTargetDTO> origParents = Collections.EMPTY_SET;
		if (!aTarget.isNew()) {
			origParents = targetDao.getAncestorDTOs(aTarget.getOid());
		}

		// Save the target.
		log.debug("Saving Target");
		int originalState = aTarget.getOriginalState();
		targetDao.save(aTarget, parents);
		
		/* ---------------------------------------------------------------- */
		/* Save the annotations */
		/* ---------------------------------------------------------------- */

		// Update the OIDs for the target.
		for (Annotation anno : aTarget.getAnnotations()) {
			anno.setObjectOid(aTarget.getOid());
		}
		// Save the annotations
		annotationDAO.saveAnnotations(aTarget.getAnnotations());
		annotationDAO.deleteAnnotations(aTarget.getDeletedAnnotations());

		aTarget = targetDao.reloadTarget(aTarget.getOid());
		log.debug("End of Save Target");

		/* ---------------------------------------------------------------- */
		/* Perform post-save auditing */
		/* ---------------------------------------------------------------- */
		if (originalState != aTarget.getState()) {
			String newState = messageSource.getMessage("target.state_" + aTarget.getState(), null, Locale.getDefault());
			auditor.audit(Target.class.getName(), aTarget.getOid(), Auditor.ACTION_TARGET_STATE_CHANGE,
					"Target " + aTarget.getName() + " has changed into state '" + newState + "'");
		}

		for (Schedule schedule : newSchedules) {
			auditor.audit(Schedule.class.getName(), schedule.getOid(), Auditor.ACTION_NEW_SCHEDULE,
					"New Schedule Created on Target " + aTarget.getName());
		}

		if (originalTarget != null) {
			auditor.audit(Target.class.getName(), aTarget.getOid(), Auditor.ACTION_UPDATE_TARGET, "Target " + aTarget.getName()
					+ " has been updated");
			if (!originalTarget.getOwnerOid().equals(aTarget.getOwningUser().getOid())) {
				auditor.audit(Target.class.getName(), aTarget.getOid(), Auditor.ACTION_TARGET_CHANGE_OWNER,
						"Target " + aTarget.getName() + " has been given to " + aTarget.getOwningUser().getNiceName());
			}

			if (originalTarget.getProfileOid()!=null && aTarget.getProfile() != null && !originalTarget.getProfileOid().equals(aTarget.getProfile().getOid())) {
				auditor.audit(Target.class.getName(), aTarget.getOid(), Auditor.ACTION_TARGET_CHANGE_PROFILE,
						"Target " + aTarget.getName() + " is now using profile " + aTarget.getProfile().getName());
			}
		}

		/* ---------------------------------------------------------------- */
		/* Check state changes and task/notification creation */
		/* ---------------------------------------------------------------- */
		if (originalState != aTarget.getState()) {
			if (aTarget.getState() == Target.STATE_NOMINATED) {
				intrayManager.generateTask(Privilege.APPROVE_TARGET, MessageType.TASK_APPROVE_TARGET, aTarget);
			}

			if (aTarget.getState() == Target.STATE_APPROVED || aTarget.getState() == Target.STATE_REJECTED) {
				intrayManager.deleteTask(aTarget.getOid(), aTarget.getResourceType(), MessageType.TASK_APPROVE_TARGET);
			}

			if (aTarget.getState() == Target.STATE_APPROVED && aTarget.isRunOnApproval()) {
				TargetInstance ti = new TargetInstance();
				ti.setTarget(aTarget);
				ti.setSchedule(null);
				ti.setScheduledTime(new Date());
				ti.setOwner(aTarget.getOwner());
				ti.setUseAQA(aTarget.isUseAQA());
				ti.setAllowOptimize(aTarget.isAllowOptimize());
				targetInstanceDao.save(ti);
			}
		}
		//Deal with harvest now
		if (aTarget.getState() == Target.STATE_APPROVED && wasHarvestNowSelected) {
			TargetInstance ti = new TargetInstance();
			ti.setTarget(aTarget);
			ti.setSchedule(null);
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.MINUTE, 5);
			ti.setScheduledTime(cal.getTime());
			ti.setOwner(aTarget.getOwner());
			ti.setUseAQA(aTarget.isUseAQA());
			ti.setAllowOptimize(aTarget.isAllowOptimize());
			targetInstanceDao.save(ti);
		}

		if (originalTarget != null) {
			if (!originalTarget.getOwnerOid().equals(aTarget.getOwningUser().getOid())) {
				// carry out notification of the target delegation
				intrayManager.generateNotification(aTarget.getOwningUser().getOid(), MessageType.CATEGORY_MISC,
						MessageType.DELEGATE_TARGET, aTarget);

				// If ownership was transferred by someone other than the original owner, let the original
				// owner know.
				Long remoteUserObjectOid = AuthUtil.getRemoteUserObject().getOid();
				if (originalTarget.getOwnerOid() != remoteUserObjectOid) {
					intrayManager.generateNotification(originalTarget.getOwnerOid(), MessageType.CATEGORY_MISC,
							MessageType.TRANSFER_TARGET, aTarget);
				}
			}
		}

		// Stay in a schedulable state. Process all schedules
		if (aTarget.isSchedulable()) {
			log.debug("Staying in Schedulable State - scheduling new schedules");
			for (Schedule schedule : aTarget.getSchedules()) {
				// targetInstances.addAll(createTargetInstances(aTarget, schedule, false));
				processSchedule(schedule);
			}
		} else {
			// Change to an unschedulable state - all scheduled target instances
			// must be removed.
			unschedule(aTarget);
		}

		// if this target record is in original state 'approved' and the state
		// is not changing and the target has no active TIs remaining (scheduled,
		// queued, running, paused, stopping) and updates to the schedules have
		// caused the removal of all scheduled TIs for this target then set the
		// status of the target to 'complete'.
		if (numActiveTIsPrevious > 0 && targetInstanceDao.countActiveTIsForTarget(aTarget.getOid()) == 0
				&& aTarget.getOriginalState() == Target.STATE_APPROVED && aTarget.getState() == Target.STATE_APPROVED) {
			aTarget.changeState(Target.STATE_COMPLETED);
		}

		// Send notification if someone other than the owner has added
		// a schedule to this target.
		if (newSchedulesAddedByNonOwner) {
			intrayManager.generateNotification(aTarget.getOwningUser().getOid(), MessageType.CATEGORY_MISC,
					MessageType.TARGET_SCHEDULE_ADDED, aTarget);
		}

		// We may need update the state of some of our parents.
		updateTargetGroupStatus(aTarget);

		// We also need to handle the updating of any groups that we used to
		// belong to.
		if (parents != null) {
			for (GroupMemberDTO dto : parents) {
				if (dto.getSaveState() == SAVE_STATE.DELETED) {
					updateTargetGroupStatus(targetDao.loadGroup(dto.getParentOid()));
				}
			}
		}

		// Propagate the group events.
		if (parents != null) {
			for (GroupMemberDTO dto : parents) {
				switch (dto.getSaveState()) {
				case NEW:
					TargetGroup grp = targetDao.loadGroup(dto.getParentOid());
					GroupEventPropagator gep = new GroupEventPropagator(this, instanceManager, grp, aTarget);
					gep.runEventChain();
					break;
				case DELETED:
					TargetGroup grp2 = targetDao.loadGroup(dto.getParentOid());
					MembersRemovedEventPropagator mrep = new MembersRemovedEventPropagator(this, instanceManager, grp2, aTarget);
					mrep.runEventChain();
					break;
				}
			}

		}

		// Send emails if necessary
		if (sendGroupUpdateNotifications && AuthUtil.getRemoteUserObject() != null) {
			String userName = AuthUtil.getRemoteUserObject().getNiceName();

			// Get the new set of parents.
			Set<AbstractTargetDTO> newParents = targetDao.getAncestorDTOs(aTarget.getOid());

			// Determine the added parents and removed parents.
			Set<AbstractTargetDTO> addedParents = new HashSet<AbstractTargetDTO>(newParents);
			addedParents.removeAll(origParents);
			Set<AbstractTargetDTO> removedParents = new HashSet<AbstractTargetDTO>(origParents);
			removedParents.removeAll(newParents);

			HashMap<Long, GroupChangeNotification> changes = getChanges(addedParents, removedParents);

			generateChangeMessageNotifications(aTarget, userName, changes);
		}
	}

	private static class GroupChangeNotification {
		private Long userOid;
		private Set<AbstractTargetDTO> addedTo = new TreeSet<AbstractTargetDTO>(new AbstractTargetDTO.NameComparator());
		private Set<AbstractTargetDTO> removedFrom = new TreeSet<AbstractTargetDTO>(new AbstractTargetDTO.NameComparator());
	}

	public void save(Target aTarget) {
		// Save the target.
		save(aTarget, null);
	}

	/**
	 * Unschedule the target by deleting all scheduled instances related to its schedules.
	 * 
	 * @param aTarget
	 */
	private void unschedule(AbstractTarget aTarget) {
		targetInstanceDao.deleteScheduledInstances(aTarget);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.webcurator.core.targets.TargetManager#save(org.webcurator.domain. model.core.TargetGroup)
	 */
	public void save(TargetGroup aTargetGroup) {
		save(aTargetGroup, null);
	}

	public void save(TargetGroup aTargetGroup, List<GroupMemberDTO> parents) {

		// First check if the TargetGroup type has changed from many-sip
		// to one-sip or vice versa. If so, unschedule the group as the
		// scheduling mechanism is entirely different.
		boolean sipTypeChanged = false;
		if (!aTargetGroup.isNew()) {
			Integer sipType = targetDao.loadPersistedGroupSipType(aTargetGroup.getOid());
			if (sipType != null && sipType.intValue() != aTargetGroup.getSipType()) {
				sipTypeChanged = true;
			}
		}

		// Deal with removed schedules
		for (Schedule schedule : aTargetGroup.getRemovedSchedules()) {
			log.debug("Removing schedule: " + schedule.getCronPattern());
			targetInstanceDao.deleteScheduledInstances(schedule);
		}

		// Load the original target group so we can do auditing afterwards.
		AbstractTargetDTO originalTargetGroup = null;
		if (!aTargetGroup.isNew()) {
			originalTargetGroup = targetDao.loadAbstractTargetDTO(aTargetGroup.getOid());
		}

		List<GroupMemberDTO> newChildren = aTargetGroup.getNewChildren();
		Set<Long> removedChildren = aTargetGroup.getRemovedChildren();

		/* ---------------------------------------------------------------- */
		/* Deal with the scheduling state changes */
		/* ---------------------------------------------------------------- */

		// Get the original parents
		Set<AbstractTargetDTO> origParents = Collections.EMPTY_SET;
		if (!aTargetGroup.isNew()) {
			origParents = targetDao.getAncestorDTOs(aTargetGroup.getOid());
		}

		// Save the TargetGroup to the database, along with all the
		// children.
		int originalState = aTargetGroup.getOriginalState();
		targetDao.save(aTargetGroup, true, parents);

		// Reload the target group to get all of its children.
		TargetGroup reloaded = targetDao.reloadTargetGroup(aTargetGroup.getOid());
		if (sipTypeChanged) {
			unschedule(reloaded);
		}
		updateTargetGroupStatus(reloaded);

		// We also need to handle the updating of any groups that we used to belong to.
		if (parents != null) {
			for (GroupMemberDTO dto : parents) {
				if (dto.getSaveState() == SAVE_STATE.DELETED) {
					updateTargetGroupStatus(targetDao.loadGroup(dto.getParentOid()));
				}
			}
		}

		/* ---------------------------------------------------------------- */
		/* Perform post-save auditing */
		/* ---------------------------------------------------------------- */
		if (originalState != aTargetGroup.getState()) {
			auditor.audit(TargetGroup.class.getName(), aTargetGroup.getOid(), Auditor.ACTION_TARGET_GROUP_STATE_CHANGE,
					"Target Group " + aTargetGroup.getName() + " has changed into state '" + aTargetGroup.getState() + "'");
		}

		if (originalTargetGroup != null) {
			auditor.audit(TargetGroup.class.getName(), aTargetGroup.getOid(), Auditor.ACTION_UPDATE_TARGET_GROUP, "Target Group "
					+ aTargetGroup.getName() + " has been updated");
			if (!originalTargetGroup.getOwnerOid().equals(aTargetGroup.getOwningUser().getOid())) {
				auditor.audit(TargetGroup.class.getName(), aTargetGroup.getOid(), Auditor.ACTION_TARGET_GROUP_CHANGE_OWNER,
						"Target Group " + aTargetGroup.getName() + " has been given to "
								+ aTargetGroup.getOwningUser().getNiceName());
			}

			if (aTargetGroup.getProfile()!=null && !originalTargetGroup.getProfileOid().equals(aTargetGroup.getProfile().getOid())) {
				auditor.audit(TargetGroup.class.getName(), aTargetGroup.getOid(), Auditor.ACTION_TARGET_GROUP_CHANGE_PROFILE,
						"Target Group " + aTargetGroup.getName() + " is now using profile " + aTargetGroup.getProfile().getName());
			}
		} else {
			auditor.audit(TargetGroup.class.getName(), aTargetGroup.getOid(), Auditor.ACTION_NEW_TARGET_GROUP, "Target Group "
					+ aTargetGroup.getName() + " has been created");
		}

		// Adjust the new/removed lists to make ignore new members that were
		// also removed (since these were never persisted).
		List<AbstractTarget> newMembers = new LinkedList<AbstractTarget>();
		for (GroupMemberDTO dto : newChildren) {
			Long childOid = dto.getChildOid();
			if (removedChildren.contains(childOid)) {
				removedChildren.remove(childOid);
			} else {
				newMembers.add(loadAbstractTarget(childOid));
			}
		}

		// Create a list of the targets that have been removed.
		List<AbstractTarget> removedTargets = new LinkedList<AbstractTarget>();
		for (Long childOid : removedChildren) {
			removedTargets.add(loadAbstractTarget(childOid));
		}

		GroupEventPropagator gep = new GroupEventPropagator(this, instanceManager, reloaded, newMembers);
		gep.runEventChain();

		MembersRemovedEventPropagator mrep = new MembersRemovedEventPropagator(this, instanceManager, reloaded, removedTargets);
		mrep.runEventChain();

		// Propagate the group events for parents.
		if (parents != null) {
			for (GroupMemberDTO dto : parents) {
				switch (dto.getSaveState()) {
				case NEW:
					TargetGroup grp = targetDao.loadGroup(dto.getParentOid());
					gep = new GroupEventPropagator(this, instanceManager, grp, reloaded);
					gep.runEventChain();
					break;
				case DELETED:
					TargetGroup grp2 = targetDao.loadGroup(dto.getParentOid());
					mrep = new MembersRemovedEventPropagator(this, instanceManager, grp2, reloaded);
					mrep.runEventChain();
					break;
				default:
					break;
				}
			}
		}

		/* ---------------------------------------------------------------- */
		/* Save the annotations */
		/* ---------------------------------------------------------------- */

		// Update the OIDs for the target.
		for (Annotation anno : aTargetGroup.getAnnotations()) {
			anno.setObjectOid(aTargetGroup.getOid());
		}
		// Save the annotations
		annotationDAO.saveAnnotations(aTargetGroup.getAnnotations());
		annotationDAO.deleteAnnotations(aTargetGroup.getDeletedAnnotations());

		// Send emails for parent groups if necessary
		if (sendGroupUpdateNotifications) {
			if (AuthUtil.getRemoteUserObject() != null) {
				String userName = AuthUtil.getRemoteUserObject().getNiceName();

				// Get the new set of parents.
				Set<AbstractTargetDTO> newParents = targetDao.getAncestorDTOs(reloaded.getOid());

				// Determine the added parents and removed parents.
				Set<AbstractTargetDTO> addedParents = new HashSet<AbstractTargetDTO>(newParents);
				addedParents.removeAll(origParents);
				Set<AbstractTargetDTO> removedParents = new HashSet<AbstractTargetDTO>(origParents);
				removedParents.removeAll(newParents);

				HashMap<Long, GroupChangeNotification> changes = getChanges(addedParents, removedParents);
				generateChangeMessageNotifications(reloaded, userName, changes);
			}

			if ((removedChildren.size() > 0 || newChildren.size() > 0)) {
				generateChangeSummary(aTargetGroup, newMembers, removedTargets, gep);
			}
		}
	}

	/**
	 * @param addedParents
	 * @param removedParents
	 * @return
	 */
	private HashMap<Long, GroupChangeNotification> getChanges(Set<AbstractTargetDTO> addedParents,
			Set<AbstractTargetDTO> removedParents) {
		HashMap<Long, GroupChangeNotification> changes = new HashMap<Long, GroupChangeNotification>();

		// Arrange the changes into a per user list.
		for (AbstractTargetDTO dto : addedParents) {
			GroupChangeNotification gcn = null;
			if (!changes.containsKey(dto.getOwnerOid())) {
				gcn = new GroupChangeNotification();
				gcn.userOid = dto.getOwnerOid();
				changes.put(dto.getOwnerOid(), gcn);
			}
			gcn = changes.get(dto.getOwnerOid());
			gcn.addedTo.add(dto);

		}
		for (AbstractTargetDTO dto : removedParents) {
			GroupChangeNotification gcn = null;
			if (!changes.containsKey(dto.getOwnerOid())) {
				gcn = new GroupChangeNotification();
				gcn.userOid = dto.getOwnerOid();
				changes.put(dto.getOwnerOid(), gcn);
			}
			gcn = changes.get(dto.getOwnerOid());
			gcn.removedFrom.add(dto);

		}
		return changes;
	}

	/**
	 * @param aTargetGroup
	 * @param newMembers
	 * @param removedTargets
	 * @param gep
	 */
	private void generateChangeSummary(TargetGroup aTargetGroup, List<AbstractTarget> newMembers,
			List<AbstractTarget> removedTargets, GroupEventPropagator gep) {
		// Generate the email.
		String userName = AuthUtil.getRemoteUserObject().getNiceName();

		StringBuffer buff = new StringBuffer();
		buff.append("<p>");
		buff.append(userName);
		buff.append(" has updated the group <i>");
		buff.append(aTargetGroup.getName());
		buff.append("</i>.</p>");

		if (newMembers.size() > 0) {
			buff.append("<p>The following members have been added:</p><ul>");
			for (AbstractTarget newMember : newMembers) {
				buff.append("<li>");
				buff.append(newMember.getName());
				buff.append("</li>");
			}
			buff.append("</ul>");
		}

		if (removedTargets.size() > 0) {
			buff.append("<p>The following members have been removed:</p><ul>");
			for (AbstractTarget removedMember : removedTargets) {
				buff.append("<li>");
				buff.append(removedMember.getName());
				buff.append("</li>");
			}
			buff.append("</ul>");
		}

		buff.append("<p>These changes directly or indirectly affect some of your groups as follows:</p>");

		for (User recipient : gep.getUpdatedGroups().keySet()) {
			StringBuffer message = new StringBuffer(buff.toString());
			message.append("<ul>");
			for (TargetGroup group : gep.getUpdatedGroups().get(recipient)) {
				message.append("<li>");
				message.append(group.getName());
				message.append("</li>");
			}
			message.append("</ul>");

			// If recpient is the current user, and only one group updated,
			// don't send the message.
			if (!recipient.getOid().equals(AuthUtil.getRemoteUserObject().getOid())
					|| gep.getUpdatedGroups().get(recipient).size() > 1) {
				intrayManager.generateNotification(recipient.getOid(), MessageType.CATEGORY_MISC,
						"Target Group membership updated", message.toString());
			}
		}
	}

	/**
	 * @param reloaded
	 * @param userName
	 * @param changes
	 */
	private void generateChangeMessageNotifications(AbstractTarget reloaded, String userName,
			HashMap<Long, GroupChangeNotification> changes) {
		
		String targetName = reloaded.getName();
		String targetType = reloaded.getClass().getSimpleName();
		
		// For each member of the changes map.
		for (Long userOid : changes.keySet()) {
			// Now construct a message for the user.
			GroupChangeNotification gcn = changes.get(userOid);
			StringBuffer buff = new StringBuffer();
			buff.append("<P>" + userName + " has modified the group membership of the " + targetType + " " + targetName
					+ ".</p>");

			if (!gcn.addedTo.isEmpty()) {
				buff.append("<p>" + targetName
						+ " has been explicitly or implicitly added to the following of your groups:</p>");
				Iterator<AbstractTargetDTO> it = gcn.addedTo.iterator();
				buff.append("<ul>");
				while (it.hasNext()) {
					buff.append("<li>" + it.next().getName() + "</li>");
				}
				buff.append("</ul>");
			}

			if (!gcn.removedFrom.isEmpty()) {
				buff.append("<p>" + targetName
						+ " has been explicitly or implicitly removed from the following of your groups:</p>");
				Iterator<AbstractTargetDTO> it = gcn.removedFrom.iterator();
				buff.append("<ul>");
				while (it.hasNext()) {
					buff.append("<li>" + it.next().getName() + "</li>");
				}
				buff.append("</ul>");
			}

			// Now send the message.
			intrayManager.generateNotification(gcn.userOid, MessageType.CATEGORY_MISC, "Target Group membership updated",
					buff.toString());
		}
	}

	/**
	 * @see org.webcurator.core.targets.TargetManager#scheduleTargetGroup(org.webcurator.domain.model.core.TargetGroup)
	 */
	public void scheduleTargetGroup(TargetGroup aGroup) {
		// Deal with the schedules
		for (Schedule schedule : aGroup.getSchedules()) {
			// instances.addAll(createTargetInstances(aGroup, schedule, aGroup.getSipType() == TargetGroup.MANY_SIP));
			processSchedule(schedule);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.webcurator.core.targets.TargetManager#getNextStates(org.webcurator .domain.model.core.Target)
	 */
	public int[] getNextStates(Target aTarget) {

		int[] emptyArray = new int[] { };
		switch (aTarget.getOriginalState()) {
		case Target.STATE_PENDING:
			return authMgr.hasPrivilege(aTarget, Privilege.APPROVE_TARGET) ? 
    				new int[] { Target.STATE_NOMINATED, Target.STATE_APPROVED, Target.STATE_REJECTED } :
    				new int[] { Target.STATE_NOMINATED };
		case Target.STATE_REINSTATED:
    		return authMgr.hasPrivilege(aTarget, Privilege.APPROVE_TARGET) ?
    				new int[] { Target.STATE_NOMINATED, Target.STATE_APPROVED, Target.STATE_REJECTED } :
    				new int[] { Target.STATE_NOMINATED };
		case Target.STATE_NOMINATED:
    		return authMgr.hasPrivilege(aTarget, Privilege.APPROVE_TARGET) ?
    				new int[] { Target.STATE_APPROVED, Target.STATE_REJECTED } :
    				emptyArray;
		case Target.STATE_REJECTED:
    		return authMgr.hasPrivilege(aTarget, Privilege.REINSTATE_TARGET) ?
    				new int[] { Target.STATE_REINSTATED } :
    				emptyArray;
		case Target.STATE_APPROVED:
    		return authMgr.hasPrivilege(aTarget, Privilege.CANCEL_TARGET) ?
    				new int[] { Target.STATE_CANCELLED } :
    				emptyArray;
		case Target.STATE_CANCELLED:
    		return authMgr.hasPrivilege(aTarget, Privilege.REINSTATE_TARGET) ?
    				new int[] { Target.STATE_REINSTATED } :
    				emptyArray;
		case Target.STATE_COMPLETED:
    		return authMgr.hasPrivilege(aTarget, Privilege.REINSTATE_TARGET) ?
    				new int[] { Target.STATE_REINSTATED } :
    				emptyArray;

		default:
			return emptyArray;
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.webcurator.core.targets.TargetManager#load(java.lang.Long)
	 */
	public Target load(Long oid) {
		return load(oid, false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.webcurator.core.targets.TargetManager#load(java.lang.Long, boolean)
	 */
	public Target load(Long oid, boolean loadFully) {
		return targetDao.load(oid, loadFully);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.webcurator.core.targets.TargetManager#getQuickPickPermissions(Target)
	 */
	public List<Permission> getQuickPickPermissions(Target aTarget) {
		return siteDao.getQuickPickPermissions(aTarget.getOwningUser().getAgency());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.webcurator.core.targets.TargetManager#findPermissionsBySiteTitle( java.lang.String, int)
	 */
	public Pagination findPermissionsBySiteTitle(Target aTarget, String aSiteTitle, int aPageNumber) {
		return siteDao.findPermissionsBySiteTitle(aTarget.getOwningUser().getAgency().getOid(), aSiteTitle, aPageNumber);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.webcurator.core.targets.TargetManager#findPermissionsByUrl(java.lang .String, int)
	 */
	public Pagination findPermissionsByUrl(Target aTarget, String aUrl, int aPageNumber) {
		List<Permission> permissionList = new LinkedList<Permission>();
		permissionList.addAll(PermissionMappingStrategy.getStrategy().getMatchingPermissions(aTarget, aUrl));
		return new Pagination(permissionList, aPageNumber, Constants.GBL_PAGE_SIZE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.webcurator.core.targets.TargetManager#loadPermission(org.webcurator.ui.target.TargetEditorContext, java.lang.String)
	 */
	public Permission loadPermission(TargetEditorContext ctx, String identity) {
		Permission p = (Permission) ctx.getObject(Permission.class, identity);

		if (p == null) {
			p = siteDao.loadPermission(Long.parseLong(identity));
			ctx.putObject(p);
		}

		return p;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.webcurator.core.targets.TargetManager#loadPermission(java.lang.Long)
	 */
	public Permission loadPermission(Long oid) {
		return siteDao.loadPermission(oid);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.webcurator.core.targets.TargetManager#isNameOk(org.webcurator.domain .model.core.Target)
	 */
	public boolean isNameOk(AbstractTarget aTarget) {
		return targetDao.isNameOk(aTarget);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.webcurator.core.targets.TargetManager#setTargetDao(org.webcurator .domain.TargetDAO)
	 */
	public void setTargetDao(TargetDAO targetDao) {
		this.targetDao = targetDao;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.webcurator.core.targets.TargetManager#setAuthMgr(org.webcurator.auth .AuthorityManager)
	 */
	public void setAuthMgr(AuthorityManager authMgr) {
		this.authMgr = authMgr;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.webcurator.core.targets.TargetManager#setSiteDao(org.webcurator.domain .SiteDAO)
	 */
	public void setSiteDao(SiteDAO siteDao) {
		this.siteDao = siteDao;
	}

	/**
	 * @see org.webcurator.core.targets.TargetManager#getAnnotations(org.webcurator.domain.model.core.AbstractTarget)
	 */
	public List<Annotation> getAnnotations(AbstractTarget aTarget) {
		List<Annotation> annotations = null;
		if (aTarget.getOid() != null) {
			String className;
			if (aTarget instanceof TargetGroup) {
				// Special case for lazy loaded groups
				className = TargetGroup.class.getName();
			} else {
				className = aTarget.getClass().getName();
			}
			annotations = annotationDAO.loadAnnotations(className, aTarget.getOid());
		}

		if (annotations == null) {
			annotations = new ArrayList<Annotation>();
		}

		return annotations;
	}

	/**
	 * @param annotationDAO
	 *            The annotationDAO to set.
	 */
	public void setAnnotationDAO(AnnotationDAO annotationDAO) {
		this.annotationDAO = annotationDAO;
	}

	/** @see TargetManager#isTargetHarvestable(TargetInstance). */
	public boolean isTargetHarvestable(TargetInstance aTargetInstance) {
		boolean harvestable = false;
		boolean foundBadSeed = false;

		AbstractTarget aTarget = aTargetInstance.getTarget();
		if (aTarget.getObjectType() == AbstractTarget.TYPE_GROUP) {
			return getSeeds(aTargetInstance).size() > 0;
		} else {
			Long oid = aTarget.getOid();
			Target target = targetDao.load(oid, true);

			Seed seed = null;
			Set<Seed> seeds = target.getSeeds();
			Iterator<Seed> it = seeds.iterator();
			while (it.hasNext()) {
				seed = (Seed) it.next();
				if (!seed.isHarvestable(new Date())) {
					foundBadSeed = true;
					break;
				}
			}

			if (!seeds.isEmpty() && !foundBadSeed) {
				harvestable = true;
			}

			return harvestable;
		}
	}

	/** @see TargetManager#isTargetUsingAQA(Long targetOid). */
	public boolean isTargetUsingAQA(Long targetOid) {

		Target target = targetDao.load(targetOid, false);
		return target.isUseAQA();
	}

	/**
	 * @see org.webcurator.core.targets.TargetManager#allowStateChange(org.webcurator.domain.model.core.Target, int)
	 */
	public boolean allowStateChange(Target aTarget, int nextState) {
		if (aTarget.getOriginalState() == nextState) {
			return true;
		} else {
			int[] nextStates = getNextStates(aTarget);

			for (int i = 0; i < nextStates.length; i++) {
				if (nextStates[i] == nextState) {
					return true;
				}
			}
			return false;
		}
	}

	/**
	 * @see TargetManager#loadGroup(Long)
	 */
	public TargetGroup loadGroup(Long oid) {
		return targetDao.loadGroup(oid);
	}

	/**
	 * @see TargetManager#loadGroup(Long, boolean)
	 */
	public TargetGroup loadGroup(Long oid, boolean loadFully) {
		return targetDao.loadGroup(oid, loadFully);
	}

	/**
	 * @see TargetManager#getMembers(TargetGroup, int)
	 */
	public Pagination getMembers(TargetGroup aTargetGroup, int pageNum, int pageSize) {
		return targetDao.getMembers(aTargetGroup, pageNum, pageSize);
	}

	/**
	 * @see TargetManager#getParents(AbstractTarget, int)
	 */
	public Pagination getParents(AbstractTarget aTarget, int pageNum, int pageSize) {
		return targetDao.getParents(aTarget, pageNum, pageSize);
	}

	/**
	 * @see TargetManager#getAbstractTargetDTOs(String, int)
	 */
	public Pagination getAbstractTargetDTOs(String name, int pageNumber, int pageSize) {
		return targetDao.getAbstractTargetDTOs(name, pageNumber, pageSize);
	}

	/**
	 * @see TargetManager#getAbstractTargetDTOs(String, int)
	 */
	public Pagination getGroupDTOs(String name, int pageNumber, int pageSize) {
		return targetDao.getGroupDTOs(name, pageNumber, pageSize);
	}

	/**
	 * @see TargetManager#getAbstractTargetDTOs(String, int)
	 */
	public Pagination getSubGroupParentDTOs(String name, int pageNumber, int pageSize) {

		List<String> types = new ArrayList<String>();

		Iterator<String> it = subGroupParentTypesList.iterator();
		while (it.hasNext()) {
			types.add(it.next());
		}

		return targetDao.getSubGroupParentDTOs(name, types, pageNumber, pageSize);
	}

	/**
	 * @see TargetManager#getAbstractTargetDTOs(String, int)
	 */
	public Pagination getNonSubGroupDTOs(String name, int pageNumber, int pageSize) {

		return targetDao.getNonSubGroupDTOs(name, subGroupTypeName, pageNumber, pageSize);
	}

	/**
	 * @see TargetManager#createGroupMemberDTO(TargetGroup, Long)
	 */
	public GroupMemberDTO createGroupMemberDTO(TargetGroup group, Long childOid) {
		AbstractTarget child = loadAbstractTarget(childOid);
		return new GroupMemberDTO(group, child);
	}

	/**
	 * @see TargetManager#createGroupMemberDTO(Long, Target)
	 */
	public GroupMemberDTO createGroupMemberDTO(Long groupOid, Target child) {
		TargetGroup group = targetDao.loadGroup(groupOid);
		return new GroupMemberDTO(group, child);
	}

	/**
	 * @see TargetManager#searchGroups(int, int, Long, String, String, String, String, String)
	 */
	public Pagination searchGroups(int pageNumber, int pageSize, Long searchOid, String name, String owner, String agency,
			String memberOf, String groupType, boolean nondisplayonly) {
		return targetDao.searchGroups(pageNumber, pageSize, searchOid, name, owner, agency, memberOf, groupType, nondisplayonly);
	}

	/**
	 * @param targetInstanceDao
	 *            The targetInstanceDao to set.
	 */
	public void setTargetInstanceDao(TargetInstanceDAO targetInstanceDao) {
		this.targetInstanceDao = targetInstanceDao;
	}

	/**
	 * Look up all schedules that need to be processed.
	 */
	public void processSchedulesJob() {
		List<Schedule> schedules = targetDao.getSchedulesToRun();
		// STK - use below line instead of above line in test environment due to performance issues.
		// List<Schedule> schedules = new LinkedList<Schedule>();

		for (Schedule s : schedules) {
			if (s.getTarget() == null) {
				log.debug(" Schedule has null target so skipping processing: " + s.getOid());
				Calendar cal = Calendar.getInstance();
				cal.setTime(new Date());
				s.setLastProcessedDate(cal.getTime());
				targetDao.save(s);
			} else {
				log.debug(" Processing schedule: " + s.getOid());
				processBatchSchedule(s);
			}
		}
	}

	/**
	 * Process a schedule and calculate all of its target instances.
	 * 
	 * @param aSchedule
	 *            The schedule to evaluate.
	 * @return A Set of Target instances.
	 */
	public void processSchedule(Schedule aSchedule) {

		AbstractTarget aTarget = aSchedule.getTarget();
		boolean checkAgency = false;
		if (aTarget.getObjectType() == AbstractTarget.TYPE_GROUP) {
			TargetGroup group;
			if (aTarget instanceof TargetGroup) {
				group = (TargetGroup) aTarget;
			} else {
				group = targetDao.loadGroup(aTarget.getOid());
			}

			if (group.getSipType() == TargetGroup.MANY_SIP) {
				checkAgency = true;
			}
		}

		createTargetInstances(aSchedule.getTarget(), aSchedule, checkAgency);

		// Get the schedule ahead time from our environment.
		int daysToSchedule = EnvironmentFactory.getEnv().getDaysToSchedule();

		// Determine when to end the schedule. This is the earliest of the
		// end date, or the current date + the number of days ahead to schedule.
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.DAY_OF_MONTH, daysToSchedule);
		cal.add(Calendar.SECOND, -1);

		Date firstAfterPeriod = aSchedule.getNextExecutionDate(cal.getTime());

		if (firstAfterPeriod != null) {
			aSchedule.setNextScheduleAfterPeriod(firstAfterPeriod);
			targetDao.save(aSchedule);
		}
	}

	/**
	 * Process a batch schedule and calculate all of its target instances.
	 * 
	 * @param aSchedule
	 *            The schedule to evaluate.
	 * @return A Set of Target instances.
	 */
	public void processBatchSchedule(Schedule aSchedule) {

		AbstractTarget aTarget = aSchedule.getTarget();

		boolean checkAgency = false;
		TargetGroup group = null;

		Hibernate.initialize(aTarget);

		if (aTarget.getObjectType() == AbstractTarget.TYPE_GROUP) {

			log.debug(" Schedules target is a group.");
			if (aTarget instanceof TargetGroup) {
				group = (TargetGroup) aTarget;
			} else {
				group = targetDao.loadGroup(aTarget.getOid(), true);
			}

			if (group.getSipType() == TargetGroup.MANY_SIP) {
				checkAgency = true;
			}
		} else {
			log.debug(" Schedules target is a target.");
		}

		createBatchTargetInstances(aTarget, aSchedule, group, checkAgency);

		// Get the schedule ahead time from our environment.
		int daysToSchedule = EnvironmentFactory.getEnv().getDaysToSchedule();

		// Determine when to end the schedule. This is the earliest of the
		// end date, or the current date + the number of days ahead to schedule.
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.DAY_OF_MONTH, daysToSchedule);
		cal.add(Calendar.SECOND, -1);

		Calendar cal2 = Calendar.getInstance();
		cal2.setTime(new Date());

		Date firstAfterPeriod = aSchedule.getNextExecutionDate(cal.getTime());

		if (firstAfterPeriod != null) {
			aSchedule.setNextScheduleAfterPeriod(firstAfterPeriod);
		}

		aSchedule.setLastProcessedDate(cal2.getTime());
		targetDao.save(aSchedule);
		log.debug(" Saved schedule: " + aSchedule.getOid() + " - set last processed date to: " + cal2.getTime());
	}

	/**
	 * Create target instances for the target.
	 * 
	 * @param aSchedule
	 *            The schedule to use.
	 */

	private void createBatchTargetInstances(AbstractTarget aTarget, Schedule aSchedule, TargetGroup aGroup, boolean checkAgency) {

		int objectType = aTarget.getObjectType();

		if (objectType == AbstractTarget.TYPE_TARGET
				|| (objectType == AbstractTarget.TYPE_GROUP && aGroup.getSipType() == TargetGroup.ONE_SIP)) {

			createAndSaveTargetInstances(aTarget, aSchedule, checkAgency);
			return;
		} else if (objectType == AbstractTarget.TYPE_GROUP && aGroup.getSipType() == TargetGroup.MANY_SIP) {

			for (GroupMember member : aGroup.getChildren()) {
				// If the child is a Sub-Group, we don't want to propagate the schedule to its members
				AbstractTarget child = targetDao.load(member.getChild().getOid());
				if (child.getObjectType() == AbstractTarget.TYPE_GROUP) {
					continue;
				}

				// Only want to add them if they're unique.
				createBatchTargetInstances(child, aSchedule, null, checkAgency);
			}

			return;
		}
		else {
			throw new WCTRuntimeException("Unknown Target Type: " + aTarget.getClass());
		}
	}

	public void createTargetInstances(AbstractTarget aTarget, Schedule aSchedule, boolean checkAgency) {
		int objectType = aTarget.getObjectType();
		if (objectType == AbstractTarget.TYPE_GROUP) {
			// Lets make sure it is loaded before we start casting!
			if (!(aTarget instanceof TargetGroup)) {
				aTarget = targetDao.loadGroup(aTarget.getOid());
			}
		}

		if (objectType == AbstractTarget.TYPE_TARGET || objectType == AbstractTarget.TYPE_GROUP
				&& ((TargetGroup) aTarget).getSipType() == TargetGroup.ONE_SIP) {
			
			createAndSaveTargetInstances(aTarget, aSchedule, checkAgency);
			return;
		} else if (objectType == AbstractTarget.TYPE_GROUP && ((TargetGroup) aTarget).getSipType() == TargetGroup.MANY_SIP) {
			TargetGroup aTargetGroup = (TargetGroup) aTarget;

			for (GroupMember member : aTargetGroup.getChildren()) {
				// If the child is a Sub-Group, we don't want to propagate the schedule to its members
				AbstractTarget child = member.getChild();
				if (child.getObjectType() == AbstractTarget.TYPE_GROUP) {
					TargetGroup childGroup = targetDao.loadGroup(child.getOid(), false);
					if (subGroupTypeName.equals(childGroup.getType())) {
						continue;
					}
				}

				// Only want to add them if they're unique.
				createTargetInstances(child, aSchedule, checkAgency);
			}

			return;
		} else {
			String type = null;
			if (aTarget instanceof TargetGroup) {
				type = "" + ((TargetGroup) aTarget).getSipType();
			}
			throw new WCTRuntimeException("Unknown Target Type: " + aTarget.getClass() + " (" + type + ")");
		}
	}


	/**
	 * @param aTarget
	 * @param aSchedule
	 * @param checkAgency
	 */
	private void createAndSaveTargetInstances(AbstractTarget aTarget, Schedule aSchedule, boolean checkAgency) {
		User targetOwner = aTarget.getOwner();
		User scheduleOwner = aSchedule.getOwningUser();
		Agency scheduleAgency = scheduleOwner.getAgency();
		Agency targetAgency = targetOwner.getAgency();
		if (checkAgency && !targetAgency.equals(scheduleAgency)) {
			// We should not schedule this one because it belongs to a different
			// agency to the schedule.
			return;
		}
		if (!aTarget.isSchedulable()) {
			// We cannot schedule an unschedulable target.
			return;
		}
		
		Date scheduleTill = getScheduleUntilDate(aSchedule);
		Date startFrom = getScheduleStartDate(aTarget, aSchedule);
		createAndSaveTargetInstances(aTarget, aSchedule, scheduleTill, startFrom);

	}

	private Date getScheduleStartDate(AbstractTarget aTarget, Schedule aSchedule) {
		// Determine when to schedule from. This is the latest of:
		// 1. Today
		// 2. The start date.
		// 3. The latest current target instance.
		Date startFrom = targetDao.getLatestScheduledDate(aTarget, aSchedule);

		if (startFrom == null) {
			startFrom = new Date();
		}
		startFrom = DateUtils.latestDate(startFrom, aSchedule.getStartDate());
		return startFrom;
	}

	private Date getScheduleUntilDate(Schedule aSchedule) {
		// Get the schedule ahead time from our environment.
		int daysToSchedule = EnvironmentFactory.getEnv().getDaysToSchedule();

		// Determine when to end the schedule. This is the earliest of the
		// end date, or the current date + the number of days ahead to schedule.
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.DAY_OF_MONTH, daysToSchedule);
		Date scheduleTill = aSchedule.getEndDate() == null ? cal.getTime() : DateUtils.earliestDate(aSchedule.getEndDate(),
				cal.getTime());
		return scheduleTill;
	}

	/**
	 * Create target instances for the target.
	 * 
	 * @param aSchedule
	 *            The schedule to use.
	 * @return 
	 */
	private void createAndSaveTargetInstances(AbstractTarget aTarget, Schedule aSchedule, Date scheduleUntil, Date startFrom) {
		int objectType = aTarget.getObjectType();

		List<Annotation> targetAnnotations = getAnnotations(aTarget);
		boolean firstForTarget = false;
		int targetInstanceCount = 0;
		for (startFrom = aSchedule.getNextExecutionDate(startFrom); startFrom != null && startFrom.before(scheduleUntil); startFrom = aSchedule
				.getNextExecutionDate(startFrom)) {

			TargetInstance ti = new TargetInstance();
			ti.setTarget(aTarget);
			ti.setSchedule(aSchedule);
			ti.setScheduledTime(startFrom);
			ti.setOwner(aSchedule.getOwningUser());

			// if this is the first TargetInstance ever to be created for
			// a particular target then set the firstFromTarget flag on the TI.
			if (!firstForTarget && instanceManager.countTargetInstancesByTarget(aTarget.getOid()) == 0) {
				ti.setFirstFromTarget(true);
				firstForTarget = true;
			}

			if (objectType == AbstractTarget.TYPE_TARGET) {
				Target target = targetDao.load(aTarget.getOid(), true);
				ti.setAllowOptimize(target.isAllowOptimize());

				copyAltertableAnnotations(targetAnnotations, ti);

				// if the useAQA flag is set on the Target then set
				// it on the Target Instance too.
				ti.setUseAQA(isTargetUsingAQA(aTarget.getOid()));

			}

			targetInstanceDao.save(ti);
			// Update the OIDs for the target.
			for (Annotation anno : ti.getAnnotations()) {
				anno.setObjectOid(ti.getOid());
			}
			// Save the annotations
			annotationDAO.saveAnnotations(ti.getAnnotations());
			log.debug(" Saved TI: " + ti.getOid());
			targetInstanceCount++;
		}
		log.debug(" Created " + targetInstanceCount);
	}

	private void copyAltertableAnnotations(List<Annotation> targetAnnotations, TargetInstance ti) {
		// copy 'alert' type annotations from a target type to each TI
		for (Annotation toCopy : targetAnnotations) {
			if (toCopy.isAlertable()) {

				Annotation annotation = new Annotation();
				annotation.setDate(toCopy.getDate());
				annotation.setNote(toCopy.getNote());
				annotation.setAlertable(true);
				annotation.setUser(toCopy.getUser());
				annotation.setObjectType(TargetInstance.class.getName());

				ti.addAnnotation(annotation);
			}
		}
	}

	/**
	 * Update the group status.
	 * 
	 * @param aTarget
	 *            The target to update parents for.
	 */
	private void updateTargetGroupStatus(Target aTarget) {
		for (GroupMember parent : aTarget.getParents()) {
			updateTargetGroupStatus(parent.getParent());
		}
	}

	/**
	 * Updates the status of a TargetGroup and its parents. This must be invoked after saving a TargetGroup to ensure that the
	 * states of parent groups remain consistent.
	 * 
	 * @param aTargetGroup
	 *            The TargetGroup to update the state on.
	 */
	private void updateTargetGroupStatus(TargetGroup aTargetGroup) {
		int originalState = aTargetGroup.getState();
		int newState = originalState;

		// Check if the target group should be expired.
		Date now = new Date();

		// The TargetGroup has reached it's end date and should be inactive.
		if (aTargetGroup.getToDate() != null && aTargetGroup.getToDate().before(now)) {
			newState = TargetGroup.STATE_INACTIVE;
		}

		// TargetGroup has not passed its end date. Need to check the child
		// states.
		else {
			boolean allInactive = true;
			newState = Target.STATE_PENDING;

			for (Integer state : targetDao.getSavedMemberStates(aTargetGroup)) {

				if (state != TargetGroup.STATE_INACTIVE) {
					allInactive = false;
				}

				if (state == Target.STATE_APPROVED || state == Target.STATE_COMPLETED || state == TargetGroup.STATE_ACTIVE) {
					newState = TargetGroup.STATE_ACTIVE;
				}
			}

			// If all the children were inactive, we should be inactive too.
			if (allInactive) {
				newState = TargetGroup.STATE_INACTIVE;
			}
		}

		if (newState == TargetGroup.STATE_ACTIVE) {
			scheduleTargetGroup(aTargetGroup);
		} else {
			log.debug("About to Unschedule the Group");
			unschedule(aTargetGroup);
			log.debug("Unscheduled the group");
		}

		if (originalState != newState) {
			// Save the target and recurse to all parents.
			aTargetGroup.changeState(newState);

			// Don't bother saving the children; they should already
			// be saved.
			targetDao.save(aTargetGroup, false, null);

			// If we're active, schedule us.
			if (aTargetGroup.getState() == TargetGroup.STATE_ACTIVE) {
				scheduleTargetGroup(aTargetGroup);
			}

			// Otherwise unschedule us.
			else {
				unschedule(aTargetGroup);
			}

			// Since my state changed, the state of my parent may
			// have changed. Recurse to all parents.
			for (GroupMember parent : aTargetGroup.getParents()) {
				updateTargetGroupStatus(parent.getParent());
			}
		}
	}

	/**
	 * Get the seeds for the given target instance.
	 * 
	 * @param aTargetInstance
	 * @return The set of Seeds.
	 */
	public Set<Seed> getSeeds(TargetInstance aTargetInstance) {
		return getSeeds(aTargetInstance.getTarget(), aTargetInstance.getOwner().getAgency().getOid());
	}

	/**
	 * Get the seeds for a Target that belong within a given agency.
	 * 
	 * @param aTarget
	 *            The target to get the seeds for.
	 * @param agencyOid
	 *            The agency to which to restrict the recursion.
	 */
	public Set<Seed> getSeeds(AbstractTarget aTarget, Long agencyOid) {
		int objectType = aTarget.getObjectType();

		if (objectType == AbstractTarget.TYPE_TARGET) {
			Target theTarget = null;
			if (aTarget instanceof Target) {
				theTarget = (Target) aTarget;
			} else {
				// Force a reload of the Target to overcome lazy load
				// ClassCastException
				theTarget = targetDao.load(aTarget.getOid());
			}
			return targetDao.getSeeds(theTarget);
		} else if (objectType == AbstractTarget.TYPE_GROUP) {
			TargetGroup theTargetGroup = null;
			if (aTarget instanceof TargetGroup) {
				theTargetGroup = (TargetGroup) aTarget;
			} else {
				// Force a reload of the TargetGroup to overcome lazy load
				// ClassCastException
				theTargetGroup = targetDao.loadGroup(aTarget.getOid());
			}

			return targetDao.getSeeds(theTargetGroup, agencyOid, subGroupTypeName);
		} else {
			throw new WCTRuntimeException("Unknown Target Type for getSeeds: " + objectType);
		}
	}

	/**
	 * @see org.webcurator.core.targets.TargetManager#countTargets(org.webcurator.domain.model.auth.User)
	 */
	public int countTargets(User aUser) {
		return targetDao.countTargets(aUser.getUsername());
	}

	/**
	 * @see org.webcurator.core.targets.TargetManager#countTargetGroups(org.webcurator.domain.model.auth.User)
	 */
	public int countTargetGroups(User aUser) {
		return targetDao.countTargetGroups(aUser.getUsername());
	}

	/**
	 * @return Returns the instanceManager.
	 */
	public TargetInstanceManager getInstanceManager() {
		return instanceManager;
	}

	/**
	 * @param instanceManager
	 *            The instanceManager to set.
	 */
	public void setInstanceManager(TargetInstanceManager instanceManager) {
		this.instanceManager = instanceManager;
	}

	/**
	 * @return Returns the intrayManager.
	 */
	public InTrayManager getIntrayManager() {
		return intrayManager;
	}

	/**
	 * @param intrayManager
	 *            The intrayManager to set.
	 */
	public void setIntrayManager(InTrayManager intrayManager) {
		this.intrayManager = intrayManager;
	}

	/**
	 * @see org.webcurator.core.targets.TargetManager#getAncestorOids(org.webcurator.domain.model.core.AbstractTarget)
	 */
	public Set<Long> getAncestorOids(AbstractTarget child) {
		return targetDao.getAncestorOids(child.getOid());
	}

	/**
	 * @see org.webcurator.core.targets.TargetManager#isDuplicateMember(org.webcurator.domain.model.core.TargetGroup,
	 *      java.lang.Long)
	 */
	public boolean isDuplicateMember(TargetGroup group, Long memberOid) {
		for (GroupMemberDTO dto : group.getNewChildren()) {
			if (dto.getChildOid().equals(memberOid)) {
				return true;
			}
		}

		return targetDao.getImmediateChildrenOids(group.getOid()).contains(memberOid);
	}

	/**
	 * @see org.webcurator.core.targets.TargetManager#loadAbstractTarget(java.lang.Long)
	 */
	public AbstractTarget loadAbstractTarget(Long oid) {
		return targetDao.loadAbstractTarget(oid);
	}

	/**
	 * @return Returns the sendGroupUpdateNotifications.
	 */
	public boolean isSendGroupUpdateNotifications() {
		return sendGroupUpdateNotifications;
	}

	/**
	 * @param sendGroupUpdateNotifications
	 *            The sendGroupUpdateNotifications to set.
	 */
	public void setSendGroupUpdateNotifications(boolean sendGroupUpdateNotifications) {
		this.sendGroupUpdateNotifications = sendGroupUpdateNotifications;
	}

	/**
	 * Detect and update TargetGroups that must be made inactive due to their end date having been passed.
	 */
	public void endDateGroups() {
		targetInstanceDao.endDateGroups();
	}

	/**
	 * Transfer Seeds from Permission A to Permission B.
	 * 
	 * @param fromPermission
	 *            The oid of the permission to transfer seeds from.
	 * @param toPermission
	 *            The oid of the permission to transfer the seeds to.
	 * @return The number of seeds transferred.
	 */
	public int transferSeeds(Permission fromPermission, Permission toPermission) {
		List<Seed> seeds = targetDao.getLinkedSeeds(fromPermission);

		for (Seed s : seeds) {
			s.removePermission(fromPermission);
			s.addPermission(toPermission);
		}

		targetDao.saveAll(seeds);

		return seeds.size();
	}

	/**
	 * Search the Permissions.
	 * 
	 * @param aPermissionCriteria
	 *            The criteria to use to search the permissions.
	 * @return A Pagination of permission records.
	 */
	public Pagination searchPermissions(PermissionCriteria aPermissionCriteria) {
		return targetDao.searchPermissions(aPermissionCriteria);
	}

	/**
	 * Retrieve a List of active parent groups.
	 * 
	 * @param aTargetInstance
	 *            The target instance to find the parent groups for.
	 * @return A List of active parent groups.
	 */
	public List<TargetGroup> getActiveParentGroups(TargetInstance aTargetInstance) {
		List<TargetGroup> ancestorList = new LinkedList<TargetGroup>();

		for (GroupMember parents : aTargetInstance.getTarget().getParents()) {
			getActiveParentGroups(parents.getParent(), aTargetInstance.getActualStartTime(), ancestorList);
		}

		return ancestorList;
	}

	/** @see TargetManager#getActivePermissions(TargetInstance). */
	public Collection<PermissionSeedDTO> getActivePermissions(TargetInstance aTargetInstance) {
		HashMap<Long, PermissionSeedDTO> permissions = new HashMap<Long, PermissionSeedDTO>();
		Set<Seed> seeds = getSeeds(aTargetInstance);
		Date now = new Date();

		PermissionSeedDTO psdto = null;
		Set<Permission> ps = null;
		for (Seed seed : seeds) {
			ps = seed.getPermissions();
			for (Permission p : ps) {
				if (p.getStartDate().before(now) && (p.getEndDate() == null || p.getEndDate().after(now))) {
					if (permissions.containsKey(p.getOid())) {
						psdto = (PermissionSeedDTO) permissions.get(p.getOid());
					} else {
						psdto = new PermissionSeedDTO(p);
					}

					psdto.getSeeds().add(seed.getSeed());

					permissions.put(psdto.getPermissionOid(), psdto);
				}
			}
		}

		return permissions.values();
	}

	/**
	 * Private method to recurse through groups and find the active parents.
	 * 
	 * @param aGroup
	 *            The parent to start from
	 * @param aDate
	 *            The date at which the group must be valid.
	 * @param destList
	 *            The list to add the parents to.
	 */
	private void getActiveParentGroups(TargetGroup aGroup, Date aDate, List<TargetGroup> destList) {
		// Check that the group is active.
		if ((aGroup.getFromDate() == null || aGroup.getFromDate().before(aDate))
				&& (aGroup.getToDate() == null || aGroup.getToDate().after(aDate))) {
			destList.add(aGroup);

			for (GroupMember parents : aGroup.getParents()) {
				getActiveParentGroups(parents.getParent(), aDate, destList);
			}
		}
	}

	/**
	 * @param auditor
	 *            The auditor to set.
	 */
	public void setAuditor(Auditor auditor) {
		this.auditor = auditor;
	}

	/**
	 * @param messageSource
	 *            The messageSource to set.
	 */
	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	/**
	 * Copy the Target
	 * 
	 * @param aTarget
	 *            the Target to copy.
	 * @return A copy of the target.
	 */
	public Target copy(Target aTarget) {
		Target copy = businessObjectFactory.newTarget();

		copy.setDescription(aTarget.getDescription());
		copy.setProfile(aTarget.getProfile());

		copy.setOverrides(aTarget.getOverrides().copy());

		// Copy the seeds.
		for (Seed seed : aTarget.getSeeds()) {
			Seed newSeed = businessObjectFactory.newSeed(copy);
			newSeed.setPrimary(seed.isPrimary());
			newSeed.setSeed(seed.getSeed());

			// Copy the related of the permissions.
			for (Permission perm : seed.getPermissions()) {
				newSeed.addPermission(perm);
			}
			copy.addSeed(newSeed);
		}

		// Copy the schedules.
		for (Schedule schedule : aTarget.getSchedules()) {
			Schedule newSchedule = businessObjectFactory.newSchedule(copy);
			newSchedule.setCronPattern(schedule.getCronPattern());
			newSchedule.setStartDate(schedule.getStartDate());
			newSchedule.setEndDate(schedule.getEndDate());
			newSchedule.setScheduleType(schedule.getScheduleType());

			copy.addSchedule(newSchedule);
		}

		return copy;
	}

	private static String trunc(String str, int length) {
		if (str.length() > length) {
			return str.substring(0, length);
		} else {
			return str;
		}
	}

	/**
	 * Copy a TargetGroup.
	 * 
	 * @param aTargetGroup
	 *            The target group to copy.
	 * @return a copy of the gropu.
	 */
	public TargetGroup copy(TargetGroup aTargetGroup) {
		TargetGroup copy = businessObjectFactory.newTargetGroup();
		copy.setName(trunc("Copy of " + aTargetGroup.getName(), AbstractTarget.CNST_MAX_NAME_LENGTH));
		copy.setDescription(aTargetGroup.getDescription());

		// Copy the schedules.
		for (Schedule schedule : aTargetGroup.getSchedules()) {
			Schedule newSchedule = businessObjectFactory.newSchedule(copy);
			newSchedule.setCronPattern(schedule.getCronPattern());
			newSchedule.setStartDate(schedule.getStartDate());
			newSchedule.setEndDate(schedule.getEndDate());
			newSchedule.setScheduleType(schedule.getScheduleType());

			copy.addSchedule(newSchedule);
		}

		// Copy the profile settings
		copy.setProfile(aTargetGroup.getProfile());
		copy.setOverrides(aTargetGroup.getOverrides().copy());

		// Copy child references.
		for (GroupMember gm : aTargetGroup.getChildren()) {
			copy.getNewChildren().add(new GroupMemberDTO(copy, gm.getChild()));
		}

		return copy;

	}

	/**
	 * Deletes a pending target.
	 * 
	 * @param aTarget
	 *            The Target to be deleted.
	 */
	public void deleteTarget(Target aTarget) {
		if (authMgr.hasPrivilege(aTarget, Privilege.DELETE_TARGET) && aTarget.getState() == Target.STATE_PENDING) {
			// Delete all links to the target(s) parents.
			if (aTarget.getParents().size() > 0) {
				for (GroupMember parent : aTarget.getParents()) {
					parent.getParent().getRemovedChildren().add(aTarget.getOid());
					save(parent.getParent());
				}
			}

			// Delete the target itself.
			targetDao.delete(aTarget);
		} else {
			log.error("Delete not permitted, no action taken");
		}
	}

	/**
	 * Deletes a TargetGroup by OID.
	 * 
	 * @param aTargetGroup
	 *            The group to be deleted.
	 * @return true if the target group was deleted.
	 */
	public boolean deleteTargetGroup(TargetGroup aTargetGroup) {

		if (authMgr.hasPrivilege(aTargetGroup, Privilege.MANAGE_GROUP)) {
			// Delete the target itself.
			return targetDao.deleteGroup(aTargetGroup);
		} else {
			log.error("Delete not permitted, no action taken");
			throw new WCTRuntimeException("You do not have the appropriate privileges to delete this group");
		}
	}

	public List<GroupMemberDTO> getParents(final AbstractTarget aTarget) {
		return targetDao.getParents(aTarget);
	}

	/**
	 * @param businessObjectFactory
	 *            The businessObjectFactory to set.
	 */
	public void setBusinessObjectFactory(BusinessObjectFactory businessObjectFactory) {
		this.businessObjectFactory = businessObjectFactory;
	}

	/**
	 * @param subGroupParentTypesList
	 *            The subGroupParentTypesList to set.
	 */
	public void setSubGroupParentTypesList(WCTTreeSet subGroupParentTypesList) {
		this.subGroupParentTypesList = subGroupParentTypesList;
	}

	/**
	 * @param subGroupTypeName
	 *            The subGroupTypeName to set.
	 */
	public void setSubGroupTypeName(String subGroupTypeName) {
		this.subGroupTypeName = subGroupTypeName;
	}

	@Override
	public void moveTargets(TargetGroup sourceGroup, TargetGroup targetGroup, List<Long> targetsToMove) {
		Iterator<Long> it = targetsToMove.iterator();
		while (it.hasNext()) {
			Long targetToMove = it.next();

			// add the target to the new group
			GroupMemberDTO dto = createGroupMemberDTO(targetGroup, targetToMove);
			targetGroup.getNewChildren().add(dto);

			// remove the target from the old group
			sourceGroup.getRemovedChildren().add(targetToMove);
		}

		save(sourceGroup);
		save(targetGroup);
	}
}