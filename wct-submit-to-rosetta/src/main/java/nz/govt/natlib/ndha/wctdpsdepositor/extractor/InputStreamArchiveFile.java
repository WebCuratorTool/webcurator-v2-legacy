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

import java.io.*;

/**
 * This class represents a file referenced in a Wct Met's document structure map.
 * Where the file is specified as a @{link InputStream}.
 */
public class InputStreamArchiveFile extends ArchiveFile {
    private final byte[] streamData;

    public InputStreamArchiveFile(String mimeType, String location, InputStream stream) {
        super.mimeType = mimeType;
        super.fileName = location;
        // copy stream so as to allow multiple reads of stream.
        streamData = copyStreamToByteArray(stream);
        closeStream(stream);
    }


    public InputStream toStream() throws IOException {
        return new ByteArrayInputStream(streamData.clone());
    }

    public File copyStreamToDirectory(String directory) throws IOException {
        File tempFile = new File(directory + "/" + fileName);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(tempFile);
            fos.write(streamData.clone());
        }
        finally {
            fos.close();
        }
        return tempFile;
    }

    private byte[] copyStreamToByteArray(InputStream stream) {
        try {
            byte[] buff = new byte[stream.available()];
            int index = 0;
            int value;
            while ((value = stream.read()) != -1) {
                buff[index] = (byte) value;
                index++;
            }
            return buff;
        }
        catch (IOException ioe) {
            throw new RuntimeException("An exception occurred while copying the stream to a byte array.");
        }
    }

    private void closeStream(InputStream stream) {
        try {
            stream.close();
        } catch (IOException ioe) {
            throw new RuntimeException("An exception occurred while closing the stream.", ioe);
        }
    }

}
