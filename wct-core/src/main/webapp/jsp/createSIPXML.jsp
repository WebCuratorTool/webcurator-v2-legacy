<%@ page language="java" pageEncoding="UTF-8"%><%--
--%><%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %><%--
--%><%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %><%--
--%><%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %><%--
--%><%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles" %><%--
--%><%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %><%--
--%><%@ taglib uri="http://java.sun.com/jstl/xml" prefix="x" %><%--
--%><%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %><%--
--%><%@ taglib uri='http://www.webcurator.org/wct' prefix='wcttaglib' %><%--
--%><c:set var="xmlData" scope="request">
    	<?xml version="1.0" encoding="UTF-8"?>
    	<mets:mets xmlns:mets="http://www.loc.gov/METS/" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns="http://www.loc.gov/METS/" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://www.loc.gov/METS/ http://www.loc.gov/standards/mets/mets.xsd">
		<tags:wct-datetime var="dateNow" date="${now}"/><mets:metsHdr CREATEDATE="${dateNow}">
			<mets:agent ROLE="DISSEMINATOR" TYPE="INDIVIDUAL">
				<mets:name><c:out value="${user.niceName}"/></mets:name>
			</mets:agent>
			<mets:agent ROLE="CREATOR" TYPE="INDIVIDUAL">
				<mets:name><c:out value="${instance.owner.niceName}"/></mets:name>
			</mets:agent>
		</mets:metsHdr>
		<mets:dmdSec ID="DMD${instance.oid}" CREATED="${dateNow}" STATUS="current">
			<mets:mdWrap MDTYPE="DC">
				<mets:xmlData>
					<dc:dc xmlns:dc="http://purl.org/dc/elements/1.1/">
						<dc:title><c:out value="${target.dublinCoreMetaData.title}"/></dc:title>
						<dc:creator><c:out value="${target.dublinCoreMetaData.creator}"/></dc:creator>
						<dc:subject><c:out value="${target.dublinCoreMetaData.subject}"/></dc:subject>						
						<dc:description><c:out value="${target.dublinCoreMetaData.description}"/></dc:description>
						<dc:publisher><c:out value="${target.dublinCoreMetaData.publisher}"/></dc:publisher>
						<dc:contributor><c:out value="${target.dublinCoreMetaData.contributor}"/></dc:contributor>
						<dc:type><c:out value="${target.dublinCoreMetaData.type}"/></dc:type>
						<dc:format><c:out value="${target.dublinCoreMetaData.format}"/></dc:format>
						<dc:identifier><c:out value="${target.dublinCoreMetaData.identifier}"/></dc:identifier>																								
						<dc:source><c:out value="${target.dublinCoreMetaData.source}"/></dc:source>
						<dc:language><c:out value="${target.dublinCoreMetaData.language}"/></dc:language>
						<dc:relation><c:out value="${target.dublinCoreMetaData.relation}"/></dc:relation>
						<dc:coverage><c:out value="${target.dublinCoreMetaData.coverage}"/></dc:coverage>
						<dc:identifier><c:out value="${target.dublinCoreMetaData.issn}"/></dc:identifier>
						<dc:identifier><c:out value="${target.dublinCoreMetaData.isbn}"/></dc:identifier>
					</dc:dc>
					<wct:wct xmlns:wct="${webCuratorUrl}"> 
<c:out value="${sipSections['targetSection']}" escapeXml="false"/>						
						<wct:Groups><%-- 
					 	--%><c:forEach var="group" items="${groups}">
							<wct:Group>
								<wct:ReferenceNumber><c:out value="${group.referenceNumber}"/></wct:ReferenceNumber>
								<wct:Name><c:out value="${group.name}"/></wct:Name>
								<wct:Description><c:out value="${group.description}"/></wct:Description>
								<wct:Type><c:out value="${group.type}"/></wct:Type>
								<wct:OwnershipInformation><c:out value="${group.ownershipMetaData}"/></wct:OwnershipInformation>
							</wct:Group><%-- 
						 --%></c:forEach>							
						</wct:Groups>
					</wct:wct>
					<mets:mdRef>order.xml</mets:mdRef>
				</mets:xmlData>
			</mets:mdWrap>
		</mets:dmdSec>
		<mets:amdSec ID="AMD${instance.oid}">
			<mets:techMD ID="TMD${instance.oid}">
				<mets:mdWrap MDTYPE="OTHER">
					<mets:xmlData>
						<wct:wct xmlns:wct="${webCuratorUrl}">
						    <wct:TargetInstance> 
								<wct:Crawl>
								    <c:choose>
								    	<c:when test="${instance.status.applicationVersion != null}">
											<wct:AppVersion><c:out value="${instance.status.applicationVersion}"/></wct:AppVersion>
								    	</c:when>
								    	<c:otherwise>
											<wct:AppVersion/>
								    	</c:otherwise>
								    </c:choose>
									<wct:StartDate><c:out value="${instance.actualStartTime}"/></wct:StartDate>
									<wct:StartDate><c:out value="${instance.actualStartTime}"/></wct:StartDate>
									<wct:Duration><c:out value="${instance.status.elapsedTime}"/></wct:Duration>
								    <c:choose>
								    	<c:when test="${instance.status.heritrixVersion != null}">
											<wct:CaptureSystem><c:out value="${instance.status.heritrixVersion}"/></wct:CaptureSystem>
								    	</c:when>
								    	<c:otherwise>
											<wct:CaptureSystem><c:out value="${heritrixVersion}"/></wct:CaptureSystem>
								    	</c:otherwise>
								    </c:choose>
									<wct:URLs>
										<wct:Downloaded><c:out value="${instance.status.urlsDownloaded}"/></wct:Downloaded>
										<wct:Failed><c:out value="${instance.status.urlsFailed}"/></wct:Failed>
									</wct:URLs>
									<wct:AverageBandwidth><c:out value="${instance.status.averageKBs}"/></wct:AverageBandwidth>
									<wct:DocumentProcessRate><c:out value="${instance.status.averageURIs}"/></wct:DocumentProcessRate>
									<wct:DownloadedDataSize><c:out value="${instance.status.dataDownloaded}"/></wct:DownloadedDataSize>
								</wct:Crawl>
								<wct:Annotations><%-- 
					 			--%><c:forEach var="note" items="${instanceAnnotations}">
									<wct:Annotation>
										<wct:Date><tags:wct-date date="${note.date}"/></wct:Date>
										<wct:Username><c:out value="${note.user.username}"/></wct:Username>
										<wct:Note><c:out value="${note.note}"/></wct:Note>
									</wct:Annotation><%-- 
							 	--%></c:forEach>
								</wct:Annotations>
							</wct:TargetInstance>
						</wct:wct>
					</mets:xmlData>
				</mets:mdWrap>
			</mets:techMD>
			<mets:rightsMD ID="RMD${instance.oid}">
				<mets:mdWrap MDTYPE="OTHER">
					<mets:xmlData>
						<wct:wct xmlns:wct="${webCuratorUrl}">
<c:out value="${sipSections['permissionSection']}" escapeXml="false"/>			 
						</wct:wct>
					</mets:xmlData>
				</mets:mdWrap>
			</mets:rightsMD>
			<mets:digiprovMD ID="DPMD${instance.oid}">
				<mets:mdWrap MDTYPE="OTHER">
					<mets:xmlData>
						<wct:wct xmlns:wct="${webCuratorUrl}">
							<wct:TargetInstance>
     							<wcttaglib:harvestResultChain chain="${resultChain}"/>
								<wct:Owner> 
									<wct:UID><c:out value="${instance.owner.username}"/></wct:UID>
									<wct:Agency><c:out value="${instance.owner.agency.name}"/></wct:Agency>
								</wct:Owner>
								<wct:ModificationType></wct:ModificationType>
								<wct:ModiicationNote></wct:ModiicationNote>
								<wct:HarvestServer><c:out value="${instance.harvestServer}"/></wct:HarvestServer>
						    	<wct:DisplayTargetInstance><c:choose><c:when test="${instance.display==true}"><c:out value="true"/></c:when><c:otherwise><c:out value="false"/></c:otherwise></c:choose></wct:DisplayTargetInstance>
						    	<wct:TargetInstanceDisplayNote><c:out value="${instance.displayNote}"/></wct:TargetInstanceDisplayNote>
							</wct:TargetInstance>
							<wct:Target>
								<wct:ObjectType><c:if test="${target.objectType==0}">Target Group</c:if><c:if test="${target.objectType==1}">Target</c:if></wct:ObjectType>
								<c:if test="${target.objectType==0}">
								<wct:GroupType><c:out value="${target.type}"/></wct:GroupType>
								<wct:GroupOwnershipInformation><c:out value="${target.ownershipMetaData}"/></wct:GroupOwnershipInformation>
								</c:if>
<c:out value="${sipSections['profileNoteSection']}" escapeXml="false"/>								
								<c:if test="${target.objectType==1}">
								<wct:HarvestType><c:out value="${target.harvestType}"/></wct:HarvestType>								
								<wct:SelectionDate><tags:wct-date date="${target.selectionDate}"/></wct:SelectionDate>
								<wct:SelectionType><c:out value="${target.selectionType}"/></wct:SelectionType>
								<wct:SelectionNote><c:out value="${target.selectionNote}"/></wct:SelectionNote>
								<wct:EvaluationNote><c:out value="${target.evaluationNote}"/></wct:EvaluationNote>
								</c:if>
								<wct:Annotations><%--  
							 --%><c:forEach var="tnote" items="${targetAnnotations}"><%-- 
							 --%><wct:Annotation>
									<wct:Date><tags:wct-date date="${tnote.date}"/></wct:Date>
									<wct:User>${tnote.user.username}</wct:User>
									<wct:Note>${tnote.note}</wct:Note>
								</wct:Annotation>
								</c:forEach><%-- 
						 --%></wct:Annotations><%-- 
						 --%><wct:DisplayTarget><c:choose><c:when test="${target.displayTarget==true}"><c:out value="true"/></c:when><c:otherwise><c:out value="false"/></c:otherwise></c:choose></wct:DisplayTarget>
						    	<wct:AccessZone><c:out value="${target.accessZone}"/></wct:AccessZone>
						    	<wct:AccessZoneText><c:out value="${target.accessZoneText}"/></wct:AccessZoneText>
						    	<wct:TargetDisplayNote><c:out value="${target.displayNote}"/></wct:TargetDisplayNote>
							</wct:Target>
						</wct:wct>
						<vCard:VCard xmlns:vCard="http://www.w3.org/2001/vcard-rdf/3.0#">
							<vCard:FN><c:out value="${instance.owner.fullName}"/></vCard:FN>
							<vCard:N>
								<vCard:FAMILY><c:out value="${instance.owner.lastname}"/></vCard:FAMILY>
								<vCard:GIVEN><c:out value="${instance.owner.firstname}"/></vCard:GIVEN>
								<vCard:PREFIX><c:out value="${instance.owner.title}"/></vCard:PREFIX>
							</vCard:N>
							<vCard:EMAIL>
								<vCard:WORK><c:out value="${instance.owner.email}"/></vCard:WORK>
							</vCard:EMAIL>
							<vCard:ORG>
								<vCard:Orgname><c:out value="${instance.owner.agency.name}"/></vCard:Orgname>
								<vCard:EMAIL><c:out value="${instance.owner.agency.email}"/></vCard:EMAIL>
							</vCard:ORG>
							<vCard:URL><c:out value="${instance.owner.agency.agencyURL}"/></vCard:URL>
							<vCard:LOGO><c:out value="${instance.owner.agency.agencyLogoURL}"/></vCard:LOGO>
							<vCard:UID><c:out value="${instance.owner.username}"/></vCard:UID>
						</vCard:VCard>
					</mets:xmlData>
				</mets:mdWrap>
			</mets:digiprovMD>
		</mets:amdSec> 
	</c:set>