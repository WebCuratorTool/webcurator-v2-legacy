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

package nz.govt.natlib.ndha.wctdpsdepositor.filemover;

import nz.govt.natlib.ndha.wctdpsdepositor.WctDepositParameter;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;


public class FtpFileMoverTest {

    @Test
    public void test_connect() throws IOException {
        Mockery mockContext = constructMockContext();

        final FTPClient mockedFtpClient = mockContext.mock(FTPClient.class);
        final FtpClientFactory mockedFactory = mockContext.mock(FtpClientFactory.class);

        mockContext.checking(new Expectations() {
            {
                one(mockedFactory).createInstance();
                will(returnValue(mockedFtpClient));

                one(mockedFtpClient).connect(with(any(String.class)));
                one(mockedFtpClient).user(with(any(String.class)));
                will(returnValue(1));

                one(mockedFtpClient).pass(with(any(String.class)));
                one(mockedFtpClient).setFileType(FTP.BINARY_FILE_TYPE);
            }
        });

        WctDepositParameter depositParameter = new WctDepositParameter();
        FtpFileMover ftpFileMover = new FtpFileMover(mockedFactory);
        ftpFileMover.connect(depositParameter);

        mockContext.assertIsSatisfied();
    }

    @Test
    public void test_calling_create_And_Change_To_Directory() throws IOException {
        Mockery mockContext = constructMockContext();

        final FTPClient mockedFtpClient = mockContext.mock(FTPClient.class);
        final FtpClientFactory mockedFactory = mockContext.mock(FtpClientFactory.class);
        final String directoryName = "directoryName";

        mockContext.checking(new Expectations() {
            {
                one(mockedFactory).createInstance();
                will(returnValue(mockedFtpClient));

                one(mockedFtpClient).connect(with(any(String.class)));
                one(mockedFtpClient).user(with(any(String.class)));
                will(returnValue(1));

                one(mockedFtpClient).pass(with(any(String.class)));
                one(mockedFtpClient).setFileType(FTP.BINARY_FILE_TYPE);

                one(mockedFtpClient).makeDirectory(directoryName);
                will(returnValue(true));

                one(mockedFtpClient).changeWorkingDirectory(directoryName);
            }
        });

        WctDepositParameter depositParameter = new WctDepositParameter();

        FtpFileMover ftpFileMover = new FtpFileMover(mockedFactory);
        ftpFileMover.connect(depositParameter);

        ftpFileMover.createAndChangeToDirectory(directoryName);

        mockContext.assertIsSatisfied();
    }

    @Test(expected = RuntimeException.class)
    public void test_calling_create_And_Change_To_Directory_when_not_connected() throws IOException {
        Mockery mockContext = constructMockContext();

        final FTPClient mockedFtpClient = mockContext.mock(FTPClient.class);
        final FtpClientFactory mockedFactory = mockContext.mock(FtpClientFactory.class);

        mockContext.checking(new Expectations() {
            {
                one(mockedFactory).createInstance();
                will(returnValue(mockedFtpClient));
            }
        });

        FtpFileMover ftpFileMover = new FtpFileMover(mockedFactory);
        ftpFileMover.createAndChangeToDirectory("directory");

        mockContext.assertIsSatisfied();
    }


    @Test
    public void test_store_file() throws IOException {
        Mockery mockContext = constructMockContext();

        final FTPClient mockedFtpClient = mockContext.mock(FTPClient.class);
        final FtpClientFactory mockedFactory = mockContext.mock(FtpClientFactory.class);
        final String fileName = "directoryName";
        final InputStream stream = new ByteArrayInputStream(fileName.getBytes());

        final WctDepositParameter depositParameter = new WctDepositParameter();

        mockContext.checking(new Expectations() {
            {
                one(mockedFactory).createInstance();
                will(returnValue(mockedFtpClient));

                one(mockedFtpClient).connect(with(any(String.class)));
                one(mockedFtpClient).user(with(any(String.class)));
                will(returnValue(1));

                one(mockedFtpClient).pass(with(any(String.class)));
                one(mockedFtpClient).setFileType(FTP.BINARY_FILE_TYPE);

                one(mockedFtpClient).storeFile(fileName, stream);
                will(returnValue(true));

            }
        });

        FtpFileMover ftpFileMover = new FtpFileMover(mockedFactory);
        ftpFileMover.connect(depositParameter);

        ftpFileMover.storeFile(fileName, stream);

        mockContext.assertIsSatisfied();
    }

    @Test
    public void test_close_disconnects_from_server() throws IOException {
        Mockery mockContext = constructMockContext();

        final FTPClient mockedFtpClient = mockContext.mock(FTPClient.class);
        final FtpClientFactory mockedFactory = mockContext.mock(FtpClientFactory.class);
        final WctDepositParameter depositParameter = new WctDepositParameter();

        mockContext.checking(new Expectations() {
            {
                one(mockedFactory).createInstance();
                will(returnValue(mockedFtpClient));

                one(mockedFtpClient).connect(with(any(String.class)));
                one(mockedFtpClient).user(with(any(String.class)));
                will(returnValue(1));

                one(mockedFtpClient).pass(with(any(String.class)));
                one(mockedFtpClient).setFileType(FTP.BINARY_FILE_TYPE);

                one(mockedFtpClient).disconnect();
            }
        });

        FtpFileMover ftpFileMover = new FtpFileMover(mockedFactory);
        ftpFileMover.connect(depositParameter);

        ftpFileMover.close();

        mockContext.assertIsSatisfied();
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
