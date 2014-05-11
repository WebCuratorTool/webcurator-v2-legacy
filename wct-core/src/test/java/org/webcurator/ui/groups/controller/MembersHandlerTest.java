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
import org.springframework.web.servlet.ModelAndView;
import org.webcurator.auth.AuthorityManagerImpl;
import org.webcurator.core.targets.MockTargetManager;
import org.webcurator.core.targets.TargetManager;
import org.webcurator.domain.model.core.TargetGroup;
import org.webcurator.test.BaseWCTTest;
import org.webcurator.ui.groups.GroupsEditorContext;
import org.webcurator.ui.groups.command.GeneralCommand;
import org.webcurator.ui.groups.command.MembersCommand;
import org.webcurator.ui.groups.command.MoveTargetsCommand;
import org.webcurator.ui.groups.validator.GeneralValidator;
import org.webcurator.ui.util.DateUtils;
import org.webcurator.ui.util.Tab;
import org.webcurator.ui.util.TabConfig;
import org.webcurator.ui.util.TabbedController;

public class MembersHandlerTest extends BaseWCTTest<MembersHandler> {

	public MembersHandlerTest() {
		super(MembersHandler.class, 
				"src/test/java/org/webcurator/ui/groups/controller/membershandlertest.xml");
	}

	public void setUp() throws Exception 
	{
		super.setUp();
		DateUtils.get().setMessageSource(new MockMessageSource());
	}
	
	private List<Tab> getTabList(TargetManager tm)
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
		
		Tab tabMembers = new Tab();
		tabMembers.setCommandClass(MembersCommand.class);
		tabMembers.setJsp("../groups-members.jsp");
		tabMembers.setPageId("MEMBERS");

		MembersHandler membersHandler = new MembersHandler();
		membersHandler.setTargetManager(tm);
		tabMembers.setTabHandler(membersHandler);
		
		tabs.add(tabMembers);
		return tabs;
	}
	
	@Test
	public final void testPreProcessNextTab() {
		HttpServletRequest aReq = new MockHttpServletRequest();
		TargetManager targetManager = new MockTargetManager(testFile);
		testInstance.setTargetManager(targetManager);
		testSetSubGroupSeparator();
		TargetGroup targetGroup = targetManager.loadGroup(15000L);
		GroupsEditorContext groupsEditorContext = new GroupsEditorContext(targetGroup,true);
		aReq.getSession().setAttribute(TabbedGroupController.EDITOR_CONTEXT, groupsEditorContext);

		HttpServletResponse aResp = new MockHttpServletResponse(); 
		MembersCommand aCmd = new MembersCommand();
		TabbedController tc = new TabbedGroupController();

		TabConfig tabConfig = new TabConfig();
		tabConfig.setViewName("group");
		List<Tab> tabs = getTabList(targetManager);
		tabConfig.setTabs(tabs);

		tc.setTabConfig(tabConfig);
		tc.setDefaultCommandClass(org.webcurator.ui.groups.command.DefaultCommand.class);
		
		Tab currentTab = tabs.get(1);
		BindException aErrors = new BindException(aCmd, "MembersCommand");
		ModelAndView mav = testInstance.preProcessNextTab(tc, currentTab, aReq, aResp, aCmd, aErrors);
		assertTrue(mav.getModel().get("command") instanceof MembersCommand);
	}

	@Test
	public final void testProcessMoveTargets() {
		HttpServletRequest aReq = new MockHttpServletRequest();
		TargetManager targetManager = new MockTargetManager(testFile);
		testInstance.setTargetManager(targetManager);
		testSetSubGroupSeparator();
		TargetGroup targetGroup = targetManager.loadGroup(15000L);
		GroupsEditorContext groupsEditorContext = new GroupsEditorContext(targetGroup,true);
		aReq.getSession().setAttribute(TabbedGroupController.EDITOR_CONTEXT, groupsEditorContext);

		HttpServletResponse aResp = new MockHttpServletResponse(); 
		MembersCommand aCmd = new MembersCommand();
		aCmd.setActionCmd(MembersCommand.ACTION_MOVE_TARGETS);
		aCmd.setTargetOids(new long[]{4000});
		
		TabbedController tc = new TabbedGroupController();

		TabConfig tabConfig = new TabConfig();
		tabConfig.setViewName("group");
		List<Tab> tabs = getTabList(targetManager);
		tabConfig.setTabs(tabs);

		tc.setTabConfig(tabConfig);
		tc.setDefaultCommandClass(org.webcurator.ui.groups.command.DefaultCommand.class);
		
		Tab currentTab = tabs.get(0);
		BindException aErrors = new BindException(aCmd, "MembersCommand");
		ModelAndView mav = testInstance.processOther(tc, currentTab, aReq, aResp, aCmd, aErrors);
		assertTrue(mav != null);
		assertEquals("group-move-targets", mav.getViewName());
		assertTrue(mav.getModel().get("command") instanceof MoveTargetsCommand);
	}

	@Test
	public final void testProcessUnlinkMember() {
		HttpServletRequest aReq = new MockHttpServletRequest();
		TargetManager targetManager = new MockTargetManager(testFile);
		testInstance.setTargetManager(targetManager);
		testSetSubGroupSeparator();
		TargetGroup targetGroup = targetManager.loadGroup(15000L);
		GroupsEditorContext groupsEditorContext = new GroupsEditorContext(targetGroup,true);
		aReq.getSession().setAttribute(TabbedGroupController.EDITOR_CONTEXT, groupsEditorContext);

		HttpServletResponse aResp = new MockHttpServletResponse(); 
		MembersCommand aCmd = new MembersCommand();
		aCmd.setActionCmd(MembersCommand.ACTION_UNLINK_MEMBER);
		aCmd.setSelectedPageSize("10");
		aCmd.setChildOid(4000L);
		
		TabbedController tc = new TabbedGroupController();

		TabConfig tabConfig = new TabConfig();
		tabConfig.setViewName("group");
		List<Tab> tabs = getTabList(targetManager);
		tabConfig.setTabs(tabs);

		tc.setTabConfig(tabConfig);
		tc.setDefaultCommandClass(org.webcurator.ui.groups.command.DefaultCommand.class);

		assertEquals(0, targetGroup.getRemovedChildren().size());
		
		Tab currentTab = tabs.get(1);
		BindException aErrors = new BindException(aCmd, "MembersCommand");
		ModelAndView mav = testInstance.processOther(tc, currentTab, aReq, aResp, aCmd, aErrors);
		assertTrue(mav != null);
		assertEquals("group", mav.getViewName());
		assertTrue(mav.getModel().get("command") instanceof MembersCommand);
		assertEquals(1, targetGroup.getRemovedChildren().size());
	}

	@Test
	public final void testSetTargetManager() {
		testInstance.setTargetManager(new MockTargetManager(testFile));
	}

	@Test
	public final void testSetSubGroupSeparator() {
		testInstance.setSubGroupSeparator(" > ");
	}

}
