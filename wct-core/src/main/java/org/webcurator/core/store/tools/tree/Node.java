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

import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

/**
 * A node in a tree. Nodes have both display names and underlying
 * objects.
 * 
 * @author bbeaumont
 *
 * @param <SUBJECT>
 */
public class Node<SUBJECT> implements Comparable {
    /** The renderer for this node */
    private static NodeRenderer renderer = new NodeRenderer();
	
	/** The unique ID of the node. */
	private long id;
	/** The display name of the node */
	private String displayName;
	/** A flag to indicate if this node is open/expanded or closed/collapsed. */
	private boolean open;
	
	/** The parent node */
	private Node<SUBJECT> parent = null;
	/** An ordered set of child nodes */
	private TreeSet<Node<SUBJECT>> children = new TreeSet<Node<SUBJECT>>(new NodeComparator());
	/** The level in the tree hierarchy of the node */
	private int level = 0;
	/** The object that the node represents */
	protected SUBJECT subject;
	
	/**
	 * Construct a new node.
	 * @param displayName The display name.
	 * @param subject     The underlying object that this node represents.
	 */
	public Node(String displayName, SUBJECT subject) {
		this.displayName = displayName;
		this.subject = subject;
	}	
	
	/**
	 * Get the underlying object of the node.
	 * @return The underlying object of the node.
	 */
	public SUBJECT getSubject() {
		return subject;
	}
	
	/**
	 * Set thee underlying object of the node.
	 * @param subject The underlying object of the node.
	 */
	public void setSubject(SUBJECT subject) {
		this.subject = subject;
	}

	/**
	 * Get the display name.
	 * @return the display name.
	 */
	public String getDisplayName() {
		return displayName;
	}

	/**
	 * Set the display name.
	 * @param displayName The display name.
	 */
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	/**
	 * Get the unique ID of this node.
	 * @return The unique ID of this node.
	 */
	public long getId() {
		return id;
	}
	
	/**
	 * Set the unique ID of this node.
	 * @param id The unique ID of this node.
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * Add a child to this node.
	 * @param child The child node to add.
	 */
	public void addChild(Node<SUBJECT> child) { 
		this.children.add(child);
		child.setParent(this);
		child.level = this.level + 1;
	}

	/**
	 * Get a set of all the children of this node.
	 * @return A set of nodes.
	 */
	public Set<Node<SUBJECT>> getChildren() {
		return children;
	}

	/**
	 * Check if the node has no children.
	 * @return true only if the node has no children.
	 */
	public boolean isLeafNode() {
		return this.children.size() == 0;
	}
	
	/**
	 * Check if the node has children.
	 * @return true if the node has children.
	 */
	public boolean hasChildren() {
		return this.children.size() > 0;
	}

	/**
	 * Get the parent of this node.
	 * @return The parent of this node.
	 */
	public Node<SUBJECT> getParent() {
		return this.parent;
	}
	
	/**
	 * Set the parent of this node.
	 * @param parent The parent of this node.
	 */
	public void setParent(Node<SUBJECT> parent) {
		this.parent = parent;
	}
	
	/**
	 * Check if this is the last child of it's parent. This is not 
	 * too useful for business purposes but is important for display.
	 * @return true if this is the last child in the parents set of children.
	 */
	public boolean isLastChild() {
		return parent == null || this == parent.children.last();		
	}
	
	/**
	 * Get the level in hierarchy at which this node sits. Root nodes 
	 * are at level = 0.
	 * @return The level in the hierarchy at which this node sites.
	 */
	public int getLevel() {
		return this.level;
	}
	
	/**
	 * Get the ancestor node of this node that is sitting at a particular
	 * level. This method is necessary for display purposes.
	 * @param level The level of the ancestor to find (0 = root level) 
	 * @return The ancestor node.
	 */
    public Node getAncestor(int level) {
        Node obj = this;
        
        while(obj.level > level) { 
            obj = obj.getParent();
        }
        return obj;
    }	
    
    
    
    /**
     * Basic comparator for nodes that compares the display names.
     * @author bbeaumont
     *
     */
    private class NodeComparator implements Comparator<Node<SUBJECT>> {

    	/**
    	 * @see Comparator#compare(T, T)
    	 */
		public int compare(Node o1, Node o2) {
			return o1.getDisplayName().compareToIgnoreCase(o2.getDisplayName());
		}
    	
    }

    /**
     * Get the renderer for this node type.
     * @return The renderer for this node type.
     */
    public NodeRenderer getRenderer() {
    	return renderer;
    }


    /**
     * Returns true if this node is expanded.
     * @return true if this node is expanded.
     */
	public boolean isOpen() {
		return open;
	}

	/**
	 * Changes whether the node is open (expanded) or closed (collapsed).
	 * @param open true to open the node; false to close it.
	 */
	public void setOpen(boolean open) {
		this.open = open;
	}

    public int compareTo(Object o) {
        if(o instanceof Node) {
            return displayName.compareToIgnoreCase(((Node)o).displayName);
        }
        else {
            throw new ClassCastException(o.getClass().getName() + " is expected to be a Node");
        }
    }
}
