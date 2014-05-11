package org.webcurator.ui.target.controller;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.springframework.context.MockMessageSource;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.validation.BindException;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.webcurator.auth.AuthorityManagerImpl;
import org.webcurator.core.agency.AgencyUserManager;
import org.webcurator.core.agency.MockAgencyUserManagerImpl;
import org.webcurator.core.common.EnvironmentImpl;
import org.webcurator.core.harvester.coordinator.MockHarvestCoordinator;
import org.webcurator.core.profiles.MockProfileManager;
import org.webcurator.core.scheduler.MockTargetInstanceManager;
import org.webcurator.core.scheduler.TargetInstanceManager;
import org.webcurator.core.targets.MockTargetManager;
import org.webcurator.core.targets.TargetManager;
import org.webcurator.domain.MockTargetDAO;
import org.webcurator.domain.model.auth.Privilege;
import org.webcurator.domain.model.core.BusinessObjectFactory;
import org.webcurator.domain.model.core.Target;
import org.webcurator.domain.model.core.TargetInstance;
import org.webcurator.domain.model.dto.GroupMemberDTO;
import org.webcurator.test.BaseWCTTest;
import org.webcurator.ui.common.Constants;
import org.webcurator.ui.site.command.DefaultSiteCommand;
import org.webcurator.ui.site.command.SiteCommand;
import org.webcurator.ui.target.TargetEditorContext;
import org.webcurator.ui.target.command.TargetDefaultCommand;
import org.webcurator.ui.target.command.TargetInstanceCommand;
import org.webcurator.ui.util.Tab;
import org.webcurator.ui.util.TabConfig;

public class TabbedTargetInstanceControllerTest extends BaseWCTTest<TabbedTargetInstanceController> {
	public TabbedTargetInstanceControllerTest()
	{
		super(TabbedTargetInstanceController.class, "src/test/java/org/webcurator/ui/target/controller/tabbedtargetinstancecontrollertest.xml");
	}
	TargetInstanceManager manager;
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
		//testInstance.setBusinessObjectFactory(factory);
		testInstance.setMessageSource(new MockMessageSource());
		manager = new MockTargetInstanceManager(testFile);
		testInstance.setTargetInstanceManager(manager);
		TabConfig tabConfig = new TabConfig();
		tabConfig.setViewName("target-instance");
		List<Tab> tabs = getTabList(manager);
		tabConfig.setTabs(tabs);
		testInstance.setTabConfig(tabConfig);
		QueueController queueController = new QueueController();
		queueController.setEnvironment(new EnvironmentImpl());
		queueController.setAgencyUserManager(new MockAgencyUserManagerImpl(testFile));
		queueController.setTargetInstanceManager(manager);
		queueController.setHarvestCoordinator(new MockHarvestCoordinator());
		//MockTargetDAO targetDao = new MockTargetDAO(testFile);
		//searchController.setTargetDao(targetDao);
		testInstance.setQueueController(queueController);
		
	}
	
	private List<Tab> getTabList(TargetInstanceManager targetInstanceManager)
	{
		List<Tab> tabs = new ArrayList<Tab>();
		
		Tab tabGeneral = new Tab();
		tabGeneral.setCommandClass(SiteCommand.class);
		tabGeneral.setJsp("../target-instance-general.jsp");
		tabGeneral.setPageId("GENERAL");
		//tabGeneral.setValidator(new TargetValidator());

		AgencyUserManager agencyUserManager = new MockAgencyUserManagerImpl(testFile); 
		
		TargetInstanceGeneralHandler genHandler = new TargetInstanceGeneralHandler();
		genHandler.setTargetInstanceManager(targetInstanceManager);
		tabGeneral.setTabHandler(genHandler);
		genHandler.setAgencyUserManager(agencyUserManager);
		genHandler.setAuthorityManager(new AuthorityManagerImpl());
		
		tabs.add(tabGeneral);
		
		return tabs;
	}


	@Test
	public final void testShowForm() {
		try
		{
			MockHttpServletRequest request = new MockHttpServletRequest();
			MockHttpServletResponse response = new MockHttpServletResponse();
			TargetInstanceCommand comm = new TargetInstanceCommand();
			comm.setCmd(TargetInstanceCommand.ACTION_EDIT);
			comm.setTargetInstanceId(5000L);
			
			BindException aError = new BindException(new DefaultSiteCommand(), null);
			ModelAndView mav = testInstance.showForm(request, response, comm, aError);
			assertTrue(mav != null);
			assertTrue(mav.getViewName().equals("target-instance"));
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
			
			//TargetInstance target = manager.getTargetInstance(5000L);
			
			request.getSession().setAttribute(TargetInstanceCommand.SESSION_MODE, true);
			request.getSession().setAttribute(Constants.GBL_SESS_EDIT_MODE, true); 
			
			TargetInstanceCommand comm = new TargetInstanceCommand();
			comm.setCmd(TargetInstanceCommand.ACTION_EDIT);
			comm.setTargetInstanceId(5000L);
			
			Tab currTab = testInstance.getTabConfig().getTabs().get(0);
			assertTrue(currTab != null);
			BindException aError = new BindException(new TargetDefaultCommand(), null);
			testInstance.showForm(request, response, comm, aError);
			//context = testInstance.getEditorContext(request);
			//context.getTarget().setName("Test Target");
			//context.setParents(new ArrayList<GroupMemberDTO>());
			ModelAndView mav = testInstance.processSave(currTab, request, response, comm, aError);
			assertTrue(mav != null);
			assertTrue(mav.getViewName().equals("TargetInstanceQueue"));
			assertTrue(mav.getModel().get("page_message") != null);
			assertTrue(((String)mav.getModel().get("page_message")).startsWith("targetInstance.saved"));
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

			TargetInstanceCommand comm = new TargetInstanceCommand();
			comm.setCmd(TargetInstanceCommand.ACTION_EDIT);
			comm.setTargetInstanceId(5000L);
			
			Tab currTab = testInstance.getTabConfig().getTabs().get(0);
			assertTrue(currTab != null);
			BindException aError = new BindException(new DefaultSiteCommand(), null);
			ModelAndView mav = testInstance.processCancel(currTab, request, response, comm, aError);
			assertTrue(mav != null);
			assertTrue(mav.getViewName().equals("redirect:/curator/target/queue.html"));
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
			
			
			
			request.getSession().setAttribute(TargetInstanceCommand.SESSION_MODE, true);
			request.getSession().setAttribute(Constants.GBL_SESS_EDIT_MODE, false); 
			
			testInstance.switchToEditMode(request);

			assertTrue((Boolean) request.getSession().getAttribute(Constants.GBL_SESS_EDIT_MODE));
			
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
			TargetInstanceCommand comm = new TargetInstanceCommand();
			comm.setCmd(TargetInstanceCommand.ACTION_VIEW);
			comm.setTargetInstanceId(5000L);
			
			TargetInstance ti = manager.getTargetInstance(comm.getTargetInstanceId(), true);
			removeAllCurrentUserPrivileges();
			
			String[] privs = {Privilege.MANAGE_TARGET_INSTANCES,Privilege.MANAGE_WEB_HARVESTER};
			
			assertFalse(authorityManager.hasAtLeastOnePrivilege(ti,privs));
			
			
			BindException aError = new BindException(new DefaultSiteCommand(), null);
			ModelAndView mav = testInstance.showForm(request, response, comm, aError);
			
			assertFalse((Boolean)request.getSession().getAttribute(Constants.GBL_SESS_CAN_EDIT));
			
			addCurrentUserPrivilege(Privilege.SCOPE_AGENCY, Privilege.MANAGE_TARGET_INSTANCES);
			
			assertTrue(authorityManager.hasAtLeastOnePrivilege(ti,privs));
			
			mav = testInstance.showForm(request, response, comm, aError);
			//assertTrue(mav != null);
			//assertTrue(mav.getViewName().equals("site"));
			
			assertTrue((Boolean)request.getSession().getAttribute(Constants.GBL_SESS_CAN_EDIT));
			
			removeCurrentUserPrivilege(Privilege.SCOPE_AGENCY, Privilege.MANAGE_TARGET_INSTANCES);
			
			assertFalse(authorityManager.hasAtLeastOnePrivilege(ti,privs));
			
			addCurrentUserPrivilege(Privilege.SCOPE_ALL, Privilege.MANAGE_TARGET_INSTANCES);
			
			assertTrue(authorityManager.hasAtLeastOnePrivilege(ti,privs));
			
			mav = testInstance.showForm(request, response, comm, aError);
			
			assertTrue((Boolean)request.getSession().getAttribute(Constants.GBL_SESS_CAN_EDIT));
			
			
			removeAllCurrentUserPrivileges();
			
		
			assertFalse(authorityManager.hasAtLeastOnePrivilege(ti,privs));
			
			
			addCurrentUserPrivilege(Privilege.SCOPE_AGENCY, Privilege.MANAGE_WEB_HARVESTER);
			
			assertTrue(authorityManager.hasAtLeastOnePrivilege(ti,privs));
			
			mav = testInstance.showForm(request, response, comm, aError);
			//assertTrue(mav != null);
			//assertTrue(mav.getViewName().equals("site"));
			
			assertTrue((Boolean)request.getSession().getAttribute(Constants.GBL_SESS_CAN_EDIT));
			
			removeCurrentUserPrivilege(Privilege.SCOPE_AGENCY, Privilege.MANAGE_WEB_HARVESTER);
			
			assertFalse(authorityManager.hasAtLeastOnePrivilege(ti,privs));
			
			addCurrentUserPrivilege(Privilege.SCOPE_ALL, Privilege.MANAGE_WEB_HARVESTER);
			
			assertTrue(authorityManager.hasAtLeastOnePrivilege(ti,privs));
			
			mav = testInstance.showForm(request, response, comm, aError);
			
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
