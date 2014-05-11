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
package org.webcurator.core.report;

import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.webcurator.domain.model.report.LogonDuration;

/**
 * Class for marking a User's login / logout, storing his/her associated session
 * @author MDubos
 *
 */
public class LogonDurationDAOImpl extends HibernateDaoSupport implements LogonDurationDAO {
	
    private static Log log = LogFactory.getLog(LogonDurationDAOImpl.class); 
    
    private TransactionTemplate txTemplate = null;

    /**
	 * User has logged in
	 * @param sessionId User's session id
	 * @param loggedInTime Time of login
	 * @param userId User's id
	 * @param username Username
	 * @param userRealName User real name
	 */
	public void setLoggedIn(String sessionId, Date loggedInTime, Long userId, 
			String username, String userRealName) {
		
		log.debug("setLoggedIn: sID=" + sessionId + " in=" + loggedInTime + 
				" uId=" + userId + " un=" + username + " uRN=" + userRealName);
			
		final LogonDuration ld = new LogonDuration();
		ld.setSessionId(sessionId);
		ld.setLogonTime(loggedInTime);
		ld.setUserOid(userId);
		ld.setUserName(username);
		ld.setUserRealName(userRealName);
		
		txTemplate.execute(
				new TransactionCallback(){
					public Object doInTransaction(TransactionStatus ts){
						try {
							log.debug("Before saving of the LogonDuration");
							getHibernateTemplate().saveOrUpdate(ld);
							log.debug("After saving of the LogonDuration");
						} catch (DataAccessException ex) {
							log.warn("Setting Rollback Only",ex);
                            ts.setRollbackOnly();
						}
						return null;
					}
				}
		);

	}
	
	
	/**
	 * User has logged out
	 * @param sessionId User's session id
	 * @param loggedOutTime Time of logout
	 */
	public void setLoggedOut(String sessionId, Date loggedOutTime){
		
		// Find associated login record
		Object[] params = new Object[] { sessionId };
		List results = getHibernateTemplate().findByNamedQuery(LogonDuration.QRY_LOGON_DURATION_BY_SESSION, params);
		
		log.debug("setLoggedOut sId=" + sessionId + " found " + (results == null ? "null" : results.size()) );
		
		if(results.size() > 0){
		
			// Set logout and duration (in seconds)
			final LogonDuration ld = (LogonDuration) results.get(0);
			ld.setLogoutTime(loggedOutTime);			
			ld.computeAndSetDuration(false);
			
			txTemplate.execute(
					new TransactionCallback(){
						public Object doInTransaction(TransactionStatus ts){
							try {
								log.debug("Before saving of the LogonDuration");
								getHibernateTemplate().saveOrUpdate(ld);
								log.debug("After saving of the LogonDuration");
							} catch (DataAccessException ex) {
								log.warn("Setting Rollback Only",ex);
	                            ts.setRollbackOnly();
							}
							return null;
						}
					}
			);
			
		} else{
			log.warn("No login associated found for session: " + sessionId);
		}
		
	}
	
	
	/**
	 * Make sure that all durations of previous logins are 
	 * set for the current user. I.e previous session are 
	 * all closed even if Tomcat has been shut down e.g.
	 *
	 */
	@SuppressWarnings("unchecked")
	public void setProperLoggedoutForCurrentUser(Long currentUserOid, String currentUserSessionId){
		Object[] params = new Object[]{currentUserOid.longValue(), currentUserSessionId};
		List<LogonDuration> results = getHibernateTemplate().findByNamedQuery(LogonDuration.QRY_UNPROPER_LOGGED_OUT_SESSIONS_FOR_CURRENT_USER, params);
		
		for(LogonDuration ld : results){
			log.warn("closing logonDuration: " + ld.getOid().longValue() + " (session: " + ld.getSessionId() + ")..." );
						
			final LogonDuration finalLd = ld.clone();
			finalLd.computeAndSetDuration(true);
			
			txTemplate.execute(
					new TransactionCallback(){
						public Object doInTransaction(TransactionStatus ts){
							try {
								getHibernateTemplate().saveOrUpdate(finalLd);
							} catch (DataAccessException ex) {
								log.warn("Setting Rollback Only",ex);
	                            ts.setRollbackOnly();
							}
							return null;
						}
					}
			);
			
		}
		
	}
	
    public void setTxTemplate(TransactionTemplate txTemplate) {
        this.txTemplate = txTemplate;
    }

}
