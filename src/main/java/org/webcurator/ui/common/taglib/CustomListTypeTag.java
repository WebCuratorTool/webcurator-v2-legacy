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
package org.webcurator.ui.common.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import org.webcurator.core.common.WCTTreeSet;

/**
 * @author nwaight
 *
 */
public class CustomListTypeTag extends TagSupport {

	private WCTTreeSet list;
	
	private String paramName;
	
	private String currentValue;
	
	private String onChangeFunction = null;
	
	private static final long serialVersionUID = -4623821725094230130L;
	
	@Override
    public int doStartTag() throws JspException {
		JspWriter writer = pageContext.getOut();
		try {
			
			
			WCTTreeSet entries = list.getCopy();
			entries.add(currentValue);
			
			if(onChangeFunction != null && !onChangeFunction.isEmpty())
			{
				writer.println("<select id=\"" + paramName + "\" name=\"" + paramName + "\" onchange=\""+onChangeFunction+"\">");
			}
			else
			{
				writer.println("<select id=\"" + paramName + "\" name=\"" + paramName + "\">");
			}
			
			for (String entry : entries) {
				if (entry.equals(currentValue)) {
					writer.println("<option value=\"" + entry +"\" selected>" + entry +"</option>");
				}
				else {
					writer.println("<option value=\"" + entry +"\">" + entry +"</option>");
				}
			}
			writer.println("</select>");
		} 
		catch (IOException e) {
			throw new JspException(e.getMessage(), e);
		}
		
		return TagSupport.SKIP_BODY;
    }
    
    @Override
    public int doEndTag() throws JspException {
    	return TagSupport.EVAL_PAGE;
    }

	/**
	 * @return the currentValue
	 */
	public String getCurrentValue() {
		return currentValue;
	}

	/**
	 * @param currentValue the currentValue to set
	 */
	public void setCurrentValue(String currentValue) {
		this.currentValue = currentValue;
	}

	/**
	 * @return the list
	 */
	public WCTTreeSet getList() {
		return list;
	}

	/**
	 * @param list the list to set
	 */
	public void setList(WCTTreeSet list) {
		this.list = list;
	}

	/**
	 * @return the paramName
	 */
	public String getParamName() {
		return paramName;
	}

	/**
	 * @param paramName the paramName to set
	 */
	public void setParamName(String paramName) {
		this.paramName = paramName;
	}

	/**
	 * @param onChangeFunction the onChangeFunction to set
	 */
	public void setOnChangeFunction(String onChangeFunction) {
		this.onChangeFunction = onChangeFunction;
	}

	/**
	 * @return the onChangeFunction
	 */
	public String getOnChangeFunction() {
		return onChangeFunction;
	}
}
