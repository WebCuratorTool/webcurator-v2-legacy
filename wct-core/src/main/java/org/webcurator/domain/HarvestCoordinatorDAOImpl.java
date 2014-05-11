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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
import org.webcurator.domain.model.core.BandwidthRestriction;

/**
 * The implementation of the HarvestCoordinatorDAO interface.
 * @author nwaight
 */
public class HarvestCoordinatorDAOImpl extends HibernateDaoSupport implements HarvestCoordinatorDAO {
    /** the logger. */
    private Log log = LogFactory.getLog(HarvestCoordinatorDAOImpl.class);    
    /** The transaction template object to use. */
    private TransactionTemplate txTemplate = null;
    
    /** @see org.webcurator.domain.HarvestCoordinatorDAO#getBandwidthRestrictions(). */
    public HashMap<String, List<BandwidthRestriction>> getBandwidthRestrictions() {
        HashMap<String, List<BandwidthRestriction>> allRestrictions = new HashMap<String, List<BandwidthRestriction>>();
        
        List restrictions = getHibernateTemplate().findByNamedQuery(BandwidthRestriction.QUERY_ALL);                      
        
        List<BandwidthRestriction> daysRestrictions = null;
        BandwidthRestriction br = null;
        Iterator it = restrictions.iterator();
        while (it.hasNext()) {
            br = (BandwidthRestriction) it.next();
            daysRestrictions = allRestrictions.get(br.getDayOfWeek());
            if (null == daysRestrictions) {
                daysRestrictions = new ArrayList<BandwidthRestriction>();
            }
            
            daysRestrictions.add(br);
            allRestrictions.put(br.getDayOfWeek(), daysRestrictions);
        }
        
        return allRestrictions;
    }

    /** @see org.webcurator.domain.HarvestCoordinatorDAO#getBandwidthRestriction(Long). */
    public BandwidthRestriction getBandwidthRestriction(Long aOid) {        
        return (BandwidthRestriction) getHibernateTemplate().load(BandwidthRestriction.class, aOid);
    }
    
    /** @see org.webcurator.domain.HarvestCoordinatorDAO#getBandwidthRestriction(String, Date). */
    public BandwidthRestriction getBandwidthRestriction(final String aDay, final Date aTime) {
        Object obj = getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session aSession) throws HibernateException, SQLException {
                Query q = aSession.getNamedQuery(BandwidthRestriction.QUERY_DAY_TIME).setReadOnly(true);
                q.setString(BandwidthRestriction.PARAM_DOW, aDay);
                q.setTimestamp(BandwidthRestriction.PARAM_START, aTime);
                q.setTimestamp(BandwidthRestriction.PARAM_END, aTime);

                return q.uniqueResult();
            }            
        });
        
        if (obj instanceof BandwidthRestriction) {
            return (BandwidthRestriction) obj;
        }
        
        return null;
    }

    /** @see org.webcurator.domain.HarvestCoordinatorDAO#saveOrUpdate(Object). */
    public void saveOrUpdate(final BandwidthRestriction aBandwidthRestriction) {
        if (log.isDebugEnabled()) {
            log.debug("Saving " + aBandwidthRestriction.getClass().getName());
        }
        txTemplate.execute(
            new TransactionCallback() {
                public Object doInTransaction(TransactionStatus ts) {
                    try { 
                        getSession().saveOrUpdate(aBandwidthRestriction);
                    }
                    catch(Exception ex) {
                        ts.setRollbackOnly();
                    }
                    return null;
                }
            }
        );        
    }
    
    /** @see org.webcurator.domain.HarvestCoordinatorDAO#delete(BandwidthRestriction). */
    public void delete(final BandwidthRestriction aBandwidthRestriction) {
    	txTemplate.execute(
                new TransactionCallback() {
                    public Object doInTransaction(TransactionStatus ts) {
                        try { 
                            getSession().delete(aBandwidthRestriction);
                        }
                        catch(Exception ex) {
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
