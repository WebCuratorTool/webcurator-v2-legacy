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

import org.webcurator.core.permissionmapping.UrlUtils;
import org.webcurator.domain.model.core.Seed;

/**
 * The command object for the addeding seeds to a target
 * @author bbeaumont
 */
public class SeedsCommand {
	public static final String ACTION_ADD = "ADD_SEED";
	public static final String ACTION_REMOVE = "REMOVE_SEED";
	public static final String ACTION_TOGGLE_PRIMARY = "TOGGLE_PRIMARY";
	public static final String ACTION_LINK_SELECTED = "ACTION_LINK_SELECTED";
	public static final String ACTION_UNLINK_SELECTED = "ACTION_UNLINK_SELECTED";
	public static final String ACTION_REMOVE_SELECTED = "ACTION_REMOVE_SELECTED";
	public static final String ACTION_UNLINK = "UNLINK";
	public static final String ACTION_LINK_NEW = "LINK_NEW";
	public static final String ACTION_LINK_NEW_CANCEL = "LINK_NEW_CANCEL";
	public static final String ACTION_LINK_NEW_CONFIRM = "LINK_NEW_CONFIRM";
	public static final String ACTION_LINK_NEW_SEARCH = "LINK_NEW_SEARCH";
	public static final String ACTION_SET_NAME = "SET_SEED_NAME";
	
	public static final String ACTION_START_IMPORT = "START_IMPORT";
	public static final String ACTION_DO_IMPORT = "DO_IMPORT";
	
	public static final String SEARCH_SITES = "site";
	public static final String SEARCH_URL = "url";
	
	public static final long PERM_MAPPING_AUTO = -2;
	public static final long PERM_MAPPING_NONE = -1;
	
	
	/** The seed for the target */
	private String seed;
    /** The selected seed **/
    private String selectedSeed;
    /** The selected permission */
    private String selectedPermission;
    /** The permission mapping option */
    private Long permissionMappingOption;
    /** The search type */
    private String searchType;
    /** The action command */
    private String actionCmd;
    /** The number */
    private int pageNumber;
    private String updatedNameSeedId;
    private String updatedNameSeedValue;
    
    private String siteSearchCriteria;
    private String urlSearchCriteria;
    private String[] linkPermIdentity;
    
    
    private byte[] seedsFile;
	
	/**
	 * Construct a command object from the Model object
	 * @param seed The model object
	 * @return A command object for the seed.
	 */
    public static SeedsCommand buildFromModel(Seed seed) {	
    	SeedsCommand command = new SeedsCommand();
    	command.setSeed(seed.getSeed());
    	return command;
    }
	
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
		this.seed = UrlUtils.fixUrl(seed);
	}

	/**
	 * @return Returns the selectedItem.
	 */
	public String getSelectedSeed() {
		return selectedSeed;
	}

	/**
	 * @param selectedItem The selectedItem to set.
	 */
	public void setSelectedSeed(String selectedItem) {
		this.selectedSeed = selectedItem;
	}

	/**
	 * @return Returns the permissionMappingOption.
	 */
	public Long getPermissionMappingOption() {
		return permissionMappingOption;
	}

	/**
	 * @param permissionMappingOption The permissionMappingOption to set.
	 */
	public void setPermissionMappingOption(Long permissionMappingOption) {
		this.permissionMappingOption = permissionMappingOption;
	}

	
	public boolean isAction(String action) { 
		return action.equals(actionCmd);
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
	 * @return Returns the selectedPermission.
	 */
	public String getSelectedPermission() {
		return selectedPermission;
	}

	/**
	 * @param selectedPermission The selectedPermission to set.
	 */
	public void setSelectedPermission(String selectedPermission) {
		this.selectedPermission = selectedPermission;
	}

	/**
	 * @return Returns the searchType.
	 */
	public String getSearchType() {
		return searchType;
	}

	/**
	 * @param searchType The searchType to set.
	 */
	public void setSearchType(String searchType) {
		this.searchType = searchType;
	}

	/**
	 * @return Returns the siteSearchCriteria.
	 */
	public String getSiteSearchCriteria() {
		return siteSearchCriteria;
	}

	/**
	 * @param siteSearchCriteria The siteSearchCriteria to set.
	 */
	public void setSiteSearchCriteria(String siteSearchCriteria) {
		this.siteSearchCriteria = siteSearchCriteria;
	}

	/**
	 * @return Returns the urlSearchCriteria.
	 */
	public String getUrlSearchCriteria() {
		return urlSearchCriteria;
	}

	/**
	 * @param urlSearchCriteria The urlSearchCriteria to set.
	 */
	public void setUrlSearchCriteria(String urlSearchCriteria) {
		this.urlSearchCriteria = UrlUtils.fixUrl(urlSearchCriteria);
	}

	/**
	 * @return Returns the linkPermIdentity.
	 */
	public String[] getLinkPermIdentity() {
		return linkPermIdentity;
	}

	/**
	 * @param linkPermIdentity The linkPermIdentity to set.
	 */
	public void setLinkPermIdentity(String[] linkPermIdentity) {
		this.linkPermIdentity = linkPermIdentity;
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
	 * @return Returns the importFile.
	 */
	public byte[] getSeedsFile() {
		return seedsFile;
	}

	/**
	 * @param importFile The importFile to set.
	 */
	public void setSeedsFile(byte[] importFile) {
		this.seedsFile = importFile;
	}

	public String getUpdatedNameSeedId() {
		return updatedNameSeedId;
	}

	public String getUpdatedNameSeedValue() {
		return updatedNameSeedValue;
	}

	public void setUpdatedNameSeedId(String updatedNameSeedId) {
		this.updatedNameSeedId = updatedNameSeedId;
	}

	public void setUpdatedNameSeedValue(String updatedNameSeedValue) {
		this.updatedNameSeedValue = updatedNameSeedValue;
	}



}
