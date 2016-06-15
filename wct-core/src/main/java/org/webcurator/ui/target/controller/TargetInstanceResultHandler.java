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

import java.text.NumberFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.webcurator.core.agency.AgencyUserManager;
import org.webcurator.core.exceptions.WCTRuntimeException;
import org.webcurator.core.harvester.coordinator.HarvestCoordinator;
import org.webcurator.core.scheduler.TargetInstanceManager;
import org.webcurator.core.store.DigitalAssetStore;
import org.webcurator.domain.model.auth.Agency;
import org.webcurator.domain.model.auth.User;
import org.webcurator.domain.model.core.CustomDepositFormCriteriaDTO;
import org.webcurator.domain.model.core.CustomDepositFormResultDTO;
import org.webcurator.domain.model.core.DublinCore;
import org.webcurator.domain.model.core.Flag;
import org.webcurator.domain.model.core.HarvestResult;
import org.webcurator.domain.model.core.RejReason;
import org.webcurator.domain.model.core.TargetInstance;
import org.webcurator.ui.admin.command.FlagCommand;
import org.webcurator.ui.common.Constants;
import org.webcurator.ui.target.command.TargetInstanceCommand;
import org.webcurator.ui.util.DateUtils;
import org.webcurator.ui.util.Tab;
import org.webcurator.ui.util.TabHandler;
import org.webcurator.ui.util.TabbedController;
import org.webcurator.ui.util.TabbedController.TabbedModelAndView;

/**
 * The handler for the target instance results/harvests tab.
 * @author nwaight
 */
public class TargetInstanceResultHandler extends TabHandler {

    private TargetInstanceManager targetInstanceManager;
    private HarvestCoordinator harvestCoordinator;
    /** the digital asset store containing the harvests. */
    private DigitalAssetStore digitalAssetStore = null;

    private AgencyUserManager agencyUserManager;

    public void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws Exception {
        NumberFormat nf = NumberFormat.getInstance(request.getLocale());
        binder.registerCustomEditor(java.lang.Long.class, new CustomNumberEditor(java.lang.Long.class, nf, true));
        binder.registerCustomEditor(java.util.Date.class, DateUtils.get().getFullDateTimeEditor(true));      
    }
    
    public void processTab(TabbedController tc, Tab currentTab,
            HttpServletRequest req, HttpServletResponse res, Object comm,
            BindException errors) {
        // process the submit of the tab called on change tab or save
    }

    public TabbedModelAndView preProcessNextTab(TabbedController tc,
            Tab nextTabID, HttpServletRequest req, HttpServletResponse res,
            Object comm, BindException errors) {
        // build mav stuff b4 displaying the tab
        
        Boolean editMode = false;
        TargetInstanceCommand cmd = null;
        
        TargetInstance ti = null;
        if (comm instanceof TargetInstanceCommand) {
        	cmd = (TargetInstanceCommand) comm;
        	if ((Boolean)(req.getSession().getAttribute(TargetInstanceCommand.SESSION_MODE))) {
        		editMode = true;
        	}
        }
        
        if (req.getSession().getAttribute(TargetInstanceCommand.SESSION_TI) == null) {
    		ti = targetInstanceManager.getTargetInstance(cmd.getTargetInstanceId(), true);
    		req.getSession().setAttribute(TargetInstanceCommand.SESSION_TI, ti);
    		req.getSession().setAttribute(TargetInstanceCommand.SESSION_MODE, editMode);
    	}
    	else {
    		ti = (TargetInstance) req.getSession().getAttribute(TargetInstanceCommand.SESSION_TI); 
    		editMode = (Boolean) req.getSession().getAttribute(TargetInstanceCommand.SESSION_MODE);
    	}  
        
        TabbedModelAndView tmav =  buildResultsModel(tc, ti, editMode, errors); 
        buildCustomDepositFormDetails(req, errors, ti, tmav);
        
        // we also need to load the coloured flags since these are needed by the GENERAL tab
		List<Flag> flags = agencyUserManager.getFlagForLoggedInUser();
		tmav.addObject(FlagCommand.MDL_FLAGS, flags);
		
        return tmav;
        
    }
    
    public TabbedModelAndView buildResultsModel(TabbedController tc, TargetInstance ti, boolean editMode, BindException errors) { 
    	TabbedModelAndView tmav = tc.new TabbedModelAndView();
        List<HarvestResult> results = targetInstanceManager.getHarvestResults(ti.getOid());
		User user = org.webcurator.core.util.AuthUtil.getRemoteUserObject();
		Agency agency = user.getAgency();
        List<RejReason> rejectionReasons = agencyUserManager.getValidRejReasonsForTIs(agency.getOid());
        tmav.addObject("editMode", editMode);
        tmav.addObject(TargetInstanceCommand.MDL_INSTANCE, ti);
        tmav.addObject("results", results);
        tmav.addObject("reasons", rejectionReasons);
        if(errors.hasErrors())
        {
        	tmav.addObject(Constants.GBL_ERRORS, errors);
        }
        return tmav;     	
    }

    public ModelAndView processOther(TabbedController tc, Tab currentTab,
            HttpServletRequest req, HttpServletResponse res, Object comm,
            BindException errors) {
        TargetInstanceCommand cmd = (TargetInstanceCommand) comm;
        if (cmd.getCmd().equals(TargetInstanceCommand.ACTION_HARVEST)) {
            ModelAndView mav = new ModelAndView();            
            HashMap agents = harvestCoordinator.getHarvestAgents();
            mav.addObject(Constants.GBL_CMD_DATA, cmd);
            mav.addObject(TargetInstanceCommand.MDL_AGENTS, agents);
            mav.setViewName(Constants.VIEW_HARVEST_NOW);
            
            return mav;
        }
        else if (cmd.getCmd().equals(TargetInstanceCommand.ACTION_ENDORSE)) {
        	// set the ti state and the hr states
        	TargetInstance ti = (TargetInstance) req.getSession().getAttribute(TargetInstanceCommand.SESSION_TI);         
        	ti.setState(TargetInstance.STATE_ENDORSED);
        	
        	for (HarvestResult hr : ti.getHarvestResults()) {
				if (hr.getOid().equals(cmd.getHarvestResultId())) {
					hr.setState(HarvestResult.STATE_ENDORSED);
				}
				else {
					if(hr.getState() != HarvestResult.STATE_REJECTED)
					{
						hr.setState(HarvestResult.STATE_REJECTED);
		        		harvestCoordinator.removeIndexes(hr);
					}
				}
				
				targetInstanceManager.save(hr);
			}
        	
        	targetInstanceManager.save(ti);
        	
        	req.getSession().setAttribute(TargetInstanceCommand.SESSION_TI, ti);
        	
        	//TabbedModelAndView tmav = preProcessNextTab(tc, currentTab, req, res, comm, errors);
        	TabbedModelAndView tmav = buildResultsModel(tc, ti, true, errors);
        	tmav.getTabStatus().setCurrentTab(currentTab);
        	buildCustomDepositFormDetails(req, errors, ti, tmav);
        	
        	return tmav;
        }
        else if (cmd.getCmd().equals(TargetInstanceCommand.ACTION_UNENDORSE)) {
        	// set the ti state and the hr states
        	TargetInstance ti = (TargetInstance) req.getSession().getAttribute(TargetInstanceCommand.SESSION_TI);         
        	ti.setState(TargetInstance.STATE_HARVESTED);
        	
        	for (HarvestResult hr : ti.getHarvestResults()) {
				hr.setState(0);
				
				targetInstanceManager.save(hr);
			}
        	
        	targetInstanceManager.save(ti);
        	
        	req.getSession().setAttribute(TargetInstanceCommand.SESSION_TI, ti);
        	
        	TabbedModelAndView tmav = buildResultsModel(tc, ti, true, errors);
        	tmav.getTabStatus().setCurrentTab(currentTab);
        	
        	return tmav;
        }
        else if (cmd.getCmd().equals(TargetInstanceCommand.ACTION_REJECT)) {
        	//	set the ti state and the hr states
        	TargetInstance ti = (TargetInstance) req.getSession().getAttribute(TargetInstanceCommand.SESSION_TI); 
        	for (HarvestResult hr : ti.getHarvestResults()) {
				if (hr.getOid().equals(cmd.getHarvestResultId())) {
					if(hr.getState() != HarvestResult.STATE_REJECTED)
					{
						Long rejReasonId = cmd.getRejReasonId();
						if(rejReasonId==null) {
			                String[] codes = {"result.rejection.missing"};
			                Object[] args = new Object[0];
			                if (errors == null) {
			                    errors = new BindException(cmd, "command");
			                }
			                errors.addError(new ObjectError("command",codes,args,"Unable to reject harvest, no rejection reasons have been created."));
			             	req.getSession().setAttribute(TargetInstanceCommand.SESSION_TI, ti);
	        	        	
			            	//TabbedModelAndView tmav = preProcessNextTab(tc, currentTab, req, res, comm, errors);
			            	TabbedModelAndView tmav = buildResultsModel(tc, ti, true, errors);
			            	tmav.getTabStatus().setCurrentTab(currentTab);
			            	return tmav;
						}
						hr.setState(HarvestResult.STATE_REJECTED);
						RejReason rejReason = agencyUserManager.getRejReasonByOid(rejReasonId);
						hr.setRejReason(rejReason);
		        		harvestCoordinator.removeIndexes(hr);
					}
					
					targetInstanceManager.save(hr);
				}
			}

        	boolean allRejected = true;
        	for (HarvestResult hr : ti.getHarvestResults()) {        		
				if ((HarvestResult.STATE_REJECTED != hr.getState()) &&
						(HarvestResult.STATE_ABORTED != hr.getState())) {
					allRejected = false;
					break;
				}
			}
        	
        	if (allRejected) {
        		ti.setState(TargetInstance.STATE_REJECTED);
        		ti.setArchivedTime(new Date());
        	}
        	
    		targetInstanceManager.save(ti);
         	req.getSession().setAttribute(TargetInstanceCommand.SESSION_TI, ti);
        	        	        	
        	//TabbedModelAndView tmav = preProcessNextTab(tc, currentTab, req, res, comm, errors);
        	TabbedModelAndView tmav = buildResultsModel(tc, ti, true, errors);
        	tmav.getTabStatus().setCurrentTab(currentTab);
        	
        	return tmav;
        }
        else if (cmd.getCmd().equals(TargetInstanceCommand.ACTION_REINDEX)) {
        	Boolean reIndexSuccessful = null;
        	TargetInstance ti = (TargetInstance) req.getSession().getAttribute(TargetInstanceCommand.SESSION_TI); 

        	//Make sure any new HarvestResults are loaded
    		ti = targetInstanceManager.getTargetInstance(ti.getOid());
    		
        	for (HarvestResult hr : ti.getHarvestResults()) {
				if (hr.getOid().equals(cmd.getHarvestResultId()) &&
						hr.getState() == HarvestResult.STATE_INDEXING) {
					reIndexSuccessful = harvestCoordinator.reIndexHarvestResult(hr);
		        	break;
				}
			}

        	if(reIndexSuccessful != null && reIndexSuccessful == false)
        	{
                String[] codes = {"result.reindex.fail"};
                Object[] args = new Object[0];
                if (errors == null) {
                    errors = new BindException(cmd, "command");
                }
                errors.addError(new ObjectError("command",codes,args,"Reindex did not occur as HarvestResult is still indexing"));
        	}
        	
         	req.getSession().setAttribute(TargetInstanceCommand.SESSION_TI, ti);
        	TabbedModelAndView tmav = buildResultsModel(tc, ti, true, errors);
        	tmav.getTabStatus().setCurrentTab(currentTab);
        	
        	return tmav;
        }
        else if (cmd.getCmd().equals(TargetInstanceCommand.ACTION_ARCHIVE)) {
        	throw new WCTRuntimeException("Archive command processing is not implemented yet.");
        } 
        else {
            throw new WCTRuntimeException("Unknown command " + cmd.getCmd() + " recieved.");
        }
    }

    /**
     * @param harvestCoordinator The harvestCoordinator to set.
     */
    public void setHarvestCoordinator(HarvestCoordinator harvestCoordinator) {
        this.harvestCoordinator = harvestCoordinator;
    }

    /**
     * @param targetInstanceManager The targetInstanceManager to set.
     */
    public void setTargetInstanceManager(TargetInstanceManager targetInstanceManager) {
        this.targetInstanceManager = targetInstanceManager;
    }

	/**
	 * @param digitalAssetStore the digitalAssetStore to set
	 */
	public void setDigitalAssetStore(DigitalAssetStore digitalAssetStore) {
		this.digitalAssetStore = digitalAssetStore;
	}

	/**
	 * @param agencyUserManager the agencyUserManager to set
	 */
	public void setAgencyUserManager(AgencyUserManager agencyUserManager) {
		this.agencyUserManager = agencyUserManager;
	}

	protected void buildCustomDepositFormDetails(HttpServletRequest req, BindException errors, TargetInstance ti, TabbedModelAndView tmav) {
		boolean customDepositFormRequired = false;
		if (TargetInstance.STATE_ENDORSED.equals(ti.getState())) {
			try {
				User user = org.webcurator.core.util.AuthUtil.getRemoteUserObject();
				Agency agency = user.getAgency();
				CustomDepositFormCriteriaDTO criteria = new CustomDepositFormCriteriaDTO();
				criteria.setUserId(user.getUsername());
				DublinCore dc = ti.getTarget().getDublinCoreMetaData();
				if (dc != null)
					criteria.setTargetType(ti.getTarget().getDublinCoreMetaData().getType());
				if (agency != null)
					criteria.setAgencyId(String.valueOf(agency.getOid()));
				criteria.setAgencyName(agency.getName());
				CustomDepositFormResultDTO response = digitalAssetStore.getCustomDepositFormDetails(criteria);
				if (response != null && response.isCustomDepositFormRequired()) {
					String customDepositFormHTMLContent = response.getHTMLForCustomDepositForm();
					String customDepositFormURL = response.getUrlForCustomDepositForm();
					if (customDepositFormURL != null) {
						customDepositFormRequired = true;
						req.getSession().setAttribute("customDepositFormURL", customDepositFormURL);
						String producerId = response.getProducerId();
						if (producerId != null) {
							req.getSession().setAttribute("customDepositFormProducerId", producerId);
						}
					}
					if (customDepositFormHTMLContent != null) {
						customDepositFormRequired = true;
						req.getSession().setAttribute("customDepositFormHTMLContent", customDepositFormHTMLContent);
					}
				}
			} catch (Exception e) {
				throw new WCTRuntimeException("Exception when trying to determine the custom deposit form details: " + e.getMessage(), e);
			}
		}
		tmav.addObject("customDepositFormRequired", customDepositFormRequired);
	}
}
