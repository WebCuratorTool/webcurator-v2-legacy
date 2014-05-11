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
package org.webcurator.core.sites;

import java.util.List;

import org.webcurator.domain.Pagination;
import org.webcurator.domain.SiteCriteria;
import org.webcurator.domain.SiteDAO;
import org.webcurator.domain.model.core.Annotation;
import org.webcurator.domain.model.core.AuthorisingAgent;
import org.webcurator.domain.model.core.Permission;
import org.webcurator.domain.model.core.Site;

/**
 * The manager for accessing Harvest Authorisation data.
 * @author bbeaumont
 */
public interface SiteManager {

    /**
     * Save the site to the database.
     * @param aSite The site to be saved.
     */
    void save(Site aSite);

    /**
     * loads a Site using its Oid
     * @param siteOid the Site Oid to load
     * @param fullyInitialise set to true to fully populate the Site object
     * @return the Site Object
     */
    Site getSite(Long siteOid, boolean fullyInitialise);

    /**
     * Check that the site title is unique.
     * @param aSite the site to check
     * @return true if the site title does not exist already on another site
     */
    boolean isSiteTitleUnique(Site aSite);
    
    /**
     * Set the Site DAO.
     * @param siteDao The siteDao to set.
     */
    void setSiteDao(SiteDAO siteDao);

    /**
     * @param listeners The listeners to set.
     */
    void setListeners(List<SiteManagerListener> listeners);
    
    /**
     * Return a list of annotations for the specified site.
     * @param aSite the site to return the annotations for
     * @return the list of annotations
     */
    List<Annotation> getAnnotations(Site aSite);

    /**
     * Return a list of annotations for the specified Permission.
     * @param aPermission the permission to return the annotations for
     * @return the list of annotations
     */
    List<Annotation> getAnnotations(Permission aPermission);
    
    
    /**
     * Return the pagination of Sites for the specified criteria.
     * @param aCriteria the site search criteria
     * @return the pagination of sites
     */
    Pagination search(final SiteCriteria aCriteria);
    
    /**
     * Return the pagination of Sites for the specified criteria and result page.
     * @param aCriteria the site search criteria
     * @param aPage the result page to return
     * @return the pagination of sites
     */
    Pagination search(final SiteCriteria aCriteria, final int aPage, final int aPageSize);
    
    /**
	 * Get a count of the number of seeds related to a given permission.
	 * @param aPermissionOid The permission oid
	 * @return The number of seeds linked to the permission
	 */
	public int countLinkedSeeds(Long aPermissionOid);    
	
    /**
     * Return a count of all the active sites.
     * @return a count of active sites
     */
    int countSites();
    
	/**
	 * Search for existing Authorising Agencies by name.
	 * @param name The name of the agency to search for. 
	 * @param page The page number.
	 * @return A pagination of results.
	 */
	public Pagination searchAuthAgents(String name, int page);
	
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