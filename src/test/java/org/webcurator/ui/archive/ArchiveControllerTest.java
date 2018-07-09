package org.webcurator.ui.archive;

import static org.junit.Assert.*;

import java.util.*;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.validation.BindException;
import org.webcurator.test.BaseWCTTest;
import org.webcurator.ui.common.Constants;
import org.webcurator.core.archive.*;
import org.webcurator.core.scheduler.MockTargetInstanceManager;
import org.webcurator.core.scheduler.TargetInstanceManager;
import org.webcurator.core.targets.MockTargetManager;
import org.webcurator.core.targets.TargetManager;
import org.webcurator.domain.model.core.HarvestResult;
import org.webcurator.domain.model.core.TargetInstance;
import org.springframework.web.servlet.ModelAndView;

public class ArchiveControllerTest extends BaseWCTTest<ArchiveController>{

	private TargetInstanceManager targetInstanceManager = null;
	private TargetManager targetManager = null;
	private ArchiveAdapter archiveAdapter = null;

	public ArchiveControllerTest()
	{
		super(ArchiveController.class,
				"src/test/java/org/webcurator/ui/archive/ArchiveControllerTest.xml");
	}
	
	@Test
	public final void testBuildSip() {
		
		testSetArchiveAdapter();
		testSetTargetInstanceManager();
		testSetTargetManager();
		testSetSipBuilder();
		
		try
		{
			MockHttpServletRequest request = new MockHttpServletRequest();
			MockHttpServletResponse response = new MockHttpServletResponse();
			
			List<HarvestResult> harvestResults = new ArrayList<HarvestResult>();
			HarvestResult hr = new HarvestResult();
			hr.setHarvestNumber(1);
			harvestResults.add(hr);
			
			TargetInstance ti = targetInstanceManager.getTargetInstance(5001L);
			ti.setHarvestResults(harvestResults);

			request.addParameter("instanceID", "5001");
			request.addParameter("harvestNumber", "1");
			
			//Note cannot test the output with Mock Objects - so add this attribute ahead of time
			request.setAttribute("xmlData", "");
			
			testInstance.buildSip(request, response, 1);
		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}

	@Test
	public final void testGetCustomDepositFormElementsAsMap() {
		MockHttpServletRequest request;
		Map customDepositFormElements;

		// Case 1 - No custom form filled
		request = new MockHttpServletRequest();
		request.addParameter("customDepositForm_customFormPopulated", "false");
		customDepositFormElements = testInstance.getCustomDepositFormElementsAsMap(request);
		assertNull(customDepositFormElements);
		request = new MockHttpServletRequest();
		request.addParameter("customDepositForm_customFormPopulated", (String)null);
		customDepositFormElements = testInstance.getCustomDepositFormElementsAsMap(request);
		assertNull(customDepositFormElements);

		// Case 2 - Custom form was filled
		request = new MockHttpServletRequest();
		request.addParameter("customDepositForm_customFormPopulated", "true");
		request.addParameter("customDepositForm_ParamX", "ValueX");
		request.addParameter("customDepositForm_ParamY", "ValueY");
		request.addParameter("someOther_ParamZ", "ValueZ");
		customDepositFormElements = testInstance.getCustomDepositFormElementsAsMap(request);
		assertNotNull(customDepositFormElements);
		assertEquals(3, customDepositFormElements.size());
		assertEquals("true", customDepositFormElements.get("customDepositForm_customFormPopulated"));
		assertEquals("ValueX", customDepositFormElements.get("customDepositForm_ParamX"));
		assertEquals("ValueY", customDepositFormElements.get("customDepositForm_ParamY"));
		assertNull(customDepositFormElements.get("someOther_ParamZ"));
	}

	@Test
	public final void testHandle() {
		testSetArchiveAdapter();
		testSetTargetInstanceManager();
		testSetTargetManager();
		testSetSipBuilder();
		
		try
		{
			MockHttpServletRequest request = new MockHttpServletRequest();
			MockHttpServletResponse response = new MockHttpServletResponse();
			
			List<HarvestResult> harvestResults = new ArrayList<HarvestResult>();
			HarvestResult hr = new HarvestResult();
			hr.setHarvestNumber(1);
			harvestResults.add(hr);
			
			TargetInstance ti = targetInstanceManager.getTargetInstance(5001L);
			ti.setHarvestResults(harvestResults);

			request.addParameter("instanceID", "5001");
			request.addParameter("harvestNumber", "1");
			
			//Note cannot test the output with Mock Objects - so add this attribute ahead of time
			request.setAttribute("xmlData", "");
			
			ArchiveCommand comm = new ArchiveCommand();
			comm.setHarvestResultNumber(1);
			comm.setTargetInstanceID(5001);

			BindException errors = new BindException(comm, "ArchiveCommand");
			
			ModelAndView mav = testInstance.handle(request, response, comm, errors);
			assertNotNull(mav);
			assertEquals(mav.getViewName(), "redirect:/curator/target/queue.html?showSubmittedMsg=y");
		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}

	@Test
	public final void testSetArchiveAdapter() {
		
		archiveAdapter = new MockArchiveAdapter();
		
		testInstance.setArchiveAdapter(archiveAdapter);
	}

	@Test
	public final void testSetTargetInstanceManager() {
		targetInstanceManager = new MockTargetInstanceManager(testFile);
		testInstance.setTargetInstanceManager(targetInstanceManager);
	}

	@Test
	public final void testSetTargetManager() {
		targetManager = new MockTargetManager(testFile);
		testInstance.setTargetManager(targetManager);
	}

	@Test
	public final void testSetHeritrixVersion() 
	{
		testInstance.setHeritrixVersion("1.14");
	}

	@Test
	public final void testSetWebCuratorUrl() 
	{
		testInstance.setWebCuratorUrl("http://dummy.url");
	}

	@Test
	public final void testSetSipBuilder() 
	{
		testInstance.setSipBuilder(new MockSipBuilder(testFile));
	}

}
