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
package guineu.modules.statistics.clustering;

import guineu.data.Parameter;
import guineu.data.ParameterType;
import guineu.data.impl.SimpleParameter;
import guineu.data.impl.SimpleParameterSet;

/**
 *
 * @author scsandra
 */
public class ClusteringParameters extends SimpleParameterSet {

    private static String[] parameters = {"Cobweb", "DensityBasedClusterer", "FarthestFirst", "SimpleKMeans"};
    private static String[] dataType = {"Samples", "Variables"};


    public static final Parameter clusteringAlgorithm = new SimpleParameter(
            ParameterType.STRING, "Select the algorithm",
            "Select the algorithm you want to use for clustering", null, parameters);

    public static final Parameter clusteringData = new SimpleParameter(
            ParameterType.STRING, "Select the algorithm",
            "Select the algorithm you want to use for clustering", null, dataType);

   public static final Parameter N = new SimpleParameter(
			ParameterType.INTEGER, "Number of clusters to generate",
			"Specify the number of clusters to generate.",
			new Integer(3));

    public ClusteringParameters() {
        super(new Parameter[]{clusteringAlgorithm, clusteringData, N});
    }
}
