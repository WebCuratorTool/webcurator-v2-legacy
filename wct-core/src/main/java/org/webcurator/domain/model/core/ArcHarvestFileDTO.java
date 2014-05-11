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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.httpclient.HttpParser;
import org.apache.commons.httpclient.StatusLine;
import org.apache.commons.httpclient.util.EncodingUtil;

import org.archive.io.ArchiveReader;
import org.archive.io.ArchiveReaderFactory;
import org.archive.io.ArchiveRecord;
import org.archive.io.ArchiveRecordHeader;
import org.archive.io.arc.ARCRecord;
import org.archive.io.warc.WARCRecord;
import org.archive.io.warc.WARCConstants;
import org.archive.io.RecoverableIOException;

/**
 * A data transfer object for passing Arc Harvest file data between the 
 * components of the Web Curator Tool.
 * @author bbeaumont
 */
public class ArcHarvestFileDTO {
	/** The id of the Arc Harvest File. */
	private Long oid;
	/** the harvest file name. */
	private String name;
	/** flag to indicate if the ARC file is compressed. */
	private boolean compressed;
	/** The base directory of the ArcHarvestFile. */
	private String baseDir;
	/** The harvest result. */
	private ArcHarvestResultDTO harvestResult;
	/** The maximum URL length to capture */
	public static final int MAX_URL_LENGTH = 1020;

	/**
	 * @return true if the ARC file is compressed.
	 */
	public boolean isCompressed() {
		return compressed;
	}

	/** 
	 * @param compressed the file compressed flag.
	 */
	public void setCompressed(boolean compressed) {
		this.compressed = compressed;
	}

	/**
	 * @return the id of the ARCHarvestFile.
	 */		
	public Long getOid() {
		return oid;
	}

	/**
	 * @param oid the id of the ARCHarvestFile.
	 */
	public void setOid(Long oid) {
		this.oid = oid;
	}

	/**
	 * @return the name of of the ARCHarvestFile.
	 */		
	public String getName() {
		return name;
	}

	/** 
	 * @param name the name of of the ARCHarvestFile.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the harvest result data.
	 */	
	public ArcHarvestResultDTO getHarvestResult() {
		return harvestResult;
	}

	/** 
	 * @param harvestResult the harvest result data.
	 */
	public void setHarvestResult(ArcHarvestResultDTO harvestResult) {
		this.harvestResult = harvestResult;
	}
	
	/**
	 * @param baseDir the base directory for the Arc Harvest File.
	 */
	public void setBaseDir(String baseDir) {
		this.baseDir = baseDir;
	}
	
	/** 
	 * Create and return the index of the ArcHarvestFile described to by this DTO.
	 * @return the index Map
	 * @throws IOException thrown if there is an error
	 * @throws ParseException 
	 */
	public Map<String, HarvestResourceDTO> index() throws IOException, ParseException {
		if( this.baseDir == null ) {
			throw new IllegalStateException("Cannot index ArcHarvestFile without a base dir");
		}

        return index(new File(this.baseDir));
	}

	public boolean checkIsCompressed() throws IOException {
		ArchiveReader reader = ArchiveReaderFactory.get(new File(baseDir, this.getName()));
		boolean result = reader.isCompressed();
		reader.close();
		return result;
	}
	
	/**
	 * Create and return the index of the ArcHarvestFile.
	 * @param baseDir the base directory of the arcs
	 * @throws IOException thrown if there is an error
	 * @throws ParseException 
	 */
	public Map<String, HarvestResourceDTO> index(File baseDir) throws IOException, ParseException {
		Map<String, HarvestResourceDTO> results = new HashMap<String, HarvestResourceDTO>();
		
		File theArchiveFile = new File(baseDir, this.getName());
		ArchiveReader reader = ArchiveReaderFactory.get(theArchiveFile);
		this.compressed = reader.isCompressed();
		
		Iterator<ArchiveRecord> it = reader.iterator();
		while(it.hasNext()) {
			ArchiveRecord rec = it.next();
			
			if(rec instanceof WARCRecord) {
				String type = rec.getHeader().getHeaderValue(WARCConstants.HEADER_KEY_TYPE).toString();
				if(type.equals(WARCConstants.RESPONSE)) {
					String mime = rec.getHeader().getMimetype();
					if(!mime.equals("text/dns")) {
						indexWARCResponse(rec, results);
					}
				}
			}
			else {
				indexARCRecord(rec, results);
			}
		}
		reader.close();
		
		return results;
	}
	
	private void indexARCRecord(ArchiveRecord rec, Map<String, HarvestResourceDTO> results) throws IOException {
		
		ARCRecord record = (ARCRecord) rec;
		ArchiveRecordHeader header = record.getHeader();
		
		// If the URL length is too long for the database, skip adding the URL
		// to the index. This ensures that the harvest completes successfully. 
		if(header.getUrl().length() > MAX_URL_LENGTH) { 
			return;
		}
			
		try {
			ArcHarvestResourceDTO res = new ArcHarvestResourceDTO();
			res.setArcFileName(this.getName());
			res.setName(header.getUrl());
			res.setResourceOffset(header.getOffset());
			res.setCompressed(this.isCompressed());
			res.setStatusCode(record.getStatusCode());

			// Calculate the length.
			long length = header.getLength() - header.getContentBegin();
			res.setLength(length);
				
			results.put(res.getName(), res);
		}
		finally {
			rec.close();
		}
	}

	private void indexWARCResponse(ArchiveRecord rec, Map<String, HarvestResourceDTO> results) throws IOException {
		
		WARCRecord record = (WARCRecord) rec;
		ArchiveRecordHeader header = record.getHeader();

		// If the URL length is too long for the database, skip adding the URL
		// to the index. This ensures that the harvest completes successfully. 
		if(header.getUrl().length() > MAX_URL_LENGTH) { 
			return;
		}
			
		try {
			ArcHarvestResourceDTO res = new ArcHarvestResourceDTO();
			res.setArcFileName(this.getName());
			res.setName(header.getUrl());
			res.setResourceOffset(header.getOffset());
			res.setCompressed(this.isCompressed());

			// need to parse the documents HTTP message and headers here: WARCReader
			// does not implement this...
			
	        byte [] statusBytes = HttpParser.readRawLine(record);
	        int eolCharCount = getEolCharsCount(statusBytes);
	        if (eolCharCount <= 0) {
	            throw new RecoverableIOException("Failed to read http status where one " +
	                " was expected: " + new String(statusBytes));
	        }
	        String statusLine = EncodingUtil.getString(statusBytes, 0,
	            statusBytes.length - eolCharCount, WARCConstants.DEFAULT_ENCODING);
	        if ((statusLine == null) ||
	                !StatusLine.startsWithHTTP(statusLine)) {
	           throw new RecoverableIOException("Failed parse of http status line.");
	        }
	        StatusLine status = new StatusLine(statusLine);
			
			res.setStatusCode(status.getStatusCode());
			
			// Calculate the length.
			long length = header.getLength() - header.getContentBegin();
			res.setLength(length);
				
			results.put(res.getName(), res);
		}
		finally {
			rec.close();
		}
	}
	
    /**
     * borrowed(copied) from org.archive.io.arc.ARCRecord...
     * 
     * @param bytes Array of bytes to examine for an EOL.
     * @return Count of end-of-line characters or zero if none.
     */
    private int getEolCharsCount(byte [] bytes) {
        int count = 0;
        if (bytes != null && bytes.length >=1 &&
                bytes[bytes.length - 1] == '\n') {
            count++;
            if (bytes.length >=2 && bytes[bytes.length -2] == '\r') {
                count++;
            }
        }
        return count;
    }
	
}
