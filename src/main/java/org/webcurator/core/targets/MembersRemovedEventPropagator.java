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
import org.webcurator.domain.model.core.TargetGroup;

/**
 * The MembersRemovedEventPropagator deals with members that are removed
 * to groups, allowing the TargetManager to (1) unschedule 
 * members as required and (2) notify owners of ancestor groups.
 *  
 * @author bbeaumont
 */
public class MembersRemovedEventPropagator {

	/** The Group to which a member was added. */
	private TargetGroup group = null;
	/** The member that has been added. */
	private List<AbstractTarget> removedTargets = null;

	/** The target instance manager */
	private TargetInstanceManager instanceManager = null;
	
	/** The target manager */
	private TargetManager targetManager = null;
	
	/** A map of the target groups updated and the users who own them. */
	private Map<User, Set<TargetGroup> > updatedGroups = new HashMap<User, Set<TargetGroup> >();
	
	/** A list of Schedule Oids found as we recurse up the tree */
	private Set<Long> ancestorScheduleOids = new HashSet<Long>();
	
	
	/**
	 * Create a new GroupEventPropagator
	 * @param targetManager The TargetManager to use
	 * @param instanceManager The InstanceManager to use.
	 * @param parent The parent that the member was added to.
	 * @param members Set of OIDs that were removed.
	 */
	public MembersRemovedEventPropagator(TargetManager targetManager, TargetInstanceManager instanceManager, TargetGroup parent, List<AbstractTarget> members) {
		this.targetManager = targetManager;
		this.instanceManager = instanceManager;
		this.group = parent;
		this.removedTargets = members;
	}
	
	
	/**
	 * Create a new GroupEventPropagator
	 * @param targetManager The TargetManager to use
	 * @param instanceManager The InstanceManager to use.
	 * @param parent The parent that the member was added to.
	 * @param member The AbstractTarget removed.
	 */
	public MembersRemovedEventPropagator(TargetManager targetManager, TargetInstanceManager instanceManager, TargetGroup parent, AbstractTarget item) {
		this(targetManager, instanceManager, parent, new LinkedList<AbstractTarget>());
		this.removedTargets.add(item);
	}	

	/**
	 * Start the event chain.
	 */
	public void runEventChain() {
		// Recurse up the chain to identify all ancestor schedules and 
		// determine the set of groups that are affected.
		Set<GroupMember> parents = group.getParents();
		for(GroupMember gm: parents) {
			recurseUp(gm.getParent(), true);
		}
		
		// Get all the schedules from the parent group itself, as these
		// also need to be removed from the removed child members.
		for(Schedule schedule : group.getSchedules()) {
			ancestorScheduleOids.add(schedule.getOid());			
		}


		// For any removed child, we need to unschedule any target instances
		// created by parent schedules.
		for(AbstractTarget removedTarget: removedTargets) {
			recurseDown(removedTarget);
		}
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
	
	
	private void recurseDown(AbstractTarget aTarget) {
		// Unschedule the item.
		for(Long oid: ancestorScheduleOids) {
			instanceManager.deleteTargetInstances(aTarget.getOid(), oid);
		}
		
		// For Many SIP groups we must keep going.
		if( aTarget.getObjectType()==AbstractTarget.TYPE_GROUP)
		{
			TargetGroup aTargetGroup = targetManager.loadGroup(aTarget.getOid());
			if(aTargetGroup != null && aTargetGroup.getSipType() == TargetGroup.MANY_SIP) {
				for(GroupMember member: aTargetGroup.getChildren()) {
					recurseDown(member.getChild());
				}
			}
		}
	}
	
	
	
	/**
	 * Run the event chain for the given ancestor.
	 * @param ancestor The ancestor to deal with.
	 * @param propagate True to propagate scheduling; otherwise false.
	 */
	private void recurseUp(TargetGroup ancestor, boolean propagate) {
		addUpdatedGroup(ancestor);
		
		// Scheduling must be propagated when the target group is
		// a many-sip group. If this is a one-sip group, then the
		// new member will be implicitly picked up through the
		// getSeeds() method call.
		propagate = propagate && ancestor.getSipType() == TargetGroup.MANY_SIP;		
		
		if(propagate) {		
			for(Schedule schedule: ancestor.getSchedules()) {
				ancestorScheduleOids.add(schedule.getOid());
			}
		}
		
		Set<GroupMember> parents = ancestor.getParents();
		for(GroupMember gm: parents) {
			recurseUp(gm.getParent(), propagate);
		}		
	}


	/**
	 * @return Returns the updatedGroups.
	 */
	public Map<User, Set<TargetGroup>> getUpdatedGroups() {
		return updatedGroups;
	}

}
