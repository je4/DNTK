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

package org.objectspace.dntk.rest.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.objectspace.dntk.rest.model.Saying;


@Path("/hello")
public class HelloWorldResource {
	
	private static final String TEMPLATE = "Hello, %s!";
	
	@GET
	@Path("{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public Saying sayHello(@PathParam("name") String name) {		
		return new Saying(String.format(TEMPLATE, name));
    }
}
