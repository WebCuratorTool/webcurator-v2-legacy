package org.webcurator.ui.common.taglib;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import org.webcurator.domain.model.core.Indicator;

/**
 * Formats a <code>List</code> strings into a comma delimited list and applies the ellipsis symbol, followed by suffix: (+n others) 
 * The list will be in the form of: string1, string2, stri... (+n others) 
 * @author twoods
 *
 */
public class EllipsisTag extends TagSupport {

	/**
	 * the ellipsis mark to append
	 */
	private final static String ELLIPSIS = "..."; 

	/**
	 * to support serialisation
	 */
	private static final long serialVersionUID = 1;
	
	/**
	 * the string value that will be rendered
	 */
	private Set<String> strings;
	
	/**
	 * the length of the string value before ellipsis is applied
	 */
	private Integer length;
    
 	@Override
	public int doStartTag() throws JspException  {
		
 		StringBuilder builder = new StringBuilder();
 		Integer listSize = strings.size();
 		
 		Iterator<String> it = strings.iterator();
 		int count = 0;
 		// build the output string
 		while (it.hasNext()) {
 			count++;
 			String value = it.next();
 			if (listSize < 2 || listSize == count) {
 				builder.append(value);
 			} else {
 				builder.append(value).append(", ");
 			}
 		}
 		
 		StringBuilder output = new StringBuilder();
 		int stringLength;
 		if (length > builder.length()) {
 			output.append(builder);
 		} else {
 			// apply ellipsis
 			stringLength = length;
 			output.append(builder.substring(0, stringLength));
 			output.append(ELLIPSIS);
 			
 	 		// count the spaces
 	 		String[] words = output.toString().split(" ");
 	 		int spaces = words.length - 1;
 	 		
 	 		// calculate the value for the "(+n others)" suffix
 	 		int others = listSize - spaces - 1;
 	 		
 			// append the suffix
 	 		if (others == 1) {
 	 			output.append(" (+");
 	 			output.append(others);
 	 			output.append(" other)");
 	 		} else if (others > 1) {
 	 			output.append(" (+");
 	 			output.append(others);
 	 			output.append(" others)");
 	 		}
 		}
 		
		try {
			pageContext.getOut().print(output);
		} catch (IOException e) {
			throw new JspException(e);
		}
		
		// release resources to prevent memory leak
		strings = null;
		length = null;
		
		return TagSupport.SKIP_BODY;
	}
	
 	/**
 	 * the string values to be rendered
 	 * @param strings
 	 */
	public void setStrings(Set<String> strings) {
		this.strings = strings;
	}

	/**
	 * the length of the output before the ellipsis symbol is applied
	 * @param length
	 */
	public void setLength(Integer length) {
		this.length = length;
	}


}
