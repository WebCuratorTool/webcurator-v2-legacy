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

import org.webcurator.core.store.tools.tree.NodeFactory;
import org.webcurator.domain.model.core.HarvestResource;

/**
 * Constructs nodes of the WCTNode type.
 * @author bbeaumont
 *
 */
public class WCTNodeFactory extends NodeFactory<HarvestResource> {
	/* @see NodeFactory
	 */
	public WCTNode createNode(String displayName, HarvestResource subject) {
		WCTNode node = new WCTNode(displayName, subject);
		node.setId(id++);
		return node;
	}
}
