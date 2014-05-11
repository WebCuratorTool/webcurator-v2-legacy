/*
 *  Copyright 2006 The National Library of New Zealand
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.webcurator.core.archive.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.MessageDigest;

import org.webcurator.core.exceptions.DigitalAssetStoreException;

/**
 * Utility class to help with files
 * @author AParker
 *
 */
public class FileUtil {

	/**
	 * Obtain the MD5 for a file
	 * @param f The file
	 * @return The MD5 hash as a hex string
	 * @throws DigitalAssetStoreException
	 */
	public static String getMD5(File f) throws DigitalAssetStoreException{
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			FileInputStream fis = new FileInputStream(f);
			byte[] buff = new byte[4096];
			int read = fis.read(buff);
			while(read>0){
				md.update(buff,0,read);
				read = fis.read(buff);
			}
			fis.close();
			return toHexString(md.digest());
		} catch (Exception e) {
			throw new DigitalAssetStoreException(e);
		}
}

	/**
	 * Move a file to a new destination optionally obtaining an MD5 checksum on the way.
	 * @param f The file
	 * @param dest The destination directory
	 * @param md5 Calculate and return an MD5?
	 * @return MD5 or null depending on whether an MD5 was requested
	 * @throws DigitalAssetStoreException
	 */
	public static String moveFile(File f,File dest,boolean md5) throws DigitalAssetStoreException{
		String result = null;
		try {
			MessageDigest md = null;
			if(md5){
				md = MessageDigest.getInstance("MD5");
			}
			File fileDest = new File(dest,f.getName());
			if(!dest.exists()){
				dest.mkdirs();
			}
			FileInputStream fis = new FileInputStream(f);
			FileOutputStream fos = new FileOutputStream(fileDest);
			byte[] buff = new byte[4096];
			int read = fis.read(buff);
			while(read>0){
				if(md5){
					md.update(buff,0,read);
				}
				fos.write(buff,0,read);
				read = fis.read(buff);
			}
			fis.close();
			fos.close();
			if(md5){
				result = toHexString(md.digest());
			}
		} catch (Exception e) {
			throw new DigitalAssetStoreException(e);
		}
		return result;
}

	private static String toHexString(byte[] buf){
		String res = "";
		for(int i=0;i<buf.length;i++){
			String s = "0"+Integer.toHexString(buf[i]);
			res += s.substring(s.length()-2);
		}
		return res;
	}
	

}
