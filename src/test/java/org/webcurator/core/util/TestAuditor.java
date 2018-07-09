package org.webcurator.core.util;

import org.webcurator.domain.model.auth.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class TestAuditor implements Auditor 
{
	private static Log log = LogFactory.getLog(TestAuditor.class);

	public void audit(String subjectType, Long subjectOid, String action,
			String message) 
	{
		log.debug("Audit: "+subjectType+";"+subjectOid+";"+action+";"+message);
	}

	public void audit(String subjectType, String action, String message) 
	{
		log.debug("Audit: "+subjectType+";"+action+";"+message);
	}

	public void audit(User user, String subjectType, Long subjectOid, String action, String message) 
	{
		log.debug("Audit: "+user.getUsername()+";"+subjectType+";"+subjectOid+";"+action+";"+message);
	}

}
