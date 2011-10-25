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
package guineu.modules.statistics.PCA;

import guineu.main.GuineuCore;
import guineu.modules.GuineuModuleCategory;
import guineu.modules.GuineuProcessingModule;
import guineu.parameters.ParameterSet;
import guineu.taskcontrol.Task;

public class CDAPlotModule implements GuineuProcessingModule {

        private ProjectionPlotParameters parameters = new ProjectionPlotParameters();

        public String toString() {
                return "Curvilinear distance analysis (CDA)";
        }

        public ProjectionPlotParameters getParameterSet() {
                return parameters;
        }

        @Override
        public Task[] runModule(ParameterSet parameters) {
                ProjectionPlotDataset dataset = new CDADataset((ProjectionPlotParameters) parameters);
                GuineuCore.getTaskController().addTask(dataset);
                return new Task[]{dataset};
        }

        @Override
        public GuineuModuleCategory getModuleCategory() {
                return GuineuModuleCategory.DATAANALYSIS;
        }

        public String getIcon() {
                return "icons/cda.png";
        }

        public boolean setSeparator() {
                return false;
        }
}
