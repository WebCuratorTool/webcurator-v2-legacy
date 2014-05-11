package org.webcurator.ui.site.controller;

import static org.junit.Assert.*;

import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import org.springframework.mock.web.*;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.WebUtils;

import org.webcurator.test.BaseWCTTest;
import org.webcurator.ui.common.Constants;
import org.webcurator.ui.groups.command.GeneralCommand;
import org.webcurator.ui.groups.command.GroupAnnotationCommand;
import org.webcurator.ui.groups.controller.GeneralHandler;
import org.webcurator.ui.groups.controller.GroupAnnotationHandler;
import org.webcurator.ui.groups.validator.GeneralValidator;
import org.webcurator.ui.groups.validator.GroupAnnotationValidator;
import org.webcurator.ui.site.SiteEditorContext;
import org.webcurator.ui.site.command.*;
import org.webcurator.ui.util.Tab;
import org.webcurator.ui.util.TabConfig;
import org.webcurator.auth.AuthorityManagerImpl;
import org.webcurator.core.agency.MockAgencyUserManagerImpl;
import org.webcurator.core.sites.*;
import org.webcurator.core.targets.TargetManager;
import org.webcurator.domain.model.core.*;

public class SiteAgencyControllerTest extends BaseWCTTest<SiteAgencyController>{

	public SiteAgencyControllerTest()
	{
		super(SiteAgencyController.class,
				"src/test/java/org/webcurator/ui/site/controller/siteagencycontrollertest.xml");
	}
	
	@Test
	public final void testHandleCancel() {
		try
		{
			MockHttpServletRequest aReq = new MockHttpServletRequest();
			SiteManager siteManager = new MockSiteManagerImpl(testFile);
			
			Site site = siteManager.getSite(9000L, true);
			SiteEditorContext ctx = new SiteEditorContext(site);
			HttpServletResponse aResp = new MockHttpServletResponse(); 
			SiteAuthorisingAgencyCommand aCmd = new SiteAuthorisingAgencyCommand();
	
			Iterator<Permission> it = site.getPermissions().iterator();
			assertTrue(it.hasNext());
			Permission p = it.next();
			ctx.putObject(p);
			aCmd.setIdentity(p.getIdentity());
			
			
			aReq.addParameter("_cancel_auth_agent", "Save");
			aReq.getSession().setAttribute(SiteController.EDITOR_CONTEXT, ctx);
			
			BindException aErrors = new BindException(aCmd, aCmd.getCmdAction());
			
			SiteController sc = new SiteController();
			testInstance.setSiteController(sc);
			TabConfig tabConfig = new TabConfig();
			tabConfig.setViewName("site");
			List<Tab> tabs = getTabList(siteManager);
			tabConfig.setTabs(tabs);

			sc.setTabConfig(tabConfig);
			
			ModelAndView mav = testInstance.handle(aReq, aResp, aCmd, aErrors);
			assertTrue(mav != null);
			assertTrue(mav.getViewName().equals("site"));
		}
		catch(Exception e)
		{
			fail(e.getClass().getName()+" - "+e.getMessage());
		}
	}

	@Test
	public final void testHandleSave() {
		try
		{
			MockHttpServletRequest aReq = new MockHttpServletRequest();
			SiteManager siteManager = new MockSiteManagerImpl(testFile);
			
			Site site = siteManager.getSite(9000L, true);
			SiteEditorContext ctx = new SiteEditorContext(site);
			
			Object[] agents = site.getAuthorisingAgents().toArray();
			AuthorisingAgent agent = (AuthorisingAgent) agents[0];
			ctx.putObject(agent);
			
			HttpServletResponse aResp = new MockHttpServletResponse(); 
			SiteAuthorisingAgencyCommand aCmd = new SiteAuthorisingAgencyCommand();
	
			Iterator<Permission> it = site.getPermissions().iterator();
			assertTrue(it.hasNext());
			Permission p = it.next();
			ctx.putObject(p);
			
			aCmd.setIdentity("8000");
			
			
			aReq.addParameter("_save_auth_agent", "Save");
			aReq.getSession().setAttribute(SiteController.EDITOR_CONTEXT, ctx);
			
			BindException aErrors = new BindException(aCmd, aCmd.getCmdAction());
			
			SiteController sc = new SiteController();
			testInstance.setSiteController(sc);
			TabConfig tabConfig = new TabConfig();
			tabConfig.setViewName(Constants.VIEW_SITE_AGENCIES);
			List<Tab> tabs = getTabList(siteManager);
			tabConfig.setTabs(tabs);

			sc.setTabConfig(tabConfig);
			
			ModelAndView mav = testInstance.handle(aReq, aResp, aCmd, aErrors);
			assertTrue(mav != null);
			assertTrue(mav.getViewName().equals(Constants.VIEW_SITE_AGENCIES));
		}
		catch(Exception e)
		{
			fail(e.getClass().getName()+" - "+e.getMessage());
		}
	}

	private List<Tab> getTabList(SiteManager siteManager)
	{
		List<Tab> tabs = new ArrayList<Tab>();
		
		Tab tabAuthAgencies = new Tab();
		tabAuthAgencies.setCommandClass(SiteAuthorisingAgencyCommand.class);
		tabAuthAgencies.setJsp("../site-auth-agencies.jsp");
		tabAuthAgencies.setPageId("AUTHORISING_AGENCIES");
		//tabGeneral.setValidator(new GeneralValidator());

		SiteAuthorisingAgencyHandler theHandler = new SiteAuthorisingAgencyHandler();
		//theHandler.setAgencyUserManager(new MockAgencyUserManagerImpl(testFile));
		//theHandler.setAuthorityManager(new AuthorityManagerImpl());
		tabAuthAgencies.setTabHandler(theHandler);
		
		tabs.add(tabAuthAgencies);
		
		/*
		Tab tabAnnotations = new Tab();
		tabAnnotations.setCommandClass(GroupAnnotationCommand.class);
		tabAnnotations.setJsp("../group-annotations.jsp");
		tabAnnotations.setPageId("ANNOTATIONS");
		tabGeneral.setValidator(new GroupAnnotationValidator());

		GroupAnnotationHandler annHandler = new GroupAnnotationHandler();
		//annHandler.setTargetManager(siteManager);
		tabAnnotations.setTabHandler(annHandler);
		
		tabs.add(tabAnnotations);
		*/

		return tabs;
	}
	
}
