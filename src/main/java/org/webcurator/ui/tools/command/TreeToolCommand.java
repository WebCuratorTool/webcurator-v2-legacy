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
package org.webcurator.ui.tools.command;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.springframework.web.multipart.MultipartFile;

/**
 * The command object for the tree tool.
 * @author bbeaumont
 */
public class TreeToolCommand {
    /** The maximum length of the provenance note */
    public static final int CNST_MAX_PROVENANCE_NOTE_LENGTH = 1024;
    
    public static final String ACTION_SAVE = "save";
    public static final String ACTION_TREE_ACTION = "treeAction";
    public static final String ACTION_VIEW = "view";
    public static final String ACTION_SHOW_HOP_PATH = "showHopPath";
    public static final String ACTION_CANCEL = "cancel";
    
    public static final String IMPORT_FILE = "file";
    public static final String IMPORT_URL = "URL";
    public static final String IMPORT_AQA_FILE= "aqa";

    public static final String PARAM_PROVENANCE_NOTE = "provenanceNote";
    
    private Long loadTree = null;
    private Long toggleId = null;
    private Long markForDelete = null;
    private Long selectedRow = null;
    private Long selectedRow2 = null;
    private Boolean propagateDelete = null;
    private Long hrOid = null;
    private String provenanceNote = null;
    private String targetURL = null;
    private String sourceURL = null;
    private String selectedUrl = null;
    private String importType = null;
    //private String fileContent;
    private MultipartFile sourceFile = null;
    private String actionCmd = null;
    private Long targetInstanceOid = null;
    private String logFileName = null;
    private String[] aqaImports;

    public boolean isAction(String actionString) {
        return actionString.equals(actionCmd);
    }
    
    public Long getLoadTree() {
        return loadTree;
    }
    public void setLoadTree(Long loadTree) {
        this.loadTree = loadTree;
    }
    public Long getToggleId() {
        return toggleId;
    }
    public void setToggleId(Long toggleId) {
        this.toggleId = toggleId;
    }
    public Long getMarkForDelete() {
        return markForDelete;
    }
    public void setMarkForDelete(Long markForDelete) {
        this.markForDelete = markForDelete;
    }
    public Boolean getPropagateDelete() {
        return propagateDelete;
    }
    public void setPropagateDelete(Boolean propagateDelete) {
        this.propagateDelete = propagateDelete;
    }
    public Long getHrOid() {
        return hrOid;
    }
    public void setHrOid(Long hrOid) {
        this.hrOid = hrOid;
    }
    public String getProvenanceNote() {
        return provenanceNote;
    }
    public void setProvenanceNote(String provenanceNote) {
        this.provenanceNote = provenanceNote;
    }
    public String getTargetURL() {
        return targetURL;
    }
    public void setTargetURL(String targetURL) {
        this.targetURL = targetURL;
    }
    public String getSourceURL() {
        return sourceURL;
    }
    public void setSourceURL(String sourceURL) {
        this.sourceURL = sourceURL;
    }
    public String getImportType() {
        return importType;
    }
    public void setImportType(String importType) {
        this.importType = importType;
    }
	public MultipartFile getSourceFile() {
		return sourceFile;
	}
	
	public void setSourceFile(MultipartFile sourceFile) {
		this.sourceFile = sourceFile;
	}
    public Long getTargetInstanceOid() {
        return targetInstanceOid;
    }
    public void setTargetInstanceOid(Long targetInstanceOid) {
        this.targetInstanceOid = targetInstanceOid;
    }
	
    /**
     * @return Returns the actionCmd.
     */
    public String getActionCmd() {
        return actionCmd;
    }
    /**
     * @param actionCmd The actionCmd to set.
     */
    public void setActionCmd(String actionCmd) {
        this.actionCmd = actionCmd;
    }
    
    public Long getSelectedRow() {
        return selectedRow;
    }
    
    public void setSelectedRow(Long selectedRow) {
        this.selectedRow = selectedRow;
    }
    
    public Long getSelectedRow2() {
        return selectedRow2;
    }
    
    public void setSelectedRow2(Long selectedRow2) {
        this.selectedRow2 = selectedRow2;
    }
    
    /**
     * @return Returns the selectedUrl.
     */
    public String getSelectedUrl() {
        return selectedUrl;
    }
    /**
     * @param selectedUrl The selectedUrl to set.
     */
    public void setSelectedUrl(String selectedUrl) {
        this.selectedUrl = selectedUrl;
    }
    
    /**
     * @return Returns the AQA logFileName.
     */
    public String getLogFileName() {
        return logFileName;
    }
    /**
     * @param logFileName The logFileName to set.
     */
    public void setLogFileName(String logFileName) {
        this.logFileName = logFileName;
    }

	/**
	 * @return the aqa import files names
	 */
	public String[] getAqaImports() {
		return aqaImports;
	}
	
	/**
	 * @param aqaImports the aqaImports to set
	 */
	public void setAqaImports(String[] aqaImports) {
		this.aqaImports = aqaImports;
	}
    
}
