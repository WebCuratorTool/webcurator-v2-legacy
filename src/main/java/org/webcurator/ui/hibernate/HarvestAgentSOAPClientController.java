package org.webcurator.ui.hibernate;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;
import org.webcurator.core.harvester.agent.HarvestAgentStatusDTO;
import org.webcurator.soap.client.HarvestAgentSOAPClient;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class HarvestAgentSOAPClientController extends AbstractController {
    private static Logger log = LogManager.getLogger(HarvestAgentSOAPClientController.class);
    private HarvestAgentSOAPClient harvestAgentSOAPClient;

    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {
        HarvestAgentStatusDTO harvestAgentStatusDTO = null;
        // get request parameter
        String jobNumber = httpServletRequest.getParameter("jobNumber");
        log.debug("Job Number = " + jobNumber);
        if (jobNumber != null && !jobNumber.equals("")) {
            harvestAgentStatusDTO = harvestAgentSOAPClient.getHarvestAgentStatus(jobNumber);
        } else {
            harvestAgentStatusDTO = new HarvestAgentStatusDTO("0", 0, "");
        }
        // update model
        ModelAndView mav = new ModelAndView();
        mav.addObject("harvestAgentStatusDTO", harvestAgentStatusDTO);
        mav.setViewName("HibernateTestView");
        return mav;
    }

    public void setHarvestAgentSOAPClient(HarvestAgentSOAPClient harvestAgentSOAPClient) {
        this.harvestAgentSOAPClient = harvestAgentSOAPClient;
    }
}
