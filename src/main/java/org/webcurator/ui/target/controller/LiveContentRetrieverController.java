package org.webcurator.ui.target.controller;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractCommandController;
import org.webcurator.core.exceptions.WCTRuntimeException;
import org.webcurator.ui.target.command.LiveContentRetrieverCommand;

public class LiveContentRetrieverController extends AbstractCommandController {

	public LiveContentRetrieverController() {
		setCommandClass(LiveContentRetrieverCommand.class);
	}
	
	@Override
	protected ModelAndView handle(HttpServletRequest req, HttpServletResponse res, Object comm, BindException errors) throws Exception {
		LiveContentRetrieverCommand cmd = (LiveContentRetrieverCommand) comm;
		
		File f = downloadTemporaryFile(cmd.getUrl());
		AttachmentView v = new AttachmentView(cmd.getContentFileName(), f, true);
		return new ModelAndView(v);
	}

	private File downloadTemporaryFile(String url) 
	{
	    GetMethod getMethod = new GetMethod(url);
	    HttpClient client = new HttpClient();
	    try 
	    {
	        int result = client.executeMethod(getMethod);
	        if(result != HttpURLConnection.HTTP_OK)
	        {
	        	throw new WCTRuntimeException("Unable to fetch content at "+url+". Status="+result);
	        }
	        return writeTemporaryFile(getMethod.getResponseBody());
	    } 
	    catch (WCTRuntimeException re)
	    {
	    	throw re;
	    }
	    catch (Exception e) 
	    {
			throw new WCTRuntimeException("Unable to fetch content at "+url+".", e);
	    }
	    finally
	    {
	    	getMethod.releaseConnection();
	    }
	}

	private File writeTemporaryFile(byte[] content) throws IOException 
	{
		File outputFile = File.createTempFile("wct", "tmp");
		BufferedOutputStream bufOutStr = new BufferedOutputStream(new FileOutputStream(outputFile));  
		
		try
		{
			bufOutStr.write(content);
			return outputFile;
		}
		finally
		{
			bufOutStr.close();
		}
	}


}
