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
package org.webcurator.auth;

import java.io.IOException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.acegisecurity.Authentication;
import org.acegisecurity.AuthenticationException;
import org.acegisecurity.context.SecurityContextHolder;
import org.acegisecurity.providers.UsernamePasswordAuthenticationToken;
import org.acegisecurity.ui.webapp.AuthenticationProcessingFilter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.web.context.WebApplicationContext;

import org.webcurator.core.common.Constants;
import org.webcurator.core.report.LogonDurationDAO;
import org.webcurator.core.util.ApplicationContextFactory;
import org.webcurator.core.util.Auditor;
import org.webcurator.core.util.CookieUtils;
import org.webcurator.domain.UserRoleDAO;
import org.webcurator.domain.model.auth.User;


/**
 * The hook for allowing the result of authentication requests to be audited.
 * @author bprice
 */
public class WCTAuthenticationProcessingFilter extends
        AuthenticationProcessingFilter {

    private static Log log = LogFactory.getLog(WCTAuthenticationProcessingFilter.class);
    private Auditor auditor = null;
    private UserRoleDAO authDAO = null;
     
    /** @see org.acegisecurity.ui.AbstractProcessingFilter#onSuccessfulAuthentication(HttpServletRequest,HttpServletResponse, Authentication) . */
    protected void onSuccessfulAuthentication(HttpServletRequest request,
            HttpServletResponse response, Authentication authResult)
            throws IOException {
        
        log.debug("calling onSuccessfulAuthentication for WCT");
        String userName = authResult.getName();
        
        User wctUser = authDAO.getUserByName(userName);
        
        if (wctUser != null) {
	        log.debug("loaded WCT User object "+wctUser.getUsername()+" from database");
	        UsernamePasswordAuthenticationToken auth =  (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
	        auth.setDetails(wctUser);
	        log.debug("pushing back upat into SecurityContext with populated WCT User");
	        SecurityContextHolder.getContext().setAuthentication(auth);
        
	        //audit successful login event
	        auditor.audit(User.class.getName(), wctUser.getOid(), Auditor.ACTION_LOGIN_SUCCESS, "Successful Login for username: "+wctUser.getUsername());
	
	        // Get the Spring Application Context.
			WebApplicationContext ctx = ApplicationContextFactory.getWebApplicationContext();

			// set or re-set the page size cookie..
			// ..first get the value of the page size cookie
			String currentPageSize = CookieUtils.getPageSize(request);
			// ..then refresh the page size cookie, to expire in a year
			CookieUtils.setPageSize(response, currentPageSize);

	        // set login for duration
	        String sessionId = request.getSession().getId();
	        LogonDurationDAO logonDurationDAO = (LogonDurationDAO) ctx.getBean(Constants.BEAN_LOGON_DURATION_DAO);
	       	logonDurationDAO.setLoggedIn(sessionId, new Date(), wctUser.getOid(), wctUser.getUsername(), wctUser.getNiceName());
	       	
			// Check previous records of duration
	       	logonDurationDAO.setProperLoggedoutForCurrentUser(wctUser.getOid(), sessionId);
	       	
		}  else {
            
            //audit successful login but unsucessful load of WCT User event
            auditor.audit(User.class.getName(), Auditor.ACTION_LOGIN_FAILURE_NO_USER, "Un-successful login for username: "+userName+" as user doesn't exist in the WCT System.");

        }
    }

    /** @see org.acegisecurity.ui.AbstractProcessingFilter#onUnsuccessfulAuthentication(HttpServletRequest,HttpServletResponse, AuthenticationException) . */
    @Override
    protected void onUnsuccessfulAuthentication(HttpServletRequest aReq, HttpServletResponse aRes, AuthenticationException e) throws IOException {
        super.onUnsuccessfulAuthentication(aReq, aRes, e);
        
        String username = aReq.getParameter("j_username");
        
        //audit failed login event
        auditor.audit(User.class.getName(), Auditor.ACTION_LOGIN_FAILURE, "Failed Login for username: "+username);
    }
    
    /**
     * Spring setter.
     * @param authDAO set the authentication dao bean.
     */
    public void setAuthDAO(UserRoleDAO authDAO) {
        this.authDAO = authDAO;
    }
    
    /** 
     * Spring setter 
     * @param auditor set the auditor bean
     */
    public void setAuditor(Auditor auditor) {
        this.auditor = auditor;
    }
}
