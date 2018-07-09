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
package org.webcurator.core.util;

/**
 * A class to encapsulate a web service end point. This is used to reduce
 * duplication in the Spring configuration files.
 * @author beaumontb
 *
 */
public class WebServiceEndPoint {
	/** the host of the web service */
	private String host = null;
	/** The port number */
	private int port = 0;
	/** The name of the service */
	private String service = null;
	
	/**
	 * No-arg constructor to allow Spring method-based injection.
	 */
	public WebServiceEndPoint() {}
	
	/**
	 * Complete constructor for the WebServiceEndPoint.
	 * @param host    The WS host.
	 * @param port    The WS port.
	 * @param service The WS service name.
	 */
	public WebServiceEndPoint(String host, int port, String service) {
		this.host = host;
		this.port = port;
		this.service = service;
	}
	/**
	 * @return the host
	 */
	public String getHost() {
		return host;
	}
	/**
	 * @param host the host to set
	 */
	public void setHost(String host) {
		this.host = host;
	}
	/**
	 * @return the port
	 */
	public int getPort() {
		return port;
	}
	/**
	 * @param port the port to set
	 */
	public void setPort(int port) {
		this.port = port;
	}
	/**
	 * @return the service
	 */
	public String getService() {
		return service;
	}
	/**
	 * @param service the service to set
	 */
	public void setService(String service) {
		this.service = service;
	}
	
}
