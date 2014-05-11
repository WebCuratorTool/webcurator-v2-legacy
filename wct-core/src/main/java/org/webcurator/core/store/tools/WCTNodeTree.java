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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.springframework.web.servlet.ModelAndView;
import org.webcurator.core.store.tools.tree.Node;
import org.webcurator.core.store.tools.tree.NodeTree;
import org.webcurator.domain.model.core.HarvestResource;
import org.webcurator.domain.model.core.HarvestResourceDTO;
import org.webcurator.ui.common.Constants;

/**
 * A Node Tree for HarvestResource objects.
 * @author bbeaumont
 *
 */
public class WCTNodeTree extends NodeTree<HarvestResource> {
	/** The set of nodes to be removed from the tree */
	private Set<WCTNode> prunedNodes = new HashSet<WCTNode>();
	/** The set of nodes to be imported into the tree */
	private Set<HarvestResourceDTO> importedNodes = new HashSet<HarvestResourceDTO>();
	/** A list of modification messages */
	private List<String> modificationNotes = new LinkedList<String>();
	
	/**
	 * Mark a particular node for deletion.
	 * @param id        The ID of the node to delete.
	 * @param propagate true to delete all children of node as well. 
	 */
	public void markForDelete(long id, boolean propagate) {
		WCTNode node = (WCTNode) getNodeCache().get(id);
		StringBuffer mod = new StringBuffer();
		mod.append("Pruned ");
		mod.append(node.getDisplayName());
		if(propagate) {
			mod.append(" and all it's children");
		}
		modificationNotes.add(mod.toString());
		
		innerMarkForDelete(id, propagate);
	}
	
	/**
	 * Mark a particular node for deletion.
	 * @param id        The ID of the node to delete.
	 * @param propagate true to delete all children of node as well. 
	 */
	private void innerMarkForDelete(long id, boolean propagate) {
		WCTNode node = (WCTNode) getNodeCache().get(id);
		node.markForDelete(false);
		this.prunedNodes.add(node);
				
		if( propagate) {
			for(Node n: node.getChildren()) {
				innerMarkForDelete(n.getId(), true);
			}
		}
	}
	
	private HarvestResourceDTO getHarvestResourceDTO(WCTNode node) {
		HarvestResourceDTO hr = new HarvestResourceDTO();
		hr.setLength(node.getSubject().getLength());
        hr.setName(node.getSubject().getName());
        hr.setOid(node.getSubject().getOid());
        hr.setStatusCode(node.getSubject().getStatusCode());
        return hr;
	}
	
	/**
	 * Import a new node.
	 * @param name        The name(URL) of the node to import.
	 * @param length      The length of the associated resource.
	 */
	public void insert(String name, long length, String tempFileName, String contentType) {
		
		// ..to use the getParent method..
		HarvestResourceNodeTreeBuilder tb = new HarvestResourceNodeTreeBuilder();

		URL parentUrl = null;
		try {
			parentUrl = tb.getParent(new URL(name));
		}
		catch (MalformedURLException me) {
			// should never get exception as we've already
			//  validated the name (URL)
		}

		boolean parentNodeExists = false;
		boolean childNodeExists = false;
		long parentNodeId = 0L;
		long childNodeId = 0L;
		
		Iterator<Node<HarvestResource>> it = this.getNodeCache().values().iterator();
		while(it.hasNext()) {
			Node<HarvestResource> node = it.next();
			if( node.getDisplayName().equals(parentUrl.toString())) {
				parentNodeExists = true; 
				parentNodeId = node.getId();
			}
			if( node.getDisplayName().equals(name)) {
				childNodeExists = true; 
				childNodeId = node.getId();
			}
			if (parentNodeExists && childNodeExists) {
				// save a bit of time..
				break;
			}
		}

		if(!parentNodeExists) {
			parentNodeId = this.getNodeCache().values().size()+1;
			HarvestResource hrParent = new HarvestResource();
			hrParent.setName(parentUrl.toString());
			hrParent.setOid(parentNodeId);
			WCTNode parentNode = new WCTNode(parentUrl.toString(), hrParent);
			parentNode.setId(parentNodeId);
			this.addRoot(parentNode);

			HarvestResourceDTO dto = this.getHarvestResourceDTO(parentNode);
			dto.setTempFileName(tempFileName);
			dto.setContentType("");
			this.importedNodes.add(dto);
		}
		
		StringBuffer mod = new StringBuffer();
		mod.append("Imported ");
		if(childNodeExists) {
			WCTNode child = (WCTNode) this.getNodeCache().get(childNodeId);
			child.markForImport();
			HarvestResourceDTO dto = this.getHarvestResourceDTO(child);
			dto.setTempFileName(tempFileName);
			dto.setLength(length);
			dto.setContentType(contentType);
			dto.setStatusCode(200);
			this.importedNodes.add(dto);
			mod.append(child.getDisplayName());
		} else {
			childNodeId = this.getNodeCache().values().size()+1;
			HarvestResource hr = new HarvestResource();
			hr.setName(name);
			hr.setLength(length);
			hr.setStatusCode(200);
			hr.setOid(childNodeId);
			WCTNode node = new WCTNode(name, hr);
			node.setId(childNodeId);
			node.markForImport();
			this.add(node);

			HarvestResourceDTO dto = this.getHarvestResourceDTO(node);
			dto.setTempFileName(tempFileName);
			dto.setContentType(contentType);
			this.importedNodes.add(dto);

			WCTNode parent = (WCTNode) this.getNodeCache().get(parentNodeId);
			parent.addChild(node);
			mod.append(node.getDisplayName());
		}
		modificationNotes.add(mod.toString());
	}
	
	/**
	 * Get the set of nodes that should be pruned.
	 * @return The set of nodes to be pruned.
	 */
	public Set<WCTNode> getPrunedNodes() {
		return prunedNodes;
	}

	/**
	 * Get the set of nodes that should be imported.
	 * @return The set of nodes to be imported.
	 */
	public Set<HarvestResourceDTO> getImportedNodes() {
		return importedNodes;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuffer buff = new StringBuffer();
		
		for(Node<HarvestResource> root: getRootNodes()) {
			toString((WCTNode)root, buff);
		}
		
		return buff.toString();
	}
	
	/**
	 * Write a node to a string.
	 * @param n     The node.
	 * @param buff  The string buffer to which to write the tree.
	 */
	private void toString(WCTNode n, StringBuffer buff) {
		for(int i=0; i < n.getLevel(); i++) {
			buff.append("  ");
		}
		buff.append(n.getDisplayName());
		buff.append(" " );
		buff.append(n.getSubject());
		
		if( n.isMarkedForDelete()) {
			buff.append(" *");
		}
		
		buff.append("\n");
		
		for(Node<HarvestResource> child: n.getChildren()) {
			toString( (WCTNode) child, buff);
		}
	}

	public List<String> getModificationNotes() {
		return modificationNotes;
	}		
	
	public void clear() {
		super.clear();
		prunedNodes.clear();
		importedNodes.clear();
		modificationNotes.clear();
	}
}
