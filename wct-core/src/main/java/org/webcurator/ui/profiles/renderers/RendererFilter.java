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

import org.webcurator.core.profiles.ProfileElement;

/**
 * A <code>RendererFilter</code> is used to filter out components
 * that should not be rendered. This can be used to ignore nested
 * elements that you want to display on a different page.
 * 
 * @author bbeaumont
 *
 */
public interface RendererFilter {
	/**
	 * The element to test for acceptance.
	 * @param anElement The element to test for acceptance. 
	 * @return true if the element is accepted; otherwise false.
	 */
	public boolean accepts(ProfileElement anElement);
}
