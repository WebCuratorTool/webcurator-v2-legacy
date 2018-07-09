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
 * Command class for adding groups to a Target.
 * @author beaumontb
 *
 */
public class TargetGroupsCommand {
	/** The List members action. */
	public static final String ACTION_LIST = "list";
	/** the unlink member action. */
	public static final String ACTION_UNLINK_PARENT = "unlink";	
	
	private int pageNumber = 0;
	private String selectedPageSize;
	private String actionCmd;
	private Long parentOid;

	/**
	 * @return the pageNumber
	 */
	public int getPageNumber() {
		return pageNumber;
	}

	/**
	 * @param pageNumber the pageNumber to set
	 */
	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}

	/**
	 * @return the selectedPageSize
	 */
	public String getSelectedPageSize() {
		return selectedPageSize;
	}

	/**
	 * @param selectedPageSize the selectedPageSize to set
	 */
	public void setSelectedPageSize(String selectedPageSize) {
		this.selectedPageSize = selectedPageSize;
	}

	public String getActionCmd() {
		return actionCmd;
	}

	public void setActionCmd(String actionCmd) {
		this.actionCmd = actionCmd;
	}

	public Long getParentOid() {
		return parentOid;
	}

	public void setParentOid(Long parentOid) {
		this.parentOid = parentOid;
	}
	
	
}
