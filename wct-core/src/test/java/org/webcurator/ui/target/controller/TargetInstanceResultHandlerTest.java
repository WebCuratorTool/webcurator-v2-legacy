package org.webcurator.ui.target.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.webcurator.auth.AuthorityManagerImpl;
import org.webcurator.core.agency.MockAgencyUserManagerImpl;
import org.webcurator.core.exceptions.DigitalAssetStoreException;
import org.webcurator.core.harvester.coordinator.HarvestCoordinatorImpl;
import org.webcurator.core.harvester.coordinator.MockHarvestCoordinator;
import org.webcurator.core.scheduler.MockTargetInstanceManager;
import org.webcurator.core.scheduler.TargetInstanceManager;
import org.webcurator.core.store.MockDigitalAssetStore;
import org.webcurator.domain.model.core.CustomDepositFormCriteriaDTO;
import org.webcurator.domain.model.core.CustomDepositFormResultDTO;
import org.webcurator.domain.model.core.HarvestResult;
import org.webcurator.domain.model.core.TargetInstance;
import org.webcurator.test.BaseWCTTest;
import org.webcurator.ui.target.command.TargetInstanceCommand;
import org.webcurator.ui.target.validator.TargetInstanceValidator;
import org.webcurator.ui.util.Tab;
import org.webcurator.ui.util.TabConfig;
import org.webcurator.ui.util.TabStatus;
import org.webcurator.ui.util.TabbedController;
import org.webcurator.ui.util.TabbedController.TabbedModelAndView;

public class TargetInstanceResultHandlerTest extends BaseWCTTest<TargetInstanceResultHandler>{

	public TargetInstanceResultHandlerTest()
	{
		super(TargetInstanceResultHandler.class,
				"src/test/java/org/webcurator/ui/target/controller/targetinstanceresulthandlertest.xml");
		
	}
	
	
	private List<Tab> getTabList(TargetInstanceManager targetInstanceManager)
	{
		List<Tab> tabs = new ArrayList<Tab>();
		
		Tab tabGeneral = new Tab();
		tabGeneral.setCommandClass(TargetInstanceCommand.class);
		tabGeneral.setJsp("../target-instance-general.jsp");
		tabGeneral.setPageId("GENERAL");
		tabGeneral.setTitle("general");
		tabGeneral.setValidator(new TargetInstanceValidator());

		TargetInstanceGeneralHandler genHandler = new TargetInstanceGeneralHandler();
		genHandler.setAgencyUserManager(new MockAgencyUserManagerImpl(testFile));
		genHandler.setAuthorityManager(new AuthorityManagerImpl());
		genHandler.setTargetInstanceManager(targetInstanceManager);
		genHandler.setHarvestCoordinator(new HarvestCoordinatorImpl());
		tabGeneral.setTabHandler(genHandler);
		
		tabs.add(tabGeneral);
		
		Tab tabResults = new Tab();
		tabResults.setCommandClass(TargetInstanceCommand.class);
		tabResults.setJsp("../../target-instance-results.jsp");
		tabResults.setPageId("RESULTS");

		TargetInstanceResultHandler resHandler = new TargetInstanceResultHandler();
		resHandler.setTargetInstanceManager(targetInstanceManager);
		tabResults.setTabHandler(resHandler);
		
		tabs.add(tabResults);
		return tabs;
	}
	

	@Test
	public final void testProcessOther() {
		HttpServletRequest aReq = new MockHttpServletRequest();
		MockTargetInstanceManager targetInstanceManager = new MockTargetInstanceManager(testFile);
		MockHarvestCoordinator coordinator = new MockHarvestCoordinator();
		coordinator.setTargetInstanceDao(targetInstanceManager.getTargetInstanceDAO());
		MockDigitalAssetStore digitalAssetStore = new MockDigitalAssetStore() {
			public CustomDepositFormResultDTO getCustomDepositFormDetails(CustomDepositFormCriteriaDTO criteria) throws DigitalAssetStoreException {
				CustomDepositFormResultDTO result = new CustomDepositFormResultDTO();
				result.setCustomDepositFormRequired(true);
				result.setHTMLForCustomDepositForm("an html");
				result.setUrlForCustomDepositForm("http://some.host.natlib.govt.nz/aURL");
				return result;
			}
		};

		testInstance.setTargetInstanceManager(targetInstanceManager);
		testInstance.setHarvestCoordinator(coordinator);
		testInstance.setDigitalAssetStore(digitalAssetStore);
		testInstance.setAgencyUserManager(new MockAgencyUserManagerImpl(testFile));
		TargetInstance targetInstance = targetInstanceManager.getTargetInstance(5000L);
		List<HarvestResult> results = targetInstanceManager.getHarvestResults(targetInstance.getOid());
		HarvestResult result = results.get(0);
		assertNotNull(result);
		
		aReq.getSession().setAttribute(TargetInstanceCommand.SESSION_TI, targetInstance);

		HttpServletResponse aResp = new MockHttpServletResponse(); 
		TabbedController tc = new TabbedTargetInstanceController();

		TabConfig tabConfig = new TabConfig();
		tabConfig.setViewName("targetInstance");
		List<Tab> tabs = getTabList(targetInstanceManager);
		tabConfig.setTabs(tabs);

		tc.setTabConfig(tabConfig);
		tc.setDefaultCommandClass(org.webcurator.ui.target.command.TargetInstanceCommand.class);
		
		Tab currentTab = tabs.get(1);

		//Endorse
		TargetInstanceCommand aCmd = new TargetInstanceCommand();
		aCmd.setCmd(TargetInstanceCommand.ACTION_ENDORSE);
		aCmd.setHarvestResultId(result.getOid());
		BindException aErrors = new BindException(aCmd, aCmd.getCmd());
		
		ModelAndView mav = testInstance.processOther(tc, currentTab, aReq, aResp, aCmd, aErrors);
		assertTrue(mav != null);
		assertTrue(mav.getViewName().equals("targetInstance"));
		assertTrue(((TabStatus)mav.getModel().get("tabStatus")).getCurrentTab().getPageId().equals("RESULTS"));
		assertEquals(targetInstance.getState(), TargetInstance.STATE_ENDORSED);
		assertEquals(result.getState(), HarvestResult.STATE_ENDORSED);
		assertNull(targetInstance.getArchivedTime());
		// Verify that the buildCustomDepositFormDetails() is called
		assertEquals(true, mav.getModel().get("customDepositFormRequired"));
		assertEquals("http://some.host.natlib.govt.nz/aURL", aReq.getSession().getAttribute("customDepositFormURL"));
		assertEquals("an html", aReq.getSession().getAttribute("customDepositFormHTMLContent"));
		List<HarvestResult> removedIndexes = coordinator.getRemovedIndexes();
		assertEquals(2,removedIndexes.size());
		removedIndexes.clear();
		
		//Unendorse
		aCmd = new TargetInstanceCommand();
		aCmd.setCmd(TargetInstanceCommand.ACTION_UNENDORSE);
		aCmd.setHarvestResultId(result.getOid());
		aErrors = new BindException(aCmd, aCmd.getCmd());
		
		mav = testInstance.processOther(tc, currentTab, aReq, aResp, aCmd, aErrors);
		assertTrue(mav != null);
		assertTrue(mav.getViewName().equals("targetInstance"));
		assertTrue(((TabStatus)mav.getModel().get("tabStatus")).getCurrentTab().getPageId().equals("RESULTS"));
		assertEquals(targetInstance.getState(),TargetInstance.STATE_HARVESTED);
		assertEquals(result.getState(), 0);
		assertNull(targetInstance.getArchivedTime());
		removedIndexes = coordinator.getRemovedIndexes();
		assertEquals(0,removedIndexes.size());
		removedIndexes.clear();
	}

	@Test
	public final void testProcessOtherReject() {
		HttpServletRequest aReq = new MockHttpServletRequest();
		MockTargetInstanceManager targetInstanceManager = new MockTargetInstanceManager(testFile);
		MockHarvestCoordinator coordinator = new MockHarvestCoordinator();
		coordinator.setTargetInstanceDao(targetInstanceManager.getTargetInstanceDAO());

		testInstance.setTargetInstanceManager(targetInstanceManager);
		testInstance.setHarvestCoordinator(coordinator);
		testInstance.setAgencyUserManager(new MockAgencyUserManagerImpl(testFile));
		TargetInstance targetInstance = targetInstanceManager.getTargetInstance(5000L);
		List<HarvestResult> results = targetInstanceManager.getHarvestResults(targetInstance.getOid());
		//Leave only one result
		for(int i = results.size()-1; i > 0; i--)
		{
			results.remove(i);
		}
		assertEquals(1, results.size());
		
		HarvestResult result = results.get(0);
		assertNotNull(result);
		
		aReq.getSession().setAttribute(TargetInstanceCommand.SESSION_TI, targetInstance);

		HttpServletResponse aResp = new MockHttpServletResponse(); 
		TargetInstanceCommand aCmd = new TargetInstanceCommand();
		TabbedController tc = new TabbedTargetInstanceController();

		TabConfig tabConfig = new TabConfig();
		tabConfig.setViewName("targetInstance");
		List<Tab> tabs = getTabList(targetInstanceManager);
		tabConfig.setTabs(tabs);

		tc.setTabConfig(tabConfig);
		tc.setDefaultCommandClass(org.webcurator.ui.target.command.TargetInstanceCommand.class);
		
		Tab currentTab = tabs.get(1);
		aCmd.setCmd(TargetInstanceCommand.ACTION_REJECT);
		aCmd.setHarvestResultId(result.getOid());
		aCmd.setRejReasonId(1L);
		BindException aErrors = new BindException(aCmd, aCmd.getCmd());
		
		ModelAndView mav = testInstance.processOther(tc, currentTab, aReq, aResp, aCmd, aErrors);
		assertTrue(mav != null);
		assertTrue(mav.getViewName().equals("targetInstance"));
		assertTrue(((TabStatus)mav.getModel().get("tabStatus")).getCurrentTab().getPageId().equals("RESULTS"));
		assertEquals(targetInstance.getState(), TargetInstance.STATE_REJECTED);
		assertEquals(result.getState(), HarvestResult.STATE_REJECTED);
		assertNotNull(targetInstance.getArchivedTime());
		List<HarvestResult> removedIndexes = coordinator.getRemovedIndexes();
		assertEquals(1,removedIndexes.size());
		removedIndexes.clear();
		
	}
	
	@Test
	public final void testProcessOtherRejectNoReasons() {
		HttpServletRequest aReq = new MockHttpServletRequest();
		MockTargetInstanceManager targetInstanceManager = new MockTargetInstanceManager(testFile);
		MockHarvestCoordinator coordinator = new MockHarvestCoordinator();
		coordinator.setTargetInstanceDao(targetInstanceManager.getTargetInstanceDAO());

		testInstance.setTargetInstanceManager(targetInstanceManager);
		testInstance.setHarvestCoordinator(coordinator);
		testInstance.setAgencyUserManager(new MockAgencyUserManagerImpl(testFile));
		TargetInstance targetInstance = targetInstanceManager.getTargetInstance(5000L);
		List<HarvestResult> results = targetInstanceManager.getHarvestResults(targetInstance.getOid());
		//Leave only one result
		for(int i = results.size()-1; i > 0; i--)
		{
			results.remove(i);
		}
		assertEquals(1, results.size());
		
		HarvestResult result = results.get(0);
		assertNotNull(result);
		
		aReq.getSession().setAttribute(TargetInstanceCommand.SESSION_TI, targetInstance);

		HttpServletResponse aResp = new MockHttpServletResponse(); 
		TargetInstanceCommand aCmd = new TargetInstanceCommand();
		TabbedController tc = new TabbedTargetInstanceController();

		TabConfig tabConfig = new TabConfig();
		tabConfig.setViewName("targetInstance");
		List<Tab> tabs = getTabList(targetInstanceManager);
		tabConfig.setTabs(tabs);

		tc.setTabConfig(tabConfig);
		tc.setDefaultCommandClass(org.webcurator.ui.target.command.TargetInstanceCommand.class);
		
		Tab currentTab = tabs.get(1);
		aCmd.setCmd(TargetInstanceCommand.ACTION_REJECT);
		aCmd.setHarvestResultId(result.getOid());
		BindException aErrors = new BindException(aCmd, aCmd.getCmd());
		
		ModelAndView mav = testInstance.processOther(tc, currentTab, aReq, aResp, aCmd, aErrors);
		assertTrue(mav != null);
		assertTrue(mav.getViewName().equals("targetInstance"));
		assertTrue(((TabStatus)mav.getModel().get("tabStatus")).getCurrentTab().getPageId().equals("RESULTS"));
		assertEquals(TargetInstance.STATE_SCHEDULED, targetInstance.getState());
		assertEquals(HarvestResult.STATE_INDEXING, result.getState());
		assertNull(targetInstance.getArchivedTime());
		List<HarvestResult> removedIndexes = coordinator.getRemovedIndexes();
		assertEquals(0,removedIndexes.size());
		removedIndexes.clear();
		
	}
	
	@Test
	public final void testProcessOtherReindex() {
		HttpServletRequest aReq = new MockHttpServletRequest();
		MockTargetInstanceManager targetInstanceManager = new MockTargetInstanceManager(testFile);
		MockHarvestCoordinator coordinator = new MockHarvestCoordinator();
		coordinator.setTargetInstanceDao(targetInstanceManager.getTargetInstanceDAO());

		testInstance.setTargetInstanceManager(targetInstanceManager);
		testInstance.setHarvestCoordinator(coordinator);
		testInstance.setAgencyUserManager(new MockAgencyUserManagerImpl(testFile));
		
		TargetInstance targetInstance = targetInstanceManager.getTargetInstance(5000L);
		List<HarvestResult> results = targetInstanceManager.getHarvestResults(targetInstance.getOid());
		HarvestResult result = results.get(0);
		assertNotNull(result);
		
		aReq.getSession().setAttribute(TargetInstanceCommand.SESSION_TI, targetInstance);

		HttpServletResponse aResp = new MockHttpServletResponse(); 
		TabbedController tc = new TabbedTargetInstanceController();

		TabConfig tabConfig = new TabConfig();
		tabConfig.setViewName("targetInstance");
		List<Tab> tabs = getTabList(targetInstanceManager);
		tabConfig.setTabs(tabs);

		tc.setTabConfig(tabConfig);
		tc.setDefaultCommandClass(org.webcurator.ui.target.command.TargetInstanceCommand.class);
		
		Tab currentTab = tabs.get(1);
		
		//Reindex successful
		TargetInstanceCommand aCmd = new TargetInstanceCommand();
		aCmd.setCmd(TargetInstanceCommand.ACTION_REINDEX);
		aCmd.setHarvestResultId(result.getOid());
		coordinator.setReIndexHarvestResultReturnValue(true);
		result.setState(HarvestResult.STATE_INDEXING);
		BindException aErrors = new BindException(aCmd, aCmd.getCmd());
		
		ModelAndView mav = testInstance.processOther(tc, currentTab, aReq, aResp, aCmd, aErrors);
		assertTrue(mav != null);
		assertTrue(mav.getViewName().equals("targetInstance"));
		assertTrue(((TabStatus)mav.getModel().get("tabStatus")).getCurrentTab().getPageId().equals("RESULTS"));
		assertEquals(targetInstance.getState(),TargetInstance.STATE_HARVESTED);
		assertEquals(result.getState(), HarvestResult.STATE_ABORTED);
		assertFalse(aErrors.hasErrors());

		//Reindex unsuccessful
		aCmd = new TargetInstanceCommand();
		aCmd.setCmd(TargetInstanceCommand.ACTION_REINDEX);
		aCmd.setHarvestResultId(result.getOid());
		coordinator.setReIndexHarvestResultReturnValue(false);
		result.setState(HarvestResult.STATE_INDEXING);
		aErrors = new BindException(aCmd, aCmd.getCmd());
		
		mav = testInstance.processOther(tc, currentTab, aReq, aResp, aCmd, aErrors);
		assertTrue(mav != null);
		assertTrue(mav.getViewName().equals("targetInstance"));
		assertTrue(((TabStatus)mav.getModel().get("tabStatus")).getCurrentTab().getPageId().equals("RESULTS"));
		assertEquals(targetInstance.getState(),TargetInstance.STATE_HARVESTED);
		assertEquals(result.getState(), HarvestResult.STATE_INDEXING);
		assertTrue(aErrors.hasErrors());

		//Reindex not tried due to not in indexing state
		aCmd = new TargetInstanceCommand();
		aCmd.setCmd(TargetInstanceCommand.ACTION_REINDEX);
		aCmd.setHarvestResultId(result.getOid());
		coordinator.setReIndexHarvestResultReturnValue(false);
		result.setState(0);
		aErrors = new BindException(aCmd, aCmd.getCmd());
		
		mav = testInstance.processOther(tc, currentTab, aReq, aResp, aCmd, aErrors);
		assertTrue(mav != null);
		assertTrue(mav.getViewName().equals("targetInstance"));
		assertTrue(((TabStatus)mav.getModel().get("tabStatus")).getCurrentTab().getPageId().equals("RESULTS"));
		assertEquals(targetInstance.getState(),TargetInstance.STATE_HARVESTED);
		assertEquals(result.getState(), 0);
		assertFalse(aErrors.hasErrors());
		List<HarvestResult> removedIndexes = coordinator.getRemovedIndexes();
		assertEquals(0,removedIndexes.size());
		removedIndexes.clear();
	}

	@Test
	public final void testBuildCustomDepositFormDetails_variousConditions() {
		MockTargetInstanceManager targetInstanceManager;
		MockHttpServletRequest aReq;
		targetInstanceManager = new MockTargetInstanceManager(testFile);
		TargetInstance targetInstance = targetInstanceManager.getTargetInstance(5000L);
		TabbedModelAndView mav;
		MockDigitalAssetStore digitalAssetStore;

		// Harvest is in ENDORSED state and the DAS returns a valid response DTO
		digitalAssetStore = new MockDigitalAssetStore() {
			public CustomDepositFormResultDTO getCustomDepositFormDetails(CustomDepositFormCriteriaDTO criteria) throws DigitalAssetStoreException {
				CustomDepositFormResultDTO result = new CustomDepositFormResultDTO();
				result.setCustomDepositFormRequired(true);
				result.setHTMLForCustomDepositForm("an html");
				result.setUrlForCustomDepositForm("http://some.host.natlib.govt.nz/aURL");
				return result;
			}
		};
		targetInstance.setState(TargetInstance.STATE_ENDORSED);
		testInstance.setDigitalAssetStore(digitalAssetStore);
		testInstance.setAgencyUserManager(new MockAgencyUserManagerImpl(testFile));
		mav = mockTabbedModelAndView(targetInstanceManager);
		aReq = new MockHttpServletRequest();
		testInstance.buildCustomDepositFormDetails(aReq, null, targetInstance, mav);
		assertEquals(true, mav.getModel().get("customDepositFormRequired"));
		assertEquals("http://some.host.natlib.govt.nz/aURL", aReq.getSession().getAttribute("customDepositFormURL"));
		assertEquals("an html", aReq.getSession().getAttribute("customDepositFormHTMLContent"));

		// Harvest is in a state other than ENDORSED
		targetInstance.setState(TargetInstance.STATE_ARCHIVED);
		mav = mockTabbedModelAndView(targetInstanceManager);
		aReq = new MockHttpServletRequest();
		testInstance.buildCustomDepositFormDetails(aReq, null, targetInstance, mav);
		assertEquals(false, mav.getModel().get("customDepositFormRequired"));
		assertNull(aReq.getSession().getAttribute("customDepositFormURL"));
		assertNull(aReq.getSession().getAttribute("customDepositFormHTMLContent"));

		// Harvest is in ENDORSED state and the DAS returns a DTO which says no to custom forms
		targetInstance.setState(TargetInstance.STATE_ENDORSED);
		digitalAssetStore = new MockDigitalAssetStore();
		testInstance.setDigitalAssetStore(digitalAssetStore);
		mav = mockTabbedModelAndView(targetInstanceManager);
		aReq = new MockHttpServletRequest();
		testInstance.buildCustomDepositFormDetails(aReq, null, targetInstance, mav);
		assertEquals(false, mav.getModel().get("customDepositFormRequired"));
		assertNull(aReq.getSession().getAttribute("customDepositFormURL"));
		assertNull(aReq.getSession().getAttribute("customDepositFormHTMLContent"));
	}

	private TabbedModelAndView mockTabbedModelAndView(MockTargetInstanceManager targetInstanceManager) {
		TabbedController tc = new TabbedTargetInstanceController();
		TabConfig tabConfig = new TabConfig();
		tabConfig.setViewName("targetInstance");
		List<Tab> tabs = getTabList(targetInstanceManager);
		tabConfig.setTabs(tabs);
		tc.setTabConfig(tabConfig);
		tc.setDefaultCommandClass(org.webcurator.ui.target.command.TargetInstanceCommand.class);
		return tc.new TabbedModelAndView();
	}
}
