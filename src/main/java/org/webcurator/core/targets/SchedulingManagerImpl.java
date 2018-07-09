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
package org.webcurator.core.targets;

import org.webcurator.domain.model.core.AbstractTarget;
import org.webcurator.domain.model.core.Schedule;

/**
 * The implementation of the SchedulingManager interface. 
 * @see SchedulingManager
 * @author bbeaumont
 */
public class SchedulingManagerImpl implements SchedulingManager {

	/**
	 * Schedule a target completely.
	 * @param aTarget The target to schedule.
	 */
	public void schedule(AbstractTarget aTarget) {
		
	}
	
	/**
	 * Unschedule a target completely.
	 * @param aTarget The target to unschedule.
	 */
	public void unschedule(AbstractTarget aTarget) {
		
	}
	
	/**
	 * Schedule this schedule and all of the targets associated
	 * with it.
	 * @param aSchedule The Schedule object to process.
	 */
	public void schedule(Schedule aSchedule) {
		
	}
}
