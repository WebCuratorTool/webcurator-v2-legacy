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

public class HasPrivilegeTag extends TagSupport {

    private static final long serialVersionUID = -8708719193688369742L;

    private String privilege = null;
    private int scope = 1000;
    private AuthorityManager authorityManager = new AuthorityManagerImpl();
    
    @Override
    public int doStartTag() throws JspException {
        if (hasPrivilege(getPrivilege(),getScope()) == true) {
            return TagSupport.EVAL_BODY_INCLUDE;
        } else {
            return TagSupport.SKIP_BODY;
        }
    }
    
    /**
     * Helper method for directly finding out if the logged in User has
     * a Privilege with the appropriate scope level.
     * @param privilege the Privilege to check
     * @param scope the scope of the Privilege
     * @return true if the User has the Privilege
     */
    public boolean hasPrivilege(String privilege, int scope) {
        User user = AuthUtil.getRemoteUserObject();
        HashMap privs = authorityManager.getPrivilegesForUser(user);
        if (privs.containsKey(privilege)) {
            RolePrivilege rp = (RolePrivilege)privs.get(privilege);
            int usersPrivScope = rp.getPrivilegeScope();
            if (usersPrivScope <= scope) {
                return true;
            }
        }
        return false;
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
