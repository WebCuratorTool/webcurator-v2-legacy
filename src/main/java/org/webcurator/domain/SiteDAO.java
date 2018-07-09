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
import org.webcurator.domain.model.core.AuthorisingAgent;
import org.webcurator.domain.model.core.Permission;
import org.webcurator.domain.model.core.Site;

/**
 * The interface used for accessing persistent Harvest Authorisation data.
 * @author bbeaumont
 */
public interface SiteDAO {
	/**
	 * Save or update the specifed site to the persistent store.
	 * @param aSite the site to save or update.
	 */
	void saveOrUpdate(Site aSite);
	
	/**
	 * Search for any Site's that match the specified criteria.
	 * @param criteria the search criteria
	 * @param page the page number to return
	 * @return the page of search results
	 */
	Pagination search(final SiteCriteria criteria, final int page, final int pageSize);	
	
	/**
	 * Load the specified site from the persistent store.
	 * @param siteOid the id of the site to load
	 * @return the requested site
	 */
	Site load(final long siteOid);
	
	/**
	 * Load the specified site from the persistent store and also
	 * load all its realted Objects if the flag is set. 
	 * @param siteOid the id of the site to load
	 * @param fullyInitialise true if the relationships should be loaded
	 * @return the requested site
	 */
	Site load(final long siteOid, boolean fullyInitialise);
	
	/**
	 * Load the specified permission from the persistent store.
	 * @param permOid the id of the requested permission
	 * @return the requested permission object.
	 */	
	Permission loadPermission(final long permOid);
	
	/**
	 * List all the site objects that match the specified title.
	 * @param aTitle the title of the sites to retrieve
	 * @return the list of sites
	 */
	List<Site> listSitesByTitle(final String aTitle);
	
	/**
	 * Return the list of permissions that are marked as quick picks 
	 * for the specified agency.
	 * @param anAgency the agency to return the quick pick list for
	 * @return the list of permission quick picks
	 */
	List<Permission> getQuickPickPermissions(Agency anAgency);
	
	/**
	 * Count the number of sites in the persistent store.
	 * @return the number of sites
	 */
	int countSites();
	
	/**
	 * Find permissions by Site
	 * @param anAgencyOid The OID of the agency to restrict the search to.
	 * @param aSiteTitle The name of the site.
	 * @param aPageNumber The page number to return.
	 * @return A List of Permissions.
	 */
	Pagination findPermissionsBySiteTitle(Long anAgencyOid, String aSiteTitle, int aPageNumber);
	
	/**
	 * Get a count of the number of seeds related to a given permission.
	 * @param aPermissionOid The permission oid
	 * @return The number of seeds linked to the permission
	 */
	public int countLinkedSeeds(Long aPermissionOid); 	
	
	/**
	 * Search for existing Authorising Agencies by name.
	 * @param name The name of the agency to search for. 
	 * @param page The page number.
	 * @return A pagination of results.
	 */
	public Pagination searchAuthAgents(final String name, final int page);	
	
	/**
	 * Load an authorising agent from the database.
	 * @param authAgentOid The OID of the authorising agent to load.
	 * @return The authorising agent.
	 */
	public AuthorisingAgent loadAuthorisingAgent(final long authAgentOid);

	/**
	 * Check that the Authorising Agent name is unique.
	 * @param oid  The OID of the authorising agent, if available.
	 * @param name The name of the authorising agent.
	 * @return True if unique; otherwise false.
	 */
    public boolean isAuthAgencyNameUnique(Long oid, String name);
}
