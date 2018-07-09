package org.webcurator.core.archive;

/**
 * Constants used for creating an archive of a harvest.
 * @author aparker
 */
public interface Constants {
	public static final String REFERENCE_NUMBER = "reference-number"; 
	public static final String ALTERNATE_REFERENCE_NUMBER = "alternate-reference-number";
	public static final String ACCESS_RESTRICTION = "access-restriction"; 
	public static final String OMS_ACCESS_RESTRICTION_OPEN_ACCESS = "ACR_OPA";
	public static final String OMS_ACCESS_RESTRICTION_ON_SITE = "ACR_ONS";
	public static final String OMS_ACCESS_RESTRICTION_ON_SITE_RESTRICTED = "ACR_OSR";
	public static final String OMS_ACCESS_RESTRICTION_RESTRICTED = "ACR_RES";
	public static final String RESTRICTION_DATE = "restriction-date"; 
	public static final String ACCESS_AVAILABLE = "access-available"; 
	public static final String PERSON_RESPONSIBLE = "person-responsible"; 
	public static final String MAINTENANCE_FLAG = "maintenance-flag"; 
	public static final String MAINTENANCE_NOTES = "maintenance-notes"; 
	public static final String DEPENDENCIES = "dependencies"; 
	public static final String ENTRY_POINT_URL = "entry-point-url"; 
	public static final String USER = "user";
	public static final String HARVEST_TYPE = "harvest-type";
	public static final int LOG_FILE = 0; 
	public static final int ARC_FILE = 1; 
	public static final int REPORT_FILE = 2; 
	public static final int ROOT_FILE = 3; 
}
