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
package org.webcurator.domain.model.dto;

import java.util.HashSet;

import org.webcurator.domain.model.core.Permission;

/**
 * DTO object for grouping seeds with their permissions
 * @author nwaight
 */
public class PermissionSeedDTO {
	/** unique id of the permission. */
	private Long permissionOid;
	/** the permission associated. */
	private Permission permission;
	/** the seed urls associated with the permission. */
	private HashSet<String> seeds;
	
	public PermissionSeedDTO(Permission aPermission) {
		permissionOid = aPermission.getOid();
		permission = aPermission;
		seeds = new HashSet<String>();
		
		permission.getSite();
		permission.getAuthorisingAgent();
	}
	
	/**
	 * @return the permission
	 */
	public Permission getPermission() {
		return permission;
	}
	/**
	 * @param permission the permission to set
	 */
	public void setPermission(Permission permission) {
		this.permission = permission;
	}
	/**
	 * @return the permissionOid
	 */
	public Long getPermissionOid() {
		return permissionOid;
	}
	/**
	 * @param permissionOid the permissionOid to set
	 */
	public void setPermissionOid(Long permissionOid) {
		this.permissionOid = permissionOid;
	}
	/**
	 * @return the seeds
	 */
	public HashSet<String> getSeeds() {
		return seeds;
	}
	/**
	 * @param seeds the seeds to set
	 */
	public void setSeeds(HashSet<String> seeds) {
		this.seeds = seeds;
	}	
}
