package org.webcurator.domain.model.core;

import static org.junit.Assert.*;

import org.junit.Test;
import org.webcurator.test.*;
import org.webcurator.core.profiles.*;
import org.webcurator.core.util.*;
import org.webcurator.domain.*;
import java.util.*;

public class BusinessObjectFactoryTest extends BaseWCTTest<BusinessObjectFactory>{

	public BusinessObjectFactoryTest()
	{
		super(BusinessObjectFactory.class,
				"src/test/java/org/webcurator/domain/model/core/businessobjectfactorytest.xml");
	}
	
	public void setUp() throws Exception {
		super.setUp();
		testInstance.setProfileManager(new MockProfileManager(testFile));
	}
	
	@Test
	public final void testNewPermission() 
	{
		UserRoleDAO urDao = new MockUserRoleDAO(testFile);

		Site site1 = new Site();
		site1.setOwningAgency(urDao.getAgencyByOid(2000L));
		Permission permission1 = testInstance.newPermission(site1);
		assertTrue(permission1 != null);
		assertSame(permission1.getOwningAgency(), site1.getOwningAgency());
		
		Site site2 = new Site();
		site2.setOwningAgency(urDao.getAgencyByOid(2001L));
		Permission permission2 = testInstance.newPermission(site2);
		assertTrue(permission2 != null);
		assertSame(permission2.getOwningAgency(), site2.getOwningAgency());
		
		assertNotSame(site1.getOwningAgency(), site2.getOwningAgency());
	}
	
	@Test
	public final void testNewSeedHistory() 
	{
		TargetInstanceDAO tidao = new MockTargetInstanceDAO(testFile);
		
		TargetInstance ti = tidao.load(5001L);
		
		Set<Seed> seeds = ti.getTarget().getSeeds();
		assertTrue(seeds.size() > 0);
		Iterator<Seed> it = seeds.iterator();
		while(it.hasNext())
		{
			Seed seed = it.next();
			SeedHistory sh = testInstance.newSeedHistory(ti, seed);
			assertNotNull(sh);
			assertNull(sh.getOid());
			assertNotNull(sh.getIdentity());
			assertEquals(sh.getSeed(), seed.getSeed());
			assertEquals(sh.isPrimary(), seed.isPrimary());
			assertEquals(sh.getTargetInstanceOid(), ti.getOid());
		}
	}
}
