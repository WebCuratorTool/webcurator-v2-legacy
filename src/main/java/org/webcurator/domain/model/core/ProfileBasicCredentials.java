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
 * Profile override credentials for HTTP BASIC authentication.
 * 
 * @author bbeaumont
 * @hibernate.joined-subclass table="PROFILE_BASIC_CREDENTIALS"
 * @hibernate.joined-subclass-key column="PBC_PC_OID"
 */
public class ProfileBasicCredentials extends ProfileCredentials {
	/** The realm to which the credentials apply */
	private String realm = null;

	/**
	 * Gets the realm the credentials apply to.
	 * @return Returns the realm.
	 * @hibernate.property column="PBC_REALM" length="255"
	 */
	public String getRealm() {
		return realm;
	}

	/**
	 * Sets the credentials realm.
	 * @param realm The realm to set.
	 */
	public void setRealm(String realm) {
		this.realm = realm;
	}

	/* (non-Javadoc)
	 * @see org.webcurator.domain.model.core.ProfileCredentials#addToProfile(org.webcurator.core.profiles.HeritrixProfile, java.lang.String)
	 */
	@Override
	public void addToProfile(HeritrixProfile profile, String aName) throws DuplicateNameException, AttributeNotFoundException, InvalidAttributeValueException, AttributeNotFoundException {
		profile.addMapElement(ProfileCredentials.ELEM_CREDENTIALS, aName, ProfileCredentials.TYPE_BASIC_CREDENTIALS);
		
		profile.setSimpleType(ProfileCredentials.ELEM_CREDENTIALS + "/" + aName + "/credential-domain", getCredentialsDomain());
		profile.setSimpleType(ProfileCredentials.ELEM_CREDENTIALS + "/" + aName + "/realm", getRealm());
		profile.setSimpleType(ProfileCredentials.ELEM_CREDENTIALS + "/" + aName + "/login", getUsername());
		profile.setSimpleType(ProfileCredentials.ELEM_CREDENTIALS + "/" + aName + "/password", getPassword());
	}
	
	/* (non-Javadoc)
	 * @see org.webcurator.domain.model.core.ProfileCredentials#getTypeName()
	 */
	@Override
	public String getTypeName() {
		return "Basic";
	}
	
	/* (non-Javadoc)
	 * @see org.webcurator.domain.model.core.ProfileCredentials#copy()
	 */
	@Override
	public ProfileCredentials copy() {
		ProfileBasicCredentials copy = new ProfileBasicCredentials();

		copy.realm = realm;
		copy.credentialsDomain = credentialsDomain;
		copy.password = password;
		copy.username = username;
		
		return copy;
	}	
}
