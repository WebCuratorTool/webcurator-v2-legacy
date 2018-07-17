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

import org.hibernate.annotations.Cascade;
import org.webcurator.domain.AgencyOwnable;

import javax.persistence.*;

/**
 * The Role class defines the relationship between users and privilege
 * within the WCT system. Users are assigned to roles, that in turn
 * have privileges. A user may belong to more than one role.
 * @author bprice
 */
@Entity
@Table(name = "WCTROLE")
@NamedQueries({@NamedQuery(name = "org.webcurator.domain.model.auth.Role.getRoles", query = "FROM Role rol order by rol.agency.name, rol.name"),
        @NamedQuery(name = "org.webcurator.domain.model.auth.Role.getAssociatedRolesByUser", query = "SELECT rol FROM Role rol, User usr WHERE usr.roles.oid = rol.oid AND usr.oid=? order by rol.name"),
        @NamedQuery(name = "org.webcurator.domain.model.auth.Role.getRolesByAgency", query = "SELECT rol FROM Role rol WHERE rol.agency.oid = ?")})
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
   @Id
   @Column(name = "ROL_OID", nullable = false)
   @GeneratedValue(generator = "roleGen", strategy = GenerationType.TABLE)
   @TableGenerator(name = "roleGen", table = "ID_GENERATOR", pkColumnName = "IG_TYPE",
           valueColumnName = "IG_VALUE", pkColumnValue = "Role")
    private Long oid;
   /** The name of the role */
   @Column(name = "ROL_NAME", length = 80, nullable = false)
   private String name;
   /** A descrption for the role */
   @Column(name = "ROL_DESCRIPTION", length = 255, nullable = true)
   private String description;
   /** The set of Users that hold this role */
   @ManyToMany(targetEntity = User.class)
   @Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE})
   @JoinTable(name = "USER_ROLE", joinColumns = {@JoinColumn(name = "URO_ROL_OID")}, foreignKey = @ForeignKey(name = "FK_USERROLE_TO_ROLE"),
           inverseJoinColumns = {@JoinColumn(name = "URO_USR_OID")}, inverseForeignKey = @ForeignKey(name = "FK_USERROLE_TO_USER"))
   private Set<User> users;
   /** The set of privileges that this role is made up from. */
   @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "role", targetEntity = RolePrivilege.class)
   private Set<RolePrivilege> rolePrivileges;
   /** The agency that this role belongs to */
   @ManyToOne(targetEntity = Agency.class)
   @JoinColumn(name = "ROL_AGENCY_OID", nullable = false, foreignKey = @ForeignKey(name = "FK_ROLE_AGENCY_OID"))
   private Agency agency;


   /**
    * gets the display description of the role
    * @return the role description
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
     */
    public Set<User> getUsers() {
        return users;
    }
    
    /**
     * Set the set of Users that have this role.
     * @param users The set of Users that have this role.
     */
    public void setUsers(Set<User> users) {
        this.users = users;
    }
    
    /**
     * get the primary key for the role
     * @return the role primary key
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
     */
    public Set<RolePrivilege> getRolePrivileges() {
        return rolePrivileges;
    }
    
    /**
     * Set the set of privileges that this role has access to.
     * @param rolePrivileges The set of privileges that this role has access to.
     */
    public void setRolePrivileges(Set<RolePrivilege> rolePrivileges) {
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
