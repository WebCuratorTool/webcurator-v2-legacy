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
 * 
 * @author bbeaumont
 * @hibernate.class table="PROFILE_CREDENTIALS"
 */
public abstract class ProfileCredentials {
	
	/** The position in the Heritrix Profile where the credentials must be placed */
	public static final String ELEM_CREDENTIALS = "/crawl-order/credential-store/credentials";
	
	/** The Heritrix type for Form Credentials */
	public static final String TYPE_HTML_FORM_CREDENTIALS = "org.archive.crawler.datamodel.credential.HtmlFormCredential";
	/** The Heritrix type for Basic Credentials */
	public static final String TYPE_BASIC_CREDENTIALS = "org.archive.crawler.datamodel.credential.Rfc2617Credential";
	
	/** The domain to which these credentials apply. */
	protected String credentialsDomain = null;
	/** The username */
	protected String username          = null;
	/** The password */
	protected String password          = null;
	
	
	
	/** The unique database ID of the profile. */
	private Long oid;
	
    /**
     * Gets the database OID of the credentials object.
     * @return Returns the oid.
     * @hibernate.id column="PC_OID" generator-class="org.hibernate.id.MultipleHiLoPerTableGenerator"
     * @hibernate.generator-param name="table" value="ID_GENERATOR"
     * @hibernate.generator-param name="primary_key_column" value="IG_TYPE"
     * @hibernate.generator-param name="value_column" value="IG_VALUE"
     * @hibernate.generator-param name="primary_key_value" value="Profile Security Credential" 
     */	
	public Long getOid() {
		return oid;
	}
	
	/**
	 * Sets the database OID for the ProfileCredentials object.
	 * @param oid The oid to set.
	 */
	public void setOid(Long oid) {
		this.oid = oid;
	}		
	
	
	/**
	 * Gets the domain to which the credentials apply.
	 * @return Returns the credentialsDomain.
	 * @hibernate.property column="PC_DOMAIN" length="255"
	 */
	public String getCredentialsDomain() {
		return credentialsDomain;
	}
	
	/**
	 * Sets the domain to which the credentials apply.
	 * @param credentialsDomain The credentialsDomain to set.
	 */
	public void setCredentialsDomain(String credentialsDomain) {
		this.credentialsDomain = credentialsDomain;
	}
	/**
	 * Returns the password
	 * @return Returns the password.
	 * @hibernate.property column="PC_PASSWORD" length="255"
	 */
	public String getPassword() {
		return password;
	}
	/**
	 * Sets the password for the credentials.
	 * @param password The password to set.
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * Gets the username for the credentials.
	 * @return Returns the username.
	 * @hibernate.property column="PC_USERNAME" length="255"
	 */
	public String getUsername() {
		return username;
	}
	
	/**
	 * Sets the username for the credentials.
	 * @param username The username to set.
	 */
	public void setUsername(String username) {
		this.username = username;
	}
	
	/**
	 * Adds the credential to the realm.
	 * @param profile  The HeritrixProfile to add the credentials to.
	 * @param aName    The name to use for the credentials.
	 * @throws DuplicateNameException is the name is already used.
	 * @throws AttributeNotFoundException if the credentials element cannot be found.
	 */	
	public abstract void addToProfile(HeritrixProfile profile, String aName) throws DuplicateNameException, AttributeNotFoundException, InvalidAttributeValueException, AttributeNotFoundException;
	
	
	/**
	 * Gets the type name.
	 * @return the type of the credentials.
	 */
	public abstract String getTypeName();
	
	
	/**
	 * Creates a deep copy of the credentials object. 
	 * @return A new profile credentials object representing the same credentials.
	 */	
	public abstract ProfileCredentials copy();
}
