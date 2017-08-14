package org.objectspace.dntk.websocket;

import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import java.util.logging.Logger;


public class SyncMessage {
		private String username;
		private String message;




		public SyncMessage() {
		}




		public SyncMessage( final String username, final String message ) {
		this.username = username;
		this.message = message;
		}




		public String getMessage() {
		return message;
		}




		public String getUsername() {
		return username;
		}




		public void setMessage( final String message ) {
		this.message = message;
		}




		public void setUsername( final String username ) {
		this.username = username;
		}
		}
