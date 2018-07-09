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
package org.webcurator.domain.model.dto;

import org.webcurator.core.notification.MessageType;
import org.webcurator.domain.model.auth.User;

/**
 * UserDTO contains all the simple User attributes of a User without
 * loading all the other relationships with a User
 * @author bprice
 */
public class UserDTO implements Comparable {
    
	/** The OID of the user */
    private Long oid;
    /** The login username of the user */
    private String username;
    /** The user's email address */
    private String email;
    /** True if the user wishes to be notified by email */
    private boolean notificationsByEmail;
    /** True if the user wishes to be notified by email */
    private boolean tasksByEmail;
    /** The user's title (Mr., Mrs., Ms., etc ). */
    private String title;
    /** The user's first name */
    private String firstname;
    /** The user's last name */
    private String lastname;
    /** The user's phone number */
    private String phone;
    /** The user's address */
    private String address;
    /** True if the user's account is active */
    private boolean active;
    /** The name of the agency the user belongs to */
    private String agencyName;
    /** Enable notifications for changes to objects the user owns */
    private boolean notifyOnGeneral = true;
    /** Enable notifications for harvester warnings. */
    private boolean notifyOnHarvestWarnings = false;
    
    /**
     * No-arg constructor.
     */
    public UserDTO() {
        
    }
 
    /**
     * Constructor for Hibernate queries.
     * @param oid				The OID of the user.	
     * @param username			The login username of the user.
     * @param email				The email address of the user.
     * @param notifyByEmail		True if the user should be notified by email of system events.
     * @param title				The title of the user (Mr., Mrs., etc.).
     * @param firstname			The first name of the user.
     * @param lastname			The user's last name.
     * @param phone				The user's phone number.
     * @param address			The user's address.
     * @param active			True if the user is active.
     * @param agencyName		The name of the agency this user belongs to.
     * @param notifyOnWarnings  True if we should notify this user of harvester warnings.
     * @param notifyOnChagnes   True if we should notify this user of changes to objects they own.
     */
    public UserDTO(Long oid, String username, String email, boolean notificationsByEmail, boolean tasksByEmail, String title, String firstname, String lastname, String phone, String address, boolean active, String agencyName, boolean notifyOnWarnings, boolean notifyOnChanges) {
        this.oid =oid;
        this.username = username;
        this.email = email;
        this.notificationsByEmail = notificationsByEmail;
        this.tasksByEmail = tasksByEmail;
        this.title = title;
        this.firstname = firstname;
        this.lastname = lastname;
        this.phone = phone;
        this.address = address;
        this.active = active;
        this.agencyName = agencyName;
        this.notifyOnHarvestWarnings = notifyOnWarnings;
        this.notifyOnGeneral = notifyOnChanges;
    }
    
    /**
     * Construct a user DTO from a User object.
     * @param aUser the user object to create the DTO from.
     */
    public UserDTO(User aUser) {
        this.oid = aUser.getOid();
        this.username = aUser.getUsername();
        this.email = aUser.getEmail();
        this.notificationsByEmail = aUser.isNotificationsByEmail();
        this.tasksByEmail = aUser.isTasksByEmail();
        this.title = aUser.getTitle();
        this.firstname = aUser.getFirstname();
        this.lastname = aUser.getLastname();
        this.phone = aUser.getPhone();
        this.address = aUser.getAddress();
        this.active = aUser.isActive();
        this.agencyName = aUser.getAgency().getName();
        this.notifyOnHarvestWarnings = aUser.isNotifyOnHarvestWarnings();
        this.notifyOnGeneral = aUser.isNotifyOnGeneral();
    }    
    
    /**
     * Get the address of the user.
     * @return The address of the user.
     */
    public String getAddress() {
        return address;
    }
    
    /**
     * Set the address of the user 
     * @param address The user's address.
     */
    public void setAddress(String address) {
        this.address = address;
    }
    
    /**
     * Get the email address of the user.
     * @return The e-mail address of the user.
     */
    public String getEmail() {
        return email;
    }
    
    /**
     * Set the email address of the user.
     * @param email the email address of the user.
     */
    public void setEmail(String email) {
        this.email = email;
    }
    
    /**
     * Get the first name of the user.
     * @return The user's first name.
     */
    public String getFirstname() {
        return firstname;
    }
    
    /**
     * Set the user's first name.
     * @param firstname The user's first name.
     */
    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }
    
    /**
     * Get the user's last name.
     * @return The user's last name.
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
     * Get the user's display name. For example, "John Smith" becomes
     * "J. Smith".
     * @return The user's display name.
     */
    public String getNiceName() {
    	return firstname.substring(0, 1) + ". " + lastname;
    }
    
    /**
     * Check whether the user is to be notified of system events by email as 
     * well as through the WCT in tray.
     * @return true to notify by e-mail; otherwise false.
     */
    public boolean isNotificationsByEmail() {
        return notificationsByEmail;
    }
    
    /**
     * Set whether the user is to be notified of system events by email as 
     * well as through the WCT in tray.
     * @param notificationsByEmail true to notify by e-mail; otherwise false.
     */

    public void setNotificationsByEmail(boolean notificationsByEmail) {
        this.notificationsByEmail = notificationsByEmail;
    }
    
    /**
     * Get the database OID of the user.
     * @return The database OID of the user.
     */
    public Long getOid() {
        return oid;
    }
    
    /**
     * Set the database OID of the user.
     * @param oid The database OID of the user.
     */
    public void setOid(Long oid) {
        this.oid = oid;
    }
    
    /**
     * Get the user's phone number.
     * @return The user's phone number.
     */
    public String getPhone() {
        return phone;
    }
    
    /**
     * Set the user's phone number.
     * @param phone The user's phone number.
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    /**
     * Get the user's title such as Mr., Mrs., Ms., Dr., etc.
     * @return The user's title.
     */
    public String getTitle() {
        return title;
    }
    
    /**
     * Set the user's title.
     * @param title The user's title.
     */
    public void setTitle(String title) {
        this.title = title;
    }
    
    /**
     * Get the user's login name. 
     * @return The user's login name.
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
     * Check whether the user account is active.
     * @return true if active; otherwise false.
     */
    public boolean getActive() {
        return active;
    }

    /**
     * Sets whether the user account is active.
     * @param active true if the user is active; otherwise false.
     */
    public void setActive(boolean active) {
        this.active = active;
    }
    
    /**
     * Get the name of the agency the user belongs to.
     * @return The name of the usre to which the user belongs.
     */
    public String getAgencyName() {
        return agencyName;
    }

    /**
     * Set the name of the agency the user belongs to.
     * @param agencyName The name of the agency the user belongs to.
     */
    public void setAgencyName(String agencyName) {
        this.agencyName = agencyName;
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
		final UserDTO other = (UserDTO) obj;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		return true;
	}

	/**
	 * Comparable implementation for user DTO objects.
	 * @see java.lang.Comparable#compareTo(Object) 
	 */
	public int compareTo(Object other) {
		if( other instanceof UserDTO ) { 
            int comparison = agencyName.compareTo(((UserDTO)other).agencyName);
            if (comparison == 0) {
              return username.compareToIgnoreCase(((UserDTO)other).username);  
            } else {
              return comparison;
            }
		}
		else {
			throw new ClassCastException("Can only compare UserDTOs with other UserDTOs: other object was of type " + other.getClass().getName());
		}
	}

	public boolean isTasksByEmail() {
		return tasksByEmail;
	}

	public void setTasksByEmail(boolean tasksByEmail) {
		this.tasksByEmail = tasksByEmail;
	}

	public boolean isNotifyOnGeneral() {
		return notifyOnGeneral;
	}

	public void setNotifyOnGeneral(boolean notifyOnChanges) {
		this.notifyOnGeneral = notifyOnChanges;
	}

	public boolean isNotifyOnHarvestWarnings() {
		return notifyOnHarvestWarnings;
	}

	public void setNotifyOnHarvestWarnings(boolean notifyOnHarvestWarnings) {
		this.notifyOnHarvestWarnings = notifyOnHarvestWarnings;
	}
	
	public boolean shouldSendNotification(int notificationCategory) {
		return 
			notificationCategory == MessageType.CATEGORY_HARVESTER_WARNING && isNotifyOnHarvestWarnings() ||
			notificationCategory == MessageType.CATEGORY_MISC && isNotifyOnGeneral();
	}
}
