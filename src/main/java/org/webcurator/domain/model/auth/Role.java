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

import org.webcurator.domain.AgencyOwnable;

/**
 * The Role class defines the relationship between users and privilege
 * within the WCT system. Users are assigned to roles, that in turn
 * have privileges. A user may belong to more than one role.
 * @author bprice
 * @hibernate.class  table="WCTROLE" lazy="false" 
 * @hibernate.query name="org.webcurator.domain.model.auth.Role.getRoles" query="FROM Role rol order by rol.agency.name, rol.name"
 * @hibernate.query name="org.webcurator.domain.model.auth.Role.getAssociatedRolesByUser" query="SELECT rol FROM Role rol, User usr WHERE usr.roles.oid = rol.oid AND usr.oid=? order by rol.name"
 * @hibernate.query name="org.webcurator.domain.model.auth.Role.getRolesByAgency" query="SELECT rol FROM Role rol WHERE rol.agency.oid = ?"
 */
public class Role implements AgencyOwnable, Serializable{

   /** The query constant for retrieving an ordered list of roles */
   public final static String QRY_GET_ROLES = "org.webcurator.domain.model.auth.Role.getRoles";
   /** The query constant for retrieving an ordered lists of roles for a given user */
   public final static String QRY_GET_ASSOCIATED_ROLES_BY_USER = "org.webcurator.domain.model.auth.Role.getAssociatedRolesByUser";
   /** The query constant for retrieving an ordered list of roles for a given agency */
   public final static String QRY_GET_ROLES_BY_AGENCY ="org.webcurator.domain.model.auth.Role.getRolesByAgency";	
	
   /** The version ID for serialization */
   private static final long serialVersionUID = 3846098707837858936L;
   
   /** The database OID of the Role */
   private Long oid;
   /** The name of the role */
   private String name;
   /** A descrption for the role */
   private String description;
   /** The set of Users that hold this role */
   private Set users;
   /** The set of privileges that this role is made up from. */
   private Set rolePrivileges;
   /** The agency that this role belongs to */
   private Agency agency;


   /**
    * gets the display description of the role
    * @return the role description
    * @hibernate.property column="ROL_DESCRIPTION" length="255" not-null="false"
    */
   public String getDescription() {
	   return description;
   }
   
   /**
    * Sets the description of the role.
    * @param description The description of the role.
    */
    public void setDescription(String description) {
        this.description = description;
    }
    
    /**
     * gets the name of the Role
     * @return the Role name
     * @hibernate.property column="ROL_NAME" not-null="true" length="80"
     */
    public String getName() {
        return name;
    }
    
    /**
     * Set the name of the role.
     * @param name The name of the role.
     */
    public void setName(String name) {
        this.name = name;
    }
    
    
    /**
     * gets the Set of Users that have this role
     * @return a set of Users
     * @hibernate.set table="USER_ROLE" cascade="save-update" lazy="false" inverse="true"
     * @hibernate.collection-key column="URO_ROL_OID"
     * @hibernate.collection-many-to-many class="org.webcurator.domain.model.auth.User" column="URO_USR_OID" foreign-key="FK_USERROLE_TO_USER"
     */
    public Set getUsers() {
        return users;
    }
    
    /**
     * Set the set of Users that have this role.
     * @param users The set of Users that have this role.
     */
    public void setUsers(Set users) {
        this.users = users;
    }
    
    /**
     * get the primary key for the role
     * @return the role primary key
     * @hibernate.id column="ROL_OID" generator-class="org.hibernate.id.MultipleHiLoPerTableGenerator"
     * @hibernate.generator-param name="table" value="ID_GENERATOR"
     * @hibernate.generator-param name="primary_key_column" value="IG_TYPE"
     * @hibernate.generator-param name="value_column" value="IG_VALUE"
     * @hibernate.generator-param name="primary_key_value" value="Role"
     */
    public Long getOid() {
        return oid;
    }
    
    /**
     * Hibernate method to set the OID of this object.
     * @param oid The new OID of the object.
     */
    public void setOid(Long oid) {
        this.oid = oid;
    }
    
    /**
     * Get the set of privileges that this role has access to.
     * @return gets the set of privileges for this role
     * @hibernate.set cascade="all-delete-orphan" lazy="false" inverse="true"
     * @hibernate.collection-key column="PRV_ROLE_OID"
     * @hibernate.collection-one-to-many class="org.webcurator.domain.model.auth.RolePrivilege"
     */
    public Set getRolePrivileges() {
        return rolePrivileges;
    }
    
    /**
     * Set the set of privileges that this role has access to.
     * @param rolePrivileges The set of privileges that this role has access to.
     */
    public void setRolePrivileges(Set rolePrivileges) {
    	//Hibernate uses it's own collections, so this should NEVER be used in objects that
    	//you expect to be managed by Hibernate.  Use clear/addRolePrivileges instead.
    	this.rolePrivileges = rolePrivileges;
    }

	public void clearRolePrivileges() {
		if(this.rolePrivileges!=null) {
			this.rolePrivileges.clear();
		}
	}

	public void addRolePrivileges(Set<RolePrivilege> selectedPrivScopes) {
		if(this.rolePrivileges==null) {
			this.rolePrivileges = selectedPrivScopes;
		} else {
	    	for(Object p:selectedPrivScopes.toArray()) {
	    		RolePrivilege privilege = (RolePrivilege)p;
	    		privilege.setRole(this);
	    		this.rolePrivileges.add(privilege);
	    	}
		}
	}
    
    /**
     * gets the Agency associated with this role
     * @return the Agency
     * @hibernate.many-to-one not-null="true" class="org.webcurator.domain.model.auth.Agency" column="ROL_AGENCY_OID" foreign-key="FK_ROLE_AGENCY_OID"
     */
    public Agency getAgency() {
        return agency;
    }
    
    /**
     * Set the agency associated with this role.
     * @param agency The agency associated with this role.
     */
    public void setAgency(Agency agency) {
        this.agency = agency;
    }
       
    /**
     * Remove a User from this role.
     * @param user The user to remove.
     */
    public void removeUser(User user) {
        getUsers().remove(user);
    }
    
    /**
     * Get the agency that owns this role.
     * @return The agency that owns this role.
     */
    public Agency getOwningAgency() {
        return getAgency();
    }


}
