<%@ page import="org.objectspace.dntk.remote.*" %>
<%
	String socketgroup = request.getParameter( "group" ); 
%>
<html>
<!-- 
File by Jürgen Enge (info-age GmbH, Basel) available under CC-BY-SA-4.0
Dieses Werk ist lizenziert unter einer Creative Commons Namensnennung - Weitergabe unter gleichen Bedingungen 4.0 International Lizenz.
http://creativecommons.org/licenses/by-sa/4.0/
-->
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
