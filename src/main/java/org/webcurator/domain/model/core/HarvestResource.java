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
 * A HarvestResource is a resource that has been harvested. It may be 
 * subclassed to provide additional functionality such as that required to
 * support resources within ARC files. 
 * 
 * All Quality Review tools should ideally work with HarvestResource objects 
 * rather than their subclasses, though this may not always be possible.
 * 
 * @hibernate.class table="HARVEST_RESOURCE" lazy="false"
 **/
public class HarvestResource {
	/** The name of the resource */
	protected String name;
	/** The length of the resource */
	protected long length;
	/** The status code of the resource */
	protected int statusCode;
	/** The database OID of the object */
	protected Long oid;
	/** The HarvestResult that this resource belongs to */
	protected HarvestResult result;

	
	/**
	 * No-arg constructor.
	 */
    public HarvestResource() {
        super();
    }
    
    /**
     * Construct a HarvestResource from its DTO.
     * @param aResource The DTO to create the HarvestResource object from.
     * @param aResult   The HarvestResult that this resource belongs to.
     */
    public HarvestResource(HarvestResourceDTO aResource, HarvestResult aResult) {
        super();
        name = aResource.getName();
        length = aResource.getLength();
        statusCode = aResource.getStatusCode();
        result = aResult;
    }
    
	/**
	 * Get the primary key of the HarvestResource.
	 * @return the primary key
     * @hibernate.id column="HRC_OID" generator-class="org.hibernate.id.MultipleHiLoPerTableGenerator"
     * @hibernate.generator-param name="table" value="ID_GENERATOR"
     * @hibernate.generator-param name="primary_key_column" value="IG_TYPE"
     * @hibernate.generator-param name="value_column" value="IG_VALUE"
     * @hibernate.generator-param name="primary_key_value" value="HarvestResource"  
	 */		
	public Long getOid() {
		return oid;
	}
	
	/**
	 * Set the primary key of the harvest resource.
	 * @param oid The OID of the resource.
	 */
	public void setOid(Long oid) {
		this.oid = oid;
	}
	
	/**
	 * Get the length of the resource in bytes.
	 * @return The length of the resource in bytes.
	 * @hibernate.property column="HRC_LENGTH"
	 */		
	public long getLength() {
		return length;
	}
	
	/**
	 * Set the length of the resource in bytes.
	 * @param length the length of the resource in bytes.
	 */
	public void setLength(long length) {
		this.length = length;
	}
	
	/**
	 * Get the name of the resource.
	 * 
	 * NB: The length of this property should be maintained both in the length
	 * Hibernate attribute, but also in ArcHarvestFileDTO.MAX_URL_LENGTH.
	 * 
	 * @return The name of the resource.
	 * @hibernate.property length="1020" not-null="true" column="HRC_NAME"
	 */	
	public String getName() {
		return name;
	}
	
	/**
	 * Set the name of the resource.
	 * @param name The name of the resource.
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Get the harvest result that this resource belongs to.
	 * @return The HarvestResult that this resource belongs to.
	 * @hibernate.many-to-one column="HRC_HARVEST_RESULT_OID"
	 */
	public HarvestResult getResult() {
		return result;
	}
	
	/**
	 * Set the harvest result that this resource belongs to.
	 * @param result The HarvesetResult that this resource belongs to.
	 */
	public void setResult(HarvestResult result) {
		this.result = result;
	}
	
	/**
	 * @return the statusCode
	 * @hibernate.property not-null="true" column="HRC_STATUS_CODE"
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
	 * Build a DTO from this object.
	 * @return A new DTO created from the data in this object.
	 */
	public HarvestResourceDTO buildDTO() {
		HarvestResourceDTO dto = new HarvestResourceDTO();
		dto.setLength(this.getLength());
		dto.setName(this.getName());
		dto.setOid(this.getOid());
		dto.setStatusCode(this.getStatusCode());
		dto.setTargetInstanceOid(this.result.targetInstance.getOid());
		return dto;
	}


	
}
