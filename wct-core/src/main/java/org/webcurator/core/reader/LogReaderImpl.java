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
package org.webcurator.core.reader;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

import org.webcurator.domain.model.core.LogFilePropertiesDTO;

/** 
 * An implementation of a LogReader that uses the specified LogProvider to access
 * the log and report files.
 * @author nwaight
 */
public class LogReaderImpl implements LogReader {
	/** the log provider to delegate to. */
	private LogProvider logProvider;
	
	/** @see LogReader#listLogFiles(String). */
	public List<String> listLogFiles(String aJob) {
		return logProvider.getLogFileNames(aJob);
	}

	/** @see LogReader#listLogFileAttributes(String). */
	public LogFilePropertiesDTO[] listLogFileAttributes(String aJob) {
		return logProvider.getLogFileAttributes(aJob);
	}

	/** @see LogReader#countLines(String, String). */
	public Integer countLines(String aJob, String aFileName)
	{
		Integer count = 0;

		File logFile = logProvider.getLogFile(aJob, aFileName);
		if (logFile != null) {
	        try{
	            BufferedReader bf = new BufferedReader(new FileReader(logFile), 8192);

	            while (bf.readLine() != null){
	            	count++;
	            }
	        } catch(IOException e){
	            e.printStackTrace();
	            return null;
	        }
		}
		
		return count;
	}
	
	/** @see LogReader#tail(String, String, int). */
	public String[] tail(String aJob, String aFileName, int noOfLines) {
		String[] theTail = {""};

		File logFile = logProvider.getLogFile(aJob, aFileName);
		if (logFile != null) {
			theTail = org.archive.crawler.util.LogReader.tail(logFile.toString(), noOfLines);
		}
		
		return theTail;
	}

	/** @see LogReader#get(String, String, int). */
	public String[] get(String aJob, String aFileName, int startLine, int noOfLines) {
		String[] theLines = {""};

		File logFile = logProvider.getLogFile(aJob, aFileName);
		if (logFile != null) {
			theLines = org.archive.crawler.util.LogReader.get(logFile.toString(), startLine, noOfLines);
		}
		
		return theLines;
	}

	/** @see LogReader#getHopPath(String, String, String, String). */
	public String[] getHopPath(String aJob, String aResultOid, String aFileName, String aUrl) {
		
		File logFile = logProvider.getLogFile(aJob, aFileName);
		List<String> hopPaths = new ArrayList<String>();

		searchForUrl(logFile, aResultOid, aUrl, hopPaths);
		
		String[] theLines = new String[hopPaths.size()];
		int count = hopPaths.size();
		
		ListIterator<String> it = hopPaths.listIterator();
		// iterate in forward direction to get to the end..
		while(it.hasNext()) {
			it.next();
		}
		// iterate in reverse direction to extract the items in reverse order..
		while(it.hasPrevious()) {
			theLines[count-1] = it.previous();
			count--;
		}

		return theLines;
	}

	private void searchForUrl(File theFile, String resultOid, String theUrl, List<String> resultsList) {

        String inLine = null;
        String referrer = null;
        boolean foundLast = false;
    	boolean foundUrl = false;


        BufferedReader inputStream;
		try {
			inputStream = new BufferedReader(new FileReader(theFile.getAbsolutePath()));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
			return;
		}

    	try {
        
    		while ((inLine = inputStream.readLine()) != null) {
            	String [] columns = inLine.split(" ");
            	String dateTime = columns[0];
            	String url = columns[3];
            	if (url.equalsIgnoreCase(theUrl)) {
            		foundUrl = true;
            		String paths = columns[4];
            		String lastPathChar = paths.substring(paths.length()-1);
            		String liveSite = "<a href='" + url + "' target='_blank'><b><u>Live Site</u></b></a>";
            		String browseTool = "<a href='curator/tools/browse/" + resultOid + "/" + url + "' target='_blank'><b><u>Browse Tool</u></b></a>";
            		resultsList.add(browseTool + " " + liveSite + " " + dateTime.substring(0, 10) + " " + dateTime.substring(11, 16) + " " + lastPathChar + " " + url + "\r");
            		if (lastPathChar.equals("-")) {
            			foundLast = true;
            			break;
            		}
            		else {
            			referrer = columns[5];
            			break;
            		}
            	}
            }

        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

       	try {
			inputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
            return;
		}
		
		if (foundUrl && !foundLast) {
			searchForUrl(theFile, resultOid, referrer, resultsList);
		}
	}

	/** @see LogReader#getByRegExpr(String, String, String, String, boolean, int, int). */
	public String[] getByRegExpr(String aJob, String aFileName, String regExpr, String addLines, boolean prependLineNumbers, int skipFirstMatches, int numberOfMatches) {
		String[] lines = {""};
		
		File logFile = logProvider.getLogFile(aJob, aFileName);
		if (logFile != null) {
			lines = org.archive.crawler.util.LogReader.getByRegExpr(logFile.toString(), regExpr, addLines, prependLineNumbers, skipFirstMatches, numberOfMatches);
		}
		
		return lines;
	}

	/** @see LogReader#findFirstLineBeginning(String, String, String). */
	public Integer findFirstLineBeginning(String aJob, String aFileName, String match) {
		Integer line = 0;
		
		try
		{
			File logFile = logProvider.getLogFile(aJob, aFileName);
			if (logFile != null) {
				line = org.archive.crawler.util.LogReader.findFirstLineBeginning(new FileReader(logFile), match);
			}
		}
		catch(IOException e)
		{
            e.printStackTrace();
            return null;
		}
		
		return line;
	}

	/** @see LogReader#findFirstLineContaining(String, String, String). */
	public Integer findFirstLineContaining(String aJob, String aFileName, String match) {
		Integer line = 0;
		
		try
		{
			File logFile = logProvider.getLogFile(aJob, aFileName);
			if (logFile != null) {
				line = org.archive.crawler.util.LogReader.findFirstLineContaining(new FileReader(logFile), match);
			}
		}
		catch(IOException e)
		{
            e.printStackTrace();
            return null;
		}
		
		return line;
	}
	
	/** @see LogReader#findFirstLineAfterTimeStamp(String, String, Long). */
	public Integer findFirstLineAfterTimeStamp(String aJob, String aFileName, Long timestamp) {
		Integer line = 0;
		
		try
		{
			File logFile = logProvider.getLogFile(aJob, aFileName);
			if (logFile != null) {
				line = findFirstLineAfterTimeStamp(new FileReader(logFile), timestamp);
			}
		}
		catch(IOException e)
		{
            e.printStackTrace();
            return null;
		}
		
		return line;
	}
	
	/**
	 * @param logProvider the logProvider to set
	 */
	public void setLogProvider(LogProvider logProvider) {
		this.logProvider = logProvider;
	}

	public File retrieveLogfile(String aJob, String aFilename) {
		File logFile = logProvider.getLogFile(aJob, aFilename);
		return logFile;
	}
	
	/**
     * Return the line number of the first line in the
     * log/file that has a matching or later timestamp.
     *
     * @param reader The reader of the log/file
     * @param prefix The prefix string to match
     * @return The line number (counting from 1, not zero) of the first line
     *         that matches the given regular expression. -1 is returned if no
     *         line matches the regular expression. -1 also is returned if 
     *         errors occur (file not found, io exception etc.)
     */
	private int findFirstLineAfterTimeStamp(InputStreamReader reader, 
                                              Long timestamp)
    {

        try{
        	Pattern logPattern = Pattern.compile("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}.*");
        	Pattern longPattern = Pattern.compile("\\d{14}.*");

            BufferedReader bf = new BufferedReader(reader, 8192);

            String line = null;
            int i = 1;
            while ((line = bf.readLine()) != null) {
                StringBuilder sb = new StringBuilder();
	       		if(logPattern.matcher(line).matches())
	    		{
		            sb.append(line.substring(0, 4));
					sb.append(line.substring(5, 7));
					sb.append(line.substring(8, 10));
					sb.append(line.substring(11, 13));
					sb.append(line.substring(14, 16));
					sb.append(line.substring(17, 19));
	    		}
	       		else if(longPattern.matcher(line).matches())
	       		{
	            	sb.append(line.substring(0, 14));
	       		}
	       		else
	       		{
	       			//Not a timestamp
	       			i++;
	       			continue;
	       		}
	            Long ldatetime = new Long(sb.toString());
                if(ldatetime >= timestamp){
                    // Found a match
                    return i;
                }
                i++;
            }
        } catch(IOException e){
            e.printStackTrace();
        }
        return -1;
    }
}
