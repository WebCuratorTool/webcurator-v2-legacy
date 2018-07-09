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

import java.util.Set;

import org.webcurator.domain.model.core.Permission;
import org.webcurator.domain.model.core.Seed;
import org.webcurator.domain.model.core.Site;
import org.webcurator.domain.model.core.Target;
import org.webcurator.domain.model.core.UrlPattern;

/**
 * Generic class for the PermissionMappingStrategy. The strategy is used to 
 * find Permissions that match seeds. Different implementations may do this
 * in different methods and may support different types of URL Patterns.
 * 
 * In version 1.0 of the Web Curator Tool, the WCT uses the
 * HierarchicalPermissionMappingStrategy.
 *  
 * @see org.webcurator.core.permissionmapping.HierarchicalPermissionMappingStrategy
 * 
 * @author bbeaumont
 *
 */
public abstract class PermissionMappingStrategy {

	/** The PermissionMappingStrategy being used in the system */
	private static PermissionMappingStrategy instance = null;
	
	/**
	 * Set the strategy. This must be called during system initialisation.
	 * @param aStrategy The strategy to use.
	 */
	public static synchronized void setStrategy(PermissionMappingStrategy aStrategy) {
		instance = aStrategy;
	}
	
	/**
	 * Get the active Permission Mapping Strategy.
	 * @return the active Permission Mapping Strategy.
	 */
	public static synchronized PermissionMappingStrategy getStrategy() {
		return instance;
	}

	
	
	/**
	 * Add a mapping.
	 * @param aPermission The permission.
	 * @param aUrlPattern The mapping.
	 */
	public abstract void add(Permission aPermission, UrlPattern aUrlPattern);
	
	/**
	 * Remove a mapping.
	 * @param aPermission The permission.
	 * @param aUrlPattern The mapping.
	 */
	public abstract void remove(Permission aPermission, UrlPattern aUrlPattern);
	
	/**
	 * Check if the URL pattern is acceptable to the strategy.
	 * @param aUrlPattern the url pattern to test.
	 * @return true if acceptable; otherwise false.
	 */
	public abstract boolean isValidPattern(UrlPattern aUrlPattern);
	
	/**
	 * Get a Set of permissions that match the seed.
	 * @param aSeed The seed to match.
	 * @param aTarget the target to match for.
	 * @return A set of permissions.
	 */
	public abstract Set<Permission> getMatchingPermissions(Target aTarget, Seed aSeed);
	
	/**
	 * Get a Set of permissions that match the URL.
	 * @param aUrl The URL to match.
	 * @param aTarget the target to match for.
	 * @return A set of permissions.
	 */	
	public abstract Set<Permission> getMatchingPermissions(Target aTarget, String aUrl);
	
	/**
	 * Check if a given Url Pattern matches a given seed.
	 * @param aUrlPattern The URL Pattern.
	 * @param aSeed		  The seed.
	 * @return true if they match; otherwise false.
	 */	
	public abstract boolean matches(UrlPattern aUrlPattern, Seed aSeed);
	
	/**
	 * Remove all mappings for the site.
	 * @param aSite The site to remove mappings for.
	 */
	public abstract void removeMappings(Site aSite);
	
	/**
	 * Save all mappings for a site.
	 * @param aSite The site to save mappings for.
	 */
	public abstract void saveMappings(Site aSite);

	
	
}
