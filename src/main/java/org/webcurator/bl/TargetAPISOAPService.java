/*
 * FILE: TargetAPISOAPService.java
 * AUTHOR: oakleigh_sk
 * DATE: 16/08/2010
 *  
 * <p>This class implements a simple web service for deployment 
 * with Apache Axis 1.4.
 *
 * <p>It allows the passing of all necessary data items for creating
 *    a new Harvest Authorisation and associated Target within the body
 *    of an XML document which is sent via a SOAP message. 
 * 
 */

package org.webcurator.bl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.xpath.XPathConstants;

import org.springframework.remoting.jaxrpc.ServletEndpointSupport;
import org.w3c.dom.*;
import org.webcurator.core.agency.AgencyUserManager;
import org.webcurator.core.sites.SiteManager;
import org.webcurator.core.targets.TargetManager;
import org.webcurator.domain.model.core.Annotation;
import org.webcurator.domain.model.core.AuthorisingAgent;
import org.webcurator.domain.model.core.BusinessObjectFactory;
import org.webcurator.domain.model.core.DublinCore;
import org.webcurator.domain.model.core.Permission;
import org.webcurator.domain.model.core.PermissionExclusion;
import org.webcurator.domain.model.core.Seed;
import org.webcurator.domain.model.core.Target;
import org.webcurator.domain.model.core.TargetGroup;
import org.webcurator.domain.model.core.UrlPattern;
import org.webcurator.domain.model.auth.User;
import org.webcurator.domain.model.core.Site;
import org.webcurator.domain.model.dto.GroupMemberDTO;
import org.webcurator.domain.model.dto.GroupMemberDTO.SAVE_STATE;
import org.apache.axis.AxisFault;

public class TargetAPISOAPService extends ServletEndpointSupport {

	/** The SiteManager */
	private SiteManager siteManager = null;
	
	/** The AgencyUserManager */
	private AgencyUserManager agencyUserManager = null;

	/** The BusinessObjectFactory */
	private BusinessObjectFactory businessObjectFactory = null;
	
	/** The TargetManager */
	private TargetManager targetManager = null;
	

    /** Initialisation for the SOAP Service. */
    protected void onInit() {
    	siteManager = (SiteManager) getWebApplicationContext().getBean("siteManager");
    	agencyUserManager = (AgencyUserManager) getWebApplicationContext().getBean("agencyUserManager");
    	businessObjectFactory = (BusinessObjectFactory) getWebApplicationContext().getBean("businessObjectFactory");
    	targetManager = (TargetManager) getWebApplicationContext().getBean("targetManager");
    }
	
	/**
	  * Method which is invoked by Axis when the Axis engine receives a SOAP 
	  * request for this service.  Axis passes in the body of the received
	  * SOAP request.
	  */
	  public Element[] processTargetXML(Element[] soapBodyElements) throws AxisFault {
	
	    try {
	      
    	  // Get the root node of the XML document from the message body
	      Element xmlRootNode = (Element) soapBodyElements[0];
	      	
		  // Need to put some validation in here to check the XML doc
		  // against its schema.  (Simple to implement with a validating
		  // parser (eg Xerces), but not yet done here).
		  
		  XPathReader reader = new XPathReader(xmlRootNode);
		  
		  // populate the site (Harvest Authorisation)
		  Site targetSite = getTargetSite(reader);
		  
		  // populate the new target
		  Target newTarget = getNewTarget(reader);
		  
		  if (targetSite != null) {
			  
			  // link seeds and permissions..
			  String useExistingSiteId = (String) reader.read("/documentRootNode/ha/ha_use_existing_id", XPathConstants.STRING);

			  Set<Permission> sitePermissions = targetSite.getPermissions();
			  Set<Seed> targetSeeds = newTarget.getSeeds();
			
			  Iterator<Seed> seedIterator = targetSeeds.iterator();
			  while (seedIterator.hasNext()) {
				  Seed seed = seedIterator.next();
				  if(useExistingSiteId == null || useExistingSiteId.isEmpty()) {
					  // we're using the new ha details as passed in the Xml, we'll do
					  // some sense checking by matching the Urls.
					  Permission perm = extractPermissionByUrl(sitePermissions, seed.getSeed());
					  if(perm != null) {
		    		    seed.addPermission(perm);
						if(perm.getExclusions().size() > 0) {
							newTarget.getOverrides().setOverrideExcludeUriFilters(true);
							for(PermissionExclusion excl : perm.getExclusions()) {
								if (!newTarget.getOverrides().getExcludeUriFilters().contains(excl.getUrl())) {
									newTarget.getOverrides().getExcludeUriFilters().add(excl.getUrl());
								}					
							}
						}
					  }
				  } else {
					  // we're using a pre-existing ha record so do not match urls.
					  Permission perm = getFirstPermission(sitePermissions);
					  if(perm != null) {
			    		  seed.addPermission(perm);
					  }
				  }
			  }

		  } else {
			  // the 'addlater' option was used, we're creating 
			  // a target without an associated HA record for now..
		  }
		  
		  // Add the target to any specified target groups (by Id).
		  List<GroupMemberDTO> parents = new ArrayList<GroupMemberDTO>();
		  
		  NodeList groupIdNodes = (NodeList)reader.read("/documentRootNode/target/t_member_of_groups/target_group_id", XPathConstants.NODESET);
		  for(int index = 0; index < groupIdNodes.getLength(); index ++){
	        Node aNode = groupIdNodes.item(index);
	        Long parentGroupId = Long.parseLong(aNode.getTextContent());
			GroupMemberDTO newDTO = targetManager.createGroupMemberDTO(parentGroupId, newTarget);
			newDTO.setSaveState(SAVE_STATE.NEW);
			parents.add(newDTO);
		  }        

		  targetManager.save(newTarget, parents);
		  Long newTargetID = newTarget.getOid();

		  DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		  factory.setNamespaceAware(true);
		  DocumentBuilder builder = factory.newDocumentBuilder();
		
		  Document responseDoc = builder.newDocument();
		  Element resRoot = responseDoc.createElementNS(
		      "http://www.bl.uk/namespace/TargetAPIReturnDoc",
		  	  "ReturnString");
		
		  // Put our output string into the document and return it.
		  // (Axis will wrap this document in a full SOAP response message).
		  Text eltText = responseDoc.createTextNode("TargetID="+newTargetID.toString());
		  resRoot.appendChild(eltText);
		  Element[] result = new Element[1];
		  result[0] = resRoot;
		  return(result);
	    }
		// Catch any exceptions and make a proper Axis fault from them.
	    catch (Exception e) {
	      throw AxisFault.makeFault(e);
	    }
	  }

	  protected Target getNewTarget(XPathReader reader) {

			SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");  

		  	String userName = (String) reader.read("/documentRootNode/creator_user_name", XPathConstants.STRING);
			  
			User validUser = agencyUserManager.getUserByUserName(userName);

			Target theTarget = businessObjectFactory.newTarget(validUser);

			theTarget.setName((String) reader.read("/documentRootNode/target/t_name", XPathConstants.STRING));
			theTarget.setDescription((String) reader.read("/documentRootNode/target/t_description", XPathConstants.STRING));
			theTarget.setReferenceNumber((String) reader.read("/documentRootNode/target/t_ref_number", XPathConstants.STRING));
			theTarget.setRunOnApproval(((String) reader.read("/documentRootNode/target/t_run_on_approval", XPathConstants.STRING)).equals("true")?true:false);
			theTarget.setRequestToArchivists((String) reader.read("/documentRootNode/target/t_request_to_archivist", XPathConstants.STRING));

			int targetState = Integer.parseInt((String) reader.read("/documentRootNode/target/t_state", XPathConstants.STRING));
			theTarget.changeState(targetState);
			
	        NodeList seedNodes = (NodeList)reader.read("/documentRootNode/target/t_seeds/seed", XPathConstants.NODESET);
	        for(int index = 1; index < (seedNodes.getLength()+1); index ++){

	            Seed seed = businessObjectFactory.newSeed(theTarget);
	            seed.setSeed((String) reader.read("/documentRootNode/target/t_seeds/seed[" + index + "]/seed_url", XPathConstants.STRING));
	            seed.setPrimary(((String) reader.read("/documentRootNode/target/t_seeds/seed[" + index + "]/seed_is_primary", XPathConstants.STRING)).equals("true")?true:false);
	            theTarget.addSeed(seed);
			}        
			
            String selectionDate = (String) reader.read("/documentRootNode/target/t_selection_date", XPathConstants.STRING);
            if(!selectionDate.isEmpty()) {
				try {
					theTarget.setSelectionDate(sdf.parse(selectionDate));
				} catch (ParseException e) {
					e.printStackTrace();
				}
            }

			theTarget.setSelectionType((String) reader.read("/documentRootNode/target/t_selection_type", XPathConstants.STRING));
			theTarget.setSelectionNote((String) reader.read("/documentRootNode/target/t_selection_note", XPathConstants.STRING));
			theTarget.setEvaluationNote((String) reader.read("/documentRootNode/target/t_evaluation_note", XPathConstants.STRING));
			theTarget.setHarvestType((String) reader.read("/documentRootNode/target/t_harvest_type", XPathConstants.STRING));

			List<Annotation> annotations = new ArrayList<Annotation>();

	        NodeList annotationNodes = (NodeList)reader.read("/documentRootNode/target/t_annotations/annotation", XPathConstants.NODESET);
	        for(int index = 1; index < (annotationNodes.getLength()+1); index ++){
	            
	            Annotation annotation = new Annotation();
	            annotation.setNote((String) reader.read("/documentRootNode/target/t_annotations/annotation[" + index + "]/description", XPathConstants.STRING));
	        	annotation.setAlertable(((String) reader.read("/documentRootNode/target/t_annotations/annotation[" + index + "]/generate_alert", XPathConstants.STRING)).equals("true")?true:false);
	            annotation.setDate(new Date());
	        	annotation.setUser(validUser);
	        	annotation.setObjectType(Target.class.getName());
	        	annotation.setObjectOid(null);

	            annotations.add(annotation);
			}        
	        theTarget.setAnnotations(annotations);
			
			DublinCore dc = new DublinCore();
			dc.setTitle((String) reader.read("/documentRootNode/target/t_dublin_core/dc_title", XPathConstants.STRING));
			dc.setIdentifier((String) reader.read("/documentRootNode/target/t_dublin_core/dc_identifier", XPathConstants.STRING));
			dc.setDescription((String) reader.read("/documentRootNode/target/t_dublin_core/dc_description", XPathConstants.STRING));
			dc.setSubject((String) reader.read("/documentRootNode/target/t_dublin_core/dc_subject", XPathConstants.STRING));
			dc.setCreator((String) reader.read("/documentRootNode/target/t_dublin_core/dc_creator", XPathConstants.STRING));
			dc.setPublisher((String) reader.read("/documentRootNode/target/t_dublin_core/dc_publisher", XPathConstants.STRING));
			dc.setContributor((String) reader.read("/documentRootNode/target/t_dublin_core/dc_contributor", XPathConstants.STRING));
			dc.setType((String) reader.read("/documentRootNode/target/t_dublin_core/dc_type", XPathConstants.STRING));
			dc.setFormat((String) reader.read("/documentRootNode/target/t_dublin_core/dc_fornat", XPathConstants.STRING));
			dc.setSource((String) reader.read("/documentRootNode/target/t_dublin_core/dc_source", XPathConstants.STRING));
			dc.setLanguage((String) reader.read("/documentRootNode/target/t_dublin_core/dc_language", XPathConstants.STRING));
			dc.setRelation((String) reader.read("/documentRootNode/target/t_dublin_core/dc_relation", XPathConstants.STRING));
			dc.setCoverage((String) reader.read("/documentRootNode/target/t_dublin_core/dc_coverage", XPathConstants.STRING));
			dc.setIssn((String) reader.read("/documentRootNode/target/t_dublin_core/dc_issn", XPathConstants.STRING));
			dc.setIsbn((String) reader.read("/documentRootNode/target/t_dublin_core/dc_isbn", XPathConstants.STRING));
			theTarget.setDublinCoreMetaData(dc);

			theTarget.setDisplayTarget(((String) reader.read("/documentRootNode/target/t_access_display", XPathConstants.STRING)).equals("true")?true:false);
			theTarget.setDisplayChangeReason((String) reader.read("/documentRootNode/target/t_access_display_change_reason", XPathConstants.STRING));

			String accessZone = (String) reader.read("/documentRootNode/target/t_access_zone", XPathConstants.STRING);
            int zone = 0;
            if (accessZone.equals("Public")) zone = 0;
            if (accessZone.equals("Onsite")) zone = 1;
            if (accessZone.equals("Restricted")) zone = 2;
			theTarget.setAccessZone(zone);
			
			theTarget.setDisplayNote((String) reader.read("/documentRootNode/target/t_access_intro_display_note", XPathConstants.STRING));

			return theTarget;
	  }
	  
	  protected Site getTargetSite(XPathReader reader) {
	
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");  

		Site theSite = null;
		
	  	String userName = (String) reader.read("/documentRootNode/creator_user_name", XPathConstants.STRING);
		  
		User validUser = agencyUserManager.getUserByUserName(userName);
		  
	  	String useExistingSiteId = (String) reader.read("/documentRootNode/ha/ha_use_existing_id", XPathConstants.STRING);
		
		if(useExistingSiteId == null || useExistingSiteId.isEmpty()) {
			
			theSite = new Site();
			
			theSite.setOwningAgency(validUser.getAgency());
			
			theSite.setTitle((String) reader.read("/documentRootNode/ha/ha_title", XPathConstants.STRING));
			theSite.setDescription((String) reader.read("/documentRootNode/ha/ha_description", XPathConstants.STRING));
			theSite.setLibraryOrderNo((String) reader.read("/documentRootNode/ha/ha_order_no", XPathConstants.STRING));
			theSite.setPublished(((String) reader.read("/documentRootNode/ha/ha_published", XPathConstants.STRING)).equals("true")?true:false);
			theSite.setActive(((String) reader.read("/documentRootNode/ha/ha_enabled", XPathConstants.STRING)).equals("true")?true:false);
			
			List<Annotation> annotations = new ArrayList<Annotation>();

	        NodeList annotationNodes = (NodeList)reader.read("/documentRootNode/ha/ha_annotations/annotation_description", XPathConstants.NODESET);
	        for(int index = 0; index < annotationNodes.getLength(); index ++){
	            Node aNode = annotationNodes.item(index);
	            Annotation annotation = new Annotation();
	            annotation.setNote(aNode.getTextContent());
	        	annotation.setDate(new Date());
	        	annotation.setUser(validUser);
	        	annotation.setObjectType(Site.class.getName());
	        	annotation.setObjectOid(null);

	            annotations.add(annotation);
			}        
			theSite.setAnnotations(annotations);

	        NodeList urlPatternNodes = (NodeList)reader.read("/documentRootNode/ha/ha_url_patterns/url_pattern", XPathConstants.NODESET);
	        for(int index = 0; index < urlPatternNodes.getLength(); index ++){
	            Node aNode = urlPatternNodes.item(index);
	            UrlPattern urlPattern = businessObjectFactory.newUrlPattern(theSite);
	            urlPattern.setPattern(aNode.getTextContent());
	            theSite.getUrlPatterns().add(urlPattern);
			}        

			Set<AuthorisingAgent> authAgents = new HashSet<AuthorisingAgent>();

	        NodeList authAgentNodes = (NodeList)reader.read("/documentRootNode/ha/ha_auth_agencies/auth_agency", XPathConstants.NODESET);
	        for(int index = 1; index < (authAgentNodes.getLength()+1); index ++){

	        	AuthorisingAgent authAgent = null;
	        	String useExistingAuthAgentId = (String) reader.read("/documentRootNode/ha/ha_auth_agencies/auth_agency[" + index + "]/aa_use_existing_id", XPathConstants.STRING);

	    	  	if(useExistingAuthAgentId == null || useExistingAuthAgentId.isEmpty()) {
		        	authAgent = businessObjectFactory.newAuthorisingAgent();
		            authAgent.setName((String) reader.read("/documentRootNode/ha/ha_auth_agencies/auth_agency[" + index + "]/aa_name", XPathConstants.STRING));
		            authAgent.setDescription((String) reader.read("/documentRootNode/ha/ha_auth_agencies/auth_agency[" + index + "]/aa_description", XPathConstants.STRING));
		            authAgent.setContact((String) reader.read("/documentRootNode/ha/ha_auth_agencies/auth_agency[" + index + "]/aa_contact", XPathConstants.STRING));
		            authAgent.setPhoneNumber((String) reader.read("/documentRootNode/ha/ha_auth_agencies/auth_agency[" + index + "]/aa_phone", XPathConstants.STRING));
		            authAgent.setEmail((String) reader.read("/documentRootNode/ha/ha_auth_agencies/auth_agency[" + index + "]/aa_email", XPathConstants.STRING));
		            authAgent.setAddress((String) reader.read("/documentRootNode/ha/ha_auth_agencies/auth_agency[" + index + "]/aa_address", XPathConstants.STRING));
		            authAgents.add(authAgent);
	    	  	} else {
	    	  		authAgent = siteManager.loadAuthorisingAgent(Long.parseLong(useExistingAuthAgentId));
		            authAgents.add(authAgent);
	    	  	}
			}        
			theSite.setAuthorisingAgents(authAgents);

			Set<Permission> permissions = new HashSet<Permission>();
			NodeList permissionNodes = (NodeList)reader.read("/documentRootNode/ha/ha_permissions/permission", XPathConstants.NODESET);
	        for(int index = 1; index < (permissionNodes.getLength()+1); index ++){
	            
	            Permission permission = businessObjectFactory.newPermission(theSite);
	            
	            String permAuthAgentName = (String) reader.read("/documentRootNode/ha/ha_permissions/permission[" + index + "]/perm_aa_name", XPathConstants.STRING);
	            AuthorisingAgent authorisingAgent = extractAuthAgentByName(authAgents, permAuthAgentName);
	            permission.setAuthorisingAgent(authorisingAgent);
	            
	            String fromDate = (String) reader.read("/documentRootNode/ha/ha_permissions/permission[" + index + "]/perm_from_date", XPathConstants.STRING);
	            if(!fromDate.isEmpty()) {
					try {
						permission.setStartDate(sdf.parse(fromDate));
					} catch (ParseException e) {
						e.printStackTrace();
					}
	            }
	            
	            String toDate = (String) reader.read("/documentRootNode/ha/ha_permissions/permission[" + index + "]/perm_to_date", XPathConstants.STRING);
	            if(!toDate.isEmpty()) {
					try {
						permission.setEndDate(sdf.parse(toDate));
					} catch (ParseException e) {
						e.printStackTrace();
					}
	            }

	            String permStatus = (String) reader.read("/documentRootNode/ha/ha_permissions/permission[" + index + "]/perm_status", XPathConstants.STRING);
	            int status = 0;
	            if (permStatus.equals("Pending")) status = Permission.STATUS_PENDING;
	            if (permStatus.equals("Requested")) status = Permission.STATUS_REQUESTED;
	            if (permStatus.equals("Approved")) status = Permission.STATUS_APPROVED;
	            if (permStatus.equals("Rejected")) status = Permission.STATUS_DENIED;
	            permission.setStatus(status);
	            
	            permission.setSpecialRequirements((String) reader.read("/documentRootNode/ha/ha_permissions/permission[" + index + "]/perm_restrictions", XPathConstants.STRING));
	            permission.setCopyrightStatement((String) reader.read("/documentRootNode/ha/ha_permissions/permission[" + index + "]/perm_copyright", XPathConstants.STRING));
	            permission.setCopyrightUrl((String) reader.read("/documentRootNode/ha/ha_permissions/permission[" + index + "]/perm_copyright_url", XPathConstants.STRING));
	            permission.setAccessStatus((String) reader.read("/documentRootNode/ha/ha_permissions/permission[" + index + "]/perm_access_status", XPathConstants.STRING));
	            
	            String openAccessDate = (String) reader.read("/documentRootNode/ha/ha_permissions/permission[" + index + "]/perm_open_access_date", XPathConstants.STRING);
	            if(!openAccessDate.isEmpty()) {
					try {
						permission.setOpenAccessDate(sdf.parse(openAccessDate));
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	            }

	            permission.setQuickPick(((String) reader.read("/documentRootNode/ha/ha_permissions/permission[" + index + "]/perm_quick_pick", XPathConstants.STRING)).equals("true")?true:false);
	            permission.setDisplayName((String) reader.read("/documentRootNode/ha/ha_permissions/permission[" + index + "]/perm_display_name", XPathConstants.STRING));
	            permission.setFileReference((String) reader.read("/documentRootNode/ha/ha_permissions/permission[" + index + "]/perm_file_reference", XPathConstants.STRING));
	            permission.setCreateSeekPermissionTask(((String) reader.read("/documentRootNode/ha/ha_permissions/permission[" + index + "]/perm_assign_approval_task", XPathConstants.STRING)).equals("Yes")?true:false);
	            
		        Set<UrlPattern> urlPatterns = new HashSet<UrlPattern>();
	            NodeList permUrlNodes = (NodeList)reader.read("/documentRootNode/ha/ha_permissions/permission[" + index + "]/perm_urls/url", XPathConstants.NODESET);
		        for(int i = 0; i < permUrlNodes.getLength(); i ++){
		            Node aNode = permUrlNodes.item(i);
		            UrlPattern urlPattern = businessObjectFactory.newUrlPattern(theSite);
		            urlPattern.setPattern(aNode.getTextContent());
		            urlPattern.setSite(theSite);
		            urlPatterns.add(urlPattern);
				}        
		        permission.adjustUrlPatternSet(urlPatterns);
		        
				List<PermissionExclusion> exclusions = new ArrayList<PermissionExclusion>();

		        NodeList exclusionNodes = (NodeList)reader.read("/documentRootNode/ha/ha_permissions/permission[" + index + "]/perm_exclusions/exclusion", XPathConstants.NODESET);
		        for(int i = 1; i < (exclusionNodes.getLength()+1); i ++){

		            PermissionExclusion exclusion = new PermissionExclusion();
		            
		            String url = (String)reader.read("/documentRootNode/ha/ha_permissions/permission[" + index + "]/perm_exclusions/exclusion[" + i + "]/url", XPathConstants.STRING);
		            exclusion.setUrl(url);
		            String reason = (String)reader.read("/documentRootNode/ha/ha_permissions/permission[" + index + "]/perm_exclusions/exclusion[" + i + "]/reason", XPathConstants.STRING);
		            exclusion.setReason(reason);
		        	
		            exclusions.add(exclusion);
				}        
				permission.setExclusions(exclusions);
	            
				List<Annotation> permAnnotations = new ArrayList<Annotation>();

		        NodeList permAnnotationNodes = (NodeList)reader.read("/documentRootNode/ha/ha_permissions/permission[" + index + "]/perm_annotations/annotation", XPathConstants.NODESET);
		        for(int i = 0; i < permAnnotationNodes.getLength(); i ++){
		            Node aNode = permAnnotationNodes.item(i);
		            Annotation annotation = new Annotation();
		            annotation.setNote(aNode.getTextContent());
		        	annotation.setDate(new Date());
		        	annotation.setUser(validUser);
		        	annotation.setObjectType(Permission.class.getName());
		        	annotation.setObjectOid(null);

		        	permAnnotations.add(annotation);
				}        
				permission.setAnnotations(permAnnotations);
				
				permissions.add(permission);
			}        
			
	        theSite.setPermissions(permissions);
	        
			siteManager.save(theSite);

		}
		else if (useExistingSiteId.equalsIgnoreCase("addlater")) {
			return theSite; //null
		}
		else {
			
			theSite = siteManager.getSite(Long.parseLong(useExistingSiteId), true);
			
			List<Annotation> annotations = siteManager.getAnnotations(theSite);
			theSite.setAnnotations(annotations);
			
			for(Permission p: theSite.getPermissions()) {
				p.setAnnotations(siteManager.getAnnotations(p));
			}
		}
		  
		return theSite;
	  }

	  protected AuthorisingAgent extractAuthAgentByName(Set<AuthorisingAgent> authAgents, String name) {
		  
		  Iterator<AuthorisingAgent> iter = authAgents.iterator();
		  
		  while (iter.hasNext()) {
			  AuthorisingAgent aa = iter.next();
			  if(aa.getName().equals(name)) {
				  return aa;
			  }
		  }
		  return null;
	  }
	  
	  protected Permission extractPermissionByUrl(Set<Permission> permissions, String url) {
		  
		  Iterator<Permission> permIterator = permissions.iterator();
		  
		  while (permIterator.hasNext()) {
			  Permission perm = permIterator.next();
			  Set<UrlPattern> urlPatterns = perm.getUrls();
			  Iterator<UrlPattern> urlIterator = urlPatterns.iterator();
			  while (urlIterator.hasNext()) {
				  UrlPattern urlP = urlIterator.next();
				  if (urlP.getPattern().toLowerCase().startsWith(url.toLowerCase())) {
					  return perm;
				  }
			  }
		  }
		  return null;
	  }

	  protected Permission getFirstPermission(Set<Permission> permissions) {
		  
		  Iterator<Permission> permIterator = permissions.iterator();
		  
		  while (permIterator.hasNext()) {
			  Permission perm = permIterator.next();
			  return perm;
		  }
		  return null;
	  }
	
}
