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

package nz.govt.natlib.ndha.wctdpsdepositor.dpsdeposit.dpsresult;

import com.exlibris.digitool.deposit.service.xmlbeans.DepositResultDocument;
import nz.govt.natlib.ndha.wctdpsdepositor.dpsdeposit.dspresult.DepositResultConverter;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import org.junit.Test;
import org.webcurator.core.archive.dps.DpsDepositFacade.DepositResult;

import java.util.Date;

public class DepositResultConverterTest {
//    public static final String SUCCESSFUL_MESSAGE =
//            "<xb:deposit_result xmlns:xb=\"http://com/exlibris/digitool/deposit/service/xmlbeans\">\n" +
//                    "  <xb:isError>false</xb:isError>\n" +
//                    "  <xb:sipId>345</xb:sipId>\n" +
//                    "  <xb:depositActivityId>365</xb:depositActivityId>\n" +
//                    "  <xb:userParams>pdsHandle=2122008101421347212008214210, materialFlowId=5, subDirectoryName=deposit - 2 - mets, producerId=1, depositSetId=1</xb:userParams>\n" +
//                    "  <xb:creationDate>Thu Feb 21 10:14:01 IST 2008</xb:creationDate>\n" +
//                    "</xb:deposit_result>";
//
//    public static final String FAILED_MESSAGE =
//            "<xb:deposit_result xmlns:xb=\"http://com/exlibris/digitool/deposit/service/xmlbeans\">\n" +
//                    "  <xb:isError>true</xb:isError>\n" +
//                    "  <xb:messageCode xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/>\n" +
//                    "  <xb:messageDesc>MaterialFlow with id 6 does not exist in DB.</xb:messageDesc>\n" +
//                    "  <xb:userParams>pdsHandle=2122008101431349212008214310, materialFlowId=6, subDirectoryName=deposit - 4 - dc - complex, producerId=1, depositSetId=1</xb:userParams>\n" +
//                    "  <xb:creationDate xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/>\n" +
//                    "</xb:deposit_result>";


    @Test
    public void test_unmarshaling_successful_message_from_xml() {
        DepositResultConverter converter = new DepositResultConverter();
        DepositResult resultAdapter = converter.unmarshalFrom(buildMessage(false));

        assertThat(resultAdapter.isError(), is(false));
        assertThat(resultAdapter.getSipId(), is(equalTo(345L)));
        assertThat(resultAdapter.getDepositActivityId(), is(equalTo(365L)));
    }

    @Test
    public void test_unmarshaling_failed_message_from_xml() {
        DepositResultConverter converter = new DepositResultConverter();
        DepositResult resultAdapter = converter.unmarshalFrom(buildMessage(true));

        assertThat(resultAdapter.isError(), is(true));
        assertThat(resultAdapter.getSipId(), is(equalTo(0L)));
        assertThat(resultAdapter.getMessageDesciption(), is(equalTo("MaterialFlow with id 6 does not exist in DB.")));
    }


    public static String buildMessage(boolean isError) {
        DepositResultDocument depositReply = DepositResultDocument.Factory.newInstance();
        DepositResultDocument.DepositResult result = depositReply.addNewDepositResult();

        if (!isError) {

            result.setSipId(345);
            result.setDepositActivityId(365);
        } else {
            result.setSipId(0);
            result.setDepositActivityId(0);
        }

        result.setIsError(isError);
        result.setMessageCode("100");
        result.setMessageDesc("MaterialFlow with id 6 does not exist in DB.");

        result.setUserParams("pdsHandle=" + "123456abc" + ", materialFlowId=" + "materialFlowId" + ", subDirectoryName=" + "fileName" + ", producerId=" + "producerId" + ", depositSetId=" + "345");
        Date current = new Date();
        result.setCreationDate(current.toString());
        return depositReply.toString();
    }


}
