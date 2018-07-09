package org.webcurator.ui.groups.validator;

import static org.junit.Assert.*;

import org.junit.Test;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.webcurator.test.BaseWCTTest;
import org.webcurator.ui.groups.command.AddMembersCommand;

public class AddMembersValidatorTest extends BaseWCTTest<AddMembersValidator> {

	public AddMembersValidatorTest() {
		super(AddMembersValidator.class, "");
	}

	@Test
	public final void testValidate() {
		try {
			AddMembersCommand cmd = new AddMembersCommand();
			Errors errors = new BindException(cmd, "AddMembersCommand");
			cmd.setActionCmd(AddMembersCommand.ACTION_ADD_MEMBERS);

			// pass in newly created objects, expect no errors.
			testInstance.validate(cmd, errors);
			assertEquals("Expecting 1 errors", 1, errors.getErrorCount());
			assertEquals("Expecting error[0]", true, errors.getAllErrors().toArray()[0].toString().contains("groups.errors.addmembers.must_select"));
		}
		catch (Exception e)
		{
			String message = e.getClass().toString() + " - " + e.getMessage();
			log.debug(message);
			fail(message);
		}
	}
}
