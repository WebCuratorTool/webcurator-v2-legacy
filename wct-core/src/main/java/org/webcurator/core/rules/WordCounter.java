package org.webcurator.core.rules;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Counts the number of occurrences of a word within a file
 * @author twoods
 *
 */
public class WordCounter {
	
	/**
	 * the logger
	 */
	private static final Log LOG = LogFactory.getLog(WordCounter.class);
    
    /**
     * Count the specified word with the supplied file
     * @param word the word to count
     * @param file the <code>File</code> to search
     * @return the number of occurrences of the word within the file
     */
	public static final Integer count(String word, File file) {
		int searchLength=word.length();
		int searchCount = 0;
		try {
			BufferedReader bout = new BufferedReader (new FileReader (file));
			String line = null;
			int lcnt = 0;

			while ((line = bout.readLine()) != null) {
				// log the line in the file
				LOG.debug(line);
				lcnt++;
				for(int searchIndex=0; searchIndex < line.length(); ) {
					int index = line.indexOf(word, searchIndex);
					if(index!=-1) {
						searchCount++;
						searchIndex+=index+searchLength;
					} else {
						break;
					}
				}
			}
		} catch(Exception e) {
			LOG.error(e);
		}
		return searchCount;
	}
	
	public static final ArrayList<String> getColumn(int columnNumber, String columnDelimiter, File file) {
		ArrayList<String> lines = new ArrayList();
		
		try {
			BufferedReader bout = new BufferedReader (new FileReader (file));
			String line = null;
			int lcnt = 0;

			while ((line = bout.readLine()) != null) {
				System.out.println("Line: " + line);
				// log the line in the file
				LOG.debug(line);
				// replace all multiple spaces with single space characters
				while (line.contains("  ")) {
					line = line.replaceAll("  ", " ");
				}
				String[] logRecord = line.split(columnDelimiter);
				// get the column
				if (logRecord[columnNumber] != null && !logRecord[columnNumber].equals("")) lines.add(logRecord[columnNumber]);
				
				lcnt++;
			}
		} catch(Exception e) {
			LOG.error(e);
		}

		return lines;
	}
	
}
