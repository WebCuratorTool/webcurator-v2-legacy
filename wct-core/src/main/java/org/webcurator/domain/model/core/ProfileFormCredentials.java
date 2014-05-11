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
package org.webcurator.domain.model.core;

import javax.management.AttributeNotFoundException;
import javax.management.InvalidAttributeValueException;

import org.webcurator.core.profiles.DuplicateNameException;
import org.webcurator.core.profiles.HeritrixProfile;

/**
 * Represents Heritrix form-based credentials.
 * 
 * @author bbeaumont
 * @hibernate.joined-subclass table="PROFILE_FORM_CREDENTIALS"
 * @hibernate.joined-subclass-key column="PRC_PC_OID" 
 *
 */
public class ProfileFormCredentials extends ProfileCredentials {
	/** Constant for POST requests */
	public static final String METHOD_POST = "POST";
	/** Constant for GET request */
	public static final String METHOD_GET  = "GET";
	
	/** The URL of the login form */
	private String loginUri      = null;
	/** The HTTP method to use - POST or GET */
	private String httpMethod    = null;
	/** The name of the username field */
	private String usernameField = null;
	/** The name of the password field */
	private String passwordField = null;
	
	/**
	 * Returns the HTTP method to be used for submitting the login form.
	 * @return Returns the httpMethod.
	 * @hibernate.property column="PFC_METHOD" length="4"
	 */
	public String getHttpMethod() {
		return httpMethod;
	}
	
	/**
	 * Sets the HTTP method to be used for submitting the login form.
	 * @param httpMethod The httpMethod to set.
	 */
	public void setHttpMethod(String httpMethod) {
		this.httpMethod = httpMethod;
	}
	
	/**
	 * Returns the URL of the login form.
	 * @return Returns the loginUri.
	 * @hibernate.property column="PFC_LOGIN_URI" length="255"
	 */
	public String getLoginUri() {
		return loginUri;
	}
	
	/**
	 * Sets the URL of the login form.
	 * @param loginUri The loginUri to set.
	 */
	public void setLoginUri(String loginUri) {
		this.loginUri = loginUri;
	}
	
	/**
	 * Gets the name of the password field on the login page.
	 * @return Returns the passwordField.
	 * @hibernate.property column="PFC_PASSWORD_FIELD" length="255"
	 */
	public String getPasswordField() {
		return passwordField;
	}
	
	/**
	 * Sets the name of the password field on the login page.
	 * @param passwordField The passwordField to set.
	 */
	public void setPasswordField(String passwordField) {
		this.passwordField = passwordField;
	}
	
	/**
	 * Gets the name of the username field on the login page.
	 * @return Returns the usernameField.
	 * @hibernate.property column="PFC_USERNAME_FIELD" length="255"
	 */
	public String getUsernameField() {
		return usernameField;
	}
	
	/**
	 * Sets the name of the username field on the login page.
	 * @param usernameField The usernameField to set.
	 */
	public void setUsernameField(String usernameField) {
		this.usernameField = usernameField;
	}
	
	/* (non-Javadoc)
	 * @see org.webcurator.domain.model.core.ProfileCredentials#addToProfile(org.webcurator.core.profiles.HeritrixProfile, java.lang.String)
	 */
	@Override
	public void addToProfile(HeritrixProfile profile, String aName) throws DuplicateNameException, AttributeNotFoundException, InvalidAttributeValueException, AttributeNotFoundException {
		profile.addMapElement(ProfileCredentials.ELEM_CREDENTIALS, aName, ProfileCredentials.TYPE_HTML_FORM_CREDENTIALS);
		
		profile.setSimpleType(ProfileCredentials.ELEM_CREDENTIALS + "/" + aName + "/credential-domain", getCredentialsDomain());
		profile.setSimpleType(ProfileCredentials.ELEM_CREDENTIALS + "/" + aName + "/login-uri", getLoginUri());
		profile.setSimpleType(ProfileCredentials.ELEM_CREDENTIALS + "/" + aName + "/http-method", getHttpMethod());
		
		profile.addSimpleMapElement(ProfileCredentials.ELEM_CREDENTIALS + "/" + aName + "/form-items", getUsernameField(), getUsername());
		profile.addSimpleMapElement(ProfileCredentials.ELEM_CREDENTIALS + "/" + aName + "/form-items", getPasswordField(), getPassword());
	}
	
	/* (non-Javadoc)
	 * @see org.webcurator.domain.model.core.ProfileCredentials#getTypeName()
	 */
	@Override
	public String getTypeName() {
		return "Form";
	}
	
	/* (non-Javadoc)
	 * @see org.webcurator.domain.model.core.ProfileCredentials#copy()
	 */
	@Override
	public ProfileCredentials copy() {
		ProfileFormCredentials copy = new ProfileFormCredentials();
		copy.httpMethod = httpMethod;
		copy.loginUri   = loginUri;
		copy.passwordField = passwordField;
		copy.usernameField = usernameField;
		copy.credentialsDomain = credentialsDomain;
		copy.password = password;
		copy.username = username;
		
		return copy;
	}
	
}
