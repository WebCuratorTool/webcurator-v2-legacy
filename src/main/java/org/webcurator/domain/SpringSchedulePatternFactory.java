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
package org.webcurator.domain;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.webcurator.domain.model.core.SchedulePattern;

/**
 * A Spring based Schedule Pattern Factory.
 * @author bbeaumont
 */
public class SpringSchedulePatternFactory implements SchedulePatternFactory {
	/** The list of schedules */
	private List<SchedulePattern> patterns = null;
	/** The id->pattern map of Schedule Patterns. */
	private Map<Integer, SchedulePattern> patternMap = new HashMap<Integer, SchedulePattern>();
	
	/**
	 * Setter called from Spring.
	 * @param aPatternList A list of schedule patterns.
	 */
	public void setPatterns(List<SchedulePattern> aPatternList) {
		// Store the list.
		patterns = aPatternList;
		
		// Map the list with the IDs.
		for(SchedulePattern sp: patterns) {
			patternMap.put(sp.getScheduleType(), sp);
		}
	}
	
	/**
	 * Return the list of patterns.
	 * @return the list of patterns.
	 */
	public List<SchedulePattern> getPatterns() {
		return patterns;
	}

	/**
	 * Gets the schedule with the specified ID.
	 * @return the schedule with the specified id; or null
	 * if the id is wrong.
	 */
	public SchedulePattern getPattern(int scheduleType) {
		return patternMap.get(scheduleType);
	}

	/**
	 * Return the list of patterns.
	 * @return the map of patterns
	 */
	public Map<Integer, SchedulePattern> getPatternMap() {
		return patternMap;
	}

}
