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

import org.webcurator.domain.model.core.Site;

/**
 * Defines the methods called on a site that can be listened for.
 * @author bbeaumont
 */
public interface SiteManagerListener {
	/**
	 * Called before the site is persisted to the database.
	 * @param aSite the site being saved
	 */
	public void beforeSave(Site aSite);
	/**
	 * Called after the site is persisted to the database.
	 * @param aSite the site being saved
	 */
	public void afterSave(Site aSite);
	/**
	 * Called before the site has been removed from the database
	 * @param aSite the site being removed
	 */
	public void beforeDelete(Site aSite);
	/**
	 * Called after the site has been removed from the database
	 * @param aSite the site that has been removed
	 */
	public void afterDelete(Site aSite);
}
