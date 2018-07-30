package org.webcurator.domain;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.webcurator.domain.model.core.HibernateTest;

import java.util.List;

@Repository
@Transactional
public class HibernateTestDAOImpl extends HibernateDaoSupport implements HibernateTestDAO {
    private static Log log = LogFactory.getLog(HibernateTestDAOImpl.class);

    private TransactionTemplate txTemplate;

    @Override
    @SuppressWarnings("unchecked")
    public List<HibernateTest> getAll() {
        Query query = currentSession().getNamedQuery(HibernateTest.QRY_GET_ALL);
        List results = query.list();
        return results;
    }

    @Override
    public void saveOrUpdate(final HibernateTest hibernateTest) {
        txTemplate.execute(
                new TransactionCallback() {
                    public Object doInTransaction(TransactionStatus ts) {
                        try {
                            log.debug("Before Saving of Object");
                            currentSession().saveOrUpdate(hibernateTest);
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

    @Override
    public int maxID() {
        Query query = currentSession().getNamedQuery(HibernateTest.QRY_MAX_ID);
        Integer max = (Integer) query.getSingleResult();
        return max;
    }

    public void setTxTemplate(TransactionTemplate txTemplate) {
        this.txTemplate = txTemplate;
    }
}
