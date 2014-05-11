package org.webcurator.ui.groups.controller;

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
import org.webcurator.domain.model.core.Annotation;
import org.webcurator.domain.model.core.TargetGroup;
import org.webcurator.test.BaseWCTTest;
import org.webcurator.ui.groups.GroupsEditorContext;
import org.webcurator.ui.groups.command.GeneralCommand;
import org.webcurator.ui.groups.command.GroupAnnotationCommand;
import org.webcurator.ui.groups.validator.GeneralValidator;
import org.webcurator.ui.groups.validator.GroupAnnotationValidator;
import org.webcurator.ui.util.Tab;
import org.webcurator.ui.util.TabConfig;
import org.webcurator.ui.util.TabStatus;
import org.webcurator.ui.util.TabbedController;

public class GroupAnnotationHandlerTest extends BaseWCTTest<GroupAnnotationHandler>{

	public GroupAnnotationHandlerTest()
	{
		super(GroupAnnotationHandler.class,
				"src/test/java/org/webcurator/ui/groups/controller/groupannotationhandlertest.xml");
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
		tabGeneral.setCommandClass(GeneralCommand.class);
		tabGeneral.setJsp("../groups-general.jsp");
		tabGeneral.setPageId("GENERAL");
		tabGeneral.setValidator(new GeneralValidator());

		GeneralHandler genHandler = new GeneralHandler();
		genHandler.setAgencyUserManager(new MockAgencyUserManagerImpl(testFile));
		genHandler.setAuthorityManager(new AuthorityManagerImpl());
		tabGeneral.setTabHandler(genHandler);
		
		tabs.add(tabGeneral);
		
		Tab tabAnnotations = new Tab();
		tabAnnotations.setCommandClass(GroupAnnotationCommand.class);
		tabAnnotations.setJsp("../group-annotations.jsp");
		tabAnnotations.setPageId("ANNOTATIONS");
		tabGeneral.setValidator(new GroupAnnotationValidator());

		GroupAnnotationHandler annHandler = new GroupAnnotationHandler();
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
			TargetGroup targetGroup = targetManager.loadGroup(15000L);
			List<Annotation> list = createAnnotationList(); 
			assertFalse(checkSortedList(list));
			targetGroup.setAnnotations(list);
			GroupsEditorContext ctx = new GroupsEditorContext(targetGroup, true);
			aReq.getSession().setAttribute(TabbedGroupController.EDITOR_CONTEXT, ctx);
			HttpServletResponse aResp = new MockHttpServletResponse(); 
			GroupAnnotationCommand aCmd = new GroupAnnotationCommand();
			TabbedController tc = new TabbedGroupController();

			TabConfig tabConfig = new TabConfig();
			tabConfig.setViewName("groups");
			List<Tab> tabs = getTabList(targetManager);
			tabConfig.setTabs(tabs);

			tc.setTabConfig(tabConfig);
			
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
			TargetGroup targetGroup = targetManager.loadGroup(15000L);
			List<Annotation> list = createAnnotationList(); 
			assertFalse(checkSortedList(list));
			targetGroup.setAnnotations(list);
			GroupsEditorContext ctx = new GroupsEditorContext(targetGroup, true);
			aReq.getSession().setAttribute(TabbedGroupController.EDITOR_CONTEXT, ctx);
			HttpServletResponse aResp = new MockHttpServletResponse(); 
			GroupAnnotationCommand aCmd = new GroupAnnotationCommand();
			TabbedController tc = new TabbedGroupController();
	
			TabConfig tabConfig = new TabConfig();
			tabConfig.setViewName("groups");
			List<Tab> tabs = getTabList(targetManager);
			tabConfig.setTabs(tabs);
	
			tc.setTabConfig(tabConfig);
			
			Tab currentTab = tabs.get(1);
			aCmd.setActionCmd(GroupAnnotationCommand.ACTION_ADD_NOTE);
			aCmd.setNote("A note");
			BindException aErrors = new BindException(aCmd, aCmd.getActionCmd());
			
			int numAnnotations = targetGroup.getAnnotations().size();
			
			ModelAndView mav = testInstance.processOther(tc, currentTab, aReq, aResp, aCmd, aErrors);
			assertTrue(mav != null);
			assertTrue(mav.getViewName().equals("groups"));
			assertTrue(((TabStatus)mav.getModel().get("tabStatus")).getCurrentTab().getPageId().equals("ANNOTATIONS"));
			assertTrue(checkSortedList((List<Annotation>)mav.getModel().get("annotations")));
			int listSize = ((List<Annotation>)mav.getModel().get("annotations")).size(); 
			assertTrue(listSize > 0);
			int noteIndex = 0;
			assertTrue(((List<Annotation>)mav.getModel().get("annotations")).get(noteIndex).getNote().equals("A note"));
			assertTrue(targetGroup.getAnnotations().size() == (numAnnotations+1));
			assertTrue(targetGroup.getAnnotations().get(noteIndex).getNote().equals("A note"));
			
			currentTab = tabs.get(1);
			aCmd.setActionCmd(GroupAnnotationCommand.ACTION_MODIFY_NOTE);
			aCmd.setNote("A new note");
			aCmd.setNoteIndex(noteIndex);
			aErrors = new BindException(aCmd, aCmd.getActionCmd());
			
			mav = testInstance.processOther(tc, currentTab, aReq, aResp, aCmd, aErrors);
			assertTrue(mav != null);
			assertTrue(mav.getViewName().equals("groups"));
			assertTrue(((TabStatus)mav.getModel().get("tabStatus")).getCurrentTab().getPageId().equals("ANNOTATIONS"));
			assertTrue(checkSortedList((List<Annotation>)mav.getModel().get("annotations")));
			listSize = ((List<Annotation>)mav.getModel().get("annotations")).size(); 
			assertTrue(listSize > 0);
			int newNoteIndex = 0;
			assertTrue(newNoteIndex == noteIndex);
			assertFalse(((List<Annotation>)mav.getModel().get("annotations")).get(noteIndex).getNote().equals("A note"));
			assertTrue(((List<Annotation>)mav.getModel().get("annotations")).get(noteIndex).getNote().equals("A new note"));
			assertTrue(targetGroup.getAnnotations().size() == (numAnnotations+1));
			assertFalse(targetGroup.getAnnotations().get(noteIndex).getNote().equals("A note"));
			assertTrue(targetGroup.getAnnotations().get(noteIndex).getNote().equals("A new note"));
			
			currentTab = tabs.get(1);
			aCmd.setActionCmd(GroupAnnotationCommand.ACTION_DELETE_NOTE);
			aCmd.setNoteIndex(noteIndex);
			aErrors = new BindException(aCmd, aCmd.getActionCmd());
			
			mav = testInstance.processOther(tc, currentTab, aReq, aResp, aCmd, aErrors);
			assertTrue(mav != null);
			assertTrue(mav.getViewName().equals("groups"));
			assertTrue(((TabStatus)mav.getModel().get("tabStatus")).getCurrentTab().getPageId().equals("ANNOTATIONS"));
			assertTrue(checkSortedList((List<Annotation>)mav.getModel().get("annotations")));
			int newListSize = ((List<Annotation>)mav.getModel().get("annotations")).size(); 
			assertTrue(newListSize == (listSize-1));
			assertTrue(targetGroup.getAnnotations().size() == numAnnotations);
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
		TargetGroup targetGroup = targetManager.loadGroup(15000L);
		List<Annotation> list = createAnnotationList(); 
		assertFalse(checkSortedList(list));
		targetGroup.setAnnotations(list);
		GroupsEditorContext ctx = new GroupsEditorContext(targetGroup, true);
		aReq.getSession().setAttribute(TabbedGroupController.EDITOR_CONTEXT, ctx);
		HttpServletResponse aResp = new MockHttpServletResponse(); 
		GroupAnnotationCommand aCmd = new GroupAnnotationCommand();
		TabbedController tc = new TabbedGroupController();

		TabConfig tabConfig = new TabConfig();
		tabConfig.setViewName("groups");
		List<Tab> tabs = getTabList(targetManager);
		tabConfig.setTabs(tabs);

		tc.setTabConfig(tabConfig);
		
		Tab currentTab = tabs.get(1);
		aCmd.setActionCmd(GroupAnnotationCommand.ACTION_ADD_NOTE);
		aCmd.setNote("A note");
		BindException aErrors = new BindException(aCmd, aCmd.getActionCmd());
		
		List<Annotation> resultAnnotations = targetGroup.getAnnotations();
		int numAnnotations = resultAnnotations.size();

		testInstance.processTab(tc, currentTab, aReq, aResp, aCmd, aErrors);
		assertEquals(resultAnnotations.size(), numAnnotations+1);
		Annotation resultAnnotation = resultAnnotations.get(resultAnnotations.size()-1);
		assertEquals("A note", resultAnnotation.getNote());
	}

}
