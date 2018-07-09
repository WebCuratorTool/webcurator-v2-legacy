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

/**
 * The <code>TabStatus</code> object represents the current status of 
 * the tabs which, in turn, affects how they are displayed. This object
 * tracks whether the tabs are enabled and which of the tabs is currently
 * selected.
 * 
 * @author bbeaumont
 *
 */
public class TabStatus {
	/** True if the tabs are enabled */
	private boolean enabled = true;
	/** The currently selected tab */
	private Tab currentTab = null;
	
	/**
	 * No-arg constructor.
	 */
	public TabStatus() {
	}
	
	/**
	 * Create a Tab Status object from the current tab. Defaults to 
	 * setting the tabs to enabled.
	 * @param currentTab The currently selected tab.
	 */
	public TabStatus(Tab currentTab) { 
		this(currentTab, true);
	}

	
	/**
	 * Create a Tab Status object from the current tab. 
	 * @param currentTab The currently selected tab.
	 * @param enabled True if the tabs should be enabled.
	 */	
	public TabStatus(Tab currentTab, boolean enabled) {
		this.currentTab = currentTab;
		this.enabled = enabled;
	}


	/**
	 * Checks if the tabs should be enabled 
	 * @return true if the tabs are enabled.
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * Sets if the tabs should be enabled 
	 * @param true to enable the tabs.
	 */	
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	/**
	 * Get the currently selected tab.
	 * @return the currently selected tab.
	 */
	public Tab getCurrentTab() {
		return currentTab;
	}

	/**
	 * Set the currently selected tab.
	 * @param currentTab the currently selected tab.
	 */
	public void setCurrentTab(Tab currentTab) {
		this.currentTab = currentTab;
	}
	
	
	
}
