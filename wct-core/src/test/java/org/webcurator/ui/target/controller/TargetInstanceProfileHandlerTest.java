package org.webcurator.ui.target.controller;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import org.springframework.context.MockMessageSource;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.validation.BindException;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.webcurator.auth.AuthorityManagerImpl;
import org.webcurator.core.agency.AgencyUserManager;
import org.webcurator.core.agency.MockAgencyUserManagerImpl;
import org.webcurator.core.harvester.coordinator.*;
import org.webcurator.core.scheduler.MockTargetInstanceManager;
import org.webcurator.core.scheduler.TargetInstanceManager;
import org.webcurator.test.BaseWCTTest;
import org.webcurator.ui.admin.command.CreateUserCommand;
import org.webcurator.ui.target.command.*;
import org.webcurator.ui.target.validator.*;
import org.webcurator.ui.util.*;
import org.webcurator.core.profiles.*;
import org.webcurator.domain.model.core.*;

public class TargetInstanceProfileHandlerTest extends BaseWCTTest<TargetInstanceProfileHandler>{

	private static TargetInstanceManager targetInstanceManager = null;
	private static AgencyUserManager agencyUserManager = null;
	public TargetInstanceProfileHandlerTest()
	{
		super(TargetInstanceProfileHandler.class, 
				"src/test/java/org/webcurator/ui/target/controller/targetinstanceprofilehandlertest.xml");
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

		TargetInstanceGeneralHandler genHandler = new TargetInstanceGeneralHandler();
		genHandler.setAgencyUserManager(getAgencyUserManager());
		genHandler.setAuthorityManager(new AuthorityManagerImpl());
		genHandler.setTargetInstanceManager(getTargetInstanceManager());
		genHandler.setHarvestCoordinator(new MockHarvestCoordinator());
		tabGeneral.setTabHandler(genHandler);
		
		tabs.add(tabGeneral);
		
		Tab tabProfile = new Tab();
		tabProfile.setCommandClass(TargetInstanceProfileCommand.class);
		tabProfile.setJsp("../target-profile.jsp");
		tabProfile.setPageId("PROFILE");
		tabProfile.setTitle("profile");
		tabProfile.setValidator(new ProfilesOverridesValidator());

		tabProfile.setTabHandler(testInstance);
		
		tabs.add(tabProfile);
		
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

	private ProfileOverrides getProfileOverrides(Long oid)
	{
		ProfileOverrides po = new ProfileOverrides();
		po.setExcludedMimeTypes("text");
		
		List<String> excl = new ArrayList<String>();
		excl.add("*/.tmp");
		excl.add("*/.exe");
		po.setExcludeUriFilters(excl);
		List<String> incl = new ArrayList<String>();
		incl.add("*/.htm");
		po.setIncludeUriFilters(incl);
		po.setMaxBytesDownload(new Long(256*1024));
		po.setMaxHarvestDocuments(23L);
		po.setMaxLinkHops(4);
		po.setMaxPathDepth(3);
		po.setMaxTimeSec(new Long(2*3600));
		po.setOid(oid);
		po.setOverrideCredentials(false);
		po.setOverrideExcludedMimeTypes(true);
		po.setOverrideExcludeUriFilters(true);
		po.setOverrideIncludeUriFilters(true);
		po.setOverrideMaxBytesDownload(true);
		po.setOverrideMaxHarvestDocuments(true);
		po.setOverrideMaxLinkHops(true);
		po.setOverrideMaxPathDepth(true);
		po.setOverrideMaxTimeSec(true);
		po.setRobotsHonouringPolicy("ignore");

		return po;
	}
	
	private boolean testList(List a, List b)
	{
		if(a.size() != b.size())
		{
			return false;
		}
		for(int i = 0; i < a.size(); i++)
		{
			if(a.get(i).equals(b.get(i)) == false)
			{
				return false;
			}
		}
		return true;
	}
	
	public void setUp() throws Exception 
	{
		super.setUp();
		DateUtils.get().setMessageSource(new MockMessageSource());
		testInstance.setProfileManager(new MockProfileManager(testFile));
		testInstance.setCredentialUrlPrefix("ti");
		
		OverrideGetter og = new OverrideGetter();
		og.setOverrideableType("Target Instance");
		testInstance.setOverrideGetter(og);
	}
	
	@Test
	public final void testProcessTab() {
		HttpServletRequest aReq = new MockHttpServletRequest();
		TargetInstance targetInstance = getTargetInstanceManager().getTargetInstance(5001L);
		ProfileOverrides po = getProfileOverrides(20000L);
		targetInstance.setOverrides(po);
		
		targetInstance.setState(TargetInstance.STATE_SCHEDULED);
		aReq.getSession().setAttribute(TargetInstanceCommand.SESSION_TI, targetInstance);
		aReq.getSession().setAttribute(TargetInstanceCommand.SESSION_MODE, true);

		HttpServletResponse aResp = new MockHttpServletResponse(); 
		TargetInstanceProfileCommand aCmd = new TargetInstanceProfileCommand();
		TabbedController tc = new TabbedTargetInstanceController();

		TabConfig tabConfig = new TabConfig();
		tabConfig.setViewName("targetInstance");
		List<Tab> tabs = getTabList();
		tabConfig.setTabs(tabs);

		tc.setTabConfig(tabConfig);
		tc.setDefaultCommandClass(org.webcurator.ui.target.command.TargetInstanceCommand.class);
		
		Tab currentTab = tabs.get(1);

		aCmd.setFromOverrides(targetInstance.getProfileOverrides());
		aCmd.setExcludedMimeTypes("OverrideTest1");
		aCmd.setOverrideTarget(true);
		
		BindException aErrors = new BindException(aCmd, aCmd.getActionCmd());
		testInstance.processTab(tc, currentTab, aReq, aResp, aCmd, aErrors);
		assertTrue(targetInstance.getOverrides().getExcludedMimeTypes().equals("OverrideTest1"));

	}
	
	@Test
	public final void testProcessTab2() {
		HttpServletRequest aReq = new MockHttpServletRequest();
		TargetInstance targetInstance = getTargetInstanceManager().getTargetInstance(5001L);
		ProfileOverrides po = getProfileOverrides(20000L);
		targetInstance.setOverrides(po);
		
		targetInstance.setState(TargetInstance.STATE_HARVESTED);
		aReq.getSession().setAttribute(TargetInstanceCommand.SESSION_TI, targetInstance);
		aReq.getSession().setAttribute(TargetInstanceCommand.SESSION_MODE, true);

		HttpServletResponse aResp = new MockHttpServletResponse(); 
		TargetInstanceProfileCommand aCmd = new TargetInstanceProfileCommand();
		TabbedController tc = new TabbedTargetInstanceController();

		TabConfig tabConfig = new TabConfig();
		tabConfig.setViewName("targetInstance");
		List<Tab> tabs = getTabList();
		tabConfig.setTabs(tabs);

		tc.setTabConfig(tabConfig);
		tc.setDefaultCommandClass(org.webcurator.ui.target.command.TargetInstanceCommand.class);
		
		Tab currentTab = tabs.get(1);

		aCmd.setFromOverrides(targetInstance.getProfileOverrides());
		aCmd.setExcludedMimeTypes("OverrideTest2");
		aCmd.setOverrideTarget(true);
		
		BindException aErrors = new BindException(aCmd, aCmd.getActionCmd());
		testInstance.processTab(tc, currentTab, aReq, aResp, aCmd, aErrors);
		assertFalse(targetInstance.getOverrides().getExcludedMimeTypes().equals("OverrideTest2"));

	}

	@Test
	public final void testPreProcessNextTab() {
		HttpServletRequest aReq = new MockHttpServletRequest();
		TargetInstance targetInstance = getTargetInstanceManager().getTargetInstance(5001L);
		ProfileOverrides po = getProfileOverrides(20000L);
		targetInstance.setOverrides(po);
		
		aReq.getSession().setAttribute(TargetInstanceCommand.SESSION_TI, targetInstance);
		aReq.getSession().setAttribute(TargetInstanceCommand.SESSION_MODE, true);

		HttpServletResponse aResp = new MockHttpServletResponse(); 
		TargetInstanceProfileCommand aCmd = new TargetInstanceProfileCommand();
		aCmd.setFromOverrides(targetInstance.getProfileOverrides());
		TabbedController tc = new TabbedTargetInstanceController();

		TabConfig tabConfig = new TabConfig();
		tabConfig.setViewName("targetInstance");
		List<Tab> tabs = getTabList();
		tabConfig.setTabs(tabs);

		tc.setTabConfig(tabConfig);
		tc.setDefaultCommandClass(org.webcurator.ui.target.command.TargetInstanceCommand.class);
		
		Tab currentTab = tabs.get(1);
		BindException aErrors = new BindException(aCmd, aCmd.getActionCmd());
		ModelAndView mav = testInstance.preProcessNextTab(tc, currentTab, aReq, aResp, aCmd, aErrors);
		assertTrue(((TargetInstanceProfileCommand)mav.getModel().get("command")).getExcludedMimeTypes().equals(targetInstance.getProfileOverrides().getExcludedMimeTypes()));
	}

	@Test
	public final void testProcessOther() {
		HttpServletRequest aReq = new MockHttpServletRequest();
		TargetInstance targetInstance = getTargetInstanceManager().getTargetInstance(5001L);
		ProfileOverrides po = getProfileOverrides(20000L);
		targetInstance.setOverrides(po);
		
		aReq.getSession().setAttribute(TargetInstanceCommand.SESSION_TI, targetInstance);
		aReq.getSession().setAttribute(TargetInstanceCommand.SESSION_MODE, true);

		HttpServletResponse aResp = new MockHttpServletResponse(); 
		TargetInstanceProfileCommand aCmd = new TargetInstanceProfileCommand();
		TabbedController tc = new TabbedTargetInstanceController();

		TabConfig tabConfig = new TabConfig();
		tabConfig.setViewName("targetInstance");
		List<Tab> tabs = getTabList();
		tabConfig.setTabs(tabs);

		tc.setTabConfig(tabConfig);
		tc.setDefaultCommandClass(org.webcurator.ui.target.command.TargetInstanceCommand.class);
		
		Tab currentTab = tabs.get(1);
		aCmd.setActionCmd("toggleOverride");
		BindException aErrors = new BindException(aCmd, aCmd.getActionCmd());
		ModelAndView mav = testInstance.processOther(tc, currentTab, aReq, aResp, aCmd, aErrors);
		assertTrue(mav != null);
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
	public final void testBuildCommand() {
		try
		{
			TargetInstance ti = getTargetInstanceManager().getTargetInstance(5000L);
			ProfileOverrides po = getProfileOverrides(20000L);
			ti.setOverrides(po);
						
			ProfileCommand command = testInstance.buildCommand((Overrideable)ti);
			
			assertNotNull(command);
			assertNotNull(ti.getProfileOverrides());
			assertEquals(ti.getProfileOverrides(), po);
			assertTrue(command.getRobots().equals(po.getRobotsHonouringPolicy()));
			assertTrue(command.getMaxPathDepth() == po.getMaxPathDepth());
			assertTrue(command.getMaxHours() * 3600 == po.getMaxTimeSec());
			assertTrue(command.getMaxHops() == po.getMaxLinkHops());
			assertTrue(command.getMaxDocuments() == po.getMaxHarvestDocuments());
			assertTrue(command.getMaxBytesDownload() * 1024 == po.getMaxBytesDownload());
			assertTrue(testList(command.stringToList(command.getForceAcceptFilters()), po.getIncludeUriFilters()));
			assertTrue(testList(command.stringToList(command.getExcludeFilters()), po.getExcludeUriFilters()));
			assertTrue(command.getExcludedMimeTypes().equals(po.getExcludedMimeTypes()));
		}
		catch (Exception e)
		{
			String message = e.getClass().toString() + " - " + e.getMessage();
			log.debug(message);
			fail(message);
		}
	}

}
