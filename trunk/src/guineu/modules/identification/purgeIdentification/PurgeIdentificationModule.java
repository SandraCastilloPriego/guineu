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
package guineu.modules.identification.purgeIdentification;

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
public class PurgeIdentificationModule implements GuineuProcessingModule {

        public static final String MODULE_NAME = "Clean Identification";

        @Override
        public ParameterSet getParameterSet() {
                return null;
        }

        @Override
        public String toString() {
                return MODULE_NAME;
        }

        @Override
        public Task[] runModule(ParameterSet parameters) {
                // prepare a new group of tasks
                Dataset[] datasets = GuineuCore.getDesktop().getSelectedDataFiles();
                Task tasks[] = new PurgeIdentificationTask[datasets.length];
                int cont = 0;
                for (Dataset dataset : datasets) {
                        tasks[cont++] = new PurgeIdentificationTask(dataset);
                }
                GuineuCore.getTaskController().addTasks(tasks);

                return tasks;
        }

        @Override
        public GuineuModuleCategory getModuleCategory() {
                return GuineuModuleCategory.LCMSIDENTIFICATIONSUBMENU;
        }

        @Override
        public String getIcon() {
                return null;
        }

        @Override
        public boolean setSeparator() {
                return false;
        }
}
