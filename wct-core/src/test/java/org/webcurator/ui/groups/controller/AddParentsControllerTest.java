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
import org.webcurator.test.BaseWCTTest;
import org.webcurator.ui.groups.GroupsEditorContext;
import org.webcurator.ui.groups.command.AddParentsCommand;
import org.webcurator.ui.groups.command.GeneralCommand;
import org.webcurator.ui.groups.command.MembersCommand;
import org.webcurator.ui.groups.validator.GeneralValidator;
import org.webcurator.ui.util.Tab;
import org.webcurator.ui.util.TabConfig;

public class AddParentsControllerTest extends BaseWCTTest<AddParentsController> {

	private TargetManager tm = null;
	
	public AddParentsControllerTest() {
		super(AddParentsController.class, "src/test/java/org/webcurator/ui/groups/controller/AddParentsControllerTest.xml");
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
	public final void testHandleAddParents() {
		try
		{
			testSetAuthorityManager();
			testSetTargetManager();
			testSetGroupsController();
			
			HttpServletRequest request = new MockHttpServletRequest();
			HttpServletResponse response = new MockHttpServletResponse();
			AddParentsCommand command = new AddParentsCommand();

			BindException errors = new BindException(command, "AddParentsCommand");

			bindEditorContext(request, 15002L);
			testInstance.getEditorContext(request).getTargetGroup().setName("ParentGroup > ChildGroup");
			
			command.setActionCmd(AddParentsCommand.ACTION_ADD_PARENTS);
			long[]  oids = {15000L};
			command.setParentOids(oids);
			
			ModelAndView mav = testInstance.handle(request, response, command, errors);
			assertNotNull(mav);
			assertEquals(mav.getViewName(), "groups");
			assertTrue(((GeneralCommand)mav.getModel().get("command")).getParentOid().equals("15000"));
			assertTrue(((GeneralCommand)mav.getModel().get("command")).getName().equals("ChildGroup"));
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
			AddParentsCommand command = new AddParentsCommand();

			BindException errors = new BindException(command, "AddParentsCommand");

			bindEditorContext(request, 15002L);
			testInstance.getEditorContext(request).getTargetGroup().setName("ParentGroup > ChildGroup");
			
			command.setActionCmd(AddParentsCommand.ACTION_CANCEL);
			long[]  oids = {15000L};
			command.setParentOids(oids);
			
			ModelAndView mav = testInstance.handle(request, response, command, errors);
			assertNotNull(mav);
			assertEquals(mav.getViewName(), "groups");
			assertTrue(((GeneralCommand)mav.getModel().get("command")).getParentOid().equals(""));
			assertTrue(((GeneralCommand)mav.getModel().get("command")).getName().equals("ChildGroup"));
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
			AddParentsCommand command = new AddParentsCommand();

			BindException errors = new BindException(command, "AddMembersCommand");

			bindEditorContext(request, 15002L);
			
			command.setActionCmd(null);
			
			ModelAndView mav = testInstance.handle(request, response, command, errors);
			assertNotNull(mav);
			assertEquals(mav.getViewName(), "group-add-parents");
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
