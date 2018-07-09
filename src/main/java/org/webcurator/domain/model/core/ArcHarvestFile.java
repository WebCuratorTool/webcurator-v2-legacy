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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.httpclient.Header;
import org.archive.io.arc.ARCReader;
import org.archive.io.arc.ARCReaderFactory;
import org.archive.io.arc.ARCRecord;

/**
 * This class stores information about an ARC file that forms part of a 
 * harvest. 
 * 
 * @hibernate.class table="ARC_HARVEST_FILE" lazy="true"
 **/
public class ArcHarvestFile {
	/** An OID for the Harvest File */
	private Long oid;
	/** The name of the ARC File */
	private String name;
	/** true if the ARC file is compressed; otherwise false */
	private boolean compressed;
	/** The base directory in which the ARC file exists. */
	private String baseDir;
	/** The ArcHarvestResult that this file belong to. */
	private ArcHarvestResult harvestResult;
    
	/**
	 * No-arg constructor.
	 */
    public ArcHarvestFile() {
        super();
    }

    /**
     * Creates an ArcHarvestFile from a DTO and HarvestResult.
     * @param aHarvestFile   The DTO object to create the object from.
     * @param aHarvestResult The ArcHarvestResult that this ARC file belongs to.
     */
    public ArcHarvestFile(ArcHarvestFileDTO aHarvestFile, ArcHarvestResult aHarvestResult) {
        harvestResult = aHarvestResult;
        name = aHarvestFile.getName();
        compressed = aHarvestFile.isCompressed();
    }
	
	/**
	 * True if the ARC file is compressed; otherwise false.
	 * 
	 * @hibernate.property not-null="true" column="AHF_COMPRESSED"
	 * @return true if the ARC file is compressed; otherwise false.
	 */
	public boolean isCompressed() {
		return compressed;
	}

	/**
	 * Set whether the ARC file is compressed.
	 * @param compressed true if compressed; otherwise false.
	 */
	public void setCompressed(boolean compressed) {
		this.compressed = compressed;
	}

	/**
	 * Returns the primary key of the database object.
	 * @return the primary key
     * @hibernate.id column="AHF_OID" generator-class="org.hibernate.id.MultipleHiLoPerTableGenerator"
     * @hibernate.generator-param name="table" value="ID_GENERATOR"
     * @hibernate.generator-param name="primary_key_column" value="IG_TYPE"
     * @hibernate.generator-param name="value_column" value="IG_VALUE"
     * @hibernate.generator-param name="primary_key_value" value="ArcHarvestFile"  
	 */		
	public Long getOid() {
		return oid;
	}

	/**
	 * Set the OID of the object.
	 * @param oid The database oid.
	 */
	public void setOid(Long oid) {
		this.oid = oid;
	}

	/**
	 * Gets the name of the ARC file.
	 * @return the name of the ARC file.
	 * @hibernate.property length="100" not-null="true" column="AHF_NAME" unique="true"
	 */		
	public String getName() {
		return name;
	}
	
	/**
	 * Sets the name of the ARC file.
	 * @param name The name of the ARC file.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Returns the ArcHarvestResult that this ARC file belongs to.
	 * @hibernate.many-to-one column="AHF_ARC_HARVEST_RESULT_ID" foreign-key="FK_AHR_ARC_HARVEST_RESULT_ID"
	 * @return the ArcHarvestResult that this ARC file belongs to
	 */	
	public ArcHarvestResult getHarvestResult() {
		return harvestResult;
	}

	/**
	 * Sets the ArcHarvestResult that this ARC file belongs to.
	 * @param harvestResult The ArcHarvestResult that this ARC file belongs to.
	 */
	public void setHarvestResult(ArcHarvestResult harvestResult) {
		this.harvestResult = harvestResult;
	}
	

	/**
	 * Setst he base directory of the ARC file.
	 * @param baseDir The base directory of the ARC file.
	 */
	public void setBaseDir(String baseDir) {
		this.baseDir = baseDir;
	}
	
	/**
	 * Indexes the ARC file assuming it is in it's base directory. The returned
	 * Map contains HarvestResource objects and is indexed by the URI of the 
	 * resource.
	 * 
	 * @return A Map of URI to HarvestResource. 
	 * @throws IOException if indexing fails.
	 */
	public Map<String, HarvestResource> index() throws IOException {
		if( this.baseDir == null ) {
			throw new IllegalStateException("Cannot index ArcHarvestFile without a base dir");
		}

        return index(new File(this.baseDir));
	}

	/**
	 * Indexes the ARC file from the passed in base directory. The returned
	 * Map contains HarvestResource objects and is indexed by the URI of the 
	 * resource.
	 * 
	 * @param baseDir the directory in which the ARC file resides.
	 * @return A Map of URI to HarvestResource. 
	 * @throws IOException if indexing fails.
	 */
	public Map<String, HarvestResource> index(File baseDir) throws IOException {
		Map<String, HarvestResource> results = new HashMap<String, HarvestResource>();
		
		ARCReader reader = ARCReaderFactory.get(new File(baseDir, this.getName()));
		this.compressed = reader.isCompressed();
		
		Iterator it = reader.iterator();
		for(int i=0; it.hasNext(); i++) {
			ARCRecord rec = (ARCRecord) it.next();
			
			try {
				ArcHarvestResource res = new ArcHarvestResource();
				res.setArcFileName(this.getName());
				res.setName(rec.getMetaData().getUrl());
				res.setResourceOffset(rec.getMetaData().getOffset());
				res.setStatusCode(rec.getStatusCode());
				res.setCompressed(this.isCompressed());
				
				// Calculate the length.
				long length = -1;
	
				// See if we can find the content-length header.
				Header[] headers = rec.getHttpHeaders();
				if(headers!=null) {
					for(int ix=0;ix<headers.length;ix++) {
						if("Content-Length".equalsIgnoreCase(headers[ix].getName())) {
							length = Long.parseLong(headers[ix].getValue());
						}
					}
				}
				
				// The content length header is not there. We will skip
				// the HTTP header and then use the avaialble() method
				// on the input stream to determine how many bytes are
				// available.
				if(length == -1) {
					rec.skipHttpHeader();
					length = rec.available();
				}
				
				res.setLength(length);
				
				results.put(res.getName(), res);
			}
			finally {
				rec.close();
			}
		}
		
		return results;
	}
}
