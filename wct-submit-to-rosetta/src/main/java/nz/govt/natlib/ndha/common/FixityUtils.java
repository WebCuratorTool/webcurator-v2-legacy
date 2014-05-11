/**
 * nz.govt.natlib.ndha.common.Common - Software License
 *
 * Copyright 2007/2008 National Library of New Zealand.
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

package nz.govt.natlib.ndha.common;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * FixityUtils is a static utility class that provides MD5 calculation methods for
 * Files, and input streams.
 *
 * @author rossb
 */
public final class FixityUtils {
    private static final int MD5_HEX_STRING_LENGTH = 32;
    private static final String MD5_ALGORITHM = "MD5";
    private static final char[] hexChars = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    enum BufferSize {
        TINY(1024), VERYSMALL(2048), SMALL(4096), MEDIUM(8192), LARGE(20480), HUGE(51200);

        BufferSize(int bytes) {
            this.size = bytes;
        }

        private int size;

        public int getSize() {
            return size;
        }
    }

    /* Default constructor made private to stop is static utility class being instantiated */
    private FixityUtils() {
    }

    /**
     * Returns the MD5 value of the physical file represented by the File object
     *
     * @param theFile - The File object associated with the physical file to have
     *                its MD5 calculated.
     * @return A String representing the MD5 value of the File.
     * @throws FileNotFoundException if the file cannot be found.
     * @throws RuntimeException      if an internal error occurs in the MD5 calculation.
     */
    public static String calculateMD5(File theFile) throws FileNotFoundException {
        return calculateMD5(theFile, BufferSize.MEDIUM);
    }

    /**
     * Returns the MD5 value of the physical file represented by the File object,
     * using a read buffer of the size specified.
     *
     * @param theFile    the File object associated with the physical file to have
     *                   its MD5 calculated.
     * @param bufferSize a BufferSize enum object identifying the size of the read buffer
     *                   to be used. e.g. <code>FixityUtils.BufferSize.MEDIUM </code>
     * @return A String representing the MD5 value of the File.
     * @throws RuntimeException      if an internal error occurs in the MD5 calculation.
     * @throws FileNotFoundException if the file cannot be found.
     */
    public static String calculateMD5(File theFile, BufferSize bufferSize) throws FileNotFoundException {
        InputStream is = new FileInputStream(theFile);
        return calculateMD5(is, bufferSize);
    }

    /**
     * Returns the MD5 value of the physical file identified in the String object.
     *
     * @param filePath a String identifying the file (with prefix path if required) to have its
     *                 MD5 calculated.
     * @return A String representing the MD5 value of the File.
     * @throws IllegalArgumentException if filePathString is null.
     * @throws FileNotFoundException    if the file cannot be found.
     * @throws RuntimeException         if an internal error occurs in the MD5 calculation.
     */
    public static String calculateMD5(String filePath) throws IllegalArgumentException, FileNotFoundException {
        return calculateMD5(filePath, BufferSize.MEDIUM);
    }

    /**
     * Returns the MD5 value of the physical file identified in the String object,
     * using a read buffer of the size specified.
     *
     * @param filePath   a String identifying the file (with prefix path if required) to have its
     *                   MD5 calculated.
     * @param bufferSize a BufferSize enum object identifying the size of the read buffer
     *                   to be used. e.g. <code>FixityUtils.BufferSize.MEDIUM </code>
     * @return A String representing the MD5 value of the File.
     * @throws IllegalArgumentException if filePathString is null.
     * @throws FileNotFoundException    if the file cannot be found.
     * @throws RuntimeException         if an internal error occurs in the MD5 calculation.
     */
    public static String calculateMD5(String filePath, BufferSize bufferSize) throws IllegalArgumentException, FileNotFoundException {
        if (null == filePath) {
            throw new IllegalArgumentException("filePath String was null.");
        }
        return calculateMD5(new File(filePath), bufferSize);
    }


    /**
     * Returns the MD5 value of the InputStream identified identified by is.
     *
     * @param is the input stream to be used in the MD5 calculation.
     * @return A String representing the MD5 value of the InputStream.
     * @throws RuntimeException if an internal error occurs in the MD5 calculation.
     */
    public static String calculateMD5(InputStream is) {
        return calculateMD5(is, BufferSize.MEDIUM);
    }

    /**
     * Returns the MD5 value of the InputStream identified identified by is.
     *
     * @param is the input stream to be used in the MD5 calculation.
     * @return A String representing the MD5 value of the InputStream.
     * @throws RuntimeException if an internal error occurs in the MD5 calculation.
     */
    public static String calculateMD5(InputStream is, BufferSize bufferSize) {
        //String md5result = null;
        try {
//			MessageDigest digest = MessageDigest.getInstance(FixityUtils.MD5_ALGORITHM);
//			byte[] buffer = new byte[bufferSize.getSize()];
//			int read = 0;
//			while( (read = is.read(buffer)) > 0) {
//			digest.update(buffer, 0, read);
//			}
//			byte[] md5sum = digest.digest();
//			BigInteger bigInt = new BigInteger(1, md5sum);
//			md5result = bigInt.toString(16);

            MessageDigest messageDigest = MessageDigest.getInstance(FixityUtils.MD5_ALGORITHM);
            byte[] buffer = new byte[bufferSize.getSize()];
            int read;
            while ((read = is.read(buffer)) >= 0) {
                messageDigest.update(buffer, 0, read);
            }
            byte[] digest = messageDigest.digest();

            int msb;
            int lsb = 0;
            StringBuffer hexString = new StringBuffer(MD5_HEX_STRING_LENGTH);
            for (int i = 0; i < digest.length; i++) {
                msb = ((int) digest[i] & 0x000000FF) / 16;
                lsb = ((int) digest[i] & 0x000000FF) % 16;
                hexString.append(hexChars[msb]);
                hexString.append(hexChars[lsb]);
            }

            return hexString.toString();
        }
        catch (NoSuchAlgorithmException e) {  //convert to unchecked exception
            throw new RuntimeException("Unable to process InputStream for MD5", e);
        }
        catch (IOException e) {  //convert to unchecked exception
            throw new RuntimeException("Unable to process InputStream for MD5", e);
        }
        finally {
            try {
                is.close();
            }
            catch (IOException e) { //convert to unchecked exception
                throw new RuntimeException("Unable to close input stream for MD5 calculation", e);
            }
        }
        //return md5result;
    }
}
