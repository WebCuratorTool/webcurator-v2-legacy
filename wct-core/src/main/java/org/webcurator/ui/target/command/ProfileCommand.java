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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import org.webcurator.domain.model.core.ProfileOverrides;

/**
 * The command object for the profile tabs.
 * @author bbeaumont
 */
public class ProfileCommand {
	public static final String PARAM_PROFILE_NOTE = "profileNote";
	public static final String ACTION_SUBMIT = "submit";
	public static final String ACTION_DELETE_CREDENTIALS = "deleteCreds";
	
	private Long profileOid = null;
	private String profileNote = null;
	
	private String actionCmd = null;
	
	private String robots;
	private boolean overrideRobots;
	
	private Long maxHours;
	private boolean overrideMaxHours;
	
	private Long maxBytesDownload;
	private boolean overrideMaxBytesDownload;
	
	private Long maxDocuments;
	private boolean overrideMaxDocuments;
	
	private Integer maxPathDepth;
	private boolean overrideMaxPathDepth;
	
	private Integer maxHops;
	private boolean overrideMaxHops;
	
	private String excludeFilters;
	private boolean overrideExcludeFilters;
	
	private String forceAcceptFilters;
	private boolean overrideForceAcceptFilters;
	
	private String excludedMimeTypes;
	private boolean overrideExcludedMimeTypes;
	
	private Integer credentialToRemove;
	private boolean overrideCredentials;
					
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
	 * @return Returns the excludedMimeTypes.
	 */
	public String getExcludedMimeTypes() {
		return excludedMimeTypes;
	}

	/**
	 * @param excludedMimeTypes The excludedMimeTypes to set.
	 */
	public void setExcludedMimeTypes(String excludedMimeTypes) {
		this.excludedMimeTypes = excludedMimeTypes;
	}

	/**
	 * @return Returns the excludeFilters.
	 */
	public String getExcludeFilters() {
		return excludeFilters;
	}

	/**
	 * @param excludeFilters The excludeFilters to set.
	 */
	public void setExcludeFilters(String excludeFilters) {
		this.excludeFilters = excludeFilters;
	}

	/**
	 * @return Returns the forceAcceptFilters.
	 */
	public String getForceAcceptFilters() {
		return forceAcceptFilters;
	}

	/**
	 * @param forceAcceptFilters The forceAcceptFilters to set.
	 */
	public void setForceAcceptFilters(String forceAcceptFilters) {
		this.forceAcceptFilters = forceAcceptFilters;
	}

	/**
	 * @return Returns the maxBytesDownload.
	 */
	public Long getMaxBytesDownload() {
		return maxBytesDownload;
	}

	/**
	 * @param maxBytesDownload The maxBytesDownload to set.
	 */
	public void setMaxBytesDownload(Long maxBytesDownload) {
		this.maxBytesDownload = maxBytesDownload;
	}

	/**
	 * @return Returns the maxDocuments.
	 */
	public Long getMaxDocuments() {
		return maxDocuments;
	}

	/**
	 * @param maxDocuments The maxDocuments to set.
	 */
	public void setMaxDocuments(Long maxDocuments) {
		this.maxDocuments = maxDocuments;
	}

	/**
	 * @return Returns the maxHops.
	 */
	public Integer getMaxHops() {
		return maxHops;
	}

	/**
	 * @param maxHops The maxHops to set.
	 */
	public void setMaxHops(Integer maxHops) {
		this.maxHops = maxHops;
	}

	/**
	 * @return Returns the maxHours.
	 */
	public Long getMaxHours() {
		return maxHours;
	}

	/**
	 * @param maxHours The maxHours to set.
	 */
	public void setMaxHours(Long maxHours) {
		this.maxHours = maxHours;
	}

	/**
	 * @return Returns the maxPathDepth.
	 */
	public Integer getMaxPathDepth() {
		return maxPathDepth;
	}

	/**
	 * @param maxPathDepth The maxPathDepth to set.
	 */
	public void setMaxPathDepth(Integer maxPathDepth) {
		this.maxPathDepth = maxPathDepth;
	}

	/**
	 * @return Returns the overrideExcludedMimeTypes.
	 */
	public boolean isOverrideExcludedMimeTypes() {
		return overrideExcludedMimeTypes;
	}

	/**
	 * @param overrideExcludedMimeTypes The overrideExcludedMimeTypes to set.
	 */
	public void setOverrideExcludedMimeTypes(boolean overrideExcludedMimeTypes) {
		this.overrideExcludedMimeTypes = overrideExcludedMimeTypes;
	}

	/**
	 * @return Returns the overrideExcludeFilters.
	 */
	public boolean isOverrideExcludeFilters() {
		return overrideExcludeFilters;
	}

	/**
	 * @param overrideExcludeFilters The overrideExcludeFilters to set.
	 */
	public void setOverrideExcludeFilters(boolean overrideExcludeFilters) {
		this.overrideExcludeFilters = overrideExcludeFilters;
	}

	/**
	 * @return Returns the overrideForceAcceptFilters.
	 */
	public boolean isOverrideForceAcceptFilters() {
		return overrideForceAcceptFilters;
	}

	/**
	 * @param overrideForceAcceptFilters The overrideForceAcceptFilters to set.
	 */
	public void setOverrideForceAcceptFilters(boolean overrideForceAcceptFilters) {
		this.overrideForceAcceptFilters = overrideForceAcceptFilters;
	}

	/**
	 * @return Returns the overrideMaxBytesDownload.
	 */
	public boolean isOverrideMaxBytesDownload() {
		return overrideMaxBytesDownload;
	}

	/**
	 * @param overrideMaxBytesDownload The overrideMaxBytesDownload to set.
	 */
	public void setOverrideMaxBytesDownload(boolean overrideMaxBytesDownload) {
		this.overrideMaxBytesDownload = overrideMaxBytesDownload;
	}

	/**
	 * @return Returns the overrideMaxDocuments.
	 */
	public boolean isOverrideMaxDocuments() {
		return overrideMaxDocuments;
	}

	/**
	 * @param overrideMaxDocuments The overrideMaxDocuments to set.
	 */
	public void setOverrideMaxDocuments(boolean overrideMaxDocuments) {
		this.overrideMaxDocuments = overrideMaxDocuments;
	}

	/**
	 * @return Returns the overrideMaxHops.
	 */
	public boolean isOverrideMaxHops() {
		return overrideMaxHops;
	}

	/**
	 * @param overrideMaxHops The overrideMaxHops to set.
	 */
	public void setOverrideMaxHops(boolean overrideMaxHops) {
		this.overrideMaxHops = overrideMaxHops;
	}

	/**
	 * @return Returns the overrideMaxHours.
	 */
	public boolean isOverrideMaxHours() {
		return overrideMaxHours;
	}

	/**
	 * @param overrideMaxHours The overrideMaxHours to set.
	 */
	public void setOverrideMaxHours(boolean overrideMaxHours) {
		this.overrideMaxHours = overrideMaxHours;
	}

	/**
	 * @return Returns the overrideMaxPathDepth.
	 */
	public boolean isOverrideMaxPathDepth() {
		return overrideMaxPathDepth;
	}

	/**
	 * @param overrideMaxPathDepth The overrideMaxPathDepth to set.
	 */
	public void setOverrideMaxPathDepth(boolean overrideMaxPathDepth) {
		this.overrideMaxPathDepth = overrideMaxPathDepth;
	}

	/**
	 * @return Returns the overrideRobots.
	 */
	public boolean isOverrideRobots() {
		return overrideRobots;
	}

	/**
	 * @param overrideRobots The overrideRobots to set.
	 */
	public void setOverrideRobots(boolean overrideRobots) {
		this.overrideRobots = overrideRobots;
	}

	/**
	 * @return Returns the robots.
	 */
	public String getRobots() {
		return robots;
	}

	/**
	 * @param robots The robots to set.
	 */
	public void setRobots(String robots) {
		this.robots = robots;
	}

	public void setFromOverrides(ProfileOverrides overrides) {
		setRobots(overrides.getRobotsHonouringPolicy());
		setOverrideRobots(overrides.isOverrideRobotsHonouringPolicy());
		
		setMaxHours(overrides.getMaxTimeSec() != null ? overrides.getMaxTimeSec() / 3600 : 0);
		setOverrideMaxHours(overrides.isOverrideMaxTimeSec());
		
		setMaxBytesDownload(overrides.getMaxBytesDownload() != null ? overrides.getMaxBytesDownload() / 1024 : 0);
		setOverrideMaxBytesDownload(overrides.isOverrideMaxBytesDownload());
	
		setMaxDocuments(overrides.getMaxHarvestDocuments());
		setOverrideMaxDocuments(overrides.isOverrideMaxHarvestDocuments());
				
		setMaxPathDepth(overrides.getMaxPathDepth());
		setOverrideMaxPathDepth(overrides.isOverrideMaxPathDepth());

		setMaxHops(overrides.getMaxLinkHops());
		setOverrideMaxHops(overrides.isOverrideMaxLinkHops());
	
		setExcludeFilters(listToString(overrides.getExcludeUriFilters()));
		setOverrideExcludeFilters(overrides.isOverrideExcludeUriFilters());
		
		setForceAcceptFilters(listToString(overrides.getIncludeUriFilters()));
		setOverrideForceAcceptFilters(overrides.isOverrideIncludeUriFilters());
		
		setExcludedMimeTypes(overrides.getExcludedMimeTypes());
		setOverrideExcludedMimeTypes(overrides.isOverrideExcludedMimeTypes());
		
		setOverrideCredentials(overrides.isOverrideCredentials());
	}
	
	public void updateOverrides(ProfileOverrides overrides) {
		overrides.setRobotsHonouringPolicy(robots);
		overrides.setOverrideRobotsHonouringPolicy(overrideRobots);
		
		if (maxHours != null) {
			overrides.setMaxTimeSec(maxHours * 3600);
		}
		overrides.setOverrideMaxTimeSec(overrideMaxHours);

		if (maxBytesDownload != null) {
			overrides.setMaxBytesDownload(maxBytesDownload * 1024);
		}
		overrides.setOverrideMaxBytesDownload(overrideMaxBytesDownload);

		overrides.setMaxHarvestDocuments(maxDocuments);
		overrides.setOverrideMaxHarvestDocuments(overrideMaxDocuments);

		overrides.setMaxPathDepth(maxPathDepth);
		overrides.setOverrideMaxPathDepth(overrideMaxPathDepth);

		overrides.setMaxLinkHops(maxHops);
		overrides.setOverrideMaxLinkHops(overrideMaxHops);

		overrides.setExcludeUriFilters(stringToList(excludeFilters));
		overrides.setOverrideExcludeUriFilters(overrideExcludeFilters);

		overrides.setIncludeUriFilters(stringToList(forceAcceptFilters));
		overrides.setOverrideIncludeUriFilters(overrideForceAcceptFilters);

		overrides.setExcludedMimeTypes(excludedMimeTypes);
		overrides.setOverrideExcludedMimeTypes(overrideExcludedMimeTypes);
		
		overrides.setOverrideCredentials(overrideCredentials);
	}
	
	public List<String> stringToList(String str) {		
		LinkedList<String> results = new LinkedList<String>();
		
		if (str != null && !str.trim().equals("")) {
			StringTokenizer tokenizer = new StringTokenizer(str, "\n\r");
			while(tokenizer.hasMoreTokens()) {
				String token = tokenizer.nextToken();
				if(token != null && !"".equals(token.trim())) {
					results.add(token);
				}
			}
		}
		
		return results;
	}
	
	public String listToString(List<String> list) {
		StringBuffer result = new StringBuffer();
		Iterator<String> it = list.iterator();
		while(it.hasNext()) {
			result.append(it.next());
			if(it.hasNext()) {
				result.append("\n");
			}
		}
		return result.toString();
	}
	
	public void setFromSummaryCommand(TargetInstanceSummaryCommand command) {
		setProfileOid(command.getProfileOid());
		
		setRobots(command.getRobots());
		setOverrideRobots(command.isOverrideRobots());
		
		setMaxHours(command.getMaxHours());
		setOverrideMaxHours(command.isOverrideMaxHours());
		
		setMaxBytesDownload(command.getMaxBytesDownload() != null ? command.getMaxBytesDownload() : 0);
		setOverrideMaxBytesDownload(command.isOverrideMaxBytesDownload());
	
		setMaxDocuments(command.getMaxDocuments());
		setOverrideMaxDocuments(command.isOverrideMaxDocuments());
				
		setMaxPathDepth(command.getMaxPathDepth());
		setOverrideMaxPathDepth(command.isOverrideMaxPathDepth());

		setMaxHops(command.getMaxHops());
		setOverrideMaxHops(command.isOverrideMaxHops());
	
		setExcludeFilters(command.getExcludeFilters());
		setOverrideExcludeFilters(command.isOverrideExcludeFilters());
		
		setForceAcceptFilters(command.getForceAcceptFilters());
		setOverrideForceAcceptFilters(command.isOverrideForceAcceptFilters());
		
	}

	/**
	 * @return Returns the credentialToRemove.
	 */
	public Integer getCredentialToRemove() {
		return credentialToRemove;
	}

	/**
	 * @param credentialToRemove The credentialToRemove to set.
	 */
	public void setCredentialToRemove(Integer credentialToRemove) {
		this.credentialToRemove = credentialToRemove;
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
	 * @return the overrideCredentials
	 */
	public boolean isOverrideCredentials() {
		return overrideCredentials;
	}

	/**
	 * @param overrideCredentials the overrideCredentials to set
	 */
	public void setOverrideCredentials(boolean overrideCredentials) {
		this.overrideCredentials = overrideCredentials;
	}

	/**
	 * @return Returns the profileNote.
	 */
	public String getProfileNote() {
		return profileNote;
	}

	/**
	 * @param profileNote The profileNote to set.
	 */
	public void setProfileNote(String profileNote) {
		this.profileNote = profileNote;
	}
}
