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
package org.webcurator.core.report;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Utility class for exported files.<br>
 * Defines formats, file export 
 * @author MDubos
 *
 */
public class FileFactory {
	
	/** CSV format */
	public static final String CSV_FORMAT = "CSV";
	
	/** HTML format */
	public static final String HTML_FORMAT = "HTML";
	
	// MIME types
	private static final Map <String,String> MIME_TYPES = new HashMap<String,String>();
	
	// File extensions
	private static final Map <String,String> FILE_EXTENSIONS = new HashMap<String,String>();
	
	
	static {
		
		MIME_TYPES.put(CSV_FORMAT,	"text/plain");
		MIME_TYPES.put(HTML_FORMAT,	"text/html");
		
		FILE_EXTENSIONS.put(CSV_FORMAT,	".csv");
		FILE_EXTENSIONS.put(HTML_FORMAT,".html");
		
	};
	
	

	private static FileFactory instance = null;

	/**
	 * Default constructor
	 *
	 */
	private FileFactory(){
	}
	
	/**
	 * Return an instance of <code>FileFactory</code>
	 * @return The instance
	 */
	public static FileFactory getInstance() {
		if(instance == null) {
			instance = new FileFactory();
		}
		return instance;
	}
	
	
	/**
	 * List of all available formats 
	 * @return A <code>List</code> of <code>String</code> formats
	 */
	@SuppressWarnings("unchecked")
	public static List<String> getFormats(){
		ArrayList formats = new ArrayList<String>();
		formats.add(CSV_FORMAT);
		formats.add(HTML_FORMAT);
		return formats;
	}
	
	/**
	 * Get the MIME type associated to a given format
	 * @param format Format as defined in this class
	 * @return MIME type
	 */
	public static String getMIMEType(String format){
		return MIME_TYPES.get(format);
	}
	
	/**
	 * File extension associated for a given format
	 * @param format Format as defined in this class
	 * @return File extension, e.g: .html
	 */
	public static String getFileExtension(String format){
		return FILE_EXTENSIONS.get(format);
	}
	

	/**
	 * Rendering to the local disk. <br>
	 * See {@link OperationalReport}
	 * @param req Request
	 * @param resp Response
	 * @param content Plain text content to be exported
	 * @param fileName Name of export file
	 * @param format Format of the file, as defined in this class
	 * @throws IOException  If an input or output exception occurred
	 */
	public void getTextFileDownloadRendering(HttpServletRequest req, HttpServletResponse resp, 
			String content, String fileName, String format, String destinationURL) throws IOException {

		resp.setContentType( getMIMEType(format) );
		resp.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + getFileExtension(format) + "\"");
//		if(destinationURL != null){
//			resp.setHeader("Refresh", "1;URL=" + destinationURL);
//		}
		
		
		ServletOutputStream out = resp.getOutputStream();
		out.print(content);
		out.flush();
	}
	

	
}
