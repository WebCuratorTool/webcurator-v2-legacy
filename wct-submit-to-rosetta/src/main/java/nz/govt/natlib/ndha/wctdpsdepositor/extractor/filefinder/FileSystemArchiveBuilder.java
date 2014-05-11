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

package nz.govt.natlib.ndha.wctdpsdepositor.extractor.filefinder;

import nz.govt.natlib.ndha.wctdpsdepositor.extractor.FileSystemArchiveFile;

import java.io.File;

/**
 * Concrete instances of this class are used to find files referenced in a Wct Met's document.
 * 
 * This class builds an instance of @{link FileSystemArchiveFile} based on the files in a
 * specified directory.
 */
public class FileSystemArchiveBuilder implements FileArchiveBuilder {
    private final String filesDirectory;

    public FileSystemArchiveBuilder(String filesDirectory) {
        this.filesDirectory = filesDirectory;
    }

    public FileSystemArchiveFile createFileFrom(String mimeType, String expectedCheckSum, String fileName) {
        File file = new File(filesDirectory + "/" + fileName);

        if (!file.exists())
            throw new RuntimeException("The file " + fileName + " was not found in the directory " + filesDirectory);

        return new FileSystemArchiveFile(mimeType, expectedCheckSum, fileName, filesDirectory);
    }

}
