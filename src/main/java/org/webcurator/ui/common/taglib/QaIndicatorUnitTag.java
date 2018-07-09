package org.webcurator.ui.common.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import org.webcurator.core.harvester.agent.HarvesterStatusUtil;
import org.webcurator.core.util.ConverterUtil;

public class QaIndicatorUnitTag extends TagSupport {

	/**
	 * to support serialisation
	 */
	private static final long serialVersionUID = 1L;

    private String unit;
    
    private String value;
    
    /**
     *	Output a value and the measurement unit with appropriate scaling 
     */
 	@Override
	public int doStartTag() throws JspException  {
				
		String output = null;
		
		if (unit.equals("integer")) {
			output = new Integer((new Float(value)).intValue()).toString();
		} else if (unit.equals("millisecond")) {
			output = getElapsedTime();
		} else if (unit.equals("byte")) {
			Long bytes;
			// scale the number of bytes as appropriate
			String[] decimal = value.split("\\.");
			if (decimal.length > 1) {
				bytes = Long.parseLong(decimal[0]);
			} else {
				bytes = Long.parseLong(value);
			}
			output = ConverterUtil.formatBytes(bytes);
		}
		
		try {
			pageContext.getOut().print(output);
		} catch (IOException e) {
			throw new JspException(e);
		}
		
		return TagSupport.SKIP_BODY;
	}
	
	public void setUnit(String unit) {
		this.unit = unit;
	}

	public void setValue(String value) {
		this.value = value;
	}

	private String getElapsedTime() {
		return HarvesterStatusUtil.formatTime((new Float(value)).longValue());
	}

}
