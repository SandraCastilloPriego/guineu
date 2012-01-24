/*
 * Copyright 2007-2012 VTT Biotechnology
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
package guineu.modules.dataanalysis.clustering.em;

import guineu.modules.dataanalysis.clustering.VisualizationType;
import guineu.parameters.Parameter;
import guineu.parameters.SimpleParameterSet;
import guineu.parameters.parametersType.ComboParameter;
import guineu.parameters.parametersType.IntegerParameter;



public class EMClustererParameters extends SimpleParameterSet {

	public static final ComboParameter<VisualizationType> visualization = new ComboParameter<VisualizationType>(
			"Visualization type",
			"Select the kind of visualization for the clustering result",
			VisualizationType.values());

	public static final IntegerParameter numberOfIterations = new IntegerParameter(
			"Number of iterantions",
			"Specify the number of iterations to terminate if EM has not converged.",
			3);

	public EMClustererParameters() {
		super(new Parameter[] { numberOfIterations, visualization });
	}
}
