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

import java.util.HashMap;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import org.webcurator.auth.AuthorityManager;
import org.webcurator.auth.AuthorityManagerImpl;
import org.webcurator.core.util.AuthUtil;
import org.webcurator.domain.model.auth.RolePrivilege;
import org.webcurator.domain.model.auth.User;

/**
 * The authority:noPrivilege tag is used to display sections of a page
 * where the user doesn't have a specified Privilege.
 * This tag can be used in combination with the authority:hasPrivilege tag
 * similar to the c:choose c:otherwise tags provided by the core tag library. 
 * @author BPrice
 */
public class NoPrivilegeTag extends TagSupport {

    private static final long serialVersionUID = 8379450274642156262L;

    private String privilege = null;
    private int scope = 1000;
    private AuthorityManager authorityManager = new AuthorityManagerImpl();
    
    @Override
    public int doStartTag() throws JspException {
        User user = AuthUtil.getRemoteUserObject();
        HashMap privs = authorityManager.getPrivilegesForUser(user);
        if (privs.containsKey(getPrivilege())) {
            RolePrivilege rp = (RolePrivilege)privs.get(getPrivilege());
            int usersPrivScope = rp.getPrivilegeScope();
            if (usersPrivScope <= getScope()) {
                return TagSupport.SKIP_BODY;
            }
        }
        return TagSupport.EVAL_BODY_INCLUDE;
    }
    
    @Override
    public int doEndTag() throws JspException {
        return TagSupport.EVAL_PAGE;
    }

    public String getPrivilege() {
        return privilege;
    }

    public void setPrivilege(String privilege) {
        this.privilege = privilege;
    }

    public int getScope() {
        return scope;
    }

    public void setScope(int scope) {
        this.scope = scope;
    }

    

}
