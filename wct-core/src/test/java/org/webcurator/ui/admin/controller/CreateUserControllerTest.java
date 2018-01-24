package org.webcurator.ui.admin.controller;

import static org.junit.Assert.*;

import org.junit.Test;
import org.springframework.mock.web.*;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.context.MockMessageSource;

import org.webcurator.test.BaseWCTTest;
import org.webcurator.ui.admin.command.*;
import org.webcurator.core.agency.*;
import org.webcurator.auth.*;
import org.webcurator.domain.model.auth.*;

import org.acegisecurity.providers.dao.salt.SystemWideSaltSource;
import org.acegisecurity.providers.encoding.PasswordEncoder;
import org.acegisecurity.providers.encoding.ShaPasswordEncoder;

import java.util.List;
import org.webcurator.core.util.AuthUtil;

public class CreateUserControllerTest extends BaseWCTTest<CreateUserController>{

	public CreateUserControllerTest()
	{
		super(CreateUserController.class,
				"src/test/java/org/webcurator/ui/admin/controller/CreateUserControllerTest.xml");
	}
	
	@Test
	public final void testCreateUserController() {
		assertTrue(testInstance != null);
	}

	@Test
	public final void testInitBinder() {
		MockHttpServletRequest request = new MockHttpServletRequest();
		ServletRequestDataBinder binder = new ServletRequestDataBinder(new CreateUserCommand(), "command");
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
			BindException aError = new BindException(new CreateUserCommand(), CreateUserCommand.ACTION_EDIT);
			AgencyUserManager manager = new MockAgencyUserManagerImpl(testFile); 
			testInstance.setAgencyUserManager(manager);
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
	public final void testProcessFormSubmission() {
		try
		{
			MockHttpServletRequest request = new MockHttpServletRequest();
			MockHttpServletResponse response = new MockHttpServletResponse();
			BindException aError = new BindException(new CreateUserCommand(), CreateUserCommand.ACTION_EDIT);
			testSetAgencyUserManager();
			testSetAuthorityManager();
			testSetMessageSource();
			testSetPasswordEncoder();
			testSetSaltSource();
			
			CreateUserCommand aCommand = new CreateUserCommand();
			aCommand.setAction(CreateUserCommand.ACTION_NEW);
			ModelAndView mav = testInstance.processFormSubmission(request, response, aCommand, aError);
			assertTrue(mav != null);
			assertTrue(mav.getViewName().equals("newUser"));

			aCommand = new CreateUserCommand();
			aCommand.setAction(CreateUserCommand.ACTION_VIEW);
			aCommand.setOid(new Long(1000));
			mav = testInstance.processFormSubmission(request, response, aCommand, aError);
			assertTrue(mav != null);
			assertTrue(mav.getViewName().equals("newUser"));
			CreateUserCommand newCommand = (CreateUserCommand)mav.getModel().get("command");
			assertTrue(newCommand != null);
			assertTrue(newCommand.getMode().equals("view"));

			aCommand = new CreateUserCommand();
			aCommand.setAction(CreateUserCommand.ACTION_EDIT);
			aCommand.setOid(new Long(1000));
			mav = testInstance.processFormSubmission(request, response, aCommand, aError);
			assertTrue(mav != null);
			assertTrue(mav.getViewName().equals("newUser"));
			newCommand = (CreateUserCommand)mav.getModel().get("command");
			assertTrue(newCommand != null);
			assertTrue(newCommand.getMode().equals("edit"));

			aCommand = new CreateUserCommand();
			aCommand.setAction(CreateUserCommand.ACTION_SAVE);
			aCommand.setOid(1000L);
			aCommand.setFirstname("Test");
			aCommand.setLastname("User");
			aCommand.setUsername("TestUserName");
			mav = testInstance.processFormSubmission(request, response, aCommand, aError);
			assertTrue(mav != null);
			assertTrue(mav.getViewName().equals("viewUsers"));
			List<Agency> agencies = (List<Agency>)mav.getModel().get("agencies");
			assertTrue(agencies != null);
			assertTrue(agencies.size() > 0);
			String message = (String)mav.getModel().get("page_message");
			assertTrue(message != null);
			assertTrue(message.startsWith("user.updated"));

			aCommand = new CreateUserCommand();
			aCommand.setAction(CreateUserCommand.ACTION_SAVE);
			aCommand.setFirstname("Test");
			aCommand.setLastname("User");
			aCommand.setUsername("TestUserName");
			aCommand.setPassword("Password");
			mav = testInstance.processFormSubmission(request, response, aCommand, aError);
			assertTrue(mav != null);
			assertTrue(mav.getViewName().equals("viewUsers"));
			agencies = (List<Agency>)mav.getModel().get("agencies");
			assertTrue(agencies != null);
			assertTrue(agencies.size() > 0);
			message = (String)mav.getModel().get("page_message");
			assertTrue(message != null);
			assertTrue(message.startsWith("user.created"));
		}
		catch (Exception e)
		{
			String message = e.getClass().toString() + " - " + e.getMessage();
			log.debug(message);
			fail(message);
		}
	}

	@Test
	public final void testSetPasswordEncoder() {
		PasswordEncoder passwordEncoder = new ShaPasswordEncoder();
		testInstance.setPasswordEncoder(passwordEncoder);
	}

	@Test
	public final void testSetSaltSource() {
		SystemWideSaltSource saltSource = new SystemWideSaltSource();
		saltSource.setSystemWideSalt("Rand0mS4lt");
		testInstance.setSaltSource(saltSource);
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
