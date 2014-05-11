package org.webcurator.ui.groups.validator;

import static org.junit.Assert.*;

import org.junit.Test;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.webcurator.test.BaseWCTTest;
import org.webcurator.ui.groups.command.MoveTargetsCommand;

public class MoveTargetsValidatorTest extends BaseWCTTest<MoveTargetsValidator> {

	public MoveTargetsValidatorTest() {
		super(MoveTargetsValidator.class, "");
	}

	@Test
	public final void testValidate() {
		try {
			MoveTargetsCommand cmd = new MoveTargetsCommand();
			Errors errors = new BindException(cmd, "MoveTargetsCommand");
			cmd.setActionCmd(MoveTargetsCommand.ACTION_MOVE_TARGETS);

			// pass in newly created objects, expect no errors.
			testInstance.validate(cmd, errors);
			assertEquals("Expecting 1 errors", 1, errors.getErrorCount());
			assertEquals("Expecting error[0]", true, errors.getAllErrors().toArray()[0].toString().contains("target.errors.addparents.must_select"));
		}
		catch (Exception e)
		{
			String message = e.getClass().toString() + " - " + e.getMessage();
			log.debug(message);
			fail(message);
		}
	}

}
