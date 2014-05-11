<%@ page import="org.webcurator.domain.model.auth.Privilege" %>
<%@ page import="org.webcurator.domain.model.core.Target" %>
<jsp:directive.page import="org.webcurator.domain.model.core.DublinCore"/>
<%@taglib prefix = "c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="authority" uri="http://www.webcurator.org/authority"  %>
<%@taglib prefix="wct" uri="http://www.webcurator.org/wct"  %>

<table cellpadding="3" cellspacing="0" border="0">
  <tr>
    <td class="subBoxTextHdr">Title:</td>
    <td class="subBoxText">
      <authority:showControl ownedObject="${ownable}" privileges="${privleges}" editMode="${editMode}">
        <authority:show>
	      <input type="text" class="inputWide" name="title" value="<c:out value="${command.title}"/>" maxlength="<%= DublinCore.MAX_LEN_TITLE %>">
	    </authority:show>
	    <authority:dont>
	      <c:out value="${command.title}"/>
	    </authority:dont>
	  </authority:showControl>
    </td>
  </tr>
  <tr>
    <td class="subBoxTextHdr">Identifier:</td>
    <td class="subBoxText">
      <authority:showControl ownedObject="${ownable}" privileges="${privleges}" editMode="${editMode}">
        <authority:show>
	      <input type="text" class="inputWide" name="identifier" value="<c:out value="${command.identifier}"/>" maxlength="<%= DublinCore.MAX_LEN_IDENTIFIER %>">
	    </authority:show>
	    <authority:dont>
	      <c:out value="${command.identifier}"/>
	    </authority:dont>
	  </authority:showControl>
    </td>
  </tr>
  <tr>
    <td class="subBoxTextHdr">Description:</td>
    <td class="subBoxText">
      <authority:showControl ownedObject="${ownable}" privileges="${privleges}" editMode="${editMode}">
        <authority:show>
          <textarea name="description" cols="40" rows="6"><c:out value="${command.description}"/></textarea>	
	    </authority:show>
	    <authority:dont>
	      <c:out value="${command.description}"/>
	    </authority:dont>
	  </authority:showControl>
    </td>
  </tr>
  <tr>
    <td class="subBoxTextHdr">Subject:</td>
    <td class="subBoxText">
      <authority:showControl ownedObject="${ownable}" privileges="${privleges}" editMode="${editMode}">
        <authority:show>
          <textarea name="subject" cols="40" rows="6"><c:out value="${command.subject}"/></textarea>	
	    </authority:show>
	    <authority:dont>
	      <c:out value="${command.subject}"/>
	    </authority:dont>
	  </authority:showControl>
    </td>
  </tr>
  <tr>
    <td class="subBoxTextHdr">Creator:</td>
    <td class="subBoxText">
      <authority:showControl ownedObject="${ownable}" privileges="${privleges}" editMode="${editMode}">
        <authority:show>
	      <input type="text" class="inputWide" name="creator" value="<c:out value="${command.creator}"/>" maxlength="<%= DublinCore.MAX_LEN_CREATOR %>">
	    </authority:show>
	    <authority:dont>
	      <c:out value="${command.creator}"/>
	    </authority:dont>
	  </authority:showControl>
    </td>
  </tr>
  <tr>
    <td class="subBoxTextHdr">Publisher:</td>
    <td class="subBoxText">
      <authority:showControl ownedObject="${ownable}" privileges="${privleges}" editMode="${editMode}">
        <authority:show>
	      <input type="text" class="inputWide" name="publisher" value="<c:out value="${command.publisher}"/>" maxlength="<%= DublinCore.MAX_LEN_PUBLISHER %>">
	    </authority:show>
	    <authority:dont>
	      <c:out value="${command.publisher}"/>
	    </authority:dont>
	  </authority:showControl>
    </td>
  </tr>
  <tr>
    <td class="subBoxTextHdr">Contributor:</td>
    <td class="subBoxText">
      <authority:showControl ownedObject="${ownable}" privileges="${privleges}" editMode="${editMode}">
        <authority:show>
	      <input type="text" class="inputWide" name="contributor" value="<c:out value="${command.contributor}"/>" maxlength="<%= DublinCore.MAX_LEN_CONTRIBUTOR %>">
	    </authority:show>
	    <authority:dont>
	      <c:out value="${command.contributor}"/>
	    </authority:dont>
	  </authority:showControl>
    </td>
  </tr>
  <tr>
    <td class="subBoxTextHdr">Type:</td>
    <td class="subBoxText">
      <authority:showControl ownedObject="${ownable}" privileges="${privleges}" editMode="${editMode}">
        <authority:show>
          <wct:list list="${types}" paramName="type" currentValue="${command.type}"/>
	    </authority:show>
	    <authority:dont>
	      <c:out value="${command.type}"/>
	    </authority:dont>
	  </authority:showControl>
    </td>
  </tr>
  <tr>
    <td class="subBoxTextHdr">Format:</td>
    <td class="subBoxText">
      <authority:showControl ownedObject="${ownable}" privileges="${privleges}" editMode="${editMode}">
        <authority:show>
	      <input type="text" class="inputWide" name="format" value="<c:out value="${command.format}"/>" maxlength="<%= DublinCore.MAX_LEN_FORMAT %>">
	    </authority:show>
	    <authority:dont>
	      <c:out value="${command.format}"/>
	    </authority:dont>
	  </authority:showControl>
    </td>
  </tr>
  <tr>
    <td class="subBoxTextHdr">Source:</td>
    <td class="subBoxText">
      <authority:showControl ownedObject="${ownable}" privileges="${privleges}" editMode="${editMode}">
        <authority:show>
	      <input type="text" class="inputWide" name="source" value="<c:out value="${command.source}"/>" maxlength="<%= DublinCore.MAX_LEN_SOURCE %>">
	    </authority:show>
	    <authority:dont>
	      <c:out value="${command.source}"/>
	    </authority:dont>
	  </authority:showControl>
    </td>
  </tr>
  <tr>
    <td class="subBoxTextHdr">Language:</td>
    <td class="subBoxText">
      <authority:showControl ownedObject="${ownable}" privileges="${privleges}" editMode="${editMode}">
        <authority:show>
	      <input type="text" class="inputWide" name="language" value="<c:out value="${command.language}"/>" maxlength="<%= DublinCore.MAX_LEN_LANGAGE %>">
	    </authority:show>
	    <authority:dont>
	      <c:out value="${command.language}"/>
	    </authority:dont>
	  </authority:showControl>
    </td>
  </tr>
  <tr>
    <td class="subBoxTextHdr">Relation:</td>
    <td class="subBoxText">
      <authority:showControl ownedObject="${ownable}" privileges="${privleges}" editMode="${editMode}">
        <authority:show>
	      <input type="text" class="inputWide" name="relation" value="<c:out value="${command.relation}"/>" maxlength="<%= DublinCore.MAX_LEN_RELATION %>">
	    </authority:show>
	    <authority:dont>
	      <c:out value="${command.relation}"/>
	    </authority:dont>
	  </authority:showControl>
    </td>
  </tr>
  <tr>
    <td class="subBoxTextHdr">Coverage:</td>
    <td class="subBoxText">
      <authority:showControl ownedObject="${ownable}" privileges="${privleges}" editMode="${editMode}">
        <authority:show>
	      <input type="text" class="inputWide" name="coverage" value="<c:out value="${command.coverage}"/>" maxlength="<%= DublinCore.MAX_LEN_COVERAGE %>">
	    </authority:show>
	    <authority:dont>
	      <c:out value="${command.coverage}"/>
	    </authority:dont>
	  </authority:showControl>
    </td>
  </tr>
  <tr>
    <td class="subBoxTextHdr">ISSN:</td>
    <td class="subBoxText">
      <authority:showControl ownedObject="${ownable}" privileges="${privleges}" editMode="${editMode}">
        <authority:show>
	      <input type="text" name="issn" value="<c:out value="${command.issn}"/>" maxlength="<%= DublinCore.MAX_LEN_ISSN %>">
	    </authority:show>
	    <authority:dont>
	      <c:out value="${command.issn}"/>
	    </authority:dont>
	  </authority:showControl>
    </td>
  </tr>
  <tr>
    <td class="subBoxTextHdr">ISBN:</td>
    <td class="subBoxText">
      <authority:showControl ownedObject="${ownable}" privileges="${privleges}" editMode="${editMode}">
        <authority:show>
	      <input type="text" name="isbn" value="<c:out value="${command.isbn}"/>" maxlength="<%= DublinCore.MAX_LEN_ISBN %>">
	    </authority:show>
	    <authority:dont>
	      <c:out value="${command.isbn}"/>
	    </authority:dont>
	  </authority:showControl>
    </td>
  </tr>
</table>