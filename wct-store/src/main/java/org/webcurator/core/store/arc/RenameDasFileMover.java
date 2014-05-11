package org.webcurator.core.store.arc;

import java.io.File;
import java.io.IOException;

public class RenameDasFileMover implements DasFileMover {

	public void moveFile(File source, File destination) throws IOException {
		if(!source.renameTo(destination)) {
			throw new IOException("Couldn't rename file");
		}
	}

}
