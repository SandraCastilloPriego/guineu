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
package guineu.modules.file.saveExpressionFile;

import guineu.data.Dataset;
import guineu.desktop.Desktop;
import guineu.main.GuineuCore;
import guineu.modules.GuineuModuleCategory;
import guineu.modules.GuineuProcessingModule;
import guineu.parameters.ParameterSet;
import guineu.taskcontrol.Task;
import guineu.taskcontrol.TaskEvent;
import guineu.taskcontrol.TaskStatus;
import guineu.util.dialogs.ExitCode;
import java.util.logging.Logger;

/**
 *
 * @author scsandra
 */
public class SaveExpressionFile implements GuineuProcessingModule {

        private Logger logger = Logger.getLogger(this.getClass().getName());
        private Desktop desktop;
        private Dataset[] Datasets;
        private SaveExpressionParameters parameters;

        public SaveExpressionFile(Dataset[] Datasets, ParameterSet parameters) {
                this.Datasets = Datasets;
                this.parameters = (SaveExpressionParameters) parameters;
        }

        public SaveExpressionFile() {
                this.parameters = new SaveExpressionParameters();
        }

        public void initModule() {
                ExitCode exitCode = parameters.showSetupDialog();
                if (exitCode != ExitCode.OK) {
                        return;
                }
                runModule();
        }

        public void taskStarted(Task task) {
                logger.info("Running Export Data set");
        }

        public void taskFinished(Task task) {
                if (task.getStatus() == TaskStatus.FINISHED) {
                        logger.info("Finished Save Data set" + ((SaveExpressionFileTask) task).getTaskDescription());
                }

                if (task.getStatus() == TaskStatus.ERROR) {

                        String msg = "Error while save Datas et on .. " + ((SaveExpressionFileTask) task).getErrorMessage();
                        logger.severe(msg);
                        desktop.displayErrorMessage(msg);

                }
        }

        public ParameterSet getParameterSet() {
                return parameters;
        }

        @Override
        public String toString() {
                return "Save Expression Data set";
        }

        public Task[] runModule() {

                // prepare a new group of tasks
                String path = (String) parameters.getParameter(SaveExpressionParameters.Expressionfilename).getValue().getAbsolutePath();
                Task tasks[] = new SaveExpressionFileTask[Datasets.length];
                for (int i = 0; i < Datasets.length; i++) {
                        String newpath = path;
                        if (i > 0) {
                                newpath = path.substring(0, path.length() - 4) + String.valueOf(i) + path.substring(path.length() - 4);
                        }
                        tasks[i] = new SaveExpressionFileTask(Datasets[i], parameters, newpath);
                }

                GuineuCore.getTaskController().addTasks(tasks);

                return tasks;

        }

        public void statusChanged(TaskEvent e) {
                throw new UnsupportedOperationException("Not supported yet.");
        }

        public Task[] runModule(ParameterSet parameters) {
                throw new UnsupportedOperationException("Not supported yet.");
        }

        public GuineuModuleCategory getModuleCategory() {
                throw new UnsupportedOperationException("Not supported yet.");
        }

        public String getIcon() {
                return null;
        }

        public boolean setSeparator() {
                return false;
        }
}
