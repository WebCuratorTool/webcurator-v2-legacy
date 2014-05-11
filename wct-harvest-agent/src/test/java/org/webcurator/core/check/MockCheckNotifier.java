package org.webcurator.core.check;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MockCheckNotifier implements CheckNotifier {

	private static Log log = LogFactory.getLog(MockCheckNotifier.class);

	public void notification(String subject, int notificationCategory,
			String message) {
		log.debug("Subject:"+subject+" Message:"+message);

	}

}
