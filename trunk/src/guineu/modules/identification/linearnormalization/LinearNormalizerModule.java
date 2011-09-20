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
package guineu.modules.identification.linearnormalization;

import guineu.data.Dataset;
import guineu.main.GuineuCore;
import guineu.modules.GuineuModuleCategory;
import guineu.modules.GuineuProcessingModule;
import guineu.parameters.ParameterSet;
import guineu.taskcontrol.Task;
import guineu.taskcontrol.TaskEvent;

/**
 * @author Taken from MZmine2
 * http://mzmine.sourceforge.net/
 */
public class LinearNormalizerModule implements GuineuProcessingModule {

        public static final String MODULE_NAME = "Linear normalizer";
        private LinearNormalizerParameters parameters = new LinearNormalizerParameters();

        public String toString() {
                return MODULE_NAME;
        }

        public ParameterSet getParameterSet() {
                return parameters;
        }

        public void statusChanged(TaskEvent e) {
                throw new UnsupportedOperationException("Not supported yet.");
        }

        public Task[] runModule(ParameterSet parameters) {
                Dataset[] peakLists = GuineuCore.getDesktop().getSelectedDataFiles();
                // check peak lists
                if ((peakLists == null) || (peakLists.length == 0)) {
                        GuineuCore.getDesktop().displayErrorMessage("Please select peak lists for normalization");
                        return null;
                }

                // prepare a new group of tasks
                Task tasks[] = new LinearNormalizerTask[peakLists.length];
                for (int i = 0; i < peakLists.length; i++) {
                        tasks[i] = new LinearNormalizerTask(peakLists[i],
                                (LinearNormalizerParameters) parameters);
                }

                GuineuCore.getTaskController().addTasks(tasks);

                return tasks;

        }

        public GuineuModuleCategory getModuleCategory() {
                return GuineuModuleCategory.NORMALIZATION;
        }

        public String getIcon() {
                return null;
        }

        public boolean setSeparator() {
                return false;
        }
}
