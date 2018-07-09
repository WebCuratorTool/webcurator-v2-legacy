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

/**
 * The Object for transfering Harvest Resources between the web curator components.
 * @author bbeaumont
 */
public class HarvestResourceDTO {
	/** The name of the harvest resource. */
	protected String name;
	/** the length of the resource. */
	protected long length;
	/** The status code of the resource. */
	protected int statusCode;
	/** the id of the resource. */
	protected Long oid;
	/** the id of the target instance the resource belongs to. */
    protected Long targetInstanceOid;
    /** the harvest result number the resource belongs to. */
	protected int harvestResultNumber;
	/** The temporary file name of a harvest resource that is being imported. */
	protected String tempFileName;
	/** The content-type of a harvest resource that is being imported. */
	protected String contentType;

	public HarvestResourceDTO() {
	}
	
	/**
	 * @return the harvestResultNumber
	 */
	public int getHarvestResultNumber() {
		return harvestResultNumber;
	}
	/**
	 * @param harvestResultNumber the harvestResultNumber to set
	 */
	public void setHarvestResultNumber(int harvestResultNumber) {
		this.harvestResultNumber = harvestResultNumber;
	}
	/**
	 * @return the length
	 */
	public long getLength() {
		return length;
	}
	/**
	 * @param length the length to set
	 */
	public void setLength(long length) {
		this.length = length;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the oid
	 */
	public Long getOid() {
		return oid;
	}
	/**
	 * @param oid the oid to set
	 */
	public void setOid(Long oid) {
		this.oid = oid;
	}
	/**
	 * @return the targetInstanceOid
	 */
	public Long getTargetInstanceOid() {
		return targetInstanceOid;
	}
	/**
	 * @param targetInstanceOid the targetInstanceOid to set
	 */
	public void setTargetInstanceOid(Long targetInstanceOid) {
		this.targetInstanceOid = targetInstanceOid;
	}	
	
	/** 
	 * @return the name of the job.
	 */
	public String buildJobName() {
        return targetInstanceOid.toString();
    }
	/**
	 * @return the statusCode
	 */
	public int getStatusCode() {
		return statusCode;
	}
	/**
	 * @param statusCode the statusCode to set
	 */
	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}	
	/**
	 * @return the temporary file name
	 */
	public String getTempFileName() {
		return tempFileName;
	}
	/**
	 * @param name the temporary file name to set
	 */
	public void setTempFileName(String tempFileName) {
		this.tempFileName = tempFileName;
	}
	/**
	 * @return the imported content type
	 */
	public String getContentType() {
		return contentType;
	}
	/**
	 * @param name the imported content type to set
	 */
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
}
