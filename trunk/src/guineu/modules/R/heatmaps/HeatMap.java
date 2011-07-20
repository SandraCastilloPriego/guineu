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
package guineu.modules.R.heatmaps;

import guineu.data.Dataset;
import guineu.desktop.GuineuMenu;
import guineu.main.GuineuCore;
import guineu.main.GuineuModule;
import guineu.parameters.ParameterSet;
import guineu.taskcontrol.Task;
import guineu.taskcontrol.TaskListener;
import guineu.taskcontrol.TaskStatus;
import guineu.util.dialogs.ExitCode;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.logging.Logger;
import org.rosuda.JRI.RMainLoopCallbacks;
import org.rosuda.JRI.Rengine;

/**
 *
 * @author scsandra
 */
public class HeatMap implements GuineuModule, TaskListener, ActionListener {

        private Logger logger = Logger.getLogger(this.getClass().getName());
        private HeatMapParameters parameters;
        private Rengine re = null;

        public HeatMap() {
                GuineuCore.getDesktop().addMenuItem(GuineuMenu.STATISTICS, "Heat Map..",
                        "Creates a Heat Map with the selected molecules in the selected data set", KeyEvent.VK_H, this, null, "icons/others.png");

                re = GuineuCore.getR();
        }

        public void taskStarted(Task task) {
                logger.info("Running Heat Map");
        }

        public void taskFinished(Task task) {
                if (task.getStatus() == TaskStatus.FINISHED) {
                        logger.info("Finished Heat Map on " + ((HeatMapTask) task).getTaskDescription());
                }

                if (task.getStatus() == TaskStatus.ERROR) {

                        String msg = "Error while Heat Map on .. " + ((HeatMapTask) task).getErrorMessage();
                        logger.severe(msg);
                        GuineuCore.getDesktop().displayErrorMessage(msg);

                }
        }

        public void actionPerformed(ActionEvent e) {
                if (parameters == null) {
                        parameters = new HeatMapParameters();
                }
                ExitCode exitCode = parameters.showSetupDialog();
                if (exitCode != ExitCode.OK) {
                        return;
                }
                runModule();
        }

        public ParameterSet getParameterSet() {
                return parameters;
        }

        public String toString() {
                return "Heat Map";
        }

        public Task[] runModule() {
                Dataset[] selectedDatasets = GuineuCore.getDesktop().getSelectedDataFiles();

                // prepare a new group of tasks
                Task tasks[] = new HeatMapTask[selectedDatasets.length];
                for (int i = 0; i < selectedDatasets.length; i++) {
                        if (re != null) {
                                tasks[i] = new HeatMapTask(selectedDatasets[i], this.parameters, re);
                        }
                }
                GuineuCore.getTaskController().addTasks(tasks);

                return tasks;
        }
}
