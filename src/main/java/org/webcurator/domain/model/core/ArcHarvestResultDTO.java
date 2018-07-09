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
import java.text.ParseException;
import java.util.Date;
import java.util.Set;

/**
 * The Object for transfering harvest result objects between the Asset store and the 
 * other web curator components
 * @author bbeaumont
 */
public class ArcHarvestResultDTO extends HarvestResultDTO {
	/** Set of ARC files that belong to the harvest result. */
	public Set<ArcHarvestFileDTO> arcFiles;
	
	
	/**
	 * Default constructor.
	 */
	public ArcHarvestResultDTO() {
	}

	/**
	 * Create a DTO based on a real object.
	 * @param hr The HarvestResult to base the DTO on.
	 */
	public ArcHarvestResultDTO(Long hrOid, Long targetInstanceOid, Date creationDate, int harvestNumber, String provenanceNote) { 
		super(hrOid, targetInstanceOid, creationDate, harvestNumber, provenanceNote);
	}
	
	
	/**
	 * @return the set of ARC file DTO's.
	 */
	public Set<ArcHarvestFileDTO> getArcFiles() {
		return arcFiles;
	}

	/** 
	 * @param arcFiles the set of ARC file DTO's.
	 */
	public void setArcFiles(Set<ArcHarvestFileDTO> arcFiles) {
		this.arcFiles = arcFiles;
	}
	
	/**
	 * Perform an index on all the ARC files in the specified base directory 
	 * @param baseDir the directory containing the ARC files to index
	 * @throws IOException thrown if there is an error
	 * @throws ParseException 
	 */
	public void index(File baseDir) throws IOException, ParseException {
		for(ArcHarvestFileDTO ahf: arcFiles) {
			this.getResources().putAll(ahf.index(baseDir));
		}
	}
    
	/** 
	 * Perform an index on all the ARC files referred to by this result. 
	 * @throws IOException thrown if there is an error
	 * @throws ParseException 
	 */
    public void index() throws IOException, ParseException {
        for(ArcHarvestFileDTO ahf: arcFiles) {
            this.getResources().putAll(ahf.index());
        }
    }
}
