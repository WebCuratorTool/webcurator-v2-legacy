package org.webcurator.core.check;

import static org.junit.Assert.*;

import org.junit.Test;
import org.webcurator.test.BaseWCTHarvestAgentTest;
import org.webcurator.core.harvester.agent.*;

public class HarvestAgentMemoryCheckerTest extends BaseWCTHarvestAgentTest<HarvestAgentMemoryChecker>  
{
	public HarvestAgentMemoryCheckerTest()
	{
		super(HarvestAgentMemoryChecker.class);
	}
	
	public void setUp() throws Exception {
		super.setUp();
		testInstance.setNotificationSubject("Test");
		testInstance.setNotifier(new MockCheckNotifier());
	}

	@Test
	public final void testOnSetWarning() {

		try
		{
			HarvestAgent ha = new MockHarvestAgent();
			ha.setMemoryWarning(false);
			testInstance.setHarvestAgent(ha); 
			
			testInstance.onSetWarning();
			assertTrue(testInstance.getHarvestAgent().getMemoryWarning());
		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}

	@Test
	public final void testOnSetError() {
		try
		{
			testInstance.onSetError();
		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}

	@Test
	public final void testOnRemoveWarning() {
		try
		{
			HarvestAgent ha = new MockHarvestAgent();
			ha.setMemoryWarning(true);
			testInstance.setHarvestAgent(ha); 
			
			testInstance.onRemoveWarning();
			assertFalse(testInstance.getHarvestAgent().getMemoryWarning());
		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}

	@Test
	public final void testOnRemoveError() {
		try
		{
			testInstance.onRemoveError();
		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}

	@Test
	public final void testGetHarvestAgent() {
		testSetHarvestAgent();
		assertTrue(testInstance.getHarvestAgent() != null);
	}

	@Test
	public final void testSetHarvestAgent() {
		testInstance.setHarvestAgent(new MockHarvestAgent()); 
		assertTrue(testInstance.getHarvestAgent() != null);
	}

	@Test
	public final void testCheck() {
		try
		{
			HarvestAgent ha = new MockHarvestAgent();
			ha.setMemoryWarning(false);
			testInstance.setHarvestAgent(ha); 

			//Test below threshold
			long totalMemory = Runtime.getRuntime().totalMemory()/1024;
			long maxMemory = Runtime.getRuntime().maxMemory()/1024;

			testInstance.setErrorThreshold(maxMemory);

			testInstance.setWarnThreshold(totalMemory);
			testInstance.check();
			assertFalse(testInstance.getHarvestAgent().getMemoryWarning());

			//Test above threshold
			testInstance.setWarnThreshold(0);
			testInstance.check();
			assertTrue(testInstance.getHarvestAgent().getMemoryWarning());

			//Test return to below threshold
			testInstance.setWarnThreshold(totalMemory);
			testInstance.check();
			assertFalse(testInstance.getHarvestAgent().getMemoryWarning());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}

}
