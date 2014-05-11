package org.webcurator.ui.target.controller;

import static org.junit.Assert.*;

import java.util.*;

import javax.servlet.http.*;
import org.springframework.web.servlet.*;
import org.springframework.mock.web.*;
import org.springframework.validation.BindException;

import org.junit.Test;
import org.webcurator.test.*;
import org.webcurator.ui.target.TargetEditorContext;
import org.webcurator.ui.target.command.*;
import org.webcurator.ui.target.validator.*;
import org.webcurator.ui.util.*;
import org.webcurator.auth.AuthorityManagerImpl;
import org.webcurator.core.targets.*;
import org.webcurator.domain.model.core.*;
import org.webcurator.domain.model.dto.GroupMemberDTO;


public class AddParentsControllerTest extends BaseWCTTest<AddParentsController>{

	private TargetManager tm = null;
	
	public AddParentsControllerTest()
	{
		super(AddParentsController.class,
				"src/test/java/org/webcurator/ui/target/controller/addparentscontrollertest.xml");
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
		tabGeneral.setCommandClass(TargetGeneralCommand.class);
		tabGeneral.setJsp("../target-general.jsp");
		tabGeneral.setPageId("GENERAL");
		tabGeneral.setTitle("general");
		tabGeneral.setValidator(new TargetGeneralValidator());

		TargetGeneralHandler genHandler = new TargetGeneralHandler();
		genHandler.setAuthorityManager(new AuthorityManagerImpl());
		tabGeneral.setTabHandler(genHandler);
		
		tabs.add(tabGeneral);
		
		Tab tabGroups = new Tab();
		tabGroups.setCommandClass(TargetGroupsCommand.class);
		tabGroups.setJsp("../target-groups.jsp");
		tabGroups.setPageId("GROUPS");

		TargetGroupsHandler groupsHandler = new TargetGroupsHandler();
		groupsHandler.setTargetManager(tm);
		tabGroups.setTabHandler(groupsHandler);
		
		tabs.add(tabGroups);
		return tabs;
	}
	
	private TargetEditorContext bindEditorContext(HttpServletRequest request)
	{
		TargetEditorContext targetEditorContext = new TargetEditorContext(tm,tm.load(4000L),true);
		targetEditorContext.setParents(tm.getParents(targetEditorContext.getTarget()));
		request.getSession().setAttribute(TabbedTargetController.EDITOR_CONTEXT, targetEditorContext);
		
		return targetEditorContext;
	}
	
	@Test
	public final void testGetEditorContext() {
		try
		{
			HttpServletRequest aReq = new MockHttpServletRequest();
			TargetEditorContext targetEditorContext = bindEditorContext(aReq);
			
			TargetEditorContext tec = testInstance.getEditorContext(aReq);
			assertNotNull(tec);
			assertEquals(targetEditorContext, tec);
		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}

	@Test
	public final void testHandleAddParents1() {
		try
		{
			testSetAuthorityManager();
			testSetTargetManager();
			testSetTargetController();

			HttpServletRequest request = new MockHttpServletRequest();
			HttpServletResponse response = new MockHttpServletResponse();
			AddParentsCommand command = new AddParentsCommand();

			BindException errors = new BindException(command, "AddParentsCommand");

			bindEditorContext(request);
			
			command.setActionCmd(AddParentsCommand.ACTION_ADD_PARENTS);
			//Already a member of this group
			long[]  oids = {15000L};
			command.setParentOids(oids);
			
			assertTrue(testInstance.getEditorContext(request).getParents().size() == 1);
			assertTrue(testInstance.getEditorContext(request).getTarget().getParents().size() == 1);
			ModelAndView mav = testInstance.handle(request, response, command, errors);
			assertNotNull(mav);
			assertTrue(testInstance.getEditorContext(request).getParents().size() == 1);
			assertTrue(testInstance.getEditorContext(request).getTarget().getParents().size() == 1);
			assertEquals(mav.getViewName(), "target-add-parents");
			assertTrue(errors.getErrorCount() == 1);
			assertTrue(errors.getMessage().indexOf("This target is already in this group") > 0);
		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}

	@Test
	public final void testHandleAddParents2() {
		try
		{
			testSetAuthorityManager();
			testSetTargetManager();
			testSetTargetController();

			HttpServletRequest request = new MockHttpServletRequest();
			HttpServletResponse response = new MockHttpServletResponse();
			AddParentsCommand command = new AddParentsCommand();

			BindException errors = new BindException(command, "AddParentsCommand");

			bindEditorContext(request);
			
			command.setActionCmd(AddParentsCommand.ACTION_ADD_PARENTS);
			long[]  oids = {15001L};
			command.setParentOids(oids);
			
			assertTrue(testInstance.getEditorContext(request).getParents().size() == 1);
			assertTrue(testInstance.getEditorContext(request).getTarget().getParents().size() == 1);
			ModelAndView mav = testInstance.handle(request, response, command, errors);
			assertNotNull(mav);
			assertTrue(testInstance.getEditorContext(request).getParents().size() == 2);
			assertTrue(testInstance.getEditorContext(request).getTarget().getParents().size() == 1);
			assertEquals(mav.getViewName(), "target");
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
			testSetTargetController();

			HttpServletRequest request = new MockHttpServletRequest();
			HttpServletResponse response = new MockHttpServletResponse();
			AddParentsCommand command = new AddParentsCommand();

			BindException errors = new BindException(command, "AddParentsCommand");

			bindEditorContext(request);
			
			command.setActionCmd(null);
			long[]  oids = {15001L, 15002L};
			command.setParentOids(oids);
			
			assertTrue(testInstance.getEditorContext(request).getParents().size() == 1);
			assertTrue(testInstance.getEditorContext(request).getTarget().getParents().size() == 1);
			List<GroupMemberDTO> selections = (List<GroupMemberDTO>)request.getSession().getAttribute(AddParentsCommand.SESSION_SELECTIONS);
			assertNull(selections);
			ModelAndView mav = testInstance.handle(request, response, command, errors);
			assertNotNull(mav);
			assertTrue(testInstance.getEditorContext(request).getParents().size() == 1);
			assertTrue(testInstance.getEditorContext(request).getTarget().getParents().size() == 1);
			assertEquals(mav.getViewName(), "target-add-parents");
			selections = (List<GroupMemberDTO>)request.getSession().getAttribute(AddParentsCommand.SESSION_SELECTIONS);
			assertNotNull(selections);
			assertTrue(selections.size() == 2);

			command = new AddParentsCommand();
			errors = new BindException(command, "AddParentsCommand");

			bindEditorContext(request);
			
			command.setActionCmd(AddParentsCommand.ACTION_CANCEL);
			
			mav = testInstance.handle(request, response, command, errors);
			assertNotNull(mav);
			assertEquals(mav.getViewName(), "target");
			selections = (List<GroupMemberDTO>)request.getSession().getAttribute(AddParentsCommand.SESSION_SELECTIONS);
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
			testSetTargetController();

			HttpServletRequest request = new MockHttpServletRequest();
			HttpServletResponse response = new MockHttpServletResponse();
			AddParentsCommand command = new AddParentsCommand();

			BindException errors = new BindException(command, "AddParentsCommand");

			bindEditorContext(request);
			
			command.setActionCmd(null);
			long[]  oids = {15001L, 15002L};
			command.setParentOids(oids);
			
			assertTrue(testInstance.getEditorContext(request).getParents().size() == 1);
			assertTrue(testInstance.getEditorContext(request).getTarget().getParents().size() == 1);
			List<GroupMemberDTO> selections = (List<GroupMemberDTO>)request.getSession().getAttribute(AddParentsCommand.SESSION_SELECTIONS);
			assertNull(selections);
			ModelAndView mav = testInstance.handle(request, response, command, errors);
			assertNotNull(mav);
			assertTrue(testInstance.getEditorContext(request).getParents().size() == 1);
			assertTrue(testInstance.getEditorContext(request).getTarget().getParents().size() == 1);
			assertEquals(mav.getViewName(), "target-add-parents");
			selections = (List<GroupMemberDTO>)request.getSession().getAttribute(AddParentsCommand.SESSION_SELECTIONS);
			assertNotNull(selections);
			assertTrue(selections.size() == 2);
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
			testSetTargetController();

			HttpServletRequest request = new MockHttpServletRequest();
			HttpServletResponse response = new MockHttpServletResponse();
			AddParentsCommand command = new AddParentsCommand();

			BindException errors = new BindException(command, "AddParentsCommand");

			bindEditorContext(request);
			
			command.setActionCmd(null);
			long[]  oids = {15001L, 15002L};
			command.setParentOids(oids);
			
			assertTrue(testInstance.getEditorContext(request).getParents().size() == 1);
			assertTrue(testInstance.getEditorContext(request).getTarget().getParents().size() == 1);
			List<GroupMemberDTO> selections = (List<GroupMemberDTO>)request.getSession().getAttribute(AddParentsCommand.SESSION_SELECTIONS);
			assertNull(selections);
			ModelAndView mav = testInstance.handle(request, response, command, errors);
			assertNotNull(mav);
			assertTrue(testInstance.getEditorContext(request).getParents().size() == 1);
			assertTrue(testInstance.getEditorContext(request).getTarget().getParents().size() == 1);
			assertEquals(mav.getViewName(), "target-add-parents");
			selections = (List<GroupMemberDTO>)request.getSession().getAttribute(AddParentsCommand.SESSION_SELECTIONS);
			assertNotNull(selections);
			assertTrue(selections.size() == 2);
			
			command = new AddParentsCommand();
			errors = new BindException(command, "AddParentsCommand");

			bindEditorContext(request);
			
			command.setActionCmd(AddParentsCommand.ACTION_REMOVE);
			command.setParentIndex(1);
			
			mav = testInstance.handle(request, response, command, errors);
			assertNotNull(mav);
			assertTrue(testInstance.getEditorContext(request).getParents().size() == 1);
			assertTrue(testInstance.getEditorContext(request).getTarget().getParents().size() == 1);
			assertEquals(mav.getViewName(), "target-add-parents");
			selections = (List<GroupMemberDTO>)request.getSession().getAttribute(AddParentsCommand.SESSION_SELECTIONS);
			assertNotNull(selections);
			assertTrue(selections.size() == 1);
			assertEquals(selections.get(0).getParentOid(), new Long(15001));
		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}

	@Test
	public final void testSetTargetController() {
		try
		{
			TabbedTargetController tc = new TabbedTargetController();

			TabConfig tabConfig = new TabConfig();
			tabConfig.setViewName("target");
			List<Tab> tabs = getTabList();
			tabConfig.setTabs(tabs);

			tc.setTabConfig(tabConfig);
			tc.setDefaultCommandClass(org.webcurator.ui.target.command.TargetDefaultCommand.class);
			testInstance.setTargetController(tc);
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
