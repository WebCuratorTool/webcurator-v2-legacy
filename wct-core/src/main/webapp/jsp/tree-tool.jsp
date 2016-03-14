<%@ page import="org.webcurator.ui.tools.command.TreeToolCommand"%>
<%@ page import="org.webcurator.ui.common.Constants"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib uri='http://www.webcurator.org/wct' prefix='wct'%>

<style>
td {
	font-size: 8pt;
	font-family: arial;
	margin: 0;
	padding: 0;
}

a {
	color: black;
	text-decoration: none;
}
.row0 { background-color:#ffffff; }
.row1 { background-color:#eeeeee; }

</style>

<script>
	String.prototype.trim = function(){
		return (this.replace(/^[\s\xA0]+/, "").replace(/[\s\xA0]+$/, ""))
	}

	String.prototype.startsWith = function(str) {
		return (this.match("^"+str)==str)
	}

	String.prototype.endsWith = function(str) {
		return (this.match(str+"$")==str)
	}
		
	function TryAjaxPost()
    {
        var ajaxRequest;
        var self = this;
    
        // Non-IE browser
        if (window.XMLHttpRequest)
        {
            self.ajaxRequest = new XMLHttpRequest();
        }
        // IE
        else
        if (window.ActiveXObject)
        {
            self.ajaxRequest = new ActiveXObject("Microsoft.XMLHTTP");
        }
    	var queryString = GetFormValues();
        self.ajaxRequest.open('POST', 'curator/tools/treetoolAJAX.html', true);
        self.ajaxRequest.setRequestHeader('Content-Type', 
                 'application/x-www-form-urlencoded');
        self.ajaxRequest.setRequestHeader("Content-length", queryString.length);
		self.ajaxRequest.setRequestHeader("Connection", "close");
    
        var LOADED=4
        self.ajaxRequest.onreadystatechange = function()
        {
            if (self.ajaxRequest.readyState == LOADED)
            {
                doUpdate(self.ajaxRequest.responseText);
                document.body.style.cursor = "default";
                
            }
        }
    
        self.ajaxRequest.send(queryString);
        document.body.style.cursor = "wait";
        
        
    }    
    
    function doUpdate(message)
    {
        document.getElementById("treePanel").innerHTML = message;
    }
    
    function GetFormValues()
	{
		var str = '';
		var elem = document.getElementById('myform').elements;
		for(var i = 0; i < elem.length; i++)
		{
			str += elem[i].name + "=" + elem[i].value + "&";
		}
		 
		if (str.length > 0)
		{
			str = str.substr(0,str.length - 1);
		}
		//alert(str);
		return str;
	}


    function toggle(item) {
      document.forms.myform.toggleId.value = item;
      document.forms.myform.markForDelete.value = "";
      document.forms.myform.propagateDelete.value = "";
      document.getElementById("a_" + item).style.cursor = "wait";
      if (window.XMLHttpRequest || window.ActiveXObject)
        {
            TryAjaxPost();
        }
        else
        {
      		document.forms.myform.submit();
      	}  
    }
    
    var selectedRow = -1;
    var origBgColor = "#CCEECC";
      
    function selRow(newSelRow) {
      if(selectedRow != -1) {
      	if (document.getElementById("row_" + selectedRow))
      	{
        	document.getElementById("row_" + selectedRow).style.backgroundColor = origBgColor;
        }
      }
      selectedRow = newSelRow;
      if (document.getElementById("row_" + newSelRow))
      {
	      origBgColor = document.getElementById("row_" + newSelRow).style.backgroundColor;
	      document.getElementById("row_" + newSelRow).style.backgroundColor = "#CCEECC";
	      document.getElementById("selectedRow").value=selectedRow;
	      document.getElementById("selectedRow2").value=selectedRow;
  		  var theSpan=document.getElementById("span_" + selectedRow);
		  if( theSpan.children.length == 0) {
		  	document.getElementById("selectedUrl").value=theSpan.innerHTML;
		  } else {
		  	document.getElementById("selectedUrl").value=theSpan.children(0).innerHTML;
		  }
      }
    }
    
    function copyURLToTarget(selRow) {
        if(selectedRow != -1) {
        	if (document.getElementById("targetURL")) {
        		var theSpan=document.getElementById("span_" + selectedRow);
        		if( theSpan.children.length == 0) {
        			document.getElementById("targetURL").value=theSpan.innerHTML;
        		} else {
        			document.getElementById("targetURL").value=theSpan.children(0).innerHTML;
        		}
        	}
        }
        else {
            alert("You must select an item first");
            return false;
          }
    }
    
    function copyURLToSource(selRow) {
        if(selectedRow != -1) {
        	if (document.getElementById("sourceURL"))
        		var theSpan=document.getElementById("span_" + selectedRow);
	    		if( theSpan.children.length == 0) {
	    			document.getElementById("sourceURL").value=theSpan.innerHTML;
	    		} else {
	    			document.getElementById("sourceURL").value=theSpan.children(0).innerHTML;
	    		}
        }
        else {
            alert("You must select an item first");
            return false;
          }
    }

    function prune(propagate) {
      if(selectedRow != -1) {
  		var theSpan=document.getElementById("span_" + selectedRow);
		if( theSpan.children.length != 0) {
	        alert("This item has been selected already.");
	        return false;
		}

      	document.forms.myform.toggleId.value = "";
        document.forms.myform.markForDelete.value = selectedRow;
        document.forms.myform.propagateDelete.value = propagate;
        if (window.XMLHttpRequest || window.ActiveXObject)
        {
            TryAjaxPost();
        }
        else
        {
      		document.forms.myform.submit();
      	} 
        selectedRow = -1;
      }
      else {
        alert("You must select an item first");
        return false;
      }
    }
    
    function view() {
      if(selectedRow != -1) { 
		var theSpan=document.getElementById("span_" + selectedRow);
		if( theSpan.children.length == 0) {
		  if (theSpan.innerHTML.endsWith("/")) {
		    alert("You must select a document Url");
		    return false;
		  }
		  else {
	        return true;
		  }
		} else {
		  if (theSpan.children(0).innerHTML.endsWith("/")) {
		    alert("You must select a document Url");
		    return false;
		  }
		  else {
	        return true;
		  }
		}
        
      }
      else {
        alert("You must select an item first");
        return false;
      }
    }

    function hopPath() {
        if(selectedRow != -1) { 
          return true;
        }
        else {
          alert("You must select an item first");
          return false;
        }
      }

    function importFile() {
        if(document.getElementById("targetURL").value.trim().toLowerCase().startsWith("http://")) {

	    	if(document.getElementById("sourceFile").value.trim() != "") {
	    	  document.forms.importform.importType.value = "file";
	    	  document.forms.importform.submit();
	          selectedRow = -1;
	        }
	        else {
	          alert("You must specify a source file name to import.");
	          return false;
        	}
        }
        else {
          alert("You must specify a valid target URL.");
          return false;
    	}
      }
    
    function importURL() {
			if(document.getElementById("targetURL").value.trim().toLowerCase().startsWith("http://") ||
					document.getElementById("targetURL").value.trim().toLowerCase().startsWith("https://")) {
			
				if(document.getElementById("sourceURL").value.trim() != "") {
					if (document.getElementById("sourceURL").value.trim().toLowerCase().startsWith("http://") ||
							document.getElementById("sourceURL").value.trim().toLowerCase().startsWith("https://")) {
				    	document.forms.importform.importType.value = "URL";
				    	document.forms.importform.submit();
				        selectedRow = -1;
					} else {
						alert("You must specify a valid source URL.");
						return false;
					}
			    }
			    else {
				    alert("You must specify a source URL to import.");
				    return false;
			   	}
			}
			else {
				alert("You must specify a valid target URL.");
				return false;
			}
      }

    function typeOf(obj) {
  	  if ( typeof(obj) == 'object' ) {
  	    if (obj.length)
  	      return 'array';
  	    else
  	      return 'object';
  	  } else {
  	    return typeof(obj);
  	  }
    }
    
    function verifySelections() {
    	// check we have selected some items to import
    	if (document.getElementById("aqaImportform").aqaImports == null) {
    		// no checkboxes present
    		alert('There are no items available to import.');
    		return false;
    	} else {
            var count=0;
    	    // a single checkbox is not an array of checkboxes.
    		if (typeOf(document.getElementById("aqaImportform").aqaImports)=='object') {
    			if (document.getElementById("aqaImportform").aqaImports.checked==true) count++;
    		} else {
    			for(var i=0; i < document.getElementById("aqaImportform").aqaImports.length; i++){
    				if(document.getElementById("aqaImportform").aqaImports[i].checked) {
    					count ++;
    				};
    			};
    		};
    		if (count == 0) {
    			alert('No items selected.');
    			return false;
    		}
    		// OK we have selected at least one item..
   			var proceed=false;
			proceed=confirm('Are you sure want to import the selected items?');
   			if (proceed) {
   				return true;
   			} else {
   				return false;
   			};
    	}
    }

    function checkAllItems()
    {
  	  if (document.getElementById("aqaImportform").aqaImports != null) {
  	      // a single checkbox is not an array of checkboxes.
  	      if (typeOf(document.getElementById("aqaImportform").aqaImports)=='object') {
  	    	document.getElementById("aqaImportform").aqaImports.checked=true;
  	      } else {
  			  for (i = 0; i < document.getElementById("aqaImportform").aqaImports.length; i++)
  				document.getElementById("aqaImportform").aqaImports[i].checked = true ;
  	      }
  	  };
  	  return false;
    }
   
    function unCheckAllItems()
    {
  	  if (document.getElementById("aqaImportform").aqaImports != null) {
  	      // a single checkbox is not an array of checkboxes.
  	      if (typeOf(document.getElementById("aqaImportform").aqaImports)=='object') {
  	    	document.getElementById("aqaImportform").aqaImports.checked=false;
  	      } else {
  			  for (i = 0; i < document.getElementById("aqaImportform").aqaImports.length; i++)
  				document.getElementById("aqaImportform").aqaImports[i].checked = false ;
  	      }
  	  };
  	  return false;
    }
    
</script>
<div id="treePanel"><wct:tree tree="${tree}" /></div>
<div id="resultsTable">
<table width="800px" cellpadding="5" cellspacing="0" border="0">
	<tr>
		<td colspan="3" class="tableRowLite"><img src="images/x.gif" alt="" width="1"
			height="5" border="0" /></td>
	</tr>
	<tr valign="top">
		<td valign="top" width="45%">
			<fieldset style="width:370px;">
			<legend class="groupBoxLabel">Viewing</legend>
			<table width=100%>
				<tr>
					<td width=50%>
						<form action="curator/tools/treetool.html" method="post" target="_blank">
						<input type="hidden" name="actionCmd" value="<%=TreeToolCommand.ACTION_VIEW%>">
						<input type="hidden" id="selectedRow" name="selectedRow" value="">
						<input type="image" src="images/home-btn-view.gif" alt="View"
							onclick="return view();">
						</form>
					</td>
					<td width=50%>
						<form action="curator/tools/treetool.html" method="post" target="_blank">
						<input type="hidden" name="actionCmd" value="<%=TreeToolCommand.ACTION_SHOW_HOP_PATH%>">
						<input type="hidden" id="selectedRow2" name="selectedRow2" value=""><br/>
						<input type="image" src="images/home-btn-hop-path.gif" alt="Show Hop Path"
							onclick="return hopPath();">
						<input type="hidden" id="selectedUrl" name="selectedUrl" value="">
						</form>
					</td>
				</tr>
			</table>
			</fieldset>
		</td>
		<td valign="top" width="10%">
		</td>
		<td valign="top" width="45%">
			<fieldset style="width:370px;">
			<legend class="groupBoxLabel">Pruning</legend>
			<table>
				<tr>
					<td>
					<form id="myform" action="curator/tools/treetool.html"
						method="post"><input type="hidden" name="actionCmd"
						id="actionCmd" value="<%=TreeToolCommand.ACTION_TREE_ACTION%>">
					<input type="hidden" name="hrOid"
						value="<c:out value="${command.hrOid}"/>"> <input
						type="hidden" name="toggleId" value=""> <input
						type="hidden" id="markForDelete" name="markForDelete" value="">
					<input type="hidden" id="propagateDelete" name="propagateDelete"
						value=""></form> <img
						src="images/generic-btn-prunesingle.gif" alt="Prune Single Item"
						onclick="prune(false);" style="cursor: pointer"> <img 
						src="images/generic-btn-prunesingleplus.gif"
						alt="Prune Item and Children" onclick="prune(true);" style="cursor: pointer">
					</td>
				</tr>
			</table>
			</fieldset>
		</td>
	</tr>
	<tr>
		<td colspan="3" class="tableRowLite"><hr/></td>
	</tr>
	<tr>
		<td valign="top" colspan="3">
			<fieldset style="width:790px;">
			<legend class="groupBoxLabel">Importing</legend>
			<form id="importform" action="curator/tools/treetool.html"
				method="post" enctype="multipart/form-data">
				<input type="hidden" name="actionCmd" id="actionCmd" value="<%=TreeToolCommand.ACTION_TREE_ACTION%>">
				<input type="hidden" name="hrOid" value="<c:out value="${command.hrOid}"/>">
				<input type="hidden" id="importType" name="importType" value="">
				<table>
					<tr>
						<td>
							<font color="blue"><strong>All URLs must be of the form http(s)://[host-name]/[file-path]/[file-name] e.g http://crawledsite.com/images/logo.jpg</strong></font><br /><br />
						</td>
					</tr>
					<tr>
						<td>
							Specify&nbsp;Target&nbsp;URL:&nbsp;<input size="100" type="text" id="targetURL" name="targetURL" value=""/> 
							&nbsp;<input type="button" alt="Copy Selected URL" style="cursor: pointer" value="Use Selected" onclick="copyURLToTarget();"/>
						</td>
					</tr>
					<tr>
						<td><hr/></td>
					</tr>
					<tr>
						<td width="100%">
							<fieldset style="width:700px;">
							<legend class="groupBoxLabel">Import from disk file</legend>
							<table width="100%">
								<tr>
									<td>
										Source File:&nbsp;<input size="100" type="file" id="sourceFile" name="sourceFile" value=""/> 
									</td>
								</tr>
								<tr>
									<td><br/></td>
								</tr>
								<tr>
									<td align="right">
										<img src="images/generic-btn-import.gif" alt="Import Disk File"
											 onclick="importFile();" style="cursor: pointer">
									</td>
								</tr>
							</table>
							</fieldset>
						</td>
					</tr>
					<tr>
						<td width="100%">
							<fieldset style="width:700px;">
							<legend class="groupBoxLabel">Import from URL</legend>
							<table width="100%">
								<tr>
									<td>
										Source&nbsp;URL:&nbsp;<input size="95" type="text" id="sourceURL" name="sourceURL" value=""/> 
										&nbsp;<input type="button" alt="Copy Selected URL" style="cursor: pointer" value="Use Selected" onclick="copyURLToSource();"/>
									</td>
								</tr>
								<tr>
									<td><br/></td>
								</tr>
								<tr>
									<td align="right">
										<img src="images/generic-btn-import.gif" alt="Import Item via URL"
											 onclick="importURL();" style="cursor: pointer">
									</td>
								</tr>
							</table>
							</fieldset>
						</td>
					</tr>
				</table>
			</form>
			</fieldset>
		</td>
	</tr>
    <c:choose>
    	<c:when test="${showAQAOption == 1}">
			<tr>
				<td valign="top" colspan="3">
					<fieldset style="width:790px;">
					<legend class="groupBoxLabel">Automated QA Imports</legend>
					<form id="aqaImportform" action="curator/tools/treetool.html" method="post">
						<input type="hidden" name="actionCmd" id="actionCmd" value="<%=TreeToolCommand.IMPORT_AQA_FILE%>">
						<input type="hidden" name="hrOid" value="<c:out value="${command.hrOid}"/>">
							<table border="0" width="100%">
								<tr>
									<th width="75%" class="tableHead">Url</th>
									<th width="25%" class="tableHead">
									AQA Stored Content<br />
									<input type="button" name="Check_all" value="Select all" onClick="checkAllItems();">&nbsp;<input type="button" name="Uncheck_all" value="De-select all" onClick="unCheckAllItems();">
									</th>
								</tr>
								
								<c:choose>
    								<c:when test="${fn:length(aqaImports) == 0}">
    									<tr>
    										<td colspan="2" class="tableRowLite">There are no URLs available for import.</td>
    									</tr>
									</c:when>
									<c:otherwise>
										<c:forEach items="${aqaImports}" var="aqaImport" varStatus="lineInfo">
										<tr class="row<c:out value="${lineInfo.index % 2}"/>">
											<td style="word-break: break-all;" valign="top">
											<c:out value="${aqaImport.url}"/>
											</td>
											<td valign="top">
											<a href="curator/target/content-viewer.html?targetInstanceOid=<c:out value="${command.targetInstanceOid}"/>&logFileName=<c:out value="${aqaImport.contentFile}"/>" target="_blank">View</a> | 
											Import&nbsp;to&nbsp;harvest:&nbsp;<input type="checkbox" name="aqaImports" value="${aqaImport.url}">
											</td>
										</tr>
										</c:forEach>
										<tr>
											<td colspan="2" align="right">
												<input type="image" src="images/generic-btn-import.gif" alt="Import checked items"
													 onclick="return verifySelections();" style="cursor: pointer"/>
											</td>
										</tr>
									</c:otherwise>
								</c:choose>
								
							</table>
					</form>
					</fieldset>
				</td>
			</tr>
		</c:when>
	</c:choose>

</table>

<form id="saveForm" action="curator/tools/treetool.html" method="post">
    <input type="hidden" id="saveFormActionCmd" name="actionCmd" value="<%=TreeToolCommand.ACTION_SAVE%>">
    <input type="hidden" name="hrOid" value="<c:out value="${command.hrOid}"/>">
	<table>
		<tr>
			<td class="tableRowLite">
				<img src="images/x.gif" alt="" width="1" height="1" border="0" />
			</td>
		</tr>
		<tr>
			<td valign="top">
				<fieldset style="width:790px;">
				<legend class="groupBoxLabel">Provenance Note:</legend>
					<table width="100%">
						<tr>
							<td>
								<textarea name="provenanceNote" 
									rows="<%=Constants.ANNOTATION_ROWS%>" 
									cols="<%=Constants.ANNOTATION_COLS%>">
									<c:out value="${command.provenanceNote}" />
								</textarea>
							</td>
						</tr>
						<tr class="tableRowLite">
							<td align="right">
								<input type="image" src="images/generic-btn-save.gif" alt="Save" name="SAVE"
								onclick="document.getElementById('saveFormActionCmd').value='<%=TreeToolCommand.ACTION_SAVE%>';">
						    	&nbsp;&nbsp;<input type="image" src="images/generic-btn-cancel.gif" alt="Cancel"
								name="CANCEL"
								onclick="document.getElementById('saveFormActionCmd').value='<%=TreeToolCommand.ACTION_CANCEL%>';">
							</td>
						</tr>
					</table>
				</fieldset>
			</td>
		</tr>
	</table>
</form>
</div>