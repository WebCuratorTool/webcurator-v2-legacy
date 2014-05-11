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
package org.webcurator.core.archive;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringEscapeUtils;
import org.webcurator.core.scheduler.TargetInstanceManager;
import org.webcurator.core.targets.TargetManager;
import org.webcurator.domain.model.core.AbstractTarget;
import org.webcurator.domain.model.core.Target;
import org.webcurator.domain.model.core.TargetGroup;
import org.webcurator.domain.model.core.Seed;
import org.webcurator.domain.model.core.TargetInstance;
import org.webcurator.domain.model.core.UrlPattern;
import org.webcurator.domain.model.dto.PermissionSeedDTO;

/**
 * Routines for building parts of the SIP.
 * @author beaumontb
 *
 */
public class SipBuilder {
	/** Reference to the Target Instance Manager */
	private TargetInstanceManager targetInstanceManager = null;
	/** Reference to the Target Manager */
	private TargetManager targetManager = null;
	
	/** The SIP date formatter */
	private static SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
	
	/**
	 * Escape a string for XML.
	 * @param str The string to escape.
	 * @return The string, escaped for XML.
	 */
	public static String es(String str) { 
		return str == null ? "" : StringEscapeUtils.escapeXml(str);
	}
	
	/**
	 * Format a date for the SIP.
	 * @param date The date to be formatted.
	 * @return A string representation of the date.
	 */
	public static String dt(Date date) { 
		return date == null ? "" : dateFormatter.format(date);
	}
	
	
	/**
	 * Get the Target section of the SIP.
	 * @param targetInstanceOid The instance ID.
	 * @return The target section of the SIP.
	 */
	public String getTargetSection(Long targetInstanceOid) {
		TargetInstance inst = targetInstanceManager.getTargetInstance(targetInstanceOid);
		return getTargetSection(inst);
	}
	
	/**
	 * Build a Map of all the SIP sections that the SipBuilder generates.
	 * @param inst The instance to generate teh sections for.
	 * @return A Map of sections, indexed by section key.
	 */
	public Map<String, String> buildSipSections(TargetInstance inst) {
		Map<String,String> sections = new HashMap<String,String>();
		sections.put("permissionSection", getPermissionSection(inst));
		sections.put("targetSection", getTargetSection(inst));
		sections.put("profileNoteSection", getProfileNoteSection(inst));
		return sections;
	}
	
	
	/**
	 * Get the ProfileNote section of the SIP.
	 * @param inst The instance to generate teh section for.
	 * @return The ProfileNote section of the SIP.
	 */	
	public String getProfileNoteSection(TargetInstance inst) {
		StringBuffer buff = new StringBuffer();
		buff.append("<wct:ProfileNote>");
		buff.append(es(inst.getTarget().getProfileNote()));
		buff.append("</wct:ProfileNote>");
		return buff.toString();
	}
	

	/**
	 * Update the Target Reference Number in the SIP.
	 * @param target The target to update the section for.
	 * @param sipSections The sipSections map.
	 * @return boolean true if updated
	 */	
	public boolean updateTargetReference(AbstractTarget target, Map<String,String> sipSections) 
	{
		AbstractTarget theTarget = null;
		boolean updated = false;
		
		if(sipSections.containsKey("targetSection"))
		{
			String targetSection = (String)sipSections.get("targetSection");
			int refNumIdx = targetSection.indexOf("<wct:ReferenceNumber></wct:ReferenceNumber>");
			if(refNumIdx >= 0)
			{
				//There is no reference number set - insert the ref number
				//Beware of lazy loaded AbstractTargets - check and reload if necessary
				if(target.getObjectType() == AbstractTarget.TYPE_TARGET &&
					!(target instanceof Target))
				{
					theTarget = targetManager.load(target.getOid());
				}
				else if(target.getObjectType() == AbstractTarget.TYPE_GROUP &&
					!(target instanceof TargetGroup))
				{
					theTarget = targetManager.loadGroup(target.getOid());
				}
				else
				{
					//The abstract target must be fully loaded
					theTarget = target;
				}
	
				if(theTarget != null && 
						theTarget.getReferenceNumber() != null && 
						theTarget.getReferenceNumber().length() > 0)
				{
					String targetSip = targetSection.substring(0, refNumIdx+("<wct:ReferenceNumber>".length())) +
							theTarget.getReferenceNumber() +
							targetSection.substring(refNumIdx+("<wct:ReferenceNumber>".length()), targetSection.length());
					
					sipSections.remove("targetSection");
					sipSections.put("targetSection", targetSip);
					updated = true;
				}
			}
		}
		
		return updated;
	}
	
	/**
	 * Get the Target section of the SIP.
	 * @param inst The instance to generate the section for.
	 * @return The target section of the SIP.
	 */	
	public String getTargetSection(TargetInstance inst) {		
		
		AbstractTarget target = inst.getTarget();
		
		StringBuffer buff = new StringBuffer();
		
		buff.append("<wct:Target>\n");
		buff.append("  <wct:ReferenceNumber>" + es(target.getReferenceNumber()) + "</wct:ReferenceNumber>\n");
		buff.append("  <wct:Name>" + es(target.getName()) + "</wct:Name>\n");
		buff.append("  <wct:Description>" + es(target.getDescription())+ "</wct:Description>\n");
		
		buff.append("  <wct:Seeds>\n");
		for(Seed seed: targetManager.getSeeds(inst)) {
			buff.append("    <wct:Seed>\n");
			buff.append("      <wct:SeedURL>" + es(seed.getSeed()) + "</wct:SeedURL>\n");
			buff.append("      <wct:SeedType>" + es(seed.isPrimary() ? "Primary" : "Secondary") + "</wct:SeedType>\n");
			buff.append("    </wct:Seed>\n");
		}
		buff.append("  </wct:Seeds>\n");
		
		buff.append("</wct:Target>\n");		
		
		return buff.toString();
	}
	
	
	/**
	 * Get the Permission Section of the SIP.
	 * @param targetInstanceOid The ID of the TargetInstance to generate the SIP section for.
	 * @return The Permission section of the SIP.
	 */
	public String getPermissionSection(Long targetInstanceOid) {
		TargetInstance inst = targetInstanceManager.getTargetInstance(targetInstanceOid);
		return getPermissionSection(inst);
	}
	
	/**
	 * Get the Permission Section of the SIP.
	 * @param inst The TargetInstance to generate the SIP section for.
	 * @return The Permission section of the SIP.
	 */
	public String getPermissionSection(TargetInstance inst) {
		
		Collection<PermissionSeedDTO> permissions = targetManager.getActivePermissions(inst);
		
		StringBuffer buff = new StringBuffer();
		
		buff.append("<wct:Permissions>\n");
		
		for(PermissionSeedDTO perm: permissions) {
			
			buff.append("  <wct:Permission>\n");
			buff.append("    <wct:State>Granted</wct:State>\n");
			buff.append("    <wct:StartDate>" + dt(perm.getPermission().getStartDate()) + "</wct:StartDate>\n");
			buff.append("    <wct:EndDate>" + dt(perm.getPermission().getEndDate()) + "</wct:EndDate>\n");
			buff.append("    <wct:HarvestAuthorisation>\n");
			if(perm.getPermission().getSite() != null)
			{
				buff.append("      <wct:Name>" + es(perm.getPermission().getSite().getTitle()) + "</wct:Name>\n");
				buff.append("      <wct:Description>" + es(perm.getPermission().getSite().getDescription()) + "</wct:Description>\n");
				buff.append("      <wct:OrderNumber>" + es(perm.getPermission().getSite().getLibraryOrderNo()) + "</wct:OrderNumber>\n");
				buff.append("      <wct:IsPublished>" + (perm.getPermission().getSite().isPublished() ? "true" : "false") + "</wct:IsPublished>\n");
			}
			else
			{
				buff.append("      <wct:Name />\n");
				buff.append("      <wct:Description />\n");
				buff.append("      <wct:OrderNumber />\n");
				buff.append("      <wct:IsPublished />\n");
			}
			buff.append("    </wct:HarvestAuthorisation>\n");
			buff.append("    <wct:AccessStatus>" + es(perm.getPermission().getAccessStatus()) + "</wct:AccessStatus>\n");
			buff.append("    <wct:SpecialRequirements>" + es(perm.getPermission().getSpecialRequirements())+ "</wct:SpecialRequirements>\n");
			buff.append("    <wct:OpenAccessDate>" + dt(perm.getPermission().getOpenAccessDate()) + "</wct:OpenAccessDate>\n");
			buff.append("    <wct:CopyrightStatement>" + es(perm.getPermission().getCopyrightStatement()) + "</wct:CopyrightStatement>\n");
			buff.append("    <wct:CopyrightURL>" + es(perm.getPermission().getCopyrightUrl()) + "</wct:CopyrightURL>\n");
			buff.append("    <wct:FileReference>" + es(perm.getPermission().getFileReference()) + "</wct:FileReference>\n");
			buff.append("    <wct:AuthorisingAgent>\n");
			if(perm.getPermission().getAuthorisingAgent() != null)
			{
				buff.append("      <wct:Name>" + es(perm.getPermission().getAuthorisingAgent().getName()) + "</wct:Name>\n");
				buff.append("      <wct:Contact>" + es(perm.getPermission().getAuthorisingAgent().getContact()) + "</wct:Contact>\n");
			}
			else
			{
				buff.append("      <wct:Name />\n");
				buff.append("      <wct:Contact />\n");
			}
			buff.append("    </wct:AuthorisingAgent>\n");

			buff.append("    <wct:Paterns>\n");
			for(UrlPattern pattern : perm.getPermission().getUrls()) {
				buff.append("      <wct:Pattern>" + es(pattern.getPattern()) + "</wct:Pattern>\n");
			}
			buff.append("    </wct:Paterns>\n");
			
			
			buff.append("    <wct:SeedsURLs>\n");
			for(String seed : perm.getSeeds()) {
				buff.append("      <wct:SeedURL>" + es(seed) + "</wct:SeedURL>\n");				
			}
			buff.append("    </wct:SeedsURLs>\n");
			
			buff.append("  </wct:Permission>\n");
		}
		buff.append("</wct:Permissions>\n");		
		
		return buff.toString();
	}

	/**
	 * Set the Target Instance Manager.
	 * @param targetInstanceManager the targetInstanceManager to set
	 */
	public void setTargetInstanceManager(TargetInstanceManager targetInstanceManager) {
		this.targetInstanceManager = targetInstanceManager;
	}

	/**
	 * Set the Target Manager.
	 * @param targetManager the targetManager to set
	 */
	public void setTargetManager(TargetManager targetManager) {
		this.targetManager = targetManager;
	}
}
