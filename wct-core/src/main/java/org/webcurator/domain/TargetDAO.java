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
package org.webcurator.domain;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.webcurator.core.targets.PermissionCriteria;
import org.webcurator.domain.model.core.AbstractTarget;
import org.webcurator.domain.model.core.Permission;
import org.webcurator.domain.model.core.Schedule;
import org.webcurator.domain.model.core.Seed;
import org.webcurator.domain.model.core.Target;
import org.webcurator.domain.model.core.TargetGroup;
import org.webcurator.domain.model.dto.AbstractTargetDTO;
import org.webcurator.domain.model.dto.GroupMemberDTO;

/**
 * The TargetDAO provides access to targets, target groups and their related objects
 * from the persistent store.
 * @author bbeaumont
 */
public interface TargetDAO extends BaseDAO {	
	/**
	 * Basic save all method. This will save all of the objects in the 
	 * collection to the database but will perform nothing more than the 
	 * Hibernate save/cascade logic. It should not be used to save a collection
	 * of targets or target groups.
	 * @param collection A collection of objects to be saved.
	 */
	public void saveAll(Collection collection);
	
	/**
	 * Save the specified target to the persistent data store.
	 * @param aTarget the target to save
	 */
	public void save(Target aTarget);
	
	/**
	 * Save a target with it's parent associations.
	 * @param aTarget The target.
	 * @param parents The parents.
	 */
	public void save(final Target aTarget, final List<GroupMemberDTO> parents);	
	
	/**
	 * Save a schedule to the database.
	 * @param aSchedule The schedule to save.
	 */
	public void save(Schedule aSchedule);
	
	/**
	 * Get schedules to re-run. This method will fully-populate the schedule,
	 * it's targets, and all child targets (recursively). This method exists
	 * specifically to retrieve the schedules that must be recalculated in order
	 * to populate the target instances xx days in advance.
	 * @return A list of schedules with their targets and child targets 
	 * populated.
	 */
	public List<Schedule> getSchedulesToRun();	
	
	/**
	 * Save a TargetGroup to the database.
	 * @param aTargetGroup The TargetGroup to save
	 * @param withChildren true to process the newChildren/removedChildren
	 *                          lists as well as saving the target group.
	 * @param parents  The parents.
	 */
	public void save(TargetGroup aTargetGroup, boolean withChildren, final List<GroupMemberDTO> parents);
		
	/**
	 * Refresh a persisted object from the database. This is typically
	 * used after performing mass updates.
	 * @param anObject The object to refresh.
	 */
	public void refresh(Object anObject);
	
	/**
	 * Find all the groups that need to be end dated.
	 * @return A List of groups to be end dated.
	 */
	public List<TargetGroup> findEndedGroups();	
	
	/**
	 * Load the persisted target group SIP type from the database.
	 * @return oid The OID of the TargetGroup.
	 */
	public Integer loadPersistedGroupSipType(Long oid);
	
	/**
	 * Returns a list of seeds linked to the given permission.
	 * @param aPermission A Permission record.
	 * @return A List of seeds.
	 */
	public List<Seed> getLinkedSeeds(final Permission aPermission);	

	/**
	 * Transfer all seeds from one permission to another.
	 * @param fromPermissionOid The oid of the permission record to transfer
	 *                          seeds from.
	 * @param toPermissionOid   The oid of the permission record to transfer
	 *                          seeds to.
	 */
	public void transferSeeds(Long fromPermissionOid, Long toPermissionOid);
	
	/**
	 * Search the Permissions.
	 * @param aPermissionCriteria The criteria to use to search the permissions.
	 * @return A Pagination of permission records.
	 */
	public Pagination searchPermissions(PermissionCriteria aPermissionCriteria);	
	
	/**
	 * Load a single AbstractTargetDTO.
	 * @param oid The OID of the DTO to load.
	 * @return The abstractTargetDTO.
	 */
	public AbstractTargetDTO loadAbstractTargetDTO(Long oid);
	
	/**
	 * Delete a pending target.
	 * @param aTarget The Target to be deleted.
	 */
	public void delete(Target aTarget);
	
	
	/**
	 * Delete a TargetGroup as long as it has no Target Instances associated
	 * with it.
	 * @param aTargetGroup The target group to delete.
	 * @return true if deleted; otherwise false.
	 */
	public boolean deleteGroup(TargetGroup aTargetGroup);
	
	/**
	 * Load the specified target from the persistent store.
	 * @param targetOid the id of the target to load
	 * @return the target
	 */
	public Target load(long targetOid);
	
	/**
	 * Load the specified target and all its relationships if 
	 * requested, from the persistent store.
	 * @param targetOid the id of the target to load
	 * @param fullyInitialise true if all the relationships should be loaded
	 * @return the target
	 */
	public Target load(long targetOid, boolean fullyInitialise);
	
	/**
	 * Load the specified target group from the persistent store.
	 * @param targetGroupOid the id of the target group to load
	 * @return the target group
	 */
	public TargetGroup loadGroup(long targetGroupOid);
	
	/**
	 * Load the specified target group and all its relationships if 
	 * requested, from the persistent store.
	 * @param targetGroupOid the id of the target group to load
	 * @param fullyInitialise true if all the relationships should be loaded
	 * @return the target group
	 */
	public TargetGroup loadGroup(long targetGroupOid, boolean fullyInitialise);

	/**
	 * Search for targets that match the specified criteria.
	 * @param pageNumber the page number of the results to return
	 * @param pageSize the page size of the results to return
	 * @param profileOid The OID of a profile that returned targets must be using.
	 * @param agencyName agency that the target belongs to
	 * @return the page of results
	 */
	public Pagination getTargetsForProfile(int pageNumber, int pageSize, Long profileOid, String agencyName);

	/**
	 * Search for targets that match the specified criteria.
	 * @param pageNumber the page number of the results to return
	 * @param pageSize the page size of the results to return
	 * @param profileOid The OID of a profile that returned targets must be using.
	 * @return the page of results
	 */
	public Pagination getAbstractTargetDTOsForProfile(int pageNumber, int pageSize, Long profileOid);

	/**
	 * Search for targets that match the specified criteria.
	 * @param pageNumber the page number of the results to return
	 * @param pageSize the page size of the results to return
	 * @param searchOid The OID to search for.
	 * @param targetName the name of the target
	 * @param states a list of states for the target
	 * @param seed the targets seed
	 * @param username the user that owns the target
	 * @param agencyName agency that the target belongs to
	 * @param description value to search within description
	 * @return the page of results
	 */
	public Pagination search(int pageNumber, int pageSize, Long searchOid, final String targetName, final Set<Integer> states, final String seed, final String username, final String agencyName, final String memberOf, final boolean nondisplayonly, final String sortorder, final String description);
	
	/**
	 * Check that the name given to an AbstractTarget is available to be used.
	 * @param aTarget the AbstactTarget whos name will be checked
	 * @return trus of the name is OK to bve used.
	 */
	public boolean isNameOk(AbstractTarget aTarget);
	
	
	/**
	 * Return a page of members for the specified target group.
	 * @param aTargetGroup the group to return the members of
	 * @param pageNum the page number to return
	 * @return the page of group members
	 */
	public Pagination getMembers(TargetGroup aTargetGroup, int pageNum, int pageSize);
	
	/**
	 * Return a list of Integers representing member states for the specified target group.
	 * @param aTargetGroup the group to return the members states of
	 * @return the list of group member states
	 */
	public List<Integer> getSavedMemberStates(final TargetGroup aTargetGroup);
	
	/**
	 * Return a page of members for the specifed target group.
	 * @param aTargetGroup the group to return the members of
	 * @param pageNum the page number to return
	 * @return the page of group members
	 */
	public Pagination getParents(AbstractTarget aTarget, int pageNum, int pageSize);
	
	/**
	 * Return a list of all immediate parents of the specified target.
	 * @param aTarget The Target to get the parents for.
	 * @return A List of DTO objects.
	 */
	public List<GroupMemberDTO> getParents(final AbstractTarget aTarget);	
	
	/**
	 * Return a page of abstract target DTOs where the name matches the 
	 * name pattern passed.
	 * @param name the name pattern to return matching AbstractTargetDTO's for 
	 * @param pageNumber the page to return
	 * @return the page of AbstractTargetDTOs
	 */
	public Pagination getAbstractTargetDTOs(String name, int pageNumber, int pageSize);
	
	
	/**
	 * Return a page of group DTOs where the name matches the 
	 * name pattern passed.
	 * @param name the name pattern to return matching AbstractTargetDTO's for 
	 * @param pageNumber the page to return
	 * @return the page of AbstractTargetDTOs
	 */
	public Pagination getGroupDTOs(String name, int pageNumber, int pageSize);

	/**
	 * Return a page of group DTOs of the specified types where the name matches the 
	 * name pattern passed.
	 * @param name the name pattern to return matching AbstractTargetDTO's for 
	 * @param types a list of group types 
	 * @param pageNumber the page to return
	 * @return the page of AbstractTargetDTOs
	 */
	public Pagination getSubGroupParentDTOs(String name, List types, int pageNumber, int pageSize);
	
	/**
	 * Return a page of group DTOs of the specified types where the name matches the 
	 * name pattern passed.
	 * @param name the name pattern to return matching AbstractTargetDTO's for 
	 * @param subGroupType a name of the sub-group type 
	 * @param pageNumber the page to return
	 * @return the page of AbstractTargetDTOs
	 */
	public Pagination getNonSubGroupDTOs(final String name, final String subGroupType, final int pageNumber, final int pageSize);
	
	/**
	 * Load the specified AbstractTarget from the persistent store.
	 * @param oid the id of the AbstractTarget to load
	 * @return the AbstractTarget
	 */
	public AbstractTarget loadAbstractTarget(Long oid);
	
	/**
	 * Saerch for target groups that match the specified criteria.
	 * @param pageNumber the page to return
	 * @param name the name pattern of the group
	 * @param searchOid The oid to search for.
	 * @param owner the owner of the group
	 * @param agency the agency the group belongs to
	 * @return the page of groups
	 */
	public Pagination searchGroups(int pageNumber, int pageSize, Long searchOid, String name, String owner, String agency, String memberOf, String groupType, boolean nondisplayonly);
	
	/**
	 * Perform a load of the requested TargetGroup from the persistent store
	 * but ensuring that the object is not loaded from the cache.
	 * @param oid the id of the group to load
	 * @return the TargetGroup
	 */
	public TargetGroup reloadTargetGroup(Long oid);

	
	/**
	 * Perform a load of the requested Target from the persistent store
	 * but ensuring that the object is not loaded from the cache.
	 * @param oid the id of the target to load
	 * @return the Target
	 */
	public Target reloadTarget(Long oid);
	
	/**
	 * Return the date and time of the last scheduled target instance 
	 * for the specified AbstractTarget and Schedule
	 * @param aTarget the AbstractTarget to get the date for
	 * @param aSchedule the schedule to get the date for
	 * @return the last scheduled date
	 */
	public Date getLatestScheduledDate(AbstractTarget aTarget, Schedule aSchedule);
	
	/**
	 * Retrun a Set of seeds for the specified target.
	 * @param aTarget the target to return the seeds for
	 * @return the set of seeds
	 */
	public Set<Seed> getSeeds(Target aTarget);
	
	/**
	 * Return the set of seeds for the specified target group and agency.
	 * Olnly seeds that belong to targets in the specified agency will be
	 * returned in the seed set.
	 * @param aTarget the target group to return the seeds for
	 * @param agencyOid the agency to return the seeds for
	 * @param subGroupTypeName the name of the sub group type
	 * @return the set of seeds
	 */
	public Set<Seed> getSeeds(TargetGroup aTarget, Long agencyOid, String subGroupTypeName);
	
	/**
	 * Return a set of the ids of all the ancestors of the specified child.
	 * @param childOid the oid of the child AbstractTarget
	 * @return the set of ancestors
	 */
	public Set<Long> getAncestorOids(Long childOid);
	
	public Set<AbstractTargetDTO> getAncestorDTOs(Long childOid);	
	
	/**
	 * Return a set of oids for a TargetGroups immediate children
	 * @param groupOid the id of the Target Group
	 * @return the set of child oids
	 */
	public Set<Long> getImmediateChildrenOids(Long groupOid);
	
	/**
	 * Return a count of all the targets that are owned by the 
	 * specified user
	 * @param username the name of the owner to count targets for
	 * @return the count of targets
	 */
	int countTargets(final String username);
	
	/**
	 * Return a count of all the target groups that are owned by the 
	 * specified user
	 * @param username the name of the owner to count target groups for
	 * @return the count of target groups
	 */
	int countTargetGroups(final String username);
}
