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
 * Command object used for default harvest autorisations.
 * @author bbeaumont
 */
public class DefaultSiteCommand {
	public static final String PARAM_SITE_OID = "siteOid";
	public static final String PARAM_EDIT_MODE = "editMode";
	public static final String PARAM_COPY_MODE = "copyMode";
	
	/** the oid of the site to operate on. */
	private Long siteOid;
	/** the flag to indicate if we are in edit mode. */	
	private boolean editMode = false; 
	/** the flag to indicate that the specified site should be copied. */
	private boolean copyMode = false;

	/**
	 * @return Returns the siteOid.
	 */
	public Long getSiteOid() {
		return siteOid;
	}

	/**
	 * @param siteOid The siteOid to set.
	 */
	public void setSiteOid(Long siteOid) {
		this.siteOid = siteOid;
	}

	/**
	 * @return the editMode
	 */
	public boolean isEditMode() {
		return editMode;
	}

	/**
	 * @param editMode the editMode to set
	 */
	public void setEditMode(boolean editMode) {
		this.editMode = editMode;
	}

	/**
	 * @return the copyMode
	 */
	public boolean isCopyMode() {
		return copyMode;
	}

	/**
	 * @param copy true if copy in mode.
	 */
	public void setCopyMode(boolean copy) {
		this.copyMode = copy;
	}
}
