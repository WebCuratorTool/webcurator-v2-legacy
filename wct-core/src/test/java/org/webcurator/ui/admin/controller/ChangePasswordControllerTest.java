package org.webcurator.ui.admin.controller;

import static org.junit.Assert.*;

import java.util.List;

import org.acegisecurity.providers.dao.salt.SystemWideSaltSource;
import org.acegisecurity.providers.encoding.PasswordEncoder;
import org.acegisecurity.providers.encoding.ShaPasswordEncoder;
import org.junit.Test;
import org.springframework.context.MockMessageSource;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.validation.BindException;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.webcurator.auth.AuthorityManager;
import org.webcurator.auth.AuthorityManagerImpl;
import org.webcurator.core.agency.AgencyUserManager;
import org.webcurator.core.agency.MockAgencyUserManagerImpl;
import org.webcurator.domain.model.auth.Agency;
import org.webcurator.test.BaseWCTTest;
import org.webcurator.ui.admin.command.*;

public class ChangePasswordControllerTest extends BaseWCTTest<ChangePasswordController>{

	public ChangePasswordControllerTest()
	{
		super(ChangePasswordController.class,
				"src/test/java/org/webcurator/ui/admin/controller/CreateUserControllerTest.xml");
	}
	
	@Test
	public final void testChangePasswordController() {
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
		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();
		BindException aError = new BindException(new CreateUserCommand(), CreateUserCommand.ACTION_EDIT);
		testSetAgencyUserManager();
		testSetAuthorityManager();
		testSetMessageSource();
		testSetEncoder();
		testSetSalt();
		
		try
		{
			ChangePasswordCommand aCommand = new ChangePasswordCommand();
			aCommand.setAction(ChangePasswordCommand.ACTION_SAVE);
			aCommand.setUserOid(1000L);
			aCommand.setNewPwd("Pa55word");
			aCommand.setConfirmPwd("Pa55word");
			ModelAndView mav = testInstance.processFormSubmission(request, response, aCommand, aError);
			assertTrue(mav != null);
			assertTrue(mav.getViewName().equals("viewUsers"));
			List<Agency> agencies = (List<Agency>)mav.getModel().get("agencies");
			assertTrue(agencies != null);
			assertTrue(agencies.size() > 0);
		}
		catch (Exception e)
		{
			String message = e.getClass().toString() + " - " + e.getMessage();
			log.debug(message);
			fail(message);
		}

	}

	@Test
	public final void testSetEncoder() {
		PasswordEncoder passwordEncoder = new ShaPasswordEncoder();
		testInstance.setEncoder(passwordEncoder);
	}

	@Test
	public final void testSetSalt() {
		SystemWideSaltSource saltSource = new SystemWideSaltSource();
		saltSource.setSystemWideSalt("Rand0mS4lt");
		testInstance.setSalt(saltSource);
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
