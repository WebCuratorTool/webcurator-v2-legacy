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

public class HasAtLeastOnePrivTag extends TagSupport {

    private static final long serialVersionUID = -1672856448368726571L;
    
    private String privileges = null;
    private AuthorityManager authorityManager = new AuthorityManagerImpl();
    
    @Override
    public int doStartTag() throws JspException {  
        String[] privArray = getPrivilegeKeys();
        if (authorityManager.hasAtLeastOnePrivilege(privArray)) {
            return TagSupport.EVAL_BODY_INCLUDE;
        }
       	return TagSupport.SKIP_BODY;
    }
    
    private String[] getPrivilegeKeys() {
       String[] privArray = null;
       String privs = getPrivileges();
       if (privs != null) {
           StringTokenizer st = new StringTokenizer(privs,",");
           privArray = new String[st.countTokens()];
           int i=0;
           while (st.hasMoreElements() == true) {
               String privKey = st.nextToken();
               privArray[i++] = privKey;
           }
       }
       return privArray;  
    }
    
    @Override
    public int doEndTag() throws JspException {
        return TagSupport.EVAL_PAGE;
    }

    public String getPrivileges() {
        return privileges;
    }

    public void setPrivileges(String privileges) {
        this.privileges = privileges;
    }

    

}

