package org.webcurator.core.archive.file;

import static org.junit.Assert.*;
import static org.webcurator.core.archive.Constants.ARC_FILE;
import static org.webcurator.core.archive.Constants.LOG_FILE;
import static org.webcurator.core.archive.Constants.REPORT_FILE;
import static org.webcurator.core.archive.Constants.ROOT_FILE;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.webcurator.core.archive.ArchiveFile;
import org.webcurator.core.util.WCTSoapCall;
import org.webcurator.test.BaseWCTStoreTest;

public class FileArchiveTest extends BaseWCTStoreTest<FileArchive> {

	private static final String BASE = "src/test/java/";
	private static String testData = BASE + "org/webcurator/core/store/arc/archiveFiles";
	private static String TestCARC = "IAH-20080610152724-00000-test.arc.gz";
	private static String TestCWARC = "IAH-20080610152754-00000-test.warc.gz";
	private static String TestCARC_CDX = "IAH-20080610152724-00000-test.cdx";
	private static String TestCWARC_CDX = "IAH-20080610152754-00000-test.cdx";

	private static long targetInstanceOid = 12345;
	private static int harvestResultNumber = 1;

	private static String storeBase = BASE + "org/webcurator/core/archive/file/Store";
	private static String archiveBase = BASE + "org/webcurator/core/archive/file/Archive";
	private static String archiveRepository = archiveBase + "/filestore";

	private static class ARCFilter implements FilenameFilter {
		public boolean accept(File dir, String name) {
			return (name.toLowerCase().endsWith(".arc") || name.toLowerCase().endsWith(".arc.gz")
					|| name.toLowerCase().endsWith(".warc") || name.toLowerCase().endsWith(".warc.gz") || name.toLowerCase()
					.endsWith(".cdx"));
		}
	}

	private static class LogFilter implements FilenameFilter {
		public boolean accept(File dir, String name) {
			return name.toLowerCase().endsWith(".log");
		}
	}

	private static class ReportFilter implements FilenameFilter {
		public boolean accept(File dir, String name) {
			return (name.toLowerCase().endsWith(".txt") || name.toLowerCase().endsWith(".xml"));
		}
	}

	public FileArchiveTest() {
		super(FileArchive.class);
	}

	@BeforeClass
	public static void initialise() throws Exception {
		BaseWCTStoreTest.initialise();

		try {
			// Create the archive folders
			File resultDir = new File(storeBase + "/" + targetInstanceOid + "/" + harvestResultNumber);
			if (!resultDir.exists()) {
				resultDir.mkdirs();

				// copy the test files into them
				copy(testData + "/" + TestCARC, storeBase + "/" + targetInstanceOid + "/" + harvestResultNumber);
				copy(testData + "/" + TestCWARC, storeBase + "/" + targetInstanceOid + "/" + harvestResultNumber);
				copy(testData + "/" + TestCARC_CDX, storeBase + "/" + targetInstanceOid + "/" + harvestResultNumber);
				copy(testData + "/" + TestCWARC_CDX, storeBase + "/" + targetInstanceOid + "/" + harvestResultNumber);
			}

			File logsDir = new File(storeBase + "/" + targetInstanceOid + "/logs");
			if (!logsDir.exists()) {
				logsDir.mkdirs();

				// copy the test files into them
				File logsTestData = new File(testData + "/logs");
				File[] fileList = logsTestData.listFiles(new LogFilter());
				for (int i = 0; i < fileList.length; i++) {
					copy(fileList[i].getAbsolutePath(), logsDir.getAbsolutePath());
				}
			}

			File reportsDir = new File(storeBase + "/" + targetInstanceOid + "/reports");
			if (!reportsDir.exists()) {
				reportsDir.mkdirs();

				// copy the test files into them
				File reportsTestData = new File(testData + "/reports");
				File[] fileList = reportsTestData.listFiles(new ReportFilter());
				for (int i = 0; i < fileList.length; i++) {
					copy(fileList[i].getAbsolutePath(), reportsDir.getAbsolutePath());
				}
			}

			File repositoryDir = new File(archiveRepository);
			if (!repositoryDir.exists()) {
				repositoryDir.mkdirs();
			}
		} catch (IOException e) {
			log.debug("Error initialising file structure: " + e.getMessage());
		}
	}

	@AfterClass
	public static void terminate() throws Exception {
		delDir(new File(storeBase));
		delDir(new File(archiveBase));
		BaseWCTStoreTest.terminate();
	}

	@Before
	public void setUp() throws Exception {
		super.setUp();

		testInstance.setArchiveArcDirectory("arcs");
		testInstance.setArchiveLogDirectory("logs");
		testInstance.setArchiveReportDirectory("reports");
		String reportFiles = "crawl.log,progress-statistics.log,local-errors.log,runtime-errors.log,uri-errors.log,hosts-report.txt,mimetype-report.txt,responsecode-report.txt,seeds-report.txt,processors-report.txt";
		testInstance.setArchiveLogReportFiles(reportFiles);
		testInstance.setArchiveRepository(archiveRepository);
	}

	@Test
	public final void testSubmitToArchive() throws Exception {
		String targetInstanceOid = "12345";
		String SIP = "";
		Map xAttributes = null;

		ArrayList<ArchiveFile> fileList = new ArrayList<ArchiveFile>();

		// Get log files
		for (File f : getLogFiles()) {
			fileList.add(new ArchiveFile(f, LOG_FILE));
		}
		// Get report files
		for (File f : getReportFiles()) {
			if (f.getName().endsWith("order.xml")) {
				fileList.add(new ArchiveFile(f, ROOT_FILE));
			} else {
				fileList.add(new ArchiveFile(f, REPORT_FILE));
			}
		}
		// Get arc files
		for (File f : getARCFiles()) {
			fileList.add(new ArchiveFile(f, ARC_FILE));
		}

		String archiveIID = testInstance.submitToArchive(targetInstanceOid, SIP, xAttributes, fileList);

		assertEquals(archiveIID, targetInstanceOid);
		checkReportFiles();
		checkLogFiles();
		checkArcFiles();
		checkBaseFiles();

	}

	private List<File> getARCFiles() {
		List<File> files = new ArrayList<File>();
		File logsTestData = new File(storeBase + "/" + targetInstanceOid + "/" + harvestResultNumber);
		File[] fileList = logsTestData.listFiles(new ARCFilter());
		for (int i = 0; i < fileList.length; i++) {
			files.add(fileList[i]);
		}
		return files;
	}

	private void checkReportFiles() {
		File archivedReports = new File(archiveRepository + "/" + targetInstanceOid + "/reports");
		File[] fileList = archivedReports.listFiles(new ReportFilter());
		assertEquals(9, fileList.length);
	}

	private void checkLogFiles() {
		File archivedReports = new File(archiveRepository + "/" + targetInstanceOid + "/logs");
		File[] fileList = archivedReports.listFiles(new LogFilter());
		assertEquals(2, fileList.length);
	}

	private void checkArcFiles() {
		//Note that this test has been amended to reflect what the code does - perhaps
		//the code is wrong, but it's the best we can do. It seems though that this test 
		//class never actually worked originally.

		//Old test:
		//File archivedReports = new File(archiveRepository + "/" + targetInstanceOid + "/" + harvestResultNumber);
		
		File archivedReports = new File(archiveRepository + "/" + targetInstanceOid + "/arcs");
		File[] fileList = archivedReports.listFiles(new ARCFilter());
		assertEquals(4, fileList.length);
	}

	private void checkBaseFiles() {
		File archivedBase = new File(archiveRepository + "/" + targetInstanceOid);
		File[] fileList = archivedBase.listFiles(new ReportFilter());
		assertEquals(2, fileList.length);
	}

	private List<File> getReportFiles() {
		List<File> files = new ArrayList<File>();
		File logsTestData = new File(storeBase + "/" + targetInstanceOid + "/reports");
		File[] fileList = logsTestData.listFiles(new ReportFilter());
		for (int i = 0; i < fileList.length; i++) {
			files.add(fileList[i]);
		}
		return files;
	}

	private List<File> getLogFiles() {
		List<File> files = new ArrayList<File>();
		File logsTestData = new File(storeBase + "/" + targetInstanceOid + "/logs");
		File[] fileList = logsTestData.listFiles(new LogFilter());
		for (int i = 0; i < fileList.length; i++) {
			files.add(fileList[i]);
		}
		return files;
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
