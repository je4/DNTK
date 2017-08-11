/**
 * 
 */
package org.objectspace.dntk.remote;

import java.io.File;
import java.net.MalformedURLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.configuration2.AbstractConfiguration;
import org.apache.commons.text.StringEscapeUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
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
	 * url of myself
	 */
	private String myurl = null;
	
	/**
	 * url of remote browser
	 */
	private String url = null;
	private String host;
	private int port;
	
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
	
	/**
	 * Constructor
	 * 
	 * Creates and initializes the WebDriver
	 * 
	 * @param cfg Configuration file
	 * @param id Current Browser ID
	 * @throws BrowserException 
	 * @throws MalformedURLException 
	 * @throws Exception 
	 */
	public Browser( AbstractConfiguration cfg, int id ) throws BrowserException, MalformedURLException {
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
	 * @throws BrowserException 
	 * @throws MalformedURLException 
	 * @throws Exception 
	 */
	public Browser( String host, int port, AbstractConfiguration cfg, int id ) throws BrowserException, MalformedURLException {
		
		this.host = host;
		this.port = port;
		this.url = "http://"+this.host+":"+this.port;
		
		// creating the correct WebDriver Object
		String browserCfg = "browsers.browser("+id+")";
		name = cfg.getString( browserCfg+".name" );
		browserType = cfg.getString( browserCfg+"[@type]" );
		myurl = cfg.getString( "jetty.url" );

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

		JavascriptExecutor js = (JavascriptExecutor)driver;
		try {
			String javascript = "return JSON.stringify({ time:(new Date()).toISOString(), appCodeName:navigator.appCodeName, appName:navigator.appName, appVersion:navigator.appVersion });";
			
			String json = (String) js.executeScript(javascript);
			
			Map<String, String> map = new HashMap<String, String>();
			map = mapper.readValue(json, new TypeReference<Map<String, String>>(){});
			lastaccess = LocalDateTime.now();
			return map;
		}
		catch( Exception ex ) {
			status = ERROR;
			return null;
		}
	}

	
	public DateTime getRemoteDate() throws BrowserException {
		if( driver == null ) throw new BrowserException( "driver not initialized" );

		JavascriptExecutor js = (JavascriptExecutor)driver;
		try {
			String utc = (String) js.executeScript("return new Date().toJSON();");
			DateTime dateTime = ISODateTimeFormat.dateTimeParser().parseDateTime(utc);
			lastaccess = LocalDateTime.now();
			return dateTime;
		}
		catch( Exception e ) {
			status = ERROR;
			Logger.getLogger(BrowserPool.class.getName()).log(Level.WARNING, null, e);
			return null;
		}
	}
	
	public String getRemoteStatus() throws BrowserException {
		if( driver == null ) throw new BrowserException( "driver not initialized" );

		JavascriptExecutor js = (JavascriptExecutor)driver;
		try {
			String status = (String) js.executeScript("if( typeof getStatus == \"function\" ) return getStatus(); else return null;");
			lastaccess = LocalDateTime.now();
			return status;
		}
		catch( Exception e ) {
			status = ERROR;
			Logger.getLogger(BrowserPool.class.getName()).log(Level.WARNING, null, e);
			return null;
		}
	}
		
	public void setRemoteStatus( String json ) throws BrowserException {
		if( driver == null ) throw new BrowserException( "driver not initialized" );

		JavascriptExecutor js = (JavascriptExecutor)driver;
		try {
			js.executeScript("if( typeof setStatus == \"function\" ) return setStatus('"+json+"'); else return null;");
			lastaccess = LocalDateTime.now();
		}
		catch( Exception e ) {
			status = ERROR;
			Logger.getLogger(BrowserPool.class.getName()).log(Level.WARNING, null, e);
		}
			
	}
	
	/**
	 * (re-)connects to the browser
	 * 
	 * @throws BrowserException
	 * @throws MalformedURLException
	 */
	public synchronized void connect() throws BrowserException, MalformedURLException {
		lastaccess = LocalDateTime.now();

		if( status != NONE ) {
			try {
				close();
			}
			catch( Exception e ) {
				Logger.getLogger(BrowserPool.class.getName()).log(Level.WARNING, null, e);
			}
		}
		if( capabilities == null ) throw new BrowserException( "Capabilities not initialized" );
		
		driver = new RemoteWebDriver(new java.net.URL( url ), capabilities);
		status = CONNECTED;
		
		get( myurl + "/content/index.html" );	
		WebDriverWait wait = new WebDriverWait(driver, 2);
		wait.until(ExpectedConditions.presenceOfElementLocated( By.id( "name" )));
		try {
			((JavascriptExecutor)driver).executeAsyncScript("document.getElementById('name').innerHTML='"+StringEscapeUtils.escapeHtml4(name)+"';");
		}
		
		catch( Exception e ) {
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
		driver.close();
		driver = null;
		status = NONE;
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
			driver.get( url );
			lastaccess = LocalDateTime.now();
		}
		catch( Exception e) {
			status = ERROR;
		}
	}
}
