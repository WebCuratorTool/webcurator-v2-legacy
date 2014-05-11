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
package org.webcurator.ui.profiles.command;

/**
 * Command class for the Profile Targets Transfer screen.
 * @author oakleigh_sk
 *
 */
public class ProfileTargetsCommand {
	
	public static String ACTION_LIST = "list";
	public static String ACTION_TRANSFER = "transfer";
	public static String ACTION_CANCEL = "cancel";
	
	private String actionCommand = ACTION_LIST;

	/** The OID of the profile to use */
	private Long profileOid;

	private int pageNumber = 0; 
	private String selectedPageSize;

    /** the selected target Oids field. */
    private long[] targetOids;
    
    private Long newProfileOid;

	private boolean cancelTargets = false;

	/**
	 * @return Returns the profileOid.
	 */
	public Long getProfileOid() {
		return profileOid;
	}

	/**
	 * @param profileOid The profileOid to set.
	 */
	public void setProfileOid(Long profileOid) {
		this.profileOid = profileOid;
	}

	/**
	 * @return Returns the actionCommand.
	 */
	public String getActionCommand() {
		return actionCommand;
	}

	/**
	 * @param actionCommand The actionCommand to set.
	 */
	public void setActionCommand(String actionCommand) {
		this.actionCommand = actionCommand;
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
	 * @return the targets
	 */
	public long[] getTargetOids() {
		return targetOids;
	}
	
	/**
	 * @param targetOids the targetOids to set
	 */
	public void setTargetOids(long[] targetOids) {
		this.targetOids = targetOids;
	}

	/**
	 * @return Returns the newProfileOid.
	 */
	public Long getNewProfileOid() {
		return newProfileOid;
	}

	/**
	 * @param newProfileOid The newProfileOid to set.
	 */
	public void setNewProfileOid(Long newProfileOid) {
		this.newProfileOid = newProfileOid;
	}

	/**
	 * @return Returns the cancelTargets.
	 */
	public boolean isCancelTargets() {
		return cancelTargets;
	}

	/**
	 * @param cancelTargets The cancelTargets to set.
	 */
	public void setCancelTargets(boolean cancelTargets) {
		this.cancelTargets = cancelTargets;
	}
	
}
