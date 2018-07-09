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
package org.webcurator.ui.target.command;

import java.beans.PropertyEditorSupport;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimeEditor extends PropertyEditorSupport {

	private static final Pattern TIME_PATTERN = Pattern.compile("^\\s*(\\d{1,2}):(\\d{2})\\s*$");
	
	private boolean required;
	
	public TimeEditor(boolean required) {
		this.required = required;
	}
	
	
	@Override
	public String getAsText() {
		return getValue().toString();
	}

	
	
	@Override
	public void setAsText(String val) throws IllegalArgumentException {
		if(!required && (val == null || val.trim().length() == 0)) {
			setValue(null);
		}
		else {
			Matcher m = TIME_PATTERN.matcher(val);
			if(m.matches()) {
				int hours = Integer.parseInt(m.group(1));
				int minutes = Integer.parseInt(m.group(2));
				if(hours < 0 || hours > 23) {
					throw new IllegalArgumentException("Hours must be between 0 and 23");
				}
				if(minutes < 0 || minutes > 59) { 
					throw new IllegalArgumentException("Minutes must be between 0 and 59");
				}
				
				Time t = new Time(hours, minutes);
				setValue(t);
			}
			else {
				throw new IllegalArgumentException("Time must be specified in hh:mm format");
			}
		}
	}

}
