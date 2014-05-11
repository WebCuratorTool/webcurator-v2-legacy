package org.webcurator.ui.target.controller;

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
import org.webcurator.ui.target.command.*;
import org.webcurator.ui.target.TargetEditorContext;
import org.webcurator.core.targets.*;
import org.webcurator.ui.util.*;
import org.webcurator.auth.AuthorityManagerImpl;
import org.webcurator.domain.model.core.*;
import org.webcurator.ui.target.validator.*;

public class TargetAccessHandlerTest extends BaseWCTTest<TargetAccessHandler> {

	public TargetAccessHandlerTest()
	{
		super(TargetAccessHandler.class,
				"src/test/java/org/webcurator/ui/target/controller/targetaccesshandlertest.xml");
	}
	
	public void setUp() throws Exception 
	{
		super.setUp();
	}
	
	private List<Tab> getTabList(TargetManager targetManager)
	{
		List<Tab> tabs = new ArrayList<Tab>();
		
		Tab tabGeneral = new Tab();
		tabGeneral.setCommandClass(TargetGeneralCommand.class);
		tabGeneral.setJsp("../target-general.jsp");
		tabGeneral.setPageId("GENERAL");
		tabGeneral.setTitle("general");
		tabGeneral.setValidator(new TargetGeneralValidator());

		TargetGeneralHandler genHandler = new TargetGeneralHandler();
		genHandler.setAuthorityManager(new AuthorityManagerImpl());
		tabGeneral.setTabHandler(genHandler);
		
		tabs.add(tabGeneral);
		
		Tab tabAccess = new Tab();
		tabAccess.setCommandClass(TargetAccessCommand.class);
		tabAccess.setJsp("../target-access.jsp");
		tabAccess.setPageId("ACCESS");

		TargetAccessHandler accessHandler = new TargetAccessHandler();
		tabAccess.setTabHandler(accessHandler);
		
		tabs.add(tabAccess);
		return tabs;
	}
	
	
	@Test
	public final void testProcessTab() {
		
		HttpServletRequest aReq = new MockHttpServletRequest();
		TargetManager targetManager = new MockTargetManager(testFile);
		testInstance.setAuthorityManager(new AuthorityManagerImpl());
		Target target = targetManager.load(4000L);
		TargetEditorContext targetEditorContext = new TargetEditorContext(targetManager,target,true);
		aReq.getSession().setAttribute(TabbedTargetController.EDITOR_CONTEXT, targetEditorContext);

		HttpServletResponse aResp = new MockHttpServletResponse(); 
		TargetAccessCommand aCmd = new TargetAccessCommand();
		//Command booleans must initialise to false as JSP check boxes only update the command when they are checked
		assertFalse(aCmd.isDisplayTarget()); 
		TabbedController tc = new TabbedTargetController();

		TabConfig tabConfig = new TabConfig();
		tabConfig.setViewName("target");
		List<Tab> tabs = getTabList(targetManager);
		tabConfig.setTabs(tabs);

		tc.setTabConfig(tabConfig);
		tc.setDefaultCommandClass(org.webcurator.ui.target.command.TargetDefaultCommand.class);
		
		Tab currentTab = tabs.get(1);
		aCmd.setAccessZone(0);
		aCmd.setDisplayNote("Some notes");
		aCmd.setDisplayChangeReason("Some reason");
		aCmd.setDisplayTarget(true);
		BindException aErrors = new BindException(aCmd, "TargetAccessCommand");
		testInstance.processTab(tc, currentTab, aReq, aResp, aCmd, aErrors);
		assertTrue(target.getAccessZone() == 0);
		assertTrue(target.getDisplayNote().equals("Some notes"));
		assertTrue(target.getDisplayChangeReason().equals("Some reason"));
		assertTrue(target.isDisplayTarget());
	}

	@Test
	public final void testPreProcessNextTab() {

		HttpServletRequest aReq = new MockHttpServletRequest();
		TargetManager targetManager = new MockTargetManager(testFile);
		testInstance.setAuthorityManager(new AuthorityManagerImpl());
		Target target = targetManager.load(4000L);
		TargetEditorContext targetEditorContext = new TargetEditorContext(targetManager,target,true);
		aReq.getSession().setAttribute(TabbedTargetController.EDITOR_CONTEXT, targetEditorContext);

		HttpServletResponse aResp = new MockHttpServletResponse(); 
		TargetAccessCommand aCmd = new TargetAccessCommand();
		TabbedController tc = new TabbedTargetController();

		TabConfig tabConfig = new TabConfig();
		tabConfig.setViewName("target");
		List<Tab> tabs = getTabList(targetManager);
		tabConfig.setTabs(tabs);

		tc.setTabConfig(tabConfig);
		tc.setDefaultCommandClass(org.webcurator.ui.target.command.TargetDefaultCommand.class);
		
		Tab currentTab = tabs.get(1);
		aCmd.setAccessZone(0);
		aCmd.setDisplayNote("Some notes");
		aCmd.setDisplayChangeReason("Some reason");
		aCmd.setDisplayTarget(true);
		BindException aErrors = new BindException(aCmd, "TargetAccessCommand");
		ModelAndView mav = testInstance.preProcessNextTab(tc, currentTab, aReq, aResp, aCmd, aErrors);
		assertTrue(((TargetAccessCommand)mav.getModel().get("command")).getAccessZone() == target.getAccessZone());
		assertTrue(((TargetAccessCommand)mav.getModel().get("command")).getDisplayNote().equals(target.getDisplayNote()));
		assertTrue(((TargetAccessCommand)mav.getModel().get("command")).getDisplayChangeReason().equals(target.getDisplayChangeReason()));
		assertTrue(((TargetAccessCommand)mav.getModel().get("command")).isDisplayTarget() == target.isDisplayTarget());
	}

	@Test
	public final void testProcessOther() {
		
		HttpServletRequest aReq = new MockHttpServletRequest();
		TargetManager targetManager = new MockTargetManager(testFile);
		testInstance.setAuthorityManager(new AuthorityManagerImpl());
		Target target = targetManager.load(4000L);
		TargetEditorContext targetEditorContext = new TargetEditorContext(targetManager,target,true);
		aReq.getSession().setAttribute(TabbedTargetController.EDITOR_CONTEXT, targetEditorContext);

		HttpServletResponse aResp = new MockHttpServletResponse(); 
		TargetAccessCommand aCmd = new TargetAccessCommand();
		TabbedController tc = new TabbedTargetController();

		TabConfig tabConfig = new TabConfig();
		tabConfig.setViewName("target");
		List<Tab> tabs = getTabList(targetManager);
		tabConfig.setTabs(tabs);

		tc.setTabConfig(tabConfig);
		tc.setDefaultCommandClass(org.webcurator.ui.target.command.TargetDefaultCommand.class);
		
		Tab currentTab = tabs.get(1);
		aCmd.setAccessZone(0);
		aCmd.setDisplayNote("Some notes");
		aCmd.setDisplayChangeReason("Some reason");
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
