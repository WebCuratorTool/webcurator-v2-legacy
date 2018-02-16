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
import org.webcurator.core.common.Constants;
import org.webcurator.core.harvester.agent.exception.HarvestAgentException;
import org.webcurator.core.harvester.agent.filter.*;
import org.webcurator.core.harvester.agent.filter.FileFilter;
import org.webcurator.core.harvester.coordinator.HarvestAgentListener;
import org.webcurator.core.reader.LogProvider;
import org.webcurator.core.store.DigitalAssetStore;
import org.webcurator.domain.model.core.ArcHarvestResultDTO;
import org.webcurator.domain.model.core.HarvestResultDTO;
import org.webcurator.domain.model.core.LogFilePropertiesDTO;
import org.webcurator.domain.model.core.harvester.agent.HarvestAgentStatusDTO;
import org.webcurator.domain.model.core.harvester.agent.HarvesterStatusDTO;

import java.io.*;
import java.util.*;

/**
 * This is an Implementation of the HarvestAgent interface that uses Heritrix as the 
 * engine to perform the harvesting of the web sites.
 * @author nwaight
 */
public class HarvestAgentH3 extends AbstractHarvestAgent implements LogProvider {
    /** The name of the profile file. */
    private static final String PROFILE_NAME = "crawler-beans.cxml";
    /** The name of the base harvest directory. */
    private String baseHarvestDirectory = "";
    /** the name of the harvest agent. */
    private String name = "";
    /** the host name of the harvest agent. */
    private String host = "";
    /** the harvest agent control port. */
    private int port = 0;
    /** the harvest agent service endpoint. */
    private String service = "";
    /** the harvest agent log reader service endpoint. */
    private String logReaderService = "";
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
            //TODO - what to do with profile and seeds files when harvests aborted? Where are these files actually created?
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
            try{
                abort(aJob);
            }
            catch(Exception ex){
                log.error("Failed to abort initilization of harvest for " + aJob + " : " + ex.getMessage(), ex);
            }

//            throw new HarvestAgentException("Failed to initiate harvest for " + aJob + " : " + e.getMessage(), e);
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

//        tidy(aJob);
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
            // If build failed then we want to leave harvest dir there for troubleshooting
            if(!harvester.getStatus().getStatus().equals("Could not launch job - Fatal InitializationException")){
                // Remove base dir of job
                if(harvester.getHarvestDir() != null){
                    harvestDir = harvester.getHarvestDir();
                }
            }

            harvester.deregister();
        }

        removeHarvester(aJob);

        if (harvestDir != null) {
            boolean deleted = FileUtils.deleteDir(harvestDir);
        	if (!deleted) {
        		log.error("Unable to delete harvest directory "+harvestDir.getAbsolutePath());
        	}
        }

        //TODO - at this point the harvester is gone, do we need the final stats here??
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
        //TODO - What if no warcs are generated? This throws null pointer otherwise
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

    private boolean dirsExist(List<File> baseDirs) {
        boolean atleastOneDirExists = false;
        for(File baseDir: baseDirs) {
            if(baseDir.exists()){
                atleastOneDirExists = true;
            }
        }
        return atleastOneDirExists;
    }



    /** @see HarvestAgent#completeHarvest(String, int). */
    public int completeHarvest(String aJob, int aFailureStep) {
        Harvester harvester = getHarvester(aJob);

        log.info("Performing Harvest Completion for job " + aJob);

        //TODO - what does old heritrix do for this last heartbeat
        harvestCoordinatorNotifier.heartbeat(getStatus());

        //TODO  - if a harvest gets aborted/stopped before complete has finished, this will throw a null pointer exception
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
            log.info("Getting digital assets to send to store for job " + aJob);

	        try {
                // BE AWARE - if a harvest is stopped before any warcs are written then a null pointer exception can be thrown
                // when getting the file list of the warc dir, because the warc dir may not exist -causing an infinite loop.
                File[] fileList = getFileArray( das, new NegateFilter(new ExtensionFileFilter(Constants.EXTN_OPEN_ARC)));
                int numberOfFiles = fileList.length;

                for(int i=0; i<numberOfFiles; i++) {
                    log.debug("Sending ARC " + (i+1) + " of " + numberOfFiles + " to digital asset store for job " + aJob);
                    digitalAssetStore.save(aJob, fileList[i]);
                    log.debug("Finished sending ARC " + (i+1) + " of " + numberOfFiles + " to digital asset store for job " + aJob);
                }

	        }
	        catch (Exception e) {
                if(dirsExist(das)){
                    log.error("Failed to send harvest result to digital asset store for job " + aJob + ": " + e.getMessage(), e);
                }
                else{
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
                String harvestLogsDir = harvester.getHarvestLogDir().getParent();
                File reportsDir = new File(harvestLogsDir + File.separator + "reports");
	        	File[] fileList = getFileArray(reportsDir, NotEmptyFileFilter.notEmpty(new ExtensionFileFilter(Constants.EXTN_REPORTS)), NotEmptyFileFilter.notEmpty(new ExactNameFilter(PROFILE_NAME)));
                log.info("Sending harvest reports to digital asset store for job " + aJob);
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
                log.info("Sending harvest result to WCT for job " + aJob);
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

        log.info("Cleaning up for job " + aJob);
        tidy(aJob);
        return NO_FAILURES;
    }


    /** @see HarvestAgent#getStatus(). */
    public HarvestAgentStatusDTO getStatus() {
        //TODO - might need adjustment for when harvest has stopped/gone
        HarvestAgentStatusDTO status = new HarvestAgentStatusDTO();
        status.setHost(host);
        status.setPort(port);
        status.setService(service);
        status.setLogReaderService(logReaderService);
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

        // Reports are stored in their own dir with H3, and are not written until harvest finished/stopped.
        File reportsDir = new File(logsDir.getPath() + File.separator + "reports");
        fileList = reportsDir.listFiles();
        if(fileList != null){
            for(File f: fileList) {
                if (f.getName().endsWith(Constants.EXTN_REPORTS)) {
                    logFiles.add(f.getName());
                }
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

        // Reports are stored in their own dir with H3
        File reportsDir = new File(logsDir.getPath() + File.separator + "reports");
        fileList = reportsDir.listFiles();
        if(fileList != null){
            for(File f: fileList) {
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

        // Load default H3 profile if it exists
        File defaultProfile = new File(baseHarvestDirectory + File.separator + "defaultH3Profile.cxml");
        StringBuilder defaultProfileText = new StringBuilder();
        try {
            if (defaultProfile.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(defaultProfile));
                String line = null;
                while((line = reader.readLine()) != null){
                    defaultProfileText.append(line);
                    defaultProfileText.append("\r\n");
                }
                reader.close();
                aProfile = defaultProfileText.toString();
            }
        }
        catch (FileNotFoundException e) {
            throw new HarvestAgentException("Failed to write the job profile " + order.getAbsolutePath() + " " + e.getMessage(), e);
        } catch (IOException e) {
            throw new HarvestAgentException("Failed to write the job profile " + order.getAbsolutePath() + " " + e.getMessage(), e);
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
                // Sometimes warc dir might not exist if harvest stopped before any warcs generated.
                if(dir.exists()){
                    fileList = dir.listFiles();
                    for(File f: fileList) {
                        if (f.getName().endsWith(Constants.EXTN_OPEN_ARC)) {
                            found = true;
                        }
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
                "  OVERRIDES   \n" +
                " -->\n" +
                " <!-- overrides from a text property list -->\n" +
                " <bean id=\"simpleOverrides\" class=\"org.springframework.beans.factory.config.PropertyOverrideConfigurer\">\n" +
                "  <property name=\"properties\">\n" +
                "   <value>\n" +
                "# This Properties map is specified in the Java 'property list' text format\n" +
                "# http://java.sun.com/javase/6/docs/api/java/util/Properties.html#load%28java.io.Reader%29\n" +
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
                "\n" +
                "   </props>\n" +
                "  </property>\n" +
                " </bean>\n" +
                "\n" +
                " <!-- CRAWL METADATA: including identification of crawler/operator -->\n" +
                " <bean id=\"metadata\" class=\"org.archive.modules.CrawlMetadata\" autowire=\"byName\">\n" +
                "       <property name=\"operatorContactUrl\" value=\"[see override above]\"/>\n" +
                "       <property name=\"jobName\" value=\"[see override above]\"/>\n" +
                "       <property name=\"description\" value=\"[see override above]\"/>\n" +
                "  <property name=\"robotsPolicyName\" value=\"ignore\"/>       \n" +
                " </bean>\n" +
                " \n" +
                " <!-- SEEDS ALTERNATE APPROACH: specifying external seeds.txt file in\n" +
                "      the job directory, similar to the H1 approach. \n" +
                "      Use either the above, or this, but not both. -->\n" +
                "\n" +
                " <bean id=\"seeds\" class=\"org.archive.modules.seeds.TextSeedModule\">\n" +
                "  <property name=\"textSource\">\n" +
                "   <bean class=\"org.archive.spring.ConfigFile\">\n" +
                "    <property name=\"path\" value=\"/seeds.txt\" />\n" +
                "   </bean>\n" +
                "  </property>\n" +
                "  <property name='sourceTagSeeds' value='false'/>\n" +
                "  <property name='blockAwaitingSeedLines' value='-1'/>\n" +
                " </bean>\n" +
                " \n" +
                " <bean id=\"acceptSurts\" class=\"org.archive.modules.deciderules.surt.SurtPrefixedDecideRule\">\n" +
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
                "  -->\n" +
                "  \n" +
                " <!-- CANDIDATE CHAIN --> \n" +
                " <!-- first, processors are declared as top-level named beans -->\n" +
                " <bean id=\"candidateScoper\" class=\"org.archive.crawler.prefetch.CandidateScoper\">\n" +
                " </bean>\n" +
                " <bean id=\"preparer\" class=\"org.archive.crawler.prefetch.FrontierPreparer\">\n" +
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
                "\n" +
                " <bean id=\"fetchHttp\" class=\"org.archive.modules.fetcher.FetchHTTP\">\n" +
                " </bean>\n" +
                " <bean id=\"extractorHttp\" class=\"org.archive.modules.extractor.ExtractorHTTP\">\n" +
                " </bean>\n" +
                " <bean id=\"extractorHtml\" class=\"org.archive.modules.extractor.ExtractorHTML\">\n" +
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
                " </bean>\n" +
                " <bean id=\"candidates\" class=\"org.archive.crawler.postprocessor.CandidatesProcessor\">\n" +
                "  <!-- <property name=\"seedsRedirectNewSeeds\" value=\"true\" /> -->\n" +
                "  <!-- <property name=\"processErrorOutlinks\" value=\"false\" /> -->\n" +
                " </bean>\n" +
                " <bean id=\"disposition\" class=\"org.archive.crawler.postprocessor.DispositionProcessor\">\n" +
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
                " </bean>\n" +
                " \n" +
                " <!-- FRONTIER: Record of all URIs discovered and queued-for-collection -->\n" +
                " <bean id=\"frontier\" \n" +
                "   class=\"org.archive.crawler.frontier.BdbFrontier\">\n" +
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
                "   REQUIRED STANDARD BEANS\n" +
                "    It will be very rare to replace or reconfigure the following beans.\n" +
                "  -->\n" +
                "\n" +
                " <!-- STATISTICSTRACKER: standard stats/reporting collector -->\n" +
                " <bean id=\"statisticsTracker\" \n" +
                "   class=\"org.archive.crawler.reporting.StatisticsTracker\" autowire=\"byName\">\n" +
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

        ha.setHarvestCoordinatorNotifier(new HarvestAgentListener() {
            @Override
            public void heartbeat(HarvestAgentStatusDTO aStatus) {}

            @Override
            public void harvestComplete(HarvestResultDTO aResult) {}

            @Override
            public void notification(Long aTargetInstanceOid, int notificationCategory, String aMessageType) {}
        });

        ha.initiateHarvest("T6666", profileText, "http://localhost:8080/wct/");
        try {Thread.sleep(50000);}catch (InterruptedException e){}

        //************
        // Done testing
        //************
//        ha.stop("T6666");
//        ha.abort("T6666");
//        List<File> logFiles = ha.getLogFileNames("T6666");
//        LogFilePropertiesDTO[] logProps = ha.getLogFileAttributes("T6666");
//        File logFile = ha.getLogFile("T6666", "nonfatal-errors.log");
//        ha.completeHarvest("T6666", 0); //TODO - can't be completely tested until HA can talk to DAS
//        ha.pause("T6666");
//        ha.resume("T6666");
//        ha.updateProfileOverrides("T6666", "");
        ha.stop("T6666");
//        String[] tiS = {"T6666"};
//        ha.purgeAbortedTargetInstances(tiS);



        //************
        // Still testing
        //************

        //TODO - getStatus could need work
//        ha.getStatus();
        System.out.println("Finished HarvestAgentH3 test");
    }

}
