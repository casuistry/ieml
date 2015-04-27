<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ page import="NewParser.*" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Parsing result</title>
</head>
<body>
 <% 
 String s=request.getParameter("iemltext");
 StringBuilder builder = new StringBuilder();
 ParserImpl parser = new ParserImpl();
 try {				
		Token n = parser.parse(s);				
	} catch (Exception e) {
		builder.append(e.getMessage()+":<br>");
		builder.append(s+"<br>");
		
		for (int i = 0 ; i < parser.GetCounter(); i++)
			builder.append(" <br>");
		builder.append("^<br>");
		//System.out.println(builder.toString());
	}		
	finally {
		parser.Reset();
	}
 %>
 <%=builder.toString()%>
</body>
</html>