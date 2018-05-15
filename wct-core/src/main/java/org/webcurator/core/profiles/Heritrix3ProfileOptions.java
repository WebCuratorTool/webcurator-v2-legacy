package org.webcurator.core.profiles;

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
    private long dataLimit;
    private ProfileDataUnit dataLimitUnit;
    private long timeLimit;
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
    private long maxFileSize;
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

    public long getDataLimit() {
        return dataLimit;
    }

    public void setDataLimit(long dataLimit) {
        this.dataLimit = dataLimit;
    }

    public ProfileDataUnit getDataLimitUnit() {
        return dataLimitUnit;
    }

    public void setDataLimitUnit(ProfileDataUnit dataLimitUnit) {
        this.dataLimitUnit = dataLimitUnit;
    }

    public long getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(long timeLimit) {
        this.timeLimit = timeLimit;
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

    public long getMaxFileSize() {
        return maxFileSize;
    }

    public void setMaxFileSize(long maxFileSize) {
        this.maxFileSize = maxFileSize;
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
            System.out.println(stringList);
        }
    }

    @Override
    public String toString() {
        return "Heritrix3ProfileOptions{" +
                "contactURL='" + contactURL + '\'' +
                ", documentLimit=" + documentLimit +
                ", dataLimit=" + dataLimit +
                ", dataLimitUnit=" + dataLimitUnit +
                ", timeLimit=" + timeLimit +
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
                ", maxFileSize=" + maxFileSize +
                ", maxFileSizeUnit=" + maxFileSizeUnit +
                ", compress=" + compress +
                ", prefix='" + prefix + '\'' +
                ", politeness=" + politeness +
                '}';
    }
}
