package org.webcurator.core.store;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class TempFileInputStream extends FileInputStream {
	private static Log log = LogFactory.getLog(TempFileInputStream.class);
	
	private File file = null;
	
	public TempFileInputStream(File f) throws FileNotFoundException {
		super(f);
		this.file = f;
	}
	
	public TempFileInputStream(String fname) throws FileNotFoundException {
		super(fname);
		this.file = new File(fname);
	}
	
	public void close() throws IOException {
		try {
			super.close();
		}
		catch(Exception ex) { 
			log.warn("Couldn't close input stream", ex);
		}
		
		//No point deleting it if it is already gone
		if(file.exists()) 
		{
			if(!this.file.delete()) {
				log.error("Failed to delete temporary file: " + file.getAbsolutePath());
			}
		}
	}
	
}
