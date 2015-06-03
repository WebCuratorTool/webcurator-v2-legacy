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

import nz.govt.natlib.ndha.wctdpsdepositor.dpsdeposit.DepositWebServicesFactory;
import nz.govt.natlib.ndha.wctdpsdepositor.dpsdeposit.dpsresult.DepositResultConverterTest;
import nz.govt.natlib.ndha.wctdpsdepositor.filemover.FileMover;
import nz.govt.natlib.ndha.wctdpsdepositor.mets.DnxMapper;
import nz.govt.natlib.ndha.wctdpsdepositor.mets.DnxMapperImpl;
import nz.govt.natlib.ndha.wctdpsdepositor.mets.MetsDocument;
import nz.govt.natlib.ndha.wctdpsdepositor.mets.MetsWriterFactoryImpl;
import nz.govt.natlib.ndha.wctdpsdepositor.pds.PdsClientFactory;
import nz.govt.natlib.ndha.wctdpsdepositor.preprocessor.ArcIndexProcessor;
import nz.govt.natlib.ndha.wctdpsdepositor.preprocessor.PreDepositProcessor;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;
import org.webcurator.core.archive.dps.DpsDepositFacade;
import org.webcurator.core.archive.dps.DpsDepositFacade.DepositResult;

import com.exlibris.dps.sdk.deposit.DepositWebServices;
import com.exlibris.dps.sdk.pds.PdsClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DpsDepositFacadeImplTest {
    private static final String wctFilesDirectory = "src/test/resources/WctFiles";
    private static final String wctMetsPath = "src/test/resources/METS-11141123.xml";

    @SuppressWarnings("unchecked")
    @Test
    public void test_submission_of_wct_sip() throws Exception {

        FileInputStream fis = new FileInputStream(wctMetsPath);
        int fileSize = fis.available();
        byte[] binaryData = new byte[fileSize];
        fis.read(binaryData);
        String wctSip = new String(binaryData);

        Mockery mockContext = constructMockContext();
        final DepositWebServicesFactory depositWebServicesFactory = mockContext.mock(DepositWebServicesFactory.class);
        final DepositWebServices depositWebServices = mockContext.mock(DepositWebServices.class);
        final PdsClientFactory pdsClientFactory = mockContext.mock(PdsClientFactory.class);
        final PdsClient pdsClient = mockContext.mock(PdsClient.class);
        final DnxMapper dnxMapper = new DnxMapperImpl(new MetsWriterFactoryImpl());
        final FileMover fileMover = mockContext.mock(FileMover.class);
        final PreDepositProcessor preDepositProcessor = new ArcIndexProcessor();
        final DpsDepositFacade dpsDeposit = new DpsDepositFacadeImpl(depositWebServicesFactory, pdsClientFactory, fileMover, dnxMapper, preDepositProcessor);
        final String pdsSessionId = "pdsSessionId";
        
        populateArcFileContents();

        List<File> fileList = extractFileDetailsFrom();
        final Map<String, String> parameters = populateDepositParameter(wctSip);

        mockContext.checking(new Expectations() {
            {
                one(depositWebServicesFactory).createInstance(with(any(WctDepositParameter.class)));
                will(returnValue(depositWebServices));

                one(depositWebServices).submitDepositActivity(with(any(String.class)), with(any(String.class)), with(any(String.class)), with(any(String.class)), with(any(String.class)));
                will(returnValue(DepositResultConverterTest.buildMessage(false)));

                one(fileMover).move(with(any(MetsDocument.class)), with(any(List.class)), with(any(WctDepositParameter.class)));

                allowing(pdsClientFactory).createInstance();
                will(returnValue(pdsClient));

                allowing(pdsClient).init(with(any(String.class)), with(any(boolean.class)));

                allowing(pdsClient).login(with(any(String.class)), with(any(String.class)), with(any(String.class)));
                will(returnValue(pdsSessionId));
            }
        });
        DepositResult depositResult = dpsDeposit.deposit(parameters, fileList);
        if (depositResult.isError())
            throw new RuntimeException("Submission to DPS failed, message from DPS: " + depositResult.getMessageDesciption());
        mockContext.assertIsSatisfied();
        assertThat(depositResult.getSipId(), is(notNullValue()));
    }

    private List<File> extractFileDetailsFrom() {
        List<File> files = new ArrayList<File>();

        File resourcesDirectory = new File(wctFilesDirectory);
        File[] resourceFiles = resourcesDirectory.listFiles();

        for (File resourceFile : resourceFiles)
            if (!resourceFile.isHidden() && resourceFile.isFile())
                files.add(resourceFile);
        return files;
    }

    private Map<String, String> populateDepositParameter(String wctMetsDocument) {
        Map<String, String> parameterMap = new HashMap<String, String>();
        parameterMap.put(DpsDepositFacade.DPS_INSTITUTION, "NLNZ");
        parameterMap.put(DpsDepositFacade.DPS_USER_NAME, "dpsusername");
        parameterMap.put(DpsDepositFacade.DPS_PASSWORD, "dpspassword");
        parameterMap.put(DpsDepositFacade.FTP_HOST, "theHost.natlib.govt.nz");
        parameterMap.put(DpsDepositFacade.FTP_PASSWORD, "ftppassword");
        parameterMap.put(DpsDepositFacade.FTP_USER_NAME, "ftpusername");
        parameterMap.put(DpsDepositFacade.FTP_DIRECTORY, "/some/where/inside/the/root");
        parameterMap.put(DpsDepositFacade.MATERIAL_FLOW_ID, "5");
        parameterMap.put(DpsDepositFacade.PDS_URL, "http://theHost.natlib.govt.nz:88889/pds");
        parameterMap.put(DpsDepositFacade.PRODUCER_ID, "10");
        parameterMap.put(DpsDepositFacade.DPS_WSDL_URL, "http://theHost.natlib.govt.nz:88888/dpsdepositservicefake?wsdl");
        parameterMap.put(DpsDepositFacade.ILS_REFERENCE, "1234567890");
        parameterMap.put(DpsDepositFacade.ACCESS_RESTRICTION, "ACR_OPA");
        parameterMap.put(DpsDepositFacade.WCT_METS_XML_DOCUMENT, wctMetsDocument);
        parameterMap.put(DpsDepositFacade.TARGET_INSTANCE_ID, "111122223333");
        return parameterMap;
    }

    private Mockery constructMockContext() {
        Mockery mockContext = new Mockery() {
            {
                setImposteriser(ClassImposteriser.INSTANCE);
            }
        };
        return mockContext;
    }
    
    // To generate arc file on the fly - avoiding any system issues with carriage returns and line feeds
    private void populateArcFileContents(){
    	StringBuilder contents = new StringBuilder();
    	contents.append("filedesc://WCT-20070813080023-00003-skynet.arc.open 0.0.0.0 20070813080023 text/plain 1157\n1 1 InternetArchive\nURL IP-address Archive-date Content-type Archive-length\n<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n<arcmetadata xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:dcterms=\"http://purl.org/dc/terms/\" xmlns:arc=\"http://archive.org/arc/1.0/\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"http://archive.org/arc/1.0/\" xsi:schemaLocation=\"http://archive.org/arc/1.0/ http://www.archive.org/arc/1.0/arc.xsd\">\n<arc:software>Heritrix 1.8.0 http://crawler.archive.org</arc:software>\n<arc:hostname>skynet</arc:hostname>\n<arc:ip>192.122.171.75</arc:ip>\n<arc:operator>WCT</arc:operator>\n<ns0:date xmlns:ns0=\"http://purl.org/dc/elements/1.1/\" xsi:type=\"dcterms:W3CDTF\">2007-08-13T08:00:21+00:00</ns0:date>\n<arc:http-header-user-agent>Mozilla/5.0 (compatible; heritrix/1.8.0 +http://webcurator.sourceforge.net/)</arc:http-header-user-agent>\n<arc:http-header-from>wct-noreply@natlib.govt.nz</arc:http-header-from>\n<arc:robots>ignore</arc:robots>\n<dc:format>ARC file version 1.1</dc:format>\n<dcterms:conformsTo xsi:type=\"dcterms:URI\">http://www.archive.org/web/researcher/ArcFileFormat.php</dcterms:conformsTo>\n</arcmetadata>\n\ndns:www.nzcee.co.nz 192.122.171.130 20070813080023 text/dns 56\n20070813080023\nwww.nzcee.co.nz.	3600	IN	A	202.27.243.13\n\nhttp://www.nzcee.co.nz/robots.txt 202.27.243.13 20070813080025 text/html 1814\nHTTP/1.1 404 Not Found\r\nContent-Length: 1635\r\nContent-Type: text/html\r\nServer: Microsoft-IIS/6.0\r\nX-Powered-By: ASP.NET\r\nDate: Mon, 13 Aug 2007 08:01:15 GMT\r\nConnection: close\r\n\r\n<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01//EN\" \"http://www.w3.org/TR/html4/strict.dtd\">\r\n<HTML><HEAD><TITLE>The page cannot be found</TITLE>\r\n<META HTTP-EQUIV=\"Content-Type\" Content=\"text/html; charset=Windows-1252\">\r\n<STYLE type=\"text/css\">\r\n  BODY { font: 8pt/12pt verdana }\r\n  H1 { font: 13pt/15pt verdana }\r\n  H2 { font: 8pt/12pt verdana }\r\n  A:link { color: red }\r\n  A:visited { color: maroon }\r\n</STYLE>\r\n</HEAD><BODY><TABLE width=500 border=0 cellspacing=10><TR><TD>\r\n\r\n<h1>The page cannot be found</h1>\r\nThe page you are looking for might have been removed, had its name changed, or is temporarily unavailable.\r\n<hr>\r\n<p>Please try the following:</p>\r\n<ul>\r\n<li>Make sure that the Web site address displayed in the address bar of your browser is spelled and formatted correctly.</li>\r\n<li>If you reached this page by clicking a link, contact\r\n the Web site administrator to alert them that the link is incorrectly formatted.\r\n</li>\r\n<li>Click the <a href=\"javascript:history.back(1)\">Back</a> button to try another link.</li>\r\n</ul>\r\n<h2>HTTP Error 404 - File or directory not found.<br>Internet Information Services (IIS)</h2>\r\n<hr>\r\n<p>Technical Information (for support personnel)</p>\r\n<ul>\r\n<li>Go to <a href=\"http://go.microsoft.com/fwlink/?linkid=8180\">Microsoft Product Support Services</a> and perform a title search for the words <b>HTTP</b> and <b>404</b>.</li>\r\n<li>Open <b>IIS Help</b>, which is accessible in IIS Manager (inetmgr),\r\n and search for topics titled <b>Web Site Setup</b>, <b>Common Administrative Tasks</b>, and <b>About Custom Error Messages</b>.</li>\r\n</ul>\r\n\r\n</TD></TR></TABLE></BODY></HTML>\r\n\nhttp://www.nzcee.co.nz/ 202.27.243.13 20070813080025 text/html 12057\nHTTP/1.1 200 OK\r\nConnection: close\r\nDate: Mon, 13 Aug 2007 08:01:15 GMT\r\nServer: Microsoft-IIS/6.0\r\nX-Powered-By: ASP.NET\r\nContent-Length: 11793\r\nContent-Type: text/html\r\nSet-Cookie: ASPSESSIONIDCQBDCCTQ=HMHBKCEDMOJJIMPGCOMIKGPH; path=/\r\nCache-control: private\r\n\r\n<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">\r\n<html><!-- InstanceBegin template=\"/Templates/Default.dwt.asp\" codeOutsideHTMLIsLocked=\"false\" -->\r\n<head>\r\n  <meta http-equiv=\"Content-Type\" content=\"text/html; charset=iso-8859-1\">\r\n  <!-- InstanceBeginEditable name=\"doctitle\" -->\r\n  <title>NZCEE: New Zealand Center for Ecological Economics</title>\r\n  <!-- InstanceEndEditable -->\r\n  <meta name=\"Keywords\" content=\"Ecological economics, ecological footprints, environmental accounting, environmental valuation, New Zealand, Massey University, Manaaki Whenua, Landcare Research, systems dynamics\">\r\n  <meta name=\"Description\" content=\"The New Zealand Centre for Ecological Economics, based in New Zealand, works in the ecological economics, ecological footprints, environmental accounting, environmental valuation, systems dynamics areas\">\r\n  <meta name=\"Abstract\" content=\"The New Zealand Centre for Ecological Economics, based in New Zealand, works in the ecological economics, ecological footprints, environmental accounting, environmental valuation, systems dynamics areas\">\r\n  <meta name=\"Security\" content=\"public\">\r\n  <meta name=\"DC.Rights\" content=\"Copyright (c) 2005 by NZCEE\">\r\n  <meta name=\"Robots\" content=\"index,follow\">\r\n  <meta name=\"DC.Language\" scheme=\"rfc1766\" content=\"en-NZ\">\r\n  \r\n  <link rel=\"stylesheet\" href=\"css/NZCEE_rma.css\" type=\"text/css\">\r\n  <link rel=\"stylesheet\" href=\"css/nzcee_print.css\" type=\"text/css\" media=\"print\">\r\n  <link rel=\"stylesheet\" href=\"css/nzcee_header_homepage.css\" type=\"text/css\">\r\n  \r\n  <link rel=\"shortcut icon\" href=\"/favicon.ico\" type=\"image/x-icon\">\r\n	<!-- InstanceBeginEditable name=\"head\" --><!-- InstanceEndEditable -->\r\n</head>\r\n<body stats=1>\r\n<!-- Start of DeepMetrix (.NET) StatScript -->\r\n<script language=\"javascript\">\r\n \r\nvar DMNETdomain = \"\";\r\nvar DMNETpage = window.location;\r\nvar DMNETversion = \"86\";\r\nvar DMNETsendTo = \"//www.landcareresearch.co.nz/dm.gif?\";\r\nvar DMNETsession;\r\n \r\nfunction dmneterr(){return true;}\r\n \r\nwindow.onerror=dmneterr;\r\nvar s = new Date();\r\n \r\nif (navigator.userAgent.indexOf('Mac') >= 0 && s.getTimezoneOffset() >= 720)\r\n  s.setTime (s.getTime() - 1440*60*1000);\r\n \r\nvar dmnetURL = location.protocol + DMNETsendTo+\"v=\"+DMNETversion+\"&vst=1\";\r\nvar dmnetCookieString = document.cookie.toString();\r\n \r\nif(dmnetCookieString.indexOf(\"_dmnid\") == -1)\r\n  {\r\n    DMNETsession = parseInt( Math.random()*1000000 ) + \"_\" + s.getTime();\r\n    var domStr = \"\";\r\n    if(DMNETdomain != \"\")\r\n    {\r\n      domStr = \"domain=\"+ DMNETdomain +\";\";\r\n    }\r\n    document.cookie = \"_dmnid=\" + DMNETsession + \";expires=Mon, 31-Dec-2008 00:00:00 GMT;\"+domStr+\"path=/;\";\r\n  }\r\ndmnetCookieString = document.cookie.toString();\r\nif(dmnetCookieString.indexOf('_dmnid') == -1)\r\n  {\r\n    DMNETsession = \"\";\r\n  }\r\nelse\r\n  {\r\n    if(dmnetCookieString.indexOf(';') == -1)\r\n      dmnetCookieString = dmnetCookieString.replace(/_dm/g, ';_dm');\r\n \r\n    var start = dmnetCookieString.indexOf(\"_dmnid=\") + 7;\r\n    var end = dmnetCookieString.indexOf(\";\",start);\r\n \r\n    if (end == -1)\r\n      end = dmnetCookieString.length;\r\n    DMNETsession = unescape(dmnetCookieString.substring(start,end));\r\n  }\r\ndmnetURL += \"&id=\"+DMNETsession+\"&url=\"+escape(DMNETpage) + \"&ref=\"+escape(document.referrer)+\"&lng=\" + ((!document.all \r\n  navigator.userAgent.match('Opera')) ? navigator.language : navigator.userLanguage) + \"&tz=\" + (Math.round(new Date('dec 1, 2002').getTimezoneOffset()/60)*-1);\r\nif(screen)\r\n  dmnetURL += \"&scr=\" + escape( screen.width + \"x\" + screen.height + \" \" + screen.colorDepth + \"bpp\" );\r\n \r\ndmnetURL += \"&rnd=\" + new Date().getTime();\r\n \r\nif(document.layers)\r\n{\r\n  document.write(\"<la\"+\"yer name=\"DMStats\" visibility=hide><img src=\"\"+dmnetURL+\"\" height=1 width=1></la\"+\"yer>\");\r\n}\r\nelse\r\n{\r\n  document.write(\"<di\"+\"v id=\"DMStats\" STYLE=\"position:absolute;visibility:hidden;\"><img src=\"\"+dmnetURL+\"\" height=1 width=1></di\"+\"v>\");\r\n}\r\n \r\n</script>\r\n<!-- End of DeepMetrix (.NET) StatScript -->\r\n<table class=\"maintable\" align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">\r\n  <tbody>\r\n    <tr>\r\n      <td valign=\"top\">\r\n      <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">\r\n        <tbody>\r\n          <tr>\r\n            <td height=\"27\">&nbsp;</td>\r\n          </tr>\r\n          <tr>\r\n            <td><img src=\"images/vert_NZCEE.gif\" width=\"30\" height=\"420\"></td>\r\n          </tr>\r\n        </tbody>\r\n      </table>\r\n      </td>\r\n      <td valign=\"top\">\r\n\r\n      <div class=\"print_hide\">\r\n			<table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">\r\n        <tbody>\r\n          <tr>\r\n            <td height=\"2\"><br></td>\r\n          </tr>\r\n          <tr>\r\n            <td colspan=\"3\" align=\"center\" background=\"/images/flax_top.gif\" height=\"26\" width=\"100%\">\r\n							<span class=\"topnav\">\r\n								<a href=\"/default.asp\" class=\"topnav\">Home</a> | \r\n								<a href=\"/pages/about_nzcee/\" class=\"topnav\">About&nbsp;NZCEE</a> | \r\n								<a href=\"/pages/research_projects/\" class=\"topnav\">Research&nbsp;Projects</a> | \r\n								<a href=\"/pages/services_products/\" class=\"topnav\">Expertise&nbsp;&amp;&nbsp;Services</a> | \r\n								<a href=\"/pages/publications/recent_publications.asp\" class=\"topnav\">Publications</a> \r\n|								<a href=\"/pages/contact_us/\" class=\"topnav\">Contact&nbsp;Us</a>\r\n							</span>\r\n						</td>\r\n          </tr>\r\n          \r\n          <tr>\r\n            <td height = \"7\" ><img src=\"/images/left_top_corner_blk.gif\" height=\"7\" width=\"8\"></td>\r\n            <td bgcolor=\"#fff6de\" height=\"7\" width=\"100%\">\r\n            <table style=\"width: 100%;\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">\r\n              <tbody>\r\n                <tr>\r\n                  <td height = \"7\" width=\"125\"></td>\r\n                  <td style=\"height: 7px;\"><img src=\"/images/mid_flax_bit.gif\" border=\"0\" height=\"7\" width=\"21\"></td>\r\n                </tr>\r\n              </tbody>\r\n            </table>\r\n            </td>\r\n            <td width=\"8\"><img src=\"/images/right_top_corner_blk.gif\" height=\"7\" width=\"8\"></td>\r\n          </tr>\r\n          \r\n          <tr>\r\n            <td class=\"topleft_bit\" width=\"8\"><br>\r\n            </td>\r\n            <td align=\"center\" bgcolor=\"#fff6de\">\r\n            <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">\r\n              <tbody>\r\n                <tr>\r\n                  <td><table style=\"width: 100%;\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">\r\n                    <tbody>\r\n                      <tr>\r\n                        <td width=\"30\">&nbsp;</td>\r\n                        <td style=\"width: 300px; height: 124px;\" background=\"/images/top_flax_and_NZCEE_part2.gif\"><br>\r\n                        </td>\r\n                        <td style=\"height: 124px; vertical-align: top; text-align: right;\" background=\"/images/sankey1.png\">\r\n \r\n <!-- SiteSearch Google -->\r\n <form style=\"margin-top: 4px; margin-bottom: 4px;\" method=\"get\" name=\"searchform\" action=\"http://www.google.co.nz/search\" target=\"_blank\"> <input name=\"as_sitesearch\" value=\"www.nzcee.org.nz\" type=\"hidden\"/> <input style=\"width: 120px;\" name=\"as_q\" size=\"20\" type=\"text\"/>&nbsp;<input value=\"Search site\" type=\"submit\"/>\r\n </form>\r\n <!-- SiteSearch Google -->\r\n\r\n												</td>\r\n                      </tr>\r\n                    </tbody>\r\n                  </table></td>\r\n                </tr>\r\n              </tbody>\r\n            </table>\r\n            </td>\r\n            <td class=\"topright_bit\" width=\"8\"><br>\r\n            </td>\r\n          </tr>\r\n        </tbody>\r\n      </table>\r\n			</div>\r\n      \r\n      <table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">\r\n        <tbody>\r\n          <tr class=\"print_hide\">\r\n            <td width=\"8\"><img src=\"/images/left_mid_corners_blk.gif\" height=\"17\" width=\"8\"></td>\r\n            <td><img src=\"/images/mid_edges_blk.gif\" height=\"17\" width=\"100%\"></td>\r\n            <td width=\"8\"><img src=\"/images/right_mid_corners_blk.gif\" height=\"17\" width=\"8\"></td>\r\n          </tr>\r\n          <tr>\r\n            <td class=\"left_bit\" width=\"8\"><br>\r\n            </td>\r\n            <td bgcolor=\"#ffffff\">\r\n            <table border=\"0\" cellpadding=\"10\" cellspacing=\"0\" width=\"100%\">\r\n\r\n\r\n              <tbody>\r\n                <tr>\r\n                  <td width=\"100%\" valign=\"top\">\r\n                  <h1><!-- InstanceBeginEditable name=\"Title\" -->Welcome to the New Zealand Centre for Ecological Economics (NZCEE) Website<!-- InstanceEndEditable --></h1>\r\n                  <!-- InstanceBeginEditable name=\"Body\" -->\r\n                  <p>The New Zealand Centre for Ecological Economics conducts  research that explores the links between   the environment, economy and people.</p>\r\n                  <p>Our research goal is to explore ways the quality of the New Zealand environment can be   maintained and enhanced while still allowing the economy and people of New Zealand to   prosper.</p>\r\n                  <p>Manaaki Whenua - Landcare Research and Massey University are collaborative   partners in this research centre. </p>\r\n                  <p>The new reality show &quot;WA$TED&quot; that airs 8pm Tuesdays on TV3  uses an Ecological Footprint Calculator produced by NZCEE. You can calculate your own &quot;household footprint&quot; at the WA$TED website: <a href=\"http://www.wastedtv.co.nz/index.cfm?&action=calculator\">http://www.wastedtv.co.nz/index.cfm?&amp;action=calculator</a> </p>\r\n                  <p>To link to <a href=\"files/newsletters/NZCEE%20News%20Iss2%20-%20August%202007.pdf\">NZCEE's latest newsletter</a> August 2007 <br>\r\n                  </p>\r\n                  <p>&nbsp;</p>\r\n                  <!-- InstanceEndEditable --></td>\r\n                  <td valign=\"top\">&nbsp;</td>\r\n                </tr>\r\n                <tr>\r\n                  <td valign=\"top\" colspan=\"2\">\r\n                  <p>&nbsp;</p>\r\n                  </td>\r\n                </tr>\r\n              </tbody>\r\n\r\n            </table>\r\n            </td>\r\n            <td class=\"right_bit\" width=\"8\"><br>\r\n            </td>\r\n          </tr>\r\n          <tr class=\"print_hide\">\r\n            <td><img src=\"/images/left_bottom_corner_w_blk.gif\" height=\"8\" width=\"8\"></td>\r\n            <td><img src=\"/images/bottom_edge_w_blk.gif\" height=\"8\" width=\"100%\"></td>\r\n            <td><img src=\"/images/right_bottom_corner_w_blk.gif\" height=\"8\" width=\"8\"></td>\r\n          </tr>\r\n          <tr>\r\n            <td colspan=\"3\" height=\"5\"></td>\r\n          </tr>\r\n        </tbody>\r\n      </table>\r\n      </td>\r\n    </tr>\r\n  </tbody>\r\n</table>\r\n\r\n<table width=\"500\" border=\"0\" align=\"center\">\r\n	<tbody>\r\n		<tr>\r\n			<td align=\"center\"><a href=\"http://www.massey.ac.nz\" target=\"_blank\"><img src=\"/images/logo_massey.gif\" alt=\"Massey University\" width=\"160\" height=\"61\" hspace=\"15\" border=\"0\"></a></td>\r\n			<td align=\"center\" nowrap=\"nowrap\" class=\"footer\">\r\n					This WWW site and all contents are the copyright of<br>\r\n					<a href=\"http://www.landcareresearch.co.nz\" target=\"_blank\" class=\"footer\">Landcare Research New Zealand Ltd</a> (<a href=\"http://www.landcareresearch.co.nz/about/disclaimer.asp\" target=\"_blank\" class=\"footer\">Disclaimer</a>) and<br>\r\n					<a href=\"http://www.massey.ac.nz\" target=\"_blank\" class=\"footer\">Massey University</a> 2007 (<a href=\"http://www.massey.ac.nz/disclaim.htm\" target=\"_blank\" class=\"footer\">Disclaimer</a>).<br>\r\n					This website is hosted by <a href=\"http://www.landcareresearch.co.nz\" target=\"_blank\" class=\"footer\">Landcare Research</a>.</td>\r\n			<td align=\"center\"><a href=\"http://www.landcareresearch.co.nz\" target=\"_blank\"><img src=\"/images/logo_landcare_research.gif\" alt=\"Landcare Research\" width=\"89\" height=\"89\" hspace=\"20\" border=\"0\"></a></td>\r\n		</tr>\r\n	</tbody>\r\n</table>\r\n\r\n\r\n</body>\r\n<!-- InstanceEnd --></html> ");
    	
    	File file = new File("src\\test\\resources\\WctFiles\\WCT-20070813080023-00003-skynet.arc");
    	if(file.exists()){
    	
    		FileOutputStream fso = null;
    		PrintWriter pw = null;
			try {
				fso = new FileOutputStream(file);
				pw = new PrintWriter(fso);
				pw.write(contents.toString());
			} catch (FileNotFoundException e) {
				throw new RuntimeException("Failed to create test arc file: " + e.getMessage());
			}
			finally{
				try {
					pw.close();
					fso.close();
				} catch (IOException e) {
					throw new RuntimeException("Failed to create test arc file: " + e.getMessage());
				}
			}
    		
    	}
    }
    
}
