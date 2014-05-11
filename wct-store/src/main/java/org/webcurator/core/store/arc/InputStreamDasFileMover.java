package org.webcurator.core.store.arc;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class InputStreamDasFileMover implements DasFileMover {
	
	/** The logger. */
	private static Log log = LogFactory.getLog(InputStreamDasFileMover.class);	

	public void moveFile(File source, File destination) throws IOException {
		boolean success = false;
		int BUFFER_SIZE = 64000;
		byte[] buffer = new byte[BUFFER_SIZE];
		int bytesRead = 0;

		InputStream is = null;
		OutputStream os = null;

		try {
			is = new BufferedInputStream(new FileInputStream(source));
			os = new BufferedOutputStream(new FileOutputStream(destination));

			while ((bytesRead = is.read(buffer)) > 0) {
				os.write(buffer, 0, bytesRead);
			}
			success = true;
		} 
		finally {
			is.close();
			os.close();
			
			if(success) { 
				if(!source.delete()) {
					log.warn("Could not delete " + source.getAbsolutePath());
				}
			}
		}
		
		
	}

}
