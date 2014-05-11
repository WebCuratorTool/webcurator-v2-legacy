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
package org.webcurator.core.permissionmapping;

import java.util.List;
import java.util.Set;

import org.webcurator.domain.model.core.Site;

/**
 * DAO for the Hierarchical Permission Mapping Strategy.
 * @author bbeaumont
 *
 */
public interface HierPermMappingDAO {
	/**
	 * Save the mapping to the database.
	 * @param aMapping The mapping to save.
	 */
	public void saveOrUpdate(final Mapping aMapping);
	
	/**
	 * Get a single mapping for a given oid.
	 * @param oid The Oid.
	 * @return A list with one Mapping for the given oid.
	 */
	public List<Mapping> getMapping(Long oid);

	/**
	 * Get all mappings for a given domain.
	 * @param domain The domain.
	 * @return A List of mappings that fall within that domain.
	 */
	public List<Mapping> getMappings(String domain);
	
	/**
	 * Get all 'mappings via a database view' for a given domain.
	 * A 'mappingview' is a (poorly named?) entity populated via
	 * a database view (pseudo table) which returns the bare minimum
	 * of data required for the calling HierarchicalPermissionMappingStrategy
	 * class to implement the required seed search logic.
	 * This entity was created to address severe performance problems when
	 * adding seed URLs to targets via the above 'List<Mapping> getMappings()' 
	 * method.  
	 * @param domain The domain.
	 * @return A List of MappingView entities that fall within that domain.
	 */
	public List<MappingView> getMappingsView(String domain);
	
	/**
	 * Delete a mapping.
	 * @param aMapping The mapping to delete.
	 */
	public void delete(final Mapping aMapping);
	
	/**
	 * Delete all mappings for the site.
	 * @param aSite The site to delete mappings for.
	 */
	public void deleteMappings(Site aSite);
	
	/**
	 * Save a list of mappings.
	 * @param mappings The list of mappings to save.
	 */
	public void saveMappings(List<Mapping> mappings);
	
	/**
	 * Update the mappings for the given site.
	 * @param aSite The site to update.
	 * @param newMappings The new set of mappings.
	 */
	public void updateMappings(Site aSite, Set<Mapping> newMappings);
}
