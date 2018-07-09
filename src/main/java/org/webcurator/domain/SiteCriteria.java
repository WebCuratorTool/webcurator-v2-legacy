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
package org.webcurator.domain;

import java.util.Set;

/**
 * The criteria object used when searching for Harvest Authorisations. 
 * @author nwaight
 */
public class SiteCriteria {
	/** the title of the site to search for. */
	private String title;
	/** the library order number. */
	private String orderNo;
	/** the name of the agent. */
	private String agentName;
	/** the flag to indicate that disabled sites should be returned. */
	private boolean showDisabled;
	/** The OID to search for */
	private Long searchOid;
	/** the site's agency. */
	private String agency;
	/** the permission file reference. */
	private String permsFileRef;
	/** the URL Pattern. */
	private String urlPattern;
	/** the permission states. */
	private Set<Integer> states;
	/** the sort order. */
	private String sortorder;
	
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
	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}
	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	public Long getSearchOid() {
		return searchOid;
	}
	public void setSearchOid(Long searchOid) {
		this.searchOid = searchOid;
	}
	/**
	 * @return the sortorder
	 */
	public String getSortorder() {
		return sortorder;
	}
	/**
	 * @param sortorder the sortorder to set
	 */
	public void setSortorder(String sortorder) {
		this.sortorder = sortorder;
	}

}
