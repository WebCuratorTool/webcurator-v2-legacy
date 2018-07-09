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
package org.webcurator.ui.admin.command;

/**
 * The command object for creating permission templates.
 * @author bprice
 */
public class TemplateCommand {
	/** the constant name of the templates model object. */
    public static final String MDL_TEMPLATES = "templates";
    /** the constant name of the agencies model object. */
    public static final String MDL_AGENCIES = "agencies";
    /** the constant name of the template types model object. */
    public static final String MDL_TEMPLATE_TYPES = "templateTypes";
    
    public static final String PARAM_ACTION = "action";
    public static final String PARAM_OID = "oid";
    public static final String PARAM_TEMPLATE_TYPE = "templateType";
    public static final String PARAM_TEMPLATE_TEXT = "templateText";
    public static final String PARAM_AGENCY_OID = "agencyOid";
    public static final String PARAM_TEMPLATE_NAME = "templateName";
    public static final String PARAM_TEMPLATE_DESCRIPTION = "templateDescription";
    
    public static final String PARAM_TEMPLATE_SUBJECT = "templateSubject";
    public static final String PARAM_TEMPLATE_OVERWRITE_FROM = "templateOverwriteFrom";
    public static final String PARAM_TEMPLATE_FROM = "templateFrom";
    public static final String PARAM_TEMPLATE_CC = "templateCc";
    public static final String PARAM_TEMPLATE_BCC = "templateBcc";
    public static final String PARAM_TEMPLATE_REPLY_TO = "replyTo";
    public static final String PARAM_EMAIL_TYPE_TEXT = "emailTypeText";
    
    public static final String ACTION_NEW = "new";
    public static final String ACTION_SAVE = "save";
    public static final String ACTION_VIEW = "view";
    public static final String ACTION_EDIT = "edit";
    public static final String ACTION_DELETE = "delete";
    
    private String action;
    private String templateType;
    private String templateText;
    private String templateName;
    private String templateDescription;
    private Long agencyOid;
    private Long oid;
    private String templateSubject;
    private boolean templateOverwriteFrom;
    private String templateFrom;
    private String templateCc;
    private String templateBcc;
    private String emailTypeText;
    private String replyTo;
    
    
    /** Default Constructor. */
    public TemplateCommand() {
    }

	/**
	 * @return the action
	 */
	public String getAction() {
		return action;
	}

	/**
	 * @param action the action to set
	 */
	public void setAction(String action) {
		this.action = action;
	}

	/**
	 * @return the agencyOid
	 */
	public Long getAgencyOid() {
		return agencyOid;
	}

	/**
	 * @param agencyOid the agencyOid to set
	 */
	public void setAgencyOid(Long agencyOid) {
		this.agencyOid = agencyOid;
	}

	/**
	 * @return the oid
	 */
	public Long getOid() {
		return oid;
	}

	/**
	 * @param oid the oid to set
	 */
	public void setOid(Long oid) {
		this.oid = oid;
	}

	/**
	 * @return the templateDescription
	 */
	public String getTemplateDescription() {
		return templateDescription;
	}

	/**
	 * @param templateDescription the templateDescription to set
	 */
	public void setTemplateDescription(String templateDescription) {
		this.templateDescription = templateDescription;
	}

	/**
	 * @return the templateName
	 */
	public String getTemplateName() {
		return templateName;
	}

	/**
	 * @param templateName the templateName to set
	 */
	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	/**
	 * @return the templateText
	 */
	public String getTemplateText() {
		return templateText;
	}

	/**
	 * @param templateText the templateText to set
	 */
	public void setTemplateText(String templateText) {
		this.templateText = templateText;
	}

	/**
	 * @return the templateType
	 */
	public String getTemplateType() {
		return templateType;
	}

	/**
	 * @param templateType the templateType to set
	 */
	public void setTemplateType(String templateType) {
		this.templateType = templateType;
	}
	
	/**
	 * @return the templateSubject
	 */
    public String getTemplateSubject() {
        return templateSubject;
    }

	/**
	 * @param templateSubject the templateSubject to set
	 */
    public void setTemplateSubject(String templateSubject) {
        this.templateSubject = templateSubject;
    }
    
	/**
	 * @return the templateOverwriteFrom
	 */
    public boolean getTemplateOverwriteFrom() {
        return templateOverwriteFrom;
    }

	/**
	 * @param templateOverwriteFrom the templateOverwriteFrom to set
	 */
    public void setTemplateOverwriteFrom(boolean templateOverwriteFrom) {
        this.templateOverwriteFrom = templateOverwriteFrom;
    }
    
	/**
	 * @return the templateFrom
	 */
    public String getTemplateFrom() {
        return templateFrom;
    }

	/**
	 * @param templateFrom the templateFrom to set
	 */
    public void setTemplateFrom(String templateFrom) {
        this.templateFrom = templateFrom;
    }
    
	/**
	 * @return the templateCc
	 */
    public String getTemplateCc() {
        return templateCc;
    }

	/**
	 * @param templateCc the templateCc to set
	 */
    public void setTemplateCc(String templateCc) {
        this.templateCc = templateCc;
    }
    
    
	/**
	 * @return the templateBcc
	 */
    public String getTemplateBcc() {
        return templateBcc;
    }

	/**
	 * @param templateBcc the templateBcc to set
	 */
    public void setTemplateBcc(String templateBcc) {
        this.templateBcc = templateBcc;
    }
    
    /**
	 * @return the emailTypeText
	 */
    public String getEmailTypeText() {
        return emailTypeText;
    }

	/**
	 * @param templateBcc the templateBcc to set
	 */
    public void setEmailTypeText(String emailTypeText) {
        this.emailTypeText = emailTypeText;
    }

	public void setReplyTo(String replyTo) {
		this.replyTo = replyTo;
	}

	public String getReplyTo() {
		return replyTo;
	}

    

}
