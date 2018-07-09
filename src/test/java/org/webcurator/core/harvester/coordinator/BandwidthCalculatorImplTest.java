/**
 * 
 */
package org.webcurator.core.harvester.coordinator;

import java.util.ArrayList;
import java.util.HashMap;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.webcurator.domain.model.core.TargetInstance;

/**
 * @author oakleigh_sk
 *
 */
public class BandwidthCalculatorImplTest extends TestCase {

	private BandwidthCalculator underTest;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		underTest = new BandwidthCalculatorImpl();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link org.webcurator.core.harvester.coordinator.underTest#calculateBandwidthAllocation(java.util.List, long, int)}.
	 */
	public final void testSingleStdTI() {
		TargetInstance ti = new TargetInstance();
		ti.setOid(1L);
						
		ArrayList<TargetInstance> runningTIs = new ArrayList<TargetInstance>();
		runningTIs.add(ti);
		
		HashMap<Long, TargetInstance> results = underTest.calculateBandwidthAllocation(runningTIs, 100, 80);
		
		assertTrue("Wrong number of results returned", results.size() == 1);	
		assertNotNull("Target Instance passed in does not exist in the result", results.get(new Long(1)));
		ti = (TargetInstance) results.get(new Long(1));
		assertEquals("Target Instances allocated bandwidth does not match", 100, ti.getAllocatedBandwidth().longValue());
	}
	
	public final void testTwoStdTI() {
		ArrayList<TargetInstance> runningTIs = new ArrayList<TargetInstance>();
		
		TargetInstance ti = new TargetInstance();
		ti.setOid(1L);
		runningTIs.add(ti);
		
		ti = new TargetInstance();
		ti.setOid(2L);
		runningTIs.add(ti);
		
		HashMap<Long, TargetInstance> results = underTest.calculateBandwidthAllocation(runningTIs, 100, 80);
		
		assertTrue("Wrong number of results returned", results.size() == 2);	
		assertNotNull("Target Instance passed in does not exist in the result", results.get(new Long(1)));
		assertNotNull("Target Instance passed in does not exist in the result", results.get(new Long(2)));
		ti = (TargetInstance) results.get(new Long(1));
		assertEquals("Target Instances allocated bandwidth does not match", 50, ti.getAllocatedBandwidth().longValue());
		ti = (TargetInstance) results.get(new Long(2));
		assertEquals("Target Instances allocated bandwidth does not match", 50, ti.getAllocatedBandwidth().longValue());
	}
	
	public final void testSingleBandwidthPercentTI() {
		ArrayList<TargetInstance> runningTIs = new ArrayList<TargetInstance>();
		
		TargetInstance ti = new TargetInstance();
		ti.setOid(1L);
		ti.setBandwidthPercent(50);
		runningTIs.add(ti);
				
		HashMap<Long, TargetInstance> results = underTest.calculateBandwidthAllocation(runningTIs, 100, 80);
		
		assertTrue("Wrong number of results returned", results.size() == 1);	
		assertNotNull("Target Instance passed in does not exist in the result", results.get(new Long(1)));
		ti = (TargetInstance) results.get(new Long(1));
		assertEquals("Target Instances allocated bandwidth does not match ", 100, ti.getAllocatedBandwidth().longValue());
	}
	
	public final void testTwoBandwidthPercentTI() {
		ArrayList<TargetInstance> runningTIs = new ArrayList<TargetInstance>();
		
		TargetInstance ti = new TargetInstance();
		ti.setOid(1L);
		ti.setBandwidthPercent(50);
		runningTIs.add(ti);
		
		ti = new TargetInstance();
		ti.setOid(2L);
		ti.setBandwidthPercent(50);
		runningTIs.add(ti);
		
		HashMap<Long, TargetInstance> results = underTest.calculateBandwidthAllocation(runningTIs, 100, 80);
		
		assertTrue("Wrong number of results returned", results.size() == 2);	
		assertNotNull("Target Instance passed in does not exist in the result", results.get(new Long(1)));
		assertNotNull("Target Instance passed in does not exist in the result", results.get(new Long(2)));
		ti = (TargetInstance) results.get(new Long(1));
		assertEquals("Target Instances allocated bandwidth does not match", 50, ti.getAllocatedBandwidth().longValue());
		ti = (TargetInstance) results.get(new Long(2));
		assertEquals("Target Instances allocated bandwidth does not match", 50, ti.getAllocatedBandwidth().longValue());
	}
	
	public final void testOneBWPTIOneStdTI() {
		ArrayList<TargetInstance> runningTIs = new ArrayList<TargetInstance>();
		
		TargetInstance ti = new TargetInstance();
		ti.setOid(1L);
		ti.setBandwidthPercent(50);
		runningTIs.add(ti);
		
		ti = new TargetInstance();
		ti.setOid(2L);
		runningTIs.add(ti);
				
		HashMap<Long, TargetInstance> results = underTest.calculateBandwidthAllocation(runningTIs, 100, 80);
		
		assertTrue("Wrong number of results returned", results.size() == 2);	
		assertNotNull("Target Instance passed in does not exist in the result", results.get(new Long(1)));
		assertNotNull("Target Instance passed in does not exist in the result", results.get(new Long(2)));
		ti = (TargetInstance) results.get(new Long(1));
		assertEquals("Target Instances allocated bandwidth does not match", 50, ti.getAllocatedBandwidth().longValue());
		ti = (TargetInstance) results.get(new Long(2));
		assertEquals("Target Instances allocated bandwidth does not match", 50, ti.getAllocatedBandwidth().longValue());		
	}
	
	public final void testOneBWPTITwoStdTI() {
		ArrayList<TargetInstance> runningTIs = new ArrayList<TargetInstance>();
		
		TargetInstance ti = new TargetInstance();
		ti.setOid(1L);
		ti.setBandwidthPercent(50);
		runningTIs.add(ti);
		
		ti = new TargetInstance();
		ti.setOid(2L);
		runningTIs.add(ti);
		
		ti = new TargetInstance();
		ti.setOid(3L);
		runningTIs.add(ti);
				
		HashMap<Long, TargetInstance> results = underTest.calculateBandwidthAllocation(runningTIs, 100, 80);
		
		assertTrue("Wrong number of results returned", results.size() == 3);	
		assertNotNull("Target Instance passed in does not exist in the result", results.get(new Long(1)));
		assertNotNull("Target Instance passed in does not exist in the result", results.get(new Long(2)));
		assertNotNull("Target Instance passed in does not exist in the result", results.get(new Long(3)));
		ti = (TargetInstance) results.get(new Long(1));
		assertEquals("Target Instances allocated bandwidth does not match", 50, ti.getAllocatedBandwidth().longValue());
		ti = (TargetInstance) results.get(new Long(2));
		assertEquals("Target Instances allocated bandwidth does not match", 25, ti.getAllocatedBandwidth().longValue());		
		ti = (TargetInstance) results.get(new Long(3));
		assertEquals("Target Instances allocated bandwidth does not match", 25, ti.getAllocatedBandwidth().longValue());
	}
	
	public final void testOneStdTITwoBWPTI() {
		ArrayList<TargetInstance> runningTIs = new ArrayList<TargetInstance>();
		
		TargetInstance ti = new TargetInstance();
		ti.setOid(1L);
		ti.setBandwidthPercent(50);
		runningTIs.add(ti);
		
		ti = new TargetInstance();
		ti.setOid(2L);
		ti.setBandwidthPercent(50);
		runningTIs.add(ti);
		
		ti = new TargetInstance();
		ti.setOid(3L);
		runningTIs.add(ti);
				
		HashMap<Long, TargetInstance> results = underTest.calculateBandwidthAllocation(runningTIs, 100, 80);
		
		assertTrue("Wrong number of results returned", results.size() == 3);	
		assertNotNull("Target Instance passed in does not exist in the result", results.get(new Long(1)));
		assertNotNull("Target Instance passed in does not exist in the result", results.get(new Long(2)));
		assertNotNull("Target Instance passed in does not exist in the result", results.get(new Long(3)));
		ti = (TargetInstance) results.get(new Long(1));
		assertEquals("Target Instances allocated bandwidth does not match", 40, ti.getAllocatedBandwidth().longValue());
		ti = (TargetInstance) results.get(new Long(2));
		assertEquals("Target Instances allocated bandwidth does not match", 40, ti.getAllocatedBandwidth().longValue());		
		ti = (TargetInstance) results.get(new Long(3));
		assertEquals("Target Instances allocated bandwidth does not match", 20, ti.getAllocatedBandwidth().longValue());
	}
	
	public final void testTwoBWPTITwoStdTI() {
		ArrayList<TargetInstance> runningTIs = new ArrayList<TargetInstance>();
		
		TargetInstance ti = new TargetInstance();
		ti.setOid(1L);
		ti.setBandwidthPercent(50);
		runningTIs.add(ti);
		
		ti = new TargetInstance();
		ti.setOid(2L);
		ti.setBandwidthPercent(50);
		runningTIs.add(ti);
		
		ti = new TargetInstance();
		ti.setOid(3L);
		runningTIs.add(ti);
		
		ti = new TargetInstance();
		ti.setOid(4L);
		runningTIs.add(ti);
				
		HashMap<Long, TargetInstance> results = underTest.calculateBandwidthAllocation(runningTIs, 100, 80);
		
		assertTrue("Wrong number of results returned", results.size() == 4);	
		assertNotNull("Target Instance passed in does not exist in the result", results.get(new Long(1)));
		assertNotNull("Target Instance passed in does not exist in the result", results.get(new Long(2)));
		assertNotNull("Target Instance passed in does not exist in the result", results.get(new Long(3)));
		assertNotNull("Target Instance passed in does not exist in the result", results.get(new Long(4)));
		ti = (TargetInstance) results.get(new Long(1));
		assertEquals("Target Instances allocated bandwidth does not match", 40, ti.getAllocatedBandwidth().longValue());
		ti = (TargetInstance) results.get(new Long(2));
		assertEquals("Target Instances allocated bandwidth does not match", 40, ti.getAllocatedBandwidth().longValue());		
		ti = (TargetInstance) results.get(new Long(3));
		assertEquals("Target Instances allocated bandwidth does not match", 10, ti.getAllocatedBandwidth().longValue());
		ti = (TargetInstance) results.get(new Long(3));
		assertEquals("Target Instances allocated bandwidth does not match", 10, ti.getAllocatedBandwidth().longValue());
	}

}
