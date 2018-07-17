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

import javax.persistence.*;

/**
 * Represents a URL excluded from a permission.
 */
@Entity
@Table(name = "PERMISSION_EXCLUSION")
public class PermissionExclusion {
	/** The URL excluded */
	@Column(name = "PEX_URL", length = 1024, columnDefinition = "text")
	private String url;
	
	/** The reason for exclusion */
	@Column(name = "PEX_REASON", length = 255)
	private String reason;
	
	/** The OID of the object */
	@Id
	@Column(name = "PEX_OID", nullable = false)
	@GeneratedValue(generator = "permExclusionGen", strategy = GenerationType.TABLE)
	@TableGenerator(name = "permExclusionGen", table = "ID_GENERATOR", pkColumnName = "IG_TYPE",
			valueColumnName = "IG_VALUE", pkColumnValue = "PermExclusion")
	private Long oid = null;

	@ManyToOne(targetEntity = Permission.class)
	@JoinColumn(name = "PEX_PERMISSION_OID")
	private Permission permission;

	/**
	 * Create a new exclusion object.
	 * @param url The URL to exclude.
	 * @param reason The reason for exclusion.
	 */
	public PermissionExclusion(String url, String reason) {
		this.url = url;
		this.reason = reason;
	}
	
	/**
	 * No-arg constructor.
	 */
	public PermissionExclusion() {
	}
	
	
	/**
	 * @return Returns the reason.
	 */
	public String getReason() {
		return reason;
	}

	/**
	 * @param reason The reason to set.
	 */
	public void setReason(String reason) {
		this.reason = reason;
	}

	/**
	 * @return Returns the url.
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @param url The url to set.
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * @return Returns the oid.
	 */
	public Long getOid() {
		return oid;
	}

	/**
	 * @param oid The oid to set.
	 */
	public void setOid(Long oid) {
		this.oid = oid;
	}

	public Permission getPermission() {
		return permission;
	}

	public void setPermission(Permission permission) {
		this.permission = permission;
	}
}
