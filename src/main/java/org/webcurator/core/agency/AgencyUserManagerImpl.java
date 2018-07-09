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
package org.webcurator.core.agency;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.webcurator.auth.AuthorityManager;
import org.webcurator.core.exceptions.NoPrivilegeException;
import org.webcurator.core.profiles.ProfileManager;
import org.webcurator.core.util.Auditor;
import org.webcurator.core.util.AuthUtil;
import org.webcurator.domain.FlagDAO;
import org.webcurator.domain.IndicatorCriteriaDAO;
import org.webcurator.domain.RejReasonDAO;
import org.webcurator.domain.UserOwnable;
import org.webcurator.domain.UserRoleDAO;
import org.webcurator.domain.RejReasonDAO;
import org.webcurator.domain.model.auth.Agency;
import org.webcurator.domain.model.auth.Privilege;
import org.webcurator.domain.model.auth.Role;
import org.webcurator.domain.model.auth.User;
import org.webcurator.domain.model.core.Flag;
import org.webcurator.domain.model.core.IndicatorCriteria;
import org.webcurator.domain.model.core.RejReason;
import org.webcurator.domain.model.dto.UserDTO;

/**
 * Implementation of the AgencyUserManager interface.
 * @see AgencyUserManager
 * @author bprice
 */
public class AgencyUserManagerImpl implements AgencyUserManager{
	private static Log log = LogFactory.getLog(AgencyUserManagerImpl.class);

    private Auditor auditor = null;
    
    private AuthorityManager authorityManager = null;
    
    private UserRoleDAO userRoleDAO = null;
    
    private RejReasonDAO rejReasonDAO = null;
    
    private IndicatorCriteriaDAO indicatorCriteriaDAO = null;
    
    private FlagDAO flagDAO = null;

    /** The ProfileManager to allow creating a default profile. */
    private ProfileManager profileManager = null;

    public List getAgencies() {
        return userRoleDAO.getAgencies();
    }

    public Agency getAgencyByOid(Long oid) {
        return userRoleDAO.getAgencyByOid(oid);
    }

    public List getUserDTOs() {
        return userRoleDAO.getUserDTOs();
    }

    public List getUserDTOs(Long agencyOid) {
        return userRoleDAO.getUserDTOs(agencyOid);
    }

    public UserDTO getUserDTOByOid(Long userOid) {
        return userRoleDAO.getUserDTOByOid(userOid);
    }

    public List getUsers() {
        return userRoleDAO.getUsers();
    }

    public List getUsers(Long agencyOid) {
        return userRoleDAO.getUsers(agencyOid);
    }

    public void setAuditor(Auditor auditor) {
        this.auditor = auditor;
    }

    public void setAuthorityManager(AuthorityManager authorityManager) {
        this.authorityManager = authorityManager;
    }

    public void setUserRoleDAO(UserRoleDAO userRoleDAO) {
        this.userRoleDAO = userRoleDAO;
    }

    public void setRejReasonDAO(RejReasonDAO rejReasonDAO) {
        this.rejReasonDAO = rejReasonDAO;
    }

    public List getAgenciesForLoggedInUser() {
        User loggedInUser = AuthUtil.getRemoteUserObject();
        Agency usersAgency = loggedInUser.getAgency();
        
        if (authorityManager.hasPrivilege(Privilege.MANAGE_AGENCIES, Privilege.SCOPE_ALL)) {
            return userRoleDAO.getAgencies();
        } else {
            Long agencyOid = usersAgency.getOid();
            List<Agency> singleAgency = new ArrayList<Agency>();
            singleAgency.add(userRoleDAO.getAgencyByOid(agencyOid));
            return singleAgency;
        }
    }
    
    public List getAgenciesForTemplatePriv() {
         User loggedInUser = AuthUtil.getRemoteUserObject();
         Agency usersAgency = loggedInUser.getAgency();
         
         if (authorityManager.hasPrivilege(Privilege.PERMISSION_REQUEST_TEMPLATE, Privilege.SCOPE_ALL)) {
             return userRoleDAO.getAgencies();
         } else {
             Long agencyOid = usersAgency.getOid();
             List<Agency> singleAgency = new ArrayList<Agency>();
             singleAgency.add(userRoleDAO.getAgencyByOid(agencyOid));
             return singleAgency;
         }
    }
    

    public void updateAgency(Agency agency, boolean update) {
        userRoleDAO.saveOrUpdate(agency);
        if (update == true) {
            auditor.audit(Agency.class.getName(),agency.getOid(),Auditor.ACTION_UPDATE_AGENCY,"The Agency named '"+agency.getName()+"' has been updated");
        } else {
            auditor.audit(Agency.class.getName(),Auditor.ACTION_NEW_AGENCY,"A new Agency has been created with a name of '"+agency.getName()+"'");
            profileManager.createDefaultProfile(agency);
        }
    }

    public List getUserDTOsForLoggedInUser() {
        User loggedInUser = AuthUtil.getRemoteUserObject();
        Agency usersAgency = loggedInUser.getAgency();
        
        if (authorityManager.hasPrivilege(Privilege.MANAGE_USERS, Privilege.SCOPE_ALL)) {
            return userRoleDAO.getUserDTOs();
        } else {
            return userRoleDAO.getUserDTOs(usersAgency.getOid());
        }
    }

    public User getUserByOid(Long userOid) {
        return userRoleDAO.getUserByOid(userOid);
    }

    public User getUserByUserName(String username) {
        return userRoleDAO.getUserByName(username);
    }
    
    public void modifyUserStatus(User user) {
        boolean currentStatus = user.isActive();
        boolean newStatus = !currentStatus;
        user.setActive(newStatus);
        if (newStatus == false) {
            //record the deactivation date
            user.setDeactivateDate(new Date());
        } else {
            //re-activate user and clear deactivation date
            user.setDeactivateDate(null);
        }
        userRoleDAO.saveOrUpdate(user);
        if (newStatus == true) {
            auditor.audit(User.class.getName(),user.getOid(),Auditor.ACTION_ACTIVATE_USER,"User "+user.getUsername()+" was enabled");
        } else {
            auditor.audit(User.class.getName(),user.getOid(),Auditor.ACTION_DEACTIVATE_USER,"User "+user.getUsername()+" was deactivated");
        }
        
    }

    public void deleteUser(User user) {
        Long userOid = user.getOid();
        String username = user.getUsername();
        user.removeAllRoles();
        userRoleDAO.delete(user);
        auditor.audit(User.class.getName(),userOid, Auditor.ACTION_DELETE_USER,"User "+username+" has been deleted");
 
    }

    public List getRolesForLoggedInUser() {
        User loggedInUser = AuthUtil.getRemoteUserObject();
        Agency usersAgency = loggedInUser.getAgency();
        
        if (authorityManager.hasPrivilege(Privilege.MANAGE_ROLES, Privilege.SCOPE_ALL)) {
            return userRoleDAO.getRoles();
        } else {
            return userRoleDAO.getRoles(usersAgency.getOid());
        }
    }

    public Role getRoleByOid(Long oid) {
        return userRoleDAO.getRoleByOid(oid);
    }

    public void updateRole(Role role, boolean update) {
        userRoleDAO.saveOrUpdate(role);
        if (update == true) {
            auditor.audit(Role.class.getName(), role.getOid(),Auditor.ACTION_UPDATE_ROLE,"The role '"+role.getName()+"' has been updated under the '"+role.getAgency().getName()+"' Agency.");
        } else {
            auditor.audit(Role.class.getName(), role.getOid(),Auditor.ACTION_NEW_ROLE,"A New role has been created with a role name of '"+role.getName()+"' under the '"+role.getAgency().getName()+"' Agency.");
        } 
    }

    public void deleteRole(Role role) {
        Long roleOid = role.getOid();
        String roleName = role.getName();
        userRoleDAO.delete(role);
        auditor.audit(Role.class.getName(), roleOid ,Auditor.ACTION_DELETE_ROLE,"The role name of '"+roleName+"' has been deleted.");
    }

    public List getAssociatedRolesForUser(Long oid) {
        return userRoleDAO.getAssociatedRolesForUser(oid);
    }

    public void updateUserRoles(User user) {
        userRoleDAO.saveOrUpdate(user);
        auditor.audit(User.class.getName(), user.getOid(),Auditor.ACTION_ASSOCIATE_ROLES,"User "+user.getUsername()+" roles have been modified to include the following roles "+user.displayRoles());
        
    }

    public List getRolesForUser(User user) {
        Agency usersAgency = user.getAgency();
        return userRoleDAO.getRoles(usersAgency.getOid());
    }

    // rejection reason related methods..
    public List getRejReasonsForLoggedInUser() {
        User loggedInUser = AuthUtil.getRemoteUserObject();
        Agency usersAgency = loggedInUser.getAgency();
        
        if (authorityManager.hasPrivilege(Privilege.MANAGE_REASONS, Privilege.SCOPE_ALL)) {
            return rejReasonDAO.getRejReasons();
        } else {
            return rejReasonDAO.getRejReasons(usersAgency.getOid());
        }
    }

    @SuppressWarnings("unchecked")
	public List getValidRejReasonsForTargets(Long agencyOid) {
    	
    	List reasons = rejReasonDAO.getRejReasons(agencyOid);
    	List<RejReason> targetReasons = new ArrayList<RejReason>();
    	
    	RejReason rr = null;
    	Iterator<RejReason> it = reasons.iterator();
    	while (it.hasNext()) {
			rr = it.next();							
			if (rr.isAvailableForTargets()) {
		    	targetReasons.add(rr);
			}
		} 
    	return targetReasons;
    }
    
    @SuppressWarnings("unchecked")
	public List getValidRejReasonsForTIs(Long agencyOid) {
    	
    	List reasons = rejReasonDAO.getRejReasons(agencyOid);
    	List<RejReason> targetInstanceReasons = new ArrayList<RejReason>();
    	
    	RejReason rr = null;
    	Iterator<RejReason> it = reasons.iterator();
    	while (it.hasNext()) {
			rr = it.next();							
			if (rr.isAvailableForTIs()) {
				targetInstanceReasons.add(rr);
			}
		} 
    	return targetInstanceReasons;
    }

    public void deleteRejReason(RejReason reason) {
        Long reasonOid = reason.getOid();
        String name = reason.getName();
        rejReasonDAO.delete(reason);
        auditor.audit(RejReason.class.getName(), reasonOid ,Auditor.ACTION_DELETE_REASON,"The rejection: '"+name+"' has been deleted.");
    }

    public RejReason getRejReasonByOid(Long oid) {
        return rejReasonDAO.getRejReasonByOid(oid);
    }
 
    public void updateRejReason(RejReason reason, boolean update) {
        rejReasonDAO.saveOrUpdate(reason);
        if (update == true) {
            auditor.audit(RejReason.class.getName(),reason.getOid(),Auditor.ACTION_UPDATE_REASON,"The rejection reason: '"+reason.getName()+"' has been updated");
        } else {
            auditor.audit(RejReason.class.getName(),Auditor.ACTION_NEW_REASON,"A new rejection reason has been created with a name of '"+reason.getName()+"'");
        }
    }
    
    public void updateUser(User user, boolean update) {
        userRoleDAO.saveOrUpdate(user);
        if (update == true) {
            auditor.audit(User.class.getName(),user.getOid(),Auditor.ACTION_UPDATE_USER,"The User named '"+user.getUsername()+"' has been updated");
        } else {
            auditor.audit(User.class.getName(),user.getOid(),Auditor.ACTION_NEW_USER,"New WCT User created with username of "+user.getUsername());
        }
    }
    
    public boolean canGiveTo(UserOwnable subject, User newOwner) {
    	
    	if(authorityManager.hasPrivilege(subject, Privilege.TAKE_OWNERSHIP)) {
    		User loggedInUser = AuthUtil.getRemoteUserObject();
    		int scope = authorityManager.getPrivilegeScopeNE(Privilege.GIVE_OWNERSHIP);
    		
    		switch(scope) {
    		case Privilege.SCOPE_AGENCY:
    			return newOwner.getAgency().equals(loggedInUser.getAgency());
    		case Privilege.SCOPE_ALL:
    			return true;
    		case Privilege.SCOPE_OWNER:
    			return newOwner.equals(loggedInUser);
    		default:
    			return false;
    		}
    	}
    	
    	// Can't take the object, so cannot give it away.
    	else {
    		return false;
    	}
    	
    }
    
    
    @SuppressWarnings("unchecked")
	public List<UserDTO> getPossibleOwners(UserOwnable subject) {
    	List<UserDTO> results = null; 
    	User remoteUser = AuthUtil.getRemoteUserObject();
    	
    	if(authorityManager.hasPrivilege(subject, Privilege.TAKE_OWNERSHIP)) {
    		log.debug("Allowed to take ownership from: " + subject.getOwningUser().getUsername());
    		int scope = authorityManager.getPrivilegeScopeNE(Privilege.GIVE_OWNERSHIP);
    		
			switch(scope) {
			case Privilege.SCOPE_NONE:
				log.debug("Not allowed to give ownership");
				results = new LinkedList<UserDTO>();
				results.add(new UserDTO(subject.getOwningUser()));
				break;
			
			case Privilege.SCOPE_OWNER:
				log.debug("Can only give ownership to myself");
				results = new LinkedList<UserDTO>();
				results.add(new UserDTO(remoteUser));
				
				// The current owner may not be the same as the logged in user.
				if(!subject.getOwningUser().equals(remoteUser)) {
					results.add(new UserDTO(subject.getOwningUser()));
					Collections.sort(results);
				}
				
				break;
				
			case Privilege.SCOPE_AGENCY:
				log.debug("Can give ownership to agency members");
				results = userRoleDAO.getUserDTOs(remoteUser.getAgency().getOid());
				
				// The current owner could be in a different agency.
				if(!subject.getOwningUser().getAgency().equals(remoteUser.getAgency())) {
					results.add(new UserDTO(subject.getOwningUser()));
					Collections.sort(results);
				}				
				
				break;
				
			case Privilege.SCOPE_ALL:
				log.debug("Can give ownership to anyone");
				results = new LinkedList<UserDTO>();
				results = userRoleDAO.getUserDTOs();
				break;
			}
    		
    	}
    	else {
        	// The current owner should always be in the list
    		log.debug("Cannot take ownership of this object");
    		results = new LinkedList<UserDTO>();
        	results.add(new UserDTO(subject.getOwningUser()));
    	}

    	return results;
    	
    }
    
    
    @SuppressWarnings("unchecked")
	public List<UserDTO> getAllowedOwners(User owner) {
		int scope = Privilege.SCOPE_NONE;
		if (owner.equals(AuthUtil.getRemoteUserObject())) {			
			if (authorityManager.hasPrivilege(Privilege.GIVE_OWNERSHIP, Privilege.SCOPE_NONE)) {				
				try {
					scope = authorityManager.getPrivilegeScope(Privilege.GIVE_OWNERSHIP);				
				} 
				catch (NoPrivilegeException e) {
					if (log.isErrorEnabled()) {
						log.error("Failed to get the privilege scope " + e.getMessage(), e);
					}
				}
			}			
		}
		else {			
			if (authorityManager.hasPrivilege(Privilege.TAKE_OWNERSHIP, Privilege.SCOPE_NONE)) {				
				try {
					scope = authorityManager.getPrivilegeScope(Privilege.TAKE_OWNERSHIP);
				} 
				catch (NoPrivilegeException e) {
					if (log.isErrorEnabled()) {
						log.error("Failed to get the privilege scope " + e.getMessage(), e);
					}
				}
			}
		}
		
		List<UserDTO> owners = new ArrayList<UserDTO>();
		if (scope == Privilege.SCOPE_AGENCY) {						
			owners = getUserDTOs(AuthUtil.getRemoteUserObject().getAgency().getOid());
		}
		
		if (scope == Privilege.SCOPE_ALL) {			
			owners = getUserDTOs();
		}
		
		UserDTO currentOwner = new UserDTO(owner);		
		if (owners.isEmpty() || !owners.contains(currentOwner)) {
			owners.add(currentOwner);
		}    	
		
		return owners;
    }
    

	/**
	 * @param profileManager The profileManager to set.
	 */
	public void setProfileManager(ProfileManager profileManager) {
		this.profileManager = profileManager;
	}
    
    @SuppressWarnings("unchecked")
	public Map getUsersWithAllPrivilege(List privileges) {
        Iterator it = privileges.iterator();
        int numOfPrivs = privileges.size();
        Map userSets[] =  new Map[numOfPrivs];
        int i=0;
        Map allUsersMap = new HashMap();
        Map userMap = new HashMap();
        while (it.hasNext()) {
            String checkPrivilege = (String) it.next();
            List users = userRoleDAO.getUserDTOsByPrivilege(checkPrivilege);
            
            log.debug("loading userDTOs with Privilege "+checkPrivilege);
            
            putUsersIntoMap(users,userMap);
            log.debug("putting UserMap into the userSets array");
            userSets[i++] = userMap;
            
        }
        Set keys = userSets[0].keySet();
        Iterator itKeys = keys.iterator();
        while (itKeys.hasNext()) {
            Long aUserOid = (Long) it.next();
            log.debug("Found a User Oid key to check with oid="+aUserOid);
            boolean hasAll = true;
            for (int j=1; j < numOfPrivs; j++) {
                hasAll = hasAll && userSets[j].containsKey(aUserOid);
                log.debug("The User with user oid="+aUserOid+" has all privileges="+hasAll);
            }
            if (hasAll == true) {
                //User has All the specified Privileges so store that user
                log.debug("Storing the User as they have all privileges.");
                allUsersMap.put(aUserOid,userSets[0].get(aUserOid));
            }
        }
        return allUsersMap;
        
    }
    
    @SuppressWarnings("unchecked")
	private void putUsersIntoMap(List users, Map allUsersMap) {
        Iterator it = users.iterator();
        while (it.hasNext()) {
            UserDTO aUser = (UserDTO) it.next();
            Long userOid = aUser.getOid();
            
            if (!allUsersMap.containsKey(userOid)) {
                allUsersMap.put(userOid,aUser);
            }
        }
    }

    public Map getUsersWithAtLeastOnePrivilege(List privileges) {
        Iterator it = privileges.iterator();
        Map allUsersMap = new HashMap();
        while (it.hasNext()) {
            String checkPrivilege = (String) it.next();
            List users = userRoleDAO.getUserDTOsByPrivilege(checkPrivilege);
            
            log.debug("loading userDTOs with Privilege "+checkPrivilege);
            putUsersIntoMap(users,allUsersMap);
        }
        
        return allUsersMap;
    }
    
    public List<UserDTO> getUserDTOsByTargetPrivilege(Long permissionOid) {
    	return userRoleDAO.getUserDTOsByTargetPrivilege(permissionOid);
    }

    public void setIndicatorCriteriaDAO(IndicatorCriteriaDAO indicatorCriteriaDAO) {
        this.indicatorCriteriaDAO = indicatorCriteriaDAO;
    }
    
	@Override
	public List getIndicatorCriteriaForLoggedInUser() {
       	User loggedInUser = AuthUtil.getRemoteUserObject();
        Agency usersAgency = loggedInUser.getAgency();
        
        if (authorityManager.hasPrivilege(Privilege.MANAGE_INDICATORS, Privilege.SCOPE_ALL)) {
            return indicatorCriteriaDAO.getIndicatorCriterias();
        } else {
            return indicatorCriteriaDAO.getIndicatorCriteriasByAgencyOid(usersAgency.getOid());
        }
	}

    public void deleteIndicatorCriteria(IndicatorCriteria indicatorCriteria) {
        Long indicatorCriteriaOid = indicatorCriteria.getOid();
        String name = indicatorCriteria.getName();
        indicatorCriteriaDAO.delete(indicatorCriteria);
        auditor.audit(RejReason.class.getName(), indicatorCriteriaOid ,Auditor.ACTION_DELETE_INDICATOR_CRITERIA,"The Indicator Criteria: '"+name+"' has been deleted.");
    }

    public IndicatorCriteria getIndicatorCriteriaByOid(Long oid) {
        return indicatorCriteriaDAO.getIndicatorCriteriaByOid(oid);
    }
 
    public void updateIndicatorCriteria(IndicatorCriteria indicatorCriteria, boolean update) {
    	indicatorCriteriaDAO.saveOrUpdate(indicatorCriteria);
        if (update == true) {
            auditor.audit(IndicatorCriteria.class.getName(),indicatorCriteria.getOid(),Auditor.ACTION_UPDATE_INDICATOR_CRITERIA,"The Indicator Criteria: '"+indicatorCriteria.getName()+"' has been updated");
        } else {
            auditor.audit(IndicatorCriteria.class.getName(),Auditor.ACTION_NEW_INDICATOR_CRITERIA,"A new Indicator Criteria has been created with a name of '"+indicatorCriteria.getName()+"'");
        }
    }


    public void setFlagDAO(FlagDAO flagDAO) {
        this.flagDAO = flagDAO;
    }
    
	@Override
	public List getFlagForLoggedInUser() {
       	User loggedInUser = AuthUtil.getRemoteUserObject();
        Agency usersAgency = loggedInUser.getAgency();
        
        if (authorityManager.hasPrivilege(Privilege.MANAGE_FLAGS, Privilege.SCOPE_ALL)) {
            return flagDAO.getFlags();
        } else {
            return flagDAO.getFlagsByAgencyOid(usersAgency.getOid());
        }
	}

    public void deleteFlag(Flag Flag) {
        Long FlagOid = Flag.getOid();
        String name = Flag.getName();
        flagDAO.delete(Flag);
        auditor.audit(RejReason.class.getName(), FlagOid ,Auditor.ACTION_DELETE_FLAG,"The Flag: '"+name+"' has been deleted.");
    }

    public Flag getFlagByOid(Long oid) {
        return flagDAO.getFlagByOid(oid);
    }
 
    public void updateFlag(Flag Flag, boolean update) {
    	flagDAO.saveOrUpdate(Flag);
        if (update == true) {
            auditor.audit(Flag.class.getName(),Flag.getOid(),Auditor.ACTION_UPDATE_FLAG,"The Flag: '"+Flag.getName()+"' has been updated");
        } else {
            auditor.audit(Flag.class.getName(),Auditor.ACTION_NEW_FLAG,"A new Flag has been created with a name of '"+Flag.getName()+"'");
        }
    }

    
}
