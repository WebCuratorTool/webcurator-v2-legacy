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
 * The data transfer object class used to provide required information to
 * WCT DAS to determine whether or not a custom deposit form is required for 
 * the specific submission module.
 *  
 * @author Raghu Pushpakath (NLNZ)
 *
 */
public class CustomDepositFormCriteriaDTO {
	/**
	 * ID of the logged in user
	 */
	private String userId;
	/**
	 * ID of the agency to which the logged in user belongs to.
	 */
	private String agencyId;
	/**
	 * Name of the agency to which the logged in user belongs to.
	 */
	private String agencyName;
	/**
	 * dc:type of the target
	 */
	private String targetType;

	/**
	 * Default constructor.
	 */
	public CustomDepositFormCriteriaDTO() {
	}
	public String getUserId() {
		return userId;
	}
	public String getAgencyId() {
		return agencyId;
	}
	public String getAgencyName() {
		return agencyName;
	}
	public String getTargetType() {
		return targetType;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public void setAgencyId(String agencyId) {
		this.agencyId = agencyId;
	}
	public void setAgencyName(String agencyName) {
		this.agencyName = agencyName;
	}
	public void setTargetType(String targetType) {
		this.targetType = targetType;
	}
	public String toString() {
		return "CustomDepositFormCriteriaDTO: "
			+ "userId(" + userId + "), "
			+ "agencyId(" + agencyId + "), "
			+ "agencyName(" + agencyName + "), "
			+ "targetType(" + targetType + ")";
	}
}


