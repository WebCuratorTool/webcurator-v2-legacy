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
    private String jobName;
    private String description;
    private String userAgent;
    private long documentLimit;
    private long dataLimit;
    private FileSizeUnit dataLimitUnit;
    private long timeLimit;
    private long maxPathDepth;
    private long maxHops;
    private long maxTransitiveHops;
    private boolean ignoreRobotsTxt;
    private boolean ignoreCookies;
    private String defaultEncoding;
    private List<String> blockURL= new ArrayList<String>();
    private List<String> includeURL= new ArrayList<String>();
    private Writer writer;
    private long maxFileSize;
    private FileSizeUnit maxFileSizeUnit;
    private boolean compress;
    private String prefix;
    private Politeness politeness;

    public enum FileSizeUnit {
        KB, MB, GB
    }

    public enum Writer {
        WARC, ARC
    }

    public enum Politeness {
        POLITE, MEDIUM, AGRESSIVE
    }

    public String getContactURL() {
        return contactURL;
    }

    public void setContactURL(String contactURL) {
        this.contactURL = contactURL;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
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

    public FileSizeUnit getDataLimitUnit() {
        return dataLimitUnit;
    }

    public void setDataLimitUnit(FileSizeUnit dataLimitUnit) {
        this.dataLimitUnit = dataLimitUnit;
    }

    public long getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(long timeLimit) {
        this.timeLimit = timeLimit;
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

    public List<String> getBlockURL() {
        return blockURL;
    }

    public void setBlockURL(List<String> blockURL) {
        this.blockURL = blockURL;
    }

    public List<String> getIncludeURL() {
        return includeURL;
    }

    public void setIncludeURL(List<String> includeURL) {
        this.includeURL = includeURL;
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

    public FileSizeUnit getMaxFileSizeUnit() {
        return maxFileSizeUnit;
    }

    public void setMaxFileSizeUnit(FileSizeUnit maxFileSizeUnit) {
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

    public Politeness getPoliteness() {
        return politeness;
    }

    public void setPoliteness(Politeness politeness) {
        this.politeness = politeness;
    }

    @Override
    public String toString() {
        return "Heritrix3ProfileOptions{" +
                "contactURL='" + contactURL + '\'' +
                ", jobName=" + jobName +
                ", description=" + description +
                ", userAgent=" + userAgent +
                ", documentLimit=" + documentLimit +
                ", dataLimit=" + dataLimit +
                ", dataLimitUnit=" + dataLimitUnit +
                ", timeLimit=" + timeLimit +
                ", maxPathDepth=" + maxPathDepth +
                ", maxHops=" + maxHops +
                ", maxTransitiveHops=" + maxTransitiveHops +
                ", ignoreRobotsTxt=" + ignoreRobotsTxt +
                ", ignoreCookies=" + ignoreCookies +
                ", defaultEncoding='" + defaultEncoding + '\'' +
                ", blockURL=" + blockURL +
                ", includeURL=" + includeURL +
                ", writer=" + writer +
                ", maxFileSize=" + maxFileSize +
                ", maxFileSizeUnit=" + maxFileSizeUnit +
                ", compress=" + compress +
                ", prefix='" + prefix + '\'' +
                ", politeness=" + politeness +
                '}';
    }
}
