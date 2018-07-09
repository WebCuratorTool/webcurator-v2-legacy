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

/**
 * The RolePrivilege class holds all the system privileges that allow
 * access to WCT functions. Privileges are assigned to users via the
 * users role.
 * 
 * A RolePrivilege is a mapping from Role to Privilege with an attached
 * scope that defines how widely the privilege can be used. 
 * 
 * All privilege strings are defined in the Privilege class.
 * 
 * @see org.webcurator.domain.model.auth.Privilege
 * 
 * @author bprice
 * @hibernate.class table="ROLE_PRIVILEGE" lazy="true" 
 * @hibernate.query name="org.webcurator.domain.model.auth.RolePrivilege.getUserPrivileges" query="SELECT distinct rolpriv FROM RolePrivilege rolpriv WHERE rolpriv.role.users.username=? "
 */
public class RolePrivilege implements Serializable {

	/** Version ID for serialization */
    private static final long serialVersionUID = 8223039237243696910L;

    /** Query to get the privileges for a particular user */
    public static final String QRY_GET_USER_PRIVILEGES = "org.webcurator.domain.model.auth.RolePrivilege.getUserPrivileges";

    /** The database OID for the Role Privilege. */
    private Long oid;
    /** The identifier of the privilege */
    private String privilege;
    /** The scope of the privilege - i.e. how widely the privilege applies */
    private int privilegeScope;
    /** The role that this privilege belongs to. */
    private Role role;
    
    /**
     * get the privilege name
     * @return the name of the privilege
     * @hibernate.property column="PRV_CODE" not-null="true" length="40"
     */
    public String getPrivilege() {
        return privilege;
    }
    
    /**
     * Set the name of the privilege.
     * @param privilege The name of the privilege.
     */
    public void setPrivilege(String privilege) {
        this.privilege = privilege;
    }
    
    /**
     * gets the Role that this privilege belongs to
     * @return the Role object
     * @hibernate.many-to-one class="org.webcurator.domain.model.auth.Role" column="PRV_ROLE_OID" foreign-key="FK_PRIV_ROLE_OID" 
     */
    public Role getRole() {
        return role;
    }
    
    /**
     * Set the role the privilege belongs to.
     * @param role The role the privilege belongs to.
     */
    public void setRole(Role role) {
        this.role = role;
    }
    
    /**
     * gets the Scope of this privilege
     * @return the scope
     * @hibernate.property column="PRV_SCOPE" not-null="true"
     */
    public int getPrivilegeScope() {
        return privilegeScope;
    }
    
    /**
     * Set the scope of this privilege.
     * @param privilegeScope The scope.
     */
    public void setPrivilegeScope(int privilegeScope) {
        this.privilegeScope = privilegeScope;
    }
    
    /**
     * gets the Primary key for the Privilege
     * @return the Oid
     * @hibernate.id column="PRV_OID" generator-class="org.hibernate.id.MultipleHiLoPerTableGenerator"
     * @hibernate.generator-param name="table" value="ID_GENERATOR"
     * @hibernate.generator-param name="primary_key_column" value="IG_TYPE"
     * @hibernate.generator-param name="value_column" value="IG_VALUE"
     * @hibernate.generator-param name="primary_key_value" value="RolePriv"
     */
    public Long getOid() {
        return oid;
    }

    /**
     * Hibernate method to set the OID of the RolePrivilege.
     * @param oid The new database OID.
     */
    public void setOid(Long oid) {
        this.oid = oid;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        RolePrivilege rolePriv = (RolePrivilege) obj;
        return this.toString().equals(rolePriv.toString());
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return this.getPrivilege()+":"+this.getPrivilegeScope();
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override 
    public int hashCode() {
    	//WARNING this doesn't consider the role property - likely a bug, but unsure how it's used so cannot
    	//change this without possibly breaking other things
        return toString().hashCode();
    }
}
