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
 * A filter that doesn't actually filter anything out. This filter
 * allows the rendering API to require a filter, even if that filter
 * doesn't actually do anything.
 * @author bbeaumont
 *
 */
public class AcceptAllRendererFilter implements RendererFilter {

	/* (non-Javadoc)
	 * @see org.webcurator.ui.profiles.renderers.RendererFilter#accepts(org.webcurator.core.profiles.ProfileElement)
	 */
	public boolean accepts(ProfileElement anElement) {
		return true;
	}

}
