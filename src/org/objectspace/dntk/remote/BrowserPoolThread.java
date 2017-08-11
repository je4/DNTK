package org.objectspace.dntk.remote;

import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.configuration2.AbstractConfiguration;
import org.joda.time.LocalDateTime;
import org.joda.time.Seconds;

public class BrowserPoolThread implements Runnable {

	private BrowserPool pool;
	private Boolean end = false;
	private int timeout;
	
	public BrowserPoolThread( AbstractConfiguration cfg, BrowserPool pool ) {
		this.pool = pool;
		this.timeout = cfg.getInt( "browsers.timeout", 20 );
	}
	
	public void stop() {
		end = true;
	}
	
	@Override
	public void run() {
		while( !end ) {
			LocalDateTime now = LocalDateTime.now();
			for( Browser b : pool) {
				Seconds s = Seconds.secondsBetween(b.getLastAccess(), now);
				if( s.getSeconds() > timeout ) {
					if( b.getStatus() == Browser.CONNECTED) {
						try {
							b.getRemoteDate();
						} catch (BrowserException e) {
							Logger.getLogger(BrowserPool.class.getName()).log(Level.WARNING, null, e);
						}
					}
					else if( b.getStatus() == Browser.ERROR) {
						try {
							b.connect();
						} catch (MalformedURLException | BrowserException e) {
							Logger.getLogger(BrowserPool.class.getName()).log(Level.WARNING, null, e);
						}
					}
				}
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				Logger.getLogger(BrowserPool.class.getName()).log(Level.WARNING, null, e);
			}
		}
	}

}
