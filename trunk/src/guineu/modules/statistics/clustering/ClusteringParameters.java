/*
 * Copyright 2007-2011 VTT Biotechnology
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

import guineu.parameters.SimpleParameterSet;
import guineu.parameters.UserParameter;
import guineu.parameters.parametersType.ComboParameter;
import guineu.parameters.parametersType.NumberParameter;
import java.text.NumberFormat;

/**
 *
 * @author scsandra
 */
public class ClusteringParameters extends SimpleParameterSet {

        private static String[] parameters = {"Cobweb", "DensityBasedClusterer", "FarthestFirst", "SimpleKMeans"};
        private static String[] dataType = {"Samples", "Variables"};
        public static final NumberFormat integerFormat = NumberFormat.getIntegerInstance();
        public static final ComboParameter<String> clusteringAlgorithm = new ComboParameter<String>(
                "Select the algorithm",
                "Select the algorithm you want to use for clustering", parameters);
        public static final ComboParameter<String> clusteringData = new ComboParameter<String>(
                "Select the algorithm",
                "Select the algorithm you want to use for clustering", dataType);
        public static final NumberParameter N = new NumberParameter(
                "Number of clusters to generate",
                "Specify the number of clusters to generate.", integerFormat,
                new Integer(3));

        public ClusteringParameters() {
                super(new UserParameter[]{clusteringAlgorithm, clusteringData, N});
        }
}
