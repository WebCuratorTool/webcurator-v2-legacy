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
package org.webcurator.ui.groups.command;


/**
 * The command for adding annotations to a target group.
 * @author bbeaumont
 */
public class GroupAnnotationCommand {
	
	public static final String ACTION_ADD_NOTE = "ADD_NOTE";
	public static final String ACTION_MODIFY_NOTE = "MODIFY_NOTE";
	public static final String ACTION_DELETE_NOTE = "DELETE_NOTE";
	
	public static final String PARAM_NOTE = "note";
	public static final String PARAM_INDEX = "noteIndex";
	public static final String PARAM_CURRENT_USER = "username";
	
	public static final int CNST_MAX_NOTE_LENGTH = 1000;
	
	private String actionCmd = null;
	private String note = null;
	private int noteIndex = -1;
	private String username = null;
	
	public GroupAnnotationCommand()
	{
		super();
		username = org.webcurator.core.util.AuthUtil.getRemoteUser();
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
	 * @param oid The note index to set.
	 */
	public void setNoteIndex(int index) {
		noteIndex = index;
	}
	
	/**
	 * @return Returns the username.
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param actionCmd The actionCmd to set.
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
}
