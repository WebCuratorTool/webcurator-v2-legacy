package org.webcurator.core.archive;

import static org.junit.Assert.*;

import org.junit.Test;
import org.webcurator.core.scheduler.*;
import org.webcurator.core.targets.*;
import org.webcurator.test.BaseWCTTest;
import java.text.SimpleDateFormat;
import org.apache.commons.lang.StringEscapeUtils;
import java.util.Date;
import org.webcurator.domain.model.core.*;
import java.util.Map;

public class SipBuilderTest extends BaseWCTTest<SipBuilder>{

	private TargetInstanceManager targetInstanceManager = null;
	private TargetManager targetManager = null;
	
	public SipBuilderTest()
	{
		super(SipBuilder.class,
				"src/test/java/org/webcurator/core/archive/SipBuilderTest.xml");
	}
	
	@Test
	public final void testEs() {
		String str = null;
		assertEquals("", SipBuilder.es(str));
		
		str = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><test id=\"1\"><!-- PRI_LOW = 1000; PRI_NRML = 100; PRI_HI = 0; --><priority>100</priority></test>";
		assertEquals(StringEscapeUtils.escapeXml(str), SipBuilder.es(str));
	}

	@Test
	public final void testDt() {
		SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
		Date date = null;
		assertEquals("", SipBuilder.dt(date));
		
		date = new Date();
		assertEquals(dateFormatter.format(date), SipBuilder.dt(date));
	}

	@Test
	public final void testGetTargetSectionLong() {
		this.testSetTargetInstanceManager();
		this.testSetTargetManager();

		try
		{
			String expectedOutput = "<wct:Target>\n  <wct:ReferenceNumber>1234</wct:ReferenceNumber>\n  <wct:Name>Test</wct:Name>\n  <wct:Description></wct:Description>\n  <wct:Seeds>\n    <wct:Seed>\n      <wct:SeedURL>www.oakleigh.co.uk</wct:SeedURL>\n      <wct:SeedType>Primary</wct:SeedType>\n    </wct:Seed>\n  </wct:Seeds>\n</wct:Target>\n";
			String targetSection = testInstance.getTargetSection(5001L);
			
			assertNotNull(targetSection);
			assertEquals(expectedOutput, targetSection);
		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}

	@Test
	public final void testBuildSipSections() {
		this.testSetTargetInstanceManager();
		this.testSetTargetManager();

		try
		{
			String permissionSection = "<wct:Permissions>\n  <wct:Permission>\n    <wct:State>Granted</wct:State>\n    <wct:StartDate>2009-02-01</wct:StartDate>\n    <wct:EndDate></wct:EndDate>\n    <wct:HarvestAuthorisation>\n      <wct:Name>Oakleigh Web Site</wct:Name>\n      <wct:Description></wct:Description>\n      <wct:OrderNumber></wct:OrderNumber>\n      <wct:IsPublished>true</wct:IsPublished>\n    </wct:HarvestAuthorisation>\n    <wct:AccessStatus></wct:AccessStatus>\n    <wct:SpecialRequirements></wct:SpecialRequirements>\n    <wct:OpenAccessDate></wct:OpenAccessDate>\n    <wct:CopyrightStatement></wct:CopyrightStatement>\n    <wct:CopyrightURL></wct:CopyrightURL>\n    <wct:FileReference></wct:FileReference>\n    <wct:AuthorisingAgent>\n      <wct:Name>Oakleigh</wct:Name>\n      <wct:Contact>Kev Urwin</wct:Contact>\n    </wct:AuthorisingAgent>\n    <wct:Paterns>\n      <wct:Pattern>http://www.oakleigh.co.uk/*</wct:Pattern>\n    </wct:Paterns>\n    <wct:SeedsURLs>\n      <wct:SeedURL>www.oakleigh.co.uk</wct:SeedURL>\n    </wct:SeedsURLs>\n  </wct:Permission>\n</wct:Permissions>\n";
			String targetSection = "<wct:Target>\n  <wct:ReferenceNumber>1234</wct:ReferenceNumber>\n  <wct:Name>Test</wct:Name>\n  <wct:Description></wct:Description>\n  <wct:Seeds>\n    <wct:Seed>\n      <wct:SeedURL>www.oakleigh.co.uk</wct:SeedURL>\n      <wct:SeedType>Primary</wct:SeedType>\n    </wct:Seed>\n  </wct:Seeds>\n</wct:Target>\n";
			String profileNoteSection = "<wct:ProfileNote></wct:ProfileNote>";
			
			TargetInstance ti  = targetInstanceManager.getTargetInstance(5001L);
			Map<String, String> sipSections = testInstance.buildSipSections(ti);
			
			assertNotNull(sipSections);
			assertEquals(sipSections.size(), 3);
			assertTrue(sipSections.containsKey("permissionSection"));
			assertTrue(sipSections.containsKey("targetSection"));
			assertTrue(sipSections.containsKey("profileNoteSection"));
			assertEquals(permissionSection, sipSections.get("permissionSection"));
			assertEquals(targetSection, sipSections.get("targetSection"));
			assertEquals(profileNoteSection, sipSections.get("profileNoteSection"));
		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}

	@Test
	public final void testGetProfileNoteSection() {
		this.testSetTargetInstanceManager();
		this.testSetTargetManager();

		try
		{
			String expectedOutput = "<wct:ProfileNote></wct:ProfileNote>";
			TargetInstance ti  = targetInstanceManager.getTargetInstance(5001L);
			
			String profileNoteSection = testInstance.getProfileNoteSection(ti);
			
			assertNotNull(profileNoteSection);
			assertEquals(expectedOutput, profileNoteSection);
		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}

	@Test
	public final void testUpdateTargetReferenceExistingRef() {
		this.testSetTargetInstanceManager();
		this.testSetTargetManager();

		AbstractTarget target  = null;
		TargetInstance inst  = null;
		Map<String, String> sipSections = null;
		
		String targetSection = "<wct:Target>\n  <wct:ReferenceNumber>1234</wct:ReferenceNumber>\n  <wct:Name>Test</wct:Name>\n  <wct:Description></wct:Description>\n  <wct:Seeds>\n    <wct:Seed>\n      <wct:SeedURL>www.oakleigh.co.uk</wct:SeedURL>\n      <wct:SeedType>Primary</wct:SeedType>\n    </wct:Seed>\n  </wct:Seeds>\n</wct:Target>\n";
		
		
		try
		{
			target  = targetManager.load(4000L);
			inst  = targetInstanceManager.getTargetInstance(5001L);
			inst.setTarget(target);
			sipSections = testInstance.buildSipSections(inst);

			assertEquals(target.getReferenceNumber(), "1234");
			assertEquals(inst.getTarget().getReferenceNumber(), "1234");

			assertFalse(testInstance.updateTargetReference(target, sipSections));

			assertEquals(targetSection, sipSections.get("targetSection"));
			assertEquals(target.getReferenceNumber(), "1234");
			assertEquals(inst.getTarget().getReferenceNumber(), "1234");
		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}

	@Test
	public final void testUpdateTargetReferenceNoRefEver() {
		this.testSetTargetInstanceManager();
		this.testSetTargetManager();

		AbstractTarget target  = null;
		TargetInstance inst  = null;
		Map<String, String> sipSections = null;
		
		String targetSection = "<wct:Target>\n  <wct:ReferenceNumber></wct:ReferenceNumber>\n  <wct:Name>Test2</wct:Name>\n  <wct:Description></wct:Description>\n  <wct:Seeds>\n    <wct:Seed>\n      <wct:SeedURL>www.oakleigh.co.uk</wct:SeedURL>\n      <wct:SeedType>Primary</wct:SeedType>\n    </wct:Seed>\n  </wct:Seeds>\n</wct:Target>\n";
		
		
		try
		{
			target  = targetManager.load(4001L);
			inst  = targetInstanceManager.getTargetInstance(5002L);
			inst.setTarget(target);
			sipSections = testInstance.buildSipSections(inst);
			
			assertEquals(target.getReferenceNumber(), "");
			assertEquals(inst.getTarget().getReferenceNumber(), "");

			assertFalse(testInstance.updateTargetReference(target, sipSections));
			
			assertEquals(targetSection, sipSections.get("targetSection"));
			assertEquals(target.getReferenceNumber(), "");
			assertEquals(inst.getTarget().getReferenceNumber(), "");
		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}

	@Test
	public final void testUpdateTargetReferenceNoRefNow() {
		this.testSetTargetInstanceManager();
		this.testSetTargetManager();

		AbstractTarget target  = null;
		TargetInstance inst  = null;
		Map<String, String> sipSections = null;
		
		String targetSection = "<wct:Target>\n  <wct:ReferenceNumber>1234</wct:ReferenceNumber>\n  <wct:Name>Test</wct:Name>\n  <wct:Description></wct:Description>\n  <wct:Seeds>\n    <wct:Seed>\n      <wct:SeedURL>www.oakleigh.co.uk</wct:SeedURL>\n      <wct:SeedType>Primary</wct:SeedType>\n    </wct:Seed>\n  </wct:Seeds>\n</wct:Target>\n";
		
		try
		{
			target  = targetManager.load(4000L);
			inst  = targetInstanceManager.getTargetInstance(5001L);
			inst.setTarget(target);
			sipSections = testInstance.buildSipSections(inst);
			
			
			assertEquals(target.getReferenceNumber(), "1234");
			assertEquals(inst.getTarget().getReferenceNumber(), "1234");
			target.setReferenceNumber("");
			assertEquals(target.getReferenceNumber(), "");
			assertEquals(inst.getTarget().getReferenceNumber(), "");
			
			assertFalse(testInstance.updateTargetReference(target, sipSections));
			
			assertEquals(targetSection, sipSections.get("targetSection"));
			assertEquals(target.getReferenceNumber(), "");
			assertEquals(inst.getTarget().getReferenceNumber(), "");
		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}

	@Test
	public final void testUpdateTargetReferenceNewRef() {
		this.testSetTargetInstanceManager();
		this.testSetTargetManager();

		AbstractTarget target  = null;
		TargetInstance inst  = null;
		Map<String, String> sipSections = null;
		
		String targetSection1 = "<wct:Target>\n  <wct:ReferenceNumber></wct:ReferenceNumber>\n  <wct:Name>Test2</wct:Name>\n  <wct:Description></wct:Description>\n  <wct:Seeds>\n    <wct:Seed>\n      <wct:SeedURL>www.oakleigh.co.uk</wct:SeedURL>\n      <wct:SeedType>Primary</wct:SeedType>\n    </wct:Seed>\n  </wct:Seeds>\n</wct:Target>\n";
		String targetSection2 = "<wct:Target>\n  <wct:ReferenceNumber>1234</wct:ReferenceNumber>\n  <wct:Name>Test2</wct:Name>\n  <wct:Description></wct:Description>\n  <wct:Seeds>\n    <wct:Seed>\n      <wct:SeedURL>www.oakleigh.co.uk</wct:SeedURL>\n      <wct:SeedType>Primary</wct:SeedType>\n    </wct:Seed>\n  </wct:Seeds>\n</wct:Target>\n";
		
		
		try
		{
			target  = targetManager.load(4001L);
			inst  = targetInstanceManager.getTargetInstance(5002L);
			inst.setTarget(target);
			sipSections = testInstance.buildSipSections(inst);
			assertEquals(targetSection1, sipSections.get("targetSection"));
			assertEquals(target.getReferenceNumber(), "");
			assertEquals(inst.getTarget().getReferenceNumber(), "");
			
			target.setReferenceNumber("1234");
			assertEquals(target.getReferenceNumber(), "1234");
			assertEquals(inst.getTarget().getReferenceNumber(), "1234");

			assertTrue(testInstance.updateTargetReference(target, sipSections));
			
			assertEquals(targetSection2, sipSections.get("targetSection"));
			assertEquals(target.getReferenceNumber(), "1234");
			assertEquals(inst.getTarget().getReferenceNumber(), "1234");
		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}

	@Test
	public final void testGetTargetSectionTargetInstance() {
		this.testSetTargetInstanceManager();
		this.testSetTargetManager();

		try
		{
			String expectedOutput = "<wct:Target>\n  <wct:ReferenceNumber>1234</wct:ReferenceNumber>\n  <wct:Name>Test</wct:Name>\n  <wct:Description></wct:Description>\n  <wct:Seeds>\n    <wct:Seed>\n      <wct:SeedURL>www.oakleigh.co.uk</wct:SeedURL>\n      <wct:SeedType>Primary</wct:SeedType>\n    </wct:Seed>\n  </wct:Seeds>\n</wct:Target>\n";

			TargetInstance ti  = targetInstanceManager.getTargetInstance(5001L);
			String targetSection = testInstance.getTargetSection(ti);
			
			assertNotNull(targetSection);
			assertEquals(expectedOutput, targetSection);
		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}

	@Test
	public final void testGetPermissionSectionLong() {
		this.testSetTargetInstanceManager();
		this.testSetTargetManager();

		try
		{
			String expectedOutput = "<wct:Permissions>\n  <wct:Permission>\n    <wct:State>Granted</wct:State>\n    <wct:StartDate>2009-02-01</wct:StartDate>\n    <wct:EndDate></wct:EndDate>\n    <wct:HarvestAuthorisation>\n      <wct:Name>Oakleigh Web Site</wct:Name>\n      <wct:Description></wct:Description>\n      <wct:OrderNumber></wct:OrderNumber>\n      <wct:IsPublished>true</wct:IsPublished>\n    </wct:HarvestAuthorisation>\n    <wct:AccessStatus></wct:AccessStatus>\n    <wct:SpecialRequirements></wct:SpecialRequirements>\n    <wct:OpenAccessDate></wct:OpenAccessDate>\n    <wct:CopyrightStatement></wct:CopyrightStatement>\n    <wct:CopyrightURL></wct:CopyrightURL>\n    <wct:FileReference></wct:FileReference>\n    <wct:AuthorisingAgent>\n      <wct:Name>Oakleigh</wct:Name>\n      <wct:Contact>Kev Urwin</wct:Contact>\n    </wct:AuthorisingAgent>\n    <wct:Paterns>\n      <wct:Pattern>http://www.oakleigh.co.uk/*</wct:Pattern>\n    </wct:Paterns>\n    <wct:SeedsURLs>\n      <wct:SeedURL>www.oakleigh.co.uk</wct:SeedURL>\n    </wct:SeedsURLs>\n  </wct:Permission>\n</wct:Permissions>\n";
			String permissionSection = testInstance.getPermissionSection(5001L);
			
			assertNotNull(permissionSection);
			assertEquals(expectedOutput, permissionSection);
		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}

	@Test
	public final void testGetPermissionSectionTargetInstance() {
		this.testSetTargetInstanceManager();
		this.testSetTargetManager();

		try
		{
			String expectedOutput = "<wct:Permissions>\n  <wct:Permission>\n    <wct:State>Granted</wct:State>\n    <wct:StartDate>2009-02-01</wct:StartDate>\n    <wct:EndDate></wct:EndDate>\n    <wct:HarvestAuthorisation>\n      <wct:Name>Oakleigh Web Site</wct:Name>\n      <wct:Description></wct:Description>\n      <wct:OrderNumber></wct:OrderNumber>\n      <wct:IsPublished>true</wct:IsPublished>\n    </wct:HarvestAuthorisation>\n    <wct:AccessStatus></wct:AccessStatus>\n    <wct:SpecialRequirements></wct:SpecialRequirements>\n    <wct:OpenAccessDate></wct:OpenAccessDate>\n    <wct:CopyrightStatement></wct:CopyrightStatement>\n    <wct:CopyrightURL></wct:CopyrightURL>\n    <wct:FileReference></wct:FileReference>\n    <wct:AuthorisingAgent>\n      <wct:Name>Oakleigh</wct:Name>\n      <wct:Contact>Kev Urwin</wct:Contact>\n    </wct:AuthorisingAgent>\n    <wct:Paterns>\n      <wct:Pattern>http://www.oakleigh.co.uk/*</wct:Pattern>\n    </wct:Paterns>\n    <wct:SeedsURLs>\n      <wct:SeedURL>www.oakleigh.co.uk</wct:SeedURL>\n    </wct:SeedsURLs>\n  </wct:Permission>\n</wct:Permissions>\n";

			TargetInstance ti  = targetInstanceManager.getTargetInstance(5001L);
			String permissionSection = testInstance.getPermissionSection(ti);
			
			assertNotNull(permissionSection);
			assertEquals(expectedOutput, permissionSection);
		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}

	@Test
	public final void testSetTargetInstanceManager() {
		targetInstanceManager = new MockTargetInstanceManager(testFile);
		testInstance.setTargetInstanceManager(targetInstanceManager);
	}

	@Test
	public final void testSetTargetManager() {
		targetManager = new MockTargetManager(testFile);
		testInstance.setTargetManager(targetManager);
	}

}
