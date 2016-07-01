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

public class Constants {
    public static final String SEED_URL = "http://www.nzcee.co.nz/";
    public static final String ACCESS_RESTRICTION = "ACR_OPA";
    public static final String ACCESS_RESTRICTION_TRANSLATED = "100";
    public static final String COPYRIGHT_URL = "a CopyrightURL";
    public static final String COPYRIGHT_STATEMENT = "a copyright statement";
    public static final String PROVENANCE_NOTE = "Original Harvest";
    public static final String CREATION_DATE = "2007-08-13";
    public static final String CREATED_BY = "LeeG";
    public static final String ILS_REFERENCE = "1010528";
    public static final String TARGET_NAME = "New Zealand Centre for Ecological Economics ; NZCEE";
    public static final String HARVEST_DATE = "2007-08-13 20:00:24.61";

    public static final String CMS_SECTION = "CMS";
    public static final String CMS_SYSTEM = "ilsdb";

    public static final String FILE_LOCATION = "file://./order.xml";
    public static final String FILE_MIME_TYPE = "text/xml";
    public static final String FILE_FIXITY = "5b8e0ef130911c544e406f99cb5eb90a";

    public static final String ARCHIVE_FILE_LOCATION = "file://./WCT-20070813080023-00003-skynet.arc";
    public static final String ARCHIVE_FILE_MIME_TYPE = "application/octet-stream";
    public static final String ARCHIVE_FILE_FIXITY = "d41d8cd98f00b204e9800998ecf8427e";

    public static final String LOG_FILE_LOCATION = "file://./crawl.log";
    public static final String LOG_FILE_NAME = "crawl.log";
    public static final String LOG_FILE_MIME_TYPE = "text/plain";
    public static final String LOG_FILE_FIXITY = "67ce4ff04a5c833aa5c06c10ad110c49";

    private static final String REPORT_FILE_LOCATION = "file://./seeds.txt";
    private static final String REPORT_FILE_MIME_TYPE = "text/plain";
    private static final String REPORT_FILE_FIXITY = "7059cd6ac6f327f4b55bee810959a8a3";

    private static final String HOME_DIRECTORY_FILE_LOCATION = "file://./order.xml";
    private static final String HOME_DIRECTORY_FILE_MIME_TYPE = "text/xml";
    private static final String HOME_DIRECTORY_FILE_FIXITY = "5b8e0ef130911c544e406f99cb5eb90a";

    public static final String WCT_METS_FILE_LOCATION = "file://./METS-11141123.xml";
    public static final String WCT_METS_FILE_MIME_TYPE = "text/xml";
    public static final String WCT_METS_FILE_FIXITY = "d41d8cd98f00b204e9800998ecf8427e";
}
