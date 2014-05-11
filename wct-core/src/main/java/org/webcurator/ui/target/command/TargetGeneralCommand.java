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
package org.webcurator.ui.target.command;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.webcurator.domain.model.core.Target;

/**
 * The command object for the Target general tab.
 * @author bbeaumont
 */
public class TargetGeneralCommand {
	public static final String MDL_NEXT_STATES = "nextStates";
	public static final String MDL_REJ_REASONS = "rejReasons";
	
	public static final String PARAM_NAME = "name";
	public static final String PARAM_DESCRIPTION = "description";
	public static final String PARAM_STATE = "state";
	public static final String PARAM_OWNER_OID = "ownerOid";
	public static final String PARAM_REFERENCE = "reference";
	
    /** The targets name. */
    private String name;
    /** the targets description. */
    private String description;
    /** The target's state */
    private int state;
    /** The ID of the owner */
    private Long ownerOid;
    /** Whether to run immediately on approval */
    private boolean runOnApproval = false;
    /** Whether to use Automated Quality Assurance */
    private boolean useAQA = false;
    /** the reference number of the target. */
    private String reference;
    /** The ID of the rejection reason */
    private Long reasonOid;
    /** Whether to automatically prune a target instance **/
    private boolean autoPrune = false;
    /** Whether to automatically denote an archived harvest as a reference crawl **/
    private boolean autoDenoteReferenceCrawl = false;
    /** Any information that the Archivists should be given **/
    private String requestToArchivists;
    
	/**
	 * @return Returns the reasonOid.
	 */
	public Long getReasonOid() {
		return reasonOid;
	}

	/**
	 * @param reasonOid The reasonOid to set.
	 */
	public void setReasonOid(Long reasonOid) {
		this.reasonOid = reasonOid;
	}

	/**
	 * @return Returns the owner.
	 */
	public Long getOwnerOid() {
		return ownerOid;
	}

	/**
	 * @param owner The owner to set.
	 */
	public void setOwnerOid(Long owner) {
		this.ownerOid = owner;
	}

	public static TargetGeneralCommand buildFromModel(Target model) {
    	TargetGeneralCommand command = new TargetGeneralCommand();
    	command.setName(model.getName());
    	command.setDescription(model.getDescription());
    	command.state = model.getState();
    	command.setOwnerOid(model.getOwner().getOid());
    	command.setRunOnApproval(model.isRunOnApproval());
    	command.setUseAQA(model.isUseAQA());
    	command.setAutoPrune(model.isAutoPrune());
    	command.setAutoDenoteReferenceCrawl(model.isAutoDenoteReferenceCrawl());
    	command.setReference(model.getReferenceNumber());
    	if (model.getRejReason() != null) {
        	command.setReasonOid(model.getRejReason().getOid());
    	}
    	command.setRequestToArchivists(model.getRequestToArchivists());
    	return command;		
	}
	

    /**
	 * @return Returns the state.
	 */
	public int getState() {
		return state;
	}

	/**
	 * @param state The state to set.
	 */
	public void setState(int state) {
		this.state = state;
	}	
	
    
	/**
	 * @return Returns the description.
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * @param description The description to set.
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name The name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return Returns the name.
	 */
	public String getEncodedName() {
		try
		{
			return URLEncoder.encode(name, "UTF-8");
		}
		catch(UnsupportedEncodingException e)
		{
			return name;
		}
	}

	/**
	 * @return Returns the runOnApproval.
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
	 * @return the reference
	 */
	public String getReference() {
		return reference;
	}

	/**
	 * @param reference the reference to set
	 */
	public void setReference(String reference) {
		this.reference = reference;
	}

	public boolean isAutoPrune() {
		return autoPrune;
	}

	public void setAutoPrune(boolean autoPrune) {
		this.autoPrune = autoPrune;
	}

	/**
	 * @return the autoDenoteReferenceCrawl
	 */
	public boolean isAutoDenoteReferenceCrawl() {
		return autoDenoteReferenceCrawl;
	}

	/**
	 * @param autoDenoteReferenceCrawl the autoDenoteReferenceCrawl to set
	 */
	public void setAutoDenoteReferenceCrawl(boolean autoDenoteReferenceCrawl) {
		this.autoDenoteReferenceCrawl = autoDenoteReferenceCrawl;
	}

	/**
	 * @return the requestToArchivists
	 */
	public String getRequestToArchivists() {
		return requestToArchivists;
	}

	/**
	 * @param requestToArchivists the requestToArchivists to set
	 */
	public void setRequestToArchivists(String requestToArchivists) {
		this.requestToArchivists = requestToArchivists;
	}
    
    
}
