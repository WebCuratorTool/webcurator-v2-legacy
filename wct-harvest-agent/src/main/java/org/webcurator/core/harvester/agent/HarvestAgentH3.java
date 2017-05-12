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
import org.archive.crawler.framework.CrawlScope;
import org.archive.crawler.settings.CrawlerSettings;
import org.archive.crawler.settings.XMLSettingsHandler;
import org.archive.util.FileUtils;
import org.webcurator.core.common.Constants;
import org.webcurator.core.harvester.agent.exception.HarvestAgentException;
import org.webcurator.core.harvester.coordinator.HarvestAgentListener;
import org.webcurator.core.reader.LogProvider;
import org.webcurator.core.store.DigitalAssetStore;
import org.webcurator.domain.model.core.ArcHarvestResultDTO;
import org.webcurator.domain.model.core.LogFilePropertiesDTO;
import org.webcurator.domain.model.core.harvester.agent.HarvestAgentStatusDTO;
import org.webcurator.domain.model.core.harvester.agent.HarvesterStatusDTO;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * This is an Implementation of the HarvestAgent interface that uses Heritrix as the 
 * engine to perform the harvesting of the web sites.
 * @author nwaight
 */
public class HarvestAgentH3 extends AbstractHarvestAgent implements LogProvider {
    /** The name of the profile file. */
    private static final String PROFILE_NAME = "order.xml";
    /** The name of the base harvest directory. */
    private String baseHarvestDirectory = "";
    /** the name of the harvest agent. */
    private String name = "";
    /** the host name of the harvest agent. */
    private String host = "";
    /** the harvest agent control port. */
    private int port = 0;
    /** the max number of harvests for this agent. */
    private int maxHarvests = 0;
    /** the provenance note to use for a complete harvest. */
    private String provenanceNote = "";
    /** the max number alerts that can occur for a harvest before a notification is sent. */
    private int alertThreshold = 0;
    /** This list of allowed Agencies. */
    private ArrayList allowedAgencies = new ArrayList();
    /** the interface to the digital asset store. */
    private DigitalAssetStore digitalAssetStore = null;
    /** the interface to the WCT harvest coordinator. */
    private HarvestAgentListener harvestCoordinatorNotifier = null;

    /** the logger. */
    private Log log;

    /** Default Constructor. */
    public HarvestAgentH3() {
        super();
        log = LogFactory.getLog(getClass());
    }

    /** @see HarvestAgent#initiateHarvest(String, String, String). */
    public void initiateHarvest(String aJob, String aProfile, String aSeeds) {
        Harvester harvester = null;

        if (log.isDebugEnabled()) {
    		log.debug("Initiating harvest for " + aJob + " " + aSeeds);
    	}

        try {
            super.initiateHarvest(aJob, aProfile, aSeeds);

            File profile = createProfile(aJob, aProfile);
            createSeedsFile(profile, aSeeds);

            harvester = getHarvester(aJob);
            harvester.start(profile, aJob);
            harvester.setAlertThreshold(alertThreshold);
        }
        catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error("Failed to initiate harvest for " + aJob + " : " + e.getMessage(), e);
            }

            abort(aJob);

            throw new HarvestAgentException("Failed to initiate harvest for " + aJob + " : " + e.getMessage(), e);
        }

        harvestCoordinatorNotifier.heartbeat(getStatus());
    }

    /** @see HarvestAgent#abort(String). */
    public void abort(String aJob) {
    	if (log.isDebugEnabled()) {
    		log.debug("Aborting harvest " + aJob);
    	}
        Harvester harvester = getHarvester(aJob);
        if (harvester != null) {
            harvester.abort();
        }

        tidy(aJob);
    }

    /**
     * Performs a clean up of a completed or abored harvest.
     * The method attempts to deregister the Heritrix instance
     * from JMX, remove the instance from the Agents list of
     * harvesters and remove the temporary harvest directory.
     * @param aJob the name of the harvest job to tidy
     */
    private void tidy(String aJob) {
    	if (log.isDebugEnabled()) {
    		log.debug("About to perform tidy for " + aJob);
    	}
        File harvestDir = null;
        Harvester harvester = getHarvester(aJob);
        if (harvester != null) {
            harvestDir = harvester.getHarvestDir();
            harvester.deregister();
        }

        removeHarvester(aJob);

        if (harvestDir != null) {
            boolean deleted = FileUtils.deleteDir(harvestDir);
        	if (!deleted && log.isDebugEnabled()) {
        		log.debug("Unable to delete harvest directory "+harvestDir.getAbsolutePath());
        	}
        }

        harvestCoordinatorNotifier.heartbeat(getStatus());
    }

    /** @see HarvestAgent#stop(String). */
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
        for(File f: files) {
        	for(FileFilter filter : filters) {
        		if(filter.accepts(f)) {
        			l.add(f);
        			break;
        		}
        	}
        }
    	return l;
    }

    private File[] toFileArray(List<File> files) {
    	File[] fileArray = new File[files.size()];
    	int i=0;
    	for(File file : files ) {
    		fileArray[i++] = file;
    	}
    	return fileArray;
    }

    private File[] getFileArray(List<File> baseDirs, FileFilter... filters) {
    	return toFileArray(getFileList(baseDirs, filters));
    }

    private List<File> getFileList(List<File> baseDirs, FileFilter... filters) {
    	List<File> results = new LinkedList<File>();
    	for(File baseDir: baseDirs) {
    		results.addAll( getFileList(baseDir, filters));
    	}
    	return results;
    }




    /** @see HarvestAgent#completeHarvest(String, int). */
    public int completeHarvest(String aJob, int aFailureStep) {
        Harvester harvester = getHarvester(aJob);

        log.info("Performing Harvest Completion for job " + aJob);

        harvestCoordinatorNotifier.heartbeat(getStatus());

        // If aborted, tidy up and cancel.
        if (harvester.isAborted()) {
            tidy(aJob);
            return NO_FAILURES;
        }

        List das = getHarvester(aJob).getHarvestDigitalAssetsDirs();
        ArcHarvestResultDTO ahr = new ArcHarvestResultDTO();


        // Make sure that the files are not longer in use.
        if(aFailureStep == NO_FAILURES) {
        	checkHarvesterFinishedWithDigitalAssets(das);
        }

        // Send the ARC files to the DAS.
        if (aFailureStep <= FAILED_ON_SEND_ARCS) {
            log.debug("Getting digital assets to send to store for job " + aJob);

	        try {
	            File[] fileList = getFileArray( das, new NegateFilter(new ExtensionFileFilter(Constants.EXTN_OPEN_ARC)));
	            int numberOfFiles = fileList.length;

	            for(int i=0; i<numberOfFiles; i++) {
	            	log.debug("Sending ARC " + (i+1) + " of " + numberOfFiles + " to digital asset store for job " + aJob);
	            	digitalAssetStore.save(aJob, fileList[i]);
	            	log.debug("Finished sending ARC " + (i+1) + " of " + numberOfFiles + " to digital asset store for job " + aJob);
	            }
	        }
	        catch (Exception e) {
                log.error("Failed to send harvest result to digital asset store for job " + aJob + ": " + e.getMessage(), e);
	            return FAILED_ON_SEND_ARCS;
	        }
        }

        // Send the log files to the DAS.
        if (aFailureStep <= FAILED_ON_SEND_LOGS) {
	        try {
	            File[] fileList = getFileArray(harvester.getHarvestLogDir(), NotEmptyFileFilter.notEmpty(new ExtensionFileFilter(Constants.EXTN_LOGS)));
                log.debug("Sending harvest logs to digital asset store for job " + aJob);
                for(int i=0;i<fileList.length;i++) {
                	digitalAssetStore.save(aJob, Constants.DIR_LOGS, fileList[i]);
                }
	        }
	        catch (Exception e) {
	            if (log.isErrorEnabled()) {
	                log.error("Failed to send harvest logs to digital asset store for job " + aJob + ": " + e.getMessage(), e);
	            }
	            return FAILED_ON_SEND_LOGS;
	        }
        }

        // Send the reports to the DAS.
        if (aFailureStep <= FAILED_ON_SEND_RPTS) {
	        try {
	        	File[] fileList = getFileArray(harvester.getHarvestDir(), NotEmptyFileFilter.notEmpty(new ExtensionFileFilter(Constants.EXTN_REPORTS)), NotEmptyFileFilter.notEmpty(new ExactNameFilter(PROFILE_NAME)));
                log.debug("Sending harvest reports to digital asset store for job " + aJob);
                for(int i=0;i<fileList.length;i++) {
                	digitalAssetStore.save(aJob, Constants.DIR_REPORTS, fileList[i]);
                }
	        }
	        catch (Exception e) {
	            if (log.isErrorEnabled()) {
	                log.error("Failed to send harvest reports to digital asset store for job " + aJob + ": " + e.getMessage(), e);
	            }
	            return FAILED_ON_SEND_RPTS;
	        }
        }

        // Send the result to the server.
        if (aFailureStep <= FAILED_ON_SEND_RESULT) {
	        try {
                log.debug("Sending harvest result to WCT for job " + aJob);
                ahr = new ArcHarvestResultDTO();
                ahr.setCreationDate(new Date());
	            ahr.setTargetInstanceOid(new Long(aJob));
	            ahr.setProvenanceNote(provenanceNote);
	            harvestCoordinatorNotifier.harvestComplete(ahr);
	        }
	        catch (Exception e) {
	            if (log.isErrorEnabled()) {
	                log.error("Failed to send harvest result for " + aJob + " to the WCT : " + e.getMessage(), e);
	            }
	            return FAILED_ON_SEND_RESULT;
	        }
        }

        log.debug("Cleaning up for job " + aJob);
        tidy(aJob);
        return NO_FAILURES;
    }

    /** @see HarvestAgent#getStatus(). */
    public HarvestAgentStatusDTO getStatus() {
        HarvestAgentStatusDTO status = new HarvestAgentStatusDTO();
        status.setHost(host);
        status.setPort(port);
        status.setName(name);
        status.setMaxHarvests(maxHarvests);
        status.setAllowedAgencies(allowedAgencies);
        status.setMemoryAvailable(Runtime.getRuntime().freeMemory()/1024);
        status.setMemoryUsed((Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory())/1024);
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

    /** @see HarvestAgent#loadSettings(String). */
    public void loadSettings(String aJob) {
        Harvester harvester = getHarvester(aJob);
        harvester.getHarvestDigitalAssetsDirs();
        harvester.isHarvestCompressed();
        harvester.getHarvestDir();
        harvester.getHarvestLogDir();
    }

    /** @see LogProvider#getLogFile(String, String) */
	public File getLogFile(String aJob, String aFileName) {
		File file = null;

		Harvester harvester = getHarvester(aJob);
		if (harvester != null) {
			File logsDir = harvester.getHarvestLogDir();
			file = new File(logsDir.getAbsolutePath() + File.separator + aFileName);
			if (!file.exists()) {
				logsDir = harvester.getHarvestDir();
				file = new File(logsDir.getAbsolutePath() + File.separator + aFileName);
			}

			if (!file.exists()) {
				return null;
			}
		}

		return file;
	}

    /** @see LogProvider#getAQAFile(String, String) */
	public File getAQAFile(String aJob, String aFileName) {
		return null;
	}

	/** @see LogProvider#getLogFileNames(String) */
	public List getLogFileNames(String aJob) {
		List<String> logFiles = new ArrayList<String>();

		Harvester harvester = getHarvester(aJob);
        File logsDir = harvester.getHarvestLogDir();
        File[] fileList = logsDir.listFiles();
        for(File f: fileList) {
            if (f.getName().endsWith(Constants.EXTN_LOGS)) {
                logFiles.add(f.getName());
            }
        }

        logsDir = harvester.getHarvestDir();
        fileList = logsDir.listFiles();
        for(File f: fileList) {
            if (f.getName().endsWith(Constants.EXTN_REPORTS) || f.getName().equals(PROFILE_NAME)) {
                logFiles.add(f.getName());
            }
        }

		return logFiles;
	}

	/** @see LogProvider#getLogFileAttributes(String) */
	public LogFilePropertiesDTO[] getLogFileAttributes(String aJob) {
		List<LogFilePropertiesDTO> logFiles = new ArrayList<LogFilePropertiesDTO>();

		Harvester harvester = getHarvester(aJob);
        File logsDir = harvester.getHarvestLogDir();
        File[] fileList = logsDir.listFiles();
        for(File f: fileList) {
            if (f.getName().endsWith(Constants.EXTN_LOGS)) {
        		LogFilePropertiesDTO lf = new LogFilePropertiesDTO();
        		lf.setName(f.getName());
        		lf.setPath(f.getAbsolutePath());
        		lf.setLengthString(HarvesterStatusUtil.formatData(f.length()));
        		lf.setLastModifiedDate(new Date(f.lastModified()));
        		logFiles.add(lf);
            }
        }

        logsDir = harvester.getHarvestDir();
        fileList = logsDir.listFiles();
        for(File f: fileList) {
            if (f.getName().endsWith(Constants.EXTN_REPORTS) || f.getName().equals(PROFILE_NAME)) {
        		LogFilePropertiesDTO lf = new LogFilePropertiesDTO();
        		lf.setName(f.getName());
        		lf.setPath(f.getAbsolutePath());
        		lf.setLengthString(HarvesterStatusUtil.formatData(f.length()));
        		lf.setLastModifiedDate(new Date(f.lastModified()));
        		logFiles.add(lf);
            }
        }
        LogFilePropertiesDTO[] result = new LogFilePropertiesDTO[logFiles.size()];
        int i = 0;
        for(LogFilePropertiesDTO r: logFiles) {
        	result[i] = r; i++;
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
     * @param aPort The port to set.
     */
    public void setPort(int aPort) {
        this.port = aPort;
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
     * @param aJob the name of the job
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
        }
        catch (IOException e) {
            throw new HarvestAgentException("Failed while creating the job profile " + order.getAbsolutePath() + " " + e.getMessage(), e);
        }

        try {
            FileWriter writer = new FileWriter(order);
            writer.write(aProfile);
            writer.flush();
            writer.close();

            return order;
        }
        catch (IOException e) {
            throw new HarvestAgentException("Failed to write the job profile " + order.getAbsolutePath() + " " + e.getMessage(), e);
        }
    }

    /**
     * Create the seeds file for the harvest job.
     * @param aProfile the profile the seeds are for
     * @param aSeeds the seeds
     */
    private void createSeedsFile(File aProfile, String aSeeds) {
        try {
            XMLSettingsHandler settings = new XMLSettingsHandler(aProfile);
            settings.initialize();

            CrawlerSettings cs = settings.getSettingsObject(null);
            // We can use the crawl scope ATTR_NAME as all the scopes extend CrawlScope
            CrawlScope scope = (CrawlScope) cs.getModule(CrawlScope.ATTR_NAME);

            String seedsfile = (String) scope.getAttribute(CrawlScope.ATTR_SEEDS);
            File seeds = new File(aProfile.getParent() + File.separator + seedsfile);

            try {
            	//TODO determine why this file might exist (as sometimes it does) and fix it
                if (seeds.exists()) {
                	seeds.delete();
            	}
                if (!seeds.createNewFile()) {
                    throw new HarvestAgentException("Failed to create the job seeds " + seeds.getAbsolutePath() + ".");
                }
            }
            catch (IOException e) {
                throw new HarvestAgentException("Failed to create the job seeds " + seeds.getAbsolutePath() + " " + e.getMessage(), e);
            }

            try {
                FileWriter writer = new FileWriter(seeds);
                writer.write(aSeeds);
                writer.flush();
                writer.close();
            }
            catch (IOException e) {
                throw new HarvestAgentException("Failed to write the job seeds " + seeds.getAbsolutePath() + " " + e.getMessage(), e);
            }
        }
        catch (Exception e) {
            throw new HarvestAgentException("Failed to create the seeds file " + e.getMessage(), e);
        }
    }

    /**
     * Check to see that the arc files are not still in the open state.
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
                fileList = dir.listFiles();
                for(File f: fileList) {
                    if (f.getName().endsWith(Constants.EXTN_OPEN_ARC)) {
                        found = true;
                    }
                }
            }

            if (!found) {
                finished = true;
            }
            else {
                try {
                    if (log.isDebugEnabled()) {
                        log.debug("Snoozing to ensure that the harvester has finished with the arcs and logs");
                    }
                    Thread.sleep(1000);
                    checkCount++;
                }
                catch (InterruptedException e) {
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
		} 
		catch (Exception e) {
            if (log.isDebugEnabled()) {
                log.debug("Failed to complete purge of aborted instance data: " + e.getMessage());
            }
		}				
	}


    public static void main(String[] args) {

        HarvestAgentH3 ha = new HarvestAgentH3();

        ha.setBaseHarvestDirectory("C:\\wct\\");

        String profileText = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<!-- \n" +
                "  HERITRIX 3 CRAWL JOB CONFIGURATION FILE\n" +
                " -->\n" +
                "<beans xmlns=\"http://www.springframework.org/schema/beans\"\n" +
                "        xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                "        xmlns:context=\"http://www.springframework.org/schema/context\"\n" +
                "        xmlns:aop=\"http://www.springframework.org/schema/aop\"\n" +
                "        xmlns:tx=\"http://www.springframework.org/schema/tx\"\n" +
                "        xsi:schemaLocation=\"http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-3.0.xsd\n" +
                "           http://www.springframework.org/schema/aop http://www.springframework.org/schema/aop/spring-aop-3.0.xsd\n" +
                "           http://www.springframework.org/schema/tx http://www.springframework.org/schema/tx/spring-tx-3.0.xsd\n" +
                "           http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context-3.0.xsd\">\n" +
                " \n" +
                " <context:annotation-config/>\n" +
                "\n" +
                "<!-- \n" +
                "  OVERRIDES\n" +
                "   Values elsewhere in the configuration may be replaced ('overridden') \n" +
                "   by a Properties map declared in a PropertiesOverrideConfigurer, \n" +
                "   using a dotted-bean-path to address individual bean properties. \n" +
                "   This allows us to collect a few of the most-often changed values\n" +
                "   in an easy-to-edit format here at the beginning of the model\n" +
                "   configuration.    \n" +
                " -->\n" +
                " <!-- overrides from a text property list -->\n" +
                " <bean id=\"simpleOverrides\" class=\"org.springframework.beans.factory.config.PropertyOverrideConfigurer\">\n" +
                "  <property name=\"properties\">\n" +
                "   <value>\n" +
                "\n" +
                "metadata.operatorContactUrl=http://www.dia.govt.nz\n" +
                "metadata.jobName=basic\n" +
                "metadata.description=Basic crawl starting with useful defaults\n" +
                "\n" +
                "##..more?..##\n" +
                "   </value>\n" +
                "  </property>\n" +
                " </bean>\n" +
                "\n" +
                " <!-- overrides from declared <prop> elements, more easily allowing\n" +
                "      multiline values or even declared beans -->\n" +
                " <bean id=\"longerOverrides\" class=\"org.springframework.beans.factory.config.PropertyOverrideConfigurer\">\n" +
                "  <property name=\"properties\">\n" +
                "   <props>\n" +
                "    <prop key=\"seeds.textSource.value\">\n" +
                "\n" +
                "# URLS HERE\n" +
                "http://localhost:8080/nlnzwebarchive/wayback/\n" +
                "\n" +
                "    </prop>\n" +
                "   </props>\n" +
                "  </property>\n" +
                " </bean>\n" +
                "\n" +
                " <!-- CRAWL METADATA: including identification of crawler/operator -->\n" +
                " <bean id=\"metadata\" class=\"org.archive.modules.CrawlMetadata\" autowire=\"byName\">\n" +
                "       <property name=\"operatorContactUrl\" value=\"[see override above]\"/>\n" +
                "       <property name=\"jobName\" value=\"[see override above]\"/>\n" +
                "       <property name=\"description\" value=\"[see override above]\"/>\n" +
                "  <property name=\"robotsPolicyName\" value=\"ignore\"/>\n" +
                "  <!-- <property name=\"operator\" value=\"\"/> -->\n" +
                "  <!-- <property name=\"operatorFrom\" value=\"\"/> -->\n" +
                "  <!-- <property name=\"organization\" value=\"\"/> -->\n" +
                "  <!-- <property name=\"audience\" value=\"\"/> -->\n" +
                "  <!-- <property name=\"userAgentTemplate\" \n" +
                "         value=\"Mozilla/5.0 (compatible; heritrix/@VERSION@ +@OPERATOR_CONTACT_URL@)\"/> -->\n" +
                "       \n" +
                " </bean>\n" +
                " \n" +
                " <!-- SEEDS: crawl starting points \n" +
                "      ConfigString allows simple, inline specification of a moderate\n" +
                "      number of seeds; see below comment for example of using an\n" +
                "      arbitrarily-large external file. -->\n" +
                " <bean id=\"seeds\" class=\"org.archive.modules.seeds.TextSeedModule\">\n" +
                "     <property name=\"textSource\">\n" +
                "      <bean class=\"org.archive.spring.ConfigString\">\n" +
                "       <property name=\"value\">\n" +
                "        <value>\n" +
                "# [see override above]\n" +
                "        </value>\n" +
                "       </property>\n" +
                "      </bean>\n" +
                "     </property>\n" +
                "<!-- <property name='sourceTagSeeds' value='false'/> -->\n" +
                "<!-- <property name='blockAwaitingSeedLines' value='-1'/> -->\n" +
                " </bean>\n" +
                " \n" +
                " <!-- SEEDS ALTERNATE APPROACH: specifying external seeds.txt file in\n" +
                "      the job directory, similar to the H1 approach. \n" +
                "      Use either the above, or this, but not both. -->\n" +
                " <!-- \n" +
                " <bean id=\"seeds\" class=\"org.archive.modules.seeds.TextSeedModule\">\n" +
                "  <property name=\"textSource\">\n" +
                "   <bean class=\"org.archive.spring.ConfigFile\">\n" +
                "    <property name=\"path\" value=\"seeds.txt\" />\n" +
                "   </bean>\n" +
                "  </property>\n" +
                "  <property name='sourceTagSeeds' value='false'/>\n" +
                "  <property name='blockAwaitingSeedLines' value='-1'/>\n" +
                " </bean>\n" +
                "  -->\n" +
                " \n" +
                " <bean id=\"acceptSurts\" class=\"org.archive.modules.deciderules.surt.SurtPrefixedDecideRule\">\n" +
                "  <!-- <property name=\"decision\" value=\"ACCEPT\"/> -->\n" +
                "  <!-- <property name=\"seedsAsSurtPrefixes\" value=\"true\" /> -->\n" +
                "  <!-- <property name=\"alsoCheckVia\" value=\"false\" /> -->\n" +
                "  <!-- <property name=\"surtsSourceFile\" value=\"\" /> -->\n" +
                "  <!-- <property name=\"surtsDumpFile\" value=\"${launchId}/surts.dump\" /> -->\n" +
                "  <!-- <property name=\"surtsSource\">\n" +
                "        <bean class=\"org.archive.spring.ConfigString\">\n" +
                "         <property name=\"value\">\n" +
                "          <value>\n" +
                "           # example.com\n" +
                "           # http://www.example.edu/path1/\n" +
                "           # +http://(org,example,\n" +
                "          </value>\n" +
                "         </property> \n" +
                "        </bean>\n" +
                "       </property> -->\n" +
                " </bean>\n" +
                "\n" +
                " <!-- SCOPE: rules for which discovered URIs to crawl; order is very \n" +
                "      important because last decision returned other than 'NONE' wins. -->\n" +
                " <bean id=\"scope\" class=\"org.archive.modules.deciderules.DecideRuleSequence\">\n" +
                "  <!-- <property name=\"logToFile\" value=\"false\" /> -->\n" +
                "  <property name=\"rules\">\n" +
                "   <list>\n" +
                "    <!-- Begin by REJECTing all... -->\n" +
                "    <bean class=\"org.archive.modules.deciderules.RejectDecideRule\" />\n" +
                "    <!-- ...then ACCEPT those within configured/seed-implied SURT prefixes... -->\n" +
                "    <ref bean=\"acceptSurts\" />\n" +
                "    <!-- ...but REJECT those more than a configured link-hop-count from start... -->\n" +
                "    <bean class=\"org.archive.modules.deciderules.TooManyHopsDecideRule\">\n" +
                "     <!-- <property name=\"maxHops\" value=\"20\" /> -->\n" +
                "    </bean>\n" +
                "    <!-- ...but ACCEPT those more than a configured link-hop-count from start... -->\n" +
                "    <bean class=\"org.archive.modules.deciderules.TransclusionDecideRule\">\n" +
                "     <!-- <property name=\"maxTransHops\" value=\"2\" /> -->\n" +
                "     <!-- <property name=\"maxSpeculativeHops\" value=\"1\" /> -->\n" +
                "    </bean>\n" +
                "    <!-- ...but REJECT those from a configurable (initially empty) set of REJECT SURTs... -->\n" +
                "    <bean class=\"org.archive.modules.deciderules.surt.SurtPrefixedDecideRule\">\n" +
                "          <property name=\"decision\" value=\"REJECT\"/>\n" +
                "          <property name=\"seedsAsSurtPrefixes\" value=\"false\"/>\n" +
                "          <property name=\"surtsDumpFile\" value=\"${launchId}/negative-surts.dump\" /> \n" +
                "     <!-- <property name=\"surtsSource\">\n" +
                "           <bean class=\"org.archive.spring.ConfigFile\">\n" +
                "            <property name=\"path\" value=\"negative-surts.txt\" />\n" +
                "           </bean>\n" +
                "          </property> -->\n" +
                "    </bean>\n" +
                "    <!-- ...and REJECT those from a configurable (initially empty) set of URI regexes... -->\n" +
                "    <bean class=\"org.archive.modules.deciderules.MatchesListRegexDecideRule\">\n" +
                "          <property name=\"decision\" value=\"REJECT\"/>\n" +
                "     <!-- <property name=\"listLogicalOr\" value=\"true\" /> -->\n" +
                "     <!-- <property name=\"regexList\">\n" +
                "           <list>\n" +
                "           </list>\n" +
                "          </property> -->\n" +
                "    </bean>\n" +
                "    <!-- ...and REJECT those with suspicious repeating path-segments... -->\n" +
                "    <bean class=\"org.archive.modules.deciderules.PathologicalPathDecideRule\">\n" +
                "     <!-- <property name=\"maxRepetitions\" value=\"2\" /> -->\n" +
                "    </bean>\n" +
                "    <!-- ...and REJECT those with more than threshold number of path-segments... -->\n" +
                "    <bean class=\"org.archive.modules.deciderules.TooManyPathSegmentsDecideRule\">\n" +
                "     <!-- <property name=\"maxPathDepth\" value=\"20\" /> -->\n" +
                "    </bean>\n" +
                "    <!-- ...but always ACCEPT those marked as prerequisitee for another URI... -->\n" +
                "    <bean class=\"org.archive.modules.deciderules.PrerequisiteAcceptDecideRule\">\n" +
                "    </bean>\n" +
                "    <!-- ...but always REJECT those with unsupported URI schemes -->\n" +
                "    <bean class=\"org.archive.modules.deciderules.SchemeNotInSetDecideRule\">\n" +
                "    </bean>\n" +
                "   </list>\n" +
                "  </property>\n" +
                " </bean>\n" +
                " \n" +
                " <!-- \n" +
                "   PROCESSING CHAINS\n" +
                "    Much of the crawler's work is specified by the sequential \n" +
                "    application of swappable Processor modules. These Processors\n" +
                "    are collected into three 'chains'. The CandidateChain is applied \n" +
                "    to URIs being considered for inclusion, before a URI is enqueued\n" +
                "    for collection. The FetchChain is applied to URIs when their \n" +
                "    turn for collection comes up. The DispositionChain is applied \n" +
                "    after a URI is fetched and analyzed/link-extracted.\n" +
                "  -->\n" +
                "  \n" +
                " <!-- CANDIDATE CHAIN --> \n" +
                " <!-- first, processors are declared as top-level named beans -->\n" +
                " <bean id=\"candidateScoper\" class=\"org.archive.crawler.prefetch.CandidateScoper\">\n" +
                " </bean>\n" +
                " <bean id=\"preparer\" class=\"org.archive.crawler.prefetch.FrontierPreparer\">\n" +
                "  <!-- <property name=\"preferenceDepthHops\" value=\"-1\" /> -->\n" +
                "  <!-- <property name=\"preferenceEmbedHops\" value=\"1\" /> -->\n" +
                "  <!-- <property name=\"canonicalizationPolicy\"> \n" +
                "        <ref bean=\"canonicalizationPolicy\" />\n" +
                "       </property> -->\n" +
                "  <!-- <property name=\"queueAssignmentPolicy\"> \n" +
                "        <ref bean=\"queueAssignmentPolicy\" />\n" +
                "       </property> -->\n" +
                "  <!-- <property name=\"uriPrecedencePolicy\"> \n" +
                "        <ref bean=\"uriPrecedencePolicy\" />\n" +
                "       </property> -->\n" +
                "  <!-- <property name=\"costAssignmentPolicy\"> \n" +
                "        <ref bean=\"costAssignmentPolicy\" />\n" +
                "       </property> -->\n" +
                " </bean>\n" +
                " <!-- now, processors are assembled into ordered CandidateChain bean -->\n" +
                " <bean id=\"candidateProcessors\" class=\"org.archive.modules.CandidateChain\">\n" +
                "  <property name=\"processors\">\n" +
                "   <list>\n" +
                "    <!-- apply scoping rules to each individual candidate URI... -->\n" +
                "    <ref bean=\"candidateScoper\"/>\n" +
                "    <!-- ...then prepare those ACCEPTed to be enqueued to frontier. -->\n" +
                "    <ref bean=\"preparer\"/>\n" +
                "   </list>\n" +
                "  </property>\n" +
                " </bean>\n" +
                "  \n" +
                " <!-- FETCH CHAIN --> \n" +
                " <!-- first, processors are declared as top-level named beans -->\n" +
                " <bean id=\"preselector\" class=\"org.archive.crawler.prefetch.Preselector\">\n" +
                "  <!-- <property name=\"recheckScope\" value=\"false\" /> -->\n" +
                "  <!-- <property name=\"blockAll\" value=\"false\" /> -->\n" +
                "  <!-- <property name=\"blockByRegex\" value=\"\" /> -->\n" +
                "  <!-- <property name=\"allowByRegex\" value=\"\" /> -->\n" +
                " </bean>\n" +
                " <bean id=\"preconditions\" class=\"org.archive.crawler.prefetch.PreconditionEnforcer\">\n" +
                "  <!-- <property name=\"ipValidityDurationSeconds\" value=\"21600\" /> -->\n" +
                "  <!-- <property name=\"robotsValidityDurationSeconds\" value=\"86400\" /> -->\n" +
                "  <!-- <property name=\"calculateRobotsOnly\" value=\"false\" /> -->\n" +
                " </bean>\n" +
                " <bean id=\"fetchDns\" class=\"org.archive.modules.fetcher.FetchDNS\">\n" +
                "  <!-- <property name=\"acceptNonDnsResolves\" value=\"false\" /> -->\n" +
                "  <!-- <property name=\"digestContent\" value=\"true\" /> -->\n" +
                "  <!-- <property name=\"digestAlgorithm\" value=\"sha1\" /> -->\n" +
                " </bean>\n" +
                " <!-- <bean id=\"fetchWhois\" class=\"org.archive.modules.fetcher.FetchWhois\">\n" +
                "       <property name=\"specialQueryTemplates\">\n" +
                "        <map>\n" +
                "         <entry key=\"whois.verisign-grs.com\" value=\"domain %s\" />\n" +
                "         <entry key=\"whois.arin.net\" value=\"z + %s\" />\n" +
                "         <entry key=\"whois.denic.de\" value=\"-T dn %s\" />\n" +
                "        </map>\n" +
                "       </property> \n" +
                "      </bean> -->\n" +
                " <bean id=\"fetchHttp\" class=\"org.archive.modules.fetcher.FetchHTTP\">\n" +
                "  <!-- <property name=\"useHTTP11\" value=\"false\" /> -->\n" +
                "  <!-- <property name=\"maxLengthBytes\" value=\"0\" /> -->\n" +
                "  <!-- <property name=\"timeoutSeconds\" value=\"1200\" /> -->\n" +
                "  <!-- <property name=\"maxFetchKBSec\" value=\"0\" /> -->\n" +
                "  <!-- <property name=\"defaultEncoding\" value=\"ISO-8859-1\" /> -->\n" +
                "  <!-- <property name=\"shouldFetchBodyRule\"> \n" +
                "        <bean class=\"org.archive.modules.deciderules.AcceptDecideRule\"/>\n" +
                "       </property> -->\n" +
                "  <!-- <property name=\"soTimeoutMs\" value=\"20000\" /> -->\n" +
                "  <!-- <property name=\"sendIfModifiedSince\" value=\"true\" /> -->\n" +
                "  <!-- <property name=\"sendIfNoneMatch\" value=\"true\" /> -->\n" +
                "  <!-- <property name=\"sendConnectionClose\" value=\"true\" /> -->\n" +
                "  <!-- <property name=\"sendReferer\" value=\"true\" /> -->\n" +
                "  <!-- <property name=\"sendRange\" value=\"false\" /> -->\n" +
                "  <!-- <property name=\"ignoreCookies\" value=\"false\" /> -->\n" +
                "  <!-- <property name=\"sslTrustLevel\" value=\"OPEN\" /> -->\n" +
                "  <!-- <property name=\"acceptHeaders\"> \n" +
                "        <list>\n" +
                "         <value>Accept: text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8</value>\n" +
                "        </list>\n" +
                "       </property>\n" +
                "  -->\n" +
                "  <!-- <property name=\"httpBindAddress\" value=\"\" /> -->\n" +
                "  <!-- <property name=\"httpProxyHost\" value=\"\" /> -->\n" +
                "  <!-- <property name=\"httpProxyPort\" value=\"0\" /> -->\n" +
                "  <!-- <property name=\"httpProxyUser\" value=\"\" /> -->\n" +
                "  <!-- <property name=\"httpProxyPassword\" value=\"\" /> -->\n" +
                "  <!-- <property name=\"digestContent\" value=\"true\" /> -->\n" +
                "  <!-- <property name=\"digestAlgorithm\" value=\"sha1\" /> -->\n" +
                " </bean>\n" +
                " <bean id=\"extractorHttp\" class=\"org.archive.modules.extractor.ExtractorHTTP\">\n" +
                " </bean>\n" +
                " <bean id=\"extractorHtml\" class=\"org.archive.modules.extractor.ExtractorHTML\">\n" +
                "  <!-- <property name=\"extractJavascript\" value=\"true\" /> -->\n" +
                "  <!-- <property name=\"extractValueAttributes\" value=\"true\" /> -->\n" +
                "  <!-- <property name=\"ignoreFormActionUrls\" value=\"false\" /> -->\n" +
                "  <!-- <property name=\"extractOnlyFormGets\" value=\"true\" /> -->\n" +
                "  <!-- <property name=\"treatFramesAsEmbedLinks\" value=\"true\" /> -->\n" +
                "  <!-- <property name=\"ignoreUnexpectedHtml\" value=\"true\" /> -->\n" +
                "  <!-- <property name=\"maxElementLength\" value=\"1024\" /> -->\n" +
                "  <!-- <property name=\"maxAttributeNameLength\" value=\"1024\" /> -->\n" +
                "  <!-- <property name=\"maxAttributeValueLength\" value=\"16384\" /> -->\n" +
                " </bean>\n" +
                " <bean id=\"extractorCss\" class=\"org.archive.modules.extractor.ExtractorCSS\">\n" +
                " </bean> \n" +
                " <bean id=\"extractorJs\" class=\"org.archive.modules.extractor.ExtractorJS\">\n" +
                " </bean>\n" +
                " <bean id=\"extractorSwf\" class=\"org.archive.modules.extractor.ExtractorSWF\">\n" +
                " </bean>    \n" +
                " <!-- now, processors are assembled into ordered FetchChain bean -->\n" +
                " <bean id=\"fetchProcessors\" class=\"org.archive.modules.FetchChain\">\n" +
                "  <property name=\"processors\">\n" +
                "   <list>\n" +
                "    <!-- re-check scope, if so enabled... -->\n" +
                "    <ref bean=\"preselector\"/>\n" +
                "    <!-- ...then verify or trigger prerequisite URIs fetched, allow crawling... -->\n" +
                "    <ref bean=\"preconditions\"/>\n" +
                "    <!-- ...fetch if DNS URI... -->\n" +
                "    <ref bean=\"fetchDns\"/>\n" +
                "    <!-- <ref bean=\"fetchWhois\"/> -->\n" +
                "    <!-- ...fetch if HTTP URI... -->\n" +
                "    <ref bean=\"fetchHttp\"/>\n" +
                "    <!-- ...extract outlinks from HTTP headers... -->\n" +
                "    <ref bean=\"extractorHttp\"/>\n" +
                "    <!-- ...extract outlinks from HTML content... -->\n" +
                "    <ref bean=\"extractorHtml\"/>\n" +
                "    <!-- ...extract outlinks from CSS content... -->\n" +
                "    <ref bean=\"extractorCss\"/>\n" +
                "    <!-- ...extract outlinks from Javascript content... -->\n" +
                "    <ref bean=\"extractorJs\"/>\n" +
                "    <!-- ...extract outlinks from Flash content... -->\n" +
                "    <ref bean=\"extractorSwf\"/>\n" +
                "   </list>\n" +
                "  </property>\n" +
                " </bean>\n" +
                "  \n" +
                " <!-- DISPOSITION CHAIN -->\n" +
                " <!-- first, processors are declared as top-level named beans  -->\n" +
                " <bean id=\"warcWriter\" class=\"org.archive.modules.writer.WARCWriterProcessor\">\n" +
                "  <!-- <property name=\"compress\" value=\"true\" /> -->\n" +
                "  <!-- <property name=\"prefix\" value=\"IAH\" /> -->\n" +
                "  <!-- <property name=\"suffix\" value=\"${HOSTNAME}\" /> -->\n" +
                "  <property name=\"maxFileSizeBytes\" value=\"50000000\" />\n" +
                "  <!-- <property name=\"poolMaxActive\" value=\"1\" /> -->\n" +
                "  <!-- <property name=\"MaxWaitForIdleMs\" value=\"500\" /> -->\n" +
                "  <!-- <property name=\"skipIdenticalDigests\" value=\"false\" /> -->\n" +
                "  <!-- <property name=\"maxTotalBytesToWrite\" value=\"0\" /> -->\n" +
                "  <!-- <property name=\"directory\" value=\"${launchId}\" /> -->\n" +
                "  <!-- <property name=\"storePaths\">\n" +
                "        <list>\n" +
                "         <value>warcs</value>\n" +
                "        </list>\n" +
                "       </property> -->\n" +
                "  <!-- <property name=\"template\" value=\"${prefix}-${timestamp17}-${serialno}-${heritrix.pid}~${heritrix.hostname}~${heritrix.port}\" /> -->\n" +
                "  <!-- <property name=\"writeRequests\" value=\"true\" /> -->\n" +
                "  <!-- <property name=\"writeMetadata\" value=\"true\" /> -->\n" +
                "  <!-- <property name=\"writeRevisitForIdenticalDigests\" value=\"true\" /> -->\n" +
                "  <!-- <property name=\"writeRevisitForNotModified\" value=\"true\" /> -->\n" +
                "  <!-- <property name=\"startNewFilesOnCheckpoint\" value=\"true\" /> -->\n" +
                " </bean>\n" +
                " <bean id=\"candidates\" class=\"org.archive.crawler.postprocessor.CandidatesProcessor\">\n" +
                "  <!-- <property name=\"seedsRedirectNewSeeds\" value=\"true\" /> -->\n" +
                "  <!-- <property name=\"processErrorOutlinks\" value=\"false\" /> -->\n" +
                " </bean>\n" +
                " <bean id=\"disposition\" class=\"org.archive.crawler.postprocessor.DispositionProcessor\">\n" +
                "  <!-- <property name=\"delayFactor\" value=\"5.0\" /> -->\n" +
                "  <!-- <property name=\"minDelayMs\" value=\"3000\" /> -->\n" +
                "  <!-- <property name=\"respectCrawlDelayUpToSeconds\" value=\"300\" /> -->\n" +
                "  <!-- <property name=\"maxDelayMs\" value=\"30000\" /> -->\n" +
                "  <!-- <property name=\"maxPerHostBandwidthUsageKbSec\" value=\"0\" /> -->\n" +
                " </bean>\n" +
                " <!-- <bean id=\"rescheduler\" class=\"org.archive.crawler.postprocessor.ReschedulingProcessor\">\n" +
                "       <property name=\"rescheduleDelaySeconds\" value=\"-1\" />\n" +
                "      </bean> -->\n" +
                " <!-- now, processors are assembled into ordered DispositionChain bean -->\n" +
                " <bean id=\"dispositionProcessors\" class=\"org.archive.modules.DispositionChain\">\n" +
                "  <property name=\"processors\">\n" +
                "   <list>\n" +
                "    <!-- write to aggregate archival files... -->\n" +
                "    <ref bean=\"warcWriter\"/>\n" +
                "    <!-- ...send each outlink candidate URI to CandidateChain, \n" +
                "         and enqueue those ACCEPTed to the frontier... -->\n" +
                "    <ref bean=\"candidates\"/>\n" +
                "    <!-- ...then update stats, shared-structures, frontier decisions -->\n" +
                "    <ref bean=\"disposition\"/>\n" +
                "    <!-- <ref bean=\"rescheduler\" /> -->\n" +
                "   </list>\n" +
                "  </property>\n" +
                " </bean>\n" +
                " \n" +
                " <!-- CRAWLCONTROLLER: Control interface, unifying context -->\n" +
                " <bean id=\"crawlController\" \n" +
                "   class=\"org.archive.crawler.framework.CrawlController\">\n" +
                "  <!-- <property name=\"maxToeThreads\" value=\"25\" /> -->\n" +
                "  <!-- <property name=\"pauseAtStart\" value=\"true\" /> -->\n" +
                "  <!-- <property name=\"runWhileEmpty\" value=\"false\" /> -->\n" +
                "  <!-- <property name=\"recorderInBufferBytes\" value=\"524288\" /> -->\n" +
                "  <!-- <property name=\"recorderOutBufferBytes\" value=\"16384\" /> -->\n" +
                "  <!-- <property name=\"scratchDir\" value=\"scratch\" /> -->\n" +
                " </bean>\n" +
                " \n" +
                " <!-- FRONTIER: Record of all URIs discovered and queued-for-collection -->\n" +
                " <bean id=\"frontier\" \n" +
                "   class=\"org.archive.crawler.frontier.BdbFrontier\">\n" +
                "  <!-- <property name=\"queueTotalBudget\" value=\"-1\" /> -->\n" +
                "  <!-- <property name=\"balanceReplenishAmount\" value=\"3000\" /> -->\n" +
                "  <!-- <property name=\"errorPenaltyAmount\" value=\"100\" /> -->\n" +
                "  <!-- <property name=\"precedenceFloor\" value=\"255\" /> -->\n" +
                "  <!-- <property name=\"queuePrecedencePolicy\">\n" +
                "        <bean class=\"org.archive.crawler.frontier.precedence.BaseQueuePrecedencePolicy\" />\n" +
                "       </property> -->\n" +
                "  <!-- <property name=\"snoozeLongMs\" value=\"300000\" /> -->\n" +
                "  <!-- <property name=\"retryDelaySeconds\" value=\"900\" /> -->\n" +
                "  <!-- <property name=\"maxRetries\" value=\"30\" /> -->\n" +
                "  <!-- <property name=\"recoveryLogEnabled\" value=\"true\" /> -->\n" +
                "  <!-- <property name=\"maxOutlinks\" value=\"6000\" /> -->\n" +
                "  <!-- <property name=\"extractIndependently\" value=\"false\" /> -->\n" +
                "  <!-- <property name=\"outbound\">\n" +
                "        <bean class=\"java.util.concurrent.ArrayBlockingQueue\">\n" +
                "         <constructor-arg value=\"200\"/>\n" +
                "         <constructor-arg value=\"true\"/>\n" +
                "        </bean>\n" +
                "       </property> -->\n" +
                "  <!-- <property name=\"inbound\">\n" +
                "        <bean class=\"java.util.concurrent.ArrayBlockingQueue\">\n" +
                "         <constructor-arg value=\"40000\"/>\n" +
                "         <constructor-arg value=\"true\"/>\n" +
                "        </bean>\n" +
                "       </property> -->\n" +
                "  <!-- <property name=\"dumpPendingAtClose\" value=\"false\" /> -->\n" +
                " </bean>\n" +
                " \n" +
                " <!-- URI UNIQ FILTER: Used by frontier to remember already-included URIs --> \n" +
                " <bean id=\"uriUniqFilter\" \n" +
                "   class=\"org.archive.crawler.util.BdbUriUniqFilter\">\n" +
                " </bean>\n" +
                " \n" +
                " <!--\n" +
                "   EXAMPLE SETTINGS OVERLAY SHEETS\n" +
                "   Sheets allow some settings to vary by context - usually by URI context,\n" +
                "   so that different sites or sections of sites can be treated differently. \n" +
                "   Here are some example Sheets for common purposes. The SheetOverlaysManager\n" +
                "   (below) automatically collects all Sheet instances declared among the \n" +
                "   original beans, but others can be added during the crawl via the scripting \n" +
                "   interface.\n" +
                "  -->\n" +
                "\n" +
                "<!-- forceRetire: any URI to which this sheet's settings are applied \n" +
                "     will force its containing queue to 'retired' status. -->\n" +
                "<bean id='forceRetire' class='org.archive.spring.Sheet'>\n" +
                " <property name='map'>\n" +
                "  <map>\n" +
                "   <entry key='disposition.forceRetire' value='true'/>\n" +
                "  </map>\n" +
                " </property>\n" +
                "</bean>\n" +
                "\n" +
                "<!-- smallBudget: any URI to which this sheet's settings are applied \n" +
                "     will give its containing queue small values for balanceReplenishAmount \n" +
                "     (causing it to have shorter 'active' periods while other queues are \n" +
                "     waiting) and queueTotalBudget (causing the queue to enter 'retired' \n" +
                "     status once that expenditure is reached by URI attempts and errors) -->\n" +
                "<bean id='smallBudget' class='org.archive.spring.Sheet'>\n" +
                " <property name='map'>\n" +
                "  <map>\n" +
                "   <entry key='frontier.balanceReplenishAmount' value='20'/>\n" +
                "   <entry key='frontier.queueTotalBudget' value='100'/>\n" +
                "  </map>\n" +
                " </property>\n" +
                "</bean>\n" +
                "\n" +
                "<!-- veryPolite: any URI to which this sheet's settings are applied \n" +
                "     will cause its queue to take extra-long politeness snoozes -->\n" +
                "<bean id='veryPolite' class='org.archive.spring.Sheet'>\n" +
                " <property name='map'>\n" +
                "  <map>\n" +
                "   <entry key='disposition.delayFactor' value='10'/>\n" +
                "   <entry key='disposition.minDelayMs' value='10000'/>\n" +
                "   <entry key='disposition.maxDelayMs' value='1000000'/>\n" +
                "   <entry key='disposition.respectCrawlDelayUpToSeconds' value='3600'/>\n" +
                "  </map>\n" +
                " </property>\n" +
                "</bean>\n" +
                "\n" +
                "<!-- highPrecedence: any URI to which this sheet's settings are applied \n" +
                "     will give its containing queue a slightly-higher than default \n" +
                "     queue precedence value. That queue will then be preferred over \n" +
                "     other queues for active crawling, never waiting behind lower-\n" +
                "     precedence queues. -->\n" +
                "<bean id='highPrecedence' class='org.archive.spring.Sheet'>\n" +
                " <property name='map'>\n" +
                "  <map>\n" +
                "   <entry key='frontier.balanceReplenishAmount' value='20'/>\n" +
                "   <entry key='frontier.queueTotalBudget' value='100'/>\n" +
                "  </map>\n" +
                " </property>\n" +
                "</bean>\n" +
                "\n" +
                "<!--\n" +
                "   EXAMPLE SETTINGS OVERLAY SHEET-ASSOCIATION\n" +
                "   A SheetAssociation says certain URIs should have certain overlay Sheets\n" +
                "   applied. This example applies two sheets to URIs matching two SURT-prefixes.\n" +
                "   New associations may also be added mid-crawl using the scripting facility.\n" +
                "  -->\n" +
                "\n" +
                "<!--\n" +
                "<bean class='org.archive.crawler.spring.SurtPrefixesSheetAssociation'>\n" +
                " <property name='surtPrefixes'>\n" +
                "  <list>\n" +
                "   <value>http://(org,example,</value>\n" +
                "   <value>http://(com,example,www,)/</value>\n" +
                "  </list>\n" +
                " </property>\n" +
                " <property name='targetSheetNames'>\n" +
                "  <list>\n" +
                "   <value>veryPolite</value>\n" +
                "   <value>smallBudget</value>\n" +
                "  </list>\n" +
                " </property>\n" +
                "</bean>\n" +
                "-->\n" +
                "\n" +
                " <!-- \n" +
                "   OPTIONAL BUT RECOMMENDED BEANS\n" +
                "  -->\n" +
                "  \n" +
                " <!-- ACTIONDIRECTORY: disk directory for mid-crawl operations\n" +
                "      Running job will watch directory for new files with URIs, \n" +
                "      scripts, and other data to be processed during a crawl. -->\n" +
                " <bean id=\"actionDirectory\" class=\"org.archive.crawler.framework.ActionDirectory\">\n" +
                " </bean> \n" +
                " \n" +
                " <!--  CRAWLLIMITENFORCER: stops crawl when it reaches configured limits -->\n" +
                " <bean id=\"crawlLimiter\" class=\"org.archive.crawler.framework.CrawlLimitEnforcer\">\n" +
                " </bean>\n" +
                " \n" +
                " <!-- CHECKPOINTSERVICE: checkpointing assistance -->\n" +
                " <bean id=\"checkpointService\" \n" +
                "   class=\"org.archive.crawler.framework.CheckpointService\">\n" +
                " </bean>\n" +
                " \n" +
                " <!-- \n" +
                "   OPTIONAL BEANS\n" +
                "    Uncomment and expand as needed, or if non-default alternate \n" +
                "    implementations are preferred.\n" +
                "  -->\n" +
                " \n" +
                " <!-- DISK SPACE MONITOR: \n" +
                "      Pauses the crawl if disk space at monitored paths falls below minimum threshold -->\n" +
                " <!-- \n" +
                " <bean id=\"diskSpaceMonitor\" class=\"org.archive.crawler.monitor.DiskSpaceMonitor\">\n" +
                "   <property name=\"pauseThresholdMiB\" value=\"500\" />\n" +
                "   <property name=\"monitorConfigPaths\" value=\"true\" />\n" +
                "   <property name=\"monitorPaths\">\n" +
                "     <list>\n" +
                "       <value>PATH</value>\n" +
                "     </list>\n" +
                "   </property>\n" +
                " </bean>\n" +
                " -->\n" +
                " \n" +
                " <!-- \n" +
                "   REQUIRED STANDARD BEANS\n" +
                "    It will be very rare to replace or reconfigure the following beans.\n" +
                "  -->\n" +
                "\n" +
                " <!-- STATISTICSTRACKER: standard stats/reporting collector -->\n" +
                " <bean id=\"statisticsTracker\" \n" +
                "   class=\"org.archive.crawler.reporting.StatisticsTracker\" autowire=\"byName\">\n" +
                "  <!-- <property name=\"reports\">\n" +
                "        <list>\n" +
                "         <bean id=\"crawlSummaryReport\" class=\"org.archive.crawler.reporting.CrawlSummaryReport\" />\n" +
                "         <bean id=\"seedsReport\" class=\"org.archive.crawler.reporting.SeedsReport\" />\n" +
                "         <bean id=\"hostsReport\" class=\"org.archive.crawler.reporting.HostsReport\">\n" +
                "     \t\t<property name=\"maxSortSize\" value=\"-1\" />\n" +
                "     \t\t<property name=\"suppressEmptyHosts\" value=\"false\" />\n" +
                "         </bean>\n" +
                "         <bean id=\"sourceTagsReport\" class=\"org.archive.crawler.reporting.SourceTagsReport\" />\n" +
                "         <bean id=\"mimetypesReport\" class=\"org.archive.crawler.reporting.MimetypesReport\" />\n" +
                "         <bean id=\"responseCodeReport\" class=\"org.archive.crawler.reporting.ResponseCodeReport\" />\n" +
                "         <bean id=\"processorsReport\" class=\"org.archive.crawler.reporting.ProcessorsReport\" />\n" +
                "         <bean id=\"frontierSummaryReport\" class=\"org.archive.crawler.reporting.FrontierSummaryReport\" />\n" +
                "         <bean id=\"frontierNonemptyReport\" class=\"org.archive.crawler.reporting.FrontierNonemptyReport\" />\n" +
                "         <bean id=\"toeThreadsReport\" class=\"org.archive.crawler.reporting.ToeThreadsReport\" />\n" +
                "        </list>\n" +
                "       </property> -->\n" +
                " </bean>\n" +
                " \n" +
                " <!-- CRAWLERLOGGERMODULE: shared logging facility -->\n" +
                " <bean id=\"loggerModule\" \n" +
                "   class=\"org.archive.crawler.reporting.CrawlerLoggerModule\">\n" +
                " </bean>\n" +
                " \n" +
                " <!-- SHEETOVERLAYMANAGER: manager of sheets of contextual overlays\n" +
                "      Autowired to include any SheetForSurtPrefix or \n" +
                "      SheetForDecideRuled beans -->\n" +
                " <bean id=\"sheetOverlaysManager\" autowire=\"byType\"\n" +
                "   class=\"org.archive.crawler.spring.SheetOverlaysManager\">\n" +
                " </bean>\n" +
                "\n" +
                " <!-- BDBMODULE: shared BDB-JE disk persistence manager -->\n" +
                " <bean id=\"bdb\" \n" +
                "  class=\"org.archive.bdb.BdbModule\">\n" +
                " </bean>\n" +
                " \n" +
                " <!-- BDBCOOKIESTORE: disk-based cookie storage for FetchHTTP -->\n" +
                " <bean id=\"cookieStore\" \n" +
                "  class=\"org.archive.modules.fetcher.BdbCookieStore\">\n" +
                " </bean>\n" +
                " \n" +
                " <!-- SERVERCACHE: shared cache of server/host info -->\n" +
                " <bean id=\"serverCache\" \n" +
                "   class=\"org.archive.modules.net.BdbServerCache\">\n" +
                " </bean>\n" +
                "\n" +
                " <!-- CONFIG PATH CONFIGURER: required helper making crawl paths relative\n" +
                "      to crawler-beans.cxml file, and tracking crawl files for web UI -->\n" +
                " <bean id=\"configPathConfigurer\" \n" +
                "   class=\"org.archive.spring.ConfigPathConfigurer\">\n" +
                " </bean>\n" +
                " \n" +
                "</beans>\n";

        ha.initiateHarvest("T6666", profileText, "http://localhost:8080/nlnzwebarchive/wayback/");


    }

}
