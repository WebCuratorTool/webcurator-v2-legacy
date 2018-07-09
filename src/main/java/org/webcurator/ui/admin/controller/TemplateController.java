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
package org.webcurator.ui.admin.controller;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.context.MessageSource;
import org.springframework.validation.BindException;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractFormController;
import org.webcurator.core.admin.PermissionTemplateManager;
import org.webcurator.core.agency.AgencyUserManager;
import org.webcurator.core.util.AuthUtil;
import org.webcurator.domain.model.auth.Agency;
import org.webcurator.domain.model.auth.User;
import org.webcurator.domain.model.core.PermissionTemplate;
import org.webcurator.ui.admin.command.TemplateCommand;
import org.webcurator.ui.common.Constants;

/**
 * The Controller for managing the creation and modification of permission templates.
 * @author bprice
 */
public class TemplateController extends AbstractFormController {
	/** the logger. */
    private Log log = LogFactory.getLog(TemplateController.class);
    /** the permission template manager. */
    private PermissionTemplateManager permissionTemplateManager;
    /** the agency user manager. */
    private AgencyUserManager agencyUserManager;
    /** the message source. */
    private MessageSource messageSource;
    /** the default Subject. */
    private String defaultSubject = null;
    
    /** Default Constructor. */
    public TemplateController() {
        super();
        this.setCommandClass(TemplateCommand.class);
    }
    
    @Override
    public void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws Exception {
        NumberFormat nf = NumberFormat.getInstance(request.getLocale());
        binder.registerCustomEditor(java.lang.Long.class, new CustomNumberEditor(java.lang.Long.class, nf, true));   
    }
    
    @Override
    protected ModelAndView showForm(HttpServletRequest aReq, HttpServletResponse aRes, BindException aError) throws Exception { 
        return getDefaultView();
    }

    @Override
    protected ModelAndView processFormSubmission(HttpServletRequest aReq, HttpServletResponse aRes, Object aCmd, BindException aError) throws Exception {
        ModelAndView mav = null;
        TemplateCommand templateCmd = (TemplateCommand) aCmd;
        if (templateCmd != null) {
            if (aError.hasErrors()) {
                mav = getNewTemplateView(templateCmd);
                mav.addObject(Constants.GBL_CMD_DATA, aError.getTarget());
                mav.addObject(Constants.GBL_ERRORS, aError);
                
            } else if (TemplateCommand.ACTION_NEW.equals(templateCmd.getAction())) {
                log.debug("New Action on TemplateController");
                mav = getNewTemplateView(templateCmd);
            } else if (TemplateCommand.ACTION_SAVE.equals(templateCmd.getAction())) {
                log.debug("Save Action on TemplateController");
                mav = getSaveTemplateView(templateCmd);
            } else if (TemplateCommand.ACTION_VIEW.equals(templateCmd.getAction())) {
                log.debug("View Action on Template Controller");
                mav = getViewTemplateView(templateCmd);
            } else if (TemplateCommand.ACTION_EDIT.equals(templateCmd.getAction())) {
                log.debug("Edit Action on Template Controller");
                mav = getNewTemplateView(templateCmd);
            } else if (TemplateCommand.ACTION_DELETE.equals(templateCmd.getAction())) {
                log.debug("Delete Action on Template Controller");
                mav = getDeleteView(templateCmd);
            }
        }
        return mav;
    }

    /** 
     * @param permissionTemplateManager the permission template manager.
     */
    public void setPermissionTemplateManager(
            PermissionTemplateManager permissionTemplateManager) {
        this.permissionTemplateManager = permissionTemplateManager;
    }
    
    /**  
     * @param agencyUserManager the agency user manager.
     */
    public void setAgencyUserManager(AgencyUserManager agencyUserManager) {
        this.agencyUserManager = agencyUserManager;
    }
    
    /** 
     * @return the default model and view.
     */
    private ModelAndView getDefaultView() {
        ModelAndView mav = new ModelAndView();
        User loggedInUser = AuthUtil.getRemoteUserObject();
        List templates = permissionTemplateManager.getTemplates(loggedInUser);
        
        mav.addObject(TemplateCommand.MDL_TEMPLATES, templates);
        
        mav.setViewName("view-templates");
        return mav;
    }
    
    /**
     * Process the delete permission template command.  
     */
    private ModelAndView getDeleteView(TemplateCommand templateCmd) {
        ModelAndView mav = new ModelAndView();
        
        //carry out the delete of the object
        if (templateCmd.getOid() != null) {
            permissionTemplateManager.delete(templateCmd.getOid());
        }
        User loggedInUser = AuthUtil.getRemoteUserObject();
        List templates = permissionTemplateManager.getTemplates(loggedInUser);
        
        mav.addObject(TemplateCommand.MDL_TEMPLATES, templates);
        
        mav.setViewName("view-templates");
        return mav;
    }

    /**
     * Process the view template command. 
     */
    private ModelAndView getViewTemplateView(TemplateCommand templateCmd) {
        ModelAndView mav = new ModelAndView();
        if (templateCmd.getOid() != null) {
            PermissionTemplate permTemp = permissionTemplateManager.getTemplate(templateCmd.getOid());
            TemplateCommand loadedTemplateCmd = new TemplateCommand();
            loadedTemplateCmd.setAgencyOid(permTemp.getAgency().getOid());
            loadedTemplateCmd.setOid(permTemp.getOid());
            loadedTemplateCmd.setTemplateDescription(permTemp.getTemplateDescription());
            loadedTemplateCmd.setTemplateName(permTemp.getTemplateName());
            loadedTemplateCmd.setTemplateText(permTemp.getTemplate());
            loadedTemplateCmd.setTemplateType(permTemp.getTemplateType());
            
            loadedTemplateCmd.setTemplateSubject(permTemp.getTemplateSubject());
            loadedTemplateCmd.setTemplateOverwriteFrom(permTemp.getTemplateOverwriteFrom());
            loadedTemplateCmd.setTemplateFrom(permTemp.getTemplateFrom());
            loadedTemplateCmd.setTemplateCc(permTemp.getTemplateCc());
            loadedTemplateCmd.setTemplateBcc(permTemp.getTemplateBcc());
            loadedTemplateCmd.setReplyTo(permTemp.getReplyTo());
            
            loadedTemplateCmd.setEmailTypeText(PermissionTemplate.EMAIL_TYPE_TEMPLATE);
            
            mav.addObject(Constants.GBL_CMD_DATA, loadedTemplateCmd);
        }
        
        List agencies = agencyUserManager.getAgenciesForTemplatePriv();
        mav.addObject(TemplateCommand.MDL_AGENCIES, agencies);
        
        mav.setViewName("view-template");
        return mav;
    }
    
    /**
     * Process the new template command. 
     */
    private ModelAndView getNewTemplateView(TemplateCommand templateCmd) {
        ModelAndView mav = new ModelAndView();
        if (templateCmd.getOid() != null) {
            PermissionTemplate permTemp = permissionTemplateManager.getTemplate(templateCmd.getOid());
            TemplateCommand loadedTemplateCmd = new TemplateCommand();
            loadedTemplateCmd.setAgencyOid(permTemp.getAgency().getOid());
            loadedTemplateCmd.setOid(permTemp.getOid());
            loadedTemplateCmd.setTemplateDescription(permTemp.getTemplateDescription());
            loadedTemplateCmd.setTemplateName(permTemp.getTemplateName());
            loadedTemplateCmd.setTemplateText(permTemp.getTemplate());
            loadedTemplateCmd.setTemplateType(permTemp.getTemplateType());
            
            loadedTemplateCmd.setTemplateSubject(permTemp.getTemplateSubject());
            loadedTemplateCmd.setTemplateOverwriteFrom(permTemp.getTemplateOverwriteFrom());
            loadedTemplateCmd.setTemplateFrom(permTemp.getTemplateFrom());
            loadedTemplateCmd.setTemplateCc(permTemp.getTemplateCc());
            loadedTemplateCmd.setTemplateBcc(permTemp.getTemplateBcc());
            loadedTemplateCmd.setReplyTo(permTemp.getReplyTo());
            
            loadedTemplateCmd.setEmailTypeText(PermissionTemplate.EMAIL_TYPE_TEMPLATE);
            
            mav.addObject(Constants.GBL_CMD_DATA, loadedTemplateCmd);
        }
        else
        {
        	TemplateCommand loadedTemplateCmd = new TemplateCommand();
        	loadedTemplateCmd.setTemplateSubject(defaultSubject);
        	loadedTemplateCmd.setEmailTypeText(PermissionTemplate.EMAIL_TYPE_TEMPLATE);
        	mav.addObject(Constants.GBL_CMD_DATA, loadedTemplateCmd);
        }
        
        List agencies = agencyUserManager.getAgenciesForTemplatePriv();
        
        List types =permissionTemplateManager.getTemplateTypes();
        mav.addObject(TemplateCommand.MDL_AGENCIES, agencies);
        mav.addObject(TemplateCommand.MDL_TEMPLATE_TYPES, types);
        
        mav.setViewName("add-template");
        return mav;
    }
    
    /** 
     * Process the save template command. 
     */
    private ModelAndView getSaveTemplateView(TemplateCommand templateCommand) {
        
        PermissionTemplate permissionTemplate = new PermissionTemplate();
        Agency agency = agencyUserManager.getAgencyByOid(templateCommand.getAgencyOid()); 
        permissionTemplate.setAgency(agency);
        permissionTemplate.setTemplate(templateCommand.getTemplateText());
        permissionTemplate.setTemplateName(templateCommand.getTemplateName());
        permissionTemplate.setTemplateType(templateCommand.getTemplateType());
        permissionTemplate.setTemplateDescription(templateCommand.getTemplateDescription());
        permissionTemplate.setOid(templateCommand.getOid());
        
        permissionTemplate.setTemplateSubject(templateCommand.getTemplateSubject());
        permissionTemplate.setTemplateOverwriteFrom(templateCommand.getTemplateOverwriteFrom());
        permissionTemplate.setTemplateFrom(templateCommand.getTemplateFrom());
        permissionTemplate.setTemplateCc(templateCommand.getTemplateCc());
        permissionTemplate.setTemplateBcc(templateCommand.getTemplateBcc());
        permissionTemplate.setReplyTo(templateCommand.getReplyTo());
        
        permissionTemplateManager.saveTemplate(permissionTemplate);
        
        ModelAndView mav = getDefaultView();
        if (templateCommand.getOid() == null) {
            mav.addObject(Constants.GBL_MESSAGES, messageSource.getMessage("template.created", new Object[] { templateCommand.getTemplateName() }, Locale.getDefault()));
        } else {
            mav.addObject(Constants.GBL_MESSAGES, messageSource.getMessage("template.updated", new Object[] { templateCommand.getTemplateName()}, Locale.getDefault()));
        }
        return mav;
    }

    /**
     * @param messageSource the message source
     */
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }
   
    public String getDefaultSubject() {
        return defaultSubject;
    }

	/**
	 * @param defaultSubject the defaultSubject to set
	 */
    public void setDefaultSubject(String defaultSubject) {
        this.defaultSubject = defaultSubject;
    }
}
