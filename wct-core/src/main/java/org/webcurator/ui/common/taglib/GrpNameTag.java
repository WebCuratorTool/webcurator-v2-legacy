package org.webcurator.ui.common.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

public class GrpNameTag extends TagSupport {
	/** Serial Version ID for Serialisation */
	private static final long serialVersionUID = -2540533017084337913L;
	private String name;
	private String subGroupSeparator;

	@Override
	public int doStartTag() throws JspException {
		try {
			if (name.contains(subGroupSeparator)) {
				int sepIndex = name.lastIndexOf(subGroupSeparator);
				String parentName = name.substring(0, sepIndex);
				String subGroupName = name.substring(sepIndex + subGroupSeparator.length());

				pageContext.getOut().print("<div class=\"subGroupParent\">");
				pageContext.getOut().print(parentName);
				pageContext.getOut().print("</div>");

				pageContext.getOut().print(subGroupSeparator);
				pageContext.getOut().print("<div class=\"subGroupChild\">");
				pageContext.getOut().print(subGroupName);
				pageContext.getOut().print("</div>");
			} else {
				pageContext.getOut().print(name);
			}
		} catch (IOException ex) {
			throw new JspException(ex);
		}

		return TagSupport.SKIP_BODY;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setSubGroupSeparator(String subGroupSeparator) {
		this.subGroupSeparator = subGroupSeparator;
	}

}
