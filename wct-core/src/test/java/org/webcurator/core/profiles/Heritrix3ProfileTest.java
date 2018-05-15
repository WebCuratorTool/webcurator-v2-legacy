package org.webcurator.core.profiles;

import org.junit.Test;
import org.webcurator.test.BaseWCTTest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Heritrix3ProfileTest extends BaseWCTTest<Heritrix3Profile> {
    private static final String LINE_SEPARATOR = System.getProperty("line.separator");

    public Heritrix3ProfileTest() {
        super(Heritrix3Profile.class, "src/test/java/org/webcurator/core/profiles/Heritrix3ProfileTest.cxml", false);
    }

    public void setUp() throws Exception {
        super.setUp();
        String xml = readXMLFile(new File(testFile));
        testInstance = new Heritrix3Profile(xml);
    }

    private String readXMLFile(File xmlFile) throws IOException {
        FileReader fileReader = new FileReader(xmlFile);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        StringBuffer stringBuffer = new StringBuffer();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            stringBuffer.append(line);
            stringBuffer.append("\n");
        }
        fileReader.close();
        return stringBuffer.toString();
    }

    @Test
    public final void testProfileOptionDataLimit() {
        Heritrix3ProfileOptions options = new Heritrix3ProfileOptions();
        options.setDataLimitAsBytes(new BigInteger("1000000"));
        options.setDataLimitUnit(null);
        assertEquals(new BigDecimal(1000000L), options.getDataLimit());
        options.setDataLimitUnit(ProfileDataUnit.B);
        assertEquals(new BigDecimal(1000000L), options.getDataLimit());
        options.setDataLimitUnit(ProfileDataUnit.KB);
        assertEquals(new BigDecimal(976.56250000d).setScale(8, BigDecimal.ROUND_HALF_UP), options.getDataLimit());
        options.setDataLimitUnit(ProfileDataUnit.MB);
        assertEquals(new BigDecimal(0.95367432d).setScale(8, BigDecimal.ROUND_HALF_UP), options.getDataLimit());
        options.setDataLimitUnit(ProfileDataUnit.GB);
        assertEquals(new BigDecimal(0.00093132d).setScale(8, BigDecimal.ROUND_HALF_UP), options.getDataLimit());
        options.setDataLimitAsBytes(new BigInteger("2684354560"));
        options.setDataLimitUnit(ProfileDataUnit.B);
        assertEquals(new BigDecimal(new BigInteger("2684354560")), options.getDataLimit());
        options.setDataLimitUnit(ProfileDataUnit.KB);
        assertEquals(new BigDecimal(2621440.00000000d).setScale(8, BigDecimal.ROUND_HALF_UP), options.getDataLimit());
        options.setDataLimitUnit(ProfileDataUnit.MB);
        assertEquals(new BigDecimal(2560.00000000d).setScale(8, BigDecimal.ROUND_HALF_UP), options.getDataLimit());
        options.setDataLimitUnit(ProfileDataUnit.GB);
        assertEquals(new BigDecimal(2.50000000d).setScale(8, BigDecimal.ROUND_HALF_UP), options.getDataLimit());

        options.setDataLimitUnit(null);
        options.setDataLimit(new BigDecimal(20000000L));
        assertEquals(new BigInteger("20000000"), options.getDataLimitAsBytes());
        options.setDataLimitUnit(ProfileDataUnit.B);
        options.setDataLimit(new BigDecimal(150000.999d));
        assertEquals(new BigInteger("150000"), options.getDataLimitAsBytes());
        options.setDataLimitUnit(ProfileDataUnit.KB);
        options.setDataLimit(new BigDecimal(7.5d));
        assertEquals(new BigInteger("7680"), options.getDataLimitAsBytes());
        options.setDataLimitUnit(ProfileDataUnit.MB);
        options.setDataLimit(new BigDecimal(7.54735763d));
        assertEquals(new BigInteger("7913978"), options.getDataLimitAsBytes());
        options.setDataLimitUnit(ProfileDataUnit.GB);
        options.setDataLimit(new BigDecimal(8.125d));
        assertEquals(new BigInteger("8724152320"), options.getDataLimitAsBytes());
    }

    @Test
    public final void testProfileOptionMaxFileSize() {
        Heritrix3ProfileOptions options = new Heritrix3ProfileOptions();
        options.setMaxFileSizeAsBytes(new BigInteger("50000000"));
        options.setMaxFileSizeUnit(null);
        assertEquals(new BigDecimal(50000000L), options.getMaxFileSize());
        options.setMaxFileSizeUnit(ProfileDataUnit.B);
        assertEquals(new BigDecimal(50000000L), options.getMaxFileSize());
        options.setMaxFileSizeUnit(ProfileDataUnit.KB);
        assertEquals(new BigDecimal(48828.12500000d).setScale(8, BigDecimal.ROUND_HALF_UP), options.getMaxFileSize());
        options.setMaxFileSizeUnit(ProfileDataUnit.MB);
        assertEquals(new BigDecimal(47.68371582d).setScale(8, BigDecimal.ROUND_HALF_UP), options.getMaxFileSize());
        options.setMaxFileSizeUnit(ProfileDataUnit.GB);
        assertEquals(new BigDecimal(0.04656613d).setScale(8, BigDecimal.ROUND_HALF_UP), options.getMaxFileSize());
        options.setMaxFileSizeAsBytes(new BigInteger("1288490188800"));
        options.setMaxFileSizeUnit(ProfileDataUnit.B);
        assertEquals(new BigDecimal(new BigInteger("1288490188800")), options.getMaxFileSize());
        options.setMaxFileSizeUnit(ProfileDataUnit.KB);
        assertEquals(new BigDecimal(1258291200.00000000d).setScale(8, BigDecimal.ROUND_HALF_UP), options.getMaxFileSize());
        options.setMaxFileSizeUnit(ProfileDataUnit.MB);
        assertEquals(new BigDecimal(1228800.00000000d).setScale(8, BigDecimal.ROUND_HALF_UP), options.getMaxFileSize());
        options.setMaxFileSizeUnit(ProfileDataUnit.GB);
        assertEquals(new BigDecimal(1200.00000000d).setScale(8, BigDecimal.ROUND_HALF_UP), options.getMaxFileSize());

        options.setMaxFileSizeUnit(null);
        options.setMaxFileSize(new BigDecimal(50000000L));
        assertEquals(new BigInteger("50000000"), options.getMaxFileSizeAsBytes());
        options.setMaxFileSizeUnit(ProfileDataUnit.B);
        options.setMaxFileSize(new BigDecimal(1577000.12345d));
        assertEquals(new BigInteger("1577000"), options.getMaxFileSizeAsBytes());
        options.setMaxFileSizeUnit(ProfileDataUnit.KB);
        options.setMaxFileSize(new BigDecimal(17.25d));
        assertEquals(new BigInteger("17664"), options.getMaxFileSizeAsBytes());
        options.setMaxFileSizeUnit(ProfileDataUnit.MB);
        options.setMaxFileSize(new BigDecimal(17.74843209d));
        assertEquals(new BigInteger("18610579"), options.getMaxFileSizeAsBytes());
        options.setMaxFileSizeUnit(ProfileDataUnit.GB);
        options.setMaxFileSize(new BigDecimal(175.7855d));
        assertEquals(new BigInteger("188748243402"), options.getMaxFileSizeAsBytes());
    }

    @Test
    public final void testProfileOptionBlockUrls() {
        Heritrix3ProfileOptions options = new Heritrix3ProfileOptions();
        // Convert block urls string into list
        String url1 = "http://aaa.com";
        String url2 = "http://bbb.com";
        String url3 = "http://ccc.com";
        String blockUrls = url1 + LINE_SEPARATOR + url2;
        options.setBlockURLs(blockUrls);
        List<String> blockUrlsAsList = options.getBlockURLsAsList();
        assertEquals(2, blockUrlsAsList.size());
        assertTrue(blockUrlsAsList.contains(url1));
        assertTrue(blockUrlsAsList.contains(url2));
        // Convert block urls to list
        String blockUrlsAsString = options.getBlockURLs();
        assertEquals(blockUrls, blockUrlsAsString);
        // Add extra separator
        String blockUrlsExtraSeparator = url1 + LINE_SEPARATOR + url2 + LINE_SEPARATOR + url3 + LINE_SEPARATOR;
        options.setBlockURLs(blockUrlsExtraSeparator);
        List<String> blockUrlsAsListExtraSeparator = options.getBlockURLsAsList();
        assertEquals(3, blockUrlsAsListExtraSeparator.size());
        assertTrue(blockUrlsAsListExtraSeparator.contains(url1));
        assertTrue(blockUrlsAsListExtraSeparator.contains(url2));
        assertTrue(blockUrlsAsListExtraSeparator.contains(url3));
        // Convert block urls to list
        String blockUrlsAsStringExtraSeparator = options.getBlockURLs();
        // Seperator removed in conversion process as the String.split doesn't add an empty string from after the last separator.
        assertEquals(url1 + LINE_SEPARATOR + url2 + LINE_SEPARATOR + url3, blockUrlsAsStringExtraSeparator);
    }

    @Test
    public final void testProfileOptionIncludeUrls() {
        Heritrix3ProfileOptions options = new Heritrix3ProfileOptions();
        // Convert include urls string into list
        String url1 = "http://xxx.com";
        String url2 = "http://yyy.com";
        String url3 = "http://zzz.com";
        String includeUrls = url1 + LINE_SEPARATOR + url2;
        options.setIncludeURLs(includeUrls);
        List<String> includeUrlsAsList = options.getIncludeURLsAsList();
        assertEquals(2, includeUrlsAsList.size());
        assertTrue(includeUrlsAsList.contains(url1));
        assertTrue(includeUrlsAsList.contains(url2));
        // Convert include urls to list
        String includeUrlsAsString = options.getIncludeURLs();
        assertEquals(includeUrls, includeUrlsAsString);
        // Add extra separator
        String includeUrlsExtraSeparator = url1 + LINE_SEPARATOR + url2 + LINE_SEPARATOR + url3 + LINE_SEPARATOR;
        options.setIncludeURLs(includeUrlsExtraSeparator);
        List<String> includeUrlsAsListExtraSeparator = options.getIncludeURLsAsList();
        assertEquals(3, includeUrlsAsListExtraSeparator.size());
        assertTrue(includeUrlsAsListExtraSeparator.contains(url1));
        assertTrue(includeUrlsAsListExtraSeparator.contains(url2));
        assertTrue(includeUrlsAsListExtraSeparator.contains(url3));
        // Convert include urls to list
        String includeUrlsAsStringExtraSeparator = options.getIncludeURLs();
        // Seperator removed in conversion process as the String.split doesn't add an empty string from after the last separator.
        assertEquals(url1 + LINE_SEPARATOR + url2 + LINE_SEPARATOR + url3, includeUrlsAsStringExtraSeparator);
    }

    @Test
    public final void testDefaultHeritrix3Profile() {
        Heritrix3Profile profile = new Heritrix3Profile();
        Heritrix3ProfileOptions profileOptions = profile.getHeritrix3ProfileOptions();
        assertEquals("http://www.natlib.govt.nz/", profileOptions.getContactURL());
        assertEquals(0L, profileOptions.getDocumentLimit());
        assertEquals(new BigInteger("0"), profileOptions.getDataLimitAsBytes());
        assertEquals(0L, profileOptions.getTimeLimitAsSeconds());
        assertEquals(20L, profileOptions.getMaxPathDepth());
        assertEquals(20L, profileOptions.getMaxHops());
        assertEquals(2L, profileOptions.getMaxTransitiveHops());
        assertFalse(profileOptions.isIgnoreRobotsTxt());
        assertFalse(profileOptions.isIgnoreCookies());
        assertEquals("ISO-8859-1", profileOptions.getDefaultEncoding());
        List<String> blockUrls = profileOptions.getBlockURLsAsList();
        assertEquals(43, blockUrls.size());
        assertTrue(blockUrls.contains(".*/text/javascript.*"));
        assertTrue(blockUrls.contains(".*youtube-nocookie.*"));
        List<String> includeUrls = profileOptions.getIncludeURLsAsList();
        assertEquals(2, includeUrls.size());
        assertTrue(includeUrls.contains(".*dia.*"));
        assertTrue(includeUrls.contains(".*natlib.*"));
        assertEquals(new BigInteger("1000000000"), profileOptions.getMaxFileSizeAsBytes());
        assertTrue(profileOptions.isCompress());
        assertEquals("IAH", profileOptions.getPrefix());
        assertEquals(Heritrix3Profile.MEDIUM, profileOptions.getPoliteness());
    }

    @Test
    public final void testXmlHeritrix3Profile() {
        Heritrix3ProfileOptions profileOptions = testInstance.getHeritrix3ProfileOptions();
        assertEquals("http://www.natlib.govt.nz/", profileOptions.getContactURL());
        assertEquals(0L, profileOptions.getDocumentLimit());
        assertEquals(new BigInteger("0"), profileOptions.getDataLimitAsBytes());
        assertEquals(0L, profileOptions.getTimeLimitAsSeconds());
        assertEquals(20L, profileOptions.getMaxPathDepth());
        assertEquals(200L, profileOptions.getMaxHops());
        assertEquals(2L, profileOptions.getMaxTransitiveHops());
        assertTrue(profileOptions.isIgnoreRobotsTxt());
        assertFalse(profileOptions.isIgnoreCookies());
        assertEquals("ISO-8859-1", profileOptions.getDefaultEncoding());
        List<String> blockUrls = profileOptions.getBlockURLsAsList();
        assertEquals(43, blockUrls.size());
        assertTrue(blockUrls.contains(".*/text/javascript.*"));
        assertTrue(blockUrls.contains(".*youtube-nocookie.*"));
        List<String> includeUrls = profileOptions.getIncludeURLsAsList();
        assertEquals(2, includeUrls.size());
        assertTrue(includeUrls.contains(".*dia.*"));
        assertTrue(includeUrls.contains(".*natlib.*"));
        assertEquals(new BigInteger("1000000000"), profileOptions.getMaxFileSizeAsBytes());
        assertTrue(profileOptions.isCompress());
        assertEquals("IAH", profileOptions.getPrefix());
        assertEquals(Heritrix3Profile.POLITE, profileOptions.getPoliteness());
    }

    @Test
    public final void testToProfileXmlHeritrix3Profile() {
        String modifiedContactURL = "http://www.dia.govt.nz";
        long modifiedDocumentLimit = 25;
        BigInteger modifiedDataLimit = new BigInteger("100");
        long modifiedTimeLimit = 250;
        long modifiedMaxPathDepth = 150;
        long modifiedMaxHops = 50;
        long modifiedMaxTransitiveHops = 5;
        boolean modifiedIgnoreRobots = true;
        boolean modifiedIgnoreCookies = true;
        String modifiedDefaultEncoding = "UTF-8";
        List<String> modifiedBlockUrls = new ArrayList<String>();
        modifiedBlockUrls.add("*aaa*");
        modifiedBlockUrls.add("*bbb*");
        List<String> modifiedIncludeUrls = new ArrayList<String>();
        modifiedIncludeUrls.add("*xxx*");
        modifiedIncludeUrls.add("*yyy*");
        modifiedIncludeUrls.add("*zzz*");
        BigInteger modifiedMaxFileSize = new BigInteger("999999999");
        boolean modifiedCompress = false;
        String modifiedPrefix = "XXX";
        String modifiedPoliteness = Heritrix3Profile.AGGRESSIVE;
        Heritrix3Profile profile = new Heritrix3Profile();
        Heritrix3ProfileOptions profileOptions = profile.getHeritrix3ProfileOptions();
        // Modify test instance
        profileOptions.setContactURL(modifiedContactURL);
        profileOptions.setDocumentLimit(modifiedDocumentLimit);
        profileOptions.setDataLimitAsBytes(modifiedDataLimit);
        profileOptions.setTimeLimitAsSeconds(modifiedTimeLimit);
        profileOptions.setMaxPathDepth(modifiedMaxPathDepth);
        profileOptions.setMaxHops(modifiedMaxHops);
        profileOptions.setMaxTransitiveHops(modifiedMaxTransitiveHops);
        profileOptions.setIgnoreRobotsTxt(modifiedIgnoreRobots);
        profileOptions.setIgnoreCookies(modifiedIgnoreCookies);
        profileOptions.setDefaultEncoding(modifiedDefaultEncoding);
        profileOptions.setBlockURLsAsList(modifiedBlockUrls);
        profileOptions.setIncludeURLsAsList(modifiedIncludeUrls);
        profileOptions.setMaxFileSizeAsBytes(modifiedMaxFileSize);
        profileOptions.setCompress(modifiedCompress);
        profileOptions.setPrefix(modifiedPrefix);
        profileOptions.setPoliteness(modifiedPoliteness);
        String modifiedXml = profile.toProfileXml();
        // Create new profile instance with modified XML
        Heritrix3Profile modifiedProfile = new Heritrix3Profile(modifiedXml);
        Heritrix3ProfileOptions modifiedProfileOptions = modifiedProfile.getHeritrix3ProfileOptions();
        assertEquals(modifiedContactURL, modifiedProfileOptions.getContactURL());
        assertEquals(modifiedDocumentLimit, modifiedProfileOptions.getDocumentLimit());
        assertEquals(modifiedDataLimit, modifiedProfileOptions.getDataLimitAsBytes());
        assertEquals(modifiedTimeLimit, modifiedProfileOptions.getTimeLimitAsSeconds());
        assertEquals(modifiedMaxPathDepth, modifiedProfileOptions.getMaxPathDepth());
        assertEquals(modifiedMaxHops, modifiedProfileOptions.getMaxHops());
        assertEquals(modifiedMaxTransitiveHops, modifiedProfileOptions.getMaxTransitiveHops());
        assertTrue(modifiedProfileOptions.isIgnoreRobotsTxt());
        assertTrue(modifiedProfileOptions.isIgnoreCookies());
        assertEquals(modifiedDefaultEncoding, modifiedProfileOptions.getDefaultEncoding());
        List<String> blockUrls = modifiedProfileOptions.getBlockURLsAsList();
        assertEquals(2, blockUrls.size());
        assertTrue(blockUrls.contains("*aaa*"));
        assertTrue(blockUrls.contains("*bbb*"));
        assertFalse(blockUrls.contains(".*/text/javascript.*"));
        assertFalse(blockUrls.contains(".*youtube-nocookie.*"));
        List<String> includeUrls = modifiedProfileOptions.getIncludeURLsAsList();
        assertEquals(3, includeUrls.size());
        assertTrue(includeUrls.contains("*xxx*"));
        assertTrue(includeUrls.contains("*yyy*"));
        assertTrue(includeUrls.contains("*zzz*"));
        assertFalse(includeUrls.contains(".*dia.*"));
        assertFalse(includeUrls.contains(".*natlib.*"));
        assertEquals(modifiedMaxFileSize, modifiedProfileOptions.getMaxFileSizeAsBytes());
        assertFalse(modifiedProfileOptions.isCompress());
        assertEquals(modifiedPrefix, modifiedProfileOptions.getPrefix());
        assertEquals(Heritrix3Profile.AGGRESSIVE, modifiedProfileOptions.getPoliteness());
    }
}
