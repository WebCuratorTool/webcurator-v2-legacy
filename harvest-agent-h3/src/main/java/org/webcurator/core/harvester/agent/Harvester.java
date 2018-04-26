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
package org.webcurator.core.harvester.agent;

import java.io.File;
import java.util.List;

import org.webcurator.domain.model.core.harvester.agent.HarvesterStatusDTO;

/**
 * The Harvester interface must be implemented for classes that wrap a specific 
 * Harvester implementation.
 * @author nwaight
 */
public interface Harvester {
    /**
     * Return the name of the harvester instance.
     * @return the name of the harvester instance.
     */
    String getName();
    
    /**
     * Returns the status of harvester.
     * @return the harvester status
     */
    HarvesterStatusDTO getStatus();
    
    /**
     * Returns a list of directories where the harvests digital asset(s) are stored.
     * @return the harvests digital asset directories
     */    
    List<File> getHarvestDigitalAssetsDirs();
    
    /** 
     * Returns a flag to indicate if the harvest data is compressed or not.
     * @return true indicates the harvest data is compressed
     */
    boolean isHarvestCompressed();
    
    /**
     * The directory containing the harvests log data.
     * @return log data directory 
     */
    File getHarvestLogDir();
    
    /**
     * The directory containing the harvest data.
     * @return the harvest job directory
     */
    File getHarvestDir();
    
    /**
     * Start a harvest with the specified profile and job name.
     * @param aProfile the profile to use for the harvest
     * @param aJobName the name of the harvest
     */
    void start(File aProfile, String aJobName);

    /**
     * Recover harvest by getting status from H3.
     */
    void recover();

    /**
     * Stop the harvester crawling.
     */
    void stop();
    
    /**
     * Pause the harvester if it is running.
     */
    void pause();
    
    /** 
     * Resume the harvest if it is paused.
     */
    void resume();
                
    /** abort the harvest. */
    void abort();
    
    /** flag to indicate that the job was aborted. */
    boolean isAborted();    
    
    /**
     * Set the maximum allowed bandwidth for the harvest.
     * @param aBandwidthLimit the maximum allowed bandwidth for the harvest
     */
    void restrictBandwidth(int aBandwidthLimit);
    
    /**
     * Perform an final tidyups.
     */
    void deregister();
    
    /**
     * Set the number of alerts that have to occur before a notification message is sent.
     * @param alertThreshold the number of alerts
     */
    void setAlertThreshold(int alertThreshold);
}
