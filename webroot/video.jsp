<html>
<!-- 
File by Jürgen Enge (info-age GmbH, Basel) available under CC-BY-SA-4.0
Dieses Werk ist lizenziert unter einer Creative Commons Namensnennung - Weitergabe unter gleichen Bedingungen 4.0 International Lizenz.
http://creativecommons.org/licenses/by-sa/4.0/
-->
<%@ page import="org.objectspace.dntk.remote.*" %>
<%
    String videourl = request.getParameter( "video" );
	String socketgroup = request.getParameter( "group" );
 	String h = request.getParameter("loop" );
	String loop = (h == null ? "" : "loop"); 
	h = request.getParameter("muted" );
	String mute = (h == null ? "" : "muted"); 
	h = request.getParameter("autoplay" );
	String autoplay = (h == null ? "" : "autoplay"); 
	h = request.getParameter("bgcolor" );
	String bgcolor = (h == null ? "black" : h); 
	h = request.getParameter("status" );
	String statusbrowser = (h == null ? null : h); 
	String status = null;
	if( statusbrowser != null ) {
		Browser b = BrowserPool.findName( statusbrowser );
		if( b != null ) status = b.getRemoteStatus(); 
	}
 %>
<head>
	<title>DNTK - Video</title>
	<style>
	body {
		padding: 0px;
		margin: 0px;
		text-align: center;
		background-color: <%= bgcolor %>
	}
	video#vid 
	{
	    width: auto;
	    height: 100%;
	    max-width: 100%;
	}
	</style>
</head>
<body>
<video playsinline <%= autoplay %> <%= mute %> <%= loop %> id="vid">
    <source src="<%= videourl %>">
</video>
	<script>
	var metadataloaded = false;
	var connection = null;
	var video = null;
	var inSync = false;
//	var status = "{\"href\":\"http://localhost:8080/content/video.jsp?muted&autoplay&video=https%3A%2F%2Fba14ns21403.fhnw.ch%2Fvideo%2Fopen%2Fasdf93484.mp4\",\"time\":154.640167}";
	var status = "<%= status == null ? "null" : status.replaceAll("\"","\\\"") %>";
	function getStatus( ) {
		var d = new Date();		
		var time = video.currentTime;
		var status = {
				"href": window.location.href,
				"time": time,
				"systemtime": d.getTime(),
		};
		return JSON.stringify(status);			
	}

	function setStatus( json ) {
		if( !metadataloaded ) {
			setTimeout( function () {
				setStatus( json );
			}, 500 );
			return;
		}
		var status = JSON.parse( json );
		var d = new Date();
		var t = d.getTime();
		var diff = t - status['systemtime'];
		video.currentTime = status['time'] + diff/1000;
	}
	
	function sendSocketStatus() {
		connection.send( JSON.stringify( { 
			type: 'timestamp',
			now: (new Date()).getTime(), 
			pos: video.currentTime*1000 
			} ));
		setTimeout( sendSocketStatus, 1000 );
	}	

	function syncVideo( data ) {
		if( data.type != 'timestamp' || inSync ) return;
		var now = (new Date()).getTime();
		var tdiff = now - data.now;
		var vdiff = (video.currentTime*1000 - data.pos) - tdiff;
		// liegt das aktuelle video mehr als 1sec zurück?
		if( vdiff <  -1000 ) {
			video.currentTime -= vdiff/1000;
		}
		// liegt das aktuelle video zwischen 1/10 und 1sec voraus?
		else if( vdiff > 100 && vdiff <= 1000 ) {
			inSync = true;
			video.pause();
			setTimeout( function () {
				video.play();
				inSync = false;
			}, vdiff );
		}
	}
	
	function connectWebsocket() {
		
		connection = new WebSocket('ws://<%= request.getLocalAddr() %>:<%= request.getLocalPort() %>/socket/sync/<%= socketgroup %>');
		connection.onopen= function () {
			connection.send( JSON.stringify( {status:"open"}));
		}
		connection.onmessage = function (e) {
			  console.log('Server: ' + e.data);
			  syncVideo( JSON.parse( e.data ));
		};
		
		connection.onclose = function(){
	        // Try to reconnect in 5 seconds
	        setTimeout(function(){connectWebsocket()}, 5000);
	    };
	}
	
	video = document.getElementById( "vid" );
	video.addEventListener('loadedmetadata', function() {
		metadataloaded = true;
		connectWebsocket();
		setTimeout( sendSocketStatus, 1000 );
	}, false);
	</script>
</body>
</html>
