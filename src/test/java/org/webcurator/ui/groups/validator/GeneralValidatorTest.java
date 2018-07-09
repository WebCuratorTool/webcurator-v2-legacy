package org.webcurator.ui.groups.validator;

import static org.junit.Assert.*;

import org.junit.Test;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.webcurator.test.*;
import org.webcurator.ui.groups.command.*;
import java.util.*;
import org.webcurator.domain.model.core.*; 

public class GeneralValidatorTest extends BaseWCTTest<GeneralValidator>{

	public GeneralValidatorTest()
	{
		super(GeneralValidator.class, "");
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
	public final void testSupports() {
		assertTrue(testInstance.supports(GeneralCommand.class));
	}

	@Test
	public void testValidateNullNameField() {
		try {
			GeneralCommand cmd = new GeneralCommand();
			Errors errors = new BindException(cmd, "GeneralCommand");
			cmd.setEditMode(true);
			cmd.setName(null);
			cmd.setFromDate(new Date());
			cmd.setSubGroupType("Sub-Group");
			cmd.setSubGroupSeparator(" > ");

			// pass in newly created objects, expect no errors.
			testInstance.validate(cmd, errors);
			assertEquals("Expecting 1 errors", 1, errors.getErrorCount());
			assertEquals("Expecting error[0] for field Name", true, errors.getAllErrors().toArray()[0].toString().contains("Name is a required field"));
		}
		catch (Exception e)
		{
			String message = e.getClass().toString() + " - " + e.getMessage();
			log.debug(message);
			fail(message);
		}
	}
	
	@Test
	public void testValidateNameContainsSeparatorField() {
		try {
			GeneralCommand cmd = new GeneralCommand();
			Errors errors = new BindException(cmd, "GeneralCommand");
			cmd.setEditMode(true);
			cmd.setName("Test > test");
			cmd.setFromDate(new Date());
			cmd.setSubGroupType("Sub-Group");
			cmd.setSubGroupSeparator(" > ");

			// pass in newly created objects, expect no errors.
			testInstance.validate(cmd, errors);
			assertEquals("Expecting 1 errors", 1, errors.getErrorCount());
			assertEquals("Expecting error[0] for field Name", true, errors.getAllErrors().toArray()[0].toString().contains("cannot be a sub-string of Name"));
		}
		catch (Exception e)
		{
			String message = e.getClass().toString() + " - " + e.getMessage();
			log.debug(message);
			fail(message);
		}
	}
	
	@Test
	public final void testValidateNullFromDate() {
		try {
			GeneralCommand cmd = new GeneralCommand();
			Errors errors = new BindException(cmd, "GeneralCommand");
			cmd.setEditMode(true);
			cmd.setName("TestName");
			cmd.setFromDate(null);
			cmd.setSubGroupType("Sub-Group");
			cmd.setSubGroupSeparator(" > ");

			
			// pass in newly created objects, expect no errors.
			testInstance.validate(cmd, errors);
			assertEquals("Not expecting errors with null from date", 0, errors.getErrorCount());
		}
		catch (Exception e)
		{
			String message = e.getClass().toString() + " - " + e.getMessage();
			log.debug(message);
			fail(message);
		}
	}

	@Test
	public void testValidateMaxNameLength() {
		try {
			GeneralCommand cmd = new GeneralCommand();
			Errors errors = new BindException(cmd, "GeneralCommand");
			cmd.setEditMode(true);
			cmd.setName(makeLongString(GeneralCommand.CNST_MAX_LEN_NAME+1, 'X'));
			cmd.setFromDate(new Date());
			cmd.setSubGroupType("Sub-Group");
			cmd.setSubGroupSeparator(" > ");
			testInstance.validate(cmd, errors);
			assertEquals("Case02: Expecting 1 errors", 1, errors.getErrorCount());
			assertEquals("Case02: Expecting error[0] for field Name", true, errors.getAllErrors().toArray()[0].toString().contains("Name is too long"));
		}
		catch (Exception e)
		{
			String message = e.getClass().toString() + " - " + e.getMessage();
			log.debug(message);
			fail(message);
		}
	}
	

	@Test
	public void testValidateMaxDescLength() {
		try {
			GeneralCommand cmd = new GeneralCommand();
			Errors errors = new BindException(cmd, "GeneralCommand");
			cmd.setEditMode(true);
			cmd.setName("TestName");
			cmd.setFromDate(new Date());
			cmd.setDescription(makeLongString(GeneralCommand.CNST_MAX_LEN_DESC+1, 'X'));
			cmd.setSubGroupType("Sub-Group");
			cmd.setSubGroupSeparator(" > ");
			testInstance.validate(cmd, errors);
			assertEquals("Case02: Expecting 1 errors", 1, errors.getErrorCount());
			assertEquals("Case02: Expecting error[0] for field Description", true, errors.getAllErrors().toArray()[0].toString().contains("Description is too long"));
		}
		catch (Exception e)
		{
			String message = e.getClass().toString() + " - " + e.getMessage();
			log.debug(message);
			fail(message);
		}
	}

	@Test
	public void testValidateMaxOwnerLength() {
		try {
			GeneralCommand cmd = new GeneralCommand();
			Errors errors = new BindException(cmd, "GeneralCommand");
			cmd.setEditMode(true);
			cmd.setName("TestName");
			cmd.setFromDate(new Date());
			cmd.setOwnershipMetaData(makeLongString(GeneralCommand.CNST_MAX_LEN_OWNER_INFO+1, 'X'));
			cmd.setSubGroupType("Sub-Group");
			cmd.setSubGroupSeparator(" > ");
			testInstance.validate(cmd, errors);
			assertEquals("Case02: Expecting 1 errors", 1, errors.getErrorCount());
			assertEquals("Case02: Expecting error[0] for field ownershipMetaData", true, errors.getAllErrors().toArray()[0].toString().contains("Owner info is too long"));
		}
		catch (Exception e)
		{
			String message = e.getClass().toString() + " - " + e.getMessage();
			log.debug(message);
			fail(message);
		}
	}

	@Test
	public void testValidateMaxTypeLength() {
		try {
			GeneralCommand cmd = new GeneralCommand();
			Errors errors = new BindException(cmd, "GeneralCommand");
			cmd.setEditMode(true);
			cmd.setName("TestName");
			cmd.setFromDate(new Date());
			cmd.setType(makeLongString(TargetGroup.MAX_TYPE_LENGTH+1, 'X'));
			cmd.setSubGroupType("Sub-Group");
			cmd.setSubGroupSeparator(" > ");
			testInstance.validate(cmd, errors);
			assertEquals("Case02: Expecting 1 errors", 1, errors.getErrorCount());
			assertEquals("Case02: Expecting error[0] for field Type", true, errors.getAllErrors().toArray()[0].toString().contains("Group Type is too long."));
		}
		catch (Exception e)
		{
			String message = e.getClass().toString() + " - " + e.getMessage();
			log.debug(message);
			fail(message);
		}
	}

	@Test
	public void testValidateSubGroupParent1() {
		try {
			GeneralCommand cmd = new GeneralCommand();
			Errors errors = new BindException(cmd, "GeneralCommand");
			cmd.setEditMode(true);
			cmd.setName("TestName");
			cmd.setFromDate(new Date());
			cmd.setSubGroupType("Sub-Group");
			cmd.setSubGroupSeparator(" > ");
			cmd.setType("Sub-Group");
			testInstance.validate(cmd, errors);
			assertEquals("Expecting 1 errors", 1, errors.getErrorCount());
			assertEquals("Expecting error[0] for field Parent Group", true, errors.getAllErrors().toArray()[0].toString().contains("Parent Group is a required field"));
		}
		catch (Exception e)
		{
			String message = e.getClass().toString() + " - " + e.getMessage();
			log.debug(message);
			fail(message);
		}
	}
	
	@Test
	public void testValidateSubGroupParent2() {
		try {
			GeneralCommand cmd = new GeneralCommand();
			Errors errors = new BindException(cmd, "GeneralCommand");
			cmd.setEditMode(true);
			cmd.setName("TestName");
			cmd.setFromDate(new Date());
			cmd.setSubGroupType("Sub-Group");
			cmd.setSubGroupSeparator(" > ");
			cmd.setType("Sub-Group");
			cmd.setAction(GeneralCommand.ACTION_ADD_PARENT);
			testInstance.validate(cmd, errors);
			assertEquals("Expecting 0 errors", 0, errors.getErrorCount());
		}
		catch (Exception e)
		{
			String message = e.getClass().toString() + " - " + e.getMessage();
			log.debug(message);
			fail(message);
		}
	}
	
}
