package org.webcurator.test;

import junit.framework.JUnit4TestAdapter;

import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	//org.webcurator.something.FirstTest.class,
    //org.webcurator.something.SecondTest.class
})

@Ignore
public class AllUITests {
    // the class remains completely empty, 
    // being used only as a holder for the above annotations
	
	// *except* that we need to add the following method to
	// allow ant's <junit> task to call this JUnit 4.x 
	// style test suite in batch mode, until the ant developers
	// get around to solving the compatibility issues!
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(AllUITests.class);
	}
}
