<%	String contentType = (String) request.getAttribute("Content-Type");
    String content = (String) request.getAttribute("content");
	response.setHeader("Content-Type", contentType);
	out.print(content);
%>