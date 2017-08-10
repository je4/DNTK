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

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.configuration2.AbstractConfiguration;
import org.apache.commons.configuration2.XMLConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.tomcat.util.scan.StandardJarScanner;
import org.eclipse.jetty.apache.jsp.JettyJasperInitializer;
import org.eclipse.jetty.jsp.JettyJspServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.component.AbstractLifeCycle;
import org.eclipse.jetty.webapp.Configuration;
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
     * JspStarter for embedded ServletContextHandlers
     * 
     * This is added as a bean that is a jetty LifeCycle on the ServletContextHandler.
     * This bean's doStart method will be called as the ServletContextHandler starts,
     * and will call the ServletContainerInitializer for the jsp engine.
     *
     */
    public static class JspStarter extends AbstractLifeCycle implements ServletContextHandler.ServletContainerInitializerCaller
    {
        JettyJasperInitializer sci;
        ServletContextHandler context;
        
        public JspStarter (ServletContextHandler context)
        {
            this.sci = new JettyJasperInitializer();
            this.context = context;
            this.context.setAttribute("org.apache.tomcat.JarScanner", new StandardJarScanner());
        }

        @Override
        protected void doStart() throws Exception
        {
            ClassLoader old = Thread.currentThread().getContextClassLoader();
            Thread.currentThread().setContextClassLoader(context.getClassLoader());
            try
            {
                sci.onStartup(null, context.getServletContext());   
                super.doStart();
            }
            finally
            {
                Thread.currentThread().setContextClassLoader(old);
            }
        }
    }
	
	/**
	 * 
	 * @param cfg
	 */
	public Main( AbstractConfiguration cfg ) {
		Main.cfg = cfg;
	}
	
	public void start() throws Exception {
		initJetty();
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
	 * @throws Exception 
	 * 
	 */
	private void initJetty() throws Exception {
		int port = cfg.getInt("jetty.port");
		String path = cfg.getString( "jetty.staticcontent.path", "/" );
		String resourcebase = cfg.getString( "jetty.resourcebase" );
		
		File tempDir = new File( cfg.getString("jetty.tempdir") );
		if (!tempDir.exists())
        {
            if (!tempDir.mkdirs())
            {
                throw new IOException("Unable to create temp directory: " + tempDir);
            }
        }
		
		jetty = new Server();
        
        // Define ServerConnector
        ServerConnector connector = new ServerConnector(jetty);
        connector.setPort(port);
        jetty.addConnector(connector);
        
		// Add annotation scanning (for WebAppContexts)
        Configuration.ClassList classlist = Configuration.ClassList
                .setServerDefault( jetty );
        classlist.addBefore(
                "org.eclipse.jetty.webapp.JettyWebXmlConfiguration",
                "org.eclipse.jetty.annotations.AnnotationConfiguration" );
		
        // Create Servlet context
		context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        context.setResourceBase(resourcebase);
        
        context.setAttribute("javax.servlet.context.tempdir",tempDir);
            
		createJSPContext();
		createJerseyContext();
		createStaticContentContext();
        
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

		String path = cfg.getString( "jetty.staticcontent.path", "/" );
		String dir = cfg.getString( "jetty.staticcontent.dir" );
        
        ServletHolder staticContent = new ServletHolder( "default", DefaultServlet.class);
        staticContent.setInitParameter("resourceBase",dir);
        staticContent.setInitParameter("dirAllowed","true");
        //staticContent.setInitParameter("pathInfoOnly","true");
        context.addServlet(staticContent, path);
	}
	
	private void createJSPContext() throws Exception {
		if( jetty == null ) throw new Exception( "jetty not initialized!" );

		// Set Classloader of Context to be sane (needed for JSTL)
        // JSP requires a non-System classloader, this simply wraps the
        // embedded System classloader in a way that makes it suitable
        // for JSP to use
        ClassLoader jspClassLoader = new URLClassLoader(new URL[0], this.getClass().getClassLoader());
        context.setClassLoader(jspClassLoader);
		
        // Manually call JettyJasperInitializer on context startup
        context.addBean(new JspStarter(context));
        
     // Create / Register JSP Servlet (must be named "jsp" per spec)
        ServletHolder holderJsp = new ServletHolder("jsp", JettyJspServlet.class);
        holderJsp.setInitOrder(0);
        holderJsp.setInitParameter("logVerbosityLevel", "DEBUG");
        holderJsp.setInitParameter("fork", "false");
        holderJsp.setInitParameter("xpoweredBy", "false");
        holderJsp.setInitParameter("compilerTargetVM", "1.8");
        holderJsp.setInitParameter("compilerSourceVM", "1.8");
        holderJsp.setInitParameter("keepgenerated", "true");
            
        context.addServlet(holderJsp, "*.jsp");
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

