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
import java.io.FileOutputStream;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import javax.activation.DataHandler;
import javax.xml.rpc.ServiceException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.webcurator.core.exceptions.WCTRuntimeException;
import org.webcurator.core.util.WCTSoapCall;
import org.webcurator.domain.model.core.LogFilePropertiesDTO;


/**
 * Log Reader SOAP Client for communicating with a remote log reader.
 * @author nwaight
 */
public class LogReaderSOAPClient implements LogReader {
	/** the logger. */
    private static Log log = LogFactory.getLog(LogReaderSOAPClient.class);
    /** The name of the host to communicate with. */
    private String host;
    /** the port to communicate on. */
    private int port;
    /** the name of the service. */
    private String service;

    /**
     * Constructor to initialise the host, port and service.
     * @param aHost the name of the host
     * @param aPort the port number
     * @param aService the service name
     */
    public LogReaderSOAPClient(String aHost, int aPort, String aService) {
        host = aHost;
        port = aPort;
        service = aService;
    }
    
	/* (non-Javadoc)
	 * @see org.webcurator.core.reader.LogReader#listLogFiles(java.lang.String)
	 */
	public List<String> listLogFiles(String aJob) {
		try {
            WCTSoapCall call = new WCTSoapCall(host, port, service, "listLogFiles");  
            Object[] data = {aJob};                        

            List<String> files = new ArrayList<String>();
            Object[] objs = (Object[]) call.invoke(data);
            for (int i = 0; i < objs.length; i++) {
            	files.add((String) objs[i]);
			}
            
            return files;
        }
        catch (RemoteException e) {
            if (log.isErrorEnabled()) {
                log.error("Failed to invoke listLogFiles on the SOAP service : " + e.getMessage(), e);
            }
            throw new WCTRuntimeException("Failed to invoke listLogFiles on the SOAP service : " + e.getMessage(), e);
        }
        catch (ServiceException e) {
            if (log.isErrorEnabled()) {
                log.error("Failed to create the SOAP call listLogFiles : " + e.getMessage(), e);
            }
            throw new WCTRuntimeException("Failed to create the SOAP call listLogFiles : " + e.getMessage(), e);
        }
	}

	/* (non-Javadoc)
	 * @see org.webcurator.core.reader.LogReader#listLogFileAttributes(java.lang.String)
	 */
	public LogFilePropertiesDTO[] listLogFileAttributes(String aJob) {
		try {
            WCTSoapCall call = new WCTSoapCall(host, port, service, "listLogFileAttributes");  
			call.regTypes(LogFilePropertiesDTO.class);
            Object[] data = {aJob};                        

            LogFilePropertiesDTO[] fileProps = (LogFilePropertiesDTO[]) call.invoke(data);

            return fileProps;
        }
        catch (RemoteException e) {
            if (log.isErrorEnabled()) {
                log.error("Failed to invoke listLogFileAttributes on the SOAP service : " + e.getMessage(), e);
            }
            throw new WCTRuntimeException("Failed to invoke listLogFileAttributes on the SOAP service : " + e.getMessage(), e);
        }
        catch (ServiceException e) {
            if (log.isErrorEnabled()) {
                log.error("Failed to create the SOAP call listLogFileAttributes : " + e.getMessage(), e);
            }
            throw new WCTRuntimeException("Failed to create the SOAP call listLogFileAttributes : " + e.getMessage(), e);
        }
	}

	/* (non-Javadoc)
	 * @see org.webcurator.core.reader.LogReader#tail(java.lang.String, java.lang.String, int)
	 */
	public String[] tail(String aJob, String aFileName, int noOfLines) {
		try {
            WCTSoapCall call = new WCTSoapCall(host, port, service, "tail");  
            Object[] data = {aJob, aFileName, new Integer(noOfLines)};                        

            
            String[] logLines = (String[]) call.invoke(data);
            
            return logLines;
        }
        catch (RemoteException e) {
            if (log.isErrorEnabled()) {
                log.error("Failed to invoke tail on the SOAP service : " + e.getMessage(), e);
            }
            throw new WCTRuntimeException("Failed to invoke tail on the SOAP service : " + e.getMessage(), e);
        }
        catch (ServiceException e) {
            if (log.isErrorEnabled()) {
                log.error("Failed to create the SOAP call tail : " + e.getMessage(), e);
            }
            throw new WCTRuntimeException("Failed to create the SOAP call tail : " + e.getMessage(), e);
        }
	}

	/* (non-Javadoc)
	 * @see org.webcurator.core.reader.LogReader#countLines(java.lang.String, java.lang.String)
	 */
	public Integer countLines(String aJob, String aFileName) {
		try {
            WCTSoapCall call = new WCTSoapCall(host, port, service, "countLines");  
            Object[] data = {aJob, aFileName};                        

            
            Integer count = (Integer) call.invoke(data);
            
            return count;
        }
        catch (RemoteException e) {
            if (log.isErrorEnabled()) {
                log.error("Failed to invoke countLines on the SOAP service : " + e.getMessage(), e);
            }
            throw new WCTRuntimeException("Failed to invoke countLines on the SOAP service : " + e.getMessage(), e);
        }
        catch (ServiceException e) {
            if (log.isErrorEnabled()) {
                log.error("Failed to create the SOAP call countLines : " + e.getMessage(), e);
            }
            throw new WCTRuntimeException("Failed to create the SOAP call countLines : " + e.getMessage(), e);
        }
	}

	/* (non-Javadoc)
	 * @see org.webcurator.core.reader.LogReader#get(java.lang.String, java.lang.String, int, int)
	 */
	public String[] get(String aJob, String aFileName, int startLine, int noOfLines) {
		try {
            WCTSoapCall call = new WCTSoapCall(host, port, service, "get");  
            Object[] data = {aJob, aFileName, new Integer(startLine), new Integer(noOfLines)};                        

            
            String[] logLines = (String[]) call.invoke(data);
            
            return logLines;
        }
        catch (RemoteException e) {
            if (log.isErrorEnabled()) {
                log.error("Failed to invoke get on the SOAP service : " + e.getMessage(), e);
            }
            throw new WCTRuntimeException("Failed to invoke get on the SOAP service : " + e.getMessage(), e);
        }
        catch (ServiceException e) {
            if (log.isErrorEnabled()) {
                log.error("Failed to create the SOAP call get : " + e.getMessage(), e);
            }
            throw new WCTRuntimeException("Failed to create the SOAP call get : " + e.getMessage(), e);
        }
	}

	/* (non-Javadoc)
	 * @see org.webcurator.core.reader.LogReader#getHopPath(java.lang.String, java.lang.String, java.lang.String)
	 */
	public String[] getHopPath(String aJob, String aResultOid, String aFileName, String aUrl) {
		try {
            WCTSoapCall call = new WCTSoapCall(host, port, service, "getHopPath");  
            Object[] data = {aJob, aResultOid, aFileName, aUrl};                        

            
            String[] logLines = (String[]) call.invoke(data);
            
            return logLines;
        }
        catch (RemoteException e) {
            if (log.isErrorEnabled()) {
                log.error("Failed to invoke getHopPath on the SOAP service : " + e.getMessage(), e);
            }
            throw new WCTRuntimeException("Failed to invoke getHopPath on the SOAP service : " + e.getMessage(), e);
        }
        catch (ServiceException e) {
            if (log.isErrorEnabled()) {
                log.error("Failed to create the SOAP call getHopPath : " + e.getMessage(), e);
            }
            throw new WCTRuntimeException("Failed to create the SOAP call getHopPath : " + e.getMessage(), e);
        }
	}

	/* (non-Javadoc)
	 * @see LogReader#findFirstLineBeginning(String, String, String). 
	 */
	public Integer findFirstLineBeginning(String aJob, String aFileName, String match) {
		try {
            WCTSoapCall call = new WCTSoapCall(host, port, service, "findFirstLineBeginning");  
            Object[] data = {aJob, aFileName, match};                        
            
            Integer logLine = (Integer) call.invoke(data);
            
            return logLine;
        }
        catch (RemoteException e) {
            if (log.isErrorEnabled()) {
                log.error("Failed to invoke findFirstLineBeginning on the SOAP service : " + e.getMessage(), e);
            }
            throw new WCTRuntimeException("Failed to invoke findFirstLineBeginning on the SOAP service : " + e.getMessage(), e);
        }
        catch (ServiceException e) {
            if (log.isErrorEnabled()) {
                log.error("Failed to create the SOAP call findFirstLineBeginning : " + e.getMessage(), e);
            }
            throw new WCTRuntimeException("Failed to create the SOAP call findFirstLineBeginning : " + e.getMessage(), e);
        }
	}

	/* (non-Javadoc)
	 * @see LogReader#findFirstLineContaining(String, String, String). 
	 */
	public Integer findFirstLineContaining(String aJob, String aFileName, String match) {
		try {
            WCTSoapCall call = new WCTSoapCall(host, port, service, "findFirstLineContaining");  
            Object[] data = {aJob, aFileName, match};                        
            
            Integer logLine = (Integer) call.invoke(data);
            
            return logLine;
        }
        catch (RemoteException e) {
            if (log.isErrorEnabled()) {
                log.error("Failed to invoke findFirstLineContaining on the SOAP service : " + e.getMessage(), e);
            }
            throw new WCTRuntimeException("Failed to invoke findFirstLineContaining on the SOAP service : " + e.getMessage(), e);
        }
        catch (ServiceException e) {
            if (log.isErrorEnabled()) {
                log.error("Failed to create the SOAP call findFirstLineContaining : " + e.getMessage(), e);
            }
            throw new WCTRuntimeException("Failed to create the SOAP call findFirstLineContaining : " + e.getMessage(), e);
        }
	}

	/* (non-Javadoc)
	 * @see LogReader#findFirstLineAfterTimeStamp(String, String, Long). 
	 */
	public Integer findFirstLineAfterTimeStamp(String aJob, String aFileName, Long timestamp) {
		try {
            WCTSoapCall call = new WCTSoapCall(host, port, service, "findFirstLineAfterTimeStamp");  
            Object[] data = {aJob, aFileName, timestamp};                        
            
            Integer logLine = (Integer) call.invoke(data);
            
            return logLine;
        }
        catch (RemoteException e) {
            if (log.isErrorEnabled()) {
                log.error("Failed to invoke findFirstLineAfterTimeStamp on the SOAP service : " + e.getMessage(), e);
            }
            throw new WCTRuntimeException("Failed to invoke findFirstLineAfterTimeStamp on the SOAP service : " + e.getMessage(), e);
        }
        catch (ServiceException e) {
            if (log.isErrorEnabled()) {
                log.error("Failed to create the SOAP call findFirstLineAfterTimeStamp : " + e.getMessage(), e);
            }
            throw new WCTRuntimeException("Failed to create the SOAP call findFirstLineAfterTimeStamp : " + e.getMessage(), e);
        }
	}
	
	/* (non-Javadoc)
	 * @see org.webcurator.core.reader.LogReader#getByRegExpr(String, String, String, String, boolean, int, int)
	 */
	public String[] getByRegExpr(String aJob, String aFileName, String regExpr, String addLines, boolean prependLineNumbers, int skipFirstMatches, int numberOfMatches) {
		try {
            WCTSoapCall call = new WCTSoapCall(host, port, service, "getByRegExpr");  
            Object[] data = {aJob, aFileName, regExpr, addLines, new Boolean(prependLineNumbers), new Integer(skipFirstMatches), new Integer(numberOfMatches)};                        
            
            String[] logLines = (String[]) call.invoke(data);
            
            return logLines;
        }
        catch (RemoteException e) {
            if (log.isErrorEnabled()) {
                log.error("Failed to invoke getByRegExpr on the SOAP service : " + e.getMessage(), e);
            }
            throw new WCTRuntimeException("Failed to invoke getByRegExpr on the SOAP service : " + e.getMessage(), e);
        }
        catch (ServiceException e) {
            if (log.isErrorEnabled()) {
                log.error("Failed to create the SOAP call getByRegExpr : " + e.getMessage(), e);
            }
            throw new WCTRuntimeException("Failed to create the SOAP call getByRegExpr : " + e.getMessage(), e);
        }
	}

	public File retrieveLogfile(String aJob, String aFilename) {
		try {
			WCTSoapCall call = new WCTSoapCall(host, port, service, "retrieveLogfile");
			call.regTypes(DataHandler.class);
			DataHandler dh = (DataHandler) call.invoke(aJob, aFilename);
			
            File f = File.createTempFile("wct", "tmp");
	        dh.writeTo(new FileOutputStream(f));
	        return f;	        
		}
		catch(Exception ex) {
            throw new WCTRuntimeException("Failed to retrieve logfile " + aFilename + " for " + aJob + ": " + ex.getMessage(), ex);
		}
	}

	public File retrieveAQAFile(String aJob, String aFilename) {
		try {
			WCTSoapCall call = new WCTSoapCall(host, port, service, "retrieveAQAFile");
			call.regTypes(DataHandler.class);
			DataHandler dh = (DataHandler) call.invoke(aJob, aFilename);
			
            File f = File.createTempFile("wct", "tmp");
	        dh.writeTo(new FileOutputStream(f));
	        return f;	        
		}
		catch(Exception ex) {
            throw new WCTRuntimeException("Failed to retrieve aqa file " + aFilename + " for " + aJob + ": " + ex.getMessage(), ex);
		}
	}
}
