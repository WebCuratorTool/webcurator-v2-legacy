package org.webcurator.core.store;

import static org.junit.Assert.*;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.webcurator.domain.model.core.ArcHarvestResultDTO;
import org.webcurator.test.BaseWCTTest;

public class CrawlLogIndexerTest extends BaseWCTTest<CrawlLogIndexer>{

	private String testCrawlLogFile = "src/test/java/org/webcurator/core/store/logs/crawl.log";
	
	private Long hrOid = 54321L;
	private Long tiOid = 12345L;
	private int harvestNumber = 1;

	private final String tmpDir = System.getProperty("java.io.tmpdir");
	
	private final File baseFolder = new File(tmpDir + "/CrawlLogIndexerTest");
	private final File archiveFolder = new File(tmpDir + "/CrawlLogIndexerTest/1");
	private final File logsFolder = new File(tmpDir + "/CrawlLogIndexerTest/logs");
	
	@SuppressWarnings("unused")
	private class CrawlLogRunner implements Runnable
	{
		
		@Override
		public void run() {
		}
		
		public void terminate()
		{
		}
	}
	
	public CrawlLogIndexerTest()
	{
		super(CrawlLogIndexer.class, "");
	}
	
	private void buildFolders()
	{
		baseFolder.mkdirs();
		logsFolder.mkdirs();
		archiveFolder.mkdirs();
		
		try {
			copyFile(new File(testCrawlLogFile), new File(logsFolder.getAbsolutePath()+"//crawl.log"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void copyFile(File source, File destination) throws IOException
	{
		int BUFFER_SIZE = 64000;
		byte[] buffer = new byte[BUFFER_SIZE];
		int bytesRead = 0;

		InputStream is = null;
		OutputStream os = null;

		try {
			is = new BufferedInputStream(new FileInputStream(source));
			os = new BufferedOutputStream(new FileOutputStream(destination));

			while ((bytesRead = is.read(buffer)) > 0) {
				os.write(buffer, 0, bytesRead);
			}
		} 
		finally {
			if(is != null) is.close();
			if(os != null) os.close();
		}
	}
	
	private boolean deleteAll(File f)
	{
		if(f.isDirectory() && f.exists())
		{
			File[] fileList = f.listFiles();
			for(int i = 0; i <fileList.length; i++)
			{
				deleteAll(fileList[i]);
			}
		}

		return f.delete();
	}
	
	@Before
	public void setUp() throws Exception {
		super.setUp();
		buildFolders();
		testInstance.setLogsSubFolder("logs");
		testInstance.setCrawlLogFileName("crawl.log");
		testInstance.setSortedLogFileName("sortedcrawl.log");
		testInstance.setStrippedLogFileName("strippedcrawl.log");
		ArcHarvestResultDTO result = new ArcHarvestResultDTO(hrOid, tiOid, new Date(), harvestNumber, "");
		testInstance.initialise(result, archiveFolder);
	}

	@After
	public void tearDown() throws Exception {
		super.tearDown();
		deleteAll(baseFolder);
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

	@Test
	public final void testIndexFiles() {
		try
		{
			Long hrOid = testInstance.begin();
			testInstance.indexFiles(hrOid);
			File[] files = logsFolder.listFiles();
			assertEquals(3, files.length);
		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}

}
