package org.webcurator.ui.util;

import java.util.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.webcurator.ui.target.command.TargetAccessCommand;
import org.webcurator.ui.target.command.TargetGeneralCommand;
import org.webcurator.ui.target.validator.TargetAccessValidator;
import org.webcurator.ui.target.validator.TargetAccessValidatorTest;
import org.webcurator.ui.util.TabbedController.TabbedModelAndView;

import junit.framework.TestCase;

public class TabbedContollerTest extends TestCase {
	
	private static Log log = LogFactory.getLog(TargetAccessValidatorTest.class);
	
	private TestTabbedController tc;
	private List<String> methodsCalled;
	
	@Before
	public void setUp() throws Exception {
		tc = new TestTabbedController();
		methodsCalled = new ArrayList<String>();
		tc.setMethodsCalled(methodsCalled);
		
		TestTabHandler tabHandler = new TestTabHandler();
		tabHandler.setMethodsCalled(methodsCalled);
		
		List<Tab> tabs = new ArrayList<Tab>();
		
		Tab tab = new Tab();
		tab.setTitle("tab1");
		tab.setPageId("1");
		tab.setTabHandler(tabHandler);
		tabs.add(tab);
		
		tab = new Tab();
		tab.setTitle("tab2");
		tab.setPageId("2");
		tab.setTabHandler(tabHandler);
		tabs.add(tab);
		
		tab.setTabHandler(tabHandler);
		
		TabConfig tabConfig = new TabConfig();
		tabConfig.setTabs(tabs);
		tc.setTabConfig(tabConfig);
		
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testShowForm() {
		//boolean result;
		try {
			assertTrue(tc!=null);		
			MockHttpServletRequest req = new MockHttpServletRequest();
			MockHttpServletResponse res = new MockHttpServletResponse();
			req.addParameter("_tab_edit", "_tab_edit");
			req.addParameter("_tab_current_page","1");
			TabbedModelAndView tmav = (TabbedModelAndView)tc.processFormSubmission(req, res, null, null);
			assertTrue(methodsCalled.get(0) == "TabbedController.switchToEditMode");
			assertTrue(methodsCalled.get(1) == "TabHandler.preProcessNextTab");
			assertTrue(tmav.getTabStatus().getCurrentTab().getPageId() == "1");

		}
		catch (Exception e)
		{
			String message = e.getClass().toString() + " - " + e.getMessage();
			log.debug(message);
			fail(message);
		}
	}
}
