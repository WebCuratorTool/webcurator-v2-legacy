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

import java.util.Date;

import org.webcurator.domain.model.dto.HeatmapConfigDTO;

/**
 * The command object for bandwidth restictions.
 * 
 * @author nwaight
 */
public class BandwidthRestrictionsCommand {
	/** the low limit of the bandwidth setting. */
	public static final Long CNSNT_LOW_LIMIT = new Long(0);

	/** The name of the edit action. */
	public static final String ACTION_EDIT = "edit";
	/** The name of the save action. */
	public static final String ACTION_SAVE = "save";
	/** The name of the save action. */
	public static final String ACTION_SAVE_HEATMAP = "saveHeatmap";
	/** The name of the delete action. */
	public static final String ACTION_DELETE = "delete";

	/** The name of the bandwidth restrictions model object. */
	public static final String MDL_RESTRICTIONS = "bandwidthRestrictions";
	/** The name of the days of the week model object. */
	public static final String MDL_DOW = "daysOfTheWeek";
	/** The name of the actionCmd model object. */
	public static final String MDL_ACTION = "action";
	public static final String MDL_HEATMAP_CONFIGS = "heatmapConfigs";

	/** The name of the parameter actionCmd. */
	public static final String PARAM_ACTION = "actionCmd";
	/** The name of the parameter day. */
	public static final String PARAM_DAY = "day";
	/** The name of the parameter oid. */
	public static final String PARAM_OID = "oid";
	/** The name of the parameter start time. */
	public static final String PARAM_START = "start";
	/** The name of the parameter end time. */
	public static final String PARAM_END = "end";
	/** The name of the parameter end time. */
	public static final String PARAM_LIMIT = "limit";
	public static final String PARAM_ALLOW_OPTIMIZE = "allowOptimize";

	public static final String PARAM_HEATMAP_CONFIGS = "heatmapConfigs";

	public static final String PARAM_HEATMAP_CONFIG_LOW = "lowHeatmapConfig";
	public static final String PARAM_HEATMAP_CONFIG_MEDIUM = "mediumHeatmapConfig";
	public static final String PARAM_HEATMAP_CONFIG_HIGH = "highHeatmapConfig";

	public static final String HEATMAP_LOW = "low";
	public static final String HEATMAP_MEDIUM = "medium";
	public static final String HEATMAP_HIGH = "high";

	/** the command actionCmd. */
	private String actionCmd = "";
	/** the restriction day. */
	private String day = "";
	/** the restriction oid. */
	private Long oid;
	/** the start time. */
	private Date start;
	/** the end time. */
	private Date end;
	/** the bandwidth restriction. */
	private Long limit;
	private boolean allowOptimize = true;

	private HeatmapConfigDTO lowHeatmapConfig = new HeatmapConfigDTO();
	private HeatmapConfigDTO mediumHeatmapConfig = new HeatmapConfigDTO();
	private HeatmapConfigDTO highHeatmapConfig = new HeatmapConfigDTO();

	/**
	 * @return Returns the actionCmd.
	 */
	public String getActionCmd() {
		return actionCmd;
	}

	/**
	 * @param action
	 *            The action to set.
	 */
	public void setActionCmd(String action) {
		this.actionCmd = action;
	}

	/**
	 * @return Returns the day.
	 */
	public String getDay() {
		return day;
	}

	/**
	 * @param day
	 *            The day to set.
	 */
	public void setDay(String day) {
		this.day = day;
	}

	/**
	 * @return Returns the oid.
	 */
	public Long getOid() {
		return oid;
	}

	/**
	 * @param oid
	 *            The oid to set.
	 */
	public void setOid(Long oid) {
		this.oid = oid;
	}

	/**
	 * @return Returns the end.
	 */
	public Date getEnd() {
		return end;
	}

	/**
	 * @param end
	 *            The end to set.
	 */
	public void setEnd(Date end) {
		this.end = end;
	}

	/**
	 * @return Returns the start.
	 */
	public Date getStart() {
		return start;
	}

	/**
	 * @param start
	 *            The start to set.
	 */
	public void setStart(Date start) {
		this.start = start;
	}

	/**
	 * @return Returns the limit.
	 */
	public Long getLimit() {
		return limit;
	}

	/**
	 * @param limit
	 *            The limit to set.
	 */
	public void setLimit(Long limit) {
		this.limit = limit;
	}

	public HeatmapConfigDTO getLowHeatmapConfig() {
		return lowHeatmapConfig;
	}

	public void setLowHeatmapConfig(HeatmapConfigDTO config) {
		this.lowHeatmapConfig = config;
	}

	public HeatmapConfigDTO getMediumHeatmapConfig() {
		return mediumHeatmapConfig;
	}

	public void setMediumHeatmapConfig(HeatmapConfigDTO config) {
		this.mediumHeatmapConfig = config;
	}

	public HeatmapConfigDTO getHighHeatmapConfig() {
		return highHeatmapConfig;
	}

	public void setHighHeatmapConfig(HeatmapConfigDTO config) {
		this.highHeatmapConfig = config;
	}

	public boolean isAllowOptimize() {
		return allowOptimize;
	}

	public void setAllowOptimize(boolean allowOptimize) {
		this.allowOptimize = allowOptimize;
	}

}
