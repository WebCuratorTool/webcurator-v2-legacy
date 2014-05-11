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
package org.webcurator.ui.archive;

/**
 * The Archive Harvest command.
 * @author aparker
 */
public class ArchiveCommand {
	/** The id of the target instance to archive. */
	private int targetInstanceID;
	/** the harvest result number to archive. */
	private int harvestResultNumber;
	/**
	 * @return the harvestResultNumber
	 */
	public int getHarvestResultNumber() {
		return harvestResultNumber;
	}
	/**
	 * @param harvestResultNumber the harvestResultNumber to set
	 */
	public void setHarvestResultNumber(int harvestResultNumber) {
		this.harvestResultNumber = harvestResultNumber;
	}
	/**
	 * @return the targetInstanceID
	 */
	public int getTargetInstanceID() {
		return targetInstanceID;
	}
	/**
	 * @param targetInstanceID the targetInstanceID to set
	 */
	public void setTargetInstanceID(int targetInstanceID) {
		this.targetInstanceID = targetInstanceID;
	}
}
