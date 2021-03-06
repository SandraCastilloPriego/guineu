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
package guineu.modules.file.saveExpressionFile;

import guineu.data.Dataset;
import guineu.main.GuineuCore;
import guineu.modules.GuineuModule;
import guineu.modules.GuineuModuleCategory;
import guineu.parameters.ParameterSet;
import guineu.parameters.SimpleParameterSet;
import guineu.taskcontrol.Task;
import guineu.util.dialogs.ExitCode;

/**
 *
 * @author scsandra
 */
public class SaveExpressionFileModule implements GuineuModule {

        public static final String MODULE_NAME = "Save Expression Data set";
        private Dataset[] datasets;
        private SaveExpressionParameters parameters = new SaveExpressionParameters();

        public void initModule(Dataset[] datasets, ParameterSet parameters) {
                this.datasets = datasets;
                this.parameters = (SaveExpressionParameters) parameters;
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

                // prepare a new group of tasks
                String path = (String) parameters.getParameter(SaveExpressionParameters.Expressionfilename).getValue().getAbsolutePath();
                Task tasks[] = new SaveExpressionFileTask[datasets.length];
                for (int i = 0; i < datasets.length; i++) {
                        String newpath = path;
                        if (i > 0) {
                                newpath = path.substring(0, path.length() - 4) + String.valueOf(i) + path.substring(path.length() - 4);
                        }
                        tasks[i] = new SaveExpressionFileTask(datasets[i], (SimpleParameterSet) parameters, newpath);
                }

                GuineuCore.getTaskController().addTasks(tasks);

                return tasks;

        }
        

        public GuineuModuleCategory getModuleCategory() {
                return GuineuModuleCategory.FILE;
        }

        public String getIcon() {
                return null;
        }

        public boolean setSeparator() {
                return false;
        }
}
