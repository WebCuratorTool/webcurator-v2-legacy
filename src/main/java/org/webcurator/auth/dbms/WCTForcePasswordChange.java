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
package org.webcurator.auth.dbms;

import java.io.IOException;
import java.security.AccessControlException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.acegisecurity.Authentication;
import org.acegisecurity.context.SecurityContextHolder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.webcurator.core.util.Auditor;
import org.webcurator.domain.model.auth.User;
import org.webcurator.ui.common.Constants;

/**
 * This filter is used by the Acegi security framework.  
 * The filter checks that the users password has expired or 
 * must be changed and redirects the user to the password
 * change view if nessacary.
 * @author bprice
 */
public class WCTForcePasswordChange implements Filter {

    /** Logger. */
    private Log log;
    /** object for creating audit entries. */
    private Auditor auditor;
    
    /** Default Constructor. */
    public WCTForcePasswordChange() {
        super();
        log = LogFactory.getLog(WCTForcePasswordChange.class);
    }

    /** @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain) */
    public void doFilter(ServletRequest aRequest, ServletResponse aResponse, FilterChain aChain) throws IOException, ServletException {
        if (log.isDebugEnabled()) {
            log.debug("Checking forced password change action.");
        }
        
        if (!(aRequest instanceof HttpServletRequest)) {
          throw new ServletException("Can only process HttpServletRequest");
      }
    
      if (!(aResponse instanceof HttpServletResponse)) {
          throw new ServletException("Can only process HttpServletResponse");
      }
    
      HttpServletRequest httpRequest = (HttpServletRequest) aRequest;
      
      Authentication auth =  SecurityContextHolder.getContext().getAuthentication();      
      if (auth != null) {            
        if (auth.isAuthenticated()) {
            User authUser = (User)auth.getDetails();

            if (authUser != null) {
              if (authUser.isForcePasswordChange() == true && authUser.isExternalAuth() == false) {
                                    
                  RequestDispatcher reqDisp = httpRequest.getRequestDispatcher("/"+Constants.CNTRL_RESET_PWD);
                  reqDisp.forward(aRequest, aResponse);  
                  auditor.audit(User.class.getName(),authUser.getOid(),Auditor.ACTION_FORCE_PWD_CHANGE,"User has been forced to change password");
              }
            }
        }
        else {
            throw new AccessControlException("The user is not authenticated correctly.");
        }
      }
     
      aChain.doFilter(aRequest, aResponse);
    }

    /** @see javax.servlet.Filter#init(javax.servlet.FilterConfig) */
    public void init(FilterConfig aConfig) throws ServletException {
    }
    
    /** @see javax.servlet.Filter#destroy() */
    public void destroy() {
    }

    /**
     * sets the Auditor bean to use for auditing of Password changes
     * @param auditor the Audit bean
     */
    public void setAuditor(Auditor auditor) {
        this.auditor = auditor;
    }
}
