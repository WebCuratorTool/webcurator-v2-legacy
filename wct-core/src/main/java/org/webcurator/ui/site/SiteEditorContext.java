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
package org.webcurator.ui.site;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.webcurator.domain.model.core.AuthorisingAgent;
import org.webcurator.domain.model.core.Site;
import org.webcurator.domain.model.core.UrlPattern;

/**
 * 
 * @author bbeaumont
 */
public class SiteEditorContext extends EditorContext {
	private Site site;
	private boolean editMode = false;
	private boolean editAnnotations = false;
	private boolean canEdit = false;
	
	public SiteEditorContext(Site aSite) {
		site = aSite;
		
		// Add all the objects to the editor context.
		putObject(site);
		putAllObjects(site.getAuthorisingAgents());
		putAllObjects(site.getUrlPatterns());
		putAllObjects(site.getPermissions());
	}
	
	public Site getSite() {
		return site;
	}
	
	public List<AuthorisingAgent> getSortedAuthAgents() {
		List<AuthorisingAgent> agents = new LinkedList<AuthorisingAgent>();
		agents.addAll(site.getAuthorisingAgents());
	    Collections.sort(agents, new AuthorisingAgent.AuthorisingAgentComparator());
	    return agents;
	}
	
	public List<UrlPattern> getSortedUrlPatterns() {
		List<UrlPattern> urlList = new LinkedList<UrlPattern>();
		urlList.addAll(site.getUrlPatterns());
		Collections.sort(urlList, new UrlPattern.UrlComparator());
		return urlList;
	}

	/**
	 * @return the editMode
	 */
	public boolean isEditMode() {
		return editMode;
	}

	/**
	 * @param editMode the editMode to set
	 */
	public void setEditMode(boolean editMode) {
		this.editMode = editMode;
	}

	/**
	 * @return the editAnnotations
	 */
	public boolean isEditAnnotations() {
		return editAnnotations;
	}

	/**
	 * @param editAnnotations the editAnnotations to set
	 */
	public void setEditAnnotations(boolean editAnnotations) {
		this.editAnnotations = editAnnotations;
	}
	

	/**
	 * @param canEdit the editMode to set
	 */
	public void setCanEdit(boolean canEdit) {
		this.canEdit = canEdit;
	}
	
	/**
	 * @return the canEdit
	 */
	public boolean isCanEdit() {
		return canEdit;
	}
	
}
