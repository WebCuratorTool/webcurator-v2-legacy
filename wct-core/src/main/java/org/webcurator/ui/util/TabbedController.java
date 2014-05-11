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
package org.webcurator.ui.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.BeanUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.Validator;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.BaseCommandController;
import org.springframework.web.util.WebUtils;
import org.webcurator.ui.common.Constants;
import org.webcurator.ui.target.command.TargetDefaultCommand;
import org.webcurator.ui.target.command.TargetGeneralCommand;
import org.webcurator.ui.target.command.TargetInstanceCommand;

/**
 * The <code>TabbedController</code> extends Spring's BaseCommandController to
 * create a controller that helps with tabbed interfaces. The TabbedController
 * itself is the main manager for a set of tabs. It is responsible for
 * controlling entry into the tabs, exit from the set of tabs, and determining
 * which tab should be displayed.
 * 
 * The TabConfig class manages the set of tabs in a given tab set.
 * 
 * The TabHandler is responsible for processing the entry or submission of any
 * given tab.
 * 
 * This controller relies on the presence of parameters in the request to
 * identify what type of action should be performed. The parameter do not need a
 * specific value - their presence is all that is required. The list of
 * parameters that control the action are as follows:
 * 
 * <ul>
 * <li><code>_tab_current_page</code> - identifies the current page.</li>
 * <li><code>_tab_change</code> - indicates that we are changing to a
 * different tab.</li>
 * <li><code>_tab_save</code> - indicates we are saving the tabset.</li>
 * <li><code>_tab_cancel</code> - indicates we are cancelling any changes we
 * have made in this tabset.</li>
 * </ul>
 * 
 * If _tab_change is specified, this controller uses the value of the
 * <code>tabChangedTo</code> parameter to identify the title of the next tab
 * to be displayed.
 * 
 * If none of these actions are selected, the TabbedController invokes the
 * processOther method of the current tab's handler and lets it decide what to
 * do. This is useful for supporting "sub-actions" within a tab.
 * 
 * The TabbedController only defines handlers for POST requests. For GET
 * requests the request is delegated through to the showForm method on the
 * subclass. This method is not exactly the same as the standard Spring showForm
 * method as it may have a Command object associated with it. If you wish to
 * bookmark an tabset, it is critical the the showForm method is overriden as it
 * is the only method that responds to a GET request.
 * 
 * @author bbeaumont
 */
public abstract class TabbedController extends BaseCommandController {

	/** The default command class for the entry into this tabbed controller */
	private Class defaultCommandClass = null;
	/** The default validator for entry into this tabbed controller */
	private Validator defaultValidator = null;
	/** The tab configuration - set of tabs in the tabset */
	private TabConfig tabConfig;

	/**
	 * Get the tab configuration.
	 * 
	 * @return The tab configuration.
	 */
	public TabConfig getTabConfig() {
		return tabConfig;
	}

	/**
	 * Set the tab configuration.
	 * 
	 * @param tabConfig
	 *            the tab configuration.
	 */
	public void setTabConfig(TabConfig tabConfig) {
		this.tabConfig = tabConfig;
	}

	/**
	 * Get the ID of the next tab to be displayed. This looks for a hidden
	 * fields named "_TAB"
	 * 
	 * @param req
	 *            The HttpServletRequest.
	 * @return The ID of the next tab to be displayed.
	 */
	public String getNextTab(HttpServletRequest req) {
		String code = req.getParameter("_TAB");
		return code;
	}

	/**
	 * Initialise the binders for this request. This method checks to see if
	 * there is a current page enabled. If there is, this calls the binder
	 * method of the tab handler for that tab.
	 */
	protected void initBinder(HttpServletRequest request,
			ServletRequestDataBinder binder) throws Exception {
		super.initBinder(request, binder);
		String currentPage = request.getParameter("_tab_current_page");
		if (currentPage != null && !currentPage.trim().equals("")) {
			Tab currentTab = tabConfig.getTabByID(currentPage);
			currentTab.getTabHandler().initBinder(request, binder);
		}
	}

	/**
	 * An inner class for a Tabbed ModelAndView. This limits the amount of setup
	 * required to ensure that tabs are correctly set up.
	 * 
	 * @author bbeaumont
	 * 
	 */
	public class TabbedModelAndView extends ModelAndView {
		/** The tab status */
		private TabStatus tabStatus = new TabStatus();

		/**
		 * Construct and new Model and View.
		 */
		public TabbedModelAndView() {
			super();
			this.setViewName(getTabConfig().getViewName());
			this.addObject("tabs", tabConfig);
			this.addObject("tabStatus", tabStatus);
		}

		/**
		 * Construct a new model and view.
		 * 
		 * @param modelName
		 *            The name of the model object to add.
		 * @param modelObj
		 *            The model object to set.
		 */
		public TabbedModelAndView(String modelName, Object modelObj) {
			super(getTabConfig().getViewName(), modelName, modelObj);
			this.addObject("tabs", tabConfig);
			this.addObject("tabStatus", tabStatus);
		}

		/**
		 * Get the tab status object for the view.
		 * 
		 * @return The tab status object for this view.
		 */
		public TabStatus getTabStatus() {
			return tabStatus;
		}
	}

	/**
	 * Process the save instruction for this set of tabs. This is invoked when
	 * the user saves a tabset. This action is identified by the presence of the
	 * _tab_save parameter in the request.
	 * 
	 * The implementation of this method should save the tabset to the database
	 * and then return a ModelAndView of a logical "next" page. The
	 * TabbedController will perform validation of the current tab, but if there
	 * is any validation required across tabs, it may need to be done in this
	 * method.
	 * 
	 * @param currentTab
	 *            The current tab being posted.
	 * @param req
	 *            The HttpServletRequest.
	 * @param res
	 *            The HttpServletResponse.
	 * @param comm
	 *            The Spring command object.
	 * @param errors
	 *            The Spring errors object.
	 * @return The ModelAndView object to display.
	 */
	protected abstract ModelAndView processSave(Tab currentTab,
			HttpServletRequest req, HttpServletResponse res, Object comm,
			BindException errors);

	/**
	 * Process the cancel instruction for this set of tabs. This is invoked when
	 * the user cancels a tabset. This action is identified by the presence of
	 * the _tab_cancel parameter in the request.
	 * 
	 * In general this method will abort any changes made in the tabset and
	 * return the user to a logical point in the application.
	 * 
	 * @param currentTab
	 *            The current tab being posted.
	 * @param req
	 *            The HttpServletRequest.
	 * @param res
	 *            The HttpServletResponse.
	 * @param comm
	 *            The Spring command object.
	 * @param errors
	 *            The Spring errors object.
	 * @return The ModelAndView object to display.
	 */
	protected abstract ModelAndView processCancel(Tab currentTab,
			HttpServletRequest req, HttpServletResponse res, Object comm,
			BindException errors);

	protected abstract void switchToEditMode(HttpServletRequest req);

	/**
	 * Process the entry into the tabset.
	 * 
	 * In general this method will load an object into the tabset.
	 * 
	 * @param req
	 *            The HttpServletRequest.
	 * @param res
	 *            The HttpServletResponse.
	 * @param comm
	 *            The Spring command object.
	 * @param errors
	 *            The Spring errors object.
	 * @return The ModelAndView object to display.
	 */
	protected abstract ModelAndView processInitial(HttpServletRequest req,
			HttpServletResponse res, Object comm, BindException errors);

	/**
	 * This is the main method of the TabbedController. It identifies which
	 * command has been entered and how the command must be processed. See the
	 * class description for the controller parameters.
	 * 
	 * @param req
	 *            The HttpServletRequest.
	 * @param res
	 *            The HttpServletResponse.
	 * @param comm
	 *            The Spring command object.
	 * @param errors
	 *            The Spring errors object.
	 * @return The next ModelAndView object to be displayed.
	 * @throws Exception
	 *             Any exception raised by the handlers.
	 */
	protected ModelAndView processFormSubmission(HttpServletRequest req, HttpServletResponse res, Object comm, BindException errors) throws Exception {
		String currentPage = req.getParameter("_tab_current_page");
		Tab currentTab = tabConfig.getTabByID(currentPage);
		//log.info("processFormSubmission");
		//log.info("Current Tab: " + currentTab);	
		
		// Tab Change Handler
		if( WebUtils.hasSubmitParameter(req, "_tab_change") 		) {
			
			Tab nextTab = null;
			
			// If there are errors, do not allow the tab to be changed. Simply
			// add the command and errors to the model and return the user to
			// the same tab.
			if(errors.hasErrors()) {
				TabbedModelAndView tmav = currentTab.getTabHandler().preProcessNextTab(this, currentTab, req, res, comm, errors);
				tmav.getTabStatus().setCurrentTab(currentTab);
				tmav.addObject(Constants.GBL_CMD_DATA, errors.getTarget());
	            tmav.addObject(Constants.GBL_ERRORS, errors);				
				return tmav;
			}
			// There are no errors, so process the tab (saving the information
			// to the business objects) and return the view for the next tab,
			// as identified by the "tabChangedTo" parameter.
			else {
				currentTab.getTabHandler().processTab(this, currentTab, req, res, comm, errors);
				nextTab = tabConfig.getTabByTitle(req.getParameter("tabChangedTo"));
				// ensure that the target instance oid is set (enables us to directly refer the user to a specific tab)
				if (comm instanceof TargetInstanceCommand && ((TargetInstanceCommand)comm).getTargetInstanceId() == null && WebUtils.hasSubmitParameter(req, "targetInstanceOid")) {
					String targetInstanceOid = req.getParameter("targetInstanceOid");
					Long targetInstanceId = Long.parseLong(targetInstanceOid);
					((TargetInstanceCommand)comm).setTargetInstanceId(targetInstanceId);
				}
				TabbedModelAndView tmav = nextTab.getTabHandler().preProcessNextTab(this, nextTab, req, res, comm, errors);
				tmav.getTabStatus().setCurrentTab(nextTab);
				
				
							
				return tmav;				
			}
		} else if (WebUtils.hasSubmitParameter(req, "_tab_edit"))
		{
			
			switchToEditMode(req);
			TabbedModelAndView tmav = currentTab.getTabHandler().preProcessNextTab(this, currentTab, req, res, comm, errors);
			tmav.getTabStatus().setCurrentTab(currentTab);
			return tmav;
		}
				
		// Save Handler
		else if( WebUtils.hasSubmitParameter(req, "_tab_save")) {
			// Process the tab.
			currentTab.getTabHandler().processTab(this, currentTab, req, res, comm, errors);
			
			// If there are any errors, do not invoke the save controller, but
			// simply return the current tab with the command and errors objects
			// populated.
			if(errors.hasErrors()) {
				TabbedModelAndView tmav = currentTab.getTabHandler().preProcessNextTab(this, currentTab, req, res, comm ,errors);
				tmav.getTabStatus().setCurrentTab(currentTab);
				tmav.addObject(Constants.GBL_CMD_DATA, comm);
				tmav.addObject(Constants.GBL_ERRORS, errors);
				return tmav;
			}		
			else {
				// There were no errors, so invoke the save method on the
				// subclass.
				return processSave(currentTab, req, res, comm, errors);
			}
		}
		
		// Cancel Handler
		else if( WebUtils.hasSubmitParameter(req, "_tab_cancel")) {
			return processCancel(currentTab, req, res, comm, errors);
		}
		
		// No standard action has been provided that the TabbedController
		// can handle. In this case, call the processOther method on the
		// handler of the current tab and let the tab decide what to do.
		else {
			//log.debug(currentTab.getTabHandler().getClass().toString());
			ModelAndView mav = currentTab.getTabHandler().processOther(this, currentTab, req, res, comm, errors);
			//if(mav instanceof TabbedModelAndView) {
			//	log.debug("Moving to: " + ((TabbedModelAndView)mav).getTabStatus().getCurrentTab().toString());
			//}
			
			if(errors.hasErrors()) {
				mav.addObject(Constants.GBL_ERRORS, errors);
			}
			
			return mav;
			// return processOther(currentTab, req, res, comm, errors);
		}
	}

	/**
	 * Get the Spring command for this request. This method overrides the Spring
	 * configuration to get the appropriate command class for the currently
	 * selected tab.
	 * 
	 * @param req
	 *            The HttpServletRequest.
	 * @return An unpopulated command class.
	 */
	@Override
	protected Object getCommand(HttpServletRequest req) throws Exception {
		String currentPage = req.getParameter("_tab_current_page");
		Tab currentTab = tabConfig.getTabByID(currentPage);
		Class clazz = currentTab.getCommandClass();
		return BeanUtils.instantiateClass(clazz);
	}

	/**
	 * General point of access to the controller.
	 * 
	 * @param request
	 *            The HttpServletRequest object.
	 * @param response
	 *            The HttpServletResponse object.
	 * @return The ModelAndView to display.
	 * @throws Exception
	 *             if anything fails.
	 */
	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// If the submission method is a POST.
		if ("POST".equals(request.getMethod())) {
			// Is there a tab currently selected? If there is, then get the
			// command object for that tab, populate and validate the command,
			// and call the processFormSubmission method to work out what
			// to do next.
			if (WebUtils.hasSubmitParameter(request, "_tab_current_page")) {
				Object command = getCommand(request);
				ServletRequestDataBinder binder = bindAndValidate(request,
						command);

				// create a TargetDefaultCommand and bind the editor context if we need to go directly to a tab
				if (WebUtils.hasSubmitParameter(request, "tabForceChangeTo") && request.getParameter("tabForceChangeTo").equals("true")) {
					Object newCommand = BeanUtils.instantiateClass(defaultCommandClass);
					if (newCommand instanceof TargetDefaultCommand && request.getParameter("targetOid") != null) {
						Long targetOid = Long.parseLong(request.getParameter("targetOid"));
						((TargetDefaultCommand)newCommand).setTargetOid(targetOid);
						showForm(request, response, newCommand, binder.getErrors());
					}
				}
				
				return processFormSubmission(request, response, command, binder
						.getErrors());
			}

			// No tab is currently selected, so we need to instantated, bind,
			// and validate the default command. In general this will load
			// an object from the database into the tabset.
			else {
				Object command = BeanUtils
						.instantiateClass(defaultCommandClass);
				ServletRequestDataBinder binder = bindAndValidate(request,
						command);
				return processInitial(request, response, command, binder
						.getErrors());
			}
		} else {
			// Even for a form, we may need a command object.
			if (defaultCommandClass != null) {
				Object command = BeanUtils
						.instantiateClass(defaultCommandClass);
				ServletRequestDataBinder binder = bindAndValidate(request,
						command);
				return showForm(request, response, command, binder.getErrors());
			} else {
				return showForm(request, response, null, null);
			}

		}
	}

	/**
	 * Override the bind and validate method to use the validate and command
	 * objects defined by the tab, instead of those defined for the controller.
	 * This allows us to have different command and validator classes per
	 * handler.
	 * 
	 * @param req
	 *            The HttpServletRequest object.
	 * @param command
	 *            The Spring command object.
	 * @param errors
	 *            The Spring errors object.
	 * @throws Exception
	 *             on failure.
	 */
	@Override
	protected void onBindAndValidate(HttpServletRequest req, Object command,
			BindException errors) throws Exception {
		// TODO Auto-generated method stub
		if (WebUtils.hasSubmitParameter(req, "_tab_current_page")) {
			String currentPage = req.getParameter("_tab_current_page");
			Tab currentTab = tabConfig.getTabByID(currentPage);
			if (currentTab.getValidator() != null) {
				currentTab.getValidator().validate(command, errors);
			}
		} else {
			if (defaultValidator != null) {
				defaultValidator.validate(command, errors);
			}
		}
	}

	/**
	 * Show the form in response to a GET request. In general this may delegate
	 * the call and do the same as processInitial. This can be important if you
	 * wish to be able to bookmark an entry point into the tab (since bookmarks
	 * must be GET requests).
	 * 
	 * @param req
	 *            The HttpServletRequest object.
	 * @param res
	 *            The HttpSerlvetResponse object.
	 * @param command
	 *            The Spring command object.
	 * @param bex
	 *            The Spring errors object.
	 * @return The model and view to display.
	 * @throws Exception
	 *             if any errors are raised.
	 */
	protected abstract ModelAndView showForm(HttpServletRequest req,
			HttpServletResponse res, Object command, BindException bex)
			throws Exception;

	/**
	 * Set the Spring command class to use if we have no current tab. e.g. The
	 * first entry into the TabbedController.
	 * 
	 * @param defaultCommand
	 *            The defaultCommandClass to set.
	 */
	public void setDefaultCommandClass(Class defaultCommand) {
		this.defaultCommandClass = defaultCommand;
	}

}
