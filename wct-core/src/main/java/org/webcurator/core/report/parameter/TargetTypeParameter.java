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
package org.webcurator.core.report.parameter;

import java.io.IOException;

/**
 * Parameter for Target Types<br>
 * <br>
 * When rendered as a HTML input, it is displayed as a <code>SELECT</code> filled 
 * with target type names, with also the option to select 'All'.<br>
 * <br>As all other <code>ListParameter</code> parameters, the option selected 
 * is then seen as a <code>StringParameter</code>.  
 * See {@link org.webcurator.core.report.parameter.ListParameter#getType()}
 * 
 * @author oakleigh_sk
 *
 */
public class TargetTypeParameter extends ListParameter {

	/**
	 * Rendering as a <code>SELECT</code> filled with target type names.
	 * Contains the option 'All target types'
	 */ 
	@SuppressWarnings("unchecked")
	public String getInputRendering() throws IOException {
		StringBuffer sb = new StringBuffer();
		
		
		// SELECT box
		String selected = getSelectedValue() != null ? (String)getSelectedValue() : ""; 
		
		sb.append("<select name=\"parameters\">\n");
		sb.append("<option " + (selected.equals("All target types") ? " SELECTED " : "") + ">All target types</option>\n");
		sb.append("<option " + (selected.equals("Target") ? " SELECTED " : "") + ">Target</option>\n");
		sb.append("<option " + (selected.equals("Group") ? " SELECTED " : "") + ">Group</option>\n");
		sb.append("</select>");

		if(!optional){
			sb.append("<font color=red size=2>&nbsp;<strong>*</strong>&nbsp;</font>");
		}		
		if(optional){
			sb.append("<i><font size=\"1\">&nbsp;(Optional)</font></i>");
		}
		sb.append("\n");
		
		return sb.toString();
	}

}
