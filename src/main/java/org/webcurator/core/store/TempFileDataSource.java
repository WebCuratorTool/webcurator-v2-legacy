package org.webcurator.core.store;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.activation.FileDataSource;

public class TempFileDataSource extends FileDataSource {
	public TempFileDataSource(File file) {
		super(file);
	}
	
	public InputStream getInputStream() throws IOException {
		return new TempFileInputStream(this.getFile());
	}
}
