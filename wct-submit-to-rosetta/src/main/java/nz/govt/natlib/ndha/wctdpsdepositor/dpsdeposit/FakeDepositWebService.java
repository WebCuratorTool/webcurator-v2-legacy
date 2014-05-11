/**
 * nz.govt.natlib.ndha.wctdpsdepositor - Software License
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

package nz.govt.natlib.ndha.wctdpsdepositor.dpsdeposit;

import com.exlibris.dps.sdk.deposit.DepositWebServices;
import com.exlibris.digitool.deposit.service.xmlbeans.DepositResultDocument;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Date;


public class FakeDepositWebService implements DepositWebServices {
    private static final Log log = LogFactory.getLog(FakeDepositWebService.class);

    private int sipId;
    private int depositActivityId;


    public String submitDepositActivity(String pdsHandle, String materialFlowId, String fileName, String producerId, String depositSetId) {
        log.info("submit deposit activity received - pdsHandle: " + pdsHandle + ", materialFlowId: " + materialFlowId + ", fileName: " + fileName + ", producerId: " + producerId + ", depositSetId: " + depositSetId);

        int sipId = ++this.sipId;
        int depositActivityId = ++this.depositActivityId;

        String now = (new Date()).toString();
        return buildXmlFragment(pdsHandle, materialFlowId, fileName, producerId, depositSetId, sipId, depositActivityId, now);
    }

    private String buildXmlFragment(String pdsHandle, String materialFlowId, String fileName, String producerId, String depositSetId, int sipId, int depositActivityId, String now) {
		DepositResultDocument depositReply = DepositResultDocument.Factory.newInstance();
		DepositResultDocument.DepositResult result = depositReply.addNewDepositResult();
		if (materialFlowId.equalsIgnoreCase("MakeError")) {
			result.setIsError(true);
			result.setMessageCode("100");
			result.setMessageDesc("Twas a dark and stormy knight");
		} else {
			result.setIsError(false);
			result.setMessageCode("0");
			result.setMessageDesc("Success");
		}
		result.setDepositActivityId(1);
		result.setSipId(1);
		result.setUserParams("pdsHandle=" + pdsHandle + ", materialFlowId=" + materialFlowId + ", subDirectoryName=" + fileName + ", producerId=" + producerId + ", depositSetId=" + depositSetId);
		Date current = new Date();
		result.setCreationDate(current.toString());
		return depositReply.toString();
    }
    
}
