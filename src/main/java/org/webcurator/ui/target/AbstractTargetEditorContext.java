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

import org.webcurator.domain.model.core.AbstractTarget;
import org.webcurator.domain.model.core.Schedule;
import org.webcurator.ui.site.EditorContext;

/**
 * The editor context for an AbstractTarget.
 * @author bbeaumont
 */
public class AbstractTargetEditorContext extends EditorContext {
	private boolean editMode = true;
	private boolean canEdit = false;
	
	private AbstractTarget abstractTarget = null;
	
	public AbstractTargetEditorContext(AbstractTarget aTarget, boolean anEditMode) {
		abstractTarget = aTarget;
		putAllObjects(abstractTarget.getSchedules());
		editMode = anEditMode;
	}	
	
	
	public AbstractTarget getAbstractTarget() {
		return abstractTarget;
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
	
	public List<Schedule> getSortedSchedules() {
		List<Schedule> schedules = new LinkedList<Schedule>();
		schedules.addAll(abstractTarget.getSchedules());
	    //Collections.sort(agents, new AuthorisingAgent.AuthorisingAgentComparator());
	    return schedules;
	}	
	
	// Remove and re-add all schedules back into context cache, ensuring correct map key is being used.
	public void refreshCachedSchedules(){
		removeObjectsOfType(Schedule.class);
		putAllObjects(abstractTarget.getSchedules());
	}
	
	/**
	 * @param canEdit the editMode to set
	 */
	public void setCanEdit(boolean canEdit) {
		this.canEdit = canEdit;
	}
	
	/**
	 * @return the canEdit
	 */
	public boolean isCanEdit() {
		return canEdit;
	}
}
