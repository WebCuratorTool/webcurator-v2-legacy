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
package org.webcurator.ui.groups;

import java.util.List;

import org.webcurator.domain.model.core.TargetGroup;
import org.webcurator.domain.model.dto.GroupMemberDTO;
import org.webcurator.ui.target.AbstractTargetEditorContext;

/** 
 * The editor context for target groups.
 * @author bbeaumont
 */
public class GroupsEditorContext extends AbstractTargetEditorContext {
	/** the target group. */
	private TargetGroup targetGroup = null;
	
	private List<GroupMemberDTO> parents;
	private List<Long> targetsToMove;
	
	
	/**
	 * Standard constructor.
	 * @param targetGroup
	 */
	public GroupsEditorContext(TargetGroup targetGroup, boolean editMode) {
		super(targetGroup, editMode);
		this.targetGroup = targetGroup;
	}

	/**
	 * @return Returns the targetGroup.
	 */
	public TargetGroup getTargetGroup() {
		return targetGroup;
	}

	/**
	 * @param targetGroup The targetGroup to set.
	 */
	public void setTargetGroup(TargetGroup targetGroup) {
		this.targetGroup = targetGroup;
	}
	
	public List<GroupMemberDTO> getParents() {
		return parents;
	}

	public void setParents(List<GroupMemberDTO> parents) {
		this.parents = parents;
	}

	public void setTargetsToMove(List<Long> targetsToMove) {
		this.targetsToMove = targetsToMove;
	}

	public List<Long> getTargetsToMove() {
		return targetsToMove;
	}	
	
}
