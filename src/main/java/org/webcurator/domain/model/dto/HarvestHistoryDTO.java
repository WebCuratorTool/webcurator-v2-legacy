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
package org.webcurator.domain.model.dto;

import java.util.Date;

import org.webcurator.core.util.ConverterUtil;

/**
 * HarvestHistoryDTO is a DTO object to hold the history
 * of harvests for a given Target.
 * 
 * @author beaumontb
 */
public class HarvestHistoryDTO {
	/**
	 * The target instance oid
	 */
	private Long oid;
	private Date startTime;
	private String state;
	private Long bytesDownloaded;
	private Long urlsDownloaded;
	private Long urlsFailed;
	private Long elapsedMillis;
	private Double kilobytesPerSecond;
	private String harvestStatus;
	
	
	
	public HarvestHistoryDTO(Long oid, Date startTime, String state, Long bytesDownloaded, Long urlsDownloaded, Long urlsFailed, Long elapsedMillis, Double kilobytesPerSecond, String harvestStatus) {
		super();
		this.oid = oid;
		this.startTime = startTime;
		this.state = state;
		this.bytesDownloaded = bytesDownloaded;
		this.urlsDownloaded = urlsDownloaded;
		this.urlsFailed = urlsFailed;
		this.elapsedMillis = elapsedMillis;
		this.kilobytesPerSecond = kilobytesPerSecond;
		this.harvestStatus = harvestStatus;
	}
	public Long getBytesDownloaded() {
		return bytesDownloaded;
	}
	public String getDownloadSize() {
		return ConverterUtil.formatBytes(bytesDownloaded);
	}
	public Long getElapsedMillis() {
		return elapsedMillis;
	}
	public String getElapsedTimeString() {
		return ConverterUtil.formatMilliseconds(elapsedMillis);
	}	
	public String getHarvestStatus() {
		return harvestStatus;
	}
	public Double getKilobytesPerSecond() {
		return kilobytesPerSecond;
	}
	/**
	 * The <code>TargetInstance</code oid
	 * @return
	 */
	public Long getOid() {
		return oid;
	}
	public Date getStartTime() {
		return startTime;
	}
	public String getState() {
		return state;
	}
	public Long getUrlsDownloaded() {
		return urlsDownloaded;
	}
	public Long getUrlsFailed() {
		return urlsFailed;
	}
	
	
	
}
