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
import org.webcurator.ui.target.AbstractTargetEditorContext;
import org.webcurator.domain.*;
import org.webcurator.core.targets.*;
import org.webcurator.ui.util.*;
import org.webcurator.auth.AuthorityManagerImpl;
import org.webcurator.domain.model.core.*;
import org.webcurator.ui.target.validator.*;

public class TargetSchedulesHandlerTest extends BaseWCTTest<TargetSchedulesHandler> {

	public TargetSchedulesHandlerTest()
	{
		super(TargetSchedulesHandler.class,
				"src/test/java/org/webcurator/ui/target/controller/TargetSchedulesHandlerTest.xml");
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
		
		Tab tabSchedules = new Tab();
		tabSchedules.setCommandClass(TargetAccessCommand.class);
		tabSchedules.setJsp("../target-schedules.jsp");
		tabSchedules.setPageId("SCHEDULES");

		TargetSchedulesHandler schedulesHandler = new TargetSchedulesHandler();
		tabSchedules.setTabHandler(schedulesHandler);
		
		tabs.add(tabSchedules);
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
		TargetSchedulesCommand aCmd = new TargetSchedulesCommand();
		TabbedController tc = new TabbedTargetController();

		TabConfig tabConfig = new TabConfig();
		tabConfig.setViewName("target");
		List<Tab> tabs = getTabList(targetManager);
		tabConfig.setTabs(tabs);

		tc.setTabConfig(tabConfig);
		tc.setDefaultCommandClass(org.webcurator.ui.target.command.TargetDefaultCommand.class);
		
		Tab currentTab = tabs.get(1);
		aCmd.setHarvestNow(true);
		BindException aErrors = new BindException(aCmd, "TargetSchedulesCommand");
		testInstance.processTab(tc, currentTab, aReq, aResp, aCmd, aErrors);
		assertFalse(target.isHarvestNow());
	}

	@Test
	public final void testPreProcessNextTab() {
		
		HttpServletRequest aReq = new MockHttpServletRequest();
		TargetManager targetManager = new MockTargetManager(testFile);
		testInstance.setTargetManager(targetManager);
		testInstance.setAuthorityManager(new AuthorityManagerImpl());
		testInstance.setPatternFactory(new SpringSchedulePatternFactory());
		Target target = targetManager.load(4000L);
		//TargetEditorContext targetEditorContext = new TargetEditorContext(targetManager,target,true);
		AbstractTargetEditorContext targetEditorContext = new AbstractTargetEditorContext(target,true);
		aReq.getSession().setAttribute(TabbedTargetController.EDITOR_CONTEXT, targetEditorContext);

		HttpServletResponse aResp = new MockHttpServletResponse(); 
		TargetSchedulesCommand aCmd = new TargetSchedulesCommand();
		TabbedController tc = new TabbedTargetController();

		TabConfig tabConfig = new TabConfig();
		tabConfig.setViewName("target");
		List<Tab> tabs = getTabList(targetManager);
		tabConfig.setTabs(tabs);

		tc.setTabConfig(tabConfig);
		tc.setDefaultCommandClass(org.webcurator.ui.target.command.TargetDefaultCommand.class);
		
		Tab currentTab = tabs.get(1);
		aCmd.setHarvestNow(true);
		BindException aErrors = new BindException(aCmd, "TargetSchedulesCommand");
		testInstance.setContextSessionKey(TabbedTargetController.EDITOR_CONTEXT);
		ModelAndView mav = testInstance.preProcessNextTab(tc, currentTab, aReq, aResp, aCmd, aErrors);
		assertTrue(mav != null);
		//assertTrue(((TargetSchedulesCommand)mav.getModel().get("command")).isHarvestNowSet() == target.isHarvestNow());
	}

	@Test
	public final void testProcessOther() {
		
		HttpServletRequest aReq = new MockHttpServletRequest();
		TargetManager targetManager = new MockTargetManager(testFile);
		testInstance.setTargetManager(targetManager);
		testInstance.setAuthorityManager(new AuthorityManagerImpl());
		testInstance.setPatternFactory(new SpringSchedulePatternFactory());
		Target target = targetManager.load(4000L);
		TargetEditorContext targetEditorContext = new TargetEditorContext(targetManager,target,true);
		aReq.getSession().setAttribute(TabbedTargetController.EDITOR_CONTEXT, targetEditorContext);

		HttpServletResponse aResp = new MockHttpServletResponse(); 
		TargetSchedulesCommand aCmd = new TargetSchedulesCommand();
		TabbedController tc = new TabbedTargetController();

		TabConfig tabConfig = new TabConfig();
		tabConfig.setViewName("target");
		List<Tab> tabs = getTabList(targetManager);
		tabConfig.setTabs(tabs);

		tc.setTabConfig(tabConfig);
		tc.setDefaultCommandClass(org.webcurator.ui.target.command.TargetDefaultCommand.class);
		
		Tab currentTab = tabs.get(1);
		aCmd.setHarvestNow(true);
		BindException aErrors = new BindException(aCmd, "TargetSchedulesCommand");
		testInstance.setContextSessionKey(TabbedTargetController.EDITOR_CONTEXT);
		ModelAndView mav = testInstance.processOther(tc, currentTab, aReq, aResp, aCmd, aErrors);
		assertTrue(mav != null);
	}
	

	@Test
	public final void testSetAuthorityManager() {
		testInstance.setAuthorityManager(new AuthorityManagerImpl());
	}
}
