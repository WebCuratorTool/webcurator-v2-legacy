package org.webcurator.test;


import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.AfterClass;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Base class for all junit tests within WCT. Provides a SecurityContext and logged on user
 * and wraps an instance of the class being tested (Generic class T)
 * @author Kevin Urwin
 */
public class BaseWCTHarvestAgentTest<T> {

	protected static Log log = LogFactory.getLog(BaseWCTHarvestAgentTest.class);
	private static int testCount = 0;
	private static String testClassName = "";
	
	private Class<T> clazz = null;
	protected T testInstance = null;
	protected String testFile = "";

    /**
     * @param clazz a Class<T> object of the class being tested
     * @param testFile the path of the XML file containing test data
     */
	public BaseWCTHarvestAgentTest(Class<T> clazz) {
		this.clazz = clazz;
		testClassName = getClass().getName();
	}

    /**
     * Called by JUnit once for all tests (@BeforeClass) before instantiation 
     * of the test class. This method establishes the security context and
     * creates the logged in user "TestUser". This method can be overridden
     * without the need to use JUnit tags, but be sure to call super.initialise()
     * from within the overridden method if a logged in user id is required  
	 * @throws java.lang.Exception
     */
	@BeforeClass
	public static void initialise() throws Exception {
		testCount = 0;
		testClassName = "";
	}
	
    /**
     * Called by JUnit once for all tests (@AfterClass) after destruction 
     * of the test class. This method has no functionality in BaseWCTTest
     * but can be overridden without the need to use JUnit tags if required  
	 * @throws java.lang.Exception
     */
	@AfterClass
	public static void terminate() throws Exception {
		log.debug("Terminating "+testClassName);
		log.debug("");
		testClassName = "";
		testCount = 0;
	}
	
    /**
     * Called by JUnit once for each test (@Before) after instantiation 
     * of the test class. This method instantiates a new instance of the
     * class under test called testInstance. If more initialisation is required,
     * this method can be overridden  
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		if(testCount == 0)
		{
			testCount++;
			log.debug("Starting "+testClassName);
			log.debug("to Test: "+clazz.getName());
			log.debug("    Performing Test: "+testCount);
		}
		else
		{
			testCount++;
			log.debug("    Performing Test: "+testCount);
		}
		testInstance = clazz.newInstance();
	}

    /**
     * Called by JUnit once for each test (@After) after instantiation 
     * of the test class. This method destroys the instance of the
     * class under test by setting testInstance = null. If more tear down
     * is required this method can be overridden  
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		testInstance = null;
	}

	
}
