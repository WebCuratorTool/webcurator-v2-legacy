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
package org.webcurator.core.harvester.agent.schedule;

/**
 * The HarvestCompleteConfig class is a bean that is configured via
 * the Spring Framework.  
 * It contains settings that are used when a harvest is completed. 
 * @author nwaight
 */
public class HarvestCompleteConfig {
	/** 
	 * the number of seconds to wait before attempting to perform the
	 * harvest completion processing.
	 */ 
	private int waitOnCompleteSeconds = 30;
	
	/** 
	 * The number of seconds to wait bofore re-trying to perform the harvest 
	 * completion processing.  Where the number of retries is below the 
	 * level retry band.
	 */
	private int waitOnFailureLevelOneSecs = 300;
	
	/** 
	 * The number of seconds to wait bofore re-trying to perform the harvest 
	 * completion processing.  Where the number of retries is above the 
	 * level retry band.
	 */
	private int waitOnFailureLevelTwoSecs = 600;
	
	/** 
	 * The number of harvest completion retries to attempt before increasing the
	 * wait period.
	 */
	private int levelRetryBand = 3;

	/**
	 * @param levelRetryBand the levelRetryBand to set
	 */
	public void setLevelRetryBand(int levelRetryBand) {
		this.levelRetryBand = levelRetryBand;
	}

	/**
	 * @param waitOnCompleteSeconds the waitOnCompleteSeconds to set
	 */
	public void setWaitOnCompleteSeconds(int waitOnCompleteSeconds) {
		this.waitOnCompleteSeconds = waitOnCompleteSeconds;
	}

	/**
	 * @param waitOnFailureLevelOneSecs the waitOnFailureLevelOneSecs to set
	 */
	public void setWaitOnFailureLevelOneSecs(int waitOnFailureLevelOneSecs) {
		this.waitOnFailureLevelOneSecs = waitOnFailureLevelOneSecs;
	}

	/**
	 * @param waitOnFailureLevelTwoSecs the waitOnFailureLevelTwoSecs to set
	 */
	public void setWaitOnFailureLevelTwoSecs(int waitOnFailureLevelTwoSecs) {
		this.waitOnFailureLevelTwoSecs = waitOnFailureLevelTwoSecs;
	}

	/**
	 * @return the levelRetryBand
	 */
	public int getLevelRetryBand() {
		return levelRetryBand;
	}

	/**
	 * @return the waitOnCompleteSeconds
	 */
	public int getWaitOnCompleteSeconds() {
		return waitOnCompleteSeconds;
	}

	/**
	 * @return the waitOnFailureLevelOneSecs
	 */
	public int getWaitOnFailureLevelOneSecs() {
		return waitOnFailureLevelOneSecs;
	}

	/**
	 * @return the waitOnFailureLevelTwoSecs
	 */
	public int getWaitOnFailureLevelTwoSecs() {
		return waitOnFailureLevelTwoSecs;
	}
}
