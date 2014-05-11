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
package org.webcurator.auth.tag;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.TagSupport;

public class ShowControlFalseTag extends TagSupport {

	private static final long serialVersionUID = 3505334273764677570L;
	
    @Override
    public int doStartTag() throws JspException {  
    	if(getParent() instanceof ShowControlTag) {
    		if(((ShowControlTag)getParent()).isShowControl()) {
    			return TagSupport.SKIP_BODY;
    		}
    		else {
    			return TagSupport.EVAL_BODY_INCLUDE;
    		}
    	}
    	else {
    		throw new JspTagException("True outside ShowControl");
    	}
    }

}
