package org.webcurator.domain;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.webcurator.domain.model.core.HibernateTest;

import java.util.List;

@Repository
@Transactional
public class HibernateTestDAOImpl extends BaseDAOImpl implements HibernateTestDAO {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    @SuppressWarnings("unchecked")
    public List<HibernateTest> getAll() {
        Query query = sessionFactory.getCurrentSession().getNamedQuery(HibernateTest.QRY_GET_ALL);
        List results = query.list();
        return results;
    }

}
