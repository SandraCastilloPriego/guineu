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
package guineu.modules.file.openBasicFiles;

import guineu.main.GuineuCore;
import guineu.modules.GuineuModuleCategory;
import guineu.modules.GuineuProcessingModule;
import guineu.parameters.ParameterSet;
import guineu.taskcontrol.Task;
import java.io.File;

/**
 *
 * @author scsandra
 */
public class OpenBasicFileModule implements GuineuProcessingModule {

        public static final String MODULE_NAME = "Open Text file";
        private OpenBasicFileParameters parameters = new OpenBasicFileParameters();

        public ParameterSet getParameterSet() {
                return parameters;
        }

        public String toString() {
                return MODULE_NAME;
        }

        public Task[] runModule(ParameterSet parameters) {
                File[] files = this.parameters.getParameter(OpenBasicFileParameters.fileNames).getValue();

                // prepare a new group of tasks
                Task tasks[] = new OpenBasicFileTask[files.length];
                for (int i = 0; i < files.length; i++) {
                        tasks[i] = new OpenBasicFileTask(files[i].getAbsolutePath());
                }
                GuineuCore.getTaskController().addTasks(tasks);

                return tasks;
        }

        public GuineuModuleCategory getModuleCategory() {
                return GuineuModuleCategory.FILE;
        }

        public String getIcon() {
                return "icons/others.png";
        }

        public boolean setSeparator() {
                return true;
        }
}
