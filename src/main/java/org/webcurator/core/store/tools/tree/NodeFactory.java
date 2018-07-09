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

/**
 * Creates nodes for a NodeTree.
 * @author bbeaumont
 *
 * @param <SUBJECT> The object type underlying a node.
 */
public class NodeFactory<SUBJECT> {
	/** The ID of the node */
	protected long id;
	
	/** 
	 * Constructor.
	 */
	public NodeFactory() {
		id = 1;
	}

	/**
	 * Creates a new node.
	 * @param displayName The display name for the node.
	 * @param subject The underlying object.
	 * @return The new node.
	 */
	public Node<SUBJECT> createNode(String displayName, SUBJECT subject) {
		Node<SUBJECT> node = new Node<SUBJECT>(displayName, subject);
		node.setId(id++);
		return node;
	}

}
