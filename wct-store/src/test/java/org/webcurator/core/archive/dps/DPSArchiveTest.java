package org.webcurator.core.archive.dps;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.webcurator.core.archive.Constants.ACCESS_RESTRICTION;
import static org.webcurator.core.archive.Constants.REFERENCE_NUMBER;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nz.govt.natlib.ndha.wctdpsdepositor.CustomDepositField;
import nz.govt.natlib.ndha.wctdpsdepositor.CustomDepositFormMapping;
import nz.govt.natlib.ndha.wctdpsdepositor.DpsDepositProxy;
import org.junit.Before;
import org.junit.Test;
import org.webcurator.core.archive.ArchiveFile;
import org.webcurator.core.archive.dps.DPSArchive.DepData;
import org.webcurator.core.archive.dps.DpsDepositFacade.DepositResult;
import org.webcurator.domain.model.core.CustomDepositFormCriteriaDTO;
import org.webcurator.domain.model.core.CustomDepositFormResultDTO;

public class DPSArchiveTest {

	private DpsDepositProxy mockDpsDepositFacade;
	private File[] testFiles = new File[] {
			new File("FileOne"),
			new File("FileTwo")
	};
	private long expectedSipId = 100;

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testGetDpsDepositFacade() {
		DPSArchive archiver = new DPSArchive();
		try {
			/*
			 * This method will throw ClassNotFoundException or NoClassDefFoundError
			 * if the DpsDepositProxy is not in the class path. In that case, don't
			 * fail the test, but print a message and the Throwable details.
			 */
			DpsDepositFacade facade = archiver.getDpsDepositFacade();
			assertNotNull(facade);
			assertEquals("nz.govt.natlib.ndha.wctdpsdepositor.DpsDepositProxy", facade.getClass().getName());
		} catch (Throwable e) {
			System.out.println("Error loading the DpsDepositFacade implementation");
			e.printStackTrace();
		}
	}

	@Test
	public void testGetCustomDepositFormDetails() throws Exception {
		String customFormUrl_eJournal = "/some/nice/url/eJournal";
		String customFormUrl_eManuscript = "/some/nice/url/eManuscript";
		String customFormUrl_Blog = "/some/nice/url/blog";
		String customFormUrls = customFormUrl_eJournal + ", " + customFormUrl_eManuscript + ", " + customFormUrl_Blog;
		DPSArchive archiver;
		CustomDepositFormResultDTO result;
		CustomDepositFormCriteriaDTO criteria; 

		// Agency ID is different from (ignoring case) the HTML serial agencies configured in DPS Archive.
		// Target DC type of harvest is equal to (ignore case) one of the HTML serial types configured in DPS Archive 
		// Expect true for the isCustomDepositFormRequired(), a proper value for the URL of custom form.
		archiver = new DPSArchive();
		archiver.setCustomDepositFormURLsForHtmlSerialIngest(customFormUrls);
		archiver.setTargetDCTypesOfHtmlSerials("HTML Serial Type 1 - eJournals, HTML Serial Type 2 - Manuscripts , HTML Serial Type 3 - Blogs    ");
		archiver.setAgenciesResponsibleForHtmlSerials("   Electronic Journals   ,  Electronic Serials     ");
		criteria = new CustomDepositFormCriteriaDTO();
		criteria.setAgencyName("some agency");
		criteria.setTargetType("HTML Serial Type 2 - maNuScripts");
		result = archiver.getCustomDepositFormDetails(criteria);
		assertNotNull(result);
		assertEquals(true, result.isCustomDepositFormRequired());
		assertEquals(customFormUrl_eManuscript, result.getUrlForCustomDepositForm());
		assertNull(result.getHTMLForCustomDepositForm());

		// Agency ID is same as (ignoring case) one of the HTML serial agencies configured in DPS Archive
		// Target DC type of harvest is different from (ignoring case) the HTML serial types configured in DPS Archive
		// Expect true for the isCustomDepositFormRequired(), and "invalid dc type JSP" for the URL of custom form.
		archiver = new DPSArchive();
		archiver.setCustomDepositFormURLsForHtmlSerialIngest(customFormUrls);
		archiver.setTargetDCTypesOfHtmlSerials("HTML Serial Type 1 - eJournals, HTML Serial Type 2 - Manuscripts , HTML Serial Type 3 - Blogs    ");
		archiver.setAgenciesResponsibleForHtmlSerials("   Electronic Journals   ,  Electronic Serials     ");
		archiver.setRestrictHTMLSerialAgenciesToHTMLSerialTypes(true);
		criteria = new CustomDepositFormCriteriaDTO();
		criteria.setAgencyName("eLectroniC SeRials");
		criteria.setTargetType("some target DC type");
		result = archiver.getCustomDepositFormDetails(criteria);
		assertNotNull(result);
		assertEquals(true, result.isCustomDepositFormRequired());
		assertEquals("/wct-store/customDepositForms/rosetta_custom_deposit_form_invalid_dctype.jsp", result.getUrlForCustomDepositForm());
		assertNull(result.getHTMLForCustomDepositForm());

		// Agency ID is same as (ignoring case) one of the HTML serial agencies configured in DPS Archive
		// Target DC type of harvest is equal to (ignore case) one of the HTML serial types configured in DPS Archive
		// Expect true for the isCustomDepositFormRequired(), a proper value for the URL of custom form.
		archiver = new DPSArchive();
		archiver.setCustomDepositFormURLsForHtmlSerialIngest(customFormUrls);
		archiver.setTargetDCTypesOfHtmlSerials("HTML Serial Type 1 - eJournals, HTML Serial Type 2 - Manuscripts , HTML Serial Type 3 - Blogs    ");
		archiver.setAgenciesResponsibleForHtmlSerials("   Electronic Journals   ,  Electronic Serials     ");
		criteria = new CustomDepositFormCriteriaDTO();
		criteria.setAgencyName("eLectroniC SeRials");
		criteria.setTargetType("HTML Serial Type 2 - maNuScripts");
		result = archiver.getCustomDepositFormDetails(criteria);
		assertNotNull(result);
		assertEquals(true, result.isCustomDepositFormRequired());
		assertEquals(customFormUrl_eManuscript, result.getUrlForCustomDepositForm());
		assertNull(result.getHTMLForCustomDepositForm());

		// Agency ID and target DC type are different from the HTML serial types and agencies configured in DPS Archive
		// Expect false for the isCustomDepositFormRequired(), null for the URL and HTML of custom form.
		archiver = new DPSArchive();
		archiver.setCustomDepositFormURLsForHtmlSerialIngest(customFormUrls);
		archiver.setTargetDCTypesOfHtmlSerials("HTML Serial Type 1 - eJournals, HTML Serial Type 2 - Manuscripts , HTML Serial Type 3 - Blogs    ");
		archiver.setAgenciesResponsibleForHtmlSerials("   Electronic Journals   ,  Electronic Serials     ");
		criteria = new CustomDepositFormCriteriaDTO();
		criteria.setAgencyName("some agency");
		criteria.setTargetType("some target DC type");
		result = archiver.getCustomDepositFormDetails(criteria);
		assertNotNull(result);
		assertEquals(false, result.isCustomDepositFormRequired());
		assertNull(result.getUrlForCustomDepositForm());
		assertNull(result.getHTMLForCustomDepositForm());

		// Null criteria object
		archiver = new DPSArchive();
		archiver.setCustomDepositFormURLsForHtmlSerialIngest(customFormUrls);
		archiver.setTargetDCTypesOfHtmlSerials("HTML Serial Type 1 - eJournals, HTML Serial Type 2 - Manuscripts , HTML Serial Type 3 - Blogs    ");
		archiver.setAgenciesResponsibleForHtmlSerials("   Electronic Journals   ,  Electronic Serials     ");
		criteria = null;
		result = archiver.getCustomDepositFormDetails(criteria);
		assertNotNull(result);
		assertEquals(false, result.isCustomDepositFormRequired());
		assertNull(result.getUrlForCustomDepositForm());
		assertNull(result.getHTMLForCustomDepositForm());
	}

	@Test
	public void testValidateMaterialFlowAssociation() {
		DPSArchive archiver;

		// No material flows are associated with the producer
		archiver = new DPSArchive() {
			public DepData[] getMaterialFlows(String producerID) {
				return null;
			}
		};
		archiver.setTargetDCTypesOfHtmlSerials("eJournal, eManuscript, eSerial");
		archiver.setMaterialFlowsOfHtmlSerials("1111, 2222, 3333");
		assertFalse(archiver.validateMaterialFlowAssociation("1234", "eSerial"));

		// The producer is not associated with the material flow of the requested target DC type
		archiver = new DPSArchive() {
			public DepData[] getMaterialFlows(String producerID) {
				DepData[] depData = new DepData[10];
				for (int i = 0; i < depData.length; i++) {
					depData[i] = mockDepData("" + i, "Description for " + i);
				}
				return depData;
			}
		};
		archiver.setTargetDCTypesOfHtmlSerials("eJournal, eManuscript, eSerial");
		archiver.setMaterialFlowsOfHtmlSerials("1111, 2222, 3333");
		assertFalse(archiver.validateMaterialFlowAssociation("1234", "eSerial"));

		// Requested target DC type is not of an HTML serial type
		assertFalse(archiver.validateMaterialFlowAssociation("1234", "someOtherDcType"));

		// The perfect situation - the producer is associated with the correct material flow
		archiver.setMaterialFlowsOfHtmlSerials("1111, 2222, 5");
		assertTrue(archiver.validateMaterialFlowAssociation("1234", "eSerial"));
	}

	@Test
	public void testToListOfLowerCaseValues() {
		List<String> output;

		// 3 tokens, some with space
		output = DPSArchive.toListOfLowerCaseValues("HTML Serial Type 1 - eJournals, HTML Serial Type 2 - Manuscripts , HTML Serial Type 3 - Blogs    ");
		assertNotNull(output);
		assertEquals(3, output.size());
		assertEquals("html serial type 1 - ejournals", output.get(0));
		assertEquals("html serial type 2 - manuscripts", output.get(1));
		assertEquals("html serial type 3 - blogs", output.get(2));

		// 1 token - no leading/trailing space
		output = DPSArchive.toListOfLowerCaseValues("Electronic Journals and Serials");
		assertNotNull(output);
		assertEquals(1, output.size());
		assertEquals("electronic journals and serials", output.get(0));

		// 1 token - with leading/trailing space
		output = DPSArchive.toListOfLowerCaseValues("   Electronic Journals and Serials      	   		");
		assertNotNull(output);
		assertEquals(1, output.size());
		assertEquals("electronic journals and serials", output.get(0));

		// Null string
		output = DPSArchive.toListOfLowerCaseValues(null);
		assertNotNull(output);
		assertEquals(0, output.size());

		// empty string content
		output = DPSArchive.toListOfLowerCaseValues("     		    ");
		assertNotNull(output);
		assertEquals(0, output.size());

		// comma-separated set of empty string contents
		output = DPSArchive.toListOfLowerCaseValues("     		    ,  		        ,    	    ");
		assertNotNull(output);
		assertEquals(0, output.size());
	}

	@Test
	public void testPopulateDepositParameterFromFields_webHarvest() {
		DPSArchive archiver = new DPSArchive();
		setVariousParameters(archiver);

		Map<String, String> attributes = mockDasAttributeMap();
		String finalSIP = "someFinalSIPXml";
		String targetInstanceOID = "112233";

		Map<String, String> parameters = archiver.populateDepositParameterFromFields(attributes, finalSIP, targetInstanceOID);

		assertEquals("aDpsUserInstitution", parameters.get(DpsDepositFacade.DPS_INSTITUTION));
		assertEquals("aDpsUserName", parameters.get(DpsDepositFacade.DPS_USER_NAME));
		assertEquals("aDpsUserPassword", parameters.get(DpsDepositFacade.DPS_PASSWORD));
		assertEquals("aFtpHost", parameters.get(DpsDepositFacade.FTP_HOST));
		assertEquals("aFtpPassword", parameters.get(DpsDepositFacade.FTP_PASSWORD));
		assertEquals("aFtpUserName", parameters.get(DpsDepositFacade.FTP_USER_NAME));
		assertEquals("aFtpDirectory", parameters.get(DpsDepositFacade.FTP_DIRECTORY));
		assertEquals("aMaterialFlowId", parameters.get(DpsDepositFacade.MATERIAL_FLOW_ID));
		assertEquals("aPdsUrl", parameters.get(DpsDepositFacade.PDS_URL));
		assertEquals("aProducerId", parameters.get(DpsDepositFacade.PRODUCER_ID));
		assertEquals("http://someserver.natlib.govt.nz:80000/dpsws/deposit/DepositWebServices?wsdl", parameters.get(DpsDepositFacade.DPS_WSDL_URL));
		assertEquals("112233", parameters.get(DpsDepositFacade.TARGET_INSTANCE_ID));
		assertEquals("someFinalSIPXml", parameters.get(DpsDepositFacade.WCT_METS_XML_DOCUMENT));
		assertEquals("1234567890", parameters.get(DpsDepositFacade.ILS_REFERENCE));
		assertEquals("anAccessRestriction", parameters.get(DpsDepositFacade.ACCESS_RESTRICTION));
		assertEquals("TraditionalWebHarvest", parameters.get(DpsDepositFacade.HARVEST_TYPE));
	}

	@Test
	public void testPopulateDepositParameterFromFields_htmlSerialHarvest() {
		DPSArchive archiver = new DPSArchive();
		setVariousParameters(archiver);
		archiver.setTargetDCTypesOfHtmlSerials("eJournal, HtmlSerialHarvest, eSerial");
		archiver.setMaterialFlowsOfHtmlSerials("1111, anHtmlSerialMaterialFlowId, 3333");
		archiver.setIeEntityTypesOfHtmlSerials("OneHTMLSerialIeEntityType, TwoHTMLSerialIeEntityType, ThreeHTMLSerialIeEntityType");
		archiver.setCustomDepositFormURLsForHtmlSerialIngest("/wct-store/customDepositForms/eJournal_form.jsp, /wct-store/customDepositForms/anHtmlSerialTargetDcType_form.jsp, /wct-store/customDepositForms/eSerial_form.jsp");

		// Custom Deposit Form Mapping
		List<CustomDepositField> customDepositFieldsForTest = new ArrayList<CustomDepositField>();
		customDepositFieldsForTest.add(new CustomDepositField("customDepositForm_bibliographicCitation", "DctermsBibliographicCitation", "bibliographicCitation", "dcterms"));
		customDepositFieldsForTest.add(new CustomDepositField("customDepositForm_dctermsAvailable", "DctermsAvailable", "available", "dcterms"));

		Map<String, List<CustomDepositField>> customDepositFieldMap = new HashMap<String, List<CustomDepositField>>();
		customDepositFieldMap.put("/wct-store/customDepositForms/anHtmlSerialTargetDcType_form.jsp", customDepositFieldsForTest);

		CustomDepositFormMapping testCustomDepositFormMapping = new CustomDepositFormMapping();
		testCustomDepositFormMapping.setCustomDepositFormFieldMaps(customDepositFieldMap);
		archiver.setCustomDepositFormMapping(testCustomDepositFormMapping);


		Map<String, String> attributes = mockDasAttributeMapForHtmlSerials();
		String finalSIP = "someFinalSIPXml";
		String targetInstanceOID = "112233";

		Map<String, String> parameters = archiver.populateDepositParameterFromFields(attributes, finalSIP, targetInstanceOID);

		assertEquals("aDpsUserInstitution", parameters.get(DpsDepositFacade.DPS_INSTITUTION));
		assertEquals("anHtmlSerialUserName", parameters.get(DpsDepositFacade.DPS_USER_NAME));
		assertEquals("anHtmlSerialUserPassword", parameters.get(DpsDepositFacade.DPS_PASSWORD));
		assertEquals("aFtpHost", parameters.get(DpsDepositFacade.FTP_HOST));
		assertEquals("aFtpPassword", parameters.get(DpsDepositFacade.FTP_PASSWORD));
		assertEquals("aFtpUserName", parameters.get(DpsDepositFacade.FTP_USER_NAME));
		assertEquals("aFtpDirectory", parameters.get(DpsDepositFacade.FTP_DIRECTORY));
		assertEquals("anHtmlSerialMaterialFlowId", parameters.get(DpsDepositFacade.MATERIAL_FLOW_ID));
		assertEquals("TwoHTMLSerialIeEntityType", parameters.get(DpsDepositFacade.IE_ENTITY_TYPE));
		assertEquals("aPdsUrl", parameters.get(DpsDepositFacade.PDS_URL));
		assertEquals("anHtmlSerialProducerId", parameters.get(DpsDepositFacade.PRODUCER_ID));
		assertEquals("http://someserver.natlib.govt.nz:80000/dpsws/deposit/DepositWebServices?wsdl", parameters.get(DpsDepositFacade.DPS_WSDL_URL));
		assertEquals("112233", parameters.get(DpsDepositFacade.TARGET_INSTANCE_ID));
		assertEquals("someFinalSIPXml", parameters.get(DpsDepositFacade.WCT_METS_XML_DOCUMENT));
		assertEquals("1234567890", parameters.get(DpsDepositFacade.ILS_REFERENCE));
		assertEquals("anAccessRestriction", parameters.get(DpsDepositFacade.ACCESS_RESTRICTION));
		assertEquals("anHtmlSerialBibCitation", parameters.get(DpsDepositFacade.DCTERMS_BIBLIOGRAPHIC_CITATION));
		assertEquals("anHtmlSerialDctermsAvailable", parameters.get(DpsDepositFacade.DCTERMS_AVAILABLE));
		assertEquals("HtmlSerialHarvest", parameters.get(DpsDepositFacade.HARVEST_TYPE));
	}

	@Test
	public void testExtractFileDetailsFrom() {
		DPSArchive archiver = new DPSArchive();
		List<ArchiveFile> archiveFileList = mockDasArchiveFileList();
		List<File> fileList = archiver.extractFileDetailsFrom(archiveFileList);
		assertEquals(testFiles.length, fileList.size());
		for (int i = 0; i < testFiles.length; i++) {
			File file = fileList.get(i);
			assertEquals(testFiles[i], file);
		}
	}

	@Test
	public void testSubmitToArchive() throws DPSUploadException {
		mockDpsDepositFacade = mockDpsDepositFacade();
		DPSArchive archiver = new UnitTestDPSArchive();
		setVariousParameters(archiver);
		Map<String, String> attributes = mockDasAttributeMap();
		String finalSIP = "someFinalSIPXml";
		String targetInstanceOID = "112233";
		final List<ArchiveFile> archiveFileList = mockDasArchiveFileList();
		String archiveId = archiver.submitToArchive(targetInstanceOID, finalSIP, attributes, archiveFileList);
		assertEquals("dps-sipid-" + expectedSipId, archiveId);
	}

	private DpsDepositProxy mockDpsDepositFacade() {
		return new DpsDepositProxy() {
			public DepositResult deposit(Map<String, String> parameters, List<File> fileList) throws RuntimeException {
				for (int i = 0; i < testFiles.length; i++) {
					File file = fileList.get(i);
					assertEquals(testFiles[i], file);
				}
				assertEquals(testFiles.length, fileList.size());
				return mockDepositResult();
			}

			public String loginToPDS(Map<String, String> parameters) throws RuntimeException {
				return null;
			}

			@Override
			public void setCustomDepositFormMapping(CustomDepositFormMapping customDepositMapping) {
				return;
			}
		};
	}

	private DepositResult mockDepositResult() {
		return new DepositResult() {
			public String getCreationDate() {
				return null;
			}
			public long getDepositActivityId() {
				return 0;
			}
			public String getMessageCode() {
				return null;
			}
			public String getMessageDesciption() {
				return null;
			}
			public long getSipId() {
				return expectedSipId ;
			}
			public String getUserParameters() {
				return null;
			}
			public boolean isError() {
				return false;
			}
		};
	}

	private List<ArchiveFile> mockDasArchiveFileList() {
		List<ArchiveFile> archiveFileList = new ArrayList<ArchiveFile>();
		for (File aFile: testFiles) {
			archiveFileList.add(new ArchiveFile(aFile, 0));
		}
		return archiveFileList;
	}
	private Map<String, String> mockDasAttributeMap() {
		Map<String, String> attributes = new HashMap<String, String>();
		attributes.put(ACCESS_RESTRICTION, "anAccessRestriction");
		attributes.put(REFERENCE_NUMBER, "1234567890");
		return attributes;
	}

	private Map<String, String> mockDasAttributeMapForHtmlSerials() {
		Map<String, String> attributes = mockDasAttributeMap();
		attributes.put("customDepositForm_customFormPopulated", "true");
		attributes.put("customDepositForm_producerAgent", "anHtmlSerialUserName");
		attributes.put("customDepositForm_producerAgentPassword", "anHtmlSerialUserPassword");
		attributes.put("customDepositForm_producerId", "anHtmlSerialProducerId");
		attributes.put("customDepositForm_targetDcType", "HtmlSerialHarvest");
		attributes.put("customDepositForm_bibliographicCitation", "anHtmlSerialBibCitation");
//		attributes.put("customDepositForm_dctermsAccrualPeriodicity", "anHtmlSerialAccrualPeriodicity");
		attributes.put("customDepositForm_dctermsAvailable", "anHtmlSerialDctermsAvailable");
		attributes.put("harvest-type", "HtmlSerialHarvest");
		return attributes;
	}

	private void setVariousParameters(DPSArchive archiver) {
		archiver.setPdsUrl("aPdsUrl");
		archiver.setFtpHost("aFtpHost");
		archiver.setFtpUserName("aFtpUserName");
		archiver.setFtpPassword("aFtpPassword");
		archiver.setFtpDirectory("aFtpDirectory");
		archiver.setDepositServerBaseUrl("http://someserver.natlib.govt.nz:80000");
		archiver.setDepositWsdlRelativePath("/dpsws/deposit/DepositWebServices?wsdl");
		archiver.setDpsUserInstitution("aDpsUserInstitution");
		archiver.setDpsUserName("aDpsUserName");
		archiver.setDpsUserPassword("aDpsUserPassword");
		archiver.setMaterialFlowId("aMaterialFlowId");
		archiver.setProducerId("aProducerId");
	}

	/**
	 * A class to override few methods, such as getDpsDepositFacade() method to
	 * pass in a mocked version of DpsDepositFacade.
	 * 
	 * @author pushpar
	 * 
	 */
	private class UnitTestDPSArchive extends DPSArchive {
		protected DpsDepositProxy getDpsDepositFacade() {
			return mockDpsDepositFacade;
		}
		/**
		 * Since we are using dummy files, the original MD5 calculation
		 * will throw FileNotFoundException. The overriding below is to
		 * take care of that issue.
		 */
		protected String calculateMD5(File file) throws FileNotFoundException {
			return "1234";
		}
	}

	private DepData mockDepData(final String id, final String desc) {
		return new DepData(id, desc);
	}
}
