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
package org.webcurator.ui.util;

import java.util.Iterator;
import java.util.List;

/**
 * Manages a set of tabs that make up an editor.
 * @author bbeaumont
 *
 */
public class TabConfig {
	/** The view that presents the tabs */
    private String viewName = null;
    /** The list of tabs to display */
	private List<Tab> tabs;

	/**
	 * Get the list of tabs.
	 * @return the list of tabs.
	 */
	public List<Tab> getTabs() {
		return tabs;
	}
	
	/**
	 * Set the list of tabs.
	 * @param tabs The list of tabs.
	 */
	public void setTabs(List<Tab> tabs) {
		this.tabs = tabs;
	}
	
	/**
	 * Get a tab by its title.
	 * @param title The title of the tab to retrieve.
	 * @return The tab; null if no tab has this title.
	 */
	public Tab getTabByTitle(String title) { 
		Iterator<Tab> t = tabs.iterator();
		while(t.hasNext()) {
			Tab tab = t.next();
			if( title.equals(tab.getTitle())) {
				return tab;
			}
		}
		return null;
	}
	
	/**
	 * Get a tab by its unique ID.
	 * @param id The ID of the tab.
	 * @return The tab; null if no tab has the specified ID.
	 */
	public Tab getTabByID(String id) { 
		Iterator<Tab> t = tabs.iterator();
		while(t.hasNext()) {
			Tab tab = t.next();
			if( tab.getPageId().equals(id)) {
				return tab;
			}
		}
		return null;
	}

    /**
     * Get the name of the view that displays this set of tabs.
     * @return Returns the viewName.
     */
    public String getViewName() {
        return viewName;
    }

    /**
     * Set the name of the view that displays this set of tabs.
     * @param viewName The viewName to set.
     */
    public void setViewName(String viewName) {
        this.viewName = viewName;
    }
	
	
}
