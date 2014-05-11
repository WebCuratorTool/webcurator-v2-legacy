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
package org.webcurator.domain;

import java.util.List;

import org.webcurator.domain.model.core.Annotation;

/**
 * The interface that defines the Data Access Object for 
 * loading and saving annotations.
 * @author nwaight
 */
public interface AnnotationDAO {
	/**
	 * Return a list of annotation's for the specified object.
	 * @param aType the type of the object to return the annoations for/
	 * @param aOid the oid of the object to return the annoations for/
	 * @return the annotation list
	 */
	List<Annotation> loadAnnotations(String aType, Long aOid);
	
	/** 
	 * Save the list of annotations provided.
	 * @param aAnnotations the annotations to save
	 */
	void saveAnnotations(List<Annotation> aAnnotations);
	
	/** 
	 * Save the list of annotations provided.
	 * @param aAnnotations the annotations to save
	 */
	void deleteAnnotations(List<Annotation> aAnnotations);
}
