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
package guineu.modules.filter.Alignment.RANSACGCGC;

import guineu.data.Dataset;
import guineu.main.GuineuCore;
import guineu.modules.GuineuModuleCategory;
import guineu.modules.GuineuProcessingModule;
import guineu.parameters.ParameterSet;
import guineu.taskcontrol.Task;

/**
 * 
 */
public class RansacGCGCAlignerModule implements GuineuProcessingModule {

        public static final String MODULE_NAME = "Ransac GCGC aligner";
        private RansacGCGCAlignerParameters parameters = new RansacGCGCAlignerParameters();

        public String toString() {
                return MODULE_NAME;
        }

        public ParameterSet getParameterSet() {
                return parameters;
        }

        public Task[] runModule(ParameterSet parameters) {
                Dataset[] peakLists = GuineuCore.getDesktop().getSelectedDataFiles();
                // check peak lists
                if ((peakLists == null) || (peakLists.length == 0)) {
                        GuineuCore.getDesktop().displayErrorMessage("Please select peak lists for alignment");
                        return null;
                }

                // prepare a new group with just one task
                Task task = new RansacGCGCAlignerTask(peakLists, (RansacGCGCAlignerParameters) parameters);

                GuineuCore.getTaskController().addTask(task);

                return new Task[]{task};
        }

        public GuineuModuleCategory getModuleCategory() {
                return GuineuModuleCategory.MYLLY;
        }

        public String getIcon() {
                return "icons/alignment.png";
        }

        public boolean setSeparator() {
                return false;
        }
}
