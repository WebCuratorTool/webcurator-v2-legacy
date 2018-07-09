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
 * The search for Target Groups Command object.
 * @author bbeaumont
 */
public class SearchCommand {
	/** The Delete Action */
	public static final String ACTION_DELETE = "delete";
	/** The reset Action */
	public static final String ACTION_RESET = "reset";
	
	/** The action code */
	private String actionCmd = null;
	/** the search result page number. */
	private int pageNumber = 0;
	/** the search result page size. */
	private String selectedPageSize;
	/** the target group name search field. */
	private String name = null;
	/** the target group agency search field. */
	private String agency = null;
	/** the target group owner search field. */
	private String owner = null;
	/** The OID of the selected group for deletion */
	private Long deletedGroupOid = null;
	/** The parent group search field */
	private String memberOf = null;
	/** The Group Type search field */
	private String groupType = null;
	/** The OID to search for */
	private Long searchOid = null;
	private boolean nondisplayonly = false;

	/**
	 * Tests if the given action has been selected.
	 * @param actionCmd The action command string to test for.
	 * @return True if the given action has been selected; otherwise false.
	 */
	public boolean isAction(String actionCmd) {
		return actionCmd.equals(this.actionCmd);
	}
	
	/**
	 * @return Returns the agency.
	 */
	public String getAgency() {
		return agency;
	}
	/**
	 * @param agency The agency to set.
	 */
	public void setAgency(String agency) {
		this.agency = agency;
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
	 * @return Returns the owner.
	 */
	public String getOwner() {
		return owner;
	}
	/**
	 * @param owner The owner to set.
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
	 * @return Returns the deletedGroupOid.
	 */
	public Long getDeletedGroupOid() {
		return deletedGroupOid;
	}

	/**
	 * @param deletedGroupOid The deletedGroupOid to set.
	 */
	public void setDeletedGroupOid(Long deletedGroupOid) {
		this.deletedGroupOid = deletedGroupOid;
	}

	public String getMemberOf() {
		return memberOf;
	}

	public void setMemberOf(String memberOf) {
		this.memberOf = memberOf;
	}

	public boolean getNondisplayonly() {
		return nondisplayonly;
	}
	public void setNondisplayonly(boolean nondisplayonly) {
		this.nondisplayonly = nondisplayonly;
	}

	public String getGroupType() {
		return groupType;
	}

	public void setGroupType(String groupType) {
		this.groupType = groupType;
	}

	public Long getSearchOid() {
		return searchOid;
	}

	public void setSearchOid(Long searchOid) {
		this.searchOid = searchOid;
	}
}
