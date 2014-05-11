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

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.webcurator.core.scheduler.TargetInstanceManager;
import org.webcurator.domain.model.auth.User;
import org.webcurator.domain.model.core.AbstractTarget;
import org.webcurator.domain.model.core.GroupMember;
import org.webcurator.domain.model.core.Schedule;
import org.webcurator.domain.model.core.Target;
import org.webcurator.domain.model.core.TargetGroup;

/**
 * The GroupEventPropagator deals with members that are added
 * to groups, allowing the TargetManager to (1) schedule any
 * elements that need scheduling and (2) notify owners of 
 * ancestor groups.
 * 
 * @author bbeaumont
 */
public class GroupEventPropagator {
	/** A reference to the TargetManager. */
	private TargetManager targetManager = null;
	/** The Group to which a member was added. */
	private TargetGroup group = null;
	/** The member that has been added. */
	private List<AbstractTarget> members = null;

	/** The target instance manager */
	private TargetInstanceManager instanceManager = null;
	
	/** A map of the target groups updated and the users who own them. */
	private Map<User, Set<TargetGroup> > updatedGroups = new HashMap<User, Set<TargetGroup> >();
	
	
	/**
	 * Create a new GroupEventPropagator
	 * @param targetManager The TargetManager to use.
	 * @param instanceManager The InstanceManager to use.
	 * @param parent The parent that the member was added to.
	 * @param members The members that where added.
	 */
	public GroupEventPropagator(TargetManager targetManager, TargetInstanceManager instanceManager, TargetGroup parent, List<AbstractTarget> members) {
		this.targetManager = targetManager;
		this.instanceManager = instanceManager;
		this.group = parent;
		this.members = members;
		
		addUpdatedGroup(parent);
	}
	
	/**
	 * Create a new GroupEventPropagator
	 * @param targetManager The TargetManager to use.
	 * @param instanceManager The InstanceManager to use.
	 * @param parent The parent that the member was added to.
	 * @param member The Target Group that was added.
	 */
	public GroupEventPropagator(TargetManager targetManager, TargetInstanceManager instanceManager, TargetGroup parent, AbstractTarget member) {
		this.targetManager = targetManager;
		this.instanceManager = instanceManager;
		this.group = parent;
		this.members = new LinkedList<AbstractTarget>();
		this.members.add(member);
		
		addUpdatedGroup(parent);
	}	
	

	/**
	 * Start the event chain.
	 */
	public void runEventChain() {
		Set<GroupMember> parents = group.getParents();
		for(GroupMember gm: parents) {
			runEventChain(gm.getParent(), true);
		}
	}

	/**
	 * Check if the target has an active schedule.
	 * @param aTarget The Target to check.
	 * @return true if there is an active schedule; otherwise false.
	 */
	private boolean hasActiveSchedule(AbstractTarget aTarget) {
		Date now = new Date();
		for(Schedule schedule : aTarget.getSchedules()) {
			Date next = schedule.getNextExecutionDate();
			if(next != null && next.after(now)) {
				return true;
			}
		}
		return false;
	}
	
	private boolean isMemberActive(AbstractTarget aTarget) {
		int state = aTarget.getState();
		return state == Target.STATE_APPROVED || 
			   state == Target.STATE_COMPLETED ||
			   state == TargetGroup.STATE_ACTIVE;		
	}

	/**
	 * Update the map of updated groups.
	 * @param ancestor The ancestor to add to the updated groups set.
	 */
	private void addUpdatedGroup(TargetGroup ancestor) {
		User owner = ancestor.getOwner();
		Set<TargetGroup> ownersGroups = updatedGroups.get(owner);
		
		if(ownersGroups == null) {
			ownersGroups = new HashSet<TargetGroup>();
			updatedGroups.put(owner, ownersGroups);
		}
		
		ownersGroups.add(ancestor);
	}
	
	
	/**
	 * Run the event chain for the given ancestor.
	 * @param ancestor The ancestor to deal with.
	 * @param propagate True to propagate scheduling; otherwise false.
	 */
	public void runEventChain(TargetGroup ancestor, boolean propagate) {
		addUpdatedGroup(ancestor);
		
		for(AbstractTarget member: members) {
			
			// A one SIP target group needs to be scheduled if adding
			// this item to the group moves the group from Pending to
			// active. If the One SIP group is already scheduled, this will
			// have no effect.
			if(ancestor.getSipType() == TargetGroup.ONE_SIP) {
				if(hasActiveSchedule(ancestor) && isMemberActive(member)) {
					targetManager.scheduleTargetGroup(ancestor);
				}
			}
			
			// An approved member must be scheduled with all of a Many SIP
			// schedules.
			else {
				if(hasActiveSchedule(ancestor) && isMemberActive(member)) {
					if(ancestor.getState() == TargetGroup.STATE_ACTIVE) {
						// As long as we're not already scheduled, schedule us.
						for(Schedule schedule: ancestor.getSchedules()) {
							targetManager.createTargetInstances(member, schedule, ancestor.getSipType() == TargetGroup.MANY_SIP);
						}
					}
					else {
						// Need to schedule the whole group.
						targetManager.scheduleTargetGroup(ancestor);
						ancestor.changeState(TargetGroup.STATE_ACTIVE);
						targetManager.save(ancestor);
						
					}
				}
			}
		}
		
		// Scheduling must be propagated when the target group is
		// a many-sip group. If this is a one-sip group, then the
		// new member will be implicitly picked up through the
		// getSeeds() method call.
		propagate = propagate && ancestor.getSipType() == TargetGroup.MANY_SIP;
		
		Set<GroupMember> parents = ancestor.getParents();
		for(GroupMember gm: parents) {
			runEventChain(gm.getParent(), propagate);
		}		
	}


	/**
	 * @return Returns the updatedGroups.
	 */
	public Map<User, Set<TargetGroup>> getUpdatedGroups() {
		return updatedGroups;
	}

}
