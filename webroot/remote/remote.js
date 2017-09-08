

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
        	this.element.append( "<div class='dntk-number'>0</div><div class='dntk-title'></div>" );
        	this.element.find( ".dntk-title" ).text( this.data.title );
        	this._on(this.element,
   	        {
   	            mouseenter:"_hover",
   	            mouseleave:"_hover",
   	            click:"_click",
   	        });
        },

        _click: function(e) {
        	if( this.selected ) {
        		this.element.find( ".dntk-number" ).html( '0' );
        		this.selected = false;
        		this.num = 0;
        	}
        	else {
        		var max = 0;
        		$('.dntk-listentry').each( function( i, e ) {
        			var w = $(e);
        			var n = w.listentry( "getNum" );
        			if( n > max ) max = n;
        		});
        		this.selected = true;
        		this.num = max+1;
        		this.element.find( '.dntk-number' ).html( this.num );
        	}
        },
        
        _hover: function (e) {
            if(e.type == "mouseenter")
            {
            	this._addClass( 'mouseover' );    
            }

            if(e.type == "mouseleave")
            {
            	this._removeClass( 'mouseover' );    
            }

        },
        
        getNum: function() {
        	return this.num;
        },
        
        getData: function() {
        	return this.data;
        }
 
    });
 
}( jQuery ));

(function ( $ ) {
	 
    $.widget( "dntk.functionentry", {
		options: {
            color: "#556b2f",
            backgroundColor: "white"
        },
        data: null,
        
        _create: function () {
        	this._addClass("dntk-functionentry");
        	this.data = this.element.data();
        	//this.element.append( "<div class='dntk-number'>0</div>" );
        	//this.element.find( "img" ).css( "height", "100%" );
        	this._on(this.element,
   	        {
   	            mouseenter:"_hover",
   	            mouseleave:"_hover",
   	            click:"_click",
   	        });
        },

        _click: function(e) {
        	window[this.data.function]();
        },
        
        _hover: function (e) {
            if(e.type == "mouseenter")
            {
            	this._addClass( 'mouseover' );    
            }

            if(e.type == "mouseleave")
            {
            	this._removeClass( 'mouseover' );    
            }

        },    	
        
    });
 
}( jQuery ));

(function ( $ ) {
	 
    $.widget( "dntk.screenentry", {
		options: {
            color: "#556b2f",
            backgroundColor: "white"
        },
        num: 0,
        selected: false,
        data: null,
        
        _create: function () {
        	this._addClass("dntk-screenentry");
        	this.data = this.element.data();
        	this.element.append( "<div class='dntk-number'>0</div><div class='dntk-title'>"+this.data.name+"</div>" );
        	//this.element.find( ".dntk-title" ).text( this.data.title );
        	this._on(this.element,
   	        {
   	            mouseenter:"_hover",
   	            mouseleave:"_hover",
   	            click:"_click",
   	        });
        },

        _click: function(e) {
        	if( this.selected ) {
        		this.element.find( ".dntk-number" ).html( '0' );
        		this.selected = false;
        		this.num = 0;
        	}
        	else {
        		var max = 0;
        		$('.dntk-screenentry').each( function( i, e ) {
        			var w = $(e);
        			var n = w.screenentry( "getNum" );
        			if( n > max ) max = n;
        		});
        		this.selected = true;
        		this.num = max+1;
        		this.element.find( '.dntk-number' ).html( this.num );
        	}
        },
        
        _hover: function (e) {
            if(e.type == "mouseenter")
            {
            	this._addClass( 'mouseover' );    
            }

            if(e.type == "mouseleave")
            {
            	this._removeClass( 'mouseover' );    
            }

        },
        
        getNum: function() {
        	return this.num;
        },
        
        getData: function() {
        	return this.data;
        }
 
    });
 
}( jQuery ));

function linear() {
	var list = [];
	var screen = [];
	var _tmp = [];
	$('.dntk-listentry').each( function( i, e ) {
		var w = $(e);
		var n = w.listentry( "getNum" );
		if( n > 0 ) _tmp.push( { num: n, data: w.listentry( "getData" ) } );
	});	
    var keys = [];
    for (var key in _tmp)
    {
       keys.push(key);
    }
    keys.sort();
    for (var i = 0; i < keys.length; i++)
    {
        list.push(_tmp[ keys[i] ]);
    }

	_tmp = [];
	$('.dntk-screenentry').each( function( i, e ) {
		var w = $(e);
		var n = w.screenentry( "getNum" );
		if( n > 0 ) _tmp.push( { num: n, data: w.screenentry( "getData" ) } );
	});	
    keys = [];
    for (var key in _tmp)
    {
       keys.push(key);
    }
    keys.sort();
    for (var i = 0; i < keys.length; i++)
    {
        screen.push(_tmp[ keys[i] ]);
    }
}
