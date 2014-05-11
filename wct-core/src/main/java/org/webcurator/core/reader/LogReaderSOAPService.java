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
package org.webcurator.core.reader;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import org.webcurator.domain.model.core.LogFilePropertiesDTO;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;

import org.springframework.remoting.jaxrpc.ServletEndpointSupport;

/**
 * The SOAP Service for reading log and report files and providing them
 * back to the client.
 * @author nwaight
 */
public class LogReaderSOAPService extends ServletEndpointSupport {
	/** The name of the Spring Log Reader Bean. */
	public static final String BEAN_LOG_READER = "logReader";

	/** The LogReader instance to delegate to. */
    private LogReader lr;
                
    protected void onInit() {
        lr = (LogReader) getWebApplicationContext().getBean(BEAN_LOG_READER);
    }
	
	/* (non-Javadoc)
	 * @see org.webcurator.core.reader.LogReader#listLogFiles(java.lang.String)
	 */
	public List listLogFiles(String aJob) {	
		return lr.listLogFiles(aJob);
	}    
    
	/* (non-Javadoc)
	 * @see org.webcurator.core.reader.LogReader#listLogFileAttributes(java.lang.String)
	 */
	public LogFilePropertiesDTO[] listLogFileAttributes(String aJob) {	
		return lr.listLogFileAttributes(aJob);
	}    

	/* (non-Javadoc)
	 * @see org.webcurator.core.reader.LogReader#countLines(java.lang.String, java.lang.String)
	 */
	public Integer countLines(String aJob, String aFileName) {		
		return lr.countLines(aJob, aFileName);
	}

	/* (non-Javadoc)
	 * @see org.webcurator.core.reader.LogReader#tail(java.lang.String, java.lang.String, int)
	 */
	public String[] tail(String aJob, String aFileName, int noOfLines) {		
		return lr.tail(aJob, aFileName, noOfLines);
	}

	/* (non-Javadoc)
	 * @see org.webcurator.core.reader.LogReader#get(java.lang.String, java.lang.String, int, int)
	 */
	public String[] get(String aJob, String aFileName, int startLine, int noOfLines) {		
		return lr.get(aJob, aFileName, startLine, noOfLines);
	}

	/* (non-Javadoc)
	 * @see org.webcurator.core.reader.LogReader#getHopPath(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public String[] getHopPath(String aJob, String aResultoid, String aFileName, String aUrl) {		
		return lr.getHopPath(aJob, aResultoid, aFileName, aUrl);
	}

	/* (non-Javadoc)
	 * @see LogReader#findFirstLineBeginning(String, String, String). 
	 */
	public Integer findFirstLineBeginning(String aJob, String aFileName, String match) {
		return lr.findFirstLineBeginning(aJob, aFileName, match);
	}

	/* (non-Javadoc)
	 * @see LogReader#findFirstLineContaining(String, String, String). 
	 */
	public Integer findFirstLineContaining(String aJob, String aFileName, String match) {
		return lr.findFirstLineContaining(aJob, aFileName, match);
	}
	
	/* (non-Javadoc)
	 * @see LogReader#findFirstLineAfterTimeStamp(String, String, Long). 
	 */
	public Integer findFirstLineAfterTimeStamp(String aJob, String aFileName, Long timestamp) {
		return lr.findFirstLineAfterTimeStamp(aJob, aFileName, timestamp);
	}

	/* (non-Javadoc)
	 * @see org.webcurator.core.reader.LogReader#getByRegExpr(String, String, String, String, boolean, int, int). 
	 */
	public String[] getByRegExpr(String aJob, String aFileName, String regExpr, String addLines, boolean prependLineNumbers, int skipFirstMatches, int numberOfMatches) {		
		return lr.getByRegExpr(aJob, aFileName, regExpr, addLines, prependLineNumbers, skipFirstMatches, numberOfMatches);
	}

	public DataHandler retrieveLogfile(String aJob, String aFilename) {
		try {
			return new DataHandler(new FileDataSource(lr.retrieveLogfile(aJob, aFilename)));	
		}
		catch(RuntimeException rex) {
			System.out.println("Error");
			throw rex;
		}			
		
	}
}
