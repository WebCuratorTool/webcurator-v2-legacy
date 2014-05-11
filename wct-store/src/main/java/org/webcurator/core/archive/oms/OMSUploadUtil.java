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
package org.webcurator.core.archive.oms;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.apache.axis.encoding.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A utility adapter which communicates directly with the 
 * National Library of New Zealand's OMS repository for
 * archiving harvest results. 
 * @author AParker
 *
 */
public class OMSUploadUtil {
	
	private static Log log = LogFactory.getLog(OMSUploadUtil.class);
	/* The size of the slices to send when communicating with the OMS repository. */
	private int partSize = 100000;
	/* The url of the OMS upload servlet. */
    private String url;
    private String ilsTapuhiFlag = "RT_ILS";
    private String collectionType = "CT_EPB";
    private String objectType = "OT_WWW";
    private String agencyResponsible = "AR_NLNZ";
    private String instanceRole = "IRC_PM";
    private String instanceCaptureSystem = "CS_WCT";
    private String instanceType = "IT_COM";
    private int user_group = 4;
    private String referenceNumber = "";
    private String accessRestriction = null;
    private Date restrictionDate = null;
    private String restrictionNarrative = "";
    private String alternativeReferenceNumber = "";
    private String sourceType = "";
    private String digitisationReason = "";
    private boolean isAccessAvailable;
    private String personResponsible = "";
    private boolean maintenanceFlag;
    private String inMaintenanceNotes = "";
    private String inNotes = "";
    private String inDependencies = "";
    private String inEntryPointURL = "";
    private boolean unixNonCompliantFlag = false;
    private String inFileDirectory = "";
    private ArrayList parts;
    private String user = "";
    private String password;
    private int usersGroup = 0;

    private SimpleDateFormat dateFormatter = new SimpleDateFormat("ddMMyyyy");
    /* The sessionid to use when uploading files to the OMS */
	private String sessionId;

	/**
	 * Contruct a new OMSUploadUtil object with a particular sessionid seed.
	 * @param sessionId
	 */
	public OMSUploadUtil(String sessionId) {
		super();
		this.sessionId = "s"+sessionId;
	}
	
	/**
	 * Add content (a file really) to the OMS repository based upon the current sessionid
	 * @param content The stream to read from
	 * @param name The name to call the file in the OMS
	 * @param actualSize The size of the stream
	 * @return The MD5 of the file
	 * @throws OMSUploadException
	 */
	public String uploadContent(InputStream content, String name, long actualSize) throws OMSUploadException{
		String msg = "";
		String md5 = "";
		try{
		    int pos = 0;
		    int part = 0;
		    int parts = 0;
		    long totalSize = 0;

			URL url = null;
		    HttpURLConnection urlConn = null;
		    boolean finished = false;
   			MessageDigest md = MessageDigest.getInstance("MD5");

		    for(;!finished;pos+=partSize,part++){
		    	long contentLength = Math.min(partSize,actualSize-(partSize*part));
		    	// URL of servlet.
				url = new URL(this.url);
				// URL connection channel.
				urlConn = getConnection(url, 5);
				// Let the run-time system (RTS) know that we want input.
				urlConn.setDoInput(true);
				// Let the RTS know that we want to do output.
				urlConn.setDoOutput(true);
				// No caching, we want the real thing.
				urlConn.setUseCaches(false);
				// Specify the content type.
				urlConn.setRequestProperty("Content-Type",
						"application/octet-stream");
				 urlConn.setRequestProperty("Content-Length", ""+contentLength);
				urlConn.setRequestProperty("filename", toSafeFileName(name));
				urlConn.setRequestProperty("realfilename", name);
				urlConn.setRequestProperty("sessionid", sessionId);
				urlConn.setRequestProperty("applet", "true");
				urlConn.setRequestMethod("POST");
				// Send POST output.
			    urlConn.setRequestProperty("part.number",part+"");
			    urlConn.setRequestProperty("part.size",contentLength+"");
				OutputStream out = null;
				try {
					for (int u = 0; (out == null) && (u < 5); u++) {
						try {
							out = urlConn.getOutputStream();
						} catch (ConnectException e) {
							log.error("Connection Problem",e);
						}
					}
					parts++;
					int sent = 0;
					byte[] buff = new byte[16384];
					int bytes = content.read(buff);
					while (bytes > 0) {
						out.write(buff, 0, bytes);
						md.update(buff, 0, bytes);
						totalSize += bytes;
						sent += bytes;
						int toRead = (int)contentLength - sent;
						if(toRead>0){
							bytes = content.read(buff,0,Math.min(buff.length,toRead));
						}else{
							bytes = 0;
							if(totalSize >= actualSize ){
								finished = true;
							}
						}
					}
				} catch (IOException e) {
					throw new OMSUploadException(e);
				} finally {
					out.flush();
					out.close();
				}
				// Get response data.
			    BufferedReader input = new BufferedReader(new InputStreamReader(urlConn.getInputStream ()));
			    msg = input.readLine();
			    input.close ();
				urlConn.disconnect();
				if(!msg.equals("SUCCESS")){
					throw new OMSUploadException("Upload failed - return code: "+msg);
				}
				else {
					log.debug("OMS returned SUCCESS");
				}
		    }
			content.close();
   			md5 = toHexString(md.digest());
		    urlConn = getConnection(url,5);
		    // Let the run-time system (RTS) know that we want input.
		    urlConn.setDoInput (true);
		    urlConn.setRequestProperty("Content-Type", "application/octet-stream");
		    urlConn.setRequestProperty("Content-Length", "0");
		    urlConn.setRequestProperty("MD5-hash",md5);
		    urlConn.setRequestProperty("filename",toSafeFileName(name));
		    urlConn.setRequestProperty("realfilename",name);
		    urlConn.setRequestProperty("sessionid",sessionId);
		    urlConn.setRequestProperty("applet","true");
		    urlConn.setRequestMethod("POST");
		    urlConn.setRequestProperty("part.number",parts+"");
		    urlConn.setRequestProperty("part.size",totalSize+"");
		    urlConn.setRequestProperty("total.size",totalSize+"");		    
		    // Get response data.
		    BufferedReader input2 = new BufferedReader(new InputStreamReader (urlConn.getInputStream ()));
		    msg = input2.readLine();
		    input2.close ();
		    urlConn.disconnect();
		}catch(Exception e){
			msg = "Error: "+e.getMessage();
			throw new OMSUploadException(msg,e);
		}
		
		return md5;
	}
	
	/**
	 * Add another instance to an existing OMS object based upon files uploaded in the current 
	 * session (sessionid) and attributes already set on this object. 
	 * @param objectIID
	 * @return The string SUCCESS on successful completion (returned from the OMS)
	 * @throws OMSUploadException
	 */
	private String addInstance(String objectIID) throws OMSUploadException {
		String msg = "";
		try{
			URL url = null;
		    HttpURLConnection urlConn = null;
	    	// URL of servlet.
			url = new URL(this.url);
			// URL connection channel.
		    urlConn = getConnection(url,5);
		    // Let the run-time system (RTS) know that we want input.
		    urlConn.setDoInput (true);
		    urlConn.setRequestProperty("Content-Type", "application/octet-stream");
		    urlConn.setRequestProperty("Content-Length", "0");
		    urlConn.setRequestProperty("sessionId",sessionId);
		    urlConn.setRequestMethod("POST");
		    urlConn.setRequestProperty("add-instance","true");
		    urlConn.setRequestProperty("objectIID",objectIID);

		    urlConn.setRequestProperty("referenceNumber",referenceNumber);
		    urlConn.setRequestProperty("ilsTapuhiFlag",ilsTapuhiFlag);
		    urlConn.setRequestProperty("collectionType",collectionType);
		    urlConn.setRequestProperty("objectType",objectType);
		    urlConn.setRequestProperty("accessRestriction",accessRestriction);
		    if(restrictionDate != null){
		    	urlConn.setRequestProperty("restrictionDate",dateFormatter.format(restrictionDate));
		    }
		    urlConn.setRequestProperty("restrictionNarrative",restrictionNarrative);
		    urlConn.setRequestProperty("agencyResponsible",agencyResponsible);
		    urlConn.setRequestProperty("alternativeReferenceNumber",alternativeReferenceNumber);
		    urlConn.setRequestProperty("sourceType",sourceType);
		    urlConn.setRequestProperty("digitisationReason",digitisationReason);
		    urlConn.setRequestProperty("instanceRole",instanceRole);
		    urlConn.setRequestProperty("isAccessAvailable",Boolean.toString(isAccessAvailable));
		    urlConn.setRequestProperty("instanceCaptureSystem",instanceCaptureSystem);
		    urlConn.setRequestProperty("personResponsible",personResponsible);
		    urlConn.setRequestProperty("maintenanceFlag",Boolean.toString(maintenanceFlag));
		    urlConn.setRequestProperty("inMaintenanceNotes",inMaintenanceNotes);
		    urlConn.setRequestProperty("inNotes",inNotes);
		    urlConn.setRequestProperty("inDependencies",inDependencies);
		    urlConn.setRequestProperty("inEntryPointURL",inEntryPointURL);
		    urlConn.setRequestProperty("unixNonCompliantFlag",Boolean.toString(unixNonCompliantFlag));
		    urlConn.setRequestProperty("instanceType",instanceType);
		    urlConn.setRequestProperty("inFileDirectory",inFileDirectory);
		    urlConn.setRequestProperty("user",user);
		    urlConn.setRequestProperty("usersGroup",Integer.toString(usersGroup));
		    urlConn.setRequestProperty("user_group",Integer.toString(user_group));
			
		    // Get response data.
		    BufferedReader input = new BufferedReader(new InputStreamReader(urlConn.getInputStream ()));
		    msg = input.readLine();
		    input.close ();
		    urlConn.disconnect();
		}catch(Exception e){
			msg = "Error: "+e.getMessage();
			throw new OMSUploadException(e);
		}
		return msg;
	}

	/**
	 * Add an OMS object based upon files uploaded in the current session (sessionid) and 
	 * attributes already set on this object.
	 * @return The archive identifier (IID) on successful completion (returned from the OMS)
	 * @throws OMSUploadException
	 */
	public String uploadPub() throws OMSUploadException{
		String msg = "";
		try{
			URL url = null;
		    HttpURLConnection urlConn = null;
	    	// URL of servlet.
			url = new URL(this.url);
			// URL connection channel.
		    urlConn = getConnection(url,5);
		    // Let the run-time system (RTS) know that we want input.
		    urlConn.setDoInput (true);
		    urlConn.setRequestProperty("Content-Type", "application/octet-stream");
		    urlConn.setRequestProperty("Content-Length", "0");
		    urlConn.setRequestProperty("sessionid",sessionId);
		    urlConn.setRequestMethod("POST");
		    urlConn.setRequestProperty("upload-object","true");

		    urlConn.setRequestProperty("referenceNumber",referenceNumber);
		    urlConn.setRequestProperty("ilsTapuhiFlag",ilsTapuhiFlag);
		    urlConn.setRequestProperty("collectionType",collectionType);
		    urlConn.setRequestProperty("objectType",objectType);
		    urlConn.setRequestProperty("accessRestriction",accessRestriction);
		    if(restrictionDate != null){
		    	urlConn.setRequestProperty("restrictionDate",dateFormatter.format(restrictionDate));
		    }
		    urlConn.setRequestProperty("restrictionNarrative",restrictionNarrative);
		    urlConn.setRequestProperty("agencyResponsible",agencyResponsible);
		    urlConn.setRequestProperty("alternativeReferenceNumber",alternativeReferenceNumber);
		    urlConn.setRequestProperty("sourceType",sourceType);
		    urlConn.setRequestProperty("digitisationReason",digitisationReason);
		    urlConn.setRequestProperty("instanceRole",instanceRole);
		    urlConn.setRequestProperty("isAccessAvailable",Boolean.toString(isAccessAvailable));
		    urlConn.setRequestProperty("instanceCaptureSystem",instanceCaptureSystem);
		    urlConn.setRequestProperty("personResponsible",personResponsible);
		    urlConn.setRequestProperty("maintenanceFlag",Boolean.toString(maintenanceFlag));
		    urlConn.setRequestProperty("inMaintenanceNotes",inMaintenanceNotes);
		    urlConn.setRequestProperty("inNotes",inNotes);
		    urlConn.setRequestProperty("inDependencies",inDependencies);
		    urlConn.setRequestProperty("inEntryPointURL",inEntryPointURL);
		    urlConn.setRequestProperty("unixNonCompliantFlag",Boolean.toString(unixNonCompliantFlag));
		    urlConn.setRequestProperty("instanceType",instanceType);
		    urlConn.setRequestProperty("inFileDirectory",inFileDirectory);
		    urlConn.setRequestProperty("user",user);
		    urlConn.setRequestProperty("usersGroup",Integer.toString(usersGroup));
		    urlConn.setRequestProperty("user_group",Integer.toString(user_group));
			
		    // Get response data.
		    BufferedReader input = new BufferedReader(new InputStreamReader(urlConn.getInputStream ()));
		    msg = urlConn.getHeaderField("oms-iid");
		    input.close ();
		    urlConn.disconnect();
		    if(msg==null){
		    	throw new OMSUploadException("Upload failed - expected an oms-iid header in the reutrn from OMS");
		    }
		}catch(Exception e){
			msg = "Error: "+e.getMessage();
			throw new OMSUploadException(e);
		}
		return msg;
	}

	private HttpURLConnection getConnection(URL url, int retries) throws IOException{
		HttpURLConnection res = null;
		for(int i=0;i<retries;i++){
			try{
				res = (HttpURLConnection)url.openConnection();
				
			    String val = user + ":" + password;
			    String encoding = Base64.encode(val.getBytes());
			    res.setRequestProperty ("Authorization", "Basic " + encoding);				
				
				break;
			}catch(IOException e){
				log.warn("Connect failed to: "+url+" - retrying...",e);
				if(i==retries-1){
					throw e;
				}
			}
		}
		return res;
	}

	private static String toSafeFileName(String filename){
		StringBuffer res = new StringBuffer();
		if(filename != null){
			for(int i=0;i<filename.length();i++){
				char c = filename.charAt(i);
				if((Character.isLetterOrDigit(c))||(c == '.')||(c == '-')||(c == '_')){
					res.append(c);
				}
			}
		}
		return res.toString();
	}

	private String toHexString(byte[] buf){
		String res = "";
		for(int i=0;i<buf.length;i++){
			String s = "0"+Integer.toHexString(buf[i]);
			res += s.substring(s.length()-2);
		}
		return res;
	}
	
	public void setAccessRestriction(String accessRestriction) {
		this.accessRestriction = accessRestriction;
	}

	public void setAgencyResponsible(String agencyResponsible) {
		this.agencyResponsible = agencyResponsible;
	}

	public void setAlternativeReferenceNumber(String alternativeReferenceNumber) {
		this.alternativeReferenceNumber = alternativeReferenceNumber;
	}

	public void setCollectionType(String collectionType) {
		this.collectionType = collectionType;
	}

	public void setDigitisationReason(String digitisationReason) {
		this.digitisationReason = digitisationReason;
	}

	public void setIlsTapuhiFlag(String ilsTapuhiFlag) {
		this.ilsTapuhiFlag = ilsTapuhiFlag;
	}

	public void setInDependencies(String inDependencies) {
		this.inDependencies = inDependencies;
	}

	public void setInEntryPointURL(String inEntryPointURL) {
		this.inEntryPointURL = inEntryPointURL;
	}

	public void setInMaintenanceNotes(String inMaintenanceNotes) {
		this.inMaintenanceNotes = inMaintenanceNotes;
	}

	public void setInNotes(String inNotes) {
		this.inNotes = inNotes;
	}

	public void setInstanceCaptureSystem(String instanceCaptureSystem) {
		this.instanceCaptureSystem = instanceCaptureSystem;
	}

	public void setInstanceRole(String instanceRole) {
		this.instanceRole = instanceRole;
	}

	public void setInstanceType(String instanceType) {
		this.instanceType = instanceType;
	}

	public void setAccessAvailable(boolean isAccessAvailable) {
		this.isAccessAvailable = isAccessAvailable;
	}

	public void setMaintenanceFlag(boolean maintenanceFlag) {
		this.maintenanceFlag = maintenanceFlag;
	}

	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}

	public void setPersonResponsible(String personResponsible) {
		this.personResponsible = personResponsible;
	}

	public void setReferenceNumber(String referenceNumber) {
		this.referenceNumber = referenceNumber;
	}

	public void setRestrictionDate(Date restrictionDate) {
		this.restrictionDate = restrictionDate;
	}

	public void setRestrictionNarrative(String restrictionNarrative) {
		this.restrictionNarrative = restrictionNarrative;
	}

	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
	}

	public void setUnixNonCompliantFlag(boolean unixNonCompliantFlag) {
		this.unixNonCompliantFlag = unixNonCompliantFlag;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public void setUser_group(int user_group) {
		this.user_group = user_group;
	}

	public void setUsersGroup(int usersGroup) {
		this.usersGroup = usersGroup;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setPartSize(int partSize) {
		this.partSize = partSize;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	
}
