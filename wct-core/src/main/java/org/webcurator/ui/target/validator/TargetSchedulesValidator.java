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
package org.webcurator.ui.target.validator;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.quartz.CronExpression;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.webcurator.domain.model.core.Schedule;
import org.webcurator.ui.common.validation.AbstractBaseValidator;
import org.webcurator.ui.common.validation.ValidatorUtil;
import org.webcurator.ui.target.command.TargetSchedulesCommand;


/**
 * Validate a schedule for a target.
 * @author bbeaumont
 */
public class TargetSchedulesValidator extends AbstractBaseValidator {

    private static Map<String,Integer> monthMap = new HashMap<String,Integer>(20);
    private static Map<String,Integer> dayMap = new HashMap<String,Integer>(60);
    
    //private static Pattern TIME_PATTERN = Pattern.compile("(\\d{1,2}):(\\d{2})");
    
	static {
        monthMap.put("JAN", new Integer(0));
        monthMap.put("FEB", new Integer(1));
        monthMap.put("MAR", new Integer(2));
        monthMap.put("APR", new Integer(3));
        monthMap.put("MAY", new Integer(4));
        monthMap.put("JUN", new Integer(5));
        monthMap.put("JUL", new Integer(6));
        monthMap.put("AUG", new Integer(7));
        monthMap.put("SEP", new Integer(8));
        monthMap.put("OCT", new Integer(9));
        monthMap.put("NOV", new Integer(10));
        monthMap.put("DEC", new Integer(11));

        dayMap.put("SUN", new Integer(1));
        dayMap.put("MON", new Integer(2));
        dayMap.put("TUE", new Integer(3));
        dayMap.put("WED", new Integer(4));
        dayMap.put("THU", new Integer(5));
        dayMap.put("FRI", new Integer(6));
        dayMap.put("SAT", new Integer(7));
    }	
	
	public boolean supports(Class clazz) {
		return TargetSchedulesCommand.class.equals(clazz);
	}

	public void validate(Object aCommand, Errors aErrors) {
		TargetSchedulesCommand command = (TargetSchedulesCommand) aCommand;
		
		if(TargetSchedulesCommand.ACTION_TEST.equals(command.getActionCmd()) || TargetSchedulesCommand.ACTION_SAVE.equals(command.getActionCmd())) {
			//ValidationUtils.rejectIfEmptyOrWhitespace(aErrors, "startDate", "required", getObjectArrayForLabel(TargetSchedulesCommand.PARAM_START_DATE), "Start Date is a required field");
			ValidationUtils.rejectIfEmptyOrWhitespace(aErrors, "startDate", "", getObjectArrayForLabel(TargetSchedulesCommand.PARAM_START_DATE), "From Date is a required field");
			ValidatorUtil.validateStartBeforeOrEqualEndTime(aErrors, command.getStartDate(), command.getEndDate(), "time.range", getObjectArrayForTwoLabels(TargetSchedulesCommand.PARAM_START_DATE, TargetSchedulesCommand.PARAM_END_DATE), "The start time must be before the end time.");
			validateCronExpression(aCommand, aErrors);
		}
		 
	}
	
	
	private void validateCronExpression(Object aCommand, Errors aErrors) {
		TargetSchedulesCommand command = (TargetSchedulesCommand) aCommand;
		boolean hasErrors = false;
		
		if(command.getScheduleType() > Schedule.CUSTOM_SCHEDULE) {
			return;
		}
		
		// Step one - make sure all fields have been provided.
		
		// If time is null, we need minutes and hours.
		if(command.getScheduleType() == 0) { 
			ValidationUtils.rejectIfEmptyOrWhitespace(aErrors, "minutes", "required", getObjectArrayForLabel(TargetSchedulesCommand.PARAM_MINUTES), "Minutes is a required field");
			ValidationUtils.rejectIfEmptyOrWhitespace(aErrors, "hours", "required", getObjectArrayForLabel(TargetSchedulesCommand.PARAM_HOURS), "Hours is a required field");
		}
		
		if(command.getScheduleType() < 0) { 
			if(command.getTime() == null) { 
				aErrors.rejectValue("time", "required", getObjectArrayForLabel(TargetSchedulesCommand.PARAM_TIME), "Time is a required field");
			}
		}

		ValidationUtils.rejectIfEmptyOrWhitespace(aErrors, "daysOfWeek", "required", getObjectArrayForLabel(TargetSchedulesCommand.PARAM_DAYS_OF_WEEK), "Days of Week is a required field");
		ValidationUtils.rejectIfEmptyOrWhitespace(aErrors, "daysOfMonth", "required", getObjectArrayForLabel(TargetSchedulesCommand.PARAM_DAYS_OF_MONTH), "Days of Month is a required field");
		ValidationUtils.rejectIfEmptyOrWhitespace(aErrors, "months", "required", getObjectArrayForLabel(TargetSchedulesCommand.PARAM_MONTHS), "Months is a required field");
		ValidationUtils.rejectIfEmptyOrWhitespace(aErrors, "years", "required", getObjectArrayForLabel(TargetSchedulesCommand.PARAM_YEARS), "Years is a required field");
		
		// If there are missing fields, there is no point doing any further
		// validation.
		if(aErrors.hasErrors()) {
			return;
		}
		
		// If time is not provided, then checks hours and minutes.
		if(command.getScheduleType() == 0) { 
			// Validate Minute Component
			if(!isValidMinuteComponent(command.getMinutes())) {
				aErrors.reject("cron.badMinutes", new Object[] {}, "Illegal minutes string");
				hasErrors = true;
			}
			
			// Validate Hours Component
			if(!isValidMinuteComponent(command.getHours())) {
				aErrors.reject("cron.badHours", new Object[] {}, "Illegal hours string");
				hasErrors = true;
			}
		}
		
		// Validate Days of Week Component
		if(!isValidDaysOfWeekComponent(command.getDaysOfWeek())) {
			aErrors.reject("cron.badDaysOfWeek", new Object[] {}, "Illegal Days of Week string");
			hasErrors = true;
		}
		
		// Validate Days of Month Component
		if(!isValidDaysOfMonthComponent(command.getDaysOfMonth())) {
			aErrors.reject("cron.badDaysOfMonth", new Object[] {}, "Illegal Days of Month string");
			hasErrors = true;
		}		
		
		// Validate Months Component
		if(!isValidMonthsComponent(command.getMonths())) {
			aErrors.reject("cron.badMonths", new Object[] {}, "Illegal Month string");
			hasErrors = true;
		}		
		
		// Validate Years Component
		if(!isValidYearsComponent(command.getYears())) {
			aErrors.reject("cron.badYears", new Object[] {}, "Illegal Years string");
			hasErrors = true;
		}	
		
		if( !"?".equals(command.getDaysOfMonth()) &&
			!"?".equals(command.getDaysOfWeek())) {
			aErrors.reject("cron.unsupported.daysofmonthweek");
			hasErrors = true;
		}
		
		// Run the CronExpression parser to see if there is anything we haven't
		// managed to catch.
		if(!hasErrors) {
			try {
				CronExpression cex = new CronExpression(command.getCronExpression());
				if(cex.getNextValidTimeAfter(new Date()) == null ) {
					aErrors.reject("cron.noFutureInstances");
				}
			}
			catch(ParseException ex) {
				aErrors.reject("parse.error", new Object[] { ex.getMessage() }, "Unknown Error");
			}
		}
	}
	
	/** The patterns to test for MINUTE testing **/
	public static String MINUTE_PATTERNS[] = new String[] {
		"^(\\d+)-(\\d+)$",
		"^(\\d+)$",
		"^\\*", "\\*/\\d+$",
		"^(\\d+)/\\d+$" };	
	
	/**
	 * Checks if the supplied minute component is valid.
	 * @param minutesString The minutes string.
	 * @return true if valid; otherwise false.
	 **/
	public static boolean isValidMinuteComponent(String minutesString) {
		String minutes = null;
		StringTokenizer tokenizer = new StringTokenizer(minutesString, ",");
		while(tokenizer.hasMoreTokens()) {
			boolean foundMatch = false;
						
			minutes = tokenizer.nextToken();
				
			// Check all the regular expressions.
			for(int ix=0; ix<MINUTE_PATTERNS.length; ix++) {
				Matcher m = Pattern.compile( MINUTE_PATTERNS[ix]).matcher(minutes);
				if(m.matches()) {
					foundMatch = true;
					// Check all the minute elements are in the right range.
					for(int g=1; g <= m.groupCount(); g++) {
						int val = Integer.parseInt(m.group(g));
						if( val < 0 || val > 59) { 
							foundMatch = false;
						}
					}
				}
			}	
			
			// Special test for range parsing
			Matcher m = Pattern.compile(MINUTE_PATTERNS[0]).matcher(minutes);
			if(m.matches()) {
				if(Integer.parseInt(m.group(1)) > Integer.parseInt(m.group(2))) {
					foundMatch = false;
				}
			}
			
			if(!foundMatch) {
				return false;
			}
		}	
		
		return true;
	}

	/** The patterns to test for MINUTE testing **/
	public static String HOUR_PATTERNS[] = new String[] {
		"^(\\d+)-(\\d+)$",		
		"^(\\d+)$",
		"^\\*$", 
		"^\\*/\\d+$",
		"^(\\d+)/\\d+" };	
	
	/**
	 * Checks if the supplied hour component is valid.
	 * @param hoursString The hour string.
	 * @return true if valid; otherwise false.
	 **/
	public static boolean isValidHoursComponent(String hoursString) {
		String hours = null;
		StringTokenizer tokenizer = new StringTokenizer(hoursString, ",");
		while(tokenizer.hasMoreTokens()) {
			boolean foundMatch = false;
			hours = tokenizer.nextToken();
				
			// Check all the regular expressions.
			for(int ix=0; ix<HOUR_PATTERNS.length; ix++) {
				Matcher m = Pattern.compile( HOUR_PATTERNS[ix]).matcher(hours);
				if(m.matches()) {
					foundMatch = true;
					for(int g=1; g <= m.groupCount(); g++) {
						int val = Integer.parseInt(m.group(g));
						if( val < 0 || val > 23) { 
							foundMatch = false;
						}
					}
				}
			}	
			
			// Special test for range parsing
			Matcher m = Pattern.compile(HOUR_PATTERNS[0]).matcher(hours);
			if(m.matches()) {
				if(Integer.parseInt(m.group(1)) > Integer.parseInt(m.group(2))) {
					foundMatch = false;
				}
			}
			
			if(!foundMatch) {
				return false;
			}
		}	
		
		return true;
	}	
	
	private static final String DAY_MATCH = "SUN|MON|TUE|WED|THU|FRI|SAT|[1-7]";
	private static final String[] DAY_OF_WEEK_PATTERNS = new String[] {
		"^(" +DAY_MATCH+ ")-(" +DAY_MATCH+ ")$",
		"^" +DAY_MATCH+ "$",
		"^\\*$",
		"^\\*/\\d+$",
		"^\\?$",
		"^(" +DAY_MATCH+ ")/\\d+$",
		"^L$",
		"^[1-7]L$",
		"^(" +DAY_MATCH+ ")#[1-5]$"
	};
	
	
	/**
	 * Checks if the supplied daysOfWeek component is valid.
	 * @param daysOfWeekString The daysOfWeek string.
	 * @return true if valid; otherwise false.
	 **/
	public static boolean isValidDaysOfWeekComponent(String daysOfWeekString) {
		
		String daysOfWeek = null;
		StringTokenizer tokenizer = new StringTokenizer(daysOfWeekString, ",");
		while(tokenizer.hasMoreTokens()) {
			boolean foundMatch = false;
			daysOfWeek = tokenizer.nextToken();
			
			// Check all the regular expressions.
			for(int ix=0; ix<DAY_OF_WEEK_PATTERNS.length; ix++) {
				if(Pattern.matches(DAY_OF_WEEK_PATTERNS[ix], daysOfWeek)) {
					foundMatch = true;
				}
			}	
			
			// Special test for range parsing
			Matcher m = Pattern.compile(DAY_OF_WEEK_PATTERNS[0]).matcher(daysOfWeek);
			if(m.matches()) {
				int day1 = isNumeric(m.group(1)) ? Integer.parseInt(m.group(1)) : dayMap.get(m.group(1));
				int day2 = isNumeric(m.group(2)) ? Integer.parseInt(m.group(2)) : dayMap.get(m.group(2));

				if(day1 > day2) {
					foundMatch = false;
				}
			}
			
			if(!foundMatch) {
				return false;
			}
		}	
		
		return true;
	}
	
	public static boolean isNumeric(String str) {
		return Pattern.matches("^\\d*$", str);
	}
	
	
	/** The patterns to test for DAYS_OF_MONTH_PATTERNS testing **/
	public static String DAYS_OF_MONTH_PATTERNS[] = new String[] {
		"^(\\d+)-(\\d+)$",		
		"^(\\d+)$",
		"^\\*$", 
		"^\\*/\\d+$",
		"^(\\d+)/\\d+",
		"^\\?$",
		"^L$",
		"^(\\d+)W$"};		

	/**
	 * Checks if the supplied daysOfMonth component is valid.
	 * @param daysOfMonthString The daysOfMonth string.
	 * @return true if valid; otherwise false.
	 **/
	public static boolean isValidDaysOfMonthComponent(String daysOfMonthString) {
		String daysOfMonth = null;
		StringTokenizer tokenizer = new StringTokenizer(daysOfMonthString, ",");
		while(tokenizer.hasMoreTokens()) {
			boolean foundMatch = false;
			daysOfMonth = tokenizer.nextToken();
				
			// Check all the regular expressions.
			for(int ix=0; ix<DAYS_OF_MONTH_PATTERNS.length; ix++) {
				Matcher m = Pattern.compile( DAYS_OF_MONTH_PATTERNS[ix]).matcher(daysOfMonth);
				if(m.matches()) {
					foundMatch = true;
					for(int g=1; g <= m.groupCount(); g++) {
						int val = Integer.parseInt(m.group(g));
						if( val < 1 || val > 31) { 
							foundMatch = false;
						}
					}
				}
			}	
			
			// Special test for range parsing
			Matcher m = Pattern.compile(DAYS_OF_MONTH_PATTERNS[0]).matcher(daysOfMonth);
			if(m.matches()) {
				if(Integer.parseInt(m.group(1)) > Integer.parseInt(m.group(2))) {
					foundMatch = false;
				}
			}
			
			if(!foundMatch) {
				return false;
			}
		}	
		
		return true;
	}	
	
	
	private static final String MONTH_STR_MATCH = "(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)";
	private static final String MONTH_NUM_MATCH = "([1-9]|10|11|12)";
	
	private static final String[] MONTH_PATTERNS = new String[] {
		"^"+MONTH_STR_MATCH +"$",
		"^"+MONTH_NUM_MATCH +"$",
		"^\\*$",
		"^\\*/\\d+$",
		"^("+MONTH_STR_MATCH+")/\\d+$",
		"^("+MONTH_NUM_MATCH+")/\\d+$"
	};		
	
	private static final Pattern MONTH_STR_RANGE = Pattern.compile("^" + MONTH_STR_MATCH + "-" + MONTH_STR_MATCH +"$");
	private static final Pattern MONTH_NUM_RANGE = Pattern.compile("^" + MONTH_NUM_MATCH + "-" + MONTH_NUM_MATCH +"$");
	
	/**
	 * Checks if the supplied months component is valid.
	 * @param monthsString The months string.
	 * @return true if valid; otherwise false.
	 **/
	public static boolean isValidMonthsComponent(String monthsString) {
		StringTokenizer tokenizer = new StringTokenizer(monthsString, ",");
		String months = null;
		while(tokenizer.hasMoreTokens()) {
			boolean foundMatch = false;
			months = tokenizer.nextToken();
			
			// Check all the regular expressions.
			for(int ix=0; ix<MONTH_PATTERNS.length; ix++) {
				if(Pattern.matches(MONTH_PATTERNS[ix], months)) {
					foundMatch = true;
					break;
				}
			}
			
			Matcher m = MONTH_STR_RANGE.matcher(months);
			if(m.matches()) {
				int firstMonth = monthMap.get(m.group(1));
				int secondMonth = monthMap.get(m.group(2));
				
				if(secondMonth >= firstMonth) {
					foundMatch = true;
				}
			}
			
			m = MONTH_NUM_RANGE.matcher(months);
			if(m.matches()) {
				int firstMonth = Integer.parseInt(m.group(1));
				int secondMonth = Integer.parseInt(m.group(2));
				
				if(secondMonth >= firstMonth) {
					foundMatch = true;
				}
			}			
			
			if(!foundMatch) { 
				return false;
			}
		}	
		
		return true;
	}
	
	
	/** The patterns to test for DAYS_OF_MONTH_PATTERNS testing **/
	public static String YEARS_PATTERNS[] = new String[] {
		"^(\\d+)-(\\d+)$",		
		"^(\\d+)$",
		"^\\*$", 
		"^\\*/\\d+$",
		"^(\\d+)/\\d+",
	};		
	
	
	/**
	 * Checks if the supplied years component is valid.
	 * @param yearsString The years string.
	 * @return true if valid; otherwise false.
	 **/
	public static boolean isValidYearsComponent(String yearsString) {
		StringTokenizer tokenizer = new StringTokenizer(yearsString, ",");
		String years = null;
		while(tokenizer.hasMoreTokens()) {
			boolean foundMatch = false;
			years = tokenizer.nextToken();
			
			// Check all the regular expressions.
			for(int ix=0; ix<YEARS_PATTERNS.length; ix++) {
				if(Pattern.matches(YEARS_PATTERNS[ix], years)) {
					foundMatch = true;
					break;
				}
			}

			// Special test for range parsing
			Matcher m = Pattern.compile(YEARS_PATTERNS[0]).matcher(years);
			if(m.matches()) {
				if(Integer.parseInt(m.group(1)) > Integer.parseInt(m.group(2))) {
					foundMatch = false;
				}
			}			
		
			if(!foundMatch) { 
				return false;
			}
		}	
		
		return true;
	}		

	
}
