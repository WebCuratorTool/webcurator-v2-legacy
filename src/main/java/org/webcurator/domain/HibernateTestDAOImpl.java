package org.webcurator.domain;

import org.webcurator.domain.model.core.HibernateTest;

import java.util.List;

public class HibernateTestDAOImpl extends BaseDAOImpl implements HibernateTestDAO {

    @Override
    @SuppressWarnings("unchecked")
    public List<HibernateTest> getAll() {
        return getHibernateTemplate().findByNamedQuery(HibernateTest.QRY_GET_ALL);
    }

}
