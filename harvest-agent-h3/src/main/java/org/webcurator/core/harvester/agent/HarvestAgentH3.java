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
package org.webcurator.core.harvester.agent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.archive.util.FileUtils;
import org.netarchivesuite.heritrix3wrapper.ScriptResult;
import org.webcurator.core.common.Constants;
import org.webcurator.core.harvester.HarvesterType;
import org.webcurator.core.harvester.agent.exception.HarvestAgentException;
import org.webcurator.core.harvester.agent.filter.*;
import org.webcurator.core.harvester.agent.filter.FileFilter;
import org.webcurator.core.harvester.coordinator.HarvestAgentListener;
import org.webcurator.core.reader.LogProvider;
import org.webcurator.core.store.DigitalAssetStore;
import org.webcurator.domain.model.core.ArcHarvestResultDTO;
import org.webcurator.domain.model.core.LogFilePropertiesDTO;
import org.webcurator.domain.model.core.harvester.agent.HarvestAgentStatusDTO;
import org.webcurator.domain.model.core.harvester.agent.HarvesterStatusDTO;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * This is an Implementation of the HarvestAgent interface that uses Heritrix as the
 * engine to perform the harvesting of the web sites.
 *
 * @author nwaight
 */
public class HarvestAgentH3 extends AbstractHarvestAgent implements LogProvider {
    private static final String VALIDATING_JOB_NAME_PREFIX = "validateJob-";
    private static final String VALIDATING_JOB_DATE_FORMAT = "yyyy-MM-dd--HH-mm-ss.SSS";

    /**
     * The name of the profile file.
     */
    private static final String PROFILE_NAME = "crawler-beans.cxml";
    /**
     * The name of the base harvest directory.
     */
    private String baseHarvestDirectory = "";
    /**
     * the name of the harvest agent.
     */
    private String name = "";
    /**
     * the harvester type of the harvest agent.
     */
    private HarvesterType harvesterType;
    /**
     * the host name of the harvest agent.
     */
    private String host = "";
    /**
     * the harvest agent control port.
     */
    private int port = 0;
    /**
     * the harvest agent service endpoint.
     */
    private String service = "";
    /**
     * the harvest agent log reader service endpoint.
     */
    private String logReaderService = "";
    /**
     * the max number of harvests for this agent.
     */
    private int maxHarvests = 0;
    /**
     * the provenance note to use for a complete harvest.
     */
    private String provenanceNote = "";
    /**
     * the max number alerts that can occur for a harvest before a notification is sent.
     */
    private int alertThreshold = 0;
    /**
     * This list of allowed Agencies.
     */
    private ArrayList allowedAgencies = new ArrayList();
    /**
     * the interface to the digital asset store.
     */
    private DigitalAssetStore digitalAssetStore = null;
    /**
     * the interface to the WCT harvest coordinator.
     */
    private HarvestAgentListener harvestCoordinatorNotifier = null;

    /**
     * the logger.
     */
    private Log log;

    /**
     * Default Constructor.
     */
    public HarvestAgentH3() {
        super();
        harvesterType = HarvesterType.HERITRIX3;
        log = LogFactory.getLog(getClass());
    }

    /**
     * @see HarvestAgent#initiateHarvest(String, String, String).
     */
    public void initiateHarvest(String aJob, String aProfile, String aSeeds) {
        Harvester harvester = null;

        if (log.isDebugEnabled()) {
            log.debug("Initiating harvest for " + aJob + " " + aSeeds);
        }

        try {
            super.initiateHarvest(aJob, aProfile, aSeeds);
            //TODO - what to do with profile and seeds files when harvests aborted? Where are these files actually created?
            File profile = createProfile(aJob, aProfile);
            createSeedsFile(profile, aSeeds);
            harvester = getHarvester(aJob);
            harvester.start(profile, aJob);
            harvester.setAlertThreshold(alertThreshold);
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error("Failed to initiate harvest for " + aJob + " : " + e.getMessage(), e);
            }
            try {
                abort(aJob);
            } catch (Exception ex) {
                log.error("Failed to abort initilization of harvest for " + aJob + " : " + ex.getMessage(), ex);
            }

//            throw new HarvestAgentException("Failed to initiate harvest for " + aJob + " : " + e.getMessage(), e);
        }

        harvestCoordinatorNotifier.heartbeat(getStatus());
    }

    /**
     * Check existence of activeJobs within H3 instance.
     * If found attempt recovery.
     *
     * @see HarvestAgent#recoverHarvests(java.util.List).
     */
    public void recoverHarvests(List<String> activeCoreJobs) {
        try {
            if (!activeCoreJobs.isEmpty()) {
                log.info("Checking for harvests to recover. Active jobs received from Core: " + activeCoreJobs.size());
                Map<String, String> activeH3JobNames = getActiveH3Jobs();

                // Check this H3 instance has active jobs
                if (!activeH3JobNames.isEmpty()) {
                    // Look for matches between Core and H3 jobs
                    for (String jobName : activeCoreJobs) {
                        log.info("Checking job " + jobName + " for recovery");
                        if (activeH3JobNames.containsKey(jobName)) {
                            // then attempt to recover
                            Harvester harvester = null;
                            // get status of h3 job
                            String h3JobState = activeH3JobNames.get(jobName);
                            // If I want to include aborted Core jobs then activeCoreJobs needs to be a Map
                            // with name and status

                            if (h3JobState.equals("RUNNING") || h3JobState.equals("PAUSED") || h3JobState.equals("FINISHED")) {
                                log.info("Harves Agent recovering job " + jobName + " from H3 in state: " + h3JobState);
                                super.initiateHarvest(jobName, "", "");
                                harvester = getHarvester(jobName);
                                harvester.recover();
                                harvester.setAlertThreshold(alertThreshold);
                            }
                        }
                    }
                } else {
                    log.info("No harvests to recover from H3 instance.");
                }
            } else {
                log.info("No harvests to recover from Core.");
            }
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error("Failed to recover harvests: " + e.getMessage(), e);
            }
        }
    }

    /**
     * @see HarvestAgent#abort(String).
     */
    public void abort(String aJob) {
        if (log.isDebugEnabled()) {
            log.debug("Aborting harvest " + aJob);
        }
        Harvester harvester = getHarvester(aJob);
        if (harvester != null) {
            harvester.abort();
        }

//        tidy(aJob);
    }

    /**
     * Performs a clean up of a completed or aborted harvest.
     * The method attempts to deregister the Heritrix instance
     * from JMX, remove the instance from the Agents list of
     * harvesters and remove the temporary harvest directory.
     *
     * @param aJob the name of the harvest job to tidy
     * @param force Delete everything, even if an error has occurred
     */
    private void tidy(String aJob, boolean force) {
        log.info("About to perform tidy for job=" + aJob);
        File harvestDir = null;
        Harvester harvester = getHarvester(aJob);
        if (harvester != null) {
            // If build failed then we want to leave harvest dir there for troubleshooting
            if (force || (!force &&
                    !"Could not launch job - Fatal InitializationException".equals(harvester.getStatus().getStatus()))) {
                // Remove base dir of job
                if (harvester.getHarvestDir() != null) {
                    harvestDir = harvester.getHarvestDir();
                }
            }
            log.info("Deregistering harvester=" + harvester.getName());
            harvester.deregister();
        }

        log.info("Removing harvester=" + aJob);
        removeHarvester(aJob);

        if (harvestDir != null) {
            boolean deleted = FileUtils.deleteDir(harvestDir);
            if (deleted) {
                log.info("Deleted harvest directory=" + harvestDir.getAbsolutePath());
            } else {
                log.error("Unable to delete harvest directory " + harvestDir.getAbsolutePath());
            }
        }

        //TODO - at this point the harvester is gone, do we need the final stats here??
        harvestCoordinatorNotifier.heartbeat(getStatus());
    }

    /**
     * @see HarvestAgent#stop(String).
     */
    public void stop(String aJob) {
        if (log.isDebugEnabled()) {
            log.debug("Stopping harvest " + aJob);
        }
        Harvester harvester = getHarvester(aJob);
        harvester.stop();
    }

    private ArcHarvestResultDTO createIndex(String aJob) throws IOException {
        ArcHarvestResultDTO ahr = new ArcHarvestResultDTO();
        ahr.setCreationDate(new Date());
        return ahr;
    }


    private File[] getFileArray(File baseDir, FileFilter... filters) {
        return toFileArray(getFileList(baseDir, filters));
    }

    private List<File> getFileList(File baseDir, FileFilter... filters) {
        List<File> l = new LinkedList<File>();
        File[] files = baseDir.listFiles();
        //TODO - What if no warcs are generated? This throws null pointer otherwise
        for (File f : files) {
            for (FileFilter filter : filters) {
                if (filter.accepts(f)) {
                    l.add(f);
                    break;
                }
            }
        }
        return l;
    }

    private File[] toFileArray(List<File> files) {
        File[] fileArray = new File[files.size()];
        int i = 0;
        for (File file : files) {
            fileArray[i++] = file;
        }
        return fileArray;
    }

    private File[] getFileArray(List<File> baseDirs, FileFilter... filters) {
        return toFileArray(getFileList(baseDirs, filters));
    }

    private List<File> getFileList(List<File> baseDirs, FileFilter... filters) {
        List<File> results = new LinkedList<File>();
        for (File baseDir : baseDirs) {
            results.addAll(getFileList(baseDir, filters));
        }
        return results;
    }

    private boolean dirsExist(List<File> baseDirs) {
        boolean atleastOneDirExists = false;
        for (File baseDir : baseDirs) {
            if (baseDir.exists()) {
                atleastOneDirExists = true;
            }
        }
        return atleastOneDirExists;
    }


    /**
     * @see HarvestAgent#completeHarvest(String, int).
     */
    public int completeHarvest(String aJob, int aFailureStep) {
        Harvester harvester = getHarvester(aJob);

        log.info("Performing Harvest Completion for job " + aJob);

        //TODO - what does old heritrix do for this last heartbeat
        harvestCoordinatorNotifier.heartbeat(getStatus());

        //TODO  - if a harvest gets aborted/stopped before complete has finished, this will throw a null pointer exception
        // If aborted, tidy up and cancel.
        if (harvester.isAborted()) {
            tidy(aJob, false);
            return NO_FAILURES;
        }

        List das = getHarvester(aJob).getHarvestDigitalAssetsDirs();
        ArcHarvestResultDTO ahr = new ArcHarvestResultDTO();


        // Make sure that the files are not longer in use.
        if (aFailureStep == NO_FAILURES) {
            checkHarvesterFinishedWithDigitalAssets(das);
        }

        // Send the ARC files to the DAS.
        if (aFailureStep <= FAILED_ON_SEND_ARCS) {
            log.info("Getting digital assets to send to store for job " + aJob);

            try {
                // BE AWARE - if a harvest is stopped before any warcs are written then a null pointer exception can be thrown
                // when getting the file list of the warc dir, because the warc dir may not exist -causing an infinite loop.
                File[] fileList = getFileArray(das, new NegateFilter(new ExtensionFileFilter(Constants.EXTN_OPEN_ARC)));
                int numberOfFiles = fileList.length;

                for (int i = 0; i < numberOfFiles; i++) {
                    log.debug("Sending ARC " + (i + 1) + " of " + numberOfFiles + " to digital asset store for job " + aJob);
                    digitalAssetStore.save(aJob, fileList[i]);
                    log.debug("Finished sending ARC " + (i + 1) + " of " + numberOfFiles + " to digital asset store for job " + aJob);
                }

            } catch (Exception e) {
                if (dirsExist(das)) {
                    log.error("Failed to send harvest result to digital asset store for job " + aJob + ": " + e.getMessage(), e);
                } else {
                    log.error("Failed to find harvest path for job " + aJob + ": " + e.getMessage(), e);
                }

                return FAILED_ON_SEND_ARCS;
            }
        }

        // Send the log files to the DAS.
        if (aFailureStep <= FAILED_ON_SEND_LOGS) {
            try {
                File[] fileList = getFileArray(harvester.getHarvestLogDir(), NotEmptyFileFilter.notEmpty(new ExtensionFileFilter(Constants.EXTN_LOGS)));
                log.info("Sending harvest logs to digital asset store for job " + aJob);
                for (int i = 0; i < fileList.length; i++) {
                    digitalAssetStore.save(aJob, Constants.DIR_LOGS, fileList[i]);
                }
            } catch (Exception e) {
                if (log.isErrorEnabled()) {
                    log.error("Failed to send harvest logs to digital asset store for job " + aJob + ": " + e.getMessage(), e);
                }
                return FAILED_ON_SEND_LOGS;
            }
        }

        // Send the reports to the DAS.
        if (aFailureStep <= FAILED_ON_SEND_RPTS) {
            try {
                String harvestLogsDir = harvester.getHarvestLogDir().getParent();
                File reportsDir = new File(harvestLogsDir + File.separator + "reports");
                File[] fileList = getFileArray(reportsDir, NotEmptyFileFilter.notEmpty(new ExtensionFileFilter(Constants.EXTN_REPORTS)), NotEmptyFileFilter.notEmpty(new ExactNameFilter(PROFILE_NAME)));
                log.info("Sending harvest reports to digital asset store for job " + aJob);
                for (int i = 0; i < fileList.length; i++) {
                    digitalAssetStore.save(aJob, Constants.DIR_REPORTS, fileList[i]);
                }
            } catch (Exception e) {
                if (log.isErrorEnabled()) {
                    log.error("Failed to send harvest reports to digital asset store for job " + aJob + ": " + e.getMessage(), e);
                }
                return FAILED_ON_SEND_RPTS;
            }
        }

        // Send the result to the server.
        if (aFailureStep <= FAILED_ON_SEND_RESULT) {
            try {
                log.info("Sending harvest result to WCT for job " + aJob);
                ahr = new ArcHarvestResultDTO();
                ahr.setCreationDate(new Date());
                ahr.setTargetInstanceOid(new Long(aJob));
                ahr.setProvenanceNote(provenanceNote);
                harvestCoordinatorNotifier.harvestComplete(ahr);
            } catch (Exception e) {
                if (log.isErrorEnabled()) {
                    log.error("Failed to send harvest result for " + aJob + " to the WCT : " + e.getMessage(), e);
                }
                return FAILED_ON_SEND_RESULT;
            }
        }

        log.info("Cleaning up for job " + aJob);
        tidy(aJob, false);
        return NO_FAILURES;
    }


    /**
     * @see HarvestAgent#getStatus().
     */
    public HarvestAgentStatusDTO getStatus() {
        //TODO - might need adjustment for when harvest has stopped/gone
        HarvestAgentStatusDTO status = new HarvestAgentStatusDTO();
        status.setHost(host);
        status.setPort(port);
        status.setService(service);
        status.setLogReaderService(logReaderService);
        status.setName(name);
        status.setHarvesterType(harvesterType.name());
        status.setMaxHarvests(maxHarvests);
        status.setAllowedAgencies(allowedAgencies);
        status.setMemoryAvailable(Runtime.getRuntime().freeMemory() / 1024);
        status.setMemoryUsed((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024);
        status.setMemoryWarning(memoryWarning);

        double currentURIs = 0;
        double averageURIs = 0;
        double currentKBs = 0;
        double averageKBs = 0;
        long urlsDownloaded = 0;
        long urlsQueued = 0;
        long dataDownloaded = 0;

        HarvesterStatusDTO s = null;
        HashMap<String, HarvesterStatusDTO> hs = new HashMap<String, HarvesterStatusDTO>();
        Harvester harvester = null;
        Iterator it = harvesters.values().iterator();
        while (it.hasNext()) {
            harvester = (Harvester) it.next();

            // Exclude the H3 profile validation job
            if (!harvester.getName().startsWith(VALIDATING_JOB_NAME_PREFIX)) {
                hs.put(harvester.getName(), harvester.getStatus());

                s = harvester.getStatus();

                currentURIs += s.getCurrentURIs();
                averageURIs += s.getAverageURIs();
                currentKBs += s.getCurrentKBs();
                averageKBs += s.getAverageKBs();
                urlsDownloaded += s.getUrlsDownloaded();
                urlsQueued += s.getUrlsQueued();
                dataDownloaded += s.getDataDownloaded();
            }
        }

        status.setHarvesterStatus(hs);

        status.setCurrentURIs(currentURIs);
        status.setAverageURIs(averageURIs);
        status.setCurrentKBs(currentKBs);
        status.setAverageKBs(averageKBs);
        status.setUrlsDownloaded(urlsDownloaded);
        status.setUrlsQueued(urlsQueued);
        status.setDataDownloaded(dataDownloaded);

        return status;
    }

    /**
     * @see HarvestAgent#loadSettings(String).
     */
    public void loadSettings(String aJob) {
        Harvester harvester = getHarvester(aJob);
        harvester.getHarvestDigitalAssetsDirs();
        harvester.isHarvestCompressed();
        harvester.getHarvestDir();
        harvester.getHarvestLogDir();
    }

    /**
     * @see LogProvider#getLogFile(String, String)
     */
    public File getLogFile(String aJob, String aFileName) {
        File file = null;

        Harvester harvester = getHarvester(aJob);
        if (harvester != null) {
            File logsDir = harvester.getHarvestLogDir();
            file = new File(logsDir.getAbsolutePath() + File.separator + aFileName);

            if (!file.exists()) {
                return null;
            }
        }

        return file;
    }


    /**
     * @see LogProvider#getLogFileNames(String)
     */
    public List getLogFileNames(String aJob) {
        List<String> logFiles = new ArrayList<String>();

        Harvester harvester = getHarvester(aJob);
        File logsDir = harvester.getHarvestLogDir();
        File[] fileList = logsDir.listFiles();
        for (File f : fileList) {
            if (f.getName().endsWith(Constants.EXTN_LOGS)) {
                logFiles.add(f.getName());
            }
        }

        // Reports are stored in their own dir with H3, and are not written until harvest finished/stopped.
        File reportsDir = new File(logsDir.getPath() + File.separator + "reports");
        fileList = reportsDir.listFiles();
        if (fileList != null) {
            for (File f : fileList) {
                if (f.getName().endsWith(Constants.EXTN_REPORTS)) {
                    logFiles.add(f.getName());
                }
            }
        }

        return logFiles;
    }

    /**
     * @see LogProvider#getLogFileAttributes(String)
     */
    public LogFilePropertiesDTO[] getLogFileAttributes(String aJob) {
        List<LogFilePropertiesDTO> logFiles = new ArrayList<LogFilePropertiesDTO>();

        Harvester harvester = getHarvester(aJob);
        File logsDir = harvester.getHarvestLogDir();
        File[] fileList = logsDir.listFiles();
        for (File f : fileList) {
            if (f.getName().endsWith(Constants.EXTN_LOGS)) {
                LogFilePropertiesDTO lf = new LogFilePropertiesDTO();
                lf.setName(f.getName());
                lf.setPath(f.getAbsolutePath());
                lf.setLengthString(HarvesterStatusUtil.formatData(f.length()));
                lf.setLastModifiedDate(new Date(f.lastModified()));
                logFiles.add(lf);
            }
        }

        // Reports are stored in their own dir with H3
        File reportsDir = new File(logsDir.getPath() + File.separator + "reports");
        fileList = reportsDir.listFiles();
        if (fileList != null) {
            for (File f : fileList) {
                if (f.getName().endsWith(Constants.EXTN_REPORTS)) {
                    LogFilePropertiesDTO lf = new LogFilePropertiesDTO();
                    lf.setName(f.getName());
                    lf.setPath(f.getAbsolutePath());
                    lf.setLengthString(HarvesterStatusUtil.formatData(f.length()));
                    lf.setLastModifiedDate(new Date(f.lastModified()));
                    logFiles.add(lf);
                }
            }
        }

        LogFilePropertiesDTO[] result = new LogFilePropertiesDTO[logFiles.size()];
        int i = 0;
        for (LogFilePropertiesDTO r : logFiles) {
            result[i] = r;
            i++;
        }
        return result;
    }

    /**
     * @see HarvestAgent#pause(String)
     */
    public void pause(String aJob) {
        super.pause(aJob);
        harvestCoordinatorNotifier.heartbeat(getStatus());
    }

    /**
     * @see HarvestAgent#resume(String)
     */
    public void resume(String aJob) {
        super.resume(aJob);
        harvestCoordinatorNotifier.heartbeat(getStatus());
    }

    /**
     * @param aHost The host to set.
     */
    public void setHost(String aHost) {
        this.host = aHost;
    }

    public String getHost() {
        return host;
    }

    /**
     * @param aMaxHarvests The maxHarvests to set.
     */
    public void setMaxHarvests(int aMaxHarvests) {
        this.maxHarvests = aMaxHarvests;
    }

    /**
     * @param aName The name to set.
     */
    public void setName(String aName) {
        this.name = aName;
    }

    /**
     * @return Returns the harvester type.
     */
    public HarvesterType getHarvesterType() {
        return harvesterType;
    }

    /**
     * @param aPort The port to set.
     */
    public void setPort(int aPort) {
        this.port = aPort;
    }

    /**
     * @param aService The service to set.
     */
    public void setService(String aService) {
        this.service = aService;
    }

    /**
     * @param aLogReaderService The log reader service to set.
     */
    public void setLogReaderService(String aLogReaderService) {
        this.logReaderService = aLogReaderService;
    }

    /**
     * @return Returns the allowedAgencies.
     */
    public ArrayList getAllowedAgencies() {
        return allowedAgencies;
    }

    /**
     * @param aAllowedAgencies The allowedAgencies to set.
     */
    public void setAllowedAgencies(ArrayList aAllowedAgencies) {
        this.allowedAgencies = aAllowedAgencies;
    }

    /**
     * @param aDigitalAssetStore The digitalAssetStore to set.
     */
    public void setDigitalAssetStore(DigitalAssetStore aDigitalAssetStore) {
        this.digitalAssetStore = aDigitalAssetStore;
    }

    /**
     * @param harvestCoordinatorNotifier The harvestCoordinatorNotifier to set.
     */
    public void setHarvestCoordinatorNotifier(
            HarvestAgentListener harvestCoordinatorNotifier) {
        this.harvestCoordinatorNotifier = harvestCoordinatorNotifier;
    }

    /**
     * @param aBaseHarvestDirectory The baseHarvestDirectory to set.
     */
    public void setBaseHarvestDirectory(String aBaseHarvestDirectory) {
        this.baseHarvestDirectory = aBaseHarvestDirectory;
    }

    /**
     * @param provenanceNote The provenanceNote to set.
     */
    public void setProvenanceNote(String provenanceNote) {
        this.provenanceNote = provenanceNote;
    }

    /**
     * Create the profile for the job and return the profile <code>File</code>.
     *
     * @param aJob     the name of the job
     * @param aProfile the profile
     * @return the jobs profile file
     */
    private File createProfile(String aJob, String aProfile) {
        File dir = new File(baseHarvestDirectory + File.separator + aJob);
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                throw new HarvestAgentException("Failed to create the job directory " + dir.getAbsolutePath() + ".");
            }
        }

        File order = new File(dir.getAbsolutePath() + File.separator + PROFILE_NAME);

        try {
            if (order.exists()) {
                order.delete();
            }

            if (!order.createNewFile()) {
                throw new HarvestAgentException("Failed to create the job profile " + order.getAbsolutePath() + ".");
            }
        } catch (IOException e) {
            throw new HarvestAgentException("Failed while creating the job profile " + order.getAbsolutePath() + " " + e.getMessage(), e);
        }

        try {
            FileWriter writer = new FileWriter(order);
            writer.write(aProfile);
            writer.flush();
            writer.close();

            return order;
        } catch (IOException e) {
            throw new HarvestAgentException("Failed to write the job profile " + order.getAbsolutePath() + " " + e.getMessage(), e);
        }
    }

    /**
     * Create the seeds file for the harvest job.
     *
     * @param aProfile the profile the seeds are for
     * @param aSeeds   the seeds
     */
    private void createSeedsFile(File aProfile, String aSeeds) {
        try {
//            XMLSettingsHandler settings = new XMLSettingsHandler(aProfile);
//            settings.initialize();

//            CrawlerSettings cs = settings.getSettingsObject(null);
//            // We can use the crawl scope ATTR_NAME as all the scopes extend CrawlScope
//            CrawlScope scope = (CrawlScope) cs.getModule(CrawlScope.ATTR_NAME);

//            String seedsfile = (String) scope.getAttribute(CrawlScope.ATTR_SEEDS);
            String seedsFile = "seeds.txt";
            File seeds = new File(aProfile.getParent() + File.separator + seedsFile);

            try {
                //TODO determine why this file might exist (as sometimes it does) and fix it
                if (seeds.exists()) {
                    seeds.delete();
                }
                if (!seeds.createNewFile()) {
                    throw new HarvestAgentException("Failed to create the job seeds " + seeds.getAbsolutePath() + ".");
                }
            } catch (IOException e) {
                throw new HarvestAgentException("Failed to create the job seeds " + seeds.getAbsolutePath() + " " + e.getMessage(), e);
            }

            try {
                FileWriter writer = new FileWriter(seeds);
                writer.write(aSeeds);
                writer.flush();
                writer.close();
            } catch (IOException e) {
                throw new HarvestAgentException("Failed to write the job seeds " + seeds.getAbsolutePath() + " " + e.getMessage(), e);
            }
        } catch (Exception e) {
            throw new HarvestAgentException("Failed to create the seeds file " + e.getMessage(), e);
        }
    }

    /**
     * Check to see that the arc files are not still in the open state.
     *
     * @param das the list of directories to check
     */
    private void checkHarvesterFinishedWithDigitalAssets(List das) {
        File dir = null;
        File[] fileList = null;
        Iterator it = null;
        boolean found = false;
        boolean finished = false;
        int checkCount = 0;

        while (!finished && checkCount <= 10) {
            found = false;
            it = das.iterator();
            while (it.hasNext()) {
                dir = (File) it.next();
                // Sometimes warc dir might not exist if harvest stopped before any warcs generated.
                if (dir.exists()) {
                    fileList = dir.listFiles();
                    for (File f : fileList) {
                        if (f.getName().endsWith(Constants.EXTN_OPEN_ARC)) {
                            found = true;
                        }
                    }
                }
            }

            if (!found) {
                finished = true;
            } else {
                try {
                    if (log.isDebugEnabled()) {
                        log.debug("Snoozing to ensure that the harvester has finished with the arcs and logs");
                    }
                    Thread.sleep(1000);
                    checkCount++;
                } catch (InterruptedException e) {
                    if (log.isDebugEnabled()) {
                        log.debug("Interupted Excption occurred during sleep " + e.getMessage());
                    }
                }
            }
        }
    }

    /**
     * @param alertThreshold the alertThreshold to set
     */
    public void setAlertThreshold(int alertThreshold) {
        this.alertThreshold = alertThreshold;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @see HarvestAgent#updateProfileOverrides(String, String)
     */
    public void updateProfileOverrides(String aJob, String aProfile) {
        if (log.isDebugEnabled()) {
            log.debug("updating profile overrides for " + aJob);
        }
        //TODO - as is this is now redundant as the profile is copied over to H3 job dir.
        //TODO - updating the profile is possible with H3, but would require more work so out of scope for now.

        createProfile(aJob, aProfile);
    }

    /**
     * @see HarvestAgent#purgeAbortedTargetInstances(String[]).
     */
    public void purgeAbortedTargetInstances(String[] targetInstanceNames) {

        if (null == targetInstanceNames || targetInstanceNames.length == 0) {
            return;
        }

        try {
            for (String tiName : targetInstanceNames) {
                File toPurge = new File(baseHarvestDirectory, tiName);
                if (log.isDebugEnabled()) {
                    log.debug("About to purge aborted target instance dir " + toPurge.toString());
                }
                FileUtils.deleteDir(toPurge);
            }
        } catch (Exception e) {
            if (log.isDebugEnabled()) {
                log.debug("Failed to complete purge of aborted instance data: " + e.getMessage());
            }
        }

    }

    public Map<String, String> getActiveH3Jobs() {
        return HarvesterH3.getActiveH3JobNames();
    }


    /**
     * @see HarvestAgent#isValidProfile(String)
     */
    public boolean isValidProfile(String profile) {

        String jobName = generatedValidateJobName();

        boolean isValid;

        // create and build job
        super.initiateHarvest(jobName, null, null);
        HarvesterH3 harvester = (HarvesterH3) getHarvester(jobName);
        log.info("Validating profile, jobName=" + jobName);
        try {
            File profileFile = createProfile(jobName, profile);
            createSeedsFile(profileFile, "");
            harvester.build(profileFile, jobName);
            isValid = harvester.hasValidProfile();
            log.info("Validating profile, jobName=" + jobName + ", isValid=" + isValid);
        } catch (Exception e) {
            isValid = false;
            log.warn("Exception while validating profile: " + e.getMessage());
        }

        // clean-up
        tidy(jobName, true);

        return isValid;

    }

    /**
     * Execute the shell script in the Heritrix3 server for the job.
     * @param jobName the job
     * @param engine the script engine: beanshell, groovy, or nashorn (ECMAScript)
     * @param shellScript the script to execute
     * @return the script result
     */
    public HarvestAgentScriptResult executeShellScript(String jobName, String engine, String shellScript) {
        Harvester harvester = new HarvesterH3();
        ScriptResult scriptResult = harvester.executeShellScript(jobName, engine, shellScript);
        if (scriptResult != null) {
            String output = scriptResult.script != null ? scriptResult.script.rawOutput : "";
            return new HarvestAgentScriptResult(scriptResult.responseCode, scriptResult.status, output);
        } else {
            return new HarvestAgentScriptResult();
        }
    }

    private String generatedValidateJobName() {
        StringBuilder jobNameBuilder = new StringBuilder(VALIDATING_JOB_NAME_PREFIX);
        DateFormat dateFormat = new SimpleDateFormat(VALIDATING_JOB_DATE_FORMAT);
        dateFormat.format(new Date());
        jobNameBuilder.append(dateFormat.format(new Date()));
        return jobNameBuilder.toString();
    }
}
