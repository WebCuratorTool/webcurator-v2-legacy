package org.webcurator.ui.target.controller;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.context.MockMessageSource;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.validation.BindException;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.webcurator.auth.AuthorityManagerImpl;
import org.webcurator.core.agency.*;
import org.webcurator.core.harvester.coordinator.*;
import org.webcurator.core.scheduler.*;
import org.webcurator.domain.MockTargetInstanceDAO;
import org.webcurator.domain.model.core.TargetInstance;
import org.webcurator.test.*;
import org.webcurator.ui.admin.command.CreateUserCommand;
import org.webcurator.ui.target.command.TargetInstanceCommand;
import org.webcurator.ui.target.validator.TargetInstanceValidator;
import org.webcurator.ui.util.DateUtils;
import org.webcurator.ui.util.Tab;
import org.webcurator.ui.util.TabConfig;
import org.webcurator.ui.util.TabbedController;
import org.webcurator.core.exceptions.WCTRuntimeException;


public class TargetInstanceGeneralHandlerTest extends BaseWCTTest<TargetInstanceGeneralHandler>{

	private static TargetInstanceManager targetInstanceManager = null;
	private static AgencyUserManager agencyUserManager = null;
	public TargetInstanceGeneralHandlerTest()
	{
		super(TargetInstanceGeneralHandler.class, 
				"src/test/java/org/webcurator/ui/target/controller/TargetInstanceGeneralHandlerTest.xml");
	}
	
	public void setUp() throws Exception 
	{
		super.setUp();
		DateUtils.get().setMessageSource(new MockMessageSource());
	}
	
	private List<Tab> getTabList()
	{
		List<Tab> tabs = new ArrayList<Tab>();
		
		Tab tabGeneral = new Tab();
		tabGeneral.setCommandClass(TargetInstanceCommand.class);
		tabGeneral.setJsp("../target-instance-general.jsp");
		tabGeneral.setPageId("GENERAL");
		tabGeneral.setTitle("general");
		tabGeneral.setValidator(new TargetInstanceValidator());

		tabGeneral.setTabHandler(testInstance);
		
		tabs.add(tabGeneral);
		
		return tabs;
	}

	private TargetInstanceManager getTargetInstanceManager()
	{
		if(targetInstanceManager == null)
		{
			targetInstanceManager = new MockTargetInstanceManager(testFile);
		}
		
		return targetInstanceManager;
	}
	
	private AgencyUserManager getAgencyUserManager()
	{
		if(agencyUserManager == null)
		{
			agencyUserManager = new MockAgencyUserManagerImpl(testFile);
		}
		
		return agencyUserManager;
	}

	@Test
	public final void testSetHarvestCoordinator() {
		HarvestCoordinatorImpl hc = new HarvestCoordinatorImpl();
		hc.setTargetInstanceManager(getTargetInstanceManager());
		hc.setTargetInstanceDao(new MockTargetInstanceDAO(testFile));
		HarvestBandwidthManager mockHarvestBandwidthManager = Mockito.mock(HarvestBandwidthManager.class);
		hc.setHarvestBandwidthManager(mockHarvestBandwidthManager);

		HarvestAgentManager mockHarvestAgentManager = Mockito.mock(HarvestAgentManager.class);
		hc.setHarvestAgentManager(mockHarvestAgentManager);

		testInstance.setHarvestCoordinator(hc);
	}

	@Test
	public final void testSetTargetInstanceManager() {
		testInstance.setTargetInstanceManager(getTargetInstanceManager());
	}

	@Test
	public final void testSetAgencyUserManager() {
		testInstance.setAgencyUserManager(getAgencyUserManager());
	}

	@Test
	public final void testSetAuthorityManager() {
		testInstance.setAuthorityManager(new AuthorityManagerImpl());
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
	public final void testProcessTab() {
		HttpServletRequest aReq = new MockHttpServletRequest();
		
		testSetAgencyUserManager();
		testSetAuthorityManager();
		testSetHarvestCoordinator();
		testSetTargetInstanceManager();
		
		TargetInstance targetInstance = getTargetInstanceManager().getTargetInstance(5001L);
		
		aReq.getSession().setAttribute(TargetInstanceCommand.SESSION_TI, targetInstance);
		aReq.getSession().setAttribute(TargetInstanceCommand.SESSION_MODE, true);

		HttpServletResponse aResp = new MockHttpServletResponse(); 
		TargetInstanceCommand aCmd = new TargetInstanceCommand(targetInstance);
		TabbedController tc = new TabbedTargetInstanceController();

		TabConfig tabConfig = new TabConfig();
		tabConfig.setViewName("targetInstance");
		List<Tab> tabs = getTabList();
		tabConfig.setTabs(tabs);

		tc.setTabConfig(tabConfig);
		tc.setDefaultCommandClass(org.webcurator.ui.target.command.TargetInstanceCommand.class);
		
		Tab currentTab = tabs.get(0);
		aCmd.setCmd(TargetInstanceCommand.ACTION_EDIT);
		aCmd.setFlagged(true);
		BindException aErrors = new BindException(aCmd, aCmd.getCmd());
		testInstance.processTab(tc, currentTab, aReq, aResp, aCmd, aErrors);
		assertTrue(targetInstance.getFlagged());

		currentTab = tabs.get(0);
		aCmd.setCmd(TargetInstanceCommand.ACTION_EDIT);
		aCmd.setFlagged(false);
		aErrors = new BindException(aCmd, aCmd.getCmd());
		testInstance.processTab(tc, currentTab, aReq, aResp, aCmd, aErrors);
		assertFalse(targetInstance.getFlagged());
	}

	@Test
	public final void testPreProcessNextTab() {
		HttpServletRequest aReq = new MockHttpServletRequest();
		
		testSetAgencyUserManager();
		testSetAuthorityManager();
		testSetHarvestCoordinator();
		testSetTargetInstanceManager();
		
		TargetInstance targetInstance = getTargetInstanceManager().getTargetInstance(5001L);
		
		aReq.getSession().setAttribute(TargetInstanceCommand.SESSION_TI, targetInstance);
		aReq.getSession().setAttribute(TargetInstanceCommand.SESSION_MODE, true);

		HttpServletResponse aResp = new MockHttpServletResponse(); 
		TargetInstanceCommand aCmd = new TargetInstanceCommand();
		aCmd.setCmd(TargetInstanceCommand.ACTION_EDIT);
		TabbedController tc = new TabbedTargetInstanceController();

		TabConfig tabConfig = new TabConfig();
		tabConfig.setViewName("targetInstance");
		List<Tab> tabs = getTabList();
		tabConfig.setTabs(tabs);

		tc.setTabConfig(tabConfig);
		tc.setDefaultCommandClass(org.webcurator.ui.target.command.TargetInstanceCommand.class);
		
		Tab currentTab = tabs.get(0);
		BindException aErrors = new BindException(aCmd, aCmd.getCmd());
		ModelAndView mav = testInstance.preProcessNextTab(tc, currentTab, aReq, aResp, aCmd, aErrors);
		assertTrue(((TargetInstanceCommand)mav.getModel().get("command")).getFlagged() == targetInstance.getFlagged());
	}

	@Test
	public final void testProcessOther() {
		HttpServletRequest aReq = new MockHttpServletRequest();
		
		testSetAgencyUserManager();
		testSetAuthorityManager();
		testSetHarvestCoordinator();
		testSetTargetInstanceManager();
		
		TargetInstance targetInstance = getTargetInstanceManager().getTargetInstance(5001L);
		
		aReq.getSession().setAttribute(TargetInstanceCommand.SESSION_TI, targetInstance);
		aReq.getSession().setAttribute(TargetInstanceCommand.SESSION_MODE, true);

		HttpServletResponse aResp = new MockHttpServletResponse(); 
		TargetInstanceCommand aCmd = new TargetInstanceCommand(targetInstance);
		TabbedController tc = new TabbedTargetInstanceController();

		TabConfig tabConfig = new TabConfig();
		tabConfig.setViewName("targetInstance");
		List<Tab> tabs = getTabList();
		tabConfig.setTabs(tabs);

		tc.setTabConfig(tabConfig);
		tc.setDefaultCommandClass(org.webcurator.ui.target.command.TargetInstanceCommand.class);
		
		Tab currentTab = tabs.get(0);
		
		aCmd.setCmd(TargetInstanceCommand.ACTION_HARVEST);
		BindException aErrors = new BindException(aCmd, aCmd.getCmd());
		ModelAndView mav = testInstance.processOther(tc, currentTab, aReq, aResp, aCmd, aErrors);
		assertTrue(mav != null);
		
		try
		{
			aCmd.setCmd(TargetInstanceCommand.ACTION_EDIT);
			aErrors = new BindException(aCmd, aCmd.getCmd());
			testInstance.processOther(tc, currentTab, aReq, aResp, aCmd, aErrors);
			fail("Exception not thrown for unknown command");
		}
		catch(WCTRuntimeException re)
		{
			assertTrue(re.getMessage().startsWith("Unknown command "));
		}
		catch(Exception e)
		{
			fail("Unexpected Exception: "+e.getMessage());
		}
	}
}
