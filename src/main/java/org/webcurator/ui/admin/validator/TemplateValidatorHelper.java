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
package org.webcurator.ui.admin.validator;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.validation.Errors;
import org.webcurator.domain.model.core.PermissionTemplate;

/**
 * This class validates a template where all template variables 
 * can be checked and replaced
 *  
 * Usage example:
 *   StringTemplate template = new StringTemplate( "To {contact_name}");
 *   Map m = new HashMap();
 *   m.put("contact_name" , "A, User");
 *   String result = template.parse(m);
 *   
 *   // result will be "To A, User" 
 * 
 * @author bbeaumont
 */
public class TemplateValidatorHelper  {
    /** The pattern for matching variable names. **/
    private Pattern varPattern = Pattern.compile("(?<!\\{)\\{([^\\}]+)\\}(?!\\})");
    /** The template string. **/
    private String template;
    /** The template type to validate **/
    private String type;
    
    /**
     * Creates a new template.
     * @param template The template string.
     */
    public TemplateValidatorHelper(String template, String type) {
        this.template = template;
        this.type = type;
    }
    
    /**
     * Parse the template for all the varible names and check that they are valid.
     * @param aErrors the errors object to populate
     */
    public void parseForErrors(Errors aErrors) {
        String target = template;
        Matcher m = varPattern.matcher(target);
        while(m.find()) { 
            //find all the defined template attributes for replacement
            String varName = m.group(1);
            isAttributeValid(varName,aErrors);
            if (aErrors.hasErrors()) {
                aErrors.reject("template.defined.attributes");
            }
        }   
    }
    
   /** 
    * Replaces the variables in the template with the values provided
    * in the map.
    * @param values A map of values
    * @return The template string with all variables replaced. Missing
    *         variables will not be replaced.
    */
    public String parseTemplate(Map values) {
        String target = template;
        Matcher m = varPattern.matcher(target);
        while(m.find()) { 
            //find all the defined template attributes for replacement
            String varName = m.group(1);
            if( values.containsKey(varName) && values.get(varName) != null) {
                //replace the variables that have a value
                if (varName.equals("agency_logo_url")) {
                    //for the agency Logo, wrap it with HTML tags
                    String logoHTML = (String) values.get(varName);
                    logoHTML = "<img src=\""+logoHTML+"\">";
                    target = target.replaceAll( "\\{" + varName + "\\}", logoHTML);
                } else {
                    target = target.replaceAll( "\\{" + varName + "\\}", (String) values.get(varName));
                }
            } else if (values.containsKey(varName)) {
                //replace the variables that don't have a value with an empty string
                target = target.replaceAll( "\\{" + varName + "\\}", "");
            }
        } 
        return target;
    }
    
    /** 
     * Check that the variable name provided is valid.
     * @param varName the name to check
     * @param aErrors the errors object to populate
     */
    private void isAttributeValid(String varName, Errors aErrors) {
        if (varName == null || "".equals(varName)) {
        	// varName is empty
            aErrors.reject("template.attribute.empty","The template text contains an empty variable");
        } else {
            if ("contact_name".equalsIgnoreCase(varName)) {
                return;
            } else if ("contact_address".equalsIgnoreCase(varName)) {
                return;
            } else if ("site_name".equalsIgnoreCase(varName)) {
                return;
            } else if ("urls_plain".equalsIgnoreCase(varName)) {
                return;
            } else if ("urls_html".equalsIgnoreCase(varName)) {
                return;
            } else if ("user_name".equalsIgnoreCase(varName)) {
                return;
            } else if ("user_address".equalsIgnoreCase(varName)) {
                return;
            } else if ("user_phone".equalsIgnoreCase(varName)) {
                return;
            } else if ("user_email".equalsIgnoreCase(varName)) {
                return;
            } else if ("agency_name".equalsIgnoreCase(varName)) {
                return;
            } else if ("agency_address".equalsIgnoreCase(varName)) {
                return;
            } else if ("agency_phone".equalsIgnoreCase(varName)) {
                return;
            } else if ("agency_url".equalsIgnoreCase(varName)) {
                return;
            } else if ("agency_email".equalsIgnoreCase(varName)) {
                return;
            } else if ("agency_fax".equalsIgnoreCase(varName)) {
                return;
//            } else if ("user_position".equalsIgnoreCase(varName)) {
//                return;
            } else if ("agency_logo_url".equalsIgnoreCase(varName)) {
                if (type.equals(PermissionTemplate.EMAIL_TYPE_TEMPLATE)) {
                    //can't be used for an email template
                    Object[] vals = {varName};
                    aErrors.reject("template.attribute.invalid.type", vals, "The template text contains an invalid variable named "+varName+" for the template type");
                }
                return;
            } else {
                //the attribute is not one of the defined attributes
                Object[] vals = {varName};
                aErrors.reject("template.attribute.notfound", vals, "The template text contains an invalid variable named "+varName);
            }
        }        
    }
}
