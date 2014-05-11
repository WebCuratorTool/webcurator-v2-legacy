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
package org.webcurator.core.store.tools;

import java.io.IOException;

import javax.servlet.jsp.JspWriter;

import org.webcurator.core.store.tools.tree.Node;
import org.webcurator.core.store.tools.tree.NodeRenderer;

/**
 * WCTNode implementation of the NodeRender.
 * 
 * @see org.webcurator.core.store.tools.tree.NodeRenderer
 * @author bbeaumont
 *
 */
public class WCTNodeRenderer extends NodeRenderer {
	
	/* (non-Javadoc)
	 * @see org.webcurator.core.store.tools.tree.NodeRenderer#renderDisplayName(javax.servlet.jsp.JspWriter, org.webcurator.core.store.tools.tree.Node)
	 */
	public void renderDisplayName(JspWriter out, Node n) throws IOException {
		WCTNode node = (WCTNode) n;
		if( node.isMarkedForDelete() && node.isMarkedForImport()) {
			out.print("<span style=\"color: red;text-decoration: line-through;\">");
			out.print(n.getDisplayName());
			out.print("</span>");
		} else if( node.isMarkedForDelete()) {
			out.print("<span style=\"text-decoration: line-through;\">");
			out.print(n.getDisplayName());
			out.print("</span>");
		} else if( node.isMarkedForImport()) {
			out.print("<span style=\"color: red;\">");
			out.print(n.getDisplayName());
			out.print("</span>");
		}
		else {
			out.print(n.getDisplayName());
		}
	}
}
