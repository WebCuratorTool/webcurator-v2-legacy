package org.webcurator.core.harvester.coordinator;

import java.io.File;
import java.util.List;

import org.webcurator.domain.model.core.LogFilePropertiesDTO;
import org.webcurator.domain.model.core.TargetInstance;

public interface HarvestLogManager {

	File getLogfile(TargetInstance aTargetInstance, String aFilename);

	String[] getHopPath(TargetInstance aTargetInstance, String aFileName, String aUrl);

	String[] getLogLinesByRegex(TargetInstance aTargetInstance, String aFileName, int aNoOfLines, String aRegex,
			boolean prependLineNumbers);

	Integer getFirstLogLineAfterTimeStamp(TargetInstance aTargetInstance, String aFileName, Long timestamp);

	Integer getFirstLogLineContaining(TargetInstance aTargetInstance, String aFileName, String match);

	Integer getFirstLogLineBeginning(TargetInstance aTargetInstance, String aFileName, String match);

	String[] getLog(TargetInstance aTargetInstance, String aFileName, int aStartLine, int aNoOfLines);

	Integer countLogLines(TargetInstance aTargetInstance, String aFileName);

	String[] headLog(TargetInstance aTargetInstance, String aFileName, int aNoOfLines);

	LogFilePropertiesDTO[] listLogFileAttributes(TargetInstance aTargetInstance);

	List<String> listLogFiles(TargetInstance aTargetInstance);

	String[] tailLog(TargetInstance targetInstance, String aFileName, int aNoOfLines);

}
