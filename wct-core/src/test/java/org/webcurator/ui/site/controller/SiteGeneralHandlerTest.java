package org.webcurator.ui.site.controller;

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
import org.webcurator.core.sites.MockSiteManagerImpl;
import org.webcurator.core.sites.SiteManager;
import org.webcurator.core.util.AuthUtil;
import org.webcurator.domain.model.core.Annotation;
import org.webcurator.domain.model.core.Site;
import org.webcurator.test.BaseWCTTest;
import org.webcurator.ui.site.SiteEditorContext;
import org.webcurator.ui.site.command.SiteCommand;
import org.webcurator.ui.site.validator.SiteValidator;
import org.webcurator.ui.util.Tab;
import org.webcurator.ui.util.TabConfig;
import org.webcurator.ui.util.TabStatus;
import org.webcurator.ui.util.TabbedController;

public class SiteGeneralHandlerTest extends BaseWCTTest<SiteGeneralHandler>{

	public SiteGeneralHandlerTest()
	{
		super(SiteGeneralHandler.class,
				"src/test/java/org/webcurator/ui/site/controller/sitegeneralhandlertest.xml");
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
	public final void testPreProcessNextTab() {
	
		try
		{
			HttpServletRequest aReq = new MockHttpServletRequest();
			SiteManager siteManager = new MockSiteManagerImpl(testFile);
			testInstance.setSiteManager(siteManager);
			Site site = siteManager.getSite(9000L, true);
			List<Annotation> list = createAnnotationList(); 
			assertFalse(checkSortedList(list));
			site.setAnnotations(list);
			SiteEditorContext ctx = new SiteEditorContext(site);
			aReq.getSession().setAttribute(SiteController.EDITOR_CONTEXT, ctx);
			HttpServletResponse aResp = new MockHttpServletResponse(); 
			SiteCommand aCmd = new SiteCommand();
			TabbedController tc = new SiteController();

			TabConfig tabConfig = new TabConfig();
			tabConfig.setViewName("site");
			List<Tab> tabs = getTabList(siteManager);
			tabConfig.setTabs(tabs);

			tc.setTabConfig(tabConfig);
			
			Tab currentTab = tabs.get(0);
			BindException aErrors = new BindException(aCmd, aCmd.getCmdAction());
			
			ModelAndView mav = testInstance.preProcessNextTab(tc, currentTab, aReq, aResp, aCmd, aErrors);
			assertNotNull(mav);
			assertTrue(checkSortedList(site.getAnnotations()));
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
			SiteManager siteManager = new MockSiteManagerImpl(testFile);
			testInstance.setSiteManager(siteManager);
			Site site = siteManager.getSite(9000L, true);
			List<Annotation> list = createAnnotationList(); 
			assertFalse(checkSortedList(list));
			site.setAnnotations(list);
			SiteEditorContext ctx = new SiteEditorContext(site);
			aReq.getSession().setAttribute(SiteController.EDITOR_CONTEXT, ctx);
			HttpServletResponse aResp = new MockHttpServletResponse(); 
			SiteCommand aCmd = new SiteCommand();
			TabbedController tc = new SiteController();
	
			TabConfig tabConfig = new TabConfig();
			tabConfig.setViewName("site");
			List<Tab> tabs = getTabList(siteManager);
			tabConfig.setTabs(tabs);
	
			tc.setTabConfig(tabConfig);
			
			Tab currentTab = tabs.get(0);
			aCmd.setCmdAction(SiteCommand.ACTION_ADD_NOTE);
			aCmd.setAnnotation("A note");
			BindException aErrors = new BindException(aCmd, aCmd.getCmdAction());
			
			int numAnnotations = site.getAnnotations().size();
			
			ModelAndView mav = testInstance.processOther(tc, currentTab, aReq, aResp, aCmd, aErrors);
			assertTrue(mav != null);
			assertTrue(mav.getViewName().equals("site"));
			assertTrue(((TabStatus)mav.getModel().get("tabStatus")).getCurrentTab().getPageId().equals("GENERAL"));
			int listSize = site.getAnnotations().size(); 
			assertTrue(listSize > 0);
			int noteIndex = 0;
			assertTrue(site.getAnnotations().size() == (numAnnotations+1));
			assertTrue(site.getAnnotations().get(noteIndex).getNote().equals("A note"));
			assertTrue(checkSortedList(site.getAnnotations()));
			
			currentTab = tabs.get(0);
			aCmd.setCmdAction(SiteCommand.ACTION_MODIFY_NOTE);
			aCmd.setAnnotation("A new note");
			aCmd.setAnnotationIndex(noteIndex);
			aErrors = new BindException(aCmd, aCmd.getCmdAction());
			
			mav = testInstance.processOther(tc, currentTab, aReq, aResp, aCmd, aErrors);
			assertTrue(mav != null);
			assertTrue(mav.getViewName().equals("site"));
			assertTrue(((TabStatus)mav.getModel().get("tabStatus")).getCurrentTab().getPageId().equals("GENERAL"));
			listSize = site.getAnnotations().size(); 
			assertTrue(listSize > 0);
			int newNoteIndex = 0;
			assertTrue(newNoteIndex == noteIndex);
			assertTrue(site.getAnnotations().size() == (numAnnotations+1));
			assertFalse(site.getAnnotations().get(noteIndex).getNote().equals("A note"));
			assertTrue(site.getAnnotations().get(noteIndex).getNote().equals("A new note"));
			assertTrue(checkSortedList(site.getAnnotations()));
			
			currentTab = tabs.get(0);
			aCmd.setCmdAction(SiteCommand.ACTION_DELETE_NOTE);
			aCmd.setAnnotationIndex(noteIndex);
			aErrors = new BindException(aCmd, aCmd.getCmdAction());
			
			mav = testInstance.processOther(tc, currentTab, aReq, aResp, aCmd, aErrors);
			assertTrue(mav != null);
			assertTrue(mav.getViewName().equals("site"));
			assertTrue(((TabStatus)mav.getModel().get("tabStatus")).getCurrentTab().getPageId().equals("GENERAL"));
			int newListSize = site.getAnnotations().size(); 
			assertTrue(newListSize == (listSize-1));
			assertTrue(site.getAnnotations().size() == numAnnotations);
			assertTrue(checkSortedList(site.getAnnotations()));
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
		SiteManager siteManager = new MockSiteManagerImpl(testFile);
		testInstance.setSiteManager(siteManager);
		Site site = siteManager.getSite(9000L, true);
		List<Annotation> list = createAnnotationList(); 
		assertFalse(checkSortedList(list));
		site.setAnnotations(list);
		SiteEditorContext ctx = new SiteEditorContext(site);
		aReq.getSession().setAttribute(SiteController.EDITOR_CONTEXT, ctx);
		HttpServletResponse aResp = new MockHttpServletResponse(); 
		SiteCommand aCmd = new SiteCommand();
		TabbedController tc = new SiteController();

		TabConfig tabConfig = new TabConfig();
		tabConfig.setViewName("site");
		List<Tab> tabs = getTabList(siteManager);
		tabConfig.setTabs(tabs);

		tc.setTabConfig(tabConfig);
		
		Tab currentTab = tabs.get(0);
		aCmd.setCmdAction(SiteCommand.ACTION_ADD_NOTE);
		aCmd.setAnnotation("A note");
		BindException aErrors = new BindException(aCmd, aCmd.getCmdAction());
		
		List<Annotation> resultAnnotations = site.getAnnotations();
		int numAnnotations = resultAnnotations.size();

		testInstance.processTab(tc, currentTab, aReq, aResp, aCmd, aErrors);
		assertEquals(resultAnnotations.size(), numAnnotations+1);
		Annotation resultAnnotation = resultAnnotations.get(resultAnnotations.size()-1);
		assertEquals("A note", resultAnnotation.getNote());
	}

}
