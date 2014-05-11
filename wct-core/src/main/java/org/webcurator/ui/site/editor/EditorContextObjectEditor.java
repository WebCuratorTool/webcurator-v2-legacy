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
package org.webcurator.ui.site.editor;

import java.beans.PropertyEditorSupport;

import org.webcurator.domain.model.core.AuthorisingAgent;
import org.webcurator.ui.site.EditorContext;

/**
 * Converts a agents idenity (oid) into an Authorising Agent object.
 * @author bbeaumont
 */
public class EditorContextObjectEditor extends PropertyEditorSupport {

	private EditorContext editorContext = null;
	private Class clazz = null;
	
	public EditorContextObjectEditor(EditorContext anEditorContext, Class aClass) {
		editorContext = anEditorContext;
		clazz = aClass;
	}
	
	/* (non-Javadoc)
	 * @see java.beans.PropertyEditorSupport#getAsText()
	 */
	@Override
	public String getAsText() {
		if (getValue() == null) {
			return "";
		}
		
		return ((AuthorisingAgent) getValue()).getName();
	}
	
	/* (non-Javadoc)
	 * @see java.beans.PropertyEditorSupport#setAsText(java.lang.String)
	 */
	@Override
	public void setAsText(String identity) throws IllegalArgumentException {
		try {
			Object value = editorContext.getObject(clazz, identity);
			
			if(value == null) { 
				throw new IllegalArgumentException("Illegal identifier provided");
			}
			else {
				setValue(value);
			}
		}
		catch(NumberFormatException ex) {
			throw new IllegalArgumentException("Authorising Agent identifier must be numeric");
		}
	}
}
