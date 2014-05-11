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
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.webcurator.core.exceptions.WCTInvalidStateRuntimeException;
import org.webcurator.core.exceptions.WCTRuntimeException;
import org.webcurator.domain.model.auth.Agency;
import org.webcurator.domain.model.core.AbstractTarget;
import org.webcurator.domain.model.core.Target;
import org.webcurator.domain.model.core.TargetInstance;
import org.webcurator.domain.model.core.Profile;
import org.webcurator.domain.model.dto.ProfileDTO;

/**
 * The implementation of the ProfileDAO interface.
 * @author bbeaumont
 */
public class ProfileDAOImpl extends BaseDAOImpl implements ProfileDAO {
	/** Logger for this class */
	private Log log = LogFactory.getLog(ProfileDAOImpl.class);
	
	
	public Profile load(Long oid) {
		return (Profile) getHibernateTemplate().load(Profile.class, oid);
	}

	public void saveOrUpdate(final Profile aProfile) {
		txTemplate.execute(
				new TransactionCallback() {
					public Object doInTransaction(TransactionStatus ts) {
						try { 
							log.debug("Before Saving of Profile: " + aProfile.getName());
							getSession().saveOrUpdate(aProfile);
							log.debug("After Saving Profile: " + aProfile.getName());
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
	

	@SuppressWarnings("unchecked")
	public List<ProfileDTO> getAllDTOs() {
		return getHibernateTemplate().findByNamedQuery(Profile.QRY_GET_ALL_DTOS);
	}		

	@SuppressWarnings("unchecked")
	public List<ProfileDTO> getDTOs(boolean showInactive) {
		if(showInactive) {
			return getAllDTOs();
		}
		else {
			return getHibernateTemplate().findByNamedQuery(Profile.QRY_GET_ACTIVE_DTOS);
		}
	}		
	
	@SuppressWarnings("unchecked")
	public List<ProfileDTO> getAgencyDTOs(Agency agency, boolean showInactive) {
		if(showInactive) {
			return getHibernateTemplate().findByNamedQueryAndNamedParam(Profile.QRY_GET_AGENCY_DTOS, "agencyOid", agency.getOid());
		}
		else {
			return getHibernateTemplate().findByNamedQueryAndNamedParam(Profile.QRY_GET_ACTIVE_AGENCY_DTOS, "agencyOid", agency.getOid());
		}
	}		
	
	@SuppressWarnings("unchecked")
	public ProfileDTO getDTO(final Long aOid) {
		List dtos = getHibernateTemplate().findByNamedQueryAndNamedParam(Profile.QRY_GET_DTO, "oid", aOid);		
		return (ProfileDTO) dtos.iterator().next();		
	}	
	
	@SuppressWarnings("unchecked")
	public ProfileDTO getLockedDTO(final Long aOrigOid, final Integer aVersion) {
		ProfileDTO theDTO = null;
		String[] paramNames = {"origOid", "version"};
		Object[] paramValues = {aOrigOid, aVersion};
		List dtos = getHibernateTemplate().findByNamedQueryAndNamedParam(Profile.QRY_GET_LOCKED_DTO, paramNames, paramValues);
		if(dtos.iterator().hasNext())
		{
			theDTO = (ProfileDTO) dtos.iterator().next();
		}
		return theDTO;
	}	

	/* (non-Javadoc)
	 * @see org.webcurator.domain.ProfileDAO#getDefaultProfile(org.webcurator.domain.model.auth.Agency)
	 */
	public Profile getDefaultProfile(Agency anAgency) {
		Criteria query = getSession().createCriteria(Profile.class);
		query.createCriteria("owningAgency").add(Restrictions.eq("oid", anAgency.getOid()));
		query.add(Restrictions.eq("defaultProfile", true));
		query.add(Restrictions.eq("status", Profile.STATUS_ACTIVE));
		
		return (Profile) query.uniqueResult();
	}

	/**
	 * Get available profiles.
	 */
	@SuppressWarnings("unchecked")
	public List<ProfileDTO> getAvailableProfiles(final Agency anAgency, final int level, final Long currentProfileOid) {
		return (List<ProfileDTO>) getHibernateTemplate().execute(new HibernateCallback() {

			public Object doInHibernate(Session aSession) throws HibernateException, SQLException {
				Query query = aSession.getNamedQuery(Profile.QRY_GET_AVAIL_DTOS);
				query.setLong("agencyOid", anAgency.getOid());
				query.setInteger("requiredLevel", level);
				query.setBoolean("default", true);
				query.setLong("currentProfileOid", currentProfileOid);
				return query.list();
			} 			
		});		
	}

	/**
	 * Counts the number of Targets, and Target Groups
	 * @param aProfile The profile to count.
	 * @return The number of targets or groups using that profile.
	 */
	public int countProfileUsage(final Profile aProfile) {
		return (Integer) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session) {						
						int targetCount = (Integer) session.createCriteria(AbstractTarget.class)
										.setProjection(Projections.rowCount())
										.createCriteria("profile")
										.add(Restrictions.eq("oid", aProfile.getOid()))
										.uniqueResult();
						
						targetCount += (Integer) session.createCriteria(TargetInstance.class)
						.setProjection(Projections.rowCount())
						.createCriteria("lockedProfile")
						.add(Restrictions.eq("origOid", aProfile.getOrigOid()))
						.add(Restrictions.eq("version", aProfile.getVersion()))
						.uniqueResult();
		
						return targetCount;
					}
				}
			);	
		
	}
	
	/**
	 * Counts the number of Active Targets 
	 * that are currently using this profile.
	 * @param aProfile The profile to count.
	 * @return The number of active targets using that profile.
	 */
	public int countProfileActiveTargets(final Profile aProfile) {
		return (Integer) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session) {						
						int targetCount = (Integer) session.createCriteria(AbstractTarget.class)
										.setProjection(Projections.rowCount())
										.add(Restrictions.eq("objectType", AbstractTarget.TYPE_TARGET))
										.add(Restrictions.eq("state", Target.STATE_APPROVED))
										.createCriteria("profile").add(Restrictions.eq("oid", aProfile.getOid()))
										.uniqueResult();
						
						return targetCount;
					}
				}
			);	
		
	}
	
	/**
	 * Set the profile as the default for this agency.
	 * @param aProfile The profile to set as default.
	 */
	public void setProfileAsDefault(final Profile aProfile) {
		txTemplate.execute(
				new TransactionCallback() {
					public Object doInTransaction(TransactionStatus ts) {
						Query q = getSession().createQuery("select p.status from Profile p where p.oid = :oid");
						q.setLong("oid", aProfile.getOid());
						
						Integer status = (Integer) q.uniqueResult();
						if (status.intValue() == Profile.STATUS_INACTIVE) {
							throw new WCTInvalidStateRuntimeException("Profile " + aProfile.getOid() + " is inactive and cannot be set to be the default profile.");
						}
						if (status.intValue() == Profile.STATUS_LOCKED) {
							throw new WCTInvalidStateRuntimeException("Profile " + aProfile.getOid() + " is locked and cannot be set to be the default profile.");
						}
						
						try { 
							getSession().createQuery("UPDATE Profile p SET p.defaultProfile = :def, p.version = p.version + 1 WHERE p.owningAgency.oid = :agencyOid AND p.oid <> :newDefault")
								.setBoolean("def", false)
								.setLong("agencyOid", aProfile.getOwningAgency().getOid())
								.setLong("newDefault", aProfile.getOid())
								.executeUpdate();
							
							getSession().createQuery("UPDATE Profile p SET p.defaultProfile = :def, p.version = p.version + 1 WHERE p.owningAgency.oid = :agencyOid AND p.oid = :newDefault")
								.setBoolean("def", true)
								.setLong("agencyOid", aProfile.getOwningAgency().getOid())
								.setLong("newDefault", aProfile.getOid())
								.executeUpdate();
							
						}
						catch(Exception ex) {
							log.debug("Setting Rollback Only");
							ts.setRollbackOnly();
							throw new WCTRuntimeException("Failed to set default profile" ,ex);
						}
						return null;
					}
				}
		);		
	}	
	
	
}
