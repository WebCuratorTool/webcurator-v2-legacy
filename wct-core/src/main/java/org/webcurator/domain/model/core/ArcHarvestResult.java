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

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * An ARC Harvest Result is the content harvested by a Target Instance, or an
 * altered version of that content created through something like the copy
 * and prune use case.
 * 
 * @author bbeaumont
 * 
 * @hibernate.joined-subclass table="ARC_HARVEST_RESULT"
 * @hibernate.joined-subclass-key column="AHRS_HARVEST_RESULT_OID"
 */
public class ArcHarvestResult extends HarvestResult {
	/** The set of ARC files that make up this HarvestResult */
	private Set<ArcHarvestFile> arcFiles;
	
	/**
	 * No-arg constructor for this object.
	 */
    public ArcHarvestResult() {
        super();
        arcFiles = new HashSet<ArcHarvestFile>();
    }
    
    public ArcHarvestResult(TargetInstance aTargetInstance, int harvestNumber) { 
    	this.targetInstance = aTargetInstance;
    	this.creationDate = new Date();
    	this.createdBy = aTargetInstance.getOwner();
    	this.harvestNumber = harvestNumber;
    }
    
    /**
     * Create an ArcHarvestResult from its DTO.
     * @param aResultDTO The DTO.
     * @param aTargetInstance The TargetInstance that this HarvestResult
     * 						  belongs to.
     */
    public ArcHarvestResult(ArcHarvestResultDTO aResultDTO, TargetInstance aTargetInstance) {
        super();
        arcFiles = new HashSet<ArcHarvestFile>();
        targetInstance = aTargetInstance;
        harvestNumber = aResultDTO.getHarvestNumber();
        provenanceNote = aResultDTO.getProvenanceNote();
        creationDate = aResultDTO.getCreationDate();
        createdBy = aTargetInstance.getOwner();
        
        ArcHarvestResource harvestResource = null;
        ArcHarvestResourceDTO harvestResourceDTO = null;
        String key = "";        
        Map resourceDtos = aResultDTO.getResources();
        Iterator it = resourceDtos.keySet().iterator();
        while (it.hasNext()) {
            key = (String) it.next();
            harvestResourceDTO = (ArcHarvestResourceDTO) resourceDtos.get(key);
            harvestResource = new ArcHarvestResource(harvestResourceDTO, this);
            resources.put(key, harvestResource);
        }
        
        ArcHarvestFile file = null;
        ArcHarvestFileDTO fileDTO = null;
        
        if(aResultDTO.getArcFiles() != null) { 
	        Set arcDTOs = aResultDTO.getArcFiles();
	        it = arcDTOs.iterator();
	        while (it.hasNext()) {
	            fileDTO = (ArcHarvestFileDTO) it.next();
	            file = new ArcHarvestFile(fileDTO, this);
	            arcFiles.add(file);
	        }
        }
    }
    
	/**
	 * Get the set of ARC files that make up this HarvestResult. 
	 * @hibernate.set cascade="save-update"
	 * @hibernate.collection-key column="AHF_ARC_HARVEST_RESULT_ID"
	 * @hibernate.collection-one-to-many class="org.webcurator.domain.model.core.ArcHarvestFile"
	 * @return the set of ARC files that make up this HarvestResult
	 */
	public Set<ArcHarvestFile> getArcFiles() {
		return arcFiles;
	}

	/**
	 * Set the set of ARC files that make up this HarvestResult.
	 * @param arcFiles The set of ARC Harvest Files.
	 */
	public void setArcFiles(Set<ArcHarvestFile> arcFiles) {
		this.arcFiles = arcFiles;
	}
	
	/**
	 * Create an index for this HarvestResult, assuming that all ARC Files
	 * are in the basedir provided.
	 * @param baseDir The base directory for the ARC files.
	 * @throws IOException if the indexing fails.
	 */
	public void index(File baseDir) throws IOException {
		for(ArcHarvestFile ahf: arcFiles) {
			this.getResources().putAll(ahf.index(baseDir));
		}
	}
    
	/**
	 * Create an index for this HarvestResult, assuming that each ARC File is 
	 * in the base directory that it states in the ArcHarvestFile object.
	 * @throws IOException if the indexing fails.
	 */
    public void index() throws IOException {
        for(ArcHarvestFile ahf: arcFiles) {
            this.getResources().putAll(ahf.index());
        }
    }
}
