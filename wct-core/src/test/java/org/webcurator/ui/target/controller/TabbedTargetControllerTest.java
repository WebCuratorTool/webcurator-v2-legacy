package org.webcurator.ui.target.controller;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.webcurator.core.profiles.MockProfileManager;
import org.webcurator.test.*;
import org.webcurator.ui.common.Constants;
import org.webcurator.ui.groups.command.DefaultCommand;
import org.webcurator.ui.site.command.*;
import org.webcurator.ui.site.validator.SiteValidator;
import org.webcurator.auth.*;
import org.webcurator.domain.MockTargetDAO;
import org.webcurator.domain.model.auth.Privilege;
import org.webcurator.domain.model.core.*;
import org.webcurator.domain.model.dto.GroupMemberDTO;
import org.springframework.context.*;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.validation.BindException;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.webcurator.core.sites.*;
import org.webcurator.core.targets.MockTargetManager;
import org.webcurator.core.targets.TargetManager;
import org.webcurator.ui.util.*;
import org.springframework.web.servlet.ModelAndView;
import org.webcurator.ui.site.SiteEditorContext;
import org.webcurator.ui.target.TargetEditorContext;
import org.webcurator.ui.target.command.TargetDefaultCommand;
import org.webcurator.core.util.*;
import org.webcurator.core.agency.*;



public class TabbedTargetControllerTest extends BaseWCTTest<TabbedTargetController>{

	public TabbedTargetControllerTest()
	{
		super(TabbedTargetController.class, "src/test/java/org/webcurator/ui/target/controller/tabbedtargetcontrollertest.xml");
	}
	TargetManager manager;
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
		manager = new MockTargetManager(testFile);
		testInstance.setTargetManager(manager);
		TabConfig tabConfig = new TabConfig();
		tabConfig.setViewName("target");
		List<Tab> tabs = getTabList(manager);
		tabConfig.setTabs(tabs);
		testInstance.setTabConfig(tabConfig);
		TargetSearchController searchController = new TargetSearchController();
		searchController.setAgencyUserManager(new MockAgencyUserManagerImpl(testFile));
		searchController.setTargetManager(manager);
		MockTargetDAO targetDao = new MockTargetDAO(testFile);
		searchController.setTargetDao(targetDao);
		testInstance.setSearchController(searchController);
		
	}
	
	private List<Tab> getTabList(TargetManager targetManager)
	{
		List<Tab> tabs = new ArrayList<Tab>();
		
		Tab tabGeneral = new Tab();
		tabGeneral.setCommandClass(SiteCommand.class);
		tabGeneral.setJsp("../target-general.jsp");
		tabGeneral.setPageId("GENERAL");
		//tabGeneral.setValidator(new TargetValidator());

		AgencyUserManager agencyUserManager = new MockAgencyUserManagerImpl(testFile); 
		
		TargetGeneralHandler genHandler = new TargetGeneralHandler();
		genHandler.setTargetManager(targetManager);
		tabGeneral.setTabHandler(genHandler);
		genHandler.setAgencyUserManager(agencyUserManager);
		
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
	public final void testShowForm() {
		try
		{
			MockHttpServletRequest request = new MockHttpServletRequest();
			MockHttpServletResponse response = new MockHttpServletResponse();
			TargetDefaultCommand comm = new TargetDefaultCommand();
			comm.setMode(TargetDefaultCommand.MODE_EDIT);
			comm.setTargetOid(null);
			
			BindException aError = new BindException(new DefaultSiteCommand(), null);
			ModelAndView mav = testInstance.showForm(request, response, comm, aError);
			assertTrue(mav != null);
			assertTrue(mav.getViewName().equals("target"));
			//TargetEditorContext context = testInstance.getEditorContext(request);
			//assertSame(context.getTarget().getOwningUser(), AuthUtil.getRemoteUserObject().getUser());
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
			
			Target target = manager.load(4000L);
			TargetEditorContext context = new TargetEditorContext(manager, target, true);
			request.getSession().setAttribute(TabbedTargetController.EDITOR_CONTEXT, context);
			
			TargetDefaultCommand comm = new TargetDefaultCommand();
			comm.setMode(TargetDefaultCommand.MODE_EDIT);
			comm.setTargetOid(4000L);
			Tab currTab = testInstance.getTabConfig().getTabs().get(0);
			assertTrue(currTab != null);
			BindException aError = new BindException(new TargetDefaultCommand(), null);
			testInstance.showForm(request, response, comm, aError);
			context = testInstance.getEditorContext(request);
			context.getTarget().setName("Test Target");
			context.setParents(new ArrayList<GroupMemberDTO>());
			ModelAndView mav = testInstance.processSave(currTab, request, response, comm, aError);
			assertTrue(mav != null);
			assertTrue(mav.getViewName().equals("target-search"));
			assertTrue(mav.getModel().get("page_message") != null);
			assertTrue(((String)mav.getModel().get("page_message")).startsWith("target.saved"));
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
			assertTrue(mav.getViewName().equals("redirect:/curator/target/search.html"));
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
			Target target = manager.load(4000L);
			TargetEditorContext context = new TargetEditorContext(manager, target, true);
			request.getSession().setAttribute(TabbedTargetController.EDITOR_CONTEXT, context);
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
	public final void testEditButtonVisiblity() {
		try
		{
			
			MockHttpServletRequest request = new MockHttpServletRequest();
			MockHttpServletResponse response = new MockHttpServletResponse();
			TargetDefaultCommand comm = new TargetDefaultCommand();
			comm.setMode(TargetDefaultCommand.MODE_VIEW);
			comm.setTargetOid(4000L);
			
			Target aTarget = manager.load(comm.getTargetOid(), true);
			removeAllCurrentUserPrivileges();
			
			assertFalse(authorityManager.hasPrivilege(aTarget,Privilege.MODIFY_TARGET));
			
			
			BindException aError = new BindException(new DefaultSiteCommand(), null);
			ModelAndView mav = testInstance.showForm(request, response, comm, aError);
			
			assertFalse((Boolean)request.getSession().getAttribute(Constants.GBL_SESS_CAN_EDIT));
			
			addCurrentUserPrivilege(Privilege.SCOPE_AGENCY, Privilege.MODIFY_TARGET);
			
			assertTrue(authorityManager.hasPrivilege(aTarget,Privilege.MODIFY_TARGET));
			
			mav = testInstance.showForm(request, response, comm, aError);
			//assertTrue(mav != null);
			//assertTrue(mav.getViewName().equals("site"));
			
			assertTrue((Boolean)request.getSession().getAttribute(Constants.GBL_SESS_CAN_EDIT));
			
			removeCurrentUserPrivilege(Privilege.SCOPE_AGENCY, Privilege.MODIFY_TARGET);
			
			assertFalse(authorityManager.hasPrivilege(aTarget,Privilege.MODIFY_TARGET));
			
			addCurrentUserPrivilege(Privilege.SCOPE_ALL, Privilege.MODIFY_TARGET);
			
			assertTrue(authorityManager.hasPrivilege(aTarget,Privilege.MODIFY_TARGET));
			
			mav = testInstance.showForm(request, response, comm, aError);
			//assertTrue(mav != null);
			//assertTrue(mav.getViewName().equals("site"));
			
			assertTrue((Boolean)request.getSession().getAttribute(Constants.GBL_SESS_CAN_EDIT));
			
			
			
		}
		catch (Exception e)
		{
			String message = e.getClass().toString() + " - " + e.getMessage();
			log.debug(message);
			fail(message);
		}
	}
}
