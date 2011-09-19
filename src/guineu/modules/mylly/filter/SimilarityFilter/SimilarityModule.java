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
package guineu.modules.mylly.filter.SimilarityFilter;

import guineu.main.GuineuCore;
import guineu.modules.GuineuModuleCategory;
import guineu.taskcontrol.Task;
import guineu.data.Dataset;
import guineu.data.impl.datasets.SimpleGCGCDataset;
import guineu.modules.GuineuProcessingModule;
import guineu.parameters.ParameterSet;

/**
 *
 * @author scsandra
 */
public class SimilarityModule implements GuineuProcessingModule {

        public static final String MODULE_NAME = "Similarity Filter";
        private SimilarityParameters parameters = new SimilarityParameters();

        public ParameterSet getParameterSet() {
                return this.parameters;
        }

        public String toString() {
                return MODULE_NAME;
        }

        public Task[] runModule(ParameterSet parameters) {
                Dataset[] DataFiles = GuineuCore.getDesktop().getSelectedDataFiles();

                // prepare a new group of tasks
                Task tasks[] = new SimilarityModuleTask[DataFiles.length];
                for (int cont = 0; cont < DataFiles.length; cont++) {
                        tasks[cont] = new SimilarityModuleTask((SimpleGCGCDataset) DataFiles[cont], (SimilarityParameters) parameters);
                }
                GuineuCore.getTaskController().addTasks(tasks);

                return tasks;
        }

        public GuineuModuleCategory getModuleCategory() {
                return GuineuModuleCategory.MYLLY;
        }
}
