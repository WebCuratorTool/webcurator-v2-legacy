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
import java.util.Iterator;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * This tag is used to support the generic display and management of a
 * NodeTree.
 * 
 * @author bbeaumont
 *
 */
public class TreeTag extends TagSupport {
	/** Version ID for serialisation. */
	private static final long serialVersionUID = -210301172665342852L;

	/** The image for spacing */
	private static final String IMAGE_EMPTY = "./images/empty.gif";
	/** The image for a vertical line */
	private static final String IMAGE_JOIN_VERT_LINE = "./images/vertline.gif";
	/** The image for the last of a parent's children when that child is a leaf node */
	private static final String IMAGE_JOIN_END_WO_CHILDREN = "./images/ic_tree_end.gif";
	/** The image for the last of a parent's children when that child has children and is expanded */
	private static final String IMAGE_JOIN_END_WITH_CHILDREN_EXP = "./images/ic_tree_end_children_minus.gif";
	/** The image for the last of a parent's children when that child has children and is collapsed */
	private static final String IMAGE_JOIN_END_WITH_CHILDREN_COL = "./images/ic_tree_end_children_plus.gif";
	/** The image for a child leaf node when that child is not the last child of its parent. */
	private static final String IMAGE_JOIN_WO_CHILDREN = "./images/ic_tree_mid_no_children.gif";
	/** The image for an expanded child node when that child is not the last child of its parent. */
	private static final String IMAGE_JOIN_WITH_CHILDREN_EXP = "./images/ic_tree_mid_minus.gif";
	/** The image for an collapsed child node when that child is not the last child of its parent. */
	private static final String IMAGE_JOIN_WITH_CHILDREN_COL = "./images/ic_tree_mid_plus.gif";
	/** The image for a closed folder. */
	private static final String IMAGE_FOLDER_CLOSED = "./images/folder.gif";
	/** The image for an open folder. */
	private static final String IMAGE_FOLDER_OPEN = "./images/folder_open.gif";
	/** The image for a resource. */
	private static final String IMAGE_RESOURCE = "./images/resource.gif";

	/** The tree to be displayed */
	private NodeTree tree = null;
        
        private int rowAlt = 0;

	/**
	 * Get the tree.
	 * @return The tree.
	 */
	public NodeTree getTree() {
		return tree;
	}

	/**
	 * Set the tree.
	 * @param tree The tree.
	 */
	public void setTree(NodeTree tree) {
		this.tree = tree;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.jsp.tagext.Tag#doStartTag()
	 */
	@SuppressWarnings("unchecked")
	public int doStartTag() throws JspException {
            rowAlt = 0;
		try {
			
			//NodeTree theTree = (NodeTree) ExpressionUtil.evalNotNull("tree", "tree", tree, NodeTree.class, this, pageContext);
			NodeTree theTree = getTree();
			Iterator<Node> rootIterator = theTree.getRootNodes().iterator();
			
			pageContext.getOut().println("<table cellspacing=\"0\" cellpadding=\"0\">");
			
			displayHeader(pageContext.getOut());
			
			while (rootIterator.hasNext()) {
				display(pageContext.getOut(), rootIterator.next(), 0);
			}
			pageContext.getOut().println("</table>");
		} 
		catch (IOException ex) {
			throw new JspException(ex.getMessage(), ex);
		}

		// Never process the body.
		return TagSupport.SKIP_BODY;
	}

	/**
	 * Overwrite this method if you want a header row.
	 */
	public void displayHeader(JspWriter out) throws IOException {
		
	}
	
	/**
	 * Write the tree to the output stream.
	 * @param out   The output stream.
	 * @param node  The node to output. 
	 * @param level The level in the hierarchy at which this node sits.
	 * @throws java.io.IOException if there are errors writing the output.
	 */
	@SuppressWarnings("unchecked")
	private void display(JspWriter out, Node node, int level)
			throws java.io.IOException {
		
		// Make sure the line does not wrap.
		if(rowAlt == 0) {
			out.print("<tr id=\"row_" + node.getId() + "\" style=\"background-color: #EEEEEE\"><td>");	
			rowAlt = 1;
		}
		else {
			out.print("<tr id=\"row_" + node.getId() + "\" style=\"background-color: #FFFFFF\"><td>");
			rowAlt = 0;
		}
		

		// Pad out to where this element should sit. At each level of padding
		// we need to choose between displaying a vertical line (in case there
		// are more children that need to be attached to the tree) or a 
		// blank space.
		for (int i = 1; i < level; i++) {
			if (node.getAncestor(i).isLastChild()) {
				drawImage(out, IMAGE_EMPTY);
			} 
			else {
				drawImage(out, IMAGE_JOIN_VERT_LINE);
			}
		}

		if (node.getLevel() != 0) {
			// If this is the last child of its parents set of ordered children
			// then we need to use one set of images.
			if (node.isLastChild()) {
				if (node.hasChildren()) {
					out.print("<a id=\"a_" + node.getId()+ "\" href=\"javascript:toggle('" + node.getId()
							+ "')\">");

					if (node.isOpen()) {
						drawImage(out, IMAGE_JOIN_END_WITH_CHILDREN_EXP);
					} 
					else {
						drawImage(out, IMAGE_JOIN_END_WITH_CHILDREN_COL);
					}
					out.print("</a>");
				} 
				else {
					drawImage(out, IMAGE_JOIN_END_WO_CHILDREN);
				}
			}
			// If this is not the last child of its parents set of ordered children
			// then we need to use a different set of images.
			else {
				if (node.hasChildren()) {
					out.print("<a id=\"a_" + node.getId()+ "\" href=\"javascript:toggle('" + node.getId()
							+ "')\">");
					if (node.isOpen()) {
						drawImage(out, IMAGE_JOIN_END_WITH_CHILDREN_EXP);
					} 
					else {
						drawImage(out, IMAGE_JOIN_END_WITH_CHILDREN_COL);
					}
					out.print("</a>");
				} 
				else {
					drawImage(out, IMAGE_JOIN_WO_CHILDREN);
				}
			}
		}

		// If this is a leaf node (no children) then we display a page icon 
		// to represent the resource.
		if (node.isLeafNode()) {
			drawImage(out, IMAGE_RESOURCE);
		} 
		
		// If the element does have children, then we need to display and open
		// or closed folder.
		else {
			if (node.isOpen()) {
				drawImage(out, IMAGE_FOLDER_OPEN);
			} 
			else {
				drawImage(out, IMAGE_FOLDER_CLOSED);
			}
		}

		// Output the display name of the node.
		out.print("&nbsp;&nbsp;");
		out.print("<span id=\"span_" + node.getId()+ "\" onclick=\"javascript:selRow(" + node.getId() + ");\">");
		node.getRenderer().renderDisplayName(out, node);
		out.print("</span>");
		out.println("</td>");
		
		displayExtraInfo(out, node);
		
		out.println("</tr>");

		// Now do the children.
		if (node.isOpen()) {
			Iterator<Node> children = node.getChildren().iterator();
			while (children.hasNext()) {
				display(out, children.next(), level + 1);
			}
		}
	}
	
	/**
	 * Overwrite this method to display extra columns if desired.
	 * @param out
	 * @param node
	 */
	public void displayExtraInfo(JspWriter out, Node node) throws IOException {
		
	}

	/**
	 * Write the HTML for an image to the output stream.
	 * @param out    The output stream.
	 * @param image  The image to show.
	 * @throws java.io.IOException if there are errors writing to the stream.
	 */
	private void drawImage(JspWriter out, String image)
			throws java.io.IOException {
		out.print("<img src='");
		out.print(image);
		out
				.print("' height=16 width=16 border=0 vspace=0 hspace=0 align=left>");
	}

	/* (non-Javadoc)
	 * @see javax.servlet.jsp.tagext.Tag#doEndTag()
	 */
	public int doEndTag() throws JspException {
		return TagSupport.EVAL_PAGE;
	}

}
