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
 * Common constants used in the Core.
 * @author nwaight
 */
public interface Constants {
    /** the name of the harvest coordinator bean. */
    String BEAN_HARVEST_COORDINATOR = "harvestCoordinator";
    /** the name of the harvest coordinator dao bean. */
    String BEAN_HARVEST_COORDINATOR_DAO = "harvestCoordinatorDao";    
    /** the name of the auditor bean. */
    String BEAN_AUDITOR = "audit";
    /** the name of the target instance manager bean. */
    String BEAN_TARGET_INSTANCE_MNGR = "targetInstanceManager";
    /** the name of the Logon Duration DAO bean */
    String BEAN_LOGON_DURATION_DAO = "logonDuration";
    /** the name of the target instance manager bean. */
    String BEAN_TARGET_MNGR = "targetManager";

    String BEAN_HARVEST_BANDWIDTH_MANAGER= "harvestBandwidthManager";

}
