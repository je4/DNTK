

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

        select: function( sel ) {
	    	if( sel && !this.selected ) {
	    		var max = 0;
	    		$('.dntk-listentry').each( function( i, e ) {
	    			var w = $(e);
	    			var n = w.listentry( "getNum" );
	    			if( n > max ) max = n;
	    		});
	    		this.selected = true;
	    		this.num = max+1;
	    	}
	    	else if( !sel && this.selected ) {
	    		this.selected = false;
	    		this.num = 0;
	    	}
	    	this.element.find( '.dntk-number' ).html( this.num );
        },
        
        _click: function(e) {
	    	if( this.selected ) {
	    		this.select(false);
	    	}
	    	else {
	    		this.select(true);
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
        
        select: function( sel ) {
	    	if( sel && !this.selected ) {
	    		var max = 0;
	    		$('.dntk-screenentry').each( function( i, e ) {
	    			var w = $(e);
	    			var n = w.screenentry( "getNum" );
	    			if( n > max ) max = n;
	    		});
	    		this.selected = true;
	    		this.num = max+1;
	    	}
	    	else if( !sel && this.selected ) {
	    		this.selected = false;
	    		this.num = 0;
	    	}
	    	this.element.find( '.dntk-number' ).html( this.num );
        },
        
        _click: function(e) {
	    	if( this.selected ) {
	    		this.select(false);
	    	}
	    	else {
	    		this.select(true);
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

function getListEntries() {
	var list = [];
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
    
    return list;
}

function getScreenEntries() {
	var screen = [];
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
    return screen;
}

function clearSelections() {
	$('.dntk-screenentry').each( function( i, e ) {
		var w = $(e);
		w.screenentry( "select", false );
	});	
	$('.dntk-listentry').each( function( i, e ) {
		var w = $(e);
		w.listentry( "select", false );
	});	
}

function linear() {
	var list = getListEntries();
    var screen = getScreenEntries();
    
    for( var key in screen ) {
    	if(!( key in list )) break;
    	var b = screen[key];
    	var rest = b.data.resturl; // +"/browsers/"+b.data.name+"/get";
    	var api = new RestClient( rest );
    	api.res({ rest:
    			{ browsers: 
    				'get' 
    			}
    		});
    	api.rest.browsers(encodeURI(b.data.name)).get.post({url: list[key].data.url.replace( "%%BASEURL%%", b.data.baseurl ) });
    }
    clearSelections();
}


function one2many() {
	var list = getListEntries();
    var screen = getScreenEntries();

    if( !(0 in list )) return;
    
    for( var key in screen ) {
    	var b = screen[key];
    	var rest = b.data.resturl; 
    	var api = new RestClient( rest );
    	api.res({ rest:
    			{ browsers: 
    				'get' 
    			}
    		});
    	api.rest.browsers(encodeURI(b.data.name)).get.post({url: list[0].data.url.replace( "%%BASEURL%%", b.data.baseurl ) });
    }
    clearSelections();
}

function info() {
	var list = getListEntries();
    var screen = getScreenEntries();

    if( !(0 in screen )) return;
    
	var b = screen[0];
	var rest = b.data.resturl;
	var api = new RestClient( rest );
	api.res({ rest:
			{ browsers: 
				'screenshot' 
			}
		});
	$( "#screeninfo" ).html( '<img src="/content/img/rings.svg" />' );
	$( "#screeninfo" ).toggleClass( 'active ');
	api.rest.browsers(encodeURI(b.data.name)).screenshot.get().then( function ( e ) {
		$( "#screeninfo" ).html( '<img src="/content'+e.path+'" style="max-width: 800px; max-height: 600PX" />' );
	});
    clearSelections();
};