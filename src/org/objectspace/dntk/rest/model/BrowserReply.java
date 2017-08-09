package org.objectspace.dntk.rest.model;

import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class BrowserReply {
	public Map<String, String> info;
	
	public BrowserReply() {
		
	}
	
	public BrowserReply(Map<String, String> info) {
		this.info = info;
	}
}

