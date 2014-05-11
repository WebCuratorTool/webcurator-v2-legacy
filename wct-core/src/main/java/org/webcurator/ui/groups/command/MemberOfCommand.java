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
 * The MemberOf Target Group Tab Command object.
 * @author oakleigh_sk
 */
public class MemberOfCommand {
	/** The List groups this group is a member of action. */
	public static final String ACTION_LIST = "list";
	/** the unlink from group action. */
	public static final String ACTION_UNLINK_FROM_GROUP = "unlink";
	/** the group list page number. */
	private int pageNumber = 0;
	/** the members of list page size. */
	private String selectedPageSize;
	/** the action command. */
	private String actionCmd = null;
	/** the child group id. */
	private Long childOid = null;

	/**
	 * @return Returns the childOid.
	 */
	public Long getChildOid() {
		return childOid;
	}

	/**
	 * @param childOid The childOid to set.
	 */
	public void setChildOid(Long childOid) {
		this.childOid = childOid;
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
}
