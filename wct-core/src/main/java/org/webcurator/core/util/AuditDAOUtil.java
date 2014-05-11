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
package org.webcurator.core.util;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.webcurator.domain.model.audit.Audit;
import org.webcurator.domain.model.auth.User;

/**
 * Common helper utility to carry out Auditing of actions within the WCT 
 * system.
 * @author bprice
 */
public class AuditDAOUtil extends HibernateDaoSupport implements Auditor {
	/** the logger. */
    private static Log log = LogFactory.getLog(AuditDAOUtil.class); 
    /** the transaction template to use. */
    private TransactionTemplate txTemplate = null;
    
    /**
     * Create an audit entry with the specified data.
     * @param subjectType the class name of the entity the audit belongs to
     * @param action the action name
     * @param message the audit message
     */
    public void audit(String subjectType, String action, String message) {
        final Audit audit = new Audit();
        audit.setAction(action);
        audit.setDateTime(new Date());
        audit.setMessage(message);
        audit.setSubjectType(subjectType);
        
        txTemplate.execute(
                new TransactionCallback() {
                    public Object doInTransaction(TransactionStatus ts) {
                        try { 
                            log.debug("Before Saving of the Audit Object");
                            getHibernateTemplate().saveOrUpdate(audit);
                            log.debug("After Saving of the AUdit Object");
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

    /**
     * Create an audit entry with the specified data.
     * @param subjectType the class name of the entity the audit belongs to
     * @param subjectOid the oid of the entity being audited
     * @param action the action name
     * @param message the audit message
     */
    public void audit(String subjectType, Long subjectOid, String action, String message) {
        
        User user = AuthUtil.getRemoteUserObject();
        if (user == null) {
            //if there is no user information, call the simplified audit message
            this.audit(subjectType, action, message);
            return;
        }
        final Audit audit = new Audit();
        audit.setAction(action);
        audit.setDateTime(new Date());
        audit.setFirstname(user.getFirstname());
        audit.setLastname(user.getLastname());
        audit.setMessage(message);
        audit.setSubjectOid(subjectOid);
        audit.setSubjectType(subjectType);
        audit.setUserName(user.getUsername());
        audit.setUserOid(user.getOid());
        audit.setAgencyOid(user.getAgency().getOid());
                
        txTemplate.execute(
                new TransactionCallback() {
                    public Object doInTransaction(TransactionStatus ts) {
                        try { 
                            log.debug("Before Saving of the Audit Object");
                            getHibernateTemplate().saveOrUpdate(audit);
                            log.debug("After Saving of the AUdit Object");
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
    
    /**
     * Create an audit entry with the specified data.
     * @param aUser the user that generated the audit entry
     * @param subjectType the class name of the entity the audit belongs to
     * @param subjectOid the oid of the entity being audited
     * @param action the action name
     * @param message the audit message
     */
    public void audit(User aUser, String subjectType, Long subjectOid, String action, String message) {
        log.debug("Recieved audit request for " + aUser + " " + subjectType + " " + subjectOid + " " + action + " " + message);
        final Audit audit = new Audit();
        audit.setAction(action);
        audit.setDateTime(new Date());
        audit.setFirstname(aUser.getFirstname());
        audit.setLastname(aUser.getLastname());
        audit.setMessage(message);
        audit.setSubjectOid(subjectOid);
        audit.setSubjectType(subjectType);
        audit.setUserName(aUser.getUsername());
        audit.setUserOid(aUser.getOid());   
        audit.setAgencyOid(aUser.getAgency().getOid());
        
        txTemplate.execute(
                new TransactionCallback() {
                    public Object doInTransaction(TransactionStatus ts) {
                        try { 
                            log.debug("Before Saving of the Audit Object");
                            getHibernateTemplate().saveOrUpdate(audit);
                            log.debug("After Saving of the AUdit Object");
                        }
                        catch(Exception ex) {
                            log.warn("Setting Rollback Only",ex);
                            ts.setRollbackOnly();
                        }
                        return null;
                    }
                }
        );
        
        log.debug("Processed audit request for " + aUser + " " + subjectType + " " + subjectOid + " " + action + " " + message);
    }

    /** 
     * @param txTemplate the transaction template
     */
    public void setTxTemplate(TransactionTemplate txTemplate) {
        this.txTemplate = txTemplate;
    }
}
