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
package org.webcurator.ui.report.command;



/**
 * Command for reporting-save.jsp.<br>
 * <br>
 * Allows to get a format for the file attachemnt. See also
 * {@link org.webcurator.ui.report.controller.ReportSaveController} 
 * 
 * @author MDubos
 *
 */
public class ReportSaveCommand {

	private String format;
	private String actionCmd;

	/**
	 * Get format of file attachment
	 * @return Format
	 */
	public String getFormat() {
		return format;
	}

	/**
	 * Set file attachment
	 * @param format
	 */
	public void setFormat(String format) {
		this.format = format;
	}

	/**
	 * @return Returns the action.
	 */
	public String getActionCmd() {
		return actionCmd;
	}

	/**
	 * @param action The action to set.
	 */
	public void setActionCmd(String actionCmd) {
		this.actionCmd = actionCmd;
	}
	
}
