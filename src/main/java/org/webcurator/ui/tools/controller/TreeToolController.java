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
package org.webcurator.ui.tools.controller;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.validation.BindException;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.multipart.support.ByteArrayMultipartFileEditor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractCommandController;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.webcurator.core.harvester.coordinator.HarvestCoordinator;
import org.webcurator.core.harvester.coordinator.HarvestLogManager;
import org.webcurator.core.scheduler.TargetInstanceManager;
import org.webcurator.core.store.tools.HarvestResourceNodeTreeBuilder;
import org.webcurator.core.store.tools.QualityReviewFacade;
import org.webcurator.core.store.tools.WCTNode;
import org.webcurator.core.store.tools.WCTNodeTree;
import org.webcurator.core.util.XMLConverter;
import org.webcurator.domain.model.core.HarvestResource;
import org.webcurator.domain.model.core.HarvestResourceDTO;
import org.webcurator.domain.model.core.HarvestResult;
import org.webcurator.domain.model.core.TargetInstance;
import org.webcurator.ui.common.Constants;
import org.webcurator.ui.tools.command.TreeToolCommand;
import org.xml.sax.SAXException;

/**
 * The TreeToolController is responsible for rendering the 
 * harvest web site as a tree structure.
 * @author bbeaumont 
 */
public class TreeToolController extends AbstractCommandController {
	
	public class AQAElement
	{
		private String url = "";
		private String contentFile = "";
		private String contentType = "";
		private long contentLength = 0L;
		
		
		private AQAElement(String url, String contentFile, String contentType, long contentLength)
		{
			this.url = url;
			this.contentFile = contentFile;
			this.contentType = contentType;
			this.contentLength = contentLength;
		}
		
		public String getUrl() {
			return url;
		}
		
		public String getContentFile() {
			return contentFile;
		}

		public String getContentType() {
			return contentType;
		}

		public long getContentLength() {
			return contentLength;
		}
	}

	private static Log log = LogFactory.getLog(TreeToolController.class);
	
	private QualityReviewFacade qualityReviewFacade = null;
	
	private String successView = "TreeTool";
	
	private HarvestResourceUrlMapper harvestResourceUrlMapper;
	private boolean enableAccessTool = false;
	private String uploadedFilesDir;
	
    /** Automated QA Url */
    private String autoQAUrl = "";
    
    /** the name of the content directory. */
    public static final String DIR_CONTENT = "content"; 


	HarvestLogManager harvestLogManager;
	TargetInstanceManager targetInstanceManager;

	public TreeToolController() {
		super.setCommandClass(TreeToolCommand.class);
	}
	
	@Override
    public void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws Exception {
		// Determine the necessary formats.
        NumberFormat nf = NumberFormat.getInstance(request.getLocale());
        
        // Register the binders.
        binder.registerCustomEditor(Long.class, new CustomNumberEditor(Long.class, nf, true));
        binder.registerCustomEditor(Boolean.class, "propagateDelete", new CustomBooleanEditor(true));
        
        // to actually be able to convert Multipart instance to byte[]
        // we have to register a custom editor (in this case the
        // ByteArrayMultipartEditor
        binder.registerCustomEditor(byte[].class, new ByteArrayMultipartFileEditor());
        // now Spring knows how to handle multipart object and convert them
    }	
	
	@SuppressWarnings("unchecked")
	@Override
	protected ModelAndView handle(HttpServletRequest req,
			HttpServletResponse res, Object comm, BindException errors)
			throws Exception {
		
		TreeToolCommand command = (TreeToolCommand) comm;
		TargetInstance ti = (TargetInstance) req.getSession().getAttribute("sessionTargetInstance");
		
		// If the tree is not loaded then load the tree into session
		// data and then load any AQA 'importable items' into session data.
		if(command.getLoadTree() != null) {
			
			// load the tree..
			log.info("Generating Tree");
			HarvestResourceNodeTreeBuilder treeBuilder = qualityReviewFacade.getHarvestResultTree(command.getLoadTree());
			WCTNodeTree tree = treeBuilder.getTree();
			req.getSession().setAttribute("tree", tree);
			command.setHrOid(command.getLoadTree());
			log.info("Tree complete");
			
			if(autoQAUrl != null && autoQAUrl.length() > 0) {

				List<AQAElement> candidateElements = new ArrayList<AQAElement>();

		    	// load AQA 'importable items' (if any)..
				File xmlFile;
				try {
					xmlFile = harvestLogManager.getLogfile(ti, command.getLogFileName());
				}
				catch (Exception e) {
					xmlFile = null;
					log.info("Missing AQA report file: " + command.getLogFileName());
				}
				
				if (xmlFile != null) {

					Document aqaResult = readXMLDocument(xmlFile);
					NodeList parentElementsNode = aqaResult.getElementsByTagName("missingElements");
					if (parentElementsNode.getLength() > 0)
					{
						NodeList elementNodes = ((Element)parentElementsNode.item(0)).getElementsByTagName("element");
						for(int i = 0; i < elementNodes.getLength(); i++)
						{
							Element element = (Element)elementNodes.item(i);
							if (element.getAttribute("statuscode").equals("200")) {
								candidateElements.add(new AQAElement(element.getAttribute("url"),
										                             element.getAttribute("contentfile"),
										                             element.getAttribute("contentType"),
										                             Long.parseLong(element.getAttribute("contentLength"))));
							}
						}
					}
					req.getSession().setAttribute("aqaImports", candidateElements);
				}
			}
		}

		// Load the tree items from the session.
		WCTNodeTree tree = (WCTNodeTree) req.getSession().getAttribute("tree");		
		List<AQAElement> imports = (List<AQAElement>) req.getSession().getAttribute("aqaImports");	
		
		// Go back to the page if there were validation errors.
		if(errors.hasErrors()) {
			ModelAndView mav = new ModelAndView(getSuccessView());
			mav.addObject( "tree", tree);
			mav.addObject( "command", command);
			mav.addObject( "aqaImports", imports);
			mav.addObject( Constants.GBL_ERRORS, errors);
			if(autoQAUrl != null && autoQAUrl.length() > 0) {
				mav.addObject("showAQAOption", 1);
			} else {
				mav.addObject("showAQAOption", 0);
			}

			return mav;			
		}
		
		// Handle any tree actions.
		else if( command.isAction(TreeToolCommand.ACTION_TREE_ACTION)){
			
			// If we are toggling things open/closed..
			if( command.getToggleId() != null) {
				tree.toggle(command.getToggleId());
			}
			
			// if we're pruning..
			if( command.getMarkForDelete() != null) {
				tree.markForDelete(command.getMarkForDelete(), command.getPropagateDelete());
			}
			
			// if we're importing single items..
			if( command.getTargetURL() != null) {

				HarvestResourceNodeTreeBuilder tb = new HarvestResourceNodeTreeBuilder();
				try {
					URL parentUrl = tb.getParent(new URL(command.getTargetURL()));
				}
				catch (MalformedURLException me) {
					errors.reject("tools.errors.invalidtargeturl");
					ModelAndView mav = new ModelAndView(getSuccessView());
					mav.addObject( "tree", tree);
					mav.addObject( "command", command);
					mav.addObject( "aqaImports", imports);
					mav.addObject( Constants.GBL_ERRORS, errors);
					if(autoQAUrl != null && autoQAUrl.length() > 0) {
						mav.addObject("showAQAOption", 1);
					} else {
						mav.addObject("showAQAOption", 0);
					}
					return mav;			
				}

				
				if( command.getImportType().equals(TreeToolCommand.IMPORT_FILE)) {
					
					// We're importing a file from the user's file system, uploaded
					// via their browser. We need to store the file so it can be added
					// to the archive when the SAVE command is eventually issued.
					// We also need to add a node to the tree-view in such a way that the
					// user can distinguish imported files from pruned files and 
					// original files.

					MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) req;
					CommonsMultipartFile uploadedFile = (CommonsMultipartFile) multipartRequest.getFile("sourceFile");
					
					// save uploaded file as tempFileName in configured uploadedFilesDir
					String tempFileName = UUID.randomUUID().toString();
					
					File xfrFile = new File(uploadedFilesDir+tempFileName);
			        
					StringBuffer buf = new StringBuffer();
			        buf.append("HTTP/1.1 200 OK\n");
			        buf.append("Content-Type: ");
			        buf.append(uploadedFile.getContentType()+"\n");
			        buf.append("Content-Length: ");
			        buf.append(uploadedFile.getSize()+"\n");
			        buf.append("Connection: close\n\n");
			        
					FileOutputStream fos = new FileOutputStream(xfrFile);
					fos.write(buf.toString().getBytes());
			        fos.write(uploadedFile.getBytes()); 
					
					fos.close();
					
					tree.insert(command.getTargetURL(),xfrFile.length(), tempFileName, uploadedFile.getContentType());
				}
				if( command.getImportType().equals(TreeToolCommand.IMPORT_URL)) {
					
					// We're importing a file via a URL that the user has specified, we
					// need to use the URL to do a HTTP GET and store the resultant
					// down-loaded file so it can be added to the archive when the 
					// SAVE command is eventually issued.
					// We also need to add a node to the tree-view in such a way that the
					// user can distinguish imported files from pruned files and 
					// original files.
					
					// save uploaded file as tempFileName in configured uploadedFilesDir
					String tempFileName = UUID.randomUUID().toString();
					
					File xfrFile = new File(uploadedFilesDir+tempFileName);
					FileOutputStream fos = new FileOutputStream(xfrFile);
					
					//String contentType = null;
					String outStrings[] = new String[1];

					
					try {
						fos.write(fetchHTTPResponse(command.getSourceURL(), outStrings));
					}
					catch (HTTPGetException ge) {
						errors.reject("tools.errors.httpgeterror", new Object[] {command.getSourceURL(), ge.getMessage()} ,"");
						ModelAndView mav = new ModelAndView(getSuccessView());
						mav.addObject( "tree", tree);
						mav.addObject( "aqaImports", imports);
						mav.addObject( "command", command);
						mav.addObject( Constants.GBL_ERRORS, errors);
						if(autoQAUrl != null && autoQAUrl.length() > 0) {
							mav.addObject("showAQAOption", 1);
						} else {
							mav.addObject("showAQAOption", 0);
						}
						return mav;			
					}
					finally {
						fos.close();
					}
					
					tree.insert(command.getTargetURL(),xfrFile.length(), tempFileName, outStrings[0]);
				}
			}

			ModelAndView mav = new ModelAndView(getSuccessView());
			mav.addObject( "tree", tree);
			mav.addObject( "aqaImports", imports);
			mav.addObject( "command", command);
			if(autoQAUrl != null && autoQAUrl.length() > 0) {
				mav.addObject("showAQAOption", 1);
			} else {
				mav.addObject("showAQAOption", 0);
			}
			return mav;
		}
		
		// Handle browse action.
        else if(command.isAction(TreeToolCommand.ACTION_VIEW)) {
            HarvestResource resource = tree.getNodeCache().get(command.getSelectedRow()).getSubject();
            Long resultOid = resource.getResult().getOid();
            String url = resource.getName();

            if (enableAccessTool && harvestResourceUrlMapper != null)
            {
            	return new ModelAndView("redirect:" + harvestResourceUrlMapper.generateUrl(resource.getResult(), resource.buildDTO()));
            }
            else
            {
            	return new ModelAndView("redirect:/curator/tools/browse/" + resultOid + "/" +url);
            }
            
        }
		
		// Handle show hop path action.
        else if(command.isAction(TreeToolCommand.ACTION_SHOW_HOP_PATH)) {

        	TargetInstance tinst = (TargetInstance) req.getSession().getAttribute("sessionTargetInstance");
        	Long instanceOid = tinst.getOid();
        	String url = command.getSelectedUrl();
            
        	return new ModelAndView("redirect:/curator/target/show-hop-path.html?targetInstanceOid=" + instanceOid + "&logFileName=sortedcrawl.log&url=" +url);
        }
		
		// handle import of one or more AQA items..
        else if(command.isAction(TreeToolCommand.IMPORT_AQA_FILE)) {
			
        	// iterate over the selected (checked) items..
			if (command.getAqaImports() != null) {
				
	        	List<AQAElement> removeElements = new ArrayList<AQAElement>();
	        	
				String[] aqaImportUrls = command.getAqaImports();
				for(int i=0;i<aqaImportUrls.length;i++) {
					
					String aqaUrl = aqaImportUrls[i];	
					
					for (Iterator iter = imports.iterator(); iter.hasNext();) {
						AQAElement elem = (AQAElement) iter.next();
						if (elem.getUrl().equals(aqaUrl)) {
						
							// We're importing a missing file, captured by the AQA process.
							// We need to store the file with HTTP header info so it can be added
							// to the archive when the SAVE command is eventually issued.
							// We also need to add a node to the tree-view in such a way that the
							// user can distinguish imported files from pruned files and 
							// original files.

							File aqaFile = null;
							try {
								aqaFile = harvestLogManager.getLogfile(ti, elem.getContentFile());

								// save imported file using a random tempFileName in configured uploadedFilesDir
								String tempFileName = UUID.randomUUID().toString();
								
								File xfrFile = new File(uploadedFilesDir+tempFileName);
						        
								StringBuffer buf = new StringBuffer();
						        buf.append("HTTP/1.1 200 OK\n");
						        buf.append("Content-Type: ");
						        buf.append(elem.getContentType()+"\n");
						        buf.append("Content-Length: ");
						        buf.append(elem.getContentLength()+"\n");
						        buf.append("Connection: close\n\n");
						        
								FileOutputStream fos = new FileOutputStream(xfrFile);
								fos.write(buf.toString().getBytes());

								FileInputStream fin = new FileInputStream(aqaFile);
								byte[] bytes = new byte[8192];
								int len = 0;
								while ((len = fin.read(bytes)) >= 0)
								{
									fos.write(bytes, 0, len);
								}

								fos.close();
								fin.close();
								
								tree.insert(aqaUrl, xfrFile.length(), tempFileName, elem.getContentType());
								removeElements.add(elem);
							}
							catch (Exception e) {
								log.info("Missing AQA import file: " + elem.getContentFile());
							}
						}
					} 
				}; //end for
				for (Iterator remit = removeElements.iterator(); remit.hasNext();) {
					AQAElement rem = (AQAElement) remit.next();
					imports.remove(rem);
				}
			};

			ModelAndView mav = new ModelAndView(getSuccessView());
			mav.addObject( "tree", tree);
			mav.addObject( "aqaImports", imports);
			mav.addObject( "command", command);
			if(autoQAUrl != null && autoQAUrl.length() > 0) {
				mav.addObject("showAQAOption", 1);
			} else {
				mav.addObject("showAQAOption", 0);
			}
			return mav;
			
		}

		// Handle the save action.
		else if(command.isAction(TreeToolCommand.ACTION_SAVE)) {
			List<String> uris = new LinkedList<String>();
			for(WCTNode node: tree.getPrunedNodes()) {
				if(node.getSubject() != null) {
					uris.add( node.getSubject().getName());
				}
			}
			List<HarvestResourceDTO> hrs = new LinkedList<HarvestResourceDTO>();
			for(HarvestResourceDTO dto: tree.getImportedNodes()) {
				hrs.add(dto);
			}
			
			HarvestResult result = qualityReviewFacade.copyAndPrune(command.getHrOid(), uris, hrs, command.getProvenanceNote(), tree.getModificationNotes());

			// Make sure that the tree is removed from memory.			
			removeTree(req);
			removeAQAImports(req);
			
			return new ModelAndView("redirect:/" + Constants.CNTRL_TI + "?targetInstanceId=" + result.getTargetInstance().getOid()+"&cmd=edit&init_tab=RESULTS");
		}
		
		else if(command.isAction(TreeToolCommand.ACTION_CANCEL)) {
			
			// Make sure that objects removed from memory.
			removeTree(req);
			removeAQAImports(req);
			
			TargetInstance tinst = (TargetInstance) req.getSession().getAttribute("sessionTargetInstance");

			return new ModelAndView("redirect:/curator/target/quality-review-toc.html?targetInstanceOid=" + tinst.getOid()  + "&harvestResultId=" + command.getHrOid());
			
		}

		// Handle an unknown action?
		else {
			ModelAndView mav = new ModelAndView(getSuccessView());
			mav.addObject( "tree", tree);
			mav.addObject( "command", command);
			mav.addObject( "aqaImports", imports);
			mav.addObject( Constants.GBL_ERRORS, errors);
			if(autoQAUrl != null && autoQAUrl.length() > 0) {
				mav.addObject("showAQAOption", 1);
			} else {
				mav.addObject("showAQAOption", 0);
			}
			return mav;	
		}
	}

	public class HTTPGetException extends RuntimeException
	{
		private static final long serialVersionUID = -82352605532244805L;

		private String message = "";
		
		protected HTTPGetException(String message)
		{
			super();
			this.message = message;
		}
		
		protected HTTPGetException(String message, Exception cause)
		{
			super(cause);
			this.message = message;
		}
		
		@Override
		public String getMessage()
		{
			return message;
		}
	}
	
	private byte[] fetchHTTPResponse(String url, String[] outStrings)
	{
	    GetMethod getMethod = new GetMethod(url);
	    HttpClient client = new HttpClient();
	    try 
	    {
	        int result = client.executeMethod(getMethod);
	        if(result != HttpURLConnection.HTTP_OK)
	        {
	        	throw new HTTPGetException("HTTP GET Status="+result);
	        }
	        Header[] headers = getMethod.getResponseHeaders();
	        StringBuffer buf = new StringBuffer();
	        buf.append("HTTP/1.1 200 OK\n");
	        for (int i=0; i<headers.length; i++) {
	        	buf.append(headers[i].getName()+": ");
	        	buf.append(headers[i].getValue()+"\n");
	        	if (headers[i].getName().equalsIgnoreCase("content-type")) {outStrings[0]=headers[i].getValue();}
	        }
	        buf.append("\n");
	        
	        ByteArrayOutputStream os = new ByteArrayOutputStream(); 
	        os.write(buf.toString().getBytes()); 
	        os.write(getMethod.getResponseBody()); 

	        return os.toByteArray();
	    } 
	    catch (HTTPGetException je)
	    {
	    	throw je;
	    }
	    catch (Exception e) 
	    {
			throw new HTTPGetException("Unable to fetch content at "+url+".", e);
	    }
	    finally
	    {
	    	getMethod.releaseConnection();
	    }
	}

	private void removeTree(HttpServletRequest req) { 
		WCTNodeTree tree = (WCTNodeTree) req.getSession().getAttribute("tree");
		tree.clear();
		req.getSession().removeAttribute("tree");
		System.gc();
	}

	private void removeAQAImports(HttpServletRequest req) { 
		req.getSession().removeAttribute("aqaImports");
		System.gc();
	}

	private Document readXMLDocument(File f) throws SAXException, IOException, ParserConfigurationException
	{
		StringBuffer sb = new StringBuffer();
        BufferedReader in = new BufferedReader(new FileReader(f));
		
        try
        {
	        String str;
	        while ((str = in.readLine()) != null) 
	        {
	               sb.append(str);
	        }
        }
        finally
        {
        	in.close();
        }
		
		return XMLConverter.StringToDocument(sb.toString());
	}

	public void setQualityReviewFacade(QualityReviewFacade qrf) {
		this.qualityReviewFacade = qrf;
	}
	
    public String getSuccessView()
    {
        return successView;
    }

    public void setSuccessView(String successView)
    {
        this.successView = successView;
    }

	public HarvestResourceUrlMapper getHarvestResourceUrlMapper() {
		return harvestResourceUrlMapper;
	}

	public void setHarvestResourceUrlMapper(
			HarvestResourceUrlMapper harvestResourceUrlMapper) {
		this.harvestResourceUrlMapper = harvestResourceUrlMapper;
	}

	public void setEnableAccessTool(boolean enableAccessTool) {
		this.enableAccessTool = enableAccessTool;
	}

	public boolean isEnableAccessTool() {
		return enableAccessTool;
	}
	
	public String getUploadedFilesDir() {
		return uploadedFilesDir;
	}
	
	public void setUploadedFilesDir(String uploadedFilesDir) {
		this.uploadedFilesDir = uploadedFilesDir;
	}

	public void setAutoQAUrl(String autoQAUrl) {
		this.autoQAUrl = autoQAUrl;
	}

	public String getAutoQAUrl() {
		return autoQAUrl;
	}

	/**
	 * @param harvestLogManager the harvestLogManager to set
	 */
	public void setHarvestLogManager(HarvestLogManager harvestLogManager) {
		this.harvestLogManager = harvestLogManager;
	}

	/**
	 * @param targetInstanceManager the targetInstanceManager to set
	 */
	public void setTargetInstanceManager(TargetInstanceManager targetInstanceManager) {
		this.targetInstanceManager = targetInstanceManager;
	}

}
