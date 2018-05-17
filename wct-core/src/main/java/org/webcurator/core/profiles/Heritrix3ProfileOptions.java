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
    private String userAgentTemplate;
    private long documentLimit;
    private BigInteger dataLimitAsBytes;
    private ProfileDataUnit dataLimitUnit;
    private BigInteger timeLimitAsSeconds;
    private ProfileTimeUnit timeLimitUnit;
    private long maxPathDepth;
    private long maxHops;
    private long maxTransitiveHops;
    private boolean ignoreRobotsTxt;
    private boolean ignoreCookies;
    private String defaultEncoding;
    private List<String> blockURLsAsList = new ArrayList<String>();
    private List<String> includeURLsAsList = new ArrayList<String>();
    private BigInteger maxFileSizeAsBytes;
    private ProfileDataUnit maxFileSizeUnit;
    private boolean compress;
    private String prefix;
    private PolitenessOptions politenessOptions;
    private static final String LINE_SEPARATOR = System.getProperty("line.separator");
    private static final int BIG_DECIMAL_SCALE = 8;
    private static final int BYTES_CONVERSION_FACTOR = 1024;
    private static final int SECONDS_PER_MINUTE = 60;
    private static final int MINUTES_PER_HOUR = 60;
    private static final int HOURS_PER_DAY = 24;
    private static final int DAYS_PER_WEEK = 7;
    private static final String CONTACT_URL_TEMPLATE = "@OPERATOR_CONTACT_URL@";

    public String getContactURL() {
        return contactURL;
    }

    public void setContactURL(String contactURL) {
        this.contactURL = contactURL;
    }

    public String getUserAgentTemplate() {
        return userAgentTemplate;
    }

    public void setUserAgentTemplate(String userAgentTemplate) {
        this.userAgentTemplate = userAgentTemplate;
    }

    /**
     * Converts the user agent template into the user agent.
     * @return
     */
    public String getUserAgent() {
        if (userAgentTemplate != null) {
            String result = userAgentTemplate.replace(CONTACT_URL_TEMPLATE + ")", "");
            return result;
        }
        return "";
    }

    /**
     * Converts the user agent into the user agent template.
     * Adds the CONTACT_URL_TEMPLATE - this **MUST** be in the user agent template otherwise the Heritrix job will not start in the server.
     * @param userAgent
     */
    public void setUserAgent(String userAgent) {
        String tempUserAgent = userAgent != null ? userAgent : "";
        if (tempUserAgent.contains(CONTACT_URL_TEMPLATE + ")")) {
            userAgentTemplate = tempUserAgent;
        } else {
            userAgentTemplate = tempUserAgent + CONTACT_URL_TEMPLATE + ")";
        }
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
            return new BigDecimal(dataLimitAsBytes).setScale(BIG_DECIMAL_SCALE, BigDecimal.ROUND_HALF_UP);
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
            dataLimitAsBytes = value.setScale(BIG_DECIMAL_SCALE, BigDecimal.ROUND_HALF_UP).toBigInteger();
        }
        dataLimitAsBytes = convertProfileDataUnitToBytes(value, dataLimitUnit);
    }

    /**
     * Convert the max file size in bytes to the unit set in the max file size unit.
     * @return
     */
    public BigDecimal getMaxFileSize() {
        if (maxFileSizeUnit == null) {
            // default to bytes
            maxFileSizeUnit = ProfileDataUnit.B;
            return new BigDecimal(maxFileSizeAsBytes).setScale(BIG_DECIMAL_SCALE, BigDecimal.ROUND_HALF_UP);
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
            maxFileSizeAsBytes = value.setScale(BIG_DECIMAL_SCALE, BigDecimal.ROUND_HALF_UP).toBigInteger();
        }
        maxFileSizeAsBytes = convertProfileDataUnitToBytes(value, maxFileSizeUnit);
    }

    /**
     * Convert the time limit in bytes to the unit set in the time limit unit.
     * @return
     */
    public BigDecimal getTimeLimit() {
        if (timeLimitUnit == null) {
            // default to seconds
            timeLimitUnit = ProfileTimeUnit.SECOND;
            return new BigDecimal(timeLimitAsSeconds).setScale(BIG_DECIMAL_SCALE, BigDecimal.ROUND_HALF_UP);
        }
        return convertSecondsToProfileTimeUnit(timeLimitAsSeconds, timeLimitUnit);
    }

    /**
     * Convert the value to seconds as per the unit.
     * @param value
     */
    public void setTimeLimit(BigDecimal value) {
        if (timeLimitUnit == null) {
            // default to seconds
            timeLimitUnit = ProfileTimeUnit.SECOND;
            timeLimitAsSeconds = value.setScale(BIG_DECIMAL_SCALE, BigDecimal.ROUND_HALF_UP).toBigInteger();
        }
        timeLimitAsSeconds = convertProfileTimeUnitToSeconds(value, timeLimitUnit);
    }

    private BigDecimal convertBytesToProfileDataUnit(BigInteger bytes, ProfileDataUnit unit) {
        BigDecimal factor = createProfileDataUnitFactor(unit);
        return new BigDecimal(bytes).divide(factor, BIG_DECIMAL_SCALE, BigDecimal.ROUND_HALF_UP).setScale(BIG_DECIMAL_SCALE, BigDecimal.ROUND_HALF_UP);
    }

    private BigDecimal convertSecondsToProfileTimeUnit(BigInteger seconds, ProfileTimeUnit unit) {
        BigDecimal factor = createProfileTimeUnitFactor(unit);
        return new BigDecimal(seconds).divide(factor, BIG_DECIMAL_SCALE, BigDecimal.ROUND_HALF_UP).setScale(BIG_DECIMAL_SCALE, BigDecimal.ROUND_HALF_UP);
    }

    private BigInteger convertProfileDataUnitToBytes(BigDecimal value, ProfileDataUnit unit) {
        BigDecimal factor = createProfileDataUnitFactor(unit);
        return value.multiply(factor).setScale(BIG_DECIMAL_SCALE, BigDecimal.ROUND_HALF_UP).toBigInteger();
    }

    private BigInteger convertProfileTimeUnitToSeconds(BigDecimal value, ProfileTimeUnit unit) {
        BigDecimal factor = createProfileTimeUnitFactor(unit);
        return value.multiply(factor).setScale(BIG_DECIMAL_SCALE, BigDecimal.ROUND_HALF_UP).toBigInteger();
    }

    private BigDecimal createProfileDataUnitFactor(ProfileDataUnit unit) {
        BigDecimal factor = BigDecimal.ONE;
        if (unit.equals(ProfileDataUnit.KB)) {
            factor = new BigDecimal(BYTES_CONVERSION_FACTOR);
        }
        if (unit.equals(ProfileDataUnit.MB)) {
            factor = new BigDecimal(BYTES_CONVERSION_FACTOR).pow(2);
        }
        if (unit.equals(ProfileDataUnit.GB)) {
            factor = new BigDecimal(BYTES_CONVERSION_FACTOR).pow(3);
        }
        return factor;
    }

    private BigDecimal createProfileTimeUnitFactor(ProfileTimeUnit unit) {
        BigDecimal factor = BigDecimal.ONE;
        if (unit.equals(ProfileTimeUnit.MINUTE)) {
            factor = new BigDecimal(SECONDS_PER_MINUTE);
        }
        if (unit.equals(ProfileTimeUnit.HOUR)) {
            factor = new BigDecimal(SECONDS_PER_MINUTE).multiply(new BigDecimal(MINUTES_PER_HOUR));
        }
        if (unit.equals(ProfileTimeUnit.DAY)) {
            factor = new BigDecimal(SECONDS_PER_MINUTE).multiply(new BigDecimal(MINUTES_PER_HOUR)).multiply(new BigDecimal(HOURS_PER_DAY));
        }
        if (unit.equals(ProfileTimeUnit.WEEK)) {
            factor = new BigDecimal(SECONDS_PER_MINUTE).multiply(new BigDecimal(MINUTES_PER_HOUR)).multiply(new BigDecimal(HOURS_PER_DAY)).multiply(new BigDecimal(DAYS_PER_WEEK));
        }
        return factor;
    }

    public ProfileDataUnit getDataLimitUnit() {
        return dataLimitUnit;
    }

    public void setDataLimitUnit(ProfileDataUnit dataLimitUnit) {
        this.dataLimitUnit = dataLimitUnit;
    }

    public BigInteger getTimeLimitAsSeconds() {
        return timeLimitAsSeconds;
    }

    public void setTimeLimitAsSeconds(BigInteger timeLimitAsSeconds) {
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

    public PolitenessOptions getPolitenessOptions() {
        return politenessOptions;
    }

    public void setPolitenessOptions(PolitenessOptions politenessOptions) {
        this.politenessOptions = politenessOptions;
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
                ", userAgentTemplate='" + userAgentTemplate + '\'' +
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
                ", maxFileSizeAsBytes=" + maxFileSizeAsBytes +
                ", maxFileSizeUnit=" + maxFileSizeUnit +
                ", compress=" + compress +
                ", prefix='" + prefix + '\'' +
                ", politenessOptions=" + politenessOptions +
                '}';
    }
}
