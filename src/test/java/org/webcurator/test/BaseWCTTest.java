package org.webcurator.test;

import java.util.*;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.AfterClass;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.webcurator.core.util.*;
import org.acegisecurity.GrantedAuthority;
import org.acegisecurity.context.*;
import org.acegisecurity.providers.*;
import org.webcurator.domain.MockUserRoleDAO;
import org.webcurator.domain.model.auth.User;
import org.webcurator.domain.model.auth.Role;
import org.webcurator.domain.model.auth.Privilege;
import org.webcurator.domain.model.auth.RolePrivilege;
import org.springframework.web.context.MockWebApplicationContext;

/**
 * Base class for all junit tests within WCT. Provides a SecurityContext and logged on user
 * and wraps an instance of the class being tested (Generic class T)
 * @author Kevin Urwin
 */
public class BaseWCTTest<T> {

	protected static Log log = LogFactory.getLog(BaseWCTTest.class);
	private static String baseTestFile = "src/test/java/org/webcurator/test/BaseWCTTest.xml";
	private static int testCount = 0;
	private static String testClassName = "";
	private boolean autoInstantiate = true;
	
	private Class<T> clazz = null;
	protected T testInstance = null;
	protected String testFile = "";

    /**
     * @param clazz a Class<T> object of the class being tested
     * @param testFile the path of the XML file containing test data
     */
	public BaseWCTTest(Class<T> clazz, String testFile) {
		this.clazz = clazz;
		this.testFile = testFile;
		this.autoInstantiate = true;
		testClassName = getClass().getName();
	}

    /**
     * @param clazz a Class<T> object of the class being tested
     * @param testFile the path of the XML file containing test data
     */
	public BaseWCTTest(Class<T> clazz, String testFile, boolean autoInstantiate) {
		this.clazz = clazz;
		this.testFile = testFile;
		this.autoInstantiate = autoInstantiate;
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
		
		try
		{
			TestingAuthenticationToken testToken = new TestingAuthenticationToken(
			"TestUser", "TestUser", new GrantedAuthority[] {});	
			
			testToken.setAuthenticated(true);
			
			MockUserRoleDAO dao = new MockUserRoleDAO(baseTestFile);
			
			testToken.setDetails(dao.getCurrentUser());
	
			// Create and store the Acegi SecurityContext into the SecurityContextHolder.
			SecurityContext securityContext = new SecurityContextImpl();
			securityContext.setAuthentication(testToken);
			SecurityContextHolder.setContext(securityContext);
			
			// Create a MockApplicationContext
			ApplicationContextFactory.setWebApplicationContext(new MockWebApplicationContext());
		}
		catch(Exception e)
		{
			if(log.isErrorEnabled())
			{
				log.error("BaseWCTTest: Failed to create current user '"+AuthUtil.getRemoteUser()+"'");
			}
			throw e;
		}
	}
	
    /**
     * Allows privileges to be added to the current user after instantiation
     * Testing can be managed such that functionality is tested before and 
     * after a user has a given privilege. Uses Privilege.SCOPE_ALL for scope.
	 * @param privilege the privilege to add
     */
	protected void addCurrentUserPrivilege(String privilege)
	{
		addCurrentUserPrivilege(Privilege.SCOPE_ALL, privilege);
	}
	
    /**
     * Allows privileges to be added to the current user after instantiation
     * Testing can be managed such that functionality is tested before and 
     * after a user has a given privilege  
	 * @param scope the scope of the privilege to add
	 * @param privilege the privilege to add
     */
	protected void addCurrentUserPrivilege(int scope, String privilege)
	{
		User currentUser = AuthUtil.getRemoteUserObject();
		Role newRole = new Role();
		Set<RolePrivilege> rolePrivileges = new HashSet<RolePrivilege>();
		RolePrivilege rp = new RolePrivilege();
		rp.setPrivilege(privilege);
		rp.setPrivilegeScope(scope);
		rp.setRole(newRole);
		rolePrivileges.add(rp);
		newRole.setName(privilege+scope);
		newRole.setAgency(currentUser.getAgency());
		newRole.setRolePrivileges(rolePrivileges);
		newRole.setUsers(new HashSet<User>());
		currentUser.addRole(newRole);
	}
	
    /**
     * Allows privileges to be removed from the current user after instantiation
     * Testing can be managed such that functionality is tested before and 
     * after a user has a given privilege. Note this function will only remove 
     * privileges added using the addCurrentUserPrivilege() function. To remove
     * privileges added via the testFile use removeAllCurrentUserPrivileges()
	 * @param scope the scope of the privilege to remove
	 * @param privilege the privilege to remove
     */
	protected void removeCurrentUserPrivilege(int scope, String privilege)
	{
		User currentUser = AuthUtil.getRemoteUserObject();
		Set<Role> roles = currentUser.getRoles();
		Iterator<Role> it = roles.iterator();
		while(it.hasNext())
		{
			Role r = it.next();
			if(r.getName().equals(privilege+scope))
			{
				currentUser.removeRole(r);
			}
		}
	}
	
    /**
     * Removes all privileges from the current user after instantiation
     */
	protected void removeAllCurrentUserPrivileges()
	{
		User currentUser = AuthUtil.getRemoteUserObject();
		currentUser.removeAllRoles();
	}
	
    /**
     * Called by JUnit once for all tests (@AfterClass) after destruction 
     * of the test class. This method has no functionality in BaseWCTTest
     * but can be overridden without the need to use JUnit tags if required  
	 * @throws java.lang.Exception
     */
	@AfterClass
	public static void terminate() throws Exception {
		if(log.isInfoEnabled())
		{
			log.info("Terminating "+testClassName);
			log.info("");
		}
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
			if(log.isInfoEnabled())
			{
				log.info("Starting "+testClassName);
				log.info("to Test: "+clazz.getName());
				log.info("    Performing Test: "+testCount);
			}
		}
		else
		{
			testCount++;
			if(log.isInfoEnabled())
			{
				log.info("    Performing Test: "+testCount);
			}
		}
		
		if(autoInstantiate)
		{
			testInstance = clazz.newInstance();
		}
		else
		{
			testInstance = null;
		}
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
