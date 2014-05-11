package org.webcurator.core.store.arc;

import java.io.File;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.webcurator.core.store.MockIndexer;

public class MockDasFileMover implements DasFileMover {

	protected static Log log = LogFactory.getLog(MockDasFileMover.class);

	public MockDasFileMover() {
	}

	public void moveFile(File source, File destination) throws IOException 
	{
		log.debug("Moving source file: "+source.getAbsolutePath()+" to destination "+destination.getAbsolutePath());
	}

}
