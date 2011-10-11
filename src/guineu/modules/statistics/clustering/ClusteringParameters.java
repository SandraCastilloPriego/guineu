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

import guineu.data.PeakListRow;
import guineu.main.GuineuCore;
import guineu.modules.statistics.PCA.ProjectionPlotParameters;
import guineu.modules.statistics.clustering.em.EMClusterer;
import guineu.modules.statistics.clustering.farthestfirst.FarthestFirstClusterer;
import guineu.modules.statistics.clustering.hierarchical.HierarClusterer;
import guineu.modules.statistics.clustering.simplekmeans.SimpleKMeansClusterer;
import guineu.parameters.Parameter;
import guineu.parameters.SimpleParameterSet;
import guineu.parameters.parametersType.ComboParameter;
import guineu.parameters.parametersType.ModuleComboParameter;
import guineu.util.dialogs.ExitCode;

public class ClusteringParameters extends SimpleParameterSet {

        private static ClusteringAlgorithm algorithms[] = new ClusteringAlgorithm[]{
                new EMClusterer(), new FarthestFirstClusterer(),
                new SimpleKMeansClusterer(), new HierarClusterer()};
        public static final ModuleComboParameter<ClusteringAlgorithm> clusteringAlgorithm = new ModuleComboParameter<ClusteringAlgorithm>(
                "Clustering algorithm",
                "Select the algorithm you want to use for clustering", algorithms);
        public static final ComboParameter<ClusteringDataType> typeOfData = new ComboParameter<ClusteringDataType>(
                "Type of data",
                "Specify the type of data used for the clustering: samples or variables",
                ClusteringDataType.values());

        public ClusteringParameters() {
                super(
                        new Parameter[]{ProjectionPlotParameters.dataFiles, clusteringAlgorithm,
                                typeOfData});
        }

        @Override
        public ExitCode showSetupDialog() {

                String dataFileChoices[];
                if (GuineuCore.getDesktop().getSelectedDataFiles().length >= 1) {
                        dataFileChoices = GuineuCore.getDesktop().getSelectedDataFiles()[0].getAllColumnNames().toArray(new String[0]);
                } else {
                        dataFileChoices = new String[0];
                }

                getParameter(ProjectionPlotParameters.dataFiles).setChoices(
                        dataFileChoices);

                return super.showSetupDialog();
        }
}
