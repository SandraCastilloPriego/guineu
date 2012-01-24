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
package guineu.modules.file.saveOtherFile;

import guineu.data.Dataset;
import guineu.main.GuineuCore;
import guineu.modules.GuineuModule;
import guineu.modules.GuineuModuleCategory;
import guineu.parameters.ParameterSet;
import guineu.taskcontrol.Task;
import guineu.util.dialogs.ExitCode;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author scsandra
 */
public class SaveOtherFileModule implements GuineuModule {

        public static final String MODULE_NAME = "Save text file";
        private Dataset[] datasets;
        private SaveOtherParameters parameters = new SaveOtherParameters();

        public void initModule(Dataset[] datasets) {
                this.datasets = datasets;
                ExitCode code = parameters.showSetupDialog();
                if (code == ExitCode.OK) {
                        runModule(parameters);
                }
        }

        public ParameterSet getParameterSet() {
                return parameters;
        }

        @Override
        public String toString() {
                return MODULE_NAME;
        }

        public Task[] runModule(ParameterSet parameters) {
                try {
                        // prepare a new group of tasks
                        String path = parameters.getParameter(SaveOtherParameters.Otherfilename).getValue().getCanonicalPath();
                        Task[] tasks = new SaveOtherFileTask[datasets.length];
                        for (int i = 0; i < datasets.length; i++) {
                                String newpath = path;
                                if (i > 0) {
                                        newpath = path.substring(0, path.length() - 4) + String.valueOf(i) + path.substring(path.length() - 4);
                                }
                                tasks[i] = new SaveOtherFileTask(datasets[i], parameters, newpath);
                        }
                        GuineuCore.getTaskController().addTasks(tasks);
                        return tasks;
                } catch (IOException ex) {
                        Logger.getLogger(SaveOtherFileModule.class.getName()).log(Level.SEVERE, null, ex);
                        return null;
                }
        }

        public GuineuModuleCategory getModuleCategory() {
                return GuineuModuleCategory.FILE;
        }
}
