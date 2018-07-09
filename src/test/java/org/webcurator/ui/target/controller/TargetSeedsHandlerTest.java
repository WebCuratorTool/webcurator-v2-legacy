package org.webcurator.ui.target.controller;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Iterator;

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
import org.webcurator.core.targets.MockTargetManager;
import org.webcurator.core.targets.TargetManager;
import org.webcurator.test.BaseWCTTest;
import org.webcurator.ui.admin.command.CreateUserCommand;
import org.webcurator.ui.target.TargetEditorContext;
import org.webcurator.ui.target.command.SeedsCommand;
import org.webcurator.ui.target.command.TargetGeneralCommand;
import org.webcurator.ui.target.validator.TargetGeneralValidator;
import org.webcurator.ui.target.validator.TargetSeedsValidator;
import org.webcurator.ui.util.Tab;
import org.webcurator.ui.util.TabConfig;
import org.webcurator.ui.util.TabStatus;
import org.webcurator.ui.util.TabbedController;
import org.webcurator.domain.model.core.BusinessObjectFactory;
import org.webcurator.domain.model.core.Seed;
import org.webcurator.domain.model.core.Permission;
import org.webcurator.domain.model.core.Target;
import org.webcurator.core.permissionmapping.*;
public class TargetSeedsHandlerTest extends BaseWCTTest<TargetSeedsHandler> {

	public TargetSeedsHandlerTest()
	{
		super(TargetSeedsHandler.class,
		"src/test/java/org/webcurator/ui/target/controller/TargetSeedsHandlerTest.xml");
	}
	
	private List<Tab> getTabList(TargetManager targetManager)
	{
		List<Tab> tabs = new ArrayList<Tab>();
		
		Tab tabGeneral = new Tab(); tabGeneral.setCommandClass(TargetGeneralCommand.class);
		tabGeneral.setJsp("../target-general.jsp");
		tabGeneral.setPageId("GENERAL");
		tabGeneral.setTitle("general");
		tabGeneral.setValidator(new TargetGeneralValidator());

		TargetGeneralHandler genHandler = new TargetGeneralHandler();
		genHandler.setAuthorityManager(new AuthorityManagerImpl());
		tabGeneral.setTabHandler(genHandler);
		
		tabs.add(tabGeneral);
		
		Tab tabSeeds = new Tab();
		tabSeeds.setCommandClass(SeedsCommand.class);
		tabSeeds.setJsp("../target-seeds.jsp");
		tabSeeds.setPageId("SEEDS");
		tabSeeds.setValidator(new TargetSeedsValidator());

		TargetSeedsHandler seedsHandler = new TargetSeedsHandler();
		tabSeeds.setTabHandler(seedsHandler);
		
		tabs.add(tabSeeds);
		return tabs;
	}
	
	@Test
	public final void testProcessTab() {
		HttpServletRequest aReq = new MockHttpServletRequest();
		TargetManager targetManager = new MockTargetManager(testFile);
		
		testInstance.setTargetManager(targetManager);
		testSetBusinessObjectFactory();
		testSetValidator();
		testSetAuthorityManager();
		testSetMessageSource();

		Target target = targetManager.load(4000L);
		TargetEditorContext targetEditorContext = new TargetEditorContext(targetManager,target,true);
		aReq.getSession().setAttribute(TabbedTargetController.EDITOR_CONTEXT, targetEditorContext);

		HttpServletResponse aResp = new MockHttpServletResponse(); 
		SeedsCommand aCmd = new SeedsCommand();
		TabbedController tc = new TabbedTargetController();

		TabConfig tabConfig = new TabConfig();
		tabConfig.setViewName("target");
		List<Tab> tabs = getTabList(targetManager);
		tabConfig.setTabs(tabs);

		tc.setTabConfig(tabConfig);
		tc.setDefaultCommandClass(org.webcurator.ui.target.command.TargetDefaultCommand.class);
		
		Tab currentTab = tabs.get(1);
		BindException aErrors = new BindException(aCmd, "SeedsCommand");
		
		try
		{
			//This method doesn't currently do anything
			testInstance.processTab(tc, currentTab, aReq, aResp, aCmd, aErrors);
		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}

	@Test
	public final void testPreProcessNextTab() {
		HttpServletRequest aReq = new MockHttpServletRequest();
		TargetManager targetManager = new MockTargetManager(testFile);
		
		testInstance.setTargetManager(targetManager);
		testSetBusinessObjectFactory();
		testSetValidator();
		testSetAuthorityManager();
		testSetMessageSource();
		
		Target target = targetManager.load(4000L);
		TargetEditorContext targetEditorContext = new TargetEditorContext(targetManager,target,true);
		aReq.getSession().setAttribute(TabbedTargetController.EDITOR_CONTEXT, targetEditorContext);

		HttpServletResponse aResp = new MockHttpServletResponse(); 
		SeedsCommand aCmd = new SeedsCommand();
		TabbedController tc = new TabbedTargetController();

		TabConfig tabConfig = new TabConfig();
		tabConfig.setViewName("target");
		List<Tab> tabs = getTabList(targetManager);
		tabConfig.setTabs(tabs);

		tc.setTabConfig(tabConfig);
		tc.setDefaultCommandClass(org.webcurator.ui.target.command.TargetDefaultCommand.class);
		
		Tab currentTab = tabs.get(1);
		BindException aErrors = new BindException(aCmd, "SeedsCommand");
		
		ModelAndView mav = testInstance.preProcessNextTab(tc, currentTab, aReq, aResp, aCmd, aErrors);
		assertNotNull(mav);
		assertEquals(((List<Seed>)mav.getModel().get("seeds")).size(), target.getSeeds().size());
		assertTrue(target.getSeeds().size() > 0);
		Iterator<Seed> it = target.getSeeds().iterator();
		List<Seed> seeds = (List<Seed>)mav.getModel().get("seeds");
		while(it.hasNext())
		{
			assertTrue(seeds.contains(it.next()));
		}
		assertSame(((List<Permission>)mav.getModel().get("quickPicks")), targetEditorContext.getQuickPickPermissions());
		assertEquals(((Boolean)mav.getModel().get("allowMultiplePrimarySeeds")), targetManager.getAllowMultiplePrimarySeeds());
	}

	@Test
	public final void testProcessOther() {
		HttpServletRequest aReq = new MockHttpServletRequest();
		MockTargetManager targetManager = new MockTargetManager(testFile);
		
		testInstance.setTargetManager(targetManager);
		testSetBusinessObjectFactory();
		testSetValidator();
		testSetAuthorityManager();
		testSetMessageSource();
		
		Target target = targetManager.load(4000L);
		TargetEditorContext targetEditorContext = new TargetEditorContext(targetManager,target,true);
		aReq.getSession().setAttribute(TabbedTargetController.EDITOR_CONTEXT, targetEditorContext);

		HttpServletResponse aResp = new MockHttpServletResponse(); 
		SeedsCommand aCmd = new SeedsCommand();
		TabbedController tc = new TabbedTargetController();

		TabConfig tabConfig = new TabConfig();
		tabConfig.setViewName("target");
		List<Tab> tabs = getTabList(targetManager);
		tabConfig.setTabs(tabs);

		tc.setTabConfig(tabConfig);
		tc.setDefaultCommandClass(org.webcurator.ui.target.command.TargetDefaultCommand.class);
		
		Tab currentTab = tabs.get(1);
		BindException aErrors = new BindException(aCmd, "SeedsCommand");
		
		targetManager.setAllowMultiplePrimarySeeds(true);
		aCmd.setActionCmd(SeedsCommand.ACTION_TOGGLE_PRIMARY);
		aCmd.setSelectedSeed("6001");
		ModelAndView mav = testInstance.processOther(tc, currentTab, aReq, aResp, aCmd, aErrors);
		assertNotNull(mav);
		List<Seed> seeds = (List<Seed>)mav.getModel().get("seeds");
		Iterator<Seed> it = seeds.iterator();
		int primaryCount = 0;
		while(it.hasNext())
		{
			primaryCount += (it.next().isPrimary()?1:0);
		}
		
		assertTrue(primaryCount > 1);

		aCmd = new SeedsCommand();
		
		targetManager.setAllowMultiplePrimarySeeds(false);
		aCmd.setActionCmd(SeedsCommand.ACTION_TOGGLE_PRIMARY);
		aCmd.setSelectedSeed("6002");
		mav = testInstance.processOther(tc, currentTab, aReq, aResp, aCmd, aErrors);
		assertNotNull(mav);
		seeds = (List<Seed>)mav.getModel().get("seeds");
		it = seeds.iterator();
		primaryCount = 0;
		while(it.hasNext())
		{
			primaryCount += (it.next().isPrimary()?1:0);
		}
		
		assertTrue(primaryCount == 1);

		aCmd = new SeedsCommand();
		targetEditorContext.getTarget().setSeeds(new HashSet<Seed>());
		String testSeed = "http://www.test.com/ ";
		String expectedSeed = "http://www.test.com/";
		
		aCmd.setActionCmd(SeedsCommand.ACTION_ADD);
		aCmd.setSeed(testSeed);
		aCmd.setPermissionMappingOption(SeedsCommand.PERM_MAPPING_NONE);
		
		mav = testInstance.processOther(tc, currentTab, aReq, aResp, aCmd, aErrors);
		assertNotNull(mav);
		assertEquals(targetEditorContext.getTarget().getSeeds().size(), 1);
		assertEquals(((Seed)targetEditorContext.getTarget().getSeeds().toArray()[0]).getSeed(),expectedSeed); 
		
	}

	@Test
	public final void testProcessOtherRemoveSelected() {
		MockHttpServletRequest aReq = new MockHttpServletRequest();
		MockTargetManager targetManager = new MockTargetManager(testFile);
		
		testInstance.setTargetManager(targetManager);
		testSetBusinessObjectFactory();
		testSetValidator();
		testSetAuthorityManager();
		testSetMessageSource();
		
		Target target = targetManager.load(4000L);
		TargetEditorContext targetEditorContext = new TargetEditorContext(targetManager,target,true);
		aReq.getSession().setAttribute(TabbedTargetController.EDITOR_CONTEXT, targetEditorContext);

		HttpServletResponse aResp = new MockHttpServletResponse(); 
		SeedsCommand aCmd = new SeedsCommand();
		TabbedController tc = new TabbedTargetController();

		TabConfig tabConfig = new TabConfig();
		tabConfig.setViewName("target");
		List<Tab> tabs = getTabList(targetManager);
		tabConfig.setTabs(tabs);

		tc.setTabConfig(tabConfig);
		tc.setDefaultCommandClass(org.webcurator.ui.target.command.TargetDefaultCommand.class);
		
		Tab currentTab = tabs.get(1);
		BindException aErrors = new BindException(aCmd, "SeedsCommand");
		
		assertEquals(3, target.getSeeds().size());

		aReq.addParameter("chkSelect6000", "on");
		aReq.addParameter("chkSelect6002", "on");
		aCmd.setActionCmd(SeedsCommand.ACTION_REMOVE_SELECTED);

		ModelAndView mav = testInstance.processOther(tc, currentTab, aReq, aResp, aCmd, aErrors);
		assertNotNull(mav);
		List<Seed> seeds = (List<Seed>)mav.getModel().get("seeds");
		assertEquals(1, seeds.size());
		String jsp = ((TabStatus)mav.getModel().get("tabStatus")).getCurrentTab().getJsp();
		assertEquals("../target-seeds.jsp", jsp);
		assertEquals(1, target.getSeeds().size());
	}

	@Test
	public final void testProcessOtherUnlinkSelected() {
		MockHttpServletRequest aReq = new MockHttpServletRequest();
		MockTargetManager targetManager = new MockTargetManager(testFile);
		
		testInstance.setTargetManager(targetManager);
		testSetBusinessObjectFactory();
		testSetValidator();
		testSetAuthorityManager();
		testSetMessageSource();
		
		Target target = targetManager.load(4000L);
		TargetEditorContext targetEditorContext = new TargetEditorContext(targetManager,target,true);
		aReq.getSession().setAttribute(TabbedTargetController.EDITOR_CONTEXT, targetEditorContext);

		HttpServletResponse aResp = new MockHttpServletResponse(); 
		SeedsCommand aCmd = new SeedsCommand();
		TabbedController tc = new TabbedTargetController();

		TabConfig tabConfig = new TabConfig();
		tabConfig.setViewName("target");
		List<Tab> tabs = getTabList(targetManager);
		tabConfig.setTabs(tabs);

		tc.setTabConfig(tabConfig);
		tc.setDefaultCommandClass(org.webcurator.ui.target.command.TargetDefaultCommand.class);
		
		Tab currentTab = tabs.get(1);
		BindException aErrors = new BindException(aCmd, "SeedsCommand");
		
		assertEquals(3, target.getSeeds().size());
		Iterator<Seed> it = target.getSeeds().iterator();
		while(it.hasNext())
		{
			Seed seed = it.next();
			assertFalse(seed.getPermissions().isEmpty());
		}

		aReq.addParameter("chkSelect6000", "on");
		aReq.addParameter("chkSelect6002", "on");
		aCmd.setActionCmd(SeedsCommand.ACTION_UNLINK_SELECTED);

		ModelAndView mav = testInstance.processOther(tc, currentTab, aReq, aResp, aCmd, aErrors);
		assertNotNull(mav);
		List<Seed> seeds = (List<Seed>)mav.getModel().get("seeds");
		assertEquals(3, seeds.size());
		String jsp = ((TabStatus)mav.getModel().get("tabStatus")).getCurrentTab().getJsp();
		assertEquals("../target-seeds.jsp", jsp);
		assertEquals(3, target.getSeeds().size());
		
		it = target.getSeeds().iterator();
		while(it.hasNext())
		{
			Seed seed = it.next();
			if(seed.getIdentity().equals("6000") || seed.getIdentity().equals("6002"))
			{
				assertTrue(seed.getPermissions().isEmpty());
			}
			else
			{
				assertFalse(seed.getPermissions().isEmpty());
			}
		}
	}


	@Test
	public final void testProcessOtherLinkSelected() {
		MockHttpServletRequest aReq = new MockHttpServletRequest();
		MockTargetManager targetManager = new MockTargetManager(testFile);
		
		testInstance.setTargetManager(targetManager);
		testSetBusinessObjectFactory();
		testSetValidator();
		testSetAuthorityManager();
		testSetMessageSource();
		
		Target target = targetManager.load(4001L);
		TargetEditorContext targetEditorContext = new TargetEditorContext(targetManager,target,true);
		aReq.getSession().setAttribute(TabbedTargetController.EDITOR_CONTEXT, targetEditorContext);

		HttpServletResponse aResp = new MockHttpServletResponse(); 
		SeedsCommand aCmd = new SeedsCommand();
		TabbedController tc = new TabbedTargetController();

		TabConfig tabConfig = new TabConfig();
		tabConfig.setViewName("target");
		List<Tab> tabs = getTabList(targetManager);
		tabConfig.setTabs(tabs);

		tc.setTabConfig(tabConfig);
		tc.setDefaultCommandClass(org.webcurator.ui.target.command.TargetDefaultCommand.class);
		
		Tab currentTab = tabs.get(1);
		BindException aErrors = new BindException(aCmd, "SeedsCommand");
		
		assertEquals(3, target.getSeeds().size());
		Iterator<Seed> it = target.getSeeds().iterator();
		while(it.hasNext())
		{
			Seed seed = it.next();
			assertTrue(seed.getPermissions().isEmpty());
		}

		aReq.addParameter("chkSelect6010", "on");
		aReq.addParameter("chkSelect6012", "on");
		aCmd.setActionCmd(SeedsCommand.ACTION_LINK_SELECTED);

		ModelAndView mav = testInstance.processOther(tc, currentTab, aReq, aResp, aCmd, aErrors);
		assertNotNull(mav);
		String jsp = ((TabStatus)mav.getModel().get("tabStatus")).getCurrentTab().getJsp();
		assertEquals("../target-seeds-link.jsp", jsp);
		assertEquals(3, target.getSeeds().size());
		
		SeedsCommand command = (SeedsCommand)mav.getModel().get("command");
		String selectedSeedActual = command.getSelectedSeed();
		assertTrue("6012,6010".equals(selectedSeedActual) || "6010,6012".equals(selectedSeedActual));
		command.setActionCmd(SeedsCommand.ACTION_LINK_NEW_CONFIRM);
		command.setLinkPermIdentity(new String[]{"7000"});

		mav = testInstance.processOther(tc, currentTab, aReq, aResp, command, aErrors);
		assertNotNull(mav);
		jsp = ((TabStatus)mav.getModel().get("tabStatus")).getCurrentTab().getJsp();
		assertEquals("../target-seeds.jsp", jsp);
		assertEquals(3, target.getSeeds().size());
		
		it = target.getSeeds().iterator();
		while(it.hasNext())
		{
			Seed seed = it.next();
			if(seed.getIdentity().equals("6010") || seed.getIdentity().equals("6012"))
			{
				assertFalse(seed.getPermissions().isEmpty());
				assertEquals(1, seed.getPermissions().size());
				Permission perm = (Permission) seed.getPermissions().toArray()[0];
				assertEquals(7000L, perm.getOid().longValue());
			}
			else
			{
				assertTrue(seed.getPermissions().isEmpty());
			}
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
	public final void testProcessLinkSearch() {
		HttpServletRequest aReq = new MockHttpServletRequest();
		TargetManager targetManager = new MockTargetManager(testFile);
		
		testInstance.setTargetManager(targetManager);
		testSetBusinessObjectFactory();
		testSetValidator();
		testSetAuthorityManager();
		testSetMessageSource();
		
		Target target = targetManager.load(4000L);
		TargetEditorContext targetEditorContext = new TargetEditorContext(targetManager,target,true);
		aReq.getSession().setAttribute(TabbedTargetController.EDITOR_CONTEXT, targetEditorContext);

		SeedsCommand aCmd = new SeedsCommand();
		TabbedController tc = new TabbedTargetController();

		TabConfig tabConfig = new TabConfig();
		tabConfig.setViewName("target");
		List<Tab> tabs = getTabList(targetManager);
		tabConfig.setTabs(tabs);

		tc.setTabConfig(tabConfig);
		tc.setDefaultCommandClass(org.webcurator.ui.target.command.TargetDefaultCommand.class);
		
		BindException aErrors = new BindException(aCmd, "SeedsCommand");
		
		PermissionMappingStrategy.setStrategy(new HierarchicalPermissionMappingStrategy());
		aCmd.setUrlSearchCriteria("www.oakleigh.co.uk/*");
		ModelAndView mav = testInstance.processLinkSearch(tc, target, aCmd, false, aErrors);
		assertNotNull(mav);
	}

	@Test
	public final void testSetBusinessObjectFactory() {
		testInstance.setBusinessObjectFactory(new BusinessObjectFactory());
	}

	@Test
	public final void testSetTargetManager() {
		testInstance.setTargetManager(new MockTargetManager(testFile));
	}

	@Test
	public final void testSetValidator() {
		testInstance.setValidator(new TargetSeedsValidator());
	}

	@Test
	public final void testSetAuthorityManager() {
		testInstance.setAuthorityManager(new AuthorityManagerImpl());
	}

	@Test
	public final void testSetMessageSource() {
		try
		{
			testInstance.setMessageSource(new MockMessageSource());
		}
		catch(Exception e)
		{
			String message = e.getClass().toString() + " - " + e.getMessage();
			log.debug(message);
			fail(message);
		}
	}

}
