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
package org.webcurator.auth.tag;

import java.util.StringTokenizer;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import org.webcurator.auth.AuthorityManager;
import org.webcurator.auth.AuthorityManagerImpl;
import org.webcurator.domain.AgencyOwnable;
import org.webcurator.domain.UserOwnable;

public class ShowControlTag extends TagSupport {
	private static AuthorityManager authorityManager = new AuthorityManagerImpl();
	private static final long serialVersionUID = 1L;

	
	private Object ownedObject = null;
	private String privileges = null;
	private boolean editMode = false;
	
	private boolean showControl = false;
	
	
	
    @Override
    public int doStartTag() throws JspException {  
    	StringTokenizer tokenizer = new StringTokenizer(privileges, ";");
    	String[] privs = new String[tokenizer.countTokens()];
    	for(int i=0; tokenizer.hasMoreTokens(); i++) {
    		privs[i] = tokenizer.nextToken();
    	}
    	
    	if(ownedObject instanceof UserOwnable) {
    		showControl = editMode && authorityManager.hasAtLeastOnePrivilege( (UserOwnable) ownedObject, privs);
    	}
    	else if(ownedObject instanceof AgencyOwnable) {
    		showControl = editMode && authorityManager.hasAtLeastOnePrivilege( (AgencyOwnable) ownedObject, privs);
    	}
    	else {
    		showControl = false;
    	}
    	// release the object (usually its a ti) from the tag to prevent a memory leak (Tags are pooled) 
    	ownedObject = null;
    	return TagSupport.EVAL_BODY_INCLUDE;
    }


    /**
	 * @return Returns the editMode.
	 */
	public boolean isEditMode() {
		return editMode;
	}


	/**
	 * @param editMode The editMode to set.
	 */
	public void setEditMode(boolean editMode) {
		this.editMode = editMode;
	}


	/**
	 * @return Returns the privileges.
	 */
	public String getPrivileges() {
		return privileges;
	}


	/**
	 * @param requiredPrivilege The privileges to set.
	 */
	public void setPrivileges(String requiredPrivilege) {
		this.privileges = requiredPrivilege;
	}


	/**
	 * @return Returns the userObject.
	 */
	public Object getOwnedObject() {
		return ownedObject;
	}


	/**
	 * @param userObject The userObject to set.
	 */
	public void setOwnedObject(Object userObject) {
		this.ownedObject = userObject;
	}


	/**
	 * @return Returns the showControl.
	 */
	public boolean isShowControl() {
		return showControl;
	}	

}
