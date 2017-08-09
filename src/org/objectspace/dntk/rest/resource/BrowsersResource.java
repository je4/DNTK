package org.objectspace.dntk.rest.resource;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.objectspace.dntk.remote.Browser;
import org.objectspace.dntk.remote.BrowserException;
import org.objectspace.dntk.remote.BrowserPool;
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
	@Path("{name}/get")
	@Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String postBrowserGet(@PathParam("name") String name, Map data) {
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
