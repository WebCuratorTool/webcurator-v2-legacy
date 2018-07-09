/*
 *  Copyright 2010 The National Library of New Zealand
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

/**
 * The data transfer object class used to receive required information from
 * WCT DAS to determine whether or not a custom deposit form is required for 
 * the specific submission module.
 *  
 * @author Raghu Pushpakath (NLNZ)
 *
 */
public class CustomDepositFormResultDTO {
	/**
	 * Flag that indicates whether a custom deposit form
	 * is required or not.
	 */
	private boolean isCustomDepositFormRequired;
	/**
	 * The URL from where the contents of custom form is to be
	 * fetched.
	 */
	private String urlForCustomDepositForm;
	/**
	 * HTML contents of the custom deposit form.
	 */
	private String htmlForCustomDepositForm;
	/**
	 * Producer Id to pass to custom form if preset.
	 */
	private String producerId;
	/**
	 * Default constructor.
	 */
	public CustomDepositFormResultDTO() {
	}
	/**
	 * Returns a flag that indicates whether a custom deposit form
	 * is required or not.
	 * <p>If this method returns true, either of the two methods
	 * {@link #getHTMLForCustomDepositForm() getHTMLForCustomDepositForm()}
	 * or
	 * {@link #getUrlForCustomDepositForm() getUrlForCustomDepositForm()}
	 * should return a non-null value.
	 * 
	 * @return
	 */
	public boolean isCustomDepositFormRequired() {
		return isCustomDepositFormRequired;
	}
	/**
	 * Returns the URL from where the contents of custom form is to be
	 * fetched. 
	 * <p>A client application can use either this method or
	 * the {@link #getHTMLForCustomDepositForm() getHTMLForCustomDepositForm()}
	 * method to fetch the contents of custom deposit form.
	 * 
	 * @return
	 */
	public String getUrlForCustomDepositForm() {
		return urlForCustomDepositForm;
	}
	/**
	 * Returns the HTML contents of the custom deposit form.
	 * <p>A client application can use either this method or
	 * the {@link #getUrlForCustomDepositForm() getUrlForCustomDepositForm()}
	 * method to fetch the contents of custom deposit form.
	 * 
	 * @return
	 */
	public String getHTMLForCustomDepositForm() {
		return htmlForCustomDepositForm;
	}
	public void setCustomDepositFormRequired(boolean isCustomDepositFormRequired) {
		this.isCustomDepositFormRequired = isCustomDepositFormRequired;
	}
	public void setUrlForCustomDepositForm(String urlForCustomDepositForm) {
		this.urlForCustomDepositForm = urlForCustomDepositForm;
	}
	public void setHTMLForCustomDepositForm(String htmlForCustomDepositForm) {
		this.htmlForCustomDepositForm = htmlForCustomDepositForm;
	}
	public String getProducerId() {
		return producerId;
	}
	public void setProducerId(String producerId) {
		this.producerId = producerId;
	}

	public String toString() {
		return "CustomDepositFormResultDTO: "
			+ "isCustomDepositFormRequired(" + isCustomDepositFormRequired + "), "
			+ "urlForCustomDepositForm(" + urlForCustomDepositForm + ")";
	}
}


