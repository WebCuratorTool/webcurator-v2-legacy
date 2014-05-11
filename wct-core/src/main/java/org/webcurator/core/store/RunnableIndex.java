package org.webcurator.core.store;

import java.io.File;

import javax.xml.rpc.ServiceException;

import org.webcurator.domain.model.core.ArcHarvestResultDTO;

public interface RunnableIndex extends Runnable {

	public enum Mode {INDEX, REMOVE};
	String getName();
	RunnableIndex getCopy();
	void setMode(Mode mode);
	void initialise(ArcHarvestResultDTO result, File directory);
	Long begin() throws ServiceException;
	void indexFiles(Long harvestResultOid) throws ServiceException;
	void markComplete(Long harvestResultOid) throws ServiceException;
	void removeIndex(Long harvestResultOid);
	boolean isEnabled();
}
