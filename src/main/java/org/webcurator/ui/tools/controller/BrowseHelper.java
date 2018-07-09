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
package org.webcurator.ui.tools.controller;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.archive.net.UURI;
import org.archive.net.UURIFactory;
import org.archive.wayback.replay.TagMagix;
import org.archive.wayback.archivalurl.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;

/**
 * The BrowseHelper is responsible for performing replacement of URLs within
 * resources so that the URL points back to the browse controller.
 * 
 * The BrowseHelper is configured within the Spring configuration. It takes
 * two types of replacements:
 *  - Generic regular expressions for replacements, per content type.
 *  - HTML Tag/Attribute replacements for HTML content. These utilise the 
 *    TagMagix class from the Wayback tool to generate complex regular
 *    expressions to support the different types (or lack of) quoting for
 *    attributes.
 * 
 * @author bbeaumont
 *
 */
public class BrowseHelper {
	/** The logger for the BrowseHelper. **/
	private static Log log = LogFactory.getLog(BrowseHelper.class);
	
	/** The prefix that will be used in the replacements. **/
	private String prefix;
	
	/** A Map of Content-Type to replacement patterns. **/
	private Map<String, List<Pattern>> contentTypePatterns = new HashMap<String, List<Pattern>>();
	
	/** A List of TagMagix expressions to run on HTML content. **/
	private List<TagMagixHelper> htmlTagPatterns = new LinkedList<TagMagixHelper>();
	
	private List<StringReplacer> urlConversionReplacements = null;
	private boolean useUrlConversionReplacements = false;
	
	/**
	 * Set the prefix for rewritten URLs.
	 * @param prefix The prefix for rewritten URLs.
	 */
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	/**
	 * Sets the replacement patterns for generic regular expressions. Each 
	 * pattern must have a single match group that consists of the URL to be
	 * rewritten. 
	 * The patterns are set through the Spring configuration.
	 * @param patterns A Map of Content-Type -> List of pattern strings.
	 */
	public void setContentTypePatterns(Map<String, List<String>> patterns) {
		for(String contentType:patterns.keySet()) {
			if(!contentTypePatterns.containsKey(contentType)) {
				contentTypePatterns.put(contentType, new LinkedList<Pattern>());
			}
			List<Pattern> l = contentTypePatterns.get(contentType);
			
			for(String strPattern: patterns.get(contentType)) {
				l.add(Pattern.compile(strPattern));
			}
		}
	}
	
	/**
	 * A helper method to create replacement URLs for HTML tag/attribute
	 * pairs. 
	 * The patterns are set in the Spring configuration in the format
	 * tag:attribute.
	 * 
	 * 
	 * @param patterns A list of patterns in the format "TAG:ATTRIBUTE"
	 */
	public void setHtmlTagPatterns(List<String> patterns) {
		String tag = null;
		String attr= null;
		for(String expr : patterns) {
			tag = expr.substring(0, expr.indexOf(':'));
			attr = expr.substring(expr.indexOf(':') + 1);
			htmlTagPatterns.add( new TagMagixHelper(tag, attr));
		}
	}

	/**
	 * This utilises the TagMagix class from the Wayback tool to
	 * generate patterns that can cope with the differing types of quoting
	 * of attributes.
	 * Due to the Wayback TagMagix class defining the getPattern method as
	 * private, this method uses reflection to get access. 
	 * @param tag The tag name.
	 * @param attr The attribute name.
	 * @return A pattern to capture the tag.
	 * 
	 * TODO Check whether this is the only way to get access to this method.
	 */
	public static Pattern getTagMagixPattern(String tag, String attr) {
		// Wayback defines a very useful method for building patterns
		// to capture HTML tag/attribute combinations. However, the method
		// is private, so we need to use special reflection techniques to
		// get access to it.
		try {
			Class<TagMagix> c = TagMagix.class;
			Method m = c.getDeclaredMethod("getPattern", String.class, String.class);
			m.setAccessible(true);
			return (Pattern) m.invoke(null, tag, attr);

		}
		catch(NoSuchMethodException ex) {
			// Should never occur since we know the method is there.
			log.fatal("Could not find 'getPattern' method on TagMagix");
		}
		catch(IllegalAccessException ex) {
			// Should never occur since we've changed to accessibility.
			log.fatal("Could not access 'getPattern' method on TagMagix");
		}
		catch(InvocationTargetException ex) {
			log.fatal("Error invoking 'getPattern' method", ex);
		}
		catch(SecurityException ex) {
			log.fatal("Failed to allow access to 'getPattern' method on TagMagix");
		}
		
		// Receive an exception, so return null
		return null;
	}
	
	
	/**
	 * Checks whether any replacements are to be made for the given content-type.
	 * @param contentType The Content-Type of the resource.
	 * @return True if there are replacement patterns set; otherwise false.
	 */
	public boolean isReplaceable(String contentType) {
		return contentTypePatterns.containsKey(contentType);
	}	
	
	/**
	 * Iterates through all the patterns for the given content type and 
	 * replaces the URLs with a rewritten URL that points back to the 
	 * BrowseController.
	 * 
	 * @param content The resource content as a StringBuilder.
	 * @param contentType The content-type of the resource.
	 * @param hrOid The OID of the harvest result, used for constructing the 
	 *              rewritten URL.
	 * @param currentResource The URL of the current resource.
	 */
	public void fix(StringBuilder content, String contentType, long hrOid, String currentResource) {

		String resourcePrefix = prefix + "/" + hrOid + "/";
		String jsPrefix = prefix.replaceFirst("curator/tools/browse", "replay/client-rewrite.js");
		
		// The Wayback 1.2 tool adds a <base href="..."> tag after the <head> tag 
		// provided there is not one already there. So we'll do the same.
		if( "text/html".equals(contentType)) {
			String existingBaseHref = TagMagix.getBaseHref(content);
			if (existingBaseHref == null) {
				insertAtStartOfHead(content, "<base href=\"" + currentResource + "\" />");
			} else {
				currentResource = existingBaseHref;
			}
		}

		// Perform TagMagix replacements for html content.
		ArchivalUrlResultURIConverter resURIConverter = new ArchivalUrlResultURIConverter();
		resURIConverter.setReplayURIPrefix(resourcePrefix);
		if( "text/html".equals(contentType)) {
			for(TagMagixHelper helper : htmlTagPatterns) {
				TagMagix.markupTagREURIC(content, resURIConverter, "", currentResource, helper.tag, helper.attribute);
			}
		}
		
		// Perform regular expression pattern matching replacements for HTML content.
		if( contentTypePatterns.containsKey(contentType)) {
			for(Pattern pattern: contentTypePatterns.get(contentType)) {
				Matcher matcher = pattern.matcher(content);
				
				int idx = 0;
				while(matcher.find(idx)) {
					String currentVal = matcher.group(1);
					
					if(!"".equals(currentVal.trim())) {

						String newVal = convertUrl(hrOid, currentResource, currentVal);
						
						log.debug("Replacing " + matcher.group(1) + " with " + newVal);
						
						int origAttrLength = currentVal.length();
						int attrStart = matcher.start(1);
						int attrEnd = matcher.end(1);
						int delta = newVal.length() - origAttrLength;
						
						content.replace(attrStart, attrEnd, newVal);
						idx = matcher.end(0) + delta;						
					}
					else {
						idx = matcher.end(0);
					}
				}
			}
		}

		// The Wayback 1.2 tool also inserts some custom JavaScript to be
		// executed on the client side, which performs further URI replacements.
		// So we'll do the same.
		if( "text/html".equals(contentType)) {
			StringBuilder toInsert = new StringBuilder(300);
			toInsert.append("<script type=\"text/javascript\">\n");
			toInsert.append("  var sWayBackCGI = \"" + resourcePrefix + "\";\n");
			toInsert.append("</script>\n");
			toInsert.append("<script type=\"text/javascript\" src=\"" + jsPrefix + "\" ></script>\n");
			insertAtEndOfBody(content, toInsert.toString());
		}
		
		// sites sometimes use the javascript:'top.location = self.location;' or 'window.location = ..' to issue a client side redirect
		// but this will cause the target instance list to be redirected when using the browser tool in the 
		// webpage preview (iframe).  We add a comment to this javascript to prevent the redirect.
		if( "text/html".equals(contentType) || "application/javascript".equals(contentType)) {
			Map<String, String> tokens = BrowseController.getFixTokens();
			Iterator<String> keys = tokens.keySet().iterator();
			
			while (keys.hasNext()) {
			
				String token = keys.next();
				int startIdx = content.toString().toLowerCase().indexOf(token);
				if (startIdx != -1) {
					int endIdx = startIdx + token.length();
					content.replace(startIdx, endIdx, tokens.get(token));
				};
			}
		}
	}	
	
	public String convertUrl(Long hrOid, String currentResource, String urlToConvert) {
		String resourcePrefix = prefix + "/" + hrOid + "/";
		String absUrl = getAbsURL(currentResource, urlToConvert);
		
		if(useUrlConversionReplacements) {
			if(urlConversionReplacements != null && !urlConversionReplacements.isEmpty()) {
				for(StringReplacer replacer: urlConversionReplacements) { 
					absUrl = replacer.replace(absUrl);
				}
			}
		}
		
		return resourcePrefix + absUrl; 		
	}
	
	/**
	 * Gets the absolute URL based on the URL string and the current resource.
	 * This method handles relative URLs.
	 * 
	 * @param currentUrl The URL of the current resource.
	 * @param subUrl The URL to be rewritten.
	 * @return The absolute URL of the resource.
	 */
	public static String getAbsURL(String currentUrl, String subUrl) {
		String replacedUrl = subUrl.trim();
		try {
			UURI base = UURIFactory.getInstance(currentUrl);
			UURI absUURI = UURIFactory.getInstance(base, replacedUrl);
			
			// does subUrl have a fragment (a.k.a a ref or jump tag) ?
			URI uri = null;
			try
			{
				uri = new URI(replacedUrl);
			}
			catch(URISyntaxException uriEx)
			{
				//Tried to parse and failed, attempt to URL encode
				String strUri = URLEncoder.encode(replacedUrl, "UTF-8");
				//put the fragment marker back (if there was one)
				Pattern p = Pattern.compile("%23");
				Matcher m = p.matcher(strUri);
				uri = new URI(m.replaceFirst("#"));
			}
			
			String fragment = uri.getFragment();
			if (fragment==null) 
			{
				return absUURI.getEscapedURI();			
			} 
			else 
			{
				return absUURI.getEscapedURI() + "#" + fragment;			
			}
		}
		catch(Exception ex) {
			log.error("Error parsing URI '"+replacedUrl+"': ", ex);
			return null;
		}
	}
	
	private class TagMagixHelper {
		private String tag;
		private String attribute;
		
		public TagMagixHelper(String tag, String attribute) {
			this.tag = tag;
			this.attribute = attribute;
		}
		
		public String getAttribute() {
			return attribute;
		}
		public String getTag() {
			return tag;
		}
		
	}

	/**
	 * @param urlConversionReplacements the urlConversionReplacements to set
	 */
	public void setUrlConversionReplacements(List<StringReplacer> urlConversionReplacements) {
		this.urlConversionReplacements = urlConversionReplacements;
	}

	/**
	 * Following helper methods borrowed and modified from HTMLPage class
	 * in wayback-core source.
	 */
	
	/**
	 * @param sb
	 * @param toInsert
	 */	
	public void insertAtStartOfHead(StringBuilder sb, String toInsert) {
		int insertPoint = TagMagix.getEndOfFirstTag(sb,"head");
		if (-1 == insertPoint) {
			insertPoint = 0;
		}
		sb.insert(insertPoint,toInsert);
	}

	/**
	 * @param sb
	 * @param toInsert
	 */
	public void insertAtEndOfBody(StringBuilder sb, String toInsert) {
		int insertPoint = sb.lastIndexOf("</body>");
		if (-1 == insertPoint) {
			insertPoint = sb.lastIndexOf("</BODY>");
		}
		if (-1 == insertPoint) {
			insertPoint = sb.length();
		}
		sb.insert(insertPoint,toInsert);
	}
	/**
	 * @param toInsert
	 */
	public void insertAtStartOfBody(StringBuilder sb, String toInsert) {
		int insertPoint = TagMagix.getEndOfFirstTag(sb,"body");
		if (-1 == insertPoint) {
			insertPoint = 0;
		}
		sb.insert(insertPoint,toInsert);
	}

	public boolean isUseUrlConversionReplacements() {
		return useUrlConversionReplacements;
	}

	public void setUseUrlConversionReplacements(boolean useUrlConversionReplacements) {
		this.useUrlConversionReplacements = useUrlConversionReplacements;
	}
	
}
