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

	private Long h3DocumentLimit;
	private boolean overrideH3DocumentLimit;

	private Double h3DataLimit;
	private boolean overrideH3DataLimit;

	private String h3DataLimitUnit;

	private Double h3TimeLimit;
	private boolean overrideH3TimeLimit;

	private String h3TimeLimitUnit;

	private Long h3MaxPathDepth;
	private boolean overrideH3MaxPathDepth;

	private Long h3MaxHops;
	private boolean overrideH3MaxHops;

	private Long h3MaxTransitiveHops;
	private boolean overrideH3MaxTransitiveHops;

	private String h3IgnoreRobots;
	private boolean overrideH3IgnoreRobots;

	private boolean h3IgnoreCookies;
	private boolean overrideH3IgnoreCookies;

	private String h3BlockedUrls;
	private boolean overrideH3BlockedUrls;

	private String h3IncludedUrls;
	private boolean overrideH3IncludedUrls;

	private String h3RawProfile;
	private boolean overrideH3RawProfile;

	private String harvesterType;

	private boolean imported;




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

	public Long getH3DocumentLimit() {
		return h3DocumentLimit;
	}

	public void setH3DocumentLimit(Long h3DocumentLimit) {
		this.h3DocumentLimit = h3DocumentLimit;
	}

	public boolean isOverrideH3DocumentLimit() {
		return overrideH3DocumentLimit;
	}

	public void setOverrideH3DocumentLimit(boolean overrideH3DocumentLimit) {
		this.overrideH3DocumentLimit = overrideH3DocumentLimit;
	}

	public Double getH3DataLimit() {
		return h3DataLimit;
	}

	public void setH3DataLimit(Double h3DataLimit) {
		this.h3DataLimit = h3DataLimit;
	}

	public boolean isOverrideH3DataLimit() {
		return overrideH3DataLimit;
	}

	public void setOverrideH3DataLimit(boolean overrideH3DataLimit) {
		this.overrideH3DataLimit = overrideH3DataLimit;
	}

	public String getH3DataLimitUnit() {
		return h3DataLimitUnit;
	}

	public void setH3DataLimitUnit(String h3DataLimitUnit) {
		this.h3DataLimitUnit = h3DataLimitUnit;
	}

	public Double getH3TimeLimit() {
		return h3TimeLimit;
	}

	public void setH3TimeLimit(Double h3TimeLimit) {
		this.h3TimeLimit = h3TimeLimit;
	}

	public boolean isOverrideH3TimeLimit() {
		return overrideH3TimeLimit;
	}

	public void setOverrideH3TimeLimit(boolean overrideH3TimeLimit) {
		this.overrideH3TimeLimit = overrideH3TimeLimit;
	}

	public String getH3TimeLimitUnit() {
		return h3TimeLimitUnit;
	}

	public void setH3TimeLimitUnit(String h3TimeLimitUnit) {
		this.h3TimeLimitUnit = h3TimeLimitUnit;
	}

	public Long getH3MaxPathDepth() {
		return h3MaxPathDepth;
	}

	public void setH3MaxPathDepth(Long h3MaxPathDepth) {
		this.h3MaxPathDepth = h3MaxPathDepth;
	}

	public boolean isOverrideH3MaxPathDepth() {
		return overrideH3MaxPathDepth;
	}

	public void setOverrideH3MaxPathDepth(boolean overrideH3MaxPathDepth) {
		this.overrideH3MaxPathDepth = overrideH3MaxPathDepth;
	}

	public Long getH3MaxHops() {
		return h3MaxHops;
	}

	public void setH3MaxHops(Long h3MaxHops) {
		this.h3MaxHops = h3MaxHops;
	}

	public boolean isOverrideH3MaxHops() {
		return overrideH3MaxHops;
	}

	public void setOverrideH3MaxHops(boolean overrideH3MaxHops) {
		this.overrideH3MaxHops = overrideH3MaxHops;
	}

	public Long getH3MaxTransitiveHops() {
		return h3MaxTransitiveHops;
	}

	public void setH3MaxTransitiveHops(Long h3MaxTransitiveHops) {
		this.h3MaxTransitiveHops = h3MaxTransitiveHops;
	}

	public boolean isOverrideH3MaxTransitiveHops() {
		return overrideH3MaxTransitiveHops;
	}

	public void setOverrideH3MaxTransitiveHops(boolean overrideH3MaxTransitiveHops) {
		this.overrideH3MaxTransitiveHops = overrideH3MaxTransitiveHops;
	}

	public String getH3IgnoreRobots() {
		return h3IgnoreRobots;
	}

	public void setH3IgnoreRobots(String h3IgnoreRobots) {
		this.h3IgnoreRobots = h3IgnoreRobots;
	}

	public boolean isOverrideH3IgnoreRobots() {
		return overrideH3IgnoreRobots;
	}

	public void setOverrideH3IgnoreRobots(boolean overrideH3IgnoreRobots) {
		this.overrideH3IgnoreRobots = overrideH3IgnoreRobots;
	}

	public boolean isH3IgnoreCookies() {
		return h3IgnoreCookies;
	}

	public void setH3IgnoreCookies(boolean h3IgnoreCookies) {
		this.h3IgnoreCookies = h3IgnoreCookies;
	}

	public boolean isOverrideH3IgnoreCookies() {
		return overrideH3IgnoreCookies;
	}

	public void setOverrideH3IgnoreCookies(boolean overrideH3IgnoreCookies) {
		this.overrideH3IgnoreCookies = overrideH3IgnoreCookies;
	}

	public String getH3BlockedUrls() {
		return h3BlockedUrls;
	}

	public void setH3BlockedUrls(String h3BlockedUrls) {
		this.h3BlockedUrls = h3BlockedUrls;
	}

	public boolean isOverrideH3BlockedUrls() {
		return overrideH3BlockedUrls;
	}

	public void setOverrideH3BlockedUrls(boolean overrideH3BlockedUrls) {
		this.overrideH3BlockedUrls = overrideH3BlockedUrls;
	}

	public String getH3IncludedUrls() {
		return h3IncludedUrls;
	}

	public void setH3IncludedUrls(String h3IncludedUrls) {
		this.h3IncludedUrls = h3IncludedUrls;
	}

	public boolean isOverrideH3IncludedUrls() {
		return overrideH3IncludedUrls;
	}

	public void setOverrideH3IncludedUrls(boolean overrideH3IncludedUrls) {
		this.overrideH3IncludedUrls = overrideH3IncludedUrls;
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

		// H3 profile overrides
		setH3DocumentLimit(overrides.getH3DocumentLimit());
		setOverrideH3DocumentLimit(overrides.isOverrideH3DocumentLimit());

		setH3DataLimit(overrides.getH3DataLimit() != null ? overrides.getH3DataLimit() : 0);
		setOverrideH3DataLimit((overrides.isOverrideH3DataLimit()));

		setH3DataLimitUnit(overrides.getH3DataLimitUnit());

		setH3TimeLimit(overrides.getH3TimeLimit() != null ? overrides.getH3TimeLimit() : 0);
		setOverrideH3TimeLimit(overrides.isOverrideH3TimeLimit());

		setH3TimeLimitUnit(overrides.getH3TimeLimitUnit());

		setH3MaxPathDepth(overrides.getH3MaxPathDepth());
		setOverrideH3MaxPathDepth(overrides.isOverrideH3MaxPathDepth());

		setH3MaxHops(overrides.getH3MaxHops());
		setOverrideH3MaxHops(overrides.isOverrideH3MaxHops());

		setH3MaxTransitiveHops(overrides.getH3MaxTransitiveHops());
		setOverrideH3MaxTransitiveHops(overrides.isOverrideH3MaxTransitiveHops());

		setH3IgnoreRobots(overrides.getH3IgnoreRobots());
		setOverrideH3IgnoreRobots(overrides.isOverrideH3IgnoreRobots());

		setH3IgnoreCookies(overrides.isH3IgnoreCookies());
		setOverrideH3IgnoreCookies(overrides.isOverrideH3IgnoreCookies());

		setH3BlockedUrls(listToString(overrides.getH3BlockedUrls()));
		setOverrideH3BlockedUrls(overrides.isOverrideH3BlockedUrls());

		setH3IncludedUrls(listToString(overrides.getH3IncludedUrls()));
		setOverrideH3IncludedUrls(overrides.isOverrideH3IncludedUrls());

		setOverrideH3RawProfile(overrides.isOverrideH3RawProfile());
		setH3RawProfile(overrides.getH3RawProfile());
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

		// H3 profile overrides
		overrides.setH3DocumentLimit(h3DocumentLimit);
		overrides.setOverrideH3DocumentLimit(overrideH3DocumentLimit);

		overrides.setH3DataLimit(h3DataLimit);
		overrides.setOverrideH3DataLimit(overrideH3DataLimit);

		overrides.setH3DataLimitUnit(h3DataLimitUnit);

		overrides.setH3TimeLimit(h3TimeLimit);
		overrides.setOverrideH3TimeLimit(overrideH3TimeLimit);

		overrides.setH3TimeLimitUnit(h3TimeLimitUnit);

		overrides.setH3MaxPathDepth(h3MaxPathDepth);
		overrides.setOverrideH3MaxPathDepth(overrideH3MaxPathDepth);

		overrides.setH3MaxHops(h3MaxHops);
		overrides.setOverrideH3MaxHops(overrideH3MaxHops);

		overrides.setH3MaxTransitiveHops(h3MaxTransitiveHops);
		overrides.setOverrideH3MaxTransitiveHops(overrideH3MaxTransitiveHops);

		overrides.setH3IgnoreRobots(h3IgnoreRobots);
		overrides.setOverrideH3IgnoreRobots(overrideH3IgnoreRobots);

		overrides.setH3IgnoreCookies(h3IgnoreCookies);
		overrides.setOverrideH3IgnoreCookies(overrideH3IgnoreCookies);

		overrides.setH3BlockedUrls(stringToList(h3BlockedUrls));
		overrides.setOverrideH3BlockedUrls(overrideH3BlockedUrls);

		overrides.setH3IncludedUrls(stringToList(h3IncludedUrls));
		overrides.setOverrideH3IncludedUrls(overrideH3IncludedUrls);

		overrides.setOverrideH3RawProfile(overrideH3RawProfile);
		overrides.setH3RawProfile(h3RawProfile);
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

		setRawProfile(command.getH3RawProfile());
		setOverrideRawProfile(command.isOverrideH3RawProfile());

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

	public String getH3RawProfile() {
		return h3RawProfile;
	}

	public void setH3RawProfile(String h3RawProfile) {
		this.h3RawProfile = h3RawProfile;
	}

	public boolean isOverrideH3RawProfile() {
		return overrideH3RawProfile;
	}

	public void setOverrideH3RawProfile(boolean overrideH3RawProfile) {
		this.overrideH3RawProfile = overrideH3RawProfile;
	}

	public String getHarvesterType() {
		return harvesterType;
	}

	public void setHarvesterType(String harvesterType) {
		this.harvesterType = harvesterType;
	}

	public boolean isImported() {
		return imported;
	}

	public void setImported(boolean imported) {
		this.imported = imported;
	}
}
