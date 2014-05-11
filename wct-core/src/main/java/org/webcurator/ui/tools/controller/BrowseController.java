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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.Header;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractCommandController;
import org.webcurator.core.exceptions.DigitalAssetStoreException;
import org.webcurator.core.store.tools.QualityReviewFacade;
import org.webcurator.domain.model.core.HarvestResourceDTO;
import org.webcurator.ui.tools.command.BrowseCommand;

/**
 * The BrowseController is responsible for handling the Browse Quality Review
 * tool. This controller is responsible for reading the requested resource from
 * the DigitalAssetStore and performing any replacements that have been
 * registered with the BrowseHelper through the Spring configuration.
 * 
 * @author bbeaumont
 */
public class BrowseController extends AbstractCommandController {
	/** Logger for the BrowseController. **/
	private static Log log = LogFactory.getLog(BrowseController.class);

	/** The BrowseHelper handles replacement of URLs in the resources. **/
	private BrowseHelper browseHelper = null;
	
	/** The QualityReviewFacade for this controller. **/
	private QualityReviewFacade qualityReviewFacade = null;
	
	private final int MAX_MEMORY_SIZE = 1024 * 1024;
	// private final int MAX_MEMORY_SIZE = 0;

	/** The buffer size for reading from the file. */
	private final int BYTE_BUFFER_SIZE = 1024 * 8;
	
	private static Pattern p = Pattern.compile("\\/(\\d+)\\/(.*)");

	private static Pattern CHARSET_PATTERN = Pattern
			.compile(";\\s+charset=([A-Za-z0-9].[A-Za-z0-9_\\-\\.:]*)");

	private static Charset CHARSET_LATIN_1 = Charset.forName("ISO-8859-1");

	/**
	 * Map containing tokens (eg: browser redirect fragment) that will be
	 * replaced during the browser helper fix operation
	 */
	private static Map<String, String> fixTokens = null;

	/**
	 * Sets the BrowseHelper for the controller. This is primarily called from
	 * the Spring configuration.
	 * 
	 * @param browseHelper
	 *            The browseHelper that the controller should use.
	 */
	public void setBrowseHelper(BrowseHelper browseHelper) {
		this.browseHelper = browseHelper;
	}


	/**
	 * Sets the QualityReviewFacade for the controller. The facade is set by the
	 * Spring configuration.
	 * 
	 * @param qualityReviewFacade
	 *            The facade the controller should use.
	 */
	public void setQualityReviewFacade(QualityReviewFacade qualityReviewFacade) {
		this.qualityReviewFacade = qualityReviewFacade;
	}

	/**
	 * Default constructor.
	 */
	public BrowseController() {
		this.setCommandClass(BrowseCommand.class);
	}

	private String getHeaderValue(Header[] headers, String key) {
		if (headers != null) {
			for (Header h : headers) {
				if (key.equalsIgnoreCase(h.getName())) {
					return h.getValue();
				}
			}
		}
		return null;
	}
	
	
	/**
	 * Get everything before the semi-colon.
	 * 
	 * @param realContentType
	 *            The full content type from the Heritrix ARC file.
	 * @return The part of the content type before the semi-colon.
	 */
	private String getSimpleContentType(String realContentType) {
		return (realContentType == null || realContentType.indexOf(';') < 0) ? realContentType
				: realContentType.substring(0, realContentType.indexOf(';'));

	}
	
	
	/**
	 * The handle method is the entry method into the browse controller.
	 */
	@Override
	protected ModelAndView handle(HttpServletRequest req,
			HttpServletResponse res, Object comm, BindException errors)
			throws Exception {

		// Cast the command to the correct command type.
		BrowseCommand command = (BrowseCommand) comm;
		
		// Build a command with the items from the URL.
		String base = req.getContextPath() + req.getServletPath();
		
		String line = req.getRequestURI().substring(base.length());
		
		Matcher matcher = p.matcher(line);
		if (matcher.matches()) {
			command.setHrOid(Long.parseLong(matcher.group(1)));
			command.setResource(matcher.group(2));
		}

		if (req.getQueryString() != null) {
			command.setResource(command.getResource() + "?"
					+ req.getQueryString());
		}
		
		
		// Check if the command is prefixed with a forward slash.
		if (command.getResource().startsWith("/")) {
			command.setResource(command.getResource().substring(1));
		}
		
		// Now make sure that the domain name is in lowercase.
		Pattern urlBreakerPattern = Pattern.compile("(.*?)://(.*?)/(.*)");
		Matcher urlBreakerMatcher = urlBreakerPattern.matcher(command
				.getResource());
		if (urlBreakerMatcher.matches()) {
			command.setResource(urlBreakerMatcher.group(1) + "://"
					+ urlBreakerMatcher.group(2).toLowerCase() + "/"
					+ urlBreakerMatcher.group(3));
		}
		
		// Load the HarvestResourceDTO from the quality review facade.
		HarvestResourceDTO dto = qualityReviewFacade.getHarvestResourceDTO(
				command.getHrOid(), command.getResource());

		// If the resource is not found, go to an error page.
		if (dto == null) {
			log.debug("Resource not found: " + command.getResource());
			return new ModelAndView("browse-tool-not-found", "resourceName",
					command.getResource());
		} else {
			Header[] headers = null;
			// catch any DigitalAssetStoreException and log assumptions
			try {
				headers = qualityReviewFacade.getHttpHeaders(dto);
			} catch (DigitalAssetStoreException e) {
				log.info("Failed to get header for ti "
						+ dto.getTargetInstanceOid());
				// throw new DigitalAssetStoreException(e);
			} catch (Exception e) {
				log.error("Unexpected exception encountered when retrieving WARC headers for ti "
						+ dto.getTargetInstanceOid());
				throw new Exception(e);
			}

			// Send the headers for a redirect.
			if (dto.getStatusCode() == HttpServletResponse.SC_MOVED_TEMPORARILY
					|| dto.getStatusCode() == HttpServletResponse.SC_MOVED_PERMANENTLY) {
				res.setStatus(dto.getStatusCode());
				String location = getHeaderValue(headers, "Location");
				if (location != null) {
					String newUrl = browseHelper.convertUrl(command.getHrOid(),
							command.getResource(), location);
					res.setHeader("Location", newUrl);
				}
			}
			
			// Get the content type.
			String realContentType = getHeaderValue(headers, "Content-Type");
			String simpleContentType = this
					.getSimpleContentType(realContentType);

			String charset = null;
			if (realContentType != null) {
				Matcher charsetMatcher = CHARSET_PATTERN
						.matcher(realContentType);
				if (charsetMatcher.find()) {
					charset = charsetMatcher.group(1);
					log.debug("Desired charset: " + charset + " for "
							+ command.getResource());
				} else {
					log.debug("No charset: " + charset + " ("
							+ command.getResource());
				}
			}
			
			
			// If the content has been registered with the browseHelper to
			// require replacements, load the content and perform the
			// necessary replacements.
			if (browseHelper.isReplaceable(simpleContentType)) {
				StringBuilder content = null;
				
				try {
					content = readFile(dto, charset);
				} catch (DigitalAssetStoreException e) {
					if (log.isWarnEnabled()) {
						log.warn(e.getMessage());
					}
				}
				ModelAndView mav = new ModelAndView("browse-tool-html");

				if (content != null) {
					// We might need to use a different base URL if a BASE HREF
					// tag
					// is used. We use the TagMagix class to perform the search.
					// Note that TagMagix leaves leading/trailing slashes on the
					// URL, so we need to do that
					String baseUrl = command.getResource();
					Pattern baseUrlGetter = BrowseHelper.getTagMagixPattern(
							"BASE", "HREF");
					Matcher m = baseUrlGetter.matcher(content);
					if (m.find()) {
						String u = m.group(1);
						if (u.startsWith("\"") && u.endsWith("\"")
								|| u.startsWith("'") && u.endsWith("'")) {

							// Ensure the detected Base HREF is not commented
							// out (unusual case, but we have seen it).
							int lastEndComment = content.lastIndexOf("-->",
									m.start());
							int lastStartComment = content.lastIndexOf("<!--",
									m.start());
							if (lastStartComment < 0
									|| lastEndComment > lastStartComment) {
								baseUrl = u.substring(1, u.length() - 1);
							}
						}

					}

					browseHelper.fix(content, simpleContentType,
							command.getHrOid(), baseUrl);
					mav.addObject("content", content.toString());
				} else {
					mav.addObject("content", "");
				}

				mav.addObject("Content-Type", realContentType);
				return mav;
			}
			
			// If there are no replacements, send the content back directly.
			else {
				if (dto.getLength() > MAX_MEMORY_SIZE) {
					Date dt = new Date();
					File f = qualityReviewFacade.getResource(dto);
					ModelAndView mav = new ModelAndView("browse-tool-other");
					mav.addObject("file", f);
					mav.addObject("contentType", realContentType);

					log.info("TIME TO GET RESOURCE(old): "
							+ (new Date().getTime() - dt.getTime()));
					return mav;
				} else {
					Date dt = new Date();
					byte[] bytesBuffer = null;
					try {
						bytesBuffer = qualityReviewFacade.getSmallResource(dto);
					} catch (org.webcurator.core.exceptions.DigitalAssetStoreException e) {
						if (log.isWarnEnabled()) {
							log.warn("Could not retrieve resource: " + dto.getName());
						}
					}
					ModelAndView mav = new ModelAndView(
							"browse-tool-other-small");
					mav.addObject("bytesBuffer", bytesBuffer);
					mav.addObject("contentType", realContentType);
					log.debug("TIME TO GET RESOURCE(new): "
							+ (new Date().getTime() - dt.getTime()));
					return mav;
				}
			}
		}
	}

	private Charset loadCharset(String charset) {
		Charset cs = CHARSET_LATIN_1;
		if (charset != null) {
			try {
				cs = Charset.forName(charset);
			} catch (Exception ex) {
				log.warn("Could not load desired charset " + charset
						+ "; using ISO-8859-1");
			}
		}
		
		return cs;
	}
	

	/**
	 * Reads the contents of the HarvestResource described by the DTO.
	 * 
	 * @param dto
	 *            The HarvestResource to read the content of.
	 * @return The content of the HarvestResource as a String.
	 * @throws IOException
	 */
	private StringBuilder readFile(HarvestResourceDTO dto, String charset)
			throws DigitalAssetStoreException {

		// this is always a temp file - need to delete it before exiting
		File f = qualityReviewFacade.getResource(dto);
		
		StringBuilder content = new StringBuilder();
		BufferedInputStream is = null;
		
		try {
			// Try to get the appropriate character set.
			Charset cs = loadCharset(charset);
			
			is = new BufferedInputStream(new FileInputStream(f));
			byte[] buff = new byte[BYTE_BUFFER_SIZE];
			int bytesRead = 0;
			
			bytesRead = is.read(buff);
			while (bytesRead > 0) {
				content.append(new String(buff, 0, bytesRead, cs.name()));
				bytesRead = is.read(buff);
			}

		} catch (Exception e) {
			throw new DigitalAssetStoreException("Failed to read file : "
					+ f.getAbsolutePath() + " : " + e.getMessage(), e);
		} finally {
			try {
				is.close();
				// No point deleting it if it is already gone
				if (f.exists()) {
					if (!f.delete()) {
						log.error("Failed to delete temporary file: "
								+ f.getAbsolutePath());
					}
				}
			} catch (IOException e) {
				if (log.isWarnEnabled()) {
					log.warn("Failed to close input stream " + e.getMessage(),
							e);
				}
			}
		}

		return content;
	}

	/**
	 * @param fixTokens
	 *            the fixTokens to set
	 */
	public void setFixTokens(Map<String, String> fixTokens) {
		this.fixTokens = fixTokens;
	}

	public static Map<String, String> getFixTokens() {
		return fixTokens;
	}

}
