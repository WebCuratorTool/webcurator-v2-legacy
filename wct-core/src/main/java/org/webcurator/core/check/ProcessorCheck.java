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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.archive.util.IoUtils;

/**
 * An available processor capacity checker.
 * @author nwaight
 */
public class ProcessorCheck extends AbstractChecker {
	/** the processor check command to run. */
	private String command = "sar -u";
	/** the pattern for getting the processor idle time. */
	private String pattern = "(?m)\\w+:\\w+:\\w+\\s\\w+\\s+\\S+\\s+\\S+\\s+\\S+\\s+\\S+\\s+(\\S+)$";	
	/** The warning processor usage threshold. */
	private int warnThreshold;
	/** The error processor usage threshold. */
	private int errorThreshold;
	/** available processor is below the warning threshold. */
	private boolean belowWarnThreshold = false;
	/** available processor is below the error threshold. */
	private boolean belowErrorThreshold = false;
		
	/** the logger. */
	private static Log log = LogFactory.getLog(ProcessorCheck.class);
	
	/* (non-Javadoc)
	 * @see org.webcurator.core.check.Checker#check()
	 */
	public void check() {
		try {
			String sar = IoUtils.readFullyAsString(Runtime.getRuntime().exec(command).getInputStream());	
			if (null == sar || sar.trim().equals("")) {
				return;
			}
			
			String lastVal = "";
			Pattern procesorPattern = Pattern.compile(pattern);
			Matcher matcher = procesorPattern.matcher(sar);
			while (matcher.find()) {
				lastVal = matcher.group(1);
			}
			
			Double processorAvail = Double.parseDouble(lastVal);
			if (log.isDebugEnabled()) {
				log.debug("The processor is " + processorAvail + "% available.");
			}
				
			if (processorAvail.intValue() <= warnThreshold && !belowWarnThreshold) {
				belowWarnThreshold = true;
				if (log.isWarnEnabled()) {
					log.warn("The available processor has fallen below the warning threshold " + warnThreshold + "% and is " + processorAvail.intValue() + "%");
				}
				notify(LEVEL_WARNING, "The available processor has fallen below the warning threshold " + warnThreshold + "% and is " + processorAvail.intValue() + "%");
			}
			else if (processorAvail.intValue() <= errorThreshold && !belowErrorThreshold) {
				belowErrorThreshold = true;
				if (log.isErrorEnabled()) {
					log.error("The available processor has fallen below the error threshold " + errorThreshold + "% and is " + processorAvail.intValue() + "%");
				}
				notify(LEVEL_ERROR, "The available processor has fallen below the error threshold " + errorThreshold + "% and is " + processorAvail.intValue() + "%");
			}
			else if (processorAvail.intValue() > warnThreshold && belowWarnThreshold) {
				belowWarnThreshold = false;
				if (log.isInfoEnabled()) {
					log.info("The available processor has recovered above the warning threshold " + warnThreshold + "% and is " + processorAvail.intValue() + "%");
				}
			}
			else if (processorAvail.intValue() > errorThreshold && belowErrorThreshold) {
				belowErrorThreshold = false;
				if (log.isInfoEnabled()) {
					log.info("The available processor has recovered above the error threshold " + errorThreshold + "% and is " + processorAvail.intValue() + "%");
				}
			}								
		} 
		catch (Exception e) {
			if (log.isErrorEnabled()) {
				log.error("Failed to complete processor check " + e.getMessage(), e);
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
	 * @param errorThreshold the errorThreshold to set
	 */
	public void setErrorThreshold(int errorThreshold) {
		this.errorThreshold = errorThreshold;
	}

	/**
	 * @param pattern the pattern to set
	 */
	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	/**
	 * @param warnThreshold the warnThreshold to set
	 */
	public void setWarnThreshold(int warnThreshold) {
		this.warnThreshold = warnThreshold;
	}
}
