package org.webcurator.domain;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.HibernateCallback;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.webcurator.domain.model.core.HibernateTest;

import java.util.List;

@Repository
@Transactional
public class HibernateTestDAOImpl extends BaseDAOImpl implements HibernateTestDAO {
    private Log log = LogFactory.getLog(HibernateTestDAOImpl.class);

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private HibernateTemplate transactionTemplate;

    @Override
    @SuppressWarnings("unchecked")
    public List<HibernateTest> getAll() {
        Query query = sessionFactory.getCurrentSession().getNamedQuery(HibernateTest.QRY_GET_ALL);
        List results = query.list();
        return results;
    }

    @Override
    public void saveOrUpdate(final HibernateTest hibernateTest) {
        transactionTemplate.execute(
                new HibernateCallback() {
                    public Object doInHibernate(Session session) {
                        try {
                            log.debug("Before Saving of Object");
                            session.saveOrUpdate(hibernateTest);
                            log.debug("After Saving Object");
                        }
                        catch(Exception ex) {
                            log.warn("Setting Rollback Only",ex);
                            session.getTransaction().setRollbackOnly();
                        }
                        return null;
                    }
                }
        );
    }

    @Override
    public int maxID() {
        Query query = sessionFactory.getCurrentSession().getNamedQuery(HibernateTest.QRY_MAX_ID);
        Integer max = (Integer) query.getSingleResult();
        return max;
    }

}
