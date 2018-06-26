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
package org.webcurator.ui.profiles.controller;

import javafx.util.Pair;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractCommandController;
import org.webcurator.auth.AuthorityManager;
import org.webcurator.core.harvester.agent.HarvestAgent;
import org.webcurator.core.harvester.agent.HarvestAgentScriptResult;
import org.webcurator.core.scheduler.TargetInstanceManager;
import org.webcurator.domain.model.auth.Privilege;
import org.webcurator.domain.model.core.TargetInstance;
import org.webcurator.ui.common.CommonViews;
import org.webcurator.ui.common.Constants;
import org.webcurator.ui.profiles.command.H3ScriptConsoleCommand;
import org.webcurator.ui.util.HarvestAgentUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;


/**
 * Controller to handle users viewing profiles.
 * @author bbeaumont
 *
 */
public class H3ScriptConsoleController extends AbstractCommandController {
	/** The profile manager to load the profile */
	private TargetInstanceManager targetInstanceManager = null;
	/** The authority manager for checking permissions */
	private AuthorityManager authorityManager = null;

	/**
	 * Construct a new ProfileViewController.
	 */
	public H3ScriptConsoleController() {
		setCommandClass(H3ScriptConsoleCommand.class);
	}

	/* (non-Javadoc)
	 * @see org.springframework.web.servlet.mvc.AbstractCommandController#handle(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.Object, org.springframework.validation.BindException)
	 */
	@Override
	protected ModelAndView handle(HttpServletRequest req, HttpServletResponse res, Object comm, BindException errors) throws Exception {
		H3ScriptConsoleCommand command = (H3ScriptConsoleCommand) comm;
		TargetInstance ti = targetInstanceManager.getTargetInstance(command.getTargetInstanceOid(), true);
		String result = "";
		// Retrieve the list of script files and the contents
		Map<Pair<String, String>, String> scripts = getScripts();
		// Set default selected script
		String selectedScript = command.getScriptSelected();
		if (selectedScript == null || selectedScript.equals("")) {
			command.setScriptSelected("none");
		}

		if (authorityManager.hasAtLeastOnePrivilege(ti.getProfile(), new String[] {Privilege.MANAGE_TARGET_INSTANCES, Privilege.MANAGE_WEB_HARVESTER})) {
			if (req.getMethod().equals("POST") && ti.getState().equals("Running")
					&& command.getActionCommand().equals(H3ScriptConsoleCommand.ACTION_EXECUTE_SCRIPT)) {
				// Run the heritrix 3 script - only if the status is still running
				HarvestAgent ha = getHarvestAgent();
				HarvestAgentScriptResult scriptResult = ha.executeShellScript(Long.toString(command.getTargetInstanceOid()), command.getScriptEngine(), command.getScript());
				result = scriptResult.toString();
			}
			ModelAndView mav = new ModelAndView("h3-script-console");
			mav.addObject("targetInstance", ti);
			mav.addObject("scripts", scripts);
			mav.addObject("result", result);
			mav.addObject(Constants.GBL_CMD_DATA, command);
			return mav;
		}
		else { 
			return CommonViews.AUTHORISATION_FAILURE;
		}
	}

	/**
	 * This method returns a map containing a Pair as the key and a String as the value.
	 * The Pair contains the script's file name as the key and the script type (beanshell, groovy, nashorn)
	 * as the value.
	 * The contents of the script file are the map's value.
	 * @return the map.
	 */
	private Map<Pair<String, String>, String> getScripts() {
		Map<Pair<String, String>, String> scripts = new HashMap<Pair<String, String>, String>();
		//TODO - parse directory - hard code for now...
		Pair<String, String> scriptPair01 = createScriptPair("Script01", "groovy");
		scripts.put(scriptPair01, "this.binding.getVariables().each{ rawOut.println(\"${it.key}=\\n ${it.value}\\n\") }");
		Pair<String, String> scriptPair02 = createScriptPair("Script02", "beanshell");
		scripts.put(scriptPair02, "Rhubarb, rhubarb");
/*
		scripts.put(scriptPair02, "appCtxData = appCtx.getData()\n" +
				"appCtxData.printProps = { rawOut, obj ->\n" +
				"  rawOut.println \"#properties\"\n" +
				"  // getProperties is a groovy introspective shortcut. it returns a map\n" +
				"  obj.properties.each{ prop ->\n" +
				"    // prop is a Map.Entry\n" +
				"    rawOut.println \"\\n\"+ prop\n" +
				"    try{ // some things don't like you to get their class. ignore those.\n" +
				"      rawOut.println \"TYPE: \"+ prop.value.class.name\n" +
				"    }catch(Exception e){}\n" +
				"  }\n" +
				"  rawOut.println \"\\n\\n#methods\"\n" +
				"  try {\n" +
				"  obj.class.methods.each{ method ->\n" +
				"    rawOut.println \"\\n${method.name} ${method.parameterTypes}: ${method.returnType}\"\n" +
				"  } }catch(Exception e){}\n" +
				"}\n" +
				" \n" +
				"// above this line need not be included in later script console sessions\n" +
				"def printProps(x) { appCtx.getData().printProps(rawOut, x) }\n" +
				" \n" +
				"// example: see what can be accessed on the frontier\n" +
				"printProps(job.crawlController.frontier)");
*/
		Pair<String, String> scriptPair03 = createScriptPair("Script03", "nashorn");
		scripts.put(scriptPair03, "Blah, blah");
/*
		scripts.put(scriptPair03, "import com.sleepycat.je.DatabaseEntry;\n" +
				"import com.sleepycat.je.OperationStatus;\n" +
				" \n" +
				"MAX_URLS_TO_LIST = 1000\n" +
				" \n" +
				"pendingUris = job.crawlController.frontier.pendingUris\n" +
				" \n" +
				"rawOut.println \"(this seems to be more of a ceiling) pendingUris.pendingUrisDB.count()=\" + pendingUris.pendingUrisDB.count()\n" +
				"rawOut.println()\n" +
				" \n" +
				"cursor = pendingUris.pendingUrisDB.openCursor(null, null);\n" +
				"key = new DatabaseEntry();\n" +
				"value = new DatabaseEntry();\n" +
				"count = 0;\n" +
				" \n" +
				"while (cursor.getNext(key, value, null) == OperationStatus.SUCCESS && count < MAX_URLS_TO_LIST) {\n" +
				"    if (value.getData().length == 0) {\n" +
				"        continue;\n" +
				"    }\n" +
				"    curi = pendingUris.crawlUriBinding.entryToObject(value);\n" +
				"    rawOut.println curi\n" +
				"    count++\n" +
				"}\n" +
				"cursor.close();\n" +
				" \n" +
				"rawOut.println()\n" +
				"rawOut.println count + \" pending urls listed\"");
*/
		return scripts;
	}

	private Pair<String, String> createScriptPair(String scriptName, String scriptType) {
		Pair<String, String> pair = new Pair<String, String>(scriptName, scriptType);
		return pair;
	}

	/**
	 * @return Returns the targetInstanceManager.
	 */
	public TargetInstanceManager getTargetInstanceManager() {
		return targetInstanceManager;
	}

	/**
	 * @param targetInstanceManager The targetInstanceManager to set.
	 */
	public void setTargetInstanceManager(TargetInstanceManager targetInstanceManager) {
		this.targetInstanceManager = targetInstanceManager;
	}

	/**
	 * @param authorityManager The authorityManager to set.
	 */
	public void setAuthorityManager(AuthorityManager authorityManager) {
		this.authorityManager = authorityManager;
	}

	/**
	 *
	 * @return The first available H3 HarvestAgent instance that we can find.
	 */
	private HarvestAgent getHarvestAgent() {

		return HarvestAgentUtil.getHarvestAgent(getApplicationContext());
	}

}
