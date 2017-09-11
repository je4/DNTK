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
						} catch (Exception e) {
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
