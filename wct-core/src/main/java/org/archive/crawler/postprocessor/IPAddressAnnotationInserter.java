/*
 *  Copyright 2011 The British Library
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
 *
 *  File:        IPAddressAnnotationInserter.java
 *  Author:      oakleigh_sk
 */
  
package org.archive.crawler.postprocessor;
  
import org.archive.crawler.datamodel.CrawlURI;
import org.archive.crawler.framework.Processor;

import org.webcurator.core.exceptions.ArgumentNotValid;
import org.apache.commons.httpclient.*; 

  
/**
 * A custom post processor that adds an annotation to the crawl log file;
 *   host-ip:<nnn.nnn.nnn.nnn>
 * for each successfully harvested URI.
 *
 */

@SuppressWarnings("serial")  
public class IPAddressAnnotationInserter extends org.archive.crawler.framework.Processor {
  
    /** Prefix associated with annotations made by this processor.*/
    public static final String IP_ADDRESS_ANNOTATION_PREFIX = "host-ip:";
  
    /**
     * Constructor.
     * @param name the name of the processor.
     * @see Processor
     */
    public IPAddressAnnotationInserter(String name) {
        super(name, "A post processor that adds an annotation"
                    + " host-ip:<bnnn.nnn.nnn.nnn> for each successfully harvested"
                    + " URI.");
    }
  
    /** For each URI with a successful status code (status code > 0),
     *  add annotation with host's IP address.
     * @param crawlURI URI to add annotation for if successful.
     * @throws ArgumentNotValid if crawlURI is null.
     * @throws InterruptedException never.
     * @see Processor#innerProcess(org.archive.crawler.datamodel.CrawlURI)
     */
    protected void innerProcess(CrawlURI crawlURI) throws InterruptedException {
        ArgumentNotValid.checkNotNull(crawlURI, "crawlURI");
        if (crawlURI.getFetchStatus() > 0) {
        	String hostName = null;
        	try {
				hostName = crawlURI.getBaseURI().getHost();
			} catch (URIException e) {
				//e.printStackTrace();
			}
        	
        	if (hostName != null) {
    			String ipAddress = this.getController().getServerCache().getHostFor(hostName).getIP().getHostAddress();
                crawlURI.addAnnotation(IP_ADDRESS_ANNOTATION_PREFIX + ipAddress);              
        	}
        }
    }
}
