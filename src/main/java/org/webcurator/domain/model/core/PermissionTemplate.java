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
package org.webcurator.domain.model.core;

import org.webcurator.domain.model.auth.Agency;

/**
 * Models the permission request template used to generate
 * letters and emails to Authorisation Agents.
 * @author bprice
 * @hibernate.class table="PERMISSION_TEMPLATE" lazy="false"
 * @hibernate.query name="org.webcurator.domain.core.PermissionTemplate.getTemplatesByAgencyOid" query="from PermissionTemplate tem where tem.agency.oid=?"
 */
public class PermissionTemplate {
	/** Query identifier to get permission request templates by Agency OID */
    public static final String QRY_GET_TEMPLATES_BY_AGENCY = "org.webcurator.domain.core.PermissionTemplate.getTemplatesByAgencyOid";
    /** Type definition for E-mail templates */
    public static final String EMAIL_TYPE_TEMPLATE = "Email Template";
    /** Type definition for Print templates */
    public static final String PRINT_TYPE_TEMPLATE = "Print Template";
    
    /** The database OID of the template */
    private Long oid;
    /** The name of the template */
    private String templateName;
    /** The description of the template */
    private String templateDescription;
    /** The agency to which this template belongs */
    private Agency agency;
    /** The template text itself */
    private String template;
    /** The type of template. One of EMAIL_TYPE_TEMPLATE or PRINT_TYPE_TEMPLATE */
    private String templateType;
    /** The template text after place-holder substitution */
    private String parsedText;
    /** The subject of the Email*/
    private String templateSubject;
    /** A flag used to control if the from field of the email is overwritten by the templateOverwriteFrom*/
    private boolean templateOverwriteFrom;
    /** The email address used in the sent from field*/
    private String templateFrom;
    /** the email address(s) the email's are cc'd to*/
    private String templateCc;
    /** the email address(s) the email's are bcc'd to*/
    private String templateBcc;
    /** the replyto email address*/
	private String replyTo;
    
    /**
     * No-arg constructor
     */
    public PermissionTemplate() {
        super();
    }

    /**
     * gets the Agency this template belongs to
     * @return the owning Agency of the PermissionTemplate
     * @hibernate.many-to-one not-null="true" class="org.webcurator.domain.model.auth.Agency" column="PRT_AGC_OID" foreign-key="FK_TEMPLATE_AGENCY_OID"
     */
    public Agency getAgency() {
        return agency;
    }

    /**
     * Sets the agency this template belongs to.
     * @param agency The agency this template belongs to.
     */
    public void setAgency(Agency agency) {
        this.agency = agency;
    }

    /**
     * gets the Primary key of the PermissionTemplate
     * @return the PermissionTemplate oid
     * @hibernate.id column="PRT_OID" generator-class="org.hibernate.id.MultipleHiLoPerTableGenerator"
     * @hibernate.generator-param name="table" value="ID_GENERATOR"
     * @hibernate.generator-param name="primary_key_column" value="IG_TYPE"
     * @hibernate.generator-param name="value_column" value="IG_VALUE"
     * @hibernate.generator-param name="primary_key_value" value="PermissionTemplate"
     * @hibernate.generator-param name="max-lo" value="16"
     */
    public Long getOid() {
        return oid;
    }
    
    /**
     * Sets the primary key of the permission template.
     * @param oid The primary key of the permission template.
     */
    public void setOid(Long oid) {
        this.oid = oid;
    }

    /**
     * gets the specified template text
     * @return the template text
     * @hibernate.property column="PRT_TEMPLATE_TEXT" type="text" not-null="true" length="10000"
     */
    public String getTemplate() {
        return template;
    }

    /**
     * Sets the template text.
     * @param template The template text.
     */
    public void setTemplate(String template) {
        this.template = template;
    }

    /**
     * gets the template name
     * @return the name of the template
     * @hibernate.property column="PRT_TEMPLATE_NAME" not-null="true" length="80"
     */
    public String getTemplateName() {
        return templateName;
    }

    /**
     * Set the name of the template.
     * @param templateName The name of the template.
     */
    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    /**
     * gets the Template Type
     * @return the TemplateType
     * @hibernate.property column="PRT_TEMPLATE_TYPE" not-null="true" length="40"
     */
    public String getTemplateType() {
        return templateType;
    }

    /**
     * Set the type of the template.
     * @param templateType The type of the template. One of EMAIL_TYPE_TEMPLATE or PRINT_TYPE_TEMPLATE.
     */
    public void setTemplateType(String templateType) {
        this.templateType = templateType;
    }

    /**
     * gets the Template Description
     * @return the Template description text, describing what the template should be used for
     * @hibernate.property column="PRT_TEMPLATE_DESC" not-null="false" length="255"
     */
    public String getTemplateDescription() {
        return templateDescription;
    }

    /**
     * Set the description of the template 
     * @param templateDescription The description of the template.
     */
    public void setTemplateDescription(String templateDescription) {
        this.templateDescription = templateDescription;
    }

    /**
     * gets the parameter-substituted template text
     * @return the parsed text
     */
    public String getParsedText() {
        return parsedText;
    }
    
    /**
     * Set the parameter-substituted template text 
     * @param parsedText The parsed text of the template.
     */
    public void setParsedText(String parsedText) {
        this.parsedText = parsedText;
    }
    
    /**
     * gets the Template Subject
     * @return the Template Subject text, describing The subject of the Email
     * @hibernate.property column="PRT_TEMPLATE_SUBJECT" not-null="false" length="255"
     */
    public String getTemplateSubject() {
        return templateSubject;
    }

    /**
     * Set the template Subject 
     * @param templateSubject the subject of the Email.
     */
    public void setTemplateSubject(String templateSubject) {
        this.templateSubject = templateSubject;
    }
    
    /**
     * gets the templateOverwriteFrom
     * @return the templateOverwriteFrom text, a flag used to control if the from field of the email is overwritten by the templateOverwriteFrom
     * @hibernate.property column="PRT_TEMPLATE_OVERWRITE_FROM" not-null="true"
     */
    public boolean getTemplateOverwriteFrom() {
        return templateOverwriteFrom;
    }

    /**
     * Set the templateOverwriteFrom 
     * @param templateOverwriteFrom, a flag used to control if the from field of the email is overwritten by the templateOverwriteFrom.
     */
    public void setTemplateOverwriteFrom(boolean templateOverwriteFrom) {
        this.templateOverwriteFrom = templateOverwriteFrom;
    }
    
    /**
     * gets the templateFrom
     * @return the templateFrom text, describing the email address used in the sent from field
     * @hibernate.property column="PRT_TEMPLATE_FROM" not-null="false" length="255"
     */
    public String getTemplateFrom() {
        return templateFrom;
    }

    /**
     * Set the templateFrom 
     * @param templateFrom The email address used in the sent from field.
     */
    public void setTemplateFrom(String templateFrom) {
        this.templateFrom = templateFrom;
    }
    
    /**
     * gets the templateCc
     * @return the templateCc text, describing the email address(s) the emails are cc'd to
     * @hibernate.property column="PRT_TEMPLATE_CC" not-null="false" length="2048"
     */
    public String getTemplateCc() {
        return templateCc;
    }

    /**
     * Set the templateCc 
     * @param templateCc the email address(s) the email's are cc'd to.
     */
    public void setTemplateCc(String templateCc) {
        this.templateCc = templateCc;
    }
    
    
    /**
     * gets the templateBcc
     * @return the templateBcc text, describing the email address(s) the emails are bcc'd to
     * @hibernate.property column="PRT_TEMPLATE_BCC" not-null="false" length="2048"
     */
    public String getTemplateBcc() {
        return templateBcc;
    }

    /**
     * Set the templateBcc 
     * @param templateBcc the email address(s) the email's are bcc'd to.
     */
    public void setTemplateBcc(String templateBcc) {
        this.templateBcc = templateBcc;
    }

    /**
     * gets the reply-to address
     * @return the reply-to address
     * @hibernate.property column="PRT_TEMPLATE_REPLY_TO" not-null="false" length="255"
     */
	public String getReplyTo() {
		return replyTo;
	}

    /**
     * Set the reply to address (optional) 
     * @param replyTo the email address used for the reply-to
     */
	public void setReplyTo(String replyTo) {
		this.replyTo = replyTo;
	}
    

}
