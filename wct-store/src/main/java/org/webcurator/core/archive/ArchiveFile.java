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
package org.webcurator.core.archive;

import java.io.File;
/**
 * A class encapsulating file archive details 
 * @author AParker
 */
public class ArchiveFile {
	/** 
	 * The physical file itself
	 */
	private File file;
	/**
	 * The type of file
	 * Possible values are: 
	 * org.webcurator.core.archive.Constants.LOG_FILE
	 * org.webcurator.core.archive.Constants.REPORT_FILE
	 * org.webcurator.core.archive.Constants.ARC_FILE
	 * org.webcurator.core.archive.Constants.ROOT_FILE
	 * @see org.webcurator.core.archive.Constants
	 */
	private int type;
	/**
	 * The MD5 of the file
	 */
	private String md5;
	public ArchiveFile(File f, int fileType) {
		this.file = f;
		this.type = fileType;
	}
	public File getFile() {
		return file;
	}
	public void setFile(File file) {
		this.file = file;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getMd5() {
		return md5;
	}
	public void setMd5(String md5) {
		this.md5 = md5;
	}
}
