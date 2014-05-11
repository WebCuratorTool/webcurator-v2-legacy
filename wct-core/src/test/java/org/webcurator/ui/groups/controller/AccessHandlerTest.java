package org.webcurator.ui.groups.controller;

import static org.junit.Assert.*;

import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import org.springframework.mock.web.*;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.*;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.webcurator.test.BaseWCTTest;
import org.webcurator.ui.admin.command.CreateUserCommand;
import org.webcurator.ui.groups.command.*;
import org.webcurator.ui.target.command.*;
import org.webcurator.ui.groups.GroupsEditorContext;
import org.webcurator.core.targets.*;
import org.webcurator.ui.util.*;
import org.webcurator.auth.AuthorityManagerImpl;
import org.webcurator.domain.model.core.*;
import org.webcurator.ui.groups.validator.*;

public class AccessHandlerTest extends BaseWCTTest<AccessHandler> {

	public AccessHandlerTest()
	{
		super(AccessHandler.class,
				"src/test/java/org/webcurator/ui/groups/controller/accesshandlertest.xml");
	}
	
	public void setUp() throws Exception 
	{
		super.setUp();
	}
	
	private List<Tab> getTabList(TargetManager targetManager)
	{
		List<Tab> tabs = new ArrayList<Tab>();
		
		Tab tabGeneral = new Tab();
		tabGeneral.setCommandClass(GeneralCommand.class);
		tabGeneral.setJsp("../groups-general.jsp");
		tabGeneral.setPageId("GENERAL");
		tabGeneral.setTitle("general");
		tabGeneral.setValidator(new GeneralValidator());

		GeneralHandler genHandler = new GeneralHandler();
		genHandler.setAuthorityManager(new AuthorityManagerImpl());
		tabGeneral.setTabHandler(genHandler);
		
		tabs.add(tabGeneral);
		
		Tab tabAccess = new Tab();
		tabAccess.setCommandClass(TargetAccessCommand.class);
		tabAccess.setJsp("../target-access.jsp");
		tabAccess.setPageId("ACCESS");

		AccessHandler accessHandler = new AccessHandler();
		tabAccess.setTabHandler(accessHandler);
		
		tabs.add(tabAccess);
		return tabs;
	}
	
	
	@Test
	public final void testProcessTab() {
		
		HttpServletRequest aReq = new MockHttpServletRequest();
		TargetManager targetManager = new MockTargetManager(testFile);
		testInstance.setAuthorityManager(new AuthorityManagerImpl());
		TargetGroup targetGroup = targetManager.loadGroup(15000L);
		GroupsEditorContext groupsEditorContext = new GroupsEditorContext(targetGroup,true);
		aReq.getSession().setAttribute(TabbedGroupController.EDITOR_CONTEXT, groupsEditorContext);

		HttpServletResponse aResp = new MockHttpServletResponse(); 
		TargetAccessCommand aCmd = new TargetAccessCommand();
		//Command booleans must initialise to false as JSP check boxes only update the command when they are checked
		assertFalse(aCmd.isDisplayTarget()); 
		TabbedController tc = new TabbedGroupController();

		TabConfig tabConfig = new TabConfig();
		tabConfig.setViewName("group");
		List<Tab> tabs = getTabList(targetManager);
		tabConfig.setTabs(tabs);

		tc.setTabConfig(tabConfig);
		tc.setDefaultCommandClass(org.webcurator.ui.groups.command.DefaultCommand.class);
		
		Tab currentTab = tabs.get(1);
		aCmd.setAccessZone(0);
		aCmd.setDisplayNote("Some Notes");
		aCmd.setDisplayChangeReason("Some reason");
		aCmd.setDisplayTarget(true);
		BindException aErrors = new BindException(aCmd, "TargetAccessCommand");
		testInstance.processTab(tc, currentTab, aReq, aResp, aCmd, aErrors);
		assertTrue(targetGroup.getAccessZone() == 0);
		assertTrue(targetGroup.getDisplayNote().equals("Some Notes"));
		assertTrue(targetGroup.getDisplayChangeReason().equals("Some reason"));
		assertTrue(targetGroup.isDisplayTarget());
	}

	@Test
	public final void testPreProcessNextTab() {

		HttpServletRequest aReq = new MockHttpServletRequest();
		TargetManager targetManager = new MockTargetManager(testFile);
		testInstance.setAuthorityManager(new AuthorityManagerImpl());
		TargetGroup targetGroup = targetManager.loadGroup(15000L);
		GroupsEditorContext groupsEditorContext = new GroupsEditorContext(targetGroup,true);
		aReq.getSession().setAttribute(TabbedGroupController.EDITOR_CONTEXT, groupsEditorContext);

		HttpServletResponse aResp = new MockHttpServletResponse(); 
		TargetAccessCommand aCmd = new TargetAccessCommand();
		TabbedController tc = new TabbedGroupController();

		TabConfig tabConfig = new TabConfig();
		tabConfig.setViewName("group");
		List<Tab> tabs = getTabList(targetManager);
		tabConfig.setTabs(tabs);

		tc.setTabConfig(tabConfig);
		tc.setDefaultCommandClass(org.webcurator.ui.groups.command.DefaultCommand.class);
		
		Tab currentTab = tabs.get(1);
		aCmd.setAccessZone(0);
		aCmd.setDisplayNote("Some notes");
		aCmd.setDisplayChangeReason("Some reason");
		aCmd.setDisplayTarget(true);
		BindException aErrors = new BindException(aCmd, "TargetAccessCommand");
		ModelAndView mav = testInstance.preProcessNextTab(tc, currentTab, aReq, aResp, aCmd, aErrors);
		assertTrue(((TargetAccessCommand)mav.getModel().get("command")).getAccessZone() == targetGroup.getAccessZone());
		assertTrue(((TargetAccessCommand)mav.getModel().get("command")).getDisplayNote().equals(targetGroup.getDisplayNote()));
		assertTrue(((TargetAccessCommand)mav.getModel().get("command")).getDisplayChangeReason().equals(targetGroup.getDisplayChangeReason()));
		assertTrue(((TargetAccessCommand)mav.getModel().get("command")).isDisplayTarget() == targetGroup.isDisplayTarget());
	}

	@Test
	public final void testProcessOther() {
		
		HttpServletRequest aReq = new MockHttpServletRequest();
		TargetManager targetManager = new MockTargetManager(testFile);
		testInstance.setAuthorityManager(new AuthorityManagerImpl());
		TargetGroup targetGroup = targetManager.loadGroup(15000L);
		GroupsEditorContext groupsEditorContext = new GroupsEditorContext(targetGroup,true);
		aReq.getSession().setAttribute(TabbedGroupController.EDITOR_CONTEXT, groupsEditorContext);

		HttpServletResponse aResp = new MockHttpServletResponse(); 
		TargetAccessCommand aCmd = new TargetAccessCommand();
		TabbedController tc = new TabbedGroupController();

		TabConfig tabConfig = new TabConfig();
		tabConfig.setViewName("group");
		List<Tab> tabs = getTabList(targetManager);
		tabConfig.setTabs(tabs);

		tc.setTabConfig(tabConfig);
		tc.setDefaultCommandClass(org.webcurator.ui.groups.command.DefaultCommand.class);
		
		Tab currentTab = tabs.get(1);
		aCmd.setAccessZone(0);
		aCmd.setDisplayNote("Some notes");
		aCmd.setDisplayTarget(true);
		BindException aErrors = new BindException(aCmd, "TargetAccessCommand");
		ModelAndView mav = testInstance.processOther(tc, currentTab, aReq, aResp, aCmd, aErrors);
		assertTrue(mav == null);
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
	public final void testSetAuthorityManager() {
		testInstance.setAuthorityManager(new AuthorityManagerImpl());
	}
}
