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
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.webcurator.core.report.parameter.Parameter;

/**
 * A <code>Report</code> with advanced functionalities
 * @author MDubos
 *
 */
public class OperationalReport extends Report {
	
	private Log log = LogFactory.getLog(OperationalReport.class);
	
	/** Elements separator used in the CSV generation */
	protected static final String COMMA_SEPARATOR = ",";
	
	/** New line used in the CSV generation */
	protected static final String NEW_LINE = "\n";
	
	
	/**
	 * Default constructor
	 */
	public OperationalReport(){
	}

	
	/**
	 * Convenient constructor<br>
	 * <br>
	 * Permits to build a new <code>OperationalReport</code> from
	 * the attributes of a given <code>Report</code> 
	 * @param name Name od te Report
	 * @param info Information of the Report
	 * @param parameters List of {@link Parameter} of the Report
	 * @param reportGenerator {@link ReportGenerator} of the Report
	 */
	public OperationalReport(String name, String info, List<Parameter> parameters, ReportGenerator reportGenerator){
		setName(name);
		setInfo(info);
		setParameters(parameters);
		setReportGenerator(reportGenerator);
	}
	
			
	/**
	 * Output rendering according to a format.
	 * @param format Format as defined in {@link FileFactory}
	 * @return Plain text content of the output. Returns an 
	 * empty <code>String</code> if the format is unknown
	 * @throws IOException I/O error
	 */
	public String getRendering(String format) throws IOException {
		
		long start = System.currentTimeMillis();
		String result;
		if(format.equals(FileFactory.CSV_FORMAT)){
			result = getCSVRendering();
		}
		else if(format.equals(FileFactory.HTML_FORMAT)){
			result = getHTMLRendering();
		}
		else {
			log.debug("Unknown format: " + format);
			result = "";
		}
		log.debug("getRendering took " + (System.currentTimeMillis()-start) + "ms");
		return result;
	}
	
	
	/**
	 * HTML rendering.<br>
	 * This method is equivalent to:<br>
	 * <code>getRendering(FileFactory.HTML_FORMAT)</code> 
	 */
	public String getHTMLRendering() throws IOException {
				
		// Generate Report Data
		List<ResultSet> data = getReportGenerator().generateData(this);
		
		StringBuffer sb = new StringBuffer();
		
		sb.append("<b>" + this.getName() + "</b><br><br>");
		sb.append(data.size() + " results:");
				
		if(data == null || data.size() == 0){
			// sb.append("no data");
		} else {
		
			sb.append("<table border=\"1\" cellspacing=\"0\">\n");
			
			// Title Row
			sb.append("<b><tr>");
			for(String title : data.get(0).getColumnHTMLNames()){
				sb.append("<th>" + title + "</th>");
			}
			sb.append("</tr></b>");
			
			// Data Rows
			for(ResultSet rs : data){
				sb.append("<tr>");
				String[] fields = rs.getDisplayableFields(); 
				for(int i=0; i<fields.length; i++){
					sb.append("<td>");
					sb.append(fields[i] == null || fields[i].equals("") ? "&nbsp;" : fields[i]);
					sb.append("</td>");
				}
				sb.append("</tr>\n");
			}
			
			sb.append("</table>\n");
			
		}
		return sb.toString();
	}
	

	
	/**
	 * CSV rendering.<br>
	 * This method is equivalent to:<br>
	 * <code>getRendering(FileFactory.CSV_FORMAT)</code> 
	 */
	private String getCSVRendering() throws IOException {
		StringBuffer sb = new StringBuffer();
		
		// Generate Report Data
		List<ResultSet> data = getReportGenerator().generateData(this);
		
		if(data == null || data.size() == 0){
			//sb.append("no data");
		} else {
			
			// Title Row
			boolean first = true;
			for(String title : data.get(0).getColumnNames()){
				if(first){
					sb.append(appropriateQuoting(title));
					first = false;
				} else{
					sb.append(COMMA_SEPARATOR + appropriateQuoting(title));
				}
			}
			sb.append(NEW_LINE);
			
			// Data Rows
			for(ResultSet rs : data){
				String[] fields = rs.getDisplayableFields();
				first = true;
				for(int i=0; i<fields.length; i++){
					if(first){
						sb.append(appropriateQuoting(fields[i]));
						first = false;
					} else {
						sb.append(COMMA_SEPARATOR + appropriateQuoting(fields[i]));
					}
				}
				sb.append(NEW_LINE);
			}
			
		}
		return sb.toString();
	}
	
	private String appropriateQuoting(String input)
	{
		String output;
		
		if(input.contains(COMMA_SEPARATOR))
		{
			output = "\""+input+"\"";
		}
		else
		{
			output = input;
		}
		
		return output;
	}
	
	/**
	 * Rendering to the local disk. <br>
	 * See {@link OperationalReport}
	 * @param req Request
	 * @param resp Response
	 * @param fileName Name of export file
	 * @param format Format of the file, as defined in this class
	 * @throws IOException  If an input or output exception occurred
	 */
	public void getDownloadRendering(HttpServletRequest req, HttpServletResponse resp, String fileName, String format) throws IOException {
		getDownloadRendering(req, resp, fileName, format, null );		
	}

	public void getDownloadRendering(HttpServletRequest req, HttpServletResponse resp, String fileName, String format, String destinationURL) throws IOException {
		String content = getRendering(format);
		FileFactory.getInstance().getTextFileDownloadRendering(req, resp, content, fileName, format, destinationURL);		
	}
	
}
