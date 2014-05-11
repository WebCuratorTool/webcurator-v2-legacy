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

import org.springframework.validation.Validator;

/**
 * Represents a tab on the user interface and stores references to its
 * command class, validator, and handlers.
 * @author bbeaumont
 */
public class Tab {
	/** The unique identifier of this tab */
	private String pageId;
	/** The title of the tab */
	private String title;
	/** The JSP to render the tab */
	private String jsp;
	/** The command class for submitting this tab */
	private Class commandClass;
	/** The validator class for validating the submission */
	private Validator validator;
	/** The handler to process submission */
	private TabHandler tabHandler;
	/** The form encoding type to use (defaults to post) */
	private String formEncodingType = null;
		
	/**
	 * Get the unique page identifier. 
	 * @return The unique page identifier.
	 */
	public String getPageId() {
		return pageId;
	}
	
	/**
	 * Set the unique page identifier.
	 * @param pageId The unique page identifier.
	 */
	public void setPageId(String pageId) {
		this.pageId = pageId;
	}
	
	/**
	 * Get the JSP that renders this tab.
	 * @return the JSP that renders this tab.
	 */
	public String getJsp() {
		return jsp;
	}
	
	/**
	 * Set the JSP that renders this tab.
	 * @param jsp the JSP that renders this tab.
	 */
	public void setJsp(String jsp) {
		this.jsp = jsp;
	}
	
	/**
	 * Get the title of this tab.
	 * @return the title of this tab.
	 */
	public String getTitle() {
		return title;
	}
	
	/**
	 * Set the title of this tab.
	 * @param title the title of this tab.
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	
	/**
	 * Get the Spring command class for this tab.
	 * @return the Spring command class for this tab.
	 */
	public Class getCommandClass() {
		return commandClass;
	}
	
	/**
	 * Set the Spring command class for this tab.
	 * @param commandClass the Spring command class for this tab.
	 */
	public void setCommandClass(Class commandClass) {
		this.commandClass = commandClass;
	}
	
	/**
	 * Get the handler that processes this tab.
	 * @return the handler that processes this tab.
	 */
	public TabHandler getTabHandler() {
		return tabHandler;
	}
	
	/**
	 * Set the handler that processes this tab.
	 * @param tabHandler the handler that processes this tab.
	 */
	public void setTabHandler(TabHandler tabHandler) {
		this.tabHandler = tabHandler;
	}
	
	/**
	 * Get the validator for this tab.
	 * @return the validator for this tab.
	 */
	public Validator getValidator() {
		return validator;
	}
	
	/**
	 * Set the validator for this tab.
	 * @param validator the validator for this tab.
	 */
	public void setValidator(Validator validator) {
		this.validator = validator;
	}
	
	/**
	 * Create a sub tab.
	 * @deprecated This is no longer to be used in the WCT. See the 
	 * 			   AuthorisingAgency tab for how to manage sub sets of
	 * 			   functionality.
	 * @param jsp The JSP that the renders the subtab.
	 * @return A subtab.
	 */
	public Tab createSubTab(String jsp) {
		Tab sub = new Tab();
		sub.commandClass = this.commandClass;
		sub.jsp = jsp;
		sub.pageId = this.pageId;
		sub.tabHandler = this.tabHandler;
		sub.title = this.title;
		sub.validator = this.validator;
		
		return sub;
	}
	
	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return this.pageId + ": " + this.commandClass.getName() + ", " + this.jsp + ", " + this.title;
	}
	
	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object o) { 
		return o instanceof Tab &&
		       ((Tab)o).pageId.equals(pageId);
	}

	/**
	 * Get the encoding type to use for the form. If none is specified,
	 * this defaults to POST.
	 * @return The encoding type for the form.
	 */
	public String getFormEncodingType() {
		return formEncodingType;
	}

	/**
	 * Set the encoding type for the form. If none is specified, this
	 * defaults to POST.
	 * @param formEncodingType The encoding type for the form.
	 */
	public void setFormEncodingType(String formEncodingType) {
		this.formEncodingType = formEncodingType;
	}	
}
