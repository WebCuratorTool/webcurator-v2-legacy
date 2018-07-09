package org.webcurator.ui.tools.controller;


import java.text.SimpleDateFormat;
import java.util.Date;
import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.webcurator.domain.model.core.ArcHarvestResource;
import org.webcurator.domain.model.core.HarvestResource;
import org.webcurator.domain.model.core.HarvestResult;
import org.webcurator.domain.model.core.TargetInstance;
import org.webcurator.ui.target.validator.TargetAccessValidatorTest;

import sun.reflect.ReflectionFactory.GetReflectionFactoryAction;

public class HarvestResourceUrlMapperTest extends TestCase {
	
	private static Log log = LogFactory.getLog(TargetAccessValidatorTest.class);
	
	ArcHarvestResource hRsr;
	
	@Before
	public void setUp() throws Exception {
		hRsr = new ArcHarvestResource();
		hRsr.setLength(10);
		hRsr.setName("HarvestResource");
		hRsr.setOid(1L);
		hRsr.setStatusCode(2);
		hRsr.setArcFileName("BL-123456-20091231235959-00000-localhost.arc.gz");
		
		HarvestResult hRslt = new HarvestResult();
		SimpleDateFormat format = new SimpleDateFormat("d/M/y hh:mm:ss");
		Date d = format.parse("3/11/2004 10:20:11");
		hRslt.setCreationDate(d);
		hRslt.setDerivedFrom(6);
		hRslt.setHarvestNumber(5);
		hRslt.setOid(99L);
		hRslt.setProvenanceNote("Prov-Note");
		hRslt.setState(3);
		TargetInstance ti = new TargetInstance();
		ti.setOid(1234L);
		hRslt.setTargetInstance(ti);
		hRsr.setResult(hRslt);
		
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testUrlMapGetterAndSetter() {
		//boolean result;
		try {
			HarvestResourceUrlMapper urlMapper = new HarvestResourceUrlMapper();
			String uRLMap = "http://www.wctTest.com";
			urlMapper.setUrlMap(uRLMap);
			assertTrue(urlMapper.getUrlMap()==uRLMap);		
		}
		catch (Exception e)
		{
			String message = e.getClass().toString() + " - " + e.getMessage();
			log.debug(message);
			fail(message);
		}
	}
	
	//Test List:
	// one property replacement for the H Rsr
	@Test
	public void testGenerateUrlMapOnePropertyReplacementHRsr() {
		//boolean result;
		try {
			HarvestResourceUrlMapper urlMapper = new HarvestResourceUrlMapper();
			String uRLMap = "http://www.wctTest.com?name={$HarvestResource.Name}";
			urlMapper.setUrlMap(uRLMap);
			assertTrue(urlMapper.generateUrl(hRsr.getResult(), hRsr.buildDTO()).equals("http://www.wctTest.com?name=HarvestResource"));
			
		}
		catch (Exception e)
		{
			String message = e.getClass().toString() + " - " + e.getMessage();
			log.debug(message);
			fail(message);
		}
	}
	
	// file date replacement for the H Rsr
	@Test
	public void testGenerateUrlMapFileDateReplacementHRsr() {
		//boolean result;
		try {
			HarvestResourceUrlMapper urlMapper = new HarvestResourceUrlMapper();
			String uRLMap = "http://www.wctTest.com/{$ArcHarvestResource.FileDate}/{$HarvestResource.Name}";
			urlMapper.setUrlMap(uRLMap);
			assertEquals(urlMapper.generateUrl(hRsr.getResult(), hRsr.buildDTO()), "http://www.wctTest.com/20091231235959/HarvestResource");
			
		}
		catch (Exception e)
		{
			String message = e.getClass().toString() + " - " + e.getMessage();
			log.debug(message);
			fail(message);
		}
	}
	
	// file date replacement for the H Rsr
	@Test
	public void testGenerateUrlMapNoDateReplacementHRsr() {
		//boolean result;
		try {
			hRsr.setArcFileName("temp.arc");
			HarvestResourceUrlMapper urlMapper = new HarvestResourceUrlMapper();
			String uRLMap = "http://www.wctTest.com/{$ArcHarvestResource.FileDate}/{$HarvestResource.Name}";
			urlMapper.setUrlMap(uRLMap);
			assertEquals(urlMapper.generateUrl(hRsr.getResult(), hRsr.buildDTO()), "http://www.wctTest.com/*/HarvestResource");
			
		}
		catch (Exception e)
		{
			String message = e.getClass().toString() + " - " + e.getMessage();
			log.debug(message);
			fail(message);
		}
	}
	
	// all property replacement for the H Rsr
	@Test
	public void testGenerateUrlMapAllPropertyReplacementHRsr() {
		//boolean result;
		try {
			HarvestResourceUrlMapper urlMapper = new HarvestResourceUrlMapper();
			String uRLMap = "http://www.wctTest.com?" +
					"name={$HarvestResource.Name}" +
					"&lenght={$HarvestResource.Length}" +
					"&Oid={$HarvestResource.Oid}" +
					"&Sc={$HarvestResource.StatusCode}";
			urlMapper.setUrlMap(uRLMap);
			//log.debug(urlMapper.generateUrl(hRsr.getResult(), hRsr.buildDTO()));
			assertTrue(urlMapper.generateUrl(hRsr.getResult(), hRsr.buildDTO()).equals("http://www.wctTest.com?" +
					"name=HarvestResource" +
					"&lenght=10" +
					"&Oid=1" +
					"&Sc=2"));
			
		}
		catch (Exception e)
		{
			String message = e.getClass().toString() + " - " + e.getMessage();
			log.debug(message);
			fail(message);
		}
	}
	
	// one property replacement for the H Result
	@Test
	public void testGenerateUrlMapOnePropertyReplacementHResult() {
		//boolean result;
		try {
			HarvestResourceUrlMapper urlMapper = new HarvestResourceUrlMapper();
			String uRLMap = "http://www.wctTest.com?" +
					"ResultOid={$HarvestResult.Oid}" ;
			urlMapper.setUrlMap(uRLMap);
			//log.debug(urlMapper.generateUrl(hRsr.getResult(), hRsr.buildDTO()));
			assertTrue(urlMapper.generateUrl(hRsr.getResult(), hRsr.buildDTO()).equals("http://www.wctTest.com?" +
					"ResultOid=99"));
			
		}
		catch (Exception e)
		{
			String message = e.getClass().toString() + " - " + e.getMessage();
			log.debug(message);
			fail(message);
		}
	}
	
	@Test
	public void testGenerateUrlMapDatePropertyReplacementHResultDefaultFormat() {
		//boolean result;
		try {
			HarvestResourceUrlMapper urlMapper = new HarvestResourceUrlMapper();
			String uRLMap = "http://www.wctTest.com?" +
					"CreationDate={$HarvestResult.CreationDate}" ;
			urlMapper.setUrlMap(uRLMap);
			//log.debug(urlMapper.generateUrl(hRsr.getResult(), hRsr.buildDTO()));
			assertTrue(urlMapper.generateUrl(hRsr.getResult(), hRsr.buildDTO()).equals("http://www.wctTest.com?" +
					"CreationDate=20041103102011"));
			
		}
		catch (Exception e)
		{
			String message = e.getClass().toString() + " - " + e.getMessage();
			log.debug(message);
			fail(message);
		}
	}
	
	@Test
	public void testGenerateUrlMapDateTwoPropertyReplacementHResultAndDefaultFormat() {
		//boolean result;
		try {
			HarvestResourceUrlMapper urlMapper = new HarvestResourceUrlMapper();
			String uRLMap = "http://www.wctTest.com?" +
					"CreationDate={$HarvestResult.CreationDate}&" +
					"ResultOid={$HarvestResult.Oid}" ;
			urlMapper.setUrlMap(uRLMap);
			//log.debug(urlMapper.generateUrl(hRsr.getResult(), hRsr.buildDTO()));
			assertTrue(urlMapper.generateUrl(hRsr.getResult(), hRsr.buildDTO()).equals("http://www.wctTest.com?" +
					"CreationDate=20041103102011&" +
					"ResultOid=99"));
			
		}
		catch (Exception e)
		{
			String message = e.getClass().toString() + " - " + e.getMessage();
			log.debug(message);
			fail(message);
		}
	}
	
	// all property replacement for the H Result
	@Test
	public void testGenerateUrlMapAllPropertyReplacementHResult() {
		//boolean result;
		try {
			HarvestResourceUrlMapper urlMapper = new HarvestResourceUrlMapper();
			String uRLMap = "http://www.wctTest.com?" +
			"CreationDate={$HarvestResult.CreationDate}" +
			"&DerivedFrom={$HarvestResult.DerivedFrom}" +
			"&HarvestNumber={$HarvestResult.HarvestNumber}" +
			"&RsltOid={$HarvestResult.Oid}" +
			"&ProvenanceNote={$HarvestResult.ProvenanceNote}" +
			"&State={$HarvestResult.State}";

			urlMapper.setUrlMap(uRLMap);
			//log.debug(urlMapper.generateUrl(hRsr.getResult(), hRsr.buildDTO()));
			assertTrue(urlMapper.generateUrl(hRsr.getResult(), hRsr.buildDTO()).equals("http://www.wctTest.com?" +
					"CreationDate=20041103102011" +
					"&DerivedFrom=6" +
					"&HarvestNumber=5" +
					"&RsltOid=99" +
					"&ProvenanceNote=Prov-Note" +
					"&State=3"));
			
		}
		catch (Exception e)
		{
			String message = e.getClass().toString() + " - " + e.getMessage();
			log.debug(message);
			fail(message);
		}
	}
	
	// all property replacement for the H Result
	@Test
	public void testGenerateUrlMapAllPropertyReplacement() {
		//boolean result;
		try {
			HarvestResourceUrlMapper urlMapper = new HarvestResourceUrlMapper();
			String uRLMap = "http://www.wctTest.com?" +
			"name={$HarvestResource.Name}" +
			"&lenght={$HarvestResource.Length}" +
			"&Oid={$HarvestResource.Oid}" +
			"&Sc={$HarvestResource.StatusCode}" +
			"CreationDate={$HarvestResult.CreationDate}" +
			"&DerivedFrom={$HarvestResult.DerivedFrom}" +
			"&HarvestNumber={$HarvestResult.HarvestNumber}" +
			"&RsltOid={$HarvestResult.Oid}" +
			"&ProvenanceNote={$HarvestResult.ProvenanceNote}" +
			"&State={$HarvestResult.State}";

			urlMapper.setUrlMap(uRLMap);
			//log.debug(urlMapper.generateUrl(hRsr.getResult(), hRsr.buildDTO()));
			assertTrue(urlMapper.generateUrl(hRsr.getResult(), hRsr.buildDTO()).equals("http://www.wctTest.com?" +
					"name=HarvestResource" +
					"&lenght=10" +
					"&Oid=1" +
					"&Sc=2"+
					"CreationDate=20041103102011" +
					"&DerivedFrom=6" +
					"&HarvestNumber=5" +
					"&RsltOid=99" +
					"&ProvenanceNote=Prov-Note" +
					"&State=3"));
			
		}
		catch (Exception e)
		{
			String message = e.getClass().toString() + " - " + e.getMessage();
			log.debug(message);
			fail(message);
		}
	}
	
	// one property replacement for the H Result with Dateformat A (dd/MM/yy)
	@Test
	public void testGenerateUrlMapDatePropertyReplacementHResultFormatA() {
		//boolean result;
		try {
			HarvestResourceUrlMapper urlMapper = new HarvestResourceUrlMapper();
			String uRLMap = "http://www.wctTest.com?" +
					"CreationDate={$HarvestResult.CreationDate,dd/MM/yy}" ;
			urlMapper.setUrlMap(uRLMap);
			//log.debug(urlMapper.generateUrl(hRsr.getResult(), hRsr.buildDTO()));
			assertTrue(urlMapper.generateUrl(hRsr.getResult(), hRsr.buildDTO()).equals("http://www.wctTest.com?" +
					"CreationDate=03/11/04"));
			
		}
		catch (Exception e)
		{
			String message = e.getClass().toString() + " - " + e.getMessage();
			log.debug(message);
			fail(message);
		}
	}
	
	// one property replacement for the H Result with Dateformat b (yyyyMMddhhmm)
	@Test
	public void testGenerateUrlMapDatePropertyReplacementHResultFormatB() {
		//boolean result;
		try {
			HarvestResourceUrlMapper urlMapper = new HarvestResourceUrlMapper();
			String uRLMap = "http://www.wctTest.com?" +
					"CreationDate={$HarvestResult.CreationDate,yyyyMMddhhmm}*" ;
			urlMapper.setUrlMap(uRLMap);
			//log.debug(urlMapper.generateUrl(hRsr.getResult(), hRsr.buildDTO()));
			assertTrue(urlMapper.generateUrl(hRsr.getResult(), hRsr.buildDTO()).equals("http://www.wctTest.com?" +
					"CreationDate=200411031020*"));
			
		}
		catch (Exception e)
		{
			String message = e.getClass().toString() + " - " + e.getMessage();
			log.debug(message);
			fail(message);
		}
	}
	// multi property replacement for the H Result with DateformatAB+C
	@Test
	public void testGenerateUrlMapMultiDatePropertyReplacementHResultFormatABC() {
		//boolean result;
		try {
			HarvestResourceUrlMapper urlMapper = new HarvestResourceUrlMapper();
			String uRLMap = "http://www.wctTest.com?" +
					"CreationDate1={$HarvestResult.CreationDate,yyyyMMddhhmm}*" +
					"&CreationDate2={$HarvestResult.CreationDate,dd/MM/yy}" +
					"&CreationDate3={$HarvestResult.CreationDate,EEE dd/MM/yyyy}*" ;
			urlMapper.setUrlMap(uRLMap);
			//log.debug(urlMapper.generateUrl(hRsr.getResult(), hRsr.buildDTO()));
			assertTrue(urlMapper.generateUrl(hRsr.getResult(), hRsr.buildDTO()).equals("http://www.wctTest.com?" +
					"CreationDate1=200411031020*"+
					"&CreationDate2=03/11/04" +
					"&CreationDate3=Wed 03/11/2004*"));
			
		}
		catch (Exception e)
		{
			String message = e.getClass().toString() + " - " + e.getMessage();
			log.debug(message);
			fail(message);
		}
	}
	// mix one property replacement from each
	@Test
	public void testGenerateUrlMapDateDuplicateOnePropertyReplacementFromEach() {
		//boolean result;
		try {
			HarvestResourceUrlMapper urlMapper = new HarvestResourceUrlMapper();
			String uRLMap = "http://www.wctTest.com?" +
					"CreationDate={$HarvestResult.CreationDate}&" +
					"CreationDate1={$HarvestResult.CreationDate}" ;
			urlMapper.setUrlMap(uRLMap);
			//log.debug(urlMapper.generateUrl(hRsr.getResult(), hRsr.buildDTO()));
			assertTrue(urlMapper.generateUrl(hRsr.getResult(), hRsr.buildDTO()).equals("http://www.wctTest.com?" +
					"CreationDate=20041103102011&" +
					"CreationDate1=20041103102011"));
			
		}
		catch (Exception e)
		{
			String message = e.getClass().toString() + " - " + e.getMessage();
			log.debug(message);
			fail(message);
		}
	}
	
	// two of the same properties from H Result
	@Test
	public void testGenerateUrlMapDateOnePropertyReplacementFromResult() {
		//boolean result;
		try {
			HarvestResourceUrlMapper urlMapper = new HarvestResourceUrlMapper();
			String uRLMap = "http://www.wctTest.com?" +
					"CreationDate={$HarvestResult.CreationDate}&" +
					"CreationDate={$HarvestResult.CreationDate}"  ;
			urlMapper.setUrlMap(uRLMap);
			//log.debug(urlMapper.generateUrl(hRsr.getResult(), hRsr.buildDTO()));
			assertTrue(urlMapper.generateUrl(hRsr.getResult(), hRsr.buildDTO()).equals("http://www.wctTest.com?" +
					"CreationDate=20041103102011&" +
					"CreationDate=20041103102011"));
			
		}
		catch (Exception e)
		{
			String message = e.getClass().toString() + " - " + e.getMessage();
			log.debug(message);
			fail(message);
		}
	}
	
	// two of the same properties from H Resource
	@Test
	public void testGenerateUrlMapDateOnePropertyReplacementFromResource() {
		//boolean result;
		try {
			HarvestResourceUrlMapper urlMapper = new HarvestResourceUrlMapper();
			String uRLMap = "http://www.wctTest.com?" +
					"Oid={$HarvestResource.Oid}&" +
					"Oid={$HarvestResource.Oid}"  ;
			urlMapper.setUrlMap(uRLMap);
			//log.debug(urlMapper.generateUrl(hRsr.getResult(), hRsr.buildDTO()));
			assertTrue(urlMapper.generateUrl(hRsr.getResult(), hRsr.buildDTO()).equals("http://www.wctTest.com?" +
					"Oid=1&" +
					"Oid=1"));
			
		}
		catch (Exception e)
		{
			String message = e.getClass().toString() + " - " + e.getMessage();
			log.debug(message);
			fail(message);
		}
	}
	
	// nulls;
	@Test
	public void testNulls() {
		//boolean result;
		try {
			HarvestResourceUrlMapper urlMapper = new HarvestResourceUrlMapper();
			String uRLMap = "http://www.wctTest.com";
			
			urlMapper.setUrlMap(uRLMap);
			assertTrue(urlMapper.generateUrl(hRsr.getResult(), null).equals(HarvestResourceUrlMapper.NULL_RESOURCE_RETURN_VAL));
			
			urlMapper.setUrlMap(null);
			assertTrue(urlMapper.generateUrl(hRsr.getResult(), hRsr.buildDTO()).equals(HarvestResourceUrlMapper.NULL_URLMAP_RETURN_VAL));
			
			urlMapper.setUrlMap(uRLMap);
			assertTrue(urlMapper.generateUrl(null, hRsr.buildDTO()).equals(HarvestResourceUrlMapper.NULL_RESULT_RETURN_VAL));
			
		}
		catch (Exception e)
		{
			String message = e.getClass().toString() + " - " + e.getMessage();
			log.debug(message);
			fail(message);
		}
	}
	
	// test ending with {$HarvestResult.CreationDate does not break it!
	@Test
	public void testGenerateUrlUncloseCreationDateSubtitution() {
		//boolean result;
		try {
			HarvestResourceUrlMapper urlMapper = new HarvestResourceUrlMapper();
			String uRLMap = "http://www.wctTest.com?" +
					"CreationDate={$HarvestResult.CreationDate"   ;
			urlMapper.setUrlMap(uRLMap);
			//log.debug(urlMapper.generateUrl(hRsr.getResult(), hRsr.buildDTO()));
			assertTrue(urlMapper.generateUrl(hRsr.getResult(), hRsr.buildDTO()).equals("http://www.wctTest.com?" +
					"CreationDate={$HarvestResult.CreationDate"));
			
		}
		catch (Exception e)
		{
			String message = e.getClass().toString() + " - " + e.getMessage();
			log.debug(message);
			fail(message);
		}
	}
	@Test
	public void testGenerateUrlCreationDateSubtitutionNoCommer() {
		//boolean result;
		try {
			HarvestResourceUrlMapper urlMapper = new HarvestResourceUrlMapper();
			String uRLMap = "http://www.wctTest.com?" +
					"CreationDate={$HarvestResult.CreationDateddMMyy}"   ;
			urlMapper.setUrlMap(uRLMap);
			//log.debug(urlMapper.generateUrl(hRsr.getResult(), hRsr.buildDTO()));
			assertTrue(urlMapper.generateUrl(hRsr.getResult(), hRsr.buildDTO()).equals("http://www.wctTest.com?" +
					"CreationDate={$HarvestResult.CreationDateddMMyy}"));
			
		}
		catch (Exception e)
		{
			String message = e.getClass().toString() + " - " + e.getMessage();
			log.debug(message);
			fail(message);
		}
	}
	
	//Null Property Values
	@Test
	public void testNullPropertyValues() {
		//boolean result;
		try {
			HarvestResourceUrlMapper urlMapper = new HarvestResourceUrlMapper();
			String uRLMap = "http://www.wctTest.com?" +
			"name={$HarvestResource.Name}" +
			"&Oid={$HarvestResource.Oid}" +
			"&CreationDate={$HarvestResult.CreationDate}" +
			"&DerivedFrom={$HarvestResult.DerivedFrom}" +
			"&RsltOid={$HarvestResult.Oid}" +
			"&ProvenanceNote={$HarvestResult.ProvenanceNote}" ;
		
			
			hRsr.setName(null);
			hRsr.setOid(null);
			hRsr.getResult().setCreationDate(null);
			hRsr.getResult().setDerivedFrom(null);
			hRsr.getResult().setOid(null);
			hRsr.getResult().setProvenanceNote(null);

			
			
			urlMapper.setUrlMap(uRLMap);
			assertTrue(urlMapper.generateUrl(hRsr.getResult(), hRsr.buildDTO()).equals("http://www.wctTest.com?" +
					"name=" +
					"&Oid=" +
					"&CreationDate=" +
					"&DerivedFrom=" +
					"&RsltOid=" +
					"&ProvenanceNote=" ));
	
		}
		catch (Exception e)
		{
			String message = e.getClass().toString() + " - " + e.getMessage();
			log.debug(message);
			fail(message);
		}
	}
	
	
	
}
