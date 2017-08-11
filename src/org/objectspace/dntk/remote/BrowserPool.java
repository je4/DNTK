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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.configuration2.AbstractConfiguration;

class BrowserPoolException extends Exception {

	/**
	 * serial number 1
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor
	 * @param string error message
	 */
	public BrowserPoolException(String string) {
		super( string );
	}
};

/**
 * @author juergen.enge
 *
 */
public class BrowserPool implements Iterable<Browser> {

	private static BrowserPool instance = null;

	private Map<String, Browser> browsers = new HashMap<String, Browser>();
	
	private BrowserPool() {

	}

	public Iterator<Browser> iterator() {
		return browsers.values().iterator();
	}
	
	public static synchronized BrowserPool getInstance() {
		if (BrowserPool.instance == null) {
			BrowserPool.instance = new BrowserPool();
		}
		return BrowserPool.instance;
	}

	public String[] getBrowserNames() {
		Set<String> keys = browsers.keySet();
		String[] names = keys.toArray(new String[keys.size()]);
		
		return names;
	}
	
	public Browser getBrowser(String name) {
		return browsers.get(name);
	}
	
	public static void init(AbstractConfiguration cfg) throws BrowserPoolException {
		String[] bs = cfg.getStringArray("browsers.browser.name");
		if( bs.length < 1 )
		{
			throw new BrowserPoolException( "configuratrion error: no browser found" );
		}

		
		BrowserPool pool = getInstance();
		
		int numBrowsers = bs.length;
		for( int i = 0; i < numBrowsers; i++ ) {
			if( !cfg.containsKey("browsers.browser("+i+").host")) continue;
			Browser b;
			try {
				b = new Browser( cfg, i );
				pool.addBrowser(b);
				b.getRemoteDate();
				b.getRemoteInfo();
			} catch ( Exception e) {
				Logger.getLogger(BrowserPool.class.getName()).log(Level.WARNING, null, e);
			}
		}

	}
	
	public static String getName( String host ) {
		for( Browser b : BrowserPool.getInstance()) {
			if( b.getHost().equals(host) ) return b.getName();
		}
		return null;
	}
	
	public static Browser findName( String name ) {
		for( Browser b : BrowserPool.getInstance()) {
			if( b.getName().equals(name) ) return b;
		}
		return null;
	}
	public static Browser findHost( String host ) {
		for( Browser b : BrowserPool.getInstance()) {
			if( b.getHost().equals(host) ) return b;
		}
		return null;
	}
	
	/**
	 * add new Browser to pool
	 * @param browser the browser to add
	 */
	public void addBrowser( Browser browser ) {
		browsers.put( browser.getName(), browser);
	}
	
	/**
	 * closes all connected Browser (i.e. shut down webbrowser)
	 */
	public void close() {
		for( Entry<String, Browser> entry : browsers.entrySet() ) {
			try {
				Logger.getLogger(BrowserPool.class.getName()).log(Level.INFO, "Shutting down "+entry.getKey());
				entry.getValue().close();
			} catch (Exception e) {
				Logger.getLogger(BrowserPool.class.getName()).log(Level.WARNING, null, e);
			}
		}

		browsers = new HashMap<String, Browser>();
	}
}
