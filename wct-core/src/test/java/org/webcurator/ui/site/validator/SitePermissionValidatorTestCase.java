package org.webcurator.ui.site.validator;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.webcurator.ui.site.command.SitePermissionCommand;
import org.webcurator.domain.model.core.AuthorisingAgent;
import org.webcurator.domain.model.core.BusinessObjectFactory;
import org.webcurator.domain.model.core.Site;
import org.webcurator.domain.model.core.UrlPattern;

public class SitePermissionValidatorTestCase extends TestCase {

	private static Log log = LogFactory.getLog(SitePermissionValidatorTestCase.class);

	// businessObjectFactory
	BusinessObjectFactory  businessObjectFactory = new BusinessObjectFactory();
	// authorisingAgent
	AuthorisingAgent authorisingAgent = null;
	// startDate
	Date startDate = null;
	// quickPick
	boolean quickPick = false;
	// urls
	Set<UrlPattern> urls = null;
	// createSeekPermissionTask
	boolean createSeekPermissionTask = false;
	// status
	int status = 0; // set to Permission.STATUS_PENDING for now
	// endDate
	Date endDate = null; // ..for now

	private String makeLongString(int size, char fillChar) {
		final int SIZE = size ;
		StringBuffer sb = new StringBuffer (SIZE) ;
    
		for ( int i = 0 ; i < SIZE; i++) {
			sb.append(fillChar) ;
		}
		return sb.toString() ;
	}
	
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testSupports() {
		boolean result;
		try {
			SitePermissionValidator validator = new SitePermissionValidator();
			// test method with the supported class..
			result = validator.supports(SitePermissionCommand.class);
			assertTrue("SitePermissionValidator.supports() does not support the SitePermissionCommand class", result);	
			// test method with an unsupported class..
			result = validator.supports(SiteValidator.class);
			assertFalse("SitePermissionValidator.supports() should not support classes other than SitePermissionValidator", result);
		}
		catch (Exception e)
		{
			String message = e.getClass().toString() + " - " + e.getMessage();
			log.debug(message);
			fail(message);
		}
	}

	@Test
	public void testValidateDefault() {
		try {
			SitePermissionCommand cmd = new SitePermissionCommand();
			SitePermissionValidator validator = new SitePermissionValidator();
			Errors errors = new BindException(cmd, "SitePermissionCommand");
			
			// pass in newly created objects, expect no errors.
			validator.validate(cmd, errors);
			assertEquals("Not expecting errors with newly initialised objects", 0, errors.getErrorCount());
		}
		catch (Exception e)
		{
			String message = e.getClass().toString() + " - " + e.getMessage();
			log.debug(message);
			fail(message);
		}
	}

	@Test
	public void testValidateActionSaveCase01() {
		try {
			SitePermissionCommand cmd = new SitePermissionCommand();
			SitePermissionValidator validator = new SitePermissionValidator();
			Errors errors = new BindException(cmd, "SitePermissionCommand");
			
			// don't set authorisingAgent, startDate or urls
			cmd.setActionCmd(SitePermissionCommand.ACTION_SAVE);
			validator.validate(cmd, errors);
			assertEquals("ACTION_SAVE Case 01: Expecting 3 errors", 3, errors.getErrorCount());
			assertEquals("ACTION_SAVE Case 01: Expecting error[0] for field authorisingAgent", true, errors.getAllErrors().toArray()[0].toString().contains("'authorisingAgent'"));
			assertEquals("ACTION_SAVE Case 01: Expecting error[1] for field startDate", true, errors.getAllErrors().toArray()[1].toString().contains("'startDate'"));
			assertEquals("ACTION_SAVE Case 01: Expecting error[2] for field Urls", true, errors.getAllErrors().toArray()[2].toString().contains("[Urls]"));
		}
		catch (Exception e)
		{
			String message = e.getClass().toString() + " - " + e.getMessage();
			log.debug(message);
			fail(message);
		}
	}

	@Test
	public void testValidateActionSaveCase02() {
		try {
			SitePermissionCommand cmd = new SitePermissionCommand();
			SitePermissionValidator validator = new SitePermissionValidator();
			Errors errors = new BindException(cmd, "SitePermissionCommand");
			
			// don't set startDate or urls
			authorisingAgent = businessObjectFactory.newAuthorisingAgent();
			authorisingAgent.setOid(1L);
	
			cmd.setActionCmd(SitePermissionCommand.ACTION_SAVE);
			cmd.setAuthorisingAgent(authorisingAgent);
			validator.validate(cmd, errors);
			assertEquals("ACTION_SAVE Case 02: Expecting 2 errors", 2, errors.getErrorCount());
			assertEquals("ACTION_SAVE Case 02: Expecting error[0] for field startDate", true, errors.getAllErrors().toArray()[0].toString().contains("'startDate'"));
			assertEquals("ACTION_SAVE Case 02: Expecting error[1] for field Urls", true, errors.getAllErrors().toArray()[1].toString().contains("[Urls]"));
		}
		catch (Exception e)
		{
			String message = e.getClass().toString() + " - " + e.getMessage();
			log.debug(message);
			fail(message);
		}
	}

	@Test
	public void testValidateActionSaveCase03() {
		try {
			SitePermissionCommand cmd = new SitePermissionCommand();
			SitePermissionValidator validator = new SitePermissionValidator();
			Errors errors = new BindException(cmd, "SitePermissionCommand");
			
			// don't set urls
			authorisingAgent = businessObjectFactory.newAuthorisingAgent();
			authorisingAgent.setOid(1L);
			startDate = new Date(); // today
	
			cmd.setActionCmd(SitePermissionCommand.ACTION_SAVE);
			cmd.setAuthorisingAgent(authorisingAgent);
			cmd.setStartDate(startDate);
			validator.validate(cmd, errors);
			assertEquals("ACTION_SAVE Case 03: Expecting 1 errors", 1, errors.getErrorCount());
			assertEquals("ACTION_SAVE Case 03: Expecting error[0] for field Urls", true, errors.getAllErrors().toArray()[0].toString().contains("[Urls]"));
		}
		catch (Exception e)
		{
			String message = e.getClass().toString() + " - " + e.getMessage();
			log.debug(message);
			fail(message);
		}
	}

	@Test
	public void testValidateActionSaveCase04() {
		try {
			SitePermissionCommand cmd = new SitePermissionCommand();
			SitePermissionValidator validator = new SitePermissionValidator();
			Errors errors = new BindException(cmd, "SitePermissionCommand");
			
			// send an empty urls set..
			authorisingAgent = businessObjectFactory.newAuthorisingAgent();
			authorisingAgent.setOid(1L);
			startDate = new Date(); // today
			urls = new HashSet<UrlPattern>(); // empty to start
			
			cmd.setActionCmd(SitePermissionCommand.ACTION_SAVE);
			cmd.setAuthorisingAgent(authorisingAgent);
			cmd.setStartDate(startDate);
			cmd.setUrls(urls);
			validator.validate(cmd, errors);
			assertEquals("ACTION_SAVE Case 04: Expecting 1 errors", 1, errors.getErrorCount());
			assertEquals("ACTION_SAVE Case 04: Expecting error[0] for field Urls", true, errors.getAllErrors().toArray()[0].toString().contains("[Urls]"));
		}
		catch (Exception e)
		{
			String message = e.getClass().toString() + " - " + e.getMessage();
			log.debug(message);
			fail(message);
		}
	}

	@Test
	public void testValidateActionSaveCase05() {
		try {
			SitePermissionCommand cmd = new SitePermissionCommand();
			SitePermissionValidator validator = new SitePermissionValidator();
			Errors errors = new BindException(cmd, "SitePermissionCommand");
		
			// initialise all main objects we need to validate..
			authorisingAgent = businessObjectFactory.newAuthorisingAgent();
			authorisingAgent.setOid(1L);
			startDate = new Date(); // today
			urls = new HashSet<UrlPattern>(); // adding a UrlPattern this time..
			Site site = new Site();
			UrlPattern urlPattern = businessObjectFactory.newUrlPattern(site);
			urlPattern.setPattern("http://www.oldyardsrugby.co.uk/*");
			urls.add(urlPattern);
			
			cmd.setActionCmd(SitePermissionCommand.ACTION_SAVE);
			cmd.setAuthorisingAgent(authorisingAgent);
			cmd.setStartDate(startDate);
			cmd.setUrls(urls);
			validator.validate(cmd, errors);
			assertEquals("ACTION_SAVE Case 05: Expecting 0 errors", 0, errors.getErrorCount());
		}
		catch (Exception e)
		{
			String message = e.getClass().toString() + " - " + e.getMessage();
			log.debug(message);
			fail(message);
		}
	}

	@Test
	public void testValidateActionSaveCase06() {
		try {
			SitePermissionCommand cmd = new SitePermissionCommand();
			SitePermissionValidator validator = new SitePermissionValidator();
			Errors errors = new BindException(cmd, "SitePermissionCommand");
		
			// test the quickPick and createSeekPermissionTask variations of the validation..
			authorisingAgent = businessObjectFactory.newAuthorisingAgent();
			authorisingAgent.setOid(1L);
			startDate = new Date(); // today
			quickPick = true;
			createSeekPermissionTask = true;
			urls = new HashSet<UrlPattern>(); // adding a UrlPattern this time..
			Site site = new Site();
			UrlPattern urlPattern = businessObjectFactory.newUrlPattern(site);
			urlPattern.setPattern("http://www.oldyardsrugby.co.uk/*");
			urls.add(urlPattern);
			
			cmd.setActionCmd(SitePermissionCommand.ACTION_SAVE);
			cmd.setAuthorisingAgent(authorisingAgent);
			cmd.setStartDate(startDate);
			cmd.setUrls(urls);

			cmd.setQuickPick(quickPick);
			cmd.setCreateSeekPermissionTask(createSeekPermissionTask);
			cmd.setStatus(1); // 1 is Not Permission.STATUS_PENDING

			validator.validate(cmd, errors);
			assertEquals("ACTION_SAVE Case 06: Expecting 2 errors", 2, errors.getErrorCount());
			assertEquals("ACTION_SAVE Case 06: Expecting error[0] for field displayName", true, errors.getAllErrors().toArray()[0].toString().contains("'displayName'"));
			assertEquals("ACTION_SAVE Case 06: Expecting error[1] to contain", true, errors.getAllErrors().toArray()[1].toString().contains("permission.errors.create_task"));
		}
		catch (Exception e)
		{
			String message = e.getClass().toString() + " - " + e.getMessage();
			log.debug(message);
			fail(message);
		}
	}

	@Test
	public void testValidateActionSaveCase07() {
		try {
			SitePermissionCommand cmd = new SitePermissionCommand();
			SitePermissionValidator validator = new SitePermissionValidator();
			Errors errors = new BindException(cmd, "SitePermissionCommand");
		
			// test field validation of non-mandatory fields..
			authorisingAgent = businessObjectFactory.newAuthorisingAgent();
			authorisingAgent.setOid(1L);
			startDate = new Date(); // today
			urls = new HashSet<UrlPattern>(); // adding a UrlPattern this time..
			Site site = new Site();
			UrlPattern urlPattern = businessObjectFactory.newUrlPattern(site);
			urlPattern.setPattern("http://www.oldyardsrugby.co.uk/*");
			urls.add(urlPattern);
			
			cmd.setActionCmd(SitePermissionCommand.ACTION_SAVE);
			cmd.setAuthorisingAgent(authorisingAgent);
			cmd.setStartDate(startDate);
			cmd.setUrls(urls);
			
			//Sets the date one day back
			long newDateTime = (new Date().getTime()) - (1000*60*60*24);
			cmd.setEndDate(new Date(newDateTime));
			cmd.setSpecialRequirements(makeLongString(2049, 'X'));
			cmd.setDisplayName(makeLongString(33, 'X'));
			cmd.setCopyrightStatement(makeLongString(2049, 'X'));
			cmd.setCopyrightUrl(makeLongString(2049, 'X'));
			cmd.setFileReference(makeLongString(256, 'X'));
			
			
			validator.validate(cmd, errors);
			assertEquals("ACTION_SAVE Case 07: Expecting 6 errors", 6, errors.getErrorCount());
		}
		catch (Exception e)
		{
			String message = e.getClass().toString() + " - " + e.getMessage();
			log.debug(message);
			fail(message);
		}
	}

	@Test
	public void testValidateActionAddExclusionCase01() {
		try {
			SitePermissionCommand cmd = new SitePermissionCommand();
			SitePermissionValidator validator = new SitePermissionValidator();
			Errors errors = new BindException(cmd, "SitePermissionCommand");
		
			// initialise all main objects we need to validate..
			authorisingAgent = businessObjectFactory.newAuthorisingAgent();
			authorisingAgent.setOid(1L);
			startDate = new Date(); // today
			urls = new HashSet<UrlPattern>(); // adding a UrlPattern this time..
			Site site = new Site();
			UrlPattern urlPattern = businessObjectFactory.newUrlPattern(site);
			urlPattern.setPattern("http://www.oldyardsrugby.co.uk/*");
			urls.add(urlPattern);
			
			cmd.setActionCmd(SitePermissionCommand.ACTION_ADD_EXCLUSION);
			cmd.setAuthorisingAgent(authorisingAgent);
			cmd.setStartDate(startDate);
			cmd.setUrls(urls);
			
			validator.validate(cmd, errors);
			assertEquals("ACTION_ADD_EXCLUSION Case 01: Expecting 1 errors", 1, errors.getErrorCount());
			assertEquals("ACTION_ADD_EXCLUSION Case 01: Expecting error[0] for field exclusionUrl", true, errors.getAllErrors().toArray()[0].toString().contains("'exclusionUrl'"));
		}
		catch (Exception e)
		{
			String message = e.getClass().toString() + " - " + e.getMessage();
			log.debug(message);
			fail(message);
		}
	}

	@Test
	public void testValidateActionAddExclusionCase02() {
		try {
			SitePermissionCommand cmd = new SitePermissionCommand();
			SitePermissionValidator validator = new SitePermissionValidator();
			Errors errors = new BindException(cmd, "SitePermissionCommand");
		
			// initialise all main objects we need to validate..
			authorisingAgent = businessObjectFactory.newAuthorisingAgent();
			authorisingAgent.setOid(1L);
			startDate = new Date(); // today
			urls = new HashSet<UrlPattern>(); // adding a UrlPattern this time..
			Site site = new Site();
			UrlPattern urlPattern = businessObjectFactory.newUrlPattern(site);
			urlPattern.setPattern("http://www.oldyardsrugby.co.uk/*");
			urls.add(urlPattern);
			
			cmd.setActionCmd(SitePermissionCommand.ACTION_ADD_EXCLUSION);
			cmd.setAuthorisingAgent(authorisingAgent);
			cmd.setStartDate(startDate);
			cmd.setUrls(urls);
			
			cmd.setExclusionUrl("http://www.oldyardsrugby.co.uk/data/*");
			cmd.setExclusionReason(makeLongString(1025, 'X'));
			
			validator.validate(cmd, errors);
			assertEquals("ACTION_ADD_EXCLUSION Case 02: Expecting 1 errors", 1, errors.getErrorCount());
			assertEquals("ACTION_ADD_EXCLUSION Case 02: Expecting error[0] for field Exclusion Reason", true, errors.getAllErrors().toArray()[0].toString().contains("Exclusion Reason"));
		}
		catch (Exception e)
		{
			String message = e.getClass().toString() + " - " + e.getMessage();
			log.debug(message);
			fail(message);
		}
	}

	@Test
	public void testValidateActionAddNoteCase01() {
		try {
			SitePermissionCommand cmd = new SitePermissionCommand();
			SitePermissionValidator validator = new SitePermissionValidator();
			Errors errors = new BindException(cmd, "SitePermissionCommand");
			
			// don't set note field
			cmd.setActionCmd(SitePermissionCommand.ACTION_ADD_NOTE);
			validator.validate(cmd, errors);
			assertEquals("ACTION_ADD_NOTE Case 01: Expecting 1 errors", 1, errors.getErrorCount());
			assertEquals("ACTION_ADD_NOTE Case 02: Expecting error[0] for field note", true, errors.getAllErrors().toArray()[0].toString().contains("'note'"));
		}
		catch (Exception e)
		{
			String message = e.getClass().toString() + " - " + e.getMessage();
			log.debug(message);
			fail(message);
		}
	}
	
	@Test
	public void testValidateActionAddNoteCase02() {
		try {
			SitePermissionCommand cmd = new SitePermissionCommand();
			SitePermissionValidator validator = new SitePermissionValidator();
			Errors errors = new BindException(cmd, "SitePermissionCommand");
			
			// don't set note field
			cmd.setActionCmd(SitePermissionCommand.ACTION_ADD_NOTE);
			cmd.setNote(makeLongString(1001, 'X'));
			
			validator.validate(cmd, errors);
			assertEquals("ACTION_ADD_NOTE Case 01: Expecting 1 errors", 1, errors.getErrorCount());
			assertEquals("ACTION_ADD_NOTE Case 02: Expecting error[0] for field note", true, errors.getAllErrors().toArray()[0].toString().contains("note"));
		}
		catch (Exception e)
		{
			String message = e.getClass().toString() + " - " + e.getMessage();
			log.debug(message);
			fail(message);
		}
	}

}

