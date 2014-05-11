/*
 *  Copyright 2006 The National Library of New Zealand
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.webcurator.ui.target.controller;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.view.AbstractView;

/**
 * A Spring View implementation that displays a raw content file.
 * @author beaumontb
 *
 */
public class ContentView extends AbstractView {
	
	/** the logger. */
    private static Log log = LogFactory.getLog(ContentView.class);

    /** The file on the server to be viewed. */
	private File file = null;
    /** The filename to be presented to the user. */
	private String filename = "";
	/** True to delete the file after being sent. */
	private boolean deleteAfterSend = false;
	
	/** The size of the buffer to use for copying the file to the output stream. */
	private static final int BUFFER_SIZE = 1024;
	
	/**
	 * Create an attachment view.
	 * @param file The File on the server to be sent.
	 * @param deleteAfterSend true to delete the file from the server after sending the view.
	 */
	public ContentView(File file, String filename, boolean deleteAfterSend) {
		this.file = file;
		this.filename = filename;
		this.deleteAfterSend = deleteAfterSend;
	}
	
	@Override
	protected void renderMergedOutputModel(Map model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		OutputStream os = null;
		BufferedInputStream bis = null;
		
		try {
			os = response.getOutputStream();
			
			if(file != null && file.exists())
			{
				bis = new BufferedInputStream(new FileInputStream(file));
			}
			else
			{
				String message = "Unable to display file: "+filename+". The file is not available.";
				bis = new BufferedInputStream(new ByteArrayInputStream(message.getBytes()));
			}
				
			byte[] buffer = new byte[BUFFER_SIZE];
			
			int bytesRead = 0;
			while((bytesRead = bis.read(buffer, 0, BUFFER_SIZE)) > 0) {
				os.write(buffer, 0, bytesRead);
			}
		}
		finally {
			bis.close();
		}
		
		os.flush();
		
		if(deleteAfterSend) {
			if(!file.delete()) {
				log.error("Could not delete file " + file.getAbsolutePath());
			}
		}
	}

}
