package org.webcurator.ui.groups.controller;

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
import org.webcurator.core.agency.MockAgencyUserManagerImpl;
import org.webcurator.core.targets.MockTargetManager;
import org.webcurator.core.targets.TargetManager;
import org.webcurator.test.*;
import org.webcurator.ui.groups.GroupsEditorContext;
import org.webcurator.ui.groups.command.AddParentsCommand;
import org.webcurator.ui.groups.command.GeneralCommand;
import org.webcurator.ui.groups.validator.GeneralValidator;
import org.webcurator.ui.target.command.TargetAccessCommand;
import org.webcurator.ui.util.DateUtils;
import org.webcurator.ui.util.Tab;
import org.webcurator.ui.util.TabConfig;
import org.webcurator.ui.util.TabbedController;
import org.webcurator.core.common.*;
import org.webcurator.domain.model.core.TargetGroup;

public class GeneralHandlerTest extends BaseWCTTest<GeneralHandler>{

	public GeneralHandlerTest()
	{
		super(GeneralHandler.class, 
				"src/test/java/org/webcurator/ui/groups/controller/GeneralHandlerTest.xml");
	}
	
	
	public void setUp() throws Exception 
	{
		super.setUp();
		DateUtils.get().setMessageSource(new MockMessageSource());
	}
	
	private List<Tab> getTabList(TargetManager targetManager)
	{
		List<Tab> tabs = new ArrayList<Tab>();
		
		Tab tabGeneral = new Tab();
		tabGeneral.setCommandClass(GeneralCommand.class);
		tabGeneral.setJsp("../groups-general.jsp");
		tabGeneral.setPageId("GENERAL");
		tabGeneral.setValidator(new GeneralValidator());

		GeneralHandler genHandler = new GeneralHandler();
		genHandler.setAgencyUserManager(new MockAgencyUserManagerImpl(testFile));
		genHandler.setAuthorityManager(new AuthorityManagerImpl());
		tabGeneral.setTabHandler(genHandler);
		
		tabs.add(tabGeneral);
		
		return tabs;
	}
	
	
	@Test
	public final void testProcessTab() {
		HttpServletRequest aReq = new MockHttpServletRequest();
		TargetManager targetManager = new MockTargetManager(testFile);
		testInstance.setTargetManager(targetManager);
		testSetAuthorityManager();
		testSetAgencyUserManager();
		testSetGroupTypesList();
		testSetSubGroupTypeName();
		
		TargetGroup targetGroup = targetManager.loadGroup(15000L);
		GroupsEditorContext groupsEditorContext = new GroupsEditorContext(targetGroup,true);
		aReq.getSession().setAttribute(TabbedGroupController.EDITOR_CONTEXT, groupsEditorContext);

		HttpServletResponse aResp = new MockHttpServletResponse(); 
		GeneralCommand aCmd = new GeneralCommand();
		TabbedController tc = new TabbedGroupController();

		TabConfig tabConfig = new TabConfig();
		tabConfig.setViewName("group");
		List<Tab> tabs = getTabList(targetManager);
		tabConfig.setTabs(tabs);

		tc.setTabConfig(tabConfig);
		tc.setDefaultCommandClass(org.webcurator.ui.groups.command.DefaultCommand.class);
		
		Tab currentTab = tabs.get(0);
		aCmd.setName("TestName");
		aCmd.setFromDate(null);
		BindException aErrors = new BindException(aCmd, "GeneralCommand");
		
		this.addCurrentUserPrivilege("CREATE_GROUP");
		this.addCurrentUserPrivilege("TAKE_OWNERSHIP");
		assertTrue(targetGroup.getDublinCoreMetaData() != null);
		assertTrue(targetGroup.getDublinCoreMetaData().getTitle().isEmpty());
		testInstance.processTab(tc, currentTab, aReq, aResp, aCmd, aErrors);
		assertTrue(targetGroup.getName().equals("TestName"));
		assertTrue(targetGroup.getFromDate() == null);
		assertTrue(targetGroup.getDublinCoreMetaData() != null);
		assertEquals(targetGroup.getDublinCoreMetaData().getTitle(),"TestName");

		aCmd.setName("TestName2");
		testInstance.processTab(tc, currentTab, aReq, aResp, aCmd, aErrors);
		assertTrue(targetGroup.getName().equals("TestName2"));
		assertTrue(targetGroup.getDublinCoreMetaData() != null);
		assertEquals(targetGroup.getDublinCoreMetaData().getTitle(),"TestName2");

		targetGroup.getDublinCoreMetaData().setTitle("TestName4");
		aCmd.setName("TestName3");
		testInstance.processTab(tc, currentTab, aReq, aResp, aCmd, aErrors);
		assertTrue(targetGroup.getName().equals("TestName3"));
		assertTrue(targetGroup.getDublinCoreMetaData() != null);
		assertEquals(targetGroup.getDublinCoreMetaData().getTitle(),"TestName4");
	}

	@Test
	public final void testPreProcessNextTab() {
		HttpServletRequest aReq = new MockHttpServletRequest();
		TargetManager targetManager = new MockTargetManager(testFile);
		testInstance.setTargetManager(targetManager);
		testSetAuthorityManager();
		testSetAgencyUserManager();
		testSetGroupTypesList();
		testSetSubGroupTypeName();
		TargetGroup targetGroup = targetManager.loadGroup(15000L);
		GroupsEditorContext groupsEditorContext = new GroupsEditorContext(targetGroup,true);
		aReq.getSession().setAttribute(TabbedGroupController.EDITOR_CONTEXT, groupsEditorContext);

		HttpServletResponse aResp = new MockHttpServletResponse(); 
		GeneralCommand aCmd = new GeneralCommand();
		TabbedController tc = new TabbedGroupController();

		TabConfig tabConfig = new TabConfig();
		tabConfig.setViewName("group");
		List<Tab> tabs = getTabList(targetManager);
		tabConfig.setTabs(tabs);

		tc.setTabConfig(tabConfig);
		tc.setDefaultCommandClass(org.webcurator.ui.groups.command.DefaultCommand.class);
		
		Tab currentTab = tabs.get(0);
		BindException aErrors = new BindException(aCmd, "GeneralCommand");
		ModelAndView mav = testInstance.preProcessNextTab(tc, currentTab, aReq, aResp, aCmd, aErrors);
		assertTrue(((GeneralCommand)mav.getModel().get("command")).getName() == targetGroup.getName());
		assertTrue(((GeneralCommand)mav.getModel().get("command")).getFromDate().equals(targetGroup.getFromDate()));
	}

	@Test
	public final void testProcessAddParent() {
		HttpServletRequest aReq = new MockHttpServletRequest();
		TargetManager targetManager = new MockTargetManager(testFile);
		testInstance.setTargetManager(targetManager);
		testSetAuthorityManager();
		testSetAgencyUserManager();
		testSetGroupTypesList();
		testSetSubGroupTypeName();
		TargetGroup targetGroup = targetManager.loadGroup(15000L);
		GroupsEditorContext groupsEditorContext = new GroupsEditorContext(targetGroup,true);
		aReq.getSession().setAttribute(TabbedGroupController.EDITOR_CONTEXT, groupsEditorContext);

		HttpServletResponse aResp = new MockHttpServletResponse(); 
		GeneralCommand aCmd = new GeneralCommand();
		aCmd.setAction(GeneralCommand.ACTION_ADD_PARENT);
		
		TabbedController tc = new TabbedGroupController();

		TabConfig tabConfig = new TabConfig();
		tabConfig.setViewName("group");
		List<Tab> tabs = getTabList(targetManager);
		tabConfig.setTabs(tabs);

		tc.setTabConfig(tabConfig);
		tc.setDefaultCommandClass(org.webcurator.ui.groups.command.DefaultCommand.class);
		
		Tab currentTab = tabs.get(0);
		BindException aErrors = new BindException(aCmd, "GeneralCommand");
		ModelAndView mav = testInstance.processOther(tc, currentTab, aReq, aResp, aCmd, aErrors);
		assertTrue(mav != null);
		assertEquals("group-add-parents", mav.getViewName());
		assertTrue(mav.getModel().get("command") instanceof AddParentsCommand);
	}

	@Test
	public final void testProcessOther() {
		HttpServletRequest aReq = new MockHttpServletRequest();
		TargetManager targetManager = new MockTargetManager(testFile);
		testInstance.setTargetManager(targetManager);
		testSetAuthorityManager();
		testSetAgencyUserManager();
		testSetGroupTypesList();
		testSetSubGroupTypeName();
		TargetGroup targetGroup = targetManager.loadGroup(15000L);
		GroupsEditorContext groupsEditorContext = new GroupsEditorContext(targetGroup,true);
		aReq.getSession().setAttribute(TabbedGroupController.EDITOR_CONTEXT, groupsEditorContext);

		HttpServletResponse aResp = new MockHttpServletResponse(); 
		GeneralCommand aCmd = new GeneralCommand();
		TabbedController tc = new TabbedGroupController();

		TabConfig tabConfig = new TabConfig();
		tabConfig.setViewName("group");
		List<Tab> tabs = getTabList(targetManager);
		tabConfig.setTabs(tabs);

		tc.setTabConfig(tabConfig);
		tc.setDefaultCommandClass(org.webcurator.ui.groups.command.DefaultCommand.class);
		
		Tab currentTab = tabs.get(0);
		BindException aErrors = new BindException(aCmd, "GeneralCommand");
		ModelAndView mav = testInstance.processOther(tc, currentTab, aReq, aResp, aCmd, aErrors);
		assertTrue(mav != null);
		assertEquals("group", mav.getViewName());
		assertTrue(((GeneralCommand)mav.getModel().get("command")).getName() == targetGroup.getName());
	}

	@Test
	public final void testInitBinder() {
		MockHttpServletRequest request = new MockHttpServletRequest();
		ServletRequestDataBinder binder = new ServletRequestDataBinder(new GeneralCommand(), "command");
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

	@Test
	public final void testSetSubGroupTypeName() {
		testInstance.setSubGroupTypeName("Sub-Group");
	}

	@Test
	public final void testSetSubGroupSeperator() {
		testInstance.setSubGroupSeparator(" > ");
	}

	@Test
	public final void testSetGroupTypesList() {
		List<String> subGroupTypes = new ArrayList<String>();
		subGroupTypes.add("");
		subGroupTypes.add("Collection");
		subGroupTypes.add("Subject");
		subGroupTypes.add("Thematic");
		subGroupTypes.add("Event");
		subGroupTypes.add("Functional");
		subGroupTypes.add("Sub-Group");
		
		WCTTreeSet groupTypesList = new WCTTreeSet(subGroupTypes, 50);
		testInstance.setGroupTypesList(groupTypesList);
	}

	@Test
	public final void testSetAgencyUserManager() {
		testInstance.setAgencyUserManager(new MockAgencyUserManagerImpl(testFile));
	}

	@Test
	public final void testSetTargetManager() {
		testInstance.setTargetManager(new MockTargetManager(testFile));
	}

}
