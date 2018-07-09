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
 * Abstract implementation of the SiteManagerListener.
 * @author bbeaumont
 */
public class AbstractSiteManagerListener implements SiteManagerListener {
	/** @see SiteManagerListener#beforeSave(Site).*/
	public void beforeSave(Site aSite) {
	}
	/** @see SiteManagerListener#afterSave(Site).*/
	public void afterSave(Site aSite) {
	}
	/** @see SiteManagerListener#beforeDelete(Site).*/
	public void beforeDelete(Site aSite) {
	}
	/** @see SiteManagerListener#afterDelete(Site).*/
	public void afterDelete(Site aSite) {
	}
}
