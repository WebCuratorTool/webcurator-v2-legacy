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

import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

/**
 * 
 * @author nwaight
 */
public class WCTTreeSet implements Iterable<String> {	
	private static final long serialVersionUID = 921737102389342733L;
	/** The list. */
	private TreeSet<String> list;
	/** the max lenght allowed for an entry in the list. */
	private int maxLength = 0;
		
	/**
	 * Create a WCTTreeSet based on the list of Strings provided and 
	 * the max length setting.
	 * @param aEntrys the list of strings to add to the set
	 * @param aMaxLength the max length of entries in the set
	 */
	public WCTTreeSet(List<String> aEntrys, int aMaxLength) {
		maxLength = aMaxLength;
		list = new TreeSet<String>();
		for (String entry : aEntrys) {
			if (entry != null) {
				if (maxLength < 0 || entry.length() <= maxLength) {
					list.add(entry);
				}
			}
		}
	}
	
	private WCTTreeSet(TreeSet<String> aEntrys, int aMaxLength) {		
		list = aEntrys;
		maxLength = aMaxLength;
	}

	public void add(String aEntry) {
		if (aEntry != null) {
			if (maxLength < 0 || aEntry.length() <= maxLength) {
				list.add(aEntry);
			}
		}
	}
	
	public Iterator<String> iterator() {		
		return list.iterator();
	}
	
	public WCTTreeSet getCopy() {
		TreeSet<String> copy = new TreeSet<String>(list);
		return new WCTTreeSet(copy, maxLength);
	}
}
