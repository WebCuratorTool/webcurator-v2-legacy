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
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

/**
 * The User object holds the information related to a logged in User.
 * @author bprice
 * @hibernate.class table="WCTUSER" lazy="false"
 * @hibernate.query name="org.webcurator.domain.model.auth.User.getUserByName" query="SELECT usr FROM User usr WHERE usr.username=? "
 * @hibernate.query name="org.webcurator.domain.model.auth.User.getUsersByAgency" query="SELECT usr FROM User usr WHERE usr.agency.oid=? ORDER BY usr.username"
 * @hibernate.query name="org.webcurator.domain.model.auth.User.getAllUserDTOs" query="SELECT new org.webcurator.domain.model.dto.UserDTO(usr.oid, usr.username, usr.email, usr.notificationsByEmail, usr.tasksByEmail, usr.title, usr.firstname, usr.lastname, usr.phone, usr.address, usr.active, usr.agency.name, usr.notifyOnHarvestWarnings, usr.notifyOnGeneral) FROM User usr ORDER BY usr.agency.name, usr.username"
 * @hibernate.query name="org.webcurator.domain.model.auth.User.getAllUserDTOsByAgency" query="SELECT new org.webcurator.domain.model.dto.UserDTO(usr.oid, usr.username, usr.email, usr.notificationsByEmail, usr.tasksByEmail, usr.title, usr.firstname, usr.lastname, usr.phone, usr.address, usr.active, usr.agency.name, usr.notifyOnHarvestWarnings, usr.notifyOnGeneral) FROM User usr WHERE usr.agency.oid=? ORDER BY usr.username"
 * @hibernate.query name="org.webcurator.domain.model.auth.User.getUserDTOsByPrivilege" query="SELECT DISTINCT new org.webcurator.domain.model.dto.UserDTO(usr.oid, usr.username, usr.email, usr.notificationsByEmail, usr.tasksByEmail, usr.title, usr.firstname, usr.lastname, usr.phone, usr.address, usr.active, usr.agency.name, usr.notifyOnHarvestWarnings, usr.notifyOnGeneral) FROM Role rol, User usr WHERE usr.roles.oid = rol.oid AND rol.rolePrivileges.privilege=?"
 * @hibernate.query name="org.webcurator.domain.model.auth.User.getUserDTOByOid" query="SELECT new org.webcurator.domain.model.dto.UserDTO(usr.oid, usr.username, usr.email, usr.notificationsByEmail, usr.tasksByEmail, usr.title, usr.firstname, usr.lastname, usr.phone, usr.address, usr.active, usr.agency.name, usr.notifyOnHarvestWarnings, usr.notifyOnGeneral) FROM User usr WHERE usr.oid=?"
 * @hibernate.query name="org.webcurator.domain.model.auth.User.getUserDTOsByPrivAgency" query="SELECT DISTINCT new org.webcurator.domain.model.dto.UserDTO(usr.oid, usr.username, usr.email, usr.notificationsByEmail, usr.tasksByEmail, usr.title, usr.firstname, usr.lastname, usr.phone, usr.address, usr.active, usr.agency.name, usr.notifyOnHarvestWarnings, usr.notifyOnGeneral) FROM Role rol, User usr WHERE usr.roles.oid = rol.oid AND rol.rolePrivileges.privilege=? AND usr.agency.oid=?"
 * @hibernate.query name="org.webcurator.domain.model.auth.User.getUserDTOsByTargetPermission" query="SELECT DISTINCT new org.webcurator.domain.model.dto.UserDTO(usr.oid, usr.username, usr.email, usr.notificationsByEmail, usr.tasksByEmail, usr.title, usr.firstname, usr.lastname, usr.phone, usr.address, usr.active, usr.agency.name, usr.notifyOnHarvestWarnings, usr.notifyOnGeneral) FROM User usr, Seed s WHERE s.target.owner = usr AND s.permissions.oid = ?" 
 */
public class User implements Serializable {
    
	/** Version ID for serialization */
    private static final long serialVersionUID = -9216399037972509157L;

    /** Query key for retrieving a User object by username */
    public static final String QRY_GET_USER_BY_NAME = "org.webcurator.domain.model.auth.User.getUserByName";
    /** Query key for retrieving User objects by agency OID */
    public static final String QRY_GET_USERS_BY_AGENCY = "org.webcurator.domain.model.auth.User.getUsersByAgency";
    /** Query key for retrieving all User DTO objects */
    public static final String QRY_GET_ALL_USER_DTOS = "org.webcurator.domain.model.auth.User.getAllUserDTOs";
    /** Query key for retrieving all User DTO objects for a given agency OID. */
    public static final String QRY_GET_ALL_USER_DTOS_BY_AGENCY = "org.webcurator.domain.model.auth.User.getAllUserDTOsByAgency";
    /** Query key for retrieving all User DTO object for users with a specified Privilege */
    public static final String QRY_GET_USER_DTOS_BY_PRIVILEGE = "org.webcurator.domain.model.auth.User.getUserDTOsByPrivilege";
    /** Query key for retrieving a User DTO object by OID */
    public static final String QRY_GET_USER_DTO_BY_OID = "org.webcurator.domain.model.auth.User.getUserDTOByOid";
    /** Query key for retrieving all User DTO objects for a given agency and privilege. */
    public static final String QRY_GET_USER_DTOS_BY_PRIVILEGE_FOR_AGENCY = "org.webcurator.domain.model.auth.User.getUserDTOsByPrivAgency";
    /** Query key for retrieving a distinct list of User DTO objects for users with targets associated to the given permission. */
    public static final String QRY_GET_USER_DTOS_BY_TARGET_PERMISSION = "org.webcurator.domain.model.auth.User.getUserDTOsByTargetPermission";
    
    /** The database OID of the User object */
    private Long oid;
    /** The login username */
    private String username;
    /** The contact email address */
    private String email;
    /** True to enable notifications to be sent by e-mail as well as to the intray */
    private boolean notificationsByEmail;
    /** True to enable tasks to be sent by e-mail as well as to the intray */
    private boolean tasksByEmail;    
    /** The title of the user: Mr., Mrs., etc */
    private String title;
    /** The first name of the user */
    private String firstname;
    /** The last name of the user */
    private String lastname;
    /** True if the user account is active */
    private boolean active;
    /** True if the user must change their password on the next login */
    private boolean forcePasswordChange;
    /** True if the user authentication should use an external authentication source such as LDAP */
    private boolean externalAuth;
    /** The user's password */
    private String password;
    /** The user's phone number */
    private String phone;
    /** The user's address */
    private String address;
    /** The set of roles the user belongs to */
    private Set roles;
    /** The agency the user belongs to */
    private Agency agency;
    /** For inactive users, the date the user was deactivated */
    private Date deactivateDate;
    
    /** Enable notifications for changes to objects the user owns */
    private boolean notifyOnGeneral = true;
    /** Enable notifications for harvester warnings. */
    private boolean notifyOnHarvestWarnings = false;
    
    /**
     * checks if this account is actually active
     * @return true if the account is still active
     * @hibernate.property column="USR_ACTIVE" not-null="true"
     */
    public boolean isActive() {
        return active;
    }
    
    /**
     * Sets if this account is active.
     * @param active true to set the account active; false to set it inactive.
     */
    public void setActive(boolean active) {
        this.active = active;
    }
    
    /**
     * Gets the Address of this user, as it may be different to the
     * agency address that they belong to
     * @return the Address of this user
     * @hibernate.property column="USR_ADDRESS" not-null="false" length="200"
     */
    public String getAddress() {
        return address;
    }
    
    /**
     * Set the address of the user.
     * @param address The user's address.
     */
    public void setAddress(String address) {
        this.address = address;
    }
    
    /**
     * Gets the email address of the user.
     * @return the email address
     * @hibernate.property column="USR_EMAIL" not-null="true" length="100" 
     */
    public String getEmail() {
        return email;
    }
    
    /**
     * Set the email address of the user.
     * @param email The user's email address.
     */
    public void setEmail(String email) {
        this.email = email;
    }
    
    /**
     * checks if the User is defined as being Authenticated Externally. A User that
     * is Authenticated externally must have their password credentials stored in an
     * external repository, like a Directory server. Refer to the wct-security.xml for
     * configuration parameters for external authentication sources.
     * @return true if this user is authenticated externally
     * @hibernate.property column="USR_EXTERNAL_AUTH" not-null="true"
     */
    public boolean isExternalAuth() {
        return externalAuth;
    }
    
    /**
     * Sets whether to use the internal or an extrernal authentication source.
     * See the wct-security.xml file for configuration parameters for the 
     * external authentication sources.
     * @param externalAuth True to enable external authentication; false to use
     * 					   the internal database authentication.
     */
    public void setExternalAuth(boolean externalAuth) {
        this.externalAuth = externalAuth;
    }
    
    /**
     * gets the users firstname
     * @return the users firstname
     * @hibernate.property column="USR_FIRSTNAME" not-null="true" length="50"
     */
    public String getFirstname() {
        return firstname;
    }
    
    /**
     * Set the first name of the user.
     * @param firstname The user's first name.
     */
    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }
    
    /**
     * checks to see if a user is forced to change their password at logon
     * @return true if they must change password
     * @hibernate.property column="USR_FORCE_PWD_CHANGE" not-null="true"
     */
    public boolean isForcePasswordChange() {
        return forcePasswordChange;
    }
    
    /**
     * Force the user to change their password on their next logon.
     * @param forcePasswordChange true to force a password change on logon.
     */
    public void setForcePasswordChange(boolean forcePasswordChange) {
        this.forcePasswordChange = forcePasswordChange;
    }
    
    /**
     * gets the users lastname
     * @return the users lastname
     * @hibernate.property column="USR_LASTNAME" not-null="true" length="50"
     */
    public String getLastname() {
        return lastname;
    }
    
    /**
     * Set the user's last name.
     * @param lastname The user's last name.
     */
    public void setLastname(String lastname) {
        this.lastname = lastname;
    }
    
    /**
     * Get a shortened name for display purposes. For example, John Smith will
     * be shortened to "J. Smith".
     * @return A display name for the user. 
     */
    public String getNiceName() {
    	return firstname.substring(0, 1) + ". " + lastname;
    }
    
    /**
     * Get full name for display purposes. For example, John Smith 
     * @return A full name for the user. 
     */
    public String getFullName() {
    	String name = "";
    	if (firstname != null && !firstname.trim().equals("")) {
    		name = firstname;
    	}
    	else {
    		name = title;
    	}
    	
    	name = name + " " + lastname;
    	
    	return name;
    }
    
    /**
     * checks if this user is notified by email for events in the system.
     * @return true if the user is notified via email
     * @hibernate.property column="USR_NOTIFICATIONS_BY_EMAIL" not-null="true"
     */
    public boolean isNotificationsByEmail() {
        return notificationsByEmail;
    }
    
    /**
     * Set whether the user wishes to be notified of system events by e-mail in
     * addition to receiving the notifications in the WCT in tray.
     * @param notifyByEmail true to enable notification by e-mail.
     */
    public void setNotificationsByEmail(boolean notifyByEmail) {
        this.notificationsByEmail = notifyByEmail;
    }
    
    /**
     * obtains the users password as a one-way hash for comparision
     * @return the password as a one-way hash
     * @hibernate.property column="USR_PASSWORD" not-null="false" length="255"
     */
    public String getPassword() {
        return password;
    }
    
    /**
     * Set the password.
     * @param password The user's password
     */
    public void setPassword(String password) {
        this.password = password;
    }
    
    /**
     * gets a users direct phone number, this may differ from the agency phone number
     * @return the users phone number
     * @hibernate.property column="USR_PHONE" not-null="false" length="16"
     */
    public String getPhone() {
        return phone;
    }
    
    /**
     * Sets the user's phone number.
     * @param phone The user's phone number.
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    /**
     * gets the users title, e.g Mr, Mrs, Ms
     * @return the users title
     * @hibernate.property column="USR_TITLE" not-null="false" length="10"
     */
    public String getTitle() {
        return title;
    }
    
    /**
     * Set the user's title.
     * @param title The user's title (e.g. Mr., Mrs., Ms., etc).
     */
    public void setTitle(String title) {
        this.title = title;
    }
    
    /**
     * gets the username of the logged in user. This must be unique across the system
     * @return the username
     * @hibernate.property column="USR_USERNAME" unique="true" length="80" not-null="true"
     */
    public String getUsername() {
        return username;
    }
    
    
    /**
     * Set the user's login name.
     * @param username The user's login name.
     */
    public void setUsername(String username) {
        this.username = username;
    }
    
    /**
     * get the primary key oid for a user
     * @return the User Oid
     * @hibernate.id column="USR_OID" generator-class="org.hibernate.id.MultipleHiLoPerTableGenerator"
     * @hibernate.generator-param name="table" value="ID_GENERATOR"
     * @hibernate.generator-param name="primary_key_column" value="IG_TYPE"
     * @hibernate.generator-param name="value_column" value="IG_VALUE"
     * @hibernate.generator-param name="primary_key_value" value="User"
     * @hibernate.generator-param name="max-lo" value="16"
     */
    public Long getOid() {
        return oid;
    }
    
    /**
     * Hibernate method to set the OID of the User.
     * @param oid The new database OID.
     */
    public void setOid(Long oid) {
        this.oid = oid;
    }
    
    /**
     * gets the Set of Roles that this user has
     * @return Set of User Roles
     * @hibernate.set table="USER_ROLE" cascade="save-update" lazy="false"
     * @hibernate.collection-key column="URO_USR_OID" 
     * @hibernate.collection-many-to-many class="org.webcurator.domain.model.auth.Role" column="URO_ROL_OID" foreign-key="FK_USERROLE_TO_ROLE"  
     */
    public Set getRoles() {
        return roles;
    }
    
    /**
     * Set the roles that the user belongs to.
     * @param aRoles The roles that the user belongs to.
     */
    public void setRoles(Set aRoles) {
        this.roles = aRoles;
    }
    
    /**
     * gets the Agency this user belongs to
     * @hibernate.many-to-one not-null="true" class="org.webcurator.domain.model.auth.Agency" column="USR_AGC_OID" foreign-key="FK_USER_AGENCY_OID"
     */
    public Agency getAgency() {
        return agency;
    }
    
    /**
     * Set the agency to which this user belongs.
     * @param agency The agency to which this user belongs.
     */
    public void setAgency(Agency agency) {
        this.agency = agency;
    }
    
    /**
     * gets the Date the user was deactivated, this is null if the user is currently active
     * @hibernate.property type="timestamp" 
     * @hibernate.column name="USR_DEACTIVATE_DATE" not-null="false" sql-type="TIMESTAMP(9)"
     */
    public Date getDeactivateDate() {
        return deactivateDate;
    }
    
    /**
     * Set the date the user was deactivated.
     * @param deactivateDate The date the user was deactivated.
     */
    public void setDeactivateDate(Date deactivateDate) {
        this.deactivateDate = deactivateDate;
    }
    
    /**
     * Add the user to a role.
     * @param newRole The role to add the user to.
     */
    @SuppressWarnings("unchecked")
    public void addRole(Role newRole) {
        newRole.getUsers().add(this);
        getRoles().add(newRole);
        
    }

    /**
     * Remove the user from all roles.
     */
    public void removeAllRoles() {
        Iterator it = roles.iterator();
        while (it.hasNext()) {
            Role role = (Role) it.next();
            role.removeUser(this);
            it.remove();
        }
    }
    
    /**
     * removes a single role from the list of roles
     * this user has.
     * @param targetRole the role you wish to remove
     */
    public void removeRole(Role targetRole) {
        if (roles.contains(targetRole)) {
            roles.remove(targetRole);
        }
    }
    
    /**
     * Obtains a printable list of Roles that this user has.
     * @return a comma seperated list of role names as a String
     */
    public String displayRoles() {
        Iterator it = this.getRoles().iterator();
        StringBuffer sb = new StringBuffer();
        String delim = "";
        while (it.hasNext()) {
            sb.append(delim);
            sb.append(((Role)it.next()).getName());
            delim = ",";
        }
        return sb.toString();
    }
    
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((username == null) ? 0 : username.hashCode());
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
		final User other = (User) obj;		
		
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		
		return true;
	}

	/**
	 * Checks if we should send tasks by email.
	 * @hibernate.property column="USR_TASKS_BY_EMAIL" not-null="true" 
	 */
	public boolean isTasksByEmail() {
		return tasksByEmail;
	}

	public void setTasksByEmail(boolean tasksByEmail) {
		this.tasksByEmail = tasksByEmail;
	}

	/**
	 * Checks if we should notifications for Harvester Warnings to this user.
	 * @hibernate.property column="USR_NOTIFY_ON_GENERAL" not-null="true" 
	 */		
	public boolean isNotifyOnGeneral() {
		return notifyOnGeneral;
	}

	public void setNotifyOnGeneral(boolean notifyOnChanges) {
		this.notifyOnGeneral = notifyOnChanges;
	}

	/**
	 * Checks if we should notifications for Harvester Warnings to this user.
	 * @hibernate.property column="USR_NOTIFY_ON_WARNINGS" not-null="true" 
	 */	
	public boolean isNotifyOnHarvestWarnings() {
		return notifyOnHarvestWarnings;
	}

	public void setNotifyOnHarvestWarnings(boolean notifyOnHarvestWarnings) {
		this.notifyOnHarvestWarnings = notifyOnHarvestWarnings;
	}
    
    
}
