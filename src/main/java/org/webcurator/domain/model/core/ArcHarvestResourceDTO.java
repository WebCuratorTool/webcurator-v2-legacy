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
 * A Data Transfer Oject for ArcHarvestResorce data.
 * @author bbeaumont
 */
public class ArcHarvestResourceDTO extends HarvestResourceDTO {
	/** The offset of the resource within the ARC file. */
	private long resourceOffset;
	/** The length of the resource. */
	private long resourceLength;
	/** the name of the arc file the resource is in. */
	private String arcFileName;
	/** flag to indicate that the arc file is compressed. */
	private boolean compressed;
	/** Default Constructor. */
	private int statusCode;
	
	
	public ArcHarvestResourceDTO() {
	}
	
	/**
	 * Constuct a new DTO passing in all the initial values.
	 * @param targetInstanceOid the id of the resouces target instance
	 * @param harvestResultNumber the number of the resorces harvest result
	 * @param oid the unique id
	 * @param name the resource name
	 * @param length the resource length
	 * @param resOffset the offset of the resource in the ARC file
	 * @param resLength the length of the resource in the ARC file
	 * @param arcFileName the name of the arc file
	 * @param compressed lag to indicate that the arc file is compressed
	 */
	public ArcHarvestResourceDTO(long targetInstanceOid, int harvestResultNumber, long oid, String name, long length, long resOffset, long resLength, String arcFileName, int statusCode, boolean compressed) {
        this.targetInstanceOid = targetInstanceOid;
		this.harvestResultNumber = harvestResultNumber;
		this.oid = oid;
		this.name = name;
		this.length = length;
		this.resourceOffset = resOffset;
		this.resourceLength = resLength;
		this.arcFileName = arcFileName;
		this.statusCode = statusCode;
		this.compressed = compressed;
		this.harvestResultNumber = harvestResultNumber;
	}
	
	/**
	 * @return the name of the arc file the resource resides in.
	 */
	public String getArcFileName() {
		return arcFileName;
	}
	
	/** 
	 * @param arcFileName the name of the arc file the resource resides in.
	 */
	public void setArcFileName(String arcFileName) {
		this.arcFileName = arcFileName;
	}
	
	/** 
	 * @return the flag to indicate that the arc file is compressed.
	 */
	public boolean isCompressed() {
		return compressed;
	}
	
	/** 
	 * @param compressed the flag to indicate that the arc file is compressed.	 
	 */
	public void setCompressed(boolean compressed) {
		this.compressed = compressed;
	}
	
	/**  
	 * @return the length of the resource.
	 */
	public long getResourceLength() {
		return resourceLength;
	}
	
	/**  
	 * @param resourceLength the length of the resource.
	 */
	public void setResourceLength(long resourceLength) {
		this.resourceLength = resourceLength;
	}
	
	/** 
	 * @return the offset of the resource in the arc file.
	 */
	public long getResourceOffset() {
		return resourceOffset;
	}
	
	/** 
	 * @param resourceOffset the offset of the resource in the arc file.
	 */
	public void setResourceOffset(long resourceOffset) {
		this.resourceOffset = resourceOffset;
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
}
