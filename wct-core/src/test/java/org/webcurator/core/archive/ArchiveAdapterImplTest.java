package org.webcurator.core.archive;

import static org.junit.Assert.*;

import java.util.HashMap;

import org.junit.Test;
import org.springframework.context.MockMessageSource;
import org.webcurator.test.BaseWCTTest;
import org.webcurator.ui.util.DateUtils;
import org.webcurator.core.targets.MockTargetManager;
import org.webcurator.core.scheduler.MockTargetInstanceManager;
import org.webcurator.core.store.MockDigitalAssetStore;
import org.webcurator.domain.*;

import com.google.common.collect.Maps;

public class ArchiveAdapterImplTest extends BaseWCTTest<ArchiveAdapterImpl>{

	public ArchiveAdapterImplTest()
	{
		super(ArchiveAdapterImpl.class,
				"src/test/java/org/webcurator/core/archive/archiveadapterimpltest.xml");
	}
	
	public void setUp() throws Exception 
	{
		super.setUp();
		DateUtils.get().setMessageSource(new MockMessageSource());
	}
	
	@Test
	public final void testSubmitToArchive() {
		try
		{
			MockTargetInstanceDAO tiDAO = new MockTargetInstanceDAO(testFile);
			this.testSetTargetInstanceManager();
			this.testSetTargetManager();
			this.testSetDigitalAssetStore();
			try
			{
				testInstance.submitToArchive(tiDAO.load(5000L), "", null, 10000);
				fail("Target Reference Number Cannot Be Blank");
			}
			catch(ArchiveException ae)
			{
				assertTrue(ae.getMessage().equals("Target Reference Number cannot be blank.  This is mandatory for archiving."));
			}

			testInstance.setTargetReferenceMandatory(false);
			try
			{
				testInstance.submitToArchive(tiDAO.load(5000L), "",  null, 10000);
			}
			catch(ArchiveException ae)
			{
				fail(ae.getMessage());
			}

			testInstance.setTargetReferenceMandatory(true);
			try
			{
				testInstance.submitToArchive(tiDAO.load(5000L), "",  null, 10000);
				fail("Target Reference Number Cannot Be Blank");
			}
			catch(ArchiveException ae)
			{
				assertTrue(ae.getMessage().equals("Target Reference Number cannot be blank.  This is mandatory for archiving."));
			}
		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}

	@Test
	public final void testSetDigitalAssetStore() {
		testInstance.setDigitalAssetStore(new MockDigitalAssetStore());
	}

	@Test
	public final void testSetTargetInstanceManager() {
		testInstance.setTargetInstanceManager(new MockTargetInstanceManager(testFile));
	}

	@Test
	public final void testSetTargetManager() {
		testInstance.setTargetManager(new MockTargetManager(testFile));
	}

	@Test
	public final void testSetGetTargetReferenceMandatory() {
		testInstance.setTargetReferenceMandatory(true);
		assertTrue(testInstance.getTargetReferenceMandatory());
		
		testInstance.setTargetReferenceMandatory(false);
		assertFalse(testInstance.getTargetReferenceMandatory());
	}
	
	@Test
	public void testStatusMap() {
		HashMap<String, String> accessStatusMap = Maps.newHashMap();
		accessStatusMap.put("open", Constants.OMS_ACCESS_RESTRICTION_OPEN_ACCESS);
		accessStatusMap.put("3 users", Constants.OMS_ACCESS_RESTRICTION_ON_SITE);
		accessStatusMap.put("onsite", Constants.OMS_ACCESS_RESTRICTION_ON_SITE_RESTRICTED);
		accessStatusMap.put("vip", Constants.OMS_ACCESS_RESTRICTION_RESTRICTED);
		
		testInstance.setAccessStatusMap(accessStatusMap);
		assertEquals("ACR_OPA", testInstance.getMappedRestriction("open"));
		assertEquals("ACR_ONS", testInstance.getMappedRestriction("3 users"));
		assertEquals("ACR_OSR", testInstance.getMappedRestriction("onsite"));
		assertEquals("ACR_RES", testInstance.getMappedRestriction("vip"));

		assertEquals("ACR_RES", testInstance.getMappedRestriction("nonexistant"));

		assertEquals("ACR_OPA", testInstance.getMappedRestriction("this is unrestricted, see"));
	}
}
