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
package org.webcurator.auth;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.webcurator.core.exceptions.NoPrivilegeException;
import org.webcurator.core.exceptions.WCTRuntimeException;
import org.webcurator.core.util.AuthUtil;
import org.webcurator.domain.AgencyOwnable;
import org.webcurator.domain.UserOwnable;
import org.webcurator.domain.model.auth.Privilege;
import org.webcurator.domain.model.auth.Role;
import org.webcurator.domain.model.auth.RolePrivilege;
import org.webcurator.domain.model.auth.User;

/**
 * The implementation of the AuthorityManager interface.
 * @author bprice
 */
public class AuthorityManagerImpl implements AuthorityManager {

    private Log log = null;
    
    public AuthorityManagerImpl() {
        log = LogFactory.getLog(AuthorityManagerImpl.class);
    }

    public boolean hasPrivilege(String privilege, int scope) {
        User user = AuthUtil.getRemoteUserObject();
        return hasPrivilege(user,privilege,scope);
    }
    
    public boolean hasPrivilege(User user, String privilege, int scope) {
        HashMap<String,RolePrivilege> privs = getPrivilegesForUser(user);
        
        RolePrivilege testRolePriv = privs.get(privilege);
        if (testRolePriv != null) {
          return testRolePriv.getPrivilegeScope() <= scope;
        }
        return false;
    }
    
    public int getPrivilegeScope(String privilege) throws NoPrivilegeException {
        User user = AuthUtil.getRemoteUserObject();
        return getPrivilegeScope(user,privilege);
    }
    
    public int getPrivilegeScope(User user, String privilege) throws NoPrivilegeException {
        if (user == null) {
            throw new WCTRuntimeException("User was null");
        }
        HashMap<String,RolePrivilege> privs = getPrivilegesForUser(user);
        RolePrivilege testRolePriv = privs.get(privilege);
        if (testRolePriv == null) {
            throw new NoPrivilegeException("The user "+user.getUsername()+" does not have the privilege "+privilege);
        }
        return testRolePriv.getPrivilegeScope();
    }
    
    public int getPrivilegeScopeNE(String privilege) {
        User user = AuthUtil.getRemoteUserObject();
        return getPrivilegeScopeNE(user,privilege);
    } 
    
    public int getPrivilegeScopeNE(User user, String privilege) {
        if (user == null) {
            throw new WCTRuntimeException("User was null");
        }
        HashMap<String,RolePrivilege> privs = getPrivilegesForUser(user);
        RolePrivilege testRolePriv = privs.get(privilege);
        if (testRolePriv == null) {
            return Privilege.SCOPE_NONE;
        }
        return testRolePriv.getPrivilegeScope();
    }    
    
    public HashMap<String,RolePrivilege> getPrivilegesForUser(User user) {
        
        HashMap<String,RolePrivilege> allRolePrivileges = new HashMap<String,RolePrivilege>();
        Set roles = user.getRoles();
        if (roles != null) {
            Iterator it = roles.iterator();
            while (it.hasNext()) {             
                Role role = (Role) it.next();
                // log.debug("User "+user.getUsername()+" has a Role of "+role.getName());
                Set rolePrivileges = role.getRolePrivileges();
                if (rolePrivileges != null) {
                    // log.debug("RolePrivilege list for Role is "+rolePrivileges.toString());
                    Iterator it2 = rolePrivileges.iterator();
                    while (it2.hasNext()) {
                        RolePrivilege rp = (RolePrivilege) it2.next();
                        String key = rp.getPrivilege();
                        
                        if (allRolePrivileges.containsKey(key)) {
                            //work out which privilege has the broadest scope
                            RolePrivilege currentRP = allRolePrivileges.get(key);
                            //log.debug("found a possibly conflicting RolePrivilege scope between "+rp.toString()+" and "+currentRP.toString());
                            allRolePrivileges.put(key, findBroadestScope(currentRP, rp));
                        } else {
                            allRolePrivileges.put(key, rp); 
                        }   
                    }
                }
            }
        }
        // log.debug("User "+user.getUsername()+" has the following complete set of Privileges: "+allRolePrivileges.toString());
        return allRolePrivileges;
    }
    

    private RolePrivilege findBroadestScope(RolePrivilege rp1, RolePrivilege rp2) {
        int rp1scope = rp1.getPrivilegeScope();
        int rp2scope = rp2.getPrivilegeScope();
        
        return rp1scope <= rp2scope?rp1:rp2;
    }
    
    public boolean hasPrivilege(AgencyOwnable object, String privilege) {
        User user = AuthUtil.getRemoteUserObject();
        return hasPrivilege(user,object, privilege);
    }


    public boolean hasPrivilege(User user, AgencyOwnable object, String privilege) {
        HashMap<String,RolePrivilege> userPrivs = getPrivilegesForUser(user);
        
        RolePrivilege rp = userPrivs.get(privilege);
        if (rp != null) {
            int scope = rp.getPrivilegeScope();
            switch (scope) {
            case Privilege.SCOPE_ALL:
                    return true;
            case Privilege.SCOPE_AGENCY:
                    return object.getOwningAgency().equals(user.getAgency());
            }
        }
        return false;
    }

    public boolean hasPrivilege(User user, UserOwnable object, String privilege) {
        HashMap<String,RolePrivilege> userPrivs = getPrivilegesForUser(user);
        
        RolePrivilege rp = userPrivs.get(privilege);
        if (rp != null) {
            int scope = rp.getPrivilegeScope();
            switch (scope) {
            case Privilege.SCOPE_ALL:
                return true;
            case Privilege.SCOPE_AGENCY:
                return object.getOwningUser().getAgency().equals(user.getAgency());
            case Privilege.SCOPE_OWNER:
                return object.getOwningUser().equals(user);
            }
        }
        return false;
    }

    public boolean hasPrivilege(UserOwnable object, String privilege) {
        User user = AuthUtil.getRemoteUserObject();
        return hasPrivilege(user,object,privilege);
    }

    public boolean hasAtLeastOnePrivilege(String[] privileges) {
        User user = AuthUtil.getRemoteUserObject();
        HashMap userPrivs = getPrivilegesForUser(user);
        for (int i=0; i < privileges.length; i++) {
            boolean found = userPrivs.containsKey(privileges[i]);
            if (found == true) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * checks if the user has one of the specified privileges
     * against the specified object.
     * @param privileges a String array of Privilege codes to look for
     * @param subject   The subject to check permissions against.
     * @return true if the User has at least one privilege in the set
     */
    public boolean hasAtLeastOnePrivilege(AgencyOwnable subject, String... privileges) {
        for (int i=0; i < privileges.length; i++) {
            if(hasPrivilege(subject, privileges[i])) {
            	return true;
            }
        }
        return false;    	
    }
    
    /**
     * checks if the user has one of the specified privileges
     * against the specified object.
     * @param privileges a String array of Privilege codes to look for
     * @param subject   The subject to check permissions against.
     * @return true if the User has at least one privilege in the set
     */
    public boolean hasAtLeastOnePrivilege(UserOwnable subject, String... privileges) {
        for (int i=0; i < privileges.length; i++) {
            if(hasPrivilege(subject, privileges[i])) {
            	return true;
            }
        }
        return false;    	
    }
    
    /**
     * Retrieves the profile level for the currently logged in user.
     * @return The maximum level that the logged in user has access to.
     */
    public int getProfileLevel() {
    	int level = 0;
    	
    	if(hasPrivilege(Privilege.SET_HARVEST_PROFILE_LV1, Privilege.SCOPE_NONE)) {
    		level = 1;
    	}
    	
    	if(hasPrivilege(Privilege.SET_HARVEST_PROFILE_LV2, Privilege.SCOPE_NONE)) {
    		level = 2;
    	}
    	
    	if(hasPrivilege(Privilege.SET_HARVEST_PROFILE_LV3, Privilege.SCOPE_NONE)) {
    		level = 3;
    	}
    	
    	return level;
    }

}
