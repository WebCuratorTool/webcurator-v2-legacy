package org.webcurator.core.targets;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.MockMessageSource;
import org.webcurator.auth.AuthorityManagerImpl;
import org.webcurator.core.common.WCTTreeSet;
import org.webcurator.core.notification.MockInTrayManager;
import org.webcurator.core.scheduler.MockTargetInstanceManager;
import org.webcurator.core.util.TestAuditor;
import org.webcurator.domain.MockAnnotationDAO;
import org.webcurator.domain.MockSiteDAO;
import org.webcurator.domain.MockTargetDAO;
import org.webcurator.domain.MockTargetInstanceDAO;
import org.webcurator.domain.model.core.BusinessObjectFactory;

public class MockTargetManager extends TargetManagerImpl {

	public MockTargetManager(String fileName) {
		this.setTargetDao(new MockTargetDAO(fileName));
		this.setSiteDao(new MockSiteDAO(fileName));
		this.setAnnotationDAO(new MockAnnotationDAO(fileName));
		this.setTargetInstanceDao(new MockTargetInstanceDAO(fileName));
		this.setAuthMgr(new AuthorityManagerImpl());
		this.setInstanceManager(new MockTargetInstanceManager(fileName));
		this.setIntrayManager(new MockInTrayManager(fileName));
		this.setAuditor(new TestAuditor());
		this.setMessageSource(new MockMessageSource());
		this.setBusinessObjectFactory(new BusinessObjectFactory());
		this.setSubGroupTypeName("Sub-Group");
		
		List<String> subGroupParentTypes = new ArrayList<String>();
		subGroupParentTypes.add("Collection");
		subGroupParentTypes.add("Thematic");
		subGroupParentTypes.add("Event");
		
		this.setSubGroupParentTypesList(new WCTTreeSet(subGroupParentTypes, 50));
	}

}
