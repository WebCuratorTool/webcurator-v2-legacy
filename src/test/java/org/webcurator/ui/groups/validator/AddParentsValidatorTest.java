package org.webcurator.ui.groups.validator;

import static org.junit.Assert.*;

import org.junit.Test;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.webcurator.test.BaseWCTTest;
import org.webcurator.ui.groups.command.AddParentsCommand;

public class AddParentsValidatorTest extends BaseWCTTest<AddParentsValidator> {

	public AddParentsValidatorTest() {
		super(AddParentsValidator.class, "");
	}

	@Test
	public final void testValidate() {
		try {
			AddParentsCommand cmd = new AddParentsCommand();
			Errors errors = new BindException(cmd, "AddParentsCommand");
			cmd.setActionCmd(AddParentsCommand.ACTION_ADD_PARENTS);

			// pass in newly created objects, expect no errors.
			testInstance.validate(cmd, errors);
			assertEquals("Expecting 1 errors", 1, errors.getErrorCount());
			assertEquals("Expecting error[0]", true, errors.getAllErrors().toArray()[0].toString().contains("groups.errors.addparents.must_select"));
		}
		catch (Exception e)
		{
			String message = e.getClass().toString() + " - " + e.getMessage();
			log.debug(message);
			fail(message);
		}
	}

}
