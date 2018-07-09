package org.webcurator.ui.report.validator;

import static org.junit.Assert.*;

import org.junit.Test;
import org.springframework.context.MockMessageSource;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.webcurator.test.BaseWCTTest;
import org.webcurator.ui.report.command.ReportCommand;
import org.webcurator.ui.util.DateUtils;

import java.util.*;

public class ReportValidatorTest extends BaseWCTTest<ReportValidator>{

	public ReportValidatorTest()
	{
		super(ReportValidator.class,"");
	}

	public void setUp() throws Exception 
	{
		super.setUp();
		DateUtils.get().setMessageSource(new MockMessageSource());
	}
	
	
	@Test
	public final void testSupports() {
		assertTrue(testInstance.supports(ReportCommand.class));
	}

	@Test
	public final void testValidateCase01() {
		try {
			ReportCommand cmd = new ReportCommand();
			Errors errors = new BindException(cmd, "ReportCommand");
			
			List<String> parameters = new ArrayList<String>();
			parameters.add("System Usage Report");
			parameters.add("startDate");
			parameters.add("01/01/2008");
			parameters.add("org.webcurator.core.report.parameter.DateParameter");
			parameters.add("Start Date");
			parameters.add("false");

			parameters.add("System Usage Report");
			parameters.add("endDate");
			parameters.add("31/12/2008");
			parameters.add("org.webcurator.core.report.parameter.DateParameter");
			parameters.add("End Date");
			parameters.add("false");

			parameters.add("System Usage Report");
			parameters.add("agency");
			parameters.add("All Agencies");
			parameters.add("org.webcurator.core.report.parameter.StringParameter");
			parameters.add("Agencies");
			parameters.add("true");

			cmd.setParameters(parameters);
			cmd.setSelectedReport("System Usage Report");

			// pass in parameters, expect no errors.
			testInstance.validate(cmd, errors);
			assertEquals("Case01: Not expecting errors with valid parameters", 0, errors.getErrorCount());
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
			ReportCommand cmd = new ReportCommand();
			Errors errors = new BindException(cmd, "ReportCommand");
			
			List<String> parameters = new ArrayList<String>();
			parameters.add("System Usage Report");
			parameters.add("startDate");
			parameters.add("");
			parameters.add("org.webcurator.core.report.parameter.DateParameter");
			parameters.add("Start Date");
			parameters.add("false");

			parameters.add("System Usage Report");
			parameters.add("endDate");
			parameters.add("");
			parameters.add("org.webcurator.core.report.parameter.DateParameter");
			parameters.add("End Date");
			parameters.add("false");

			parameters.add("System Usage Report");
			parameters.add("agency");
			parameters.add("All Agencies");
			parameters.add("org.webcurator.core.report.parameter.StringParameter");
			parameters.add("Agencies");
			parameters.add("true");

			cmd.setParameters(parameters);
			cmd.setSelectedReport("System Usage Report");

			// pass in parameters, expect two errors.
			testInstance.validate(cmd, errors);
			assertEquals("Case02: Expecting two errors with invalid parameters", 2, errors.getErrorCount());
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
			ReportCommand cmd = new ReportCommand();
			Errors errors = new BindException(cmd, "ReportCommand");
			
			List<String> parameters = new ArrayList<String>();
			parameters.add("System Activity Report");
			parameters.add("startDate");
			parameters.add("01/01/2008");
			parameters.add("org.webcurator.core.report.parameter.DateParameter");
			parameters.add("Start Date");
			parameters.add("false");

			parameters.add("System Activity Report");
			parameters.add("endDate");
			parameters.add("31/12/2008");
			parameters.add("org.webcurator.core.report.parameter.DateParameter");
			parameters.add("End Date");
			parameters.add("false");

			parameters.add("System Activity Report");
			parameters.add("agency");
			parameters.add("All Agencies");
			parameters.add("org.webcurator.core.report.parameter.StringParameter");
			parameters.add("Agencies");
			parameters.add("true");

			parameters.add("System Activity Report");
			parameters.add("user");
			parameters.add("All users");
			parameters.add("org.webcurator.core.report.parameter.StringParameter");
			parameters.add("Users");
			parameters.add("true");

			cmd.setParameters(parameters);
			cmd.setSelectedReport("System Activity Report");

			// pass in parameters, expect no errors.
			testInstance.validate(cmd, errors);
			assertEquals("Case03: Not expecting errors with valid parameters", 0, errors.getErrorCount());
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
			ReportCommand cmd = new ReportCommand();
			Errors errors = new BindException(cmd, "ReportCommand");
			
			List<String> parameters = new ArrayList<String>();
			parameters.add("System Activity Report");
			parameters.add("startDate");
			parameters.add("");
			parameters.add("org.webcurator.core.report.parameter.DateParameter");
			parameters.add("Start Date");
			parameters.add("false");

			parameters.add("System Activity Report");
			parameters.add("endDate");
			parameters.add("");
			parameters.add("org.webcurator.core.report.parameter.DateParameter");
			parameters.add("End Date");
			parameters.add("false");

			parameters.add("System Activity Report");
			parameters.add("agency");
			parameters.add("All Agencies");
			parameters.add("org.webcurator.core.report.parameter.StringParameter");
			parameters.add("Agencies");
			parameters.add("true");

			parameters.add("System Activity Report");
			parameters.add("user");
			parameters.add("All users");
			parameters.add("org.webcurator.core.report.parameter.StringParameter");
			parameters.add("Users");
			parameters.add("true");

			cmd.setParameters(parameters);
			cmd.setSelectedReport("System Activity Report");

			// pass in parameters, expect two errors.
			testInstance.validate(cmd, errors);
			assertEquals("Case04: Expecting two errors with invalid parameters", 2, errors.getErrorCount());
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
			ReportCommand cmd = new ReportCommand();
			Errors errors = new BindException(cmd, "ReportCommand");
			
			List<String> parameters = new ArrayList<String>();
			parameters.add("Crawler Activity Report");
			parameters.add("startDate");
			parameters.add("01/01/2008");
			parameters.add("org.webcurator.core.report.parameter.DateParameter");
			parameters.add("Start Date");
			parameters.add("false");

			parameters.add("Crawler Activity Report");
			parameters.add("endDate");
			parameters.add("31/12/2008");
			parameters.add("org.webcurator.core.report.parameter.DateParameter");
			parameters.add("End Date");
			parameters.add("false");

			cmd.setParameters(parameters);
			cmd.setSelectedReport("Crawler Activity Report");

			// pass in parameters, expect no errors.
			testInstance.validate(cmd, errors);
			assertEquals("Case05: Not expecting errors with valid parameters", 0, errors.getErrorCount());
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
			ReportCommand cmd = new ReportCommand();
			Errors errors = new BindException(cmd, "ReportCommand");
			
			List<String> parameters = new ArrayList<String>();
			parameters.add("Crawler Activity Report");
			parameters.add("startDate");
			parameters.add("");
			parameters.add("org.webcurator.core.report.parameter.DateParameter");
			parameters.add("Start Date");
			parameters.add("false");

			parameters.add("Crawler Activity Report");
			parameters.add("endDate");
			parameters.add("");
			parameters.add("org.webcurator.core.report.parameter.DateParameter");
			parameters.add("End Date");
			parameters.add("false");

			cmd.setParameters(parameters);
			cmd.setSelectedReport("Crawler Activity Report");

			// pass in parameters, expect two errors.
			testInstance.validate(cmd, errors);
			assertEquals("Case06: Expecting two errors with invalid parameters", 2, errors.getErrorCount());
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
			ReportCommand cmd = new ReportCommand();
			Errors errors = new BindException(cmd, "ReportCommand");
			
			List<String> parameters = new ArrayList<String>();
			parameters.add("Target/Group Schedules Report");
			parameters.add("agency");
			parameters.add("All agencies");
			parameters.add("org.webcurator.core.report.parameter.StringParameter");
			parameters.add("Agencies");
			parameters.add("true");

			parameters.add("Target/Group Schedules Report");
			parameters.add("users");
			parameters.add("All users");
			parameters.add("org.webcurator.core.report.parameter.StringParameter");
			parameters.add("Users");
			parameters.add("true");

			parameters.add("Target/Group Schedules Report");
			parameters.add("targettype");
			parameters.add("All target types");
			parameters.add("org.webcurator.core.report.parameter.StringParameter");
			parameters.add("Target Types");
			parameters.add("true");

			cmd.setParameters(parameters);
			cmd.setSelectedReport("Target/Group Schedules Report");

			// pass in parameters, expect no errors.
			testInstance.validate(cmd, errors);
			assertEquals("Case09: Not expecting errors with valid parameters", 0, errors.getErrorCount());
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
			ReportCommand cmd = new ReportCommand();
			Errors errors = new BindException(cmd, "ReportCommand");
			
			List<String> parameters = new ArrayList<String>();
			parameters.add("Summary Target Schedules Report");
			parameters.add("agency");
			parameters.add("All agencies");
			parameters.add("org.webcurator.core.report.parameter.StringParameter");
			parameters.add("Agencies");
			parameters.add("true");

			cmd.setParameters(parameters);
			cmd.setSelectedReport("Summary Target Schedules Report");

			// pass in parameters, expect no errors.
			testInstance.validate(cmd, errors);
			assertEquals("Case09: Not expecting errors with valid parameters", 0, errors.getErrorCount());
		}
		catch (Exception e)
		{
			String message = e.getClass().toString() + " - " + e.getMessage();
			log.debug(message);
			fail(message);
		}
	}
	
}
