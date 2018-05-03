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
package org.webcurator.core.profiles;

import java.util.List;

import org.webcurator.auth.AuthorityManager;
import org.webcurator.core.exceptions.WCTInvalidStateRuntimeException;
import org.webcurator.core.harvester.HarvesterType;
import org.webcurator.core.util.Auditor;
import org.webcurator.core.util.AuthUtil;
import org.webcurator.domain.ProfileDAO;
import org.webcurator.domain.model.auth.Agency;
import org.webcurator.domain.model.auth.User;
import org.webcurator.domain.model.core.AbstractTarget;
import org.webcurator.domain.model.core.Profile;
import org.webcurator.domain.model.dto.ProfileDTO;

/**
 * The manager for Profile actions.
 * @author bbeaumont
 *
 */
public class ProfileManager {
	/** The DAO for profiles */
	private ProfileDAO profileDao;
	/** The authority manager */
	private AuthorityManager authorityManager;
	/** the auditor. */
	private Auditor auditor;
	
	/**
	 * Create the default profile for the given agency.
	 * @param anAgency The agency to create the profile for.
	 */
	public void createDefaultProfile(Agency anAgency) {
		Profile profile = new Profile();
		profile.setName("Default - " + anAgency.getName());
		profile.setDescription("Default profile created by new agency action");
		profile.setDefaultProfile(true);
		profile.setOwningAgency(anAgency);
		profile.setRequiredLevel(1);
		profile.setStatus(Profile.STATUS_ACTIVE);
		profile.setProfile(HeritrixProfile.create().toString());
		profile.setHarvesterType(HarvesterType.DEFAULT.name());
		
		profileDao.saveOrUpdate(profile);
		auditor.audit(AuthUtil.getRemoteUserObject(), Profile.class.getName(), profile.getOid(), Auditor.ACTION_NEW_PROFILE, "A new profile " + profile.getName() + " has been created for " + profile.getOwningAgency().getName());
	}
	
	/**
	 * Save or update the profile.
	 * @param aProfile the profile to save or update.
	 */
	public void saveOrUpdate(Profile aProfile) {
		Long oid = aProfile.getOid();	
		ProfileDTO originalProfile = null;
		if (oid != null) {
			originalProfile = profileDao.getDTO(oid);
		}
		
		if (aProfile.getStatus() == Profile.STATUS_INACTIVE && originalProfile.getStatus() == Profile.STATUS_ACTIVE) {
			int countActivetargets = profileDao.countProfileActiveTargets(aProfile);
			if (countActivetargets > 0 ) {
				throw new WCTInvalidStateRuntimeException("Profile " + aProfile.getOid() + " cannot be set inactive because it is used by active targets.");
			}
		}
		profileDao.saveOrUpdate(aProfile);
		if (originalProfile == null) {
			auditor.audit(AuthUtil.getRemoteUserObject(), Profile.class.getName(), aProfile.getOid(), Auditor.ACTION_NEW_PROFILE, "A new profile " + aProfile.getName() + " has been created for " + aProfile.getOwningAgency().getName());
		}
		else {			
			if (aProfile.getStatus() == Profile.STATUS_INACTIVE && originalProfile.getStatus() == Profile.STATUS_ACTIVE) {
				auditor.audit(AuthUtil.getRemoteUserObject(), Profile.class.getName(), aProfile.getOid(), Auditor.ACTION_DEACTIVATE_PROFILE, "The profile " + aProfile.getName() + " has been deactivated for " + aProfile.getOwningAgency().getName());
			}		
			else if (aProfile.getStatus() == Profile.STATUS_ACTIVE && originalProfile.getStatus() == Profile.STATUS_INACTIVE) {
				auditor.audit(AuthUtil.getRemoteUserObject(), Profile.class.getName(), aProfile.getOid(), Auditor.ACTION_ACTIVATE_PROFILE, "The profile " + aProfile.getName() + " has been activated for " + aProfile.getOwningAgency().getName());
			}
			else {
				auditor.audit(AuthUtil.getRemoteUserObject(), Profile.class.getName(), aProfile.getOid(), Auditor.ACTION_UPDATE_PROFILE, "The profile " + aProfile.getName() + " has been updated for " + aProfile.getOwningAgency().getName());
			}
		}		
	}
	
	/**
	 * Load a profile based on its OID.
	 * @param oid The OID of the profile to load.
	 * @return The profile.
	 */
	public Profile load(Long oid) {
		return this.profileDao.load(oid);
	}
	
	/**
	 * Get the default profile for the given agency.
	 * @param anAgency The agency to get the profile for.
	 * @return The default profile.
	 */
	public Profile getDefaultProfile(Agency anAgency) {
		return profileDao.getDefaultProfile(anAgency);
	}

	/**
	 * Set the DAO from Spring.
	 * @param profileDao The profileDao to set.
	 */
	public void setProfileDao(ProfileDAO profileDao) {
		this.profileDao = profileDao;		
	}
	
	/**
	 * Checks if the profile is being used by any targets or groups.
	 * @param aProfile The profile to check for.
	 * @return true if the profile is being used; otherwise false.
	 */
	public boolean isProfileInUse(Profile aProfile) {
		return profileDao.countProfileUsage(aProfile) > 0;
	}
	
	
	/**
	 * Delete the profile.
	 * @param aProfile The profile to delete.
	 */
	public void delete(Profile aProfile) {
		profileDao.delete(aProfile);		
		auditor.audit(AuthUtil.getRemoteUserObject(), Profile.class.getName(), aProfile.getOid(), Auditor.ACTION_DELETE_PROFILE, "The profile " + aProfile.getName() + " has been deleted form " + aProfile.getOwningAgency().getName());
	}
	
	
	/**
	 * Get the list of profiles that can be set. The current profile can always
	 * be set (since this is the same as not changing the profile).
	 * @param aTarget The target that you want to set the profile on.
	 * @return A list of ProfileDTO objects for available profiles.
	 */
	public List<ProfileDTO> getAvailableProfiles(AbstractTarget aTarget) {
		User remoteUser = AuthUtil.getRemoteUserObject();
		int profileLevel = authorityManager.getProfileLevel();
		return profileDao.getAvailableProfiles(remoteUser.getAgency(), profileLevel, aTarget.getProfile().getOid());
	}

	/**
	 * Get the list of profiles that can be set. The current profile can always
	 * be set (since this is the same as not changing the profile).
	 * @param aCurrentProfileOid The OID of the current profile.
	 * @return A list of the profiles that can be set for this target.
	 */
	public List<ProfileDTO> getAvailableProfiles(Long aCurrentProfileOid) {
		User remoteUser = AuthUtil.getRemoteUserObject();
		int profileLevel = authorityManager.getProfileLevel();
		return profileDao.getAvailableProfiles(remoteUser.getAgency(), profileLevel, aCurrentProfileOid);
	}
	
	/**
	 * Set the profile as the default for this agency.
	 * @param aProfile The profile to set as default.
	 */
	public void setProfileAsDefault(Profile aProfile) {
		profileDao.setProfileAsDefault(aProfile);		
		auditor.audit(AuthUtil.getRemoteUserObject(), Profile.class.getName(), aProfile.getOid(), Auditor.ACTION_SET_DEFAULT_PROFILE, "The profile " + aProfile.getName() + " has been set as the default for " + aProfile.getOwningAgency().getName());
	}
	
	/**
	 * Return a list of all profile DTOs.
	 * @return The list of profile DTOs.
	 */
	public List<ProfileDTO> getAllDTOs() {
		return profileDao.getAllDTOs();
	}
	
	/**
	 * Return a list of profile DTOs.
	 * @param showInactive include inactive profiles
	 * @param type filter by this harvester type (null means don't filter)
	 * @return The list of profile DTOs.
	 */
	public List<ProfileDTO> getDTOs(boolean showInactive, String type) {
		return profileDao.getDTOs(showInactive, type);
	}


	/**
	 * Return a list of profile DTOs for an agency.
	 * @param agency the agency whose profiles are required
	 * @param showInactive include inactive profiles
	 * @param type filter by this harvester type (null means don't filter)
	 * @return The list of profile DTOs.
	 */
	public List<ProfileDTO> getAgencyDTOs(Agency agency, boolean showInactive, String type) {
		return profileDao.getAgencyDTOs(agency, showInactive, type);
	}
		
	/**
	 * Spring setter method for the Authority Manager.
	 * @param authorityManager The authorityManager to set.
	 */
	public void setAuthorityManager(AuthorityManager authorityManager) {
		this.authorityManager = authorityManager;
	}

	/**
	 * @param auditor the auditor to set
	 */
	public void setAuditor(Auditor auditor) {
		this.auditor = auditor;
	}

}
