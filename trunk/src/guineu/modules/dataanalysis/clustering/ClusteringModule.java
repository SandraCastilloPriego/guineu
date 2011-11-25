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
package guineu.modules.dataanalysis.clustering;

import guineu.main.GuineuCore;
import guineu.modules.GuineuModuleCategory;
import guineu.taskcontrol.Task;
import guineu.modules.GuineuProcessingModule;
import guineu.modules.dataanalysis.PCA.ProjectionPlotDataset;
import guineu.parameters.ParameterSet;

/**
 *
 * @author scsandra
 */
public class ClusteringModule implements GuineuProcessingModule {

        public static final String MODULE_NAME = "Clustering algorithms";
        private ClusteringParameters parameters = new ClusteringParameters();

        public ParameterSet getParameterSet() {
                return this.parameters;
        }

        @Override
        public String toString() {
                return MODULE_NAME;
        }

        public Task[] runModule(ParameterSet parameters) {
                ProjectionPlotDataset dataset = new ClusteringTask(parameters);
		GuineuCore.getTaskController().addTask(dataset);
		return new Task[] { dataset };
        }

        public GuineuModuleCategory getModuleCategory() {
                return GuineuModuleCategory.DATAANALYSIS;
        }

        public String getIcon() {
                return "icons/clustering.png";
        }

        public boolean setSeparator() {
                return false;
        }
}
