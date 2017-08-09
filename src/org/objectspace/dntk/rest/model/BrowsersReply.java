package org.objectspace.dntk.rest.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class BrowsersReply {
	public String[] browsers;
	
	public BrowsersReply() {
		
	}
	
	public BrowsersReply(String[] browsers) {
		this.browsers = browsers;
	}
}

