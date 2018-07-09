package org.webcurator.core.store;

import it.unipi.di.util.ExternalSort;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javax.xml.rpc.ServiceException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.webcurator.domain.model.core.ArcHarvestResultDTO;

public class CrawlLogIndexer extends IndexerBase {

	//Static variables
	private static Log log = LogFactory.getLog(CrawlLogIndexer.class);

	//Passed in variables
	private ArcHarvestResultDTO result;
	private File directory;
	
	//Spring initialised variables (to be copied in copy constructor)
	private String crawlLogFileName;
	private String strippedLogFileName;
	private String sortedLogFileName;
	private String logsSubFolder;

	private boolean enabled = false;
	
	public CrawlLogIndexer()
	{
	}
	
	protected CrawlLogIndexer(CrawlLogIndexer original)
	{
		super(original);
		crawlLogFileName = original.crawlLogFileName;
		strippedLogFileName = original.strippedLogFileName;
		sortedLogFileName = original.sortedLogFileName;
		logsSubFolder = original.logsSubFolder;
		enabled = original.enabled;
	}
	
	@Override
	public RunnableIndex getCopy() {
		return new CrawlLogIndexer(this);
	}

	@Override
	protected ArcHarvestResultDTO getResult() {
		return result;
	}

	@Override
	public Long begin() throws ServiceException {
		return getResult().getOid();
	}

	@Override
	public String getName() {
		return getClass().getCanonicalName();
	}

	@Override
	public void indexFiles(Long harvestResultOid) throws ServiceException {
		
		// sort the crawl.log file to create a sortedcrawl.log file in the same
		// directory.
        log.info("Generating " + sortedLogFileName + " file for " + getResult().getTargetInstanceOid());

		// create path to log files folder from input directory..
		String logPath = directory.getAbsolutePath().substring(0, directory.getAbsolutePath().length()-1);
        logPath = logPath + logsSubFolder + directory.separator;

        // write new 'stripped' crawl.log, replacing multiple spaces with a single space in each record..
        try {
            
            BufferedReader inputStream = new BufferedReader(new FileReader(logPath + crawlLogFileName));
            PrintWriter outputStream   = new PrintWriter(new FileWriter(logPath + strippedLogFileName));

            String inLine = null;

            while ((inLine = inputStream.readLine()) != null) {
                outputStream.println(inLine.replaceAll(" +", " "));
            }

            outputStream.close();
            inputStream.close();

        } catch (IOException e) {

        	log.error("Could not create " + strippedLogFileName + " file in directory: " + logPath );
        	return;
        }
        
		// sort the 'stripped' crawl.log file to create a 'sorted' crawl.log file...
        ExternalSort sort = new ExternalSort();
        try {
			sort.setInFile(logPath + strippedLogFileName);
		} catch (FileNotFoundException e1) {
        	log.error("Could not find " + strippedLogFileName + " file in directory: " + logPath );
        	return;
		}
        try {
			sort.setOutFile(logPath + sortedLogFileName);
		} catch (FileNotFoundException e1) {
        	log.error("Could not find directory: " + logPath );
        	return;
		}
		// sort on fourth column (url) then first column (timestamp)..
		int[] cols = {3,0};
		sort.setColumns(cols);
		sort.setSeparator(' ');  // space 
        
		try {
			sort.run();
		} catch (IOException e1) {
        	log.error("Could not sort " + crawlLogFileName + " file in directory: " + logPath );
        	return;
		}
		
		log.info("Completed sort of crawl.log for job " + getResult().getTargetInstanceOid());
	}
	
	@Override
	public void removeIndex(Long harvestResultOid)
	{
		return;
	}
	
	@Override
	public void initialise(ArcHarvestResultDTO result, File directory) {
		this.result = result;
		this.directory = directory;
	}

	public void setCrawlLogFileName(String crawlLogFileName) {
		this.crawlLogFileName = crawlLogFileName;
	}

	public String getCrawlLogFileName() {
		return crawlLogFileName;
	}

	public void setStrippedLogFileName(String strippedLogFileName) {
		this.strippedLogFileName = strippedLogFileName;
	}

	public String getStrippedLogFileName() {
		return strippedLogFileName;
	}

	public void setSortedLogFileName(String sortedLogFileName) {
		this.sortedLogFileName = sortedLogFileName;
	}

	public String getSortedLogFileName() {
		return sortedLogFileName;
	}

	public void setLogsSubFolder(String logsSubFolder) {
		this.logsSubFolder = logsSubFolder;
	}

	public String getLogsSubFolder() {
		return logsSubFolder;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}

}
