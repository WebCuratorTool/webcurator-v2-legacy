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
package org.webcurator.core.sites;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.webcurator.core.agency.AgencyUserManager;
import org.webcurator.core.notification.InTrayManager;
import org.webcurator.core.notification.MessageType;
import org.webcurator.core.util.Auditor;
import org.webcurator.domain.AnnotationDAO;
import org.webcurator.domain.Pagination;
import org.webcurator.domain.SiteCriteria;
import org.webcurator.domain.SiteDAO;
import org.webcurator.domain.model.auth.Privilege;
import org.webcurator.domain.model.core.Annotatable;
import org.webcurator.domain.model.core.Annotation;
import org.webcurator.domain.model.core.AuthorisingAgent;
import org.webcurator.domain.model.core.Permission;
import org.webcurator.domain.model.core.Site;
import org.webcurator.domain.model.dto.UserDTO;

/**
 * The implementation of the <code>SiteManager<code/> interface.
 * @see SiteManager
 * @author bbeaumont
 */
public class SiteManagerImpl implements SiteManager {
	
	private Auditor auditor = null;
	
	/** Site Data Access Object */
	private SiteDAO siteDao = null;
	
	/** the annotation data access object. */
	private AnnotationDAO annotationDAO = null;
	
	/** The intray manager */
	private InTrayManager intrayManager = null;

	/** The AgencyUserManager */
	private AgencyUserManager agencyUserManager = null;
	
	/** The List of listeners */
	private List<SiteManagerListener> listeners = new LinkedList<SiteManagerListener>();
	
	private enum EVENT_TYPE { before_save, after_save, before_delete, after_delete };
	
	
	/**
	 * Save the annotations on the Annotatable object.
	 * @param annotatable The object for which to save the annotations.
	 */
	private void saveAnnotations(Annotatable annotatable) {
		// Only save if there is something to do.
		List<Annotation> annotations = annotatable.getAnnotations();
		List<Annotation> deletedAnnotations = annotatable.getDeletedAnnotations();
		
		if (annotations != null && !annotations.isEmpty()) {
			
			// Iterate through the annotations and make sure that the OID and 
			// classname are set.
			for (Annotation annotation : annotations) {
				annotation.setObjectOid(annotatable.getOid());
				annotation.setObjectType(annotatable.getClass().getName());
			}
			
			annotationDAO.saveAnnotations(annotations);
		}
		
		if (deletedAnnotations != null && !deletedAnnotations.isEmpty()) {
			
			annotationDAO.deleteAnnotations(deletedAnnotations);
		}
	}
	
	
	/* (non-Javadoc)
     * @see org.webcurator.core.sites.SiteManager#save(org.webcurator.domain.model.core.Site)
     */
	public void save(Site aSite) {
		// Fire the before save event handlers.
		fireEvent( EVENT_TYPE.before_save, aSite);
		
		Long soid = aSite.getOid();	
		
		if (soid==null) {
			Date now = new Date();
			aSite.setCreationDate(now);
		}
		
		// Track permissions that will trigger audit events.
		Set<Permission> removedPermissions = aSite.getRemovedPermissions();
		List<Permission> newPermissions = new LinkedList<Permission>();
		
		for(Permission perm : aSite.getPermissions()) {
			if(perm.isNew()) {
				newPermissions.add(perm);
			}
		}
		
		// Save the site.
		siteDao.saveOrUpdate(aSite);

		// Save the annotations for the site.
		saveAnnotations(aSite);
		
		// Save the annotations for the Permissions.
		for(Permission p: aSite.getPermissions()) {
			saveAnnotations(p);
		}
		
		// Auditing messages.
		if (soid == null) {
			auditor.audit(Site.class.getName(), aSite.getOid(), Auditor.ACTION_NEW_SITE, "New Harvest Authorisation created");
		}
		else {
			auditor.audit(Site.class.getName(), aSite.getOid(), Auditor.ACTION_UPDATE_SITE, "Update Harvest Authorisation " + aSite.getTitle());
		}
		
		
		// New Permissions.
		for(Permission p: newPermissions) {
			auditor.audit(Permission.class.getName(), p.getOid(), Auditor.ACTION_NEW_PERMISSION, "New permission created");
		}
		
		// Removed Permissions.
		for(Permission p: removedPermissions) {
			auditor.audit(Permission.class.getName(), p.getOid(), Auditor.ACTION_DELETE_PERMISSION, "Permission deleted");
			intrayManager.deleteTask(p.getOid(), p.getResourceType(), MessageType.TASK_SEEK_PERMISSON);
		}
		
		// Updated Permissions.
		for(Permission p: aSite.getPermissions()) {
			if(p.isDirty()) {
				auditor.audit(Permission.class.getName(), p.getOid(), Auditor.ACTION_UPDATE_PERMISSION, "Permission updated");
			}
		}		
		

		for(Permission p: aSite.getPermissions()) {
			// Permissions that have been approved.			
			if(p.hasChangedState() && p.getStatus() == Permission.STATUS_APPROVED) {
				auditor.audit(Permission.class.getName(), p.getOid(), Auditor.ACTION_APPROVE_PERMISSION, "Permission approved");
				intrayManager.deleteTask(p.getOid(), p.getResourceType(), MessageType.TASK_SEEK_PERMISSON);
				
				List<UserDTO> users = agencyUserManager.getUserDTOsByTargetPrivilege(p.getOid());
				for(UserDTO user: users) {
					intrayManager.generateNotification(user.getOid(), MessageType.CATEGORY_MISC, MessageType.NOTIFICATION_PERMISSION_APPROVED, p);
				}
			}
			
			if(p.hasChangedState() && p.getStatus() == Permission.STATUS_DENIED) {
				intrayManager.deleteTask(p.getOid(), p.getResourceType(), MessageType.TASK_SEEK_PERMISSON);
				
				List<UserDTO> users = agencyUserManager.getUserDTOsByTargetPrivilege(p.getOid());
				for(UserDTO user: users) {
					intrayManager.generateNotification(user.getOid(), MessageType.CATEGORY_MISC, MessageType.NOTIFICATION_PERMISSION_DENIED, p);
				}				
			}

			if(p.hasChangedState() && p.getStatus() == Permission.STATUS_REQUESTED) {
				auditor.audit(Permission.class.getName(), p.getOid(), Auditor.ACTION_REQUESTED_PERMISSION, "Permission has been requested from the authoring agent(s)");
			}

			
			if(p.getStatus() == Permission.STATUS_PENDING && p.isCreateSeekPermissionTask()) {
				auditor.audit(Permission.class.getName(), p.getOid(), Auditor.ACTION_SEEK_PERMISSION, "A task has been created for another user to request permission from an authoring agent");
				intrayManager.generateUniqueTask(Privilege.CONFIRM_PERMISSION, MessageType.TASK_SEEK_PERMISSON, p);
			}
		}
		
		// Fire the after save event handlers.
		fireEvent( EVENT_TYPE.after_save, aSite);
	}
	
    
    /* (non-Javadoc)
     * @see org.webcurator.core.sites.SiteManager#load(java.lang.Long, boolean)
     */
    public Site getSite(Long siteOid, boolean fullyInitialise) {
        return siteDao.load(siteOid,fullyInitialise);
    }
    
	
    /* (non-Javadoc)
     * @see org.webcurator.core.sites.SiteManager#isSiteTitleUnique(Site)
     */
    public boolean isSiteTitleUnique(Site aSite) {
    	List<Site> sites = siteDao.listSitesByTitle(aSite.getTitle().toLowerCase());
    	if (null == sites || sites.isEmpty()) {
    		return true;
    	}
    	
    	if (sites.size() > 1) {
    		return false;
    	}
    	
    	if (sites.size() == 1) {
    		Site s = (Site) sites.iterator().next();
    		if (s.getOid().equals(aSite.getOid())) {
    			return true;
    		}
    	}
    	
    	return false;
    }
    
    public boolean isAuthAgencyNameUnique(Long oid, String name) {
    	return siteDao.isAuthAgencyNameUnique(oid, name);
    }
    
        
	/* (non-Javadoc)
     * @see org.webcurator.core.sites.SiteManager#fireEvent(org.webcurator.core.sites.SiteManagerImpl.EVENT_TYPE, org.webcurator.domain.model.core.Site)
     */
	private void fireEvent(EVENT_TYPE eventType, Site aSite) {
		
		for(SiteManagerListener l: listeners) {
			switch(eventType) {
				case before_save:
					l.beforeSave(aSite);
					break;
					
				case after_save:
					l.afterSave(aSite);
					break;
					
				case before_delete:
					l.beforeDelete(aSite);
					break;
			
				case after_delete:
					l.afterDelete(aSite);
					break;
			}
		}
	}


	/* (non-Javadoc)
     * @see org.webcurator.core.sites.SiteManager#setSiteDao(org.webcurator.domain.SiteDAO)
     */
	public void setSiteDao(SiteDAO siteDao) {
		this.siteDao = siteDao;
	}
	

	/* (non-Javadoc)
     * @see org.webcurator.core.sites.SiteManager#setListeners(java.util.List)
     */
	public void setListeners(List<SiteManagerListener> listeners) {
		this.listeners = listeners;
	}

	
	/* (non-Javadoc)
     * @see org.webcurator.core.sites.SiteManager#getAnnotations(Site)
     */
	public List<Annotation> getAnnotations(Site aSite) {
		List<Annotation> annotations = null;
		if (aSite.getOid() != null) {
			annotations = annotationDAO.loadAnnotations(Site.class.getName(), aSite.getOid());
		}
		
		if (annotations == null) {
			annotations = new ArrayList<Annotation>();
		}
		
		return annotations;
	}
	
	
    /**
     * Return a list of annotations for the specified Permission.
     * @param aPermission the permission to return the annotations for
     * @return the list of annotations
     */
    public List<Annotation> getAnnotations(Permission aPermission) {
		List<Annotation> annotations = null;
		if (aPermission.getOid() != null) {
			annotations = annotationDAO.loadAnnotations(Permission.class.getName(), aPermission.getOid());
		}
		
		if (annotations == null) {
			annotations = new ArrayList<Annotation>();
		}
		
		return annotations;
    }
	
    
    
	public int countSites() {
		return siteDao.countSites();
	}
	

	/** @see SiteManager#search(SiteCriteria). */
	public Pagination search(SiteCriteria aCriteria) { 
		return search(aCriteria, 0, 10);
	}

	
	/** @see SiteManager#search(SiteCriteria, int). */
	public Pagination search(SiteCriteria aCriteria, int aPage, int aPageSize) {
		return siteDao.search(aCriteria, aPage, aPageSize);
	}
	
	
	/**
	 * @param annotationDAO the annotationDAO to set
	 */
	public void setAnnotationDAO(AnnotationDAO annotationDAO) {
		this.annotationDAO = annotationDAO;
	}
	
	
	/**
	 * Get a count of the number of seeds related to a given permission.
	 * @param aPermissionOid The permission oid
	 * @return The number of seeds linked to the permission
	 */
	public int countLinkedSeeds(Long aPermissionOid) {
		return siteDao.countLinkedSeeds(aPermissionOid);
	}

	
	/**
	 * @param auditor The auditor to set.
	 */
	public void setAuditor(Auditor auditor) {
		this.auditor = auditor;
	}

	
	/**
	 * @param intrayManager The intrayManager to set.
	 */
	public void setIntrayManager(InTrayManager intrayManager) {
		this.intrayManager = intrayManager;
	}
	
	
	/**
	 * Search for existing Authorising Agencies by name.
	 * @param name The name of the agency to search for. 
	 * @param page The page number.
	 * @return A pagination of results.
	 */
	public Pagination searchAuthAgents(String name, int page) {
		return siteDao.searchAuthAgents(name, page);
	}
	
	
	/**
	 * Load an authorising agent from the database.
	 * @param authAgentOid The OID of the authorising agent to load.
	 * @return The authorising agent.
	 */
	public AuthorisingAgent loadAuthorisingAgent(final long authAgentOid) {
		return siteDao.loadAuthorisingAgent(authAgentOid);
	}

	
	/**
	 * Spring setter method for the Agency User Manager.
	 * @param agencyUserManager The agencyUserManager to set.
	 */
	public void setAgencyUserManager(AgencyUserManager agencyUserManager) {
		this.agencyUserManager = agencyUserManager;
	}
}
