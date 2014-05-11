package org.webcurator.ui.common.taglib;

import java.io.IOException;
import java.util.Date;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import org.webcurator.core.harvester.agent.HarvesterStatusUtil;
import org.webcurator.ui.util.DateUtils;

public class DateTag extends TagSupport {

	/** Serial Version ID for Serialisation */
	private static final long serialVersionUID = 1L;
	private Date value;
	private String type;


	@Override
	public int doStartTag() throws JspException  {
		try {
			if("fullDateTime".equals(type)) {
				pageContext.getOut().print(DateUtils.get().formatFullDateTime(value));
			}
			else if("shortDateTime".equals(type)) { 
				pageContext.getOut().print(DateUtils.get().formatShortDateTime(value));
			}
			else if("shortDate".equals(type)) { 
				pageContext.getOut().print(DateUtils.get().formatShortDate(value));
			}
			else if("longDateTime".equals(type)) {
				pageContext.getOut().print(DateUtils.get().formatLongDateTime(value));
			}
			else if("fullDate".equals(type)) {
				pageContext.getOut().print(DateUtils.get().formatFullDate(value));
			}
			else if("fullTime".equals(type)) {
				pageContext.getOut().print(DateUtils.get().formatFullTime(value));
			}			
			else {
				throw new IllegalArgumentException("Illegal Type provided");
			}
		}
		catch(IOException ex) { 
			throw new JspException(ex);
		}
		
		return TagSupport.SKIP_BODY;
	}
	
	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}


	/**
	 * @param value the value to set
	 */
	public void setValue(Date value) {
		this.value = value;
	}	

}
