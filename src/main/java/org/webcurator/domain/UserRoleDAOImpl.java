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
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.hibernate5.HibernateCallback;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
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
@Repository
@Transactional
public class UserRoleDAOImpl implements UserRoleDAO {
    
    private static Log log = LogFactory.getLog(UserRoleDAOImpl.class);

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private HibernateTemplate transactionTemplate;

    public List getUserDTOs(Long agencyOid) {
        Query query = sessionFactory.getCurrentSession().getNamedQuery(User.QRY_GET_ALL_USER_DTOS_BY_AGENCY);
        query.setParameter(1, agencyOid);
        List results = query.list();
        return results;
    }

    public List getUserDTOs() {
        Query query = sessionFactory.getCurrentSession().getNamedQuery(User.QRY_GET_ALL_USER_DTOS);
        List results = query.list();
        return results;
    }

    public UserDTO getUserDTOByOid(final Long userOid) {
        return (UserDTO)transactionTemplate.execute(
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
        return sessionFactory.getCurrentSession().getNamedQuery(Role.QRY_GET_ROLES).list();
    }

    public List getRoles(Long agencyOid) {
        Query query = sessionFactory.getCurrentSession().getNamedQuery(Role.QRY_GET_ROLES_BY_AGENCY);
        query.setParameter(1, agencyOid);
        List results = query.list();
        return results;
    }

    public User getUserByOid(Long oid) {
        return (User)transactionTemplate.load(User.class,oid);
    }

    public User getUserByName(String username) {
        Query query = sessionFactory.getCurrentSession().getNamedQuery(User.QRY_GET_USER_BY_NAME);
        query.setParameter(1, username);
        List results = query.list();

        if(results.size() == 1) {
            return (User) results.get(0);
        }
        else {
            return null;
        }
    }

    public Agency getAgencyByOid(Long oid) {
        return (Agency)transactionTemplate.load(Agency.class,oid);
    }
    
    public List getUserPrivileges(String username) {
        Query query = sessionFactory.getCurrentSession().getNamedQuery(RolePrivilege.QRY_GET_USER_PRIVILEGES);
        query.setParameter(1, username);
        List results = query.list();
        return results;
    }

    public void saveOrUpdate(final Object aObject) {
        transactionTemplate.execute(
                new HibernateCallback() {
                    public Object doInHibernate(Session session) {
                        try { 
                            log.debug("Before Saving of Object");
                            session.saveOrUpdate(aObject);
                            log.debug("After Saving Object");
                        }
                        catch(Exception ex) {
                            log.warn("Setting Rollback Only",ex);
                            session.getTransaction().setRollbackOnly();
                        }
                        return null;
                    }
                }
        );    
    }

    public void delete(final Object aObject) {
        transactionTemplate.execute(
                new HibernateCallback() {
                    public Object doInHibernate(Session session) {
                        try {
                            log.debug("Before Delete of Object");
                            transactionTemplate.delete(aObject);
                            log.debug("After Deletes Object");
                        }
                        catch (DataAccessException e) {
                            log.warn("Setting Rollback Only",e);
                            session.getTransaction().setRollbackOnly();
                            throw e;
                        }
                        return null;
                    }
                }
        );    
    }
    
    public List getUsers() {
        return (List)transactionTemplate.execute(
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
        Query query = sessionFactory.getCurrentSession().getNamedQuery(User.QRY_GET_USERS_BY_AGENCY);
        query.setParameter(1, agencyOid);
        List results = query.list();
        return results;
    }

    public List getAgencies() {
        return sessionFactory.getCurrentSession().getNamedQuery(Agency.QRY_GET_ALL_AGENCIES).list();
    }

    public List getAssociatedRolesForUser(Long userOid) {
        Query query = sessionFactory.getCurrentSession().getNamedQuery(Role.QRY_GET_ASSOCIATED_ROLES_BY_USER);
        query.setParameter(1, userOid);
        List results = query.list();
        return results;
    }

    public Role getRoleByOid(Long oid) {
        return (Role)transactionTemplate.load(Role.class, oid);
    }

    @SuppressWarnings("unchecked")
    public List<UserDTO> getUserDTOsByPrivilege(String privilege) {
        Query query = sessionFactory.getCurrentSession().getNamedQuery(User.QRY_GET_USER_DTOS_BY_PRIVILEGE);
        query.setParameter(1, privilege);
        List results = query.list();
        return results;
    }

    /**
     * @see org.webcurator.domain.UserRoleDAO#getUserDTOsByPrivilege(java.lang.String, java.lang.Long)
     */
    @SuppressWarnings("unchecked")
    public List<UserDTO> getUserDTOsByPrivilege(String privilege, Long agencyOid) {
        Query query = sessionFactory.getCurrentSession().getNamedQuery(User.QRY_GET_USER_DTOS_BY_PRIVILEGE_FOR_AGENCY);
        query.setParameter(1, privilege);
        query.setParameter(2, agencyOid);
        List results = query.list();
        return results;
    }
    
    /**
     * @see org.webcurator.domain.UserRoleDAO#getUserDTOsByTargetPrivilege(java.lang.Long)
     */
    @SuppressWarnings("unchecked")
	public List<UserDTO> getUserDTOsByTargetPrivilege(Long permissionOid) {
        Query query = sessionFactory.getCurrentSession().getNamedQuery(User.QRY_GET_USER_DTOS_BY_TARGET_PERMISSION);
        query.setParameter(1, permissionOid);
        List results = query.list();
        return results;
    }
}
