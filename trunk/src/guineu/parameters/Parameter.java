/*
 * Copyright 2007-2011 VTT Biotechnology
 * 
 * This file is part of Guineu.
 * 
 * Guineu is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * Guineu is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * Guineu; if not, write to the Free Software Foundation, Inc., 51 Franklin St,
 * Fifth Floor, Boston, MA 02110-1301 USA
 */

package guineu.parameters;

import org.w3c.dom.Element;

/**
 * Parameter interface, represents parameters or variables used in the project
 */
public interface Parameter {

	/**
	 * Returns this parameter's name. The name must be unique within one
	 * ParameterSet.
	 * 
	 * @return Parameter name
	 */
	public String getName();
	
	public void loadValueFromXML(Element xmlElement);

	public void saveValueToXML(Element xmlElement);

	// Modules which have one single parameter set may return this
	public Parameter clone();
	
}
