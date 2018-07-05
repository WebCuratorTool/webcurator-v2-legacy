package org.webcurator.ui.util;

import org.springframework.context.ApplicationContext;
import org.webcurator.core.harvester.HarvesterType;
import org.webcurator.core.harvester.agent.HarvestAgent;
import org.webcurator.core.harvester.agent.HarvestAgentFactory;
import org.webcurator.core.harvester.coordinator.HarvestAgentManager;
import org.webcurator.domain.model.core.harvester.agent.HarvestAgentStatusDTO;

/**
 * Util class for common harvest agent methods
 */
public class HarvestAgentUtil {

    /**
     *
     * @return The first available H3 HarvestAgent instance that we can find.
     */
    public static HarvestAgent getHarvestAgent(ApplicationContext applicationContext) {

        HarvestAgentManager harvestAgentManager = (HarvestAgentManager) applicationContext.getBean("harvestAgentManager");
        HarvestAgentFactory harvestAgentFactory = (HarvestAgentFactory) applicationContext.getBean("harvestAgentFactory");

        HarvestAgentStatusDTO has = null;
        for (HarvestAgentStatusDTO h : harvestAgentManager.getHarvestAgents().values()) {
            if (h.getHarvesterType().equals(HarvesterType.HERITRIX3.name())) {
                has = h;
                break;
            }
        }
        if (has == null) {
            throw new RuntimeException("Could not find harvest agent of type " + HarvesterType.HERITRIX3);
        }
        return harvestAgentFactory.getHarvestAgent(has.getHost(), has.getPort(), has.getService());
    }}
