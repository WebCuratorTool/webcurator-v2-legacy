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
package org.webcurator.ui.agent.command;

/**
 * The command object for managing harvest agents.
 * @author nwaight
 */
public class ManageHarvestAgentCommand {
    /** The name of the harvest coordinator model object. */
    public static final String MDL_HARVEST_AGENTS = "harvestAgents";
    /** The name of the harvest coordinator model object. */
    public static final String MDL_HARVEST_AGENT = "harvestAgent";
    
    /** The name of the show harvest agent detail action. */
    public static final String ACTION_AGENT = "agentDetail";
    /** The name of the show harvest agent detail action. */
    public static final String ACTION_SUMMARY = "agentSummary";
    /** The name of the show harvest agent detail action. */
    public static final String ACTION_HOME = "agentHome";
    /** The name of the pause all harvest agents action. */
    public static final String ACTION_PAUSE = "pauseAll";
    /** The name of the resume all harvest agents action. */
    public static final String ACTION_RESUME = "resumeAll";    
    /** The name of the pause all harvest agents action. */
    public static final String ACTION_PAUSEQ = "pauseQueue";
    /** The name of the resume all harvest agents action. */
    public static final String ACTION_RESUMEQ = "resumeQueue";    

    public static final String ACTION_PAUSE_AGENT = "pauseAgent";
    public static final String ACTION_RESUME_AGENT = "resumeAgent";    

    public static final String ACTION_OPTIMIZE_DISABLE = "disableOptimization";
    public static final String ACTION_OPTIMIZE_ENABLE = "enableOptimization";    

    /** The name of the parameter actionCmd. */
    public static final String PARAM_ACTION = "actionCmd";
    /** The name of the parameter agent name. */
    public static final String PARAM_AGENT = "agentName";
    /** The name of the parameter queue paused. */
    public static final String PARAM_QUEUE_PAUSED = "queuePaused";
    public static final String PARAM_OPTIMIZATION_ENABLED = "optimizationEnabled";
    
    /** the command actionCmd. */
    private String actionCmd = "";
    /** the name of the harvest agent. */
    private String agentName = "";
    /** is the queue paused? */
    private boolean queuePaused = false;
    private boolean optimizationEnabled = false;
    private int optimizationLookaheadHours = 0;
        
    /** default constructor. */
    public ManageHarvestAgentCommand() {
        super();
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

    /**
     * @return Returns the agentName.
     */
    public String getAgentName() {
        return agentName;
    }

    /**
     * @param agentName The agentName to set.
     */
    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }   
    /**
     * @return Returns the queuePaused flag.
     */
    public boolean getQueuePaused() {
        return queuePaused;
    }

    /**
     * @param queuePaused The queuePaused flag to set.
     */
    public void setQueuePaused(boolean queuePaused) {
        this.queuePaused = queuePaused;
    }

	public boolean isOptimizationEnabled() {
		return optimizationEnabled;
	}

	public void setOptimizationEnabled(boolean optimizationEnabled) {
		this.optimizationEnabled = optimizationEnabled;
	}

	public int getOptimizationLookaheadHours() {
		return optimizationLookaheadHours;
	}

	public void setOptimizationLookaheadHours(int optimizationLookaheadHours) {
		this.optimizationLookaheadHours = optimizationLookaheadHours;
	}   

}
