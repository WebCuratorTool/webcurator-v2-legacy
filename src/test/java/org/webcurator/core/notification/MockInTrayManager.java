package org.webcurator.core.notification;

import java.util.Properties;

import org.webcurator.core.util.TestAuditor;
import org.webcurator.domain.MockUserRoleDAO;
import org.webcurator.core.agency.MockAgencyUserManagerImpl;
import org.webcurator.domain.*;
import org.springframework.context.*;

public class MockInTrayManager extends InTrayManagerImpl {

	public MockInTrayManager(String filename) 
	{
		Properties mailConfig = new Properties();
		//mailConfig.load(arg0);
		this.setAgencyUserManager(new MockAgencyUserManagerImpl(filename));
		this.setAudit(new TestAuditor());
		this.setInTrayDAO(new MockInTrayDAO(filename));
		this.setMailServer(new MailServerImpl(mailConfig));
		this.setMessageSource(new MockMessageSource());
		this.setSender("Test@localhost.co.uk");
		this.setUserRoleDAO(new MockUserRoleDAO(filename));
		this.setWctBaseUrl("http://localhost:8080/wct/");
	}

}
