<!DOCTYPE html>
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
	<link rel="stylesheet" href="standart.css" />
	<link rel="stylesheet" href="remote.css" />
	<link href="https://fonts.googleapis.com/css?family=IM+Fell+English" rel="stylesheet"> 
</head>
<body>
<div class="wrapper">
  <div class="area header">DNTK - The Digital Narration ToolKit</div>
  <div class="area slides">
  	<div class="listentry" data-title="Slide 1" data-url="http://www.heise.de" data-status="{&quot;title&quot;: &quot;Testing&quot;, &quot;url&quot;: &quot;http://sdfsdf&quot; }"></div>
  	<div class="listentry" data-title="Slide 2" data-url="http://www.heise.de" data-status="{&quot;title&quot;: &quot;Testing&quot;, &quot;url&quot;: &quot;http://sdfsdf&quot; }"></div>
  	<div class="listentry" data-title="Slide 3" data-url="http://www.heise.de" data-status="{&quot;title&quot;: &quot;Testing&quot;, &quot;url&quot;: &quot;http://sdfsdf&quot; }"></div>
  	<div class="listentry" data-title="Slide 4" data-url="http://www.heise.de" data-status="{&quot;title&quot;: &quot;Testing&quot;, &quot;url&quot;: &quot;http://sdfsdf&quot; }"></div>
  	<div class="listentry" data-title="Slide 5" data-url="http://www.heise.de" data-status="{&quot;title&quot;: &quot;Testing&quot;, &quot;url&quot;: &quot;http://sdfsdf&quot; }"></div>
  </div>
  <div class="area screens">
<%
	for( org.objectspace.dntk.remote.Browser b: org.objectspace.dntk.remote.BrowserPool.getInstance() ) {
		if( b.getStatus() != org.objectspace.dntk.remote.Browser.CONNECTED) continue;
%>
		<div class="screenentry" data-name="<%= b.getName() %>"></div>
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
</div>
</div>


<script src="../js/jquery-3.2.1.min.js"></script>
<script src="../js/jquery-ui.min.js"></script>
<script src="remote.js"></script>
<script>

$( window ).on( "load", function() { 
	$( '.listentry' ).listentry();
	$( '.functionentry' ).functionentry();
	$( '.screenentry' ).screenentry();
});

</script>
</body>
</html>