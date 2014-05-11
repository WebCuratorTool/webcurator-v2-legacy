package org.webcurator.ui.tools.controller;

import static org.junit.Assert.*;

import java.io.File;
import java.util.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.webcurator.test.BaseWCTTest;

//XML file imports
import java.io.*;


public class BrowseHelperTest extends BaseWCTTest<BrowseHelper> {

	private DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
	private Document theFile = null; 
	private String carriageReturn = "\n";
	
	/** A Map of Content-Type to replacement patterns. **/
	private Map<String, List<String>> contentTypePatterns = new HashMap<String, List<String>>();

	/** A List of TagMagix expressions to run on HTML content. **/
	//private List<TagMagixHelper> htmlTagPatterns = new LinkedList<TagMagixHelper>();
	
	//private List<StringReplacer> urlConversionReplacements = null;


	public BrowseHelperTest()
	{
		super(BrowseHelper.class, "src/test/java/org/webcurator/ui/tools/controller/browsehelpertest.xml");
		
	}
	
    //Override BaseWCTTest setup method
	public void setUp() throws Exception {
		//call the overridden method as well
		super.setUp();
	}

	@Test
	public final void testGetAbsURL() {
		String url;
		
		url = BrowseHelper.getAbsURL("http://test.com", "test.htm");
		assertNotNull(url);
		assertEquals("http://test.com/test.htm", url);
		
		url = BrowseHelper.getAbsURL("http://test.com", "test 2.htm");
		assertNotNull(url);
		assertEquals("http://test.com/test%202.htm", url);
		
		url = BrowseHelper.getAbsURL("http://test.com", "test3.htm#fred");
		assertNotNull(url);
		assertEquals("http://test.com/test3.htm#fred", url);
		
		url = BrowseHelper.getAbsURL("http://test.com", "test4.htm#fred 4");
		assertNotNull(url);
		assertEquals("http://test.com/test4.htm#fred+4", url);
		
		url = BrowseHelper.getAbsURL("http://test.com", "test5.htm#fred 5#VML");
		assertNotNull(url);
		assertEquals("http://test.com/test5.htm#fred+5#VML", url);
		
		url = BrowseHelper.getAbsURL("http://test.com", "test6.htm#VML");
		assertNotNull(url);
		assertEquals("http://test.com/test6.htm#VML", url);
		
		url = BrowseHelper.getAbsURL("http://test.com", "\ntest7.htm");
		assertNotNull(url);
		assertEquals("http://test.com/test7.htm", url);
		
		url = BrowseHelper.getAbsURL("http://test.com", "fred\\test8.htm");
		assertNotNull(url);
		assertEquals("http://test.com/fred/test8.htm", url);
		
		url = BrowseHelper.getAbsURL("http://test.com", "fred\\test9.htm?value=%7CProgressive%200.4%%7C");
		assertNotNull(url);
		assertEquals("http://test.com/fred/test9.htm?value=%7CProgressive%200.4%%7C", url);
		
		url = BrowseHelper.getAbsURL("http://test.com", "mailto:book@bats.co.nz?subject=Booking: Destination: Death&amp;body=PLEASE INCLUDE1. your name : 2. your phone number : 3. the date of the show you are attending : 4. the number of tickets you would like : thank you.");
		assertNotNull(url);
		assertEquals("mailto:book@bats.co.nz?subject=Booking:%20Destination:%20Death&amp;body=PLEASE%20INCLUDE1.%20your%20name%20:%202.%20your%20phone%20number%20:%203.%20the%20date%20of%20the%20show%20you%20are%20attending%20:%204.%20the%20number%20of%20tickets%20you%20would%20like%20:%20thank%20you.", url);
		
		url = BrowseHelper.getAbsURL("http://test.com", "+photos[0]+");
		assertNotNull(url);
		assertEquals("http://test.com/+photos%5B0%5D+", url);
	}
	
	@Test
	public final void testFix() {

		try {
			
			List<String> textHtmlPatterns = new ArrayList<String>();
			List<String> textCssPatterns = new ArrayList<String>();
			
			// See property name="contentTypePatterns" in cfg/wct-browse-servlet.xml config. file
			
			textHtmlPatterns.add("(?i)\\burl\\((?![\"'].)([^\\)]*)\\)");
			textHtmlPatterns.add("(?i)\\burl\\(\"([^\"]*)\"\\)");
			textHtmlPatterns.add("(?i)\\burl\\('([^']*)'\\)");
			
			/*
			textHtmlPatterns.add("(?i)background-image\\s*:\\s+url\\(([^\\)]*)\\)");
			textHtmlPatterns.add("(?i)background-image\\s*:\\s+url\\('([^']*)'\\)");
			textHtmlPatterns.add("(?i)background-image\\s*:\\s+url\\(\"([^\"]*)\"\\)");
			textHtmlPatterns.add("(?i)@import\\s+url\\(\"([^\"]*)\"\\)");
			textHtmlPatterns.add("(?i)@import\\s+url\\((?!\")([^\\)]*)\\)");
			*/

			textHtmlPatterns.add("(?i)@import\\s+\"([^\"]*)\"");
            
            // <!--  A:HREF -->
            textHtmlPatterns.add("(?i)<\\s*A\\s+[^>]*\\bHREF\\s*=\\s*\"((?!javascript:)[^\"]*)\"");
            textHtmlPatterns.add("(?i)<\\s*A\\s+[^>]*\\bHREF\\s*=\\s*'((?!javascript:)[^']*)'");	
            textHtmlPatterns.add("(?i)<\\s*a\\s+[^>]*\\bhref=((?!javascript:)[^\\t\\n\\x0B\\f\\r>\\\"']+)"); 
            
            // <!--  META URL --> 
            textHtmlPatterns.add("(?i)<\\s*META\\s+[^>]*\\bURL\\s*=\\s*\"([^\"]*)\"");
            textHtmlPatterns.add("(?i)<\\s*META\\s+[^>]*\\bURL\\s*=\\s*'([^']*)'");	
            textHtmlPatterns.add("(?i)<\\s*META\\s+[^>]*\\bURL=([^\\t\\n\\x0B\\f\\r>\\\"']+)");                                                     

            // <!--  OBJECT CODEBASE --> 
            textHtmlPatterns.add("(?i)<\\s*OBJECT\\s+[^>]*\\bCODEBASE\\s*=\\s*\"([^\"]*)\"");
            textHtmlPatterns.add("(?i)<\\s*OBJECT\\s+[^>]*\\bCODEBASE\\s*=\\s*'([^']*)'");	
            textHtmlPatterns.add("(?i)<\\s*OBJECT\\s+[^>]*\\bCODEBASE=([^\\t\\n\\x0B\\f\\r>\\\"']+)");     
            
            // <!--  OBJECT DATA --> 
            textHtmlPatterns.add("(?i)<\\s*OBJECT\\s+[^>]*\\bDATA\\s*=\\s*\"([^\"]*)\"");
            textHtmlPatterns.add("(?i)<\\s*OBJECT\\s+[^>]*\\bDATA\\s*=\\s*'([^']*)'");	
            textHtmlPatterns.add("(?i)<\\s*OBJECT\\s+[^>]*\\bDATA=([^\\t\\n\\x0B\\f\\r>\\\"']+)");                                                                                                                            
            
            // <!--  APPLET CODEBASE --> 
            textHtmlPatterns.add("(?i)<\\s*APPLET\\s+[^>]*\\bCODEBASE\\s*=\\s*\"([^\"]*)\"");
            textHtmlPatterns.add("(?i)<\\s*APPLET\\s+[^>]*\\bCODEBASE\\s*=\\s*'([^']*)'");	
            textHtmlPatterns.add("(?i)<\\s*APPLET\\s+[^>]*\\bCODEBASE=([^\\t\\n\\x0B\\f\\r>\\\"']+)");                         
                                                           
            // <!--  APPLET ARCHIVE --> 
            textHtmlPatterns.add("(?i)<\\s*APPLET\\s+[^>]*\\bARCHIVE\\s*=\\s*\"([^\"]*)\"");
            textHtmlPatterns.add("(?i)<\\s*APPLET\\s+[^>]*\\bARCHIVE\\s*=\\s*'([^']*)'");	
            
            // <!--  BODY/TD BACKGROUND --> 
            textHtmlPatterns.add("(?i)<\\s*(?:BODY|TD)\\s+[^>]*\\bBACKGROUND\\s*=\\s*\"([^\"]*)\"");
            textHtmlPatterns.add("(?i)<\\s*(?:BODY|TD)\\s+[^>]*\\bBACKGROUND\\s*=\\s*'([^']*)'");	
            textHtmlPatterns.add("(?i)<\\s*(?:BODY|TD)\\s+[^>]*\\bBACKGROUND=(?!\\\\\")([^\\t\\n\\x0B\\f\\r>\"']+)");    
            
            //<!--  Note that the following regular expressions are merged sets. They have proven
            //      to be slightly faster than specifying each one independently, and prevent a
            //      lot of duplication. -->
            
            // <!-- Regular expression for HREFs attributes -->
			textHtmlPatterns.add("(?i)<\\s*(?:LINK|AREA)\\s+[^>]*\\bHREF=\"([^\"]*)\"");
            textHtmlPatterns.add("(?i)<\\s*(?:LINK|AREA)\\s+[^>]*\\bHREF='([^']*)'");	
            textHtmlPatterns.add("(?i)<\\s*(?:LINK|AREA)\\s+[^>]*\\bHREF=([^\\\\t\\\\n\\\\x0B\\\\f\\\\r>\\\"']+)");  

			// <!--  Regular expressions for HREF attributes -->
            textHtmlPatterns.add("(?i)<\\s*(?:IMG|FRAME|SCRIPT|EMBED|INPUT)\\s+[^>]*\\bSRC\\s*=\\s*\"([^\"]*)\"");
            textHtmlPatterns.add("(?i)<\\s*(?:IMG|FRAME|SCRIPT|EMBED|INPUT)\\s+[^>]*\\bSRC\\s*=\\s*'([^']*)'");	
            textHtmlPatterns.add("(?i)<\\s*(?:IMG|FRAME|SCRIPT|EMBED|INPUT)\\s+[^>]*\\bSRC=([^\\t\\n\\x0B\\f\\r>\\\"']+)");   
                                    
            // <!--  Simple JavaScript replacement -->
            textHtmlPatterns.add("window.location=\"([^\"]*)\";");
			
			
			contentTypePatterns.put("text/html", textHtmlPatterns);
			
        	textCssPatterns.add("(?i)\\burl\\((?![\"'].)([^\\)]*)\\)");
        	textCssPatterns.add("(?i)\\burl\\(\"([^\"]*)\"\\)");
        	textCssPatterns.add("(?i)\\burl\\('([^']*)'\\)");                    	
        	
        	/*
            textCssPatterns.add("(?i)background-image\\s*:\\s+url\\(([^\\)]*)\\)");
            textCssPatterns.add("background:\\s*url\\(([^\\)]*)\\)");
            textCssPatterns.add("background: transparent url\\(\"([^\\\"]*)\"\\)");
            textCssPatterns.add("background: transparent url\\('([^\\']*)'\\)");
            textCssPatterns.add("background: transparent url\\((?!'|\")([^\\)]*)\\)");                        
            textCssPatterns.add("@import\\s+url\\(\"([^\"]*)\"\\)");
            textCssPatterns.add("@import\\s+url\\((?!\")([^\\)]*)\\)");
			*/
			
			contentTypePatterns.put("text/css", textCssPatterns);
			
			BrowseHelper helper = new BrowseHelper();
			helper.setPrefix("http://localhost:8080/wct/curator/tools/browse");

			helper.setContentTypePatterns(contentTypePatterns);

			// add a map of patterns to fix
			Map<String, String> fixes = new HashMap<String, String>();
			fixes.put("top.location", "//top.location");
			fixes.put("window.location", "//window.location");
			fixes.put("http-equiv=&quot;refresh&quot; content=&quot;0; url=/", "http-equiv=&quot;refresh&quot; content=&quot;0; url=./");
			BrowseController controller = new BrowseController();
			controller.setFixTokens(fixes);
			
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
	        theFile = docBuilder.parse (new File(testFile));

	    	NodeList pageTestNodes = theFile.getElementsByTagName("pagetest");
	    	
	    	for (int i = 0; i < pageTestNodes.getLength(); i++)
	    	{
	    		String inFileName = "";
	    		String outFileName = "";
	    		String contentType = "";
	    		String Oid = "";
	    		String resourceUrl = "";
	    		
	    		Node testNode = pageTestNodes.item(i);
	    		if(testNode.getNodeType() == Node.ELEMENT_NODE)
	    		{
	    	 		NodeList children = testNode.getChildNodes();
	    			for(int j = 0; j < children.getLength(); j++)
	    			{
	    				Node child = children.item(j);
	    				if(child.getNodeType() == Node.ELEMENT_NODE)
	    				{
	    					if(child.getNodeName().equals("infilename"))
	    					{
	    						inFileName = getString(child);
	    					}
	    					else if(child.getNodeName().equals("outfilename"))
	    					{
	    						outFileName = getString(child);
	    					}
	    					else if(child.getNodeName().equals("contenttype"))
	    					{
	    						contentType = getString(child);
	    					}
	    					else if(child.getNodeName().equals("oid"))
	    					{
	    						Oid = getString(child);
	    					}
	    					else if(child.getNodeName().equals("resourceurl"))
	    					{
	    						resourceUrl = getString(child);
	    					}
	    				}
	    			}
	    			
	    			StringBuilder pageContent = readFile(inFileName);
	    			StringBuilder expectedPatchedContent = readFile(outFileName);
	    			
	    			helper.fix(pageContent, contentType, Long.valueOf(Oid), resourceUrl);
	    			//writeFile(pageContent); // helps with debugging
	    			
	    			log.debug("pageContent.length() is: " + pageContent.length());
	    			log.debug("expectedPatchedContent.length() is: " + expectedPatchedContent.length());
	    			// we add 2 to the expected figure since all occurrences of 'top.location' will have two
	    			// characters : "//" inserted before them to disable client-side redirects
	    			assertTrue("BrowseHelper.fix(): patched content length is not equal to expected content length.", (pageContent.length() == expectedPatchedContent.length() + 2));
	    		}
	    	}
		}
		catch (Exception e)
		{
			String message = e.getClass().toString() + " - " + e.getMessage();
			log.debug(message);
			e.printStackTrace();
			fail(message);
		}
	}

    private String getString(Node child)
    {
    	return child.getTextContent();
    }
	
	/**
	* Read the file specified by the filePath parameter using
	* a FileReader and a BufferedReader
	* 
	* @param filePath
	* Path to the file
	* 
	* @return
	* Contents of the file
	*/
	private StringBuilder readFile(String filePath)
	{

		FileReader fileReader = null;
		BufferedReader bufferedReader = null;

		StringBuilder textFromFile = new StringBuilder();
	
		try
		{
			fileReader = new FileReader(filePath); // throws FileNotFoundException
			bufferedReader = new BufferedReader(fileReader);
		
			// Read through the entire file
			String currentLineFromFile = bufferedReader.readLine(); // throws IOException
			while(currentLineFromFile != null)
			{
				// Add a carriage return (line break) to preserve the file formatting.
				textFromFile.append(currentLineFromFile + carriageReturn);
				currentLineFromFile = bufferedReader.readLine(); // throws IOException
			}
			
			return textFromFile;
		}
		catch (IOException ioException) 
		{
			// Failed to read the file
			System.err.println("Problems reading from the file with the path '" + filePath + "'. Program will now exit");
			ioException.printStackTrace();
		}
		finally
		{
			// Good practice: Close the readers to free up any resources.
			try
			{
				if (bufferedReader != null)
				{
					bufferedReader.close();
				}
			
				if (fileReader != null)
				{
					fileReader.close();
				}
			}
			catch(IOException ioExceptionIgnore)
			{
				// Problems while closing the Readers. 
				// Nothing much we can do and so we ignore.
			}

		}
		return textFromFile;
	}

	private void writeFile(StringBuilder data) {
		
		FileOutputStream fileOutputStream = null;
		
		try {
			
			fileOutputStream = new FileOutputStream("src/test/java/org/webcurator/ui/tools/controller/patched.txt");
			fileOutputStream.write(data.toString().getBytes());
		}
		catch (Exception e) {
			// do nothing
		}
	}
}

