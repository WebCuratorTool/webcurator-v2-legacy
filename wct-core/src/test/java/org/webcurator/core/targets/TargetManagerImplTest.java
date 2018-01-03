package org.webcurator.core.targets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.*;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.MockMessageSource;
import org.webcurator.auth.AuthorityManagerImpl;
import org.webcurator.core.common.Environment;
import org.webcurator.core.common.EnvironmentFactory;
import org.webcurator.core.exceptions.WCTRuntimeException;
import org.webcurator.core.notification.MessageType;
import org.webcurator.core.notification.MockInTrayManager;
import org.webcurator.core.permissionmapping.PermissionMappingStrategy;
import org.webcurator.core.scheduler.TargetInstanceManager;
import org.webcurator.core.util.Auditor;
import org.webcurator.core.util.AuthUtil;
import org.webcurator.core.util.TestAuditor;
import org.webcurator.domain.MockAnnotationDAO;
import org.webcurator.domain.MockSiteDAO;
import org.webcurator.domain.MockTargetInstanceDAO;
import org.webcurator.domain.Pagination;
import org.webcurator.domain.TargetDAO;
import org.webcurator.domain.model.auth.Agency;
import org.webcurator.domain.model.auth.Privilege;
import org.webcurator.domain.model.auth.User;
import org.webcurator.domain.model.core.AbstractTarget;
import org.webcurator.domain.model.core.Annotation;
import org.webcurator.domain.model.core.BusinessObjectFactory;
import org.webcurator.domain.model.core.GroupMember;
import org.webcurator.domain.model.core.Permission;
import org.webcurator.domain.model.core.Profile;
import org.webcurator.domain.model.core.Schedule;
import org.webcurator.domain.model.core.Seed;
import org.webcurator.domain.model.core.Target;
import org.webcurator.domain.model.core.TargetGroup;
import org.webcurator.domain.model.core.TargetInstance;
import org.webcurator.domain.model.dto.AbstractTargetDTO;
import org.webcurator.domain.model.dto.GroupMemberDTO;
import org.webcurator.domain.model.dto.GroupMemberDTO.SAVE_STATE;
import org.webcurator.ui.target.TargetEditorContext;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class TargetManagerImplTest {

	private TargetManagerImpl underTest;

	@Mock TargetDAO targetDao;
	@Mock TargetInstanceManager tim;

	@Mock MockAnnotationDAO annotationDAO;
	@Mock TestAuditor auditor;
	@Mock AuthorityManagerImpl authMgr;
	@Mock BusinessObjectFactory businessObjectFactory;

	@Mock MockInTrayManager intrayManager;
	@Mock MockMessageSource messageSource;
	@Mock MockSiteDAO siteDao;
	@Mock MockTargetInstanceDAO targetInstanceDao;
	@Mock User mockUser;
	@Mock Agency mockAgency;

	//Important that this is earlier than now, otherwise a race condition can present itself
	final long now = System.currentTimeMillis() - 1000000L;

	private Target target4000;
	private TargetInstance targetInstance5000;
	private TargetGroup targetGroup15000;
	private TargetGroup targetGroup25000;

	@BeforeClass
	public static void setEnvironment() {
		Environment mockEnvironment = mock(Environment.class);
		when(mockEnvironment.getApplicationVersion()).thenReturn("unit test");
		when(mockEnvironment.getHeritrixVersion()).thenReturn("unit test");
		EnvironmentFactory.setEnvironment(mockEnvironment);
	}

	@AfterClass
	public static void unsetEnvironment() {
		EnvironmentFactory.setEnvironment(null);
		AuthUtil.setUser(null);
	}
	
	public TargetManagerImplTest() {
		MockitoAnnotations.initMocks(this);
	}

	@Before
	public void setUp() throws Exception {
		reset(targetDao, tim, annotationDAO, auditor, authMgr, businessObjectFactory, intrayManager, messageSource, siteDao, targetInstanceDao, mockUser, mockAgency);
		when(mockUser.getNiceName()).thenReturn("testUser");
		when(mockUser.getAgency()).thenReturn(mockAgency);
		when(mockAgency.getOid()).thenReturn(4001L);
		AuthUtil.setUser(mockUser);

		target4000 = mockTarget(4000L);
		targetInstance5000 = mockTargetInstance(5000L);
		targetGroup15000 = mockTargetGroup(15000L);
		targetGroup25000 = mockTargetGroup(25000L);

		underTest = new TargetManagerImpl();
		underTest.setAnnotationDAO(annotationDAO);
		underTest.setAuditor(auditor);
		underTest.setAuthMgr(authMgr);
		underTest.setBusinessObjectFactory(businessObjectFactory);

		underTest.setInstanceManager(tim);
		underTest.setIntrayManager(intrayManager);
		underTest.setMessageSource(messageSource);
		underTest.setSiteDao(siteDao);

		underTest.setTargetDao(targetDao);
		underTest.setTargetInstanceDao(targetInstanceDao);
		underTest.setSendGroupUpdateNotifications(false);

	}

	private Target mockTarget(long oid) {
		Target result = mock(Target.class);
		when(result.getOid()).thenReturn(oid);
		when(result.getObjectType()).thenReturn(AbstractTarget.TYPE_TARGET);
		Seed seed = mock(Seed.class);
		when(seed.isHarvestable(any(Date.class))).thenReturn(true);
		HashSet<Seed> seeds = Sets.<Seed> newHashSet(seed);
		when(result.getSeeds()).thenReturn(seeds);

		when(targetDao.load(oid)).thenReturn(result);
		when(targetDao.load(eq(oid), anyBoolean())).thenReturn(result);
		when(targetDao.reloadTarget(oid)).thenReturn(result);

		when(result.getName()).thenReturn(oid + "");

		when(result.getOwner()).thenReturn(mockUser);
		when(result.getOwningUser()).thenReturn(mockUser);
		return result;
	}

	private TargetGroup mockTargetGroup(long oid) {
		TargetGroup result = mock(TargetGroup.class);
		when(result.getOid()).thenReturn(oid);
		when(result.getObjectType()).thenReturn(AbstractTarget.TYPE_GROUP);
		when(targetDao.loadGroup(oid)).thenReturn(result);
		when(targetDao.loadGroup(eq(oid), anyBoolean())).thenReturn(result);
		when(targetDao.reloadTargetGroup(oid)).thenReturn(result);
		when(result.getName()).thenReturn(oid + "");

		when(result.getOwner()).thenReturn(mockUser);
		when(result.getOwningUser()).thenReturn(mockUser);

		return result;
	}

	private TargetInstance mockTargetInstance(long oid) {
		TargetInstance result = mock(TargetInstance.class);
		when(result.getOid()).thenReturn(oid);
		when(result.getTarget()).thenReturn(target4000);
		return result;
	}

	@Ignore
	@Test
	public final void testSaveTargetListOfGroupMemberDTO() {
		List<GroupMemberDTO> parents = new ArrayList<GroupMemberDTO>();

		GroupMemberDTO dto = new GroupMemberDTO(targetGroup15000, target4000);
		dto.setSaveState(SAVE_STATE.ORIGINAL);
		parents.add(dto);

		assertTrue(target4000.getParents().size() == 0);
		underTest.save(target4000, parents);
		assertTrue(target4000.getParents().size() > 0);
	}

	@Test
	public final void testSaveTargetDirtyHasPrivilege() {
		when(target4000.getState()).thenReturn(1);
		when(target4000.isDirty()).thenReturn(true);
		when(authMgr.hasPrivilege(target4000, Privilege.APPROVE_TARGET)).thenReturn(true);
		when(target4000.getOriginalState()).thenReturn(Target.STATE_APPROVED);
		underTest.save(target4000);
		verify(target4000, times(0)).changeState(Target.STATE_NOMINATED);
		verify(targetDao).save(target4000, null);
	}

	@Test
	public final void testSaveTargetDirtyHasPrivilegeNotApproved() {
		when(target4000.getState()).thenReturn(1);
		when(target4000.isDirty()).thenReturn(true);
		when(authMgr.hasPrivilege(target4000, Privilege.APPROVE_TARGET)).thenReturn(true);
		when(target4000.getOriginalState()).thenReturn(Target.STATE_CANCELLED);
		underTest.save(target4000);
		verify(target4000, times(0)).changeState(Target.STATE_NOMINATED);
		verify(targetDao).save(target4000, null);
	}

	@Test
	public final void testSaveTargetDirtyNoPrivilege() {
		when(target4000.getState()).thenReturn(1);
		when(target4000.isDirty()).thenReturn(true);
		when(target4000.getOriginalState()).thenReturn(Target.STATE_APPROVED);
		underTest.save(target4000);
		verify(target4000).changeState(Target.STATE_NOMINATED);
		verify(targetDao).save(target4000, null);
	}

	@Test
	public final void testSaveTarget() {
		when(target4000.getState()).thenReturn(1);
		when(target4000.getOriginalState()).thenReturn(1);
		underTest.save(target4000);
		verify(targetDao).save(target4000, null);
	}

	@Test
	public final void testSaveTargetIsNew() {
		when(target4000.getState()).thenReturn(1);
		when(target4000.getOriginalState()).thenReturn(1);
		when(target4000.isNew()).thenReturn(true);
		underTest.save(target4000);
		verify(targetDao).save(target4000, null);
	}

	@Test
	public final void testSaveTargetRemovedSchedules() {
		Schedule mockSchedule = mock(Schedule.class);
		when(target4000.getRemovedSchedules()).thenReturn(Sets.newHashSet(mockSchedule));
		when(target4000.getState()).thenReturn(1);
		when(target4000.getOriginalState()).thenReturn(1);
		underTest.save(target4000);
		verify(targetDao).save(target4000, null);
		verify(targetInstanceDao).deleteScheduledInstances(mockSchedule);
		verify(targetInstanceDao).save(mockSchedule);
	}

	@Test
	public final void testSaveTargetAuditStateChangedNullProfile() {
		when(target4000.getState()).thenReturn(1);
		when(messageSource.getMessage("target.state_1", null, Locale.getDefault())).thenReturn("test");
		Schedule mockSchedule = mock(Schedule.class);
		when(target4000.getRemovedSchedules()).thenReturn(Sets.newHashSet(mockSchedule));
		when(target4000.getOriginalState()).thenReturn(2);
		underTest.save(target4000);
		verify(auditor).audit(Target.class.getName(), 4000L, Auditor.ACTION_TARGET_STATE_CHANGE,
				"Target 4000 has changed into state 'test'");
		verifyNoMoreInteractions(auditor);
	}

	@Test
	public final void testSaveTargetAuditOriginalGroupNotNullWithNullProfile() {
		AbstractTargetDTO mockAbstractTargetDto = mock(AbstractTargetDTO.class);
		when(targetDao.loadAbstractTargetDTO(4000L)).thenReturn(mockAbstractTargetDto);
		underTest.save(target4000);
		verify(auditor).audit(Target.class.getName(), 4000L, Auditor.ACTION_UPDATE_TARGET, "Target 4000 has been updated");
		verifyNoMoreInteractions(auditor);
	}

	@Test
	public final void testSaveTargetAuditOriginalGroupNotNullWithSameProfile() {
		AbstractTargetDTO mockAbstractTargetDto = mock(AbstractTargetDTO.class);
		when(targetDao.loadAbstractTargetDTO(4000L)).thenReturn(mockAbstractTargetDto);
		Profile mockProfile = mock(Profile.class);
		long profileOid = 1236458L;
		when(mockProfile.getOid()).thenReturn(profileOid);
		when(mockProfile.getName()).thenReturn("testProfile");
		when(target4000.getProfile()).thenReturn(mockProfile);
		when(mockAbstractTargetDto.getProfileOid()).thenReturn(profileOid); // Different OIDs
		when(targetDao.loadAbstractTargetDTO(4000L)).thenReturn(mockAbstractTargetDto);
		underTest.save(target4000);
		verify(auditor).audit(Target.class.getName(), 4000L, Auditor.ACTION_UPDATE_TARGET, "Target 4000 has been updated");
		verifyNoMoreInteractions(auditor);
	}

	@Test
	public final void testSaveTargetAuditOriginalGroupNotNullWithProfile() {
		Schedule mockSchedule = mock(Schedule.class);
		when(target4000.getRemovedSchedules()).thenReturn(Sets.newHashSet(mockSchedule));
		Profile mockProfile = mock(Profile.class);
		long profileOid = 1236458L;
		when(mockProfile.getOid()).thenReturn(profileOid);
		when(mockProfile.getName()).thenReturn("testProfile");
		when(target4000.getProfile()).thenReturn(mockProfile);
		AbstractTargetDTO mockAbstractTargetDto = mock(AbstractTargetDTO.class);
		when(mockAbstractTargetDto.getOid()).thenReturn(profileOid + 123); // Different OIDs
		when(targetDao.loadAbstractTargetDTO(4000L)).thenReturn(mockAbstractTargetDto);
		underTest.save(target4000);
		verify(auditor).audit(Target.class.getName(), 4000L, Auditor.ACTION_UPDATE_TARGET, "Target 4000 has been updated");
		verify(auditor).audit(Target.class.getName(), 4000L, Auditor.ACTION_TARGET_CHANGE_PROFILE,
				"Target 4000 is now using profile testProfile");
		verifyNoMoreInteractions(auditor);
	}

	@Test
	public final void testSaveTargetAuditChangeOwner() {
		Schedule mockSchedule = mock(Schedule.class);
		when(target4000.getRemovedSchedules()).thenReturn(Sets.newHashSet(mockSchedule));
		Profile mockProfile = mock(Profile.class);
		when(mockProfile.getName()).thenReturn("testProfile");
		when(target4000.getProfile()).thenReturn(mockProfile);
		AbstractTargetDTO mockAbstractTargetDto = mock(AbstractTargetDTO.class);
		when(mockAbstractTargetDto.getOwnerOid()).thenReturn(12345L);
		when(targetDao.loadAbstractTargetDTO(4000L)).thenReturn(mockAbstractTargetDto);
		underTest.save(target4000);
		verify(auditor).audit(Target.class.getName(), 4000L, Auditor.ACTION_UPDATE_TARGET, "Target 4000 has been updated");
		verify(auditor).audit(Target.class.getName(), 4000L, Auditor.ACTION_TARGET_CHANGE_OWNER,
				"Target 4000 has been given to testUser");
		verifyNoMoreInteractions(auditor);
	}

	@Test
	public final void testSaveTargetAuditNoChangeToOwner() {
		Schedule mockSchedule = mock(Schedule.class);
		when(target4000.getRemovedSchedules()).thenReturn(Sets.newHashSet(mockSchedule));
		Profile mockProfile = mock(Profile.class);
		when(mockProfile.getName()).thenReturn("testProfile");
		when(target4000.getProfile()).thenReturn(mockProfile);
		AbstractTargetDTO mockAbstractTargetDto = mock(AbstractTargetDTO.class);
		when(targetDao.loadAbstractTargetDTO(4000L)).thenReturn(mockAbstractTargetDto);
		underTest.save(target4000);
		verify(auditor).audit(Target.class.getName(), 4000L, Auditor.ACTION_UPDATE_TARGET, "Target 4000 has been updated");
		verifyNoMoreInteractions(auditor);
	}

	@Test
	public final void testSaveTargetAuditStateChanged() {
		when(target4000.getState()).thenReturn(1);
		when(messageSource.getMessage("target.state_1", null, Locale.getDefault())).thenReturn("test");
		Schedule mockSchedule = mock(Schedule.class);
		when(target4000.getRemovedSchedules()).thenReturn(Sets.newHashSet(mockSchedule));
		when(target4000.getOriginalState()).thenReturn(2);
		underTest.save(target4000);
		verify(targetDao).save(target4000, null);
		verify(targetInstanceDao).deleteScheduledInstances(mockSchedule);
		verify(targetInstanceDao).save(mockSchedule);
		verify(auditor).audit("org.webcurator.domain.model.core.Target", 4000L, "TARGET_STATE_CHANGE",
				"Target 4000 has changed into state 'test'");
	}

	@Test
	public final void testSaveTargetTaskGeneratedNominated() {
		when(target4000.getState()).thenReturn(Target.STATE_NOMINATED);
		when(target4000.getOriginalState()).thenReturn(Target.STATE_REJECTED);
		underTest.save(target4000);
		verify(targetDao).save(target4000, null);
		verify(intrayManager).generateTask(Privilege.APPROVE_TARGET, MessageType.TASK_APPROVE_TARGET, target4000);
	}

	@Test
	public final void testSaveTargetTaskGeneratedApproved() {
		when(target4000.getResourceType()).thenReturn("testResourceType");
		when(target4000.getState()).thenReturn(Target.STATE_APPROVED);
		when(target4000.getOriginalState()).thenReturn(Target.STATE_PENDING);
		underTest.save(target4000);
		verify(targetDao).save(target4000, null);
		verify(intrayManager).deleteTask(4000L, "testResourceType", MessageType.TASK_APPROVE_TARGET);
	}

	@Test
	public final void testSaveTargetTaskGeneratedHarvestOnApprove() {
		when(target4000.getState()).thenReturn(Target.STATE_APPROVED);
		when(target4000.getOriginalState()).thenReturn(Target.STATE_PENDING);
		when(target4000.isRunOnApproval()).thenReturn(true);
		underTest.save(target4000);
		verify(targetDao).save(target4000, null);
		verify(targetInstanceDao).save(any(TargetInstance.class));
	}

	@Test
	public final void testSaveTargetTaskGeneratedHarvestNow() {
		when(target4000.getState()).thenReturn(Target.STATE_APPROVED);
		when(target4000.getOriginalState()).thenReturn(Target.STATE_APPROVED);
		when(target4000.isHarvestNow()).thenReturn(true);
		underTest.save(target4000);
		verify(targetDao).save(target4000, null);
		verify(targetInstanceDao).save(any(TargetInstance.class));
	}

	@Test
	public final void testSaveTargetTaskGeneratedHarvestNowFalse() {
		when(target4000.getState()).thenReturn(Target.STATE_APPROVED);
		when(target4000.getOriginalState()).thenReturn(Target.STATE_APPROVED);
		when(target4000.isHarvestNow()).thenReturn(false);
		underTest.save(target4000);
		verify(targetDao).save(target4000, null);
		verify(targetInstanceDao, times(0)).save(any(TargetInstance.class));
	}

	@Test
	public final void testSaveTargetTaskGeneratedRejected() {
		when(target4000.getResourceType()).thenReturn("testResourceType");
		when(target4000.getState()).thenReturn(Target.STATE_REJECTED);
		when(target4000.getOriginalState()).thenReturn(Target.STATE_COMPLETED);
		underTest.save(target4000);
		verify(targetDao).save(target4000, null);
		verify(intrayManager).deleteTask(4000L, "testResourceType", MessageType.TASK_APPROVE_TARGET);
	}

	@Test
	public final void testSaveTargetSchedulable() {
		when(target4000.isSchedulable()).thenReturn(true);
		when(target4000.getState()).thenReturn(Target.STATE_REJECTED);
		when(target4000.getOriginalState()).thenReturn(Target.STATE_COMPLETED);
		Schedule mockSchedule = mock(Schedule.class);
		when(mockSchedule.getOwningUser()).thenReturn(mockUser);
		Date nowPlusAMillionMs = new Date(System.currentTimeMillis() + 1000000L);
		when(mockSchedule.getNextExecutionDate(any(Date.class))).thenReturn(nowPlusAMillionMs);
		when(mockSchedule.getStartDate()).thenReturn(new Date(System.currentTimeMillis() + 1000000L));
		when(mockSchedule.getTarget()).thenReturn(target4000);
		when(target4000.getSchedules()).thenReturn(Sets.newHashSet(mockSchedule));
		underTest.save(target4000);
		verify(targetDao).save(mockSchedule);
	}

	@Test
	public final void testSaveTargetSchedulableMultiple() {
		when(target4000.isSchedulable()).thenReturn(true);
		when(target4000.getState()).thenReturn(Target.STATE_REJECTED);
		when(target4000.getOriginalState()).thenReturn(Target.STATE_COMPLETED);
		Schedule mockSchedule1 = mock(Schedule.class);
		when(mockSchedule1.getOwningUser()).thenReturn(mockUser);
		Date nowPlusAMillionMs = new Date(System.currentTimeMillis() + 1000000L);
		when(mockSchedule1.getNextExecutionDate(any(Date.class))).thenReturn(nowPlusAMillionMs);
		when(mockSchedule1.getTarget()).thenReturn(target4000);
		when(mockSchedule1.getStartDate()).thenReturn(nowPlusAMillionMs);
		Schedule mockSchedule2 = mock(Schedule.class);
		when(mockSchedule2.getOwningUser()).thenReturn(mockUser);
		when(mockSchedule2.getStartDate()).thenReturn(nowPlusAMillionMs);
		when(mockSchedule2.getTarget()).thenReturn(target4000);
		Schedule mockSchedule3 = mock(Schedule.class);
		when(mockSchedule3.getOwningUser()).thenReturn(mockUser);
		when(mockSchedule3.getNextExecutionDate(any(Date.class))).thenReturn(nowPlusAMillionMs);
		when(mockSchedule3.getStartDate()).thenReturn(nowPlusAMillionMs);
		when(mockSchedule3.getTarget()).thenReturn(target4000);
		when(target4000.getSchedules()).thenReturn(Sets.newHashSet(mockSchedule1, mockSchedule2, mockSchedule3));
		underTest.save(target4000);
		verify(targetDao).save(mockSchedule1);
		verify(targetDao, times(0)).save(mockSchedule2);
	}

	@Test
	public final void testSaveTargetGroup() {
		when(targetGroup15000.getState()).thenReturn(1);
		when(targetGroup15000.getOriginalState()).thenReturn(1);
		underTest.save(targetGroup15000);
		verify(targetDao).save(targetGroup15000, true, null);
	}

	@Test
	public final void testSaveTargetGroupIsNew() {
		when(targetGroup15000.getState()).thenReturn(1);
		when(targetGroup15000.getOriginalState()).thenReturn(1);
		when(targetGroup15000.isNew()).thenReturn(true);
		underTest.save(targetGroup15000);
		verify(targetDao).save(targetGroup15000, true, null);
	}

	@Test
	public final void testSaveTargetGroupRemovedSchedules() {
		Schedule mockSchedule = mock(Schedule.class);
		when(targetGroup15000.getRemovedSchedules()).thenReturn(Sets.newHashSet(mockSchedule));
		when(targetGroup15000.getState()).thenReturn(1);
		when(targetGroup15000.getOriginalState()).thenReturn(1);
		underTest.save(targetGroup15000);
		verify(targetDao).save(targetGroup15000, true, null);
		verify(targetInstanceDao).deleteScheduledInstances(mockSchedule);
	}

	@Test
	public final void testSaveTargetGroupAuditStateChangedNullProfile() {
		when(targetGroup15000.getState()).thenReturn(1);
		when(messageSource.getMessage("target.state_1", null, Locale.getDefault())).thenReturn("test");
		Schedule mockSchedule = mock(Schedule.class);
		when(targetGroup15000.getRemovedSchedules()).thenReturn(Sets.newHashSet(mockSchedule));
		when(targetGroup15000.getOriginalState()).thenReturn(2);
		underTest.save(targetGroup15000);
		verify(auditor).audit(TargetGroup.class.getName(), 15000L, Auditor.ACTION_TARGET_GROUP_STATE_CHANGE,
				"Target Group 15000 has changed into state '1'");
		verify(auditor).audit(TargetGroup.class.getName(), 15000L, Auditor.ACTION_NEW_TARGET_GROUP,
				"Target Group 15000 has been created");
		verifyNoMoreInteractions(auditor);
	}

	@Test
	public final void testSaveTargetGroupAuditOriginalGroupNotNullWithNullProfile() {
		AbstractTargetDTO mockAbstractTargetDto = mock(AbstractTargetDTO.class);
		when(targetDao.loadAbstractTargetDTO(15000L)).thenReturn(mockAbstractTargetDto);
		underTest.save(targetGroup15000);
		verify(auditor).audit(TargetGroup.class.getName(), 15000L, Auditor.ACTION_UPDATE_TARGET_GROUP,
				"Target Group 15000 has been updated");
		verifyNoMoreInteractions(auditor);
	}

	@Test
	public final void testSaveTargetGroupAuditOriginalGroupNotNullWithSameProfile() {
		AbstractTargetDTO mockAbstractTargetDto = mock(AbstractTargetDTO.class);
		when(targetDao.loadAbstractTargetDTO(15000L)).thenReturn(mockAbstractTargetDto);
		Profile mockProfile = mock(Profile.class);
		long profileOid = 1236458L;
		when(mockProfile.getOid()).thenReturn(profileOid);
		when(mockProfile.getName()).thenReturn("testProfile");
		when(targetGroup15000.getProfile()).thenReturn(mockProfile);
		when(mockAbstractTargetDto.getProfileOid()).thenReturn(profileOid); // Different OIDs
		when(targetDao.loadAbstractTargetDTO(15000L)).thenReturn(mockAbstractTargetDto);
		underTest.save(targetGroup15000);
		verify(auditor).audit(TargetGroup.class.getName(), 15000L, Auditor.ACTION_UPDATE_TARGET_GROUP,
				"Target Group 15000 has been updated");
		verifyNoMoreInteractions(auditor);
	}

	@Test
	public final void testSaveTargetGroupAuditOriginalGroupNotNullWithProfile() {
		Schedule mockSchedule = mock(Schedule.class);
		when(targetGroup15000.getRemovedSchedules()).thenReturn(Sets.newHashSet(mockSchedule));
		Profile mockProfile = mock(Profile.class);
		long profileOid = 1236458L;
		when(mockProfile.getOid()).thenReturn(profileOid);
		when(mockProfile.getName()).thenReturn("testProfile");
		when(targetGroup15000.getProfile()).thenReturn(mockProfile);
		AbstractTargetDTO mockAbstractTargetDto = mock(AbstractTargetDTO.class);
		when(mockAbstractTargetDto.getOid()).thenReturn(profileOid + 123); // Different OIDs
		when(targetDao.loadAbstractTargetDTO(15000L)).thenReturn(mockAbstractTargetDto);
		underTest.save(targetGroup15000);
		verify(auditor).audit(TargetGroup.class.getName(), 15000L, Auditor.ACTION_UPDATE_TARGET_GROUP,
				"Target Group 15000 has been updated");
		verify(auditor).audit(TargetGroup.class.getName(), 15000L, Auditor.ACTION_TARGET_GROUP_CHANGE_PROFILE,
				"Target Group 15000 is now using profile testProfile");
		verifyNoMoreInteractions(auditor);
	}

	@Test
	public final void testSaveTargetGroupAuditChangeOwner() {
		Schedule mockSchedule = mock(Schedule.class);
		when(targetGroup15000.getRemovedSchedules()).thenReturn(Sets.newHashSet(mockSchedule));
		Profile mockProfile = mock(Profile.class);
		when(mockProfile.getName()).thenReturn("testProfile");
		when(targetGroup15000.getProfile()).thenReturn(mockProfile);
		AbstractTargetDTO mockAbstractTargetDto = mock(AbstractTargetDTO.class);
		when(mockAbstractTargetDto.getOwnerOid()).thenReturn(12345L);
		when(targetDao.loadAbstractTargetDTO(15000L)).thenReturn(mockAbstractTargetDto);
		underTest.save(targetGroup15000);
		verify(auditor).audit(TargetGroup.class.getName(), 15000L, Auditor.ACTION_UPDATE_TARGET_GROUP,
				"Target Group 15000 has been updated");
		verify(auditor).audit(TargetGroup.class.getName(), 15000L, Auditor.ACTION_TARGET_GROUP_CHANGE_OWNER,
				"Target Group 15000 has been given to testUser");
		verifyNoMoreInteractions(auditor);
	}

	@Test
	public final void testSaveTargetGroupSendsNoNotifications() {
		Set<AbstractTargetDTO> changeSet = Sets.newHashSet();
		when(targetDao.getAncestorDTOs(anyLong())).thenReturn(changeSet);
		when(targetGroup15000.isNew()).thenReturn(true);
		underTest.setSendGroupUpdateNotifications(true);
		when(targetGroup15000.getState()).thenReturn(1);
		when(targetGroup15000.getOriginalState()).thenReturn(1);
		underTest.save(targetGroup15000);
		verify(targetDao).save(targetGroup15000, true, null);
		verifyNoMoreInteractions(intrayManager);
	}

	@Test
	public final void testSaveTargetGroupSendNotificationsAddedParents() {
		AbstractTargetDTO mockAbstractTargetDTO = mock(AbstractTargetDTO.class);
		when(mockAbstractTargetDTO.getName()).thenReturn(new Random().nextInt() + "");
		Set<AbstractTargetDTO> changeSet = Sets.newHashSet(mockAbstractTargetDTO);
		when(targetDao.getAncestorDTOs(anyLong())).thenReturn(changeSet);
		when(targetGroup15000.isNew()).thenReturn(true);
		underTest.setSendGroupUpdateNotifications(true);
		when(targetGroup15000.getState()).thenReturn(1);
		when(targetGroup15000.getOriginalState()).thenReturn(1);
		underTest.save(targetGroup15000);
		verify(targetDao).save(targetGroup15000, true, null);
		verify(intrayManager).generateNotification(
				anyLong(), 
				eq(MessageType.CATEGORY_MISC), 
				eq("Target Group membership updated"),
				anyString());
	}

	@Test
	public final void testSaveTargetGroupSendNotificationsRemovedParents() {
		AbstractTargetDTO mockAbstractTargetDTO = mock(AbstractTargetDTO.class);
		when(mockAbstractTargetDTO.getName()).thenReturn(new Random().nextInt() + "");
		Set<AbstractTargetDTO> changeSet = Sets.newHashSet(mockAbstractTargetDTO);
		Set<AbstractTargetDTO> emptyChangeSet = Sets.newHashSet();
		when(targetDao.getAncestorDTOs(anyLong())).thenReturn(changeSet).thenReturn(emptyChangeSet);
		when(targetGroup15000.isNew()).thenReturn(false);
		underTest.setSendGroupUpdateNotifications(true);
		when(targetGroup15000.getState()).thenReturn(1);
		when(targetGroup15000.getOriginalState()).thenReturn(1);


		underTest.save(targetGroup15000);
		verify(targetDao, times(2)).getAncestorDTOs(anyLong());
		verify(targetDao).save(targetGroup15000, true, null);
		verify(intrayManager).generateNotification(
				anyLong(), 
				eq(MessageType.CATEGORY_MISC), 
				eq("Target Group membership updated"),
				anyString());
	}
	
	@Test
	public void testTargetGroupUpdateParentStatus() {
		GroupMemberDTO mockGroupMemberDto = mock(GroupMemberDTO.class);
		when(mockGroupMemberDto.getSaveState()).thenReturn(SAVE_STATE.ORIGINAL);
		ArrayList<GroupMemberDTO> parents = Lists.newArrayList(mockGroupMemberDto);
		underTest.save(targetGroup15000, parents);
		verify(targetDao, times(0)).loadGroup(anyInt());
	}

	@Test
	public void testTargetGroupPropagateEventsNew() {
		GroupMemberDTO mockGroupMemberDto = mock(GroupMemberDTO.class);
		when(mockGroupMemberDto.getSaveState()).thenReturn(SAVE_STATE.NEW);
		when(mockGroupMemberDto.getParentOid()).thenReturn(25000L);
		when(targetDao.loadGroup(25000L)).thenReturn(targetGroup25000);
		ArrayList<GroupMemberDTO> parents = Lists.newArrayList(mockGroupMemberDto);
		underTest.save(targetGroup15000, parents);
		verify(targetDao, times(1)).loadGroup(25000L);
	}

	@Test
	public void testTargetGroupPropagateEventsDeleted() {
		GroupMemberDTO mockGroupMemberDto = mock(GroupMemberDTO.class);
		when(mockGroupMemberDto.getSaveState()).thenReturn(SAVE_STATE.DELETED);
		when(mockGroupMemberDto.getParentOid()).thenReturn(25000L);
		when(targetDao.loadGroup(25000L)).thenReturn(targetGroup25000);
		ArrayList<GroupMemberDTO> parents = Lists.newArrayList(mockGroupMemberDto);
		underTest.save(targetGroup15000, parents);
		//Loaded once by event propagator, twice by TargetManager itself.
		verify(targetDao, times(2)).loadGroup(25000L);
		verify(targetInstanceDao, times(2)).deleteScheduledInstances(targetGroup25000);
		verify(targetInstanceDao, times(2)).deleteScheduledInstances(targetGroup15000);
	}

	@Test
	public final void testScheduleTargetGroupNoSchedules() {
		when(targetGroup15000.getSchedules()).thenReturn(Sets.<Schedule> newHashSet());
		underTest.scheduleTargetGroup(targetGroup15000);
	}

	@Test
	public final void testScheduleTargetGroup() {
		Schedule schedule = mock(Schedule.class);
		when(schedule.getOwningUser()).thenReturn(mockUser);
		when(schedule.getTarget()).thenReturn(target4000);
		HashSet<Schedule> schedules = Sets.<Schedule> newHashSet(schedule);
		when(targetGroup15000.getSchedules()).thenReturn(schedules);
		underTest.scheduleTargetGroup(targetGroup15000);
	}

	@Test
	public final void testGetNextStatesPendingAuthorized() {
		when(authMgr.hasPrivilege(target4000, Privilege.APPROVE_TARGET)).thenReturn(true);
		when(target4000.getOriginalState()).thenReturn(Target.STATE_PENDING);
		int[] states = underTest.getNextStates(target4000);
		assertEquals(states.length, 3);
	}

	@Test
	public final void testGetNextStatesPendingNotAuthorized() {
		when(authMgr.hasPrivilege(target4000, Privilege.APPROVE_TARGET)).thenReturn(false);
		when(target4000.getOriginalState()).thenReturn(Target.STATE_PENDING);
		int[] states = underTest.getNextStates(target4000);
		assertEquals(states.length, 1);
	}

	@Test
	public final void testGetNextStatesReinstatedAuthorized() {
		when(authMgr.hasPrivilege(target4000, Privilege.APPROVE_TARGET)).thenReturn(true);
		when(target4000.getOriginalState()).thenReturn(Target.STATE_REINSTATED);
		int[] states = underTest.getNextStates(target4000);
		assertEquals(states.length, 3);
	}

	@Test
	public final void testGetNextStatesReinstatedNotAuthorized() {
		when(authMgr.hasPrivilege(target4000, Privilege.APPROVE_TARGET)).thenReturn(false);
		when(target4000.getOriginalState()).thenReturn(Target.STATE_REINSTATED);
		int[] states = underTest.getNextStates(target4000);
		assertEquals(states.length, 1);
	}

	@Test
	public final void testGetNextStatesNominatedAuthorized() {
		when(authMgr.hasPrivilege(target4000, Privilege.APPROVE_TARGET)).thenReturn(true);
		when(target4000.getOriginalState()).thenReturn(Target.STATE_NOMINATED);
		int[] states = underTest.getNextStates(target4000);
		assertEquals(states.length, 2);
	}

	@Test
	public final void testGetNextStatesNominatedNotAuthorized() {
		when(authMgr.hasPrivilege(target4000, Privilege.APPROVE_TARGET)).thenReturn(false);
		when(target4000.getOriginalState()).thenReturn(Target.STATE_NOMINATED);
		int[] states = underTest.getNextStates(target4000);
		assertEquals(states.length, 0);
	}

	@Test
	public final void testGetNextStatesRejectedAuthorized() {
		when(authMgr.hasPrivilege(target4000, Privilege.REINSTATE_TARGET)).thenReturn(true);
		when(target4000.getOriginalState()).thenReturn(Target.STATE_REJECTED);
		int[] states = underTest.getNextStates(target4000);
		assertEquals(states.length, 1);
	}

	@Test
	public final void testGetNextStatesRejectedNotAuthorized() {
		when(authMgr.hasPrivilege(target4000, Privilege.REINSTATE_TARGET)).thenReturn(false);
		when(target4000.getOriginalState()).thenReturn(Target.STATE_REJECTED);
		int[] states = underTest.getNextStates(target4000);
		assertEquals(states.length, 0);
	}

	@Test
	public final void testGetNextStatesApprovedAuthorized() {
		when(authMgr.hasPrivilege(target4000, Privilege.CANCEL_TARGET)).thenReturn(true);
		when(target4000.getOriginalState()).thenReturn(Target.STATE_APPROVED);
		int[] states = underTest.getNextStates(target4000);
		assertEquals(states.length, 1);
	}

	@Test
	public final void testGetNextStatesApprovedNotAuthorized() {
		when(authMgr.hasPrivilege(target4000, Privilege.CANCEL_TARGET)).thenReturn(false);
		when(target4000.getOriginalState()).thenReturn(Target.STATE_APPROVED);
		int[] states = underTest.getNextStates(target4000);
		assertEquals(states.length, 0);
	}

	@Test
	public final void testGetNextStatesCancelledAuthorized() {
		when(authMgr.hasPrivilege(target4000, Privilege.REINSTATE_TARGET)).thenReturn(true);
		when(target4000.getOriginalState()).thenReturn(Target.STATE_CANCELLED);
		int[] states = underTest.getNextStates(target4000);
		assertEquals(states.length, 1);
	}

	@Test
	public final void testGetNextStatesCancelledNotAuthorized() {
		when(authMgr.hasPrivilege(target4000, Privilege.REINSTATE_TARGET)).thenReturn(false);
		when(target4000.getOriginalState()).thenReturn(Target.STATE_CANCELLED);
		int[] states = underTest.getNextStates(target4000);
		assertEquals(states.length, 0);
	}

	@Test
	public final void testGetNextStatesCompletedAuthorized() {
		when(authMgr.hasPrivilege(target4000, Privilege.REINSTATE_TARGET)).thenReturn(true);
		when(target4000.getOriginalState()).thenReturn(Target.STATE_COMPLETED);
		int[] states = underTest.getNextStates(target4000);
		assertEquals(states.length, 1);
	}

	@Test
	public final void testGetNextStatesCompletedNotAuthorized() {
		when(authMgr.hasPrivilege(target4000, Privilege.REINSTATE_TARGET)).thenReturn(false);
		when(target4000.getOriginalState()).thenReturn(Target.STATE_COMPLETED);
		int[] states = underTest.getNextStates(target4000);
		assertEquals(states.length, 0);
	}

	@Test
	public final void testGetNextStatesUnknown() {
		when(target4000.getOriginalState()).thenReturn(79047902);
		int[] states = underTest.getNextStates(target4000);
		assertEquals(states.length, 0);
	}

	@Test
	public final void testLoadLong() {

		assertNotNull(target4000);
	}

	@Test
	public final void testLoadLongBoolean() {
		Target t = underTest.load(4000L, false);
		assertNotNull(t);
		t = underTest.load(4000L, true);
		assertNotNull(t);
	}

	@Test
	public final void testGetQuickPickPermissions() {
		underTest.getQuickPickPermissions(target4000);
		verify(siteDao).getQuickPickPermissions(any(Agency.class));
	}

	@Test
	public final void testFindPermissionsBySiteTitle() {
		underTest.findPermissionsBySiteTitle(target4000, "Oakleigh Web Site", 0);
		verify(siteDao).findPermissionsBySiteTitle(4001L, "Oakleigh Web Site", 0);
	}

	@Test
	public final void testFindPermissionsByUrl() {
		String url = "www.oakleigh.co.uk";
		PermissionMappingStrategy strategy = mock(PermissionMappingStrategy.class);
		Permission mockPermission = mock(Permission.class);
		HashSet<Permission> newHashSet = Sets.<Permission> newHashSet(mockPermission);
		when(strategy.getMatchingPermissions(target4000, url)).thenReturn(newHashSet);
		PermissionMappingStrategy.setStrategy(strategy);

		Pagination permissions = underTest.findPermissionsByUrl(target4000, url, 0);
		verify(strategy).getMatchingPermissions(target4000, url);

		assertNotNull(permissions);
		assertEquals(1, permissions.getTotal());
		assertFalse(permissions.getList().isEmpty());
	}

	@Test
	public final void testLoadPermissionTargetEditorContextString() {
		Permission mockPermission = mock(Permission.class);
		when(mockPermission.getOid()).thenReturn(12345L);
		when(siteDao.loadPermission(7000L)).thenReturn(mockPermission);
		TargetEditorContext ctx = new TargetEditorContext(underTest, target4000, true);
		ctx.putObject(mockPermission);
		Permission permission = underTest.loadPermission(ctx, "7000");
		assertNotNull(permission);
	}

	@Test
	public final void testLoadPermissionTargetEditorContextStringFromDao() {
		Permission mockPermission = mock(Permission.class);
		when(mockPermission.getOid()).thenReturn(12345L);
		when(siteDao.loadPermission(7000L)).thenReturn(mockPermission);
		TargetEditorContext ctx = new TargetEditorContext(underTest, target4000, true);
		Permission permission = underTest.loadPermission(ctx, "7000");

		assertNotNull(permission);
	}

	@Test
	public final void testLoadPermissionLong() {
		long oid = 7000L;
		Permission permission = mock(Permission.class);
		when(siteDao.loadPermission(oid)).thenReturn(permission);
		Permission result = underTest.loadPermission(oid);
		assertEquals(permission, result);
	}

	@Test
	public final void testIsNameOk() {
		underTest.isNameOk(target4000);
		verify(targetDao).isNameOk(target4000);
	}

	@Test
	public final void testGetAnnotationsNoneFound() {
		List<Annotation> result = underTest.getAnnotations(target4000);
		assertNotNull(result);
		assertEquals(0, result.size());
		verify(annotationDAO).loadAnnotations(anyString(), eq(4000L));
	}

	@Test
	public final void testGetAnnotations() {
		ArrayList<Annotation> annotations = Lists.newArrayList();
		when(annotationDAO.loadAnnotations(anyString(), eq(4000L))).thenReturn(annotations);
		underTest.getAnnotations(target4000);
		verify(annotationDAO).loadAnnotations(anyString(), eq(4000L));
	}

	@Test
	public final void testGetAnnotationsGroup() {
		ArrayList<Annotation> annotations = Lists.newArrayList();
		when(annotationDAO.loadAnnotations(anyString(), eq(15000L))).thenReturn(annotations);
		underTest.getAnnotations(targetGroup15000);
		verify(annotationDAO).loadAnnotations(TargetGroup.class.getName(), 15000L);
	}

	@Test
	public final void testIsTargetHarvestable() {
		assertTrue(underTest.isTargetHarvestable(targetInstance5000));
	}

	@Test
	public final void testIsTargetHarvestableNoSeeds() {
		targetInstance5000.getTarget().getSeeds().clear();
		assertFalse(underTest.isTargetHarvestable(targetInstance5000));
	}

	@Test
	public final void testIsTargetHarvestableSeedNotHarvestable() {
		Set<Seed> seeds = targetInstance5000.getTarget().getSeeds();
		seeds.clear();
		Seed seed = mock(Seed.class);
		when(seed.isHarvestable(any(Date.class))).thenReturn(false);
		seeds.add(seed);
		assertFalse(underTest.isTargetHarvestable(targetInstance5000));
	}

	@Test
	public final void testAllowStateChangeIsInSameState() {
		when(target4000.getOriginalState()).thenReturn(Target.STATE_NOMINATED);
		boolean result = underTest.allowStateChange(target4000, Target.STATE_NOMINATED);
		assertTrue(result);
	}

	@Test
	public final void testAllowStateChangeFromPendingToNominatedAndHasPrivilege() {
		when(authMgr.hasPrivilege(target4000, Privilege.APPROVE_TARGET)).thenReturn(true);
		when(target4000.getOriginalState()).thenReturn(Target.STATE_PENDING);
		boolean result = underTest.allowStateChange(target4000, Target.STATE_NOMINATED);
		assertTrue(result);
	}

	@Test
	public final void testAllowStateChangePendingToApprovedAndHasPrivilege() {
		when(authMgr.hasPrivilege(target4000, Privilege.APPROVE_TARGET)).thenReturn(true);
		when(target4000.getOriginalState()).thenReturn(Target.STATE_PENDING);
		boolean result = underTest.allowStateChange(target4000, Target.STATE_APPROVED);
		assertTrue(result);
	}

	@Test
	public final void testAllowStateChangePendingToApprovedAndHasNoPrivilege() {
		when(authMgr.hasPrivilege(target4000, Privilege.APPROVE_TARGET)).thenReturn(false);
		when(target4000.getOriginalState()).thenReturn(Target.STATE_PENDING);
		boolean result = underTest.allowStateChange(target4000, Target.STATE_APPROVED);
		assertFalse(result);
	}

	@Test
	public final void testLoadGroupLong() {
		underTest.loadGroup(15000L);
		verify(targetDao).loadGroup(15000L);
	}

	@Test
	public final void testLoadGroupLongBoolean() {
		underTest.loadGroup(15000L, false);
		verify(targetDao).loadGroup(15000L, false);
		underTest.loadGroup(15000L, true);
		verify(targetDao).loadGroup(15000L, true);
	}

	@Test
	public final void testGetActiveParentGroups() {
		TargetInstance ti = mock(TargetInstance.class);
		Date now = new Date();
		when(ti.getActualStartTime()).thenReturn(now);

		AbstractTarget abstractTarget = mock(AbstractTarget.class);
		when(ti.getTarget()).thenReturn(abstractTarget);
		TargetGroup group1 = mock(TargetGroup.class);
		TargetGroup group2 = mock(TargetGroup.class);
		GroupMember member1 = mock(GroupMember.class);
		when(member1.getParent()).thenReturn(group1);
		GroupMember member2 = mock(GroupMember.class);
		when(member2.getParent()).thenReturn(group2);
		when(abstractTarget.getParents()).thenReturn(Sets.newHashSet(member1, member2));

		// Test with valid fromDate
		List<TargetGroup> groups = underTest.getActiveParentGroups(ti);
		assertNotNull(groups);
		assertEquals(2, groups.size());
	}

	@Test
	public final void testGetActiveParentGroupsBefore() {
		TargetInstance ti = mock(TargetInstance.class);
		Date now = new Date();
		when(ti.getActualStartTime()).thenReturn(now);

		AbstractTarget abstractTarget = mock(AbstractTarget.class);
		when(ti.getTarget()).thenReturn(abstractTarget);
		TargetGroup group1 = mock(TargetGroup.class);
		when(group1.getFromDate()).thenReturn(new Date(now.getTime() + 1000L));
		TargetGroup group2 = mock(TargetGroup.class);
		GroupMember member1 = mock(GroupMember.class);
		when(member1.getParent()).thenReturn(group1);
		GroupMember member2 = mock(GroupMember.class);
		when(member2.getParent()).thenReturn(group2);
		when(abstractTarget.getParents()).thenReturn(Sets.newHashSet(member1, member2));

		// Test with valid fromDate
		List<TargetGroup> groups = underTest.getActiveParentGroups(ti);
		assertNotNull(groups);
		assertEquals(1, groups.size());
		assertFalse(groups.contains(group1));
		assertTrue(groups.contains(group2));
	}

	@Test
	public final void testGetActiveParentGroupsNotBefore() {
		TargetInstance ti = mock(TargetInstance.class);
		Date now = new Date();
		when(ti.getActualStartTime()).thenReturn(now);

		AbstractTarget abstractTarget = mock(AbstractTarget.class);
		when(ti.getTarget()).thenReturn(abstractTarget);
		TargetGroup group1 = mock(TargetGroup.class);
		when(group1.getFromDate()).thenReturn(new Date(now.getTime() - 1000L));
		TargetGroup group2 = mock(TargetGroup.class);
		GroupMember member1 = mock(GroupMember.class);
		when(member1.getParent()).thenReturn(group1);
		GroupMember member2 = mock(GroupMember.class);
		when(member2.getParent()).thenReturn(group2);
		when(abstractTarget.getParents()).thenReturn(Sets.newHashSet(member1, member2));

		// Test with valid fromDate
		List<TargetGroup> groups = underTest.getActiveParentGroups(ti);
		assertNotNull(groups);
		assertEquals(2, groups.size());
		assertTrue(groups.contains(group1));
		assertTrue(groups.contains(group2));
	}

	@Test
	public final void testGetActiveParentGroupsAfter() {
		TargetInstance ti = mock(TargetInstance.class);
		Date now = new Date();
		when(ti.getActualStartTime()).thenReturn(now);

		AbstractTarget abstractTarget = mock(AbstractTarget.class);
		when(ti.getTarget()).thenReturn(abstractTarget);
		TargetGroup group1 = mock(TargetGroup.class);
		when(group1.getToDate()).thenReturn(new Date(now.getTime() - 1000L));
		TargetGroup group2 = mock(TargetGroup.class);
		GroupMember member1 = mock(GroupMember.class);
		when(member1.getParent()).thenReturn(group1);
		GroupMember member2 = mock(GroupMember.class);
		when(member2.getParent()).thenReturn(group2);
		when(abstractTarget.getParents()).thenReturn(Sets.newHashSet(member1, member2));

		// Test with valid fromDate
		List<TargetGroup> groups = underTest.getActiveParentGroups(ti);
		assertNotNull(groups);
		assertEquals(1, groups.size());
		assertFalse(groups.contains(group1));
		assertTrue(groups.contains(group2));
	}

	@Test
	public final void testGetActiveParentGroupsNotAfter() {
		TargetInstance ti = mock(TargetInstance.class);
		Date now = new Date();
		when(ti.getActualStartTime()).thenReturn(now);

		AbstractTarget abstractTarget = mock(AbstractTarget.class);
		when(ti.getTarget()).thenReturn(abstractTarget);
		TargetGroup group1 = mock(TargetGroup.class);
		when(group1.getToDate()).thenReturn(new Date(now.getTime() + 1000L));
		TargetGroup group2 = mock(TargetGroup.class);
		GroupMember member1 = mock(GroupMember.class);
		when(member1.getParent()).thenReturn(group1);
		GroupMember member2 = mock(GroupMember.class);
		when(member2.getParent()).thenReturn(group2);
		when(abstractTarget.getParents()).thenReturn(Sets.newHashSet(member1, member2));

		// Test with valid fromDate
		List<TargetGroup> groups = underTest.getActiveParentGroups(ti);
		assertNotNull(groups);
		assertEquals(2, groups.size());
		assertTrue(groups.contains(group1));
		assertTrue(groups.contains(group2));
	}

	@Test
	public final void testGetActiveParentGroupsRecurse() {
		TargetInstance ti = mock(TargetInstance.class);
		Date now = new Date();
		when(ti.getActualStartTime()).thenReturn(now);

		AbstractTarget abstractTarget = mock(AbstractTarget.class);
		when(ti.getTarget()).thenReturn(abstractTarget);
		TargetGroup group1 = mock(TargetGroup.class);
		TargetGroup group2 = mock(TargetGroup.class);
		GroupMember member1 = mock(GroupMember.class);
		when(member1.getParent()).thenReturn(group1);
		GroupMember member2 = mock(GroupMember.class);
		when(member2.getParent()).thenReturn(group2);
		when(abstractTarget.getParents()).thenReturn(Sets.newHashSet(member1, member2));

		TargetGroup group3 = mock(TargetGroup.class);
		GroupMember member3 = mock(GroupMember.class);
		when(member3.getParent()).thenReturn(group3);
		when(group1.getParents()).thenReturn(Sets.newHashSet(member3));

		// Test with valid fromDate
		List<TargetGroup> groups = underTest.getActiveParentGroups(ti);
		assertNotNull(groups);
		assertEquals(3, groups.size());
		assertTrue(groups.contains(group1));
		assertTrue(groups.contains(group2));
		assertTrue(groups.contains(group3));
	}

	@Test
	public void testPropagateEventsOriginal() {
		GroupMemberDTO mockGroupMemberDto = mock(GroupMemberDTO.class);
		when(mockGroupMemberDto.getSaveState()).thenReturn(SAVE_STATE.ORIGINAL);
		ArrayList<GroupMemberDTO> parents = Lists.newArrayList(mockGroupMemberDto);
		underTest.save(target4000, parents);
		verify(targetDao, times(0)).loadGroup(anyInt());
	}

	@Test
	public void testPropagateEventsNew() {
		GroupMemberDTO mockGroupMemberDto = mock(GroupMemberDTO.class);
		when(mockGroupMemberDto.getSaveState()).thenReturn(SAVE_STATE.NEW);
		when(mockGroupMemberDto.getParentOid()).thenReturn(15000L);
		when(targetDao.loadGroup(15000L)).thenReturn(targetGroup15000);
		ArrayList<GroupMemberDTO> parents = Lists.newArrayList(mockGroupMemberDto);
		underTest.save(target4000, parents);
		verify(targetDao).loadGroup(anyInt());
	}

	@Test
	public void testPropagateEventsDeleted() {
		GroupMemberDTO mockGroupMemberDto = mock(GroupMemberDTO.class);
		when(mockGroupMemberDto.getSaveState()).thenReturn(SAVE_STATE.DELETED);
		when(mockGroupMemberDto.getParentOid()).thenReturn(15000L);
		when(targetDao.loadGroup(15000L)).thenReturn(targetGroup15000);
		ArrayList<GroupMemberDTO> parents = Lists.newArrayList(mockGroupMemberDto);
		underTest.save(target4000, parents);
		verify(targetDao, times(2)).loadGroup(anyInt());
	}

	@Test
	public void testDeleteNoPrivilege() {
		when(authMgr.hasPrivilege(any(Target.class), anyString())).thenReturn(false);
		underTest.deleteTarget(target4000);
		verify(authMgr).hasPrivilege(eq(target4000), anyString());
		verify(targetDao, times(0)).delete(targetDao);
	}

	@Test
	public void testDeleteHasPrivilegeNotPending() {
		when(target4000.getState()).thenReturn(Target.STATE_APPROVED);
		when(authMgr.hasPrivilege(target4000, Privilege.DELETE_TARGET)).thenReturn(true);
		underTest.deleteTarget(target4000);
		verify(authMgr).hasPrivilege(target4000, Privilege.DELETE_TARGET);
		verify(targetDao, times(0)).delete(targetDao);
	}

	@Test
	public void testDeleteHasPrivilegeIsPending() {
		when(target4000.getState()).thenReturn(Target.STATE_PENDING);
		when(authMgr.hasPrivilege(target4000, Privilege.DELETE_TARGET)).thenReturn(true);
		underTest.deleteTarget(target4000);
		verify(authMgr).hasPrivilege(target4000, Privilege.DELETE_TARGET);
		verify(targetDao).delete(target4000);
	}

	@Test
	public void testDeleteHasPrivilegeIsPendingHasParents() {
		when(target4000.getState()).thenReturn(Target.STATE_PENDING);
		GroupMember mockGroupMember = mock(GroupMember.class);
		when(mockGroupMember.getParent()).thenReturn(targetGroup15000);
		Set<GroupMember> parents = Sets.newHashSet(mockGroupMember);
		when(target4000.getParents()).thenReturn(parents);
		when(authMgr.hasPrivilege(target4000, Privilege.DELETE_TARGET)).thenReturn(true);
		underTest.deleteTarget(target4000);
		verify(authMgr).hasPrivilege(target4000, Privilege.DELETE_TARGET);
		verify(targetDao).delete(target4000);
		// Make sure the parent is saved
		verify(targetDao).save(targetGroup15000, true, null);
	}

	@Test
	public void testDeleteGroupNoPrivilege() {
		when(authMgr.hasPrivilege(any(Target.class), anyString())).thenReturn(false);
		try {
			underTest.deleteTargetGroup(targetGroup15000);
			fail();
		} catch (WCTRuntimeException e) {
			verify(authMgr).hasPrivilege(eq(targetGroup15000), anyString());
			verify(targetDao, times(0)).delete(targetDao);
		}
	}

	@Test
	public void testDeleteGroupHasPrivilege() {
		when(target4000.getState()).thenReturn(Target.STATE_APPROVED);
		when(authMgr.hasPrivilege(targetGroup15000, Privilege.MANAGE_GROUP)).thenReturn(true);
		underTest.deleteTargetGroup(targetGroup15000);
		verify(authMgr).hasPrivilege(targetGroup15000, Privilege.MANAGE_GROUP);
		verify(targetDao, times(0)).delete(targetDao);
	}

	@Test
	public void testProcessBatchScheduleTarget() {
		Schedule mockSchedule = mock(Schedule.class);
		when(mockSchedule.getOwningUser()).thenReturn(mockUser);
		
		Date startDate = new Date(now);
		when(mockSchedule.getStartDate()).thenReturn(startDate);
		when(mockSchedule.getNextExecutionDate(any(Date.class))).thenReturn(startDate).thenReturn(null);
		
		Date endDate = new Date(now + 100000L);
		when(mockSchedule.getEndDate()).thenReturn(endDate);
		
		when(mockSchedule.getTarget()).thenReturn(target4000);
		when(target4000.getObjectType()).thenReturn(AbstractTarget.TYPE_TARGET);
		when(target4000.isSchedulable()).thenReturn(true);
		underTest.processBatchSchedule(mockSchedule);
		verify(targetDao).save(mockSchedule);
		verify(targetInstanceDao).save(any(TargetInstance.class));
		verify(mockSchedule).setLastProcessedDate(any(Date.class));
	}

	@Test
	public void testProcessBatchScheduleGroupOneSip() {
		Schedule mockSchedule = mock(Schedule.class);
		when(mockSchedule.getOwningUser()).thenReturn(mockUser);
		
		Date startDate = new Date(now);
		when(targetDao.getLatestScheduledDate(targetGroup15000, mockSchedule)).thenReturn(startDate);
		when(mockSchedule.getStartDate()).thenReturn(startDate);
		when(mockSchedule.getNextExecutionDate(any(Date.class))).thenReturn(startDate).thenReturn(null);
		
		Date endDate = new Date(now + 100000L);
		when(mockSchedule.getEndDate()).thenReturn(endDate);
		
		when(mockSchedule.getTarget()).thenReturn(targetGroup15000);
		when(targetGroup15000.getObjectType()).thenReturn(AbstractTarget.TYPE_GROUP);
		when(targetGroup15000.getSipType()).thenReturn(TargetGroup.ONE_SIP);
		when(targetGroup15000.isSchedulable()).thenReturn(true);
		underTest.processBatchSchedule(mockSchedule);
		verify(targetDao).save(mockSchedule);
		verify(targetInstanceDao).save(any(TargetInstance.class));
		verify(mockSchedule).setLastProcessedDate(any(Date.class));
	}

	@Test
	public void testProcessBatchScheduleGroupManySip() {
		Schedule mockSchedule = mock(Schedule.class);
		
		Date startDate = new Date(now);
		when(targetDao.getLatestScheduledDate(targetGroup15000, mockSchedule)).thenReturn(startDate);
		when(targetDao.getLatestScheduledDate(target4000, mockSchedule)).thenReturn(startDate);
		Date endDate = new Date(now + 100000L);
		when(mockSchedule.getStartDate()).thenReturn(startDate);
		when(mockSchedule.getNextExecutionDate(any(Date.class))).thenReturn(startDate).thenReturn(null);
		when(mockSchedule.getEndDate()).thenReturn(endDate);
		when(mockSchedule.getOwningUser()).thenReturn(mockUser);
		when(mockSchedule.getTarget()).thenReturn(targetGroup15000);
		
		when(targetGroup15000.getObjectType()).thenReturn(AbstractTarget.TYPE_GROUP);
		when(targetGroup15000.getSipType()).thenReturn(TargetGroup.MANY_SIP);
		when(targetGroup15000.isSchedulable()).thenReturn(true);
		GroupMember mockGroupMember = mock(GroupMember.class);
		when(target4000.isSchedulable()).thenReturn(true);
		when(mockGroupMember.getChild()).thenReturn(target4000);
		when(targetGroup15000.getChildren()).thenReturn(Sets.newHashSet(mockGroupMember));
		underTest.processBatchSchedule(mockSchedule);
		verify(targetDao).save(mockSchedule);
		verify(targetInstanceDao).save(any(TargetInstance.class));
		verify(mockSchedule).setLastProcessedDate(any(Date.class));
	}

	@Test
	public void testProcessScheduleTarget() {
		Schedule mockSchedule = mock(Schedule.class);
		when(mockSchedule.getOwningUser()).thenReturn(mockUser);
		
		Date startDate = new Date(now);
		when(mockSchedule.getStartDate()).thenReturn(startDate);
		when(mockSchedule.getNextExecutionDate(any(Date.class))).thenReturn(startDate).thenReturn(null);
		
		Date endDate = new Date(now + 100000L);
		when(mockSchedule.getEndDate()).thenReturn(endDate);
		
		when(mockSchedule.getTarget()).thenReturn(target4000);
		when(target4000.getObjectType()).thenReturn(AbstractTarget.TYPE_TARGET);
		when(target4000.isSchedulable()).thenReturn(true);
		underTest.processSchedule(mockSchedule);
		verify(targetInstanceDao).save(any(TargetInstance.class));
	}

	@Test
	public void testProcessScheduleGroupOneSip() {
		Schedule mockSchedule = mock(Schedule.class);
		when(mockSchedule.getOwningUser()).thenReturn(mockUser);
		
		Date startDate = new Date(now);
		when(targetDao.getLatestScheduledDate(targetGroup15000, mockSchedule)).thenReturn(startDate);
		when(mockSchedule.getStartDate()).thenReturn(startDate);
		when(mockSchedule.getNextExecutionDate(any(Date.class))).thenReturn(startDate).thenReturn(null);
		
		Date endDate = new Date(now + 100000L);
		when(mockSchedule.getEndDate()).thenReturn(endDate);
		
		when(mockSchedule.getTarget()).thenReturn(targetGroup15000);
		when(targetGroup15000.getObjectType()).thenReturn(AbstractTarget.TYPE_GROUP);
		when(targetGroup15000.getSipType()).thenReturn(TargetGroup.ONE_SIP);
		when(targetGroup15000.isSchedulable()).thenReturn(true);
		underTest.processSchedule(mockSchedule);
		verify(targetInstanceDao).save(any(TargetInstance.class));
	}

	@Test
	public void testProcessScheduleGroupManySip() {
		Schedule mockSchedule = mock(Schedule.class);
		
		Date startDate = new Date(now);
		when(targetDao.getLatestScheduledDate(targetGroup15000, mockSchedule)).thenReturn(startDate);
		when(targetDao.getLatestScheduledDate(target4000, mockSchedule)).thenReturn(startDate);
		Date endDate = new Date(now + 100000L);
		when(mockSchedule.getStartDate()).thenReturn(startDate);
		when(mockSchedule.getNextExecutionDate(any(Date.class))).thenReturn(startDate).thenReturn(null);
		when(mockSchedule.getEndDate()).thenReturn(endDate);
		when(mockSchedule.getOwningUser()).thenReturn(mockUser);
		when(mockSchedule.getTarget()).thenReturn(targetGroup15000);
		
		when(targetGroup15000.getObjectType()).thenReturn(AbstractTarget.TYPE_GROUP);
		when(targetGroup15000.getSipType()).thenReturn(TargetGroup.MANY_SIP);
		when(targetGroup15000.isSchedulable()).thenReturn(true);
		GroupMember mockGroupMember = mock(GroupMember.class);
		when(target4000.isSchedulable()).thenReturn(true);
		when(mockGroupMember.getChild()).thenReturn(target4000);
		when(targetGroup15000.getChildren()).thenReturn(Sets.newHashSet(mockGroupMember));
		underTest.processSchedule(mockSchedule);
		verify(targetInstanceDao).save(any(TargetInstance.class));
	}

	@Test
	public final void testSaveTargetSendsNoNotifications() {
		Set<AbstractTargetDTO> changeSet = Sets.newHashSet();
		when(targetDao.getAncestorDTOs(anyLong())).thenReturn(changeSet);
		when(target4000.isNew()).thenReturn(true);
		underTest.setSendGroupUpdateNotifications(true);
		when(target4000.getState()).thenReturn(1);
		when(target4000.getOriginalState()).thenReturn(1);
		underTest.save(target4000);
		verify(targetDao).save(target4000, null);
		verifyNoMoreInteractions(intrayManager);
	}

	@Test
	public final void testSaveTargetSendNotificationsAddedParents() {
		AbstractTargetDTO mockAbstractTargetDTO = mock(AbstractTargetDTO.class);
		when(mockAbstractTargetDTO.getName()).thenReturn(new Random().nextInt() + "");
		Set<AbstractTargetDTO> changeSet = Sets.newHashSet(mockAbstractTargetDTO);
		when(targetDao.getAncestorDTOs(anyLong())).thenReturn(changeSet);
		when(target4000.isNew()).thenReturn(true);
		underTest.setSendGroupUpdateNotifications(true);
		when(target4000.getState()).thenReturn(1);
		when(target4000.getOriginalState()).thenReturn(1);
		underTest.save(target4000);
		verify(targetDao).save(target4000, null);
		verify(intrayManager).generateNotification(
				anyLong(), 
				eq(MessageType.CATEGORY_MISC), 
				eq("Target Group membership updated"),
				anyString());
	}

	@Test
	public final void testSaveTargetSendNotificationsRemovedParents() {
		AbstractTargetDTO mockAbstractTargetDTO = mock(AbstractTargetDTO.class);
		when(mockAbstractTargetDTO.getName()).thenReturn(new Random().nextInt() + "");
		Set<AbstractTargetDTO> changeSet = Sets.newHashSet(mockAbstractTargetDTO);
		Set<AbstractTargetDTO> emptyChangeSet = Sets.newHashSet();
		when(targetDao.getAncestorDTOs(anyLong())).thenReturn(changeSet).thenReturn(emptyChangeSet);
		when(target4000.isNew()).thenReturn(false);
		underTest.setSendGroupUpdateNotifications(true);
		when(target4000.getState()).thenReturn(1);
		when(target4000.getOriginalState()).thenReturn(1);
		
		underTest.save(target4000);
		verify(targetDao, times(2)).getAncestorDTOs(anyLong());
		verify(targetDao).save(target4000, null);
		verify(intrayManager).generateNotification(
				anyLong(), 
				eq(MessageType.CATEGORY_MISC), 
				eq("Target Group membership updated"),
				anyString());
	}
}
