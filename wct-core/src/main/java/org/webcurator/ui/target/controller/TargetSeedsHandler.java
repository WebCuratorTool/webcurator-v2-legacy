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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.NumberFormat;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.context.MessageSource;
import org.springframework.validation.BindException;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.multipart.support.ByteArrayMultipartFileEditor;
import org.springframework.web.servlet.ModelAndView;
import org.webcurator.auth.AuthorityManager;
import org.webcurator.core.permissionmapping.PermissionMappingStrategy;
import org.webcurator.core.permissionmapping.UrlUtils;
import org.webcurator.core.targets.TargetManager;
import org.webcurator.domain.Pagination;
import org.webcurator.domain.model.auth.Privilege;
import org.webcurator.domain.model.core.BusinessObjectFactory;
import org.webcurator.domain.model.core.Permission;
import org.webcurator.domain.model.core.PermissionExclusion;
import org.webcurator.domain.model.core.Seed;
import org.webcurator.domain.model.core.Target;
import org.webcurator.ui.common.Constants;
import org.webcurator.ui.target.TargetEditorContext;
import org.webcurator.ui.target.command.SeedsCommand;
import org.webcurator.ui.target.validator.TargetSeedsValidator;
import org.webcurator.ui.util.Tab;
import org.webcurator.ui.util.TabbedController;
import org.webcurator.ui.util.TabbedController.TabbedModelAndView;

/**
 * The handler for the target seeds handler.
 * @author bbeaumont
 */
public class TargetSeedsHandler extends AbstractTargetTabHandler {
	private static Log log = LogFactory.getLog(TargetSeedsHandler.class);

	private BusinessObjectFactory businessObjectFactory = null;
	
	private TargetManager targetManager = null;
	
	private TargetSeedsValidator validator = null;

	private AuthorityManager authorityManager = null;
	
	private MessageSource messageSource = null;
	
	@Override
    public void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws Exception {
		// Determine the necessary formats.
        NumberFormat nf = NumberFormat.getInstance(request.getLocale());
        
        // Register the binders.
        binder.registerCustomEditor(Long.class, new CustomNumberEditor(Long.class, nf, true));
        
        // to actually be able to convert Multipart instance to byte[]
        // we have to register a custom editor (in this case the
        // ByteArrayMultipartEditor
        binder.registerCustomEditor(byte[].class, new ByteArrayMultipartFileEditor());
        // now Spring knows how to handle multipart object and convert them
    }
	
	/* (non-Javadoc)
	 * @see org.webcurator.ui.util.TabHandler#processTab(org.webcurator.ui.util.TabbedController, org.webcurator.ui.util.Tab, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.Object, org.springframework.validation.BindException)
	 */
	public void processTab(TabbedController tc, Tab currentTab,
			HttpServletRequest req, HttpServletResponse res, Object comm,
			BindException errors) {
		// Do nothing.
	}

	/* (non-Javadoc)
	 * @see org.webcurator.ui.util.TabHandler#preProcessNextTab(org.webcurator.ui.util.TabbedController, org.webcurator.ui.util.Tab, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.Object, org.springframework.validation.BindException)
	 */
	public TabbedModelAndView preProcessNextTab(TabbedController tc,
			Tab nextTabID, HttpServletRequest req, HttpServletResponse res,
			Object comm, BindException errors) {
		TabbedModelAndView tmav = tc.new TabbedModelAndView();
		
		TargetEditorContext ctx = getEditorContext(req);
		
		List<Seed> seeds = ctx.getSortedSeeds();
		
		tmav.addObject("seeds", seeds);
		tmav.addObject("quickPicks", ctx.getQuickPickPermissions());
		tmav.addObject("allowMultiplePrimarySeeds", targetManager.getAllowMultiplePrimarySeeds());
		
		return tmav;
	}
	
	private boolean mapSeed(TargetEditorContext ctx, Seed aSeed, long mappingOption) throws SeedLinkWrongAgencyException {
		// Automatic mapping.
		if(mappingOption == SeedsCommand.PERM_MAPPING_AUTO) {
			Set<Permission> permissions = PermissionMappingStrategy.getStrategy().getMatchingPermissions(ctx.getTarget(), aSeed);
			
			// Make sure our linked permissions are in our context cache.
			Set<Permission> toLink = new HashSet<Permission>();
			for(Permission p : permissions) {
				ctx.putObject(p);
				toLink.add(p);
			}
			
			//aSeed.setPermissions(toLink);
			return linkSeed(ctx.getTarget(), aSeed, toLink);
			
		}
		
		// Map to nothing
		else if(mappingOption == SeedsCommand.PERM_MAPPING_NONE) {
			// Don't set any permissions.
			return false;
		}
		
		// Map to a quick pick.
		else {
			Permission quickPick = (Permission) ctx.getObject(Permission.class, mappingOption);
			//aSeed.addPermission(quickPick);
			return linkSeed(ctx.getTarget(), aSeed, quickPick);
		}
	}
	
	/**
	 * 
	 * @param aTarget  The target that is being linked.
	 * @param aSeed The seed.
	 * @param aPermission The permisison.
	 * @return true if any exclusions have been added.
	 */
	private boolean linkSeed(Target aTarget, Seed aSeed, Permission aPermission) throws SeedLinkWrongAgencyException {
		if(!aPermission.getOwningAgency().equals(aTarget.getOwningUser().getAgency())) {
			throw new SeedLinkWrongAgencyException();
		}
		else {
			aSeed.addPermission(aPermission);
			if(aPermission.getExclusions().size() > 0) {
				aTarget.getOverrides().setOverrideExcludeUriFilters(true);
				for(PermissionExclusion excl : aPermission.getExclusions()) {
					if (!aTarget.getOverrides().getExcludeUriFilters().contains(excl.getUrl())) {
						aTarget.getOverrides().getExcludeUriFilters().add(excl.getUrl());
					}					
				}
			}
			
			return aPermission.getExclusions().size() > 0;
		}
	}
	
	/**
	 * 
	 * @param aTarget  The target that is being linked.
	 * @param aPermission The permisison.
	 * @param aSeed The seed.
	 * @return true if any exclusions have been added.
	 */
	private boolean linkSeed(Target aTarget, Seed aSeed, Set<Permission> aPermissionList) throws SeedLinkWrongAgencyException {
		boolean exclusionsAdded = false;
		for(Permission p : aPermissionList) {
			exclusionsAdded = linkSeed(aTarget, aSeed, p) | exclusionsAdded;
		}
		
		return exclusionsAdded;
	}	
	
	private boolean isSelected(HttpServletRequest req, Seed seed)
	{
		String onOff = (req.getParameterValues("chkSelect"+seed.getIdentity())!=null)?req.getParameterValues("chkSelect"+seed.getIdentity())[0]:"off";
		
		return onOff.equals("on");
	}
	
	/* (non-Javadoc)
	 * @see org.webcurator.ui.util.TabHandler#processOther(org.webcurator.ui.util.TabbedController, org.webcurator.ui.util.Tab, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.Object, org.springframework.validation.BindException)
	 */
	public ModelAndView processOther(TabbedController tc, Tab currentTab,
			HttpServletRequest req, HttpServletResponse res, Object comm,
			BindException errors) {
		SeedsCommand command = (SeedsCommand) comm;		

		TargetEditorContext ctx = getEditorContext(req);
		
		if(authorityManager.hasAtLeastOnePrivilege(ctx.getTarget(), Privilege.MODIFY_TARGET, Privilege.CREATE_TARGET )) {
		
			if(command.isAction(SeedsCommand.ACTION_ADD)) {
				if(errors.hasErrors()) {
					// Go to the Seeds tab.
					TabbedModelAndView tmav = preProcessNextTab(tc, currentTab, req, res, comm, errors);
					tmav.addObject(Constants.GBL_CMD_DATA, command);
					tmav.getTabStatus().setCurrentTab(currentTab);
					return tmav;				
				}
				else {
					// Create the seed business object.
					Seed seed = businessObjectFactory.newSeed(ctx.getTarget());
					seed.setSeed( command.getSeed().trim() ); //remove any trailing spaces
					
					// Set the first seed to be primary, others to be secondary.
					seed.setPrimary(ctx.getTarget().getSeeds().size() == 0);
					
					// Associate with the correct permissions.
					long option = command.getPermissionMappingOption().longValue();
					
					boolean addedExclusions = false;
					try {
						addedExclusions = mapSeed(ctx, seed, option);
						
						ctx.putObject(seed);
						ctx.getTarget().addSeed(seed);
					}
					
					// The seed we've tried to link to is from a different 
					// agency.
					catch(SeedLinkWrongAgencyException ex) {
						errors.reject("target.seeds.link.wrong_agency", new Object[] { }, "One of the selected seeds cannot be linked because it belongs to another agency.");
					}
						
					// Go to the Seeds tab.
					TabbedModelAndView tmav = preProcessNextTab(tc, currentTab, req, res, comm, errors);
					tmav.getTabStatus().setCurrentTab(currentTab);
					
					if(addedExclusions) { 
						tmav.addObject(Constants.GBL_MESSAGES, messageSource.getMessage("target.linkseeds.exclusions", new Object[] {}, Locale.getDefault()));
					}
					
					return tmav;
					
					
				}
			}
			
			if(command.isAction(SeedsCommand.ACTION_REMOVE)) {
				// Remove the URL.
				Seed seedToRemove = (Seed) ctx.getObject(Seed.class, command.getSelectedSeed());
				ctx.getTarget().removeSeed(seedToRemove);
				
				// Go back to the URLs tab.
				TabbedModelAndView tmav = preProcessNextTab(tc, currentTab, req, res, comm, errors);
				tmav.getTabStatus().setCurrentTab(currentTab);
				return tmav;			
			}
		
			if(command.isAction(SeedsCommand.ACTION_REMOVE_SELECTED)) {
				// Remove the selected URLs.
				Set<Seed> seeds = ctx.getTarget().getSeeds();
				Set<Seed> seedsToRemove = new HashSet<Seed>();
				Iterator<Seed> it = seeds.iterator();
				while(it.hasNext())
				{
					Seed seed = it.next();
					if(isSelected(req, seed))
					{
						seedsToRemove.add(seed);
					}
				}
				
				it = seedsToRemove.iterator();
				while(it.hasNext())
				{
					Seed seedToRemove = it.next();
					ctx.getTarget().removeSeed(seedToRemove);
				}
				
				// Go back to the URLs tab.
				TabbedModelAndView tmav = preProcessNextTab(tc, currentTab, req, res, comm, errors);
				tmav.getTabStatus().setCurrentTab(currentTab);
				return tmav;			
			}
		
			if( command.isAction(SeedsCommand.ACTION_TOGGLE_PRIMARY)) {
				Seed seed = (Seed) ctx.getObject(Seed.class, command.getSelectedSeed());

				if(targetManager.getAllowMultiplePrimarySeeds())
				{
					//toggle the selected seed
					seed.setPrimary(!seed.isPrimary());
				}
				else
				{
					//Reset all seeds to not primary
					List<Seed> seeds = ctx.getSortedSeeds();
					Iterator<Seed> it = seeds.iterator();
					while(it.hasNext())
					{
						Seed listItem = it.next();
						listItem.setPrimary(false);
					}
					
					//Set the selected seed to primary
					seed.setPrimary(true);
				}
				
				// Go back to the URLs tab.
				TabbedModelAndView tmav = preProcessNextTab(tc, currentTab, req, res, comm, errors);
				tmav.getTabStatus().setCurrentTab(currentTab);
				return tmav;				
			}
			
			if( command.isAction(SeedsCommand.ACTION_UNLINK)) {
				Seed seed = (Seed) ctx.getObject(Seed.class, command.getSelectedSeed());
				Permission permission = (Permission) ctx.getObject(Permission.class, command.getSelectedPermission());
				
				
				seed.removePermission(permission);
				
				// Go back to the URLs tab.
				TabbedModelAndView tmav = preProcessNextTab(tc, currentTab, req, res, comm, errors);
				tmav.getTabStatus().setCurrentTab(currentTab);
				return tmav;				
			}
			
			if(command.isAction(SeedsCommand.ACTION_UNLINK_SELECTED)) {
				//Unlink all permissions from the selected seeds
				Set<Seed> seeds = ctx.getTarget().getSeeds();
				Iterator<Seed> it = seeds.iterator();
				while(it.hasNext())
				{
					Seed seed = it.next();
					if(isSelected(req, seed))
					{
						Set<Permission> permissions = seed.getPermissions();
						permissions.clear();
						seed.getTarget().setDirty(true);
					}
				}

				// Go back to the URLs tab.
				TabbedModelAndView tmav = preProcessNextTab(tc, currentTab, req, res, comm, errors);
				tmav.getTabStatus().setCurrentTab(currentTab);
				return tmav;				
			}
			
			if( command.isAction(SeedsCommand.ACTION_LINK_NEW)) {
				SeedsCommand newCommand = new SeedsCommand();
				Seed selectedSeed = (Seed) ctx.getObject(Seed.class, command.getSelectedSeed());
				
				newCommand.setSelectedSeed(command.getSelectedSeed());
				newCommand.setSearchType(SeedsCommand.SEARCH_URL);
				newCommand.setUrlSearchCriteria(selectedSeed.getSeed());
				
				boolean doSearch = validator.validateLinkSearch(command, errors);
				
				return processLinkSearch(tc, ctx.getTarget(), newCommand, doSearch, errors);
			}
			
			if(command.isAction(SeedsCommand.ACTION_LINK_SELECTED)) {
				//Unlink all permissions from the selected seeds
				Set<Seed> seeds = ctx.getTarget().getSeeds();
				Set<Seed> selectedSeeds = new HashSet<Seed>();
				String seedList = "";
				Iterator<Seed> it = seeds.iterator();
				while(it.hasNext())
				{
					Seed seed = it.next();
					if(isSelected(req, seed))
					{
						selectedSeeds.add(seed);
						if(seedList.isEmpty())
						{
							seedList = seed.getIdentity();
						}
						else
						{
							seedList += ","+seed.getIdentity();
						}
					}
				}
				
				if(selectedSeeds.size() > 0)
				{
					SeedsCommand newCommand = new SeedsCommand();
					
					newCommand.setSelectedSeed(seedList);
					newCommand.setSearchType(SeedsCommand.SEARCH_SITES);
					newCommand.setSiteSearchCriteria("");
					
					ctx.putAllObjects(selectedSeeds);
					
					return processLinkSearch(tc, ctx.getTarget(), newCommand, false, errors);
				}
				else
				{
					// Go back to the URLs tab.
					TabbedModelAndView tmav = preProcessNextTab(tc, currentTab, req, res, comm, errors);
					tmav.getTabStatus().setCurrentTab(currentTab);
					return tmav;				
				}
			}
			
			if( command.isAction(SeedsCommand.ACTION_LINK_NEW_SEARCH)) {
				
				if(errors.hasErrors()) {
					return processLinkSearch(tc, ctx.getTarget(), command, false, errors);
				}
				else {
					SeedsCommand newCommand = new SeedsCommand();
					
					newCommand.setSelectedSeed(command.getSelectedSeed());
					newCommand.setSearchType(command.getSearchType());
					newCommand.setSiteSearchCriteria(command.getSiteSearchCriteria());
					newCommand.setUrlSearchCriteria(command.getUrlSearchCriteria());
					newCommand.setPageNumber(command.getPageNumber());
					
					return processLinkSearch(tc, ctx.getTarget(), newCommand, true, errors);
				}
				
			}
			
			if( command.isAction(SeedsCommand.ACTION_LINK_NEW_CONFIRM) ) {
				if(errors.hasErrors()) {
					boolean doSearch = validator.validateLinkSearch(command, errors);
					return processLinkSearch(tc, ctx.getTarget(), command, doSearch, errors);
				}
				else {
					// Go back to the URLs tab.
					TabbedModelAndView tmav = preProcessNextTab(tc, currentTab, req, res, comm, errors);
					tmav.getTabStatus().setCurrentTab(currentTab);
					
					// Get the selected seed(s).
					String[] seedList = command.getSelectedSeed().split(",");
					for(int s = 0; s < seedList.length; s++)
					{
						Seed theSeed = (Seed) ctx.getObject(Seed.class, seedList[s]);
			
						// Link all the selected permissions.
						String[] perms = command.getLinkPermIdentity();
						Set<Permission> toLink = new HashSet<Permission>();	
						
						boolean wrongAgencyPermission = false;
						for(int i=0; i<perms.length && !wrongAgencyPermission; i++) {
							Permission linkPerm = targetManager.loadPermission(ctx, perms[i]);
							toLink.add(linkPerm);
							
							if(!linkPerm.getOwningAgency().equals(ctx.getTarget().getOwner().getAgency())) {
								wrongAgencyPermission = true;
							}
						}
						
						try {
							boolean addedExclusions = linkSeed(ctx.getTarget(), theSeed, toLink);					
							
							if(addedExclusions) {
								tmav.addObject(Constants.GBL_MESSAGES, messageSource.getMessage("target.linkseeds.exclusions", new Object[] {}, Locale.getDefault()));						
							}
							
						}
						catch(SeedLinkWrongAgencyException ex) {
							errors.reject("target.seeds.link.wrong_agency", new Object[] { }, "One of the selected seeds cannot be linked because it belongs to another agency.");
							
							boolean doSearch = validator.validateLinkSearch(command, errors);
							return processLinkSearch(tc, ctx.getTarget(), command, doSearch, errors);
						}
					}
					return tmav;
				}
			}		
			
			if( command.isAction(SeedsCommand.ACTION_LINK_NEW_CANCEL) ) {
				// Go back to the URLs tab.
				TabbedModelAndView tmav = preProcessNextTab(tc, currentTab, req, res, comm, errors);
				tmav.getTabStatus().setCurrentTab(currentTab);
				return tmav;				
			}
			
			if( command.isAction(SeedsCommand.ACTION_START_IMPORT) ) {
				return getImportSeedsTabModel(tc, ctx);
			}
			
			
			if( command.isAction(SeedsCommand.ACTION_DO_IMPORT) ) {
				
				BufferedReader reader = null;
				
				MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) req;
				CommonsMultipartFile file = (CommonsMultipartFile) multipartRequest.getFile("seedsFile");

				if(command.getSeedsFile().length == 0 || file.getOriginalFilename() == null || "".equals(file.getOriginalFilename().trim())) {
					errors.reject("target.seeds.import.nofile");
					TabbedModelAndView tmav = getImportSeedsTabModel(tc, ctx);
					tmav.addObject(Constants.GBL_ERRORS, errors);
					return  tmav;					
				}
				if(!file.getOriginalFilename().endsWith(".txt") && !"text/plain".equals(file.getContentType())) {
					errors.reject("target.seeds.import.filetype");
					TabbedModelAndView tmav = getImportSeedsTabModel(tc, ctx);
					tmav.addObject(Constants.GBL_ERRORS, errors);
					return  tmav;
				}
				else {
					
					
					try {
						reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(command.getSeedsFile())));
						
						
						
						List<Seed> validSeeds = new LinkedList<Seed>();
						boolean success = true;
						
						int lineNumber = 1;
						String line = reader.readLine();
						SeedsCommand importCommand = new SeedsCommand();
						while(success && line != null) {
							if( !line.startsWith("#") && !"".equals(line.trim())) {
								Seed seed = businessObjectFactory.newSeed(ctx.getTarget());
								importCommand.setSeed(line);
								seed.setSeed(importCommand.getSeed());
								
								if(UrlUtils.isUrl(seed.getSeed())) {
									mapSeed(ctx, seed, command.getPermissionMappingOption());
	
									// Update the target.
			     					ctx.putObject(seed);
			     					validSeeds.add(seed);
								}
								else {
									success = false;
								}
							}
							
							if(success) {
								line = reader.readLine();
								lineNumber++;
							}
						}
						
						if(success) {
							for(Seed seed: validSeeds) {
								ctx.getTarget().addSeed(seed);
							}
						}
						else {
							errors.reject("target.seeds.import.badline", new Object[] { lineNumber }, "Bad seed detected on line: " + lineNumber);
						}
						
					}
					catch(SeedLinkWrongAgencyException ex) {
						errors.reject("target.seeds.link.wrong_agency", new Object[] { }, "One of the selected seeds cannot be linked because it belongs to another agency.");
					}
					catch(IOException ex) {
						errors.reject("target.seeds.import.ioerror");
						log.error("Failed to import seeds", ex);
					}
					finally {
						try { reader.close(); } catch(Exception ex) { log.debug("Failed to close uploaded seeds file", ex); }
					}
					
					if(errors.hasErrors()) {
						TabbedModelAndView tmav = getImportSeedsTabModel(tc, ctx);
						tmav.addObject(Constants.GBL_ERRORS, errors);
						return  tmav;
					}
					else {
						// Go back to the URLs tab.
						TabbedModelAndView tmav = preProcessNextTab(tc, currentTab, req, res, comm, errors);
						tmav.getTabStatus().setCurrentTab(currentTab);
						return tmav;
					}
				}
			}
		}
		
		if(command.isAction(SeedsCommand.ACTION_SET_NAME)) {
			String id = command.getUpdatedNameSeedId();
			if(id!=null) {
				for(Seed seed:ctx.getSortedSeeds()) {
					if(seed.getOid().equals(Long.valueOf(id))) {
						String value = command.getUpdatedNameSeedValue();
						if(value.trim().equals("")) {
							errors.reject("target.seeds.name.edit.required");
							TabbedModelAndView tmav = preProcessNextTab(tc, currentTab, req, res, comm, errors);
							tmav.addObject(Constants.GBL_CMD_DATA, command);
							tmav.getTabStatus().setCurrentTab(currentTab);
							return tmav;				
						}
						if(UrlUtils.isUrl(seed.getSeed())) {
							seed.setSeed(UrlUtils.fixUrl(value));
	     					ctx.putObject(seed);
						} else {
							errors.reject("target.seeds.name.edit.invalid");
							TabbedModelAndView tmav = preProcessNextTab(tc, currentTab, req, res, comm, errors);
							tmav.addObject(Constants.GBL_CMD_DATA, command);
							tmav.getTabStatus().setCurrentTab(currentTab);
							return tmav;				
						}
						System.out.println("Found seed " + id);
					}
				}
			}
		}
		
		TabbedModelAndView tmav = preProcessNextTab(tc, currentTab, req, res, comm, errors);
		tmav.getTabStatus().setCurrentTab(currentTab);
		return tmav;
	}
	
	public ModelAndView processLinkSearch(TabbedController tc, Target aTarget, SeedsCommand command, boolean doSearch, BindException errors) {
		TabbedModelAndView tmav = getSeedLinkTabModel(tc);
		tmav.addObject(Constants.GBL_CMD_DATA, command);

		if(doSearch) {
			Pagination results = null;
		
			if(SeedsCommand.SEARCH_SITES.equals(command.getSearchType())) {
				results = targetManager.findPermissionsBySiteTitle(aTarget, command.getSiteSearchCriteria(), command.getPageNumber());
			}
			else {
				results = targetManager.findPermissionsByUrl(aTarget, command.getUrlSearchCriteria(), command.getPageNumber());
			}	
			tmav.addObject("results", results);
		}
		
		tmav.addObject(Constants.GBL_ERRORS, errors);
		
		return tmav;	
	}
	
	private TabbedModelAndView getSeedLinkTabModel(TabbedController controller) {
		Tab tab = controller.getTabConfig().getTabByID("SEEDS").createSubTab("../target-seeds-link.jsp");
		TabbedModelAndView tmav = controller.new TabbedModelAndView();
		tmav.getTabStatus().setCurrentTab(tab);
		tmav.getTabStatus().setEnabled(false);
		
		return tmav;
	}	

	private TabbedModelAndView getImportSeedsTabModel(TabbedController controller, TargetEditorContext ctx) {
		Tab tab = controller.getTabConfig().getTabByID("SEEDS").createSubTab("../target-seeds-import.jsp");
		tab.setFormEncodingType("multipart/form-data");
		
		TabbedModelAndView tmav = controller.new TabbedModelAndView();
		tmav.getTabStatus().setCurrentTab(tab);
		tmav.getTabStatus().setEnabled(false);
		
		tmav.addObject("quickPicks", ctx.getQuickPickPermissions());		
		
		return tmav;
	}	
		
	/**
	 * @param businessObjectFactory The businessObjectFactory to set.
	 */
	public void setBusinessObjectFactory(BusinessObjectFactory businessObjectFactory) {
		this.businessObjectFactory = businessObjectFactory;
	}


	/**
	 * @param targetManager The targetManager to set.
	 */
	public void setTargetManager(TargetManager targetManager) {
		this.targetManager = targetManager;
	}


	/**
	 * @param validator The validator to set.
	 */
	public void setValidator(TargetSeedsValidator validator) {
		this.validator = validator;
	}


	/**
	 * @param authorityManager The authorityManager to set.
	 */
	public void setAuthorityManager(AuthorityManager authorityManager) {
		this.authorityManager = authorityManager;
	}

	/**
	 * @param messageSource The messageSource to set.
	 */
	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}
	
}
