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
package org.webcurator.auth.ldap;

import java.util.Iterator;
import java.util.List;

import org.acegisecurity.GrantedAuthority;
import org.acegisecurity.GrantedAuthorityImpl;
import org.acegisecurity.ldap.LdapDataAccessException;
import org.acegisecurity.providers.ldap.LdapAuthoritiesPopulator;
import org.acegisecurity.userdetails.ldap.LdapUserDetails;
import org.webcurator.domain.UserRoleDAO;
import org.webcurator.domain.model.auth.RolePrivilege;

/**
 * If the user is authenticated by the LDAP repository then the WCTAuthoritiesPopulator
 * sets the granted authorities for the user from the WCT Database.
 * @author bprice
 */
public class WCTAuthoritiesPopulator implements LdapAuthoritiesPopulator {

    private UserRoleDAO auth;
    
    public WCTAuthoritiesPopulator() {
        super();
    }
    
    /**
     * Select the granted authorities for the sepcified user and return and 
     * array of the authorities found.
     * @param username the user name to get the authorities for
     * @return the list of granted authorities
     * @throws LdapDataAccessException thrown if there is an error
     */
    private GrantedAuthority[] getGrantedAuthorities(String username) throws LdapDataAccessException {
       
        List privileges = auth.getUserPrivileges(username);
        if (privileges != null) {
            int privSize = privileges.size();
            GrantedAuthority roles[] = new GrantedAuthority[privSize]; 
        
            int i=0;
            Iterator it = privileges.iterator();
            while (it.hasNext()) {
                RolePrivilege priv = (RolePrivilege) it.next();
                GrantedAuthority ga = new GrantedAuthorityImpl("ROLE_"+priv.getPrivilege());
                roles[i++] = ga;
            }
            
            return roles;
        }
        return new GrantedAuthority[0];
    }
    
    /** 
     * Spring config setter.
     * @param auth the data access object for authorisation data.
     */
    public void setAuthDAO(UserRoleDAO auth) {
        this.auth = auth;
    }

    /** @see LdapAuthoritiesPopulator#getGrantedAuthorities(LdapUserDetails) .*/
    public GrantedAuthority[] getGrantedAuthorities(LdapUserDetails userDetails) throws LdapDataAccessException {        
        return getGrantedAuthorities(userDetails.getUsername());
    }
}
