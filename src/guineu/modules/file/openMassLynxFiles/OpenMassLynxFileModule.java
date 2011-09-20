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
package guineu.modules.file.openMassLynxFiles;

import guineu.main.GuineuCore;
import guineu.modules.GuineuModuleCategory;
import guineu.modules.GuineuProcessingModule;
import guineu.parameters.ParameterSet;
import guineu.taskcontrol.Task;

/**
 *
 * @author scsandra
 */
public class OpenMassLynxFileModule implements GuineuProcessingModule {

        public static final String MODULE_NAME = "Mass Lynx Files";
        private massLynxParameters parameters = new massLynxParameters();

        public ParameterSet getParameterSet() {
                return this.parameters;
        }

        public String toString() {
                return MODULE_NAME;
        }

        public Task[] runModule(ParameterSet parameters) {
                String FilePath = parameters.getParameter(massLynxParameters.fileName).getName();
                // prepare a new group of tasks
                if (FilePath != null) {
                        Task tasks[] = new OpenFileTask[1];
                        tasks[0] = new OpenFileTask(FilePath);

                        GuineuCore.getTaskController().addTasks(tasks);

                        return tasks;

                } else {
                        return null;
                }
        }

        public GuineuModuleCategory getModuleCategory() {
                return GuineuModuleCategory.FILE;
        }

        public String getIcon() {
                throw new UnsupportedOperationException("Not supported yet.");
        }

        public boolean setSeparator() {
                throw new UnsupportedOperationException("Not supported yet.");
        }
}
