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

/**
 * The default target command.
 * @author bbeaumont
 */
public class TargetDefaultCommand {
	public static final String MODE_EDIT = "edit";
	public static final String MODE_VIEW = "view";
	public static final String PARAM_OID = "targetOid";
	
	private Long targetOid;
	private String mode;
	private boolean copyMode = false;
	
	/**
	 * @return Returns the targetOid.
	 */
	public Long getTargetOid() {
		return targetOid;
	}

	/**
	 * @param targetOid The targetOid to set.
	 */
	public void setTargetOid(Long targetOid) {
		this.targetOid = targetOid;
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
