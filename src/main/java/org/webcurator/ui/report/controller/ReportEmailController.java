/*
 *  Copyright 2006 The National Library of New Zealand
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.webcurator.ui.report.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.acegisecurity.Authentication;
import org.acegisecurity.context.SecurityContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractFormController;
import org.webcurator.core.notification.MailServer;
import org.webcurator.core.notification.Mailable;
import org.webcurator.core.report.FileFactory;
import org.webcurator.core.report.OperationalReport;
import org.webcurator.domain.model.auth.User;
import org.webcurator.ui.report.command.ReportEmailCommand;

/**
 * Report Email Controller
 * 
 * @author MDubos
 *
 */
public class ReportEmailController extends AbstractFormController {
	
	public static final String ACTION_EMAIL = "Email";
	public static final String ACTION_CANCEL = "Cancel";

	private Log log = LogFactory.getLog(ReportEmailController.class);
	
	private MailServer mailServer = null;
	

	/**
	 * Default constructor
	 *
	 */
	public ReportEmailController() {
        setCommandClass(ReportEmailController.class);
	}
	
	@Override
	protected ModelAndView showForm(HttpServletRequest req,
			HttpServletResponse resp, BindException exc) throws Exception {
				
		return null;		
	}

	
	@Override
	protected ModelAndView processFormSubmission(HttpServletRequest req,
			HttpServletResponse resp, Object comm, BindException exc)
			throws Exception {
		
		ReportEmailCommand com = (ReportEmailCommand) comm;
		ModelAndView mav = new ModelAndView();
		
		if(com.getActionCmd().equals(ACTION_EMAIL)){
		
			OperationalReport operationalReport = (OperationalReport) req.getSession().getAttribute("operationalReport");
	
			// Get user's email address 
			// ...user
	        String remoteUser = null;
	        Authentication auth = null;        
	        SecurityContext acegiCtx = (SecurityContext) req.getSession().getAttribute("ACEGI_SECURITY_CONTEXT");
	        if( acegiCtx != null) {
	            auth = acegiCtx.getAuthentication();
	            if (auth != null) {
	                remoteUser = auth.getName();
	            }
	        }
	        // ...email address
	        User user = (User) auth.getDetails();
	        String userEmailAddress = user.getEmail(); 
					
	        // Build attachment content
			String dataAttachment = operationalReport.getRendering(com.getFormat());
			
			// E-mail
			Mailable email = new Mailable();
			email.setRecipients(com.getRecipient());
			email.setSender(userEmailAddress);
			email.setSubject(com.getSubject());
			email.setMessage(com.getMessage());
			mailServer.send(email, 
					"report" + FileFactory.getFileExtension(com.getFormat()),
					FileFactory.getMIMEType(com.getFormat()),
					dataAttachment );
			
			log.debug("email sent:");
			log.debug("  from:" + userEmailAddress);
			log.debug("  format=" + com.getFormat());
			log.debug("  to=" + com.getRecipient());
			log.debug("  subject=" + com.getSubject());
			log.debug("  msg=" + com.getMessage());
		
			mav.setViewName("reporting-preview");
			
		} else {
			log.error("Did not get send request: " + com.getActionCmd());
			mav.setViewName("reporting-preview");
		}
		
		return mav;
				
	}

	/**
	 * 
	 * @param mailServer Set the email server
	 */
	public void setMailServer(MailServer mailServer) {
		this.mailServer = mailServer;
	}


}
