package org.objectspace.dntk.rest.resource;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.objectspace.dntk.Main;
import org.objectspace.dntk.remote.Browser;
import org.objectspace.dntk.remote.BrowserException;
import org.objectspace.dntk.remote.BrowserPool;
import org.objectspace.dntk.rest.model.BrowserAddReply;
import org.objectspace.dntk.rest.model.BrowserReply;
import org.objectspace.dntk.rest.model.BrowsersReply;

@Path("/browsers")
public class BrowsersResource {
	@GET
	@Path("{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public BrowserReply getBrowser(@PathParam("name") String name) {
		BrowserPool pool = BrowserPool.getInstance();
		Browser b = pool.getBrowser(name);
		try {
			return new BrowserReply( b.getInfo());
		} catch (BrowserException e) {
			Logger.getLogger(BrowserPool.class.getName()).log(Level.WARNING, null, e);
			return null;
		}		
		
    }

	@POST
	@Path("add")
	@Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public BrowserAddReply addBrowser(@Context HttpServletRequest request, Map<String, Object> data) {
		int port = (int) data.get("port");
		String secret = (String) data.get( "secret" );
		String[] bs = Main.cfg.getStringArray("browsers.browser.name");
		String url = "http://"+request.getRemoteAddr()+":"+port;
		
		for( int i = 0; i < bs.length; i++ ) {
			if( Main.cfg.getString( "browsers.browser("+i+").url", "7e15618d1bf16df8bf0ecf2914ed1964a387ba0bx") != secret ) continue;
			Browser b;
			try {
				b = new Browser( url, Main.cfg, i );
				BrowserPool.getInstance().addBrowser(b);
				b.getDate();
				b.getInfo();
				return new BrowserAddReply( b.getName());
			} catch ( Exception e) {
				Logger.getLogger(BrowserPool.class.getName()).log(Level.WARNING, null, e);
				break;
			}
		}
		return new BrowserAddReply();
    }

	@POST
	@Path("{name}/get")
	@Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String postBrowserGet(@PathParam("name") String name, Map<String, Object> data) {
		BrowserPool pool = BrowserPool.getInstance();
		Browser b = pool.getBrowser(name);
		String url = (String) data.get("url");
		try {
			b.get(url);
			return "ok";
		} catch (BrowserException e) {
			Logger.getLogger(BrowserPool.class.getName()).log(Level.WARNING, null, e);
			return null;
		}		
		
    }

	@GET
	//@Path("")
    @Produces(MediaType.APPLICATION_JSON)
    public BrowsersReply getBrowsers() {
		BrowserPool pool = BrowserPool.getInstance();
		return new BrowsersReply( pool.getBrowserNames());
    }
}
