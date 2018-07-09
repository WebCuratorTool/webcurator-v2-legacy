package org.webcurator.core.scheduler;

import org.webcurator.core.notification.MockInTrayManager;
import org.webcurator.core.util.TestAuditor;
import org.webcurator.domain.IndicatorCriteriaDAO;
import org.webcurator.domain.IndicatorDAO;
import org.webcurator.domain.MockAnnotationDAO;
import org.webcurator.domain.MockIndicatorCriteriaDAO;
import org.webcurator.domain.MockIndicatorDAO;
import org.webcurator.domain.MockProfileDAO;
import org.webcurator.domain.MockTargetInstanceDAO;
import org.webcurator.domain.TargetInstanceDAO;

public class MockTargetInstanceManager extends TargetInstanceManagerImpl {

	private TargetInstanceDAO mTargetInstanceDao = null;
	private MockIndicatorDAO mIndicatorDAO = null;
	private MockIndicatorCriteriaDAO mIndicatorCriteriaDAO = null;
	
	public MockTargetInstanceManager(String filename) 
	{
		super();
		this.setAnnotationDAO(new MockAnnotationDAO(filename));
		this.setProfileDAO(new MockProfileDAO(filename));
		this.setAuditor(new TestAuditor());
		this.setInTrayManager(new MockInTrayManager(filename));
		
		mIndicatorCriteriaDAO = new MockIndicatorCriteriaDAO(filename);
		this.setIndicatorCriteriaDAO(mIndicatorCriteriaDAO);
		mIndicatorDAO = new MockIndicatorDAO(filename);
		this.setIndicatorDAO(mIndicatorDAO);

		mTargetInstanceDao = new MockTargetInstanceDAO(filename);
		this.setTargetInstanceDao(mTargetInstanceDao);
	}
	
	public TargetInstanceDAO getTargetInstanceDAO()
	{
		return mTargetInstanceDao;
	}
	
	public IndicatorDAO getIndicatorDAO() {
		return mIndicatorDAO;
	}

	public IndicatorCriteriaDAO getIndicatorCriteriaDAO() {
		return mIndicatorCriteriaDAO;
	}
}
