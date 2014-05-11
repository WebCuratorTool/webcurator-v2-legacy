package org.webcurator.core.store.arc;

import java.io.File;
import java.io.IOException;

public interface DasFileMover {
	public void moveFile(File source, File destination) throws IOException;
}
