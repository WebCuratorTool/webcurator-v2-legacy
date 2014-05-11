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
package org.webcurator.ui.target;

import java.util.LinkedList;
import java.util.List;

import org.webcurator.core.targets.TargetManager;
import org.webcurator.domain.model.core.Permission;
import org.webcurator.domain.model.core.Seed;
import org.webcurator.domain.model.core.Target;
import org.webcurator.domain.model.dto.GroupMemberDTO;

/**
 * A target editor context.
 * @author nwaight
 */
public class TargetEditorContext extends AbstractTargetEditorContext {
	private Target target;
	
	private List<Permission> quickPickPermissions;	
	
	private List<GroupMemberDTO> parents;
	
	private boolean editMode = true;
	
	public TargetEditorContext(TargetManager targetManager, Target aTarget, boolean anEditMode) {
		super(aTarget, anEditMode);
		target = aTarget;

		putAllObjects(target.getSeeds());
		
		for(Seed s: target.getSeeds()) {
			putAllObjects(s.getPermissions());
		}
		
		quickPickPermissions = targetManager.getQuickPickPermissions(aTarget);
		putAllObjects(quickPickPermissions);	
		
		editMode = anEditMode;
	}
	
	public List<Permission> getQuickPickPermissions() {
		return quickPickPermissions;
	}	
	
	/**
	 * @return Returns the target.
	 */
	public Target getTarget() {
		return target;
	}
	
	public List<Seed> getSortedSeeds() {
		List<Seed> seeds = new LinkedList<Seed>();
		seeds.addAll(target.getSeeds());
		//Collections.sort(urlList, new UrlPattern.UrlComparator());
		return seeds;
	}

	/**
	 * @return Returns the editMode.
	 */
	public boolean isEditMode() {
		return editMode;
	}

	/**
	 * @param editMode The editMode to set.
	 */
	public void setEditMode(boolean editMode) {
		this.editMode = editMode;
	}

	public List<GroupMemberDTO> getParents() {
		return parents;
	}

	public void setParents(List<GroupMemberDTO> parents) {
		this.parents = parents;
	}	
}
