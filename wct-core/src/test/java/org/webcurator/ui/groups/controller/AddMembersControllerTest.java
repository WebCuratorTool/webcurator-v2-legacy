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
import org.webcurator.core.targets.MockTargetManager;
import org.webcurator.core.targets.TargetManager;
import org.webcurator.domain.model.auth.Privilege;
import org.webcurator.test.BaseWCTTest;
import org.webcurator.ui.groups.*;
import org.webcurator.ui.groups.command.*;
import org.webcurator.ui.groups.validator.*;
import org.webcurator.ui.util.Tab;
import org.webcurator.ui.util.TabConfig;

public class AddMembersControllerTest extends BaseWCTTest<AddMembersController>{

	private TargetManager tm = null;
	
	public AddMembersControllerTest()
	{
		super(AddMembersController.class,
				"src/test/java/org/webcurator/ui/groups/controller/addmemberscontrollertest.xml");
	}
	
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
	public final void testHandleAddMembers1() {
		try
		{
			testSetAuthorityManager();
			testSetTargetManager();
			testSetGroupsController();
			
			this.addCurrentUserPrivilege(Privilege.ADD_TARGET_TO_GROUP);

			HttpServletRequest request = new MockHttpServletRequest();
			HttpServletResponse response = new MockHttpServletResponse();
			AddMembersCommand command = new AddMembersCommand();

			BindException errors = new BindException(command, "AddMembersCommand");

			bindEditorContext(request, 15000L);
			
			command.setActionCmd(AddMembersCommand.ACTION_ADD_MEMBERS);
			//Already a member of this group
			long[]  oids = {4000L};
			command.setMemberOids(oids);
			
			assertTrue(testInstance.getEditorContext(request).getTargetGroup().getNewChildren().size() == 0);
			ModelAndView mav = testInstance.handle(request, response, command, errors);
			assertNotNull(mav);
			assertTrue(testInstance.getEditorContext(request).getTargetGroup().getNewChildren().size() == 0);
			assertEquals(mav.getViewName(), "group-add-members");
			assertTrue(errors.getErrorCount() == 1);
			assertTrue(errors.getMessage().indexOf("Already a member of this group") > 0);
		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}

	@Test
	public final void testHandleAddMembers2() {
		try
		{
			testSetAuthorityManager();
			testSetTargetManager();
			testSetGroupsController();

			this.addCurrentUserPrivilege(Privilege.ADD_TARGET_TO_GROUP);
			
			HttpServletRequest request = new MockHttpServletRequest();
			HttpServletResponse response = new MockHttpServletResponse();
			AddMembersCommand command = new AddMembersCommand();

			BindException errors = new BindException(command, "AddMembersCommand");

			bindEditorContext(request, 15001L);
			
			command.setActionCmd(AddMembersCommand.ACTION_ADD_MEMBERS);
			long[]  oids = {4000L};
			command.setMemberOids(oids);
			
			assertTrue(testInstance.getEditorContext(request).getTargetGroup().getNewChildren().size() == 0);
			ModelAndView mav = testInstance.handle(request, response, command, errors);
			assertNotNull(mav);
			assertEquals(mav.getViewName(), "groups");
			assertTrue(errors.getErrorCount() == 0);
			assertTrue(testInstance.getEditorContext(request).getTargetGroup().getNewChildren().size() == 1);
		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}

	@Test
	public final void testHandleAddMembers2NoPriv() {
		try
		{
			testSetAuthorityManager();
			testSetTargetManager();
			testSetGroupsController();

			this.removeAllCurrentUserPrivileges();
			
			HttpServletRequest request = new MockHttpServletRequest();
			HttpServletResponse response = new MockHttpServletResponse();
			AddMembersCommand command = new AddMembersCommand();

			BindException errors = new BindException(command, "AddMembersCommand");

			bindEditorContext(request, 15001L);
			
			command.setActionCmd(AddMembersCommand.ACTION_ADD_MEMBERS);
			long[]  oids = {4000L};
			command.setMemberOids(oids);
			
			assertTrue(testInstance.getEditorContext(request).getTargetGroup().getNewChildren().size() == 0);
			ModelAndView mav = testInstance.handle(request, response, command, errors);
			assertNotNull(mav);
			assertEquals(mav.getViewName(), "groups");
			assertTrue(errors.getErrorCount() == 0);
			assertTrue(testInstance.getEditorContext(request).getTargetGroup().getNewChildren().size() == 0);
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

			HttpServletRequest request = new MockHttpServletRequest();
			HttpServletResponse response = new MockHttpServletResponse();
			AddMembersCommand command = new AddMembersCommand();

			BindException errors = new BindException(command, "AddMembersCommand");

			bindEditorContext(request, 15001L);
			
			command.setActionCmd(null);
			long[]  oids = {4000L, 15002L};
			command.setMemberOids(oids);
			
			List<AddMembersController.MemberSelection> selections = (List<AddMembersController.MemberSelection>)request.getSession().getAttribute(AddMembersCommand.SESSION_SELECTIONS);
			assertNull(selections);
			ModelAndView mav = testInstance.handle(request, response, command, errors);
			assertNotNull(mav);
			assertEquals(mav.getViewName(), "group-add-members");
			selections = (List<AddMembersController.MemberSelection>)request.getSession().getAttribute(AddMembersCommand.SESSION_SELECTIONS);
			assertNotNull(selections);
			assertTrue(selections.size() == 2);
			assertEquals(selections.get(0).getOid(), new Long(4000));
			assertEquals(selections.get(1).getOid(), new Long(15002));

			command = new AddMembersCommand();
			errors = new BindException(command, "AddMembersCommand");

			bindEditorContext(request, 15001L);
			
			command.setActionCmd(AddMembersCommand.ACTION_CANCEL);
			
			mav = testInstance.handle(request, response, command, errors);
			assertNotNull(mav);
			assertEquals(mav.getViewName(), "groups");
			selections = (List<AddMembersController.MemberSelection>)request.getSession().getAttribute(AddMembersCommand.SESSION_SELECTIONS);
			assertNull(selections);
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

			HttpServletRequest request = new MockHttpServletRequest();
			HttpServletResponse response = new MockHttpServletResponse();
			AddMembersCommand command = new AddMembersCommand();

			BindException errors = new BindException(command, "AddMembersCommand");

			bindEditorContext(request, 15001L);
			
			command.setActionCmd(null);
			long[]  oids = {4000L, 15002L};
			command.setMemberOids(oids);
			
			List<AddMembersController.MemberSelection> selections = (List<AddMembersController.MemberSelection>)request.getSession().getAttribute(AddMembersCommand.SESSION_SELECTIONS);
			assertNull(selections);
			ModelAndView mav = testInstance.handle(request, response, command, errors);
			assertNotNull(mav);
			assertEquals(mav.getViewName(), "group-add-members");
			selections = (List<AddMembersController.MemberSelection>)request.getSession().getAttribute(AddMembersCommand.SESSION_SELECTIONS);
			assertNotNull(selections);
			assertTrue(selections.size() == 2);
			assertEquals(selections.get(0).getOid(), new Long(4000));
			assertEquals(selections.get(1).getOid(), new Long(15002));
		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}

	@Test
	public final void testHandleRemove() {
		try
		{
			testSetAuthorityManager();
			testSetTargetManager();
			testSetGroupsController();

			HttpServletRequest request = new MockHttpServletRequest();
			HttpServletResponse response = new MockHttpServletResponse();
			AddMembersCommand command = new AddMembersCommand();

			BindException errors = new BindException(command, "AddMembersCommand");

			bindEditorContext(request, 15001L);
			
			command.setActionCmd(null);
			long[]  oids = {4000L, 15002L};
			command.setMemberOids(oids);
			
			List<AddMembersController.MemberSelection> selections = (List<AddMembersController.MemberSelection>)request.getSession().getAttribute(AddMembersCommand.SESSION_SELECTIONS);
			assertNull(selections);
			ModelAndView mav = testInstance.handle(request, response, command, errors);
			assertNotNull(mav);
			assertEquals(mav.getViewName(), "group-add-members");
			selections = (List<AddMembersController.MemberSelection>)request.getSession().getAttribute(AddMembersCommand.SESSION_SELECTIONS);
			assertNotNull(selections);
			assertTrue(selections.size() == 2);
			assertEquals(selections.get(0).getOid(), new Long(4000));
			assertEquals(selections.get(1).getOid(), new Long(15002));
			
			command = new AddMembersCommand();
			errors = new BindException(command, "AddMembersCommand");

			bindEditorContext(request, 15001L);
			
			command.setActionCmd(AddMembersCommand.ACTION_REMOVE);
			command.setMemberIndex(1);
			
			mav = testInstance.handle(request, response, command, errors);
			assertNotNull(mav);
			assertEquals(mav.getViewName(), "group-add-members");
			selections = (List<AddMembersController.MemberSelection>)request.getSession().getAttribute(AddMembersCommand.SESSION_SELECTIONS);
			assertNotNull(selections);
			assertTrue(selections.size() == 1);
			assertEquals(selections.get(0).getOid(), new Long(4000));
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
