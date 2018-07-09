package org.webcurator.core.targets;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.MockMessageSource;
import org.webcurator.auth.AuthorityManagerImpl;
import org.webcurator.core.notification.MockInTrayManager;
import org.webcurator.core.scheduler.MockTargetInstanceManager;
import org.webcurator.core.scheduler.TargetInstanceManager;
import org.webcurator.core.util.TestAuditor;
import org.webcurator.domain.MockAnnotationDAO;
import org.webcurator.domain.MockSiteDAO;
import org.webcurator.domain.MockTargetDAO;
import org.webcurator.domain.MockTargetInstanceDAO;
import org.webcurator.domain.model.core.BusinessObjectFactory;
import org.webcurator.domain.model.core.Target;
import org.webcurator.domain.model.core.TargetGroup;
import org.webcurator.domain.model.core.TargetInstance;
import org.webcurator.test.BaseWCTTest;

public class MembersRemovedEventPropagatorTest extends BaseWCTTest<MembersRemovedEventPropagator>{

	private MockTargetManager targetManager;
	private TargetInstanceManager instanceManager;
	
	public MembersRemovedEventPropagatorTest()
	{
		super(MembersRemovedEventPropagator.class, 
				"src/test/java/org/webcurator/core/targets/MembersRemovedEventPropagatorTest.xml",
				false);
	}
	
	@Before
	public void setUp() throws Exception {
		super.setUp();
		targetManager = new MockTargetManager(testFile);
		instanceManager = targetManager.getInstanceManager();
	}

	@Test
	public final void testRunEventChain() {
		TargetGroup parentGroup = targetManager.loadGroup(15001L);
		TargetGroup childGroup = targetManager.loadGroup(15000L);
		try
		{
			testInstance = new MembersRemovedEventPropagator(targetManager, instanceManager, parentGroup, childGroup);
			TargetInstance ti = instanceManager.getTargetInstance(5003L);
			assertTrue(ti != null);
			testInstance.runEventChain();
			ti = instanceManager.getTargetInstance(5003L);
			assertTrue(ti == null);
		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}

}
