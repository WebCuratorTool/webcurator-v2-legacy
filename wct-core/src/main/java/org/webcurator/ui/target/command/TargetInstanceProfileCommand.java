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
package org.webcurator.ui.target.command;

/**
 * Profile Command for adjusting the Target Instance profile.
 * @author beaumontb
 *
 */
public class TargetInstanceProfileCommand extends ProfileCommand {
	private boolean overrideTarget = false;

	/**
	 * @return the overrideTarget
	 */
	public boolean isOverrideTarget() {
		return overrideTarget;
	}

	/**
	 * @param overrideTarget the overrideTarget to set
	 */
	public void setOverrideTarget(boolean overrideTarget) {
		this.overrideTarget = overrideTarget;
	}
	
	

}
