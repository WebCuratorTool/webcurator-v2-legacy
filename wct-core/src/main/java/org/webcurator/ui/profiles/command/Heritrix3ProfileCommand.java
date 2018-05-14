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

import org.webcurator.core.profiles.Heritrix3Profile;
import org.webcurator.core.profiles.Heritrix3ProfileOptions;

/**
 * The command for editing the scope information about a H3 profile.
 *
 */
public class Heritrix3ProfileCommand {
	private String contactURL;
	private long documentLimit;
	private long dataLimit;
	private long timeLimit;
	private long maxPathDepth;
	private long maxHops;
	private long maxTransitiveHops;
	private boolean ignoreRobotsTxt;
	private boolean ignoreCookies;
	private String defaultEncoding;
	private String blockUrls;
	private String includeUrls;
	private long maxFileSize;
	private boolean compress;
	private String prefix;

	/**
	 * Build a command object from the Heritrix3Profile.
	 * @param heritrix3Profile The business model object.
	 * @return A new Heritrix3ProfileCommand object.
	 */
	public static Heritrix3ProfileCommand buildFromModel(Heritrix3Profile heritrix3Profile) {
		Heritrix3ProfileCommand command = new Heritrix3ProfileCommand();
		Heritrix3ProfileOptions options = heritrix3Profile.getHeritrix3ProfileOptions();
		command.setContactURL(options.getContactURL());
		command.setDocumentLimit(options.getDocumentLimit());
		command.setDataLimit(options.getDataLimit());
		command.setTimeLimit(options.getTimeLimit());
		command.setMaxPathDepth(options.getMaxPathDepth());
		command.setMaxHops(options.getMaxHops());
		command.setMaxTransitiveHops(options.getMaxTransitiveHops());
		command.setIgnoreRobotsTxt(options.isIgnoreRobotsTxt());
		command.setIgnoreCookies(options.isIgnoreCookies());
		command.setDefaultEncoding(options.getDefaultEncoding());
		command.setBlockUrls(options.getBlockURLs());
		command.setIncludeUrls(options.getIncludeURLs());
		command.setMaxFileSize(options.getMaxFileSize());
		command.setCompress(options.isCompress());
		command.setPrefix(options.getPrefix());

		return command;
	}
	
	/**
	 * Update the business object.
	 * @param heritrix3Profile The profile to update.
	 */
	public void updateBusinessModel(Heritrix3Profile heritrix3Profile) {
		Heritrix3ProfileOptions options = heritrix3Profile.getHeritrix3ProfileOptions();
		options.setContactURL(contactURL);
		options.setDocumentLimit(documentLimit);
		options.setDataLimit(dataLimit);
		options.setTimeLimit(timeLimit);
		options.setMaxPathDepth(maxPathDepth);
		options.setMaxHops(maxHops);
		options.setMaxTransitiveHops(maxTransitiveHops);
		options.setIgnoreRobotsTxt(ignoreRobotsTxt);
		options.setIgnoreCookies(ignoreCookies);
		options.setDefaultEncoding(defaultEncoding);
		options.setBlockURLs(blockUrls);
		options.setIncludeURLs(includeUrls);
		options.setMaxFileSize(maxFileSize);
		options.setCompress(compress);
		options.setPrefix(prefix);
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

	public long getDocumentLimit() {
		return documentLimit;
	}

	public void setDocumentLimit(long documentLimit) {
		this.documentLimit = documentLimit;
	}

	public long getDataLimit() {
		return dataLimit;
	}

	public void setDataLimit(long dataLimit) {
		this.dataLimit = dataLimit;
	}

	public long getTimeLimit() {
		return timeLimit;
	}

	public void setTimeLimit(long timeLimit) {
		this.timeLimit = timeLimit;
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

	public long getMaxFileSize() {
		return maxFileSize;
	}

	public void setMaxFileSize(long maxFileSize) {
		this.maxFileSize = maxFileSize;
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
}
