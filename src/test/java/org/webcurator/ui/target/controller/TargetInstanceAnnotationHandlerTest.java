package org.webcurator.ui.target.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.webcurator.auth.AuthorityManagerImpl;
import org.webcurator.core.agency.MockAgencyUserManagerImpl;
import org.webcurator.core.harvester.coordinator.HarvestCoordinatorImpl;
import org.webcurator.core.scheduler.MockTargetInstanceManager;
import org.webcurator.core.scheduler.TargetInstanceManager;
import org.webcurator.core.util.AuthUtil;
import org.webcurator.domain.model.core.Annotation;
import org.webcurator.domain.model.core.TargetInstance;
import org.webcurator.test.BaseWCTTest;
import org.webcurator.ui.target.command.TargetAnnotationCommand;
import org.webcurator.ui.target.command.TargetInstanceCommand;
import org.webcurator.ui.target.validator.TargetInstanceValidator;
import org.webcurator.ui.util.Tab;
import org.webcurator.ui.util.TabConfig;
import org.webcurator.ui.util.TabStatus;
import org.webcurator.ui.util.TabbedController;

public class TargetInstanceAnnotationHandlerTest extends BaseWCTTest<TargetInstanceAnnotationHandler>{

	public TargetInstanceAnnotationHandlerTest()
	{
		super(TargetInstanceAnnotationHandler.class,
				"src/test/java/org/webcurator/ui/target/controller/TargetInstanceAnnotationHandlerTest.xml");
		
	}

	private SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss", Locale.UK);

	private final String[][] ANNOTATIONS = {
			{"4", "01-APR-2001 00:00:00"},
			{"2", "01-FEB-2001 00:00:00"},
			{"1", "01-JAN-2001 00:00:00"},
			{"3", "01-MAR-2001 00:00:00"}
	};
	private Annotation createAnnotation(String note, String date) throws ParseException
	{
		Annotation ann = new Annotation();
		
		ann.setDate(sdf.parse(date));
		ann.setNote(note);
		ann.setUser(AuthUtil.getRemoteUserObject());
		return ann;
	}
	
	private List<Annotation> createAnnotationList() throws ParseException
	{
		List<Annotation> list = new ArrayList<Annotation>();
		for(int i = 0; i < ANNOTATIONS.length; i++)
		{
			list.add(createAnnotation(ANNOTATIONS[i][0], ANNOTATIONS[i][1]));
		}
		return list;
	}
	
	private boolean checkSortedList(List<Annotation> list) throws ParseException
	{
		Date lastDate = sdf.parse("01-JAN-2070 00:00:00");
		Annotation[] array = list.toArray(new Annotation[list.size()]);
		assertEquals(array.length, list.size());
		for(int i = 0; i < array.length; i++)
		{
			if(array[i].getDate().after(lastDate))
			{
				return false;
			}
			
			lastDate = array[i].getDate();
		}
		
		return true;
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
		
		Tab tabAnnotations = new Tab();
		tabAnnotations.setCommandClass(TargetAnnotationCommand.class);
		tabAnnotations.setJsp("../target-annotations.jsp");
		tabAnnotations.setPageId("ANNOTATIONS");

		TargetInstanceAnnotationHandler annHandler = new TargetInstanceAnnotationHandler();
		annHandler.setTargetInstanceManager(targetInstanceManager);
		tabAnnotations.setTabHandler(annHandler);
		
		tabs.add(tabAnnotations);
		return tabs;
	}

	@Test
	public final void testPreProcessNextTab() {
	
		try
		{
			HttpServletRequest aReq = new MockHttpServletRequest();
			TargetInstanceManager targetInstanceManager = new MockTargetInstanceManager(testFile);
			testInstance.setTargetInstanceManager(targetInstanceManager);
			TargetInstance targetInstance = targetInstanceManager.getTargetInstance(5000L);
			List<Annotation> list = createAnnotationList(); 
			assertFalse(checkSortedList(list));
			targetInstance.setAnnotations(list);
			aReq.getSession().setAttribute(TargetInstanceCommand.SESSION_TI, targetInstance);
			aReq.getSession().setAttribute(TargetInstanceCommand.SESSION_MODE, true);

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
			aCmd.setCmd(TargetInstanceCommand.ACTION_EDIT);
			BindException aErrors = new BindException(aCmd, aCmd.getCmd());
			
			ModelAndView mav = testInstance.preProcessNextTab(tc, currentTab, aReq, aResp, aCmd, aErrors);
			assertNotNull(mav);
			assertTrue(checkSortedList(((TargetInstance)mav.getModel().get(TargetInstanceCommand.MDL_INSTANCE)).getAnnotations()));
		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}
	
	@Test
	public final void testProcessOther() {
		try
		{
			HttpServletRequest aReq = new MockHttpServletRequest();
			TargetInstanceManager targetInstanceManager = new MockTargetInstanceManager(testFile);
			testInstance.setTargetInstanceManager(targetInstanceManager);
			TargetInstance targetInstance = targetInstanceManager.getTargetInstance(5000L);
			List<Annotation> list = createAnnotationList(); 
			assertFalse(checkSortedList(list));
			targetInstance.setAnnotations(list);
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
			aCmd.setCmd(TargetInstanceCommand.ACTION_ADD_NOTE);
			aCmd.setNote("A note");
			BindException aErrors = new BindException(aCmd, aCmd.getCmd());
			
			int numAnnotations = targetInstance.getAnnotations().size();
			
			ModelAndView mav = testInstance.processOther(tc, currentTab, aReq, aResp, aCmd, aErrors);
			assertTrue(mav != null);
			assertTrue(mav.getViewName().equals("targetInstance"));
			assertTrue(((TabStatus)mav.getModel().get("tabStatus")).getCurrentTab().getPageId().equals("ANNOTATIONS"));
			int noteIndex = 0;
			assertTrue(targetInstance.getAnnotations().size() == (numAnnotations+1));
			assertTrue(targetInstance.getAnnotations().get(noteIndex).getNote().equals("A note"));
			assertTrue(checkSortedList(((TargetInstance)mav.getModel().get(TargetInstanceCommand.MDL_INSTANCE)).getAnnotations()));
			
			currentTab = tabs.get(1);
			aCmd.setCmd(TargetInstanceCommand.ACTION_MODIFY_NOTE);
			aCmd.setNote("A new note");
			aCmd.setNoteIndex(noteIndex);
			aErrors = new BindException(aCmd, aCmd.getCmd());
			
			mav = testInstance.processOther(tc, currentTab, aReq, aResp, aCmd, aErrors);
			assertTrue(mav != null);
			assertTrue(mav.getViewName().equals("targetInstance"));
			assertTrue(((TabStatus)mav.getModel().get("tabStatus")).getCurrentTab().getPageId().equals("ANNOTATIONS"));
			int listSize = targetInstance.getAnnotations().size();
			int newNoteIndex = 0;
			assertTrue(newNoteIndex == noteIndex);
			assertTrue(targetInstance.getAnnotations().size() == (numAnnotations+1));
			assertFalse(targetInstance.getAnnotations().get(noteIndex).getNote().equals("A note"));
			assertTrue(targetInstance.getAnnotations().get(noteIndex).getNote().equals("A new note"));
			assertTrue(checkSortedList(((TargetInstance)mav.getModel().get(TargetInstanceCommand.MDL_INSTANCE)).getAnnotations()));
			
			currentTab = tabs.get(1);
			aCmd.setCmd(TargetInstanceCommand.ACTION_DELETE_NOTE);
			aCmd.setNoteIndex(noteIndex);
			aErrors = new BindException(aCmd, aCmd.getCmd());
			
			mav = testInstance.processOther(tc, currentTab, aReq, aResp, aCmd, aErrors);
			assertTrue(mav != null);
			assertTrue(mav.getViewName().equals("targetInstance"));
			assertTrue(((TabStatus)mav.getModel().get("tabStatus")).getCurrentTab().getPageId().equals("ANNOTATIONS"));
			int newListSize = targetInstance.getAnnotations().size(); 
			assertTrue(newListSize == (listSize-1));
			assertTrue(targetInstance.getAnnotations().size() == numAnnotations);
			assertTrue(checkSortedList(((TargetInstance)mav.getModel().get(TargetInstanceCommand.MDL_INSTANCE)).getAnnotations()));
		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}

	/**
	 * This test ensures that the "processTab" method correctly adds an annotation.
	 * This function is required to support the new prompt for saving the record when
	 * the user clicks "add" for annotations.
	 * Refer to https://sourceforge.net/p/webcurator/enhancements/84/
	 * @throws Exception
	 */
	@Test
	public final void testProcessTab() throws Exception {
		HttpServletRequest aReq = new MockHttpServletRequest();
		TargetInstanceManager targetInstanceManager = new MockTargetInstanceManager(testFile);
		testInstance.setTargetInstanceManager(targetInstanceManager);
		TargetInstance targetInstance = targetInstanceManager.getTargetInstance(5000L);
		List<Annotation> list = createAnnotationList(); 
		assertFalse(checkSortedList(list));
		targetInstance.setAnnotations(list);
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
		aCmd.setCmd(TargetInstanceCommand.ACTION_ADD_NOTE);
		aCmd.setNote("A note");
		BindException aErrors = new BindException(aCmd, aCmd.getCmd());
		
		List<Annotation> resultAnnotations = targetInstance.getAnnotations();
		int numAnnotations = resultAnnotations.size();

		testInstance.processTab(tc, currentTab, aReq, aResp, aCmd, aErrors);
		assertEquals(resultAnnotations.size(), numAnnotations+1);
		Annotation resultAnnotation = resultAnnotations.get(resultAnnotations.size()-1);
		assertEquals("A note", resultAnnotation.getNote());
	}


}
