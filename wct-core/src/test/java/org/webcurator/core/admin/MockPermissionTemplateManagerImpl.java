package org.webcurator.core.admin;

import org.webcurator.auth.*;
import org.webcurator.domain.*;
import org.webcurator.domain.model.auth.User;
import org.webcurator.domain.model.core.PermissionTemplate;

public class MockPermissionTemplateManagerImpl extends PermissionTemplateManagerImpl {

	public MockPermissionTemplateManagerImpl(String filename) {
		super();
		this.setAuthorityManager(new AuthorityManagerImpl());
		this.setPermissionTemplateDAO(new MockPermissionTemplateDAO(filename));
	}

}
