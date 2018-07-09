/**
 * 
 */
package org.webcurator.ui.site.controller;

import static org.junit.Assert.*;

import java.util.Properties;

import org.junit.Test;

import org.springframework.context.MockMessageSource;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;

import org.webcurator.core.admin.PermissionTemplateManager;
import org.webcurator.core.admin.MockPermissionTemplateManagerImpl;
import org.webcurator.core.notification.Mailable;
import org.webcurator.core.notification.MockMailServer;
import org.webcurator.core.sites.SiteManager;
import org.webcurator.core.sites.MockSiteManagerImpl;
import org.webcurator.domain.model.core.Permission;
import org.webcurator.domain.model.core.PermissionTemplate;
import org.webcurator.test.BaseWCTTest;
import org.webcurator.ui.site.command.GeneratePermissionTemplateCommand;

/**
 * @author oakleigh_sk
 *
 */
public class GeneratePermissionTemplateControllerTest extends BaseWCTTest<GeneratePermissionTemplateController> {

	
	public GeneratePermissionTemplateControllerTest()
	{
		super(GeneratePermissionTemplateController.class,
				"src/test/java/org/webcurator/ui/site/controller/GeneratePermissionTemplateControllerTest.xml");
	}
	
	/**
	 * Test method for {@link org.webcurator.ui.site.controller.GeneratePermissionTemplateController#GeneratePermissionTemplateController()}.
	 */
	@Test
	public final void testGeneratePermissionTemplateController() {
		assertTrue(testInstance != null);
	}
	
	/**
	 * Test method for {@link org.webcurator.ui.site.controller.GeneratePermissionTemplateController#showForm(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, org.springframework.validation.BindException)}.
	 */
	@Test
	public void testShowForm() {
		try {
			MockHttpServletRequest request = new MockHttpServletRequest();
			MockHttpServletResponse response = new MockHttpServletResponse();
			BindException aError = new BindException(new GeneratePermissionTemplateCommand(), GeneratePermissionTemplateCommand.ACTION_GENERATE_TEMPLATE);
			
			SiteManager sitemanager = new MockSiteManagerImpl(testFile); 
			PermissionTemplateManager permissionTemplateManager = new MockPermissionTemplateManagerImpl(testFile);

			testInstance.setSiteManager(sitemanager);
			testInstance.setPermissionTemplateManager(permissionTemplateManager);
			
			request.addParameter("siteOid", "9000");

			ModelAndView mav = testInstance.showForm(request, response, aError);
			assert(mav != null);
			assert(mav.getModel().values() != null);
			assert(mav.getViewName().equals("select-permission"));
		}
		catch (Exception e)
		{
			String message = e.getClass().toString() + " - " + e.getMessage();
			log.debug(message);
			fail(message);
		}
	}

	/**
	 * Test method for {@link org.webcurator.ui.site.controller.GeneratePermissionTemplateController#processFormSubmission(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.Object, org.springframework.validation.BindException)}.
	 */
	@Test
	public void testProcessFormSubmission() {
		try {
			MockHttpServletRequest request = new MockHttpServletRequest();
			MockHttpServletResponse response = new MockHttpServletResponse();
			BindException aError = new BindException(new GeneratePermissionTemplateCommand(), GeneratePermissionTemplateCommand.ACTION_GENERATE_TEMPLATE);
			
			SiteManager sitemanager = new MockSiteManagerImpl(testFile); 
			PermissionTemplateManager permissionTemplateManager = new MockPermissionTemplateManagerImpl(testFile);
			PermissionTemplate temp = permissionTemplateManager.getTemplate(1L);
			temp.setTemplate("test");
			temp.setTemplateType("testType");

			testInstance.setSiteManager(sitemanager);
			testInstance.setPermissionTemplateManager(permissionTemplateManager);
			testInstance.setMessageSource(new MockMessageSource());
			
			GeneratePermissionTemplateCommand aCommand = null;
			ModelAndView mav = null;
			
			// test action GeneratePermissionTemplateCommand.ACTION_GENERATE_TEMPLATE
			aCommand = new GeneratePermissionTemplateCommand();
			aCommand.setAction(GeneratePermissionTemplateCommand.ACTION_GENERATE_TEMPLATE);
			aCommand.setTemplateOid(1L);
			aCommand.setPermissionOid(1L);

			mav = testInstance.processFormSubmission(request, response, aCommand, aError);
			assert(mav != null);
			assert(mav.getModel().values() != null);
			assert(mav.getViewName().equals("generate-request"));

			// test action GeneratePermissionTemplateCommand.ACTION_PRINTIT
			aCommand = new GeneratePermissionTemplateCommand();
			aCommand.setAction(GeneratePermissionTemplateCommand.ACTION_PRINTIT);
			aCommand.setPermissionOid(1L);

			mav = testInstance.processFormSubmission(request, response, aCommand, aError);
			assert(mav != null);
			assert(mav.getModel().values() != null);
			assert(mav.getViewName().equals("select-permission"));

			// test action GeneratePermissionTemplateCommand.ACTION_SEND_EMAIL
			aCommand = new GeneratePermissionTemplateCommand();
			aCommand.setAction(GeneratePermissionTemplateCommand.ACTION_SEND_EMAIL);
			aCommand.setTemplateOid(1L);
			aCommand.setPermissionOid(1L);

			mav = testInstance.processFormSubmission(request, response, aCommand, aError);
			assert(mav != null);
			assert(mav.getModel().values() != null);
			assert(mav.getViewName().equals("select-permission"));
		}
		catch (Exception e)
		{
			String message = e.getClass().toString() + " - " + e.getMessage();
			log.debug(message);
			fail(message);
		}
	}
	
	
	@Test
	public void testProcessEmailFormSubmission() {
		try {
			MockHttpServletRequest request = new MockHttpServletRequest();
			MockHttpServletResponse response = new MockHttpServletResponse();
			BindException aError = new BindException(new GeneratePermissionTemplateCommand(), GeneratePermissionTemplateCommand.ACTION_GENERATE_TEMPLATE);
			
			SiteManager sitemanager = new MockSiteManagerImpl(testFile); 
			PermissionTemplateManager permissionTemplateManager = new MockPermissionTemplateManagerImpl(testFile);
			Permission perm = permissionTemplateManager.getPermission(1L);
			perm.getAuthorisingAgent().setEmail("rec@rep.com");
			
			PermissionTemplate temp = permissionTemplateManager.getTemplate(1L);
			temp.setTemplateBcc("bcc@rep.com");
			temp.setTemplateCc("cc@rep.com");
			temp.setTemplateOverwriteFrom(true);
			temp.setTemplateFrom("from@rep.com");
			temp.setTemplateSubject("subject");
			temp.setReplyTo("replyto@rep.com");
			temp.setTemplate("test");
			temp.setTemplateType("testType");
			
			testInstance.setSiteManager(sitemanager);
			testInstance.setPermissionTemplateManager(permissionTemplateManager);
			testInstance.setMessageSource(new MockMessageSource());
			
			
			
			
			GeneratePermissionTemplateCommand aCommand = null;
			ModelAndView mav = null;
			
			// test action GeneratePermissionTemplateCommand.ACTION_GENERATE_TEMPLATE
			aCommand = new GeneratePermissionTemplateCommand();
			aCommand.setAction(GeneratePermissionTemplateCommand.ACTION_SEND_EMAIL);
			aCommand.setTemplateOid(1L);
			aCommand.setPermissionOid(1L);
			
			Properties mailConfig = new Properties();
			MockMailServer ms = new MockMailServer(mailConfig);
			testInstance.setMailServer(ms);

			mav = testInstance.processFormSubmission(request, response, aCommand, aError);
			Mailable email = ms.getEmailResult();
			assertEquals("rec@rep.com",email.getRecipients());
			assertEquals("cc@rep.com",email.getCcs());
			assertEquals("bcc@rep.com",email.getBccs());
			assertEquals("subject",email.getSubject());
			assertEquals("replyto@rep.com",email.getReplyTo());
			
		}
		catch (Exception e)
		{
			String message = e.getClass().toString() + " - " + e.getMessage();
			log.debug(message);
			fail(message);
		}
	}
	
	

}
