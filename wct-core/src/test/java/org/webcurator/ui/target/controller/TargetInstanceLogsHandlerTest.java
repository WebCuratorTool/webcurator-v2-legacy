package org.webcurator.ui.target.controller;

import static org.junit.Assert.*;

import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.MockMessageSource;
import org.junit.Test;
import org.springframework.mock.web.*;
import org.springframework.validation.BindException;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.webcurator.test.BaseWCTTest;
import org.webcurator.ui.admin.command.CreateUserCommand;
import org.webcurator.ui.target.command.*;
import org.webcurator.core.agency.*;
import org.webcurator.ui.util.*;
import org.webcurator.core.scheduler.*;
import org.webcurator.auth.AuthorityManagerImpl;
import org.webcurator.domain.model.core.*;
import org.webcurator.ui.target.validator.*;
import org.webcurator.core.exceptions.WCTRuntimeException;
import org.webcurator.core.harvester.coordinator.*;

public class TargetInstanceLogsHandlerTest extends BaseWCTTest<TargetInstanceLogsHandler> {

	public TargetInstanceLogsHandlerTest()
	{
		super(TargetInstanceLogsHandler.class,
				"src/test/java/org/webcurator/ui/target/controller/targetinstancelogshandlertest.xml");
	}
	
	public void setUp() throws Exception 
	{
		super.setUp();
		DateUtils.get().setMessageSource(new MockMessageSource());
	}
	
	private List<Tab> getTabList(TargetInstanceManager targetInstanceManager)
	{
		List<Tab> tabs = new ArrayList<Tab>();
		
		Tab tabGeneral = new Tab();
		tabGeneral.setCommandClass(TargetInstanceCommand.class);
		tabGeneral.setJsp("../target-instance-general.jsp");
		tabGeneral.setPageId("GENERAL");
		tabGeneral.setTitle("general");
		tabGeneral.setValidator(new TargetInstanceValidator());

		TargetInstanceGeneralHandler genHandler = new TargetInstanceGeneralHandler();
		genHandler.setAgencyUserManager(new MockAgencyUserManagerImpl(testFile));
		genHandler.setAuthorityManager(new AuthorityManagerImpl());
		genHandler.setTargetInstanceManager(targetInstanceManager);
		//genHandler.setHarvestCoordinator(new MockHarvestCoordinator());
		genHandler.setHarvestCoordinator(new HarvestCoordinatorImpl());
		tabGeneral.setTabHandler(genHandler);
		
		tabs.add(tabGeneral);
		
		Tab tabLogs = new Tab();
		tabLogs.setCommandClass(TargetInstanceCommand.class);
		tabLogs.setJsp("../target-instance-logs.jsp");
		tabLogs.setPageId("LOGS");

		TargetInstanceLogsHandler logsHandler = new TargetInstanceLogsHandler();
		logsHandler.setTargetInstanceManager(targetInstanceManager);
		tabLogs.setTabHandler(logsHandler);
		
		tabs.add(tabLogs);
		return tabs;
	}
	
	
	@Test
	public final void testProcessTab() {
		
		HttpServletRequest aReq = new MockHttpServletRequest();
		TargetInstanceManager targetInstanceManager = new MockTargetInstanceManager(testFile);
		testInstance.setTargetInstanceManager(targetInstanceManager);
		TargetInstance targetInstance = targetInstanceManager.getTargetInstance(5001L);
		
		aReq.getSession().setAttribute(TargetInstanceCommand.SESSION_TI, targetInstance);
		aReq.getSession().setAttribute(TargetInstanceCommand.SESSION_MODE, true);

		HttpServletResponse aResp = new MockHttpServletResponse(); 
		TargetInstanceCommand aCmd = new TargetInstanceCommand(targetInstance);
		TabbedController tc = new TabbedTargetInstanceController();

		TabConfig tabConfig = new TabConfig();
		tabConfig.setViewName("targetInstance");
		List<Tab> tabs = getTabList(targetInstanceManager);
		tabConfig.setTabs(tabs);

		tc.setTabConfig(tabConfig);
		tc.setDefaultCommandClass(org.webcurator.ui.target.command.TargetInstanceCommand.class);
		
		Tab currentTab = tabs.get(1);
		aCmd.setCmd(TargetInstanceCommand.ACTION_EDIT);
		BindException aErrors = new BindException(aCmd, aCmd.getCmd());
		testInstance.processTab(tc, currentTab, aReq, aResp, aCmd, aErrors);
		// processTab currently returns with no processing done.
		assertTrue(aErrors.getAllErrors().size()==0);
	}

	@Test
	public final void testPreProcessNextTab() {
		
		HttpServletRequest aReq = new MockHttpServletRequest();
		TargetInstanceManager targetInstanceManager = new MockTargetInstanceManager(testFile);
		testInstance.setTargetInstanceManager(targetInstanceManager);
		TargetInstance targetInstance = targetInstanceManager.getTargetInstance(5001L);
		testInstance.setHarvestCoordinator(new MockHarvestCoordinator());
		
		aReq.getSession().setAttribute(TargetInstanceCommand.SESSION_TI, targetInstance);
		aReq.getSession().setAttribute(TargetInstanceCommand.SESSION_MODE, true);

		HttpServletResponse aResp = new MockHttpServletResponse(); 
		TargetInstanceCommand aCmd = new TargetInstanceCommand();
		aCmd.setCmd(TargetInstanceCommand.ACTION_EDIT);
		TabbedController tc = new TabbedTargetInstanceController();

		TabConfig tabConfig = new TabConfig();
		tabConfig.setViewName("targetInstance");
		List<Tab> tabs = getTabList(targetInstanceManager);
		tabConfig.setTabs(tabs);

		tc.setTabConfig(tabConfig);
		tc.setDefaultCommandClass(org.webcurator.ui.target.command.TargetInstanceCommand.class);
		
		Tab currentTab = tabs.get(1);
		BindException aErrors = new BindException(aCmd, aCmd.getCmd());
		ModelAndView mav = testInstance.preProcessNextTab(tc, currentTab, aReq, aResp, aCmd, aErrors);
		assertTrue(mav != null);
		assertNotNull((List<LogFilePropertiesDTO>)mav.getModel().get(TargetInstanceCommand.MDL_LOG_LIST));
		List<LogFilePropertiesDTO> logFiles = (List<LogFilePropertiesDTO>)mav.getModel().get(TargetInstanceCommand.MDL_LOG_LIST);
		assertEquals(3, logFiles.size());
		Iterator<LogFilePropertiesDTO> it = logFiles.iterator();
		while(it.hasNext())
		{
			LogFilePropertiesDTO logFile = it.next();
			if(logFile.getName().startsWith("aqa-report"))
			{
				assertEquals("aqa-viewer.html", logFile.getViewer());
			}
			else
			{
				assertEquals("log-viewer.html", logFile.getViewer());
			}
		}
		
	}

	@Test
	public final void testProcessOther() {
		
		HttpServletRequest aReq = new MockHttpServletRequest();
		TargetInstanceManager targetInstanceManager = new MockTargetInstanceManager(testFile);
		testInstance.setTargetInstanceManager(targetInstanceManager);
		TargetInstance targetInstance = targetInstanceManager.getTargetInstance(5001L);
		
		aReq.getSession().setAttribute(TargetInstanceCommand.SESSION_TI, targetInstance);
		aReq.getSession().setAttribute(TargetInstanceCommand.SESSION_MODE, true);

		HttpServletResponse aResp = new MockHttpServletResponse(); 
		TargetInstanceCommand aCmd = new TargetInstanceCommand(targetInstance);
		TabbedController tc = new TabbedTargetInstanceController();

		TabConfig tabConfig = new TabConfig();
		tabConfig.setViewName("targetInstance");
		List<Tab> tabs = getTabList(targetInstanceManager);
		tabConfig.setTabs(tabs);

		tc.setTabConfig(tabConfig);
		tc.setDefaultCommandClass(org.webcurator.ui.target.command.TargetInstanceCommand.class);
		
		Tab currentTab = tabs.get(1);
		aCmd.setCmd(TargetInstanceCommand.ACTION_EDIT);
		BindException aErrors = new BindException(aCmd, aCmd.getCmd());
		try {
			ModelAndView mav = testInstance.processOther(tc, currentTab, aReq, aResp, aCmd, aErrors);
		}
		catch (Exception e) {
			assertTrue(e.getMessage().equals("Unknown command " + TargetInstanceCommand.ACTION_EDIT + " received."));
		}
	}

	@Test
	public final void testInitBinder() {
		MockHttpServletRequest request = new MockHttpServletRequest();
		ServletRequestDataBinder binder = new ServletRequestDataBinder(new CreateUserCommand(), "command");
		try
		{
			testInstance.initBinder(request, binder);
		}
		catch (Exception e)
		{
			String message = e.getClass().toString() + " - " + e.getMessage();
			log.debug(message);
			fail(message);
		}
	}

	@Test
	public final void testSetTargetInstanceManager() {
		TargetInstanceManager targetInstanceManager = new MockTargetInstanceManager(testFile);
		testInstance.setTargetInstanceManager(targetInstanceManager);
	}

}
