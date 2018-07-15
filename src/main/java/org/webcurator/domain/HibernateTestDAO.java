package org.webcurator.domain;

import org.webcurator.domain.model.core.HibernateTest;

import java.util.List;

public interface HibernateTestDAO extends BaseDAO {

    public List<HibernateTest> getAll();
}
