package org.webcurator.ui.profiles.controller;

import static org.junit.Assert.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import org.springframework.mock.web.*;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.webcurator.test.BaseWCTTest;
import org.webcurator.auth.AuthorityManagerImpl;
import org.webcurator.core.agency.*;
import org.webcurator.core.profiles.*;
import org.webcurator.domain.model.auth.Privilege;
import org.webcurator.ui.common.Constants;
import org.webcurator.ui.profiles.command.ProfileListCommand;
import org.webcurator.ui.profiles.command.ViewCommand;

public class MakeDefaultProfileControllerTest extends BaseWCTTest<MakeDefaultProfileController> {

	public MakeDefaultProfileControllerTest()
	{
		super(MakeDefaultProfileController.class,
				"src/test/java/org/webcurator/ui/profiles/controller/profilelistcontrollertest.xml");
	}
	
	private ModelAndView performTestHandle(String defaultAgency, Long profileOid)
	{
		ModelAndView mav = null;
		
		try
		{
			HttpServletRequest req = new MockHttpServletRequest();
			HttpServletResponse res = new MockHttpServletResponse();
			req.getSession().setAttribute(ProfileListController.SESSION_KEY_SHOW_INACTIVE, false);
			req.getSession().setAttribute(ProfileListController.SESSION_AGENCY_FILTER, defaultAgency);

			ViewCommand comm = new ViewCommand();
			comm.setProfileOid(profileOid);
			
			BindException errors = new BindException(comm, null);
	
			mav = testInstance.handle(req, res, comm, errors);
			assertTrue(mav != null);
		}
		catch(Exception e)
		{
			fail(e.getClass().getName()+": "+e.getMessage());
		}
		
		return mav;
	}
		
	@Test
	public final void testHandle() {
		ModelAndView mav = null;
		ProfileListCommand command = null;
		ProfileManager profileManager = new MockProfileManager(testFile);
		testInstance.setProfileManager(profileManager);
		testInstance.setAuthorityManager(new AuthorityManagerImpl());
		testInstance.setAgencyUserManager(new MockAgencyUserManagerImpl(testFile));
		
		try
		{
			this.removeAllCurrentUserPrivileges();
			mav = performTestHandle("",13001L);
			assertTrue(mav.getViewName().equals("authorisation-failure"));
			assertTrue(profileManager.load(13000L).isDefaultProfile());
			
			this.addCurrentUserPrivilege(Privilege.SCOPE_ALL, Privilege.MANAGE_PROFILES);
			mav = performTestHandle("",13001L);
			command = (ProfileListCommand)mav.getModel().get("command");
			assertTrue(command != null);
			assertTrue(command.getDefaultAgency().equals(""));
			assertTrue(mav.getViewName().equals("profile-list"));
			assertTrue(mav.getModel().get(Constants.GBL_ERRORS)!= null);
			assertTrue(profileManager.load(13000L).isDefaultProfile());
			
			mav = performTestHandle("",13004L);
			command = (ProfileListCommand)mav.getModel().get("command");
			assertTrue(command != null);
			assertTrue(command.getDefaultAgency().equals(""));
			assertTrue(mav.getViewName().equals("profile-list"));
			assertFalse(profileManager.load(13000L).isDefaultProfile());
			assertTrue(profileManager.load(13004L).isDefaultProfile());
		}
		catch(Exception e)
		{
			fail(e.getClass().getName()+": "+e.getMessage());
		}
	}

	@Test
	public final void testMakeDefaultProfileController() {
		assertTrue(testInstance != null);
	}

	@Test
	public final void testGetProfileManager() {
		testInstance.setProfileManager(new MockProfileManager(testFile));
	}

}
