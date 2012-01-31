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
package guineu.modules.visualization.Rintensityboxplot;

import guineu.data.Dataset;
import guineu.main.GuineuCore;
import guineu.modules.GuineuModuleCategory;
import guineu.modules.GuineuProcessingModule;
import guineu.parameters.ParameterSet;
import guineu.taskcontrol.Task;

/**
 *
 * @author scsandra
 */
public class BoxPlotModule implements GuineuProcessingModule {

        public static final String MODULE_NAME = "Box plot (R)";
        private BoxPlotParameters parameters = new BoxPlotParameters();

        public ParameterSet getParameterSet() {
                return parameters;
        }

        @Override
        public String toString() {
                return MODULE_NAME;
        }

        public Task[] runModule(ParameterSet parameters) {
                Dataset[] selectedDatasets = GuineuCore.getDesktop().getSelectedDataFiles();
                // prepare a new group of tasks
                if (selectedDatasets != null && selectedDatasets.length > 0) {
                        Task tasks[] = new BoxPlotTask[1];
                        tasks[0] = new BoxPlotTask(selectedDatasets[0], this.parameters);
                        GuineuCore.getTaskController().addTasks(tasks);
                        return tasks;
                } else {
                        return null;
                }
        }

        public GuineuModuleCategory getModuleCategory() {
                return GuineuModuleCategory.VISUALIZATION;
        }

        public String getIcon() {
                return "icons/boxplot.png";
        }

        public boolean setSeparator() {
                return false;
        }
}
