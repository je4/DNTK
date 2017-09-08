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
package org.objectspace.dntk.rest.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class BrowserScreenshotReply {
	public String path = null;
	public int errorcode = 0;
	public String errormsg = "no error";
	
	public BrowserScreenshotReply() {
		
	}

	public BrowserScreenshotReply( String path ) {
		this.path = path;
	}

	public BrowserScreenshotReply( int errorcode, String errormsg, String namepath ) {
		this.path = path;
		this.errorcode = errorcode;
		this.errormsg = errormsg;
	}
}
