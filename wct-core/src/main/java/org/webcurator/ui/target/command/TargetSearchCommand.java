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

import java.util.Set;

/**
 * The command object for searching for targets.
 * @author bbeaumont
 */
public class TargetSearchCommand {
	public static final String ACTION_SEARCH = "search";
	public static final String ACTION_DELETE = "delete";
	public static final String ACTION_RESET = "reset";
	
	public static final String SORT_NAME_ASC = "nameasc";
	public static final String SORT_NAME_DESC = "namedesc";
	public static final String SORT_DATE_ASC = "dateasc";
	public static final String SORT_DATE_DESC = "datedesc";

	private int pageNumber = 0; 
	private String selectedPageSize;
	private String name;
	private Set<Integer> states;
	private String seed;
	private String agency;
	private String owner;
	private String memberOf;
	private Long searchOid;
	private Long selectedTargetOid;
	private boolean nondisplayonly=false;
	private String sortorder;
	private String description;
	
	private String actionCmd;
	
	/**
	 * @return Returns the seed.
	 */
	public String getSeed() {
		return seed;
	}
	/**
	 * @param seed The seed to set.
	 */
	public void setSeed(String seed) {
		this.seed = seed;
	}
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
	 * @return Returns the states.
	 */
	public Set<Integer> getStates() {
		return states;
	}
	/**
	 * @param states The states to set.
	 */
	public void setStates(Set<Integer> states) {
		this.states = states;
	}
	/**
	 * @return Returns the agencyOid.
	 */
	public String getAgency() {
		return agency;
	}
	/**
	 * @param agency The agencyOid to set.
	 */
	public void setAgency(String agency) {
		this.agency = agency;
	}
	/**
	 * @return Returns the ownerOid.
	 */
	public String getOwner() {
		return owner;
	}
	/**
	 * @param owner The ownerOid to set.
	 */
	public void setOwner(String owner) {
		this.owner = owner;
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
	/**
	 * @return Returns the selectedTargetOid.
	 */
	public Long getSelectedTargetOid() {
		return selectedTargetOid;
	}
	/**
	 * @param selectedTargetOid The selectedTargetOid to set.
	 */
	public void setSelectedTargetOid(Long selectedTargetOid) {
		this.selectedTargetOid = selectedTargetOid;
	}
	public String getMemberOf() {
		return memberOf;
	}
	public void setMemberOf(String memberOf) {
		this.memberOf = memberOf;
	}
	public Long getSearchOid() {
		return searchOid;
	}
	public void setSearchOid(Long searchOid) {
		this.searchOid = searchOid;
	}
	
	public boolean getNondisplayonly() {
		return nondisplayonly;
	}
	public void setNondisplayonly(boolean nondisplayonly) {
		this.nondisplayonly = nondisplayonly;
	}
	
	/**
	 * @return Returns the sortorder.
	 */
	public String getSortorder() {
		return sortorder;
	}

	/**
	 * @param sortorder The sortorder to set.
	 */

	public void setSortorder(String sortorder) {
		this.sortorder = sortorder;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

}
