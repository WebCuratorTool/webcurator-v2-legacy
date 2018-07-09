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
package org.webcurator.domain;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.webcurator.domain.model.auth.Agency;
import org.webcurator.domain.model.auth.Role;
import org.webcurator.domain.model.auth.RolePrivilege;
import org.webcurator.domain.model.auth.User;
import org.webcurator.domain.model.dto.UserDTO;

/**
 * implements the UserRoleDAO Interface and provides the database calls for
 * querying any objects related to Authentication. These include User, Roles
 * and Privilges.
 * @author bprice
 */
public class UserRoleDAOImpl extends HibernateDaoSupport implements UserRoleDAO{
    
    private Log log = LogFactory.getLog(UserRoleDAOImpl.class);
    
    private TransactionTemplate txTemplate = null;
    
    public List getUserDTOs(Long agencyOid) {
        Object[] params = new Object[] {agencyOid};   
        List results = getHibernateTemplate().findByNamedQuery(User.QRY_GET_ALL_USER_DTOS_BY_AGENCY, params);
        return results;
    }

    public List getUserDTOs() {
        
        List results = getHibernateTemplate().findByNamedQuery(User.QRY_GET_ALL_USER_DTOS);
        return results;
    }

    public UserDTO getUserDTOByOid(final Long userOid) {
        return (UserDTO)getHibernateTemplate().execute(
                new HibernateCallback() {
                    public Object doInHibernate(Session session) {
                        Query query = session.getNamedQuery(User.QRY_GET_USER_DTO_BY_OID);
                        query.setLong(0,userOid);
                        return query.uniqueResult();
                    }
                }
            );
          
    }

    public List getRoles() {
        return getHibernateTemplate().findByNamedQuery(Role.QRY_GET_ROLES);
    }

    public List getRoles(Long agencyOid) {
        Object[] params = new Object[] {agencyOid};
        return getHibernateTemplate().findByNamedQuery(Role.QRY_GET_ROLES_BY_AGENCY,params);
    }

    public User getUserByOid(Long oid) {
        return (User)getHibernateTemplate().load(User.class,oid);
    }

    public User getUserByName(String username) {
        Object[] params = new Object[] {username};   
        List results = getHibernateTemplate().findByNamedQuery(User.QRY_GET_USER_BY_NAME,params);
        
        if(results.size() == 1) {
            return (User) results.get(0);
        }
        else {
            return null;
        }
    }

    public Agency getAgencyByOid(Long oid) {
        return (Agency)getHibernateTemplate().load(Agency.class,oid);
    }
    
    public List getUserPrivileges(String username) {
        Object[] params = new Object[] {username};
        return getHibernateTemplate().findByNamedQuery(RolePrivilege.QRY_GET_USER_PRIVILEGES,params);
    }

    public void saveOrUpdate(final Object aObject) {
        txTemplate.execute(
                new TransactionCallback() {
                    public Object doInTransaction(TransactionStatus ts) {
                        try { 
                            log.debug("Before Saving of Object");
                            getSession().saveOrUpdate(aObject);
                            log.debug("After Saving Object");
                        }
                        catch(Exception ex) {
                            log.warn("Setting Rollback Only",ex);
                            ts.setRollbackOnly();
                        }
                        return null;
                    }
                }
        );    
    }
    
//    public void saveOrUpdate(Object aObject) {
//        getHibernateTemplate().saveOrUpdate(aObject);
//    }
    
    public void delete(final Object aObject) {
        txTemplate.execute(
                new TransactionCallback() {
                    public Object doInTransaction(TransactionStatus ts) {
                        try {
                            log.debug("Before Delete of Object");
                            getHibernateTemplate().delete(aObject);
                            log.debug("After Deletes Object");
                        }
                        catch (DataAccessException e) {
                            log.warn("Setting Rollback Only",e);
                            ts.setRollbackOnly();
                            throw e;
                        }
                        return null;
                    }
                }
        );    
    }
    
    public List getUsers() {
//        return getHibernateTemplate().loadAll(User.class);
        return (List)getHibernateTemplate().execute(
                new HibernateCallback() {
                    public Object doInHibernate(Session session) {
                        Criteria query = session.createCriteria(User.class);
                        query.addOrder(Order.asc("username"));
                        return query.list();
                    }
                }
            );                          

    }
    
    public List getUsers(Long agencyOid) {
        Object[] params = new Object[] {agencyOid};
        return getHibernateTemplate().findByNamedQuery(User.QRY_GET_USERS_BY_AGENCY,params);
    }

    public void setTxTemplate(TransactionTemplate txTemplate) {
        this.txTemplate = txTemplate;
    }

    public List getAgencies() {
        return getHibernateTemplate().findByNamedQuery(Agency.QRY_GET_ALL_AGENCIES);
    }

    public List getAssociatedRolesForUser(Long userOid) {
        Object[] params = new Object[] {userOid};
        return getHibernateTemplate().findByNamedQuery(Role.QRY_GET_ASSOCIATED_ROLES_BY_USER,params);
    }

    public Role getRoleByOid(Long oid) {
        return (Role)getHibernateTemplate().load(Role.class, oid);
    }

//    public void deleteUserRolesForUser(final User user) {
//        txTemplate.execute(
//                new TransactionCallback() {
//                    public Object doInTransaction(TransactionStatus ts) {
//                        try { 
//                                Set userRoles = user.getRoles();
//                                Iterator it = userRoles.iterator();
//                                while(it.hasNext()) {
//                                    UserRole userRole = (UserRole) it.next();
//                                    userRole.getRole().getUsers().remove(userRole);
//                                    userRole.getUser().getRoles().remove(userRole);
//                                    userRole.setUser(null);
//                                    userRole.setRole(null);
//                                }
//                                getHibernateTemplate().deleteAll(userRoles);        
//                        }
//                        catch(Exception ex) {
//                            log.warn("Setting Rollback Only",ex);
//                            ts.setRollbackOnly();
//                        }
//                        return null;
//                    }
//                }
//        );    
//    }

    @SuppressWarnings("unchecked")
    public List<UserDTO> getUserDTOsByPrivilege(String privilege) {
        Object[] params = new Object[] {privilege};
        return getHibernateTemplate().findByNamedQuery(User.QRY_GET_USER_DTOS_BY_PRIVILEGE,params);
    }

    /**
     * @see org.webcurator.domain.UserRoleDAO#getUserDTOsByPrivilege(java.lang.String, java.lang.Long)
     */
    @SuppressWarnings("unchecked")
    public List<UserDTO> getUserDTOsByPrivilege(String privilege, Long agencyOid) {
        Object[] params = new Object[] {privilege, agencyOid};
        return getHibernateTemplate().findByNamedQuery(User.QRY_GET_USER_DTOS_BY_PRIVILEGE_FOR_AGENCY,params);
    }
    
    /**
     * @see org.webcurator.domain.UserRoleDAO#getUserDTOsByTargetPrivilege(java.lang.Long)
     */
    @SuppressWarnings("unchecked")
	public List<UserDTO> getUserDTOsByTargetPrivilege(Long permissionOid) {
    	return getHibernateTemplate().findByNamedQuery(User.QRY_GET_USER_DTOS_BY_TARGET_PERMISSION, permissionOid);
    }
}
