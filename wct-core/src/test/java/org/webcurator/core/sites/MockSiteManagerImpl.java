package org.webcurator.core.sites;

import org.webcurator.domain.*;
import org.webcurator.core.util.*;
import org.webcurator.core.agency.*;
import org.webcurator.core.notification.*;

public class MockSiteManagerImpl extends SiteManagerImpl {

	public MockSiteManagerImpl(String filename) {
		
		super();
		this.setSiteDao(new MockSiteDAO(filename));
		this.setAuditor(new TestAuditor());
		this.setAgencyUserManager(new MockAgencyUserManagerImpl(filename));
		this.setAnnotationDAO(new MockAnnotationDAO(filename));
		this.setIntrayManager(new MockInTrayManager(filename));
	}

}
