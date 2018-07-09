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
import java.text.SimpleDateFormat;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.lang.StringEscapeUtils;
import org.webcurator.domain.model.core.HarvestResult;

/**
 * Tag for generating a HarvestResult Chain in the SIP. This section
 * of the SIP is abstracted into a tag due to its recursive nature.
 * @author beaumontb
 */
public class HarvestResultChain extends TagSupport {

	
	private static final SimpleDateFormat dateFormatter = new java.text.SimpleDateFormat("yyyy-MM-dd");
	
	/** Serializable */
	private static final long serialVersionUID = 1L;
	
	private List<HarvestResult> chain;

	@Override
	public int doStartTag() throws JspException {
		try {
			doIt(0);
			return TagSupport.SKIP_BODY;
		}
		catch(IOException ex) { 
			throw new JspException(ex);
		}
	}
	
	public void setChain(List<HarvestResult> chain) {
		this.chain = chain;
	}
	
	public void doIt(int ix) throws JspException, IOException {
		JspWriter writer = pageContext.getOut();
		
		HarvestResult result = chain.get(ix);
		
		writer.println("<wct:HarvestResult>");
		writer.print("<wct:Creator>");
		writer.print(StringEscapeUtils.escapeXml(result.getCreatedBy().getUsername()) + " " + ix + "/" + chain.size());
		writer.println("</wct:Creator>");
		
		writer.print("<wct:CreationDate>");
		writer.print(dateFormatter.format(result.getCreationDate()));
		writer.println("</wct:CreationDate>");
		
		writer.print("<wct:ProvenanceNote>");
		writer.print(StringEscapeUtils.escapeXml(result.getProvenanceNote()));
		writer.println("</wct:ProvenanceNote>");

		if(!result.getModificationNotes().isEmpty()) {
			writer.println("<wct:ModificationNotes>");
			for(String note: result.getModificationNotes()) {
				writer.print("<wct:ModificationNote>");
				writer.print(StringEscapeUtils.escapeXml(note));
				writer.println("</wct:ModificationNote>");
			}
			writer.println("</wct:ModificationNotes>");
		}
		
		if((ix+1) < chain.size()) {
			writer.println("<wct:DerivedFrom>");
			doIt(ix+1);
			writer.println("</wct:DerivedFrom>");
		}
		
		writer.println("</wct:HarvestResult>");
	}
	
	

}
