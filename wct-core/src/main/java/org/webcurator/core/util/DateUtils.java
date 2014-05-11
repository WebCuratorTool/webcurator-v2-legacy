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
package org.webcurator.core.util;

import java.util.Calendar;
import java.util.Date;

/**
 * A set of useful date methods.
 * @author bbeaumont
 */
public final class DateUtils {
    /**
     * Determine the earliest date of the two provided.
     * @param aDate1 The first date
     * @param aDate2 The second date
     * @return The earliest date.
     */
    public static Date earliestDate(Date aDate1, Date aDate2) {
    	return aDate1.after(aDate2) ? aDate2 : aDate1; 
    }
    
    /**
     * Determine the latest date of the two provided.
     * @param aDate1 The first date
     * @param aDate2 The second date
     * @return The earliest date.
     */
    public static Date latestDate(Date aDate1, Date aDate2) {
    	return aDate1.after(aDate2) ? aDate1 : aDate2; 
    }
    
    /**
     * Return the Date part (nil out the time).
     * @param aDate
     * @return The date with the time part reset.
     **/
    public static Date getDatePart(Date aDate) {
    	Calendar baseCal = Calendar.getInstance();
    	Calendar retCal = Calendar.getInstance();
    	
    	baseCal.setTime(aDate);
    	    	
    	retCal.clear();
    	retCal.set(baseCal.get(Calendar.YEAR), baseCal.get(Calendar.MONTH), baseCal.get(Calendar.DATE));
    	
    	return retCal.getTime();   
    }     
}
