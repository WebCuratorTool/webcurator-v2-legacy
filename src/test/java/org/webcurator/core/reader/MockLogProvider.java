package org.webcurator.core.reader;

import java.io.*;
import java.util.*;

import org.webcurator.core.harvester.agent.HarvesterStatusUtil;
import org.webcurator.domain.model.core.LogFilePropertiesDTO;

public class MockLogProvider implements LogProvider{

	private class LogFileFilter implements FileFilter {
	    public boolean accept(File f) {
	        if (f.isDirectory()) return false;
	        String name = f.getName().toLowerCase();
	        return name.endsWith("log") || name.endsWith("txt") || name.endsWith("xml");
	    }//end accept
	}//end class LogFileFilter
	
	private String basePath = "";
	private String pageImagePrefix = "PageImage";
	private String aqaReportPrefix = "aqa-report";
	
	public MockLogProvider(String basePath)
	{
		if(basePath.endsWith("/"))
		{
			basePath = basePath.substring(basePath.length()-1);
		}
		this.basePath = basePath;
	}
	
	public File getLogFile(String aJob, String aFileName)
	{
		return new File(basePath+"/"+aFileName);
	}
	
	public File getAQAFile(String aJob, String aFileName)
	{
		return new File(basePath+"/"+aFileName);
	}

	public List<String> getLogFileNames(String aJob)
	{
		List<String> filenames = new ArrayList<String>();
		File dir = new File(basePath);
		if(dir.isDirectory())
		{
			File[] files = dir.listFiles(new LogFileFilter());
			for(int i = 0; i < files.length; i++)
			{
				filenames.add(files[i].getName());
			}
		}
		return filenames;
	}

	public LogFilePropertiesDTO[] getLogFileAttributes(String aJob)
	{
		LogFilePropertiesDTO[] arProps = null;		
		File dir = new File(basePath);
		if(dir.isDirectory())
		{
			File[] files = dir.listFiles(new LogFileFilter());
			arProps = new LogFilePropertiesDTO[files.length];
			for(int i = 0; i < files.length; i++)
			{
				File f = files[i];
				LogFilePropertiesDTO props = new LogFilePropertiesDTO();
				props.setLastModifiedDate(new Date(f.lastModified()));
				props.setLengthString(HarvesterStatusUtil.formatData(f.length()));
				props.setName(f.getName());
				props.setPath(f.getAbsolutePath());
				
        		//Special case for AQA reports and images
        		if(f.getName().startsWith(pageImagePrefix))
        		{
        			props.setViewer("content-viewer.html");
        		}
        		else if(f.getName().startsWith(aqaReportPrefix))
        		{
        			props.setViewer("aqa-viewer.html");
        		}
        		
				arProps[i] = props;
			}
		}

		return arProps;
	}
}
