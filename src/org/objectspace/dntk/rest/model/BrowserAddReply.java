package org.objectspace.dntk.rest.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class BrowserAddReply {
	public String name = null;
	public int errorcode = 0;
	public String errormsg = "no error";
	
	public BrowserAddReply() {
		
	}

	public BrowserAddReply( String name ) {
		this.name = name;
	}

	public BrowserAddReply( int errorcode, String errormsg, String name ) {
		this.name = name;
		this.errorcode = errorcode;
		this.errormsg = errormsg;
	}
}
