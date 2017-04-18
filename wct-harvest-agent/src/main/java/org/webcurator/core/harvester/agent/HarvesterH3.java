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
import org.archive.crawler.Heritrix;
import org.archive.crawler.admin.CrawlJob;
import org.archive.crawler.admin.CrawlJobHandler;
import org.archive.crawler.admin.StatisticsTracker;
import org.archive.crawler.datamodel.CrawlOrder;
import org.archive.crawler.framework.StatisticsTracking;
import org.archive.crawler.frontier.BdbFrontier;
import org.archive.crawler.settings.CrawlerSettings;
import org.archive.crawler.settings.MapType;
import org.archive.crawler.settings.XMLSettingsHandler;
import org.archive.crawler.writer.ARCWriterProcessor;
import org.archive.crawler.writer.WARCWriterProcessor;
import org.netarchivesuite.heritrix3wrapper.jaxb.Job;
import org.webcurator.core.harvester.agent.exception.HarvesterException;
import org.webcurator.core.harvester.util.AlertLogger;
import org.webcurator.domain.model.core.harvester.agent.HarvesterStatusDTO;

import org.netarchivesuite.heritrix3wrapper.*;

import javax.management.Attribute;
import javax.management.InvalidAttributeValueException;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * The HarvesterH3 is an implementation of the harvester interface
 * that uses Heritrix v3.* (H3) as the engine to perform the harvest.
 * @author b.obrien
 */
public class HarvesterH3 implements Harvester {
    /** The name of the profile file. */
    private static final String PROFILE_NAME = "order.xml";
    /** The name of this harvester. */
    private String name = null;
    /** harvester. */
//    private Heritrix heritrix = null;
    private Heritrix3Wrapper heritrix = null;
    /** The current status of the Harvester. */
    private HarvesterStatusDTO status = null;
    /** the current active harvest. */
    private CrawlJob job = null;
    /** the list of directories that the arcs are in. */
    private List<File> harvestDigitalAssetsDirs = null;
    /** the harvest directory. */
    private File harvestDir = null;
    /** the harvest logs directory. */
    private File harvestLogsDir = null;
    /** the flag to indicate that the arc files are compressed. */
    private Boolean compressed = null;
    /** The logger for this class. */
    private Log log = null;
    /** flag to indicate that the stop is an abort .*/
    private boolean aborted = false;
    /** the alert threshold.*/
    private int alertThreshold = 0;
    /** flag to indicate that the alert threshold message has been sent .*/
    private boolean alertThresholdMsgSent = false;
    /** the logger for alerts from heritrix. */
    private AlertLogger alertLogger = null;

    /** init
     * HarvesterHeritrix Constructor.
     * @param aHarvesterName the name of this harvester
     */
    public HarvesterH3(String aHarvesterName) throws HarvesterException {
        super();
        log = LogFactory.getLog(HarvesterH3.class);

        name = aHarvesterName;

        String hostname = "localhost";
        int port = 8443;
        File keystoreFile = null;
        String keyStorePassword = "";
        String userName = "admin";
        String password = "admin";


        try {
            heritrix = Heritrix3Wrapper.getInstance(hostname, port, keystoreFile, keyStorePassword, userName, password);
        }
        catch (Exception e) {
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

    /** Default Constructor. */
    public HarvesterH3() throws HarvesterException {
        super();
        name = "HarvesterH3-" + System.currentTimeMillis();
        try {
            heritrix = Heritrix3Wrapper.getInstance("localhost", 8443, null, "", "admin", "admin");
        }
        catch (Exception e) {
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

    /** @see Harvester#getStatus(). */
    public HarvesterStatusDTO getStatus() {
    	if (log.isDebugEnabled()) {
    		log.debug("Getting current status for " + name);
    	}

        heritrix.rescanJobDirectory();
//        status.setHarvesterState(heritrix.getStatus());
        if (job != null) {
            status.setJobName(job.getJobName());
            status.setStatus(job.getStatus());

            StatisticsTracking st = job.getStatisticsTracking();
            if (st != null) {
            	if (st.getCrawlerTotalElapsedTime() > 0) {
	                status.setCurrentURIs(st.currentProcessedDocsPerSec());
	                status.setCurrentKBs(st.currentProcessedKBPerSec());
	                status.setAverageURIs(st.processedDocsPerSec());
	                status.setAverageKBs(st.processedKBPerSec());
	                status.setElapsedTime(st.getCrawlerTotalElapsedTime());
	                status.setDataDownloaded(st.totalBytesWritten());
	                if (st instanceof StatisticsTracker) {
	                    StatisticsTracker statsTrack = (StatisticsTracker) st;
	                    status.setUrlsDownloaded(statsTrack.successfullyFetchedCount());
	                    status.setUrlsQueued(statsTrack.queuedUriCount());
	                    status.setUrlsFailed(statsTrack.failedFetchAttempts());
	                }
            	}
            }
        }

        return status;
    }

    /** @see Harvester#getHarvestDigitalAssetsDirs(). */
    public List<File> getHarvestDigitalAssetsDirs() {
    	if (log.isDebugEnabled()) {
    		log.debug("Getting the digital asset directories for " + name);
    	}

        if (harvestDigitalAssetsDirs != null) {
            return harvestDigitalAssetsDirs;
        }

        XMLSettingsHandler settings = getSettingsHandler();

        List<File> outputDirs = new ArrayList<File>();
        try {
            MapType writers = (MapType) settings.getOrder().getAttribute(CrawlOrder.ATTR_WRITE_PROCESSORS);
            Object obj = null;
            Iterator it = writers.iterator(null);

            while (it.hasNext()) {
                obj = it.next();
                if (obj instanceof ARCWriterProcessor) {
                    ARCWriterProcessor processor = (ARCWriterProcessor) obj;
                    outputDirs.addAll(processor.getOutputDirs());
                }

                if (obj instanceof WARCWriterProcessor) {
                    WARCWriterProcessor processor = (WARCWriterProcessor) obj;
                    outputDirs.addAll(processor.getOutputDirs());
                }
            }

            if (outputDirs != null && !outputDirs.isEmpty()) {
                harvestDigitalAssetsDirs = outputDirs;
            }

            return outputDirs;
        }
        catch (Exception e) {
        	if (log.isErrorEnabled()) {
        		log.error("Failed to get archive directories " + name + ": " + e.getMessage(), e);
        	}
            throw new HarvesterException("Failed to get archive directories " + name + ": " + e.getMessage(), e);
        }
    }

    /** @see Harvester#isHarvestCompressed(). */
    public boolean isHarvestCompressed() {
    	if (log.isDebugEnabled()) {
    		log.debug("Getting the harvest compressed flag for " + name);
    	}

        if (compressed != null) {
            return compressed.booleanValue();
        }

        XMLSettingsHandler settings = getSettingsHandler();

        try {
            MapType writers = (MapType) settings.getOrder().getAttribute(CrawlOrder.ATTR_WRITE_PROCESSORS);
            Object obj = null;
            Iterator it = writers.iterator(null);
            boolean found = false;
            while (it.hasNext()) {
                obj = it.next();
                if (obj instanceof ARCWriterProcessor) {
                	ARCWriterProcessor processor = (ARCWriterProcessor) obj;
                	compressed = new Boolean(processor.isCompressed());
                	found = true;
                	break;
                }
                if (obj instanceof WARCWriterProcessor) {
                	WARCWriterProcessor processor = (WARCWriterProcessor) obj;
                	compressed = new Boolean(processor.isCompressed());
                	found = true;
                	break;
                }
            }

            if(!found)
            {
	            if (log.isErrorEnabled()) {
	        		log.error("Failed to find ARCWriterProcessor or WARCWriterProcessor");
	        	}
	            throw new HarvesterException("Failed to find ARCWriterProcessor or WARCWriterProcessor");
            }
        }
        catch (Exception e) {
        	if (log.isErrorEnabled()) {
        		log.error("Failed to get compressed flag " + name + ": " + e.getMessage(), e);
        	}
            throw new HarvesterException("Failed to get compressed flag " + name + ": " + e.getMessage(), e);
        }

        return compressed;
    }

	/**
	 * @return
	 */
	private XMLSettingsHandler getSettingsHandler() {
		XMLSettingsHandler settings = job.getSettingsHandler();
        if (settings == null || settings.getOrder() == null) {
            File profile = new File(job.getDirectory() + File.separator + PROFILE_NAME);
            try {
                settings = new XMLSettingsHandler(profile);
                settings.initialize();
            }
            catch (InvalidAttributeValueException e) {
            	if (log.isErrorEnabled()) {
            		log.error("Failed to get settings for job " + name + ": " + e.getMessage(), e);
            	}
                throw new HarvesterException("Failed to get settings for job " + name + ": " + e.getMessage(), e);
            }
        }
		return settings;
	}

    /** @see Harvester#getHarvestLogDir(). */
    public File getHarvestLogDir() {
    	if (log.isDebugEnabled()) {
    		log.debug("Getting the harvest log directory for " + name);
    	}

        if (harvestLogsDir != null) {
            return harvestLogsDir;
        }

        XMLSettingsHandler settings = getSettingsHandler();
        CrawlOrder order = settings.getOrder();

        try {
            String subDirName = (String) order.getAttribute(CrawlOrder.ATTR_LOGS_PATH);
            File dir = getHarvestDir();

            if (dir != null) {
                harvestLogsDir = new File(dir.getAbsolutePath() + File.separator + subDirName);
                return harvestLogsDir;
            }
        }
        catch (Exception e) {
        	if (log.isErrorEnabled()) {
        		log.error("Failed to get log directory " + name + ": " + e.getMessage(), e);
        	}
            throw new HarvesterException("Failed to get log directory " + name + ": " + e.getMessage(), e);
        }

        return null;
    }

    /** @see Harvester#getHarvestDir(). */
    public File getHarvestDir() {
    	if (log.isDebugEnabled()) {
    		log.debug("Getting the harvest root directory for " + name);
    	}

        if (harvestDir != null) {
            return harvestDir;
        }

        if (job != null) {
            harvestDir = job.getDirectory();
            return job.getDirectory();
        }

        return null;
    }

    /** @see Harvester#pause(). */
    public void pause() {
        if (job != null && job.getStatus().equals(CrawlJob.STATUS_RUNNING)) {
            if (log.isDebugEnabled()) {
                log.debug("pausing job " + job.getJobName() + " on " + name);
            }
//            heritrix.getJobHandler().pauseJob();
            heritrix.rescanJobDirectory();

        }
    }

    /** @see Harvester#resume(). */
    public void resume() {
        if (job != null && job.getStatus().equals(CrawlJob.STATUS_PAUSED)) {
            if (log.isDebugEnabled()) {
                log.debug("resuming job " + job.getJobName() + " on " + name);
            }

            // Re-initialise the job as the profile overrides may have been changed.
            job.getSettingsHandler().initialize();
            job.kickUpdate();

//            heritrix.getJobHandler().resumeJob();
            heritrix.rescanJobDirectory();
        }
    }

    /** @see Harvester#restrictBandwidth(int). */
    public void restrictBandwidth(int aBandwidthLimit) {
        try {
        	XMLSettingsHandler settings = job.getSettingsHandler();
        	if (settings == null) {
        		if (log.isInfoEnabled()) {
        			log.info("Attempted to restrict bandwidth on " + name + ". No settings available.");
        		}
        		return;
        	}

        	CrawlerSettings cs = null;
            try {
				cs = settings.getSettingsObject(null);
			}
            catch (RuntimeException e) {
            	if (log.isInfoEnabled()) {
        			log.info("Attempted to restrict bandwidth on " + name + ". Failed to get Crawler Settings.");
        		}
        		return;
			}

            BdbFrontier frontier = (BdbFrontier) cs.getModule(BdbFrontier.ATTR_NAME);

            frontier.setAttribute(new Attribute(BdbFrontier.ATTR_MAX_OVERALL_BANDWIDTH_USAGE, new Integer(aBandwidthLimit)));
            settings.writeSettingsObject(cs);
            if (log.isDebugEnabled()) {
                log.debug("Attempting to restrict bandwidth on " + job.getDisplayName() + " to " + aBandwidthLimit);
            }

            // Check that the job is in a state where we can set the bandwidth
            if (CrawlJob.STATUS_ABORTED.equals(job.getStatus()) ||
                CrawlJob.STATUS_DELETED.equals(job.getStatus()) ||
                CrawlJob.STATUS_MISCONFIGURED.equals(job.getStatus()) ||
                job.getStatus().startsWith(CrawlJob.STATUS_FINISHED)) {
                if (log.isInfoEnabled()) {
                    log.info("Job " + job.getDisplayName() + " is in the state " + job.getStatus() + ". Ignoring bandwidth restriction.");
                }
                return;
            }

            while (!job.isRunning()) {
                Thread.sleep(1000);
                // Need to check again just incase the job has now failed/finished
                if (CrawlJob.STATUS_ABORTED.equals(job.getStatus()) ||
                    CrawlJob.STATUS_DELETED.equals(job.getStatus()) ||
                    CrawlJob.STATUS_MISCONFIGURED.equals(job.getStatus()) ||
                    job.getStatus().startsWith(CrawlJob.STATUS_FINISHED)) {
                    if (log.isInfoEnabled()) {
                        log.info("The Job " + job.getDisplayName() + " is in the state " + job.getStatus() + ". Ignoring bandwidth restriction.");
                    }
                    return;
                }
            }

            job.kickUpdate();
            if (log.isDebugEnabled()) {
                log.debug("Restricted the bandwidth for Job " + job.getDisplayName() + " to " + aBandwidthLimit + " KB.");
            }
        }
        catch (Exception e) {
        	if (log.isErrorEnabled()) {
        		log.error("Failed to restrict bandwidth " + name + ": " + e.getMessage(), e);
        	}
            throw new HarvesterException("Failed to restrict bandwidth " + name + ": " + e.getMessage(), e);
        }
    }

    /** @see Harvester#getName(). */
    public String getName() {
        return name;
    }

    /** @see Harvester#start(File, String). */
    public void start(File aProfile, String aJobName) throws HarvesterException {
        try {
            Job jobStatus = null;
            heritrix.waitForEngineReady(10, 1000);
            EngineResult er = heritrix.createNewJob(aJobName);
            jobStatus = heritrix.job(aJobName).job;
            if (log.isInfoEnabled()) {
                log.info("Launched harvester " + name);
            }

            //TODO - update cxml file and build
            if(jobStatus.statusDescription.equals("Unbuilt")){
                // Update cxml file
                String destDirPath = jobStatus.primaryConfig.replace("" + File.separator + "crawler-beans.cxml", "");
                File srcFile = aProfile.getAbsoluteFile();
                File destDir = new File(destDirPath);
                Heritrix3Wrapper.copyFileAs(srcFile, destDir, "crawler-beans.cxml");
                // Build H3 job
                jobStatus = heritrix.buildJobConfiguration(aJobName).job;
            }
            else{
                log.error("Failed to start harvester " + name + ": Could not build H3 job.");
                throw new HarvesterException("Failed to start harvester " + name + ": Could not build H3 job.");
            }



            //TODO - if built successfully then launch
            if(jobStatus.statusDescription.equals("Ready")){
                jobStatus = heritrix.launchJob(aJobName).job;
                jobStatus = heritrix.waitForJobState(aJobName, Heritrix3Wrapper.CrawlControllerState.PAUSED, 5, 1000).job;
            }
            else{
                log.error("Failed to start harvester " + name + ": Could not launch H3 job.");
                throw new HarvesterException("Failed to start harvester " + name + ": Could not launch H3 job.");
            }

            //TODO  - if launched and paused, one last status check, then unpause
            if(jobStatus.statusDescription.equals("Active: PAUSED")){
                jobStatus = heritrix.unpauseJob(aJobName).job;

            }
            else{
                log.error("Failed to start harvester " + name + ": Could not unpause and launch H3 job.");
                throw new HarvesterException("Failed to start harvester " + name + ": Could not unpause and launch H3 job.");
            }


            //TODO - HarvesterH3 will need to poll H3 instance regularly to get updates, status changes etc
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
            if(crawlStarted != null && crawlStarted.job.statusDescription.equals("Active: RUNNING")){
                started = true;
            }

        }
        catch (Exception e) {
        	if (log.isErrorEnabled()) {
        		log.error("Failed to start harvester " + name + ": " + e.getMessage(), e);
        	}

        	try {
				deregister();
			} catch (Throwable ex) {
				if (log.isWarnEnabled()) {
					log.warn("Failed to deregister harvester " + name + ": " + ex.getMessage(), ex);
				}
			}

            throw new HarvesterException("Failed to start harvester " + name + ": " + e.getMessage(), e);
        }
    }

    /** @see Harvester#stop(). */
    public void stop() {

        try{
            //TODO - get job
            Job preTeardownJob = heritrix.job("TI55555").job;
            Job postTeardownJob = null;
            String jobName = "TI55555";

            //TODO - if pausing or stopping then wait
            if(preTeardownJob.crawlControllerState.equals(Heritrix3Wrapper.CrawlControllerState.PAUSING)){
                heritrix.waitForJobState(jobName, Heritrix3Wrapper.CrawlControllerState.PAUSED, 50, 1000);

            }

            //TODO - if status is in (RUNNING, PAUSED)
            if(preTeardownJob.availableActions.contains("terminate")){
                postTeardownJob = heritrix.teardownJob(jobName).job;
                EngineResult er = heritrix.rescanJobDirectory();
            }




            if (job != null) {
                if (log.isDebugEnabled()) {
                    log.debug("stopping job " + job.getJobName() + " on " + name);
                }
    //            heritrix.getJobHandler().deleteJob(job.getUID());
//                heritrix.rescanJobDirectory();
            }

    //        heritrix.stopCrawling();
    //        if (log.isInfoEnabled()) {
    //            log.info("Stopped harvester " + name + " " + heritrix.getStatus());
    //        }

            if (aborted) {
                deregister();
            }
        } catch (UnsupportedEncodingException e) {
            if (log.isErrorEnabled()) {
                log.error("Failed to stop harvest " + name + ": " + e.getMessage(), e);
            }
        }
    }

    /** @see Harvester#abort(). */
    public void abort() {
        aborted = true;
        stop();
    }

    /** @see Harvester#isAborted(). */
    public boolean isAborted() {
        return aborted;
    }

    /** @see Harvester#deregister(). */
    public void deregister() {    	
//    	Heritrix.unregisterMBean(Heritrix.getMBeanServer(), heritrix.getMBeanName());
    }

	/**
	 * @param alertThreshold the alertThreshold to set
	 */
	public void setAlertThreshold(int alertThreshold) {
		this.alertThreshold = alertThreshold;
	}

    public static void main(String[] args) {
        Harvester H3 = new HarvesterH3();

        H3.start(new File("/crawler-beans.cxml"), "TI55555");

        H3.stop();
    }
}
