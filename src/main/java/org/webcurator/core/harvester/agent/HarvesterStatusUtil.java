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

import java.text.DecimalFormat;

/**
 * This utility class has methods for rendering harvester status information in 
 * a human readable format.
 * @author nwaight
 */
public class HarvesterStatusUtil {
	/**
	 * Format the time in milliseconds into a string in the format dd:hh:mm:ss
	 * @param aTime an elapsed time in ms
	 * @return the formated time string
	 */
	public static String formatTime(long aTime) {
        long remainder = 0;
        long days = 0;
        long hours = 0;
        long minutes = 0;
        long seconds = 0;
        
        days = aTime / 86400000;
        remainder = aTime - (days * 86400000);
        
        hours = remainder / 3600000;
        remainder = remainder - (hours * 3600000);
        
        minutes = remainder / 60000;
        remainder = remainder - (minutes * 60000);
        
        seconds = remainder / 1000;

        DecimalFormat df = new DecimalFormat("00");
        
        return df.format(days) + ":" + df.format(hours) + ":" + df.format(minutes) + ":" + df.format(seconds);
	}
	
	/**
	 * Format the amount of data in bytes to a nice readable string
	 * @param aAmount the amount in bytes
	 * @return a nice data size string
	 */
	public static String formatData(double aAmount) {
		DecimalFormat df = new DecimalFormat("#,###,###.##");
        String type = "";
        double niceDataDown = 0;
        double dataDown = aAmount;        
        
        if (dataDown < 1024) {
            niceDataDown = dataDown;
            type = " bytes";
        }
        else if ((dataDown / 1024) < 1024) {
            niceDataDown = dataDown / 1024;
            type = " KB";
        }
        else if ((dataDown / (1024 * 1024)) < 1024) {
            niceDataDown = dataDown / (1024 * 1024);
            type = " MB";
        }
        else if ((dataDown / (1024 * 1024 * 1024)) < 1024) {
            niceDataDown = dataDown / (1024 * 1024 * 1024);
            type = " GB";
        }               
        
        return df.format(niceDataDown) + type;
	}
}
