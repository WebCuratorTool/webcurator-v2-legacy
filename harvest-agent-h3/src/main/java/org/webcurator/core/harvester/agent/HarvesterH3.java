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
import org.archive.crawler.admin.CrawlJob;
import org.archive.crawler.settings.XMLSettingsHandler;
import org.netarchivesuite.heritrix3wrapper.jaxb.ConfigFile;
import org.netarchivesuite.heritrix3wrapper.jaxb.Engine;
import org.netarchivesuite.heritrix3wrapper.jaxb.Job;
import org.netarchivesuite.heritrix3wrapper.jaxb.JobShort;
import org.webcurator.core.harvester.agent.exception.HarvesterException;
import org.webcurator.core.harvester.util.AlertLogger;
import org.webcurator.domain.model.core.harvester.agent.HarvesterStatusDTO;

import org.netarchivesuite.heritrix3wrapper.*;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * The HarvesterH3 is an implementation of the harvester interface
 * that uses Heritrix v3.* (H3) as the engine to perform the harvest.
 *
 * @author b.obrien
 */
public class HarvesterH3 implements Harvester {
    /**
     * The name of the profile file.
     */
    private static final String PROFILE_NAME = "order.xml";
    /**
     * The name of this harvester.
     */
    private String name = null;
    /**
     * harvester.
     */
//    private Heritrix heritrix = null;
    private Heritrix3Wrapper heritrix = null;
    /**
     * The current status of the Harvester.
     */
    private HarvesterStatusDTO status = null;
    /**
     * the current active harvest.
     */
    private CrawlJob job = null;
    private Job h3job = null;
    /**
     * the list of directories that the arcs are in.
     */
    private List<File> harvestDigitalAssetsDirs = null;
    /**
     * the harvest directory.
     */
    private File harvestDir = null;
    /**
     * the harvest logs directory.
     */
    private File harvestLogsDir = null;
    /**
     * the flag to indicate that the arc files are compressed.
     */
    private Boolean compressed = null;
    /**
     * The logger for this class.
     */
    private Log log = null;
    /**
     * flag to indicate that the stop is an abort .
     */
    private boolean aborted = false;
    /**
     * the alert threshold.
     */
    private int alertThreshold = 0;
    /**
     * flag to indicate that the alert threshold message has been sent .
     */
    private boolean alertThresholdMsgSent = false;
    /**
     * the logger for alerts from heritrix.
     */
    private AlertLogger alertLogger = null;

    /**
     * init
     * HarvesterHeritrix Constructor.
     *
     * @param aHarvesterName the name of this harvester
     */
    public HarvesterH3(String aHarvesterName) throws HarvesterException {
        super();
        log = LogFactory.getLog(HarvesterH3.class);
        name = aHarvesterName;

        try {
            heritrix = getH3WrapperInstance();
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error("Failed to create an instance of H3 " + e.getMessage(), e);
            }
            throw new HarvesterException("Failed to create an instance of H3 " + e.getMessage(), e);
        }

        if (log.isDebugEnabled()) {
            log.debug("Created new harvester " + aHarvesterName);
        }
        status = new HarvesterStatusDTO(name);
    }

    /**
     * Default Constructor.
     */
    public HarvesterH3() throws HarvesterException {
        super();
        name = "HarvesterH3-" + System.currentTimeMillis();
        try {
            heritrix = getH3WrapperInstance();
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error("Failed to create an instance of H3 " + e.getMessage(), e);
            }
            throw new HarvesterException("Failed to create an instance of H3 " + e.getMessage(), e);
        }
        log = LogFactory.getLog(HarvesterH3.class);
        if (log.isDebugEnabled()) {
            log.debug("Created new harvester " + name);
        }
        status = new HarvesterStatusDTO(name);
    }

    private static Heritrix3Wrapper getH3WrapperInstance() {
        String hostname = "localhost";
        int port = 8443;
        File keystoreFile = null;
        String keyStorePassword = "";
        String userName = "admin";
        String password = "admin";

        return Heritrix3Wrapper.getInstance(hostname, port, keystoreFile, keyStorePassword, userName, password);
    }

    public static Map<String, String> getActiveH3JobNames() {
        Heritrix3Wrapper h3 = getH3WrapperInstance();

        EngineResult engineResult = h3.rescanJobDirectory();
        Engine engine = engineResult.engine;
        Map<String, String> activeH3JobNames = new HashMap<>();
        for (JobShort job : engine.jobs) {
            if (job.crawlControllerState != null) {
                activeH3JobNames.put(job.shortName, job.crawlControllerState);
            }
        }

        return activeH3JobNames;

    }

    /**
     * @see Harvester#getStatus().
     */
    public HarvesterStatusDTO getStatus() {
        if (log.isDebugEnabled()) {
            log.debug("Getting current status for " + name);
        }

        if (h3job != null) {
            // Get update of job from H3 engine
            EngineResult engineResult = heritrix.rescanJobDirectory();
            h3job = heritrix.job(h3job.shortName).job;
            if (h3job != null) {

                status.setJobName(h3job.shortName);
                status.setStatus(h3job.crawlControllerState);
                String heritrixBuild = engineResult.engine.heritrixVersion;
                status.setHeritrixVersion("Heritrix " + heritrixBuild.substring(0, heritrixBuild.indexOf("-")));

                if (h3job.crawlControllerState != null) {

                    //TODO - these status conversions should be more generic, rather than using old heritrix 1.14.1 statuses
                    if (h3job.crawlControllerState.equals("NASCENT") && !h3job.isLaunchable) {
                        status.setStatus("Could not launch job - Fatal InitializationException");
                    } else if (h3job.crawlControllerState.equals("FINISHED") || h3job.crawlControllerState.equals("STOPPING")) {
                        status.setStatus("Finished");
                    }

                    if (h3job.elapsedReport.elapsedMilliseconds > 0) {
                        status.setCurrentURIs(h3job.rateReport.currentDocsPerSecond);
                        status.setCurrentKBs(h3job.rateReport.currentKiBPerSec);
                        status.setAverageURIs(h3job.rateReport.averageDocsPerSecond);
                        status.setAverageKBs(h3job.rateReport.averageKiBPerSec);
                        status.setElapsedTime(h3job.elapsedReport.elapsedMilliseconds);
                        status.setDataDownloaded(h3job.sizeTotalsReport.total);
                        status.setUrlsDownloaded(h3job.uriTotalsReport.downloadedUriCount);
                        status.setUrlsQueued(h3job.uriTotalsReport.queuedUriCount);
                        //TODO - failed URIs
                        //                    status.setUrlsFailed(statsTrack.failedFetchAttempts());
                    }
                } else {
                    for (String entry : h3job.jobLogTail) {
                        if (entry.contains("SEVERE Invalid property")) {
                            status.setStatus("Could not launch job - Fatal InitializationException");
                            break;
                        }
                    }
                }
            } else {
                //TODO - If job no longer exits in H3 engine, then maybe remove from this HarvestH3 object
                System.out.println("HarvesterH3 - job no longer exsts in H3 engine");
//            status = null;
            }
        }

        return status;
    }

    /**
     * @see Harvester#getHarvestDigitalAssetsDirs().
     */
    public List<File> getHarvestDigitalAssetsDirs() {
        if (log.isDebugEnabled()) {
            log.debug("Getting the digital asset directories for " + name);
        }

        if (harvestDigitalAssetsDirs != null) {
            return harvestDigitalAssetsDirs;
        }

        List<File> outputDirs = new ArrayList<File>();

        if (h3job != null) {

            try {
                String subDirName = "warcs";
                getHarvestDir();

                ConfigFile warcBase = null;
                if (h3job.configFiles != null) {
                    List<ConfigFile> configFiles = h3job.configFiles;
                    for (ConfigFile config : configFiles) {
                        if (config.key.equals("warcWriter.directory")) {
                            warcBase = config;
                            break;
                        }
                    }
                }

                if (warcBase != null) {
                    String harvestWarcsPath = warcBase.path;
                    outputDirs.add(new File(harvestWarcsPath + File.separator + subDirName));
                }

                if (outputDirs != null && !outputDirs.isEmpty()) {
                    harvestDigitalAssetsDirs = outputDirs;
                }

            } catch (Exception e) {
                if (log.isErrorEnabled()) {
                    log.error("Failed to get archive directories " + name + ": " + e.getMessage(), e);
                }
                throw new HarvesterException("Failed to get archive directories " + name + ": " + e.getMessage(), e);
            }
        }

        return outputDirs;
    }

    /**
     * @see Harvester#isHarvestCompressed().
     */
    public boolean isHarvestCompressed() {
        if (log.isDebugEnabled()) {
            log.debug("Getting the harvest compressed flag for " + name);
        }
        return false;
//        if (compressed != null) {
//            return compressed.booleanValue();
//        }
//
//        XMLSettingsHandler settings = getSettingsHandler();
//        //TODO - Look at using H3 scripting console to look at warc writer bean
//        try {
//            MapType writers = (MapType) settings.getOrder().getAttribute(CrawlOrder.ATTR_WRITE_PROCESSORS);
//            Object obj = null;
//            Iterator it = writers.iterator(null);
//            boolean found = false;
//            while (it.hasNext()) {
//                obj = it.next();
//                if (obj instanceof ARCWriterProcessor) {
//                	ARCWriterProcessor processor = (ARCWriterProcessor) obj;
//                	compressed = new Boolean(processor.isCompressed());
//                	found = true;
//                	break;
//                }
//                if (obj instanceof WARCWriterProcessor) {
//                	WARCWriterProcessor processor = (WARCWriterProcessor) obj;
//                	compressed = new Boolean(processor.isCompressed());
//                	found = true;
//                	break;
//                }
//            }
//
//            if(!found)
//            {
//	            if (log.isErrorEnabled()) {
//	        		log.error("Failed to find ARCWriterProcessor or WARCWriterProcessor");
//	        	}
//	            throw new HarvesterException("Failed to find ARCWriterProcessor or WARCWriterProcessor");
//            }
//        }
//        catch (Exception e) {
//        	if (log.isErrorEnabled()) {
//        		log.error("Failed to get compressed flag " + name + ": " + e.getMessage(), e);
//        	}
//            throw new HarvesterException("Failed to get compressed flag " + name + ": " + e.getMessage(), e);
//        }
//
//        return compressed;
    }

    /**
     * @return
     */
    private XMLSettingsHandler getSettingsHandler() {
//		XMLSettingsHandler settings = job.getSettingsHandler();
//        if (settings == null || settings.getOrder() == null) {
//            File profile = new File(job.getDirectory() + File.separator + PROFILE_NAME);
//            try {
//                settings = new XMLSettingsHandler(profile);
//                settings.initialize();
//            }
//            catch (InvalidAttributeValueException e) {
//            	if (log.isErrorEnabled()) {
//            		log.error("Failed to get settings for job " + name + ": " + e.getMessage(), e);
//            	}
//                throw new HarvesterException("Failed to get settings for job " + name + ": " + e.getMessage(), e);
//            }
//        }
//		return settings;
        return null;
    }

    /**
     * @see Harvester#getHarvestLogDir().
     */
    public File getHarvestLogDir() {
        if (log.isDebugEnabled()) {
            log.debug("Getting the harvest log directory for " + name);
        }

        if (harvestLogsDir != null) {
            return harvestLogsDir;
        }

        if (h3job != null) {
            try {
                getHarvestDir();
                ConfigFile logsBase = null;
                if (h3job.configFiles != null) {
                    List<ConfigFile> configFiles = h3job.configFiles;
                    for (ConfigFile config : configFiles) {
                        if (config.key.equals("loggerModule.path")) {
                            logsBase = config;
                            break;
                        }
                    }
                }

                if (logsBase != null) {
                    String harvestLogsPath = logsBase.path;
                    harvestLogsDir = new File(harvestLogsPath);
                    return harvestLogsDir;
                }
            } catch (Exception e) {
                if (log.isErrorEnabled()) {
                    log.error("Failed to get log directory " + name + ": " + e.getMessage(), e);
                }
                throw new HarvesterException("Failed to get log directory " + name + ": " + e.getMessage(), e);
            }
        }
        return null;
    }

    /**
     * @see Harvester#getHarvestDir().
     */
    public File getHarvestDir() {
        if (log.isDebugEnabled()) {
            log.debug("Getting the harvest root directory for " + name);
        }

        if (harvestDir != null) {
            return harvestDir;
        }

        if (h3job != null) {
            ConfigFile jobBase = null;
            if (h3job.configFiles != null) {
                List<ConfigFile> configFiles = h3job.configFiles;
                for (ConfigFile config : configFiles) {
                    if (config.name.equals("job base")) {
                        jobBase = config;
                        break;
                    }
                }
            }

            if (jobBase != null) {
                String harvestDirPath = jobBase.path;
                harvestDir = new File(harvestDirPath);
                return harvestDir;
            }

            if (h3job.primaryConfig != null) {
                String harvestDirPath = new File(h3job.primaryConfig).getParent();
                harvestDir = new File(harvestDirPath);
                return harvestDir;
            }

        }
        return null;
    }

    /**
     * @see Harvester#pause().
     */
    public void pause() {
        if (h3job != null && h3job.crawlControllerState.equals("RUNNING")) {
            if (log.isDebugEnabled()) {
                log.debug("pausing job " + h3job.shortName + " on " + name);
            }
            h3job = heritrix.pauseJob(h3job.shortName).job;
            heritrix.rescanJobDirectory();
        }
    }

    /**
     * @see Harvester#resume().
     */
    public void resume() {
        if (h3job != null && h3job.statusDescription.equals("Active: PAUSED")) {
            if (log.isDebugEnabled()) {
                log.debug("resuming job " + h3job.shortName + " on " + name);
            }

            // Re-initialise the job as the profile overrides may have been changed.
            //TODO - find our whether this is possible in H3???
//            job.getSettingsHandler().initialize();
//            job.kickUpdate();
            h3job = heritrix.unpauseJob(h3job.shortName).job;
        }
    }

    /**
     * @see Harvester#restrictBandwidth(int).
     */
    public void restrictBandwidth(int aBandwidthLimit) {
        //TODO - see if this can be done via the scripting console.
//        try {
//        	XMLSettingsHandler settings = job.getSettingsHandler();
//        	if (settings == null) {
//        		if (log.isInfoEnabled()) {
//        			log.info("Attempted to restrict bandwidth on " + name + ". No settings available.");
//        		}
//        		return;
//        	}
//
//        	CrawlerSettings cs = null;
//            try {
//				cs = settings.getSettingsObject(null);
//			}
//            catch (RuntimeException e) {
//            	if (log.isInfoEnabled()) {
//        			log.info("Attempted to restrict bandwidth on " + name + ". Failed to get Crawler Settings.");
//        		}
//        		return;
//			}
//
//            BdbFrontier frontier = (BdbFrontier) cs.getModule(BdbFrontier.ATTR_NAME);
//
//            frontier.setAttribute(new Attribute(BdbFrontier.ATTR_MAX_OVERALL_BANDWIDTH_USAGE, new Integer(aBandwidthLimit)));
//            settings.writeSettingsObject(cs);
//            if (log.isDebugEnabled()) {
//                log.debug("Attempting to restrict bandwidth on " + job.getDisplayName() + " to " + aBandwidthLimit);
//            }
//
//            // Check that the job is in a state where we can set the bandwidth
//            if (CrawlJob.STATUS_ABORTED.equals(job.getStatus()) ||
//                CrawlJob.STATUS_DELETED.equals(job.getStatus()) ||
//                CrawlJob.STATUS_MISCONFIGURED.equals(job.getStatus()) ||
//                job.getStatus().startsWith(CrawlJob.STATUS_FINISHED)) {
//                if (log.isInfoEnabled()) {
//                    log.info("Job " + job.getDisplayName() + " is in the state " + job.getStatus() + ". Ignoring bandwidth restriction.");
//                }
//                return;
//            }
//
//            while (!job.isRunning()) {
//                Thread.sleep(1000);
//                // Need to check again just incase the job has now failed/finished
//                if (CrawlJob.STATUS_ABORTED.equals(job.getStatus()) ||
//                    CrawlJob.STATUS_DELETED.equals(job.getStatus()) ||
//                    CrawlJob.STATUS_MISCONFIGURED.equals(job.getStatus()) ||
//                    job.getStatus().startsWith(CrawlJob.STATUS_FINISHED)) {
//                    if (log.isInfoEnabled()) {
//                        log.info("The Job " + job.getDisplayName() + " is in the state " + job.getStatus() + ". Ignoring bandwidth restriction.");
//                    }
//                    return;
//                }
//            }
//
//            job.kickUpdate();
//            if (log.isDebugEnabled()) {
//                log.debug("Restricted the bandwidth for Job " + job.getDisplayName() + " to " + aBandwidthLimit + " KB.");
//            }
//        }
//        catch (Exception e) {
//        	if (log.isErrorEnabled()) {
//        		log.error("Failed to restrict bandwidth " + name + ": " + e.getMessage(), e);
//        	}
//            throw new HarvesterException("Failed to restrict bandwidth " + name + ": " + e.getMessage(), e);
//        }
    }

    /**
     * @see Harvester#getName().
     */
    public String getName() {
        return name;
    }


    protected void build(File aProfile, String aJobName) throws Exception {
        Job jobStatus = null;
        heritrix.waitForEngineReady(10, 1000);
        EngineResult er = heritrix.createNewJob(aJobName);
        jobStatus = heritrix.job(aJobName).job;
        if (log.isInfoEnabled()) {
            log.info("Launched harvester " + name);
        }

        // Update cxml file and build
        if (jobStatus.statusDescription.equals("Unbuilt")) {
            // Update cxml file
            String destDir = jobStatus.primaryConfig.replace("" + File.separator + "crawler-beans.cxml", "");
            String srcDir = aProfile.getPath().substring(0, aProfile.getPath().lastIndexOf(File.separator));
            Heritrix3Wrapper.copyFileAs(aProfile, new File(destDir), "crawler-beans.cxml");
            // Update seeds file
            File srcSeedsFile = new File(srcDir + File.separator + "seeds.txt");
            Heritrix3Wrapper.copyFileAs(srcSeedsFile, new File(destDir), "seeds.txt");
            // Build H3 job
            log.info("Building H3 job " + aJobName + ".....");
            jobStatus = heritrix.buildJobConfiguration(aJobName).job;
            // Set h3Job now, in case of any errors we can still deregister
            h3job = jobStatus;
            getHarvestDir();
        } else {
            log.error("Failed to start harvester " + name + ": Could not build H3 job.");
            throw new HarvesterException("Failed to start harvester " + name + ": Could not build H3 job.");
        }

    }

    /**
     * @see Harvester#start(File, String).
     */
    public void start(File aProfile, String aJobName) throws HarvesterException {
        try {

            // First build the job
            build(aProfile, aJobName);

            Job jobStatus = h3job;

            // If built successfully then launch
            if (jobStatus.statusDescription.equals("Ready")) {
                log.info("Launching H3 job " + aJobName + ".....");
                heritrix.launchJob(aJobName);
                jobStatus = heritrix.waitForJobState(aJobName, Heritrix3Wrapper.CrawlControllerState.PAUSED, 5, 1000).job;
            } else {
                log.error("Failed to start harvester " + name + ": Could not launch H3 job.");
                throw new HarvesterException("Failed to start harvester " + name + ": Could not launch H3 job.");
            }

            // If launched and paused, one last status check, then unpause
            if (jobStatus.statusDescription.equals("Active: PAUSED")) {
                jobStatus = heritrix.unpauseJob(aJobName).job;

            } else {
                log.error("Failed to start harvester " + name + ": Could not unpause and launch H3 job.");
                throw new HarvesterException("Failed to start harvester " + name + ": Could not unpause and launch H3 job.");
            }


            //TODO - HarvesterH3 will need to poll H3 instance regularly to get updates, status changes etc
            h3job = jobStatus;
//            CrawlJob tmpJob = null;
//            CrawlJobHandler cjw = heritrix.getJobHandler();
//            Iterator it = cjw.getPendingJobs().iterator();
//            while (it.hasNext()) {
//                tmpJob = (CrawlJob) it.next();
//                if (tmpJob.getJobName().equals(aJobName)) {
//                    job = tmpJob;
//                    job.getSettingsHandler().initialize();
//                    if (log.isInfoEnabled()) {
//                        log.info("Found and initialised job " + job.getJobName());
//                    }
//
//                    HarvesterListener listener = new HarvesterListener();
//                    job.addNotificationListener(listener, null, aJobName);
//                }
//                else {
//                    if (log.isDebugEnabled()) {
//                        log.info("About to delete job " + tmpJob.getJobName());
//                    }
//                    cjw.deleteJob(tmpJob.getJobName());
//                }
//            }
//
//            heritrix.startCrawling();

            boolean started = false;
            if (log.isDebugEnabled()) {
                log.debug("Waiting for job to start.");
            }
            JobResult crawlStarted = heritrix.waitForJobState(aJobName, Heritrix3Wrapper.CrawlControllerState.RUNNING, 50, 1000);
            if (crawlStarted != null && crawlStarted.job.statusDescription.equals("Active: RUNNING")) {
                started = true;
                h3job = crawlStarted.job;
                log.info("H3 job " + aJobName + " has started.");
                // Pre-load dir vars
                getHarvestLogDir();
                getHarvestDigitalAssetsDirs();
            }

        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error("Failed to start harvester " + name + ": " + e.getMessage(), e);
            }

//        	try {
//				deregister();
//                h3job = null;
//			} catch (Throwable ex) {
//				if (log.isWarnEnabled()) {
//					log.warn("Failed to deregister harvester " + name + ": " + ex.getMessage(), ex);
//				}
//			}

            throw new HarvesterException("Failed to start harvester " + name + ": " + e.getMessage(), e);
        }
    }

    /**
     * @see Harvester#recover().
     */
    public void recover() {
        try {
            h3job = heritrix.job(name).job;
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error("Failed to recover harvester " + name + ": " + e.getMessage(), e);
            }
            h3job = null;
            throw new HarvesterException("Failed to recover harvester " + name + ": " + e.getMessage(), e);
        }
    }

    /**
     * @see Harvester#stop().
     */
    public void stop() {

        try {
            if (h3job != null) {

                // Get job
                String jobName = h3job.shortName;
                Job preTeardownJob = heritrix.job(jobName).job;
                Job postTeardownJob = null;

                // If pausing or stopping then wait
                if (preTeardownJob.crawlControllerState != null) {
                    if (preTeardownJob.crawlControllerState.equals(Heritrix3Wrapper.CrawlControllerState.PAUSING)) {
                        heritrix.waitForJobState(jobName, Heritrix3Wrapper.CrawlControllerState.PAUSED, 50, 1000);
                    }
                }

                // If status is in (RUNNING, PAUSED)
                if (preTeardownJob.availableActions.contains("terminate")) {
                    postTeardownJob = heritrix.terminateJob(jobName).job;
                    EngineResult er = heritrix.rescanJobDirectory();

                    if (log.isDebugEnabled()) {
                        log.debug("stopping job " + h3job.shortName + " on " + name);
                    }

                    //TODO - What to do with any data in preTeardownJob
                    h3job = postTeardownJob;
                }


//                if (aborted) {
//                    deregister();
//                }
            }


//            if (h3job != null) {

            //            heritrix.getJobHandler().deleteJob(job.getUID());
//                heritrix.rescanJobDirectory();
//            }

            //        heritrix.stopCrawling();
            //        if (log.isInfoEnabled()) {
            //            log.info("Stopped harvester " + name + " " + heritrix.getStatus());
            //        }


        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error("Failed to stop harvest " + name + ": " + e.getMessage(), e);
            }
            throw new HarvesterException("Failed to stop harvest " + name, e);
        }
    }

    /**
     * @see Harvester#abort().
     */
    public void abort() {
        aborted = true;
        stop();
    }

    /**
     * @see Harvester#isAborted().
     */
    public boolean isAborted() {
        return aborted;
    }

    /**
     * @see Harvester#deregister().
     */
    public void deregister() {
//    	Heritrix.unregisterMBean(Heritrix.getMBeanServer(), heritrix.getMBeanName());
        if (h3job != null) {

            // Get job
            String jobName = h3job.shortName;
            Job preTeardownJob = heritrix.job(jobName).job;
            Job postTeardownJob = null;

            // If job can be torn down
            //TODO - do we need to wait for job to finish terminating, if aborted
//            JobResult crawlStarted = heritrix.waitForJobState(jobName, Heritrix3Wrapper.CrawlControllerState.RUNNING, 50, 1000);
            if (preTeardownJob.availableActions.contains("teardown")) {
                postTeardownJob = heritrix.teardownJob(jobName).job;

                if (log.isDebugEnabled()) {
                    //TODO - some check to make sure teardown successful
//                    if(postTeardownJob.crawlExitStatus)
                    log.debug("H3 job " + h3job.shortName + " being torn down");
                }
//                h3job = null;
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
     * @return true iff this job has a valid profile
     */
    protected boolean hasValidProfile() {
        if (h3job != null) {
            heritrix.launchJob(h3job.shortName);
            try {
                heritrix.waitForJobState(h3job.shortName, Heritrix3Wrapper.CrawlControllerState.PAUSED, 10, 1000);
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
            h3job = heritrix.job(h3job.shortName).job;
            return h3job.statusDescription.equals("Active: PAUSED");
        } else {
            return false;
        }
    }

    public static void main(String[] args) {
        try {

            Harvester H3 = new HarvesterH3();

            // START
            H3.start(new File("/crawler-beans.cxml"), "TI55555");
            System.out.println("Started crawl");
            HarvesterStatusDTO statusDTO = null;

            // Get Harvest log dir
            File logDir = H3.getHarvestLogDir();
            System.out.println("Log Dir check 1) " + logDir.getAbsolutePath());
            System.out.println("Retrieved harvest log dir");
            // PAUSE
            while (true) {
                statusDTO = H3.getStatus();
                Thread.sleep(2000);
                if (statusDTO.getStatus().equals(Heritrix3Wrapper.CrawlControllerState.RUNNING.toString())) {
                    H3.pause();
                    System.out.println("Pausing crawl");
                    break;
                }
            }

            // UNPAUSE
            while (true) {
                statusDTO = H3.getStatus();
                Thread.sleep(2000);
                if (statusDTO.getStatus().equals(Heritrix3Wrapper.CrawlControllerState.PAUSED.toString())) {
                    System.out.println("Paused crawl");
                    H3.resume();
                    System.out.println("Resuming crawl");
                    break;
                }
            }

            // Get Harvest log dir
            logDir = H3.getHarvestLogDir();
            System.out.println("Log Dir check 2) " + logDir.getAbsolutePath());
            List<File> assetsDir = H3.getHarvestDigitalAssetsDirs();
            System.out.println("Asset Dir check) " + assetsDir.get(0).getAbsolutePath());

            // STOP
            while (true) {
                statusDTO = H3.getStatus();
                Thread.sleep(2000);
                if (statusDTO.getStatus().equals(Heritrix3Wrapper.CrawlControllerState.RUNNING.toString())) {
                    H3.stop();
                    break;
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
