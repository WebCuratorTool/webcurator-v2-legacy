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
 * The Constants class contains global constants used in the harvest agent. 
 * @author nwaight
 */
public final class Constants {
    /** the name of the harvest agent bean. */
    public static final String BEAN_HARVEST_AGENT = "harvestAgent"; 
    /** the name of the quartz scheduler factory bean. */
    public static final String BEAN_SCHEDULER_FACTORY = "schedulerFactory";
    /** the name of the harvest coordinator nofifier bean. */
    public static final String BEAN_NOTIFIER = "harvestCoordinatorNotifier";
    /** the name of the harvest complete config bean. */
    public static final String BEAN_HARVEST_COMPLETE_CONFIG = "harvestCompleteConfig";
    
    /** the name of the Logs directory. */
    public static final String DIR_LOGS = "logs";
    /** the name of the reports directory. */
    public static final String DIR_REPORTS = "reports";
    /** the name of the Log file extension. */
    public static final String EXTN_LOGS = ".log";
    /** the name of the reports file extension. */
    public static final String EXTN_REPORTS = ".txt";
    /** the name of the open arc file extension file extension. */
    public static final String EXTN_OPEN_ARC = ".open";    
}
