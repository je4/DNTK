<%@ page import="org.objectspace.dntk.remote.*" %><!DOCTYPE html>
<html lang="de-DE">
<!-- 
File by Jürgen Enge (info-age GmbH, Basel) available under CC-BY-SA-4.0
Dieses Werk ist lizenziert unter einer Creative Commons Namensnennung - Weitergabe unter gleichen Bedingungen 4.0 International Lizenz.
http://creativecommons.org/licenses/by-sa/4.0/
-->
<head>
	<meta charset="utf-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<title>DNTK - Remote Control</title>
	<link rel="stylesheet" href="https://unpkg.com/onsenui/css/onsenui.css">
	<link rel="stylesheet" href="https://unpkg.com/onsenui/css/onsen-css-components.min.css">	
	<link rel="stylesheet" href="standart.css" />
	<link rel="stylesheet" href="remote.css" />
	<link href="https://fonts.googleapis.com/css?family=IM+Fell+English" rel="stylesheet"> 
</head>
<body>
<div class="wrapper">
  <div class="area header">DNTK - The Digital Narration ToolKit</div>
  <div class="area slides">
  	<div class="listentry" data-title="Slide 1" data-url="%%BASEURL%%/content/video.jsp?muted&loop&autoplay&group=g1&video=https%3A%2F%2Fba14ns21403.fhnw.ch%2Fvideo%2Fopen%2Fasdf93484.mp4" data-status="{&quot;title&quot;: &quot;Testing&quot;, &quot;url&quot;: &quot;http://sdfsdf&quot; }"></div>
  	<div class="listentry" data-title="Slide 2" data-url="http://www.heise.de" data-status="{&quot;title&quot;: &quot;Testing&quot;, &quot;url&quot;: &quot;http://sdfsdf&quot; }"></div>
  	<div class="listentry" data-title="Slide 3" data-url="http://www.heise.de" data-status="{&quot;title&quot;: &quot;Testing&quot;, &quot;url&quot;: &quot;http://sdfsdf&quot; }"></div>
  	<div class="listentry" data-title="Slide 4" data-url="http://www.heise.de" data-status="{&quot;title&quot;: &quot;Testing&quot;, &quot;url&quot;: &quot;http://sdfsdf&quot; }"></div>
  	<div class="listentry" data-title="Slide 5" data-url="http://www.heise.de" data-status="{&quot;title&quot;: &quot;Testing&quot;, &quot;url&quot;: &quot;http://sdfsdf&quot; }"></div>
  </div>
  <div class="area screens">
<%
	for( Browser b: BrowserPool.getInstance() ) {
		if( b.getStatus() != org.objectspace.dntk.remote.Browser.CONNECTED) continue;
%>
		<div class="screenentry" data-name="<%= b.getName() %>" data-baseurl="<%= b.getBaseURL() %>" data-resturl="http://<%= request.getLocalAddr() %>:<%= request.getLocalPort() %>"></div>
<%		
	}
  %>
  </div>
  <div class="area commands">
  <div class="functionentry" data-title="chain" data-function="chain">
 	  		<img src="/content/img/chain.png" />
  </div>
  <div class="functionentry" data-title="linear" data-function="linear">
	<img src="/content/img/linear.png" />
  </div>
  <div class="functionentry" data-title="one2many" data-function="one2many">
	<img src="/content/img/onetomany.png" />
  </div>
  <div class="functionentry" data-title="swap" data-function="swap">
	<img src="/content/img/swap.png" />
  </div>
  <div class="functionentry" data-title="info" data-function="info">
	<img src="/content/img/info.png" />
  </div>
</div>
</div>
<div style="position: absolute; right: 0px; bottom: 0px;">(c) Copyright 2017 info-age GmbH, Basel</div>

<div class="infobox" id="screeninfo" title="Info">
  <p>This is the default dialog which is useful for displaying information. The dialog window can be moved, resized and closed with the 'x' icon.</p>
</div>

<script src="https://unpkg.com/onsenui/js/onsenui.min.js"></script>
<script src="../js/jquery-3.2.1.min.js"></script>
<script src="../js/jquery-ui.min.js"></script>
<!--  <script src="../js/jquery.ui.touch-punch.min.js"></script>  -->
<script src="../js/rest-client.js"></script>
<script src="remote.js"></script>
<script>



$( window ).on( "load", function() { 
	$( '.listentry' ).listentry();
	$( '.listentry' ).draggable();
	$( '.functionentry' ).functionentry();
	$( '.screenentry' ).screenentry();
	$( '.screenentry' ).draggable();
	$( '.infobox' ).click( function() {
		$(this).toggleClass( "active" );
	})
});

</script>
</body>
</html>