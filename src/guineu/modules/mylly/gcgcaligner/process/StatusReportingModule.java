/*
    Copyright 2006-2007 VTT Biotechnology

    This file is part of MYLLY.

    MYLLY is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    MYLLY is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with MYLLY; if not, write to the Free Software
    Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
*/
package guineu.modules.mylly.gcgcaligner.process;



import guineu.modules.mylly.gcgcaligner.gui.parameters.ParameterInputException;
import java.awt.Frame;


/**
 * Every class implementing this should implement a zero-argument constructor too.
 * The class will then be configured by {@link StatusReportingModule#askParameters(Frame)}.
 * 
 */
public interface StatusReportingModule<T, Y> extends ProcessUI, StatusReportingProcess<T, Y>
{
	
	public StatusReportingModule<T,Y> clone();
	
	/**
	 * How to handle errors?
	 * @param parentWindow
	 * @throws ParameterInputException when it encounters problems reading parameters.
	 */
	public abstract void askParameters(Frame parentWindow) throws ParameterInputException;

	/**
	 * Parameters do anything important?
	 * @return
	 */
	public abstract boolean isConfigurable();;
}
