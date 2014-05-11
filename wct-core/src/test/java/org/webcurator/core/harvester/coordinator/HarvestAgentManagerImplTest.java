package org.webcurator.core.harvester.coordinator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.context.WebApplicationContext;
import org.webcurator.core.common.Environment;
import org.webcurator.core.harvester.agent.HarvestAgent;
import org.webcurator.core.harvester.agent.HarvestAgentFactory;
import org.webcurator.core.scheduler.TargetInstanceManager;
import org.webcurator.core.util.ApplicationContextFactory;
import org.webcurator.domain.TargetInstanceDAO;
import org.webcurator.domain.model.core.TargetInstance;
import org.webcurator.domain.model.core.harvester.agent.HarvestAgentStatusDTO;
import org.webcurator.domain.model.core.harvester.agent.HarvesterStatusDTO;

import com.google.common.collect.Maps;

public class HarvestAgentManagerImplTest {

	private HarvestAgentManagerImpl underTest;
	@Mock
	private HarvestAgentFactory mockHarvestAgentFactory;
	@Mock
	private TargetInstanceDAO mockTargetInstanceDAO;
	@Mock
	private TargetInstanceManager mockTargetInstanceManager;
	@Mock
	private Environment mockEnvironment;

	public HarvestAgentManagerImplTest() {
		MockitoAnnotations.initMocks(this);
	}

	@Before
	public void setup() {
		underTest = new HarvestAgentManagerImpl();
		underTest.setHarvestAgentFactory(mockHarvestAgentFactory);
		underTest.setTargetInstanceDao(mockTargetInstanceDAO);
		underTest.setTargetInstanceManager(mockTargetInstanceManager);

		HarvestAgentManagerImpl.targetInstanceLocks.clear();

		WebApplicationContext context = mock(WebApplicationContext.class);
		when(context.getBean("environment")).thenReturn(mockEnvironment);
		ApplicationContextFactory.setWebApplicationContext(context);
	}

	@Test
	public void testStop() {
		String jobName = "testJob";
		HarvestAgent mockHarvestAgent = setHarvestAgentFactory(jobName);

		TargetInstance mockTi = mock(TargetInstance.class);
		when(mockTi.getJobName()).thenReturn(jobName);
		underTest.stop(mockTi);
		verify(mockHarvestAgent).stop(jobName);
	}

	@Test
	public void testStopJobDoesntExist() {
		String jobName = "nonexistant";
		underTest.harvestAgents.clear();

		TargetInstance mockTi = mock(TargetInstance.class);
		when(mockTi.getJobName()).thenReturn(jobName);
		underTest.stop(mockTi);
		// No result, but also no exception
	}

	private HarvestAgent setHarvestAgentFactory(String jobName) {
		HarvestAgentStatusDTO mockHarvestAgentStatusDTO = mock(HarvestAgentStatusDTO.class);
		HashMap<String, HarvesterStatusDTO> newHashMap = Maps.newHashMap();
		HarvesterStatusDTO mockHarvesterStatusDTO = mock(HarvesterStatusDTO.class);
		when(mockHarvesterStatusDTO.getJobName()).thenReturn(jobName);
		newHashMap.put("testKey", mockHarvesterStatusDTO);
		when(mockHarvestAgentStatusDTO.getHarvesterStatus()).thenReturn(newHashMap);
		underTest.harvestAgents.put(jobName, mockHarvestAgentStatusDTO);

		HarvestAgentFactory mockHarvestAgentFactory = mock(HarvestAgentFactory.class);
		HarvestAgent mockHarvestAgent = mock(HarvestAgent.class);
		when(mockHarvestAgentFactory.getHarvestAgent(anyString(), anyInt())).thenReturn(mockHarvestAgent);
		underTest.setHarvestAgentFactory(mockHarvestAgentFactory);
		return mockHarvestAgent;
	}

	@Test
	public void testAgentHasJobNoJobs() {
		HarvestAgentStatusDTO harvestAgentStatusDTO = new HarvestAgentStatusDTO();
		HashMap<String, HarvesterStatusDTO> statusMap = Maps.newHashMap();
		harvestAgentStatusDTO.setHarvesterStatus(statusMap);
		boolean result = underTest.agentHasJob("test", harvestAgentStatusDTO);
		assertFalse(result);
	}

	@Test
	public void testAgentHasJobButNotThisOne() {
		HarvestAgentStatusDTO harvestAgentStatusDTO = new HarvestAgentStatusDTO();
		HashMap<String, HarvesterStatusDTO> statusMap = Maps.newHashMap();
		HarvesterStatusDTO harvesterStatusDTO = new HarvesterStatusDTO();
		harvesterStatusDTO.setJobName("notTheSameJob");
		statusMap.put("irrelevant", harvesterStatusDTO);
		harvestAgentStatusDTO.setHarvesterStatus(statusMap);
		boolean result = underTest.agentHasJob("test", harvestAgentStatusDTO);
		assertFalse(result);
	}

	@Test
	public void testAgentHasJobsButNotThisOne() {
		String jobName = "differentJob";
		HarvestAgentStatusDTO harvestAgentStatusDTO = setupHarvestAgentStatus(jobName);
		boolean result = underTest.agentHasJob("test", harvestAgentStatusDTO);
		assertFalse(result);
	}

	@Test
	public void testAgentHasJobsFindsJob() {
		String jobName = "test";
		HarvestAgentStatusDTO harvestAgentStatusDTO = setupHarvestAgentStatus(jobName);
		boolean result = underTest.agentHasJob("test", harvestAgentStatusDTO);
		assertTrue(result);
	}

	private HarvestAgentStatusDTO setupHarvestAgentStatus(String secondJobName) {
		HarvestAgentStatusDTO harvestAgentStatusDTO = new HarvestAgentStatusDTO();
		HashMap<String, HarvesterStatusDTO> statusMap = Maps.newHashMap();
		HarvesterStatusDTO harvesterStatusDTO = new HarvesterStatusDTO();
		harvesterStatusDTO.setJobName("notTheSameJob");
		statusMap.put("irrelevant", harvesterStatusDTO);
		HarvesterStatusDTO harvesterStatusDTO2 = new HarvesterStatusDTO();
		harvesterStatusDTO2.setJobName(secondJobName);
		statusMap.put("irrelevant2", harvesterStatusDTO2);
		harvestAgentStatusDTO.setHarvesterStatus(statusMap);
		return harvestAgentStatusDTO;
	}

	@Test
	public void testGetHarvestAgentStatusForTiNoAgents() {
		HarvestAgentStatusDTO result = underTest.getHarvestAgentStatusFor("test");
		assertNull(result);
	}

	@Test
	public void testGetHarvestAgentStatusForTiAgentDoesntHaveJob() {
		String jobName = "anotherJob";
		HarvestAgentStatusDTO harvestAgentStatusDTO = setupHarvestAgentStatus(jobName);
		underTest.harvestAgents.put("irrelevant", harvestAgentStatusDTO);
		HarvestAgentStatusDTO result = underTest.getHarvestAgentStatusFor("test");
		assertNull(result);
	}

	@Test
	public void testGetHarvestAgentStatusForTiAgentHasNullStatus() {
		String jobName = "anotherJob";
		HarvestAgentStatusDTO harvestAgentStatusDTO = setupHarvestAgentStatus(jobName);
		harvestAgentStatusDTO.setHarvesterStatus(null);
		underTest.harvestAgents.put("irrelevant", harvestAgentStatusDTO);
		HarvestAgentStatusDTO result = underTest.getHarvestAgentStatusFor("test");
		assertNull(result);
	}

	@Test
	public void testGetHarvestAgentStatusForTiAgentHasJob() {
		HarvestAgentStatusDTO harvestAgentStatusDTO = setupHarvestAgentStatus("differentJob");
		underTest.harvestAgents.put("irrelevant", harvestAgentStatusDTO);
		String jobName = "test";
		harvestAgentStatusDTO = setupHarvestAgentStatus(jobName);
		underTest.harvestAgents.put("irrelevant2", harvestAgentStatusDTO);
		HarvestAgentStatusDTO result = underTest.getHarvestAgentStatusFor("test");
		assertNotNull(result);
	}

	@Test
	public void testRunningOrPaused() {
		TargetInstance targetInstance = new TargetInstance();
		targetInstance.setState(TargetInstance.STATE_RUNNING);
		assertTrue(underTest.runningOrPaused(targetInstance));
		targetInstance.setState(TargetInstance.STATE_PAUSED);
		assertTrue(underTest.runningOrPaused(targetInstance));

		targetInstance.setState(TargetInstance.STATE_SCHEDULED);
		assertFalse(underTest.runningOrPaused(targetInstance));
		targetInstance.setState(TargetInstance.STATE_SCHEDULED);
		assertFalse(underTest.runningOrPaused(targetInstance));
		targetInstance.setState(TargetInstance.STATE_QUEUED);
		assertFalse(underTest.runningOrPaused(targetInstance));
		targetInstance.setState(TargetInstance.STATE_STOPPING);
		assertFalse(underTest.runningOrPaused(targetInstance));
		targetInstance.setState(TargetInstance.STATE_ABORTED);
		assertFalse(underTest.runningOrPaused(targetInstance));
		targetInstance.setState(TargetInstance.STATE_HARVESTED);
		assertFalse(underTest.runningOrPaused(targetInstance));
		targetInstance.setState(TargetInstance.STATE_REJECTED);
		assertFalse(underTest.runningOrPaused(targetInstance));
		targetInstance.setState(TargetInstance.STATE_ENDORSED);
		assertFalse(underTest.runningOrPaused(targetInstance));
		targetInstance.setState(TargetInstance.STATE_ARCHIVED);
		assertFalse(underTest.runningOrPaused(targetInstance));
		targetInstance.setState(TargetInstance.STATE_ARCHIVING);
		assertFalse(underTest.runningOrPaused(targetInstance));
	}

	@Test
	public void testHeartbeatNewJobNoHarvests() {
		HarvestAgentStatusDTO agentStatusDTO = new HarvestAgentStatusDTO();
		String harvesterName = "status1";
		agentStatusDTO.setName(harvesterName);
		agentStatusDTO.setHarvesterStatus(Maps.<String, HarvesterStatusDTO> newHashMap());
		underTest.heartbeat(agentStatusDTO);
		assertTrue(underTest.harvestAgents.containsKey(harvesterName));
		verifyNoMoreInteractions(mockTargetInstanceDAO);
		verifyNoMoreInteractions(mockTargetInstanceManager);
	}

	@Test
	public void testHeartbeatReconnectAcceptTasksReset() {
		String harvesterName = "reconnect";
		HarvestAgentStatusDTO agentStatusDTO1 = createHarvestAgentStatusDto(harvesterName);
		agentStatusDTO1.setAcceptTasks(false);
		underTest.harvestAgents.put(harvesterName, agentStatusDTO1);

		HarvestAgentStatusDTO agentStatusDTO2 = createHarvestAgentStatusDto(harvesterName);
		agentStatusDTO2.setAcceptTasks(true);
		underTest.heartbeat(agentStatusDTO2);
		assertTrue(underTest.harvestAgents.containsKey(harvesterName));
		verifyNoMoreInteractions(mockTargetInstanceDAO);
		verifyNoMoreInteractions(mockTargetInstanceManager);
		assertFalse(agentStatusDTO2.isAcceptTasks());

		agentStatusDTO1.setAcceptTasks(true);
		underTest.harvestAgents.put(harvesterName, agentStatusDTO1);
		agentStatusDTO2.setAcceptTasks(false);
		underTest.heartbeat(agentStatusDTO2);
		assertTrue(agentStatusDTO2.isAcceptTasks());

	}

	@Test
	public void testHeartbeatUpdatesTiWithStatusPaused() {
		Long tOid = 123L;
		TargetInstance mockTargetInstance = mock(TargetInstance.class);
		// Behaviour is not defined when TI state is not running/paused..
		when(mockTargetInstance.getState()).thenReturn(TargetInstance.STATE_RUNNING);

		HarvestAgentStatusDTO agentStatusDTO2 = setupHarvestAgentWithHarvestState(tOid, "Paused", mockTargetInstance);
		underTest.heartbeat(agentStatusDTO2);
		verify(mockTargetInstanceDAO).load(tOid);
		verify(mockTargetInstanceManager).save(mockTargetInstance);
		verify(mockTargetInstance).setState(TargetInstance.STATE_PAUSED);

	}

	@Test
	public void testHeartbeatUpdatesTiAlreadyPaused() {
		Long tOid = 123L;
		TargetInstance mockTargetInstance = mock(TargetInstance.class);
		// Behaviour is not defined when TI state is not running/paused..
		when(mockTargetInstance.getState()).thenReturn(TargetInstance.STATE_PAUSED);

		HarvestAgentStatusDTO agentStatusDTO2 = setupHarvestAgentWithHarvestState(tOid, "Paused", mockTargetInstance);
		underTest.heartbeat(agentStatusDTO2);
		verify(mockTargetInstanceDAO).load(tOid);
		verify(mockTargetInstanceManager).save(mockTargetInstance);
		verify(mockTargetInstance, times(0)).setState(TargetInstance.STATE_PAUSED);

	}

	@Test
	public void testHeartbeatUpdatesTiRunningFromPaused() {
		Long tOid = 123L;
		TargetInstance mockTargetInstance = mock(TargetInstance.class);
		// Behaviour is not defined when TI state is not paused/queued..
		when(mockTargetInstance.getState()).thenReturn(TargetInstance.STATE_PAUSED);

		HarvestAgentStatusDTO agentStatusDTO2 = setupHarvestAgentWithHarvestState(tOid, "Running", mockTargetInstance);
		underTest.heartbeat(agentStatusDTO2);
		verify(mockTargetInstanceDAO).load(tOid);
		verify(mockTargetInstanceManager).save(mockTargetInstance);
		verify(mockTargetInstance).setState(TargetInstance.STATE_RUNNING);

	}

	@Test
	public void testHeartbeatUpdatesTiRunningFromQueued() {
		Long tOid = 123L;
		TargetInstance mockTargetInstance = mock(TargetInstance.class);
		// Behaviour is not defined when TI state is not paused/queued..
		when(mockTargetInstance.getState()).thenReturn(TargetInstance.STATE_QUEUED);

		HarvestAgentStatusDTO agentStatusDTO2 = setupHarvestAgentWithHarvestState(tOid, "Running", mockTargetInstance);
		underTest.heartbeat(agentStatusDTO2);
		verify(mockTargetInstanceDAO).load(tOid);
		verify(mockTargetInstanceManager).save(mockTargetInstance);
		verify(mockTargetInstance).setState(TargetInstance.STATE_RUNNING);

	}

	@Test
	public void testHeartbeatUpdatesTiStoppingFromRunning() {
		Long tOid = 123L;
		TargetInstance mockTargetInstance = mock(TargetInstance.class);
		// Behaviour is not defined when TI state is not paused/queued..
		when(mockTargetInstance.getState()).thenReturn(TargetInstance.STATE_RUNNING);

		HarvestAgentStatusDTO agentStatusDTO2 = setupHarvestAgentWithHarvestState(tOid, "Finished", mockTargetInstance);
		underTest.heartbeat(agentStatusDTO2);
		verify(mockTargetInstanceDAO).load(tOid);
		verify(mockTargetInstanceManager).save(mockTargetInstance);
		verify(mockTargetInstance).setState(TargetInstance.STATE_STOPPING);

	}

	@Test
	public void testHeartbeatUpdatesTiStoppingAlreadyStopped() {
		Long tOid = 123L;
		TargetInstance mockTargetInstance = mock(TargetInstance.class);
		// Behaviour is not defined when TI state is not paused/queued..
		when(mockTargetInstance.getState()).thenReturn(TargetInstance.STATE_STOPPING);

		HarvestAgentStatusDTO agentStatusDTO2 = setupHarvestAgentWithHarvestState(tOid, "Finished", mockTargetInstance);
		underTest.heartbeat(agentStatusDTO2);
		verify(mockTargetInstanceDAO).load(tOid);
		verify(mockTargetInstanceManager).save(mockTargetInstance);
		verify(mockTargetInstance, times(0)).setState(TargetInstance.STATE_STOPPING);

	}

	@Test
	public void testHeartbeatUpdatesTiAbortedFromRunning() {
		Long tOid = 123L;
		TargetInstance mockTargetInstance = mock(TargetInstance.class);
		String jobName = String.valueOf(tOid);
		HarvestAgent mockHarvestAgent = setHarvestAgentFactory(jobName);

		// Behaviour is not defined when TI state is not paused/queued..
		when(mockTargetInstance.getState()).thenReturn(TargetInstance.STATE_RUNNING);
		HarvestAgentStatusDTO agentStatusDTO2 = setupHarvestAgentWithHarvestState(tOid,
				"Could not launch job - Fatal InitializationException", mockTargetInstance);
		underTest.heartbeat(agentStatusDTO2);
		verify(mockTargetInstanceDAO).load(tOid);
		verify(mockTargetInstanceManager).save(mockTargetInstance);
		verify(mockTargetInstance).setState(TargetInstance.STATE_ABORTED);
		verify(mockHarvestAgent).abort(jobName);
	}

	@Test
	public void testHeartbeatUpdatesTiAbortedNotRunning() {
		Long tOid = 123L;
		TargetInstance mockTargetInstance = mock(TargetInstance.class);
		// Behaviour is not defined when TI state is not paused/queued..
		when(mockTargetInstance.getState()).thenReturn(TargetInstance.STATE_ARCHIVING);

		HarvestAgentStatusDTO agentStatusDTO2 = setupHarvestAgentWithHarvestState(tOid,
				"Could not launch job - Fatal InitializationException", mockTargetInstance);
		underTest.heartbeat(agentStatusDTO2);
		verify(mockTargetInstanceDAO).load(tOid);
		verify(mockTargetInstanceManager).save(mockTargetInstance);
		verify(mockTargetInstance, times(0)).setState(TargetInstance.STATE_ABORTED);
	}

	@Test
	public void testUpdateProfileOverrides() {
		Long tOid = 123L;
		TargetInstance mockTargetInstance = mock(TargetInstance.class);
		HarvestAgent mockHarvestAgent = mock(HarvestAgent.class);
		when(mockHarvestAgentFactory.getHarvestAgent(anyString(), anyInt())).thenReturn(mockHarvestAgent);
		String profileString = "test profile";
		HarvestAgentStatusDTO agentStatusDTO = setupHarvestAgentWithHarvestState(tOid,
				"Could not launch job - Fatal InitializationException", mockTargetInstance);
		underTest.harvestAgents.put("test", agentStatusDTO);

		underTest.updateProfileOverrides(mockTargetInstance, profileString);
		verify(mockHarvestAgent).updateProfileOverrides(mockTargetInstance.getJobName(), profileString);
	}

	@Test
	public void testUpdateProfileOverridesNoAgent() {
		TargetInstance mockTargetInstance = mock(TargetInstance.class);
		String profileString = "test profile";
		createHarvestAgentStatusDto("test");

		underTest.updateProfileOverrides(mockTargetInstance, profileString);
		verifyNoMoreInteractions(mockHarvestAgentFactory);
	}

	@Test
	public void testPauseAllNoHarvestAgents() {
		underTest.pauseAll();
		verifyNoMoreInteractions(mockHarvestAgentFactory);
	}

	@Test
	public void testPauseAllOneHarvestAgent() {
		HarvestAgent mockHarvestAgent = mock(HarvestAgent.class);
		when(mockHarvestAgentFactory.getHarvestAgent(anyString(), anyInt())).thenReturn(mockHarvestAgent);
		underTest.harvestAgents.put("test", createHarvestAgentStatusDtoWithStatusDto("123", "test"));
		underTest.pauseAll();
		verify(mockHarvestAgent).pauseAll();
	}

	@Test
	public void testPauseAllOneHarvestAgentNoHarvests() {
		HarvestAgent mockHarvestAgent = mock(HarvestAgent.class);
		when(mockHarvestAgentFactory.getHarvestAgent(anyString(), anyInt())).thenReturn(mockHarvestAgent);
		underTest.harvestAgents.put("test", createHarvestAgentStatusDto("test"));
		underTest.pauseAll();
		verify(mockHarvestAgent, times(0)).pauseAll();
	}

	@Test
	public void testPauseAllOneHarvestAgentNullHarvests() {
		HarvestAgent mockHarvestAgent = mock(HarvestAgent.class);
		when(mockHarvestAgentFactory.getHarvestAgent(anyString(), anyInt())).thenReturn(mockHarvestAgent);
		HarvestAgentStatusDTO harvestAgentStatusDto = createHarvestAgentStatusDto("test");
		harvestAgentStatusDto.setHarvesterStatus(null);
		underTest.harvestAgents.put("test", harvestAgentStatusDto);
		underTest.pauseAll();
		verify(mockHarvestAgent, times(0)).pauseAll();
	}

	@Test
	public void testPauseAllMultipleHarvestAgents() {
		HarvestAgent mockHarvestAgent1 = mock(HarvestAgent.class);
		HarvestAgent mockHarvestAgent2 = mock(HarvestAgent.class);
		HarvestAgent mockHarvestAgent3 = mock(HarvestAgent.class);
		// Returns the harvest agents in order for each call
		when(mockHarvestAgentFactory.getHarvestAgent(anyString(), anyInt())).thenReturn(mockHarvestAgent1)
				.thenReturn(mockHarvestAgent2).thenReturn(mockHarvestAgent3);
		underTest.harvestAgents.put("test1", createHarvestAgentStatusDtoWithStatusDto("123", "test1"));
		underTest.harvestAgents.put("test2", createHarvestAgentStatusDtoWithStatusDto("124", "test2"));
		underTest.harvestAgents.put("test3", createHarvestAgentStatusDtoWithStatusDto("125", "test3"));
		underTest.pauseAll();
		verify(mockHarvestAgent1).pauseAll();
		verify(mockHarvestAgent2).pauseAll();
		verify(mockHarvestAgent3).pauseAll();
	}

	@Test
	public void testResumeAllNoHarvestAgents() {
		underTest.resumeAll();
		verifyNoMoreInteractions(mockHarvestAgentFactory);
	}

	@Test
	public void testResumeAllOneHarvestAgent() {
		HarvestAgent mockHarvestAgent = mock(HarvestAgent.class);
		when(mockHarvestAgentFactory.getHarvestAgent(anyString(), anyInt())).thenReturn(mockHarvestAgent);
		underTest.harvestAgents.put("test", createHarvestAgentStatusDtoWithStatusDto("123", "test"));
		underTest.resumeAll();
		verify(mockHarvestAgent).resumeAll();
	}

	@Test
	public void testResumeAllOneHarvestAgentNoHarvests() {
		HarvestAgent mockHarvestAgent = mock(HarvestAgent.class);
		when(mockHarvestAgentFactory.getHarvestAgent(anyString(), anyInt())).thenReturn(mockHarvestAgent);
		underTest.harvestAgents.put("test", createHarvestAgentStatusDto("test"));
		underTest.resumeAll();
		verify(mockHarvestAgent, times(0)).resumeAll();
	}

	@Test
	public void testResumeAllOneHarvestAgentNullHarvests() {
		HarvestAgent mockHarvestAgent = mock(HarvestAgent.class);
		when(mockHarvestAgentFactory.getHarvestAgent(anyString(), anyInt())).thenReturn(mockHarvestAgent);
		HarvestAgentStatusDTO harvestAgentStatusDto = createHarvestAgentStatusDto("test");
		harvestAgentStatusDto.setHarvesterStatus(null);
		underTest.harvestAgents.put("test", harvestAgentStatusDto);
		underTest.resumeAll();
		verify(mockHarvestAgent, times(0)).resumeAll();
	}

	@Test
	public void testResumeAllMultipleHarvestAgents() {
		HarvestAgent mockHarvestAgent1 = mock(HarvestAgent.class);
		HarvestAgent mockHarvestAgent2 = mock(HarvestAgent.class);
		HarvestAgent mockHarvestAgent3 = mock(HarvestAgent.class);
		// Returns the harvest agents in order for each call
		when(mockHarvestAgentFactory.getHarvestAgent(anyString(), anyInt())).thenReturn(mockHarvestAgent1)
				.thenReturn(mockHarvestAgent2).thenReturn(mockHarvestAgent3);
		underTest.harvestAgents.put("test1", createHarvestAgentStatusDtoWithStatusDto("123", "test1"));
		underTest.harvestAgents.put("test2", createHarvestAgentStatusDtoWithStatusDto("124", "test2"));
		underTest.harvestAgents.put("test3", createHarvestAgentStatusDtoWithStatusDto("125", "test3"));
		underTest.resumeAll();
		verify(mockHarvestAgent1).resumeAll();
		verify(mockHarvestAgent2).resumeAll();
		verify(mockHarvestAgent3).resumeAll();
	}

	@Test
	public void testPauseAgentQueue() {
		HarvestAgentStatusDTO mockHarvestAgentStatusDTO = mock(HarvestAgentStatusDTO.class);
		underTest.harvestAgents.put("test", mockHarvestAgentStatusDTO);
		underTest.pauseAgent("test");
		verify(mockHarvestAgentStatusDTO).setAcceptTasks(false);
	}

	@Test
	public void testPauseAgentQueueNotFound() {
		HarvestAgentStatusDTO mockHarvestAgentStatusDTO = mock(HarvestAgentStatusDTO.class);
		underTest.harvestAgents.put("test", mockHarvestAgentStatusDTO);
		underTest.pauseAgent("notTheSame");
		verify(mockHarvestAgentStatusDTO, times(0)).setAcceptTasks(anyBoolean());
	}

	@Test
	public void testResumeAgentQueue() {
		HarvestAgentStatusDTO mockHarvestAgentStatusDTO = mock(HarvestAgentStatusDTO.class);
		underTest.harvestAgents.put("test", mockHarvestAgentStatusDTO);
		underTest.resumeAgent("test");
		verify(mockHarvestAgentStatusDTO).setAcceptTasks(true);
	}

	@Test
	public void testResumeAgentQueueNotFound() {
		HarvestAgentStatusDTO mockHarvestAgentStatusDTO = mock(HarvestAgentStatusDTO.class);
		underTest.harvestAgents.put("test", mockHarvestAgentStatusDTO);
		underTest.resumeAgent("notTheSame");
		verify(mockHarvestAgentStatusDTO, times(0)).setAcceptTasks(anyBoolean());
	}

	@Test
	public void testPauseTargetInstance() {
		Long tOid = 123L;
		TargetInstance mockTargetInstance = mock(TargetInstance.class);
		HarvestAgentStatusDTO agentStatusDTO2 = setupHarvestAgentWithHarvestState(tOid, "irrelevant", mockTargetInstance);
		underTest.harvestAgents.put("test1", agentStatusDTO2);
		HarvestAgent mockHarvestAgent = mock(HarvestAgent.class);
		when(mockHarvestAgentFactory.getHarvestAgent(anyString(), anyInt())).thenReturn(mockHarvestAgent);
		underTest.pause(mockTargetInstance);
		verify(mockTargetInstance).setState(TargetInstance.STATE_PAUSED);
		verify(mockTargetInstanceDAO).save(mockTargetInstance);
		verify(mockHarvestAgent).pause(mockTargetInstance.getJobName());
	}

	@Test
	public void testPauseTargetInstanceNoStatus() {
		TargetInstance mockTargetInstance = mock(TargetInstance.class);
		HarvestAgent mockHarvestAgent = mock(HarvestAgent.class);
		when(mockHarvestAgentFactory.getHarvestAgent(anyString(), anyInt())).thenReturn(mockHarvestAgent);
		underTest.pause(mockTargetInstance);
		verifyNoMoreInteractions(mockTargetInstanceDAO, mockHarvestAgent);
	}

	@Test
	public void testResumeTargetInstance() {
		Long tOid = 123L;
		TargetInstance mockTargetInstance = mock(TargetInstance.class);
		HarvestAgentStatusDTO agentStatusDTO2 = setupHarvestAgentWithHarvestState(tOid, "irrelevant", mockTargetInstance);
		underTest.harvestAgents.put("test1", agentStatusDTO2);
		HarvestAgent mockHarvestAgent = mock(HarvestAgent.class);
		when(mockHarvestAgentFactory.getHarvestAgent(anyString(), anyInt())).thenReturn(mockHarvestAgent);
		underTest.resume(mockTargetInstance);
		verify(mockTargetInstance).setState(TargetInstance.STATE_RUNNING);
		verify(mockTargetInstanceManager).save(mockTargetInstance);
		verify(mockHarvestAgent).resume(mockTargetInstance.getJobName());
	}

	@Test
	public void testResumeTargetInstanceNoStatus() {
		TargetInstance mockTargetInstance = mock(TargetInstance.class);
		HarvestAgent mockHarvestAgent = mock(HarvestAgent.class);
		when(mockHarvestAgentFactory.getHarvestAgent(anyString(), anyInt())).thenReturn(mockHarvestAgent);
		underTest.resume(mockTargetInstance);
		verifyNoMoreInteractions(mockTargetInstanceDAO, mockHarvestAgent);
	}
	
	@Test
	public void testAbortTargetInstance() {
		Long tOid = 123L;
		TargetInstance mockTargetInstance = mock(TargetInstance.class);
		HarvestAgentStatusDTO agentStatusDTO2 = setupHarvestAgentWithHarvestState(tOid, "irrelevant", mockTargetInstance);
		underTest.harvestAgents.put("test1", agentStatusDTO2);
		HarvestAgent mockHarvestAgent = mock(HarvestAgent.class);
		when(mockHarvestAgentFactory.getHarvestAgent(anyString(), anyInt())).thenReturn(mockHarvestAgent);
		underTest.abort(mockTargetInstance);
		verify(mockTargetInstance).setState(TargetInstance.STATE_ABORTED);
		verify(mockTargetInstanceDAO).save(mockTargetInstance);
		verify(mockHarvestAgent).abort(mockTargetInstance.getJobName());
	}

	@Test
	public void testAbortTargetInstanceRuntimeException() {
		Long tOid = 123L;
		TargetInstance mockTargetInstance = mock(TargetInstance.class);
		HarvestAgentStatusDTO agentStatusDTO2 = setupHarvestAgentWithHarvestState(tOid, "irrelevant", mockTargetInstance);
		underTest.harvestAgents.put("test1", agentStatusDTO2);
		HarvestAgent mockHarvestAgent = mock(HarvestAgent.class);
		when(mockHarvestAgentFactory.getHarvestAgent(anyString(), anyInt())).thenReturn(mockHarvestAgent);
		doThrow(new RuntimeException()).when(mockHarvestAgent).abort(anyString());
		underTest.abort(mockTargetInstance);
		verify(mockTargetInstance).setState(TargetInstance.STATE_ABORTED);
		verify(mockTargetInstanceDAO).save(mockTargetInstance);
	}

	@Test
	public void testAbortTargetInstanceNoStatus() {
		TargetInstance mockTargetInstance = mock(TargetInstance.class);
		HarvestAgent mockHarvestAgent = mock(HarvestAgent.class);
		when(mockHarvestAgentFactory.getHarvestAgent(anyString(), anyInt())).thenReturn(mockHarvestAgent);
		underTest.abort(mockTargetInstance);
		verify(mockTargetInstance).setState(TargetInstance.STATE_ABORTED);
		verify(mockTargetInstanceDAO).save(mockTargetInstance);
		verifyNoMoreInteractions(mockHarvestAgent);
	}

	// Sets up a new harvest agent and adds a harvest status to it
	private HarvestAgentStatusDTO setupHarvestAgentWithHarvestState(Long tOid, String harvestState,
			TargetInstance mockTargetInstance) {
		String tOidString = String.valueOf(tOid);
		when(mockTargetInstance.getJobName()).thenReturn(tOidString);
		when(mockTargetInstanceDAO.load(tOid)).thenReturn(mockTargetInstance);
		String harvesterName = "test";
		HarvestAgentStatusDTO agentStatusDTO = createHarvestAgentStatusDto(harvesterName);
		agentStatusDTO.setAcceptTasks(true);

		HashMap<String, HarvesterStatusDTO> statusDtoMap = Maps.newHashMap();
		HarvesterStatusDTO harvesterStatusDTO = new HarvesterStatusDTO();
		harvesterStatusDTO.setJobName(tOidString);
		harvesterStatusDTO.setStatus(harvestState);
		statusDtoMap.put(tOidString, harvesterStatusDTO);
		agentStatusDTO.setHarvesterStatus(statusDtoMap);
		return agentStatusDTO;
	}

	private HarvestAgentStatusDTO createHarvestAgentStatusDtoWithStatusDto(String tOidString, String harvesterName) {
		HarvestAgentStatusDTO result = createHarvestAgentStatusDto(harvesterName);
		HashMap<String, HarvesterStatusDTO> statusDtoMap = Maps.newHashMap();
		HarvesterStatusDTO harvesterStatusDTO = new HarvesterStatusDTO();
		harvesterStatusDTO.setJobName(tOidString);
		statusDtoMap.put(tOidString, harvesterStatusDTO);
		result.setHarvesterStatus(statusDtoMap);
		return result;
	}

	private HarvestAgentStatusDTO createHarvestAgentStatusDto(String harvesterName) {
		HarvestAgentStatusDTO agentStatusDTO = new HarvestAgentStatusDTO();
		agentStatusDTO.setName(harvesterName);
		agentStatusDTO.setHarvesterStatus(Maps.<String, HarvesterStatusDTO> newHashMap());
		return agentStatusDTO;
	}
}
