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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * The Object for transfering Harvest Results between the web curator components.
 * @author bbeaumont 
 */
public class HarvestResultDTO {
	/** The unique ID of the HarvestResult */
	protected Long oid;
	/** the id of the target instance that this result belongs to. */
    protected Long targetInstanceOid;
    /** the date the result was created. */
    protected Date creationDate;
    /** the number of the harvest. */
    protected int harvestNumber = 1;
    /** The harvests provenance note. */
    protected String provenanceNote;
    /** the resources that belong to this result. */
	protected Map<String,HarvestResourceDTO> resources = new HashMap<String,HarvestResourceDTO>();
	
	public HarvestResultDTO() {
	}

	/**
	 * Create a HarvestResultDTO from the HarvestResult, excluding the resources.
	 * @param hr The HarvestResult to base the DTO on.
	 */
	public HarvestResultDTO(Long hrOid, Long targetInstanceOid, Date creationDate, int harvestNumber, String provenanceNote) {
		this.oid = hrOid;
		this.targetInstanceOid = targetInstanceOid;
		this.creationDate = creationDate;
		this.harvestNumber = harvestNumber;
		this.provenanceNote = provenanceNote;
	}

	/**
     * @return Returns the resources.
     */
    public Map<String, HarvestResourceDTO> getResources() {
        return resources;
    }
    /**
     * @param resources The resources to set.
     */
    public void setResources(Map<String, HarvestResourceDTO> resources) {
        this.resources = resources;
    }
    
    /**
     * @return Returns the targetInstanceOid.
     */
    public Long getTargetInstanceOid() {
        return targetInstanceOid;
    }
    /**
     * @param targetInstanceOid The targetInstanceOid to set.
     */
    public void setTargetInstanceOid(Long targetInstanceOid) {
        this.targetInstanceOid = targetInstanceOid;
    }
    /**
     * @return Returns the creationDate.
     */
    public Date getCreationDate() {
        return creationDate;
    }
    /**
     * @param creationDate The creationDate to set.
     */
    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }
    /**
     * @return Returns the harvestNumber.
     */
    public int getHarvestNumber() {
        return harvestNumber;
    }
    /**
     * @param harvestNumber The harvestNumber to set.
     */
    public void setHarvestNumber(int harvestNumber) {
        this.harvestNumber = harvestNumber;
    }
    /**
     * @return Returns the provenanceNote.
     */
    public String getProvenanceNote() {
        return provenanceNote;
    }
    /**
     * @param provenanceNote The provenanceNote to set.
     */
    public void setProvenanceNote(String provenanceNote) {
        this.provenanceNote = provenanceNote;
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
}
