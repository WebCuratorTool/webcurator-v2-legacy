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

import java.util.List;

import org.webcurator.domain.model.core.ProfileCredentials;
import org.webcurator.domain.model.core.ProfileFormCredentials;

/**
 * the command for the form credentials view.
 * @author bbeaumont
 */
public class FormCredentialsCommand {
	public static final String ACTION_NEW  = "new";
	public static final String ACTION_EDIT = "edit";
	public static final String ACTION_SAVE = "save";
	public static final String ACTION_CANCEL = "cancel";

	private String credentialsDomain;
	
	private String httpMethod;
	private String loginUri;
	private String usernameField;
	private String username;
	private String passwordField;	
	private String password;
	
	private Integer listIndex;
	
	private String actionCmd;

	public static FormCredentialsCommand fromModel(List<ProfileCredentials> allCreds, Integer index) {
		ProfileFormCredentials model = (ProfileFormCredentials) allCreds.get(index);
		
		FormCredentialsCommand me = new FormCredentialsCommand();
		me.credentialsDomain = model.getCredentialsDomain();
		me.loginUri = model.getLoginUri();
		me.httpMethod = model.getHttpMethod();
		me.usernameField = model.getUsernameField();
		me.username = model.getUsername();
		me.passwordField = model.getPasswordField();
		me.password = model.getPassword();
		me.listIndex = index;
		
		return me;
	}
	
	public ProfileFormCredentials toModelObject() {
		ProfileFormCredentials creds = new ProfileFormCredentials();
		creds.setCredentialsDomain(credentialsDomain);
		creds.setLoginUri(loginUri);
		creds.setHttpMethod(httpMethod);
		creds.setUsernameField(usernameField);
		creds.setUsername(username);
		creds.setPasswordField(passwordField);
		creds.setPassword(password);
		
		return creds;
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
	public void setActionCmd(String action) {
		this.actionCmd = action;
	}

	/**
	 * @return Returns the credentialsDomain.
	 */
	public String getCredentialsDomain() {
		return credentialsDomain;
	}

	/**
	 * @param credentialsDomain The credentialsDomain to set.
	 */
	public void setCredentialsDomain(String credentialsDomain) {
		this.credentialsDomain = credentialsDomain;
	}

	/**
	 * @return Returns the listIndex.
	 */
	public Integer getListIndex() {
		return listIndex;
	}

	/**
	 * @param listIndex The listIndex to set.
	 */
	public void setListIndex(Integer listIndex) {
		this.listIndex = listIndex;
	}

	/**
	 * @return Returns the password.
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password The password to set.
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return Returns the username.
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username The username to set.
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @return Returns the httpMethod.
	 */
	public String getHttpMethod() {
		return httpMethod;
	}

	/**
	 * @param httpMethod The httpMethod to set.
	 */
	public void setHttpMethod(String httpMethod) {
		this.httpMethod = httpMethod;
	}

	/**
	 * @return Returns the loginUri.
	 */
	public String getLoginUri() {
		return loginUri;
	}

	/**
	 * @param loginUri The loginUri to set.
	 */
	public void setLoginUri(String loginUri) {
		this.loginUri = loginUri;
	}

	/**
	 * @return Returns the passwordField.
	 */
	public String getPasswordField() {
		return passwordField;
	}

	/**
	 * @param passwordField The passwordField to set.
	 */
	public void setPasswordField(String passwordField) {
		this.passwordField = passwordField;
	}

	/**
	 * @return Returns the usernameField.
	 */
	public String getUsernameField() {
		return usernameField;
	}

	/**
	 * @param usernameField The usernameField to set.
	 */
	public void setUsernameField(String usernameField) {
		this.usernameField = usernameField;
	}
	
	
	
}
