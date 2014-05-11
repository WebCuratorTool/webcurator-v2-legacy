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
package org.webcurator.core.targets;

/**
 * The criteria for searching for a permission.
 * @author bbeaumont
 */
public class PermissionCriteria {
	/** The OID of the agency to search */
	private Long agencyOid;
	/** The name of the site to search */
	private String siteName;
	/** The URL pattern to search */
	private String urlPattern;
	/** The pagenumber of the page for which to display the results. */
	private int pageNumber;
	
	/**
	 * @return Returns the siteName.
	 */
	public String getSiteName() {
		return siteName;
	}
	/**
	 * @param siteName The siteName to set.
	 */
	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}
	/**
	 * @return Returns the url.
	 */
	public String getUrlPattern() {
		return urlPattern;
	}
	/**
	 * @param url The url to set.
	 */
	public void setUrlPattern(String url) {
		this.urlPattern = url;
	}
	/**
	 * @return Returns the agencyOid.
	 */
	public Long getAgencyOid() {
		return agencyOid;
	}
	/**
	 * @param agencyOid The agencyOid to set.
	 */
	public void setAgencyOid(Long agencyOid) {
		this.agencyOid = agencyOid;
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
