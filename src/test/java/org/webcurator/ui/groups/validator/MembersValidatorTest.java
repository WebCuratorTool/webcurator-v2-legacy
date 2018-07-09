package org.webcurator.ui.groups.validator;

import static org.junit.Assert.*;

import org.junit.Test;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.webcurator.test.BaseWCTTest;
import org.webcurator.ui.groups.command.MembersCommand;

public class MembersValidatorTest extends BaseWCTTest<MembersValidator> {

	public MembersValidatorTest() {
		super(MembersValidator.class, "");
	}

	@Test
	public final void testValidate() {
		try {
			MembersCommand cmd = new MembersCommand();
			Errors errors = new BindException(cmd, "MembersCommand");
			cmd.setActionCmd(MembersCommand.ACTION_MOVE_TARGETS);

			// pass in newly created objects, expect no errors.
			testInstance.validate(cmd, errors);
			assertEquals("Expecting 1 errors", 1, errors.getErrorCount());
			assertEquals("Expecting error[0]", true, errors.getAllErrors().toArray()[0].toString().contains("groups.errors.members.must_select"));
		}
		catch (Exception e)
		{
			String message = e.getClass().toString() + " - " + e.getMessage();
			log.debug(message);
			fail(message);
		}
	}

}
