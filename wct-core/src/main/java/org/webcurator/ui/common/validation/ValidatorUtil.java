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
package org.webcurator.ui.common.validation;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.webcurator.core.permissionmapping.UrlUtils;
import org.apache.commons.logging.LogFactory;
import org.apache.oro.text.regex.MalformedPatternException;
import org.apache.oro.text.regex.Perl5Compiler;
import org.apache.oro.text.regex.Perl5Matcher;
import org.apache.oro.text.regex.Perl5Pattern;
import org.springframework.context.ApplicationContext;
import org.springframework.validation.Errors;
import org.webcurator.core.common.Constants;
import org.webcurator.core.harvester.coordinator.HarvestBandwidthManager;
import org.webcurator.core.harvester.coordinator.HarvestCoordinator;
import org.webcurator.core.scheduler.TargetInstanceManager;
import org.webcurator.core.targets.TargetManager;
import org.webcurator.core.util.ApplicationContextFactory;
import org.webcurator.domain.HarvestCoordinatorDAO;
import org.webcurator.domain.model.core.BandwidthRestriction;
import org.webcurator.domain.model.core.TargetInstance;
import org.webcurator.ui.agent.command.BandwidthRestrictionsCommand;

/**
 * Utility class providing useful validation methods.
 * @author nwaight
 */
public final class ValidatorUtil {
    
    public final static String EMAIL_VALIDATION_REGEX = 
        "^[_a-zA-Z0-9-]+(\\.[_a-zA-Z0-9-]+)*@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*(\\.[a-zA-Z]{2,4})$";
    
    
    /**
     * Helper function to validate that a number is greater than the
     * low limit value.
     * @param aErrors the errors object to populate 
     * @param aNumber the number to check
     * @param aLowLimit the low limit
     * @param aErrorCode the error code to use
     * @param aValues the values to set in the error message
     * @param aFailureMessage the default error message
     */
    public static void validateMinNumber(Errors aErrors, Number aNumber, Number aLowLimit, String aErrorCode, Object[] aValues, String aFailureMessage) {
        if (aNumber != null) {
            if (aNumber.doubleValue() <= aLowLimit.doubleValue()) {
                aErrors.reject(aErrorCode, aValues, aFailureMessage);
            }
        }
    }
    
    /**
     * Helper function to validate the length of a string input field.
     * @param aErrors the errors object to populate
     * @param aField the field to check the length of
     * @param aMaxLength the length to check against
     * @param aErrorCode the code for the message resource value
     * @param aValues the list of values to replace in the i8n messages
     * @param aFailureMessage the default failure message
     */
    public static void validateStringMaxLength(Errors aErrors, String aField, int aMaxLength, String aErrorCode, Object[] aValues, String aFailureMessage) {
        if (aField != null && !aField.trim().equals("")) {
            if (aField.length() > aMaxLength) {
                aErrors.reject(aErrorCode, aValues, aFailureMessage);
            }
        }
    }

    /**
     * Helper function to validate the length of a string input field.
     * @param aErrors the errors object to populate
     * @param aField the field to check the length of
     * @param aMinLength the length to check against
     * @param aErrorCode the code for the message resource value
     * @param aValues the list of values to replace in the i8n messages
     * @param aFailureMessage the default failure message
     */
    public static void validateStringMinLength(Errors aErrors, String aField, int aMinLength, String aErrorCode, Object[] aValues, String aFailureMessage) {
        if (aField != null && !aField.trim().equals("")) {
            if (aField.length() < aMinLength) {
                aErrors.reject(aErrorCode, aValues, aFailureMessage);
            }
        }
    }
    
    /**
     * Helper method used to check two string values match.
     * @param aErrors the errors object to populate
     * @param val1 String value 1
     * @param val2 String value 2
     * @param aErrorCode the error code
     * @param aValues the values
     * @param aFailureMessage the default message
     */
    public static void validateValueMatch(Errors aErrors, String val1, String val2, String aErrorCode, Object[] aValues, String aFailureMessage) {
        if (val1 != null && val2 != null) {
            if (val1.equals(val2) == true) {
                return;
            }
        }
        
        aErrors.reject(aErrorCode, aValues, aFailureMessage);
    }

    /**
     * Helper method used to check two string values are different.
     * @param aErrors the errors object to populate
     * @param val1 String value 1
     * @param val2 String value 2
     * @param aErrorCode the error code
     * @param aValues the values
     * @param aFailureMessage the default message
     */
    public static void validateValuesDifferent(Errors aErrors, String val1, String val2, String aErrorCode, Object[] aValues, String aFailureMessage) {
        if (val1 != null && val2 != null) {
            if (val1.equals(val2) == false) {
                return;
            }
        }
        
        aErrors.reject(aErrorCode, aValues, aFailureMessage);
    }
    
    /**
     * Helper method to validated that a start time is before and 
     * not the same as an end time.
     * @param aErrors the errors object to populate 
     * @param aStart the start time
     * @param aEnd the end time
     * @param aErrorCode the error code
     * @param aValues the values to set in the error message
     * @param aFailureMessage the default failure message
     */
    public static void validateStartBeforeEndTime(Errors aErrors, Date aStart, Date aEnd, String aErrorCode, Object[] aValues, String aFailureMessage) {
        if (aStart != null && aEnd != null) {
            if (aStart.after(aEnd)) {
                aErrors.reject(aErrorCode, aValues, aFailureMessage);
            }
            
            if (aStart.equals(aEnd)) {
                aErrors.reject(aErrorCode, aValues, aFailureMessage);
            }
        }
    }
    
    /**
     * Helper method to validated that a start time is before or equal to the 
     * end time.
     * @param aErrors the errors object to populate 
     * @param aStart the start time
     * @param aEnd the end time
     * @param aErrorCode the error code
     * @param aValues the values to set in the error message
     * @param aFailureMessage the default failure message
     */
    public static void validateStartBeforeOrEqualEndTime(Errors aErrors, Date aStart, Date aEnd, String aErrorCode, Object[] aValues, String aFailureMessage) {
        if (aStart != null && aEnd != null) {
            if (aStart.after(aEnd)) {
                aErrors.reject(aErrorCode, aValues, aFailureMessage);
            }
        }
    }    
    
    /**
     * Helper method to validated that a start time is before and 
     * not the same as an end time for a specified bandwidth restriction.
     * @param aErrors the errors object to populate 
     * @param aCmd the bandwidth restriction command
     * @param aErrorCode the error code
     * @param aValues the values to set in the error message
     * @param aFailureMessage the default failure message
     */
    public static void validateNoBandwidthPeriodOverlaps(Errors aErrors, BandwidthRestrictionsCommand aCmd,  String aErrorCode, Object[] aValues, String aFailureMessage) {
        if (aCmd != null && aCmd.getStart() != null && aCmd.getEnd() != null && aCmd.getDay() != null) {
            ApplicationContext context = ApplicationContextFactory.getWebApplicationContext();
            HarvestCoordinatorDAO hcDao = (HarvestCoordinatorDAO) context.getBean(Constants.BEAN_HARVEST_COORDINATOR_DAO);
            HashMap<String, List<BandwidthRestriction>> allRestrictions = hcDao.getBandwidthRestrictions();
            List restrictions = allRestrictions.get(aCmd.getDay());            
            if (restrictions != null && !restrictions.isEmpty()) {
                BandwidthRestriction newBr = new BandwidthRestriction();
                newBr.setStartTime(aCmd.getStart());
                newBr.setEndTime(aCmd.getEnd());
                
                BandwidthRestriction element = null;
                Iterator it = restrictions.iterator();
                while (it.hasNext()) {
                    element = (BandwidthRestriction) it.next();
                    if (aCmd.getOid() == null || !aCmd.getOid().equals(element.getOid())) {
                        if (newBr.getStartTime().after(element.getStartTime()) && newBr.getStartTime().before(element.getEndTime())) {
                            aErrors.reject(aErrorCode, aValues, aFailureMessage);
                            return;
                        }
      
                        if (newBr.getEndTime().after(element.getStartTime()) && newBr.getEndTime().before(element.getEndTime())) {
                            aErrors.reject(aErrorCode, aValues, aFailureMessage);
                            return;
                        }
                        
                        if (newBr.getStartTime().equals(element.getStartTime()) || newBr.getStartTime().equals(element.getEndTime())) {
                            aErrors.reject(aErrorCode, aValues, aFailureMessage);
                            return;
                        }
                        
                        if (newBr.getEndTime().equals(element.getStartTime()) && newBr.getEndTime().equals(element.getEndTime())) {
                            aErrors.reject(aErrorCode, aValues, aFailureMessage);
                            return;
                        }
                    }
                }                 
            }
        }
    }
    
    public static void validateIsDate(Errors errors, String aDate, String aDateFormat, String aErrorCode, Object[] aValues, String aDefaultMessage) {
    	DateFormat simpleDateFormat = new SimpleDateFormat(aDateFormat);
    	try {
    		simpleDateFormat.parse(aDate);
    	} 
    	catch (ParseException e) {
    		errors.reject(aErrorCode, aValues, aDefaultMessage);
		}
    }
    
    /**
     * Helper method to check to see if adding the new target instance will mean that 
     * this or any other running harvest has a bandwidth setting below the minimum
     * @param aErrors the errors object to populate
     * @param aTargetInstanceOid the target instance id to check
     * @param aErrorCode the error code for a vaildation failure
     * @param aValues the values to set for a failure
     * @param aFailureMessage the default failure message
     */
    public static void validateMinimumBandwidthAvailable(Errors aErrors, Long aTargetInstanceOid, String aErrorCode, Object[] aValues, String aFailureMessage) {
        if (aTargetInstanceOid != null) {
            ApplicationContext context = ApplicationContextFactory.getWebApplicationContext();
            TargetInstanceManager targetInstanceManager = (TargetInstanceManager) context.getBean(Constants.BEAN_TARGET_INSTANCE_MNGR);
            HarvestBandwidthManager harvestBandwidthManager  = (HarvestBandwidthManager) context.getBean(Constants.BEAN_HARVEST_BANDWIDTH_MANAGER);            
            
            TargetInstance ti = targetInstanceManager.getTargetInstance(aTargetInstanceOid);
        	if (!harvestBandwidthManager.isMiniumBandwidthAvailable(ti)) {
            	// failure bandwidth setting is too low.
            	aErrors.reject(aErrorCode, aValues, aFailureMessage);
            }
        }        
    }     
    
    /**
     * Helper method to check to see if the bandwidth percentage setting is
     * less than or equal to the maximum
     * @param aErrors the errors object to populate
     * @param aErrorCode the error code for a vaildation failure
     * @param aPercentage the percentage to check.
     */
    public static void validateMaxBandwidthPercentage(Errors aErrors, int aPercentage, String aErrorCode) {    	
        ApplicationContext context = ApplicationContextFactory.getWebApplicationContext();        
        HarvestBandwidthManager harvestBandwidthManager  = (HarvestBandwidthManager) context.getBean(Constants.BEAN_HARVEST_BANDWIDTH_MANAGER);            
                                       
        if (aPercentage > harvestBandwidthManager.getMaxBandwidthPercent()) {
        	// failure bandwidth percentage setting is too high.
        	Object[] vals = new Object[1];
         	vals[0] = harvestBandwidthManager.getMaxBandwidthPercent();
        	aErrors.reject(aErrorCode, vals, "max bandwidth percentage exeeded");
        }           
    }
    
    /**
     * Helper method to check to see if the Target of a target instance is approved for harvest.
     * @param aErrors the errors object to populate
     * @param aTargetInstanceOid the target instance to check
     * @param aErrorCode the error code for a vaildation failure
     */
    public static void validateTargetApproved(Errors aErrors, Long aTargetInstanceOid, String aErrorCode) {    	
        ApplicationContext context = ApplicationContextFactory.getWebApplicationContext();        
        TargetInstanceManager tiManager = (TargetInstanceManager) context.getBean(Constants.BEAN_TARGET_INSTANCE_MNGR);
        TargetManager targetManager = (TargetManager) context.getBean(Constants.BEAN_TARGET_MNGR);
        
        TargetInstance ti = tiManager.getTargetInstance(aTargetInstanceOid);
        if (!targetManager.isTargetHarvestable(ti)) {
        	// failure target is not approved to be harvested.        	        
        	Object[] vals = new Object[1];
        	vals[0] = ti.getTarget().getOid().toString();        	
        	aErrors.reject(aErrorCode, vals, "target instance is not approved for harvest");        	
        }        
    }
    
    /**
     * Helper method to validate the specified string does not contain anything other than
     * that specified by the regular expression
     * @param aErrors The errors object to populate
     * @param aValue the string to check
     * @param aRegEx the Perl based regular expression to check the value against
     * @param aErrorCode the error code for getting the i8n failure message
     * @param aValues the list of values to replace in the i8n messages
     * @param aFailureMessage the default error message
     */
    public static void validateRegEx(Errors aErrors, String aValue, String aRegEx, String aErrorCode, Object[] aValues, String aFailureMessage) {
        if (aValue != null && aRegEx != null && !aRegEx.equals("") && !aValue.trim().equals("")) {
            Perl5Compiler ptrnCompiler = new Perl5Compiler();
            Perl5Matcher matcher = new Perl5Matcher();
            try {
                Perl5Pattern aCharPattern = (Perl5Pattern) ptrnCompiler.compile(aRegEx);
                                
                if (!matcher.contains(aValue, aCharPattern)) {
                    aErrors.reject(aErrorCode, aValues, aFailureMessage);               
                    return;
                }
            }
            catch (MalformedPatternException e) {
                LogFactory.getLog(ValidatorUtil.class).fatal("Perl pattern malformed: pattern used was "+aRegEx +" : "+ e.getMessage(), e);
            }           
        }
    }
    
    /**
     * Helper method to validate a supplied URL is correctly formatted
     * @param aErrors The errors object to populate
     * @param aURL The URL to check
     * @param aErrorCode the error code for getting the i8n failure message
     * @param aValues the list of values to replace in the i8n messages
     * @param aFailureMessage the default error message
     */
    public static void validateURL(Errors aErrors, String aURL, String aErrorCode, Object[] aValues, String aFailureMessage) {        
        if(!UrlUtils.isUrl(aURL)) {
            aErrors.reject(aErrorCode, aValues, aFailureMessage);
        }
    }
    
    /**
     * Helper method to validate a new password for a user id.
     * @param aErrors The errors object to populate
     * @param aNewPwd the new password for the user id
     * @param aErrorCode the error code
     * @param aValues the values
     * @param aFailureMessage the default message
     */
    public static void validateNewPassword(Errors aErrors, String aNewPwd, String aErrorCode, Object[] aValues, String aFailureMessage) {
        if (aNewPwd != null && !aNewPwd.trim().equals("")) {
            Perl5Compiler ptrnCompiler = new Perl5Compiler();
            Perl5Matcher matcher = new Perl5Matcher();
            try {
                Perl5Pattern lcCharPattern = (Perl5Pattern) ptrnCompiler.compile("[a-z]");
                Perl5Pattern ucCharPattern = (Perl5Pattern) ptrnCompiler.compile("[A-Z]");
                Perl5Pattern numericPattern = (Perl5Pattern) ptrnCompiler.compile("[0-9]");
                
                if (aNewPwd.length() < 6) {
                    aErrors.reject(aErrorCode, aValues, aFailureMessage);
                    return;
                }
                
                if (!matcher.contains(aNewPwd, lcCharPattern)) {
                    aErrors.reject(aErrorCode, aValues, aFailureMessage);               
                    return;
                }
                
                if (!matcher.contains(aNewPwd, ucCharPattern)) {
                    aErrors.reject(aErrorCode, aValues, aFailureMessage);
                    return;
                }
                
                if (!matcher.contains(aNewPwd, numericPattern)) {
                    aErrors.reject(aErrorCode, aValues, aFailureMessage);
                    return;
                }

            }
            catch (MalformedPatternException e) {
                LogFactory.getLog(ValidatorUtil.class).fatal("Perl patterns malformed: " + e.getMessage(), e);
            }           
        }
    }
    
    /**
     * Helper method used to check string value 1 does not contain value 2.
     * @param aErrors the errors object to populate
     * @param val1 String value 1
     * @param val2 String value 2
     * @param aErrorCode the error code
     * @param aValues the values
     * @param aFailureMessage the default message
     */
    public static void validateValueNotContained(Errors aErrors, String val1, String val2, String aErrorCode, Object[] aValues, String aFailureMessage) {
        if (val1 != null && val2 != null) {
            if (val1.contains(val2) == false) {
                return;
            }
        }
        
        aErrors.reject(aErrorCode, aValues, aFailureMessage);
    }
    
    /** private constructor. */
    private ValidatorUtil() {
        super();
    }
    

    
}
