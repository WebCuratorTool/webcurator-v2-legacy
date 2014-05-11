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
package org.webcurator.domain;

import java.util.List;

import org.webcurator.domain.model.auth.Agency;
import org.webcurator.domain.model.core.Profile;
import org.webcurator.domain.model.dto.ProfileDTO;

/**
 * DAO for loading/saving Profiles to the database.
 * @author bbeaumont
 */
public interface ProfileDAO extends BaseDAO {
	
	/**
	 * Load a Profile by its database ID.
	 * @param oid The database ID.
	 * @return The Profile from the database.
	 */
	public Profile load(Long oid);
	
	/**
	 * Save the profile to the database.
	 * @param profile The profile to save.
	 */
	public void saveOrUpdate(Profile profile);
	
	/**
	 * List all of the profile DTOs.
	 * @return A List of profile DTOs.
	 */
	public List<ProfileDTO> getAllDTOs();
	
	
	/**
	 * Gets the profile DTOs.
	 * @param showInactive show inactive profiles.
	 * @return A list of profiles.
	 */
	public List<ProfileDTO> getDTOs(boolean showInactive);

	/**
	 * Gets the profile DTOs for an Agency.
	 * @param agency the agency whose profiles should be returned.
	 * @param showInactive show inactive profiles.
	 * @return A list of profiles.
	 */
	public List<ProfileDTO> getAgencyDTOs(Agency agency, boolean showInactive);

	/**
	 * Return the ProfileDTO for the specified profile oid.
	 * @param aOid thge oid to return the dto for
	 * @return the dto
	 */
	ProfileDTO getDTO(final Long aOid);
	
	/**
	 * Return the ProfileDTO for the specified locked profile oid and version.
	 * @param aOrigOid the original oid to return the locked dto for
	 * @param aVersion the version of the original dto
	 * @return the dto
	 */
	ProfileDTO getLockedDTO(final Long aOrigOid, final Integer aVersion);
	
	/**
	 * Get the default profile for the given agency.
	 * @param anAgency The agency to get the default profile for.
	 */
	public Profile getDefaultProfile(Agency anAgency);
	
	/**
	 * Get available profiles.
	 */
	public List<ProfileDTO> getAvailableProfiles(Agency anAgency, int level, Long currentProfileOid);
	
	/**
	 * Counts the number of Targets, Target Groups, and Target Instances 
	 * that are currently using this profile.
	 * @param aProfile The profile to count.
	 * @return The number of targets or groups using that profile.
	 */
	public int countProfileUsage(Profile aProfile);
	
	/**
	 * Counts the number of Active Targets 
	 * that are currently using this profile.
	 * @param aProfile The profile to count.
	 * @return The number of active targets using that profile.
	 */
	public int countProfileActiveTargets(Profile aProfile);

	/**
	 * Set the profile as the default for this agency.
	 * @param aProfile The profile to set as default.
	 */
	public void setProfileAsDefault(Profile aProfile);
	
}
