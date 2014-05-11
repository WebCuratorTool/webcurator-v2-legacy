/**
 * nz.govt.natlib.ndha.test - Software License
 *
 * Copyright 2007/2009 National Library of New Zealand.
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0 
 *
 * or the file "LICENSE.txt" included with the software.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 */

package nz.govt.natlib.ndha.test;

import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import junit.framework.TestCase;
import junit.framework.JUnit4TestAdapter;
@Ignore
@RunWith(Suite.class)
@Suite.SuiteClasses({
	nz.govt.natlib.ndha.common.mets.OmsCodeToMetsMappingTest.class,
	nz.govt.natlib.ndha.wctdpsdepositor.dpsdeposit.dpsresult.DepositResultConverterTest.class,
	nz.govt.natlib.ndha.wctdpsdepositor.DpsDepositFacadeImplTest.class,
	nz.govt.natlib.ndha.wctdpsdepositor.extractor.filearchivebuilder.CollectionFileArchiveBuilderTest.class,
	nz.govt.natlib.ndha.wctdpsdepositor.extractor.FileSystemArchiveFileTest.class,
	nz.govt.natlib.ndha.wctdpsdepositor.extractor.InputStreamArchiveFileTest.class,
	nz.govt.natlib.ndha.wctdpsdepositor.extractor.WctDataExtractValidatorTest.class,
	nz.govt.natlib.ndha.wctdpsdepositor.extractor.XpathWctMetsExtractorTest.class,
	nz.govt.natlib.ndha.wctdpsdepositor.filemover.FileMoverTest.class,
	nz.govt.natlib.ndha.wctdpsdepositor.filemover.FtpFileMoverTest.class,
	nz.govt.natlib.ndha.wctdpsdepositor.mets.DnxMapperTest.class,
	nz.govt.natlib.ndha.wctdpsdepositor.mets.DpsMetsWriterTest.class,
	nz.govt.natlib.ndha.wctdpsdepositor.mets.MetsWriterFactoryTest.class,
	nz.govt.natlib.ndha.wctdpsdepositor.preprocessor.ArcIndexProcessorTest.class,
	nz.govt.natlib.ndha.wctdpsdepositor.WctDepositParameterTest.class
})

public class AllTests extends TestCase {
	// the class remains completely empty, 
	// being used only as a holder for the above annotations
	
	// *except* that we need to add the following method to
	// allow ant's <junit> task to call this JUnit 4.x 
	// style test suite in batch mode, until the ant developers
	// get around to solving the compatibility issues!
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(AllTests.class);
	}
}
