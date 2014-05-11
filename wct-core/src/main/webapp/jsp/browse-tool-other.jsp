<%@page import="java.io.*"%><%	
    File f = (File) request.getAttribute("file");
    String contentType = (String) request.getAttribute("contentType");
	response.setHeader("Content-Type", contentType);

    OutputStream os = response.getOutputStream();
    FileInputStream fis = null;
    byte[] byteBuff = new byte[1024];
    int bytesRead = -1;
    
    try {
    	fis = new FileInputStream(f);
		while( (bytesRead = fis.read(byteBuff, 0, 1024)) != -1) {
			os.write(byteBuff, 0, bytesRead);
		}
	}
	catch(Exception ex) {
		ex.printStackTrace();
	}
	finally
	{
		fis.close();
		if(f.exists())
		{
			f.delete();
		}
	}
%>