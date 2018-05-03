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
package org.webcurator.ui.profiles.command;

/**
 * Command class for the List Profiles screen.
 * @author bbeaumont
 *
 */
public class ProfileListCommand {
	
	public static String ACTION_LIST = "list";
	public static String ACTION_FILTER = "filter";
	public static String ACTION_IMPORT = "import";
	
	/** True to show inactive profiles; otherwise false */
	private boolean showInactive = false;

	private String defaultAgency = "";
	private String actionCommand = ACTION_LIST;
	private String harvesterType = null;


	/**
	 * @return Returns the showInactive.
	 */
	public boolean isShowInactive() {
		return showInactive;
	}

	/**
	 * @param showInactive The showInactive to set.
	 */
	public void setShowInactive(boolean showInactive) {
		this.showInactive = showInactive;
	}
	
	/**
	 * @return Returns the defaultAgency.
	 */
	public String getDefaultAgency() {
		return defaultAgency;
	}

	/**
	 * @param defaultAgency The defaultAgency to set.
	 */
	public void setDefaultAgency(String defaultAgency) {
		this.defaultAgency = defaultAgency;
	}
	
	/**
	 * @return Returns the actionCommand.
	 */
	public String getActionCommand() {
		return actionCommand;
	}

	/**
	 * @param actionCommand The actionCommand to set.
	 */
	public void setActionCommand(String actionCommand) {
		this.actionCommand = actionCommand;
	}

	public String getHarvesterType() {
		return harvesterType;
	}

	public void setHarvesterType(String harvesterType) {
		this.harvesterType = harvesterType;
	}
}
