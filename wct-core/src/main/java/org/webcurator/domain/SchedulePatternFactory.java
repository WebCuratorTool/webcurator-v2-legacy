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

import java.util.List;
import java.util.Map;

import org.webcurator.domain.model.core.SchedulePattern;

/**
 * An interface for the Schedule Pattern Factory. This allows
 * us to abstract the source of the patterns. The first release
 * of the WCT will extract the schedule patterns from the 
 * Spring configuration. Later releases may move the patterns
 * to the database.
 * 
 * @author bbeaumont
 *
 */
public interface SchedulePatternFactory {
	/** 
	 * Get a list of all the Schedule Patterns defined by the
	 * system.
	 * @return A List of Schedule Patterns.
	 */
	public List<SchedulePattern> getPatterns();
	
	/**
	 * Gets a schedule pattern based on the type ID.
	 * @param scheduleType The ID of the type.
	 * @return A SchedulePattern object.
	 */
	public SchedulePattern getPattern(int scheduleType);
	
	/**
	 * Gets a map of scheduleType -> schedulePattern.
	 */
	public Map<Integer,SchedulePattern> getPatternMap();
}
