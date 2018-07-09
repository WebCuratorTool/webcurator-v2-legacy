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

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.webcurator.core.exceptions.WCTRuntimeException;
import org.webcurator.domain.model.auth.Agency;
import org.webcurator.domain.model.core.AuthorisingAgent;
import org.webcurator.domain.model.core.Permission;
import org.webcurator.domain.model.core.Seed;
import org.webcurator.domain.model.core.Site;
import org.webcurator.domain.model.core.UrlPattern;
import org.webcurator.ui.common.Constants;
import org.webcurator.ui.site.command.SiteSearchCommand;

/**
 * The implementation of the SiteDAO interface.
 * @author bbeaumont
 */
public class SiteDAOImpl extends HibernateDaoSupport implements SiteDAO {
	private Log log = LogFactory.getLog(SiteDAOImpl.class);
	
	private TransactionTemplate txTemplate = null;
	
	/**
	 * @param txTemplate The txTemplate to set.
	 */
	public void setTxTemplate(TransactionTemplate txTemplate) {
		this.txTemplate = txTemplate;
	}	
	
	public void saveOrUpdate(final Site aSite) {
		txTemplate.execute(
				new TransactionCallback() {
					public Object doInTransaction(TransactionStatus ts) {
						log.debug("Before Saving of Site");
							
						try {
							for(AuthorisingAgent agent: aSite.getAuthorisingAgents()) {
								getSession().saveOrUpdate(agent);
							}
							getSession().saveOrUpdate(aSite);
						}
						catch(Exception ex) {
							ts.setRollbackOnly();
							log.error(ex.getMessage(), ex);
							throw new WCTRuntimeException(ex.getMessage(), ex);
						}
						
						log.debug("After Saving Site");
						
						return null;
					}
				}
		);
		
		
	}
	
	public Site load(final long siteOid) {
		return load(siteOid, false);
	}
	
	public Site load(final long siteOid, boolean fullyInitialise) {
		if( !fullyInitialise) {
			return (Site) getHibernateTemplate().load(Site.class, siteOid);
		}
		else {
			Site site = (Site) getHibernateTemplate().load(Site.class, siteOid);
			
			// Initialise some more items that we'll need. This is used
			// to prevent lazy load exceptions, since we're doing things
			// across multiple sessions.
			for(Permission p : site.getPermissions()) {
				Hibernate.initialize(p.getUrls());
			}
			
			for(UrlPattern p : site.getUrlPatterns()) {
				Hibernate.initialize(p.getPermissions());
			}	
			
			return site;
		}	
	}
	
	/**
	 * Load an authorising agent from the database.
	 * @param authAgentOid The OID of the authorising agent to load.
	 * @return The authorising agent.
	 */
	public AuthorisingAgent loadAuthorisingAgent(final long authAgentOid) {
		return (AuthorisingAgent) getHibernateTemplate().load(AuthorisingAgent.class, authAgentOid);
	}	

	@SuppressWarnings("unchecked")
	public List<Permission> getQuickPickPermissions(Agency anAgency) {
		Criteria criteria = getSession().createCriteria(Permission.class);
		criteria.add(Restrictions.disjunction().add(Restrictions.isNull("endDate")).add(Restrictions.ge("endDate", new Date())));
		criteria.add(Restrictions.eq("quickPick", true));
		criteria.add(Restrictions.eq("owningAgency", anAgency));
		//criteria.add(Restrictions.eq("active", true));
		criteria.addOrder(Order.asc("displayName"));
		
		
		return criteria.list();
		
	}
	
	@SuppressWarnings("unchecked")
	public List<Site> listSitesByTitle(final String aTitle) {		
		Object o = getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(final Session session) {
				Query query = session.createQuery("from Site s where lower(s.title) = :siteTitle");
				query.setString("siteTitle", aTitle);
				
				return query.list();
			}
		});
		
		return (List<Site>) o;
	}
	
	/**
	 * Find permissions by Site
	 * @param anAgencyOid The OID of the agency to restrict the search to.
	 * @param aSiteTitle The title of the site.
	 * @param aPageNumber The page number to return.
	 * @return A List of Permissions.
	 */
	public Pagination findPermissionsBySiteTitle(final Long anAgencyOid, final String aSiteTitle, final int aPageNumber) {
		return (Pagination) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session) {
						
						Criteria query = session.createCriteria(Permission.class);
						query.add(Restrictions.disjunction()
							 .add(Restrictions.isNull("endDate"))
							 .add(Restrictions.ge("endDate", new Date())));
					
						query.createCriteria("owningAgency")
							 .add(Restrictions.eq("oid", anAgencyOid));

						query.createCriteria("site")
							 .add(Restrictions.ilike("title", aSiteTitle, MatchMode.START))
							 .add(Restrictions.eq("active", true))
							 .addOrder(Order.asc("title"));
						
						Criteria cntQuery = session.createCriteria(Permission.class);
						cntQuery.add(Restrictions.disjunction()
							 .add(Restrictions.isNull("endDate"))
							 .add(Restrictions.ge("endDate", new Date())));
					
						cntQuery.createCriteria("owningAgency")
							 .add(Restrictions.eq("oid", anAgencyOid));

						cntQuery.createCriteria("site")
							 .add(Restrictions.ilike("title", aSiteTitle, MatchMode.START))
							 .add(Restrictions.eq("active", true));	

						cntQuery.setProjection(Projections.rowCount());
						
						return new Pagination(cntQuery, query, aPageNumber, Constants.GBL_PAGE_SIZE);
					}
				}
			);			
	}

	
	public Pagination search(final SiteCriteria aCriteria, final int page, final int pageSize) {
		return (Pagination) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session) {
						
						Criteria query = session.createCriteria(Site.class);
						Criteria cntQuery = session.createCriteria(Site.class);
						
						if(aCriteria != null && aCriteria.getTitle() != null && !"".equals(aCriteria.getTitle().trim())) {
							query.add(Restrictions.ilike("title", aCriteria.getTitle().trim(), MatchMode.START));
							cntQuery.add(Restrictions.ilike("title", aCriteria.getTitle().trim(), MatchMode.START));
						}
						
						if(aCriteria != null && aCriteria.getOrderNo() != null && !"".equals(aCriteria.getOrderNo().trim())) {
							query.add(Restrictions.ilike("libraryOrderNo", aCriteria.getOrderNo().trim(), MatchMode.START));
							cntQuery.add(Restrictions.ilike("libraryOrderNo", aCriteria.getOrderNo().trim(), MatchMode.START));
						}
						
						if(aCriteria != null && aCriteria.getAgentName() != null && !"".equals(aCriteria.getAgentName().trim())) {
							query.createCriteria("authorisingAgents").add(Restrictions.ilike("name", aCriteria.getAgentName().trim(), MatchMode.START));
							cntQuery.createCriteria("authorisingAgents").add(Restrictions.ilike("name", aCriteria.getAgentName().trim(), MatchMode.START));
						}
						
						if(aCriteria != null) {
							if (!aCriteria.isShowDisabled()) {
								query.add(Restrictions.eq("active", true));
								cntQuery.add(Restrictions.eq("active", true));
							}							
						}

						// Owning Agency criteria.
						if(aCriteria != null && aCriteria.getAgency() != null && !"".equals(aCriteria.getAgency().trim())) {
							query.createCriteria("owningAgency").add(Restrictions.ilike("name", aCriteria.getAgency().trim(), MatchMode.START));
							cntQuery.createCriteria("owningAgency").add(Restrictions.ilike("name", aCriteria.getAgency().trim(), MatchMode.START));
						}
						
						if(aCriteria != null && aCriteria.getSearchOid() != null) {
							query.add(Restrictions.eq("oid", aCriteria.getSearchOid()));
							cntQuery.add(Restrictions.eq("oid", aCriteria.getSearchOid()));
						}

						// URL Pattern's URL pattern criteria.
						if(aCriteria != null && aCriteria.getUrlPattern() != null && !"".equals(aCriteria.getUrlPattern().trim())) {
							query.createCriteria("urlPatterns").add(Restrictions.ilike("pattern", aCriteria.getUrlPattern().trim(), MatchMode.START));
							cntQuery.createCriteria("urlPatterns").add(Restrictions.ilike("pattern", aCriteria.getUrlPattern().trim(), MatchMode.START));
						}
						
						Criteria permissionsCriteria = null;
						Criteria cntPermissionsCriteria = null;

						// Permission's File Reference criteria.
						if(aCriteria != null && aCriteria.getPermsFileRef() != null && !"".equals(aCriteria.getPermsFileRef().trim())) {
							if(permissionsCriteria == null) {
								permissionsCriteria = query.createCriteria("permissions");	
								cntPermissionsCriteria = cntQuery.createCriteria("permissions");	
							}
							permissionsCriteria.add(Restrictions.ilike("fileReference", aCriteria.getPermsFileRef().trim(), MatchMode.START));
							cntPermissionsCriteria.add(Restrictions.ilike("fileReference", aCriteria.getPermsFileRef().trim(), MatchMode.START));
						}

						// Permission's status flags criteria.
						Set<Integer> states = null;
						if(aCriteria != null) { states = aCriteria.getStates(); }
						if(aCriteria != null && states != null && states.size() > 0) {
							Disjunction stateDisjunction = Restrictions.disjunction();
							for(Integer i: states) {
								stateDisjunction.add(Restrictions.eq("status", i));
							}
							if(permissionsCriteria == null) {
								permissionsCriteria = query.createCriteria("permissions");	
								cntPermissionsCriteria = cntQuery.createCriteria("permissions");	
							}
							permissionsCriteria.add(stateDisjunction);
							cntPermissionsCriteria.add(stateDisjunction);
						}

						
						if( aCriteria.getSortorder() == null || 
							aCriteria.getSortorder().equals(SiteSearchCommand.SORT_NAME_ASC)) {
							query.addOrder(Order.asc("title"));
						} else if (aCriteria.getSortorder().equals(SiteSearchCommand.SORT_NAME_DESC)) {
							query.addOrder(Order.desc("title"));
						} else if (aCriteria.getSortorder().equals(SiteSearchCommand.SORT_DATE_ASC)) {
							query.addOrder(Order.asc("creationDate"));
						} else if (aCriteria.getSortorder().equals(SiteSearchCommand.SORT_DATE_DESC)) {
							query.addOrder(Order.desc("creationDate"));
						}
						
						query.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

						cntQuery.setProjection(Projections.rowCount());
						cntQuery.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
						
						return new Pagination(cntQuery, query, page, pageSize);
					}
				}
			);	
	}

	
	public Pagination searchAuthAgents(final String name, final int page) {
		return (Pagination) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session) {
						
						Criteria query = session.createCriteria(AuthorisingAgent.class);
						Criteria cntQuery = session.createCriteria(AuthorisingAgent.class);
						
						if(name != null) { 
							query.add(Restrictions.ilike("name", name, MatchMode.START));
							cntQuery.add(Restrictions.ilike("name", name, MatchMode.START));
						}
						
						query.addOrder(Order.asc("name"));
						cntQuery.setProjection(Projections.rowCount());
						
						return new Pagination(cntQuery, query, page, Constants.GBL_PAGE_SIZE);
					}
				}
			);	
	}
	
	
	
	public int countSites() {
		return (Integer) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session) {
						Criteria query = session.createCriteria(Site.class);
						query.setProjection(Projections.rowCount());						
						query.add(Restrictions.eq("active", true));
						
						Integer count = (Integer) query.uniqueResult();
		                
		                return count;
					}
				}
			);	
	}
	
	public Permission loadPermission(long permOid) {
		Permission perm = (Permission) getSession().load(Permission.class, permOid);
		Hibernate.initialize(perm.getUrls());
		return perm;
	}	

	/**
	 * Get a count of the number of seeds related to a given permission.
	 * @param aPermissionOid The permission oid
	 * @return The number of seeds linked to the permission
	 */
	public int countLinkedSeeds(final Long aPermissionOid) {
		return (Integer) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session) {
						return session.createCriteria(Seed.class)
							.createCriteria("permissions")
							.add(Restrictions.eq("oid", aPermissionOid))
							.setProjection(Projections.rowCount())
							.uniqueResult();
					}
				}
			);			
	}

	/**
	 * Check that the Authorising Agent name is unique.
	 * @param oid  The OID of the authorising agent, if available.
	 * @param name The name of the authorising agent.
	 * @return True if unique; otherwise false.
	 */
    public boolean isAuthAgencyNameUnique(final Long oid, final String name) {
		int count = (Integer) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session) {
						Criteria query = session.createCriteria(AuthorisingAgent.class);
						
						if(oid != null) { 
							query.add(Restrictions.ne("oid", oid));
						}
						
						query.add(Restrictions.ilike("name", name, MatchMode.START))
							.setProjection(Projections.rowCount());
						
						return query.uniqueResult();
					}
				}
			);
		
		return count == 0;
    }	
	
	
}
