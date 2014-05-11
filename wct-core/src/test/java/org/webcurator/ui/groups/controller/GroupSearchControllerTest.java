package org.webcurator.ui.groups.controller;

import static org.junit.Assert.*;

import java.util.*;

import org.junit.Test;
import org.springframework.mock.web.*;
import org.springframework.validation.BindException;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.webcurator.test.BaseWCTTest;
import org.webcurator.ui.groups.command.*;
import org.webcurator.core.agency.*;
import org.webcurator.core.common.WCTTreeSet;
import org.webcurator.core.targets.*;
import org.webcurator.core.util.AuthUtil;
import org.webcurator.domain.model.auth.Agency;
import org.webcurator.domain.model.auth.Privilege;
import org.webcurator.domain.model.auth.User;
import org.webcurator.domain.model.dto.*;
import org.springframework.context.MockMessageSource;
import org.webcurator.domain.Pagination;


public class GroupSearchControllerTest extends BaseWCTTest<GroupSearchController>{

	public GroupSearchControllerTest()
	{
		super(GroupSearchController.class,
				"src/test/java/org/webcurator/ui/groups/controller/groupsearchcontrollertest.xml");
	}
	
	@Test
	public final void testGroupSearchController() {
		assertTrue(testInstance != null);
	}

	@Test
	public final void testInitBinder() {
		MockHttpServletRequest request = new MockHttpServletRequest();
		ServletRequestDataBinder binder = new ServletRequestDataBinder(new SearchCommand(), "command");
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
	public final void testShowForm() {
		try
		{
			MockHttpServletRequest request = new MockHttpServletRequest();
			MockHttpServletResponse response = new MockHttpServletResponse();
			this.testSetAgencyUserManager();
			this.testSetTargetManager();
			this.testSetMessageSource();
			this.testSetGroupTypesList();
			
			BindException aError = new BindException(new SearchCommand(), null);
			testInstance.showForm(request, response, aError);
		}
		catch (Exception e)
		{
			String message = e.getClass().toString() + " - " + e.getMessage();
			log.debug(message);
			fail(message);
		}
	}

	@Test
	public final void testGetDefaultCommand() {
		User currentUser = AuthUtil.getRemoteUserObject();
		
		SearchCommand command = testInstance.getDefaultCommand();
		assertTrue(command.getAgency().equals(currentUser.getAgency().getName()));
		assertTrue(command.getPageNumber() == 0);
		assertTrue(command.getGroupType() == null);
		assertTrue(command.getOwner().equals(currentUser.getUsername()));
		assertTrue(command.getSearchOid() == null);
		
		testInstance.setDefaultSearchOnAgencyOnly(true);

		command = testInstance.getDefaultCommand();
		assertTrue(command.getAgency().equals(currentUser.getAgency().getName()));
		assertTrue(command.getPageNumber() == 0);
		assertTrue(command.getGroupType() == null);
		assertTrue(command.getOwner() == null);
		assertTrue(command.getSearchOid() == null);
		
		testInstance.setDefaultSearchOnAgencyOnly(false);

		command = testInstance.getDefaultCommand();
		assertTrue(command.getAgency().equals(currentUser.getAgency().getName()));
		assertTrue(command.getPageNumber() == 0);
		assertTrue(command.getGroupType() == null);
		assertTrue(command.getOwner().equals(currentUser.getUsername()));
		assertTrue(command.getSearchOid() == null);
	}

	@Test
	public final void testPrepareSearchView() {
		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();
		this.testSetAgencyUserManager();
		this.testSetTargetManager();
		this.testSetMessageSource();
		this.testSetGroupTypesList();
		
		SearchCommand command = testInstance.getDefaultCommand();
		command.setSelectedPageSize("10");
		
		request.getSession().setAttribute("groupsSearchCommand", command);
		BindException errors = new BindException(command, command.getActionCmd());
		
		ModelAndView mav = testInstance.prepareSearchView(request, response, (SearchCommand)command, errors);
		assertTrue(mav != null);
		assertTrue(mav.getViewName().equals("groups-search"));
		SearchCommand mavCommand = (SearchCommand)mav.getModel().get("command"); 
		List<Agency> mavAgencies = (List<Agency>)mav.getModel().get("agencies"); 
		List<UserDTO> mavOwners = (List<UserDTO>)mav.getModel().get("owners"); 
		WCTTreeSet mavGroupTypesList = (WCTTreeSet)mav.getModel().get("groupTypesList");
		Pagination mavResults = (Pagination)mav.getModel().get("page"); 
		assertTrue(mavCommand != null);
		assertTrue(mavAgencies != null);
		assertTrue(mavAgencies.size() > 0);
		assertTrue(mavOwners != null);
		assertTrue(mavOwners.size() > 0);
		assertTrue(mavGroupTypesList != null);
		assertTrue(mavResults != null);
		assertTrue(mavResults.getList() != null);
		assertTrue(mavResults.getList().size() > 0);
	}

	@Test
	public final void testProcessFormSubmission() {
		try
		{
			MockHttpServletRequest request = new MockHttpServletRequest();
			MockHttpServletResponse response = new MockHttpServletResponse();
			this.testSetAgencyUserManager();
			this.testSetMessageSource();
			this.testSetGroupTypesList();

			TargetManager tm = new MockTargetManager(testFile);
			testInstance.setTargetManager(tm);

			try
			{
				SearchCommand aCommand = testInstance.getDefaultCommand();
				aCommand.setActionCmd(SearchCommand.ACTION_DELETE);
				aCommand.setDeletedGroupOid(15000L);
				BindException aError = new BindException(aCommand, aCommand.getActionCmd());
				ModelAndView mav = testInstance.processFormSubmission(request, response, aCommand, aError);
				fail("Insufficient Privileges to delete group");
			}
			catch(org.webcurator.core.exceptions.WCTRuntimeException wctre)
			{
				assertTrue(wctre.getMessage().equals("You do not have the appropriate privileges to delete this group"));
			}

			this.addCurrentUserPrivilege(Privilege.MANAGE_GROUP);
			try
			{
				assertTrue(tm.loadGroup(15000L) != null);
				SearchCommand aCommand = testInstance.getDefaultCommand();
				aCommand.setSelectedPageSize("10");
				aCommand.setActionCmd(SearchCommand.ACTION_DELETE);
				aCommand.setDeletedGroupOid(15000L);
				BindException aError = new BindException(aCommand, aCommand.getActionCmd());
				ModelAndView mav = testInstance.processFormSubmission(request, response, aCommand, aError);
				assertTrue(mav != null);
				assertFalse(tm.loadGroup(15000L) != null);
			}
			catch(org.webcurator.core.exceptions.WCTRuntimeException wctre)
			{
				fail(wctre.getMessage());
			}

			SearchCommand aCommand = testInstance.getDefaultCommand();
			aCommand.setSelectedPageSize("10");
			aCommand.setActionCmd(SearchCommand.ACTION_RESET);
			BindException aError = new BindException(aCommand, aCommand.getActionCmd());
			ModelAndView mav = testInstance.processFormSubmission(request, response, aCommand, aError);
			assertTrue(mav != null);
			SearchCommand command = (SearchCommand)mav.getModel().get("command"); 
			assertTrue(command.getActionCmd().equals("reset"));
			assertTrue(command.getAgency().equals(""));
			assertTrue(command.getName().equals(""));
			assertTrue(command.getOwner().equals(""));
			assertTrue(command.getGroupType().equals(""));
			assertTrue(command.getSearchOid() == null);
		}
		catch (Exception e)
		{
			String message = e.getClass().toString() + " - " + e.getMessage();
			log.debug(message);
			fail(message);
		}
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
	public final void testSetMessageSource() {
		testInstance.setMessageSource(new MockMessageSource());
	}

	@Test
	public final void testSetGroupTypesList() {
		List<String> aEntrys = new ArrayList<String>();
		testInstance.setGroupTypesList(new WCTTreeSet(aEntrys,0));
	}

	@Test
	public final void testSetGetDefaultSearchOnAgencyOnly() {
		testInstance.setDefaultSearchOnAgencyOnly(true);
		assertTrue(testInstance.getDefaultSearchOnAgencyOnly());
		
		testInstance.setDefaultSearchOnAgencyOnly(false);
		assertFalse(testInstance.getDefaultSearchOnAgencyOnly());
	}

}
