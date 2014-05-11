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
 * Command for reporting-email.jsp<br>
 * <br>
 * Allows to reteive parameter needed for sending a report
 * via e-mail. See also {@link org.webcurator.ui.report.controller.ReportEmailController}
 * 
 * @author MDubos
 *
 */
public class ReportEmailCommand {

	private String actionCmd;
	private String format;
	private String recipient;
	private String subject;
	private String message;
	
	/** Get attachment's file format */
	public String getFormat() {
		return format;
	}
	
	/** Set attachment's file format */
	public void setFormat(String format) {
		this.format = format;
	}
	
	/** Get e-mail message */
	public String getMessage() {
		return message;
	}
	
	/** Set e-mail message */
	public void setMessage(String message) {
		this.message = message;
	}
	
	/** Get email receipient */
	public String getRecipient() {
		return recipient;
	}
	
	/** Set email recipient */
	public void setRecipient(String recipient) {
		this.recipient = recipient;
	}
	
	/** Get subject */
	public String getSubject() {
		return subject;
	}
	
	/** Set email subject */
	public void setSubject(String subject) {
		this.subject = subject;
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
	
	
}
