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
package org.webcurator.domain.model.core;

import org.webcurator.core.harvester.agent.HarvesterStatusUtil;
import org.webcurator.domain.model.core.harvester.agent.HarvesterStatusDTO;

/**
 * The status of a harvest.
 * @author nwaight
 * @hibernate.class table="HARVEST_STATUS"
 */
public class HarvesterStatus {
    /** the primary key. */
    private Long oid;
    /** The name of the harvest job. */
    private String jobName = "";
    /** the average number of URI's processed per second. */
    private double averageURIs = 0;
    /** The average rate of data downloaded. */
    private double averageKBs = 0;
    /** The number of urls that have been downloaded. */
    private long urlsDownloaded = 0;
    /** The number of urls failed to download.*/
    private long urlsFailed = 0;
    /** The uncompressed amount of data downloaded. */
    private long dataDownloaded = 0;
    /** The status of the harvest job. */
    private String status;
    /** The amount of elapsed time for the job. */    
    private long elapsedTime = 0; 
    /** the number of alerts that occurred during the harvest. */
    private int alertCount = 0;
    /** the WCT version. */
    private String applicationVersion;
    /** the Heritrix version. */
    private String heritrixVersion;
    /** the URLs downloaded successfully (derived field needed for sorting) **/
    private transient long urlsSucceeded;
    /** the percentage of URLs that failed to download successfully (derived field needed for sorting) **/
    private transient float percentageUrlsFailed;
    
    /** 
     * Default HarvesterStatus Constructor. 
     */
    public HarvesterStatus() {
        super();
    }
    
    /** 
     * Constructor to populate the object from a DTO
     * @param aStatusDTO the DTO to populate from
     */
    public HarvesterStatus(HarvesterStatusDTO aStatusDTO) {
        super();
        update(aStatusDTO);
    }
    
    /**
     * Update the HarvesterStatus object from the DTO.
     * @param aStatusDTO The DTO to update the object from.
     */
    public void update(HarvesterStatusDTO aStatusDTO) {
        jobName = aStatusDTO.getJobName();
        status = aStatusDTO.getStatus();
        applicationVersion = aStatusDTO.getApplicationVersion();
        heritrixVersion = aStatusDTO.getHeritrixVersion();
        
        if (aStatusDTO.getDataDownloaded() >= dataDownloaded) {
        	averageURIs = aStatusDTO.getAverageURIs();
        	averageKBs = aStatusDTO.getAverageKBs();
        	urlsDownloaded = aStatusDTO.getUrlsDownloaded();
        	urlsFailed = aStatusDTO.getUrlsFailed();
        	dataDownloaded = aStatusDTO.getDataDownloaded();
        	elapsedTime = aStatusDTO.getElapsedTime();
        	alertCount = aStatusDTO.getAlertCount();
        }                
    }
    
    /**
     * Returns a DTO that represents this HarvesterStatus.
     * @return A DTO that represents this HarvesterStatus.
     */
    public HarvesterStatusDTO getAsDTO() {
    	HarvesterStatusDTO dto = new HarvesterStatusDTO();
    	
    	dto.setAverageKBs(averageKBs);
    	dto.setAverageURIs(averageURIs);
    	dto.setDataDownloaded(dataDownloaded);
    	dto.setElapsedTime(elapsedTime);
    	dto.setJobName(jobName);
    	dto.setStatus(status);
    	dto.setUrlsDownloaded(urlsDownloaded);
    	dto.setUrlsFailed(urlsFailed);
    	dto.setAlertCount(alertCount);
    	dto.setApplicationVersion(applicationVersion);
    	dto.setHeritrixVersion(heritrixVersion);
    	
    	return dto;
    }
    
    /**
     * Return the average kilobytes per second being downloaded.
     * @return Returns the averageKBs.
     * @hibernate.property column="HS_AVG_KB"
     */
    public double getAverageKBs() {
        return averageKBs;
    }

    /**
     * Set the average kilobytes being dowloaded.
     * @param aAverageKBs The averageKBs to set.
     */
    public void setAverageKBs(double aAverageKBs) {
        this.averageKBs = aAverageKBs;
    }

    /**
     * Return the average number URIs being downloaded per second.
     * @return Returns the averageURIs.
     * @hibernate.property column="HS_AVG_URI"
     */
    public double getAverageURIs() {
        return averageURIs;
    }

    /**
     * Set the average number of URIs being downloaded per second.
     * @param aAverageURIs The averageURIs to set.
     */
    public void setAverageURIs(double aAverageURIs) {
        this.averageURIs = aAverageURIs;
    }

    /**
     * Return the amount of data downloaded as a readable string. For example, 
     * values are returned with the bytes, KB, MB, or GB suffix.
     * @return The amount of data downloaded as a readable string.
     */
    public String getDataDownloadedString() {
        return HarvesterStatusUtil.formatData(dataDownloaded);
    }
    
    /**
     * Get the amount of data downloaded as bytes.
     * @return Returns the dataDownloaded.
     * @hibernate.property column="HS_DATA_AMOUNT"
     */
    public long getDataDownloaded() {
        return dataDownloaded;
    }

    /**
     * Set the amount of data downloaded in bytes.
     * @param aDataDownloaded The dataDownloaded to set.
     */
    public void setDataDownloaded(long aDataDownloaded) {
        this.dataDownloaded = aDataDownloaded;
    }

    /**
     * Get the elapsed time for the harvest as 
     * a formatted string in the format dd:hh:mi:ss
     * @return Returns the elapsedTime. 
     */
    public String getElapsedTimeString() {
        return HarvesterStatusUtil.formatTime(elapsedTime);
    }
    
    /**
     * Get the number of milli-seconds elapsed since the start of the harvest.
     * @return Returns the elapsedTime.
     * @hibernate.property column="HS_ELAPSED_TIME"
     */
    public long getElapsedTime() {
        return elapsedTime;
    }

    /**
     * Set the number of milli-seconds elapsed since the start of the harvest.
     * @param aElapsedTime The elapsedTime to set.
     */
    public void setElapsedTime(long aElapsedTime) {
        this.elapsedTime = aElapsedTime;
    }

    /**
     * Get the name of the job being harvested.
     * @return Returns the jobName.
     * @hibernate.property column="HS_JOB_NAME" length="500"
     */
    public String getJobName() {
        return jobName;
    }

    /**
     * Set the name of the job being harvested.
     * @param aJobName The jobName to set.
     */
    public void setJobName(String aJobName) {
        this.jobName = aJobName;
    }

    /**
     * Get the status of the harvest.
     * @return Returns the status.
     * @hibernate.property column="HS_STATUS" length="255"
     */
    public String getStatus() {
        return status;
    }

    /**
     * Set the status of the harvest.
     * @param aStatus The status to set.
     */
    public void setStatus(String aStatus) {
        this.status = aStatus;
    }

    /**
     * Get the number of URLs downloaded.
     * @return Returns the urlsDownloaded.
     * @hibernate.property column="HS_URLS_DOWN"
     */
    public long getUrlsDownloaded() {
        return urlsDownloaded;
    }

    /**
     * Set the number of URLs downloaded.
     * @param aUrlsDownloaded The urlsDownloaded to set.
     */
    public void setUrlsDownloaded(long aUrlsDownloaded) {
        this.urlsDownloaded = aUrlsDownloaded;
    }

    /**
     * Get the number of URLs that failed to download.
     * @return Returns the urlsFailed.
     * @hibernate.property column="HS_URLS_FAILED"
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
     * Get tthe OID of the HarvesterStatus.
     * @return Returns the oid.
     * @hibernate.id column="HS_OID" generator-class="assigned" 
     */
    public Long getOid() {
        return oid;
    }

    /**
     * Set the OID of the HarvesterStatus. 
     * @param oid The oid to set.
     */
    public void setOid(Long oid) {
        this.oid = oid;
    }

	/**
	 * Get the number of alerts raised by the harvest.
	 * @return the alertCount
	 * @hibernate.property column="HS_ALERTS"
	 */
	public int getAlertCount() {
		return alertCount;
	}

	/**
	 * Set the number of alerts raised by the harvest.
	 * @param alertCount the alertCount to set
	 */
	public void setAlertCount(int alertCount) {
		this.alertCount = alertCount;
	}

	/**
	 * Set the application version for the harvest.
	 * @param applicationVersion the applicationVersion to set
	 */
	public void setApplicationVersion(String applicationVersion) {
		this.applicationVersion = applicationVersion;
	}

	/**
	 * Get the application version for the harvest.
	 * @return the applicationVersion
	 * @hibernate.property column="HS_APP_VERSION"
	 */
	public String getApplicationVersion() {
		return applicationVersion;
	}

	/**
	 * Set the heritrix version for the harvest.
	 * @param heritrixVersion the heritrixVersion to set
	 */
	public void setHeritrixVersion(String heritrixVersion) {
		this.heritrixVersion = heritrixVersion;
	}

	/**
	 * Get the heritrix version for the harvest.
	 * @return the heritrixVersion
	 * @hibernate.property column="HS_HRTX_VERSION"
	 */
	public String getHeritrixVersion() {
		return heritrixVersion;
	}   
	
	/**
	 * Get the number of URLs that were downloaded successfully (derived from getUrlsDownloaded() - this.getUrlsFailed()).
	* @hibernate.property name="urlsSucceeded" 
	* formula="HS_URLS_DOWN-HS_URLS_FAILED" type="long"  
	 * @return the number of URLs downloaded successfully
	 */
	public long getUrlsSucceeded() {
		return this.urlsSucceeded;
	}

	/**
	 * Set the number of URLs downloaded successfully (called by Hibernate formula)
	 * @param the number of URLs to set
	 */
	public void setUrlsSucceeded(long urlsSucceeded) {
		if (urlsSucceeded < 0) {
			this.urlsSucceeded = 0L;
		} else
			this.urlsSucceeded = urlsSucceeded;
	}

	/**
	 * Get the percentage of URLs that failed to download successfully accurate to 2 decimal places
	 * NOTE: The +0.00001 term removes the singularity when urls downloaded is 0.
	* @hibernate.property name="urlsSucceeded" 
	* formula="HS_URLS_FAILED/((HS_URLS_DOWN+0.00001)/100)" type="float"  
	 * @return the percentage of URLs that failed to download successfully
	 */
	public float getPercentageUrlsFailed() {
		return this.percentageUrlsFailed;
	}

	/**
	 * Set the number of URLs downloaded successfully (called by Hibernate formula)
	 * @param the number of URLs to set
	 */
	public void setPercentageUrlsFailed(float percentageUrlsFailed) {
		this.percentageUrlsFailed = percentageUrlsFailed;
	}
}
