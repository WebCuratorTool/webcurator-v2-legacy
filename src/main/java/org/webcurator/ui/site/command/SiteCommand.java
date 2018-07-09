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
package org.webcurator.ui.site.command;

import org.webcurator.domain.model.core.Site;

/**
 * Command object used for harvest autorisations.
 * @author bbeaumont
 */
public class SiteCommand {
	public static final int CNST_MAX_LEN_TITLE = 255;
	public static final int CNST_MAX_LEN_DESC = 4000;
	public static final int CNST_MAX_LEN_ORDERNO = 32;
	
	public static final String ACTION_ADD_NOTE = "ADD_NOTE";
	public static final String ACTION_MODIFY_NOTE = "MODIFY_NOTE";
	public static final String ACTION_DELETE_NOTE = "DELETE_NOTE";

	public static final String PARAM_NOTE = "annotation";
    public static final String PARAM_NOTE_INDEX = "annotationIndex";
    public static final String PARAM_CURRENT_USER = "username";
	
	private String description;
	private String libraryOrderNo;
	private boolean published;
	private boolean active;
	private String title;
	private String annotation;
	private int annotationIndex;
	private String notes; 
	private String username;
	private boolean editMode = false;	
	private String cmdAction;

	public SiteCommand()
	{
		super();
		username = org.webcurator.core.util.AuthUtil.getRemoteUser();
	}
	public static SiteCommand buildFromModel(Site model) {
		SiteCommand sc = new SiteCommand();
		
		sc.setTitle(model.getTitle());
		sc.setDescription(model.getDescription());
		sc.setLibraryOrderNo(model.getLibraryOrderNo());
		sc.setNotes(model.getNotes());
		sc.setPublished(model.isPublished());
		sc.setActive(model.isActive());
		
		return sc;
	}
	
	public void updateBusinessModel(Site site) {
		site.setTitle(this.title);
		site.setDescription(this.description);
		site.setLibraryOrderNo(this.libraryOrderNo);
		site.setNotes(this.notes);
		site.setPublished(this.published);
		site.setActive(this.active);
	}
	
	
	public String getAnnotation() {
		return annotation;
	}

	public void setAnnotation(String annotation) {
		this.annotation = annotation;
	}

	public int getAnnotationIndex() {
		return annotationIndex;
	}

	public void setAnnotationIndex(int annotationIndex) {
		this.annotationIndex = annotationIndex;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLibraryOrderNo() {
		return libraryOrderNo;
	}

	public void setLibraryOrderNo(String orderNumber) {
		this.libraryOrderNo = orderNumber;
	}

	public boolean isPublished() {
		return published;
	}

	public void setPublished(boolean published) {
		this.published = published;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	/**
	 * @return Returns the notes.
	 */
	public String getNotes() {
		return notes;
	}

	/**
	 * @param notes The notes to set.
	 */
	public void setNotes(String notes) {
		this.notes = notes;
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
	 * @return the cmdAction
	 */
	public String getCmdAction() {
		return cmdAction;
	}

	/**
	 * @param cmdAction the cmdAction to set
	 */
	public void setCmdAction(String cmdAction) {
		this.cmdAction = cmdAction;
	}
	
	public boolean isAction(String action) {
		return cmdAction != null && cmdAction.equals(action);
	}

	/**
	 * @return the active
	 */
	public boolean isActive() {
		return active;
	}

	/**
	 * @param active the active to set
	 */
	public void setActive(boolean active) {
		this.active = active;
	}
}
