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
package guineu.modules.statistics.clustering;

import guineu.desktop.Desktop;
import guineu.main.GuineuCore;
import guineu.modules.GuineuModuleCategory;
import guineu.taskcontrol.Task;
import guineu.taskcontrol.TaskEvent;
import guineu.taskcontrol.TaskStatus;

import guineu.util.dialogs.ExitCode;
import java.awt.event.ActionEvent;
import guineu.data.Dataset;
import guineu.modules.GuineuProcessingModule;
import guineu.modules.statistics.PCA.ProjectionPlotDataset;
import guineu.parameters.ParameterSet;
import guineu.util.GUIUtils;
import java.util.logging.Logger;

/**
 *
 * @author scsandra
 */
public class ClusteringTest implements GuineuProcessingModule {

        private Logger logger = Logger.getLogger(this.getClass().getName());
        private Desktop desktop;
        private ClusteringParameters parameters = new ClusteringParameters();
        final String helpID = GUIUtils.generateHelpID(this);

             

        public void taskStarted(Task task) {
                logger.info("Clustering data");
        }

        public void taskFinished(Task task) {
                if (task.getStatus() == TaskStatus.FINISHED) {
                        logger.info("Clustering data ");
                }

                if (task.getStatus() == TaskStatus.ERROR) {

                        String msg = "Error while Clustering datat .. ";
                        logger.severe(msg);
                        desktop.displayErrorMessage(msg);

                }
        }

        public void actionPerformed(ActionEvent e) {
                try {
                        Dataset[] dataFiles = desktop.getSelectedDataFiles();
                        if (dataFiles != null && dataFiles[0].getParametersName().size() > 0) {
                                ExitCode exitCode = this.parameters.showSetupDialog();
                                if (exitCode != ExitCode.OK) {
                                        return;
                                }
                                runModule();
                        } else {
                        }
                } catch (Exception exception) {
                }
        }

        public ParameterSet getParameterSet() {
                return this.parameters;
        }

        public void setParameters(ParameterSet parameterValues) {
                parameters = (ClusteringParameters) parameters;
        }

        @Override
        public String toString() {
                return "Clustering data";
        }

        public Task[] runModule() {

               // Dataset[] DataFiles = desktop.getSelectedDataFiles();
                // prepare a new group of tasks
                Task tasks[] = new ClusteringTestTask[1];
                ProjectionPlotDataset dataset = new ClusteringTask(parameters);
               // tasks[0] = new ClusteringTestTask(DataFiles[0], parameters);
                tasks[0] = dataset;
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
