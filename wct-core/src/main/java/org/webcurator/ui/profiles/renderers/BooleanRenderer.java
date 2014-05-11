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

/**
 * Renders a boolean Profile Element with a true/false drop-down.
 * 
 * @see org.webcurator.ui.profiles.renderers.Renderer
 * 
 * @author bbeaumont
 */
public class BooleanRenderer extends Renderer {

	public void render(ProfileElement element, PageContext context, RendererFilter filter)
			throws IOException {
		JspWriter out = context.getOut();
		
		//out.print(element.getName());
		//out.print(":");
		out.print("<select name=\"");
		out.print(element.getAbsoluteName());
		out.println("\">");
		out.print("<option value=\"true\"");
		if(element.getValue().equals(Boolean.TRUE)) {
			out.print(" SELECTED ");
		}
		out.println(">true</option>");
		out.print("<option value=\"false\"");
		if(element.getValue().equals(Boolean.FALSE)) {
			out.print(" SELECTED ");
		}
		out.println(">false</false>");
		out.print("</select><br/>");
	}

}
