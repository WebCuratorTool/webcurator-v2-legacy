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

import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;

import org.webcurator.core.profiles.ComplexProfileElement;
import org.webcurator.core.profiles.ProfileElement;

/**
 * Renders a map with simple values.
 * @author bbeaumont
 */
public class SimpleMapRenderer extends Renderer {

	/* (non-Javadoc)
	 * @see org.webcurator.ui.profiles.renderers.Renderer#render(org.webcurator.core.profiles.ProfileElement, javax.servlet.jsp.PageContext, org.webcurator.ui.profiles.renderers.RendererFilter)
	 */
	public void render(ProfileElement element, PageContext context, RendererFilter filter) throws IOException {
		// Get the writer.
		JspWriter out = context.getOut();
		
		out.println("<div class=\"profileMainHeading\">" + element.getName() + "</div>");
		out.println("<div class=\"profileSublevel\">");
		
		ComplexProfileElement complexElement = (ComplexProfileElement) element;
		
		// Render the component to add new items.
		
		out.println("<table><tr><td>");
		
		out.println("<input type=\"text\" name=\"" + element.getAbsoluteName() + ".key\">");
		
		out.println("</td><td>--&gt;</td><td>");
		
		out.println("<input type=\"text\" name=\"" + element.getAbsoluteName() + ".value\">");
		out.println("</td><td>");
		out.println("<input type=\"image\" src=\"images/subtabs-add-btn.gif\" style=\"vertical-align: bottom\" onclick=\"simpleMapAdd('"+ element.getAbsoluteName()+"');\">");
		
		out.println("</td></tr>");
		
		// Render the items in the map.
		for(ProfileElement p: complexElement.getSimpleChildren()) {
			out.println("<tr><td>");
			out.print(p.getName());
			out.println("</td><td>--&gt;</td><td>");
			out.println(p.getValue());
			out.println("</td><td>");
			out.print("<a href=\"javascript:mapAction('"+complexElement.getAbsoluteName()+"','"+p.getName()+"','remove')\">Remove</a>");
			out.println("</td></tr>");
		}
		
		out.println("</table>");
		out.println("</div>");
	}

}
