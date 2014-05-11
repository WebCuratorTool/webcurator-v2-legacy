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

import org.webcurator.core.profiles.ProfileElement;
import org.webcurator.core.profiles.SimpleProfileElement;

/**
 * Renders a profile element as a drop-down with a set of options.
 * @author bbeaumont
 *
 */
public class OptionRenderer extends Renderer {

	/* (non-Javadoc)
	 * @see org.webcurator.ui.profiles.renderers.Renderer#render(org.webcurator.core.profiles.ProfileElement, javax.servlet.jsp.PageContext, org.webcurator.ui.profiles.renderers.RendererFilter)
	 */
	public void render(ProfileElement element, PageContext context, RendererFilter filter) throws IOException {
		JspWriter out = context.getOut();
		
		//out.print(element.getName());
		//out.print(":");
		if (((SimpleProfileElement) element).isTransient()) {
			out.println(element.getValue()+"<br/>\n");
		} else {
			out.print("<select name=\"");
			out.print(element.getAbsoluteName());
			out.println("\">");
			
			// Get the options.
			SimpleProfileElement simpleElement = (SimpleProfileElement) element;
			Object[] legalValues = simpleElement.getLegalValues();
			
	        //Have legal values. Build combobox.
	        for(int i=0 ; i < legalValues.length ; i++){
	            out.print("<option value=\""+legalValues[i]+"\"");
	            if(element.getValue().equals(legalValues[i])){
	                out.print(" selected");
	            }
	            out.println(">"+legalValues[i]+"</option>\n");
	        }
	        out.println("</select><br/>\n");		
		}
	}

}
