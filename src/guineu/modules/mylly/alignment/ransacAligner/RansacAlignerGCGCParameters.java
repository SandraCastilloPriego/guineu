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
package guineu.modules.mylly.alignment.ransacAligner;

import guineu.data.Parameter;
import guineu.data.ParameterType;
import guineu.data.impl.SimpleParameter;
import guineu.data.impl.SimpleParameterSet;
import guineu.main.GuineuCore;
import java.text.NumberFormat;



public class RansacAlignerGCGCParameters extends SimpleParameterSet {

	public static final NumberFormat percentFormat = NumberFormat
			.getPercentInstance();

	public static final Parameter peakListName = new SimpleParameter(
			ParameterType.STRING, "Peak list name", "Peak list name", null,
			"Aligned peak list", null);

	public static final Parameter RT1Tolerance = new SimpleParameter(
			ParameterType.DOUBLE, "RT1 tolerance",
			"Maximum allowed RT1 difference", null, new Double(10.0),
			new Double(0.0), null, GuineuCore.getMZFormat());
	
	public static final Parameter RT2Tolerance = new SimpleParameter(
			ParameterType.DOUBLE, "RT2 tolerance",
			"Maximum allowed absolute RT2 difference", null, new Double(0.01),
			new Double(0.0), null, GuineuCore.getMZFormat());

	public static final Parameter Iterations = new SimpleParameter(
			ParameterType.INTEGER, "RANSAC Iterations",
			"Maximum number of iterations allowed in the algorithm",
			new Integer(1000));

	public static final Parameter NMinPoints = new SimpleParameter(
			ParameterType.DOUBLE, "Minimun Number of Points",
			"Minimum number of aligned peaks required to fit the model", "%",
			new Double(0.2), null, null, percentFormat);

	public static final Parameter Margin = new SimpleParameter(
			ParameterType.DOUBLE, "Threshold value",
			"Threshold value for determining when a data point fits a model",
			"seconds", new Double(3.0), null, GuineuCore.getMZFormat());

	public static final Parameter Linear = new SimpleParameter(
			ParameterType.BOOLEAN, "Linear model",
			"Switch between polynomial model or lineal model", new Boolean(
					true));

	public RansacAlignerGCGCParameters() {
		super(new Parameter[] { peakListName, RT1Tolerance, RT2Tolerance,
				Iterations, NMinPoints, Margin, Linear});
	}
}
