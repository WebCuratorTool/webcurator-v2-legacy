package org.webcurator.core.profiles;

import org.junit.Test;
import org.webcurator.test.BaseWCTTest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Heritrix3ProfileTest extends BaseWCTTest<Heritrix3Profile> {

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
    public final void testDefaultHeritrix3Profile() {
        Heritrix3Profile profile = new Heritrix3Profile();
        Heritrix3ProfileOptions profileOptions = profile.getHeritrix3ProfileOptions();
        assertEquals("http://www.natlib.govt.nz", profileOptions.getContactURL());
        assertEquals(0L, profileOptions.getDocumentLimit());
        assertEquals(0L, profileOptions.getDataLimit());
        assertEquals(0L, profileOptions.getTimeLimit());
        assertEquals(20L, profileOptions.getMaxPathDepth());
        assertEquals(20L, profileOptions.getMaxHops());
        assertEquals(2L, profileOptions.getMaxTransitiveHops());
        assertFalse(profileOptions.isIgnoreCookies());
        assertEquals("ISO-8859-1", profileOptions.getDefaultEncoding());
        assertEquals(1000000000L, profileOptions.getMaxFileSize());
        assertTrue(profileOptions.isCompress());
        assertEquals("IAH", profileOptions.getPrefix());
    }

    @Test
    public final void testXmlHeritrix3Profile() {
        Heritrix3ProfileOptions profileOptions = testInstance.getHeritrix3ProfileOptions();
        assertEquals("http://www.natlib.govt.nz", profileOptions.getContactURL());
        assertEquals(0L, profileOptions.getDocumentLimit());
        assertEquals(0L, profileOptions.getDataLimit());
        assertEquals(0L, profileOptions.getTimeLimit());
        assertEquals(20L, profileOptions.getMaxPathDepth());
        assertEquals(200L, profileOptions.getMaxHops());
        assertEquals(2L, profileOptions.getMaxTransitiveHops());
        assertFalse(profileOptions.isIgnoreCookies());
        assertEquals("ISO-8859-1", profileOptions.getDefaultEncoding());
        assertEquals(1000000000L, profileOptions.getMaxFileSize());
        assertTrue(profileOptions.isCompress());
        assertEquals("IAH", profileOptions.getPrefix());
    }

    @Test
    public final void testToProfileXmlHeritrix3Profile() {
        String modifiedContactURL = "http://www.dia.govt.nz";
        long modifiedDocumentLimit = 25;
        long modifiedDataLimit = 100;
        long modifiedTimeLimit = 250;
        long modifiedMaxPathDepth = 150;
        long modifiedMaxHops = 50;
        long modifiedMaxTransitiveHops = 5;
        boolean modifiedIgnoreCookies = true;
        String modifiedDefaultEncoding = "UTF-8";
        long modifiedMaxFileSize = 999999999;
        boolean modifiedCompress = false;
        String modifiedPrefix = "XXX";
        Heritrix3Profile profile = new Heritrix3Profile();
        Heritrix3ProfileOptions profileOptions = profile.getHeritrix3ProfileOptions();
        // Modify test instance
        profileOptions.setContactURL(modifiedContactURL);
        profileOptions.setDocumentLimit(modifiedDocumentLimit);
        profileOptions.setDataLimit(modifiedDataLimit);
        profileOptions.setTimeLimit(modifiedTimeLimit);
        profileOptions.setMaxPathDepth(modifiedMaxPathDepth);
        profileOptions.setMaxHops(modifiedMaxHops);
        profileOptions.setMaxTransitiveHops(modifiedMaxTransitiveHops);
        profileOptions.setIgnoreCookies(modifiedIgnoreCookies);
        profileOptions.setDefaultEncoding(modifiedDefaultEncoding);
        profileOptions.setMaxFileSize(modifiedMaxFileSize);
        profileOptions.setCompress(modifiedCompress);
        profileOptions.setPrefix(modifiedPrefix);
        String modifiedXml = profile.toProfileXml();
        // Create new profile instance with modified XML
        Heritrix3Profile modifiedProfile = new Heritrix3Profile(modifiedXml);
        Heritrix3ProfileOptions modifiedProfileOptions = modifiedProfile.getHeritrix3ProfileOptions();
        assertEquals(modifiedContactURL, modifiedProfileOptions.getContactURL());
        assertEquals(modifiedDocumentLimit, modifiedProfileOptions.getDocumentLimit());
        assertEquals(modifiedDataLimit, modifiedProfileOptions.getDataLimit());
        assertEquals(modifiedTimeLimit, modifiedProfileOptions.getTimeLimit());
        assertEquals(modifiedMaxPathDepth, modifiedProfileOptions.getMaxPathDepth());
        assertEquals(modifiedMaxHops, modifiedProfileOptions.getMaxHops());
        assertEquals(modifiedMaxTransitiveHops, modifiedProfileOptions.getMaxTransitiveHops());
        assertTrue(modifiedProfileOptions.isIgnoreCookies());
        assertEquals(modifiedDefaultEncoding, modifiedProfileOptions.getDefaultEncoding());
        assertEquals(modifiedMaxFileSize, modifiedProfileOptions.getMaxFileSize());
        assertFalse(modifiedProfileOptions.isCompress());
        assertEquals(modifiedPrefix, modifiedProfileOptions.getPrefix());
    }
}
