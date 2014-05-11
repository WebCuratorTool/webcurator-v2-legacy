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

import org.springframework.beans.propertyeditors.CustomCollectionEditor;
import org.webcurator.domain.model.core.UrlPattern;
import org.webcurator.ui.site.SiteEditorContext;

/**
 * A custom collection editor for rendering a list of URL's
 * @author bbeaumont
 */
public class UrlPatternCollectionEditor extends CustomCollectionEditor {

	private SiteEditorContext ctx = null;	
	
	public UrlPatternCollectionEditor(Class collectionType, boolean nullAsEmptySet, SiteEditorContext aContext) {
		super(collectionType, nullAsEmptySet);
		ctx = aContext;		
	}

	public UrlPatternCollectionEditor(Class collectionType, SiteEditorContext aContext) {
		super(collectionType);
		ctx = aContext;
	}

	/* (non-Javadoc)
	 * @see org.springframework.beans.propertyeditors.CustomCollectionEditor#convertElement(java.lang.Object)
	 */
	@Override
	protected Object convertElement(Object anObject) {
		try {
			String text = (String) anObject; 
			
			String identity = text;
			Object trackedObject = ctx.getObject(UrlPattern.class, identity);
			
			if(trackedObject == null) { 
				throw new IllegalArgumentException("Illegal identifier provided");
			}
			else {
				
				return trackedObject;
			}
		}
		catch(ClassCastException ex) {
			throw new IllegalArgumentException("Input was not a string: " + anObject.getClass().getName());
		}
		catch(NumberFormatException ex) {
			throw new IllegalArgumentException("Authorising Agent identifier must be numeric");
		}
	}
}
