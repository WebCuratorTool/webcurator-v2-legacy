<%	
    byte[] bytesBuffer = (byte[]) request.getAttribute("bytesBuffer");
    String contentType = (String) request.getAttribute("contentType");
	response.setHeader("Content-Type", contentType);
	if(bytesBuffer!=null) {    
		response.getOutputStream().write(bytesBuffer);
  	}
%>