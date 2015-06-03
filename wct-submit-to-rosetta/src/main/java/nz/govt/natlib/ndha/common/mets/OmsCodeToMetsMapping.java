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

import java.util.HashMap;
import java.util.Map;

import nz.govt.natlib.ndha.common.dublincore.DCFormatElement;
import nz.govt.natlib.ndha.common.dublincore.DCTypeElement;

/**
 * This class maps different OMS Object codes to the codes used in METS DNX and DC.
 * This is kept under Common package because several other projects (such as 
 * OMS Extractor and WCT DPS Plugin) will need to use this.
 * 
 * @author pushpar
 *
 */
public class OmsCodeToMetsMapping {
	// OMS Type Codes
	public static final String OT_IMG = "OT_IMG";
	public static final String OT_MON = "OT_MON";
	public static final String OT_SER = "OT_SER";
	public static final String OT_SND = "OT_SND";
	public static final String OT_TXT = "OT_TXT";
	public static final String OT_VID = "OT_VID";
	public static final String OT_WWW = "OT_WWW";
	public static final String OT_NULL = "null";

	// IE Entity Types
	private static final String PeriodicIE = "PeriodicIE";
	private static final String OneOffIE = "OneOffIE";
	public static final String UnpublishedIE = "UnpublishedIE"; // this was made public purposefully - OMS Extractor would use this.
	private static final String DigitisedImageIE = "DigitisedImageIE";
	private static final String DigitisedSoundIE = "DigitisedSoundIE";
	private static final String WebHarvestIE = "WebHarvestIE";
	private static final String DefaultIE = "DefaultIE";
	
	// DPS Access Codes
//	private static final int DNX_OPEN_ACCESS = 100;
	private static final int DNX_PUBLISHED_RESTRICTED = 200;		//Restricted to 3 people
	private static final int DNX_UNPUBLISHED_RESTRICTED_BY_LOCATION = 300;
	private static final int DNX_UNPUBLISHED_RESTRICTED_BY_PERSON = 400;
	private static int DNX_OPEN_ACCESS;

	// OMS Access Codes
	private static final String ACR_OPA = "ACR_OPA";
	private static final String ACR_OSR = "ACR_OSR";
	private static final String ACR_ONS = "ACR_ONS";
	private static final String ACR_RES = "ACR_RES";
	
	private static final Map<String, ObjectTypeCodeMapping> omsObjectTypeCodeMap;
	private static final Map<String, Integer> omsObjectAccessRestrictionMap;

	static {
		omsObjectTypeCodeMap =  new HashMap<String, ObjectTypeCodeMapping>();
		omsObjectTypeCodeMap.put(OT_IMG, new ObjectTypeCodeMapping(DCTypeElement.Image, DCFormatElement.Image, DigitisedImageIE));
		omsObjectTypeCodeMap.put(OT_MON, new ObjectTypeCodeMapping(DCTypeElement.Image, DCFormatElement.Image, OneOffIE));
		omsObjectTypeCodeMap.put(OT_SER, new ObjectTypeCodeMapping(DCTypeElement.Text, DCFormatElement.Text, PeriodicIE));
		omsObjectTypeCodeMap.put(OT_SND, new ObjectTypeCodeMapping(DCTypeElement.Sound, DCFormatElement.Audio, DigitisedSoundIE));
		omsObjectTypeCodeMap.put(OT_TXT, new ObjectTypeCodeMapping(DCTypeElement.Text, DCFormatElement.Text, DefaultIE));
		omsObjectTypeCodeMap.put(OT_VID, new ObjectTypeCodeMapping(DCTypeElement.MovingImage, DCFormatElement.Video, OneOffIE));
		omsObjectTypeCodeMap.put(OT_WWW, new ObjectTypeCodeMapping(DCTypeElement.InteractiveResource, DCFormatElement.Text, WebHarvestIE));
		omsObjectTypeCodeMap.put(OT_NULL, new ObjectTypeCodeMapping(null, null, DefaultIE));

		omsObjectAccessRestrictionMap = new HashMap<String, Integer>();
		omsObjectAccessRestrictionMap.put(ACR_OPA, DNX_OPEN_ACCESS);
		omsObjectAccessRestrictionMap.put(ACR_OSR, DNX_PUBLISHED_RESTRICTED);
		omsObjectAccessRestrictionMap.put(ACR_ONS, DNX_UNPUBLISHED_RESTRICTED_BY_LOCATION);
		omsObjectAccessRestrictionMap.put(ACR_RES, DNX_UNPUBLISHED_RESTRICTED_BY_PERSON);
	}

	public static class ObjectTypeCodeMapping {
		public final DCTypeElement type;
		public final DCFormatElement format;
		public final String ieEntityType;
		private ObjectTypeCodeMapping(final DCTypeElement type, final DCFormatElement format, final String ieEntityType) {
			this.type = type;
			this.format = format;
			this.ieEntityType = ieEntityType;
		}
	}

	/**
	 * Maps the OMS Object Type Code into a DC/DNX Entity Type
	 * @param omsTypeCode
	 * @return
	 */
	public static ObjectTypeCodeMapping getObjectTypeCodeMapping(String omsTypeCode) {
		if (omsTypeCode == null || (omsObjectTypeCodeMap.containsKey(omsTypeCode) == false)) return omsObjectTypeCodeMap.get(OT_NULL);
		return omsObjectTypeCodeMap.get(omsTypeCode);
	}

	/**
	 * Maps the OMS Access Restriction Code into a DC Rights / DNX Access Restriction Policy ID
	 * @param omsAccessCodeId
	 * @return
	 */
	public static String getMappedOmsAccessCode(String omsAccessCodeId) {
		if (omsObjectAccessRestrictionMap.containsKey(omsAccessCodeId) == false) return null;
		return String.valueOf(omsObjectAccessRestrictionMap.get(omsAccessCodeId));
	}
	
	public static void setDNX_OPEN_ACCESS(String omsCode) {
		try{
			DNX_OPEN_ACCESS = Integer.parseInt(omsCode);
		}
		catch(NumberFormatException ex){
			throw new RuntimeException("Could not parse DNX OPEN ACCESS code.");
		}
        
    }

}
