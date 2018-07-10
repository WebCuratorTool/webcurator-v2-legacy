package org.webcurator.domain;

import org.webcurator.domain.model.HibernateTest;

import java.util.List;

public interface HibernateTestDAO extends BaseDAO {

    public List<HibernateTest> getAll();
}
