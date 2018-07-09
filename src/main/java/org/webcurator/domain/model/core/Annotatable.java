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

import java.util.List;

/**
 * This interface is implemented by objects that support annotations being 
 * attached. This allows the object to have a set of audited notes persisted.
 * 
 * @author bbeaumont
 */
public interface Annotatable extends HasOid {
	
	/**
	 * Hibernate required method. Set the List of annotations attached to this 
	 * object.
	 * @param aAnnotations A list of annotations.
	 */
	public void setAnnotations(List<Annotation> aAnnotations);
	
	/**
	 * Add an annotation to this object.
	 * @param annotation The annotation to add.
	 */
	public void addAnnotation(Annotation annotation);
	
	/**
	 * Get the annotation at a specified position in the list.
	 * @param index The index of the annotation to get.
	 */
	public Annotation getAnnotation(int index);
	
	/**
	 * Delete the annotation at a specified position in the list.
	 * @param index The index of the annotation to delete.
	 */
	public void deleteAnnotation(int index);
		
	/**
	 * Returns the list of annotations attached to this object.
	 * @return the list of annotations attached to this object
	 */
	public List<Annotation> getAnnotations();
	
	/**
	 * Returns the list of deleted annotations attached to this object.
	 * @return the list of deleted annotations attached to this object
	 */
	public List<Annotation> getDeletedAnnotations();
	
	/**
	 * Checks if the annotations set has been initialised.
	 * @return true if initialised; otherwise false.
	 */
	public boolean isAnnotationsSet();
	
	/**
	 * Returns a sorted list of annotations by date. Annotations
	 * are only sorted if they have been reloaded or added to.
	 * @return a sorted list of annotations by date.
	 */
	public List<Annotation> getSortedAnnotations();
	
	
	/**
	 * Forces annotations to be sorted by date.
	 */
	public void sortAnnotations();
	
}
