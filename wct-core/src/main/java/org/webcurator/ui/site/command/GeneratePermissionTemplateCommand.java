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
package org.webcurator.ui.site.command;

/**
 * Command object used for generating a harvest authorisations permission template.
 * @author bbeaumont
 */
public class GeneratePermissionTemplateCommand {

    public static final String MDL_TEMPLATES = "templates";
    public static final String MDL_TEMPLATE = "template";
    
    public static final String PARAM_SITE_OID = "siteOid";
    public static final String PARAM_ACTION = "action";
    public static final String PARAM_TEMPLATE_OID = "templateOid";
    public static final String PARAM_PERMISSION_OID = "permissionOid";
    
    public static final String ACTION_GENERATE_TEMPLATE = "generateTemplate";
    public static final String ACTION_SEND_EMAIL = "sendEmail";
    public static final String ACTION_PRINTIT = "printIt";
       
    private Long siteOid;
    private String action;
    private Long templateOid;
    private Long permissionOid;
    
    public GeneratePermissionTemplateCommand() {

    }

    public Long getSiteOid() {
        return siteOid;
    }

    public void setSiteOid(Long siteOid) {
        this.siteOid = siteOid;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Long getPermissionOid() {
        return permissionOid;
    }

    public void setPermissionOid(Long permissionOid) {
        this.permissionOid = permissionOid;
    }

    public Long getTemplateOid() {
        return templateOid;
    }

    public void setTemplateOid(Long templateOid) {
        this.templateOid = templateOid;
    }

}
