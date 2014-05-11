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

import java.io.IOException;

import javax.servlet.jsp.PageContext;

import org.webcurator.core.profiles.ProfileElement;


/**
 * <code>Renderer</code> is an abstract class that takes a profile elements 
 * and prints it to a JSP. 
 * @author bbeaumont
 *
 */
public abstract class Renderer {
	
	/**
	 * Render the given element to the JSP. Simply calls 
	 * render(ProfileElement, PageContext, RendererFilter) with an instance
	 * of the AcceptAllRendererFilter.
	 * 
	 * @see #render(ProfileElement, PageContext, RendererFilter)
	 * @see AcceptAllRendererFilter
	 * 
	 * @param element The element to render.
	 * @param context The Page Context of the JSP.
	 * @throws IOException if there are any IO errors.
	 */
	public void render(ProfileElement element, PageContext context) throws IOException {
		render(element, context, new AcceptAllRendererFilter());
	}
	
	/**
	 * Get the title of the element.
	 * @param element The element.
	 * @return The title.
	 */
	public String getTitle(ProfileElement element) {
		return element.getName();
		
	}
	
	/**
	 * Render the given element to the JSP.
	 * @param element The element to render.
	 * @param context The Page Context of the JSP.
	 * @param filter  The filter to use to limit which items are rendered.
	 * @throws IOException if there are any IO errors.
	 */	
	public abstract void render(ProfileElement element, PageContext context, RendererFilter filter) throws IOException;
}
