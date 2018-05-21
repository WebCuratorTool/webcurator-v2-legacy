package org.webcurator.core.profiles;

import org.junit.Test;
import org.webcurator.domain.model.core.ProfileOverrides;
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
        assertEquals(new BigDecimal(1000000L).setScale(8, BigDecimal.ROUND_HALF_UP), options.getDataLimit());
        options.setDataLimitUnit(ProfileDataUnit.B);
        assertEquals(new BigDecimal(1000000L).setScale(8, BigDecimal.ROUND_HALF_UP), options.getDataLimit());
        options.setDataLimitUnit(ProfileDataUnit.KB);
        assertEquals(new BigDecimal(976.56250000d).setScale(8, BigDecimal.ROUND_HALF_UP), options.getDataLimit());
        options.setDataLimitUnit(ProfileDataUnit.MB);
        assertEquals(new BigDecimal(0.95367432d).setScale(8, BigDecimal.ROUND_HALF_UP), options.getDataLimit());
        options.setDataLimitUnit(ProfileDataUnit.GB);
        assertEquals(new BigDecimal(0.00093132d).setScale(8, BigDecimal.ROUND_HALF_UP), options.getDataLimit());
        options.setDataLimitAsBytes(new BigInteger("2684354560"));
        options.setDataLimitUnit(ProfileDataUnit.B);
        assertEquals(new BigDecimal(new BigInteger("2684354560")).setScale(8, BigDecimal.ROUND_HALF_UP), options.getDataLimit());
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
        assertEquals(new BigDecimal(50000000L).setScale(8, BigDecimal.ROUND_HALF_UP), options.getMaxFileSize());
        options.setMaxFileSizeUnit(ProfileDataUnit.B);
        assertEquals(new BigDecimal(50000000L).setScale(8, BigDecimal.ROUND_HALF_UP), options.getMaxFileSize());
        options.setMaxFileSizeUnit(ProfileDataUnit.KB);
        assertEquals(new BigDecimal(48828.12500000d).setScale(8, BigDecimal.ROUND_HALF_UP), options.getMaxFileSize());
        options.setMaxFileSizeUnit(ProfileDataUnit.MB);
        assertEquals(new BigDecimal(47.68371582d).setScale(8, BigDecimal.ROUND_HALF_UP), options.getMaxFileSize());
        options.setMaxFileSizeUnit(ProfileDataUnit.GB);
        assertEquals(new BigDecimal(0.04656613d).setScale(8, BigDecimal.ROUND_HALF_UP), options.getMaxFileSize());
        options.setMaxFileSizeAsBytes(new BigInteger("1288490188800"));
        options.setMaxFileSizeUnit(ProfileDataUnit.B);
        assertEquals(new BigDecimal(new BigInteger("1288490188800")).setScale(8, BigDecimal.ROUND_HALF_UP), options.getMaxFileSize());
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
    public final void testProfileOptionTimeLimit() {
        Heritrix3ProfileOptions options = new Heritrix3ProfileOptions();
        options.setTimeLimitAsSeconds(new BigInteger("50000000"));
        options.setTimeLimitUnit(null);
        assertEquals(new BigDecimal(50000000L).setScale(8, BigDecimal.ROUND_HALF_UP), options.getTimeLimit());
        options.setTimeLimitUnit(ProfileTimeUnit.SECOND);
        assertEquals(new BigDecimal(50000000L).setScale(8, BigDecimal.ROUND_HALF_UP), options.getTimeLimit());
        options.setTimeLimitUnit(ProfileTimeUnit.MINUTE);
        assertEquals(new BigDecimal(833333.33333333d).setScale(8, BigDecimal.ROUND_HALF_UP), options.getTimeLimit());
        options.setTimeLimitUnit(ProfileTimeUnit.HOUR);
        assertEquals(new BigDecimal(13888.88888889d).setScale(8, BigDecimal.ROUND_HALF_UP), options.getTimeLimit());
        options.setTimeLimitUnit(ProfileTimeUnit.DAY);
        assertEquals(new BigDecimal(578.70370370d).setScale(8, BigDecimal.ROUND_HALF_UP), options.getTimeLimit());
        options.setTimeLimitUnit(ProfileTimeUnit.WEEK);
        assertEquals(new BigDecimal(82.67195767d).setScale(8, BigDecimal.ROUND_HALF_UP), options.getTimeLimit());
        options.setTimeLimitAsSeconds(new BigInteger("1512000"));
        options.setTimeLimitUnit(ProfileTimeUnit.SECOND);
        assertEquals(new BigDecimal(new BigInteger("1512000")).setScale(8, BigDecimal.ROUND_HALF_UP), options.getTimeLimit());
        options.setTimeLimitUnit(ProfileTimeUnit.MINUTE);
        assertEquals(new BigDecimal(25200.00000000d).setScale(8, BigDecimal.ROUND_HALF_UP), options.getTimeLimit());
        options.setTimeLimitUnit(ProfileTimeUnit.HOUR);
        assertEquals(new BigDecimal(420.00000000d).setScale(8, BigDecimal.ROUND_HALF_UP), options.getTimeLimit());
        options.setTimeLimitUnit(ProfileTimeUnit.DAY);
        assertEquals(new BigDecimal(17.50000000d).setScale(8, BigDecimal.ROUND_HALF_UP), options.getTimeLimit());
        options.setTimeLimitUnit(ProfileTimeUnit.WEEK);
        assertEquals(new BigDecimal(2.50000000d).setScale(8, BigDecimal.ROUND_HALF_UP), options.getTimeLimit());

        options.setTimeLimitUnit(null);
        options.setTimeLimit(new BigDecimal(50000000L));
        assertEquals(new BigInteger("50000000"), options.getTimeLimitAsSeconds());
        options.setTimeLimitUnit(ProfileTimeUnit.SECOND);
        options.setTimeLimit(new BigDecimal(4563554.6844d));
        assertEquals(new BigInteger("4563554"), options.getTimeLimitAsSeconds());
        options.setTimeLimitUnit(ProfileTimeUnit.MINUTE);
        options.setTimeLimit(new BigDecimal(17.25d));
        assertEquals(new BigInteger("1035"), options.getTimeLimitAsSeconds());
        options.setTimeLimitUnit(ProfileTimeUnit.HOUR);
        options.setTimeLimit(new BigDecimal(17.74843209d));
        assertEquals(new BigInteger("63894"), options.getTimeLimitAsSeconds());
        options.setTimeLimitUnit(ProfileTimeUnit.DAY);
        options.setTimeLimit(new BigDecimal(175.7855d));
        assertEquals(new BigInteger("15187867"), options.getTimeLimitAsSeconds());
        options.setTimeLimitUnit(ProfileTimeUnit.WEEK);
        options.setTimeLimit(new BigDecimal(7.65443d));
        assertEquals(new BigInteger("4629399"), options.getTimeLimitAsSeconds());
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
    public final void testProfileOptionUserAgent() {
        Heritrix3Profile profile = new Heritrix3Profile();
        Heritrix3ProfileOptions profileOptions = profile.getHeritrix3ProfileOptions();
        assertEquals("Mozilla/5.0 (compatible; heritrix/@VERSION@ +@OPERATOR_CONTACT_URL@)", profileOptions.getUserAgentTemplate());
        assertEquals("Mozilla/5.0 (compatible; heritrix/@VERSION@ +", profileOptions.getUserAgent());
        profileOptions.setUserAgent("Mozilla/5.0 (compatible; heritrix/3.3.0 +");
        assertEquals("Mozilla/5.0 (compatible; heritrix/3.3.0 +@OPERATOR_CONTACT_URL@)", profileOptions.getUserAgentTemplate());

        // Test idempotency
        assertEquals("Mozilla/5.0 (compatible; heritrix/3.3.0 +", profileOptions.getUserAgent());
        assertEquals("Mozilla/5.0 (compatible; heritrix/3.3.0 +", profileOptions.getUserAgent());
        profileOptions.setUserAgent("Mozilla/5.0 (compatible; heritrix/3.3.0 +");
        profileOptions.setUserAgent("Mozilla/5.0 (compatible; heritrix/3.3.0 +");
        assertEquals("Mozilla/5.0 (compatible; heritrix/3.3.0 +@OPERATOR_CONTACT_URL@)", profileOptions.getUserAgentTemplate());
        assertEquals("Mozilla/5.0 (compatible; heritrix/3.3.0 +", profileOptions.getUserAgent());
    }

    @Test
    public final void testDefaultHeritrix3Profile() {
        Heritrix3Profile profile = new Heritrix3Profile();
        Heritrix3ProfileOptions profileOptions = profile.getHeritrix3ProfileOptions();
        assertDefaultProfileOptions(profileOptions);
    }

    private void assertDefaultProfileOptions(Heritrix3ProfileOptions profileOptions) {
        assertEquals("http://www.natlib.govt.nz/", profileOptions.getContactURL());
        assertEquals("Mozilla/5.0 (compatible; heritrix/@VERSION@ +@OPERATOR_CONTACT_URL@)", profileOptions.getUserAgentTemplate());
        assertEquals(0L, profileOptions.getDocumentLimit());
        assertEquals(new BigInteger("0"), profileOptions.getDataLimitAsBytes());
        assertEquals(new BigInteger("0"), profileOptions.getTimeLimitAsSeconds());
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
        PolitenessOptions politenessOptions = profileOptions.getPolitenessOptions();
        assertEquals(5.0d, politenessOptions.getDelayFactor(), 0.0);
        assertEquals(3000L, politenessOptions.getMinDelayMs());
        assertEquals(30000L, politenessOptions.getMaxDelayMs());
        assertEquals(300L, politenessOptions.getRespectCrawlDelayUpToSeconds());
        assertEquals(800L, politenessOptions.getMaxPerHostBandwidthUsageKbSec());
    }


    @Test
    public final void testXmlHeritrix3Profile() {
        Heritrix3ProfileOptions profileOptions = testInstance.getHeritrix3ProfileOptions();
        assertEquals("http://www.natlib.govt.nz/", profileOptions.getContactURL());
        assertEquals("Mozilla/5.0 (compatible; heritrix/@VERSION@ +@OPERATOR_CONTACT_URL@)", profileOptions.getUserAgentTemplate());
        assertEquals(0L, profileOptions.getDocumentLimit());
        assertEquals(new BigInteger("0"), profileOptions.getDataLimitAsBytes());
        assertEquals(new BigInteger("0"), profileOptions.getTimeLimitAsSeconds());
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
        PolitenessOptions politenessOptions = profileOptions.getPolitenessOptions();
        assertEquals(10.0d, politenessOptions.getDelayFactor(), 0.0);
        assertEquals(9000L, politenessOptions.getMinDelayMs());
        assertEquals(90000L, politenessOptions.getMaxDelayMs());
        assertEquals(900L, politenessOptions.getRespectCrawlDelayUpToSeconds());
        assertEquals(400L, politenessOptions.getMaxPerHostBandwidthUsageKbSec());
    }

    @Test
    public final void testToProfileXmlHeritrix3Profile() {
        String modifiedContactURL = "http://www.dia.govt.nz";
        String modifiedUserAgentTemplate = "IE/11.0 (compatible; heritrix/@VERSION@ +@OPERATOR_CONTACT_URL@)";
        long modifiedDocumentLimit = 25;
        BigInteger modifiedDataLimit = new BigInteger("100");
        BigInteger modifiedTimeLimit = new BigInteger("250");
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
        double modifiedDelayFactor = 10.0d;
        long modifiedMinDelayMs = 9000L;
        long modifiedMaxDelayMs = 90000L;
        long modifiedRespectCrawlDelayUpToSeconds = 900L;
        long modifiedMaxPerHostBandwidthUsageKbSec = 400L;
        Heritrix3Profile profile = new Heritrix3Profile();
        Heritrix3ProfileOptions profileOptions = profile.getHeritrix3ProfileOptions();
        // Modify test instance
        profileOptions.setContactURL(modifiedContactURL);
        profileOptions.setUserAgentTemplate(modifiedUserAgentTemplate);
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
        PolitenessOptions politenessOptions = profileOptions.getPolitenessOptions();
        politenessOptions.setDelayFactor(modifiedDelayFactor);
        politenessOptions.setMinDelayMs(modifiedMinDelayMs);
        politenessOptions.setMaxDelayMs(modifiedMaxDelayMs);
        politenessOptions.setRespectCrawlDelayUpToSeconds(modifiedRespectCrawlDelayUpToSeconds);
        politenessOptions.setMaxPerHostBandwidthUsageKbSec(modifiedMaxPerHostBandwidthUsageKbSec);
        String modifiedXml = profile.toProfileXml();
        // Create new profile instance with modified XML
        Heritrix3Profile modifiedProfile = new Heritrix3Profile(modifiedXml);
        Heritrix3ProfileOptions modifiedProfileOptions = modifiedProfile.getHeritrix3ProfileOptions();
        assertEquals(modifiedContactURL, modifiedProfileOptions.getContactURL());
        assertEquals(modifiedUserAgentTemplate, modifiedProfileOptions.getUserAgentTemplate());
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
        PolitenessOptions modifiedPolitenessOptions = modifiedProfileOptions.getPolitenessOptions();
        assertEquals(10.0d, modifiedPolitenessOptions.getDelayFactor(), 0.0);
        assertEquals(9000L, modifiedPolitenessOptions.getMinDelayMs());
        assertEquals(90000L, modifiedPolitenessOptions.getMaxDelayMs());
        assertEquals(900L, modifiedPolitenessOptions.getRespectCrawlDelayUpToSeconds());
        assertEquals(400L, modifiedPolitenessOptions.getMaxPerHostBandwidthUsageKbSec());
    }

    @Test
    public final void testAllProfileOverrides() {
        ProfileOverrides profileOverrides = new ProfileOverrides();
        Heritrix3Profile profile = new Heritrix3Profile();
        // Set the profileOverrides data
        // No overrides set to true
        long modifiedDocumentLimit = 25;
        double modifiedDataLimit = 100.0d;
        double modifiedTimeLimit = 250.0d;
        long modifiedMaxPathDepth = 150;
        long modifiedMaxHops = 50;
        long modifiedMaxTransitiveHops = 5;
        String modifiedIgnoreRobots = "ignore";
        boolean modifiedIgnoreCookies = true;
        List<String> modifiedBlockUrls = new ArrayList<String>();
        modifiedBlockUrls.add("*aaa*");
        modifiedBlockUrls.add("*bbb*");
        List<String> modifiedIncludeUrls = new ArrayList<String>();
        modifiedIncludeUrls.add("*xxx*");
        modifiedIncludeUrls.add("*yyy*");
        modifiedIncludeUrls.add("*zzz*");
        profileOverrides.setH3DocumentLimit(modifiedDocumentLimit);
        profileOverrides.setH3DataLimit(modifiedDataLimit);
        profileOverrides.setH3DataLimitUnit(ProfileDataUnit.KB.name());
        profileOverrides.setH3TimeLimit(modifiedTimeLimit);
        profileOverrides.setH3TimeLimitUnit(ProfileTimeUnit.MINUTE.name());
        profileOverrides.setH3MaxPathDepth(modifiedMaxPathDepth);
        profileOverrides.setH3MaxHops(modifiedMaxHops);
        profileOverrides.setH3MaxTransitiveHops(modifiedMaxTransitiveHops);
        profileOverrides.setH3IgnoreRobots(modifiedIgnoreRobots);
        profileOverrides.setH3IgnoreCookies(modifiedIgnoreCookies);
        profileOverrides.setH3BlockedUrls(modifiedBlockUrls);
        profileOverrides.setH3IncludedUrls(modifiedIncludeUrls);
        // Apply
        profileOverrides.apply(profile);
        String modifiedXml = profile.toProfileXml();
        // Create new profile instance with modified XML
        Heritrix3Profile modifiedProfile = new Heritrix3Profile(modifiedXml);
        // Assert defaults
        assertDefaultProfileOptions(modifiedProfile.getHeritrix3ProfileOptions());
        // override all
        profileOverrides.setOverrideH3DocumentLimit(true);
        profileOverrides.setOverrideH3DataLimit(true);
        profileOverrides.setOverrideH3TimeLimit(true);
        profileOverrides.setOverrideH3MaxPathDepth(true);
        profileOverrides.setOverrideH3MaxHops(true);
        profileOverrides.setOverrideH3MaxTransitiveHops(true);
        profileOverrides.setOverrideH3IgnoreRobots(true);
        profileOverrides.setOverrideH3IgnoreCookies(true);
        profileOverrides.setOverrideH3BlockedUrls(true);
        profileOverrides.setOverrideH3IncludedUrls(true);
        // apply
        // Apply
        profileOverrides.apply(modifiedProfile);
        String overriddenXml = modifiedProfile.toProfileXml();
        // Create new profile instance with overridden XML
        Heritrix3Profile overriddenProfile = new Heritrix3Profile(overriddenXml);
        // Assertions
        Heritrix3ProfileOptions overriddenProfileOptions = overriddenProfile.getHeritrix3ProfileOptions();
        assertEquals("http://www.natlib.govt.nz/", overriddenProfileOptions.getContactURL());
        assertEquals("Mozilla/5.0 (compatible; heritrix/@VERSION@ +@OPERATOR_CONTACT_URL@)", overriddenProfileOptions.getUserAgentTemplate());
        assertEquals(25L, overriddenProfileOptions.getDocumentLimit());
        assertEquals(new BigInteger("102400"), overriddenProfileOptions.getDataLimitAsBytes());
        assertEquals(new BigInteger("15000"), overriddenProfileOptions.getTimeLimitAsSeconds());
        assertEquals(150L, overriddenProfileOptions.getMaxPathDepth());
        assertEquals(50L, overriddenProfileOptions.getMaxHops());
        assertEquals(5L, overriddenProfileOptions.getMaxTransitiveHops());
        assertTrue(overriddenProfileOptions.isIgnoreRobotsTxt());
        assertTrue(overriddenProfileOptions.isIgnoreCookies());
        assertEquals("ISO-8859-1", overriddenProfileOptions.getDefaultEncoding());
        List<String> blockUrls = overriddenProfileOptions.getBlockURLsAsList();
        assertEquals(2, blockUrls.size());
        assertTrue(blockUrls.contains("*aaa*"));
        assertTrue(blockUrls.contains("*bbb*"));
        List<String> includeUrls = overriddenProfileOptions.getIncludeURLsAsList();
        assertEquals(3, includeUrls.size());
        assertTrue(includeUrls.contains("*xxx*"));
        assertTrue(includeUrls.contains("*yyy*"));
        assertTrue(includeUrls.contains("*zzz*"));
        assertEquals(new BigInteger("1000000000"), overriddenProfileOptions.getMaxFileSizeAsBytes());
        assertTrue(overriddenProfileOptions.isCompress());
        assertEquals("IAH", overriddenProfileOptions.getPrefix());
        PolitenessOptions politenessOptions = overriddenProfileOptions.getPolitenessOptions();
        assertEquals(5.0d, politenessOptions.getDelayFactor(), 0.0);
        assertEquals(3000L, politenessOptions.getMinDelayMs());
        assertEquals(30000L, politenessOptions.getMaxDelayMs());
        assertEquals(300L, politenessOptions.getRespectCrawlDelayUpToSeconds());
        assertEquals(800L, politenessOptions.getMaxPerHostBandwidthUsageKbSec());
    }
}
