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
package org.webcurator.core.common;

/**
 * Holds the specific environment varaibales needed by the application.
 * This object can be obtained by the EnvironmentFactory.
 * @author bbeaumont
 */
public interface Environment {
	/**
	 * @return Returns the daysToSchedule.
	 */
	public int getDaysToSchedule();
    
	/**
	 * @return Returns the schedulesPerBatch.
	 */
	public int getSchedulesPerBatch();

	/**
     * @return the WCT Application version, which defines the current version
     * of the application.
     */
    public String getApplicationVersion();
    /**
     * @return the WCT Heritrix version, which defines the current version
     * of Heritrix.
     */
    public String getHeritrixVersion();
}
