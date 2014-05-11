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
package org.webcurator.ui.groups.command;

/**
 * The default Target Group Command object.
 * @author bbeaumont
 */
public class DefaultCommand {
	/** A constant for the edit mode. */
	public static final String MODE_EDIT = "edit";
	/** A constant for the view mode. */
	public static final String MODE_VIEW = "view";	
	/** A constant for the copy mode. */	
	private boolean copyMode = false;
	/** the target group id field. */
	private Long targetGroupOid;
	/** the mode field. */
	private String mode;
	
	/**
	 * @return Returns the targetGroupOid.
	 */
	public Long getTargetGroupOid() {
		return targetGroupOid;
	}

	/**
	 * @param targetGroupOid The targetGroupOid to set.
	 */
	public void setTargetGroupOid(Long targetGroupOid) {
		this.targetGroupOid = targetGroupOid;
	}

	public boolean isEditMode() {
		return MODE_EDIT.equals(mode);
	}

	/**
	 * @return Returns the mode.
	 */
	public String getMode() {
		return mode;
	}

	/**
	 * @param mode The mode to set.
	 */
	public void setMode(String mode) {
		this.mode = mode;
	}

	/**
	 * @return Returns the copyMode.
	 */
	public boolean isCopyMode() {
		return copyMode;
	}

	/**
	 * @param copyMode The copyMode to set.
	 */
	public void setCopyMode(boolean copyMode) {
		this.copyMode = copyMode;
	}
}
