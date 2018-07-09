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
package org.webcurator.ui.site.controller;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.MessageSource;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractFormController;
import org.webcurator.core.admin.PermissionTemplateManager;
import org.webcurator.core.notification.MailServer;
import org.webcurator.core.notification.Mailable;
import org.webcurator.core.sites.SiteManager;
import org.webcurator.core.util.AuthUtil;
import org.webcurator.domain.model.auth.User;
import org.webcurator.domain.model.core.Permission;
import org.webcurator.domain.model.core.PermissionTemplate;
import org.webcurator.domain.model.core.Site;
import org.webcurator.ui.common.Constants;
import org.webcurator.ui.site.command.GeneratePermissionTemplateCommand;

/**
 * The Controller for generating a permission request template.
 * @author bprice
 */
public class GeneratePermissionTemplateController extends AbstractFormController {
	/** The manager for accessing site information. */
    private SiteManager siteManager = null;
    /** The manager for permission request templates. */
    private PermissionTemplateManager permissionTemplateManager;
    /** The mail server to use to send email messages. */
    private MailServer mailServer;
    /** the message source. */
    private MessageSource messageSource;
    
    /** Default Constructor. */
    public GeneratePermissionTemplateController() {
        super();
        this.setCommandClass(GeneratePermissionTemplateCommand.class);
    }

    @Override
    protected ModelAndView showForm(HttpServletRequest aReq,
            HttpServletResponse aRes, BindException aErrors) throws Exception {
        
        String siteOid = aReq.getParameter("siteOid");
        Long oid= Long.valueOf(siteOid);
        ModelAndView mav = new ModelAndView();
        Site aSite = siteManager.getSite(oid,true);
        Set permissions = aSite.getPermissions();
        mav.addObject("permissions", permissions);
        List templates = permissionTemplateManager.getTemplates(AuthUtil.getRemoteUserObject());
        mav.addObject(GeneratePermissionTemplateCommand.MDL_TEMPLATES, templates);
        mav.setViewName("select-permission");
        return mav;
    }

    @Override
    protected ModelAndView processFormSubmission(HttpServletRequest aReq,
            HttpServletResponse aRes, Object aCmd, BindException aErrors)
            throws Exception {
        GeneratePermissionTemplateCommand tempCmd = (GeneratePermissionTemplateCommand) aCmd;
        User loggedInUser = AuthUtil.getRemoteUserObject();
        ModelAndView mav = new ModelAndView();
        
        if (tempCmd != null) {
            if (aErrors.hasErrors()) {
                mav.addObject(Constants.GBL_CMD_DATA, aErrors.getTarget());
                mav.addObject(Constants.GBL_ERRORS, aErrors);
                mav.setViewName("generate-request");
                
            } else if (GeneratePermissionTemplateCommand.ACTION_GENERATE_TEMPLATE.equals(tempCmd.getAction())) {
                Long templateOid = tempCmd.getTemplateOid();
                Long permissionOid = tempCmd.getPermissionOid();
                PermissionTemplate template = permissionTemplateManager.completeTemplate(templateOid, AuthUtil.getRemoteUserObject(),permissionOid);
                mav.addObject(GeneratePermissionTemplateCommand.MDL_TEMPLATE, template);
                mav.addObject(Constants.GBL_CMD_DATA, tempCmd);
                mav.setViewName("generate-request");
            } else if (GeneratePermissionTemplateCommand.ACTION_PRINTIT.equals(tempCmd.getAction())) {
                Long permissionOid = tempCmd.getPermissionOid();
                Permission perm = permissionTemplateManager.getPermission(permissionOid);

                // User chose to print a permission authorisation request
                // so change permission's status to 'Requested'.
                perm.setStatus(Permission.STATUS_REQUESTED);
                perm.setPermissionSentDate(new Date());
        		perm.setDirty(true);
        		perm.getSite().setPermissions(perm.getSite().getPermissions());
                
        		// During the following siteManager.save(), Hibernate throws a HibernateException
        		// with the following message; 'Found two representations of same collection..'
        		// However the permission status appears to be saved OK anyway, 
        		// so we'll just catch the exception and continue..
        		try {
                	siteManager.save(perm.getSite());
                }
        		catch (Exception e) {
        		}
                
        		mav.addObject(Constants.GBL_MESSAGES, messageSource.getMessage("site.print.success", new Object[] {null}, Locale.getDefault()));
                mav.addObject("permissions", perm.getSite().getPermissions());
                List templates = permissionTemplateManager.getTemplates(AuthUtil.getRemoteUserObject());
                mav.addObject(GeneratePermissionTemplateCommand.MDL_TEMPLATES, templates);
                mav.setViewName("select-permission");
            	
            } else if (GeneratePermissionTemplateCommand.ACTION_SEND_EMAIL.equals(tempCmd.getAction())) {
                Long templateOid = tempCmd.getTemplateOid();
                Long permissionOid = tempCmd.getPermissionOid();
                Permission perm = permissionTemplateManager.getPermission(permissionOid);
                PermissionTemplate template = permissionTemplateManager.completeTemplate(templateOid, AuthUtil.getRemoteUserObject(),permissionOid);
                if (perm.getAuthorisingAgent().getEmail() != null ) {
                    if (!"".equals(perm.getAuthorisingAgent().getEmail().trim())) {
                        String recipientAddress = perm.getAuthorisingAgent().getEmail();
                        
                        Mailable email = new Mailable();
                        
                        if (template.getTemplateOverwriteFrom() && notNullOrEmpty(template.getTemplateFrom()))
                        	email.setSender(template.getTemplateFrom());
                        else
                        	email.setSender(loggedInUser.getEmail());
                        
                        if (notNullOrEmpty(template.getTemplateCc()))
                        	email.setCcs(template.getTemplateCc());
                        
                        if (notNullOrEmpty(template.getTemplateBcc()))
                        	email.setBccs(template.getTemplateBcc());
                        
                        email.setRecipients(recipientAddress);
                        //email.setMessage(template.getTemplate());
                        email.setMessage(template.getParsedText());
                        
                        String replyTo = template.getReplyTo();
						if(notNullOrEmpty(replyTo)) {
                        	email.setReplyTo(replyTo);
                        }
                        
                        if (notNullOrEmpty(template.getTemplateSubject()))
                        	email.setSubject(template.getTemplateSubject());
                        else
                        	email.setSubject("Web Curator Tool - Permission Request Email");
                        
                       	mailServer.send(email);
                        
                        // A permission authorisation request email was sent to
                        // the SMTP server OK, so change permission's status
                        // to 'Requested'.
                        perm.setStatus(Permission.STATUS_REQUESTED);
                        perm.setPermissionSentDate(new Date());
                		perm.setDirty(true);
                		perm.getSite().setPermissions(perm.getSite().getPermissions());
                		
                		// During the following siteManager.save() Hibernate throws a HibernateException
                		// with the following message; 'Found two representations of same collection..'
                		// However the permission status appears to be saved OK anyway, 
                		// so we'll just catch the exception and continue..
                		try {
                        	siteManager.save(perm.getSite());
                        }
                		catch (Exception e) {
                		}

                		mav.addObject(Constants.GBL_MESSAGES, messageSource.getMessage("site.email.success", new Object[] {recipientAddress}, Locale.getDefault()));
                		
                    } else {
                        //The email address is an empty string but not null
                        mav.addObject(Constants.GBL_MESSAGES,messageSource.getMessage("site.invalid.email.for.template", new Object[] {template.getTemplateName() }, Locale.getDefault()));
                    }
                } else {
                    //The Harvest Authorisation object doesn't have a valid contact email address (it is null)
                    mav.addObject(Constants.GBL_MESSAGES,messageSource.getMessage("site.invalid.email.for.template", new Object[] {template.getTemplateName() }, Locale.getDefault()));
                }
                
                mav.addObject("permissions", perm.getSite().getPermissions());
                List templates = permissionTemplateManager.getTemplates(AuthUtil.getRemoteUserObject());
                mav.addObject(GeneratePermissionTemplateCommand.MDL_TEMPLATES, templates);
                mav.setViewName("select-permission");

            }
        }
        return mav;
    }
    
    private boolean notNullOrEmpty(String value) {
    	return value!=null && value.trim().length()>0;
    }

    public void setSiteManager(SiteManager siteManager) {
        this.siteManager = siteManager;
    }

    public void setPermissionTemplateManager(
            PermissionTemplateManager permissionTemplateManager) {
        this.permissionTemplateManager = permissionTemplateManager;
    }

    public void setMailServer(MailServer mailServer) {
        this.mailServer = mailServer;
    }

    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

}
