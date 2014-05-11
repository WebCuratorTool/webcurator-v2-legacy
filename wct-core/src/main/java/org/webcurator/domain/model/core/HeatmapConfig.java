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
package org.webcurator.domain.model.core;

/**
 * @author mclean
 * @hibernate.class table="HEATMAP_CONFIG" lazy="false"
 * @hibernate.query name="org.webcurator.domain.model.core.HeatmapConfig.all" query="from org.webcurator.domain.model.core.HeatmapConfig hm order by hm.thresholdLowest"
 * @hibernate.query name="org.webcurator.domain.model.core.HeatmapConfig.getConfigByOid" query="SELECT hm FROM HeatmapConfig hm WHERE hm_oid=?"
 */
public class HeatmapConfig {
	public static final String QUERY_ALL = "org.webcurator.domain.model.core.HeatmapConfig.all";
	/** name if the Day and Time bandwidth restrictions query. */
	public static final String PARAM_START = "start";
	/**
	 * name if the Day and Time bandwidth restrictions query parameter end time.
	 */
	public static final String PARAM_END = "end";

    public static final String QRY_GET_CONFIG_BY_OID = "org.webcurator.domain.model.core.HeatmapConfig.getConfigByOid";

	/** The primary key. */
	private Long oid;
	private String name;
	private String displayName;
	/** Hexadecimal string representing the RGB color, e.g. 8FBC8F **/
	private String color;
	/**
	 * lowest value to display this color. The upper limit is set by other
	 * configurations, if any e.g. low=1, medium=7, high=12 - low=1 to 6,
	 * medium=7 to 11, high=above 12. 0 in this example will be uncolored
	 **/
	private int thresholdLowest;

	/**
	 * Get the OID of the object.
	 * 
	 * @return Returns the oid.
	 * @hibernate.id column="HM_OID"
	 *               generator-class="org.hibernate.id.MultipleHiLoPerTableGenerator"
	 * @hibernate.generator-param name="table" value="ID_GENERATOR"
	 * @hibernate.generator-param name="primary_key_column" value="IG_TYPE"
	 * @hibernate.generator-param name="value_column" value="IG_VALUE"
	 * @hibernate.generator-param name="primary_key_value" value="General"
	 */
	public Long getOid() {
		return oid;
	}

	public void setOid(Long oid) {
		this.oid = oid;
	}

	/**
	 * @hibernate.property column="HM_NAME" not-null="true"
	 */
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @hibernate.property column="HM_COLOR" not-null="true"
	 */
	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	/**
	 * @hibernate.property column="HM_THRESHOLD_LOWEST" not-null="true"
	 */
	public int getThresholdLowest() {
		return thresholdLowest;
	}

	public void setThresholdLowest(int thresholdLowest) {
		this.thresholdLowest = thresholdLowest;
	}

	/**
	 * @hibernate.property column="HM_DISPLAY_NAME" not-null="true"
	 */
	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

}
