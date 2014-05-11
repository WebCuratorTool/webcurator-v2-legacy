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
 * Command object used for the harvest autorisations transfer seeds tab.
 * @author bbeaumont
 */
public class TransferSeedsCommand {
	public static final String ACTION_INIT   = "init";
	public static final String ACTION_SEARCH = "search";
	public static final String ACTION_TRANSFER = "transfer";
	public static final String ACTION_CANCEL = "cancel";
	public static final String ACTION_NEXT   = "next";
	public static final String ACTION_PREV   = "previous";
	
	public static final String PARAM_ACTION = "actionCmd";
	public static final String PARAM_FROM_PERMISSION_OID = "fromPermissionOid";
	public static final String PARAM_TO_PERMISSION_OID = "toPermissionOid";
	public static final String PARAM_SITE_TITLE = "siteTitle";
	public static final String PARAM_URL_PATTERN = "patternName";
	
	private String actionCmd = null;
	
	private Long fromPermissionOid = null;
	private Long toPermissionOid   = null;
	
	private String siteTitle = null;
	private String urlPattern = null;
	
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
	 * @return Returns the fromPermission.
	 */
	public Long getFromPermissionOid() {
		return fromPermissionOid;
	}
	/**
	 * @param fromPermission The fromPermission to set.
	 */
	public void setFromPermissionOid(Long fromPermission) {
		this.fromPermissionOid = fromPermission;
	}
	/**
	 * @return Returns the siteName.
	 */
	public String getSiteTitle() {
		return siteTitle;
	}
	/**
	 * @param siteName The siteName to set.
	 */
	public void setSiteTitle(String siteName) {
		this.siteTitle = siteName;
	}
	/**
	 * @return Returns the toPermission.
	 */
	public Long getToPermissionOid() {
		return toPermissionOid;
	}
	/**
	 * @param toPermission The toPermission to set.
	 */
	public void setToPermissionOid(Long toPermission) {
		this.toPermissionOid = toPermission;
	}
	/**
	 * @return Returns the urlPattern.
	 */
	public String getUrlPattern() {
		return urlPattern;
	}
	/**
	 * @param urlPattern The urlPattern to set.
	 */
	public void setUrlPattern(String urlPattern) {
		this.urlPattern = urlPattern;
	}
	
	
	
	
	
}
