package org.webcurator.core.profiles;

import org.webcurator.auth.*;
import org.webcurator.domain.*;
import org.webcurator.core.util.*;

public class MockProfileManager extends ProfileManager 
{
	public MockProfileManager(String filename)
	{
		super();
		setAuditor(new TestAuditor());
		setAuthorityManager(new AuthorityManagerImpl());
		setProfileDao(new MockProfileDAO(filename));
	}
	
}
