package org.webcurator.ui.groups.controller;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.springframework.context.MockMessageSource;
import org.webcurator.domain.model.auth.Privilege;
import org.webcurator.domain.model.core.TargetGroup;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.validation.BindException;

import org.springframework.web.servlet.ModelAndView;
import org.webcurator.auth.AuthorityManagerImpl;
import org.webcurator.core.agency.AgencyUserManager;
import org.webcurator.core.agency.MockAgencyUserManagerImpl;
import org.webcurator.core.common.WCTTreeSet;
import org.webcurator.core.profiles.MockProfileManager;
import org.webcurator.core.targets.MockTargetManager;
import org.webcurator.core.targets.TargetManager;

import org.webcurator.domain.model.core.BusinessObjectFactory;

import org.webcurator.test.BaseWCTTest;
import org.webcurator.ui.common.Constants;
import org.webcurator.ui.groups.GroupsEditorContext;
import org.webcurator.ui.groups.command.DefaultCommand;
import org.webcurator.ui.site.command.DefaultSiteCommand;
import org.webcurator.ui.site.command.SiteCommand;

import org.webcurator.ui.util.Tab;
import org.webcurator.ui.util.TabConfig;

public class TabbedGroupControllerTest extends
		BaseWCTTest<TabbedGroupController> {

	public TabbedGroupControllerTest() {
		super(
				TabbedGroupController.class,
				"src/test/java/org/webcurator/ui/groups/controller/TabbedGroupControllerTest.xml");
	}

	TargetManager manager;

	AuthorityManagerImpl authorityManager;

	// Override BaseWCTTest setup method
	public void setUp() throws Exception {
		// call the overridden method as well
		super.setUp();

		// add the extra bits
		authorityManager = new AuthorityManagerImpl();
		testInstance.setAuthorityManager(authorityManager);
		BusinessObjectFactory factory = new BusinessObjectFactory();
		factory.setProfileManager(new MockProfileManager(testFile));
		testInstance.setBusinessObjectFactory(factory);
		testInstance.setMessageSource(new MockMessageSource());
		manager = new MockTargetManager(testFile);
		testInstance.setTargetManager(manager);
		TabConfig tabConfig = new TabConfig();
		tabConfig.setViewName("group");
		List<Tab> tabs = getTabList(manager);
		tabConfig.setTabs(tabs);
		testInstance.setTabConfig(tabConfig);
		GroupSearchController searchController = new GroupSearchController();
		searchController.setAgencyUserManager(new MockAgencyUserManagerImpl(
				testFile));
		searchController.setTargetManager(manager);
		// MockGroupDAO targetDao = new MockGroupDAO(testFile);
		// searchController.set GroupDao(targetDao);
		testInstance.setSearchController(searchController);

	}

	private List<Tab> getTabList(TargetManager targetManager) {
		List<Tab> tabs = new ArrayList<Tab>();

		Tab tabGeneral = new Tab();
		tabGeneral.setCommandClass(SiteCommand.class);
		tabGeneral.setJsp("../target-general.jsp");
		tabGeneral.setPageId("GENERAL");
		// tabGeneral.setValidator(new GroupValidator());

		AgencyUserManager agencyUserManager = new MockAgencyUserManagerImpl(
				testFile);

		GeneralHandler genHandler = new GeneralHandler();
		genHandler.setAuthorityManager(new AuthorityManagerImpl());
		tabGeneral.setTabHandler(genHandler);
		genHandler.setAgencyUserManager(agencyUserManager);
		genHandler.setTargetManager(targetManager);
		genHandler.setSubGroupTypeName("Sub-Group");
		genHandler.setSubGroupSeparator(" > ");
		setGroupTypes(genHandler);

		tabs.add(tabGeneral);

		return tabs;
	}

	private void setGroupTypes(GeneralHandler genHandler)
	{
		List<String> subGroupTypes = new ArrayList<String>();
		subGroupTypes.add("");
		subGroupTypes.add("Collection");
		subGroupTypes.add("Subject");
		subGroupTypes.add("Thematic");
		subGroupTypes.add("Event");
		subGroupTypes.add("Functional");
		subGroupTypes.add("Sub-Group");
		
		WCTTreeSet groupTypesList = new WCTTreeSet(subGroupTypes, 50);
		genHandler.setGroupTypesList(groupTypesList);
	}
	
	@Test
	public final void testShowForm() {
		try {
			MockHttpServletRequest request = new MockHttpServletRequest();
			MockHttpServletResponse response = new MockHttpServletResponse();
			DefaultCommand comm = new DefaultCommand();
			comm.setMode(DefaultCommand.MODE_EDIT);
			comm.setTargetGroupOid(null);

			BindException aError = new BindException(new DefaultSiteCommand(),
					null);
			ModelAndView mav = testInstance.showForm(request, response, comm,
					aError);
			assertTrue(mav != null);
			assertTrue(mav.getViewName().equals("group"));
			// GroupEditorContext context =
			// testInstance.getEditorContext(request);
			// assertSame(context.getGroup().getOwningUser(),
			// AuthUtil.getRemoteUserObject().getUser());
		} catch (Exception e) {
			String message = e.getClass().toString() + " - " + e.getMessage();
			log.debug(message);
			fail(message);
		}
	}

	@Test
	public final void testProcessSave() {
		try {

			MockHttpServletRequest request = new MockHttpServletRequest();
			MockHttpServletResponse response = new MockHttpServletResponse();

			TargetGroup group = manager.loadGroup(15000L);
			GroupsEditorContext context = new GroupsEditorContext(group, true);
			request.getSession().setAttribute(
					TabbedGroupController.EDITOR_CONTEXT, context);

			DefaultCommand comm = new DefaultCommand();
			comm.setMode(DefaultCommand.MODE_EDIT);
			comm.setTargetGroupOid(15000L);
			Tab currTab = testInstance.getTabConfig().getTabs().get(0);
			assertTrue(currTab != null);
			BindException aError = new BindException(new DefaultCommand(), null);
			testInstance.showForm(request, response, comm, aError);
			context = testInstance.getEditorContext(request);
			context.getTargetGroup().setName("Test Group");
			ModelAndView mav = testInstance.processSave(currTab, request,
					response, comm, aError);
			assertTrue(mav != null);
			assertTrue(mav.getViewName().equals("groups-search"));
			assertTrue(mav.getModel().get("page_message") != null);
			assertTrue(((String) mav.getModel().get("page_message"))
					.startsWith("group.saved"));
		} catch (Exception e) {
			String message = e.getClass().toString() + " - " + e.getMessage();
			log.debug(message);
			fail(message);
		}
	}

	@Test
	public final void testProcessCancel() {
		try {
			MockHttpServletRequest request = new MockHttpServletRequest();
			MockHttpServletResponse response = new MockHttpServletResponse();
			DefaultCommand comm = new DefaultCommand();
			comm.setMode(DefaultCommand.MODE_EDIT);

			Tab currTab = testInstance.getTabConfig().getTabs().get(0);
			assertTrue(currTab != null);
			BindException aError = new BindException(new DefaultSiteCommand(),
					null);
			ModelAndView mav = testInstance.processCancel(currTab, request,
					response, comm, aError);
			assertTrue(mav != null);
			assertTrue(mav.getViewName().equals(
					"redirect:/curator/groups/search.html"));
		} catch (Exception e) {
			String message = e.getClass().toString() + " - " + e.getMessage();
			log.debug(message);
			fail(message);
		}
	}

	@Test
	public final void testSwitchToEditMode() {
		try {
			MockHttpServletRequest request = new MockHttpServletRequest();
			TargetGroup group = manager.loadGroup(15000L);
			GroupsEditorContext context = new GroupsEditorContext(group, false);

			request.getSession().setAttribute(
					TabbedGroupController.EDITOR_CONTEXT, context);
			testInstance.switchToEditMode(request);
			context = testInstance.getEditorContext(request);
			assertTrue(context.isEditMode());

		} catch (Exception e) {
			String message = e.getClass().toString() + " - " + e.getMessage();
			log.debug(message);
			fail(message);
		}
	}

	@Test
	public final void testEditButtonVisiblity() {
		try {

			MockHttpServletRequest request = new MockHttpServletRequest();
			MockHttpServletResponse response = new MockHttpServletResponse();
			DefaultCommand comm = new DefaultCommand();
			comm.setMode(DefaultCommand.MODE_VIEW);
			comm.setTargetGroupOid(15000L);

			TargetGroup aTargetGroup = manager.loadGroup(comm
					.getTargetGroupOid(), true);

			removeAllCurrentUserPrivileges();
			assertFalse(authorityManager.hasPrivilege(aTargetGroup,
					Privilege.CREATE_GROUP));

			BindException aError = new BindException(new DefaultSiteCommand(),
					null);
			ModelAndView mav = testInstance.showForm(request, response, comm,
					aError);

			assertFalse((Boolean) request.getSession().getAttribute(
					Constants.GBL_SESS_CAN_EDIT));

		} catch (Exception e) {
			String message = e.getClass().toString() + " - " + e.getMessage();
			log.debug(message);
			fail(message);
		}
	}

	@Test
	public final void testEditButtonVisiblity1() {
		try {

			MockHttpServletRequest request = new MockHttpServletRequest();
			MockHttpServletResponse response = new MockHttpServletResponse();
			DefaultCommand comm = new DefaultCommand();
			comm.setMode(DefaultCommand.MODE_VIEW);
			comm.setTargetGroupOid(15000L);

			TargetGroup aTargetGroup = manager.loadGroup(comm
					.getTargetGroupOid(), true);

			removeAllCurrentUserPrivileges();
			addCurrentUserPrivilege(Privilege.SCOPE_AGENCY,
					Privilege.CREATE_GROUP);

			assertTrue(authorityManager.hasPrivilege(aTargetGroup,
					Privilege.CREATE_GROUP));

			BindException aError = new BindException(new DefaultSiteCommand(),
					null);
			ModelAndView mav = testInstance.showForm(request, response, comm,
					aError);

			assertTrue((Boolean) request.getSession().getAttribute(
					Constants.GBL_SESS_CAN_EDIT));

		} catch (Exception e) {
			String message = e.getClass().toString() + " - " + e.getMessage();
			log.debug(message);
			fail(message);
		}
	}

	@Test
	public final void testEditButtonVisiblity2() {
		try {
			MockHttpServletRequest request = new MockHttpServletRequest();
			MockHttpServletResponse response = new MockHttpServletResponse();
			DefaultCommand comm = new DefaultCommand();
			comm.setMode(DefaultCommand.MODE_VIEW);
			comm.setTargetGroupOid(15000L);

			TargetGroup aTargetGroup = manager.loadGroup(comm
					.getTargetGroupOid(), true);

			removeCurrentUserPrivilege(Privilege.SCOPE_AGENCY,
					Privilege.CREATE_GROUP);

			assertFalse(authorityManager.hasPrivilege(aTargetGroup,
					Privilege.CREATE_GROUP));

			addCurrentUserPrivilege(Privilege.SCOPE_ALL, Privilege.CREATE_GROUP);

			assertTrue(authorityManager.hasPrivilege(aTargetGroup,
					Privilege.CREATE_GROUP));

			BindException aError = new BindException(new DefaultSiteCommand(),
					null);
			ModelAndView mav = testInstance.showForm(request, response, comm,
					aError);
			// assertTrue(mav != null);
			// assertTrue(mav.getViewName().equals("site"));

			assertTrue((Boolean) request.getSession().getAttribute(
					Constants.GBL_SESS_CAN_EDIT));

		} catch (Exception e) {
			String message = e.getClass().toString() + " - " + e.getMessage();
			log.debug(message);
			fail(message);
		}

	}

}
