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
package org.webcurator.core.store;

/**
 * The config parameters for the digital asset store.
 * @author nwaight
 */
public interface DigitalAssetStoreConfig {
	/** 
	 * Return the host name or ip address for the digital asset store.
	 * @return the host name or ip address
	 */
	String getHost();
	
	/**
	 * Return the port number that the digital asset store is listening on.
	 * @return the port number
	 */
	int getPort();
	
	/**
	 * Return the name of the asset store service.
	 * @return the service name
	 */
	String getAssetStoreServiceName();
	
	/**
	 * Return the asset stores log reader service name
	 * @return the service name
	 */
	String getLogReaderServiceName();
	
}
