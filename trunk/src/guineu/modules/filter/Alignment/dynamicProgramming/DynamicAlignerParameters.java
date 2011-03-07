/*
 * Copyright 2007-2010 VTT Biotechnology
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
package guineu.modules.filter.Alignment.dynamicProgramming;

import guineu.data.Parameter;
import guineu.data.ParameterType;
import guineu.data.impl.SimpleParameter;
import guineu.data.impl.SimpleParameterSet;
import guineu.main.GuineuCore;
import java.text.NumberFormat;



public class DynamicAlignerParameters extends SimpleParameterSet {

	public static final NumberFormat percentFormat = NumberFormat
			.getPercentInstance();

	public static final Parameter peakListName = new SimpleParameter(
			ParameterType.STRING, "Peak list name", "Peak list name", null,
			"Aligned peak list", null);

	public static final Parameter MZTolerance = new SimpleParameter(
			ParameterType.DOUBLE, "m/z tolerance",
			"Maximum allowed M/Z difference", "m/z", new Double(0.02),
			new Double(0.0), null, GuineuCore.getMZFormat());	

	public static final Parameter RTTolerance = new SimpleParameter(
			ParameterType.DOUBLE, "RT tolerance",
			"Maximum allowed absolute RT difference", null, new Double(15.0),
			new Double(0.0), null, GuineuCore.getRTFormat());

	public DynamicAlignerParameters() {
		super(new Parameter[] { peakListName, MZTolerance,
				RTTolerance});
	}
}
