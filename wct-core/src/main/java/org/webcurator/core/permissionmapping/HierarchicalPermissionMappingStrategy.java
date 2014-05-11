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

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.webcurator.domain.model.core.Permission;
import org.webcurator.domain.model.core.Seed;
import org.webcurator.domain.model.core.Site;
import org.webcurator.domain.model.core.Target;
import org.webcurator.domain.model.core.UrlPattern;

/**
 * The HierarchicalPermissionMappingStrategy is a strategy for fast searches to
 * find permissions linked to URL Patterns that match a given seed. 
 * 
 * For example, the URL Pattern http://*.govt.nz/* will be indexed under
 * govt.nz. 
 * 
 * When searching for an match to http://www.corrections.govt.nz/ this strategy
 * will first look for patterns under the "www.corrections.govt.nz" key. Failing
 * that it will search the "corrections.govt.nz" key. Failing a second time it
 * will try "govt.nz", which will find our pattern above. The strategy will
 * finally try the "nz" key in case there are further permissions of the 
 * format "http://*.nz/*". 
 * 
 * Expression matches will be applied to any URL Patterns found in the index.
 * If the expression matches are successful, then we have found a URL Pattern/
 * Permission mapping that is appropriate for a seed.
 * 
 * @author bbeaumont
 *
 */
public class HierarchicalPermissionMappingStrategy extends PermissionMappingStrategy {
	
	/** The DAO for loading mappings */
	private HierPermMappingDAO dao = null;
	
	/**
	 * Constructor
	 */
	public HierarchicalPermissionMappingStrategy() {};

	/**
	 * Add a Permission/UrlPattern mapping.
	 * @param aPermission The permission 
	 * @param aUrlPattern The UrlPattern.
	 */
	public void add(Permission aPermission, UrlPattern aUrlPattern) {
		Mapping m = new Mapping(aUrlPattern, aPermission);
		dao.saveOrUpdate(m);
		
	}
	
	/**
	 * Remove a Permission/UrlPattern mapping.
	 * @param aPermission The permission 
	 * @param aUrlPattern The UrlPattern.
	 */
	public void remove(Permission aPermission, UrlPattern aUrlPattern) {
		Mapping m = new Mapping(aUrlPattern, aPermission);
		dao.delete(m);
	}
	
	/**
	 * Remove all mappings for a given site.
	 * @param aSite The site to remove mappings for.
	 */	
	public void removeMappings(Site aSite) {
		dao.deleteMappings(aSite);
	}
	
	/**
	 * Save all mappings for the given site.
	 * @param aSite The site to save the mappings for.
	 */
	public void saveMappings(Site aSite) {
		List<Mapping> newMappings = new LinkedList<Mapping>();
		
		for(Permission p: aSite.getPermissions()) {
			for(UrlPattern u: p.getUrls()) {
				newMappings.add(new Mapping(u, p));
			}
		}	
		
		dao.saveMappings(newMappings);
	}
	
	/**
	 * Check the the URL pattern is valid for this mapping strategy.
	 * @param aUrlPattern The UrlPattern to test.
	 * @return true if the URL pattern is valid.
	 */
	public boolean isValidPattern(UrlPattern aUrlPattern) {
		return isValidPattern(aUrlPattern.getPattern());
	}

	/**
	 * Test that the Url pattern meets the following criteria:
	 * 
	 * A star may be the first character in the host part pattern,
	 * but may not be anywhere else. A star may be the last character
	 * in the resource part of the pattern, but nowhere else.
	 * 
	 * So, for example, http://*.nz/* is valid, but http://*.co.*\/*.html is not.			
	 * 
	 * @param aUrlPattern The pattern to test.
	 * @return True if valid; otherwise false.
	 */
	public boolean isValidPattern(String aUrlPattern) {
		// A star may be the first character in the host part pattern,
		// but may not be anywhere else. A star may be the last character
		// in the resource part of the pattern, but nowhere else.			
		try {
			boolean hostOkay = UrlUtils.getHost(aUrlPattern).lastIndexOf('*') <= 0;
			
			String resourcePart = UrlUtils.getResource(aUrlPattern);
			int starIndex = resourcePart.indexOf('*');
			boolean resourceOkay = starIndex < 0 || starIndex == resourcePart.length() -1;
			
			return hostOkay && resourceOkay;
		} 
		catch (Exception e) {
			return false;
		}
	}

	/**
	 * Get all the permissions that match the given seed.
	 * @param aSeed The seed to match.
	 * @param aTarget the target to match for.
	 * @return a set of permissions that match. May be empty.
	 */
	public Set<Permission> getMatchingPermissions(Target aTarget, Seed aSeed) {
		return getMatchingPermissions(aTarget, aSeed.getSeed());
	}

	/**
	 * Get all the permissions that match the given url.
	 * @param aUrl The url to match.
	 * @param aTarget the target to match for.
	 * @return a set of permissions that match. May be empty.
	 */	
	public Set<Permission> getMatchingPermissions(Target aTarget, String aUrl) {
		Set<Permission> permissions = new HashSet<Permission>();
		Set<Long> oids = new HashSet<Long>();
		
		// Get the ALL mappings.
		// Use the MappingView entity for performance (data fetched in single call)
		List<MappingView> mappings = dao.getMappingsView("*");
		for(MappingView m: mappings) {
			if( matches(m.getUrlPattern(), aUrl) 
					&& m.isActiveNowOrInFuture() 
					&& m.getOwningAgencyId().equals(aTarget.getOwner().getAgency().getOid())
					&& m.isSiteActive()) {
				
				if(!oids.contains(m.getPermissionOId())) {
					// now fetch the main Mapping entity to get at the associated Permission
					List<Mapping> mapping = dao.getMapping(m.getOid());
					for(Mapping map: mapping) {
						permissions.add(map.getPermission());
					}
					oids.add(m.getPermissionOId());
				}
			}			
		}

		DomainIterator di = new DomainIterator(UrlUtils.getHost(aUrl));
		while(di.hasNext()) {
			// Use the MappingView entity for performance (data fetched in single call)
			mappings = dao.getMappingsView(di.next());
			for(MappingView m: mappings) {
				if( matches(m.getUrlPattern(), aUrl) 
						&& m.isActiveNowOrInFuture() 
						&& m.getOwningAgencyId().equals(aTarget.getOwner().getAgency().getOid())
						&& m.isSiteActive()) {
					
					if(!oids.contains(m.getPermissionOId())) {
						// now fetch the main Mapping entity to get at the associated Permission
						List<Mapping> mapping = dao.getMapping(m.getOid());
						for(Mapping map: mapping) {
							permissions.add(map.getPermission());
						}
						oids.add(m.getPermissionOId());
					}
				}			
			}
		}
		
		return permissions;
	}

	/**
	 * Tests if a given pattern matches a given seed.
	 * @param aUrlPattern The UrlPattern.
	 * @param aSeed The seed.
	 * @return ture if they match; otherwise false.
	 */
	public boolean matches(UrlPattern aUrlPattern, Seed aSeed) {
		return matches(aUrlPattern, aSeed.getSeed());
	}

	
	/**
	 * Tests if a given pattern matches a given URL.
	 * @param aUrlPattern The UrlPattern (entity).
	 * @param aUrl The url to test.
	 * @return true if they match; otherwise false.
	 */	
	public boolean matches(UrlPattern aUrlPattern, String aUrl) {
		
		int hostPartStar = UrlUtils.getHost(aUrlPattern.getPattern()).lastIndexOf('*');
		String hostPart = hostPartStar < 0 ? UrlUtils.getHost(aUrlPattern.getPattern()) :
			UrlUtils.getHost(aUrlPattern.getPattern()).substring(hostPartStar+1);

		int resourcePartStar = UrlUtils.getResource(aUrlPattern.getPattern()).indexOf('*');
		String resourcePart = resourcePartStar < 0 ? UrlUtils.getResource(aUrlPattern.getPattern()) :
			UrlUtils.getResource(aUrlPattern.getPattern()).substring(0, resourcePartStar);
		
		return UrlUtils.getSchema(aUrl).equals(UrlUtils.getSchema(aUrlPattern.getPattern())) &&
			UrlUtils.getHost(aUrl).endsWith(hostPart) &&
			UrlUtils.getResource(aUrl).startsWith(resourcePart);
	}
	
	/**
	 * Tests if a given pattern matches a given URL.
	 * @param aUrlPattern The UrlPattern (String).
	 * @param aUrl The url to test.
	 * @return true if they match; otherwise false.
	 */	
	public boolean matches(String aUrlPattern, String aUrl) {
		
		int hostPartStar = UrlUtils.getHost(aUrlPattern).lastIndexOf('*');
		String hostPart = hostPartStar < 0 ? UrlUtils.getHost(aUrlPattern) :
			UrlUtils.getHost(aUrlPattern).substring(hostPartStar+1);

		int resourcePartStar = UrlUtils.getResource(aUrlPattern).indexOf('*');
		String resourcePart = resourcePartStar < 0 ? UrlUtils.getResource(aUrlPattern) :
			UrlUtils.getResource(aUrlPattern).substring(0, resourcePartStar);
		
		return UrlUtils.getSchema(aUrl).equals(UrlUtils.getSchema(aUrlPattern)) &&
			UrlUtils.getHost(aUrl).endsWith(hostPart) &&
			UrlUtils.getResource(aUrl).startsWith(resourcePart);
	}
	
	/**
	 * Calculates the effective base domain based on the UrlPattern.
	 * @param aUrlPattern The Url Pattern as a string.
	 * @return The effective domain.
	 */
	public static String calculateDomain(String aUrlPattern) {
		String domain = UrlUtils.getHost(aUrlPattern);
		int starIndex = domain.indexOf('*');
		if( starIndex < 0) { 
			return domain;
		}
		else {
			int dotIndex = domain.indexOf('.', starIndex) + 1;
			return domain.length() >= dotIndex ? domain.substring(dotIndex) : "*";
		}		
	}

	
	/**
	 * Spring setter method for the DAO.
	 * @param dao The dao to set.
	 */
	public void setDao(HierPermMappingDAO dao) {
		this.dao = dao;
	}
	
	
}
