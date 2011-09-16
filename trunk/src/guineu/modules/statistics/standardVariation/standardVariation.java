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
package guineu.modules.statistics.standardVariation;

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
import java.awt.event.ActionEvent;
import java.util.logging.Logger;

/**
 *
 * @author scsandra
 */
public class standardVariation implements GuineuProcessingModule {

        private Logger logger = Logger.getLogger(this.getClass().getName());
        private Desktop desktop;
        private Dataset dataset;
        private String[] group1, group2;

       
        public void taskStarted(Task task) {
                logger.info("Running Standard Variation");
        }

        public void taskFinished(Task task) {
                if (task.getStatus() == TaskStatus.FINISHED) {
                        logger.info("Finished Standard Variation on " + ((standarVariationTask) task).getTaskDescription());
                }

                if (task.getStatus() == TaskStatus.ERROR) {

                        String msg = "Error while Standard Variation on .. " + ((standarVariationTask) task).getErrorMessage();
                        logger.severe(msg);
                        desktop.displayErrorMessage(msg);

                }
        }

        public void actionPerformed(ActionEvent e) {
                ExitCode exitCode = setupParameters();
                if (exitCode != ExitCode.OK) {
                        return;
                }

                runModule();
        }

        public ExitCode setupParameters() {
                try {
                        Dataset[] datasets = desktop.getSelectedDataFiles();
                        dataset = datasets[0];
                        standardVariationDataDialog dialog = new standardVariationDataDialog(dataset);
                        dialog.setVisible(true);
                        group1 = dialog.getGroup1();
                        group2 = dialog.getGroup2();
                        return dialog.getExitCode();
                } catch (Exception exception) {
                        return ExitCode.CANCEL;
                }
        }

        public ParameterSet getParameterSet() {
                return null;
        }

        public String toString() {
                return "Standard Variation";
        }

        public Task[] runModule() {

                // prepare a new group of tasks

                Task tasks[] = new standarVariationTask[1];
                tasks[0] = new standarVariationTask(group1, group2, dataset);

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
}
