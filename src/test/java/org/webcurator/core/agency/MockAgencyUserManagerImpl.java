package org.webcurator.core.agency;

import org.webcurator.auth.*;
import org.webcurator.domain.*;
import org.webcurator.core.util.*;
import org.webcurator.core.profiles.MockProfileManager;

public class MockAgencyUserManagerImpl extends AgencyUserManagerImpl {

	public MockAgencyUserManagerImpl(String filename) {
		super();
		this.setAuthorityManager(new AuthorityManagerImpl());
		this.setUserRoleDAO(new MockUserRoleDAO(filename));
		this.setAuditor(new TestAuditor());
		this.setProfileManager(new MockProfileManager(filename));
		this.setRejReasonDAO(new MockRejReasonDAO(filename));
	}

}
