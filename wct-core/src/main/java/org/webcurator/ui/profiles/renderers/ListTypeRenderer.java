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

import org.archive.crawler.settings.ListType;
import org.archive.util.TextUtils;
import org.webcurator.core.profiles.ProfileElement;

/**
 * Render a List Type.
 * @author bbeaumont
 *
 */
public class ListTypeRenderer extends Renderer {

	/* (non-Javadoc)
	 * @see org.webcurator.ui.profiles.renderers.Renderer#render(org.webcurator.core.profiles.ProfileElement, javax.servlet.jsp.PageContext, org.webcurator.ui.profiles.renderers.RendererFilter)
	 */
	public void render(ProfileElement element, PageContext context, RendererFilter filter) throws IOException {
		JspWriter out = context.getOut();
		
		ListType list = (ListType) element.getValue();
	
		out.println("<input type=\"hidden\" name=\"" + element.getAbsoluteName() + "\" id=\"" + element.getAbsoluteName() + "\">");
		out.println("<select multiple id=\"" + element.getAbsoluteName() + ".list\" size=\"5\" style=\"width: 500px\">");
		for(int i=0 ; i<list.size() ; i++){
            out.println("<option value='" + list.get(i) +"'>"+list.get(i)+"</option>");
        }
		out.println("</select>");

		out.println("<a  onclick=\"removeFromList('"+element.getAbsoluteName()+"');\"><image src=\"images/action-icon-delete.gif\" alt=\"Remove\"></a><br/>");
		
		out.print("<input id=\"");
		out.print(element.getAbsoluteName() + ".new");
		out.print("\" type=\"text\">");
		out.println("<a onclick=\"addToList('"+element.getAbsoluteName()+"');\"><image src=\"images/subtabs-add-btn.gif\" style=\"vertical-align: bottom\" alt=\"Add\"></a>");

		
		// Set the text field.
		out.println("<script>");
		out.println("var val = document.getElementById('" + element.getAbsoluteName() + "').value;");
		for(int i=0 ; i<list.size() ; i++){
			if( i == 0) { 
				out.println("val = val + '" + TextUtils.escapeForHTMLJavascript(list.get(i).toString()) + "';");
			}
			else {
				out.println("val = val + '\\n' + '" + TextUtils.escapeForHTMLJavascript(list.get(i).toString()) +"';");
			}
        }
		out.println("document.getElementById('" + element.getAbsoluteName() + "').value = val;");		
		out.println("</script>");
		
		out.println("<br/>");
		
	}
	

}
