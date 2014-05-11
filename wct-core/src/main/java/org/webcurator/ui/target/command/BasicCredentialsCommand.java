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

import org.webcurator.domain.model.core.ProfileBasicCredentials;
import org.webcurator.domain.model.core.ProfileCredentials;

/**
 * The command for the basic credentials view.
 * @author bbeaumont
 */
public class BasicCredentialsCommand {
	public static final String ACTION_NEW  = "new";
	public static final String ACTION_EDIT = "edit";
	public static final String ACTION_SAVE = "save";
	public static final String ACTION_CANCEL = "cancel";

	private String credentialsDomain;
	private String realm;
	private String username;
	private String password;
	
	private Integer listIndex;
	
	private String actionCmd;

	public static BasicCredentialsCommand fromModel(List<ProfileCredentials> allCreds, Integer index) {
		ProfileBasicCredentials model = (ProfileBasicCredentials) allCreds.get(index);
		
		BasicCredentialsCommand me = new BasicCredentialsCommand();
		me.credentialsDomain = model.getCredentialsDomain();
		me.realm = model.getRealm();
		me.username = model.getUsername();
		me.password = model.getPassword();
		me.listIndex = index;
		
		return me;
	}
	
	public ProfileBasicCredentials toModelObject() {
		ProfileBasicCredentials creds = new ProfileBasicCredentials();
		creds.setCredentialsDomain(credentialsDomain);
		creds.setRealm(realm);
		creds.setUsername(username);
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
	 * @return Returns the realm.
	 */
	public String getRealm() {
		return realm;
	}

	/**
	 * @param realm The realm to set.
	 */
	public void setRealm(String realm) {
		this.realm = realm;
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
	
	
	
}
