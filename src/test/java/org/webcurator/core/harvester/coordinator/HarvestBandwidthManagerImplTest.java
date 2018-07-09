package org.webcurator.core.harvester.coordinator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.webcurator.core.exceptions.WCTRuntimeException;
import org.webcurator.core.util.Auditor;
import org.webcurator.domain.HarvestCoordinatorDAO;
import org.webcurator.domain.TargetInstanceCriteria;
import org.webcurator.domain.TargetInstanceDAO;
import org.webcurator.domain.model.core.BandwidthRestriction;
import org.webcurator.domain.model.core.TargetInstance;
import org.webcurator.domain.model.dto.QueuedTargetInstanceDTO;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@RunWith(MockitoJUnitRunner.class)
public class HarvestBandwidthManagerImplTest {
	@Mock private Auditor mockAuditor;
	@Mock private HarvestAgentManager mockHarvestAgentManager;
	@Mock private HarvestCoordinatorDAO mockHarvestCoordinatorDao;
	@Mock private TargetInstanceDAO mockTargetInstanceDao;
	@Mock private BandwidthCalculator mockBandwidthCalculator;
	
	@InjectMocks private HarvestBandwidthManagerImpl underTest;

	@Test
	public void testSendBandWidthRestrictions() throws Exception {
		BandwidthRestriction mockBandwidthRestriction = mock(BandwidthRestriction.class);
		long maxBandwidth = 23L;
		int maxBandwidthPercent = 34;
		when(mockBandwidthRestriction.getBandwidth()).thenReturn(maxBandwidth);
		when(mockHarvestCoordinatorDao.getBandwidthRestriction(anyString(), any(Date.class))).thenReturn(mockBandwidthRestriction);
		TargetInstance mockTargetInstance = mock(TargetInstance.class);
		ArrayList<TargetInstance> running = Lists.newArrayList(mockTargetInstance);
		when(mockTargetInstanceDao.findTargetInstances(any(TargetInstanceCriteria.class))).thenReturn(running);
		HashMap<Long, TargetInstance> map = Maps.newHashMap();
		map.put(1L, mockTargetInstance);
		when(mockBandwidthCalculator.calculateBandwidthAllocation(running, maxBandwidth, maxBandwidthPercent)).thenReturn(map );
		underTest.setMaxBandwidthPercent(maxBandwidthPercent);
		underTest.sendBandWidthRestrictions();
		verify(mockTargetInstanceDao).findTargetInstances(any(TargetInstanceCriteria.class));
		verify(mockBandwidthCalculator).calculateBandwidthAllocation(running, maxBandwidth, maxBandwidthPercent);
		verify(mockHarvestAgentManager).restrictBandwidthFor(mockTargetInstance);
	}

	@Test
	public void testCalculateBandwidthAllocationTargetInstance() throws Exception {
		BandwidthRestriction mockBandwidthRestriction = mock(BandwidthRestriction.class);
		long maxBandwidth = 23L;
		int maxBandwidthPercent = 34;
		when(mockBandwidthRestriction.getBandwidth()).thenReturn(maxBandwidth);
		when(mockHarvestCoordinatorDao.getBandwidthRestriction(anyString(), any(Date.class))).thenReturn(mockBandwidthRestriction);
		TargetInstance mockTargetInstance = mock(TargetInstance.class);
		ArrayList<TargetInstance> running = Lists.newArrayList(mockTargetInstance);
		when(mockTargetInstanceDao.findTargetInstances(any(TargetInstanceCriteria.class))).thenReturn(running);
		HashMap<Long, TargetInstance> map = Maps.newHashMap();
		long tiOid = 1L;
		map.put(tiOid, mockTargetInstance);
		when(mockBandwidthCalculator.calculateBandwidthAllocation(running, maxBandwidth, maxBandwidthPercent)).thenReturn(map );
		underTest.setMaxBandwidthPercent(maxBandwidthPercent);
		HashMap<Long, TargetInstance> result = underTest.calculateBandwidthAllocation(mockTargetInstance);
		verify(mockTargetInstanceDao).findTargetInstances(any(TargetInstanceCriteria.class));
		verify(mockBandwidthCalculator).calculateBandwidthAllocation(running, maxBandwidth, maxBandwidthPercent);
		assertEquals(1, result.size());
		assertTrue(result.containsKey(tiOid));
		assertEquals(mockTargetInstance, result.get(tiOid));
	}

	@Test
	public void testCalculateBandwidthAllocation() throws Exception {
		BandwidthRestriction mockBandwidthRestriction = mock(BandwidthRestriction.class);
		long maxBandwidth = 23L;
		int maxBandwidthPercent = 34;
		when(mockBandwidthRestriction.getBandwidth()).thenReturn(maxBandwidth);
		when(mockHarvestCoordinatorDao.getBandwidthRestriction(anyString(), any(Date.class))).thenReturn(mockBandwidthRestriction);
		TargetInstance mockTargetInstance = mock(TargetInstance.class);
		ArrayList<TargetInstance> running = Lists.newArrayList(mockTargetInstance);
		when(mockTargetInstanceDao.findTargetInstances(any(TargetInstanceCriteria.class))).thenReturn(running);
		HashMap<Long, TargetInstance> map = Maps.newHashMap();
		long tiOid = 1L;
		map.put(tiOid, mockTargetInstance);
		when(mockBandwidthCalculator.calculateBandwidthAllocation(running, maxBandwidth, maxBandwidthPercent)).thenReturn(map );
		underTest.setMaxBandwidthPercent(maxBandwidthPercent);
		HashMap<Long, TargetInstance> result = underTest.calculateBandwidthAllocation();
		verify(mockTargetInstanceDao).findTargetInstances(any(TargetInstanceCriteria.class));
		verify(mockBandwidthCalculator).calculateBandwidthAllocation(running, maxBandwidth, maxBandwidthPercent);
		assertEquals(1, result.size());
		assertTrue(result.containsKey(tiOid));
		assertEquals(mockTargetInstance, result.get(tiOid));
	}

	@Test
	public void testGetCurrentGlobalMaxBandwidth() throws Exception {
		BandwidthRestriction mockBandwidthRestriction = mock(BandwidthRestriction.class);
		long maxBandwidth = 23L;
		when(mockBandwidthRestriction.getBandwidth()).thenReturn(maxBandwidth);
		when(mockHarvestCoordinatorDao.getBandwidthRestriction(anyString(), any(Date.class))).thenReturn(mockBandwidthRestriction);
		underTest.getCurrentGlobalMaxBandwidth();
		verify(mockHarvestCoordinatorDao).getBandwidthRestriction(anyString(), any(Date.class));
	}

	@Test
	public void testIsMiniumBandwidthAvailableGlobalBandwidthTooLow() throws Exception {
		BandwidthRestriction mockBandwidthRestriction = mock(BandwidthRestriction.class);
		long maxBandwidth = 23L;
		when(mockBandwidthRestriction.getBandwidth()).thenReturn(maxBandwidth);
		underTest.setMinimumBandwidth(24);
		
		QueuedTargetInstanceDTO mockQueuedTargetInstanceDTO = mock(QueuedTargetInstanceDTO.class);
		boolean result = underTest.isMiniumBandwidthAvailable(mockQueuedTargetInstanceDTO);
		assertFalse(result);
	}

	@Test
	public void testIsMiniumBandwidthAvailableGlobalBandwidthTooLowTargetInstance() throws Exception {
		BandwidthRestriction mockBandwidthRestriction = mock(BandwidthRestriction.class);
		long maxBandwidth = 23L;
		when(mockBandwidthRestriction.getBandwidth()).thenReturn(maxBandwidth);
		underTest.setMinimumBandwidth(24);
		
		TargetInstance mockTargetInstance = mock(TargetInstance.class);
		boolean result = underTest.isMiniumBandwidthAvailable(mockTargetInstance);
		assertFalse(result);
	}


	@Test
	public void testIsMiniumBandwidthAvailableTargetInstance() throws Exception {
		BandwidthRestriction mockBandwidthRestriction = mock(BandwidthRestriction.class);
		long maxBandwidth = 23L;
		when(mockBandwidthRestriction.getBandwidth()).thenReturn(maxBandwidth);
		when(mockHarvestCoordinatorDao.getBandwidthRestriction(anyString(), any(Date.class))).thenReturn(mockBandwidthRestriction);
		underTest.setMinimumBandwidth(1);
		
		TargetInstance mockTargetInstance = mock(TargetInstance.class);
		when(mockTargetInstance.getState()).thenReturn(TargetInstance.STATE_QUEUED);
		long tiOid = 123L;
		when(mockTargetInstance.getOid()).thenReturn(tiOid);
		when(mockTargetInstanceDao.load(tiOid)).thenReturn(mockTargetInstance);
		when(mockTargetInstanceDao.populate(mockTargetInstance)).thenReturn(mockTargetInstance);
		boolean result = underTest.isMiniumBandwidthAvailable(mockTargetInstance);
		assertTrue(result);
	}

	@Test
	public void testIsMiniumBandwidthAvailableTargetInstanceAllocationTooLow() throws Exception {
		BandwidthRestriction mockBandwidthRestriction = mock(BandwidthRestriction.class);
		long maxBandwidth = 23L;
		when(mockBandwidthRestriction.getBandwidth()).thenReturn(maxBandwidth);
		when(mockHarvestCoordinatorDao.getBandwidthRestriction(anyString(), any(Date.class))).thenReturn(mockBandwidthRestriction);
		underTest.setMinimumBandwidth(10);
		
		TargetInstance mockTargetInstance = mock(TargetInstance.class);
		when(mockTargetInstance.getAllocatedBandwidth()).thenReturn(9L);
		when(mockTargetInstance.getBandwidthPercent()).thenReturn(null);
		when(mockTargetInstance.getState()).thenReturn(TargetInstance.STATE_QUEUED);
		long tiOid = 123L;
		when(mockTargetInstance.getOid()).thenReturn(tiOid);
		when(mockTargetInstanceDao.load(tiOid)).thenReturn(mockTargetInstance);
		when(mockTargetInstanceDao.populate(mockTargetInstance)).thenReturn(mockTargetInstance);
		boolean result = underTest.isMiniumBandwidthAvailable(mockTargetInstance);
		assertFalse(result);
	}

	@Test
	public void testIsMiniumBandwidthAvailableTargetInstanceAllocationEnough() throws Exception {
		BandwidthRestriction mockBandwidthRestriction = mock(BandwidthRestriction.class);
		long maxBandwidth = 23L;
		when(mockBandwidthRestriction.getBandwidth()).thenReturn(maxBandwidth);
		when(mockHarvestCoordinatorDao.getBandwidthRestriction(anyString(), any(Date.class))).thenReturn(mockBandwidthRestriction);
		underTest.setMinimumBandwidth(10);
		
		TargetInstance mockTargetInstance = mock(TargetInstance.class);
		when(mockTargetInstance.getAllocatedBandwidth()).thenReturn(20L);
		when(mockTargetInstance.getBandwidthPercent()).thenReturn(null);
		when(mockTargetInstance.getState()).thenReturn(TargetInstance.STATE_QUEUED);
		long tiOid = 123L;
		when(mockTargetInstance.getOid()).thenReturn(tiOid);
		when(mockTargetInstanceDao.load(tiOid)).thenReturn(mockTargetInstance);
		when(mockTargetInstanceDao.populate(mockTargetInstance)).thenReturn(mockTargetInstance);
		boolean result = underTest.isMiniumBandwidthAvailable(mockTargetInstance);
		assertTrue(result);
	}

	@Test
	public void testIsMiniumBandwidthAvailableQueuedTargetInstanceAllocationEnough() throws Exception {
		BandwidthRestriction mockBandwidthRestriction = mock(BandwidthRestriction.class);
		long maxBandwidth = 23L;
		when(mockBandwidthRestriction.getBandwidth()).thenReturn(maxBandwidth);
		when(mockHarvestCoordinatorDao.getBandwidthRestriction(anyString(), any(Date.class))).thenReturn(mockBandwidthRestriction);
		underTest.setMinimumBandwidth(10);
		
		QueuedTargetInstanceDTO mockQueuedTargetInstance = mock(QueuedTargetInstanceDTO.class);
		long tiOid = 123L;
		when(mockQueuedTargetInstance.getOid()).thenReturn(tiOid);
		TargetInstance mockTargetInstance = mock(TargetInstance.class);
		when(mockTargetInstance.getAllocatedBandwidth()).thenReturn(20L);
		when(mockTargetInstance.getBandwidthPercent()).thenReturn(null);
		when(mockTargetInstance.getState()).thenReturn(TargetInstance.STATE_QUEUED);
		when(mockTargetInstance.getOid()).thenReturn(tiOid);
		when(mockTargetInstanceDao.load(tiOid)).thenReturn(mockTargetInstance);
		when(mockTargetInstanceDao.populate(mockTargetInstance)).thenReturn(mockTargetInstance);
		boolean result = underTest.isMiniumBandwidthAvailable(mockQueuedTargetInstance);
		assertTrue(result);
	}

	@Test
	public void testIsMiniumBandwidthAvailableTargetInstancePercentAllocation() throws Exception {
		BandwidthRestriction mockBandwidthRestriction = mock(BandwidthRestriction.class);
		long maxBandwidth = 23L;
		when(mockBandwidthRestriction.getBandwidth()).thenReturn(maxBandwidth);
		when(mockHarvestCoordinatorDao.getBandwidthRestriction(anyString(), any(Date.class))).thenReturn(mockBandwidthRestriction);
		underTest.setMinimumBandwidth(10);

		TargetInstance mockRunningTi = mock(TargetInstance.class);
		when(mockTargetInstanceDao.findTargetInstances(any(TargetInstanceCriteria.class))).thenReturn(Lists.newArrayList(mockRunningTi));
		
		TargetInstance mockTargetInstance = mock(TargetInstance.class);
		when(mockTargetInstance.getAllocatedBandwidth()).thenReturn(null);
		when(mockTargetInstance.getBandwidthPercent()).thenReturn(10);
		when(mockTargetInstance.getState()).thenReturn(TargetInstance.STATE_QUEUED);
		long tiOid = 123L;
		when(mockTargetInstance.getOid()).thenReturn(tiOid);
		when(mockTargetInstanceDao.load(tiOid)).thenReturn(mockTargetInstance);
		when(mockTargetInstanceDao.populate(mockTargetInstance)).thenReturn(mockTargetInstance);
		boolean result = underTest.isMiniumBandwidthAvailable(mockTargetInstance);
		assertTrue(result);
	}

	@Test
	public void testIsMiniumBandwidthAvailableTargetInstancePercentAllocationTooLow() throws Exception {
		BandwidthRestriction mockBandwidthRestriction = mock(BandwidthRestriction.class);
		long maxBandwidth = 23L;
		when(mockBandwidthRestriction.getBandwidth()).thenReturn(maxBandwidth);
		when(mockHarvestCoordinatorDao.getBandwidthRestriction(anyString(), any(Date.class))).thenReturn(mockBandwidthRestriction);
		underTest.setMinimumBandwidth(10);
		int maxBandwidthPercent = 20;
		underTest.setMaxBandwidthPercent(maxBandwidthPercent);

		TargetInstance mockRunningTi = mock(TargetInstance.class);
		when(mockRunningTi.getAllocatedBandwidth()).thenReturn(9L);
		when(mockRunningTi.getBandwidthPercent()).thenReturn(null);
		ArrayList<TargetInstance> runningList = Lists.newArrayList(mockRunningTi);
		when(mockTargetInstanceDao.findTargetInstances(any(TargetInstanceCriteria.class))).thenReturn(runningList);
		
		HashMap<Long, TargetInstance> runningMap = Maps.newHashMap();
		runningMap.put(1L, mockRunningTi);
		when(mockBandwidthCalculator.calculateBandwidthAllocation(runningList, maxBandwidth, maxBandwidthPercent)).thenReturn(runningMap);
		
		TargetInstance mockTargetInstance = mock(TargetInstance.class);
		when(mockTargetInstance.getAllocatedBandwidth()).thenReturn(null);
		when(mockTargetInstance.getBandwidthPercent()).thenReturn(10);
		when(mockTargetInstance.getState()).thenReturn(TargetInstance.STATE_QUEUED);
		long tiOid = 123L;
		when(mockTargetInstance.getOid()).thenReturn(tiOid);
		when(mockTargetInstanceDao.load(tiOid)).thenReturn(mockTargetInstance);
		when(mockTargetInstanceDao.populate(mockTargetInstance)).thenReturn(mockTargetInstance);
		boolean result = underTest.isMiniumBandwidthAvailable(mockTargetInstance);
		assertFalse(result);
	}

	@Test
	public void testIsHarvestOptimizationAllowedNotFound() throws Exception {
		boolean result = underTest.isHarvestOptimizationAllowed();
		verify(mockHarvestCoordinatorDao).getBandwidthRestriction(anyString(), any(Date.class));
		assertFalse(result);
	}

	@Test
	public void testIsHarvestOptimizationAllowed() throws Exception {
		BandwidthRestriction mockBandwidthRestriction = mock(BandwidthRestriction.class);
		when(mockBandwidthRestriction.isAllowOptimize()).thenReturn(true);
		when(mockHarvestCoordinatorDao.getBandwidthRestriction(anyString(), any(Date.class))).thenReturn(mockBandwidthRestriction);
		boolean result = underTest.isHarvestOptimizationAllowed();
		verify(mockHarvestCoordinatorDao).getBandwidthRestriction(anyString(), any(Date.class));
		assertTrue(result);

		when(mockBandwidthRestriction.isAllowOptimize()).thenReturn(false);
		result = underTest.isHarvestOptimizationAllowed();
		assertFalse(result);

	}

	@Test
	public void testGetBandwidthRestrictionLong() throws Exception {
		long oid = 123L;
		underTest.getBandwidthRestriction(oid);
		verify(mockHarvestCoordinatorDao).getBandwidthRestriction(oid);
	}

	@Test
	public void testSaveOrUpdateNew() throws Exception {
		BandwidthRestriction mockBandwidthRestriction = mock(BandwidthRestriction.class);
		when(mockBandwidthRestriction.getOid()).thenReturn(null);
		underTest.saveOrUpdate(mockBandwidthRestriction);
		verify(mockHarvestCoordinatorDao).saveOrUpdate(mockBandwidthRestriction);
		verify(mockAuditor).audit(anyString(), eq((Long)null), eq(Auditor.ACTION_NEW_BANDWIDTH_RESTRICTION), anyString());
		
	}
	@Test
	public void testSaveOrUpdateNotNew() throws Exception {
		BandwidthRestriction mockBandwidthRestriction = mock(BandwidthRestriction.class);
		long oid = 123L;
		when(mockBandwidthRestriction.getOid()).thenReturn(oid);
		underTest.saveOrUpdate(mockBandwidthRestriction);
		verify(mockHarvestCoordinatorDao).saveOrUpdate(mockBandwidthRestriction);
		verify(mockAuditor).audit(anyString(), eq(oid), eq(Auditor.ACTION_CHANGE_BANDWIDTH_RESTRICTION), anyString());
		
	}

	@Test
	public void testDelete() throws Exception {
		BandwidthRestriction mockBandwidthRestriction = mock(BandwidthRestriction.class);
		underTest.delete(mockBandwidthRestriction);
		verify(mockHarvestCoordinatorDao).delete(mockBandwidthRestriction);
	}

	@Test
	public void testGetBandwidthRestrictionStringDate() throws Exception {
		Date time = new Date();
		String day = "Monday";
		underTest.getBandwidthRestriction(day, time);
		verify(mockHarvestCoordinatorDao).getBandwidthRestriction(day, time);
	}

	@Test
	public void testGetBandwidthRestrictions() throws Exception {
		underTest.getBandwidthRestrictions();
		verify(mockHarvestCoordinatorDao).getBandwidthRestrictions();
	}

	@Test(expected=WCTRuntimeException.class)
	public void testMinimumBandwidthNullQueued() {
		underTest.isMiniumBandwidthAvailable((QueuedTargetInstanceDTO)null);
	}

	@Test(expected=WCTRuntimeException.class)
	public void testMinimumBandwidthNullTi() {
		underTest.isMiniumBandwidthAvailable((TargetInstance)null);
	}

}
