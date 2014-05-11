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
package org.webcurator.ui.target.command;

import org.webcurator.domain.model.core.Target;

/**
 * The command object for the Target access tab.
 * @author oakleigh_sk
 */
public class TargetAccessCommand {
	
	public static final String PARAM_DISPLAY_TARGET = "displayTarget";
	public static final String PARAM_ACCESS_ZONE = "accessZone";
	public static final String PARAM_DISPLAY_NOTE = "displayNote";
	public static final String PARAM_DISPLAY_CHANGE_REASON = "displayChangeReason";
	public static final String GROUP_TYPE = "Group";
	public static final String TARGET_TYPE = "Target";

	
    /** The tab type. */
    private String tabType;
    /** The targets 'target introductory display note'. */
    private String displayNote;
    /** The targets 'display change reason'. */
    private String displayChangeReason;
    /** The ID of the access zone */
    private int accessZone;
    /** Whether to display the target */
    private boolean displayTarget;

    
	/**
	 * @return Returns the displayTarget option.
	 */
	public boolean isDisplayTarget() {
		return displayTarget;
	}

	/**
	 * @param displayTarget The displayTarget to set.
	 */
	public void setDisplayTarget(boolean displayTarget) {
		this.displayTarget = displayTarget;
	}

	/**
	 * @return Returns the access zone.
	 */
	public int getAccessZone() {
		return accessZone;
	}

	/**
	 * @param accessZone The access zone to set.
	 */
	public void setAccessZone(int accessZone) {
		this.accessZone = accessZone;
	}

	/**
	 * @return Returns the display note.
	 */
	public String getDisplayNote() {
		return displayNote;
	}
	/**
	 * @param displayNote The display note to set.
	 */
	public void setDisplayNote(String displayNote) {
		this.displayNote = displayNote;
	}

	/**
	 * @return Returns the displayChangeReason.
	 */
	public String getDisplayChangeReason() {
		return displayChangeReason;
	}
	/**
	 * @param displayChangeReason The display change reason to set.
	 */
	public void setDisplayChangeReason(String displayChangeReason) {
		this.displayChangeReason = displayChangeReason;
	}


	/**
	 * @return Returns the tabType note.
	 */
	public String getTabType() {
		return tabType;
	}
	/**
	 * @param tabType The tabType to set.
	 */
	public void setTabType(String tabType) {
		this.tabType = tabType;
	}


	public static TargetAccessCommand buildFromModel(Target model) {
    	TargetAccessCommand command = new TargetAccessCommand();
    	command.setDisplayTarget(model.isDisplayTarget());
    	command.setDisplayNote(model.getDisplayNote());
    	command.setDisplayChangeReason(model.getDisplayChangeReason());
    	command.setAccessZone(model.getAccessZone());
    	return command;		
	}
	
    
}
