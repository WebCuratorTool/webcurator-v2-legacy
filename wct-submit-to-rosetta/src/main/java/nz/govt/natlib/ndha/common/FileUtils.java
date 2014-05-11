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
 * FileUtils.java
 * $Rev$
 * PlayerM
 * 26/11/2007
 *
 */

package nz.govt.natlib.ndha.common;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * Some simple file utilities that aren't supplied natively in Java
 * renameFile
 *
 * @author PlayerM
 */
public class FileUtils {
    private final static Log log = LogFactory.getLog(FileUtils.class);

    /**
     * Obtains the file name from the fileIn parameter
     * Copies the file to the destination directory using the same file name
     *
     * @param fileFrom
     * @param destinationDirectory
     * @throws IOException
     */
    public static void copyFileToDirectory(String fileFrom, String destinationDirectory) throws IOException {
        File inFile = new File(fileFrom);
        String fileName = inFile.getName();
        copyFile(fileFrom, destinationDirectory + "/" + fileName);
    }

    /**
     * Copies a file from one place to another
     *
     * @param fileFrom full file name
     * @param fileTo   full file name
     * @throws IOException
     */
    public static void copyFile(String fileFrom, String fileTo) throws IOException {
        File in = new File(fileFrom);
        File out = new File(fileTo);
        copyFile(in, out);
    }


    /**
     * Name is self explanatory
     *
     * @param from
     * @param to
     * @throws IOException
     */
    public static void copyFile(File from, File to) throws IOException {
        FileChannel inChannel = new FileInputStream(from).getChannel();
        FileChannel outChannel = new FileOutputStream(to).getChannel();
        try {
            inChannel.transferTo(0, inChannel.size(), outChannel);
        }
        catch (IOException e) {
            throw e;
        }
        finally {
            if (inChannel != null) inChannel.close();
            if (outChannel != null) outChannel.close();
        }
    }


    /**
     * @param fileFrom The abstract pathname of the file to be renamed
     * @param fileTo   The new abstract pathname for the named file
     * @return <code>true</code> if and only if the renaming succeeded;
     *         <code>false</code> otherwise
     * @throws SecurityException
     * @throws NullPointerException
     */
    public static boolean renameFile(String fileFrom, String fileTo) throws SecurityException, NullPointerException, IOException {
        File in = new File(fileFrom);
        File out = new File(fileTo);
        return renameFile(in, out);
    }


    /**
     * @param from The abstract pathname of the file to be renamed
     * @param to   The new abstract pathname for the named file
     * @return <code>true</code> if and only if the renaming succeeded;
     *         <code>false</code> otherwise
     * @throws SecurityException
     * @throws NullPointerException
     */
    public static boolean renameFile(File from, File to) throws SecurityException, NullPointerException, IOException {
        log.debug("Renaming file " + from.getAbsolutePath() + " to " + to.getAbsolutePath());
        boolean success = from.renameTo(to);
        if (!success) {
            //Try copying & deleting it
            try {
                log.debug("Failed rename, try copy & delete?");
                copyFile(from, to);
                deleteFileOrDirectoryRecursive(from);
                success = true;
            } catch (IOException ex) {
                throw ex;
            }
        }
        log.debug("Succeeded? " + success + ", from exists? " + from.exists() + ", to exists? " + to.exists());
        return success;
    }

    /**
     * Recurse through children deleting all as we go
     *
     * @param dir
     */
    public static void deleteFileOrDirectoryRecursive(String dir) {
        File theDir = new File(dir);
        if (theDir != null) {
            deleteFileOrDirectoryRecursive(theDir);
        }
    }

    /**
     * Recurse through children deleting all as we go
     *
     * @param dir
     */
    public static void deleteFileOrDirectoryRecursive(File dir) {
        if (dir.isDirectory()) {
            for (File file : dir.listFiles()) {
                if (file.isDirectory()) {
                    deleteFileOrDirectoryRecursive(file);
                } else {
                    file.delete();
                }
            }
            dir.delete();
        } else {
            dir.delete();
        }
    }

    /**
     * Recurse through parents making sure they all exist
     *
     * @param dir
     */
    public static void ensureDirectoryExists(String dir) {
        File theDir = new File(dir);
        if (theDir != null) {
            ensureDirectoryExists(theDir);
        }
    }

    /**
     * Recurse through parents making sure they all exist
     *
     * @param dir
     */
    public static void ensureDirectoryExists(File dir) {
        if (!dir.exists()) {
            File parent = dir.getParentFile();
            ensureDirectoryExists(parent);
            dir.mkdir();
        }
    }

    /**
     * Get the file name & suffix from a path
     *
     * @param path
     * @return
     */
    public static String getFileName(String path) {
        return getFileName(new File(path));
    }

    /**
     * Get the file name & suffix from a file
     *
     * @param file
     * @return
     */
    public static String getFileName(File file) {
        return file.getName();
    }

    /**
     * Get just the file name without a suffix from a path
     *
     * @param path
     * @return
     */
    public static String getFileNameNoSuffix(String path) {
        return getFileNameNoSuffix(new File(path));
    }

    /**
     * Get just the file name without a suffix from a file
     *
     * @param file
     * @return
     */
    public static String getFileNameNoSuffix(File file) {
        String fileName = file.getName();
        int dotPos = fileName.lastIndexOf(".");
        if (dotPos >= 0) {
            fileName = fileName.substring(0, dotPos);
        }
        return fileName;
    }

    /**
     * Get just the suffix of a file from a path
     *
     * @param path
     * @return
     */
    public static String getFileSuffix(String path) {
        return getFileSuffix(new File(path));
    }

    /**
     * Get just the suffix of a file from a file
     *
     * @param file
     * @return
     */
    public static String getFileSuffix(File file) {
        String fileName = file.getName();
        int dotPos = fileName.lastIndexOf(".");
        if (dotPos >= 0) {
            fileName = fileName.substring(dotPos + 1);
        }
        return fileName;
    }
    
    
    public static void removeFTPDirectory(FTPClient ftpClient, String directoryName) {
    	try {
        	ftpClient.changeWorkingDirectory(directoryName);
        	for (FTPFile file : ftpClient.listFiles()) {
        		if (file.isDirectory()) {
        			FileUtils.removeFTPDirectory(ftpClient, file.getName());
        		} else {
            	    log.debug("Deleting " + file.getName());
        			ftpClient.deleteFile(file.getName());
        		}
        	}
        	ftpClient.changeWorkingDirectory(directoryName);
        	ftpClient.changeToParentDirectory();
    	    log.debug("Deleting " + directoryName);
        	ftpClient.removeDirectory(directoryName);
    	} catch (Exception ex) {
    		
    	}
    }
    
	
	public static String parseValidFileName(String inputText) {
		String rubbishString = "\\/:*?<>\"|"; 
		String regexp = "[\\" + rubbishString + "]";
		String result = inputText.replaceAll(regexp, "");
		return result;
	}
}
