package org.webcurator.test;

import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import junit.framework.TestCase;
import junit.framework.JUnit4TestAdapter;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	AllNonUITests.class,
	AllUITests.class
})
@Ignore
public class AllTests extends TestCase {
    // the class remains completely empty, 
    // being used only as a holder for the above annotations
	
	// *except* that we need to add the following method to
	// allow ant's <junit> task to call this JUnit 4.x 
	// style test suite in batch mode, until the ant developers
	// get around to solving the compatibility issues!
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(AllTests.class);
	}
}
