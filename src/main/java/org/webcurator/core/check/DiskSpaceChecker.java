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
package org.webcurator.core.check;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.archive.util.IoUtils;

/**
 * A checker that can perform a check of the available disk space 
 * on a unix/linux system
 * @author nwaight
 */
public class DiskSpaceChecker extends AbstractChecker { 
	/** value for all flags off. */
	private static final int ALL_OFF = 0x0000;
	/** mask for turning on the WARNING flag. */
	private static final int MASK_WARN_ON = 0x0001;
	/** mask for turning on the ERROR flag. */
	private static final int MASK_ERROR_ON = 0x0002;
	/** mask for turning off the WARNING flag. */
	private static final int MASK_WARN_OFF = 0xFFFE;
	/** mask for turning off the ERROR flag. */
	private static final int MASK_ERROR_OFF = 0xFFFD;
	
	/** the disk check command to run. */
	private String command = "df -k";
	/** the pattern for checking disk space. */
	private String pattern = "(?m)\\s(\\d+)\\s+(\\d+)%\\s+(\\S+)$"; 
	/** the index in the pattern of the mount usage %. */
	private int usageIndex = 2;
	/** the index in the pattern of the mount name. */
	private int mountIndex = 3;
	/** The warning processor usage threshold. */
	private int warnThreshold;
	/** The error processor usage threshold. */
	private int errorThreshold;	
	/** The list of mounts to check the disk space for. */ 
	private HashMap<String, Integer> mounts = new HashMap<String, Integer>();	
	/** the logger. */
	private static Log log = LogFactory.getLog(DiskSpaceChecker.class);
			
	/**
	 * Constructor 
	 * @param aWarnThreshold warning level threshold
	 * @param aErrorThreshold error level threshold
	 * @param aMounts the list of mount names
	 */
	public DiskSpaceChecker(int aWarnThreshold, int aErrorThreshold, ArrayList<String> aMounts) {
		warnThreshold = aWarnThreshold;
		errorThreshold = aErrorThreshold;
		
		for (String mnt : aMounts) {
			mounts.put(mnt, new Integer(0));
		} 
	}
	
	/* (non-Javadoc)
	 * @see org.webcurator.core.check.Checker#check()
	 */
	public void check() {
		try {
			String df = IoUtils.readFullyAsString(Runtime.getRuntime().exec(command).getInputStream());
			if (null == df || df.trim().equals("")) {
				return;
			}
			
			Pattern spacePattern = Pattern.compile(pattern);
			Integer flags = null;
			Integer diskUsed = null;
			Matcher matcher = spacePattern.matcher(df);
			while (matcher.find()) {
				if (mounts.containsKey(matcher.group(mountIndex))) {
					flags = mounts.get(matcher.group(mountIndex));					
					diskUsed = Integer.parseInt(matcher.group(usageIndex));
					if (log.isDebugEnabled()) {
						log.debug("The mount " + matcher.group(mountIndex) + " is " + diskUsed + "% available.");
					}					
					
					if (diskUsed.intValue() >= warnThreshold && (flags & MASK_WARN_ON) == ALL_OFF) {
						flags = flags | MASK_WARN_ON;
						if (log.isWarnEnabled()) {
							log.warn("The used disk is above the warning threshold " + warnThreshold + "% and is " + diskUsed.intValue() + "%");
						}
						notify(LEVEL_WARNING, "The used disk is above the warning threshold " + warnThreshold + "% and is " + diskUsed.intValue() + "%");
					}
					else if (diskUsed.intValue() >= errorThreshold && (flags & MASK_ERROR_ON) == ALL_OFF) {
						flags = flags | MASK_ERROR_ON;
						if (log.isErrorEnabled()) {
							log.error("The used disk is above the error threshold " + errorThreshold + "% and is " + diskUsed.intValue() + "%");
						}
						notify(LEVEL_ERROR, "The used disk is above the error threshold " + errorThreshold + "% and is " + diskUsed.intValue() + "%");						
					}
					else if (diskUsed.intValue() < warnThreshold && (flags & MASK_WARN_ON) == MASK_WARN_ON) {
						flags = flags & MASK_WARN_OFF;
						if (log.isInfoEnabled()) {
							log.info("The available disk has recovered below the warning thershold " + warnThreshold + "% and is " + diskUsed.intValue() + "%");
						}
					}
					else if (diskUsed.intValue() < errorThreshold && (flags & MASK_ERROR_ON) == MASK_ERROR_ON) {
						flags = flags & MASK_ERROR_OFF;
						if (log.isInfoEnabled()) {
							log.info("The available disk has recovered below the error thershold " + errorThreshold + "% and is " + diskUsed.intValue() + "%");
						}
					}
					
					mounts.put(matcher.group(mountIndex), flags);
				}
			}
		} 
		catch (Exception e) {
			if (log.isErrorEnabled()) {
				log.error("Failed to complete disk space check " + e.getMessage(), e);
			}
		}
	}

	/**
	 * @param command the command to set
	 */
	public void setCommand(String command) {
		this.command = command;
	}

	/**
	 * @param mountIndex the mountIndex to set
	 */
	public void setMountIndex(int mountIndex) {
		this.mountIndex = mountIndex;
	}

	/**
	 * @param pattern the pattern to set
	 */
	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	/**
	 * @param usageIndex the usageIndex to set
	 */
	public void setUsageIndex(int usageIndex) {
		this.usageIndex = usageIndex;
	}
}
