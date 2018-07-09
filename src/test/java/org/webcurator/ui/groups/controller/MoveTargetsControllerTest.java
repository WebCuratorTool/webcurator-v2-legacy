package org.webcurator.ui.groups.controller;

import static org.junit.Assert.*;

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
import org.webcurator.core.common.WCTTreeSet;
import org.webcurator.core.targets.MockTargetManager;
import org.webcurator.core.targets.TargetManager;
import org.webcurator.domain.model.auth.Privilege;
import org.webcurator.domain.model.core.TargetGroup;
import org.webcurator.test.BaseWCTTest;
import org.webcurator.ui.groups.GroupsEditorContext;
import org.webcurator.ui.groups.command.GeneralCommand;
import org.webcurator.ui.groups.command.MembersCommand;
import org.webcurator.ui.groups.command.MoveTargetsCommand;
import org.webcurator.ui.groups.validator.GeneralValidator;
import org.webcurator.ui.util.Tab;
import org.webcurator.ui.util.TabConfig;

public class MoveTargetsControllerTest extends BaseWCTTest<MoveTargetsController> {

	public MoveTargetsControllerTest() {
		super(MoveTargetsController.class, "src/test/java/org/webcurator/ui/groups/controller/MoveTargetsControllerTest.xml");
	}

	private TargetManager tm = null;
	
	public void setUp() throws Exception 
	{
		super.setUp();
		tm = new MockTargetManager(testFile);
	}

	
	private List<Tab> getTabList()
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
		genHandler.setAgencyUserManager(new MockAgencyUserManagerImpl(testFile));
		genHandler.setTargetManager(tm);
		genHandler.setSubGroupSeparator(" > ");
		genHandler.setSubGroupTypeName("Sub-Group");
		List<String> subGroupTypes = new ArrayList<String>();
		subGroupTypes.add("");
		subGroupTypes.add("Collection");
		subGroupTypes.add("Subject");
		subGroupTypes.add("Thematic");
		subGroupTypes.add("Event");
		subGroupTypes.add("Functional");
		subGroupTypes.add("Sub-Group");
		
		WCTTreeSet groupTypesList = new WCTTreeSet(subGroupTypes, 50);
		genHandler.setGroupTypesList(groupTypesList);
		
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
	
	private GroupsEditorContext bindEditorContext(HttpServletRequest request, Long groupOid)
	{
		GroupsEditorContext groupsEditorContext = new GroupsEditorContext(tm.loadGroup(groupOid),true);
		request.getSession().setAttribute(TabbedGroupController.EDITOR_CONTEXT, groupsEditorContext);
		
		return groupsEditorContext;
	}
	
	@Test
	public final void testGetEditorContext() {
		try
		{
			HttpServletRequest aReq = new MockHttpServletRequest();
			GroupsEditorContext groupsEditorContext = bindEditorContext(aReq, 15000L);
			
			GroupsEditorContext gec = testInstance.getEditorContext(aReq);
			assertNotNull(gec);
			assertEquals(groupsEditorContext, gec);
		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}

	@Test
	public final void testHandleMoveTargets() {
		try
		{
			testSetAuthorityManager();
			testSetTargetManager();
			testSetGroupsController();
			
			this.addCurrentUserPrivilege(Privilege.ADD_TARGET_TO_GROUP);

			HttpServletRequest request = new MockHttpServletRequest();
			HttpServletResponse response = new MockHttpServletResponse();
			MoveTargetsCommand command = new MoveTargetsCommand();

			BindException errors = new BindException(command, "MoveTargetsCommand");

			bindEditorContext(request, 15002L);
			
			GroupsEditorContext ctx = testInstance.getEditorContext(request);
			
			List<Long> targetsToMove = new ArrayList<Long>();
			targetsToMove.add(4000L);
			targetsToMove.add(4001L);
			
			ctx.setTargetsToMove(targetsToMove);
			
			command.setActionCmd(MoveTargetsCommand.ACTION_MOVE_TARGETS);
			long[]  oids = {15000L};
			command.setParentOids(oids);
			
			TargetGroup srcGrp = tm.loadGroup(15002L);
			assertEquals(0, srcGrp.getRemovedChildren().size());
			TargetGroup dstGrp = tm.loadGroup(15000L);
			assertEquals(0, dstGrp.getNewChildren().size());
			
			ModelAndView mav = testInstance.handle(request, response, command, errors);
			assertNotNull(mav);
			assertEquals(mav.getViewName(), "groups");
			
			assertEquals(2, srcGrp.getRemovedChildren().size());
			assertTrue(srcGrp.getRemovedChildren().contains(4000L));
			assertTrue(srcGrp.getRemovedChildren().contains(4001L));
			
			assertEquals(2, dstGrp.getNewChildren().size());
		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}

	@Test
	public final void testHandleMoveTargetsNoPriv() {
		try
		{
			testSetAuthorityManager();
			testSetTargetManager();
			testSetGroupsController();
			
			this.removeAllCurrentUserPrivileges();

			HttpServletRequest request = new MockHttpServletRequest();
			HttpServletResponse response = new MockHttpServletResponse();
			MoveTargetsCommand command = new MoveTargetsCommand();

			BindException errors = new BindException(command, "MoveTargetsCommand");

			bindEditorContext(request, 15002L);
			
			GroupsEditorContext ctx = testInstance.getEditorContext(request);
			
			List<Long> targetsToMove = new ArrayList<Long>();
			targetsToMove.add(4000L);
			targetsToMove.add(4001L);
			
			ctx.setTargetsToMove(targetsToMove);
			
			command.setActionCmd(MoveTargetsCommand.ACTION_MOVE_TARGETS);
			long[]  oids = {15000L};
			command.setParentOids(oids);
			
			TargetGroup srcGrp = tm.loadGroup(15002L);
			assertEquals(0, srcGrp.getRemovedChildren().size());
			TargetGroup dstGrp = tm.loadGroup(15000L);
			assertEquals(0, dstGrp.getNewChildren().size());
			
			ModelAndView mav = testInstance.handle(request, response, command, errors);
			assertNotNull(mav);
			assertEquals(mav.getViewName(), "groups");
			
			assertEquals(0, srcGrp.getRemovedChildren().size());
			assertEquals(0, dstGrp.getNewChildren().size());
		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}

	@Test
	public final void testHandleCancel() {
		try
		{
			testSetAuthorityManager();
			testSetTargetManager();
			testSetGroupsController();
			
			this.addCurrentUserPrivilege(Privilege.ADD_TARGET_TO_GROUP);

			HttpServletRequest request = new MockHttpServletRequest();
			HttpServletResponse response = new MockHttpServletResponse();
			MoveTargetsCommand command = new MoveTargetsCommand();

			BindException errors = new BindException(command, "MoveTargetsCommand");

			bindEditorContext(request, 15002L);
			
			GroupsEditorContext ctx = testInstance.getEditorContext(request);
			
			List<Long> targetsToMove = new ArrayList<Long>();
			targetsToMove.add(4000L);
			targetsToMove.add(4001L);
			
			ctx.setTargetsToMove(targetsToMove);
			
			command.setActionCmd(MoveTargetsCommand.ACTION_CANCEL);
			long[]  oids = {15000L};
			command.setParentOids(oids);
			
			TargetGroup srcGrp = tm.loadGroup(15002L);
			assertEquals(0, srcGrp.getRemovedChildren().size());
			TargetGroup dstGrp = tm.loadGroup(15000L);
			assertEquals(0, dstGrp.getNewChildren().size());
			
			ModelAndView mav = testInstance.handle(request, response, command, errors);
			assertNotNull(mav);
			assertEquals(mav.getViewName(), "groups");
			
			assertEquals(0, srcGrp.getRemovedChildren().size());
			assertEquals(0, dstGrp.getNewChildren().size());
		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}

	@Test
	public final void testHandleOther() {
		try
		{
			testSetAuthorityManager();
			testSetTargetManager();
			testSetGroupsController();
			
			this.addCurrentUserPrivilege(Privilege.ADD_TARGET_TO_GROUP);

			HttpServletRequest request = new MockHttpServletRequest();
			HttpServletResponse response = new MockHttpServletResponse();
			MoveTargetsCommand command = new MoveTargetsCommand();

			BindException errors = new BindException(command, "MoveTargetsCommand");

			bindEditorContext(request, 15002L);
			
			GroupsEditorContext ctx = testInstance.getEditorContext(request);
			
			List<Long> targetsToMove = new ArrayList<Long>();
			targetsToMove.add(4000L);
			targetsToMove.add(4001L);
			
			ctx.setTargetsToMove(targetsToMove);
			
			command.setActionCmd("");
			long[]  oids = {15000L};
			command.setParentOids(oids);
			
			TargetGroup srcGrp = tm.loadGroup(15002L);
			assertEquals(0, srcGrp.getRemovedChildren().size());
			TargetGroup dstGrp = tm.loadGroup(15000L);
			assertEquals(0, dstGrp.getNewChildren().size());
			
			ModelAndView mav = testInstance.handle(request, response, command, errors);
			assertNotNull(mav);
			assertEquals(mav.getViewName(), "group-move-targets");
			
			assertEquals(0, srcGrp.getRemovedChildren().size());
			assertEquals(0, dstGrp.getNewChildren().size());
		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}
	
	@Test
	public final void testSetGroupsController() {
		try
		{
			TabbedGroupController tc = new TabbedGroupController();

			TabConfig tabConfig = new TabConfig();
			tabConfig.setViewName("groups");
			List<Tab> tabs = getTabList();
			tabConfig.setTabs(tabs);

			tc.setTabConfig(tabConfig);
			tc.setDefaultCommandClass(org.webcurator.ui.groups.command.DefaultCommand.class);
			testInstance.setGroupsController(tc);
		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}

	@Test
	public final void testSetTargetManager() {
		try
		{
			testInstance.setTargetManager(tm);
		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}

	@Test
	public final void testSetAuthorityManager() {
		try
		{
			testInstance.setAuthorityManager(new AuthorityManagerImpl());
		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}
}
