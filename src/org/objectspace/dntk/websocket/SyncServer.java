package org.objectspace.dntk.websocket;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint("/sync/{group}")
public class SyncServer {
	private static final Logger LOG = Logger.getLogger(SyncMessage.class.getName());
    private Session session;
    private int sessionHash;
    private String group;
    private RemoteEndpoint.Async remote;
	private static Map<String, Map<Integer, Session>> map = new HashMap<String, Map<Integer, Session>>();
    

	@OnClose
    public void onWebSocketClose(CloseReason close)
    {
		map.get(group).remove(sessionHash);
		this.sessionHash = 0;
		this.group = null;
        this.session = null;
        this.remote = null;
        LOG.log(Level.INFO, "WebSocket Close: {} - {} / "+close.getCloseCode(),close.getReasonPhrase());
    }

    @OnOpen
    public void onWebSocketOpen(@PathParam("group") String group, Session session)
    {
        this.session = session;
        this.group = group;	
        this.remote = this.session.getAsyncRemote();
        this.sessionHash = System.identityHashCode(session);
        if( !map.containsKey(group)) map.put(this.group, new HashMap<Integer, Session>() );
        map.get(this.group).put(this.sessionHash, this.session);
        
        LOG.log(Level.INFO, "WebSocket Connect: {} / "+group+" / "+session);
        this.remote.sendText("You are now connected to " + this.getClass().getName());
    }

    @OnError
    public void onWebSocketError(Throwable cause)
    {
    	LOG.log(Level.WARNING, "WebSocket Error "+cause);
    }

    @OnMessage
    public void onWebSocketText(String message)
    {
    	// send text to all except sender
    	for( Entry<Integer, Session> e : map.get(group).entrySet()) {
    		if( e.getKey() == sessionHash ) continue;
    		e.getValue().getAsyncRemote().sendText(message);
    	}
    	
//    	LOG.log(Level.INFO, "Echoing back text message [{}] / "+message);
        // Using shortcut approach to sending messages.
        // You could use a void method and use remote.sendText()
    }
}
