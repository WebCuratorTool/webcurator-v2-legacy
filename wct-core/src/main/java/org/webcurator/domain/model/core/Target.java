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

package org.webcurator.domain.model.core;

import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * A Target is a set of seeds, schedules and profile overrides that specify
 * what to harvest, at what times, and what harvest profile to use.  
 * 
 * @author nwaight
 * @hibernate.joined-subclass table="TARGET" lazy="false" 
 * @hibernate.joined-subclass-key column="T_AT_OID"
 */
public class Target extends AbstractTarget implements Optimizable {
	/** The maximum length of the name string */
	public static final int MAX_NAME_LENGTH = 255;
	/** The maximum length of the description string */
	public static final int MAX_DESC_LENGTH = 4000;
	
	/** Maximum length for the Selection Note */
	public static int MAX_SELECTION_NOTE_LENGTH = 1000;
	/** Maximum length for the Evaluation Note */
	public static int MAX_EVALUATION_NOTE_LENGTH = 1000;	
	/** Maximum length for the Selection Type */
	public static int MAX_SELECTION_TYPE_LENGTH = 255;
	/** Maximum length for the Harvest Type field */
	public static int MAX_HARVEST_TYPE_LENGTH = 255;
	
	/** The state constant for Pending - A target that is still being edited and is not ready for approval */
	public static final int STATE_PENDING = 1;
	/** The state constant for Reinstated - A target that has come out of the cancelled or completed state */
	public static final int STATE_REINSTATED = 2;
	/** The state constant for Nominated - A target that is ready for approval */
	public static final int STATE_NOMINATED = 3;
	/** The state constant for Rejected - A target that was nominated but is not approved for harvest */
	public static final int STATE_REJECTED = 4;
	/** The state constant for Approved - A target that has been approved for harvest */
	public static final int STATE_APPROVED = 5;	
	/** The state constant for Cancelled - A target that was approved, but has since been cancelled for some reason prior to the schedules completing. */
	public static final int STATE_CANCELLED = 6;
	/** The state constant for Completed - A target whose schedules have all reached their end dates */
	public static final int STATE_COMPLETED = 7;
	/** Date at which the target was first nominated or approved */
	private Date selectionDate;
	/** The type of the selection */
	private String selectionType;
	/** A selection note */
	private String selectionNote;
	/** An evaluation note */
	private String evaluationNote;
	/** The type of harvest */
	private String harvestType;
    /** The seeds. **/
    private Set<Seed> seeds = new HashSet<Seed>();
    
    /** Run the target as soon as approved */
    private boolean runOnApproval = false;
    
    /** Use Automated Quality Assurance on Harvests derived from this Target */
    private boolean useAQA = false;

    /** Run the target in five minutes */
    private boolean harvestNow = false;
	private boolean allowOptimize;

    /**
     * Protected constructor - all instances should be created through the
     * <code>BusinessObjectFactory</code>.
     */
    protected Target() {
    	super(AbstractTarget.TYPE_TARGET);
    }
    
    /**
     * Return the Set of Seeds attached to this target.
	 * @return Returns the seeds.
     * @hibernate.set cascade="all-delete-orphan" 
     * @hibernate.collection-key column="S_TARGET_ID" 
     * @hibernate.collection-one-to-many class="org.webcurator.domain.model.core.Seed" 
	 */
	public Set<Seed> getSeeds() {
		return seeds;
	}

	/**
	 * Set the set of seeds in this target.
	 * @param seeds The seeds to set.
	 */
	public void setSeeds(Set<Seed> seeds) {
		this.seeds = seeds;
	}

	
	/**
	 * Is the target now schedulable?
	 * @return True if the new state of the target is schedulable.
	 */
	public boolean isSchedulable() {
		return Target.isScheduleableState(getState());
	}
	
	/**
	 * Was the target schedulable when initialised?
	 * @return True if the original state was schedulable.
	 */
	public boolean wasSchedulable() {
		return Target.isScheduleableState(getOriginalState());
	}
	
	
	/**
	 * Checks if the specified state is schedulable - i.e. should the target
	 * have target instances.
	 * @param aState The state to test.
 	 * @return true if schedulable; otherwise false.
	 */
	public static boolean isScheduleableState(int aState) {
		return aState == STATE_APPROVED || 
		       aState == STATE_COMPLETED;
		
	}
	

	/**
	 * Adds a seed to the target.
	 * @param seed The seed to add.
	 */
	public void addSeed(Seed seed) {
		setDirty(true);
		seed.setTarget(this);
		seeds.add(seed);
	}
	
	/**
	 * Remove a seed from the target.
	 * @param seed The seed to remove.
	 */
	public void removeSeed(Seed seed) {
		setDirty(true);
		seed.setTarget(null);
		seeds.remove(seed);
	}	
	
	
   
    /**
     * Checks if the target can be approved. A target can be approved so long 
     * as:
     * <ul>
     *   <li>There is at least one seed</li>
     *   <li>Every seed is attached to at least one permission;</li>
     *   <li>No seed is attached to a denied permission;</li>
     * </ul>
     * @return true if the target can be approved; otherwise false.
     */
    public boolean isApprovable() {
    	// As long as there is at least one seed, assume that the target can
    	// be approved. If there are no seeds, set approvable to false which
    	// will cause all the loops to be skipped.
    	boolean approvable = seeds.size() != 0;

    	// Loop through the seeds, stopping if any seed is not ready.
    	Iterator<Seed> seedsIterator = seeds.iterator();
    	while(seedsIterator.hasNext() && approvable) {
    		Seed s = seedsIterator.next();
    		
    		if(s.getPermissions().size() == 0) {
    			// The seed is not linked.
    			approvable = false;
    		}
    		else {
    			// The seed is linked, make sure there are no denied permissions
    			Iterator<Permission> permIt = s.getPermissions().iterator();
    			while(permIt.hasNext() && approvable) {
    				Permission p = permIt.next();
    				if(p.getStatus() == Permission.STATUS_DENIED) {
    					approvable = false;
    				}
    			}
    		}
    	}
    	
    	return approvable;
    }


	/**
	 * @return Returns the runOnApproval.
     * @hibernate.property column="T_RUN_ON_APPROVAL" 
	 */
	public boolean isRunOnApproval() {
		return runOnApproval;
	}


	/**
	 * @param runOnApproval The runOnApproval to set.
	 */
	public void setRunOnApproval(boolean runOnApproval) {
		this.runOnApproval = runOnApproval;
	}

	/**
	 * @return Returns the useAQA.
     * @hibernate.property column="T_USE_AQA" 
	 */
	public boolean isUseAQA() {
		return useAQA;
	}


	/**
	 * @param useAQA The useAQA to set.
	 */
	public void setUseAQA(boolean useAQA) {
		this.useAQA = useAQA;
	}

	
	/**
	 * @return Returns the evaluationNote.
     * @hibernate.property column="T_EVALUATION_NOTE" length="255"
	 */
	public String getEvaluationNote() {
		return evaluationNote;
	}


	/**
	 * @param evaluationNote The evaluationNote to set.
	 */
	public void setEvaluationNote(String evaluationNote) {
		this.evaluationNote = evaluationNote;
	}


	/**
	 * @return Returns the selectionDate.
     * @hibernate.property type="timestamp"
     * @hibernate.column name="T_SELECTION_DATE" sql-type="TIMESTAMP(9)"   
	 */
	public Date getSelectionDate() {
		return selectionDate;
	}


	/**
	 * @param selectionDate The selectionDate to set.
	 */
	public void setSelectionDate(Date selectionDate) {
		this.selectionDate = selectionDate;
	}


	/**
	 * @return Returns the selectionNote.
     * @hibernate.property column="T_SELECTION_NOTE" length="255"
	 */
	public String getSelectionNote() {
		return selectionNote;
	}


	/**
	 * @param selectionNote The selectionNote to set.
	 */
	public void setSelectionNote(String selectionNote) {
		this.selectionNote = selectionNote;
	}


	/**
	 * @return Returns the selectionType.
     * @hibernate.property column="T_SELECTION_TYPE" length="255"
	 */
	public String getSelectionType() {
		return selectionType;
	}


	/**
	 * @param selectionType The selectionType to set.
	 */
	public void setSelectionType(String selectionType) {
		this.selectionType = selectionType;
	}

	/**
	 * @return Returns the harvestType.
	 * @hibernate.property column="T_HARVEST_TYPE" length="255"
	 */
	public String getHarvestType() {
		return harvestType;
	}

	/**
	 * @param harvestType The harvestType to set.
	 */
	public void setHarvestType(String harvestType) {
		this.harvestType = harvestType;
	}
    
	/**
	 * @return Returns the harvestNow.
	 */
	public boolean isHarvestNow() {
		return harvestNow;
	}


	/**
	 * @param harvestNow The harvestNow to set.
	 */
	public void setHarvestNow(boolean harvestNow) {
		this.harvestNow = harvestNow;
	}

	/**
	 * @return Returns the harvestType.
	 * @hibernate.property column="T_ALLOW_OPTIMIZE"
	 */
	public boolean isAllowOptimize() {
		return allowOptimize;
	}

	public void setAllowOptimize(boolean allowOptimize) {
		this.allowOptimize = allowOptimize;
	}
	
}
