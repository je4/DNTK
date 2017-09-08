<html>
<head>
<title>Testing JSP</title>
</head>
<body>
 <%
	String remoteIP = request.getRemoteAddr();
	

	String name = org.objectspace.dntk.remote.BrowserPool.getName( remoteIP );
 
  %>
  <h3>xxx<%= remoteIP  %>: <%= name  %></h3>
  <a href="<%= request.getRequestURI() %>"><h3>Try Again</h3></a>
</body>
</html>