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

import static org.webcurator.core.archive.Constants.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.webcurator.core.exceptions.DigitalAssetStoreException;
import org.webcurator.core.scheduler.TargetInstanceManager;
import org.webcurator.core.store.DigitalAssetStore;
import org.webcurator.core.targets.TargetManager;
import org.webcurator.domain.model.core.Permission;
import org.webcurator.domain.model.core.Seed;
import org.webcurator.domain.model.core.TargetInstance;
import org.webcurator.ui.util.DateUtils;

/**
 * Implementation of the ArchiveAdapter interface.
 * @see ArchiveAdapter
 * @author aparker
 */
public class ArchiveAdapterImpl implements ArchiveAdapter {
	/** the logger. */
	private static Log log = LogFactory.getLog(ArchiveAdapterImpl.class);
	/** the digital asset store containing the harvests. */
	private DigitalAssetStore digitalAssetStore = null;
	/** the manager for accessing target instance data. */
	private TargetInstanceManager targetInstanceManager;
	/** the manager for accessing target and group data. */
	private TargetManager targetManager;

	private Map<String, String> accessStatusMap;
	private boolean targetReferenceMandatory = true;
	
	/** @see ArchiveAdapter#submitToArchive(TargetInstance, String, Map, int). */
	@SuppressWarnings("unchecked")
	public void submitToArchive(TargetInstance instance, String sipXML, Map customDepositFormElements, int harvestNumber) throws Exception{
		// Populate the map of SIP information.
		Map map = createMap(instance);

		if (customDepositFormElements != null && customDepositFormElements.isEmpty() == false)
			map.putAll(customDepositFormElements);

		try {
			digitalAssetStore.submitToArchive(instance.getOid().toString(),sipXML,map,harvestNumber);
			// update the state of the target instance to archived
			instance.setState(TargetInstance.STATE_ARCHIVING);
			targetInstanceManager.save(instance);			
		} catch (DigitalAssetStoreException e) {
			// TODO Auto-generated catch block
			throw new Exception(e.getMessage(), e);
		}
	}

	@SuppressWarnings("unchecked")
	private Map createMap(TargetInstance instance) throws ArchiveException {
		HashMap map = new HashMap();
		try{
		/* Should be target reference number
		 */  
		if(targetReferenceMandatory && ((instance.getTarget().getReferenceNumber() == null)||(instance.getTarget().getReferenceNumber().length()==0))){
			throw new ArchiveException("Target Reference Number cannot be blank.  This is mandatory for archiving.");
		}
		map.put(REFERENCE_NUMBER,instance.getTarget().getReferenceNumber());
		map.put(ALTERNATE_REFERENCE_NUMBER, "WCT:"+instance.getTarget().getOid());
		map.put(MAINTENANCE_NOTES, DateUtils.get().formatFullDateTime(instance.getActualStartTime()));
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
		String restriction = OMS_ACCESS_RESTRICTION_OPEN_ACCESS;
		Date restrictionDate = null;
		boolean accessAvailable = true;
		String entryPointURL = ""; 
		
		Set<Seed> seedSet = targetManager.getSeeds(instance);
		for(Seed seed : seedSet){
			if(seed.isPrimary()){
				if(entryPointURL.length()>0){
					entryPointURL += " ";
				}
				entryPointURL += seed.getSeed();
			}
			for( Permission perm : seed.getPermissions()){
				restriction = getLeastRestrictiveAccess(restriction,getMappedRestriction(perm.getAccessStatus()));
				restrictionDate = getLatestDate(restrictionDate,perm.getOpenAccessDate());
				accessAvailable = accessAvailable & perm.isAvailableFlag(); 
			}
		}
		map.put(ACCESS_RESTRICTION,restriction);
		/* Use the OpenAccessDate field in the permission table (use the latest if multiple apply). */
		if(restrictionDate!=null){
			map.put(RESTRICTION_DATE,restrictionDate);
		}
		/* Use Permission.IsPublicallyAccessable (a.k.a. AvailabilityFlag). 
		 * If there are multiple Permission records, use their conjunction. 
		 */
		map.put(ACCESS_AVAILABLE,accessAvailable);
		/* Owner of the Target/Schedule This should be the owner of the TargetInstance. 
		 * However, the advice we have is that it has to be an OMS username, and is generally 
		 * set to the username of the person who submits to the OMS, so we will probably have to do that.
		 */ 
		/* Use seed URL(s) where Seed.Type = primary. (But we are not exactly sure how to implant it…)
		 */
		map.put(ENTRY_POINT_URL,entryPointURL);
		/* Who is the logged in user??? 
		 */ 
		map.put(USER,org.webcurator.core.util.AuthUtil.getRemoteUserObject().getUsername());
//		uploader.setUser(null); // TODO

		map.put(HARVEST_TYPE,instance.getTarget().getDublinCoreMetaData().getType());
		}
		catch(ArchiveException ex) {
			// Make sure that the Archive Exception is thrown to the front-end.
			throw ex;
		}
		catch(Exception e){
			log.error("Failed to complete SIP map",e);
		}
		return map;
	}
	
	private Date getLatestDate(Date d1,Date d2){
		if(d1 == null)return d2;
		if(d2 == null)return d1;
		if(d1.after(d2))return d1;
		return d2;
	}

	private String getLeastRestrictiveAccess(String acc1, String acc2){
		if(getRestrictionLevel(acc1)<getRestrictionLevel(acc2)){
			return acc2;
		}else{
			return acc1;
		}
	}
	private int getRestrictionLevel(String res){
		if((res == null) || (res.equals(OMS_ACCESS_RESTRICTION_OPEN_ACCESS)) || (res.indexOf("unrestricted")>-1)) return 0;
		if(res.equals(OMS_ACCESS_RESTRICTION_ON_SITE)) return 1;
		if(res.equals(OMS_ACCESS_RESTRICTION_ON_SITE_RESTRICTED)) return 2;
		return 3;
	}
	
	String getMappedRestriction(String wctRestriction){
		if(accessStatusMap.containsKey(wctRestriction)) {
			return accessStatusMap.get(wctRestriction);
		}
		if((wctRestriction==null) || (wctRestriction.indexOf("unrestricted")>-1)){
			return OMS_ACCESS_RESTRICTION_OPEN_ACCESS;
		} else{
			return OMS_ACCESS_RESTRICTION_RESTRICTED;
		}
	}

	public void setDigitalAssetStore(DigitalAssetStore digitalAssetStore) {
		this.digitalAssetStore = digitalAssetStore;
	}

	/**
	 * @param targetInstanceManager the targetInstanceManager to set
	 */
	public void setTargetInstanceManager(TargetInstanceManager targetInstanceManager) {
		this.targetInstanceManager = targetInstanceManager;
	}

	/**
	 * @param targetManager the targetManager to set
	 */
	public void setTargetManager(TargetManager targetManager) {
		this.targetManager = targetManager;
	}

	/**
	 * Make a target reference mandatory for archiving a target instance
	 * @param targetReferenceMandatory targetReferenceMandatory flag 
	 */
	public void setTargetReferenceMandatory(boolean targetReferenceMandatory)
	{
		this.targetReferenceMandatory = targetReferenceMandatory;
	}
	
	/**
	 * Gets the target reference mandatory flag
	 * @return targetReferenceMandatory flag 
	 */
	public boolean getTargetReferenceMandatory()
	{
		return targetReferenceMandatory;
	}

	public void setAccessStatusMap(Map<String, String> accessStatusMap) {
		this.accessStatusMap = accessStatusMap;
	}
	
}
