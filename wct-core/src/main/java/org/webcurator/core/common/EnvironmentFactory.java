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

import org.springframework.context.ApplicationContext;
import org.webcurator.core.util.ApplicationContextFactory;

/**
 * A factory method to provide access to the Core's Environment.
 * @author bbeaumont
 */
public class EnvironmentFactory {
	
	static Environment environment;
	/** 
	 * @return The Environment object of the Core.
	 */
	public static Environment getEnv() {
		if(environment==null) {
			ApplicationContext ctx = ApplicationContextFactory.getWebApplicationContext();
			environment = (Environment) ctx.getBean("environment");
		}
		return environment;
	}
	
	public static void setEnvironment(Environment e) {
		environment = e;
	}
}
