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
package org.webcurator.ui.site.command;

import org.webcurator.core.permissionmapping.UrlUtils;

/**
 * Command object used for the harvest autorisations urls tab.
 * @author bbeaumont
 */
public class UrlCommand {
	public static final String ACTION_ADD_URL = "_ADD_URL";
	public static final String ACTION_REMOVE_URL = "_REMOVE_URL";
	
	private String url;
	private String urlId;
	
	private String actionCmd;
	
	public boolean isAction(String actionType) {
		return actionType.equals(actionCmd);
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = UrlUtils.fixUrl(url);
	}

	public String getActionCmd() {
		return actionCmd;
	}

	public void setActionCmd(String actionCmd) {
		this.actionCmd = actionCmd;
	}

	public String getUrlId() {
		return urlId;
	}

	public void setUrlId(String urlId) {
		this.urlId = urlId;
	}
	
}
