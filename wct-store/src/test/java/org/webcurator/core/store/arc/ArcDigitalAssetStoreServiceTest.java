package org.webcurator.core.store.arc;

import static org.junit.Assert.*;

import java.io.*;
import java.util.*;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.webcurator.test.BaseWCTStoreTest;
import org.webcurator.core.store.MockIndexer;
import org.webcurator.core.archive.MockArchive;
import org.webcurator.core.exceptions.DigitalAssetStoreException;
import org.webcurator.domain.model.core.*;
import org.apache.commons.httpclient.Header;

public class ArcDigitalAssetStoreServiceTest extends BaseWCTStoreTest<ArcDigitalAssetStoreService> {

	private static String baseDir = "src/test/java/org/webcurator/core/store/arc/archiveFiles";
	private static String TestCARC = "IAH-20080610152724-00000-test.arc.gz";
	private static String TestCWARC = "IAH-20080610152754-00000-test.warc.gz";

	private static long targetInstanceOid = 12345;
	private static int harvestResultNumber = 1;

	private class ARCFilter implements FilenameFilter {
		public boolean accept(File dir, String name) {
			return (name.toLowerCase().endsWith(".arc") || name.toLowerCase().endsWith(".arc.gz")
					|| name.toLowerCase().endsWith(".warc") || name.toLowerCase().endsWith(".warc.gz"));
		}
	}

	public ArcDigitalAssetStoreServiceTest() {
		super(ArcDigitalAssetStoreService.class);
	}

	@BeforeClass
	public static void initialise() throws Exception {
		BaseWCTStoreTest.initialise();

		// Create the archive folders
		File resultDir = new File(baseDir + "/" + targetInstanceOid + "/" + harvestResultNumber);
		if (!resultDir.exists()) {
			resultDir.mkdirs();

			// copy the test files into them
			copy(baseDir + "/" + TestCARC, baseDir + "/" + targetInstanceOid + "/" + harvestResultNumber);
			copy(baseDir + "/" + TestCWARC, baseDir + "/" + targetInstanceOid + "/" + harvestResultNumber);
		}
	}

	public void setUp() throws Exception {
		super.setUp();
		testInstance.setBaseDir(baseDir);
		testInstance.setArchive(new MockArchive());
		testInstance.setDasFileMover(new MockDasFileMover());
		testInstance.setIndexer(new MockIndexer());
	}

	@Test
	public final void testARCGetResource() throws Exception {
		long length = 7109;
		long offset = 6865980;
		String name = "http://webcurator.sourceforge.net/contact.shtml";
		HarvestResourceDTO dto = new ArcHarvestResourceDTO(targetInstanceOid, harvestResultNumber, 54321, name, length, offset, 0,
				TestCARC, 200, true);

		File res = testInstance.getResource(new Long(targetInstanceOid).toString(), harvestResultNumber, dto);
		assertTrue(res != null);
		assertTrue(res.length() == length);
	}

	@Test
	public final void testWARCGetResource() throws Exception {
		long reslength = 7109;
		long length = 7250;
		long offset = 1723422;
		String name = "http://webcurator.sourceforge.net/contact.shtml";
		HarvestResourceDTO dto = new ArcHarvestResourceDTO(targetInstanceOid, harvestResultNumber, 54321, name, length, offset, 0,
				TestCWARC, 200, true);

		File res = testInstance.getResource(new Long(targetInstanceOid).toString(), harvestResultNumber, dto);
		assertTrue(res != null);
		assertTrue(res.length() == reslength);
	}

	@Test
	public final void testARCGetSmallResource() throws Exception {
		long length = 7109;
		long offset = 6865980;
		String name = "http://webcurator.sourceforge.net/contact.shtml";
		HarvestResourceDTO dto = new ArcHarvestResourceDTO(targetInstanceOid, harvestResultNumber, 54321, name, length, offset, 0,
				TestCARC, 200, true);

		byte[] res = testInstance.getSmallResource(new Long(targetInstanceOid).toString(), harvestResultNumber, dto);
		assertTrue(res != null);
		assertTrue(res.length == length);
	}

	@Test
	public final void testWARCGetSmallResource() throws Exception {
		long reslength = 7109;
		long length = 7250;
		long offset = 1723422;
		String name = "http://webcurator.sourceforge.net/contact.shtml";
		HarvestResourceDTO dto = new ArcHarvestResourceDTO(targetInstanceOid, harvestResultNumber, 54321, name, length, offset, 0,
				TestCWARC, 200, true);

		byte[] res = testInstance.getSmallResource(new Long(targetInstanceOid).toString(), harvestResultNumber, dto);
		assertTrue(res != null);
		assertTrue(res.length == reslength);
	}

	@Test
	public final void testARCGetHeaders() throws Exception {
		long length = 7109;
		long offset = 6865980;
		String name = "http://webcurator.sourceforge.net/contact.shtml";
		HarvestResourceDTO dto = new ArcHarvestResourceDTO(targetInstanceOid, harvestResultNumber, 54321, name, length, offset, 0,
				TestCARC, 200, true);

		Header[] headers = testInstance.getHeaders(new Long(targetInstanceOid).toString(), harvestResultNumber, dto);
		assertTrue(headers != null);
		assertTrue(headers.length == 4);
	}

	@Test
	public final void testWARCGetHeaders() throws Exception {
		long length = 7250;
		long offset = 1723422;
		String name = "http://webcurator.sourceforge.net/contact.shtml";
		HarvestResourceDTO dto = new ArcHarvestResourceDTO(targetInstanceOid, harvestResultNumber, 54321, name, length, offset, 0,
				TestCWARC, 200, true);

		Header[] headers = testInstance.getHeaders(new Long(targetInstanceOid).toString(), harvestResultNumber, dto);
		assertTrue(headers != null);
		assertTrue(headers.length == 4);
	}

	/**
	 * TODO This test needs to be expanded to actually test more than something-in, something-out
	 * 
	 * @throws Exception
	 */
	@Ignore  //Ignored because it is unreliable and also tests essentially no logic.
	@Test
	public final void testCopyAndPrune() throws Exception {
		String targetInstanceName = new Long(targetInstanceOid).toString();
		String uri = "http://webcurator.sourceforge.net/contact.shtml";

		File destDir = new File(baseDir, targetInstanceName + "/" + (harvestResultNumber + 1));
		delDir(destDir);

		List<String> urisToDelete = new ArrayList<String>();
		List<HarvestResourceDTO> hrsToImport = new ArrayList<HarvestResourceDTO>();
		urisToDelete.add(uri);

		HarvestResultDTO dto = testInstance.copyAndPrune(targetInstanceName, harvestResultNumber, harvestResultNumber + 1,
				urisToDelete, hrsToImport);
		assertNotNull(dto);

		// Ensure the destination directory exists.
		assertTrue(destDir.exists());

		// Get all the ARC/WARC files from the dest dir.
		File[] arcFiles = destDir.listFiles(new ARCFilter());
		assertEquals(1, arcFiles.length);

		delDir(destDir);
	}

	private static void copy(String fromFileName, String toFileName) throws IOException {
		File fromFile = new File(fromFileName);
		File toFile = new File(toFileName);

		if (!fromFile.exists())
			throw new IOException("FileCopy: " + "no such source file: " + fromFileName);
		if (!fromFile.isFile())
			throw new IOException("FileCopy: " + "can't copy directory: " + fromFileName);
		if (!fromFile.canRead())
			throw new IOException("FileCopy: " + "source file is unreadable: " + fromFileName);

		if (toFile.isDirectory()) {
			toFile = new File(toFile, fromFile.getName());
		}

		if (toFile.exists()) {
			if (!toFile.canWrite())
				throw new IOException("FileCopy: " + "destination file is unwriteable: " + toFileName);
		} else {
			String parent = toFile.getParent();
			if (parent == null)
				parent = System.getProperty("user.dir");
			File dir = new File(parent);
			if (!dir.exists())
				throw new IOException("FileCopy: " + "destination directory doesn't exist: " + parent);
			if (dir.isFile())
				throw new IOException("FileCopy: " + "destination is not a directory: " + parent);
			if (!dir.canWrite())
				throw new IOException("FileCopy: " + "destination directory is unwriteable: " + parent);
		}

		FileInputStream from = null;
		FileOutputStream to = null;
		try {
			from = new FileInputStream(fromFile);
			to = new FileOutputStream(toFile);
			byte[] buffer = new byte[4096];
			int bytesRead;

			while ((bytesRead = from.read(buffer)) != -1)
				to.write(buffer, 0, bytesRead); // write
		} finally {
			if (from != null) {
				try {
					from.close();
				} catch (IOException e) {
					log.debug("Failed to close 'from' stream: " + e.getMessage());
				}
			}
			if (to != null) {
				try {
					to.close();
				} catch (IOException e) {
					log.debug("Failed to close 'to' stream: " + e.getMessage());
				}
			}
		}
	}

	private static void delDir(File directory) {
		// Ensure the destination directory exists.
		if (directory.exists()) {
			// Get all the files from the directory.
			File[] files = directory.listFiles();
			for (int i = 0; i < files.length; i++) {
				File file = files[i];
				if (file.isDirectory()) {
					delDir(file);
				} else {
					if (file.delete()) {
						log.debug("Deleted File: " + file.getAbsolutePath());
					} else {
						log.debug("Failed to delete File: " + file.getAbsolutePath());
					}
				}
			}

			if (directory.delete()) {
				log.debug("Deleted Directory: " + directory.getAbsolutePath());
			} else {
				log.debug("Failed to delete Directory: " + directory.getAbsolutePath());
			}
		}
	}
}
