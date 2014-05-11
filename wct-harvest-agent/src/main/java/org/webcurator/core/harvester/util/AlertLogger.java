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
package org.webcurator.core.harvester.util;

import java.io.File;
import java.io.FileWriter;
import java.util.Enumeration;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.archive.io.SinkHandlerLogRecord;

/**
 * The AlertLogger is responsible for writing a harvesters alerts to a log file
 * in the directory with the other Heritrix logs.
 * 
 * Note: There is a problem with this as Heritrix does not currently identify the 
 * Job that generated the alert and there is single alert logger for all the Heritrix
 * instances.
 *  
 * @author nwaight
 */
public class AlertLogger {
	/** used to write the alert log file. */
	private FileWriter fw = null;
	/** the loger. */
	public static final Log log = LogFactory.getLog(AlertLogger.class);

	/** 
	 * Constructor taking the jobs log directory and the alert file name.
	 * @param aParent the harvest job's log directory
	 * @param aFileName the alert log name
	 */
	public AlertLogger(File aParent, String aFileName) {
		try {
			File file = new File(aParent, aFileName);
			if (!file.exists()) {
				file.createNewFile();
			}
			
			fw = new FileWriter(file);
		} 
		catch (Exception e) {
			if (log.isErrorEnabled()) {
				log.error("Failed to create the AlertLogger " + e.getMessage(), e);
			}			
			fw = null;
		}
	}
	
	/**
	 * Write the alerts provided in the vector to the log file.
	 * @param aAlerts the list of alerts to log
	 */
	public void writeAlerts(Vector aAlerts) {
		if (null == fw) {
			if (log.isErrorEnabled()) {
				log.error("Attempting to use an invalid AlertLogger");
			}
			return;
		}
				
		try {						
			SinkHandlerLogRecord alert;
			Enumeration e = aAlerts.elements();
			while (e.hasMoreElements()) {
				alert = (SinkHandlerLogRecord) e.nextElement();
				fw.write(alert.getLevel() + " " + alert.getCreationTime() + " " + alert.getMessage() + " " + alert.getSequenceNumber() + "\n");
			}
			
			fw.flush();			
		} 
		catch (Exception e) {
			if (log.isWarnEnabled()) {
				log.warn("Failed to log alerts. " + e.getMessage(), e);
			}
		}		
	}
	
	/**
	 * Close the log file.
	 */
	public void close() {
		try {
			if (fw != null) {
				fw.close();
			}
			fw = null;
		} 
		catch (Exception e) {
			if (log.isWarnEnabled()) {
				log.warn("Failed to close the alerts log file. " + e.getMessage(), e);
			}
		}
	}
	
	/**
	 * Attempt to close the log file when the object is garbage collected.
	 */
	protected void finalize() {
		close();
	}
}
