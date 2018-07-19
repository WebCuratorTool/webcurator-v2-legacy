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

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.context.WebApplicationContext;

import org.webcurator.core.util.ApplicationContextFactory;
import org.webcurator.core.util.CookieUtils;
import org.webcurator.domain.UserRoleDAO;
import org.webcurator.domain.model.auth.User;


/**
 * The hook for allowing the result of authentication requests to be audited.
 * @author bprice
 */
public class WCTAuthenticationProcessingFilter extends UsernamePasswordAuthenticationFilter {

    private static Log log = LogFactory.getLog(WCTAuthenticationProcessingFilter.class);
    private UserRoleDAO authDAO = null;

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult)
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
        
	        // Get the Spring Application Context.
			WebApplicationContext ctx = ApplicationContextFactory.getWebApplicationContext();

			// set or re-set the page size cookie..
			// ..first get the value of the page size cookie
			String currentPageSize = CookieUtils.getPageSize(request);
			// ..then refresh the page size cookie, to expire in a year
			CookieUtils.setPageSize(response, currentPageSize);

        }
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest aReq, HttpServletResponse aRes, AuthenticationException e) throws IOException, ServletException {
        super.unsuccessfulAuthentication(aReq, aRes, e);
        
        String username = aReq.getParameter("j_username");
        
    }
    
    /**
     * Spring setter.
     * @param authDAO set the authentication dao bean.
     */
    public void setAuthDAO(UserRoleDAO authDAO) {
        this.authDAO = authDAO;
    }

}
