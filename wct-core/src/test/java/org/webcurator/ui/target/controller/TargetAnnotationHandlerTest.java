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
import org.webcurator.core.targets.MockTargetManager;
import org.webcurator.core.targets.TargetManager;
import org.webcurator.core.util.AuthUtil;
import org.webcurator.domain.MockUserRoleDAO;
import org.webcurator.domain.model.core.Annotation;
import org.webcurator.domain.model.core.Target;
import org.webcurator.test.BaseWCTTest;
import org.webcurator.ui.target.TargetEditorContext;
import org.webcurator.ui.target.command.TargetAnnotationCommand;
import org.webcurator.ui.target.command.TargetGeneralCommand;
import org.webcurator.ui.util.Tab;
import org.webcurator.ui.util.TabConfig;
import org.webcurator.ui.util.TabStatus;
import org.webcurator.ui.util.TabbedController;

public class TargetAnnotationHandlerTest extends BaseWCTTest<TargetAnnotationHandler>{

	public TargetAnnotationHandlerTest()
	{
		super(TargetAnnotationHandler.class,
				"src/test/java/org/webcurator/ui/target/controller/targetannotationhandlertest.xml");
		
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
	
	private List<Tab> getTabList(TargetManager targetManager)
	{
		List<Tab> tabs = new ArrayList<Tab>();
		
		Tab tabGeneral = new Tab();
		tabGeneral.setCommandClass(TargetGeneralCommand.class);
		tabGeneral.setJsp("../target-general.jsp");
		tabGeneral.setPageId("GENERAL");

		TargetGeneralHandler genHandler = new TargetGeneralHandler();
		genHandler.setAgencyUserManager(new MockAgencyUserManagerImpl(testFile));
		genHandler.setAuthorityManager(new AuthorityManagerImpl());
		genHandler.setTargetManager(targetManager);
		genHandler.setUserRoleDao(new MockUserRoleDAO(testFile));
		tabGeneral.setTabHandler(genHandler);
		
		tabs.add(tabGeneral);
		
		Tab tabAnnotations = new Tab();
		tabAnnotations.setCommandClass(TargetAnnotationCommand.class);
		tabAnnotations.setJsp("../target-annotations.jsp");
		tabAnnotations.setPageId("ANNOTATIONS");

		TargetAnnotationHandler annHandler = new TargetAnnotationHandler();
		annHandler.setTargetManager(targetManager);
		tabAnnotations.setTabHandler(annHandler);
		
		tabs.add(tabAnnotations);
		return tabs;
	}

	@Test
	public final void testPreProcessNextTab() {
	
		try
		{
			HttpServletRequest aReq = new MockHttpServletRequest();
			TargetManager targetManager = new MockTargetManager(testFile);
			testInstance.setTargetManager(targetManager);
			Target target = targetManager.load(4000L);
			List<Annotation> list = createAnnotationList(); 
			assertFalse(checkSortedList(list));
			target.setAnnotations(list);
			TargetEditorContext ctx = new TargetEditorContext(targetManager,
																target,
																true);
			aReq.getSession().setAttribute(TabbedTargetController.EDITOR_CONTEXT, ctx);
			HttpServletResponse aResp = new MockHttpServletResponse(); 
			TargetAnnotationCommand aCmd = new TargetAnnotationCommand();
			TabbedController tc = new TabbedTargetController();
	
			TabConfig tabConfig = new TabConfig();
			tabConfig.setViewName("target");
			List<Tab> tabs = getTabList(targetManager);
			tabConfig.setTabs(tabs);
	
			tc.setTabConfig(tabConfig);
			tc.setDefaultCommandClass(org.webcurator.ui.target.command.TargetDefaultCommand.class);
			
			Tab currentTab = tabs.get(1);
			BindException aErrors = new BindException(aCmd, aCmd.getActionCmd());
			
			ModelAndView mav = testInstance.preProcessNextTab(tc, currentTab, aReq, aResp, aCmd, aErrors);
			assertNotNull(mav);
			assertTrue(checkSortedList((List<Annotation>)mav.getModel().get("annotations")));
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
			TargetManager targetManager = new MockTargetManager(testFile);
			testInstance.setTargetManager(targetManager);
			Target target = targetManager.load(4000L);
			List<Annotation> list = createAnnotationList(); 
			assertFalse(checkSortedList(list));
			target.setAnnotations(list);
			TargetEditorContext ctx = new TargetEditorContext(targetManager,
																target,
																true);
			aReq.getSession().setAttribute(TabbedTargetController.EDITOR_CONTEXT, ctx);
			HttpServletResponse aResp = new MockHttpServletResponse(); 
			TargetAnnotationCommand aCmd = new TargetAnnotationCommand();
			TabbedController tc = new TabbedTargetController();
	
			TabConfig tabConfig = new TabConfig();
			tabConfig.setViewName("target");
			List<Tab> tabs = getTabList(targetManager);
			tabConfig.setTabs(tabs);
	
			tc.setTabConfig(tabConfig);
			tc.setDefaultCommandClass(org.webcurator.ui.target.command.TargetDefaultCommand.class);
			
			Tab currentTab = tabs.get(1);
			aCmd.setActionCmd(TargetAnnotationCommand.ACTION_ADD_NOTE);
			aCmd.setNote("A note");
			BindException aErrors = new BindException(aCmd, aCmd.getActionCmd());
			
			int numAnnotations = target.getAnnotations().size();
			
			ModelAndView mav = testInstance.processOther(tc, currentTab, aReq, aResp, aCmd, aErrors);
			assertTrue(mav != null);
			assertTrue(mav.getViewName().equals("target"));
			assertTrue(((TabStatus)mav.getModel().get("tabStatus")).getCurrentTab().getPageId().equals("ANNOTATIONS"));
			int listSize = ((List<Annotation>)mav.getModel().get("annotations")).size(); 
			assertTrue(listSize > 0);
			int noteIndex = 0;
			assertTrue(checkSortedList((List<Annotation>)mav.getModel().get("annotations")));
			assertTrue(((List<Annotation>)mav.getModel().get("annotations")).get(noteIndex).getNote().equals("A note"));
			assertTrue(target.getAnnotations().size() == (numAnnotations+1));
			assertTrue(target.getAnnotations().get(noteIndex).getNote().equals("A note"));
			
			currentTab = tabs.get(1);
			aCmd.setActionCmd(TargetAnnotationCommand.ACTION_MODIFY_NOTE);
			aCmd.setNote("A new note");
			aCmd.setNoteIndex(noteIndex);
			aErrors = new BindException(aCmd, aCmd.getActionCmd());
			
			mav = testInstance.processOther(tc, currentTab, aReq, aResp, aCmd, aErrors);
			assertTrue(mav != null);
			assertTrue(mav.getViewName().equals("target"));
			assertTrue(((TabStatus)mav.getModel().get("tabStatus")).getCurrentTab().getPageId().equals("ANNOTATIONS"));
			assertTrue(checkSortedList((List<Annotation>)mav.getModel().get("annotations")));
			listSize = ((List<Annotation>)mav.getModel().get("annotations")).size(); 
			assertTrue(listSize > 0);
			int newNoteIndex = 0;
			assertTrue(newNoteIndex == noteIndex);
			assertFalse(((List<Annotation>)mav.getModel().get("annotations")).get(noteIndex).getNote().equals("A note"));
			assertTrue(((List<Annotation>)mav.getModel().get("annotations")).get(noteIndex).getNote().equals("A new note"));
			assertTrue(target.getAnnotations().size() == (numAnnotations+1));
			assertFalse(target.getAnnotations().get(noteIndex).getNote().equals("A note"));
			assertTrue(target.getAnnotations().get(noteIndex).getNote().equals("A new note"));
			
			currentTab = tabs.get(1);
			aCmd.setActionCmd(TargetAnnotationCommand.ACTION_DELETE_NOTE);
			aCmd.setNoteIndex(noteIndex);
			aErrors = new BindException(aCmd, aCmd.getActionCmd());
			
			mav = testInstance.processOther(tc, currentTab, aReq, aResp, aCmd, aErrors);
			assertTrue(mav != null);
			assertTrue(mav.getViewName().equals("target"));
			assertTrue(((TabStatus)mav.getModel().get("tabStatus")).getCurrentTab().getPageId().equals("ANNOTATIONS"));
			assertTrue(checkSortedList((List<Annotation>)mav.getModel().get("annotations")));
			int newListSize = ((List<Annotation>)mav.getModel().get("annotations")).size(); 
			assertTrue(newListSize == (listSize-1));
			assertTrue(target.getAnnotations().size() == numAnnotations);
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
		TargetManager targetManager = new MockTargetManager(testFile);
		testInstance.setTargetManager(targetManager);
		Target target = targetManager.load(4000L);
		List<Annotation> list = createAnnotationList(); 
		assertFalse(checkSortedList(list));
		target.setAnnotations(list);
		TargetEditorContext ctx = new TargetEditorContext(targetManager,
				target,
				true);
		aReq.getSession().setAttribute(TabbedTargetController.EDITOR_CONTEXT, ctx);
		HttpServletResponse aResp = new MockHttpServletResponse(); 
		TargetAnnotationCommand aCmd = new TargetAnnotationCommand();
		TabbedController tc = new TabbedTargetController();

		TabConfig tabConfig = new TabConfig();
		tabConfig.setViewName("target");
		List<Tab> tabs = getTabList(targetManager);
		tabConfig.setTabs(tabs);

		tc.setTabConfig(tabConfig);
		tc.setDefaultCommandClass(org.webcurator.ui.target.command.TargetDefaultCommand.class);

		Tab currentTab = tabs.get(1);
		aCmd.setActionCmd(TargetAnnotationCommand.ACTION_ADD_NOTE);
		aCmd.setNote("A note");
		BindException aErrors = new BindException(aCmd, aCmd.getActionCmd());

		List<Annotation> resultAnnotations = target.getAnnotations();
		int numAnnotations = resultAnnotations.size();

		testInstance.processTab(tc, currentTab, aReq, aResp, aCmd, aErrors);
		assertEquals(resultAnnotations.size(), numAnnotations+1);
		Annotation resultAnnotation = resultAnnotations.get(resultAnnotations.size()-1);
		assertEquals("A note", resultAnnotation.getNote());
	}
}
