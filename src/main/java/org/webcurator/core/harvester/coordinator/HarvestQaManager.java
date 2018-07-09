package org.webcurator.core.harvester.coordinator;

import org.webcurator.core.exceptions.DigitalAssetStoreException;
import org.webcurator.domain.model.core.ArcHarvestResult;
import org.webcurator.domain.model.core.TargetInstance;

public interface HarvestQaManager {
	void autoPrune(TargetInstance ti) throws DigitalAssetStoreException;
	void initialiseQaRecommentationService(Long harvestResultOid);
	void runQaRecommentationService(TargetInstance ti);
	void triggerAutoQA(ArcHarvestResult ahr);

}
