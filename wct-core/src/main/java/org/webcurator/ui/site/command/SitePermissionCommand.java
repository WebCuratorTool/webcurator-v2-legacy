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

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.webcurator.domain.model.core.AuthorisingAgent;
import org.webcurator.domain.model.core.Permission;
import org.webcurator.domain.model.core.UrlPattern;

/**
 * Command object used for the harvest authorisations permission tab.
 * @author bbeaumont
 */
public class SitePermissionCommand {
	public static final String ACTION_SAVE="save";
	public static final String ACTION_CANCEL="cancel";
	public static final String ACTION_ADD_NOTE="add_note";
	public static final String ACTION_MODIFY_NOTE="modify_note";
	public static final String ACTION_DELETE_NOTE="delete_note";
	public static final String ACTION_ADD_EXCLUSION = "add_exclusion";
	public static final String ACTION_DELETE_EXCLUSION = "delete_exclusion";
	
	public static final int CNST_MAX_NOTE_LENGTH = 1000;
	
	public static final String PARAM_NOTE = "note";
	public static final String PARAM_NOTE_INDEX = "noteIndex";
	public static final String PARAM_USERNAME = "username";
		
	private Long uiid;
	private String identity;
	private String selectedPermission;
	private AuthorisingAgent authorisingAgent;
	private Date startDate;
	private Date endDate;
	private int status;
	private int originalStatus;
	private boolean quickPick;
	private String displayName;
	private String actionCmd;
	private boolean createSeekPermissionTask;
	private String copyrightStatement;
	private String copyrightUrl;
	private Date openAccessDate;
	private String accessStatus;
	private String note = null;
	private int noteIndex = -1;
	private String username = null;
	private String exclusionUrl;
	private String exclusionReason;
	private Integer deleteExclusionIndex;
	private String specialRequirements;
	private String fileReference;
	private String authResponse;
	
	private Set<UrlPattern> urls = new HashSet<UrlPattern>();
	
	public static SitePermissionCommand buildFromModel(Permission perm) {
		SitePermissionCommand command = new SitePermissionCommand();
		command.identity = perm.getIdentity();
		command.setEndDate(perm.getEndDate());
		command.setStartDate(perm.getStartDate());
		command.setAuthorisingAgent(perm.getAuthorisingAgent());
		command.setDisplayName(perm.getDisplayName());
		command.setQuickPick(perm.isQuickPick());
		command.setSpecialRequirements(perm.getSpecialRequirements());
		command.setStatus(perm.getStatus());
		command.setOriginalStatus(perm.getStatus());
		command.setCreateSeekPermissionTask(perm.isCreateSeekPermissionTask());
		command.urls.addAll(perm.getUrls());
		command.setCopyrightStatement(perm.getCopyrightStatement());
		command.setCopyrightUrl(perm.getCopyrightUrl());
		command.setOpenAccessDate(perm.getOpenAccessDate());
		command.setAccessStatus(perm.getAccessStatus());
		command.setFileReference(perm.getFileReference());
		command.setAuthResponse(perm.getAuthResponse());
		return command;
	}
	
	public SitePermissionCommand()
	{
		super();
		username = org.webcurator.core.util.AuthUtil.getRemoteUser();
	}
	
	/**
	 * Checks if the command refers to a given action.
	 * @param actionCmd The action command to compare against.
	 * @return True if the action command is the one provided.
	 */
	public boolean isAction(String actionCmd) {
		return actionCmd.equals(this.actionCmd);
	}
	
	/**
	 * @return Returns the authorisingAgentIndex.
	 */
	public AuthorisingAgent getAuthorisingAgent() {
		return authorisingAgent;
	}
	/**
	 * @param anAuthorisingAgent The authorising agent to set.
	 */
	public void setAuthorisingAgent(AuthorisingAgent anAuthorisingAgent) {
		authorisingAgent = anAuthorisingAgent;
	}
	/**
	 * @return Returns the endDate.
	 */
	public Date getEndDate() {
		return endDate;
	}
	/**
	 * @param endDate The endDate to set.
	 */
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	/**
	 * @return Returns the startDate.
	 */
	public Date getStartDate() {
		return startDate;
	}
	/**
	 * @param startDate The startDate to set.
	 */
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	
	/**
	 * @return Returns the status.
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * @param status The status to set.
	 */
	public void setStatus(int status) {
		this.status = status;
	}

	/**
	 * @return Returns the uiid.
	 */
	public Long getUiid() {
		return uiid;
	}

	/**
	 * @param uiid The uiid to set.
	 */
	public void setUiid(Long uiid) {
		this.uiid = uiid;
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
	 * @return Returns the urls.
	 */
	public Set<UrlPattern> getUrls() {
		return urls;
	}

	/**
	 * @param urls The urls to set.
	 */
	public void setUrls(Set<UrlPattern> urls) {
		this.urls = urls;
	}

	/**
	 * @return Returns the temporaryIdentity.
	 */
	public String getIdentity() {
		return identity;
	}

	/**
	 * @param temporaryIdentity The temporaryIdentity to set.
	 */
	public void setIdentity(String temporaryIdentity) {
		this.identity = temporaryIdentity;
	}

	/**
	 * @return Returns the quickPick.
	 */
	public boolean isQuickPick() {
		return quickPick;
	}

	/**
	 * @param quickPick The quickPick to set.
	 */
	public void setQuickPick(boolean quickPick) {
		this.quickPick = quickPick;
	}

	/**
	 * @return Returns the displayName.
	 */
	public String getDisplayName() {
		return displayName;
	}

	/**
	 * @param displayName The displayName to set.
	 */
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}



	/**
	 * @return Returns the specialConditions.
	 */
	public String getSpecialRequirements() {
		return specialRequirements;
	}



	/**
	 * @param specialConditions The specialConditions to set.
	 */
	public void setSpecialRequirements(String specialConditions) {
		this.specialRequirements = specialConditions;
	}

	/**
	 * @return Returns the authResponse.
	 */
	public String getAuthResponse() {
		return authResponse;
	}

	/**
	 * @param authResponse The authResponse to set.
	 */
	public void setAuthResponse(String authResponse) {
		this.authResponse = authResponse;
	}

	/**
	 * @return the cmdAction
	 */
	public String getActionCmd() {
		return actionCmd;
	}

	/**
	 * @param cmdAction the cmdAction to set
	 */
	public void setActionCmd(String cmdAction) {
		this.actionCmd = cmdAction;
	}

	/**
	 * @return Returns the createSeekPermissionTask.
	 */
	public boolean isCreateSeekPermissionTask() {
		return createSeekPermissionTask;
	}

	/**
	 * @param createSeekPermissionTask The createSeekPermissionTask to set.
	 */
	public void setCreateSeekPermissionTask(boolean createSeekPermissionTask) {
		this.createSeekPermissionTask = createSeekPermissionTask;
	}

	/**
	 * @return the copyrightStatement
	 */
	public String getCopyrightStatement() {
		return copyrightStatement;
	}

	/**
	 * @param copyrightStatement the copyrightStatement to set
	 */
	public void setCopyrightStatement(String copyrightStatement) {
		this.copyrightStatement = copyrightStatement;
	}

	/**
	 * @return the copyrightUrl
	 */
	public String getCopyrightUrl() {
		return copyrightUrl;
	}

	/**
	 * @param copyrightUrl the copyrightUrl to set
	 */
	public void setCopyrightUrl(String copyrightUrl) {
		this.copyrightUrl = copyrightUrl;
	}

	/**
	 * @return the openAccessDate
	 */
	public Date getOpenAccessDate() {
		return openAccessDate;
	}

	/**
	 * @param openAccessDate the openAccessDate to set
	 */
	public void setOpenAccessDate(Date openAccessDate) {
		this.openAccessDate = openAccessDate;
	}

	/**
	 * @return Returns the accessStatus.
	 */
	public String getAccessStatus() {
		return accessStatus;
	}

	/**
	 * @param accessStatus The accessStatus to set.
	 */
	public void setAccessStatus(String accessStatus) {
		this.accessStatus = accessStatus;
	}

	/**
	 * @return Returns the annotation.
	 */
	public String getNote() {
		return note;
	}

	/**
	 * @param annotation The annotation to set.
	 */
	public void setNote(String annotation) {
		this.note = annotation;
	}

	/**
	 * @return Returns the annotation index.
	 */
	public int getNoteIndex() {
		return noteIndex;
	}

	/**
	 * @param noteIndex The note index to set.
	 */
	public void setNoteIndex(int noteIndex) {
		this.noteIndex = noteIndex;
	}

	/**
	 * @return Returns the username.
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username The username to set.
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @return Returns the exclusionReason.
	 */
	public String getExclusionReason() {
		return exclusionReason;
	}

	/**
	 * @param exclusionReason The exclusionReason to set.
	 */
	public void setExclusionReason(String exclusionReason) {
		this.exclusionReason = exclusionReason;
	}

	/**
	 * @return Returns the exclusionUrl.
	 */
	public String getExclusionUrl() {
		return exclusionUrl;
	}

	/**
	 * @param exclusionUrl The exclusionUrl to set.
	 */
	public void setExclusionUrl(String exclusionUrl) {
		this.exclusionUrl = exclusionUrl;
	}

	/**
	 * @return Returns the deleteExclusionIndex.
	 */
	public Integer getDeleteExclusionIndex() {
		return deleteExclusionIndex;
	}

	/**
	 * @param deleteExclusionIndex The deleteExclusionIndex to set.
	 */
	public void setDeleteExclusionIndex(Integer deleteExclusionIndex) {
		this.deleteExclusionIndex = deleteExclusionIndex;
	}

	/**
	 * @return Returns the fileReference.
	 */
	public String getFileReference() {
		return fileReference;
	}

	/**
	 * @param fileReference The fileReference to set.
	 */
	public void setFileReference(String fileReference) {
		this.fileReference = fileReference;
	}

	/**
	 * @return the originalStatus
	 */
	public int getOriginalStatus() {
		return originalStatus;
	}

	/**
	 * @param originalStatus the originalStatus to set
	 */
	public void setOriginalStatus(int originalStatus) {
		this.originalStatus = originalStatus;
	}	
}
