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
package org.webcurator.core.report.parameter;

import java.util.List;

import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

/**
 * Dummy implementation of {@link Errors}<br>
 * <br>
 * The methods that can be used from this 
 * implementation are only:<br>
 * <br>
 * The default constructor: SimpleErrors()<br>
 * The methods: reject(xxx), hasErrors()<br>
 *      
 * @author MDubos
 */
public class SimpleErrors implements Errors {
	
	private boolean hasSomeErrors = false;
	
	
	/**
	 * Empty method
	 * @deprecated
	 */
	public String getObjectName() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Empty method
	 * @deprecated
	 */
	public void setNestedPath(String arg0) {
		// TODO Auto-generated method stub

	}

	/**
	 * Empty method
	 * @deprecated
	 */
	public String getNestedPath() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Empty method
	 * @deprecated
	 */
	public void pushNestedPath(String arg0) {
		// TODO Auto-generated method stub
	}

	/**
	 * Empty method
	 * @deprecated
	 */
	public void popNestedPath() throws IllegalStateException {
		// TODO Auto-generated method stub
	}

	/**
	 * Simple implementation of {@link Errors#reject(java.lang.String)} 
	 */
	public void reject(String arg0) {
		hasSomeErrors = true;
	}

	/**
	 * Simple implementation of {@link Errors#reject(java.lang.String, java.lang.String)} 
	 */
	public void reject(String arg0, String arg1) {
		hasSomeErrors = true;
	}

	/**
	 * Simple implementation of {@link Errors#reject(java.lang.String, java.lang.Object[], java.lang.String)} 
	 */
	public void reject(String arg0, Object[] arg1, String arg2) {
		hasSomeErrors = true;
	}

	/**
	 * Simple implementation of {@link Errors#rejectValue(java.lang.String, java.lang.String)} 
	 */
	public void rejectValue(String arg0, String arg1) {
		hasSomeErrors = true;
	}

	/**
	 * Simple implementation of {@link Errors#rejectValue(java.lang.String, java.lang.String, java.lang.String)} 
	 */
	public void rejectValue(String arg0, String arg1, String arg2) {
		hasSomeErrors = true;
	}

	/**
	 * Simple implementation of {@link Errors#rejectValue(java.lang.String, java.lang.String, java.lang.Object[], java.lang.String)} 
	 */
	public void rejectValue(String arg0, String arg1, Object[] arg2, String arg3) {
		hasSomeErrors = true;
	}

	/**
	 * Empty method
	 * @deprecated
	 */
	public void addAllErrors(Errors arg0) {
		// TODO Auto-generated method stub

	}

	
	/**
	 * Empty method
	 * @deprecated
	 */
	public boolean hasErrors() {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * Empty method
	 * @deprecated
	 */
	public int getErrorCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * Empty method
	 * @deprecated
	 */
	public List getAllErrors() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Empty method
	 * @deprecated
	 */
	public boolean hasGlobalErrors() {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * Empty method
	 * @deprecated
	 */
	public int getGlobalErrorCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	
	/**
	 * Empty method
	 * @deprecated
	 */
	public List getGlobalErrors() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Empty method
	 * @deprecated
	 */
	public ObjectError getGlobalError() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Empty method
	 * @deprecated
	 */
	public boolean hasFieldErrors(String arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * Empty method
	 * @deprecated
	 */
	public int getFieldErrorCount(String arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * Empty method
	 * @deprecated
	 */
	public List getFieldErrors(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Empty method
	 * @deprecated
	 */
	public FieldError getFieldError(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Empty method
	 * @deprecated
	 */
	public Object getFieldValue(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

}
