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
package org.webcurator.core.archive.file;

import static org.webcurator.core.archive.Constants.ARC_FILE;
import static org.webcurator.core.archive.Constants.LOG_FILE;
import static org.webcurator.core.archive.Constants.REPORT_FILE;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.webcurator.core.archive.ArchiveFile;
import org.webcurator.core.archive.BaseArchive;
import org.webcurator.domain.model.core.CustomDepositFormCriteriaDTO;
import org.webcurator.domain.model.core.CustomDepositFormResultDTO;

/**
 * A specific file-based archiver.
 * Archives to a configurable filesystem  
 * @author AParker
 */
public class FileArchive extends BaseArchive {
	/**
	 * The base directory for archiving
	 */
	private String archiveRepository = null;
	/**
	 * The offset directory for archiving logs for a harvest
	 */
	private String archiveLogDirectory = null;
	/**
	 * The offset directory for archiving reports for a harvest
	 */
	private String archiveReportDirectory = null;
	/**
	 * The offset directory for archiving arc files for a harvest
	 */
	private String archiveArcDirectory = null;
	
	private static Log log = LogFactory.getLog(FileArchive.class);

	/**
	 * @param targetInstanceOID The target instance oid 
	 * @param SIP The METS xml structure for completion and archival
	 * @param xAttributes Any extra attributes that may be required for archival (not needed for this File Archiver)
	 * @param files A list of files (@see org.webcurator.core.archive.ArchiveFile) to archive 
	 * @return A unique archive identifier
	 * @throws FileArchiveException
	 */
	public String submitToArchive(String targetInstanceOID, String SIP, Map xAttributes, List<ArchiveFile> files) throws FileArchiveException{
		if(targetInstanceOID != null){
			File archiveRoot = null;
			try {
				archiveRoot = new File(archiveRepository,targetInstanceOID);
				for(ArchiveFile f : files){
					File fileArchiveDirectory = archiveRoot;
					switch(f.getType()){
					case LOG_FILE: fileArchiveDirectory = new File(archiveRoot,archiveLogDirectory);break;
					case REPORT_FILE: fileArchiveDirectory = new File(archiveRoot,archiveReportDirectory);break;
					case ARC_FILE: fileArchiveDirectory = new File(archiveRoot,archiveArcDirectory);break;
					}
					f.setMd5(FileUtil.moveFile(f.getFile(),fileArchiveDirectory,true));
				}
				// TODO finish SIP METS xml based on files
				String finalSIP = getFinalSIP(SIP, targetInstanceOID, files);
				String metsFileName = "METS-"+targetInstanceOID+".xml";
				archiveRoot = new File(archiveRepository,targetInstanceOID);
				File metsFile = new File(archiveRoot,metsFileName);
				BufferedWriter bw = new BufferedWriter(new FileWriter(metsFile));
				bw.write(finalSIP);
				bw.close();
			} catch (Exception e) {
				log.error("Archiving error",e);
				deleteCascade(archiveRoot);
				throw new FileArchiveException(e);
			}
		}
		return targetInstanceOID;
	}

	private void deleteCascade(File file) {
		if(file != null){
			if(file.exists()){
				if(file.isDirectory()){
					File files[] = file.listFiles();
					for(File f : files){
						deleteCascade(f);
					}
				}
				file.delete();
			}
		}
	}

	/**
	 * This implementation of Archive module does not require a custom form to be filled 
	 * before archiving any harvest. Therefore, this method will return null.
	 */
	public CustomDepositFormResultDTO getCustomDepositFormDetails(CustomDepositFormCriteriaDTO criteria) {
		return null;
	}

	public void setArchiveArcDirectory(String archiveArcDirectory) {
		this.archiveArcDirectory = archiveArcDirectory;
	}

	public void setArchiveLogDirectory(String archiveLogDirectory) {
		this.archiveLogDirectory = archiveLogDirectory;
	}

	public void setArchiveReportDirectory(String archiveReportDirectory) {
		this.archiveReportDirectory = archiveReportDirectory;
	}

	public void setArchiveRepository(String archiveRepository) {
		this.archiveRepository = archiveRepository;
	}

	/**
	 * @return the archiveRepository
	 */
	public String getArchiveRepository() {
		return archiveRepository;
	}

	/**
	 * @return the archiveArcDirectory
	 */
	public String getArchiveArcDirectory() {
		return archiveArcDirectory;
	}
}
