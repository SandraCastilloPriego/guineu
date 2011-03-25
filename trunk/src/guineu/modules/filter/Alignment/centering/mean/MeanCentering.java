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
package guineu.modules.filter.Alignment.centering.mean;

import guineu.data.Dataset;
import guineu.desktop.Desktop;
import guineu.desktop.GuineuMenu;
import guineu.main.GuineuCore;
import guineu.main.GuineuModule;
import guineu.parameters.ParameterSet;
import guineu.taskcontrol.Task;
import guineu.taskcontrol.TaskListener;
import guineu.taskcontrol.TaskStatus;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.logging.Logger;

/**
 * 
 */
public class MeanCentering implements GuineuModule, TaskListener, ActionListener {

        private Logger logger = Logger.getLogger(this.getClass().getName());
        private Desktop desktop;

        public MeanCentering() {

                this.desktop = GuineuCore.getDesktop();

                desktop.addMenuItem(GuineuMenu.ALIGNMENT, "Mean centering..",
                        "Mean centering", KeyEvent.VK_M, this, null, null);
        }

        public String toString() {
                return "Mean centering";
        }

        public ParameterSet getParameterSet() {
                return null;
        }

        public void actionPerformed(ActionEvent e) {

                Dataset[] peakLists = desktop.getSelectedDataFiles();

                if (peakLists.length == 0) {
                        desktop.displayErrorMessage("Please select peak lists for Mean centering");
                        return;
                }

                runModule(null, peakLists, null);

        }

        public Task[] runModule(Dataset[] dataFiles, Dataset[] peakLists,
                ParameterSet parameters) {

                // check peak lists
                if ((peakLists == null) || (peakLists.length == 0)) {
                        desktop.displayErrorMessage("Please select peak lists for Mean centering");
                        return null;
                }

                // prepare a new group with just one task
                Task task = new MeanCenteringTask(peakLists);

                GuineuCore.getTaskController().addTask(task);

                return new Task[]{task};

        }

        public void taskStarted(Task task) {
                logger.info("Running Mean centering");
        }

        public void taskFinished(Task task) {
                if (task.getStatus() == TaskStatus.FINISHED) {
                        logger.info("Finished Mean centering on " + ((MeanCenteringTask) task).getTaskDescription());
                }

                if (task.getStatus() == TaskStatus.ERROR) {

                        String msg = "Error while Mean centering on .. " + ((MeanCenteringTask) task).getErrorMessage();
                        logger.severe(msg);
                        desktop.displayErrorMessage(msg);

                }
        }
}
