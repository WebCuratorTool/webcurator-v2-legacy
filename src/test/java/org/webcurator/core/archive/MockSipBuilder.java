package org.webcurator.core.archive;

import org.webcurator.core.targets.MockTargetManager;
import org.webcurator.core.scheduler.MockTargetInstanceManager;

public class MockSipBuilder extends SipBuilder 
{
	public MockSipBuilder(String filename)
	{
		this.setTargetInstanceManager(new MockTargetInstanceManager(filename));
		this.setTargetManager(new MockTargetManager(filename));
	}
}
