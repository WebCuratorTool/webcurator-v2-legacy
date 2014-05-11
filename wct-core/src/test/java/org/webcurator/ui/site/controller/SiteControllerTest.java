package org.webcurator.ui.site.controller;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.webcurator.core.profiles.MockProfileManager;
import org.webcurator.test.*;
import org.webcurator.ui.common.Constants;
import org.webcurator.ui.site.command.*;
import org.webcurator.ui.site.validator.SiteValidator;
import org.webcurator.auth.*;
import org.webcurator.domain.model.auth.Privilege;
import org.webcurator.domain.model.core.*;
import org.springframework.context.*;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.validation.BindException;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.webcurator.core.sites.*;
import org.webcurator.ui.util.*;
import org.springframework.web.servlet.ModelAndView;
import org.webcurator.ui.site.SiteEditorContext;
import org.webcurator.core.util.*;
import org.webcurator.core.agency.*;



public class SiteControllerTest extends BaseWCTTest<SiteController>{

	public SiteControllerTest()
	{
		super(SiteController.class, "src/test/java/org/webcurator/ui/site/controller/sitecontrollertest.xml");
	}
	AuthorityManagerImpl authorityManager;
	
    //Override BaseWCTTest setup method
	public void setUp() throws Exception {
		//call the overridden method as well
		super.setUp();
		
		//add the extra bits
		authorityManager = new AuthorityManagerImpl();
		testInstance.setAuthorityManager(authorityManager);
		BusinessObjectFactory factory = new BusinessObjectFactory();
		factory.setProfileManager(new MockProfileManager(testFile));
		testInstance.setBusinessObjectFactory(factory);
		testInstance.setMessageSource(new MockMessageSource());
		SiteManager manager = new MockSiteManagerImpl(testFile);
		testInstance.setSiteManager(manager);
		TabConfig tabConfig = new TabConfig();
		tabConfig.setViewName("site");
		List<Tab> tabs = getTabList(manager);
		tabConfig.setTabs(tabs);
		testInstance.setTabConfig(tabConfig);
		SiteSearchController searchController = new SiteSearchController();
		searchController.setAgencyUserManager(new MockAgencyUserManagerImpl(testFile));
		searchController.setSiteManager(manager);
		testInstance.setSiteSearchController(searchController);
		
	}
	
	private List<Tab> getTabList(SiteManager siteManager)
	{
		List<Tab> tabs = new ArrayList<Tab>();
		
		Tab tabGeneral = new Tab();
		tabGeneral.setCommandClass(SiteCommand.class);
		tabGeneral.setJsp("../site-general.jsp");
		tabGeneral.setPageId("GENERAL");
		tabGeneral.setValidator(new SiteValidator());

		SiteGeneralHandler genHandler = new SiteGeneralHandler();
		genHandler.setSiteManager(siteManager);
		tabGeneral.setTabHandler(genHandler);
		
		tabs.add(tabGeneral);
		
		return tabs;
	}
	
	@Test
	public final void testInitBinder() {
		MockHttpServletRequest request = new MockHttpServletRequest();
		ServletRequestDataBinder binder = new ServletRequestDataBinder(new SiteCommand(), "command");
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
	public final void testProcessInitial() {
		try
		{
			MockHttpServletRequest request = new MockHttpServletRequest();
			MockHttpServletResponse response = new MockHttpServletResponse();
			DefaultSiteCommand comm = new DefaultSiteCommand();
			comm.setEditMode(true);
			comm.setSiteOid(null);
			
			BindException aError = new BindException(new DefaultSiteCommand(), null);
			ModelAndView mav = testInstance.processInitial(request, response, comm, aError);
			assertTrue(mav != null);
			assertTrue(mav.getViewName().equals("site"));
			SiteEditorContext context = testInstance.getEditorContext(request);
			assertSame(context.getSite().getOwningAgency(), AuthUtil.getRemoteUserObject().getAgency());
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
			DefaultSiteCommand comm = new DefaultSiteCommand();
			comm.setEditMode(true);
			comm.setSiteOid(null);
			
			BindException aError = new BindException(new DefaultSiteCommand(), null);
			ModelAndView mav = testInstance.showForm(request, response, comm, aError);
			assertTrue(mav != null);
			assertTrue(mav.getViewName().equals("site"));
			SiteEditorContext context = testInstance.getEditorContext(request);
			assertSame(context.getSite().getOwningAgency(), AuthUtil.getRemoteUserObject().getAgency());
		}
		catch (Exception e)
		{
			String message = e.getClass().toString() + " - " + e.getMessage();
			log.debug(message);
			fail(message);
		}
	}

	@Test
	public final void testShowForm2() {
		try
		{
			MockHttpServletRequest request = new MockHttpServletRequest();
			MockHttpServletResponse response = new MockHttpServletResponse();
			DefaultSiteCommand comm = new DefaultSiteCommand();
			comm.setEditMode(false);
			comm.setSiteOid(9001L);
			
			BindException aError = new BindException(new DefaultSiteCommand(), null);
			this.addCurrentUserPrivilege(100, Privilege.MODIFY_SITE);
			ModelAndView mav = testInstance.showForm(request, response, comm, aError);
			assertTrue(mav != null);
			assertTrue(mav.getViewName().equals("site"));
		}
		catch (Exception e)
		{
			String message = e.getClass().toString() + " - " + e.getMessage();
			log.debug(message);
			fail(message);
		}
	}

	@Test
	public final void testProcessSave() {
		try
		{
			MockHttpServletRequest request = new MockHttpServletRequest();
			MockHttpServletResponse response = new MockHttpServletResponse();
			DefaultSiteCommand comm = new DefaultSiteCommand();
			comm.setEditMode(true);
			comm.setSiteOid(null);
			Tab currTab = testInstance.getTabConfig().getTabs().get(0);
			assertTrue(currTab != null);
			BindException aError = new BindException(new DefaultSiteCommand(), null);
			testInstance.processInitial(request, response, comm, aError);
			SiteEditorContext context = testInstance.getEditorContext(request);
			context.getSite().setTitle("Test Site");
			ModelAndView mav = testInstance.processSave(currTab, request, response, comm, aError);
			assertTrue(mav != null);
			assertTrue(mav.getViewName().equals("site-search"));
			assertTrue(mav.getModel().get("page_message") != null);
			assertTrue(((String)mav.getModel().get("page_message")).startsWith("site.saved"));
		}
		catch (Exception e)
		{
			String message = e.getClass().toString() + " - " + e.getMessage();
			log.debug(message);
			fail(message);
		}
	}

	@Test
	public final void testProcessCancel() {
		try
		{
			MockHttpServletRequest request = new MockHttpServletRequest();
			MockHttpServletResponse response = new MockHttpServletResponse();
			DefaultSiteCommand comm = new DefaultSiteCommand();
			comm.setEditMode(true);
			comm.setSiteOid(null);
			Tab currTab = testInstance.getTabConfig().getTabs().get(0);
			assertTrue(currTab != null);
			BindException aError = new BindException(new DefaultSiteCommand(), null);
			ModelAndView mav = testInstance.processCancel(currTab, request, response, comm, aError);
			assertTrue(mav != null);
			assertTrue(mav.getViewName().equals("redirect:/curator/site/search.html"));
		}
		catch (Exception e)
		{
			String message = e.getClass().toString() + " - " + e.getMessage();
			log.debug(message);
			fail(message);
		}
	}
	
	@Test
	public final void testSwitchToEditMode() {
		try
		{
			MockHttpServletRequest request = new MockHttpServletRequest();
			SiteEditorContext context = new SiteEditorContext(new Site());
			request.getSession().setAttribute(SiteController.EDITOR_CONTEXT, context);
			testInstance.switchToEditMode(request);
			context = testInstance.getEditorContext(request);
			assertTrue(context.isEditMode());
			
		}
		catch (Exception e)
		{
			String message = e.getClass().toString() + " - " + e.getMessage();
			log.debug(message);
			fail(message);
		}
	}
	
	@Test
	public final void testEditButtonVisiblityScopeAll() {
		try
		{
			
			MockHttpServletRequest request = new MockHttpServletRequest();
			MockHttpServletResponse response = new MockHttpServletResponse();
			DefaultSiteCommand comm = new DefaultSiteCommand();
			comm.setEditMode(false);
			comm.setSiteOid(9000L);
			
			this.removeAllCurrentUserPrivileges();

			assertFalse(authorityManager.hasPrivilege(Privilege.MODIFY_SITE,Privilege.SCOPE_ALL));
			
			
			BindException aError = new BindException(new DefaultSiteCommand(), null);
			ModelAndView mav = testInstance.showForm(request, response, comm, aError);
			assertTrue(mav != null);
			assertTrue(mav.getViewName().equals("site"));
			
			assertFalse((Boolean)request.getSession().getAttribute(Constants.GBL_SESS_CAN_EDIT));
			
			addCurrentUserPrivilege(Privilege.SCOPE_ALL, Privilege.MODIFY_SITE);

			assertTrue(authorityManager.hasPrivilege(Privilege.MODIFY_SITE,Privilege.SCOPE_ALL));
		
			
			mav = testInstance.showForm(request, response, comm, aError);
			assertTrue(mav != null);
			assertTrue(mav.getViewName().equals("site"));
			
			assertTrue((Boolean)request.getSession().getAttribute(Constants.GBL_SESS_CAN_EDIT));
			
			
			
			
		}
		catch (Exception e)
		{
			String message = e.getClass().toString() + " - " + e.getMessage();
			log.debug(message);
			fail(message);
		}
	}

	@Test
	public final void testEditButtonVisiblityScopeAgency() {
		try
		{
			
			MockHttpServletRequest request = new MockHttpServletRequest();
			MockHttpServletResponse response = new MockHttpServletResponse();
			DefaultSiteCommand comm = new DefaultSiteCommand();
			comm.setEditMode(false);
			comm.setSiteOid(9000L);
			
			this.removeAllCurrentUserPrivileges();
			
			assertFalse(authorityManager.hasPrivilege(Privilege.MODIFY_SITE,Privilege.SCOPE_AGENCY));
			
			
			BindException aError = new BindException(new DefaultSiteCommand(), null);
			ModelAndView mav = testInstance.showForm(request, response, comm, aError);
			assertTrue(mav != null);
			assertTrue(mav.getViewName().equals("site"));
			
			assertFalse((Boolean)request.getSession().getAttribute(Constants.GBL_SESS_CAN_EDIT));
			
			addCurrentUserPrivilege(Privilege.SCOPE_AGENCY, Privilege.MODIFY_SITE);

			assertTrue(authorityManager.hasPrivilege(Privilege.MODIFY_SITE,Privilege.SCOPE_AGENCY));
		
			
			mav = testInstance.showForm(request, response, comm, aError);
			assertTrue(mav != null);
			assertTrue(mav.getViewName().equals("site"));
			
			assertTrue((Boolean)request.getSession().getAttribute(Constants.GBL_SESS_CAN_EDIT));
			
			
			
			
		}
		catch (Exception e)
		{
			String message = e.getClass().toString() + " - " + e.getMessage();
			log.debug(message);
			fail(message);
		}
	}

	@Test
	public final void testEditButtonVisiblityScopeAgency2() {
		try
		{
			
			MockHttpServletRequest request = new MockHttpServletRequest();
			MockHttpServletResponse response = new MockHttpServletResponse();
			DefaultSiteCommand comm = new DefaultSiteCommand();
			comm.setEditMode(false);
			comm.setSiteOid(9001L);
			
			this.removeAllCurrentUserPrivileges();
			
			assertFalse(authorityManager.hasPrivilege(Privilege.MODIFY_SITE,Privilege.SCOPE_AGENCY));
			
			
			BindException aError = new BindException(new DefaultSiteCommand(), null);
			ModelAndView mav = testInstance.showForm(request, response, comm, aError);
			assertTrue(mav != null);
			assertTrue(mav.getViewName().equals("site"));
			
			assertFalse((Boolean)request.getSession().getAttribute(Constants.GBL_SESS_CAN_EDIT));
			
			addCurrentUserPrivilege(Privilege.SCOPE_AGENCY, Privilege.MODIFY_SITE);

			assertTrue(authorityManager.hasPrivilege(Privilege.MODIFY_SITE,Privilege.SCOPE_AGENCY));
		
			
			mav = testInstance.showForm(request, response, comm, aError);
			assertTrue(mav != null);
			assertTrue(mav.getViewName().equals("site"));
			
			assertFalse((Boolean)request.getSession().getAttribute(Constants.GBL_SESS_CAN_EDIT));
			
			
			
			
		}
		catch (Exception e)
		{
			String message = e.getClass().toString() + " - " + e.getMessage();
			log.debug(message);
			fail(message);
		}
	}
}
