package org.webcurator.ui.target.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractCommandController;
import org.webcurator.ui.archive.ArchiveCommand;
import org.webcurator.ui.common.Constants;

public class CustomDepositFormController extends AbstractCommandController {
    /** the logger. */
    private Log log;
	public CustomDepositFormController() {
        log = LogFactory.getLog(getClass());
		setCommandClass(ArchiveCommand.class);
	}

	@Override
	protected ModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object comm, BindException errors) throws Exception {
		ArchiveCommand command = (ArchiveCommand) comm;
		ModelAndView mav = new ModelAndView("deposit-form-envelope");
		mav.addObject(Constants.GBL_CMD_DATA, command);
		return mav;
	}

}
