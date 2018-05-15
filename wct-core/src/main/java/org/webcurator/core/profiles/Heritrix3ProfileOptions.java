package org.webcurator.core.profiles;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * This class contains the profile options used in the Heritrix 3 profile.
 *
 * It is used primarily:
 * 1. when generating Heritrix 3 xml from profile options and vice-versa
 * 2. to hold the Heritrix 3 profile options in the UI
 */
public class Heritrix3ProfileOptions {
    private String contactURL;
    private long documentLimit;
    private BigInteger dataLimitAsBytes;
    private ProfileDataUnit dataLimitUnit;
    private long timeLimitAsSeconds;
    private ProfileTimeUnit timeLimitUnit;
    private long maxPathDepth;
    private long maxHops;
    private long maxTransitiveHops;
    private boolean ignoreRobotsTxt;
    private boolean ignoreCookies;
    private String defaultEncoding;
    private List<String> blockURLsAsList = new ArrayList<String>();
    private List<String> includeURLsAsList = new ArrayList<String>();
    private Writer writer;
    private BigInteger maxFileSizeAsBytes;
    private ProfileDataUnit maxFileSizeUnit;
    private boolean compress;
    private String prefix;
    private String politeness;
    private static final String LINE_SEPARATOR = System.getProperty("line.separator");

    public enum Writer {
        WARC, ARC
    }

    public String getContactURL() {
        return contactURL;
    }

    public void setContactURL(String contactURL) {
        this.contactURL = contactURL;
    }

    public long getDocumentLimit() {
        return documentLimit;
    }

    public void setDocumentLimit(long documentLimit) {
        this.documentLimit = documentLimit;
    }

    public BigInteger getDataLimitAsBytes() {
        return dataLimitAsBytes;
    }

    public void setDataLimitAsBytes(BigInteger dataLimitAsBytes) {
        this.dataLimitAsBytes = dataLimitAsBytes;
    }

    /**
     * Convert the data limit in bytes to the unit set in the data limit unit.
     * @return
     */
    public BigDecimal getDataLimit() {
        if (dataLimitUnit == null) {
            // default to bytes
            dataLimitUnit = ProfileDataUnit.B;
            return new BigDecimal(dataLimitAsBytes);
        }
        return convertBytesToProfileDataUnit(dataLimitAsBytes, dataLimitUnit);
    }

    /**
     * Convert the value to bytes as per the unit.
     * @param value
     */
    public void setDataLimit(BigDecimal value) {
        if (dataLimitUnit == null) {
            // default to bytes
            dataLimitUnit = ProfileDataUnit.B;
            dataLimitAsBytes = value.toBigInteger();
        }
        dataLimitAsBytes = convertProfileDataUnitToBytes(value, dataLimitUnit);
    }

    /**
     * Convert the max file size in bytes to the unit set in the max file size.
     * @return
     */
    public BigDecimal getMaxFileSize() {
        if (maxFileSizeUnit == null) {
            // default to bytes
            maxFileSizeUnit = ProfileDataUnit.B;
            return new BigDecimal(maxFileSizeAsBytes);
        }
        return convertBytesToProfileDataUnit(maxFileSizeAsBytes, maxFileSizeUnit);
    }

    /**
     * Convert the value to bytes as per the unit.
     * @param value
     */
    public void setMaxFileSize(BigDecimal value) {
        if (maxFileSizeUnit == null) {
            // default to bytes
            maxFileSizeUnit = ProfileDataUnit.B;
            maxFileSizeAsBytes = value.toBigInteger();
        }
        maxFileSizeAsBytes = convertProfileDataUnitToBytes(value, maxFileSizeUnit);
    }

    private BigDecimal convertBytesToProfileDataUnit(BigInteger bytes, ProfileDataUnit unit) {
        if (unit.equals(ProfileDataUnit.B)) {
            return new BigDecimal(bytes);
        }
        if (unit.equals(ProfileDataUnit.KB)) {
            BigDecimal divisor = new BigDecimal(1024);
            return new BigDecimal(bytes).divide(divisor, 8, BigDecimal.ROUND_HALF_UP);
        }
        if (unit.equals(ProfileDataUnit.MB)) {
            BigDecimal divisor = new BigDecimal(1024).pow(2);
            return new BigDecimal(bytes).divide(divisor, 8, BigDecimal.ROUND_HALF_UP);
        }
        if (unit.equals(ProfileDataUnit.GB)) {
            BigDecimal divisor = new BigDecimal(1024).pow(3);
            return new BigDecimal(bytes).divide(divisor, 8, BigDecimal.ROUND_HALF_UP);
        }
        return new BigDecimal(bytes);
    }

    private BigInteger convertProfileDataUnitToBytes(BigDecimal value, ProfileDataUnit unit) {
        if (unit.equals(ProfileDataUnit.B)) {
            return value.toBigInteger();
        }
        if (unit.equals(ProfileDataUnit.KB)) {
            BigDecimal multiplier = new BigDecimal(1024);
            return value.multiply(multiplier).toBigInteger();
        }
        if (unit.equals(ProfileDataUnit.MB)) {
            BigDecimal multiplier = new BigDecimal(1024).pow(2);
            return value.multiply(multiplier).toBigInteger();
        }
        if (unit.equals(ProfileDataUnit.GB)) {
            BigDecimal multiplier = new BigDecimal(1024).pow(3);
            return value.multiply(multiplier).toBigInteger();
        }
        return value.toBigInteger();
    }

    public ProfileDataUnit getDataLimitUnit() {
        return dataLimitUnit;
    }

    public void setDataLimitUnit(ProfileDataUnit dataLimitUnit) {
        this.dataLimitUnit = dataLimitUnit;
    }

    public long getTimeLimitAsSeconds() {
        return timeLimitAsSeconds;
    }

    public void setTimeLimitAsSeconds(long timeLimitAsSeconds) {
        this.timeLimitAsSeconds = timeLimitAsSeconds;
    }

    public ProfileTimeUnit getTimeLimitUnit() {
        return timeLimitUnit;
    }

    public void setTimeLimitUnit(ProfileTimeUnit timeLimitUnit) {
        this.timeLimitUnit = timeLimitUnit;
    }

    public long getMaxPathDepth() {
        return maxPathDepth;
    }

    public void setMaxPathDepth(long maxPathDepth) {
        this.maxPathDepth = maxPathDepth;
    }

    public long getMaxHops() {
        return maxHops;
    }

    public void setMaxHops(long maxHops) {
        this.maxHops = maxHops;
    }

    public long getMaxTransitiveHops() {
        return maxTransitiveHops;
    }

    public void setMaxTransitiveHops(long maxTransitiveHops) {
        this.maxTransitiveHops = maxTransitiveHops;
    }

    public boolean isIgnoreRobotsTxt() {
        return ignoreRobotsTxt;
    }

    public void setIgnoreRobotsTxt(boolean ignoreRobotsTxt) {
        this.ignoreRobotsTxt = ignoreRobotsTxt;
    }

    public boolean isIgnoreCookies() {
        return ignoreCookies;
    }

    public void setIgnoreCookies(boolean ignoreCookies) {
        this.ignoreCookies = ignoreCookies;
    }

    public String getDefaultEncoding() {
        return defaultEncoding;
    }

    public void setDefaultEncoding(String defaultEncoding) {
        this.defaultEncoding = defaultEncoding;
    }

    public String getBlockURLs() {
        return convertStringListToString(blockURLsAsList);
    }

    public void setBlockURLs(String blockURLs) {
        convertStringToList(blockURLs, blockURLsAsList);
    }

    public List<String> getBlockURLsAsList() {
        return blockURLsAsList;
    }

    public void setBlockURLsAsList(List<String> blockURLsAsList) {
        this.blockURLsAsList = blockURLsAsList;
    }

    public String getIncludeURLs() {
        return convertStringListToString(includeURLsAsList);
    }

    public void setIncludeURLs(String includeURLs) {
        convertStringToList(includeURLs, includeURLsAsList);
    }

    public List<String> getIncludeURLsAsList() {
        return includeURLsAsList;
    }

    public void setIncludeURLsAsList(List<String> includeURLsAsList) {
        this.includeURLsAsList = includeURLsAsList;
    }

    public Writer getWriter() {
        return writer;
    }

    public void setWriter(Writer writer) {
        this.writer = writer;
    }

    public BigInteger getMaxFileSizeAsBytes() {
        return maxFileSizeAsBytes;
    }

    public void setMaxFileSizeAsBytes(BigInteger maxFileSizeAsBytes) {
        this.maxFileSizeAsBytes = maxFileSizeAsBytes;
    }

    public ProfileDataUnit getMaxFileSizeUnit() {
        return maxFileSizeUnit;
    }

    public void setMaxFileSizeUnit(ProfileDataUnit maxFileSizeUnit) {
        this.maxFileSizeUnit = maxFileSizeUnit;
    }

    public boolean isCompress() {
        return compress;
    }

    public void setCompress(boolean compress) {
        this.compress = compress;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getPoliteness() {
        return politeness;
    }

    public void setPoliteness(String politeness) {
        this.politeness = politeness;
    }

    private String convertStringListToString(List<String> stringList) {
        if (stringList.isEmpty()) {
            return "";
        } else {
            StringBuilder sb = new StringBuilder();
            for (String item : stringList) {
                sb.append(item);
                sb.append(LINE_SEPARATOR);
            }
            String convertedString = sb.toString();
            // Remove last separator
            convertedString = convertedString.substring(0, convertedString.length() - LINE_SEPARATOR.length());
            return convertedString;
        }
    }

    private void convertStringToList(String data, List<String> stringList) {
        // Re-init list
        stringList.clear();
        if (data != null && !data.isEmpty()) {
            // Split the string
            String[] temp = data.split(LINE_SEPARATOR);
            // Trim the data in temp
            for (String tString : temp) {
                stringList.add(tString.trim());
            }
        }
    }

    @Override
    public String toString() {
        return "Heritrix3ProfileOptions{" +
                "contactURL='" + contactURL + '\'' +
                ", documentLimit=" + documentLimit +
                ", dataLimitAsBytes=" + dataLimitAsBytes +
                ", dataLimitUnit=" + dataLimitUnit +
                ", timeLimitAsSeconds=" + timeLimitAsSeconds +
                ", timeLimitUnit=" + timeLimitUnit +
                ", maxPathDepth=" + maxPathDepth +
                ", maxHops=" + maxHops +
                ", maxTransitiveHops=" + maxTransitiveHops +
                ", ignoreRobotsTxt=" + ignoreRobotsTxt +
                ", ignoreCookies=" + ignoreCookies +
                ", defaultEncoding='" + defaultEncoding + '\'' +
                ", blockURLsAsList=" + blockURLsAsList +
                ", includeURLsAsList=" + includeURLsAsList +
                ", writer=" + writer +
                ", maxFileSizeAsBytes=" + maxFileSizeAsBytes +
                ", maxFileSizeUnit=" + maxFileSizeUnit +
                ", compress=" + compress +
                ", prefix='" + prefix + '\'' +
                ", politeness=" + politeness +
                '}';
    }
}
