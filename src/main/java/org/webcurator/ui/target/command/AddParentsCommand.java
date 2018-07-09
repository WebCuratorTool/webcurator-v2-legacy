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

import org.webcurator.domain.model.dto.GroupMemberDTO;
import java.util.*;

/**
 * Command object used when adding members to a Target Group.
 * @author bbeaumont
 */
public class AddParentsCommand {
	/** The search command action. */
	public static final String ACTION_SEARCH = "Search";
	/** The cancel command action. */
	public static final String ACTION_CANCEL = "Cancel";
	/** The add command action. */
	public static final String ACTION_ADD_PARENTS = "Add";
	/** The remove command action. */
	public static final String ACTION_REMOVE = "Remove";
	/** The add command action. */
	public static final String SESSION_SELECTIONS = "TargetParentSelections";
	/** The add command action. */
	public static final String PARAM_SELECTIONS = "selections";
	/** The search field. */
	private String search = null;
	/** the page number of the search result field. */
	private int pageNumber = 0;
	/** the search result page size. */
	private String selectedPageSize;
	/** the action command field. */
	private String actionCmd = null;
	/** the selected group member oids field.*/
	private long[] parentOids = null;
	/** the selected parent name to remove.*/
	private int parentIndex = 0;
	/** the selected parent name to remove.*/
	private int selectedCount = 0;
	
	public AddParentsCommand()
	{
		parentOids = new long[0];
	}
	
	/**
	 * @return Returns the action.
	 */
	public String getActionCmd() {
		return actionCmd;
	}
	/**
	 * @param action The action to set.
	 */
	public void setActionCmd(String action) {
		this.actionCmd = action;
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
	 * @return Returns the search.
	 */
	public String getSearch() {
		return search;
	}
	/**
	 * @param search The search to set.
	 */
	public void setSearch(String search) {
		this.search = search;
	}
	/**
	 * @return Returns the memberOids.
	 */
	public long[] getParentOids() {
		return parentOids;
	}
	/**
	 * @param memberOids The memberOids to set.
	 */
	public void setParentOids(long[] parentOids) {
		this.parentOids = parentOids;
	}
	/**
	 * @return Returns the parentIndex.
	 */
	public int getParentIndex() {
		return parentIndex;
	}
	/**
	 * @param parentIndex The parentIndex to set.
	 */
	public void setParentIndex(int parentIndex) {
		this.parentIndex = parentIndex;
	}
	/**
	 * @return Returns the selectedCount.
	 */
	public int getSelectedCount() {
		return selectedCount;
	}
	/**
	 * @param selectedCount The selectedCount to set.
	 */
	public void setSelectedCount(int selectedCount) {
		this.selectedCount = selectedCount;
	}
}
