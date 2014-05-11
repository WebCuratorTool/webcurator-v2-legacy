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

package nz.govt.natlib.ndha.wctdpsdepositor;

import nz.govt.natlib.ndha.wctdpsdepositor.dpsdeposit.DepositWebServicesFactory;
import nz.govt.natlib.ndha.wctdpsdepositor.dpsdeposit.dpsresult.DepositResultConverterTest;
import nz.govt.natlib.ndha.wctdpsdepositor.filemover.FileMover;
import nz.govt.natlib.ndha.wctdpsdepositor.mets.DnxMapper;
import nz.govt.natlib.ndha.wctdpsdepositor.mets.DnxMapperImpl;
import nz.govt.natlib.ndha.wctdpsdepositor.mets.MetsDocument;
import nz.govt.natlib.ndha.wctdpsdepositor.mets.MetsWriterFactoryImpl;
import nz.govt.natlib.ndha.wctdpsdepositor.pds.PdsClientFactory;
import nz.govt.natlib.ndha.wctdpsdepositor.preprocessor.ArcIndexProcessor;
import nz.govt.natlib.ndha.wctdpsdepositor.preprocessor.PreDepositProcessor;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;
import org.webcurator.core.archive.dps.DpsDepositFacade;
import org.webcurator.core.archive.dps.DpsDepositFacade.DepositResult;

import com.exlibris.dps.sdk.deposit.DepositWebServices;
import com.exlibris.dps.sdk.pds.PdsClient;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DpsDepositFacadeImplTest {
    private static final String wctFilesDirectory = "src/test/resources/WctFiles";
    private static final String wctMetsPath = "src/test/resources/METS-11141123.xml";

    @SuppressWarnings("unchecked")
    @Test
    public void test_submission_of_wct_sip() throws Exception {

        FileInputStream fis = new FileInputStream(wctMetsPath);
        int fileSize = fis.available();
        byte[] binaryData = new byte[fileSize];
        fis.read(binaryData);
        String wctSip = new String(binaryData);

        Mockery mockContext = constructMockContext();
        final DepositWebServicesFactory depositWebServicesFactory = mockContext.mock(DepositWebServicesFactory.class);
        final DepositWebServices depositWebServices = mockContext.mock(DepositWebServices.class);
        final PdsClientFactory pdsClientFactory = mockContext.mock(PdsClientFactory.class);
        final PdsClient pdsClient = mockContext.mock(PdsClient.class);
        final DnxMapper dnxMapper = new DnxMapperImpl(new MetsWriterFactoryImpl());
        final FileMover fileMover = mockContext.mock(FileMover.class);
        final PreDepositProcessor preDepositProcessor = new ArcIndexProcessor();
        final DpsDepositFacade dpsDeposit = new DpsDepositFacadeImpl(depositWebServicesFactory, pdsClientFactory, fileMover, dnxMapper, preDepositProcessor);
        final String pdsSessionId = "pdsSessionId";

        List<File> fileList = extractFileDetailsFrom();
        final Map<String, String> parameters = populateDepositParameter(wctSip);

        mockContext.checking(new Expectations() {
            {
                one(depositWebServicesFactory).createInstance(with(any(WctDepositParameter.class)));
                will(returnValue(depositWebServices));

                one(depositWebServices).submitDepositActivity(with(any(String.class)), with(any(String.class)), with(any(String.class)), with(any(String.class)), with(any(String.class)));
                will(returnValue(DepositResultConverterTest.buildMessage(false)));

                one(fileMover).move(with(any(MetsDocument.class)), with(any(List.class)), with(any(WctDepositParameter.class)));

                allowing(pdsClientFactory).createInstance();
                will(returnValue(pdsClient));

                allowing(pdsClient).init(with(any(String.class)), with(any(boolean.class)));

                allowing(pdsClient).login(with(any(String.class)), with(any(String.class)), with(any(String.class)));
                will(returnValue(pdsSessionId));
            }
        });
        DepositResult depositResult = dpsDeposit.deposit(parameters, fileList);
        if (depositResult.isError())
            throw new RuntimeException("Submission to DPS failed, message from DPS: " + depositResult.getMessageDesciption());
        mockContext.assertIsSatisfied();
        assertThat(depositResult.getSipId(), is(notNullValue()));
    }

    private List<File> extractFileDetailsFrom() {
        List<File> files = new ArrayList<File>();

        File resourcesDirectory = new File(wctFilesDirectory);
        File[] resourceFiles = resourcesDirectory.listFiles();

        for (File resourceFile : resourceFiles)
            if (!resourceFile.isHidden() && resourceFile.isFile())
                files.add(resourceFile);
        return files;
    }

    private Map<String, String> populateDepositParameter(String wctMetsDocument) {
        Map<String, String> parameterMap = new HashMap<String, String>();
        parameterMap.put(DpsDepositFacade.DPS_INSTITUTION, "NLNZ");
        parameterMap.put(DpsDepositFacade.DPS_USER_NAME, "dpsusername");
        parameterMap.put(DpsDepositFacade.DPS_PASSWORD, "dpspassword");
        parameterMap.put(DpsDepositFacade.FTP_HOST, "theHost.natlib.govt.nz");
        parameterMap.put(DpsDepositFacade.FTP_PASSWORD, "ftppassword");
        parameterMap.put(DpsDepositFacade.FTP_USER_NAME, "ftpusername");
        parameterMap.put(DpsDepositFacade.FTP_DIRECTORY, "/some/where/inside/the/root");
        parameterMap.put(DpsDepositFacade.MATERIAL_FLOW_ID, "5");
        parameterMap.put(DpsDepositFacade.PDS_URL, "http://theHost.natlib.govt.nz:88889/pds");
        parameterMap.put(DpsDepositFacade.PRODUCER_ID, "10");
        parameterMap.put(DpsDepositFacade.DPS_WSDL_URL, "http://theHost.natlib.govt.nz:88888/dpsdepositservicefake?wsdl");
        parameterMap.put(DpsDepositFacade.ILS_REFERENCE, "1234567890");
        parameterMap.put(DpsDepositFacade.ACCESS_RESTRICTION, "ACR_OPA");
        parameterMap.put(DpsDepositFacade.WCT_METS_XML_DOCUMENT, wctMetsDocument);
        parameterMap.put(DpsDepositFacade.TARGET_INSTANCE_ID, "111122223333");
        return parameterMap;
    }

    private Mockery constructMockContext() {
        Mockery mockContext = new Mockery() {
            {
                setImposteriser(ClassImposteriser.INSTANCE);
            }
        };
        return mockContext;
    }
}
