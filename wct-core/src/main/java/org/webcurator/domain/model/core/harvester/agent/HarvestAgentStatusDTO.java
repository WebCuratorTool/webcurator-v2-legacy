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
package org.webcurator.domain.model.core.harvester.agent;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.webcurator.core.harvester.agent.HarvesterStatusUtil;

/**
 * Data transfer object for passing the status of a Harvest Agent from
 * the agent to the core.
 * @author nwaight
 */
public class HarvestAgentStatusDTO {
    /** the name of the harvest agent. */
    private String name;
    /** the host name of the harvest agent. */
    private String host;
    /** the port of the harvest agent. */
    private int port;
    /** the service of the harvest agent. */
    private String service;
    /** the log reader service of the harvest agent. */
    private String logReaderService;
    /** This list of allowed Agencies. */
    private ArrayList<String> allowedAgencies = new ArrayList<String>();
    /** the max number of harvests this agent can manage. */
    private int maxHarvests;
    /** the status of all the agents harvests. */
    private HashMap<String, HarvesterStatusDTO> harvesterStatus;
    /** the amount of available memory. */
    private long memoryAvailable;
    /** the amount of memory used. */
    private long memoryUsed;    
    /** the current number of URI's processed per second. */
    private double currentURIs = 0;
    /** the average number of URI's processed per second. */
    private double averageURIs = 0;
    /** The current rate of data downloaded. */
    private double currentKBs = 0;
    /** The average rate of data downloaded. */
    private double averageKBs = 0;
    /** The number of urls that have been downloaded. */
    private long urlsDownloaded = 0;
    /** The number of urls queued.*/
    private long urlsQueued = 0;
    /** The uncompressed amount of data downloaded. */
    private long dataDownloaded = 0;
    /** the date and time the status was updated. */
    private Date lastUpdated;
    /** flag to indicate that this status may change shortly. */
    private boolean inTransition = false;
    /** flag to indicate that this agent has a memory warning. */
    private boolean memoryWarning = false;
	
    private boolean acceptTasks = true;
    
    /** Default Constructor. */
    public HarvestAgentStatusDTO() {
        super();
    }

    /**
     * @return Returns the harvesterStatus.
     */
    public HashMap<String, HarvesterStatusDTO> getHarvesterStatus() {
        return harvesterStatus;
    }

    /**
     * @param aHarvesterStatus The harvesterStatus to set.
     */
    public void setHarvesterStatus(HashMap<String, HarvesterStatusDTO> aHarvesterStatus) {
        this.harvesterStatus = aHarvesterStatus;
    }
    
    /**
     * @return the number of active harvests
     */
    public int getHarvesterStatusCount() {
        if (harvesterStatus == null || harvesterStatus.isEmpty()) {
            return 0;
        }
        
        return harvesterStatus.size();
    }

    /**
     * @return Returns the host.
     */
    public String getHost() {
        return host;
    }

    /**
     * @param aHost The host to set.
     */
    public void setHost(String aHost) {
        this.host = aHost;
    }

    /**
     * @return Returns the maxHarvests.
     */
    public int getMaxHarvests() {
        return maxHarvests;
    }

    /**
     * @param aMaxHarvests The maxHarvests to set.
     */
    public void setMaxHarvests(int aMaxHarvests) {
        this.maxHarvests = aMaxHarvests;
    }
    
    /**
     * @return Returns the memoryAvailable.
     */
    public long getMemoryAvailable() {
        return memoryAvailable;
    }

    /**
     * @param aMemoryAvailable The memoryAvailable to set.
     */
    public void setMemoryAvailable(long aMemoryAvailable) {
        this.memoryAvailable = aMemoryAvailable;
    }
    
    /**
     * @return Returns the memoryAvailable.
     */
    public String getMemoryAvailableString() {
        return HarvesterStatusUtil.formatData(memoryAvailable * 1024);
    }

    /**
     * @return Returns the memoryUsed.
     */
    public long getMemoryUsed() {
        return memoryUsed;
    }

    /**
     * @param aMemoryUsed The memoryUsed to set.
     */
    public void setMemoryUsed(long aMemoryUsed) {
        this.memoryUsed = aMemoryUsed;
    }

    /**
     * @return Returns the memory used.
     */
    public String getMemoryUsedString() {
        return HarvesterStatusUtil.formatData(memoryUsed * 1024);
    }
    
    /**
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }

    /**
     * @param aName The name to set.
     */
    public void setName(String aName) {
        this.name = aName;
    }

    /**
     * @return Returns the port.
     */
    public int getPort() {
        return port;
    }

    /**
     * @param aPort The port to set.
     */
    public void setPort(int aPort) {
        this.port = aPort;
    }

    /**
     * @return Returns the service.
     */
    public String getService() {
        return service;
    }

    /**
     * @param aService The service to set.
     */
    public void setService(String aService) {
        this.service = aService;
    }

    /**
     * @return Returns the log reader service.
     */
    public String getLogReaderService() {
        return logReaderService;
    }

    /**
     * @param aLogReaderService The service to set.
     */
    public void setLogReaderService(String aLogReaderService) {
        this.logReaderService = aLogReaderService;
    }

    /**
     * @return Returns the allowedAgencies.
     */
    public ArrayList<String> getAllowedAgencies() {
        return allowedAgencies;
    }

    /**
     * @param aAllowedAgencies The allowedAgencies to set.
     */
    public void setAllowedAgencies(ArrayList<String> aAllowedAgencies) {
        this.allowedAgencies = aAllowedAgencies;
    }

    /**
     * @return Returns the lastUpdated.
     */
    public Date getLastUpdated() {
        return lastUpdated;
    }

    /**
     * @param aLastUpdated The lastUpdated to set.
     */
    public void setLastUpdated(Date aLastUpdated) {
        this.lastUpdated = aLastUpdated;
    }

    /**
     * @return Returns the averageKBs.
     */
    public double getAverageKBs() {
        return averageKBs;
    }

    /**
     * @param aAverageKBs The averageKBs to set.
     */
    public void setAverageKBs(double aAverageKBs) {
        this.averageKBs = aAverageKBs;
    }

    /**
     * @return Returns the averageURIs.
     */
    public double getAverageURIs() {
        return averageURIs;
    }

    /**
     * @param aAverageURIs The averageURIs to set.
     */
    public void setAverageURIs(double aAverageURIs) {
        this.averageURIs = aAverageURIs;
    }

    /**
     * @return Returns the currentKBs.
     */
    public double getCurrentKBs() {
        return currentKBs;
    }

    /**
     * @param aCurrentKBs The currentKBs to set.
     */
    public void setCurrentKBs(double aCurrentKBs) {
        this.currentKBs = aCurrentKBs;
    }

    /**
     * @return Returns the currentURIs.
     */
    public double getCurrentURIs() {
        return currentURIs;
    }

    /**
     * @param aCurrentURIs The currentURIs to set.
     */
    public void setCurrentURIs(double aCurrentURIs) {
        this.currentURIs = aCurrentURIs;
    }

    /**
     * @return Returns the dataDownloaded.
     */
    public long getDataDownloaded() {
        return dataDownloaded;
    }

    /**
     * @param aDataDownloaded The dataDownloaded to set.
     */
    public void setDataDownloaded(long aDataDownloaded) {
        this.dataDownloaded = aDataDownloaded;
    }

    public String getDataDownloadedString() {
        return HarvesterStatusUtil.formatData(dataDownloaded);
    }
    
    /**
     * @return Returns the urlsDownloaded.
     */
    public long getUrlsDownloaded() {
        return urlsDownloaded;
    }

    /**
     * @param aUrlsDownloaded The urlsDownloaded to set.
     */
    public void setUrlsDownloaded(long aUrlsDownloaded) {
        this.urlsDownloaded = aUrlsDownloaded;
    }

    /**
     * @return Returns the urlsQueued.
     */
    public long getUrlsQueued() {
        return urlsQueued;
    }

    /**
     * @param aUrlsQueued The urlsQueued to set.
     */
    public void setUrlsQueued(long aUrlsQueued) {
        this.urlsQueued = aUrlsQueued;
    }

	/**
	 * @return the inTransition
	 */
	public boolean isInTransition() {
		return inTransition;
	}

	/**
	 * @param inTransition the inTransition to set
	 */
	public void setInTransition(boolean inTransition) {
		this.inTransition = inTransition;
	}

	/**
	 * @return the memoryWarning flag
	 */
	public boolean getMemoryWarning() {
		return memoryWarning;
	}

	/**
	 * @param memoryWarning the memoryWarning flag to set
	 */
	public void setMemoryWarning(boolean memoryWarning) {
		this.memoryWarning = memoryWarning;
	}

	public void setAcceptTasks(boolean acceptTasks) {
		this.acceptTasks = acceptTasks;
	}
	
	public boolean isAcceptTasks() {
		return acceptTasks;
	}
}
