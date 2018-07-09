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
package org.webcurator.ui.profiles.renderers;

import java.util.LinkedList;
import java.util.List;

import org.webcurator.core.profiles.ProfileElement;

/**
 * Filter that accepts only the elements from the main part of the profile.
 * The constructor builds a list of items to be ignored. Any other item
 * will be included.
 * @author bbeaumont
 *
 */
public class GeneralOnlyRendererFilter implements RendererFilter {
	/** The list of elements to ignore */
	private List<String> ignoreElems = new LinkedList<String>();
	
	/**
	 * Constructor - builds the list of items to ignore.
	 */
	public GeneralOnlyRendererFilter() {
		ignoreElems.add("/crawl-order/scope");
		ignoreElems.add("/crawl-order/frontier");
		ignoreElems.add("/crawl-order/pre-fetch-processors");
		ignoreElems.add("/crawl-order/fetch-processors");
		ignoreElems.add("/crawl-order/extract-processors");
		ignoreElems.add("/crawl-order/write-processors");
		ignoreElems.add("/crawl-order/post-processors");
	}
	
	/* (non-Javadoc)
	 * @see org.webcurator.ui.profiles.renderers.RendererFilter#accepts(org.webcurator.core.profiles.ProfileElement)
	 */
	public boolean accepts(ProfileElement anElement) {
		return !ignoreElems.contains(anElement.getAbsoluteName());
	}

}
