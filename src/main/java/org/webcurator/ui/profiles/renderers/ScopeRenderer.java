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
import java.util.Iterator;
import java.util.List;

import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;

import org.archive.crawler.framework.CrawlScope;
import org.webcurator.core.profiles.ComplexProfileElement;
import org.webcurator.core.profiles.HeritrixProfile;
import org.webcurator.core.profiles.ProfileElement;
import org.webcurator.core.profiles.RendererManager;

/**
 * Renders a control to select another scope.
 * @author bbeaumont
 *
 */
public class ScopeRenderer extends ComplexTypeRenderer {

	/* (non-Javadoc)
	 * @see org.webcurator.ui.profiles.renderers.Renderer#render(org.webcurator.core.profiles.ProfileElement, javax.servlet.jsp.PageContext, org.webcurator.ui.profiles.renderers.RendererFilter)
	 */
	public void render(ProfileElement element, PageContext context, RendererFilter filter) throws IOException {
		// Get the writer.
		JspWriter out = context.getOut();
		
		ComplexProfileElement complexElement = (ComplexProfileElement) element;
		
		String currentScope = element.getValue().getClass().getName();
		
		out.print("<div class=\"profileMainHeading\">");
		out.print("<a href=\"javascript:maximise('sub_"+ element.getAbsoluteName() +"')\">Max</a> ");
		out.print("<a href=\"javascript:minimise('sub_"+ element.getAbsoluteName() +"')\">Min</a> ");
		
		
		out.print(element.getName() + "</div>");
		out.println("<div id=\"sub_"+element.getAbsoluteName()+"\" class=\"profileSublevel\">");		

		// Render the Change Scope Drop-down
		out.print("<select id=\"" + element.getAbsoluteName() + ".type\">");

		Iterator it = HeritrixProfile.getOptionsForType(CrawlScope.class).iterator();
		while(it.hasNext()) {
			String type = (String) it.next();
			out.print("<option value=\"" + type + "\"");
			
			if(currentScope.equals(type)) {
				out.print(" SELECTED");
			}
			
			out.println(">" + type + "</option>");
		}
		out.println("</select>");
		out.println("<input type=\"image\" src=\"images/subtabs-change-btn.gif\" style=\"vertical-align: bottom\" onclick=\"changeScope('"+ element.getAbsoluteName()+"');\">");		
		
		// Render all the simple elements.
		List<ProfileElement> simpleChildren = complexElement.getSimpleChildren();
		if(simpleChildren.size() > 0) {
			out.println("<table>");
			for(ProfileElement p: simpleChildren) {
				out.print("<tr><td>");
				out.print(p.getName());
				out.print("</td><td>");
				RendererManager.getRenderer(p).render(p, context);
				out.println("</td></tr>");
			}
			out.println("</table>");
		}

		// Render the items in the map.
		for(ProfileElement p: complexElement.getComplexChildren()) {
			if( filter.accepts(p)) {
				RendererManager.getRenderer(p).render(p, context, filter);
			}
		}
		
		out.println("</div>");

	}	
	
}
