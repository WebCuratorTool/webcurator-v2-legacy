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
package org.webcurator.ui.target.command;

/**
 * The command object for the permissions popup view.
 * @author bbeaumont
 */
public class PermissionPopupCommand {
	private Long permissionOid;

	/**
	 * @return Returns the permissionOid.
	 */
	public Long getPermissionOid() {
		return permissionOid;
	}

	/**
	 * @param permissionOid The permissionOid to set.
	 */
	public void setPermissionOid(Long permissionOid) {
		this.permissionOid = permissionOid;
	}
	
	
}
