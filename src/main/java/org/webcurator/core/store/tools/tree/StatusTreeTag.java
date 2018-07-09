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
package org.webcurator.core.store.tools.tree;

import java.io.IOException;

import javax.servlet.jsp.JspWriter;

import org.webcurator.core.store.tools.WCTNode;
import org.webcurator.core.util.ConverterUtil;
import org.webcurator.domain.model.core.ArcHarvestResource;
import org.webcurator.domain.model.core.HarvestResource;

public class StatusTreeTag extends TreeTag {

	/** Serialization ID */
	private static final long serialVersionUID = 1L;
	
	/* (non-Javadoc)
	 * @see org.webcurator.core.store.tools.tree.TreeTag#displayExtraInfo(javax.servlet.jsp.JspWriter, org.webcurator.core.store.tools.tree.Node)
	 */
	@Override
	public void displayExtraInfo(JspWriter out, Node node) throws IOException {
		WCTNode n = (WCTNode) node;
//		if( node.getSubject() instanceof org.webcurator.domain.model.core.HarvestResource) {
//			HarvestResource res = (HarvestResource) node.getSubject();
//		} else {
//			ArcHarvestResource res = (ArcHarvestResource) node.getSubject();
//		}
		HarvestResource res = (HarvestResource) node.getSubject();
		
		if(res != null) {
			out.println("  <td align=\"right\" style=\"padding: 0 6px 0 6px;\">"+res.getStatusCode()+"</td>");
			out.println("  <td align=\"right\" style=\"padding: 0 6px 0 6px;\">"+ConverterUtil.formatBytes(res.getLength())+"</td>");
		}
		else {
			out.println("  <td align=\"right\" style=\"padding: 0 6px 0 6px;\">&nbsp;</td>");
			out.println("  <td align=\"right\" style=\"padding: 0 6px 0 6px;\">&nbsp;</td>");	
		}
		
		out.println("  <td align=\"right\" style=\"padding: 0 6px 0 6px;\">"+n.getChildCount()+"</td>");
		out.println("  <td align=\"right\" align=\"right\" style=\"padding: 0 6px 0 6px;\">"+n.getTotalSuccessfulChildren()+"</td>");
		out.println("  <td align=\"right\" align=\"right\" style=\"padding: 0 6px 0 6px;\">"+n.getTotalFailedChildren()+"</td>");
		out.println("  <td align=\"right\" style=\"padding: 0 6px 0 6px;\">"+ConverterUtil.formatBytes(n.getTotalChildSize())+"</td>");
	}
	
	
	/* (non-Javadoc)
	 * @see org.webcurator.core.store.tools.tree.TreeTag#displayHeader(javax.servlet.jsp.JspWriter)
	 */
	@Override
	public void displayHeader(JspWriter out) throws IOException {
		out.println("<tr>");
		out.println("  <td style=\"padding: 0 6px 0 6px; font-weight:bold; width: 70%;\">Resource</td>");
		out.println("  <td style=\"padding: 0 6px 0 6px; font-weight:bold;\">Status</td>");
		out.println("  <td style=\"padding: 0 6px 0 6px; font-weight:bold;\">Size</td>");
		out.println("  <td style=\"padding: 0 6px 0 6px; font-weight:bold;\">Tot. URLs</td>");
		out.println("  <td style=\"padding: 0 6px 0 6px; font-weight:bold;\">Tot. Success</td>");
		out.println("  <td style=\"padding: 0 6px 0 6px; font-weight:bold;\">Tot. Failed</td>");
		out.println("  <td style=\"padding: 0 6px 0 6px; font-weight:bold;\">Tot. Size</td>");
		out.println("</tr>");
	}

}
