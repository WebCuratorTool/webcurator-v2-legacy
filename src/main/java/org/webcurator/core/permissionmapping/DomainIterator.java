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
package org.webcurator.core.permissionmapping;

/**
 * The DomainIterator iterates through a hostname, progressively moving to
 * a wider grouping of domains.
 * 
 * For example, if you start with <code>www.alphabetsoup.com</code> you will
 * get three items out of the iterator:
 * <ul>
 *   <li>www.alphabetsoup.com</li>
 *   <li>alphabetsoup.com</li>
 *   <li>com</li>
 * </ul> 
 *
 * @author bbeaumont
 *
 */
public class DomainIterator {
	
	/** The host to iterator through */
	private String host = null;
	/** True once finished */
	private boolean finished = false;
	/** The index of the next dot */
	private int nextDotIndex = -1;
	
	
	/**
	 * Construct a new domain iterator.
	 * @param aHost The host to iterator through.
	 */
	public DomainIterator(String aHost) {
		host = aHost;
	}
	
	/**
	 * Returns true if there are any more elements.
	 * @return true if there are any more elements.
	 */
	public boolean hasNext() {
		return !finished && host.length() >= nextDotIndex + 1;
	}
	
	/**
	 * Returns the next '.' in the host string.
	 * @return the index of hte next dot in the host string.
	 */
	private int getNextDot() {
		return host.indexOf('.', nextDotIndex+1);
	}
	
	/**
	 * Get the next widest domain name.
	 * @return The next widest domain name.
	 */
	public String next() {
		String result = nextDotIndex == -1 ? host : host.substring(nextDotIndex + 1);
		nextDotIndex = getNextDot();
		finished = nextDotIndex < 0;
		return result;
	}
	
}

