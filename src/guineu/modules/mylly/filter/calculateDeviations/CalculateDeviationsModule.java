/*
 * Copyright 2007-2013 VTT Biotechnology
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
package guineu.modules.mylly.filter.calculateDeviations;

import guineu.main.GuineuCore;
import guineu.modules.GuineuModuleCategory;
import guineu.taskcontrol.Task;
import guineu.data.Dataset;
import guineu.modules.GuineuProcessingModule;
import guineu.parameters.ParameterSet;

/**
 *
 * @author scsandra
 */
public class CalculateDeviationsModule implements GuineuProcessingModule {

        public static final String MODULE_NAME = "Calculate Deviations";
        private CalculateDeviationsParameters parameters = new CalculateDeviationsParameters();

        public ParameterSet getParameterSet() {
                return this.parameters;
        }

        public String toString() {
                return MODULE_NAME;
        }

        public Task[] runModule(ParameterSet parameters) {
                Dataset[] AlignmentFiles = GuineuCore.getDesktop().getSelectedDataFiles();


                // prepare a new group of tasks
                Task tasks[] = new CalculateDeviationsTask[AlignmentFiles.length];
                for (int i = 0; i < AlignmentFiles.length; i++) {
                        tasks[i] = new CalculateDeviationsTask(AlignmentFiles[i], (CalculateDeviationsParameters) parameters);
                }
                GuineuCore.getTaskController().addTasks(tasks);

                return tasks;
        }

        public GuineuModuleCategory getModuleCategory() {
                return GuineuModuleCategory.MYLLY;
        }

        public String getIcon() {
                return "icons/deviations.png";
        }

        public boolean setSeparator() {
                return false;
        }
}
