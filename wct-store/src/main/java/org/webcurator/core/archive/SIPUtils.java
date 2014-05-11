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

import static org.webcurator.core.archive.Constants.ARC_FILE;
import static org.webcurator.core.archive.Constants.LOG_FILE;
import static org.webcurator.core.archive.Constants.REPORT_FILE;
import static org.webcurator.core.archive.Constants.ROOT_FILE;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A utility object to fill in the file structure of the METS SIP xml document
 * @author AParker
 *
 */
public class SIPUtils {
	private static final String indents = "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t";
	private static SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");


	public static String finishSIP(String sip, String targetInstanceOID, List<ArchiveFile> files) {
		return finishSIP(sip,targetInstanceOID,files,false);
	}	
	
	/**
	 * Finish the METS xml document based upon these files
	 * @param sip The generated METS sip xml so far 
	 * @param targetInstanceOID the target instance oid
	 * @param files The list of files in this harvest
	 * @return
	 */
	public static String finishSIP(String sip, String targetInstanceOID, List<ArchiveFile> files, boolean flat) {
		List<ArchiveFile> logFiles = getFiles(files,LOG_FILE);
		List<ArchiveFile> reportFiles = getFiles(files,REPORT_FILE);
		List<ArchiveFile> arcFiles = getFiles(files,ARC_FILE);	
		List<ArchiveFile> homeFiles = getFiles(files,ROOT_FILE);//filesSubset(reportFiles,new String[]{"order.xml"});
		return sip+"\n"+getFileSecAndStructureMap(Integer.parseInt(targetInstanceOID),homeFiles,logFiles,reportFiles,arcFiles,flat);
	}

	private static String getFileSecAndStructureMap(int instanceOID, List<ArchiveFile> homeFiles, List<ArchiveFile> logs, List<ArchiveFile> reports, List<ArchiveFile> arcs,boolean flat){
		String fsres = "";
		String smres = "";
		String fileSecID = "AMD"+instanceOID;
		String dmdID = "DMD"+instanceOID;
		int groupID = 1;
		int nextFileNumber = 1;
		// Call getLogFileNames on the DigitalAssetStore to get the names of log and report files 
		fsres += getXMLTag("mets:fileSec",1);
		smres += getXMLTag("mets:structMap",1);
		fsres += getXMLTag("mets:fileGrp ADMID=\""+fileSecID+"\" ID=\""+getFileGrpStr(groupID++)+"\"",2);
		smres += getXMLTag("mets:div ADMID=\""+fileSecID+"\" DMDID=\""+dmdID+"\" LABEL=\"Home Directory\"",2);
		Map<Object,Object> res = createFileGroup(instanceOID,groupID++,nextFileNumber,"HOME DIRECTORY",fileSecID,homeFiles,null);
		fsres += res.get("FSRES");
		smres += res.get("SMRES");
		nextFileNumber = (Integer)res.get("NEXT_FILE_NUMBER");
		res = createFileGroup(instanceOID,groupID++,nextFileNumber,"ARCHIVE",fileSecID,arcs,flat?null:"/arcs");
		fsres += res.get("FSRES");
		smres += res.get("SMRES");
		nextFileNumber = (Integer)res.get("NEXT_FILE_NUMBER");
		res = createFileGroup(instanceOID,groupID++,nextFileNumber,"LOGS",fileSecID,logs,flat?null:"/logs");
		fsres += res.get("FSRES");
		smres += res.get("SMRES");
		nextFileNumber = (Integer)res.get("NEXT_FILE_NUMBER");
		res = createFileGroup(instanceOID,groupID++,nextFileNumber,"REPORTS",fileSecID,reports,flat?null:"/reports");
		fsres += res.get("FSRES");
		smres += res.get("SMRES");
		fsres += getXMLTag("mets:fileGrp",2,true);
		smres += getXMLTag("mets:div",2,true);
		fsres += getXMLTag("mets:fileSec",1,true);
		smres += getXMLTag("mets:structMap",1,true);
		smres += getXMLTag("mets:mets",0,true);		
		return fsres+smres;
	}


	private static Map<Object,Object> createFileGroup(int instanceOID, int groupId, int fileId, String dirName, String fileSecID, List<ArchiveFile> files, String dir) {
		Map res = new HashMap<Object,Object>();
		String fsres = "";
		String smres = "";
		fsres += getXMLTag("mets:fileGrp ID=\""+getFileGrpStr(groupId++)+"\" USE=\""+dirName+"\"",3);
		if(dir!=null){
			smres += getXMLTag("mets:div ADMID=\""+fileSecID+"\" DMDID=\"DMD"+instanceOID+"\" TYPE=\"directory\" LABEL=\""+dir+"\"",4);
		}
		for(ArchiveFile file : files){
			String fileStr = getFileNumStr(fileId++);
			fsres += getXMLTag("mets:file ID=\""+fileStr+"\" ADMID=\""+fileSecID
					+"\" MIMETYPE=\""+getMimeType(file.getFile().getName())+"\" SIZE=\""+file.getFile().length()
					+"\" CREATED=\""+dateFormatter.format(new Date(file.getFile().lastModified()))
					+"\" CHECKSUM=\""+file.getMd5()+"\" CHECKSUMTYPE=\"MD5\"",4);
			String dirStr = (dir==null)?"":dir;
			fsres += getXMLTag("mets:FLocat LOCTYPE=\"URL\" xlink:href=\"file://."+dirStr+"/"+file.getFile().getName()+"\"",5,false,true);
			fsres += getXMLTag("mets:file",4,true);
			smres += getXMLTag("mets:fptr FILEID=\""+fileStr+"\"",5,false,true);
		}
		fsres += getXMLTag("mets:fileGrp",3,true);
		if(dir!=null){
			smres += getXMLTag("mets:div",4,true);
		}
		res.put("NEXT_FILE_NUMBER",fileId);
		res.put("FSRES",fsres);
		res.put("SMRES",smres);
		return res;
	}

	private static String getMimeType(String file) {
		String ext = file.substring(file.lastIndexOf('.')+1);
		if(ext.equalsIgnoreCase("txt")){
			return "text/plain"; 
		}else if(ext.equalsIgnoreCase("log")){
			return "text/plain"; 
		}else if(ext.equalsIgnoreCase("xml")){
			return "text/xml"; 
		}else if(ext.equalsIgnoreCase("arc")){
			return "application/octet-stream"; 
		} else if(ext.equalsIgnoreCase(".gz")){
			return "application/x-gzip"; 
		}
		
		return "text/plain";
	}

	private static String getFileGrpStr(int groupID) {
		String full = "000"+groupID;
		return "FGRP"+full.substring(full.length()-3);
	}

	private static String getFileNumStr(int fileID) {
		String full = "000"+fileID;
		return "FILE"+full.substring(full.length()-3);
	}

	private static String getXMLTag(String tag, int indent){
		return getXMLTag(tag, indent, false);
	}

	private static String getXMLTag(String tag, int indent, boolean end){
		return getXMLTag(tag, indent, end, false);
	}

	private static String getXMLTag(String tag, int indent, boolean end, boolean complete){
		String res = indents.substring(0,indent);
		res += "<" + (end?"/":"") + tag;
		res += (complete?" /":"")+">\n";
		return res;
	}

	private static List<ArchiveFile> getFiles(List<ArchiveFile> set, int type){
		List<ArchiveFile> files = new ArrayList<ArchiveFile>();
		for(ArchiveFile f : set){
			if(f.getType()==type){
				files.add(f);
			}
		}
		return files;
	}
}
