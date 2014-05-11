package org.webcurator.ui.target.validator;

import static org.junit.Assert.*;

import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.MockMessageSource;
import org.junit.Test;
import org.springframework.mock.web.*;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.webcurator.test.BaseWCTTest;
import org.webcurator.ui.admin.command.CreateUserCommand;
import org.webcurator.ui.target.command.*;
import org.webcurator.core.agency.*;
import org.webcurator.ui.util.*;
import org.webcurator.core.scheduler.*;
import org.webcurator.auth.AuthorityManagerImpl;
import org.webcurator.domain.model.core.*;
import org.webcurator.ui.target.validator.*;
import org.webcurator.core.harvester.coordinator.*;

public class TargetInstanceValidatorTest extends BaseWCTTest<TargetInstanceValidator>{

	public TargetInstanceValidatorTest()
	{
		super(TargetInstanceValidator.class,"");
	}
	
	private String makeLongString(int size, char fillChar) {
		final int SIZE = size ;
		StringBuffer sb = new StringBuffer (SIZE) ;
    
		for ( int i = 0 ; i < SIZE; i++) {
			sb.append(fillChar) ;
		}
		return sb.toString() ;
	}
	
	@Test
	public final void testTargetInstanceValidator() {
		assertTrue(testInstance != null);
	}

	@Test
	public final void testSupports() {
		assertTrue(testInstance.supports(TargetInstanceCommand.class));
		assertFalse(testInstance.supports(TargetInstanceProfileCommand.class));
	}


	@Test
	public void testValidateCase01() {
		try {
			TargetInstanceCommand cmd = new TargetInstanceCommand();
			TargetInstanceValidator validator = new TargetInstanceValidator();
			Errors errors = new BindException(cmd, "TargetInstanceCommand");
			
			cmd.setCmd(TargetInstanceCommand.ACTION_EDIT);
			cmd.set_tab_current_page("DISPLAY");
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
			TargetInstanceCommand cmd = new TargetInstanceCommand();
			TargetInstanceValidator validator = new TargetInstanceValidator();
			Errors errors = new BindException(cmd, "TargetInstanceCommand");
			
			cmd.setCmd(TargetInstanceCommand.ACTION_EDIT);
			cmd.set_tab_current_page("DISPLAY");
			// pass in displayNote string with 4000 chars..
			cmd.setDisplayNote(makeLongString(4000, 'X'));
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
			TargetInstanceCommand cmd = new TargetInstanceCommand();
			TargetInstanceValidator validator = new TargetInstanceValidator();
			Errors errors = new BindException(cmd, "TargetInstanceCommand");
			
			cmd.setCmd(TargetInstanceCommand.ACTION_EDIT);
			cmd.set_tab_current_page("DISPLAY");
			// pass in displayNote string greater than 4000 chars..
			cmd.setDisplayNote(makeLongString(4001, 'X'));
			validator.validate(cmd, errors);
			assertEquals("Case02: Expecting 1 errors", 1, errors.getErrorCount());
			assertEquals("Case02: Expecting error[0] for field Display Note", true, errors.getAllErrors().toArray()[0].toString().contains("Display note is too long"));
		}
		catch (Exception e)
		{
			String message = e.getClass().toString() + " - " + e.getMessage();
			log.debug(message);
			fail(message);
		}
	}

}
