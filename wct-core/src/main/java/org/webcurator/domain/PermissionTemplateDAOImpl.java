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

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.webcurator.domain.model.core.Permission;
import org.webcurator.domain.model.core.PermissionTemplate;

/**
 * The implementation of the PermissionTemplateDAO interface.
 * @author bprice
 */
public class PermissionTemplateDAOImpl extends HibernateDaoSupport implements PermissionTemplateDAO {

    private Log log = LogFactory.getLog(PermissionTemplateDAOImpl.class);
    
    private TransactionTemplate txTemplate;
    
    public PermissionTemplateDAOImpl() {

    }

    public PermissionTemplate getTemplate(Long oid) {
        return (PermissionTemplate)getHibernateTemplate().load(PermissionTemplate.class,oid);
    }

    public List getTemplates(Long agencyOid) {
        Object[] params = new Object[] {agencyOid};
        return getHibernateTemplate().findByNamedQuery(PermissionTemplate.QRY_GET_TEMPLATES_BY_AGENCY, params);
    }

    public List getAllTemplates() {
        return getHibernateTemplate().loadAll(PermissionTemplate.class);
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

    public Permission getPermission(Long oid) {
        return (Permission)getHibernateTemplate().load(Permission.class,oid);
    }

    public void delete(final Object aObject) {
        txTemplate.execute(
                new TransactionCallback() {
                    public Object doInTransaction(TransactionStatus ts) {
                        try {
                            log.debug("Before Delete of Object");
                            getHibernateTemplate().delete(aObject);
                            log.debug("After Delete Object");
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
