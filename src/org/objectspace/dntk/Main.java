/*******************************************************************************
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
/*******************************************************************************
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
/*******************************************************************************
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
/*******************************************************************************
 * This file is part of DNTK - Digital Narration ToolKit.
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

/**
 * 
 */
package org.objectspace.dntk;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.configuration2.AbstractConfiguration;
import org.apache.commons.configuration2.XMLConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.objectspace.dntk.remote.BrowserPool;
import org.objectspace.dntk.remote.BrowserPoolThread;
import org.objectspace.dntk.rest.resource.HelloResource;

/**
 * @author juergen.enge
 *
 * Main class for initializing and starting all services
 */
public class Main {

	private Server jetty = null;
	private ServletContextHandler context = null;
	
	/**
	 * the configuration file
	 */
	public static AbstractConfiguration cfg = null;
	
	/**
	 * 
	 * @param cfg
	 */
	public Main( AbstractConfiguration cfg ) {
		Main.cfg = cfg;
	}
	
	public void start() throws Exception {
		initJetty();
		createJerseyContext();
		createStaticContentContext();
		JettyThread jettythread = new JettyThread( jetty );
		Thread runner = new Thread(jettythread);
		runner.start();

		BrowserPoolThread browserpoolthread = new BrowserPoolThread( cfg, BrowserPool.getInstance());

		try {
			BrowserPool.init( cfg );
			Thread runner2 = new Thread(browserpoolthread);
			runner2.start();

			System.out.println( "Press enter to quit." );
			// wait for return key
			System.in.read();
		}
		catch( Exception e ) {
			e.printStackTrace();
		}
		finally {
			browserpoolthread.stop();
			// close webdrivers
			BrowserPool.getInstance().close();
			// stop jetty
			jetty.stop();
		}
		System.out.println( "end" );
		
	}
	
	
	/**
	 * 
	 */
	private void initJetty() {
		int port = cfg.getInt("jetty.port");
		jetty = new Server(port);	
		context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        jetty.setHandler(context);
	}

	/**
	 * 
	 */
	private void createJerseyContext() throws Exception {
		if( jetty == null ) throw new Exception( "jetty not initialized!" );

		String jerseyPath = cfg.getString( "jetty.jersey.path", "/rest/*" );
		
		ResourceConfig resourceConfig = new ResourceConfig();		
		resourceConfig.packages(HelloResource.class.getPackage().getName());
		resourceConfig.register(JacksonFeature.class);
		ServletContainer servletContainer = new ServletContainer(resourceConfig);
		ServletHolder sh = new ServletHolder(servletContainer);                
        
        context.addServlet(sh, jerseyPath);
	}
	
	private void createStaticContentContext() throws Exception {
		if( jetty == null ) throw new Exception( "jetty not initialized!" );

		String path = cfg.getString( "jetty.staticcontent.path", "/static/*" );
		String dir = cfg.getString( "jetty.staticcontent.dir" );
        
        ServletHolder staticContent = new ServletHolder( new DefaultServlet());
        staticContent.setInitParameter("resourceBase",dir);
        staticContent.setInitParameter("dirAllowed","true");
        staticContent.setInitParameter("pathInfoOnly","true");
        context.addServlet(staticContent, path);
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String configfilename = "dntk.xml";
		if (args.length > 0) {
			configfilename = args[0];
		}

		Parameters params = new Parameters();
		FileBasedConfigurationBuilder<XMLConfiguration> builder = new FileBasedConfigurationBuilder<XMLConfiguration>(
				XMLConfiguration.class).configure(params.xml().setFileName(configfilename));

		try {
			XMLConfiguration config = builder.getConfiguration();
			Main main = new Main( config );
			main.start();
		} catch (Exception e) {
			Logger.getLogger(BrowserPool.class.getName()).log(Level.WARNING, null, e);
		}

    }
} 

