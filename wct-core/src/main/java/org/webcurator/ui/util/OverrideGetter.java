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
package org.webcurator.ui.util;

import javax.servlet.http.HttpServletRequest;

import org.webcurator.core.exceptions.WCTRuntimeException;
import org.webcurator.domain.model.core.Overrideable;
import org.webcurator.domain.model.core.TargetGroup;
import org.webcurator.domain.model.core.TargetInstance;
import org.webcurator.ui.groups.GroupsEditorContext;
import org.webcurator.ui.groups.controller.TabbedGroupController;
import org.webcurator.ui.target.TargetEditorContext;
import org.webcurator.ui.target.command.TargetInstanceCommand;
import org.webcurator.ui.target.controller.TabbedTargetController;

/**
 * This class is responsible for getting an Overridable object from the 
 * session or Http request depending on the type.
 * @author nwaight
 */
public class OverrideGetter {
	/** The name of the Target Overridable type. */
	public static final String TYPE_TARGET = "Target";
	/** The name of the Target Instance Overridable type. */
	public static final String TYPE_TARGET_INSTANCE = "Target Instance";
	/** The name of the Target Group Overridable type. */
	public static final String TYPE_TARGET_GROUP = "Target Group";
	/** The type of overridable object for this getter to return. */
	private String OverrideableType; 
	
	/**
	 * Return the Overrideable object of the specified type 
	 * @param aReq the HttpServletRequest to retrieve the Overrideable object from 
	 * @return the Overrideable object
	 */
	public Overrideable getOverrideable(HttpServletRequest aReq) {
		Overrideable o = null;
		if (null == OverrideableType) {
			throw new WCTRuntimeException("A null overrideable type was provided.");
		}
					
		if (OverrideableType.trim().equals(TYPE_TARGET)) {
			TargetEditorContext ctx = (TargetEditorContext) aReq.getSession().getAttribute(TabbedTargetController.EDITOR_CONTEXT);
			if( ctx == null) {
				throw new IllegalStateException("TargetEditorContext not yet bound to the session");
			}
			
			o = ctx.getTarget();
		}
		else if (OverrideableType.trim().equals(TYPE_TARGET_INSTANCE)) {
			TargetInstance ti = (TargetInstance) aReq.getSession().getAttribute(TargetInstanceCommand.SESSION_TI);
			if (null == ti) {
				throw new IllegalStateException("TargetInstance not yet bound to the session");
			}
			
			o = ti;
		}
		else if (OverrideableType.trim().equals(TYPE_TARGET_GROUP)) {
			TargetGroup tg = ((GroupsEditorContext) aReq.getSession().getAttribute(TabbedGroupController.EDITOR_CONTEXT)).getTargetGroup();
			if(tg == null) {
				throw new IllegalStateException("TargetGroup not yet bound to the session");
			}
			o = tg;
		}
		else {
			throw new WCTRuntimeException("An unknown overrideable type was provided " + OverrideableType);
		}
		
		return o;
	}

	/**
	 * Retuurn the edit mode for the overridable type
	 * @param aReq the HttpServletRequest to retrieve the Overrideable object from 
	 * @return true if the overridable object is in editmode
	 */
	public boolean isOverrideableEditable(HttpServletRequest aReq) {
		if (null == OverrideableType) {
			return false;
		}
					
		if (OverrideableType.trim().equals(TYPE_TARGET)) {
			TargetEditorContext ctx = (TargetEditorContext) aReq.getSession().getAttribute(TabbedTargetController.EDITOR_CONTEXT);
			if( ctx == null) {
				return false;
			}
			
			return ctx.isEditMode();
		}
		else if (OverrideableType.trim().equals(TYPE_TARGET_INSTANCE)) {
			Boolean editMode = (Boolean) aReq.getSession().getAttribute(TargetInstanceCommand.SESSION_MODE);
			if (null == editMode) {
				return false;
			}
									
			return editMode.booleanValue();
		}
		else if (OverrideableType.trim().equals(TYPE_TARGET_GROUP)) {
			GroupsEditorContext ctx = (GroupsEditorContext) aReq.getSession().getAttribute(TabbedGroupController.EDITOR_CONTEXT);					
			if(ctx == null) {
				return false;
			}
			
			return ctx.isEditMode();
		}
		
		return false;
	}
	
	/**
	 * @param overrideableType the overrideableType to set
	 */
	public void setOverrideableType(String overrideableType) {
		OverrideableType = overrideableType;
	}	
}
