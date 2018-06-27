package org.webcurator.ui.profiles.controller;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import org.webcurator.auth.AuthorityManager;
import org.webcurator.core.scheduler.TargetInstanceManager;
import org.webcurator.domain.model.auth.Privilege;
import org.webcurator.domain.model.core.TargetInstance;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

public class H3ScriptFileController implements Controller {
    /** The profile manager to load the profile */
    private TargetInstanceManager targetInstanceManager = null;
    /** The authority manager for checking permissions */
    private AuthorityManager authorityManager = null;
    /**
     * The name of the h3 scripts directory.
     */
    private String h3ScriptsDirectory = "";
    /** Logger for the H3ScriptFileController. **/
    private static Log log = LogFactory.getLog(H3ScriptFileController.class);

    @Override
    public ModelAndView handleRequest(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {
        // get target instance oid and script file name
        String targetInstanceOid = httpServletRequest.getParameter("targetInstanceOid");
        String scriptFileName = httpServletRequest.getParameter("scriptFileName"); // includes file extension
        if ((targetInstanceOid == null || targetInstanceOid.equals(""))
                || (scriptFileName == null || scriptFileName.equals(""))) {
            httpServletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } else {
            // check authority
            TargetInstance ti = targetInstanceManager.getTargetInstance(Long.parseLong(targetInstanceOid), true);
            if (authorityManager.hasAtLeastOnePrivilege(ti.getProfile(), new String[] {Privilege.MANAGE_TARGET_INSTANCES, Privilege.MANAGE_WEB_HARVESTER})) {
                httpServletResponse.setStatus(HttpServletResponse.SC_OK);
                httpServletResponse.setContentType("text/plain");
                PrintWriter pw = httpServletResponse.getWriter();
                String fileContents = getFileContents(scriptFileName);
                pw.println(fileContents);
                pw.flush();
            } else {
                httpServletResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
            }
        }
        return null;
    }

    private String getFileContents(String scriptFileName) {
        String script = "";
        try {
            File file = new File(h3ScriptsDirectory + File.separator + scriptFileName);
            script = FileUtils.readFileToString(file);
        } catch (IOException e) {
            log.error(e);
        }
        return script;
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
     * @param h3ScriptsDirectory The h3ScriptsDirectory to set.
     */
    public void setH3ScriptsDirectory(String h3ScriptsDirectory) {
        this.h3ScriptsDirectory = h3ScriptsDirectory;
    }
}
