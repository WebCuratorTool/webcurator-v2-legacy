package org.webcurator.ui.site.controller;

import static org.junit.Assert.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.Iterator;
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
import org.webcurator.core.targets.MockTargetManager;
import org.webcurator.core.targets.TargetManager;
import org.webcurator.core.util.AuthUtil;
import org.webcurator.domain.model.core.Annotation;
import org.webcurator.domain.model.core.Permission;
import org.webcurator.domain.model.core.Site;
import org.webcurator.domain.model.core.Target;
import org.webcurator.domain.model.core.BusinessObjectFactory;
import org.webcurator.test.BaseWCTTest;
import org.webcurator.ui.site.SiteEditorContext;
import org.webcurator.ui.site.command.SitePermissionCommand;
import org.webcurator.ui.site.validator.SiteValidator;
import org.webcurator.ui.target.command.TargetAnnotationCommand;
import org.webcurator.ui.target.controller.TargetAnnotationHandler;
import org.webcurator.ui.util.Tab;
import org.webcurator.ui.util.TabConfig;
import org.webcurator.ui.util.TabStatus;
import org.webcurator.ui.util.TabbedController;

public class SitePermissionHandlerTest extends BaseWCTTest<SitePermissionHandler> {

	public SitePermissionHandlerTest()
	{
		super(SitePermissionHandler.class,
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
		tabGeneral.setCommandClass(SitePermissionCommand.class);
		tabGeneral.setJsp("../site-general.jsp");
		tabGeneral.setPageId("GENERAL");
		tabGeneral.setValidator(new SiteValidator());

		SiteGeneralHandler genHandler = new SiteGeneralHandler();
		genHandler.setSiteManager(siteManager);
		tabGeneral.setTabHandler(genHandler);
		
		tabs.add(tabGeneral);
		
		Tab tabPermissions = new Tab();
		tabPermissions.setCommandClass(TargetAnnotationCommand.class);
		tabPermissions.setJsp("../site-permissions.jsp");
		tabPermissions.setPageId("PERMISSIONS");

		SitePermissionHandler permHandler = new SitePermissionHandler();
		permHandler.setSiteManager(siteManager);
		tabPermissions.setTabHandler(permHandler);
		
		tabs.add(tabPermissions);
		
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
			SitePermissionCommand aCmd = new SitePermissionCommand();
			TabbedController tc = new SiteController();

			TabConfig tabConfig = new TabConfig();
			tabConfig.setViewName("site");
			List<Tab> tabs = getTabList(siteManager);
			tabConfig.setTabs(tabs);

			tc.setTabConfig(tabConfig);
			
			Tab currentTab = tabs.get(1);
			BindException aErrors = new BindException(aCmd, aCmd.getActionCmd());
			
			ModelAndView mav = testInstance.preProcessNextTab(tc, currentTab, aReq, aResp, aCmd, aErrors);
			assertNotNull(mav);
			assertFalse(checkSortedList(site.getAnnotations()));
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
			MockHttpServletRequest aReq = new MockHttpServletRequest();
			SiteManager siteManager = new MockSiteManagerImpl(testFile);
			testInstance.setSiteManager(siteManager);
			testInstance.setBusinessObjectFactory(new BusinessObjectFactory());
			Site site = siteManager.getSite(9000L, true);
			Set<Permission> permissions = site.getPermissions();
			List<Annotation> list = createAnnotationList(); 
			assertFalse(checkSortedList(list));
			Iterator<Permission> it = permissions.iterator();
			while(it.hasNext())
			{
				Permission p = it.next();
				if(p.getOid() == 7000L)
				{
					p.setAnnotations(list);
				}
			}
			SiteEditorContext ctx = new SiteEditorContext(site);
			aReq.getSession().setAttribute(SiteController.EDITOR_CONTEXT, ctx);
			HttpServletResponse aResp = new MockHttpServletResponse(); 
			SitePermissionCommand aCmd = new SitePermissionCommand();
			TabbedController tc = new SiteController();
	
			TabConfig tabConfig = new TabConfig();
			tabConfig.setViewName("site");
			List<Tab> tabs = getTabList(siteManager);
			tabConfig.setTabs(tabs);
	
			tc.setTabConfig(tabConfig);
			
			Tab currentTab = tabs.get(1);
			BindException aErrors = new BindException(aCmd, aCmd.getActionCmd());
			
			aReq.addParameter("_new", "");
			
			ModelAndView mav = testInstance.processOther(tc, currentTab, aReq, aResp, aCmd, aErrors);
			assertNotNull(mav);
			
			aReq.removeParameter("_new");
			aReq.addParameter("_edit_permission", "");
			
			aCmd.setSelectedPermission("7000");
			mav = testInstance.processOther(tc, currentTab, aReq, aResp, aCmd, aErrors);
			assertNotNull(mav);
			Permission permission = (Permission) ctx.getObject(Permission.class, "7000");
			assertNotNull(permission);
			assertEquals(permission.getAnnotations().size(), ANNOTATIONS.length);
			assertTrue(checkSortedList(permission.getAnnotations()));
		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}

}
