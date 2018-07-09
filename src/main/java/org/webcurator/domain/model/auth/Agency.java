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
package org.webcurator.domain.model.auth;

import java.io.Serializable;
import java.util.Set;

/**
 * The Agency class identifies an agency within the
 * Web Curator Tool. An Agency contains a set of Users
 * that logically belong to the agency. 
 * @author bprice
 * @hibernate.class table="AGENCY" lazy="false"
 * @hibernate.query name="org.webcurator.domain.model.auth.Agency.getAllAgencies" query="from Agency agc order by agc.name"
 */
public class Agency implements Serializable {
    /** Identifier for query to get all agencies in an ordered list */
    public static final String QRY_GET_ALL_AGENCIES = "org.webcurator.domain.model.auth.Agency.getAllAgencies";
    
    /** The serial version ID */
    private static final long serialVersionUID = -126650342213891294L;
    
    /** The database OID of the Agency */
    private Long oid;
    /** The name of the agency */
    private String name;
    /** The address of the agency */
    private String address;
    /** The phone number for the agency */
    private String phone;
    /** The URL for the agency - such as their home page */
    private String agencyURL;
    /** The URL to a logo that can be used in the Permission Request templates */
    private String agencyLogoURL;
    /** The email to use to contact the agency */
    private String email;
    /** The fax number to use to contact the agency */
    private String fax;
    /** The set of users in this agency */
    private Set users;
    /** The set of roles in this agency */
    private Set roles;

    /** Flag for displaying tasks on the intray screen */
    private boolean showTasks = false;
    /** Default description used for creating targets */
	private String defaultDescriptionType;

    
    /**
     * gets the name of the Agency.
     * This property can be used in
     * the permission request templates.  
     * @return the Agency Name
     * @hibernate.property column="AGC_NAME" length="80" unique="true" not-null="true"
     */
    public String getName() {
        return name;
    }
    
    /**
     * Set the name of the agency.
     * @param agencyName The name of the agency.
     */
    public void setName(String agencyName) {
        this.name = agencyName;
    }
    
    /**
     * gets the primary key of the Agency
     * @return the Agency Oid
     * @hibernate.id column="AGC_OID" generator-class="org.hibernate.id.MultipleHiLoPerTableGenerator"
     * @hibernate.generator-param name="table" value="ID_GENERATOR"
     * @hibernate.generator-param name="primary_key_column" value="IG_TYPE"
     * @hibernate.generator-param name="value_column" value="IG_VALUE"
     * @hibernate.generator-param name="primary_key_value" value="Agency"
     */
    public Long getOid() {
        return oid;
    }
    
    /**
     * Set the database OID of the agency. Only to be used by Hibernate.
     * @param oid The OID of the agency. 
     */
    public void setOid(Long oid) {
        this.oid = oid;
    }
    
    /**
     * gets the Set of Users associated with this Agency 
     * @return a Set of User objects
     * @hibernate.set cascade="all-delete-orphan" lazy="true" inverse="true"
     * @hibernate.collection-key column="USR_AGC_OID" 
     * @hibernate.collection-one-to-many class="org.webcurator.domain.model.auth.User" 
     */
    public Set getUsers() {
        return users;
    }
    
    /**
     * Set the set of users belonging to this agency.
     * @param users The set of users belonging to this agency.
     */
    public void setUsers(Set users) {
        this.users = users;
    }
    
    /**
     * gets the Agency Address details.
     * This property can be used in
     * the permission request templates.
     * @return the Agency Address
     * @hibernate.property column="AGC_ADDRESS" length="255" not-null="true"
     */
    public String getAddress() {
        return address;
    }
    
    /**
     * Set the address details.
     * @param address The address of the agency.
     */
    public void setAddress(String address) {
        this.address = address;
    }
    
    /**
     * gets the URL that points to the Agency Logo on the web.
     * This property can be used in
     * the permission request templates.
     * @return the AgencyLogoURL
     * @hibernate.property column="AGC_LOGO_URL" length="255" not-null="false"
     */
    public String getAgencyLogoURL() {
        return agencyLogoURL;
    }
    
    /**
     * Set the URL that points to a logo for the agency. This logo can then be
     * used in the permission request templates.
     * @param agencyLogoURL A URL of a logo to use for the agency.
     */
    public void setAgencyLogoURL(String agencyLogoURL) {
        this.agencyLogoURL = agencyLogoURL;
    }
    
    /**
     * gets the URL for this agencies website. This property can be used in
     * the permission request templates.
     * @return the Agency URL
     * @hibernate.property column="AGC_URL" length="255" not-null="false"
     */
    public String getAgencyURL() {
        return agencyURL;
    }
    
    /**
     * Set the URL of the agency's website.
     * @param agencyURL The URL of the agency's website.
     */
    public void setAgencyURL(String agencyURL) {
        this.agencyURL = agencyURL;
    }
    
    /**
     * gets the generic email address for the agency
     * @return the email address
     * @hibernate.property column="AGC_EMAIL" length="80" not-null="false"
     */
    public String getEmail() {
        return email;
    }
    
    /**
     * Set the email address used for contacting the agency.
     * @param email The email address for contacting the agency.
     */
    public void setEmail(String email) {
        this.email = email;
    }
    
    /**
     * gets the main fax number for the agency
     * @return the agency fax number
     * @hibernate.property column="AGC_FAX" length="20" not-null="false"
     */
    public String getFax() {
        return fax;
    }
    
    /**
     * Set the main fax number for the agency.
     * @param fax The main fax number for the agency.
     */
    public void setFax(String fax) {
        this.fax = fax;
    }
    
    /**
     * gets the main phone number for the agency
     * @return the agency phone number
     * @hibernate.property column="AGC_PHONE" length="20" not-null="false"
     */
    public String getPhone() {
        return phone;
    }
    
    /**
     * Set the main phone number for the agency.
     * @param phone The main phone number for the agency.
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    /**
     * gets the Set of Roles that belong to this agency
     * @return Set of Roles
     * @hibernate.set lazy="true" inverse="true"
     * @hibernate.collection-key column="ROL_AGENCY_OID" 
     * @hibernate.collection-one-to-many class="org.webcurator.domain.model.auth.Role" 
     */
    public Set getRoles() {
        return roles;
    }
    
    /**
     * Set the set of roles that belong to this agency.
     * @param aRoles The set of roles contained in this agency.
     */
    public void setRoles(Set aRoles) {
        this.roles = aRoles;
    }

    /**
     * Flag to show tasks in the in-tray, as some agencies do not use the intray.
     * @return true if tasks should be displayed in the intray, false otherwise.
     * @hibernate.property column="AGC_SHOW_TASKS"
     */
    public boolean getShowTasks() {
    	return showTasks;
    }

    public void setShowTasks(boolean value) {
    	this.showTasks = value;
    }

    /**
     * The default description type for this agency, used when creating targets
    * @hibernate.property column="AGC_DEFAULT_DESC_TYPE"
     */
	public String getDefaultDescriptionType() {
		return defaultDescriptionType;
	}

    public void setDefaultDescriptionType(String descriptionType) {
		this.defaultDescriptionType = descriptionType;
		
	}

    
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((oid == null) ? 0 : oid.hashCode());
		return result;
	}
	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final Agency other = (Agency) obj;
		if (oid == null) {
			if (other.oid != null)
				return false;
		} else if (!oid.equals(other.oid))
			return false;
		return true;
	}

    
}
