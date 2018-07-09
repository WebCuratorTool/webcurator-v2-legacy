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

import org.webcurator.core.store.tools.tree.Node;
import org.webcurator.domain.model.core.HarvestResource;

/**
 * Represents a node in a WCTNodeTree.
 * @author bbeaumont
 *
 */
public class WCTNode extends Node<HarvestResource> {
    /** The renderer for the nodes */
    private static WCTNodeRenderer renderer = new WCTNodeRenderer();
    
    /** The number of children of this node */
    private int childCount = 0;
    /** The total size of all the children */
    private long totalChildSize = 0;
    /** Total successful children */
    private int totalSuccessfulChildren = 0;
    /** Total successful children */
    private int totalFailedChildren = 0;
    
    /** Flag to indicate that this node has been marked for deletion */
    private boolean markedForDelete = false;
    
    /** Flag to indicate that this node has been marked for import */
    private boolean markedForImport = false;

    /**
     * Create a new node.
     * @param displayName The display name for the node.
     * @param subject The underlying object of the node.
     */
    public WCTNode(String displayName, HarvestResource subject) {
        super(displayName, subject);
       	updateStats(subject);
    }
    
    
    /**
     * Mark this node to be deleted.
     * @param propagate true to propagate the deletion to all children.
     */
    public void markForDelete(boolean propagate) {
        this.markedForDelete = true;
        if(propagate) {
            for(Node<HarvestResource> node: getChildren()) {
                ((WCTNode)node).markForDelete(true);
            }
        }
    }
    
    /**
     * Mark this node to be imported.
     */
    public void markForImport() {
        this.markedForImport = true;
    }

    /**
     * Get the renderer for this node.
     * @return A renderer.
     */
    public WCTNodeRenderer getRenderer() {
        return renderer;
    }
    
    /**
     * Check if this node is marked for deletion.
     * @return true if this node is marked for deletion.
     */
    public boolean isMarkedForDelete() {
        return this.markedForDelete;
    }
    
    /**
     * Check if this node is marked for import.
     * @return true if this node is marked for import.
     */
    public boolean isMarkedForImport() {
        return this.markedForImport;
    }

    /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
    public String toString() {
        String val = getDisplayName();
        if (getSubject() != null) {
            val += " " + getSubject().getName();
        }
        
        return val;
    }
    
    
        /* (non-Javadoc)
         * @see org.webcurator.core.store.tools.tree.Node#addChild(org.webcurator.core.store.tools.tree.Node)
         */
    @Override
    public void addChild(Node<HarvestResource> child) {
        super.addChild(child);
        updateStats(child.getSubject());
    }
    
        /* (non-Javadoc)
         * @see org.webcurator.core.store.tools.tree.Node#setSubject(java.lang.Object)
         */
    @Override
    public void setSubject(HarvestResource subject) {
        if(this.subject == null) {
            super.setSubject(subject);
            if(this.getParent() != null) {
                updateStats(subject);
            }
        } else {
            throw new RuntimeException("Cannot update subject if it is already set");
            //super.setSubject(subject);
        }
    }
    
    
    public void updateStats(HarvestResource subject) {
        if(subject != null) {
            childCount++;
            totalChildSize += subject.getLength();
            
            if(subject.getStatusCode() >= 200 &&
                    subject.getStatusCode() <= 399) {
                totalSuccessfulChildren ++;
            } else {
                totalFailedChildren ++;
            }
        }
        
        if(this.getParent() != null) {
            ((WCTNode)this.getParent()).updateStats(subject);
        }
    }
    
    
    /**
     * @return the childCount
     */
    public int getChildCount() {
        return childCount;
    }
    
    
    /**
     * @param childCount the childCount to set
     */
    public void setChildCount(int childCount) {
        this.childCount = childCount;
    }
    
    
    /**
     * @return the totalChildSize
     */
    public long getTotalChildSize() {
        return totalChildSize;
    }
    
    
    /**
     * @param totalChildSize the totalChildSize to set
     */
    public void setTotalChildSize(long totalChildSize) {
        this.totalChildSize = totalChildSize;
    }
    
    
    /**
     * @return the totalFailedChildren
     */
    public int getTotalFailedChildren() {
        return totalFailedChildren;
    }
    
    
    /**
     * @param totalFailedChildren the totalFailedChildren to set
     */
    public void setTotalFailedChildren(int totalFailedChildren) {
        this.totalFailedChildren = totalFailedChildren;
    }
    
    
    /**
     * @return the totalSuccessfulChildren
     */
    public int getTotalSuccessfulChildren() {
        return totalSuccessfulChildren;
    }
    
    
    /**
     * @param totalSuccessfulChildren the totalSuccessfulChildren to set
     */
    public void setTotalSuccessfulChildren(int totalSuccessfulChildren) {
        this.totalSuccessfulChildren = totalSuccessfulChildren;
    }
}
