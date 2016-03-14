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

import static org.webcurator.core.archive.Constants.ACCESS_AVAILABLE;
import static org.webcurator.core.archive.Constants.ACCESS_RESTRICTION;
import static org.webcurator.core.archive.Constants.DEPENDENCIES;
import static org.webcurator.core.archive.Constants.ENTRY_POINT_URL;
import static org.webcurator.core.archive.Constants.MAINTENANCE_FLAG;
import static org.webcurator.core.archive.Constants.MAINTENANCE_NOTES;
import static org.webcurator.core.archive.Constants.PERSON_RESPONSIBLE;
import static org.webcurator.core.archive.Constants.REFERENCE_NUMBER;
import static org.webcurator.core.archive.Constants.RESTRICTION_DATE;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.StringBufferInputStream;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.webcurator.core.archive.ArchiveFile;
import org.webcurator.core.archive.BaseArchive;
import org.webcurator.core.archive.SIPUtils;
import org.webcurator.domain.model.core.CustomDepositFormCriteriaDTO;
import org.webcurator.domain.model.core.CustomDepositFormResultDTO;

/**
 * A specific OMS-based archiver for the National Library of New Zealand's OMS archival system.
 * @author AParker
 */

public class OMSArchive extends BaseArchive {
	private static Log log = LogFactory.getLog(OMSArchive.class);

	private int partSize = 100000;
    private String url = "";
    private String ilsTapuhiFlag = "RT_ILS";
    private String collectionType = "CT_EPB";
    private String objectType = "OT_WWW";
    private String agencyResponsible = "AR_NLNZ";
    private String instanceRole = "IRC_PM";
    private String instanceCaptureSystem = "CS_WCT";
    private String instanceType = "IT_COM";
    private int user_group = 4;

	private String user;
	private String password;
	
	/**
	 * @param targetInstanceOID The target instance oid 
	 * @param SIP The METS xml structure for completion and archival
	 * @param xAttributes Any extra attributes that may be required for archival (generally contains parameters for OMS meta-data)
	 * @param files A list of files (@see org.webcurator.core.archive.ArchiveFile) to archive 
	 * @return A unique archive identifier (IID returned form the OMS system)
	 * @throws OMSArchiveException
	 */
	public String submitToArchive(String targetInstanceOID, String SIP, Map xAttributes, List<ArchiveFile> files) throws OMSUploadException{
		String IID = null;
		if(targetInstanceOID != null){
			OMSUploadUtil uploader = new OMSUploadUtil(targetInstanceOID);
			try {
				populateUploader(uploader, xAttributes);
				for(ArchiveFile f : files){
					FileInputStream fis = new FileInputStream(f.getFile());
					f.setMd5(uploader.uploadContent(fis,f.getFile().getName(),f.getFile().length()));
					fis.close();
				}
				// TODO finish SIP METS xml based on files
				String finalSIP = getFinalSIP(SIP, targetInstanceOID, files);
				InputStream sipIS = new StringBufferInputStream(finalSIP);
				String metsFileName = "METS-"+targetInstanceOID+".xml";
				uploader.uploadContent(sipIS,metsFileName,finalSIP.length());
				IID = uploader.uploadPub();
			} catch (FileNotFoundException fnfe) {
				// TODO Auto-generated catch block
				log.error("Content file not found",fnfe);
				throw new OMSUploadException(fnfe);
			} catch (OMSUploadException oue) {
				// TODO Auto-generated catch block
				log.error("Error submitting to archive",oue);
				throw new OMSUploadException(oue);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				log.error("Error submiting to archive",e);
				throw new OMSUploadException(e);
			}
		}
		return IID;
	}
	
	protected String getFinalSIP(String sip, String targetInstanceOID, List<ArchiveFile> files) {
		return SIPUtils.finishSIP(sip, targetInstanceOID, files, true);
	}

	private void populateUploader(OMSUploadUtil uploader, Map attributes) {
		// Populate the basics from our config-populated fields.
		uploader.setIlsTapuhiFlag(ilsTapuhiFlag);
		uploader.setPartSize(partSize);
		uploader.setUrl(url);
		uploader.setCollectionType(collectionType);
		uploader.setObjectType(objectType);
		uploader.setAgencyResponsible(agencyResponsible);
		uploader.setInstanceRole(instanceRole);
		uploader.setInstanceCaptureSystem(instanceCaptureSystem);
		uploader.setInstanceType(instanceType);
		uploader.setUser_group(user_group);
		
		/* Should be target reference number - what target reference number?
		 */  
		//Set<Seed> seedSet = targetInstance.getTarget().getSeeds();
		uploader.setReferenceNumber((String)attributes.get(REFERENCE_NUMBER)); // TODO 
		uploader.setAlternativeReferenceNumber((String)attributes.get(org.webcurator.core.archive.Constants.ALTERNATE_REFERENCE_NUMBER)); // TODO 
		/* This depends on what the Target Access Restrictions were. 
		 * 
		 * The four values in Permissions: 
		 * Access Restrictions should match these exactly for NLNZ. 
		 * When multiple permission records apply, use the most restrictive value in the OMS SIP.
		 *	OMS_ACCESS_RESTRICTION_OPEN_ACCESS [ACR_OPA] Open Access
		 *	OMS_ACCESS_RESTRICTION_ON_SITE [ACR_ONS] On Site
		 *	OMS_ACCESS_RESTRICTION_ON_SITE_RESTRICTED [ACR_OSR] On Site Restricted
		 *	OMS_ACCESS_RESTRICTION_RESTRICTED [ACR_RES] Restricted
		 */
		uploader.setAccessRestriction((String)attributes.get(ACCESS_RESTRICTION)); // TODO 
		/* Use the OpenAccessDate field in the permission table (use the latest if multiple apply). */
		Object d =  attributes.get(RESTRICTION_DATE);
		if(d!=null){
			if(d instanceof GregorianCalendar){
				uploader.setRestrictionDate(((GregorianCalendar)d).getTime()); // TODO
			}else if(d instanceof Date){
				uploader.setRestrictionDate((Date)d); // TODO				
			}
		}
        /* Use Permission.IsPublicallyAccessable (a.k.a. AvailabilityFlag). 
		 * If there are multiple Permission records, use their conjunction. 
		 */
		uploader.setAccessAvailable((Boolean)attributes.get(ACCESS_AVAILABLE)); // TODO
		/* Owner of the Target/Schedule This should be the owner of the TargetInstance. 
		 * However, the advice we have is that it has to be an OMS username, and is generally 
		 * set to the username of the person who submits to the OMS, so we will probably have to do that.
		 */ 
		uploader.setPersonResponsible((String)attributes.get(PERSON_RESPONSIBLE)); // TODO
		/* If the TargetInstance.ModificationsNote field is non-empty then set to true, 
		 * otherwise set to false.
		 */ 
		Object o = attributes.get(MAINTENANCE_FLAG);
		if(o != null){
			uploader.setMaintenanceFlag((Boolean)o); // TODO
		}
		/* Use TargetInstance.ModificationsNote */
		o = attributes.get(MAINTENANCE_NOTES);
		if(o != null){
			uploader.setInMaintenanceNotes((String)o); // TODO
		}
		/* Use TargetInstance.AssessmentNote */
		o = attributes.get(DEPENDENCIES);
		if(o != null){
			uploader.setInDependencies((String)o);
		}
		/* Use seed URL(s) where Seed.Type = primary. (But we are not exactly sure how to implant it…)
		 */
		o = attributes.get(ENTRY_POINT_URL);
		if(o != null){
			uploader.setInEntryPointURL((String)o); // TODO
		}
		/* Who is the logged in user??? 
		 */
		//uploader.setUser("wctuser");//(String)attributes.get(USER)); // TODO
		uploader.setUser(user);
		uploader.setPassword(password);
	}

	/**
	 * This implementation of Archive module does not require a custom form to be filled 
	 * before archiving any harvest. Therefore, this method will return null.
	 */
	public CustomDepositFormResultDTO getCustomDepositFormDetails(CustomDepositFormCriteriaDTO criteria) {
		return null;
	}

	public void setAgencyResponsible(String agencyResponsible) {
		this.agencyResponsible = agencyResponsible;
	}

	public void setCollectionType(String collectionType) {
		this.collectionType = collectionType;
	}

	public void setIlsTapuhiFlag(String ilsTapuhiFlag) {
		this.ilsTapuhiFlag = ilsTapuhiFlag;
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

	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}

	public void setPartSize(int partSize) {
		this.partSize = partSize;
	}

	public void setUser_group(int user_group) {
		this.user_group = user_group;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @param user the user to set
	 */
	public void setUser(String user) {
		this.user = user;
	}

}
