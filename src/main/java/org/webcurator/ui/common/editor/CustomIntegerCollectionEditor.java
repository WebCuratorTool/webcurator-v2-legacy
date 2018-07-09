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
package org.webcurator.ui.common.editor;

import org.springframework.beans.propertyeditors.CustomCollectionEditor;

/**
 * A Custom Collection Editor for mapping a collection of Integers.
 * @author bbeaumont
 */
public class CustomIntegerCollectionEditor extends CustomCollectionEditor {

	public CustomIntegerCollectionEditor(Class aClazz, boolean aAllowEmpty) {
		super(aClazz, aAllowEmpty);
	}

	public CustomIntegerCollectionEditor(Class aClazz) {
		super(aClazz);
	}

	/* (non-Javadoc)
	 * @see org.springframework.beans.propertyeditors.CustomCollectionEditor#convertElement(java.lang.Object)
	 */
	@Override
	protected Object convertElement(Object arg0) {
		try {
			Integer val = Integer.parseInt((String)arg0);
			return val;
		}
		catch(NumberFormatException ex) {
			throw new IllegalArgumentException("{0} must be a number");
		}
	}
}
