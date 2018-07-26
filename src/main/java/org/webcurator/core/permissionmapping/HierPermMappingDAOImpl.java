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
package org.webcurator.core.permissionmapping;

import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.HibernateCallback;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.webcurator.domain.model.core.Site;

/**
 * Implementation of the Hierarchical Permission Mapping DAO.
 * 
 * @see org.webcurator.core.permissionmapping.HierPermMappingDAO
 * @author bbeaumont
 *
 */
@Repository
@Transactional
public class HierPermMappingDAOImpl implements HierPermMappingDAO {
	/** The logger for this class */
	private final static Log log = LogFactory.getLog(HierPermMappingDAOImpl.class);

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	private HibernateTemplate transactionTemplate;

	/* (non-Javadoc)
	 * @see org.webcurator.core.permissionmapping.HierPermMappingDAO#saveOrUpdate(org.webcurator.core.permissionmapping.Mapping)
	 */
	public void saveOrUpdate(final Mapping aMapping) {
		// A Mapping can only be saved if it references real objects.
		if(aMapping.getPermission().getOid() == null ||
		   aMapping.getUrlPattern().getOid() == null) {
			return;
		}

		transactionTemplate.execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session) {
						try { 
							session.save(aMapping);
						}
						catch(Exception ex) {
							log.debug("Setting Rollback Only", ex);
							session.getTransaction().setRollbackOnly();
						}
						return null;
					}
				}
		);
	}
	
	/* (non-Javadoc)
	 * @see org.webcurator.core.permissionmapping.HierPermMappingDAO#delete(org.webcurator.core.permissionmapping.Mapping)
	 */
	public void delete(final Mapping aMapping) {
		// A Mapping can only be deleted if it references real objects.
		if(aMapping.getPermission().getOid() == null ||
		   aMapping.getUrlPattern().getOid() == null) {
			return;
		}		
		
		// Run the deletion.
		transactionTemplate.execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session) {
						try { 
							
							Query q = session.getNamedQuery(Mapping.DELETE);
							q.setLong("urlPatternOid", aMapping.getUrlPattern().getOid());
							q.setLong("permissionOid", aMapping.getPermission().getOid());
							
							//log.debug("Before Deleting Mappings");
							
							int rowsAffected = q.executeUpdate();
							
							log.debug("After Deleting Mappings: " + rowsAffected);
						}
						catch(Exception ex) {
							log.debug("Setting Rollback Only", ex);
							session.getTransaction().setRollbackOnly();
						}
						return null;
					}
				}
		);
	}
	
	/* (non-Javadoc)
	 * @see org.webcurator.core.permissionmapping.HierPermMappingDAO#getMapping(java.lang.Long)
	 */
	@SuppressWarnings("unchecked")
	public List<Mapping> getMapping(Long mappingOid) {
		Query query = sessionFactory.getCurrentSession().getNamedQuery(Mapping.QUERY_BY_OID);
		query.setParameter(1, mappingOid);
		List results = query.list();
		return results;
	}

	/* (non-Javadoc)
	 * @see org.webcurator.core.permissionmapping.HierPermMappingDAO#getMappings(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public List<Mapping> getMappings(String domain) {
		Query query = sessionFactory.getCurrentSession().getNamedQuery(Mapping.QUERY_BY_DOMAIN);
		query.setParameter(1, domain);
		List results = query.list();
		return results;
	}

	/* (non-Javadoc)
	 * @see org.webcurator.core.permissionmapping.HierPermMappingDAO#getMappingsView(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public List<MappingView> getMappingsView(String domain) {
		Query query = sessionFactory.getCurrentSession().getNamedQuery(MappingView.QUERY_BY_DOMAIN);
		query.setParameter(1, domain);
		List results = query.list();
		return results;
	}

	/* (non-Javadoc)
	 * @see org.webcurator.core.permissionmapping.HierPermMappingDAO#updateMappings(org.webcurator.domain.model.core.Site, java.util.Set)
	 */
	public void updateMappings(final Site aSite, final Set<Mapping> newMappings) {
		log.debug("Into updateMappings method");
		// Run the deletion.
		transactionTemplate.execute(
				new HibernateCallback() {
					@SuppressWarnings("unchecked")
					public Object doInHibernate(Session session) {
						try { 
							
							Criteria query = session.createCriteria(Mapping.class);
							query.createCriteria("permission")
							     .createCriteria("site")
							     .add(Restrictions.eq("oid", aSite.getOid()));
							
							List<Mapping> mappings = query.list();
							
							for(Mapping m: mappings) {
								if(!newMappings.contains(m)) {
									log.debug("Deleting: " + m.getOid());
									session.delete(m);
								}
								else {
									log.debug("Keeping: " + m.getOid());
									newMappings.remove(m);
								}
							}
							
							for(Mapping m: newMappings) {
								session.save(m);
							}
						}
						catch(Exception ex) {
							log.debug("Setting Rollback Only", ex);
							session.getTransaction().setRollbackOnly();
						}
						return null;
					}
				}
		);		
	}
	
	/* (non-Javadoc)
	 * @see org.webcurator.core.permissionmapping.HierPermMappingDAO#deleteMappings(org.webcurator.domain.model.core.Site)
	 */	
	public void deleteMappings(final Site aSite) {
		// Run the deletion.
		transactionTemplate.execute(
				new HibernateCallback() {
					@SuppressWarnings("unchecked")
					public Object doInHibernate(Session session) {
						try { 
							Criteria query = session.createCriteria(Mapping.class);
							query.createCriteria("permission")
							     .createCriteria("site")
							     .add(Restrictions.eq("oid", aSite.getOid()));
							
							List<Mapping> mappings = query.list();
							for(Mapping m: mappings) {
								session.delete(m);
							}
						}
						catch(Exception ex) {
							log.debug("Setting Rollback Only", ex);
							session.getTransaction().setRollbackOnly();
						}
						return null;
					}
				}
		);
		
		// Clear the session to evict anything
		// that we don't want hanging around.	
		sessionFactory.getCurrentSession().clear();
		
	}

	
	
	/**
	 * Save all of the mappings.
	 * @param mappings The mappings to save.
	 */
	public void saveMappings(final List<Mapping> mappings) {
		transactionTemplate.execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session) {
						try { 
							
							for(Mapping m: mappings) {
								session.save(m);
							}
						}
						catch(Exception ex) {
							log.debug("Setting Rollback Only", ex);
							session.getTransaction().setRollbackOnly();
						}
						return null;
					}
				}
		);		
		
		// Clear the session to evict anything
		// that we don't want hanging around.	
		sessionFactory.getCurrentSession().clear();
		
	}

}
