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
package org.webcurator.core.common;

import java.security.NoSuchAlgorithmException;
import java.security.Security;

import javax.net.ssl.SSLContext;
import javax.servlet.http.HttpServlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.webcurator.core.permissionmapping.PermissionMappingStrategy;
import org.webcurator.core.util.ApplicationContextFactory;

/**
 * A servlet that is loaded on startup to initialise the core.
 * @author nwaight
 */
public class StartupServlet extends HttpServlet {
    
    /** Serial Version UID. */
    private static final long serialVersionUID = 7300074460312541802L;
    private Logger log = LoggerFactory.getLogger(getClass());

    /** @see javax.servlet.Servlet#init(javax.servlet.ServletConfig). */
    public void init() {
        WebApplicationContext context = WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext());
        ApplicationContextFactory.setWebApplicationContext(context);
        
        // Initialise the permission mapping strategy.
        PermissionMappingStrategy strategy = (PermissionMappingStrategy) context.getBean("permissionMappingStrategy");
        PermissionMappingStrategy.setStrategy(strategy);
        
        // Add the default SSLContext provider for SSL, used by the ldaps protocol
        try {
			Security.addProvider(SSLContext.getDefault().getProvider());
		} catch (NoSuchAlgorithmException e) {
			log.warn("Error adding SSL Provider to startup servlet.", e);
		}   
    }

    /** @see javax.servlet.Servlet#destroy(). */
    public void destroy() {
    }
}
