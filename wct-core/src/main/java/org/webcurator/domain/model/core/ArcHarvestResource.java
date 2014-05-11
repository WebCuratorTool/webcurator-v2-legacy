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
 * @hibernate.joined-subclass table="ARC_HARVEST_RESOURCE"
 * @hibernate.joined-subclass-key column="AHRC_HARVEST_RESOURCE_OID"
 */
public class ArcHarvestResource extends HarvestResource {
	/** The offset into the ARC file where this resource starts */
	private long resourceOffset;
	/** The length of the resource in the ARC file. */
	private long resourceLength;
	/** The name of the ARC file this resource is in. */
	private String arcFileName;
	/** Whether the resource (and ARC file) are compressed */
	private boolean compressed;
	
	/** 
	 * No-arg constructor.
	 */
    public ArcHarvestResource() {
        super();
    }

    /**
     * Create an ArcHarvestResource from its DTO.
     * @param aResourceDTO The ArcHarvestResourceDTO to construct this object from.
     * @param aResult The ArcHarvestResult this resource belongs to.
     */
    public ArcHarvestResource(ArcHarvestResourceDTO aResourceDTO, ArcHarvestResult aResult) {
        super(aResourceDTO, aResult);
        resourceOffset = aResourceDTO.getResourceOffset();
        resourceLength = aResourceDTO.getResourceLength();
        arcFileName = aResourceDTO.getArcFileName();
        compressed = aResourceDTO.isCompressed();
    }
    
	/**
	 * Gets the length of the resource in the ARC file.
	 * @return The length of the resource in the ARC file.
	 * @hibernate.property length="100" not-null="true" column="AHRC_RESOURCE_LENGTH"
	 */		
	public long getResourceLength() {
		return resourceLength;
	}
	
	/**
	 * Sets the length of the resource in the ARC file.
	 * @param resourceLength the length of the resource in the ARC file.
	 */
	public void setResourceLength(long resourceLength) {
		this.resourceLength = resourceLength;
	}
	
	/**
	 * Gets the offset into the ARC file where this resource resides.
	 * @return the offset into the ARC file where this resource resides.
	 * @hibernate.property length="100" not-null="true" column="AHRC_RESOURCE_OFFSET"
	 */		
	public long getResourceOffset() {
		return resourceOffset;
	}
	
	/**
	 * Sets the offset into the ARC file where this resource resides.
	 * @param resourceOffset the offset into the ARC file where this resource resides.
	 */
	public void setResourceOffset(long resourceOffset) {
		this.resourceOffset = resourceOffset;
	}
	
	/**
	 * Gets the name of the ARC file in which this resource resides.
	 * @return The name of the ARC file.
	 * @hibernate.property length="100" not-null="true" column="AHRC_ARC_FILE_NAME"
	 */
	public String getArcFileName() {
		return this.arcFileName;
	}
	
	/**
	 * Sets the name of the ARC file in which this resource resides.
	 * @param arcFileName The name of the ARC file.
	 */
	public void setArcFileName(String arcFileName) {
		this.arcFileName = arcFileName;
	}
	
	/**
	 * Returns true if this resource (and therefore the ARC it is in) is 
	 * compressed.
	 * 
	 * @return True if the ARC resource is compressed; otherwise false.
	 * @hibernate.property length="100" not-null="true" column="AHRC_COMPRESSED_YN"
	 */		
	public boolean isCompressed() {
		return compressed;
	}
	
	/**
	 * Set whether the ARC resource is compressed.
	 * @param compressed True if the resource is compressed; otherwise false.
	 */
	public void setCompressed(boolean compressed) {
		this.compressed = compressed;
	}
	
	/**
	 * Builds an ArcHarvestResourceDTO from this object.
	 * @return A DTO build from this domain object.
	 */
	public ArcHarvestResourceDTO buildDTO() {
		ArcHarvestResourceDTO dto = new ArcHarvestResourceDTO();
		dto.setLength(this.getLength());
		dto.setName(this.getName());
		dto.setOid(this.getOid());
		dto.setTargetInstanceOid(result.targetInstance.getOid());
		
		dto.setArcFileName(this.arcFileName);
		dto.setCompressed(this.compressed);
		dto.setResourceLength(this.resourceLength);
		dto.setResourceOffset(this.resourceOffset);
		dto.setStatusCode(this.statusCode);
		
		return dto;		
	}


}
