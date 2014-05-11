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
package org.webcurator.core.archive;

import java.util.ArrayList;
import java.util.List;
import static org.webcurator.core.archive.Constants.*;

/**
 * The parent class of all implementing specific Archive objects 
 * @author AParker
 *
 */
public abstract class BaseArchive implements Archive {

	private String archiveLogReportFiles = null;
	
	private List totalArchiveFileList = new ArrayList();

	/**
	 * Obtains the final METS SIP xml file structure as a string
	 * @param sip The METS xml structure before adding file data
	 * @param targetInstanceOID The target instance oid
	 * @param files A List of the file for archiving
	 * @return
	 */
	protected String getFinalSIP(String sip, String targetInstanceOID, List<ArchiveFile> files) {
		files = trimFiles(files);
		return SIPUtils.finishSIP(sip, targetInstanceOID, files);
	}
	
	private List<ArchiveFile> trimFiles(List<ArchiveFile> files) {
		List<ArchiveFile> newFiles = new ArrayList<ArchiveFile>();
		for(ArchiveFile f:files){
			if((f.getType()==LOG_FILE)||(f.getType()==REPORT_FILE)){
				if(totalArchiveFileList.contains(f.getFile().getName())){
					newFiles.add(f);
				}
			}else{
				newFiles.add(f);
			}
		}
		return newFiles;
	}

	public void setArchiveLogReportFiles(String archiveLogReportFiles) {
		this.archiveLogReportFiles = archiveLogReportFiles;
		String[] files = archiveLogReportFiles.split(",");
		totalArchiveFileList = new ArrayList();
		for(String f:files){
			totalArchiveFileList.add(f);
		}
	}


}
