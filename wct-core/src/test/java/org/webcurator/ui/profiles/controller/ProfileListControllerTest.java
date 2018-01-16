package org.webcurator.ui.profiles.controller;

import static org.junit.Assert.*;

import java.util.*;

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
import org.webcurator.core.util.AuthUtil;
import org.webcurator.domain.model.auth.Agency;
import org.webcurator.domain.model.auth.Privilege;
import org.webcurator.domain.model.dto.*;
import org.webcurator.ui.profiles.command.ProfileListCommand;

public class ProfileListControllerTest extends BaseWCTTest<ProfileListController> {

	public ProfileListControllerTest()
	{
		super(ProfileListController.class,
				"src/test/java/org/webcurator/ui/profiles/controller/ProfileListControllerTest.xml");
	}
	
	private void performTestGetView(int scope, String privilege, boolean showInactive)
	{
		ModelAndView mav = null;
		
		this.removeAllCurrentUserPrivileges();
		
		if(scope >= 0)
		{
			this.addCurrentUserPrivilege(scope, privilege);
		}
		ProfileListCommand comm = new ProfileListCommand();
		comm.setShowInactive(showInactive);
		comm.setActionCommand(ProfileListCommand.ACTION_LIST);
		mav = testInstance.getView(comm);
		assertTrue(mav != null);
		assertTrue(mav.getViewName().equals("profile-list"));
		ProfileListCommand command = (ProfileListCommand)mav.getModel().get("command");
		assertTrue(command != null);
		assertEquals(command.isShowInactive(), showInactive);
		List<Agency> agencies = (List<Agency>)mav.getModel().get("agencies");
		assertTrue(agencies != null);
		List<ProfileDTO> profiles = (List<ProfileDTO>)mav.getModel().get("profiles");
		assertTrue(profiles != null);
		switch(scope)
		{
		case -1:
			assertTrue(agencies.size() == 0);
			assertTrue(profiles.size() == 0);
			break;
		case Privilege.SCOPE_AGENCY:
			assertTrue(agencies.size() == 1);
			assertTrue(showInactive?profiles.size() == 3: profiles.size() == 2);
			break;
		case Privilege.SCOPE_ALL:
			assertTrue(agencies.size() == 2);
			assertTrue(showInactive?profiles.size() == 5: profiles.size() == 3);
			break;
		}
	}
	
	private void performTestHandle(String action, int scope, String privilege, boolean showInactive)
	{
		ModelAndView mav = null;
		
		this.removeAllCurrentUserPrivileges();
		
		if(scope >= 0)
		{
			this.addCurrentUserPrivilege(scope, privilege);
		}
		
		try
		{
			HttpServletRequest req = new MockHttpServletRequest();
			HttpServletResponse res = new MockHttpServletResponse();
			
			ProfileListCommand comm = new ProfileListCommand();
			comm.setShowInactive(showInactive);
			comm.setActionCommand(action);
			
			BindException errors = new BindException(comm, null);
	
			mav = testInstance.handle(req, res, comm, errors);
			assertTrue(mav != null);
			assertTrue(mav.getViewName().equals("profile-list"));
			ProfileListCommand command = (ProfileListCommand)mav.getModel().get("command");
			assertTrue(command != null);
			assertEquals(command.isShowInactive(), showInactive);
			if(action.equals(ProfileListCommand.ACTION_FILTER))
			{
				assertTrue(command.getDefaultAgency().equals(""));
			}
			if(action.equals(ProfileListCommand.ACTION_LIST))
			{
				assertTrue(command.getDefaultAgency().equals(AuthUtil.getRemoteUserObject().getAgency().getName()));
			}
			List<Agency> agencies = (List<Agency>)mav.getModel().get("agencies");
			assertTrue(agencies != null);
			List<ProfileDTO> profiles = (List<ProfileDTO>)mav.getModel().get("profiles");
			assertTrue(profiles != null);
			switch(scope)
			{
			case -1:
				assertTrue(agencies.size() == 0);
				assertTrue(profiles.size() == 0);
				break;
			case Privilege.SCOPE_AGENCY:
				assertTrue(agencies.size() == 1);
				assertTrue(showInactive?profiles.size() == 3: profiles.size() == 2);
				break;
			case Privilege.SCOPE_ALL:
				assertTrue(agencies.size() == 2);
				assertTrue(showInactive?profiles.size() == 5: profiles.size() == 3);
				break;
			}
			assertTrue(((Boolean)req.getSession().getAttribute(ProfileListController.SESSION_KEY_SHOW_INACTIVE)).equals(showInactive));
		}
		catch(Exception e)
		{
			fail(e.getClass().getName()+": "+e.getMessage());
		}
	}
	
	@Test
	public final void testProfileListController() {
		assertTrue(testInstance != null);
	}

	@Test
	public final void testHandle() {
		
		this.testSetAgencyUserManager();
		this.testSetAuthorityManager();
		this.testSetProfileManager();
		
		performTestHandle(ProfileListCommand.ACTION_LIST, -1, "", false);
		performTestHandle(ProfileListCommand.ACTION_LIST, -1, "", true);
		performTestHandle(ProfileListCommand.ACTION_LIST, Privilege.SCOPE_AGENCY, Privilege.VIEW_PROFILES, false);
		performTestHandle(ProfileListCommand.ACTION_LIST, Privilege.SCOPE_AGENCY, Privilege.VIEW_PROFILES, true);
		performTestHandle(ProfileListCommand.ACTION_LIST, Privilege.SCOPE_AGENCY, Privilege.MANAGE_PROFILES, false);
		performTestHandle(ProfileListCommand.ACTION_LIST, Privilege.SCOPE_AGENCY, Privilege.MANAGE_PROFILES, true);
		performTestHandle(ProfileListCommand.ACTION_LIST, Privilege.SCOPE_ALL, Privilege.VIEW_PROFILES, false);
		performTestHandle(ProfileListCommand.ACTION_LIST, Privilege.SCOPE_ALL, Privilege.VIEW_PROFILES, true);
		performTestHandle(ProfileListCommand.ACTION_LIST, Privilege.SCOPE_ALL, Privilege.MANAGE_PROFILES, false);
		performTestHandle(ProfileListCommand.ACTION_LIST, Privilege.SCOPE_ALL, Privilege.MANAGE_PROFILES, true);
		performTestHandle(ProfileListCommand.ACTION_FILTER, -1, "", false);
		performTestHandle(ProfileListCommand.ACTION_FILTER, -1, "", true);
		performTestHandle(ProfileListCommand.ACTION_FILTER, Privilege.SCOPE_AGENCY, Privilege.VIEW_PROFILES, false);
		performTestHandle(ProfileListCommand.ACTION_FILTER, Privilege.SCOPE_AGENCY, Privilege.VIEW_PROFILES, true);
		performTestHandle(ProfileListCommand.ACTION_FILTER, Privilege.SCOPE_AGENCY, Privilege.MANAGE_PROFILES, false);
		performTestHandle(ProfileListCommand.ACTION_FILTER, Privilege.SCOPE_AGENCY, Privilege.MANAGE_PROFILES, true);
		performTestHandle(ProfileListCommand.ACTION_FILTER, Privilege.SCOPE_ALL, Privilege.VIEW_PROFILES, false);
		performTestHandle(ProfileListCommand.ACTION_FILTER, Privilege.SCOPE_ALL, Privilege.VIEW_PROFILES, true);
		performTestHandle(ProfileListCommand.ACTION_FILTER, Privilege.SCOPE_ALL, Privilege.MANAGE_PROFILES, false);
		performTestHandle(ProfileListCommand.ACTION_FILTER, Privilege.SCOPE_ALL, Privilege.MANAGE_PROFILES, true);
	}

	@Test
	public final void testGetView() {
		
		this.testSetAgencyUserManager();
		this.testSetAuthorityManager();
		this.testSetProfileManager();
		
		performTestGetView(-1, "", false);
		performTestGetView(-1, "", true);
		performTestGetView(Privilege.SCOPE_AGENCY, Privilege.VIEW_PROFILES, false);
		performTestGetView(Privilege.SCOPE_AGENCY, Privilege.VIEW_PROFILES, true);
		performTestGetView(Privilege.SCOPE_AGENCY, Privilege.MANAGE_PROFILES, false);
		performTestGetView(Privilege.SCOPE_AGENCY, Privilege.MANAGE_PROFILES, true);
		performTestGetView(Privilege.SCOPE_ALL, Privilege.VIEW_PROFILES, false);
		performTestGetView(Privilege.SCOPE_ALL, Privilege.VIEW_PROFILES, true);
		performTestGetView(Privilege.SCOPE_ALL, Privilege.MANAGE_PROFILES, false);
		performTestGetView(Privilege.SCOPE_ALL, Privilege.MANAGE_PROFILES, true);
	}
	
	@Test
	public final void testSetProfileManager() {
		testInstance.setProfileManager(new MockProfileManager(testFile));
	}

	@Test
	public final void testSetAgencyUserManager() {
		testInstance.setAgencyUserManager(new MockAgencyUserManagerImpl(testFile));
	}

	@Test
	public final void testSetAuthorityManager() {
		testInstance.setAuthorityManager(new AuthorityManagerImpl());
	}

}
