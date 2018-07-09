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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.webcurator.core.sites.AbstractSiteManagerListener;
import org.webcurator.domain.model.core.Site;

/**
 * Listens for save/delete events on a site so that the permission/url pattern
 * mappings can be updated.
 * 
 * @author bbeaumont
 */
public class PermMappingSiteListener extends AbstractSiteManagerListener{
	/** The log file for this class */
	private static Log log = LogFactory.getLog(PermMappingSiteListener.class);
	
	/** The permission mapping strategy to use */
	private PermissionMappingStrategy strategy = null;

	
	
	
	/* (non-Javadoc)
	 * @see org.webcurator.core.sites.AbstractSiteManagerListener#beforeSave(org.webcurator.domain.model.core.Site)
	 */
	@Override
	public void beforeSave(Site aSite) {
		// TODO Auto-generated method stub
		log.debug("Before Save Event Handler");
		strategy.removeMappings(aSite);
	}

	/* (non-Javadoc)
	 * @see org.webcurator.core.sites.AbstractSiteManagerListener#afterSave(org.webcurator.domain.model.core.Site)
	 */
	@Override
	public void afterSave(Site aSite) {
		log.debug("After Save Event Handler");
		if(aSite.isActive()) {
			strategy.saveMappings(aSite);
		}
		log.debug("After Save Mappings");
	}

	/* (non-Javadoc)
	 * @see org.webcurator.core.sites.AbstractSiteManagerListener#beforeDelete(org.webcurator.domain.model.core.Site)
	 */
	@Override
	public void beforeDelete(Site aSite) {
		log.debug("Before Delete Event Handler");
		strategy.removeMappings(aSite);
	}


	/**
	 * Spring Setter.
	 * @param strategy The strategy to set.
	 */
	public void setStrategy(PermissionMappingStrategy strategy) {
		this.strategy = strategy;
	}

}
