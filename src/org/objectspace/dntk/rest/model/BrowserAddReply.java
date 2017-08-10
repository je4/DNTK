package org.objectspace.dntk.rest.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class BrowserAddReply {
	public String name = null;
	
	public BrowserAddReply() {
		
	}

	public BrowserAddReply( String name ) {
		this.name = name;
	}
}
