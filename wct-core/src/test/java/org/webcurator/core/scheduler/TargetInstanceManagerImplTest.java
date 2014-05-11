package org.webcurator.core.scheduler;

import static org.junit.Assert.*;

import java.util.*;

import org.junit.Test;
import org.webcurator.test.*;
import org.webcurator.domain.*;
import org.webcurator.domain.model.core.TargetInstance;
import org.webcurator.core.util.TestAuditor;
import org.webcurator.core.notification.*;
import org.webcurator.domain.model.core.*;

public class TargetInstanceManagerImplTest extends BaseWCTTest<TargetInstanceManagerImpl>{

	private TargetInstanceDAO tiDao = null;
	private ProfileDAO pDao = null;
	
	public TargetInstanceManagerImplTest()
	{
		super(TargetInstanceManagerImpl.class, 
				"src/test/java/org/webcurator/core/scheduler/targetinstancemanagerimpltest.xml");
	}
	
	public void setUp() throws Exception {
		super.setUp();
		testInstance.setAnnotationDAO(new MockAnnotationDAO(testFile));
		testInstance.setAuditor(new TestAuditor());
		testInstance.setInTrayManager(new MockInTrayManager(testFile));
		testInstance.setProfileDAO(pDao = new MockProfileDAO(testFile));
		testInstance.setTargetInstanceDao(tiDao = new MockTargetInstanceDAO(testFile));
	}
	
	@Test
	public final void testSaveRunningTargetInstance() {
		
		TargetInstance aTargetInstance = tiDao.load(5001L);
		
        // Update the state of the allocated Target Instance
		aTargetInstance.setActualStartTime(new Date());
        aTargetInstance.setState(TargetInstance.STATE_RUNNING);
        aTargetInstance.setHarvestServer("My Local Agent");
        
        Profile orig_p = aTargetInstance.getProfile();
        
        // Save the updated information.
        testInstance.save(aTargetInstance);

        //Check that the Profile is locked
        Profile new_p = aTargetInstance.getProfile();
        assertTrue(orig_p.getOid() != new_p.getOid());
        assertTrue(orig_p.getOid() == new_p.getOrigOid());
        assertFalse(orig_p.isLocked());
        assertTrue(new_p.isLocked());
        assertTrue(orig_p.getProfile().length() > 0);
        assertEquals(orig_p.getProfile(), new_p.getProfile());
	}
	
	@Test
	public final void testSaveQueuedTargetInstance() {
		
		TargetInstance aTargetInstance = tiDao.load(5001L);
		
        Profile orig_p = aTargetInstance.getProfile();
        
        // Save the updated information.
        testInstance.save(aTargetInstance);

        //Check that the Profile is unaffected
        Profile new_p = aTargetInstance.getProfile();
        assertTrue(orig_p.getOid() == new_p.getOid());
        assertNull(new_p.getOrigOid());
        assertFalse(orig_p.isLocked());
        assertFalse(new_p.isLocked());
        assertTrue(orig_p.getProfile().length() > 0);
        assertEquals(orig_p.getProfile(), new_p.getProfile());
	}
	
	@Test
	public final void testPurgeTargetInstance() {
		
		TargetInstance aTargetInstance = tiDao.load(5003L);
        assertFalse(aTargetInstance.isPurged());
        List<HarvestResult> results = aTargetInstance.getHarvestResults();
        assertNotNull(results);
        assertTrue(results.size() == 1);
        assertFalse(results.get(0).getResources().isEmpty());
		
        testInstance.purgeTargetInstance(aTargetInstance);

        assertTrue(aTargetInstance.isPurged());
        results = aTargetInstance.getHarvestResults();
        assertNotNull(results);
        assertTrue(results.size() == 1);
        assertTrue(results.get(0).getResources().isEmpty());
	}

}
