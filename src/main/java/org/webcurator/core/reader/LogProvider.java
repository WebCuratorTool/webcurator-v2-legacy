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
import java.util.List;
import org.webcurator.domain.model.core.LogFilePropertiesDTO;;

/**
 * The LogProvider is implemented by an object that provides access to 
 * log and report files.
 * @author nwaight
 */
public interface LogProvider {

	/** 
	 * Return the File object for the specified job and file name.
	 * @param aJob the job to return the log file for
	 * @param aFileName the name of the file
	 * @return the File referred to by the specified job and file name
	 */
	File getLogFile(String aJob, String aFileName);
	
	/**
	 * Return a list of log file names that are available for the specified Job.
	 * @param aJob the job to return the list of file names for
	 * @return the list of available file names
	 */
	List<String> getLogFileNames(String aJob);

	/**
	 * Return an array of 'log file objects' that are available for the specified Job.
	 * @param aJob the job to return the list of file names for
	 * @return the list of available files
	 */
	LogFilePropertiesDTO[] getLogFileAttributes(String aJob);
}
