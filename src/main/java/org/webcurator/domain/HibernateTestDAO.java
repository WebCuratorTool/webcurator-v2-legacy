package org.webcurator.domain;

import org.webcurator.domain.model.core.HibernateTest;

import java.util.List;

public interface HibernateTestDAO extends BaseDAO {

    public List<HibernateTest> getAll();

    /**
     * Save or update a hibernate test to the persistent data store.
     * @param aObject the hibernate test to save or update
     */
    public void saveOrUpdate(HibernateTest hibernateTest);

    public int maxID();
}
