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
package guineu.modules.identification.AdductSearch;

import guineu.data.Dataset;
import guineu.main.GuineuCore;
import guineu.modules.GuineuModuleCategory;
import guineu.modules.GuineuProcessingModule;
import guineu.parameters.ParameterSet;
import guineu.taskcontrol.Task;

/**
 * 
 */
public class AdductSearchModule implements GuineuProcessingModule {

        public static final String MODULE_NAME = "Adduct search";
        private AdductSearchParameters parameters = new AdductSearchParameters();

        public ParameterSet getParameterSet() {
                return parameters;
        }

        public String toString() {
                return MODULE_NAME;
        }

        public Task[] runModule(ParameterSet parameters) {
                Dataset[] peakLists = GuineuCore.getDesktop().getSelectedDataFiles();
                if (peakLists == null) {
                        throw new IllegalArgumentException(
                                "Cannot run identification without a peak list");
                }

                // prepare a new sequence of tasks
                Task tasks[] = new AdductSearchTask[peakLists.length];
                for (int i = 0; i < peakLists.length; i++) {
                        tasks[i] = new AdductSearchTask(
                                (AdductSearchParameters) parameters, peakLists[i]);
                }

                GuineuCore.getTaskController().addTasks(tasks);

                return tasks;
        }

        public GuineuModuleCategory getModuleCategory() {
                return GuineuModuleCategory.LCMSIDENTIFICATIONSUBMENU;
        }

        public String getIcon() {
                return "icons/adductsearch.png";
        }

        public boolean setSeparator() {
                return false;
        }
}
