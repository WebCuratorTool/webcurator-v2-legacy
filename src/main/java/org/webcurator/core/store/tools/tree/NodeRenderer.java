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

/**
 * Renders a Node to a JSP.
 * @author bbeaumont
 *
 */
public class NodeRenderer {
	
	/**
	 * Basic renderer that simply displays the node's display name.
	 * @param out The JspWriter.
	 * @param n   The node to output.
	 * @throws IOException if there are IO errors.
	 */
	public void renderDisplayName(JspWriter out, Node n) throws IOException {
		out.print(n.getDisplayName());
	}

}
