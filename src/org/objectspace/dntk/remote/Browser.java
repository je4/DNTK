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
 *     If you need a commercial license please contact info-age GmbH, Basel.
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
package org.objectspace.dntk.remote;

import java.io.File;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.configuration2.AbstractConfiguration;
import org.apache.commons.io.FileUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;


/**
 * @author juergen.enge
 * 
 * Creates a Selenium Webdriver and implements the necessary functionality
 */
public class Browser {
	
	public final static int NONE = 0;
	public final static int CONNECTED = 1;
	public final static int DISCONNECTED = 2;
	public final static int ERROR = 4;
	
	/**
	 * status of the actual browser connection
	 */
	private int status = NONE;
	
	/**
	 * The WebDriver
	 */
	private WebDriver driver;

	
	/**
	 * url of remote browser
	 */
	private String url = null;
	private String host;
	private String baseurl;
	private int port;
	private String webroot = null;
	private String screenshotlocation = null;
	
	/**
	 * capabilities for restarting the driver
	 */
	private DesiredCapabilities capabilities = null;
	
	/**
	 * Browser type
	 */
	private String browserType = null;

	/**
	 * name of the instance
	 */
	private String name;
	
	private LocalDateTime lastaccess = LocalDateTime.now();
	
	private ObjectMapper mapper = new ObjectMapper();
	
	final ReentrantLock lock = new ReentrantLock();
	
	/**
	 * Constructor
	 * 
	 * Creates and initializes the WebDriver
	 * 
	 * @param cfg Configuration file
	 * @param id Current Browser ID
	 * @throws Exception 
	 */
	public Browser( AbstractConfiguration cfg, int id ) throws Exception {
		this( cfg.getString( "browsers.browser("+id+").host" ), cfg.getInt( "browsers.browser("+id+").port" ),  cfg, id );
	}

	/**
	 * Constructor
	 * 
	 * Creates and initializes the WebDriver
	 * 
	 * @param url remote browser url
	 * @param cfg Configuration file
	 * @param id Current Browser ID
	 * @throws Exception 
	 */
	public Browser( String host, int port, AbstractConfiguration cfg, int id ) throws Exception {
		
		this.host = host;
		this.port = port;
		this.url = "http://"+this.host+":"+this.port;
		this.webroot = cfg.getString( "jetty.webroot" );
		this.screenshotlocation = cfg.getString( "jetty.screenshotlocation" );
		this.baseurl = cfg.getString( "browsers.browser("+id+").baseurl" );
		
		// creating the correct WebDriver Object
		String browserCfg = "browsers.browser("+id+")";
		name = cfg.getString( browserCfg+".name" );
		browserType = cfg.getString( browserCfg+"[@type]" );

		int multiple = cfg.getInt( browserCfg+"[@multiple]" );
		if( multiple == 1 ) {
			name += " - "+ this.host;
		}

		switch( browserType ) {
		case "chrome":
			capabilities = initChrome( cfg, id );
			break;
		case "firefox":
			capabilities = initFirefox( cfg, id );
			break;
		case "safari":
			capabilities = initSafari( cfg, id );
			break;
		default:
			throw new BrowserException( "Invalid Type "+ browserType + " for browser " + name + " (#" + id + ")" );
		}
		
		connect();
	}

	public int getStatus() {
		return status;
	}

	public String getURL() {
		return url;
	}

	public String getBaseURL() {
		return baseurl;
	}

	public LocalDateTime getLastAccess() {
		return lastaccess;
	}
	
	/**
	 * get some info about the running webbrowser
	 * 
	 * @return
	 * @throws BrowserException
	 */
	public Map<String, String> getRemoteInfo()  throws BrowserException {
		if( driver == null ) throw new BrowserException( "driver not initialized" );

		try {
			if (lock.tryLock(2, TimeUnit.SECONDS)) {
			
				JavascriptExecutor js = (JavascriptExecutor)driver;
				String javascript = "return JSON.stringify({ time:(new Date()).toISOString(), appCodeName:navigator.appCodeName, appName:navigator.appName, appVersion:navigator.appVersion });";
				
				String json = (String) js.executeScript(javascript);
				
				Map<String, String> map = new HashMap<String, String>();
				map = mapper.readValue(json, new TypeReference<Map<String, String>>(){});
				lastaccess = LocalDateTime.now();
				return map;
			}
		}
		catch( Exception e ) {
			status = ERROR;
			Logger.getLogger(BrowserPool.class.getName()).log(Level.WARNING, null, e);
		}
		finally {
			if (lock.isHeldByCurrentThread()) lock.unlock();
		}
		return null;
	}

	
	public DateTime getRemoteDate() throws BrowserException {
		if( driver == null ) throw new BrowserException( "driver not initialized" );
		
		try {
			if (lock.tryLock(2, TimeUnit.SECONDS)) {
				JavascriptExecutor js = (JavascriptExecutor)driver;
				String utc = (String) js.executeScript("return new Date().toJSON();");
				DateTime dateTime = ISODateTimeFormat.dateTimeParser().parseDateTime(utc);
				lastaccess = LocalDateTime.now();
				return dateTime;
			}
		}
		catch( Exception e ) {
			status = ERROR;
			Logger.getLogger(BrowserPool.class.getName()).log(Level.WARNING, null, e);
		}
		finally {
			if (lock.isHeldByCurrentThread()) lock.unlock();
		}
		return null;
	}
	
	public String getRemoteStatus() throws BrowserException {
		if( driver == null ) throw new BrowserException( "driver not initialized" );

		try {
			if (lock.tryLock(2, TimeUnit.SECONDS)) {
		
				JavascriptExecutor js = (JavascriptExecutor)driver;
				String status = (String) js.executeScript("if( typeof getStatus == \"function\" ) return getStatus(); else return null;");
				lastaccess = LocalDateTime.now();
				return status;
			}
		}
		catch( Exception e ) {
			status = ERROR;
			Logger.getLogger(BrowserPool.class.getName()).log(Level.WARNING, null, e);
		}
		finally {
			if (lock.isHeldByCurrentThread()) lock.unlock();
		}
		return null;
	}
		
	public String getScreenshot() throws BrowserException {
		if( driver == null ) throw new BrowserException( "driver not initialized" );

		try {
			if (lock.tryLock(2, TimeUnit.SECONDS)) {
				File scrFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
	
				String fileName = new SimpleDateFormat("yyyy-MM-dd HHmmss'.png'").format(new Date());
				FileUtils.copyFile(scrFile, new File(webroot+screenshotlocation+"/"+fileName));
				return screenshotlocation+"/"+fileName;
			}
		}
		catch( Exception e ) {
			status = ERROR;
			Logger.getLogger(BrowserPool.class.getName()).log(Level.WARNING, null, e);
		}
		finally {
			if (lock.isHeldByCurrentThread()) lock.unlock();
		}
		return null;
	}
	
	/**
	 * opens a url in browser
	 * @param url
	 * @throws BrowserException 
	 */
	public void get( String url ) throws BrowserException {
		if( driver == null ) throw new BrowserException( "driver not initialized" );
		if( status != CONNECTED ) return;
		try {
			if (lock.tryLock(2, TimeUnit.SECONDS)) {
				driver.get( url );
				lastaccess = LocalDateTime.now();
			}
		}
		catch( Exception e ) {
			status = ERROR;
			Logger.getLogger(BrowserPool.class.getName()).log(Level.WARNING, null, e);
		}
		finally {
			if (lock.isHeldByCurrentThread()) lock.unlock();
		}
	}
	
	public void setRemoteStatus( String json ) throws BrowserException {
		if( driver == null ) throw new BrowserException( "driver not initialized" );

		try {
			if (lock.tryLock(2, TimeUnit.SECONDS)) {
				JavascriptExecutor js = (JavascriptExecutor)driver;
				js.executeScript("if( typeof setStatus == \"function\" ) return setStatus('"+json+"'); else return null;");
				lastaccess = LocalDateTime.now();
			}
		}
		catch( Exception e ) {
			status = ERROR;
			Logger.getLogger(BrowserPool.class.getName()).log(Level.WARNING, null, e);
		}
		finally {
			if (lock.isHeldByCurrentThread()) lock.unlock();
		}
	}
	
	/**
	 * (re-)connects to the browser
	 * @throws Exception 
	 */
	public void connect() throws Exception {
		lastaccess = LocalDateTime.now();

		if( status != NONE ) {
			try {
				close();
				status = DISCONNECTED;
			}
			catch( Exception e ) {
				Logger.getLogger(BrowserPool.class.getName()).log(Level.WARNING, null, e);
			}
		}
		if( capabilities == null ) throw new BrowserException( "Capabilities not initialized" );
		
		try {
			if (lock.tryLock(1, TimeUnit.SECONDS)) {
				driver = new RemoteWebDriver(new java.net.URL( url ), capabilities);
				driver.manage().timeouts().setScriptTimeout(10, TimeUnit.SECONDS );
				status = CONNECTED;

				get( baseurl + "/content/index.html" );	
				WebDriverWait wait = new WebDriverWait(driver, 4);
				wait.until(ExpectedConditions.presenceOfElementLocated( By.id( "name" )));
				((JavascriptExecutor)driver).executeScript("document.getElementById('name').innerHTML='"+StringEscapeUtils.escapeHtml4(name)+"'; return;");
			}
		}
		catch( Exception e ) {
			status = ERROR;
			Logger.getLogger(BrowserPool.class.getName()).log(Level.WARNING, null, e);
		}
		finally {
			if (lock.isHeldByCurrentThread()) lock.unlock();
		}
	}
	
	/**
	 * creates safari capabilities based on configuration file
	 * 
	 * @param cfg configuration file
	 * @param id browser number
	 * @return capabilities
	 */
	private static DesiredCapabilities initSafari( AbstractConfiguration cfg, int id ) {
		DesiredCapabilities capabilities = DesiredCapabilities.safari();
		return capabilities;
	}

	/**
	 * creates firefox capabilities based on configuration file
	 * 
	 * @param cfg configuration file
	 * @param id browser number
	 * @return capabilities
	 */
	private static DesiredCapabilities initFirefox( AbstractConfiguration cfg, int id ) {
		String browserCfg = "browsers.browser("+id+")";
		
		FirefoxOptions options = new FirefoxOptions();
		// add arguments
		String[] arguments = cfg.getStringArray(browserCfg+".arguments.argument");
		options.addArguments(arguments);

		DesiredCapabilities capabilities = DesiredCapabilities.firefox();
		capabilities.setCapability(FirefoxOptions.FIREFOX_OPTIONS, options);
		return capabilities;
	}

	/**
	 * creates chrome capabilities based on configuration file
	 * 
	 * @param cfg configuration file
	 * @param id browser number
	 * @return capabilities
	 */
	private static DesiredCapabilities initChrome(AbstractConfiguration cfg, int id)  {
		String browserCfg = "browsers.browser("+id+")";
		ChromeOptions options = new ChromeOptions();
		
		// add extensions
		String[] extensions = cfg.getStringArray(browserCfg+".extensions.extension");
		if( extensions.length > 0 ) {
			for( String ext : extensions) {
				options.addExtensions(new File(ext));
			}
		}
		
		// add arguments
		String[] arguments = cfg.getStringArray(browserCfg+".arguments.argument");
		options.addArguments(arguments);
		
		// add preferences
		String[] ps = cfg.getStringArray(browserCfg+".prefs.pref");
		if(ps.length > 0 )
		{
			int numPrefs = ps.length;
			Map<String, Object> prefs = new HashMap<String, Object>();
			if( numPrefs > 0 ) {
				for( int i = 0; i < numPrefs; i++ ) {
					String pref = cfg.getString(browserCfg + ".prefs.pref("+i+")[@name]");
					String value = cfg.getString(browserCfg + ".prefs.pref("+i+")");
					
					if( pref != null && value != null ) {
						prefs.put( pref, value);
					}
				}
				prefs.put( "show_fullscreen_toolbar", false );
				if( prefs.size() > 0 ) {
					options.setExperimentalOption("prefs", prefs);
				}
			}
		}
		
		// set binary path
		String binary = cfg.getString( browserCfg+".binary");
		if( binary != null ) {
			options.setBinary(binary);
		}
		
		options.setExperimentalOption("excludeSwitches", Collections.singletonList("enable-automation"));
		
		DesiredCapabilities capabilities = DesiredCapabilities.chrome();
		capabilities.setCapability(ChromeOptions.CAPABILITY, options);
		return capabilities;
	}
	
	/**
	 * returns the name of the browser instance
	 * 
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * return host/ip of brwoser instance
	 * 
	 * @return the host/ip
	 */
	public String getHost() {
		return host;
	}
	
	/**
	 * closes driver
	 * @throws BrowserException 
	 */
	public void close() throws BrowserException {
		if( status == NONE ) return;
		
		if( driver == null ) throw new BrowserException( "driver not initialized" );
		try {
			if (lock.tryLock(2, TimeUnit.SECONDS)) {
				driver.close();
				driver = null;
				status = NONE;
			}
		}
		catch( Exception e ) {
			status = ERROR;
			Logger.getLogger(BrowserPool.class.getName()).log(Level.WARNING, null, e);
		}
		finally {
			if (lock.isHeldByCurrentThread()) lock.unlock();
		}
	}

}

