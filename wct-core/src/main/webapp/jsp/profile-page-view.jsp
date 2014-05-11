<%@page import="org.archive.crawler.settings.*"%>
<%@page import="org.webcurator.core.profiles.*"%>
<%@page import="org.archive.crawler.filter.HopsFilter"%>
<%@page import="org.archive.crawler.framework.Filter"%>
<%@page import="org.archive.crawler.deciderules.DecidingFilter"%>
<%@page import="org.archive.crawler.fetcher.FetchDNS"%>
<%@page import="org.archive.crawler.settings.MapType"%>
<%@page import="org.webcurator.ui.profiles.renderers.*"%>


  <input type="hidden" id="action" name="action">
  
  <input type="hidden" id="mapName" name="mapName">
  <input type="hidden" id="elementToMove" name="elementToMove">
  <input type="hidden" name="subaction" id="subaction">
  <input type="hidden" id="moveUpForm_scrollY" name="scrollY">  

  <input type="hidden" id="newElemName" name="newElemName">
  <input type="hidden" id="newElemType" name="newElemType">
  <input type="hidden" id="newMapElemForm_scrollY" name="scrollY">

  <input type="hidden" name="action" value="changeScope">
  <input type="hidden" id="scopeClass" name="scopeClass">


<script>

  function simpleMapAdd(mapName) {
	document.getElementById('action').value = 'simpleMap-add';
    document.getElementById('mapName').value = mapName;
//    submitForm();
  }
  
  function mapAction(complexElement, elementToMove, subAction) {
      document.getElementById('mapName').value = complexElement;
      document.getElementById('elementToMove').value = elementToMove;
      document.getElementById('subaction').value = subAction;
      document.getElementById('moveUpForm_scrollY').value = getScrollY();
      
      document.getElementById('action').value='map';
      
      submitForm();
  }
  
  function minimise(elementName) {
      document.getElementById(elementName).style.display = 'none';
  }
  
  function maximise(elementName) {
      document.getElementById(elementName).style.display = 'block';
  }  
  
  function addMapElement(complexElement) {
      var newElemName = document.getElementById(complexElement + '.name').value;
      var newElemType = document.getElementById(complexElement + '.type').value;
      
      document.getElementById('mapName').value = complexElement;
      document.getElementById('newElemName').value = newElemName;
      document.getElementById('newElemType').value = newElemType;
      document.getElementById('newMapElemForm_scrollY').value = getScrollY();
      
      document.getElementById('action').value='addMapElement';      
      
     // submitForm();
  }
  
  function addProcessorMapElement(complexElement) {
      var newElem = document.getElementById(complexElement + '.type').value;
      a = document.getElementById(complexElement + '.type').value.split('|');
      
      document.getElementById('mapName').value = complexElement;
      document.getElementById('newElemName').value = a[1];
      document.getElementById('newElemType').value = a[0];
      document.getElementById('newMapElemForm_scrollY').value = getScrollY();
      
      document.getElementById('action').value='addMapElement';
      
//      submitForm();
  }  
  
  function addToList(listTypeElement) {
      var val = document.getElementById(listTypeElement + '.new').value;
      var opt = new Option(val, val, false, false);
      document.getElementById(listTypeElement + '.list').options.add(opt);
      listToText(listTypeElement);
  }
  
  function removeFromList(listTypeElement) {
      listBox = document.getElementById(listTypeElement + '.list');
      listBox.options[listBox.selectedIndex] = null;
      listToText(listTypeElement);
  }
  
  function listToText(listTypeElement) {
    listBox = document.getElementById(listTypeElement + '.list');
    var val = '';
   
    for(i=0;i<listBox.length;i++) {
      if(i != 0) {
        val = val + '\n';
      }
      val = val + listBox.options[i].value;
    }
    
    document.getElementById(listTypeElement).value = val;  
  }
  
  
  function changeScope(absElementName) {
      document.getElementById('scopeClass').value = document.getElementById(absElementName+'.type').value;
      document.getElementById('action').value='changeScope';
  }
  
  function submitForm() {
  	document.getElementById('tabForm').submit();
  }
  
  var allLists = new Array();
  
  function registerList(listElementName) {
      allLists[allLists.length] = listElementName;
  }
  
  function selectAllItems(listBox) {
      for(i=0; i<listBox.options.length; i++) {
          listBox.options[i].selected = true;
      }
  }
  
  function onTabSubmit() {
      for(i=0;i<allLists.length;i++) {
           selectAllItems(document.getElementById(allLists[i]));
      }
  }
  
    function getScrollY() {
      var scrOfY = 0;
      if( typeof( window.pageYOffset ) == 'number' ) {
        //Netscape compliant
        scrOfY = window.pageYOffset;
      } else if( document.body && ( document.body.scrollLeft || document.body.scrollTop ) ) {
        //DOM compliant
        scrOfY = document.body.scrollTop;
      } else if( document.documentElement && ( document.documentElement.scrollLeft || document.documentElement.scrollTop ) ) {
        //IE6 standards compliant mode
        scrOfY = document.documentElement.scrollTop;
      }
      return scrOfY;
    }  
  
  
</script>

<div id="profile">
<div id="annotationsBox">
<%  
    // Load the items out of the request.
    HeritrixProfile profile = (HeritrixProfile) request.getAttribute("heritrixProfile");
    RendererFilter filter = (RendererFilter) request.getAttribute("recursionFilter");
    String baseAttribute = (String) request.getAttribute("baseAttribute");
    
    // Display the profile.
    ProfileElement pe = profile.getElement(baseAttribute);
	Renderer r = RendererManager.getRenderer(pe);
	r.render(pe, pageContext, filter);
%>
</div>
</div>

