<html>
<%@ page import="org.objectspace.dntk.remote.*" %>
<%
    String videourl = request.getParameter( "video" );
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
//	var status = "{\"href\":\"http://localhost:8080/content/video.jsp?muted&autoplay&video=https%3A%2F%2Fba14ns21403.fhnw.ch%2Fvideo%2Fopen%2Fasdf93484.mp4\",\"time\":154.640167}";
	var status = "<%= status == null ? "null" : status.replaceAll("\"","\\\"") %>";
	function getStatus( ) {
		var d = new Date();		
		var time = document.getElementById( "vid" ).currentTime;
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
		document.getElementById( "vid" ).currentTime = status['time'] + diff/1000;
	}
	
	document.getElementById('vid').addEventListener('loadedmetadata', function() {
		metadataloaded = true;
	}, false);
	</script>
</body>
</html>
