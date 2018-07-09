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

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.webcurator.auth.AuthorityManager;
import org.webcurator.domain.Pagination;
import org.webcurator.domain.SiteDAO;
import org.webcurator.domain.TargetDAO;
import org.webcurator.domain.model.auth.User;
import org.webcurator.domain.model.core.AbstractTarget;
import org.webcurator.domain.model.core.Annotation;
import org.webcurator.domain.model.core.Permission;
import org.webcurator.domain.model.core.Schedule;
import org.webcurator.domain.model.core.Seed;
import org.webcurator.domain.model.core.Target;
import org.webcurator.domain.model.core.TargetGroup;
import org.webcurator.domain.model.core.TargetInstance;
import org.webcurator.domain.model.dto.GroupMemberDTO;
import org.webcurator.domain.model.dto.PermissionSeedDTO;
import org.webcurator.ui.target.TargetEditorContext;

/**
 * The manager for accessing Target and Target Group data.
 * @author bbeaumont
 */
public interface TargetManager {
	
	/**
	 * Allow a target to have multiple primary seeds. Configure on the 
	 * TargetManager bean in wct_core.xml
	 * @return false will allow only one primary seed per target.
	 */
	public boolean getAllowMultiplePrimarySeeds();
	
	/**
	 * Save the target to the database.
	 * @param aTarget The target to save.
	 */
	void save(Target aTarget);
	
	/**
	 * Save the target to the database.
	 * @param aTarget The target to save.
	 * @param parent An array of GroupMember parent DTO objects.
	 */
	void save(Target aTarget, List<GroupMemberDTO> parents);	
	
	/**
	 * Save TargetGroup to the database
	 * @param aTargetGroup The group to save
	 */
	void save(TargetGroup aTargetGroup);

	/**
	 * Save TargetGroup to the database
	 * @param aTargetGroup The group to save
	 * @param parent An array of GroupMember parent DTO objects.
	 */
	void save(TargetGroup aTargetGroup, List<GroupMemberDTO> parents);

	/**
	 * Return an array of the next allowed states for 
	 * the specified Target.
	 * @param aTarget the target to return the states for
	 * @return the array of allowed states
	 */
	int[] getNextStates(Target aTarget);
	
	/**
	 * Check that the selected state is in the list of allowed
	 * next states for this target
	 * @param aTarget the target to check
	 * @param nextState the next state
	 * @return true of the state change is allowed
	 */
	boolean allowStateChange(Target aTarget, int nextState);

	/**
	 * Load a target from the database.
	 * @param oid The OID of the target.
	 * @return The target.
	 */
	Target load(Long oid);

	/**
	 * Load a target from the database.
	 * @param oid The OID of the target.
	 * @param loadFully Set to true to fully load the object; false to lazy-
	 * load.
	 * @return The target.
	 */
	Target load(Long oid, boolean loadFully);
	
	/**
	 * Load a target group from the database.
	 * @param oid The OID of the target group.
	 * @return The target group.
	 */
	TargetGroup loadGroup(Long oid);

	/**
	 * Load a target group from the database.
	 * @param oid The OID of the target group.
	 * @param loadFully Set to true to fully load the object; false to lazy-
	 * load.
	 * @return The target group.
	 */
	TargetGroup loadGroup(Long oid, boolean loadFully);	

	/**
	 * Load the quick permission.
	 * @return An ordered list of quick permissions.
	 */
	List<Permission> getQuickPickPermissions(Target aTarget);

	/**
	 * Find permissions by Site Title
	 * @param aTarget The target to find a permission for.
	 * @param aSiteTitle The name of the site.
	 * @param aPageNumber The page number to return.
	 * @return A Pagination of Permissions.
	 */
	Pagination findPermissionsBySiteTitle(Target aTarget, String aSiteTitle,
			int aPageNumber);

	/**
	 * Find permissions by Site
	 * @param aTarget The target to find the permissions for.
	 * @param aUrl The name of the site.
	 * @param aPageNumber The page number to return.
	 * @return A Pagination of Permissions.
	 */
	Pagination findPermissionsByUrl(Target aTarget, String aUrl, int aPageNumber);

	/**
	 * Load a permission from the TargetEditorContext, falling back to the
	 * database if necessary.
	 * @param ctx The TargetEditorContext.
	 * @param identity The identity of the permission.
	 * @return The Permission object.
	 */
	Permission loadPermission(TargetEditorContext ctx,
			String identity);

	/**
	 * Load a permission directly from the database.
	 * @param oid The OID of the permission to load.
	 * @return The Permission object.
	 */
	Permission loadPermission(Long oid);

	/**
	 * Check that the target name is not a duplicate.
	 * @param aTarget The target to check.
	 * @return true if the name is okay; otherwise false.
	 */
	boolean isNameOk(AbstractTarget aTarget);

	/**
	 * Sets the Target DAO.
	 * @param targetDao The targetDao to set.
	 */
	void setTargetDao(TargetDAO targetDao);

	/**
	 * @param authMgr The authMgr to set.
	 */
	void setAuthMgr(AuthorityManager authMgr);
		
	/**
	 * Check to see that the AbstractTarget associated with this TargetInstance
	 * is in the approved state and all the seeds current permissions are approved.
	 * @param aTargetInstance the target instance to check
	 * @return true if the TargetInstance can be harvested
	 */
	boolean isTargetHarvestable(TargetInstance aTargetInstance);

	/**
	 * Get the members of the target group.
	 * @param aTargetGroup The target group.
	 * @return A pagination of results.
	 */
	public Pagination getMembers(TargetGroup aTargetGroup, int pageNum, int pageSize);
	
	/**
	 * Get the parents of the target.
	 * @param aTarget The child target.
	 * @return A pagination of results.
	 */
	public Pagination getParents(AbstractTarget aTarget, int pageNum, int pageSize);	
	
	/**
	 * Detect and update TargetGroups that must be made inactive due to their
	 * end date having been passed.
	 */
	public void endDateGroups(); 	
	
	/**
	 * @param siteDao The siteDao to set.
	 */
	public void setSiteDao(SiteDAO siteDao);
	
	/**
	 * Transfer Seeds from Permission A to Permission B.
	 * @param fromPermissionOid The permission to transfer seeds from.
	 * @param toPermissionOid The permission to transfer the seeds to.
	 * @return The number of seeds updated.
	 */
	public int transferSeeds(Permission fromPermissionOid, Permission toPermissionOid);
	
	/**
	 * Search the Permissions.
	 * @param aPermissionCriteria The criteria to use to search the permissions.
	 * @return A Pagination of permission records.
	 */
	public Pagination searchPermissions(PermissionCriteria aPermissionCriteria);
	
	/** 
	 * Retrieve a List of active parent groups. Note that this method assumes
	 * that there is an active Hibernate Session available so that any
	 * lazy associations can be initialised.
	 * @param aTargetInstance The target instance to find the parent groups for.
	 * @return A List of active parent groups.
	 */
	public List<TargetGroup> getActiveParentGroups(TargetInstance aTargetInstance);
	
	/**
	 * Retrieve a list of all the active permissions. Note that this method assumes
	 * that there is an active Hibernate Session available so that any
	 * lazy associations can be initialised.
	 * @param aTargetInstance The target instance to find the permissions for
	 * @return collection of permission seed dtos
	 */
	public Collection<PermissionSeedDTO> getActivePermissions(TargetInstance aTargetInstance);
	
	/**
	 * Copy a Target.
	 * @param aTarget The target to copy.
	 * @return A copy of the target.
	 */
	public Target copy(Target aTarget);
	
	/** 
	 * Copy a TargetGroup.
	 * @param aTargetGroup The target group to copy.
	 * @return a copy of the gropu.
	 */
	public TargetGroup copy(TargetGroup aTargetGroup);
	
	/**
	 * Deletes a pending target.
	 * @param aTarget The Target to be deleted.
	 */
	void deleteTarget(Target aTarget);
	
	/**
	 * Deletes a TargetGroup by OID.
	 * @param aTargetGroup the group to be deleted.
	 * @return true if the target group was deleted.
	 */
	boolean deleteTargetGroup(TargetGroup aTargetGroup);
	
	/**
	 * Look up and create target instnaces for all the schedules that must be 
	 * run to create target instances for the given period.
	 */
	public void processSchedulesJob();
	
	/**
	 * Return a list of Annotations associated with the specified AbstractTarget.
	 * @param aTarget the AbstractTarget to return the Annotations for
	 * @return the list of annotations
	 */
	public List<Annotation> getAnnotations(AbstractTarget aTarget);

	/**
	 * Return a page of AbstactTarget DTO's that match the specified name and page number.
	 * @param name the name of the AbstractTarget
	 * @param pageNumber the page number of the result to return
	 * @return the page of results
	 */
	public Pagination getAbstractTargetDTOs(String name, int pageNumber, int pageSize);
	
	/**
	 * Return a page of AbstractTarget Group DTO's that match the specified 
	 * name and page number.
	 * @param name the name of the Group
	 * @param pageNumber the page number of the result to return
	 * @return the page of results
	 */
	public Pagination getGroupDTOs(String name, int pageNumber, int pageSize);	
	
	/**
	 * Return a page of AbstractTarget Group DTO's that match the specified 
	 * name and page number and are valid Sub-Group parents.
	 * @param name the name of the Group
	 * @param pageNumber the page number of the result to return
	 * @return the page of results
	 */
	public Pagination getSubGroupParentDTOs(String name, int pageNumber, int pageSize);
	
	/**
	 * Return a page of AbstractTarget Group DTO's that match the specified 
	 * name and page number and are not Sub-Groups.
	 * @param name the name of the Group
	 * @param pageNumber the page number of the result to return
	 * @return the page of results
	 */
	public Pagination getNonSubGroupDTOs(String name, int pageNumber, int pageSize);
	
	/**
	 * Create a new group member DTO for the specified group and child oid.
	 * @param group the group the child will be/is a member of
	 * @param childOid the child/member of the group
	 * @return the GroupMemberDTO
	 */
	public GroupMemberDTO createGroupMemberDTO(TargetGroup group, Long childOid);
	
	/**
	 * Create a new group member DTO for the specified parent oid and child.
	 * @param groupOid The OID of the parent group
	 * @param childOid The child target
	 * @return the GroupMemberDTO
	 */	
	public GroupMemberDTO createGroupMemberDTO(Long groupOid, Target child);	
	
	/**
	 * Search for groups that match the specified criteria.
	 * @param pageNumber the page number of the results to return
	 * @param pageSize the page size of the results to return
	 * @param searchOid the oid to search for.
	 * @param name the name of the group
	 * @param owner the owner of the group
	 * @param agency the agency the group belongs to
	 * @param memberOf the group the group belongs to
	 * @param groupType the type of the group
	 * @return the page of group results
	 */
	public Pagination searchGroups(int pageNumber, int PageSize, Long searchOid, String name, String owner, String agency, String memberOf, String groupType, boolean nondisplayonly);
	
	/**
	 * Return the set of seeds for the specifed TargetInstance
	 * @param aTargetInstance the target instance to return the seeds for
	 * @return the list of seeds
	 */
	public Set<Seed> getSeeds(TargetInstance aTargetInstance);
	/**
	 * Return a list of seeds for the specified AbstractTarget
	 * where if the AbstractTarget is a group only the seeds that 
	 * belong to the specified agency are returned.
	 * @param aTarget the AbstractTarget to return the seeds for
	 * @param agencyOid the agency to return the seeds for
	 * @return the list of seeds
	 */
	public Set<Seed> getSeeds(AbstractTarget aTarget, Long agencyOid);
	
	/**
	 * FIXME bbeaumont to validate this
	 * Create the scheduled target instances required for the 
	 * specified TargetGroup
	 * @param aGroup the group to schedule
	 */
	public void scheduleTargetGroup(TargetGroup aGroup);
	
	/**
	 * Create the TargetInstances for the specified AbstractTarget and schedule. 
	 * @param aTarget the AbstractTarget to create TargetInstances for
	 * @param aSchedule the schedule to create TargetInstances based on
	 * @param checkAgency true if only children belonging to the schedules agency are scheduled 
	 * @return the set of TargetInstances
	 */
	public void createTargetInstances(AbstractTarget aTarget, Schedule aSchedule, boolean checkAgency);
	
	/**
	 * Return a Set of oids for all the ancestors of the specified AbstractTarget
	 * @param child the child to get the ancestor for
	 * @return the list of ancestor oids
	 */
	public Set<Long> getAncestorOids(AbstractTarget child);
	
	/**
	 * Check to see of the member is already part of a group
	 * @param group the group to check membership on
	 * @param memberOid the oid of the member to check
	 * @return true if the member already exists
	 */
	public boolean isDuplicateMember(TargetGroup group, Long memberOid);
	
	/**
	 * Load the AbstractTarget from the database 
	 * @param oid the oid of the AbstractTarget to load
	 * @return the AbstractTarget
	 */
	public AbstractTarget loadAbstractTarget(Long oid);
	
	/**
	 * Count the number of Targets owned by the specified user. 
	 * @param aUser the user to count targets for
	 * @return the number of targets owned by the user
	 */
	int countTargets(User aUser);
	
	/**
	 * Count the number of Targets Groups owned by the specified user. 
	 * @param aUser the user to count target groups for
	 * @return the number of target groups owned by the user
	 */
	int countTargetGroups(User aUser);
	
	/**
	 * Return a list of all immediate parents of the specified target.
	 * @param aTarget The Target to get the parents for.
	 * @return A List of DTO objects.
	 */
	List<GroupMemberDTO> getParents(final AbstractTarget aTarget);
	
	/**
	 * Move a list of targets from a source group to a destination group.
	 * @param sourceGroup The TargetGroup to move the targets from.
	 * @param targetGroup The TargetGroup to move the targets to.
	 * @param targetsToMove The List of Targets to move.
	 */
	void moveTargets(TargetGroup sourceGroup, TargetGroup targetGroup, List<Long> targetsToMove);
}