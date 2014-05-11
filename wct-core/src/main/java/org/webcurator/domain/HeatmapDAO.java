package org.webcurator.domain;

import java.util.Map;

import org.webcurator.domain.model.core.HeatmapConfig;

public interface HeatmapDAO {

	/**
	 * Get scheduling heat map configurations sorted by ascending
	 * threshold value 
	 * @return list of configurations, sorted by ascending threshold 
	 */
    Map<String, HeatmapConfig> getHeatmapConfigurations();
    HeatmapConfig getConfigByOid(Long oid);
    void saveOrUpdate(HeatmapConfig config);

}
