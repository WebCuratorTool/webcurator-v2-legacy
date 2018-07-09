package org.webcurator.ui.admin.controller;

import static org.junit.Assert.*;

import org.junit.Test;
import org.springframework.context.MockMessageSource;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.webcurator.auth.AuthorityManager;
import org.webcurator.auth.AuthorityManagerImpl;
import org.webcurator.core.agency.AgencyUserManager;
import org.webcurator.core.agency.MockAgencyUserManagerImpl;
import org.webcurator.core.util.AuthUtil;
import org.webcurator.test.BaseWCTTest;
import org.webcurator.ui.admin.command.AssociateUserRoleCommand;
import org.webcurator.ui.admin.command.UserCommand;
import org.webcurator.domain.model.auth.User;

public class AssociateUserRoleControllerTest extends BaseWCTTest<AssociateUserRoleController>{

	public AssociateUserRoleControllerTest()
	{
		super(AssociateUserRoleController.class,
				"src/test/java/org/webcurator/ui/admin/controller/CreateUserControllerTest.xml");
	}
	
	@Test
	public final void testAssociateUserRoleController() {
		assertTrue(testInstance != null);
	}

	@Test
	public final void testProcessFormSubmissionACTION_ASSOCIATE_VIEW() {
		try
		{
			MockHttpServletRequest request = new MockHttpServletRequest();
			MockHttpServletResponse response = new MockHttpServletResponse();
			AssociateUserRoleCommand command = new AssociateUserRoleCommand();
			command.setChoosenUserOid(1001L);
			command.setActionCmd(AssociateUserRoleCommand.ACTION_ASSOCIATE_VIEW);
			BindException aError = new BindException(new AssociateUserRoleCommand(), AssociateUserRoleCommand.ACTION_ASSOCIATE_VIEW);
			AgencyUserManager manager = new MockAgencyUserManagerImpl(testFile); 
			testInstance.setAgencyUserManager(manager);
			this.testSetAuthorityManager();
			this.testSetMessageSource();
			ModelAndView mav = testInstance.processFormSubmission(request, response, command, aError);
			assertTrue(mav != null);
			assertTrue(mav.getViewName().equals("viewUserRoleAssociations"));
			String chosenUser = (String)mav.getModel().get(AssociateUserRoleCommand.MDL_USER);
			assertTrue(chosenUser != null);
			assertTrue(chosenUser.equals(manager.getUserByOid(1001L).getUsername()));
		}
		catch (Exception e)
		{
			String message = e.getClass().toString() + " - " + e.getMessage();
			log.debug(message);
			fail(message);
		}
	}

	@Test
	public final void testProcessFormSubmissionACTION_ASSOCIATE_SAVE() {
		try
		{
			MockHttpServletRequest request = new MockHttpServletRequest();
			MockHttpServletResponse response = new MockHttpServletResponse();
			AssociateUserRoleCommand command = new AssociateUserRoleCommand();
			this.testSetAuthorityManager();
			this.testSetMessageSource();
			AgencyUserManager manager = new MockAgencyUserManagerImpl(testFile); 
			testInstance.setAgencyUserManager(manager);
			User user = manager.getUserByOid(1001L);
			assertFalse(user.getRoles().contains(manager.getRoleByOid(3001L)));
			command.setChoosenUserOid(1001L);
			command.setSelectedRoles("3001");
			command.setActionCmd(AssociateUserRoleCommand.ACTION_ASSOCIATE_SAVE);
			BindException aError = new BindException(new AssociateUserRoleCommand(), AssociateUserRoleCommand.ACTION_ASSOCIATE_VIEW);
			ModelAndView mav = testInstance.processFormSubmission(request, response, command, aError);
			assertTrue(mav != null);
			assertTrue(mav.getViewName().equals("viewUsers"));
			assertTrue(mav.getModel().get(UserCommand.MDL_AGENCIES)!= null);
			assertTrue(mav.getModel().get(UserCommand.MDL_USERS)!=null);
			assertTrue(mav.getModel().get(UserCommand.MDL_LOGGED_IN_USER)!= null);
			String agencyFilter = (String)mav.getModel().get(UserCommand.MDL_AGENCYFILTER);
			assertTrue(agencyFilter != null);
			assertTrue(agencyFilter.equals(AuthUtil.getRemoteUserObject().getAgency().getName()));
			assertTrue(user.getRoles().contains(manager.getRoleByOid(3001L)));
			
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
			BindException aError = new BindException(new AssociateUserRoleCommand(), AssociateUserRoleCommand.ACTION_ASSOCIATE_VIEW);
			ModelAndView mav = testInstance.showForm(request, response, aError);
			assertTrue(mav == null);
		}
		catch (Exception e)
		{
			String message = e.getClass().toString() + " - " + e.getMessage();
			log.debug(message);
			fail(message);
		}
	}

	@Test
	public final void testSetAgencyUserManager() {
		try
		{
			AgencyUserManager manager = new MockAgencyUserManagerImpl(testFile); 
			testInstance.setAgencyUserManager(manager);
		}
		catch(Exception e)
		{
			String message = e.getClass().toString() + " - " + e.getMessage();
			log.debug(message);
			fail(message);
		}
	}

	@Test
	public final void testSetAuthorityManager() {
		try
		{
			AuthorityManager manager = new AuthorityManagerImpl(); 
			testInstance.setAuthorityManager(manager);
		}
		catch(Exception e)
		{
			String message = e.getClass().toString() + " - " + e.getMessage();
			log.debug(message);
			fail(message);
		}
	}

	@Test
	public final void testSetMessageSource() {
		try
		{
			testInstance.setMessageSource(new MockMessageSource());
		}
		catch(Exception e)
		{
			String message = e.getClass().toString() + " - " + e.getMessage();
			log.debug(message);
			fail(message);
		}
	}

}
