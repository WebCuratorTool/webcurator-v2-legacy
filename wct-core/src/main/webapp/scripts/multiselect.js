  ///////////////////////////////////////////////////////////////
  // Moves the selected item from one listbox to another
  ///////////////////////////////////////////////////////////////

  function moveItem(from, to) {
    var curIndex = from.selectedIndex;

    if(curIndex == -1) { 
      // No item selected, nothing to do.
      return;
    }

    // Create the element.
    var element = document.createElement("OPTION");
    element.text = from.options[curIndex].text;
    element.value = from.options[curIndex].value;

    // Add the site back to the not linked sites list
    var found = false;
    

    // Find out where to put the element in the TO box.
    for (var i = 0; i < to.options.length && !found; i++) {
      if (to.options[i].text > element.text) {
        found = true;
        to.options.add(element,i);
      }
    }
    if( !found ) {  
      to.options.add(element);
    }

    //Remove the site from the from list
    from.remove(curIndex);

  }  
  
  
  ///////////////////////////////////////////////////////////////
  // Takes the ids from a listbox, and places them in a hidden
  // text field.  Each element is separated with the vertical
  // bar ( | ).
  ///////////////////////////////////////////////////////////////

  function listBoxToHidden(listbox, hidden) {
    hidden.value = "";
    var length = listbox.options.length;

    if(length > 0) {
      hidden.value = listbox.options[0].value;
      
      for( var i = 1; i < length; i++) {
        hidden.value = hidden.value + "|" + listbox.options[i].value;
      }
    }  
  }  