package org.webcurator.core.harvester.coordinator;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webcurator.core.exceptions.WCTRuntimeException;
import org.webcurator.core.reader.LogReader;
import org.webcurator.core.store.DigitalAssetStoreFactory;
import org.webcurator.domain.model.core.LogFilePropertiesDTO;
import org.webcurator.domain.model.core.TargetInstance;

public class HarvestLogManagerImpl implements HarvestLogManager {

	private HarvestAgentManager harvestAgentManager;
	private DigitalAssetStoreFactory digitalAssetStoreFactory;

	private Logger log = LoggerFactory.getLogger(getClass());

	@Override
	public List<String> listLogFiles(TargetInstance targetInstance) {
		checkNotNull(targetInstance);

		if (harvestAgentManager.runningOrPaused(targetInstance)) {
			LogReader logReader = harvestAgentManager.getLogReader(targetInstance);
			if (logReader == null) {
				log.warn("list Log Files Failed. Failed to find the Log Reader for the Job {}.", targetInstance.getJobName());
				return new ArrayList<String>();
			}
			return logReader.listLogFiles(targetInstance.getJobName());
		} else {
			// if not then check to see if the log files are available from the
			// digital asset store.
			LogReader logReader = digitalAssetStoreFactory.getLogReader();
			return logReader.listLogFiles(targetInstance.getJobName());
		}
	}

	@Override
	public LogFilePropertiesDTO[] listLogFileAttributes(TargetInstance targetInstance) {
		checkNotNull(targetInstance);

		LogReader logReader = getLogReader(targetInstance);
		if (logReader == null) {
			log.warn("listLogFileAttributes Failed. Failed to find the Log Reader for the Job {}.", targetInstance.getJobName());
			LogFilePropertiesDTO[] empty = new LogFilePropertiesDTO[0];
			return empty;
		}
		return logReader.listLogFileAttributes(targetInstance.getJobName());
	}

	@Override
	public String[] tailLog(TargetInstance targetInstance, String aFileName, int aNoOfLines) {
		checkNotNull(targetInstance);

		String[] data = { "" };
		LogReader logReader = getLogReader(targetInstance);
		if (logReader == null) {
			log.warn("Tail Log Files Failed. Failed to find the log Reader for the Job {}.", targetInstance.getJobName());
			return data;
		}
		return logReader.tail(targetInstance.getJobName(), aFileName, aNoOfLines);
	}

	@Override
	public Integer countLogLines(TargetInstance targetInstance, String aFileName) {
		checkNotNull(targetInstance);

		Integer count = 0;
		LogReader logReader = getLogReader(targetInstance);
		if (logReader == null) {
			log.warn("Count Log Lines Failed. Failed to find the log Reader for the Job {}.", targetInstance.getJobName());
			return count;
		}
		return logReader.countLines(targetInstance.getJobName(), aFileName);
	}

	@Override
	public String[] headLog(TargetInstance targetInstance, String aFileName, int aNoOfLines) {
		checkNotNull(targetInstance);

		String[] data = { "" };
		LogReader logReader = getLogReader(targetInstance);
		if (logReader == null) {
			log.warn("Head Log Files Failed. Failed to find the log Reader for the Job {}.", targetInstance.getJobName());
			return data;
		}
		return logReader.get(targetInstance.getJobName(), aFileName, 1, aNoOfLines);
	}

	@Override
	public String[] getLog(TargetInstance targetInstance, String aFileName, int aStartLine, int aNoOfLines) {
		checkNotNull(targetInstance);

		String[] data = { "" };
		LogReader logReader = getLogReader(targetInstance);
		if (logReader == null) {
			log.warn("Get Log Files Failed. Failed to find the log Reader for the Job {}.", targetInstance.getJobName());
			return data;
		}
		return logReader.get(targetInstance.getJobName(), aFileName, aStartLine, aNoOfLines);
	}

	@Override
	public Integer getFirstLogLineBeginning(TargetInstance targetInstance, String aFileName, String match) {
		checkNotNull(targetInstance);

		LogReader logReader = getLogReader(targetInstance);
		if (logReader == null) {
			log.warn("Get First Log Line Beginning failed. Failed to find the log Reader for the Job {}.",
					targetInstance.getJobName());
			return new Integer(0);
		}
		return logReader.findFirstLineBeginning(targetInstance.getJobName(), aFileName, match);
	}

	@Override
	public Integer getFirstLogLineContaining(TargetInstance targetInstance, String aFileName, String match) {
		checkNotNull(targetInstance);

		LogReader logReader = getLogReader(targetInstance);
		if (logReader == null) {
			log.warn("Get First Log Line Containing failed. Failed to find the log Reader for the Job {}.",
					targetInstance.getJobName());
			return new Integer(0);
		}
		return logReader.findFirstLineContaining(targetInstance.getJobName(), aFileName, match);
	}

	@Override
	public Integer getFirstLogLineAfterTimeStamp(TargetInstance targetInstance, String aFileName, Long timestamp) {
		checkNotNull(targetInstance);

		LogReader logReader = getLogReader(targetInstance);
		if (logReader == null) {
			log.warn("Get First Log Line After Timestamp failed. Failed to find the log Reader for the Job {}.",
					targetInstance.getJobName());
			return new Integer(0);
		}
		return logReader.findFirstLineAfterTimeStamp(targetInstance.getJobName(), aFileName, timestamp);
	}

	@Override
	public String[] getLogLinesByRegex(TargetInstance targetInstance, String aFileName, int aNoOfLines, String aRegex,
			boolean prependLineNumbers) {
		checkNotNull(targetInstance);

		String[] data = { "" };
		LogReader logReader = getLogReader(targetInstance);
		if (logReader == null) {
			log.warn("Get log lines by regex failed. Failed to find the log Reader for the Job {}.", targetInstance.getJobName());
			return data;
		}
		return logReader.getByRegExpr(targetInstance.getJobName(), aFileName, aRegex, "zzzzzzzzz", prependLineNumbers, 0,
				aNoOfLines);
	}

	@Override
	public String[] getHopPath(TargetInstance targetInstance, String aFileName, String aUrl) {
		checkNotNull(targetInstance);

		String[] data = { "" };
		LogReader logReader = getLogReader(targetInstance);
		if (logReader == null) {
			log.warn("Get Log Files Failed. Failed to find the log Reader for the Job {}.", targetInstance.getJobName());
			return data;
		}
		return logReader.getHopPath(targetInstance.getJobName(), targetInstance.getHarvestResult(1).getOid().toString(), aFileName,
				aUrl);
	}

	@Override
	public File getLogfile(TargetInstance targetInstance, String aFilename) {
		checkNotNull(targetInstance);

		LogReader logReader = getLogReader(targetInstance);
		if (logReader == null) {
			log.warn("Tail Log Files Failed. Failed to find the log Reader for the Job {}.", targetInstance.getJobName());
			return null;
		}
		return logReader.retrieveLogfile(targetInstance.getJobName(), aFilename);
	}

	private void checkNotNull(TargetInstance targetInstance) {
		if (targetInstance == null) {
			throw new WCTRuntimeException("Target instance must not be null");
		}
	}

	private LogReader getLogReader(TargetInstance targetInstance) {
		if (harvestAgentManager.runningOrPaused(targetInstance)) {
			// If we are harvesting then get the log files from the harvester
			LogReader logReader = harvestAgentManager.getLogReader(targetInstance);
			return logReader;
		} else {
			// if not then check to see if the log files are available from the
			// digital asset store.
			LogReader logReader = digitalAssetStoreFactory.getLogReader();
			return logReader;
		}

	}

	public HarvestAgentManager getHarvestAgentManager() {
		return harvestAgentManager;
	}

	public void setHarvestAgentManager(HarvestAgentManager harvestAgentManager) {
		this.harvestAgentManager = harvestAgentManager;
	}

	public DigitalAssetStoreFactory getDigitalAssetStoreFactory() {
		return digitalAssetStoreFactory;
	}

	public void setDigitalAssetStoreFactory(DigitalAssetStoreFactory digitalAssetStoreFactory) {
		this.digitalAssetStoreFactory = digitalAssetStoreFactory;
	}
	
	
}
