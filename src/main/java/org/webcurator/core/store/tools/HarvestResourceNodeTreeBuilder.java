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
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.webcurator.core.store.tools.tree.Node;
import org.webcurator.domain.model.core.HarvestResource;

/**
 * Builds a tree for the Copy and Prune quality review tool.
 * @author bbeaumont
 */
public class HarvestResourceNodeTreeBuilder {
	/** The map of nodes at the root of the tree. */
	private Map<String, WCTNode> rootCache = new HashMap<String, WCTNode>();
	/** A cache of all nodes in the tree. Used during the building to find existing nodes. */
	private Map<String, WCTNode>  allNodesCache = new HashMap<String, WCTNode>();
	/** The factory for creating nodes. */
	private WCTNodeFactory factory = new WCTNodeFactory();
	
	/** The tree itself */
	private WCTNodeTree tree = null;
	/** The root node of the tree */
	private WCTNode theRootNode = null;
	
	/**
	 * Create a new builder.
	 */
	public HarvestResourceNodeTreeBuilder() {
		tree = new WCTNodeTree();
		
		// Create a base root node.
		theRootNode = factory.createNode("Harvest", null);
		tree.addRoot(theRootNode);
	}
	
	/**
	 * Add a resource to the tree. 
	 * @param resource The resource to add.
	 * @throws MalformedURLException if the resource's URL is invalid.
	 */
	public void addNode(HarvestResource resource) throws MalformedURLException {
		this.addNode(new URL(resource.getName()), resource);
	}
	
	/**
	 * Add a resource to the tree.
	 * @param url The URL to key the element with in the cache.
	 * @param resource The HarvestResource to add to the tree.
	 * @throws MalformedURLException if the resource's URL is invalid.
	 */
	public void addNode(URL url, HarvestResource resource) throws MalformedURLException {
		if(allNodesCache.containsKey(url.toString())) {
			WCTNode n = allNodesCache.get(url.toString());
			if(n.getSubject() == null) {
				n.setSubject(resource);
			}
			return;
		}
		if(isRoot(url)) {
			if(!rootCache.containsKey(url.toString())) {
				WCTNode n = factory.createNode(url.toString(), resource);
				rootCache.put(url.toString(), n);
				allNodesCache.put(url.toString(), n);
				//tree.addRoot(n);
				theRootNode.addChild(n);
				tree.add(n);
			}
			else {
				// Node is already in the cache, but does it have
				// a resource?
				Node<HarvestResource> n = rootCache.get(url);
				if(n.getSubject() == null && resource != null) {
					n.setSubject(resource);
				}
			}
		}
		else {
			URL parentUrl = getParent(url);
			if(!allNodesCache.containsKey(parentUrl.toString())) {
				addNode(parentUrl, null);
			}
			
			WCTNode n = factory.createNode(url.toString(), resource);
			WCTNode p = allNodesCache.get(parentUrl.toString());
			p.addChild(n);
			allNodesCache.put(url.toString(), n);
			tree.add(n);
		}
	}
	
	
	/**
	 * Checks if the URL is the base of a site. e.g. http://www.alphabetsoup.com/
	 * @param url The URL to test.
	 * @return true if the URL is the a root; otherwise false.
	 */
	public boolean isRoot(URL url) {
		return "/".equals(url.getPath());
	}
	
	/**
	 * Get the parent URL. e.g. the parent of http://www.abc.com/aboutus/index.html
	 * is http://www.abc.com/aboutus/
	 * @param url The URL to get the parent for.
	 * @return The parent URL.
	 * @throws MalformedURLException if the URL is malformed.
	 */
	public URL getParent(URL url) throws MalformedURLException {
		if( isRoot(url)) {
			return null;
		}
		
		String urlPath = url.getPath();
		String parent = null;
		
		// Handle a folder
		if( urlPath.endsWith("/")) {
			urlPath = urlPath.substring(0, urlPath.length()-1);
			parent = urlPath.substring(0, urlPath.lastIndexOf('/')) + "/";
		}
		
		// Handle a file
		else {
			parent = urlPath.substring(0, urlPath.lastIndexOf('/')) + "/";
		}
		
		return new URL(url.getProtocol(), url.getHost(), parent);
	}
	
	/**
	 * Geterate a string to display the tree to the console.
	 * @param buff The string buffer.
	 * @param n    The node to start the tree at.
	 * @param tab  The indent level. 
	 */
	public void genString(StringBuffer buff, Node<HarvestResource> n, int tab) {
		for(int i=0; i<tab; i++) {
			buff.append("  ");
		}
		buff.append(n.getDisplayName());
		if(n.getSubject() != null) { 
			buff.append(" * ");
		}
		buff.append("\n");
		
		Iterator<Node<HarvestResource>> children = n.getChildren().iterator();
		while(children.hasNext()) {
			genString(buff, children.next(), tab+1);
		}
		
	}
	
	/**
	 * Convert the tree to a string 
	 * @return The tree as a string suitable for displaying on the console.
	 */
	public String toString() {
		Iterator<WCTNode> it = rootCache.values().iterator();
		StringBuffer buff = new StringBuffer();
		
		while(it.hasNext()) {
			Node<HarvestResource> n = it.next();
			genString(buff, n, 0);
		}
		
		return buff.toString();
	}
	
	
	/**
	 * Get the root nodes of the tree. 
	 * @return A collection of the root nodes.
	 */
	public Collection<WCTNode> getRootNodes() {
		return rootCache.values();
	}
	
	/**
	 * Retrieve the tree itself.
	 * @return The tree.
	 */
	public WCTNodeTree getTree() {
		return tree;
	}
	
}
