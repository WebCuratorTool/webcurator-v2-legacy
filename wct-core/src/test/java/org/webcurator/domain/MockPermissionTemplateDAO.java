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
package org.webcurator.domain;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.transaction.support.TransactionTemplate;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.webcurator.domain.model.auth.Agency;
import org.webcurator.domain.model.core.AuthorisingAgent;
import org.webcurator.domain.model.core.BusinessObjectFactory;
import org.webcurator.domain.model.core.Permission;
import org.webcurator.domain.model.core.PermissionTemplate;
import org.webcurator.domain.model.core.Site;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class MockPermissionTemplateDAO implements PermissionTemplateDAO {

	private static Log log = LogFactory.getLog(MockPermissionTemplateDAO.class);
	private Document theFile = null;
	private DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory
			.newInstance();
	private BusinessObjectFactory factory = new BusinessObjectFactory();
	private Map<Long, Permission> permissionOids = new HashMap<Long, Permission>();

	public MockPermissionTemplateDAO(String filename) {

		super();
		try {
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			theFile = docBuilder.parse(new File(filename));

			NodeList permissionNodes = theFile
					.getElementsByTagName("permission");

			// force a nested load of everything
			// loadPermissionsFromNodeList(permissionNodes);
		} catch (SAXParseException err) {
			log.debug("** Parsing error" + ", line " + err.getLineNumber()
					+ ", uri " + err.getSystemId());
			log.debug(" " + err.getMessage());
		} catch (SAXException se) {
			Exception x = se.getException();
			((x == null) ? se : x).printStackTrace();
		} catch (Exception e) {
			log.debug(e.getClass().getName() + ": " + e.getMessage());
		}
	}

	/*
	 * private List<Permission> loadPermissionsFromNodeList(NodeList
	 * permissionNodes) { List<Permission> permissions = new ArrayList<Permission>();
	 * for (int i = 0; i < permissionNodes.getLength(); i++) { Node
	 * permissionNode = permissionNodes.item(i); if(permissionNode.getNodeType() ==
	 * Node.ELEMENT_NODE) {
	 * permissions.add(loadPermissionFromNode(permissionNode)); } }
	 * 
	 * return permissions; }
	 * 
	 * private Permission loadPermissionFromNode(Node permissionNode) { //Check
	 * the oid first Long oid = getOid(permissionNode); if(oid != null &&
	 * permissionNode.hasChildNodes() && !permissionOids.containsKey(oid)) {
	 * Site site = new Site(); Permission permission =
	 * factory.newPermission(site);
	 * 
	 * NodeList children = permissionNode.getChildNodes(); for(int i = 0; i <
	 * children.getLength(); i++) { Node child = children.item(i);
	 * if(child.getNodeType() == Node.ELEMENT_NODE) {
	 * if(child.getNodeName().equals("title")) {
	 * site.setTitle(getString(child)); } else
	 * if(child.getNodeName().equals("description")) {
	 * site.setDescription(getString(child)); } else
	 * if(child.getNodeName().equals("notes")) {
	 * site.setNotes(getString(child)); } else
	 * if(child.getNodeName().equals("libraryOrderNo")) {
	 * site.setLibraryOrderNo(getString(child)); } else
	 * if(child.getNodeName().equals("published")) {
	 * site.setPublished(getBool(child)); } else
	 * if(child.getNodeName().equals("active")) {
	 * site.setActive(getBool(child)); } else
	 * if(child.getNodeName().equals("authorisingAgents")) {
	 * site.setAuthorisingAgents(loadAuthorisingAgentsFromNodeList(child.getChildNodes())); }
	 * else if(child.getNodeName().equals("urlPatterns")) {
	 * site.setUrlPatterns(loadUrlPatternsFromNodeList(site,
	 * child.getChildNodes())); } else
	 * if(child.getNodeName().equals("permissions")) {
	 * site.setPermissions(loadPermissionsFromNodeList(site,
	 * child.getChildNodes())); } else
	 * if(child.getNodeName().equals("annotations")) {
	 * site.setAnnotations(annotationDAO.loadAnnotationsFromNodeList(site,
	 * child.getChildNodes())); } } }
	 * 
	 * siteOids.put(oid, site); }
	 * 
	 * return siteOids.get(oid); }
	 * 
	 * private Set<Permission> loadPermissionsFromNodeList(Site site, NodeList
	 * pNodes) { Set<Permission> permissions = new HashSet<Permission>(); for
	 * (int i = 0; i < pNodes.getLength(); i++) { Node pNode = pNodes.item(i);
	 * if(pNode.getNodeType() == Node.ELEMENT_NODE) {
	 * permissions.add(loadPermissionFromNode(site, pNode)); } }
	 * 
	 * return permissions; }
	 * 
	 * private Permission loadPermissionFromNode(Site site, Node pNode) {
	 * //Check the oid first Long oid = getOid(pNode); if(oid != null &&
	 * pNode.hasChildNodes() && !pOids.containsKey(oid)) { Permission p =
	 * factory.newPermission(site); p.setOid(oid);
	 * 
	 * NodeList children = pNode.getChildNodes(); for(int i = 0; i <
	 * children.getLength(); i++) { Node child = children.item(i);
	 * if(child.getNodeType() == Node.ELEMENT_NODE) {
	 * if(child.getNodeName().equals("authorisingAgent")) {
	 * p.setAuthorisingAgent(aaOids.get(getOid(child))); } else
	 * if(child.getNodeName().equals("urlPatterns")) { Set<UrlPattern>
	 * urlPatterns = new HashSet<UrlPattern>(); NodeList urlPatternNodes =
	 * child.getChildNodes(); for(i = 0; i < urlPatternNodes.getLength(); i++) {
	 * Node urlPatternNode = urlPatternNodes.item(i);
	 * if(urlPatternNode.getNodeType() == Node.ELEMENT_NODE) {
	 * urlPatterns.add(urlOids.get(getOid(urlPatternNode))); } }
	 * p.setUrls(urlPatterns); } if(child.getNodeName().equals("startdate")) {
	 * p.setStartDate(getDate(child)); } else
	 * if(child.getNodeName().equals("enddate")) { p.setEndDate(getDate(child)); }
	 * else if(child.getNodeName().equals("approved")) {
	 * p.setApproved(getBool(child)); } else
	 * if(child.getNodeName().equals("status")) {
	 * p.setStatus(getInteger(child)); } else
	 * if(child.getNodeName().equals("notes")) { p.setNotes(getString(child)); }
	 * else if(child.getNodeName().equals("accessStatus")) {
	 * p.setAccessStatus(getString(child)); } else
	 * if(child.getNodeName().equals("openAccessDate")) {
	 * p.setOpenAccessDate(getDate(child)); } else
	 * if(child.getNodeName().equals("availableFlag")) {
	 * p.setAvailableFlag(getBool(child)); } else
	 * if(child.getNodeName().equals("specialRequirements")) {
	 * p.setSpecialRequirements(getString(child)); } else
	 * if(child.getNodeName().equals("creationDate")) {
	 * p.setCreationDate(getDate(child)); } else
	 * if(child.getNodeName().equals("copyrightUrl")) {
	 * p.setCopyrightUrl(getString(child)); } else
	 * if(child.getNodeName().equals("copyrightStatement")) {
	 * p.setCopyrightStatement(getString(child)); } else
	 * if(child.getNodeName().equals("permissionSentDate")) {
	 * p.setPermissionSentDate(getDate(child)); } else
	 * if(child.getNodeName().equals("permissionGrantedDate")) {
	 * p.setPermissionGrantedDate(getDate(child)); } else
	 * if(child.getNodeName().equals("quickPick")) {
	 * p.setQuickPick(getBool(child)); } else
	 * if(child.getNodeName().equals("displayName")) {
	 * p.setDisplayName(getString(child)); }
	 * if(child.getNodeName().equals("owningAgency")) {
	 * p.setOwningAgency(userRoleDAO.getAgencyByOid(getOid(child))); } else
	 * if(child.getNodeName().equals("fileReference")) {
	 * p.setFileReference(getString(child)); } else
	 * if(child.getNodeName().equals("exclusions")) { //TODO: handle exclusions }
	 * else if(child.getNodeName().equals("annotations")) {
	 * p.setAnnotations(annotationDAO.loadAnnotationsFromNodeList(p,
	 * child.getChildNodes())); } } }
	 * 
	 * pOids.put(oid, p); }
	 * 
	 * return pOids.get(oid); }
	 */

	private Long getOid(Node child) {
		Node idNode = child.getAttributes().getNamedItem("id");
		if (idNode != null) {
			return new Long(idNode.getNodeValue());
		} else {
			return null;
		}
	}
	
	PermissionTemplate permissionTemplate;

	public PermissionTemplate getTemplate(Long oid) {
		if (permissionTemplate == null)
		{
			permissionTemplate = new PermissionTemplate();
			permissionTemplate.setOid(oid);
			Agency agency = new Agency();
			agency.setOid((long) 2000);
			permissionTemplate.setAgency(agency);
		}
		return permissionTemplate;
	}

	public List getTemplates(Long agencyOid) {
		// TODO
		return null;
	}

	public List getAllTemplates() {
		// TODO
		return null;
	}

	public void setTxTemplate(TransactionTemplate txTemplate) {
		// TODO
	}

	public void saveOrUpdate(final Object aObject) {
		// TODO
	}

	private Permission permission;

	public Permission getPermission(Long oid) {
		if (permission == null) {
			BusinessObjectFactory factory = new BusinessObjectFactory();
			Site site = new Site();
			site.setOid(9000L);
			site.setTitle("My Test Site");
			permission = factory.newPermission(site);
			AuthorisingAgent authorisingAgent = factory.newAuthorisingAgent();
			permission.setOid(oid);
			permission.setAuthorisingAgent(authorisingAgent);
		}
		return permission;
	}

	public void delete(final Object aObject) {
		// TODO
	}
}
