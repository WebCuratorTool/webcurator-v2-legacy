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
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.webcurator.domain.model.core.Annotation;

/**
 * The implemantation of the AnnotationDAO interface.
 * @author nwaight
 */
public class AnnotationDAOImpl extends HibernateDaoSupport implements AnnotationDAO {
	/** the logger. */
    private Log log = LogFactory.getLog(AnnotationDAOImpl.class);    
    /** The transaction template object to use. */
    private TransactionTemplate txTemplate = null;
    
	/**
	 * @see org.webcurator.domain.AnnotationDAO#loadAnnotations(java.lang.String, java.lang.Long)
	 */
	public List<Annotation> loadAnnotations(final String aType, final Long aOid) {
		if (log.isDebugEnabled()) {
			log.debug("Load annotations for " + aType + " " + aOid);
		}
		Object obj = getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session aSession) throws HibernateException, SQLException {
				Query q = aSession.getNamedQuery(Annotation.QRY_GET_NOTES);
				q.setString(Annotation.PARAM_TYPE, aType);
				q.setLong(Annotation.PARAM_OID, aOid);
				
				return q.list();
			}
		});			
        
        return (List<Annotation>) obj;
	}

	/**
	 * @see org.webcurator.domain.AnnotationDAO#saveAnnotations(java.util.List)
	 */
	public void saveAnnotations(final List<Annotation> aAnnotations) {		
        txTemplate.execute(
            new TransactionCallback() {
                public Object doInTransaction(TransactionStatus ts) {
                    try { 
                    	Annotation a = null;
                    	Iterator<Annotation> it = aAnnotations.iterator();
                    	while (it.hasNext()) {
							a = it.next();							
							if (log.isDebugEnabled()) {
								log.debug("Saving annotation " + a.getNote());
							}
							getSession().saveOrUpdate(a);
						}                    	                       
                    }
                    catch(Exception ex) {
                    	if (log.isWarnEnabled()) {
                    		log.warn("Failed to add annotations " + ex.getMessage(), ex);
                    	}
                        ts.setRollbackOnly();
                    }
                    return null;
                }
            }
        );        
	}
	
	/**
	 * @see org.webcurator.domain.AnnotationDAO#deleteAnnotations(java.util.List)
	 */
	public void deleteAnnotations(final List<Annotation> aAnnotations) {
		txTemplate.execute(
            new TransactionCallback() {
                public Object doInTransaction(TransactionStatus ts) {
                    try { 
                    	Annotation a = null;
                    	Iterator<Annotation> it = aAnnotations.iterator();
                    	while (it.hasNext()) {
							a = it.next();							
							if (log.isDebugEnabled()) {
								log.debug("Deleting annotation " + a.getNote());
							}
							getSession().delete(a);
						}                    	                       
                    }
                    catch(Exception ex) {
                    	if (log.isWarnEnabled()) {
                    		log.warn("Failed to delete annotations " + ex.getMessage(), ex);
                    	}
                        ts.setRollbackOnly();
                    }
                    return null;
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
}
