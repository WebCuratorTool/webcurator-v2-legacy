/*
 *  Copyright 2006 The National Library of New Zealand
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.webcurator.core.check;

import java.util.ArrayList;
import java.util.List;

/**
 * The CheckProcessor executes all the checks in its list of Checks
 * when the check method is called.
 * This class is used to perform a group of checks for a WCT component.
 * @author nwaight
 */
public class CheckProcessor {
	/** The list of check beans to be run. */
	List<Checker> checks = new ArrayList<Checker>();
	
	/* (non-Javadoc)
	 * @see org.webcurator.core.check.Checker#check()
	 */
	public void check() {
		if (checks.isEmpty()) {
			return;
		}

		for (Checker check : checks) {
			check.check();
		}
	}

	/**
	 * @param checks the checks to set
	 */
	public void setChecks(List<Checker> checks) {
		this.checks = checks;
	}
}
