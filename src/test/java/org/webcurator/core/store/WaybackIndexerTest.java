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

public class WaybackIndexerTest extends BaseWCTTest<WaybackIndexer>{

	private String archivePath = "src/test/java/org/webcurator/domain/model/core/archiveFiles";
	
	private Long hrOid = 54321L;
	private Long tiOid = 12345L;
	private int harvestNumber = 1;
	
	private final File baseFolder = new File("C:/temp/WaybackIndexerTest");
	private final File inputFolder = new File(baseFolder.getAbsolutePath()+"/input");
	private final File mergedFolder = new File(baseFolder.getAbsolutePath()+"/merged");
	private final File failedFolder = new File(baseFolder.getAbsolutePath()+"/failed");
	
	private class WaybackRunner implements Runnable
	{
		private boolean fail = false;
		private boolean terminate = false;
		public WaybackRunner(boolean fail)
		{
			this.fail = fail;
		}
		
		@Override
		public void run() {
			try
			{
				log.info("Started Wayback Simulator mode="+(fail?"fail":"succeed"));
				while(!terminate)
				{
					synchronized(this)
					{
						File[] inputs = inputFolder.listFiles();
						for(int i = 0; i < inputs.length; i++)
						{
							if(!fail)
							{
								copyFile(inputs[i], new File(mergedFolder.getAbsolutePath()+"/"+inputs[i].getName()));
							}
							else
							{
								copyFile(inputs[i], new File(failedFolder.getAbsolutePath()+"/"+inputs[i].getName()));
							}
						}
					}
					
					Thread.sleep(3000);
				}
				log.info("Terminated Wayback Simulator");
			}
			catch(Exception e)
			{
				fail(e.getMessage());
			}
		}
		
		public void terminate()
		{
			synchronized(this)
			{
				terminate = true;
			}
		}
	}
	
	public WaybackIndexerTest()
	{
		super(WaybackIndexer.class, "");
	}
	
	private void buildFolders()
	{
		baseFolder.mkdirs();
		inputFolder.mkdirs();
		mergedFolder.mkdirs();
		failedFolder.mkdirs();
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
	
	private WaybackRunner startWaybackSim(boolean fail)
	{
		log.info("Attempting to start Wayback Simulator. mode="+(fail?"fail":"succeed"));
		WaybackRunner testRunner = new WaybackRunner(fail); 
		Thread testThread = new Thread(testRunner);
		testThread.start();
		
		return testRunner;
	}
	
	private void stopWaybackSim(WaybackRunner testRunner)
	{
		log.info("Attempting to terminate Wayback Simulator");
		testRunner.terminate();
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
	
	@Before
	public void setUp() throws Exception {
		super.setUp();
		buildFolders();
		testInstance.setTimeout(10000);
		testInstance.setWaittime(1000);
		testInstance.setWaybackFailedFolder(failedFolder.getAbsolutePath());
		testInstance.setWaybackInputFolder(inputFolder.getAbsolutePath());
		testInstance.setWaybackMergedFolder(mergedFolder.getAbsolutePath());
		ArcHarvestResultDTO result = new ArcHarvestResultDTO(hrOid, tiOid, new Date(), harvestNumber, "");
		testInstance.initialise(result, new File(archivePath));
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
	public final void testIndexFilesSuccess() {
		try
		{
			WaybackRunner testRunner =  startWaybackSim(false);
			Long hrOid = testInstance.begin();
			testInstance.indexFiles(hrOid);
			stopWaybackSim(testRunner);
			File[] files = inputFolder.listFiles();
			assertEquals(2, files.length);
		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}

	@Test
	public final void testIndexFilesFail() {
		try
		{
			WaybackRunner testRunner =  startWaybackSim(true);
			Long hrOid = testInstance.begin();
			testInstance.indexFiles(hrOid);
			stopWaybackSim(testRunner);
			File[] files = inputFolder.listFiles();
			assertEquals(2, files.length);
		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}

	@Test
	public final void testRemoveIndex() {
		try
		{
			File[] files = new File(archivePath).listFiles(testInstance.new ARCFilter());
			for(int i = 0; i < files.length; i++)
			{
				WaybackIndexer.MonitoredFile mf = testInstance.new MonitoredFile(files[i]);
				this.copyFile(files[i], new File(inputFolder.getAbsolutePath()+"/"+mf.getVersionedName()));
			}
			
			files = inputFolder.listFiles();
			assertEquals(2, files.length);
			
			Long hrOid = testInstance.begin();
			testInstance.removeIndex(hrOid);
			files = inputFolder.listFiles();
			assertEquals(0, files.length);
		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}

}
