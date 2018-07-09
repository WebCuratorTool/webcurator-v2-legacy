package org.webcurator.ui.target.controller;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractFormController;
import org.webcurator.core.scheduler.TargetInstanceManager;
import org.webcurator.core.targets.TargetManager;
import org.webcurator.core.util.AuthUtil;
import org.webcurator.domain.Pagination;
import org.webcurator.domain.TargetInstanceCriteria;
import org.webcurator.domain.model.auth.User;
import org.webcurator.domain.model.core.Target;
import org.webcurator.domain.model.core.TargetInstance;
import org.webcurator.ui.common.Constants;
import org.webcurator.ui.target.command.TargetInstanceCommand;

public class AnnotationAjaxController extends AbstractFormController {

    /** The manager to use to access the target instance. */
    private TargetInstanceManager targetInstanceManager;
    /** The manager to use to access the target. */
    private TargetManager targetManager;
    /** the logger. */
    private Log log;
    
    /** Default constructor. */    
    public AnnotationAjaxController() {
        super();
        setCommandClass(TargetInstanceCommand.class);
        log = LogFactory.getLog(getClass());
    }
    
	@Override
	protected ModelAndView processFormSubmission(HttpServletRequest request, HttpServletResponse response, Object aCmd, BindException aErrors)
			throws Exception {

		String ajaxRequest = request.getParameter(Constants.AJAX_REQUEST_TYPE);
		if (ajaxRequest.equals(Constants.AJAX_REQUEST_FOR_TI_ANNOTATIONS)) {
			return processTargetInstanceRequest(request, response, aCmd, aErrors);
		}
		if (ajaxRequest.equals(Constants.AJAX_REQUEST_FOR_TARGET_ANNOTATIONS)) {
			return processTargetRequest(request, response, aCmd, aErrors);
		}
		return null;
	}
	
	private ModelAndView processTargetInstanceRequest(HttpServletRequest request, HttpServletResponse response, Object aCmd, BindException aErrors)
			throws Exception {
		TargetInstanceCommand searchCommand = (TargetInstanceCommand) request.getSession().getAttribute(TargetInstanceCommand.SESSION_TI_SEARCH_CRITERIA);      
        
		// ensure that the seachCommand is valid
		if (searchCommand == null) return null;
		
        TargetInstanceCriteria criteria = new TargetInstanceCriteria();
        criteria.setSortorder(TargetInstanceCommand.SORT_DATE_DESC_BY_TARGET_OID);
        criteria.setTargetSearchOid(new Long(request.getParameter("targetOid")));
        criteria.setSearchOid(new Long(request.getParameter("targetInstanceOid")));
		User user = AuthUtil.getRemoteUserObject();
    	criteria.setAgency(user.getAgency().getName());        	
    	searchCommand.setAgency(user.getAgency().getName());
    	
        Set<String> states = new HashSet<String>();
		states.add(TargetInstance.STATE_HARVESTED);
		states.add(TargetInstance.STATE_ENDORSED);
		states.add(TargetInstance.STATE_ARCHIVED);
		states.add(TargetInstance.STATE_REJECTED);
		criteria.setStates(states);
      
		Pagination instances = targetInstanceManager.search(criteria, 0, 3); 
		
		ModelAndView mav = new ModelAndView(Constants.VIEW_TI_ANNOTATION_HISTORY);
		
		// the annotations are not recovered by hibernate during the ti fetch so we need to add them
		if ( instances != null ) {
			for (Iterator<TargetInstance> i = ((List<TargetInstance>) instances.getList()).iterator( ); i.hasNext(); ) {
				TargetInstance ti = i.next();
				ti.setAnnotations(targetInstanceManager.getAnnotations(ti));
			}		   	 
			mav.addObject(TargetInstanceCommand.MDL_INSTANCES, instances);
	        mav.addObject(Constants.GBL_CMD_DATA, searchCommand);
	        request.getSession().setAttribute(TargetInstanceCommand.SESSION_TI_SEARCH_CRITERIA, searchCommand);
	        mav.addObject("instances", instances.getList().size());
		}
		
		instances = null;
						
		return mav;

	}
	
	private ModelAndView processTargetRequest(HttpServletRequest request, HttpServletResponse response, Object aCmd, BindException aErrors)
			throws Exception {
		Long targetOid = new Long(request.getParameter("targetOid"));
		Target target = targetManager.load(targetOid, true);
		// the annotations are not recovered by hibernate during the target fetch so we need to add them
		target.setAnnotations(targetManager.getAnnotations(target));
		ModelAndView mav = new ModelAndView(Constants.VIEW_TARGET_ANNOTATION_HISTORY);
		mav.addObject(TargetInstanceCommand.TYPE_TARGET, target);
		return mav;
	}

	@Override
	protected ModelAndView showForm(HttpServletRequest request,
			HttpServletResponse response, BindException aErrors) throws Exception {
		
		String ajaxRequest = request.getParameter(Constants.AJAX_REQUEST_TYPE);
		if (ajaxRequest.equals(Constants.AJAX_REQUEST_FOR_TI_ANNOTATIONS)) {
			return processTargetInstanceRequest(request, response, null, aErrors);
		}
		if (ajaxRequest.equals(Constants.AJAX_REQUEST_FOR_TARGET_ANNOTATIONS)) {
			return processTargetRequest(request, response, null, aErrors);
		}
		return null;

	}

    /**
     * @param aTargetInstanceManager The targetInstanceManager to set.
     */
    public void setTargetInstanceManager(TargetInstanceManager aTargetInstanceManager) {
        targetInstanceManager = aTargetInstanceManager;
    }
    
	/**
	 * @param targetManager The targetManager to set.
	 */
	public void setTargetManager(TargetManager targetManager) {
		this.targetManager = targetManager;
	}	
}
