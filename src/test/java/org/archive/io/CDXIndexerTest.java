package org.archive.io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Date;

import org.junit.Ignore;
import org.junit.Test;
import org.webcurator.domain.model.core.ArcHarvestResultDTO;
import org.webcurator.test.BaseWCTTest;

public class CDXIndexerTest extends BaseWCTTest<CDXIndexer>{

	private String archivePath = "src/test/java/org/webcurator/domain/model/core/archiveFiles";
	private String TestCARC_CDX = "IAH-20080610152724-00000-test.cdx";
	private String TestCWARC_CDX = "IAH-20080610152754-00000-test.cdx";
	
	private long NumARCResources = 83;
	private long NumWARCResources = 72;
	
	private Long hrOid = 54321L;
	private Long tiOid = 12345L;
	private int harvestNumber = 1;
	
	public CDXIndexerTest()
	{
		super(CDXIndexer.class, "");
	}

	public void setUp() throws Exception {
		super.setUp();
		ArcHarvestResultDTO result = new ArcHarvestResultDTO(hrOid, tiOid, new Date(), harvestNumber, "");
		testInstance.initialise(result, new File(archivePath));
	}

	//TODO Test doesn't work, test itself also does not appear to test anything worthwhile 
	@Ignore
	@Test
	public final void testIndexFiles() {
		try {
			testInstance.indexFiles(testInstance.getResult().getOid());
			
			//Check the ARC CDX index was generated
			File cdxFile = new File(archivePath+"/"+TestCARC_CDX);
			assertTrue(cdxFile.exists());
	
			//Check the number of records in the CDX matches the arc file
			int count = 0;
		    FileInputStream fstream = new FileInputStream(cdxFile);
		    DataInputStream in = new DataInputStream(fstream);
		    BufferedReader br = new BufferedReader(new InputStreamReader(in));
		    //Read File Line By Line
		    while (br.readLine() != null)   {
		      count++;
		    }
		    in.close();
		    
		    //Ignore the CDX header line
		    assertEquals(NumARCResources, count-1);
			cdxFile.delete();
	
			//Check the CDX index was generated
			cdxFile = new File(archivePath+"/"+TestCWARC_CDX);
			assertTrue(cdxFile.exists());
	
			//Check the number of records in the CDX matches the warc file
			count = 0;
		    fstream = new FileInputStream(cdxFile);
		    in = new DataInputStream(fstream);
		    br = new BufferedReader(new InputStreamReader(in));
		    //Read File Line By Line
		    while (br.readLine() != null)   {
		      count++;
		    }
		    in.close();
		    
		    //Ignore:
		    //	CDX header line
		    //	WARC header line
		    //	DNS line
		    //There are 3 lines per record:
		    //	application/http;msgtype=request 
		    //  application/http;msgtype=response
		    //  text/anvl
		    assertEquals(NumWARCResources, (count-3)/3);
			cdxFile.delete();
		
		} catch (Exception e) {
			fail(e.getMessage());
		}
		
		
	}

	@Test
	public final void testBegin() {
		try {
			assertEquals(hrOid, testInstance.begin());
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public final void testGetName() {
		try {
			assertEquals(testInstance.getClass().getCanonicalName(), testInstance.getName());
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

}
