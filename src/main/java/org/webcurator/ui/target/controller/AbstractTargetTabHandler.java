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
package org.webcurator.ui.target.controller;

import javax.servlet.http.HttpServletRequest;

import org.webcurator.ui.target.TargetEditorContext;
import org.webcurator.ui.util.TabHandler;

/**
 * The Abstract base call for all the Target tab handlers.
 * @author bbeamont
 */
public abstract class AbstractTargetTabHandler extends TabHandler {
	public TargetEditorContext getEditorContext(HttpServletRequest req) {
		TargetEditorContext ctx = (TargetEditorContext) req.getSession().getAttribute(TabbedTargetController.EDITOR_CONTEXT);
		if( ctx == null) {
			throw new IllegalStateException("tabEditorContext not yet bound to the session");
		}
		
		return ctx;
	}
}
