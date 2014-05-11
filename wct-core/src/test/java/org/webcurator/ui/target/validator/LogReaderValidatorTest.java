package org.webcurator.ui.target.validator;

import static org.junit.Assert.*;

import org.junit.Test;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.webcurator.test.BaseWCTTest;
import org.webcurator.ui.target.command.LogReaderCommand;

public class LogReaderValidatorTest extends BaseWCTTest<LogReaderValidator>{

	public LogReaderValidatorTest()
	{
		super(LogReaderValidator.class,"");
	}
	
	
	@Test
	public final void testSupports() {
		assertTrue(testInstance.supports(LogReaderCommand.class));
	}

	@Test
	public final void testValidateCase01() {
		try {
			LogReaderCommand cmd = new LogReaderCommand();
			Errors errors = new BindException(cmd, "LogReaderCommand");
			
			cmd.setFilterType(LogReaderCommand.VALUE_FROM_LINE);
			cmd.setFilter("1034");
			cmd.setNoOfLines(700);

			// pass in newly created objects, expect no errors.
			testInstance.validate(cmd, errors);
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
	public final void testValidateCase02() {
		try {
			LogReaderCommand cmd = new LogReaderCommand();
			Errors errors = new BindException(cmd, "LogReaderCommand");
			
			cmd.setFilterType(LogReaderCommand.VALUE_FROM_LINE);
			cmd.setFilter("");
			cmd.setNoOfLines(700);

			// pass in newly created objects, expect no errors.
			testInstance.validate(cmd, errors);
			assertEquals("Case02: Expecting 1 errors", 1, errors.getErrorCount());
			assertEquals("Case02: Expecting error[0] for field Filter", true, errors.getAllErrors().toArray()[0].toString().contains("Line Number is a required field"));
		}
		catch (Exception e)
		{
			String message = e.getClass().toString() + " - " + e.getMessage();
			log.debug(message);
			fail(message);
		}
	}

	@Test
	public final void testValidateCase02a() {
		try {
			LogReaderCommand cmd = new LogReaderCommand();
			Errors errors = new BindException(cmd, "LogReaderCommand");
			
			cmd.setFilterType(LogReaderCommand.VALUE_FROM_LINE);
			cmd.setFilter("a");
			cmd.setNoOfLines(700);

			// pass in newly created objects, expect no errors.
			testInstance.validate(cmd, errors);
			assertEquals("Case02a: Expecting 1 errors", 1, errors.getErrorCount());
			assertEquals("Case02a: Expecting error[0] for field Filter", true, errors.getAllErrors().toArray()[0].toString().contains("Line Number must be an integer"));
		}
		catch (Exception e)
		{
			String message = e.getClass().toString() + " - " + e.getMessage();
			log.debug(message);
			fail(message);
		}
	}

	@Test
	public final void testValidateCase03() {
		try {
			LogReaderCommand cmd = new LogReaderCommand();
			Errors errors = new BindException(cmd, "LogReaderCommand");
			
			cmd.setFilterType(LogReaderCommand.VALUE_TIMESTAMP);
			cmd.setFilter("0");
			cmd.setNoOfLines(700);

			// pass in newly created objects, expect no errors.
			testInstance.validate(cmd, errors);
			assertEquals("Case03: Not expecting errors with newly initialised objects", 0, errors.getErrorCount());
		}
		catch (Exception e)
		{
			String message = e.getClass().toString() + " - " + e.getMessage();
			log.debug(message);
			fail(message);
		}
	}

	@Test
	public final void testValidateCase04() {
		try {
			LogReaderCommand cmd = new LogReaderCommand();
			Errors errors = new BindException(cmd, "LogReaderCommand");
			
			cmd.setFilterType(LogReaderCommand.VALUE_TIMESTAMP);
			cmd.setFilter("");
			cmd.setNoOfLines(700);

			// pass in newly created objects, expect no errors.
			testInstance.validate(cmd, errors);
			assertEquals("Case04: Expecting 1 errors", 1, errors.getErrorCount());
			assertEquals("Case04: Expecting error[0] for field Filter", true, errors.getAllErrors().toArray()[0].toString().contains("Date/Time is a required field"));
		}
		catch (Exception e)
		{
			String message = e.getClass().toString() + " - " + e.getMessage();
			log.debug(message);
			fail(message);
		}
	}

	@Test
	public final void testValidateCase05() {
		try {
			LogReaderCommand cmd = new LogReaderCommand();
			Errors errors = new BindException(cmd, "LogReaderCommand");
			
			cmd.setFilterType(LogReaderCommand.VALUE_REGEX_MATCH);
			cmd.setFilter("0");
			cmd.setNoOfLines(700);

			// pass in newly created objects, expect no errors.
			testInstance.validate(cmd, errors);
			assertEquals("Case05: Not expecting errors with newly initialised objects", 0, errors.getErrorCount());
		}
		catch (Exception e)
		{
			String message = e.getClass().toString() + " - " + e.getMessage();
			log.debug(message);
			fail(message);
		}
	}

	@Test
	public final void testValidateCase06() {
		try {
			LogReaderCommand cmd = new LogReaderCommand();
			Errors errors = new BindException(cmd, "LogReaderCommand");
			
			cmd.setFilterType(LogReaderCommand.VALUE_REGEX_MATCH);
			cmd.setFilter("");
			cmd.setNoOfLines(700);

			// pass in newly created objects, expect no errors.
			testInstance.validate(cmd, errors);
			assertEquals("Case06: Expecting 1 errors", 1, errors.getErrorCount());
			assertEquals("Case06: Expecting error[0] for field Filter", true, errors.getAllErrors().toArray()[0].toString().contains("Regular Expression is a required field"));
		}
		catch (Exception e)
		{
			String message = e.getClass().toString() + " - " + e.getMessage();
			log.debug(message);
			fail(message);
		}
	}

	@Test
	public final void testValidateCase07() {
		try {
			LogReaderCommand cmd = new LogReaderCommand();
			Errors errors = new BindException(cmd, "LogReaderCommand");
			
			cmd.setFilterType(LogReaderCommand.VALUE_REGEX_CONTAIN);
			cmd.setFilter("0");
			cmd.setNoOfLines(700);

			// pass in newly created objects, expect no errors.
			testInstance.validate(cmd, errors);
			assertEquals("Case07: Not expecting errors with newly initialised objects", 0, errors.getErrorCount());
		}
		catch (Exception e)
		{
			String message = e.getClass().toString() + " - " + e.getMessage();
			log.debug(message);
			fail(message);
		}
	}

	@Test
	public final void testValidateCase08() {
		try {
			LogReaderCommand cmd = new LogReaderCommand();
			Errors errors = new BindException(cmd, "LogReaderCommand");
			
			cmd.setFilterType(LogReaderCommand.VALUE_REGEX_CONTAIN);
			cmd.setFilter("");
			cmd.setNoOfLines(700);

			// pass in newly created objects, expect no errors.
			testInstance.validate(cmd, errors);
			assertEquals("Case08: Expecting 1 errors", 1, errors.getErrorCount());
			assertEquals("Case08: Expecting error[0] for field Filter", true, errors.getAllErrors().toArray()[0].toString().contains("Regular Expression is a required field"));
		}
		catch (Exception e)
		{
			String message = e.getClass().toString() + " - " + e.getMessage();
			log.debug(message);
			fail(message);
		}
	}

	@Test
	public final void testValidateCase09() {
		try {
			LogReaderCommand cmd = new LogReaderCommand();
			Errors errors = new BindException(cmd, "LogReaderCommand");
			
			cmd.setFilterType(LogReaderCommand.VALUE_REGEX_INDENT);
			cmd.setFilter("0");
			cmd.setNoOfLines(700);

			// pass in newly created objects, expect no errors.
			testInstance.validate(cmd, errors);
			assertEquals("Case09: Not expecting errors with newly initialised objects", 0, errors.getErrorCount());
		}
		catch (Exception e)
		{
			String message = e.getClass().toString() + " - " + e.getMessage();
			log.debug(message);
			fail(message);
		}
	}

	@Test
	public final void testValidateCase10() {
		try {
			LogReaderCommand cmd = new LogReaderCommand();
			Errors errors = new BindException(cmd, "LogReaderCommand");
			
			cmd.setFilterType(LogReaderCommand.VALUE_REGEX_INDENT);
			cmd.setFilter("");
			cmd.setNoOfLines(700);

			// pass in newly created objects, expect no errors.
			testInstance.validate(cmd, errors);
			assertEquals("Case10: Expecting 1 errors", 1, errors.getErrorCount());
			assertEquals("Case10: Expecting error[0] for field Filter", true, errors.getAllErrors().toArray()[0].toString().contains("Regular Expression is a required field"));
		}
		catch (Exception e)
		{
			String message = e.getClass().toString() + " - " + e.getMessage();
			log.debug(message);
			fail(message);
		}
	}

}
