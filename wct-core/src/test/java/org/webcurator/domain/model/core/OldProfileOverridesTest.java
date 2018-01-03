package org.webcurator.domain.model.core;

import static org.junit.Assert.*;

import java.io.*;
import java.util.*;

import javax.xml.xpath.*;

import org.junit.Test;
import org.webcurator.test.*;
import org.webcurator.core.profiles.HeritrixProfile;
import org.xml.sax.InputSource;


public class OldProfileOverridesTest extends BaseWCTTest<ProfileOverrides> {

	private HeritrixProfile profile = null; 
	private String fileText = null;
	
	public OldProfileOverridesTest()
	{
		super(ProfileOverrides.class, 
			"src/test/java/org/webcurator/domain/model/core/OldProfileOverridesTest.xml");
	}
	
	public void setUp() throws Exception {
		super.setUp();
		
        FileInputStream fis = new FileInputStream(testFile);
        int x= fis.available();
        byte b[]= new byte[x];
        fis.read(b);
        fileText = new String(b);
        
        profile = HeritrixProfile.fromString(fileText);
	}

	private String getResult(String xpath) throws XPathExpressionException
	{
        XPathFactory factory = XPathFactory.newInstance(); 
        XPath xPath = factory.newXPath();
        
        InputSource doc = new InputSource(new StringReader(profile.toString()));

        return xPath.evaluate(xpath, doc);
	}
	
	@Test
	public final void testApply_overrideMaxTimeSec() 
	{
		try
		{
			Long value = 5L;
			testInstance.setMaxTimeSec(value);
			testInstance.setOverrideMaxTimeSec(true);
			testInstance.apply(profile);
	
			String result = getResult("/crawl-order/controller/long[@name='max-time-sec']");
	        assertTrue(result.equals(value.toString()));
		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}

	@Test
	public final void testApply_overrideMaxBytesDownload() 
	{
		try
		{
			Long value = 5L;
			testInstance.setMaxBytesDownload(value);
			testInstance.setOverrideMaxBytesDownload(true);
			testInstance.apply(profile);
	
			String result = getResult("/crawl-order/controller/long[@name='max-bytes-download']");
	        assertTrue(result.equals(value.toString()));
		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}

	@Test
	public final void testApply_overrideMaxHarvestDocuments() 
	{
		try
		{
			Long value = 5L;
			testInstance.setMaxHarvestDocuments(value);
			testInstance.setOverrideMaxHarvestDocuments(true);
			testInstance.apply(profile);
	
			String result = getResult("/crawl-order/controller/long[@name='max-document-download']");
	        assertTrue(result.equals(value.toString()));
		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}

	@Test
	public final void testApply_overrideMaxLinkHops() 
	{
		try
		{
			Integer value = 5;
			testInstance.setMaxLinkHops(value);
			testInstance.setOverrideMaxLinkHops(true);
			testInstance.apply(profile);
	
			String result = getResult("/crawl-order/controller/newObject[@name='scope']/integer[@name='max-link-hops']");
			assertTrue(result.equals(value.toString()));
			result = getResult("/crawl-order/controller/newObject[@name='scope']/integer[@name='max-trans-hops']");
			assertTrue(result.equals(value.toString()));
		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}

	@Test
	public final void testApply_overrideMaxPathDepth() 
	{
		try
		{
			Integer value = 5;
			testInstance.setMaxPathDepth(value);
			testInstance.setOverrideMaxPathDepth(true);
			testInstance.apply(profile);
	
			String result = getResult("/crawl-order/controller/newObject[@name='scope']/newObject[@name='exclude-filter']/map/newObject[@name='_wct_max_depth']/integer[@name='max-path-depth']");
			assertTrue(result.equals(value.toString()));
		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}

	@Test
	public final void testApply_overrideExcludeUriFilters() 
	{
		try
		{
			String filter1 = "*.htm";
			String filter2 = "*.html";
			
			List<String> uriFilters = new ArrayList<String>();
			uriFilters.add(filter1);
			uriFilters.add(filter2);
			
			testInstance.setExcludeUriFilters(uriFilters);
			testInstance.setOverrideExcludeUriFilters(true);
			testInstance.apply(profile);
	
			String result = getResult("/crawl-order/controller/newObject[@name='scope']/newObject[@name='exclude-filter']/map/newObject[@name='_wct_excl_uris']/stringList/string[1]");
			assertTrue(result.equals(filter1));
			result = getResult("/crawl-order/controller/newObject[@name='scope']/newObject[@name='exclude-filter']/map/newObject[@name='_wct_excl_uris']/stringList/string[2]");
			assertTrue(result.equals(filter2));
			result = getResult("/crawl-order/controller/newObject[@name='scope']/newObject[@name='exclude-filter']/map/newObject[@name='_wct_excl_uris']/string[@name='list-logic']");
			assertTrue(result.equals("OR"));
			result = getResult("/crawl-order/controller/newObject[@name='scope']/newObject[@name='exclude-filter']/map/newObject[@name='_wct_excl_uris']/boolean[@name='enabled']");
			assertTrue(result.equals("true"));
			result = getResult("/crawl-order/controller/newObject[@name='scope']/newObject[@name='exclude-filter']/map/newObject[@name='_wct_excl_uris']/boolean[@name='if-match-return']");
			assertTrue(result.equals("true"));
		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}

	@Test
	public final void testApply_overrideIncludeUriFilters() 
	{
		try
		{
			String filter1 = "*.htm";
			String filter2 = "*.html";
			
			List<String> uriFilters = new ArrayList<String>();
			uriFilters.add(filter1);
			uriFilters.add(filter2);
			
			testInstance.setIncludeUriFilters(uriFilters);
			testInstance.setOverrideIncludeUriFilters(true);
			testInstance.apply(profile);
		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}

	@Test
	public final void testApply_overrideExcludedMimeTypes() 
	{
		try
		{
			String value = "text/html";
			
			testInstance.setExcludedMimeTypes(value);
			testInstance.setOverrideExcludedMimeTypes(true);
			testInstance.apply(profile);
	
			String result = getResult("/crawl-order/controller/map[@name='write-processors']/newObject[@name='Archiver']/newObject/map/newObject[@name='_wct_content_type']/string[@name='regexp']");
			assertTrue(result.equals(value));
			result = getResult("/crawl-order/controller/map[@name='write-processors']/newObject[@name='Archiver']/newObject/map/newObject[@name='_wct_content_type']/string[@name='decision']");
			assertTrue(result.equals("REJECT"));
		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}
	
	@Test
	public final void testApply_overrideRobotsHonouringPolicy() 
	{
		try
		{
			String value = "ignore";
			
			testInstance.setRobotsHonouringPolicy(value);
			testInstance.setOverrideRobotsHonouringPolicy(true);
			testInstance.apply(profile);
	
			String result = getResult("/crawl-order/controller/newObject[@name='robots-honoring-policy']/string[@name='type']");
			assertTrue(result.equals(value));
		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}
	
	@Test
	public final void testApply_overrideCredentials() 
	{
		try
		{
			String domain = "TestDomain";
			String username = "username";
			String password = "password";
			String realm = "testRealm";
			String method = "GET";
			String login = "http://test/login";
			
			ProfileBasicCredentials basicCredentials = new ProfileBasicCredentials();
			basicCredentials.setCredentialsDomain(domain);
			basicCredentials.setPassword(password);
			basicCredentials.setRealm(realm);
			basicCredentials.setUsername(username);
			
			ProfileFormCredentials formCredentials = new ProfileFormCredentials();
			formCredentials.setCredentialsDomain(domain);
			formCredentials.setHttpMethod(method);
			formCredentials.setLoginUri(login);
			formCredentials.setPassword(password);
			formCredentials.setPasswordField(password+"Field");
			formCredentials.setUsername(username);
			formCredentials.setUsernameField(username+"Field");
			
			List<ProfileCredentials> credentials = new ArrayList<ProfileCredentials>();
			credentials.add(basicCredentials);
			credentials.add(formCredentials);
			
			testInstance.setCredentials(credentials);
			testInstance.setOverrideCredentials(true);
			testInstance.apply(profile);
	
			String result = getResult("/crawl-order/controller/newObject[@name='credential-store']/map[@name='credentials']/newObject[@name='_creds_0']/string[@name='credential-domain']");
			assertTrue(result.equals(domain));
			result = getResult("/crawl-order/controller/newObject[@name='credential-store']/map[@name='credentials']/newObject[@name='_creds_0']/string[@name='realm']");
			assertTrue(result.equals(realm));
			result = getResult("/crawl-order/controller/newObject[@name='credential-store']/map[@name='credentials']/newObject[@name='_creds_0']/string[@name='login']");
			assertTrue(result.equals(username));
			result = getResult("/crawl-order/controller/newObject[@name='credential-store']/map[@name='credentials']/newObject[@name='_creds_0']/string[@name='password']");
			assertTrue(result.equals(password));

			result = getResult("/crawl-order/controller/newObject[@name='credential-store']/map[@name='credentials']/newObject[@name='_creds_1']/string[@name='credential-domain']");
			assertTrue(result.equals(domain));
			result = getResult("/crawl-order/controller/newObject[@name='credential-store']/map[@name='credentials']/newObject[@name='_creds_1']/string[@name='login-uri']");
			assertTrue(result.equals(login));
			result = getResult("/crawl-order/controller/newObject[@name='credential-store']/map[@name='credentials']/newObject[@name='_creds_1']/string[@name='http-method']");
			assertTrue(result.equals(method));
			result = getResult("/crawl-order/controller/newObject[@name='credential-store']/map[@name='credentials']/newObject[@name='_creds_1']/map/string[@name='"+username+"Field']");
			assertTrue(result.equals(username));
			result = getResult("/crawl-order/controller/newObject[@name='credential-store']/map[@name='credentials']/newObject[@name='_creds_1']/map/string[@name='"+password+"Field']");
			assertTrue(result.equals(password));
		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}
}
