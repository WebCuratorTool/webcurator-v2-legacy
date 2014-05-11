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

import java.util.Set;

/**
 * Command object used for harvest authorisations search.
 * @author bbeaumont
 */
public class SiteSearchCommand {
	public static final String SESSION_SITE_CRITERIA = "siteSearchCriteria";
	
	public static final String ACTION_RESET = "reset";
	
	public static final String PARAM_TITLE = "title";	
	public static final String PARAM_ACTION = "cmdAction";
	public static final String PARAM_PAGENO = "pageNo";
	public static final String PARAM_PAGESIZE = "selectedPageSize";
	public static final String PARAM_ORDERNO = "orderNo";
	public static final String PARAM_PERMS_FILE_REF = "permsFileRef";
	public static final String PARAM_URL_PATTERN = "urlPattern";
	public static final String PARAM_AGENT = "agentName";
	public static final String PARAM_SHOW_DISABLED = "showDisabled";
	public static final String PARAM_SEARCH_OID = "searchOid";
	
	public static final String SORT_NAME_ASC = "nameasc";
	public static final String SORT_NAME_DESC = "namedesc";
	public static final String SORT_DATE_ASC = "dateasc";
	public static final String SORT_DATE_DESC = "datedesc";
	
	private String title;
	private Set<Integer> states;
	private String orderNo;
	private String agency;
	private String permsFileRef;
	private String urlPattern;
	private String agentName;
	private String cmdAction;
	private Long searchOid = null;
	private int pageNo;
	private String selectedPageSize;
	private boolean showDisabled;
	private String sortorder; 

	/**
	 * @return Returns the title.
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title The title to set.
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the cmdAction
	 */
	public String getCmdAction() {
		return cmdAction;
	}

	/**
	 * @param cmdAction the cmdAction to set
	 */
	public void setCmdAction(String cmdAction) {
		this.cmdAction = cmdAction;
	}

	/**
	 * @return the pageNo
	 */
	public int getPageNo() {
		return pageNo;
	}

	/**
	 * @param pageNo the pageNo to set
	 */
	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
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
	 * @return the agentName
	 */
	public String getAgentName() {
		return agentName;
	}

	/**
	 * @param agentName the agentName to set
	 */
	public void setAgentName(String agentName) {
		this.agentName = agentName;
	}

	/**
	 * @return the orderNo
	 */
	public String getOrderNo() {
		return orderNo;
	}

	/**
	 * @param orderNo the orderNo to set
	 */
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	/**
	 * @return the agency
	 */
	public String getAgency() {
		return agency;
	}

	/**
	 * @param agency the agency to set
	 */
	public void setAgency(String agency) {
		this.agency = agency;
	}

	/**
	 * @return the permsFileRef
	 */
	public String getPermsFileRef() {
		return permsFileRef;
	}

	/**
	 * @param permsFileRef the permsFileRef to set
	 */
	public void setPermsFileRef(String permsFileRef) {
		this.permsFileRef = permsFileRef;
	}

	/**
	 * @return the urlPattern
	 */
	public String getUrlPattern() {
		return urlPattern;
	}

	/**
	 * @param urlPattern the urlPattern to set
	 */
	public void setUrlPattern(String urlPattern) {
		this.urlPattern = urlPattern;
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
	 * @return the showDisabled
	 */
	public boolean isShowDisabled() {
		return showDisabled;
	}

	/**
	 * @param showDisabled the showDisabled to set
	 */
	public void setShowDisabled(boolean showDisabled) {
		this.showDisabled = showDisabled;
	}

	public Long getSearchOid() {
		return searchOid;
	}

	public void setSearchOid(Long searchOid) {
		this.searchOid = searchOid;
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
	
}
