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
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.webcurator.core.common.EnvironmentFactory;
import org.webcurator.core.exceptions.WCTRuntimeException;
import org.webcurator.core.targets.PermissionCriteria;
import org.webcurator.domain.model.core.AbstractTarget;
import org.webcurator.domain.model.core.AbstractTargetGroupTypeView;
import org.webcurator.domain.model.core.GroupMember;
import org.webcurator.domain.model.core.Permission;
import org.webcurator.domain.model.core.Schedule;
import org.webcurator.domain.model.core.Seed;
import org.webcurator.domain.model.core.Target;
import org.webcurator.domain.model.core.TargetGroup;
import org.webcurator.domain.model.core.TargetInstance;
import org.webcurator.domain.model.dto.AbstractTargetDTO;
import org.webcurator.domain.model.dto.GroupMemberDTO;
import org.webcurator.domain.model.dto.GroupMemberDTO.SAVE_STATE;
import org.webcurator.ui.common.Constants;
import org.webcurator.ui.target.command.TargetSearchCommand;
import org.webcurator.ui.util.Utils;

/**
 * The implementation of the TargetDAO interface.
 * @see TargetDAO
 * @author bbeaumont
 */
public class TargetDAOImpl extends BaseDAOImpl implements TargetDAO {
	private Log log = LogFactory.getLog(TargetDAOImpl.class);
	
	public void save(final Target aTarget) {
		save(aTarget, null);
	}
	

	
	public void save(final Target aTarget, final List<GroupMemberDTO> parents) {
		txTemplate.execute(
				new TransactionCallback() {
					public Object doInTransaction(TransactionStatus ts) {
						try { 
							log.debug("Before Saving of Target");
							getSession().saveOrUpdate(aTarget);
							
							if(parents != null) {
								for(GroupMemberDTO parent : parents) {
									switch(parent.getSaveState()) { 
									case NEW:
										GroupMember member = new GroupMember();
										TargetGroup grp = loadGroup(parent.getParentOid());
										member.setParent(grp);
										member.setChild(aTarget);
										grp.getChildren().add(member);
										aTarget.getParents().add(member);
										getSession().save(member);									
										break;
										
									case DELETED:
										getSession().createQuery("delete GroupMember where child.oid = :childOid and parent.oid = :parentOid")
						   				.setLong("childOid", aTarget.getOid())
						   				.setLong("parentOid", parent.getParentOid())
						   				.executeUpdate();									
										break;
									}								
								}
							}
							
							log.debug("After Saving Target");
						}
						catch(Exception ex) {
							log.debug("Setting Rollback Only", ex);
							ts.setRollbackOnly();
							throw new WCTRuntimeException("Failed to save target", ex);
						}
						return null;
					}
				}
		);	
	}
	
	
	
	public void save(final Schedule aSchedule) {
		txTemplate.execute(
				new TransactionCallback() {
					public Object doInTransaction(TransactionStatus ts) {
						try { 
							log.debug("Before Saving of Schedule");
							getSession().saveOrUpdate(aSchedule);
							log.debug("After Saving Schedule");
						}
						catch(Exception ex) {
							log.debug("Setting Rollback Only", ex);
							ts.setRollbackOnly();
							throw new WCTRuntimeException("Failed to save schedule", ex);
						}
						return null;
					}
				}
		);	
	}	
	
	public void save(final TargetGroup aTargetGroup, final boolean withChildren, final List<GroupMemberDTO> parents) {
		txTemplate.execute(
				new TransactionCallback() {
					public Object doInTransaction(TransactionStatus ts) {
						try { 
							log.debug("Before Saving of TargetGroup");
						
							getSession().saveOrUpdate(aTargetGroup);
							
							// Save all the new children.
							if(withChildren) {
								List<GroupMemberDTO> groupMemberDTOs = aTargetGroup.getNewChildren();
								for(GroupMemberDTO dto : groupMemberDTOs) {
									
									// Only consider new items not in the "Remove Children" list.
									if(!aTargetGroup.getRemovedChildren().contains(dto.getChildOid())) {
										GroupMember member = new GroupMember();
										member.setParent(aTargetGroup);
										AbstractTarget child = loadAbstractTarget(dto.getChildOid());
										member.setChild(child);
										getSession().save(member);
									}
								}
								
								// Delete all the removed children.
								for(Long childOid : aTargetGroup.getRemovedChildren()) {
									getSession().createQuery("delete GroupMember where child.oid = :childOid and parent.oid = :parentOid")
									   				.setLong("childOid", childOid)
									   				.setLong("parentOid", aTargetGroup.getOid())
									   				.executeUpdate();
								}
							}
							
							if(parents != null) {
								for(GroupMemberDTO parent : parents) {
									switch(parent.getSaveState()) { 
									case NEW:
										GroupMember member = new GroupMember();
										TargetGroup grp = loadGroup(parent.getParentOid());
										member.setParent(grp);
										member.setChild(aTargetGroup);
										grp.getChildren().add(member);
										aTargetGroup.getParents().add(member);
										getSession().save(member);									
										break;
										
									case DELETED:
										getSession().createQuery("delete GroupMember where child.oid = :childOid and parent.oid = :parentOid")
						   				.setLong("childOid", aTargetGroup.getOid())
						   				.setLong("parentOid", parent.getParentOid())
						   				.executeUpdate();									
										break;
									}								
								}
							}
							
							log.debug("After Saving TargetGroup");
						}
						catch(Exception ex) {
							log.debug("Setting Rollback Only", ex);
							ts.setRollbackOnly();
							throw new WCTRuntimeException("Failed to save Target Group", ex);
						}
						return null;
					}
				}
		);	
	}		
	
	public Target load(long targetOid) {
		return load(targetOid, false);
	}
	
	public Target load(final long targetOid, final boolean fullyInitialise) {
		
		return (Target) getHibernateTemplate().execute(new HibernateCallback() {

			public Object doInHibernate(Session aSession) throws HibernateException, SQLException {
				if(!fullyInitialise) {
					Target aTarget = (Target) aSession.load(Target.class, targetOid);
					aTarget.setDirty(false);
					return aTarget;
				}
				else {
					// Initialise some more items that we'll need. This is used
					// to prevent lazy load exceptions, since we're doing things
					// across multiple sessions.
					Target t = (Target) aSession.load(Target.class, targetOid);
					
					Hibernate.initialize(t.getSeeds());
					Hibernate.initialize(t.getSchedules());					
					Hibernate.initialize(t.getOverrides());
					Hibernate.initialize(t.getOverrides().getExcludeUriFilters());
					Hibernate.initialize(t.getOverrides().getIncludeUriFilters());
					Hibernate.initialize(t.getOverrides().getCredentials());
					
					for(Seed s: t.getSeeds()) {
						Hibernate.initialize(s.getPermissions());
					}
					
					t.setDirty(false);
					
					return t;
				}
			} 			
		});
	}
	
	
	public Pagination getMembers(final TargetGroup aTargetGroup, final int pageNum, final int pageSize) {
		if(aTargetGroup.isNew()) {
			return new Pagination(aTargetGroup.getNewChildren(), pageNum, pageSize);
		}
		else {
			return (Pagination) getHibernateTemplate().execute(
					new HibernateCallback() {
						public Object doInHibernate(Session session) {
							Query query = session.getNamedQuery(GroupMember.QUERY_GET_MEMBERS);
							Query cntQuery = session.getNamedQuery(GroupMember.QUERY_CNT_MEMBERS);
							query.setLong("parentOid", aTargetGroup.getOid());
							cntQuery.setLong("parentOid", aTargetGroup.getOid());
							Pagination pagination = new Pagination(aTargetGroup.getNewChildren(), cntQuery, query, pageNum, pageSize);
							return pagination;
						}
					}
					);
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<Integer> getSavedMemberStates(final TargetGroup aTargetGroup) {
		if(aTargetGroup.isNew()) {
			return new LinkedList<Integer>();
		}
		else {
			return (List<Integer>) getHibernateTemplate().execute(
					new HibernateCallback() {
						@SuppressWarnings("unchecked")
						public Object doInHibernate(Session session) {
							Query q = session.getNamedQuery(GroupMember.QUERY_GET_MEMBERSTATES);
							q.setLong("parentOid", aTargetGroup.getOid());
							List<Integer> states = q.list();
							
							return states;
						}
					}
				);			
		}		
	}	

		
	@SuppressWarnings("unchecked")
	public List<GroupMemberDTO> getParents(final AbstractTarget aTarget) {
		if(aTarget.isNew()) {
			return new LinkedList<GroupMemberDTO>();
		}
		else {
			return (List<GroupMemberDTO>) getHibernateTemplate().execute(
					new HibernateCallback() {
						@SuppressWarnings("unchecked")
						public Object doInHibernate(Session session) {
							Query q = session.getNamedQuery(GroupMember.QUERY_GET_PARENTS);
							q.setLong("childOid", aTarget.getOid());
							List<GroupMemberDTO> dtos = q.list();
							
							for(GroupMemberDTO dto: dtos) {
								dto.setSaveState(SAVE_STATE.ORIGINAL);
							}
							
							return dtos;
						}
					}
				);			
		}		
	}	
	
	
	public Pagination getParents(final AbstractTarget aTarget, final int pageNum, final int pageSize) {
		if(aTarget.isNew()) {
			return new Pagination(aTarget.getNewParents(), pageNum, pageSize);
		}
		else {
			return (Pagination) getHibernateTemplate().execute(
					new HibernateCallback() {
						public Object doInHibernate(Session session) {
							Query query = session.getNamedQuery(GroupMember.QUERY_GET_PARENTS);
							Query cntQuery = session.getNamedQuery(GroupMember.QUERY_CNT_PARENTS);
							query.setLong("childOid", aTarget.getOid());
							cntQuery.setLong("childOid", aTarget.getOid());
							//FIXME Need to get the new parent groups.
							Pagination pagination = new Pagination(aTarget.getNewParents(), cntQuery, query, pageNum, pageSize);
							return pagination;
						}
					}
					);
		}		
	}

	public Pagination getTargetsForProfile(final int pageNumber, final int pageSize, final Long profileOid, final String agencyName) {
		return (Pagination) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session) {
						
						Criteria query = session.createCriteria(Target.class);
						Criteria cntQuery = session.createCriteria(Target.class);
						Criteria ownerCriteria = null;
						Criteria cntOwnerCriteria = null;
						
						if(profileOid != null) {
							query.add(Restrictions.eq("t.profile.oid", profileOid));
							cntQuery.add(Restrictions.eq("t.profile.oid", profileOid));
						}
						
						if(!Utils.isEmpty(agencyName)) {
							if(ownerCriteria == null) { 
								ownerCriteria = query.createCriteria("owner"); 
								cntOwnerCriteria = cntQuery.createCriteria("owner"); 
							}
							ownerCriteria.createCriteria("agency").add(Restrictions.eq("name", agencyName));
							cntOwnerCriteria.createCriteria("agency").add(Restrictions.eq("name", agencyName));
						}
						
						query.addOrder(Order.asc("name"));

						cntQuery.setProjection(Projections.rowCount());
						
						return new Pagination(cntQuery, query, pageNumber, pageSize);
					}
				}
			);	
	}
	
	public Pagination getAbstractTargetDTOsForProfile(final int pageNumber, final int pageSize, final Long profileOid) {
		return (Pagination) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session) {
						
						Query q = session.getNamedQuery(AbstractTarget.QUERY_TARGET_DTOS_BY_PROFILE);
						Query cq = session.getNamedQuery(AbstractTarget.QUERY_CNT_TARGET_DTOS_BY_PROFILE);
						q.setLong("profileoid", profileOid);
						cq.setLong("profileoid", profileOid);

						return new Pagination(cq, q, pageNumber, pageSize);
					}
				}
			);	
	}
	
	
	public Pagination search(final int pageNumber, final int pageSize, final Long searchOid, final String targetName, final Set<Integer> states, final String seed, final String username, final String agencyName, final String memberOf, final boolean nondisplayonly, final String sortorder, final String description) {
		return (Pagination) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session) {
						
						Criteria query = session.createCriteria(Target.class);
						Criteria cntQuery = session.createCriteria(Target.class);
						Criteria ownerCriteria = null;
						Criteria cntOwnerCriteria = null;
						
						if(targetName != null && !"".equals(targetName.trim())) {
							query.add(Restrictions.ilike("name", targetName, MatchMode.START));
							cntQuery.add(Restrictions.ilike("name", targetName, MatchMode.START));
						}
						
						if(description != null && !"".equals(description.trim())) {
							query.add(Restrictions.ilike("description", description, MatchMode.ANYWHERE));
							cntQuery.add(Restrictions.ilike("description", description, MatchMode.ANYWHERE));
						}
						
						if(states != null && states.size() > 0) {
							Disjunction stateDisjunction = Restrictions.disjunction();
							for(Integer i: states) {
								stateDisjunction.add(Restrictions.eq("state", i));
							}
							query.add(stateDisjunction);
							cntQuery.add(stateDisjunction);
						}
						
						if(seed != null && !"".equals(seed.trim())) {
							query.createCriteria("seeds").add(Restrictions.like("seed", seed, MatchMode.START));
							cntQuery.createCriteria("seeds").add(Restrictions.like("seed", seed, MatchMode.START));
						}
						
						if(!Utils.isEmpty(username)) {
							if(ownerCriteria == null) { 
								ownerCriteria = query.createCriteria("owner"); 
								cntOwnerCriteria = cntQuery.createCriteria("owner");
							}
							ownerCriteria.add(Restrictions.eq("username", username));
							cntOwnerCriteria.add(Restrictions.eq("username", username));
						}
						
						// Parents criteria.
						if(!Utils.isEmpty(memberOf)) {
							query.createCriteria("parents").createCriteria("parent").add(Restrictions.ilike("name", memberOf, MatchMode.START));
							cntQuery.createCriteria("parents").createCriteria("parent").add(Restrictions.ilike("name", memberOf, MatchMode.START));
						}
						
						if(!Utils.isEmpty(agencyName)) {
							if(ownerCriteria == null) { 
								ownerCriteria = query.createCriteria("owner"); 
								cntOwnerCriteria = cntQuery.createCriteria("owner"); 
							}
							ownerCriteria.createCriteria("agency").add(Restrictions.eq("name", agencyName));
							cntOwnerCriteria.createCriteria("agency").add(Restrictions.eq("name", agencyName));
						}
						
						if(searchOid != null) {
							query.add(Restrictions.eq("oid", searchOid));
							cntQuery.add(Restrictions.eq("oid", searchOid));
						}
						
						if (nondisplayonly) {
							query.add(Restrictions.eq("displayTarget", false));
							cntQuery.add(Restrictions.eq("displayTarget", false));
						}
						
						if(sortorder == null || sortorder.equals(TargetSearchCommand.SORT_NAME_ASC)) {
							query.addOrder(Order.asc("name"));
						} else if (sortorder.equals(TargetSearchCommand.SORT_NAME_DESC)) {
							query.addOrder(Order.desc("name"));
						} else if (sortorder.equals(TargetSearchCommand.SORT_DATE_ASC)) {
							query.addOrder(Order.asc("creationDate"));
						} else if (sortorder.equals(TargetSearchCommand.SORT_DATE_DESC)) {
							query.addOrder(Order.desc("creationDate"));
						}
						cntQuery.setProjection(Projections.rowCount());
						
						return new Pagination(cntQuery, query, pageNumber, pageSize);
					}
				}
			);	
	}
	
	public Pagination searchGroups(final int pageNumber, final int pageSize, final Long searchOid, final String name, final String owner, final String agency, final String memberOf, final String groupType, final boolean nondisplayonly) {
		return (Pagination) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session) {
						
						Criteria query = session.createCriteria(TargetGroup.class);
						Criteria cntQuery = session.createCriteria(TargetGroup.class);
						Criteria ownerCriteria = null;
						Criteria cntOwnerCriteria = null;
						
						if(name != null && !"".equals(name.trim())) {
							query.add(Restrictions.ilike("name", name, MatchMode.START));
							cntQuery.add(Restrictions.ilike("name", name, MatchMode.START));
						}
						
						if(!Utils.isEmpty(owner)) {
							if(ownerCriteria == null) { 
								ownerCriteria = query.createCriteria("owner"); 
								cntOwnerCriteria = cntQuery.createCriteria("owner");
							}
							ownerCriteria.add(Restrictions.eq("username", owner));
							cntOwnerCriteria.add(Restrictions.eq("username", owner));
						}
						
						// Parents criteria.
						if(!Utils.isEmpty(memberOf)) {
							query.createCriteria("parents").createCriteria("parent").add(Restrictions.ilike("name", memberOf, MatchMode.START));
							cntQuery.createCriteria("parents").createCriteria("parent").add(Restrictions.ilike("name", memberOf, MatchMode.START));
						}

						// Group Type criteria.
						if(!Utils.isEmpty(groupType)) {
							query.add(Restrictions.eq("type", groupType));
							cntQuery.add(Restrictions.eq("type", groupType));
						}
						
						if(!Utils.isEmpty(agency)) {
							if(ownerCriteria == null) { 
								ownerCriteria = query.createCriteria("owner");
								cntOwnerCriteria = cntQuery.createCriteria("owner");
							}
							ownerCriteria.createCriteria("agency").add(Restrictions.eq("name", agency));
							cntOwnerCriteria.createCriteria("agency").add(Restrictions.eq("name", agency));
						}
						
						if(searchOid != null) {
							query.add(Restrictions.eq("oid", searchOid));
							cntQuery.add(Restrictions.eq("oid", searchOid));
						}
						
						if (nondisplayonly) {
							query.add(Restrictions.eq("displayTarget", false));
							cntQuery.add(Restrictions.eq("displayTarget", false));
						}
						
						query.addOrder(Order.asc("name"));
						
						cntQuery.setProjection(Projections.rowCount());
						
						return new Pagination(cntQuery, query, pageNumber, pageSize);
					}
				}
			);	
	}

	public int countTargets(final String username) {
		return (Integer) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session) {						
						Criteria query = session.createCriteria(Target.class);
						Criteria ownerCriteria = null;
						query.setProjection(Projections.rowCount());
						if(!Utils.isEmpty(username)) {
							ownerCriteria = query.createCriteria("owner").add(Restrictions.eq("username", username));
						}
						
						Integer count = (Integer) query.uniqueResult();
		                
		                return count;
					}
				}
			);	
	}
	
	public int countTargetGroups(final String username) {
		return (Integer) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session) {						
						Criteria query = session.createCriteria(TargetGroup.class);
						Criteria ownerCriteria = null;
						query.setProjection(Projections.rowCount());
						if(!Utils.isEmpty(username)) {
							if(ownerCriteria == null) { ownerCriteria = query.createCriteria("owner"); }
							ownerCriteria.add(Restrictions.eq("username", username));
						}
						
						Integer count = (Integer) query.uniqueResult();
		                
		                return count;
					}
				}
			);	
	}
	
	/**
	 * @param txTemplate The txTemplate to set.
	 */
	public void setTxTemplate(TransactionTemplate txTemplate) {
		this.txTemplate = txTemplate;
	}

	public boolean isNameOk(AbstractTarget aTarget) {
		Criteria criteria = getSession().createCriteria(AbstractTarget.class);
		criteria.setProjection(Projections.rowCount());
		criteria.add(Restrictions.eq("name", aTarget.getName()));
		if (aTarget instanceof TargetGroup) {
			criteria.add(Restrictions.eq("objectType", 0));
		}
		if (aTarget instanceof Target) {
			criteria.add(Restrictions.eq("objectType", 1));
		}
		if( aTarget.getOid() != null) {
			criteria.add(Restrictions.ne("oid", aTarget.getOid()));
		}
		
		Integer count = (Integer) criteria.uniqueResult();
		
		return count.intValue() == 0;
	}

	public Pagination getAbstractTargetDTOs(final String name, final int pageNumber, final int pageSize) {
		return (Pagination) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session) {
						
						Query q = session.getNamedQuery(AbstractTarget.QUERY_DTO_BY_NAME);
						Query cq = session.getNamedQuery(AbstractTarget.QUERY_CNT_DTO_BY_NAME);
						q.setString(0, name);
						cq.setString(0, name);

						return new Pagination(cq, q, pageNumber, pageSize);
					}
				}
			);	
	}
	
	public Pagination getGroupDTOs(final String name, final int pageNumber, final int pageSize) {
		return (Pagination) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session) {
						
						Query q = session.getNamedQuery(AbstractTarget.QUERY_GROUP_DTOS_BY_NAME);
						Query cq = session.getNamedQuery(AbstractTarget.QUERY_CNT_GROUP_DTOS_BY_NAME);
						q.setString(0, name);
						cq.setString(0, name);

						return new Pagination(cq, q, pageNumber, pageSize);
					}
				}
			);	
	}
	
	public Pagination getSubGroupParentDTOs(final String name, final List types, final int pageNumber, final int pageSize) {
		return (Pagination) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session) {
						
						Query q = session.getNamedQuery(TargetGroup.QUERY_GROUP_DTOS_BY_NAME_AND_TYPE);
						Query cq = session.getNamedQuery(TargetGroup.QUERY_CNT_GROUP_DTOS_BY_NAME_AND_TYPE);
						q.setParameter("name", name);
						q.setParameterList("types", types);
						cq.setParameter("name", name);
						cq.setParameterList("types", types);

						return new Pagination(cq, q, pageNumber, pageSize);
					}
				}
			);	
	}
	
	public Pagination getNonSubGroupDTOs(final String name, final String subGroupType, final int pageNumber, final int pageSize) {
		return (Pagination) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session) {
						
						Query q = session.getNamedQuery(AbstractTargetGroupTypeView.QUERY_NON_SUBGROUP_DTOS_BY_NAME_AND_TYPE);
						Query cq = session.getNamedQuery(AbstractTargetGroupTypeView.QUERY_CNT_NON_SUBGROUP_DTOS_BY_NAME_AND_TYPE);
						q.setParameter("name", name);
						q.setParameter("subgrouptype", subGroupType);
						cq.setParameter("name", name);
						cq.setParameter("subgrouptype", subGroupType);

						return new Pagination(cq, q, pageNumber, pageSize);
					}
				}
			);	
	}
	
	public AbstractTargetDTO loadAbstractTargetDTO(final Long oid) {
		return (AbstractTargetDTO) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session) {
						return session.getNamedQuery(AbstractTarget.QUERY_DTO_BY_OID)
									.setLong("oid", oid)
									.uniqueResult();
					}
				}
			);		
	}

	public TargetGroup loadGroup(long targetGroupOid) {
		return loadGroup(targetGroupOid, false);
	}

	public TargetGroup loadGroup(final long targetGroupOid, final boolean fullyInitialise) {
		return (TargetGroup) getHibernateTemplate().execute(new HibernateCallback() {

			public Object doInHibernate(Session aSession) throws HibernateException, SQLException {
				if(!fullyInitialise) {
					TargetGroup aTargetGroup = (TargetGroup) aSession.load(TargetGroup.class, targetGroupOid);
					aTargetGroup.setDirty(false);
					return aTargetGroup;
				}
				else {
					// Initialise some more items that we'll need. This is used
					// to prevent lazy load exceptions, since we're doing things
					// across multiple sessions.
					TargetGroup t = (TargetGroup) aSession.load(TargetGroup.class, targetGroupOid);
					
					Hibernate.initialize(t.getSchedules());					
					Hibernate.initialize(t.getOverrides());
					Hibernate.initialize(t.getOverrides().getExcludeUriFilters());
					Hibernate.initialize(t.getOverrides().getIncludeUriFilters());
					Hibernate.initialize(t.getOverrides().getCredentials());
					//Hibernate.initialize(t.getChildren());
					
					t.setDirty(false);
					
					return t;
				}
			} 			
		});
	}
	
	
	public AbstractTarget loadAbstractTarget(Long oid) {
		return (AbstractTarget) getHibernateTemplate().load(AbstractTarget.class, oid);
	}
	
	public void refresh(Object anObject) {
		getSession().refresh(anObject);
	}
	
	public TargetGroup reloadTargetGroup(Long oid) {
		// Evict the group from the session and reload.		
		getSession().evict(getHibernateTemplate().load(TargetGroup.class, oid));
		
		return (TargetGroup) getHibernateTemplate().load(TargetGroup.class, oid);		
	}
	
	public Target reloadTarget(Long oid) {
		// Evict the group from the session and reload.		
		getSession().evict(getHibernateTemplate().load(Target.class, oid));
		
		return (Target) getHibernateTemplate().load(Target.class, oid);		
	}
	
	public Date getLatestScheduledDate(final AbstractTarget aTarget, final Schedule aSchedule) {
		return (Date) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session aSession) {
				Query query = aSession.getNamedQuery(TargetInstance.QUERY_GET_LATEST_FOR_TARGET);
				query.setLong("targetOid", aTarget.getOid());
				query.setLong("scheduleOid", aSchedule.getOid());
				
				Date dt = (Date) query.uniqueResult();
				return dt;
			}
		});
	}
	
	
	@SuppressWarnings("unchecked")
	public Set<Seed> getSeeds(final Target aTarget) {
		Set<Seed> seeds = new HashSet<Seed>();
		seeds.addAll( (List) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session aSession) {
				Query q = aSession.createQuery("from Seed s where s.target.oid = :targetOid");
				q.setLong("targetOid", aTarget.getOid());
				return q.list();
			}
		}));		
		return seeds;
	}
	
	
	@SuppressWarnings("unchecked")
	public Set<Seed> getSeeds(final TargetGroup aTarget, final Long agencyOid, final String subGroupTypeName) {
		Set<Seed> seeds = new HashSet<Seed>();
		seeds.addAll( (Set) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session aSession) {
				TargetGroup tg = (TargetGroup) aSession.load(TargetGroup.class, aTarget.getOid());
				return getSeeds(aSession, tg);
			}
			
			public Set<Seed> getSeeds(Session aSession, AbstractTarget target) {
				if(target.getObjectType() == AbstractTarget.TYPE_GROUP) {
					Set<Seed> seeds = new HashSet<Seed>();
					for(GroupMember groupMember: ((TargetGroup)target).getChildren()) {
						AbstractTarget child = groupMember.getChild();
						
						if(child.getObjectType() == AbstractTarget.TYPE_GROUP) {
							TargetGroup childGroup;
							if(child instanceof TargetGroup)
							{
								childGroup = (TargetGroup) child;
							}
							else
							{
								childGroup = (TargetGroup) aSession.load(TargetGroup.class, child.getOid());
							}
							
							//If the childGroup is a sub-group, we don't want to include the seeds from the sub-group members
							if(!subGroupTypeName.equals(childGroup.getType()))
							{
								seeds.addAll(getSeeds(aSession, childGroup));
							}
						}
						else {
							Target childTarget;
							if(child instanceof Target)
							{
								childTarget = (Target) child;
							}
							else
							{
								childTarget = (Target) aSession.load(Target.class, child.getOid());
							}
							
							if(	isApprovedForHarvest(childTarget) && childTarget.getOwner().getAgency().getOid().equals(agencyOid)) {
								seeds.addAll(childTarget.getSeeds());
							}
						}
					}
					return seeds;
				}
				else {
					return target.getSeeds();
				}
			}
			
		}));		
		return seeds;
	}
	
    private boolean isApprovedForHarvest(Target aTarget) {
    	
    	if(	aTarget.getState() == Target.STATE_APPROVED || aTarget.getState() == Target.STATE_COMPLETED) {    	
			boolean approved = false;
			boolean foundBadSeed = false;
			
			Seed seed = null;
			Set<Seed> seeds = aTarget.getSeeds();
			Iterator<Seed> it = seeds.iterator();
			while (it.hasNext()) {
				seed = (Seed) it.next();
				if (!seed.isHarvestable(new Date())) {
					foundBadSeed = true;
					break;
				}
			}
			
			if (!seeds.isEmpty() && !foundBadSeed) {
				approved = true;
			}
			
			return approved;
    	}
    	else {
    		return false;
    	}
    }	
    
    public boolean causesLoop(TargetGroup parentOid, AbstractTarget childOid) {
    	
    	
    	
    	return false;
    }
    
    @SuppressWarnings("unchecked")
	public Set<Long> getAncestorOids(final Long childOid) {
    	if(childOid == null) { 
    		return Collections.EMPTY_SET;
    	}
    	else {
	    	Set<Long> parentOids = new HashSet<Long>();
	    	
	    	List<Long> immediateParents = getHibernateTemplate().executeFind(new HibernateCallback() {
	
				public Object doInHibernate(Session aSession) throws HibernateException, SQLException {
					//Criteria q = aSession.createCriteria("new java.lang.Long(oid) FROM TargetGroup").createCriteria("children").add(Restrictions.eq("oid", childOid));
					Query q = aSession.createQuery("SELECT new java.lang.Long(gm.parent.oid) FROM GroupMember gm where gm.child.oid = :childOid");
					q.setLong("childOid", childOid);
					return q.list();
				}
	    		
	    	});
	    	
	    	for(Long parentOid: immediateParents) {
	    		parentOids.add(parentOid);
	    		parentOids.addAll(getAncestorOids(parentOid));
	    	}
	    	
	    	return parentOids;
    	}
    }
    
    
    
    @SuppressWarnings("unchecked")
	public Set<AbstractTargetDTO> getAncestorDTOs(final Long childOid) {
    	if(childOid == null) { 
    		return Collections.EMPTY_SET;
    	}
    	else {
	    	Set<AbstractTargetDTO> parents = new HashSet<AbstractTargetDTO>();
	    	
	    	List<AbstractTargetDTO> immediateParents = getHibernateTemplate().executeFind(new HibernateCallback() {
	
				public Object doInHibernate(Session aSession) throws HibernateException, SQLException {
//					Criteria c = aSession.createCriteria("new org.webcurator.domain.model.dto.AbstractTargetDTO(t.oid, t.name, t.owner.oid, t.owner.username, t.owner.agency.name, t.state, t.profile.oid, t.objectType)", "TargetGroup");
//					c.createCriteria("children").createCriteria("child").add(Restrictions.eq("oid", childOid));
//					return c.list();
					
					Query q = aSession.createQuery("SELECT new org.webcurator.domain.model.dto.AbstractTargetDTO(t.oid, t.name, t.owner.oid, t.owner.username, t.owner.agency.name, t.state, t.profile.oid, t.objectType) FROM TargetGroup t LEFT JOIN t.children AS gm INNER JOIN gm.child AS child where child.oid = :childOid");
					q.setLong("childOid", childOid);
					return q.list();
				}
	    		
	    	});
	    	
	    	for(AbstractTargetDTO parent: immediateParents) {
	    		parents.add(parent);
	    		parents.addAll(getAncestorDTOs(parent.getOid()));
	    	}
	    	
	    	return parents;
    	}
    }    
    
    
    
    @SuppressWarnings("unchecked")
	public Set<Long> getImmediateChildrenOids(final Long parentOid) {
    	if(parentOid == null) {
    		return Collections.EMPTY_SET;
    	}
    	else {
	    	List<Long> immediateChildren = getHibernateTemplate().executeFind(new HibernateCallback() {
				public Object doInHibernate(Session aSession) throws HibernateException, SQLException {
					//Criteria q = aSession.createCriteria("new java.lang.Long(oid) FROM TargetGroup").createCriteria("children").add(Restrictions.eq("oid", childOid));
					Query q = aSession.createQuery("SELECT new java.lang.Long(gm.child.oid) FROM GroupMember gm where gm.parent.oid = :parentOid");
					q.setLong("parentOid", parentOid);
					return q.list();
				}
	    	});
    	
	    	Set<Long> retval = new HashSet<Long>();
	    	retval.addAll(immediateChildren);
	    	return retval;
    	}
    }
    
	/**
	 * Find all the groups that need to be end dated.
	 * @return A List of groups to be end dated.
	 */
	@SuppressWarnings("unchecked")
	public List<TargetGroup> findEndedGroups() {
		return getHibernateTemplate().executeFind(new HibernateCallback() {
			public Object doInHibernate(Session aSession) throws HibernateException, SQLException {
				List<TargetGroup> results = aSession.createCriteria(TargetGroup.class)
					.add(Restrictions.ne("state", TargetGroup.STATE_ACTIVE))
					.add(Restrictions.lt("toDate", new Date()))
					.setFetchMode("schedules", FetchMode.JOIN)
					.setFetchMode("parents", FetchMode.JOIN)
					.setFetchMode("children", FetchMode.JOIN)
					.list();
				
				log.debug("Found " + results.size() + " groups that need to be unscheduled");
				
				return results;
			}
		});
	}

	
	/**
	 * Load the persisted target group SIP type from the database.
	 * @return oid The OID of the TargetGroup.
	 */
	public Integer loadPersistedGroupSipType(final Long oid) {
		return (Integer) getHibernateTemplate().execute(new HibernateCallback() {

			public Object doInHibernate(Session aSession) throws HibernateException, SQLException {
				return (Integer) aSession.createQuery("SELECT new java.lang.Integer(sipType) FROM TargetGroup WHERE oid=:groupOid")
					.setLong("groupOid", oid)
					.uniqueResult();
			}
		});
	}
	
	
	
	@SuppressWarnings("unchecked")
	public List<Seed> getLinkedSeeds(final Permission aPermission) {
		return getHibernateTemplate().find("FROM Seed WHERE permissions.oid = ?", aPermission.getOid());
	}
	
	
	/**
	 * Transfer all seeds from one permission to another.
	 * @param fromPermissionOid The oid of the permission record to transfer
	 *                          seeds from.
	 * @param toPermissionOid   The oid of the permission record to transfer
	 *                          seeds to.
	 */
	public void transferSeeds(final Long fromPermissionOid, final Long toPermissionOid) {
		txTemplate.execute(
			new TransactionCallback() {
				public Object doInTransaction(TransactionStatus ts) {
					try {
						return getSession().createQuery("UPDATE SEED_PERMISSION SET SP_PERMISSION_ID = :toPermissionOid WHERE SP_PERMISSION_ID = :fromPermissionOid")
							.setLong("toPermissionOid", toPermissionOid)
							.setLong("fromPermissionOid", fromPermissionOid)
							.executeUpdate();
						
					}
					catch(Exception ex) {
						ts.setRollbackOnly();
						log.error("Exception transferring seeds", ex);
						throw new WCTRuntimeException("Exception transferring seeds", ex);
					}
				}
			}
		);			
	}
	
	/**
	 * Basic save all method. This will save all of the objects in the 
	 * collection to the database but will perform nothing more than the 
	 * Hibernate save/cascade logic. It should not be used to save a collection
	 * of targets or target groups.
	 * @param collection A collection of objects to be saved.
	 */
	public void saveAll(final Collection collection) {
        txTemplate.execute(
                new TransactionCallback() {
                    public Object doInTransaction(TransactionStatus ts) {
                        try { 
                            log.debug("Before Saving Object");
                            for(Object o: collection) {
                            	getSession().saveOrUpdate(o);
                            }
                            log.debug("After Saving Object");
                        }
                        catch(Exception ex) {
                            log.debug("Setting Rollback Only");
                            ts.setRollbackOnly();
                        }
                        return null;
                    }
                }
        );
	}
	
	private boolean nullOrEmpty(String aString) {
		return aString == null || "".equals(aString.trim());
	}
	
	/**
	 * Search the Permissions.
	 * @param aPermissionCriteria The criteria to use to search the permissions.
	 * @return A Pagination of permission records.
	 */
	public Pagination searchPermissions(final PermissionCriteria aPermissionCriteria) {
		return (Pagination) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session) {
						
						Criteria query = session.createCriteria(Permission.class);
						Criteria cntQuery = session.createCriteria(Permission.class);
						
						if(!nullOrEmpty(aPermissionCriteria.getSiteName())) {
							query.createCriteria("site")
								.add(Restrictions.ilike("title", aPermissionCriteria.getSiteName(), MatchMode.START));
							cntQuery.createCriteria("site")
								.add(Restrictions.ilike("title", aPermissionCriteria.getSiteName(), MatchMode.START));
						}
						
						if(!nullOrEmpty(aPermissionCriteria.getUrlPattern())) {
							query.createCriteria("urls")
								.add(Restrictions.ilike("pattern", aPermissionCriteria.getUrlPattern(), MatchMode.START));
							cntQuery.createCriteria("urls")
								.add(Restrictions.ilike("pattern", aPermissionCriteria.getUrlPattern(), MatchMode.START));							
						}
						
						if(aPermissionCriteria.getAgencyOid() != null) {
							query.createCriteria("owningAgency")
								.add(Restrictions.eq("oid", aPermissionCriteria.getAgencyOid()));
							cntQuery.createCriteria("owningAgency")
								.add(Restrictions.eq("oid", aPermissionCriteria.getAgencyOid()));							
						}
						
						query.setFetchMode("permissions", FetchMode.JOIN);
						cntQuery.setFetchMode("permissions", FetchMode.JOIN);
						cntQuery.setProjection(Projections.rowCount());
						
						return new Pagination(cntQuery, query, aPermissionCriteria.getPageNumber(), Constants.GBL_PAGE_SIZE);
					}
				}
			);			
	}
	
	/**
	 * Delete a pending target.
	 * @param aTarget The Target to be deleted.
	 */
	public void delete(final Target aTarget) {
        txTemplate.execute(
                new TransactionCallback() {
                    public Object doInTransaction(TransactionStatus ts) {
                        try { 
                            log.debug("Before Deleting Object");
                            getSession().delete(aTarget);
                            log.debug("Object deleted successfully");
                        }
                        catch(Exception ex) {
                            log.error("Setting Rollback Only");
                            ts.setRollbackOnly();
                        }
                        return null;
                    }
                }
        );		
	}
	
	/**
	 * Delete a TargetGroup as long as it has no Target Instances associated
	 * with it.
	 * @param aTargetGroup The target group to delete.
	 * @return true if deleted; otherwise false.
	 */
	public boolean deleteGroup(final TargetGroup aTargetGroup) {
        return (Boolean) txTemplate.execute(
                new TransactionCallback() {
                    public Object doInTransaction(TransactionStatus ts) {
                        try { 
                            log.debug("Before Deleting Object");
                            
                            // Step one - check that the target group has 
                            // no target instances.
                            Criteria criteria = getSession()
                            	.createCriteria(TargetInstance.class)
                            	.createCriteria("schedule")
                            	.createCriteria("target")
                            	.add(Restrictions.eq("oid", aTargetGroup.getOid()))
                            	.setProjection(Projections.rowCount());
                            
                            Integer count = (Integer) criteria.uniqueResult();
                                                        
                            // If there are instances, we can't delete the object.
                            if(count.intValue() > 0) { 
                            	log.debug("Delete failed due to target instances existing");
                            	return false;
                            }
                            
                            // There are no instances, so delete away.
                            else {
                            	// Delete all links to parents and children.
                                getSession()
                                	.createQuery("delete from GroupMember g where g.child.oid = :groupOid or g.parent.oid = :groupOid")
                                	.setLong("groupOid", aTargetGroup.getOid())
                                	.executeUpdate();
                                
                                // Finally delete the group.
                                getSession().delete(aTargetGroup);
                                
                                log.debug("Delete Successful");
                                
                            	return true;
                            }
                        }
                        catch(Exception ex) {
                            log.error("Setting Rollback Only", ex);
                            ts.setRollbackOnly();
                            return false;
                        }
                        
                    }
                }
        );		
	}
	
	/**
	 * Get schedules to re-run
	 */
	@SuppressWarnings("unchecked")
	public List<Schedule> getSchedulesToRun() {
    	
		return (List<Schedule>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session) {
						
						final Calendar cal = Calendar.getInstance();
						
				    	cal.setTime(new Date());
				    	cal.add(Calendar.DAY_OF_MONTH, EnvironmentFactory.getEnv().getDaysToSchedule());
				    	cal.set(Calendar.HOUR_OF_DAY, 23);
				    	cal.set(Calendar.MINUTE, 59);
				    	cal.set(Calendar.SECOND, 59);
				    	cal.set(Calendar.MILLISECOND, 999);
				    	
				    	List<Schedule> schedules = session.createCriteria(Schedule.class)
				    		.add(Restrictions.le("nextScheduleAfterPeriod", cal.getTime()))
				    		.list();
				    	
				    	for(Schedule s: schedules) {
				        	if (s.getTarget() == null) {
				        		System.out.println("Schedule has null target so skipping initialisation: " + s.getOid());
				        		log.debug("Schedule has null target so skipping initialisation: " + s.getOid());
				        	} else {
					        	log.debug("Initialising target and children for schedule: " + s.getOid());
					    		initTargetAndChildrenInSession(s.getTarget(), session);
				        	}
				    	}
				    	
				    	return schedules;
					}
				}
			);			
	}
	
	private void initTargetAndChildrenInSession(AbstractTarget aTarget, Session session) {
		log.debug("Initialising target and children for abstract target: " + aTarget.getOid());
		if(aTarget.getObjectType() == AbstractTarget.TYPE_GROUP) {
			TargetGroup group = loadGroup(aTarget.getOid(), true);
			if(group.getSipType() == TargetGroup.MANY_SIP) {
				log.debug("Initialising a target group.");
				Hibernate.initialize(group);
				Hibernate.initialize(group.getChildren());
				for(GroupMember gm: group.getChildren()) {
					AbstractTarget childTarget = (AbstractTarget) session.load(AbstractTarget.class, gm.getChild().getOid());
					initTargetAndChildrenInSession(childTarget, session);
				}
			}
		}
		else {
			log.debug("Initialising a target.");
			Hibernate.initialize(aTarget);
		}
	}
}