/*******************************************************************************
 * Copyright (c) 2017 Juergen Enge.
 *
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

import org.eclipse.jetty.server.Server;

/**
 * @author juergen.enge
 *
 * Thread to take care on proper shutdown of jetty
 */
public class JettyThread implements Runnable {

	/**
	 * the jetty server
	 */
	private Server jetty;
	
	/**
	 * Constructor
	 * @param jetty the jetty server to start
	 */
	public JettyThread( Server jetty ) {
		this.jetty = jetty;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public synchronized void run() {
		try {
			jetty.start();
			jetty.join();
			Logger.getLogger(Main.class.getName()).log(Level.INFO, "Jetty stopped" );
        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
        	jetty.destroy();
        }
	}

}
