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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * A tree of nodes.
 * @author bbeaumont
 *
 * @param <SUBJECT> The underlying object of the nodes in the tree.
 */
public class NodeTree<SUBJECT> {
	/** Set of root nodes **/
	private Set<Node<SUBJECT>> rootNodes = new TreeSet<Node<SUBJECT>>();
	
	/** Map of all nodes **/
	private Map<Long, Node<SUBJECT>> nodeCache = new HashMap<Long, Node<SUBJECT>>();

	/**
	 * Get the set of nodes that are at the root of the tree.
	 * @return The set of root nodes.
	 */
	public Set<Node<SUBJECT>> getRootNodes() {
		return rootNodes;
	}
	
	/**
	 * Add a non-root node to the tree. 
	 * @param node The node to add to the tree.
	 */
	public void add(Node<SUBJECT> node) {
		this.nodeCache.put(node.getId(), node);
	}
	
	/**
	 * Add a root node to the tree.
	 * @param node The root node to add.
	 */
	public void addRoot(Node<SUBJECT> node) {
		this.rootNodes.add(node);
		this.nodeCache.put(node.getId(), node);
		node.setOpen(true);
	}
	
	/**
	 * Toggle whether a node is open or closed.
	 * @param id The ID of the node to toggle.
	 */
	public void toggle(long id) {
		Node<SUBJECT> node = this.nodeCache.get(id);
		node.setOpen( node.isOpen() ? false : true);
	}

	/**
	 * Get the cache of nodes.
	 * @return The cache of nodes.
	 */
	public Map<Long, Node<SUBJECT>> getNodeCache() {
		return nodeCache;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuffer buff = new StringBuffer();
		
		for(Node<SUBJECT> root: this.rootNodes) {
			toString(root, buff);
		}
		
		return buff.toString();
	}
	
	/**
	 * Internal method for generating the toString output.
	 * @param n The node to output.
	 * @param buff The buffer to add the node to.
	 */
	private void toString(Node<SUBJECT> n, StringBuffer buff) {
		for(int i=0; i < n.getLevel(); i++) {
			buff.append("  ");
		}
		buff.append(n.getDisplayName());
		buff.append(" " );
		buff.append(n.getSubject());
		
		buff.append("\n");
		
		for(Node<SUBJECT> child: n.getChildren()) {
			toString(child, buff);
		}
	}	
	
	public void clear() { 
		rootNodes.clear();
		nodeCache.clear();
	}

}
