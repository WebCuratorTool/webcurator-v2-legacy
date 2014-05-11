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
package org.webcurator.core.admin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.webcurator.auth.AuthorityManager;
import org.webcurator.domain.PermissionTemplateDAO;
import org.webcurator.domain.model.auth.Privilege;
import org.webcurator.domain.model.auth.User;
import org.webcurator.domain.model.core.Permission;
import org.webcurator.domain.model.core.PermissionTemplate;
import org.webcurator.domain.model.core.Site;
import org.webcurator.domain.model.core.UrlPattern;
import org.webcurator.ui.admin.validator.TemplateValidatorHelper;

/**
 * Implementation of the PermissionTemplateManager interface.
 * @see PermissionTemplateManager
 * @author bprice
 */
public class PermissionTemplateManagerImpl implements PermissionTemplateManager {

    PermissionTemplateDAO permissionTemplateDAO;
    
    AuthorityManager authorityManager;
    
    public PermissionTemplateManagerImpl() {
        
    }

    public PermissionTemplate getTemplate(Long templateOid) {
        return permissionTemplateDAO.getTemplate(templateOid);
    }
    
    public Permission getPermission(Long permissionOid) {
        return permissionTemplateDAO.getPermission(permissionOid);
    }

    public List getTemplates(User loggedInUser) {
        if (authorityManager.hasPrivilege(loggedInUser, Privilege.PERMISSION_REQUEST_TEMPLATE, Privilege.SCOPE_ALL)) {
            //User can see and manage all Templates
            return permissionTemplateDAO.getAllTemplates();
        } else if (authorityManager.hasPrivilege(loggedInUser, Privilege.PERMISSION_REQUEST_TEMPLATE, Privilege.SCOPE_AGENCY)) {
            //User can only see and manage their own templates
            return permissionTemplateDAO.getTemplates(loggedInUser.getAgency().getOid());
        } else {
            //User can only see their own templates
            return permissionTemplateDAO.getTemplates(loggedInUser.getAgency().getOid());
        }
    }

    public void saveTemplate(PermissionTemplate permissionTemplate) {
        permissionTemplateDAO.saveOrUpdate(permissionTemplate); 
    }

    public void setPermissionTemplateDAO(PermissionTemplateDAO permissionTemplateDAO) {
        this.permissionTemplateDAO = permissionTemplateDAO;
    }

    public void setAuthorityManager(AuthorityManager authorityManager) {
        this.authorityManager = authorityManager;
    }

    public List getTemplateTypes() {
        List<String> types = new ArrayList<String>();
        types.add(PermissionTemplate.EMAIL_TYPE_TEMPLATE);
        types.add(PermissionTemplate.PRINT_TYPE_TEMPLATE);
        
        return types;
    }

    public PermissionTemplate completeTemplate(Long templateOid, User user, Long permissionOid) {
        PermissionTemplate template = getTemplate(templateOid);
        Permission perm = getPermission(permissionOid);
        
        Site site = perm.getSite();
        String siteName = site.getTitle();
        String contactName = perm.getAuthorisingAgent().getContact();
        String contactAddress = perm.getAuthorisingAgent().getAddress();
        Set<UrlPattern> urlPatterns = perm.getUrls();
        StringBuffer urlsPlain = new StringBuffer();
        StringBuffer urlsHTML = new StringBuffer();
        String delim = "";
        for (UrlPattern urlPattern:urlPatterns) {
            urlsPlain.append(delim);
            urlsPlain.append(urlPattern.getPattern());
            delim = "\n";
        }
        urlsHTML.append("<ul>");
        for (UrlPattern urlPattern:urlPatterns) {
            urlsHTML.append("<li>");
            urlsHTML.append(urlPattern.getPattern());
            urlsHTML.append("</li>");
        }
        urlsHTML.append("</ul>");
        
        TemplateValidatorHelper templateValidatorHelper = new TemplateValidatorHelper(template.getTemplate(),template.getTemplateType());
        Map <String, String>parameterMap =  new HashMap<String, String>();
        
        parameterMap.put("contact_name",contactName);
        parameterMap.put("contact_address",contactAddress);
        parameterMap.put("site_name",siteName);
        parameterMap.put("urls_plain",urlsPlain.toString());
        parameterMap.put("urls_html", urlsHTML.toString());
        parameterMap.put("user_name", user.getFirstname()+" "+user.getLastname());
        //TODO include user_position once it has been added to the database
        //"user_position"
        parameterMap.put("user_address", user.getAddress());
        parameterMap.put("user_phone", user.getPhone());
        parameterMap.put("user_email", user.getEmail());
        parameterMap.put("agency_name", user.getAgency().getName());
        parameterMap.put("agency_address", user.getAgency().getAddress());
        parameterMap.put("agency_phone", user.getAgency().getPhone());
        parameterMap.put("agency_url", user.getAgency().getAgencyURL());
        parameterMap.put("agency_logo_url", user.getAgency().getAgencyLogoURL());
        parameterMap.put("agency_email", user.getAgency().getEmail());
        parameterMap.put("agency_fax", user.getAgency().getFax());
        
        //Store the fully populated template text back into the template object
        String newTemplateText = templateValidatorHelper.parseTemplate(parameterMap);
        template.setParsedText(newTemplateText);
        return template;
    }

    public void delete(Long templateOid) {
        PermissionTemplate permTemp = permissionTemplateDAO.getTemplate(templateOid);
        //TODO add check for ownership of object before deleting
        permissionTemplateDAO.delete(permTemp);
    }

}
