package org.webcurator.core.harvester.coordinator;

import java.util.Collection;

import org.webcurator.domain.model.core.ArcHarvestFileDTO;
import org.webcurator.domain.model.core.HarvestResourceDTO;
import org.webcurator.domain.model.core.HarvestResultDTO;

public interface IndexerService {
    
    /**
     * Create a HarvestResult on the server in the Indexing state.
     * @param harvestResultDTO A DTO for the Harvest Result.
     * @return The OID of the Harvest Result.
     */
    Long createHarvestResult(HarvestResultDTO harvestResultDTO);


    /**
     * Add an ArcHarvestFile to a HarvestResult.
     * @param harvestResultOid The OID of the HarvestResult.
     * @param ahf The ArcHarvestFile DTO.
     */
    void addToHarvestResult(Long harvestResultOid, ArcHarvestFileDTO ahf);
    
    /**
     * Finalise the index by marking it as complete.
     */
    void finaliseIndex(Long harvestResultOid);
    
    /**
     * Notification that AQA is complete.
     */
    void notifyAQAComplete(String aqaId);
    
    
    void addHarvestResources(Long harvestResultOid, Collection<HarvestResourceDTO> harvestResources);
}
