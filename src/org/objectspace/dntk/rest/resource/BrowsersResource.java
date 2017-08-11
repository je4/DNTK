/*******************************************************************************
 * (c) Copyright 2017 info-age GmbH, Basel
 *
 * This file is part of DNTK - The Digital Narration ToolKit.
 *
 *     DNTK - Digital Narration ToolKit is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     DNTK - Digital Narration ToolKit is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with DNTK - Digital Narration ToolKit.  If not, see <http://www.gnu.org/licenses/>.
 *
 *
 * Contributors:
 *     info-age GmbH, Basel - initial implementation
 *******************************************************************************/
package org.objectspace.dntk.rest.resource;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Path("/browsers")
public class BrowsersResource {
	@GET
	@Path("{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public BrowserReply getBrowser(@PathParam("name") String name) {
		BrowserPool pool = BrowserPool.getInstance();
		Browser b = pool.getBrowser(name);
		try {
			return new BrowserReply( b.getRemoteInfo());
		} catch (BrowserException e) {
			Logger.getLogger(BrowserPool.class.getName()).log(Level.WARNING, null, e);
			return null;
		}		
		
    }

	/**
	 * Add a new Browser to Pool
	 * 
	 * @param request
	 * @param data
	 * @return
	 */
	@PUT
	@Path("add")
	@Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public BrowserAddReply addBrowser(@Context HttpServletRequest request, Map<String, Object> data) {
		if( !data.containsKey("port")) return new BrowserAddReply( 2, "no port defined", null );
		if( !data.containsKey("secret")) return new BrowserAddReply( 2, "no secret defined", null );

		try {
			int port = (int)data.get("port");
			String secret = (String) data.get( "secret" );
			String[] bs = Main.cfg.getStringArray("browsers.browser.name");
			
			for( int i = 0; i < bs.length; i++ ) {
				String cfgSecret = Main.cfg.getString( "browsers.browser("+i+").secret", "7e15618d1bf16df8bf0ecf2914ed1964a387ba0bx");
				if(  !cfgSecret.equals( secret )) continue;
				Browser b;
				b = new Browser( request.getRemoteAddr(), port, Main.cfg, i );
				BrowserPool.getInstance().addBrowser(b);
				b.getRemoteDate();
				b.getRemoteInfo();
				return new BrowserAddReply( b.getName());
			}
			return new BrowserAddReply( 1, "browser not found", null );
		}
		catch( Exception e ) {
			Logger.getLogger(BrowserPool.class.getName()).log(Level.WARNING, null, e);
			return new BrowserAddReply( 3, "error: "+e.getMessage(), null );
		}
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

	@POST
	@Path("{name}/clone/{target}")
    @Produces(MediaType.APPLICATION_JSON)
    public String postBrowserClone(@PathParam("name") String name, @PathParam("target") String target) {
		Browser b = BrowserPool.findName(name);
		Browser t = BrowserPool.findName(target);
		if( b == null || t == null ) {
			return null;
		}
		try {
			String json = b.getRemoteStatus();
			if( json == null ) return null;
			ObjectMapper mapper = new ObjectMapper(); 
		    HashMap<String, Object> o = mapper.readValue(json, new TypeReference<HashMap<String,Object>>() {});
		    String url = (String) o.get( "href" );
		    t.get(url);
		    t.setRemoteStatus(json);
			return "ok";
		} catch (IOException | BrowserException e) {
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
