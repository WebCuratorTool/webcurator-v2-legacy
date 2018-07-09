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
package org.webcurator.core.archive;

import java.util.Map;

import org.webcurator.domain.model.core.TargetInstance;

/**
 * The archive adapter provides the mechanisim for archiving a harvested target instance 
 * into an archive repository. 
 * @author aparker
 */
public interface ArchiveAdapter {
	/**
	 * Store the harvested target instance in the archive.
	 * @param instance the target instance to archive
	 * @param sipXML the generated meta data for the harvest
	 * @param customDepositFormElements name/value of custom deposit form parameters, if any.
	 * @param harvestNumber the number of the harvest being archived
	 * @throws Exception thrown if there is an error
	 */
	public void submitToArchive(TargetInstance instance, String sipXML, Map customDepositFormElements, int harvestNumber)throws Exception;
}