/**
 * 
 */
package org.webcurator.ui.admin.controller;

import static org.junit.Assert.*;

import java.util.List;

import org.webcurator.test.*;

import org.junit.Test;
import org.springframework.mock.web.*;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.context.MockMessageSource;

import org.webcurator.ui.admin.command.*;
import org.webcurator.core.agency.*;
import org.webcurator.domain.model.auth.Agency;


/**
 * @author kurwin
 *
 */
public class AgencyControllerTest extends BaseWCTTest<AgencyController>{

	public AgencyControllerTest()
	{
		super(AgencyController.class,
				"src/test/java/org/webcurator/ui/admin/controller/AgencyControllerTest.xml");
	}
	
	/**
	 * Test method for {@link org.webcurator.ui.admin.controller.AgencyController#AgencyController()}.
	 */
	@Test
	public final void testAgencyController() {
		assertTrue(testInstance != null);
	}

	/**
	 * Test method for {@link org.webcurator.ui.admin.controller.AgencyController#initBinder(javax.servlet.http.HttpServletRequest, org.springframework.web.bind.ServletRequestDataBinder)}.
	 */
	@Test
	public final void testInitBinder() {
		MockHttpServletRequest request = new MockHttpServletRequest();
		ServletRequestDataBinder binder = new ServletRequestDataBinder(new AgencyCommand(), "command");
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

	/**
	 * Test method for {@link org.webcurator.ui.admin.controller.AgencyController#showForm(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, org.springframework.validation.BindException)}.
	 */
	@Test
	public final void testShowForm() {
		try
		{
			MockHttpServletRequest request = new MockHttpServletRequest();
			MockHttpServletResponse response = new MockHttpServletResponse();
			BindException aError = new BindException(new AgencyCommand(), AgencyCommand.ACTION_EDIT);
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

	/**
	 * Test method for {@link org.webcurator.ui.admin.controller.AgencyController#processFormSubmission(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.Object, org.springframework.validation.BindException)}.
	 */
	@Test
	public final void testProcessFormSubmission() {
		try
		{
			MockHttpServletRequest request = new MockHttpServletRequest();
			MockHttpServletResponse response = new MockHttpServletResponse();
			BindException aError = new BindException(new AgencyCommand(), AgencyCommand.ACTION_EDIT);
			testSetAgencyUserManager();
			testSetMessageSource();
			
			AgencyCommand aCommand = new AgencyCommand();
			aCommand.setActionCommand(AgencyCommand.ACTION_NEW);
			aCommand.setOid(new Long(2000));
			ModelAndView mav = testInstance.processFormSubmission(request, response, aCommand, aError);
			assertTrue(mav != null);
			assertTrue(mav.getViewName().equals("newAgency"));
			List<Agency> agencies = (List<Agency>)mav.getModel().get("agencies");
			assertTrue(agencies != null);
			assertTrue(agencies.size() > 0);

			aCommand = new AgencyCommand();
			aCommand.setActionCommand(AgencyCommand.ACTION_VIEW);
			aCommand.setOid(new Long(2000));
			mav = testInstance.processFormSubmission(request, response, aCommand, aError);
			assertTrue(mav != null);
			assertTrue(mav.getViewName().equals("newAgency"));
			agencies = (List<Agency>)mav.getModel().get("agencies");
			assertTrue(agencies != null);
			assertTrue(agencies.size() > 0);
			AgencyCommand newCommand = (AgencyCommand)mav.getModel().get("command");
			assertTrue(newCommand != null);
			assertTrue(newCommand.getViewOnlyMode());

			aCommand = new AgencyCommand();
			aCommand.setActionCommand(AgencyCommand.ACTION_EDIT);
			aCommand.setOid(new Long(2000));
			mav = testInstance.processFormSubmission(request, response, aCommand, aError);
			assertTrue(mav != null);
			assertTrue(mav.getViewName().equals("newAgency"));
			agencies = (List<Agency>)mav.getModel().get("agencies");
			assertTrue(agencies != null);
			assertTrue(agencies.size() > 0);
			newCommand = (AgencyCommand)mav.getModel().get("command");
			assertTrue(newCommand != null);
			assertFalse(newCommand.getViewOnlyMode());

			aCommand = new AgencyCommand();
			aCommand.setActionCommand(AgencyCommand.ACTION_SAVE);
			aCommand.setName("New Test Agency");
			mav = testInstance.processFormSubmission(request, response, aCommand, aError);
			assertTrue(mav != null);
			assertTrue(mav.getViewName().equals("viewAgencies"));
			agencies = (List<Agency>)mav.getModel().get("agencies");
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

	/**
	 * Test method for {@link org.webcurator.ui.admin.controller.AgencyController#setAgencyUserManager(org.webcurator.core.agency.AgencyUserManager)}.
	 */
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

	/**
	 * Test method for {@link org.webcurator.ui.admin.controller.AgencyController#setMessageSource(org.springframework.context.MessageSource)}.
	 */
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
