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

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.webcurator.core.notification.AgencyInTrayResource;
import org.webcurator.core.notification.InTrayResource;
import org.webcurator.core.notification.MessageType;
import org.webcurator.core.notification.UserInTrayResource;
import org.webcurator.domain.model.auth.Privilege;
import org.webcurator.domain.model.auth.RolePrivilege;
import org.webcurator.domain.model.auth.User;
import org.webcurator.domain.model.core.Notification;
import org.webcurator.domain.model.core.Task;

/**
 * The implementation of the InTrayDAO interface.
 * @author bprice
 */
public class InTrayDAOImpl extends HibernateDaoSupport implements InTrayDAO {

    private Log log = LogFactory.getLog(InTrayDAOImpl.class);
    
    private TransactionTemplate txTemplate = null;
    
    public InTrayDAOImpl() {

    }

    public void setTxTemplate(TransactionTemplate txTemplate) {
        this.txTemplate = txTemplate;
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
    
    public Pagination getNotifications(Long userOid, int pageNum, int pageSize) {
        Map <String,Long>params = new HashMap<String,Long>();
        params.put("recipientOid", userOid);

        SessionFactory aSessionFactory = getHibernateTemplate().getSessionFactory();
        return new Pagination(Notification.QRY_CNT_USER_NOTIFICATIONS, Notification.QRY_GET_USER_NOTIFICATIONS, params, pageNum, pageSize, true, aSessionFactory);
    }

    public int countNotifications(final Long userOid) {
    	return (Integer) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				
				Query query = session.createQuery("select count(*) from Notification n where n.recipientOid = :userOid ");
				query.setLong("userOid", userOid);
				
				return ((Number) query.uniqueResult()).intValue();
			}
    	});
    }
    
    public void delete(final Object obj) {
        txTemplate.execute(
                new TransactionCallback() {
                    public Object doInTransaction(TransactionStatus ts) {
                        try {
                            log.debug("Before Delete of Object");
                            getHibernateTemplate().delete(obj);
                            log.debug("After Delete of Object");
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

    public Object load(Class clazz, Long oid) {
        return getHibernateTemplate().load(clazz,oid);
    }

    public InTrayResource populateOwner(final InTrayResource wctResource) {
        return (InTrayResource)getHibernateTemplate().execute(new HibernateCallback(){

            public Object doInHibernate(Session aSession) throws HibernateException, SQLException {
                Object object = aSession.load(wctResource.getResourceType(),wctResource.getOid());
                if (wctResource instanceof UserInTrayResource) {
                    UserInTrayResource uitr = (UserInTrayResource) object;
                    Hibernate.initialize(uitr.getOwningUser());
                    Hibernate.initialize(uitr.getOwningUser().getAgency());
                    return uitr;
                } else if (wctResource instanceof AgencyInTrayResource) {
                    AgencyInTrayResource aitr = (AgencyInTrayResource) object;
                    Hibernate.initialize(aitr.getOwningAgency());
                    return aitr;
                }
                return null;
            }
        }

        );
    }

    public Pagination getTasks(final User user, final List<RolePrivilege> privs, final int pageNum, final int pageSize) {
        return (Pagination) getHibernateTemplate().execute(new HibernateCallback() {
  
            public Object doInHibernate(Session aSession) throws HibernateException, SQLException {
                Criteria query = aSession.createCriteria(Task.class);
                
                Disjunction dis = Restrictions.disjunction();
                
                for(RolePrivilege userPriv: privs) {
                  dis.add(Restrictions.eq("privilege", userPriv.getPrivilege()));
                }
                dis.add(Restrictions.eq("assigneeOid",user.getOid()));

                query.add(dis);
                query.createCriteria("agency").add(Restrictions.eq("oid", user.getAgency().getOid()));
                query.addOrder(Order.desc("sentDate"));
                
                Criteria cntQuery = aSession.createCriteria(Task.class);
                cntQuery.add(dis);
                cntQuery.createCriteria("agency").add(Restrictions.eq("oid", user.getAgency().getOid()));
                cntQuery.setProjection(Projections.rowCount());
                
                return new Pagination(cntQuery, query, pageNum, pageSize);
            }
        });
    }
    
    public int countTasks(final User user, final List<RolePrivilege> privs) {
    	return (Integer) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				
				Criteria query = session.createCriteria(Task.class);
				query.setProjection(Projections.rowCount());
                Disjunction dis = Restrictions.disjunction();
                
                for(RolePrivilege userPriv: privs) {
                  dis.add(Restrictions.eq("privilege", userPriv.getPrivilege()));
                }
                dis.add(Restrictions.eq("assigneeOid",user.getOid()));

                query.add(dis);
                query.createCriteria("agency").add(Restrictions.eq("oid", user.getAgency().getOid()));
                                
                Integer count = (Integer) query.uniqueResult();
                
                return count;
			}
    	});
    }
    
    /** @see InTrayDAO#getTask(Long, String, String). */
    public Task getTask(final Long aResourceOid, final String aResourceType, final String aTaskType) {
        return (Task) getHibernateTemplate().execute(new HibernateCallback() {
  
            public Object doInHibernate(Session aSession) throws HibernateException, SQLException {
                Criteria query = aSession.createCriteria(Task.class);

                query.add(Restrictions.eq("resourceOid", aResourceOid));                
                query.add(Restrictions.eq("resourceType", aResourceType));
                query.add(Restrictions.eq("messageType", aTaskType));
                                
                return query.uniqueResult();
            }            
        });
    }    
    
    /** @see InTrayDAO#getTasks(Long, String, String). */
    @SuppressWarnings("unchecked")
    public List<Task> getTasks(final Long aResourceOid, final String aResourceType, final String aTaskType) {
        return (List<Task>) getHibernateTemplate().execute(new HibernateCallback() {
  
            public Object doInHibernate(Session aSession) throws HibernateException, SQLException {
                Criteria query = aSession.createCriteria(Task.class);

                query.add(Restrictions.eq("resourceOid", aResourceOid));                
                query.add(Restrictions.eq("resourceType", aResourceType));
                query.add(Restrictions.eq("messageType", aTaskType));
                                
                return query.list();
            }            
        });
    }    

    public void claimTask(User user, final Task task) {
        task.setPrivilege(null);
        task.setAssigneeOid(user.getOid());
        
        txTemplate.execute(
                new TransactionCallback() {
                    public Object doInTransaction(TransactionStatus ts) {
                        try {
                            log.debug("Before Save of Object");
                            getHibernateTemplate().saveOrUpdate(task);
                            log.debug("After Save of Object");
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
    
    public void unclaimTask(User user, final Task task) {
        if (task.getMessageType().equals(MessageType.TARGET_INSTANCE_ENDORSE)) {
        	task.setPrivilege(Privilege.ENDORSE_HARVEST);
        }
        else if (task.getMessageType().equals(MessageType.TASK_APPROVE_TARGET)) {
        	task.setPrivilege(Privilege.APPROVE_TARGET);
        }
        else if (task.getMessageType().equals(MessageType.TARGET_INSTANCE_ARCHIVE)) {
        	task.setPrivilege(Privilege.ARCHIVE_HARVEST);
        }
        else if (task.getMessageType().equals(MessageType.TASK_SEEK_PERMISSON)) {
        	task.setPrivilege(Privilege.CONFIRM_PERMISSION);
        }
        
        task.setAssigneeOid(null);
        
        txTemplate.execute(
                new TransactionCallback() {
                    public Object doInTransaction(TransactionStatus ts) {
                        try {
                            log.debug("Before Save of Object");
                            getHibernateTemplate().saveOrUpdate(task);
                            log.debug("After Save of Object");
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
    
    public int countTasks(final String messageType, final InTrayResource wctResource) {
    	return (Integer) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				return (Integer) session.createCriteria(Task.class)
					.setProjection(Projections.rowCount())
					.add(Restrictions.eq("messageType", messageType))
					.add(Restrictions.eq("resourceOid", wctResource.getOid()))
				    .add(Restrictions.eq("resourceType", wctResource.getResourceType()))
				    .uniqueResult();
			}
    	});    	
    }

	public void deleteNotificationsByUser(final Long userOid) {
        txTemplate.execute(
                new TransactionCallback() {
                    public Object doInTransaction(TransactionStatus ts) {
                        try {
                            log.debug("Before Deleting all Notifications");
                            
                            String hqlDelete = "delete Notification n where n.recipientOid = :recipientOid";
                            int deletedEntities = getSession().createQuery( hqlDelete )
                                    .setLong( "recipientOid", userOid )
                                    .executeUpdate();
                            
                            //getHibernateTemplate().delete(obj);
                            log.debug("After Deleting "+deletedEntities+" Notifications");
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
    
	public void deleteAllTasks() {
        txTemplate.execute(
                new TransactionCallback() {
                    public Object doInTransaction(TransactionStatus ts) {
                        try {
                            log.debug("Before Deleting all Tasks");
                            
                            String hqlDelete = "delete Task t";
                            int deletedEntities = getSession().createQuery( hqlDelete )
                                    .executeUpdate();
                            
                            //getHibernateTemplate().delete(obj);
                            log.debug("After Deleting "+deletedEntities+" Tasks");
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
}
