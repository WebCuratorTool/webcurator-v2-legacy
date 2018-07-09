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
package org.webcurator.ui.site.command;

/**
 * Command object used for seaching for authorising agents.
 * @author bbeaumont
 */
public class AgencySearchCommand {
	
	public static final String PARAM_NAME = "name";
	public static final String PARAM_ACTION_CMD = "actionCmd";
	public static final String PARAM_SELECTED_OIDS = "selectedOids";
	
	public static final String ACTION_CANCEL = "cancel";
	public static final String ACTION_ADD    = "add";
	public static final String ACTION_SEARCH = "search";
		
	private String name;
	private long[] selectedOids;
	private String actionCmd;
	private int pageNumber;

	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name The name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return Returns the selectedOids.
	 */
	public long[] getSelectedOids() {
		return selectedOids;
	}

	/**
	 * @param selectedOids The selectedOids to set.
	 */
	public void setSelectedOids(long[] selectedOids) {
		this.selectedOids = selectedOids;
	}

	/**
	 * @return Returns the actionCmd.
	 */
	public String getActionCmd() {
		return actionCmd;
	}

	/**
	 * @param actionCmd The actionCmd to set.
	 */
	public void setActionCmd(String actionCmd) {
		this.actionCmd = actionCmd;
	}

	/**
	 * @return Returns the pageNumber.
	 */
	public int getPageNumber() {
		return pageNumber;
	}

	/**
	 * @param pageNumber The pageNumber to set.
	 */
	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}

	
	
}

