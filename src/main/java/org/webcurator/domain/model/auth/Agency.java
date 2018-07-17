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

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

/**
 * The Agency class identifies an agency within the
 * Web Curator Tool. An Agency contains a set of Users
 * that logically belong to the agency. 
 * @author bprice
 */
@Entity
@Table(name = "AGENCY")
@NamedQueries({@NamedQuery(name = "org.webcurator.domain.model.auth.Agency.getAllAgencies", query = "from Agency agc order by agc.name")})
public class Agency implements Serializable {
    /** Identifier for query to get all agencies in an ordered list */
    public static final String QRY_GET_ALL_AGENCIES = "org.webcurator.domain.model.auth.Agency.getAllAgencies";
    
    /** The serial version ID */
    private static final long serialVersionUID = -126650342213891294L;
    
    /** The database OID of the Agency */
    @Id
    @Column(name = "AGC_OID", nullable = false)
    @GeneratedValue(generator = "agencyGen", strategy = GenerationType.TABLE)
    @TableGenerator(name = "agencyGen", table = "ID_GENERATOR", pkColumnName = "IG_TYPE",
            valueColumnName = "IG_VALUE", pkColumnValue = "Agency")
    private Long oid;
    /** The name of the agency */
    @Column(name = "AGC_NAME", length = 80, unique = true, nullable = false)
    private String name;
    /** The address of the agency */
    @Column(name = "AGC_ADDRESS", length = 255, nullable = false)
    private String address;
    /** The phone number for the agency */
    @Column(name = "AGC_PHONE", length = 20, nullable = true)
    private String phone;
    /** The URL for the agency - such as their home page */
    @Column(name = "AGC_URL", length = 255, nullable = true)
    private String agencyURL;
    /** The URL to a logo that can be used in the Permission Request templates */
    @Column(name = "AGC_LOGO_URL", length = 255, nullable = true)
    private String agencyLogoURL;
    /** The email to use to contact the agency */
    @Column(name = "AGC_EMAIL", length = 80, nullable = true)
    private String email;
    /** The fax number to use to contact the agency */
    @Column(name = "AGC_FAX", length = 20, nullable = true)
    private String fax;
    /** The set of users in this agency */
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "agency", targetEntity = User.class)
    private Set<User> users;
    /** The set of roles in this agency */
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "agency", targetEntity = Role.class)
    private Set<Role> roles;

    /** Flag for displaying tasks on the intray screen */
    @Column(name = "AGC_SHOW_TASKS")
    private boolean showTasks = false;
    /** Default description used for creating targets */
    @Column(name = "AGC_DEFAULT_DESC_TYPE")
	private String defaultDescriptionType;

    
    /**
     * gets the name of the Agency.
     * This property can be used in
     * the permission request templates.  
     * @return the Agency Name
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
     */
    public Set<User> getUsers() {
        return users;
    }
    
    /**
     * Set the set of users belonging to this agency.
     * @param users The set of users belonging to this agency.
     */
    public void setUsers(Set<User> users) {
        this.users = users;
    }
    
    /**
     * gets the Agency Address details.
     * This property can be used in
     * the permission request templates.
     * @return the Agency Address
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
     */
    public Set<Role> getRoles() {
        return roles;
    }
    
    /**
     * Set the set of roles that belong to this agency.
     * @param aRoles The set of roles contained in this agency.
     */
    public void setRoles(Set<Role> aRoles) {
        this.roles = aRoles;
    }

    /**
     * Flag to show tasks in the in-tray, as some agencies do not use the intray.
     * @return true if tasks should be displayed in the intray, false otherwise.
     */
    public boolean getShowTasks() {
    	return showTasks;
    }

    public void setShowTasks(boolean value) {
    	this.showTasks = value;
    }

    /**
     * The default description type for this agency, used when creating targets
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
