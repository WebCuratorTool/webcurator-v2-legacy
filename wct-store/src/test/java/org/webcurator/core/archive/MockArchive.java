package org.webcurator.core.archive;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.io.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.webcurator.core.exceptions.DigitalAssetStoreException;
import org.webcurator.domain.model.core.CustomDepositFormCriteriaDTO;
import org.webcurator.domain.model.core.CustomDepositFormResultDTO;

public class MockArchive extends BaseArchive {

	protected static Log log = LogFactory.getLog(MockArchive.class);
	
	public MockArchive() {
	}

	public String submitToArchive(String targetInstanceOID, String SIP,
			Map attributes, List<ArchiveFile> fileList)
			throws DigitalAssetStoreException 
	{
		log.debug("Archiving :"+targetInstanceOID);
		Iterator<ArchiveFile> it = fileList.iterator();
		while(it.hasNext())
		{
			File theFile = it.next().getFile();
			if(theFile != null)
			{
				log.debug("	File: "+theFile.getAbsolutePath());
			}
		}
		return targetInstanceOID;
	}

	/**
	 * This implementation of Archive module does not require a custom form to be filled 
	 * before archiving any harvest. Therefore, this method will return null.
	 */
	public CustomDepositFormResultDTO getCustomDepositFormDetails(CustomDepositFormCriteriaDTO criteria) {
		return null;
	}

}
