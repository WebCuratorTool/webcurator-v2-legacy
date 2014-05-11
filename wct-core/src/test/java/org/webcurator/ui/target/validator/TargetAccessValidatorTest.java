package org.webcurator.ui.target.validator;

import java.util.Date;
import java.util.Set;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.webcurator.ui.target.command.*;
import org.webcurator.domain.model.core.AuthorisingAgent;
import org.webcurator.domain.model.core.BusinessObjectFactory;
import org.webcurator.domain.model.core.UrlPattern;


public class TargetAccessValidatorTest extends TestCase {

	private static Log log = LogFactory.getLog(TargetAccessValidatorTest.class);

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
			TargetAccessValidator validator = new TargetAccessValidator();
			// test method with the supported class..
			result = validator.supports(TargetAccessCommand.class);
			assertTrue("TargetAccessValidator.supports() does not support the TargetAccessCommand class", result);	
			// test method with an unsupported class..
			result = validator.supports(TargetGeneralCommand.class);
			assertFalse("TargetAccessValidator.supports() should not support classes other than TargetAccessCommand", result);
		}
		catch (Exception e)
		{
			String message = e.getClass().toString() + " - " + e.getMessage();
			log.debug(message);
			fail(message);
		}
	}

	@Test
	public void testValidateCase01() {
		try {
			TargetAccessCommand cmd = new TargetAccessCommand();
			TargetAccessValidator validator = new TargetAccessValidator();
			Errors errors = new BindException(cmd, "TargetAccessCommand");
			
			// pass in newly created objects, expect no errors.
			validator.validate(cmd, errors);
			assertEquals("Case01: Not expecting errors with newly initialised objects", 0, errors.getErrorCount());
		}
		catch (Exception e)
		{
			String message = e.getClass().toString() + " - " + e.getMessage();
			log.debug(message);
			fail(message);
		}
	}

	@Test
	public void testValidateCase02() {
		try {
			TargetAccessCommand cmd = new TargetAccessCommand();
			TargetAccessValidator validator = new TargetAccessValidator();
			Errors errors = new BindException(cmd, "TargetAccessCommand");
			
			// pass in displayNote string less than or equal 4000 chars..
			cmd.setDisplayNote(makeLongString(33, 'X'));
			validator.validate(cmd, errors);
			assertEquals("Case02: Expecting 0 errors", 0, errors.getErrorCount());
		}
		catch (Exception e)
		{
			String message = e.getClass().toString() + " - " + e.getMessage();
			log.debug(message);
			fail(message);
		}
	}

	@Test
	public void testValidateCase03() {
		try {
			TargetAccessCommand cmd = new TargetAccessCommand();
			TargetAccessValidator validator = new TargetAccessValidator();
			Errors errors = new BindException(cmd, "TargetAccessCommand");
			
			// pass in displayNote string greater than 4000 chars..
			cmd.setDisplayNote(makeLongString(4001, 'X'));
			validator.validate(cmd, errors);
			assertEquals("Case02: Expecting 1 errors", 1, errors.getErrorCount());
			assertEquals("Case02: Expecting error[0] for field Display Note", true, errors.getAllErrors().toArray()[0].toString().contains("Display Note"));
		}
		catch (Exception e)
		{
			String message = e.getClass().toString() + " - " + e.getMessage();
			log.debug(message);
			fail(message);
		}
	}
}

