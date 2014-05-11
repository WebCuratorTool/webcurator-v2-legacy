/**
 * nz.govt.natlib.ndha.common.mets - Software License
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

package nz.govt.natlib.ndha.common.mets;

import static org.junit.Assert.*;

import nz.govt.natlib.ndha.common.dublincore.DCFormatElement;
import nz.govt.natlib.ndha.common.dublincore.DCTypeElement;
import nz.govt.natlib.ndha.common.mets.OmsCodeToMetsMapping.ObjectTypeCodeMapping;

import org.junit.Before;
import org.junit.Test;

public class OmsCodeToMetsMappingTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testGetObjectTypeCodeMapping() {
		test("OT_IMG", DCTypeElement.Image, DCFormatElement.Image, "DigitisedImageIE");
		test("OT_SND", DCTypeElement.Sound, DCFormatElement.Audio, "DigitisedSoundIE");
		test("OT_TXT", DCTypeElement.Text, DCFormatElement.Text, "DefaultIE");
		test("OT_WWW", DCTypeElement.InteractiveResource, DCFormatElement.Text, "WebHarvestIE");
		test("OT_SER", DCTypeElement.Text, DCFormatElement.Text, "PeriodicIE");
		test("OT_MON", DCTypeElement.Image, DCFormatElement.Image, "OneOffIE");
		test("OT_VID", DCTypeElement.MovingImage, DCFormatElement.Video, "OneOffIE");
		test(OmsCodeToMetsMapping.OT_NULL, null, null, "DefaultIE");
		test("Some Junk", null, null, "DefaultIE");
		test(null, null, null, "DefaultIE");
	}

	@Test
	public void testGetMappedOmsAccessCode() {
		assertEquals("100", OmsCodeToMetsMapping.getMappedOmsAccessCode("ACR_OPA"));
		assertEquals("200", OmsCodeToMetsMapping.getMappedOmsAccessCode("ACR_OSR"));
		assertEquals("300", OmsCodeToMetsMapping.getMappedOmsAccessCode("ACR_ONS"));
		assertEquals("400", OmsCodeToMetsMapping.getMappedOmsAccessCode("ACR_RES"));
		assertEquals("400", OmsCodeToMetsMapping.getMappedOmsAccessCode("ACR_RES"));
		assertNull(OmsCodeToMetsMapping.getMappedOmsAccessCode("Some Junk"));
		assertNull(OmsCodeToMetsMapping.getMappedOmsAccessCode(null));
	}

	private void test(String typeCode, DCTypeElement type, DCFormatElement format, String ieEntityType) {
		ObjectTypeCodeMapping mapping = OmsCodeToMetsMapping.getObjectTypeCodeMapping(typeCode);
		assertNotNull(mapping);
		assertEquals(type, mapping.type);
		assertEquals(format, mapping.format);
		assertEquals(ieEntityType, mapping.ieEntityType);
	}
}
