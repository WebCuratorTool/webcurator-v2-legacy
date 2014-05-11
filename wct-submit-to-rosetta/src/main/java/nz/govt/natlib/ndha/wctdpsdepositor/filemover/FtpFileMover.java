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

import com.google.inject.Inject;
import nz.govt.natlib.ndha.wctdpsdepositor.WctDepositParameter;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTP;

import java.io.IOException;
import java.io.InputStream;


public class FtpFileMover implements FileMoverStrategy {

    private final FtpClientFactory ftpClientFactory;
    private FTPClient ftpClient = null;

    @Inject
    public FtpFileMover(FtpClientFactory ftpClientFactory) {
        this.ftpClientFactory = ftpClientFactory;
    }

    public void connect(WctDepositParameter depositParameter) {
        try {
            this.ftpClient = ftpClientFactory.createInstance();

            ftpClient.connect(depositParameter.getFtpHost());
            ftpClient.user(depositParameter.getFtpUserName());
            ftpClient.pass(depositParameter.getFtpPassword());
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
        }
        catch (IOException ioe) {
            throw new RuntimeException("Failed to open connection to FTP server: " + depositParameter.getFtpHost(), ioe);
        }

    }

    public void createAndChangeToDirectory(String depositDirectory) throws IOException {
        checkConnectedToServer();

        ftpClient.makeDirectory(depositDirectory);
        changeToDirectory(depositDirectory);
    }

    public void changeToDirectory(String depositDirectory) throws IOException {
        checkConnectedToServer();

        ftpClient.changeWorkingDirectory(depositDirectory);
    }

    public void storeFile(String fileName, InputStream stream) throws IOException {
        checkConnectedToServer();

        ftpClient.storeFile(fileName, stream);
    }

    public void close() {
        if (ftpClient != null)
            try {
                ftpClient.disconnect();
            } catch (IOException ioe) {
                throw new RuntimeException("An exception occurred while uploading files to the FTP server", ioe);
            }
    }

    private void checkConnectedToServer() {
        if (ftpClient == null)
            throw new RuntimeException("Not connected to Ftp server.");
    }

}
