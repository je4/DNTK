<%@ page import="org.objectspace.dntk.remote.*" %>
<%
	String socketgroup = request.getParameter( "group" ); 
%>
<html>
<head>
<title>Testing JSP</title>
</head>
<body>

<script src="js/jquery-3.2.1.min.js"></script>
<script>

function sendSocketStatus() {
	connection.send( JSON.stringify( { 
		now: (new Date()).getTime(), 
		} ));	
	setTimeout( sendSocketStatus, 1000 );
}

$( document ).ready(function() {
	connection = new WebSocket('ws://<%= request.getLocalAddr() %>:<%= request.getLocalPort() %>/socket/sync/<%= socketgroup %>');
	connection.onopen= function () {
		connection.send( JSON.stringify( {status:"open"}));
	}
	connection.onmessage = function (e) {
		  console.log('Server: ' + e.data);
	};
	setTimeout( sendSocketStatus, 1000 );
});
</script>
</body>
</html>
