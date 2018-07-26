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

import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.HibernateCallback;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.webcurator.core.exceptions.WCTRuntimeException;

/**
 * The implementation of the BaseDAO interface.
 * @author bbeaumont
 */
@Repository
@Transactional
public class BaseDAOImpl implements BaseDAO {
	private static Log log = LogFactory.getLog(BaseDAOImpl.class);

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	private HibernateTemplate transactionTemplate;
	
	public void evict(Object anObject) {
		transactionTemplate.evict(anObject);
	}
	
	/**
	 * Delete all objects in the collection.
	 * @param anObject The object to remove.
	 */	
	public void delete(final Object anObject) {
		transactionTemplate.execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session) {
						try { 
							log.debug("Before Delete");
							session.delete(anObject);
							log.debug("After Delete");
						}
						catch(Exception ex) {
							log.error("Setting Rollback Only", ex);
							session.getTransaction().setRollbackOnly();
							throw new WCTRuntimeException("Failed to delete object", ex);
						}
						return null;
					}
				}
		);	
	}	
	
	/**
	 * Delete all objects in the collection.
	 * @param aCollection The collection of objects to remove.
	 */
	public void deleteAll(final Collection aCollection) {
		transactionTemplate.execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session) {
						try { 
							log.debug("Before Delete");
							for(Object anObject: aCollection) {
								session.delete(anObject);
							}
							log.debug("After Delete");
						}
						catch(Exception ex) {
							log.error("Setting Rollback Only", ex);
							session.getTransaction().setRollbackOnly();
						}
						return null;
					}
				}
		);	
	}	

	public void initialize(Object anObject) {
		transactionTemplate.initialize(anObject);
	}
	
}
