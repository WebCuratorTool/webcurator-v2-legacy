package org.webcurator.core.profiles;

import static org.junit.Assert.*;

import org.archive.crawler.settings.*;
import org.junit.Test;
import org.webcurator.test.BaseWCTTest;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.StringReader;
import java.util.*;

import javax.xml.parsers.SAXParserFactory;
import org.springframework.mock.web.*;


public class HeritrixProfileTest extends BaseWCTTest<HeritrixProfile>{

	/** The Heritrix x-path to the max-time-sec element */
	public static final String ELEM_MAX_TIME_SEC = "/crawl-order/max-time-sec";
	/** The Heritrix x-path to the max-bytes-download element */
	public static final String ELEM_MAX_BYTES_DOWNLOAD = "/crawl-order/max-bytes-download";
	/** The Heritrix x-path to the max-document-download element */
	public static final String ELEM_MAX_DOCUMENT_DOWNLOAD = "/crawl-order/max-document-download";
	/** The Heritrix x-path to the robots-honoring-policy element */
	public static final String ELEM_ROBOTS_HONOURING_POLICY = "/crawl-order/robots-honoring-policy/type";
	/** The Heritrix x-path to the max-links-hops element */
	public static final String ELEM_MAX_LINK_HOPS = "/crawl-order/scope/max-link-hops";
	/** The Heritrix x-path to the max-trans-hops element */
	public static final String ELEM_MAX_TRANSITIVE_HOPS = "/crawl-order/scope/max-trans-hops";
	/** The Heritrix x-path to the exclude-filter element */
	public static final String ELEM_SCOPE_EXCLUDE_FILTER = "/crawl-order/scope/exclude-filter/filters";
	/** The Heritrix x-path to the force-accept-filter element */
	public static final String ELEM_SCOPE_FORCE_ACCEPT_FILTER = "/crawl-order/scope/force-accept-filter/filters";
	/** The Heritrix x-path to the write-processor filters element */
	public static final String ELEM_WRITE_PROCESSORS_FILTER = "/crawl-order/write-processors/Archiver/filters";
	/** The Heritrix x-path to the http fetch exclude filters element */
	public static final String ELEM_FETCH_HTTP_EXCLUDE_FILTER = "/crawl-order/fetch-processors/HTTP/filters";
	/** The Heritrix x-path to the scope decide rules element */
	public static final String ELEM_SCOPE_DECIDE_RULES = "/crawl-order/scope/decide-rules/rules";
	/** The Heritrix x-path to the write-processor decide rules element */
	public static final String ELEM_WRITE_PROCESSORS_DECIDE_RULES = "/crawl-order/write-processors/Archiver/Archiver#decide-rules/rules";
	/** The Heritrix x-path to the toe threads element */
	public static final String ELEM_TOE_THREADS = "/crawl-order/max-toe-threads";
	
	public HeritrixProfileTest()
	{
		super(HeritrixProfile.class, 
				"src/test/java/org/webcurator/core/profiles/HeritrixProfileTest.xml");
	}
	
	public void setUp() throws Exception {
		
		try
		{
			super.setUp();
		}
		catch(java.lang.InstantiationException e)
		{
			testInstance = HeritrixProfile.create(new File(testFile));
		}
	}
	
	@Test
	public final void testHeritrixProfile() {
		StringBuffer buffer = new StringBuffer();
	   	BufferedReader profileReader = null;
	   	try
	   	{
		    profileReader = new BufferedReader(new FileReader(new File(testFile)));
			String line = null;
			
			while( (line=profileReader.readLine()) != null) {
				buffer.append(line);
				buffer.append("\n");
			}
			
		    // Create a settings handler. The file is a dummy file simply to allow
		    // us to construct the object.
		    XMLSettingsHandler settingsHandler = new XMLSettingsHandler(new File("dummy_file"));
		    XMLReader parser = SAXParserFactory.newInstance().newSAXParser().getXMLReader();
		    StringReader reader = new StringReader(buffer.toString());
		    
			parser.setContentHandler(new CrawlSettingsSAXHandler(settingsHandler.getSettings(null)));
			InputSource source = new InputSource(reader);
			parser.parse(source);
			
			HeritrixProfile profile = new HeritrixProfile(settingsHandler, null);
			assertNotNull(profile);
	   	}
	   	catch(Exception ex)
	   	{
	   		fail(ex.getMessage());
	   	}
	   	finally
	   	{
    		try { profileReader.close(); } catch(Exception ex) {}
	   	}
		
	}

	@Test
	public final void testElementExists() {
		
		try
		{
			assertTrue(testInstance.elementExists(ELEM_SCOPE_DECIDE_RULES));
			assertFalse(testInstance.elementExists("dummy"));
		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}

	@Test
	public final void testGetElement() {
		
		try
		{
			ProfileElement element = testInstance.getElement(ELEM_SCOPE_DECIDE_RULES);
			assertNotNull(element);
			assertEquals(element.getAbsoluteName(), ELEM_SCOPE_DECIDE_RULES);
		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}

	@Test
	public final void testMoveMapElementUp() {
		try
		{
			assertTrue(testInstance.moveMapElementUp(ELEM_SCOPE_DECIDE_RULES, "acceptIfSurtPrefixed"));
			assertFalse(testInstance.moveMapElementUp(ELEM_SCOPE_DECIDE_RULES, "acceptIfSurtPrefixed"));
		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}

	@Test
	public final void testMoveMapElementDown() {
		try
		{
			assertTrue(testInstance.moveMapElementDown(ELEM_SCOPE_DECIDE_RULES, "rejectIfTooManyPathSegs"));
			assertFalse(testInstance.moveMapElementDown(ELEM_SCOPE_DECIDE_RULES, "rejectIfTooManyPathSegs"));
		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}

	@Test
	public final void testRemoveMapElement() {
		try
		{
			testInstance.removeMapElement(ELEM_SCOPE_DECIDE_RULES, "rejectIfTooManyPathSegs");
		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}

	@Test
	public final void testClearMap() {
		try
		{
			testInstance.clearMap(ELEM_SCOPE_DECIDE_RULES);
		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}

	@Test
	public final void testSetToeThreads() {
		try
		{
			ProfileElement element = testInstance.getElement(ELEM_TOE_THREADS);
			int count = (Integer)element.getValue();
			testInstance.setToeThreads(count+1);
			assertEquals((Integer)element.getValue(), new Integer(count+1));
		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}

	@Test
	public final void testRemoveFromMapByType() {
		try
		{
			ProfileElement element = testInstance.getElement(ELEM_SCOPE_DECIDE_RULES+"/rejectIfTooManyPathSegs");
			testInstance.removeFromMapByType(ELEM_SCOPE_DECIDE_RULES, element.getType());
		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}

	@Test
	public final void testAddSimpleMapElement() {
		try
		{
			testInstance.addSimpleMapElement(ELEM_SCOPE_DECIDE_RULES, "testElement", "test");
			assertEquals((String)testInstance.getElement(ELEM_SCOPE_DECIDE_RULES+"/testElement").getValue(), "test");
		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}

	@Test
	public final void testAddMapElementStringSimpleType() {
		try
		{
			testInstance.addMapElement(ELEM_SCOPE_DECIDE_RULES, new SimpleType("testElement2", "Description Text", "test2"));
			assertEquals((String)testInstance.getElement(ELEM_SCOPE_DECIDE_RULES+"/testElement2").getValue(), "test2");
		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}

	@Test
	public final void testAddMapElementStringStringString() {
		try
		{
			//testInstance.addMapElement("/crawl-order/extract-processors", "BeanShellProcessor", "org.archive.crawler.processor.BeanShellProcessor");
			testInstance.addMapElement("/crawl-order/extract-processors", "ExtractorHTML2", "org.archive.crawler.extractor.ExtractorHTML");
		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}

	@Test
	public final void testGetOptionsForType() {
		try
		{
			List<String> options = HeritrixProfile.getOptionsForType(org.archive.crawler.framework.Processor.class);
			assertNotNull(options);
			assertFalse(options.isEmpty());
		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}

	@Test
	public final void testSetAllSimpleTypesHttpServletRequestComplexProfileElement() {
		try
		{
			MockHttpServletRequest request = new MockHttpServletRequest();
			request.addParameter("/crawl-order/disk-path", "testVal");
			
	        ProfileElement pe = testInstance.getElement("/crawl-order");
	        
	        testInstance.setAllSimpleTypes(request, (ComplexProfileElement) pe);
	        
	        assertEquals((String)testInstance.getElement("/crawl-order/disk-path").getValue(), "testVal");
		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}

	@Test
	public final void testSetListTypeStringList() {
		try
		{
			List<String> excludeUriFilters = new ArrayList<String>();
			excludeUriFilters.add("one");
			excludeUriFilters.add("two");
			excludeUriFilters.add("three");
			
			ListType<String> listType = new StringList("regexp-list","");
			testInstance.addMapElement(ELEM_SCOPE_DECIDE_RULES, new SimpleType("_wct_excl_uris", "", listType));
			
	        testInstance.setListType(ELEM_SCOPE_DECIDE_RULES + "/_wct_excl_uris", excludeUriFilters);

	        ProfileElement pe = testInstance.getElement(ELEM_SCOPE_DECIDE_RULES + "/_wct_excl_uris");
	        
	        assertEquals(((ListType)pe.getValue()).size(), excludeUriFilters.size());
	        for(int i = 0; i < excludeUriFilters.size(); i++)
	        {
		        assertEquals(((ListType)pe.getValue()).get(i), excludeUriFilters.get(i));
	        }
		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}

	@Test
	public final void testSetListTypeListTypeList() {
		try
		{
			List<String> excludeUriFilters = new ArrayList<String>();
			excludeUriFilters.add("one");
			excludeUriFilters.add("two");
			excludeUriFilters.add("three");
			
			ListType<String> listType = new StringList("regexp-list","");
			testInstance.addMapElement(ELEM_SCOPE_DECIDE_RULES, new SimpleType("_wct_excl_uris", "", listType));
			
	        testInstance.setListType(listType, excludeUriFilters);

	        ProfileElement pe = testInstance.getElement(ELEM_SCOPE_DECIDE_RULES + "/_wct_excl_uris");
	        
	        assertEquals(((ListType)pe.getValue()).size(), excludeUriFilters.size());
	        for(int i = 0; i < excludeUriFilters.size(); i++)
	        {
		        assertEquals(((ListType)pe.getValue()).get(i), excludeUriFilters.get(i));
	        }
		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}

	@Test
	public final void testSetAllSimpleTypesHttpServletRequestSimpleProfileElement() {
		try
		{
			MockHttpServletRequest request = new MockHttpServletRequest();
			request.addParameter("/crawl-order/disk-path", "testVal");
			
	        ProfileElement pe = testInstance.getElement("/crawl-order/disk-path");
	        
	        testInstance.setAllSimpleTypes(request, (SimpleProfileElement) pe);
	        
	        assertEquals((String)testInstance.getElement("/crawl-order/disk-path").getValue(), "testVal");
		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}

	@Test
	public final void testSetSimpleType() {
		try
		{
	        ProfileElement pe = testInstance.getElement("/crawl-order/disk-path");
	        
	        testInstance.setSimpleType("/crawl-order/disk-path", "testSetSimpleType");
	        
	        assertEquals((String)testInstance.getElement("/crawl-order/disk-path").getValue(), "testSetSimpleType");
		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}

	@Test
	public final void testSetScopeClass() {
		try
		{
	        ProfileElement pe = testInstance.getElement("/crawl-order/disk-path");
	        
	        testInstance.setScopeClass("org.archive.crawler.scope.ClassicScope");
		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}

	@Test
	public final void testToString() {
		try
		{
	        assertNotNull(testInstance.toString());
	        assertTrue(testInstance.toString().length() > 0);
		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}

	@Test
	public final void testCreate() {
		try
		{
			assertNotNull(HeritrixProfile.create());
		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}

	@Test
	public final void testCreateFile() {
		try
		{
			assertNotNull(HeritrixProfile.create(new File(testFile)));
		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}

	@Test
	public final void testFromString() {
		StringBuffer buffer = new StringBuffer();
	   	BufferedReader profileReader = null;
	   	try
	   	{
		    profileReader = new BufferedReader(new FileReader(new File(testFile)));
			String line = null;
			
			while( (line=profileReader.readLine()) != null) {
				buffer.append(line);
				buffer.append("\n");
			}
			
			assertNotNull(HeritrixProfile.fromString(buffer.toString()));
		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}

}
