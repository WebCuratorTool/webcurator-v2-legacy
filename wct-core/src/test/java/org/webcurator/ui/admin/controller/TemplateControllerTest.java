package org.webcurator.ui.admin.controller;

import static org.junit.Assert.*;
import java.util.*;


import org.junit.Test;
import org.springframework.context.MockMessageSource;
import org.springframework.mock.web.*;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.webcurator.test.*;
import org.webcurator.ui.admin.command.*;
import org.webcurator.auth.AuthorityManagerImpl;
import org.webcurator.core.admin.PermissionTemplateManagerImpl;
import org.webcurator.core.agency.*;
import org.webcurator.domain.MockPermissionTemplateDAO;
import org.webcurator.domain.MockUserRoleDAO;
import org.webcurator.domain.model.auth.Agency;


final class MockAgencyUserManager extends AgencyUserManagerImpl {

	   public List getAgenciesForTemplatePriv() {
	             List<Agency> singleAgency = new ArrayList<Agency>();
	             return singleAgency;
	   }
}

public class TemplateControllerTest extends BaseWCTTest<TemplateController> {

	public TemplateControllerTest()
	{
		super(TemplateController.class, "src/test/java/org/webcurator/ui/admin/controller/TemplateControllerTest.xml");
		
	}
	
	@Test
	public final void testProcessFormSubmission_view() {
		
		String action = TemplateCommand.ACTION_VIEW;
		
		setUpController();
		
		TemplateCommand aCmd = setUpCommand(action, 1);
		BindException aError = new BindException(aCmd, action);
		
		
		MockHttpServletRequest aReq = new MockHttpServletRequest();
		MockHttpServletResponse aRes = new MockHttpServletResponse();
		
		
		try
		{
			ModelAndView mav = testInstance.processFormSubmission(aReq, aRes, aCmd, aError);
			assertTrue(mav.getViewName().equals("view-template"));
		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}

	private TemplateCommand setUpCommand(String action, int oid) {
		TemplateCommand aCmd = new TemplateCommand();
		aCmd.setAction(action);
		if (oid > 0)
			aCmd.setOid((long)oid);
		return aCmd;
	}

	private void setUpController() {
		
		TemplateController controller = new TemplateController();
		AgencyUserManagerImpl aum = new AgencyUserManagerImpl();
		MockUserRoleDAO murDao = new MockUserRoleDAO("src/test/java/org/webcurator/ui/admin/controller/TemplateControllerTest.xml");
		aum.setUserRoleDAO(murDao);
		aum.setAuthorityManager(new AuthorityManagerImpl());
		testInstance.setAgencyUserManager(aum);
		
		testInstance.setMessageSource(new MockMessageSource());
		
		MockPermissionTemplateDAO ptDAO = new MockPermissionTemplateDAO("src/test/java/org/webcurator/ui/admin/controller/TemplateControllerTest.xml");
		PermissionTemplateManagerImpl ptm = new PermissionTemplateManagerImpl();
		ptm.setPermissionTemplateDAO(ptDAO);
		ptm.setAuthorityManager(new AuthorityManagerImpl());
		testInstance.setPermissionTemplateManager(ptm);
		
	}
	
	@Test
	public final void testProcessFormSubmission_new() {
		
		String action = TemplateCommand.ACTION_NEW;
		
		setUpController();
		
		TemplateCommand aCmd = setUpCommand(action, 0);
		BindException aError = new BindException(aCmd, action);
		
		
		MockHttpServletRequest aReq = new MockHttpServletRequest();
		MockHttpServletResponse aRes = new MockHttpServletResponse();

		try
		{
			ModelAndView mav = testInstance.processFormSubmission(aReq, aRes, aCmd, aError);
			assertTrue(mav.getViewName().equals("add-template"));
		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}
	
	@Test
	public final void testProcessFormSubmission_edit() {
		
		String action = TemplateCommand.ACTION_EDIT;
		
		setUpController();
		
		TemplateCommand aCmd = setUpCommand(action, 1);
		BindException aError = new BindException(aCmd, action);
		
		
		MockHttpServletRequest aReq = new MockHttpServletRequest();
		MockHttpServletResponse aRes = new MockHttpServletResponse();

		try
		{
			ModelAndView mav = testInstance.processFormSubmission(aReq, aRes, aCmd, aError);
			assertTrue(mav.getViewName().equals("add-template"));
		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}
	
	@Test
	public final void testProcessFormSubmission_save() {
		
		String action = TemplateCommand.ACTION_SAVE;
		setUpController();
		
		TemplateCommand aCmd = setUpCommand(action, 1);
		BindException aError = new BindException(aCmd, action);
		
		
		MockHttpServletRequest aReq = new MockHttpServletRequest();
		MockHttpServletResponse aRes = new MockHttpServletResponse();

		try
		{
			ModelAndView mav = testInstance.processFormSubmission(aReq, aRes, aCmd, aError);
			assertTrue(mav.getViewName().equals("view-templates"));
		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
}

}
