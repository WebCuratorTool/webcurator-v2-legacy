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

import org.webcurator.core.harvester.agent.HarvesterStatusUtil;

/**
 * The Data transfer object for passing the status of a harvest from 
 * the agent to the core.
 * @author nwaight
 */
public class HarvesterStatusDTO {
    /** The name of the harvester. */
    private String harvesterName = "UNKNOWN";
    /** The the harvester state. */
    private String harvesterState = "UNKNOWN";
    /** The name of the harvest job. */
    private String jobName = "";
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
    /** The number of urls failed to download.*/
    private long urlsFailed = 0;
    /** The uncompressed amount of data downloaded. */
    private long dataDownloaded = 0;
    /** The status of the harvest job. */
    private String status = "UNKNOWN";
    /** The amount of elapsed time for the job. */    
    private long elapsedTime = 0;    
    /** The number of alerts that occurred for the harvest. */
    private int alertCount = 0;
    /** the WCT version. */
    private String applicationVersion;
    /** the Heritrix version. */
    private String heritrixVersion;
    
    /** 
     * Default HarvesterStatusDTO Constructor. 
     */
    public HarvesterStatusDTO() {
        super();
    }
    
    /** 
     * HarvesterStatusDTO Constructor.
     * @param aHarvesterName the name of the harvester this status belongs to 
     */
    public HarvesterStatusDTO(String aHarvesterName) {
        super();
        harvesterName = aHarvesterName;
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

    public String getDataDownloadedString() {
        return HarvesterStatusUtil.formatData(dataDownloaded);
    }
    
    /**
     * @param aDataDownloaded The dataDownloaded to set.
     */
    public void setDataDownloaded(long aDataDownloaded) {
        this.dataDownloaded = aDataDownloaded;
    }

    /**
     * @return Returns the elapsedTime. 
     */
    public String getElapsedTimeString() {
        return HarvesterStatusUtil.formatTime(elapsedTime);
    }
    
    /**
     * @return Returns the elapsedTime.
     */
    public long getElapsedTime() {
        return elapsedTime;
    }

    /**
     * @param aElapsedTime The elapsedTime to set.
     */
    public void setElapsedTime(long aElapsedTime) {
        this.elapsedTime = aElapsedTime;
    }

    /**
     * @return Returns the harvesterName.
     */
    public String getHarvesterName() {
        return harvesterName;
    }

    /**
     * @return Returns the jobName.
     */
    public String getJobName() {
        return jobName;
    }

    /**
     * @param aJobName The jobName to set.
     */
    public void setJobName(String aJobName) {
        this.jobName = aJobName;
    }

    /**
     * @return Returns the status.
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param aStatus The status to set.
     */
    public void setStatus(String aStatus) {
        this.status = aStatus;
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
     * Reset the status object.
     */
    public void reset() {
        jobName = "";
        currentURIs = 0;
        averageURIs = 0;
        currentKBs = 0;
        averageKBs = 0;
        urlsDownloaded = 0;
        urlsQueued = 0;
        dataDownloaded = 0;
        status = "";    
        elapsedTime = 0;    
    }

    /**
     * @return Returns the harvesterState.
     */
    public String getHarvesterState() {
        return harvesterState;
    }

    /**
     * @param harvesterState The harvesterState to set.
     */
    public void setHarvesterState(String harvesterState) {
        this.harvesterState = harvesterState;
    }

    /**
     * @param harvesterName The harvesterName to set.
     */
    public void setHarvesterName(String harvesterName) {
        this.harvesterName = harvesterName;
    }

    /**
     * @return Returns the urlsFailed.
     */
    public long getUrlsFailed() {
        return urlsFailed;
    }

    /**
     * @param urlsFailed The urlsFailed to set.
     */
    public void setUrlsFailed(long urlsFailed) {
        this.urlsFailed = urlsFailed;
    }

	/**
	 * @return the alertCount
	 */
	public int getAlertCount() {
		return alertCount;
	}

	/**
	 * @param alertCount the alertCount to set
	 */
	public void setAlertCount(int alertCount) {
		this.alertCount = alertCount;
	}
	
	/**
	 * @param applicationVersion the applicationVersion to set
	 */
	public void setApplicationVersion(String applicationVersion) {
		this.applicationVersion = applicationVersion;
	}

	/**
	 * @return the applicationVersion
	 */
	public String getApplicationVersion() {
		return applicationVersion;
	}

	/**
	 * @param heritrixVersion the heritrixVersion to set
	 */
	public void setHeritrixVersion(String heritrixVersion) {
		this.heritrixVersion = heritrixVersion;
	}

	/**
	 * @return the heritrixVersion
	 */
	public String getHeritrixVersion() {
		return heritrixVersion;
	}   
	
}
