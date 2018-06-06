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

import org.webcurator.core.profiles.*;
import org.webcurator.domain.model.core.Profile;

import java.math.BigDecimal;

/**
 * The command for editing the scope information about a H3 profile.
 *
 */
public class Heritrix3ProfileCommand {
	private String contactURL;
	private String userAgent;
	private long documentLimit;
	private double dataLimit;
	private String dataLimitUnit;
	private double timeLimit;
	private String timeLimitUnit;
	private long maxPathDepth;
	private long maxHops;
	private long maxTransitiveHops;
	private boolean ignoreRobotsTxt;
	private boolean ignoreCookies;
	private String defaultEncoding;
	private String blockUrls;
	private String includeUrls;
	private double maxFileSize;
	private String maxFileSizeUnit;
	private boolean compress;
	private String prefix;
	private String politeness;
	private double delayFactor;
	private long minDelayMs;
	private long maxDelayMs;
	private long respectCrawlDelayUpToSeconds;
	private long maxPerHostBandwidthUsageKbSec;

	/**
	 * Build a command object from the Heritrix3Profile.
	 * @param heritrix3Profile The business model object.
	 * @return A new Heritrix3ProfileCommand object.
	 */
	public static Heritrix3ProfileCommand buildFromModel(Heritrix3Profile heritrix3Profile, Profile profile) {
		Heritrix3ProfileCommand command = new Heritrix3ProfileCommand();
		Heritrix3ProfileOptions options = heritrix3Profile.getHeritrix3ProfileOptions();
		// set up the units
		if (profile != null) {
			options.setDataLimitUnit(profile.getDataLimitUnit() != null ? ProfileDataUnit.valueOf(profile.getDataLimitUnit()) : ProfileDataUnit.DEFAULT);
			options.setTimeLimitUnit(profile.getTimeLimitUnit() != null ? ProfileTimeUnit.valueOf(profile.getTimeLimitUnit()) : ProfileTimeUnit.DEFAULT);
			options.setMaxFileSizeUnit(profile.getMaxFileSizeUnit() != null ? ProfileDataUnit.valueOf(profile.getMaxFileSizeUnit()) : ProfileDataUnit.DEFAULT);
			command.setDataLimitUnit(profile.getDataLimitUnit() != null ? profile.getDataLimitUnit() : ProfileDataUnit.DEFAULT.name());
			command.setTimeLimitUnit(profile.getTimeLimitUnit() != null ? profile.getTimeLimitUnit() : ProfileTimeUnit.DEFAULT.name());
			command.setMaxFileSizeUnit(profile.getMaxFileSizeUnit() != null ? profile.getMaxFileSizeUnit() : ProfileDataUnit.DEFAULT.name());
		} else {
			options.setDataLimitUnit(ProfileDataUnit.DEFAULT);
			options.setTimeLimitUnit(ProfileTimeUnit.DEFAULT);
			options.setMaxFileSizeUnit(ProfileDataUnit.DEFAULT);
			command.setDataLimitUnit(ProfileDataUnit.DEFAULT.name());
			command.setTimeLimitUnit(ProfileTimeUnit.DEFAULT.name());
			command.setMaxFileSizeUnit(ProfileDataUnit.DEFAULT.name());
		}
		command.setContactURL(options.getContactURL());
		command.setUserAgent(options.getUserAgent());
		command.setDocumentLimit(options.getDocumentLimit());
		command.setDataLimit(options.getDataLimit().doubleValue());
		command.setTimeLimit(options.getTimeLimit().doubleValue());
		command.setMaxPathDepth(options.getMaxPathDepth());
		command.setMaxHops(options.getMaxHops());
		command.setMaxTransitiveHops(options.getMaxTransitiveHops());
		command.setIgnoreRobotsTxt(options.isIgnoreRobotsTxt());
		command.setIgnoreCookies(options.isIgnoreCookies());
		command.setDefaultEncoding(options.getDefaultEncoding());
		command.setBlockUrls(options.getBlockURLs());
		command.setIncludeUrls(options.getIncludeURLs());
		command.setMaxFileSize(options.getMaxFileSize().doubleValue());
		command.setCompress(options.isCompress());
		command.setPrefix(options.getPrefix());
		PolitenessOptions politenessOptions = options.getPolitenessOptions();
		command.setDelayFactor(politenessOptions.getDelayFactor());
		command.setMinDelayMs(politenessOptions.getMinDelayMs());
		command.setMaxDelayMs(politenessOptions.getMaxDelayMs());
		command.setRespectCrawlDelayUpToSeconds(politenessOptions.getRespectCrawlDelayUpToSeconds());
		command.setMaxPerHostBandwidthUsageKbSec(politenessOptions.getMaxPerHostBandwidthUsageKbSec());
		// set the politeness combo box value
		command.setPoliteness(politenessOptions.getPoliteness());

		return command;
	}
	
	/**
	 * Update the business object.
	 * @param heritrix3Profile The profile to update.
	 */
	public void updateBusinessModel(Heritrix3Profile heritrix3Profile, Profile profile) {
		Heritrix3ProfileOptions options = heritrix3Profile.getHeritrix3ProfileOptions();
		// update the units
		if (profile != null) {
			profile.setDataLimitUnit(dataLimitUnit);
			profile.setTimeLimitUnit(timeLimitUnit);
			profile.setMaxFileSizeUnit(maxFileSizeUnit);
		}
		options.setContactURL(contactURL);
		options.setUserAgent(userAgent);
		options.setDocumentLimit(documentLimit);
		options.setDataLimitUnit(ProfileDataUnit.valueOf(dataLimitUnit));
		options.setDataLimit(new BigDecimal(dataLimit).setScale(8, BigDecimal.ROUND_HALF_UP));
		options.setTimeLimitUnit(ProfileTimeUnit.valueOf(timeLimitUnit));
		options.setTimeLimit(new BigDecimal(timeLimit).setScale(8, BigDecimal.ROUND_HALF_UP));
		options.setMaxPathDepth(maxPathDepth);
		options.setMaxHops(maxHops);
		options.setMaxTransitiveHops(maxTransitiveHops);
		options.setIgnoreRobotsTxt(ignoreRobotsTxt);
		options.setIgnoreCookies(ignoreCookies);
		options.setDefaultEncoding(defaultEncoding);
		options.setBlockURLs(blockUrls);
		options.setIncludeURLs(includeUrls);
		options.setMaxFileSizeUnit(ProfileDataUnit.valueOf(maxFileSizeUnit));
		options.setMaxFileSize(new BigDecimal(maxFileSize).setScale(8, BigDecimal.ROUND_HALF_UP));
		options.setCompress(compress);
		options.setPrefix(prefix);
		PolitenessOptions politenessOptions = options.getPolitenessOptions();
		politenessOptions.setDelayFactor(delayFactor);
		politenessOptions.setMinDelayMs(minDelayMs);
		politenessOptions.setMaxDelayMs(maxDelayMs);
		politenessOptions.setRespectCrawlDelayUpToSeconds(respectCrawlDelayUpToSeconds);
		politenessOptions.setMaxPerHostBandwidthUsageKbSec(maxPerHostBandwidthUsageKbSec);
		// update the profile xml
		String profileXml = heritrix3Profile.toProfileXml();
		heritrix3Profile.setProfileXml(profileXml);
	}

	public String getContactURL() {
		return contactURL;
	}

	public void setContactURL(String contactURL) {
		this.contactURL = contactURL;
	}

	public String getUserAgent() {
		return userAgent;
	}

	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}

	public long getDocumentLimit() {
		return documentLimit;
	}

	public void setDocumentLimit(long documentLimit) {
		this.documentLimit = documentLimit;
	}

	public double getDataLimit() {
		return dataLimit;
	}

	public void setDataLimit(double dataLimit) {
		this.dataLimit = dataLimit;
	}

	public String getDataLimitUnit() {
		return dataLimitUnit;
	}

	public void setDataLimitUnit(String dataLimitUnit) {
		this.dataLimitUnit = dataLimitUnit;
	}

	public double getTimeLimit() {
		return timeLimit;
	}

	public void setTimeLimit(double timeLimit) {
		this.timeLimit = timeLimit;
	}

	public String getTimeLimitUnit() {
		return timeLimitUnit;
	}

	public void setTimeLimitUnit(String timeLimitUnit) {
		this.timeLimitUnit = timeLimitUnit;
	}

	public long getMaxPathDepth() {
		return maxPathDepth;
	}

	public void setMaxPathDepth(long maxPathDepth) {
		this.maxPathDepth = maxPathDepth;
	}

	public long getMaxHops() {
		return maxHops;
	}

	public void setMaxHops(long maxHops) {
		this.maxHops = maxHops;
	}

	public long getMaxTransitiveHops() {
		return maxTransitiveHops;
	}

	public void setMaxTransitiveHops(long maxTransitiveHops) {
		this.maxTransitiveHops = maxTransitiveHops;
	}

	public boolean isIgnoreRobotsTxt() {
		return ignoreRobotsTxt;
	}

	public void setIgnoreRobotsTxt(boolean ignoreRobotsTxt) {
		this.ignoreRobotsTxt = ignoreRobotsTxt;
	}

	public boolean isIgnoreCookies() {
		return ignoreCookies;
	}

	public void setIgnoreCookies(boolean ignoreCookies) {
		this.ignoreCookies = ignoreCookies;
	}

	public String getDefaultEncoding() {
		return defaultEncoding;
	}

	public void setDefaultEncoding(String defaultEncoding) {
		this.defaultEncoding = defaultEncoding;
	}

	public String getBlockUrls() {
		return blockUrls;
	}

	public void setBlockUrls(String blockUrls) {
		this.blockUrls = blockUrls;
	}

	public String getIncludeUrls() {
		return includeUrls;
	}

	public void setIncludeUrls(String includeUrls) {
		this.includeUrls = includeUrls;
	}

	public double getMaxFileSize() {
		return maxFileSize;
	}

	public void setMaxFileSize(double maxFileSize) {
		this.maxFileSize = maxFileSize;
	}

	public String getMaxFileSizeUnit() {
		return maxFileSizeUnit;
	}

	public void setMaxFileSizeUnit(String maxFileSizeUnit) {
		this.maxFileSizeUnit = maxFileSizeUnit;
	}

	public boolean isCompress() {
		return compress;
	}

	public void setCompress(boolean compress) {
		this.compress = compress;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getPoliteness() {
		return politeness;
	}

	public void setPoliteness(String politeness) {
		this.politeness = politeness;
	}

	public double getDelayFactor() {
		return delayFactor;
	}

	public void setDelayFactor(double delayFactor) {
		this.delayFactor = delayFactor;
	}

	public long getMinDelayMs() {
		return minDelayMs;
	}

	public void setMinDelayMs(long minDelayMs) {
		this.minDelayMs = minDelayMs;
	}

	public long getMaxDelayMs() {
		return maxDelayMs;
	}

	public void setMaxDelayMs(long maxDelayMs) {
		this.maxDelayMs = maxDelayMs;
	}

	public long getRespectCrawlDelayUpToSeconds() {
		return respectCrawlDelayUpToSeconds;
	}

	public void setRespectCrawlDelayUpToSeconds(long respectCrawlDelayUpToSeconds) {
		this.respectCrawlDelayUpToSeconds = respectCrawlDelayUpToSeconds;
	}

	public long getMaxPerHostBandwidthUsageKbSec() {
		return maxPerHostBandwidthUsageKbSec;
	}

	public void setMaxPerHostBandwidthUsageKbSec(long maxPerHostBandwidthUsageKbSec) {
		this.maxPerHostBandwidthUsageKbSec = maxPerHostBandwidthUsageKbSec;
	}
}
