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
package org.webcurator.ui.target.command;

/**
 * Command class for retrieving log files from the server.
 * @author beaumontb
 *
 */
public class LogRetrieverCommand {
	private Long targetInstanceOid;
	private String logFileName;
	
	/**
	 * @return the logFileName
	 */
	public String getLogFileName() {
		return logFileName;
	}
	/**
	 * @param logFileName the logFileName to set
	 */
	public void setLogFileName(String logFileName) {
		this.logFileName = logFileName;
	}
	/**
	 * @return the targetInstanceOid
	 */
	public Long getTargetInstanceOid() {
		return targetInstanceOid;
	}
	/**
	 * @param targetInstanceOid the targetInstanceOid to set
	 */
	public void setTargetInstanceOid(Long targetInstanceOid) {
		this.targetInstanceOid = targetInstanceOid;
	}
}
