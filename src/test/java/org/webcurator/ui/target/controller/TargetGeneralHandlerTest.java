package org.webcurator.ui.target.controller;

import static org.junit.Assert.*;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.webcurator.auth.AuthorityManagerImpl;
import org.webcurator.core.agency.MockAgencyUserManagerImpl;
import org.webcurator.core.targets.MockTargetManager;
import org.webcurator.core.targets.TargetManager;
import org.webcurator.domain.MockUserRoleDAO;
import org.webcurator.domain.model.core.Target;
import org.webcurator.test.BaseWCTTest;
import org.webcurator.ui.target.TargetEditorContext;
import org.webcurator.ui.target.command.TargetGeneralCommand;
import org.webcurator.ui.target.validator.TargetGeneralValidator;
import org.webcurator.ui.util.Tab;
import org.webcurator.ui.util.TabConfig;
import org.webcurator.ui.util.TabbedController;

public class TargetGeneralHandlerTest extends BaseWCTTest<TargetGeneralHandler>{

	public TargetGeneralHandlerTest()
	{
		super(TargetGeneralHandler.class,
		"src/test/java/org/webcurator/ui/target/controller/TargetGeneralHandlerTest.xml");
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
		
		return tabs;
	}
	
	
	@Test
	public final void testProcessTab() {
		
		HttpServletRequest aReq = new MockHttpServletRequest();
		TargetManager targetManager = new MockTargetManager(testFile);
		testInstance.setAuthorityManager(new AuthorityManagerImpl());
		testInstance.setTargetManager(targetManager);
		testInstance.setAgencyUserManager(new MockAgencyUserManagerImpl(testFile));
		testInstance.setUserRoleDao(new MockUserRoleDAO(testFile));
		Target target = targetManager.load(4000L);
		TargetEditorContext targetEditorContext = new TargetEditorContext(targetManager,target,true);
		aReq.getSession().setAttribute(TabbedTargetController.EDITOR_CONTEXT, targetEditorContext);

		HttpServletResponse aResp = new MockHttpServletResponse(); 
		TargetGeneralCommand aCmd = TargetGeneralCommand.buildFromModel(target);
		TabbedController tc = new TabbedTargetController();

		TabConfig tabConfig = new TabConfig();
		tabConfig.setViewName("target");
		List<Tab> tabs = getTabList(targetManager);
		tabConfig.setTabs(tabs);

		tc.setTabConfig(tabConfig);
		tc.setDefaultCommandClass(org.webcurator.ui.target.command.TargetDefaultCommand.class);
		
		Tab currentTab = tabs.get(0);
		aCmd.setName("TestName");
		
		BindException aErrors = new BindException(aCmd, "TargetGeneralCommand");

		assertTrue(target.getDublinCoreMetaData() != null);
		assertTrue(target.getDublinCoreMetaData().getTitle().isEmpty());
		testInstance.processTab(tc, currentTab, aReq, aResp, aCmd, aErrors);
		assertTrue(target.getName().equals("TestName"));
		assertTrue(target.getDublinCoreMetaData() != null);
		assertEquals(target.getDublinCoreMetaData().getTitle(),"TestName");

		aCmd.setName("TestName2");
		testInstance.processTab(tc, currentTab, aReq, aResp, aCmd, aErrors);
		assertTrue(target.getName().equals("TestName2"));
		assertTrue(target.getDublinCoreMetaData() != null);
		assertEquals(target.getDublinCoreMetaData().getTitle(),"TestName2");

		target.getDublinCoreMetaData().setTitle("TestName4");
		aCmd.setName("TestName3");
		testInstance.processTab(tc, currentTab, aReq, aResp, aCmd, aErrors);
		assertTrue(target.getName().equals("TestName3"));
		assertTrue(target.getDublinCoreMetaData() != null);
		assertEquals(target.getDublinCoreMetaData().getTitle(),"TestName4");
		
	}

	private String getEncodedString(String value)
	{
		try
		{
			return URLEncoder.encode(value, "UTF-8");
		}
		catch(UnsupportedEncodingException e)
		{
			return value;
		}
	}
	
	@Test
	public final void testPreProcessNextTab() {
		String name = "A test & target";

		HttpServletRequest aReq = new MockHttpServletRequest();
		TargetManager targetManager = new MockTargetManager(testFile);
		testInstance.setAuthorityManager(new AuthorityManagerImpl());
		testInstance.setTargetManager(targetManager);
		testInstance.setAgencyUserManager(new MockAgencyUserManagerImpl(testFile));
		testInstance.setUserRoleDao(new MockUserRoleDAO(testFile));
		Target target = targetManager.load(4000L);
		target.setName(name);
		TargetEditorContext targetEditorContext = new TargetEditorContext(targetManager,target,true);
		aReq.getSession().setAttribute(TabbedTargetController.EDITOR_CONTEXT, targetEditorContext);

		HttpServletResponse aResp = new MockHttpServletResponse(); 
		TargetGeneralCommand aCmd = TargetGeneralCommand.buildFromModel(target);
		assertEquals(aCmd.getName(), name);
		assertEquals(aCmd.getEncodedName(), getEncodedString(name));
		TabbedController tc = new TabbedTargetController();

		TabConfig tabConfig = new TabConfig();
		tabConfig.setViewName("target");
		List<Tab> tabs = getTabList(targetManager);
		tabConfig.setTabs(tabs);

		tc.setTabConfig(tabConfig);
		tc.setDefaultCommandClass(org.webcurator.ui.target.command.TargetDefaultCommand.class);
		
		Tab currentTab = tabs.get(0);
		target.setRunOnApproval(true);
		BindException aErrors = new BindException(aCmd, "TargetGeneralCommand");
		ModelAndView mav = testInstance.preProcessNextTab(tc, currentTab, aReq, aResp, aCmd, aErrors);
		assertNotNull(mav);
	}

	@Test
	public final void testProcessOther() {
		HttpServletRequest aReq = new MockHttpServletRequest();
		TargetManager targetManager = new MockTargetManager(testFile);
		testInstance.setAuthorityManager(new AuthorityManagerImpl());
		testInstance.setTargetManager(targetManager);
		testInstance.setAgencyUserManager(new MockAgencyUserManagerImpl(testFile));
		testInstance.setUserRoleDao(new MockUserRoleDAO(testFile));
		Target target = targetManager.load(4000L);
		TargetEditorContext targetEditorContext = new TargetEditorContext(targetManager,target,true);
		aReq.getSession().setAttribute(TabbedTargetController.EDITOR_CONTEXT, targetEditorContext);

		HttpServletResponse aResp = new MockHttpServletResponse(); 
		TargetGeneralCommand aCmd = TargetGeneralCommand.buildFromModel(target);
		TabbedController tc = new TabbedTargetController();

		TabConfig tabConfig = new TabConfig();
		tabConfig.setViewName("target");
		List<Tab> tabs = getTabList(targetManager);
		tabConfig.setTabs(tabs);

		tc.setTabConfig(tabConfig);
		tc.setDefaultCommandClass(org.webcurator.ui.target.command.TargetDefaultCommand.class);
		
		Tab currentTab = tabs.get(0);
		BindException aErrors = new BindException(aCmd, "TargetGeneralCommand");
		ModelAndView mav = testInstance.processOther(tc, currentTab, aReq, aResp, aCmd, aErrors);
		assertNull(mav);
	}

	@Test
	public final void testSetUserRoleDao() {
		testInstance.setUserRoleDao(new MockUserRoleDAO(testFile));
	}

	@Test
	public final void testSetTargetManager() {
		testInstance.setTargetManager(new MockTargetManager(testFile));
	}

	@Test
	public final void testSetAgencyUserManager() {
		testInstance.setAgencyUserManager(new MockAgencyUserManagerImpl(testFile));
	}

	@Test
	public final void testSetAuthorityManager() {
		testInstance.setAuthorityManager(new AuthorityManagerImpl());
	}

}
