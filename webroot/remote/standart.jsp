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
	<link rel="stylesheet" href="../css/dntk.css" />
	<link href="https://fonts.googleapis.com/css?family=IM+Fell+English" rel="stylesheet"> 
</head>
<body>
<div class="wrapper">
  <div class="area header">DNTK - The Digital Narration ToolKit</div>
  <div class="area slides">
  	<div class="listentry" data-title="Slide 1" data-url="http://www.heise.de" data-status="{&quot;title&quot;: &quot;Testing&quot;, &quot;url&quot;: &quot;http://sdfsdf&quot; }"></div>
  	<div class="listentry" data-title="Slide 2" data-url="http://www.heise.de" data-status="{&quot;title&quot;: &quot;Testing&quot;, &quot;url&quot;: &quot;http://sdfsdf&quot; }"></div>
  	<div class="listentry" data-title="Slide 3" data-url="http://www.heise.de" data-status="{&quot;title&quot;: &quot;Testing&quot;, &quot;url&quot;: &quot;http://sdfsdf&quot; }"></div>
  </div>
  <div class="area screens">screens</div>
  <div class="area commands">commands</div>
</div>


<script src="../js/jquery-3.2.1.min.js"></script>
<script src="../js/jquery-ui.min.js"></script>
<script>
(function ( $ ) {
	 
    $.widget( "dntk.listentry", {
		options: {
            color: "#556b2f",
            backgroundColor: "white"
        },
        num: 0,
        selected: false,
        data: null,
        
        _create: function () {
        	this._addClass("dntk-listentry");
        	this.data = this.element.data();
        	this.element.append( "<div class='number'></div><div class='title'></div>" );
        	this.element.find( ".title" ).text( this.data.title );
        },
 
    
 
    });
 
}( jQuery ));

$( window ).on( "load", function() { 
	$( '.listentry' ).listentry();
});

</script>
</body>
</html>