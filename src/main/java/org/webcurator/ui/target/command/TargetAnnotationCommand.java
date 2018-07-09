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
package org.webcurator.ui.target.command;

import java.util.Date;

/**
 * The command for adding annotations to a target.
 * @author bbeaumont
 */
public class TargetAnnotationCommand {
	
	public static final String ACTION_ADD_NOTE = "ADD_NOTE";
	public static final String ACTION_MODIFY_NOTE = "MODIFY_NOTE";
	public static final String ACTION_DELETE_NOTE = "DELETE_NOTE";
	
	public static final String PARAM_NOTE = "note";
    public static final String PARAM_NOTE_INDEX = "noteIndex";
	public static final String PARAM_EVALUATION_NOTE = "evaluationNote";
	public static final String PARAM_SELECTION_NOTE = "selectionNote";
	public static final String PARAM_SELECTION_TYPE = "evaluationType";
	public static final String PARAM_SELECTION_DATE = "selectionDate";
	public static final String PARAM_HARVEST_TYPE = "harvestType";
    public static final String PARAM_CURRENT_USER = "username";
	
	public static final int CNST_MAX_NOTE_LENGTH = 1000;
	
	private String actionCmd = null;
	private String note = null;
	private int noteIndex = -1;
    private boolean alertable;
	private String username = null;
	
	/** The evaluation note */
	private String evaluationNote;
	/** A note describing why this target was selected */
	private String selectionNote;
	/** The date the target first moved to Nominated/Approved */
	private Date selectionDate;
	/** The type of the selection */
	private String selectionType;
	/** The type of the harvest */
	private String harvestType;

    
	
	public TargetAnnotationCommand()
	{
		super();
		username = org.webcurator.core.util.AuthUtil.getRemoteUser();
	}
	
	/**
	 * @return Returns the harvestType.
	 */
	public String getHarvestType() {
		return harvestType;
	}

	/**
	 * @param harvestType The harvestType to set.
	 */
	public void setHarvestType(String harvestType) {
		this.harvestType = harvestType;
	}

	/**
	 * @return Returns the note.
	 */
	public String getNote() {
		return note;
	}

	/**
	 * @param note The note to set.
	 */
	public void setNote(String note) {
		this.note = note;
	}

	/**
	 * @return Returns the note index.
	 */
	public int getNoteIndex() {
		return noteIndex;
	}

	/**
	 * @param noteIndex The note index to set.
	 */
	public void setNoteIndex(int noteIndex) {
		this.noteIndex = noteIndex;
	}

	/**
	 * @return Returns the alertable option.
	 */
	public boolean isAlertable() {
		return alertable;
	}

	/**
	 * @param alertable The alertable to set.
	 */
	public void setAlertable(boolean alertable) {
		this.alertable = alertable;
	}
	
	/**
	 * @return Returns the user name.
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param note The user name to set.
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @return Returns the actionCmd.
	 */
	public String getActionCmd() {
		return actionCmd;
	}

	/**
	 * @param actionCmd The actionCmd to set.
	 */
	public void setActionCmd(String actionCmd) {
		this.actionCmd = actionCmd;
	}
	
	public boolean isAction(String action) {
		return actionCmd != null && actionCmd.equals(action);
	}

	/**
	 * @return Returns the evaluationNote.
	 */
	public String getEvaluationNote() {
		return evaluationNote;
	}

	/**
	 * @param evaluationNote The evaluationNote to set.
	 */
	public void setEvaluationNote(String evaluationNote) {
		this.evaluationNote = evaluationNote;
	}

	/**
	 * @return Returns the selectionDate.
	 */
	public Date getSelectionDate() {
		return selectionDate;
	}

	/**
	 * @param selectionDate The selectionDate to set.
	 */
	public void setSelectionDate(Date selectionDate) {
		this.selectionDate = selectionDate;
	}

	/**
	 * @return Returns the selectionNote.
	 */
	public String getSelectionNote() {
		return selectionNote;
	}

	/**
	 * @param selectionNote The selectionNote to set.
	 */
	public void setSelectionNote(String selectionNote) {
		this.selectionNote = selectionNote;
	}

	/**
	 * @return Returns the selectionType.
	 */
	public String getSelectionType() {
		return selectionType;
	}

	/**
	 * @param selectionType The selectionType to set.
	 */
	public void setSelectionType(String selectionType) {
		this.selectionType = selectionType;
	}
	
}
