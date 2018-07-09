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
package org.webcurator.ui.site.controller;

import javax.servlet.http.HttpServletRequest;

import org.webcurator.ui.site.SiteEditorContext;
import org.webcurator.ui.util.TabHandler;

/**
 * Abstact base class for all Site tab handlers.
 * @author bbeaumont
 */
public abstract class AbstractSiteHandler extends TabHandler {
	/**
	 * Return the SiteEditorContext from the session
	 * @param req the request to get the session from
	 * @return the SiteEditorContext
	 */
	public SiteEditorContext getEditorContext(HttpServletRequest req) {
		SiteEditorContext ctx = (SiteEditorContext) req.getSession().getAttribute("siteEditorContext");
		if( ctx == null) {
			throw new IllegalStateException("siteEditorContext not yet bound to the session");
		}
		
		return ctx;
	}
}
