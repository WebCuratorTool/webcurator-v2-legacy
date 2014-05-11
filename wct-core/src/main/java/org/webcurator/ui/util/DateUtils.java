package org.webcurator.ui.util;

import java.beans.PropertyEditor;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;

public class DateUtils implements MessageSourceAware {
	private static DateUtils instance = null;
	private MessageSource messageSource = null;
	
	private DateUtils() { 
	}
	
	public static DateUtils get() {
		if(instance == null) { 
			instance = new DateUtils();
		}
		return instance;
	}
	
	public SimpleDateFormat getDateFormat(String type) { 
		String format = messageSource.getMessage(type, new Object[] {}, Locale.getDefault());
		SimpleDateFormat df = new SimpleDateFormat(format);
		df.setLenient(false);
		return df;
	}

	public PropertyEditor getDateEditor(String style, boolean allowEmpty) {
		String format = messageSource.getMessage(style, new Object[] {}, Locale.getDefault());
		SimpleDateFormat df = new SimpleDateFormat(format);
		df.setLenient(false);
		return new CustomDateEditor(df, allowEmpty, format.length());
	}
	
	
	public PropertyEditor getFullDateTimeEditor(boolean allowEmpty) {
		return getDateEditor("core.common.fullDateTimeMask", allowEmpty);
	}
	
	public PropertyEditor getFullDateEditor(boolean allowEmpty) {
		return getDateEditor("core.common.fullDateMask", allowEmpty);
	}	
	
	public PropertyEditor getFullTimeEditor(boolean allowEmpty) {
		return getDateEditor("core.common.fullTimeMask", allowEmpty);
	}		

	public String format(Date dt, String type) { 
		String format = messageSource.getMessage(type, new Object[] {}, Locale.getDefault());
		SimpleDateFormat df = new SimpleDateFormat(format);
		return dt == null ? "" : df.format(dt);
	}
	
	public String formatFullDateTime(Date dt) {
		return format(dt, "core.common.fullDateTimeMask");
	}
	
	public String formatFullDate(Date dt) {
		return format(dt, "core.common.fullDateMask");
	}	
	
	public String formatLongDateTime(Date dt) { 
		return format(dt, "core.common.longDateTimeMask");
	}
	
	public String formatShortDateTime(Date dt) { 
		return format(dt, "core.common.shortDateTimeMask");
	}

	public String formatShortDate(Date dt) { 
		return format(dt, "core.common.shortDateMask");
	}

	public String formatFullTime(Date dt) { 
		return format(dt, "core.common.fullTimeMask");
	}
	

	/**
	 * @param messageSource the messageSource to set
	 */
	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}	

}
