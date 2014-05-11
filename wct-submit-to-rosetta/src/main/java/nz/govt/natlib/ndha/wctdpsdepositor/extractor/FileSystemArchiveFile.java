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

package nz.govt.natlib.ndha.wctdpsdepositor.extractor;

import nz.govt.natlib.ndha.common.FixityUtils;
import nz.govt.natlib.ndha.common.FileUtils;

import java.io.*;

/**
 * This class represents a file referenced in a Wct Met's document structure map.
 * Where the file is specified as a @{link InputStream}.
 */
public class FileSystemArchiveFile extends ArchiveFile {
    protected String filesDirectory;
    protected String originalFixity;

    public FileSystemArchiveFile(String mimeType, String originalFixity, String fileName, String filesDirectory) {
        super.mimeType = mimeType;        
        super.fileName = fileName;
        this.originalFixity = originalFixity;
        this.filesDirectory = filesDirectory;
    }

    /**
     * Constructs a new <code>FileInputStream</code> from the archived file.
     * Note: the client must close the stream.
     *
     * @return a <code>FileInputStream</code>
     * @throws IOException thrown by <code>FileInputStream</code>
     */
    public InputStream toStream() throws IOException {
        File archiveFile = new File(generateFilePath());
        checkMd5Of(archiveFile);

        return new FileInputStream(archiveFile);
    }

    public File copyStreamToDirectory(String directory) throws IOException {
        FileUtils.copyFileToDirectory(filesDirectory + "/" + fileName, directory);
        return new File(directory + "/" + fileName);
    }

    private void checkMd5Of(File archiveFile) {
        String calculatedMd5;
        try {
            calculatedMd5 = FixityUtils.calculateMD5(archiveFile);
        } catch (FileNotFoundException fnfe) {
            throw new RuntimeException("FileNotFoundException thrown by MD5 fixity utility.", fnfe);
        }

        if (!originalFixity.equals(calculatedMd5))
            throw new RuntimeException("The fixity calculated from the file does not match the recorded fixity, file: " + archiveFile.getName() + ", calculated: " + calculatedMd5 + ", original: " + originalFixity);

    }

    public String generateFilePath() {
        String fullPath = filesDirectory + "/" + fileName;

        File file = new File(fullPath);
        if (!file.exists())
            throw new RuntimeException("The file was not found at the fileName: " + fullPath);

        return fullPath;
    }

}
