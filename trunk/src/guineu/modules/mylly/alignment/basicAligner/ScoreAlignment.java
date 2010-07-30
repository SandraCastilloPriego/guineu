/*
 * Copyright 2007-2010 VTT Biotechnology
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
package guineu.modules.mylly.alignment.basicAligner;

import guineu.data.ParameterSet;
import guineu.desktop.Desktop;
import guineu.desktop.GuineuMenu;
import guineu.main.GuineuCore;
import guineu.main.GuineuModule;
import guineu.taskcontrol.Task;
import guineu.taskcontrol.TaskStatus;

import guineu.taskcontrol.TaskListener;
import guineu.util.dialogs.ExitCode;
import guineu.util.dialogs.ParameterSetupDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.logging.Logger;
import guineu.data.Dataset;
import guineu.util.GUIUtils;

/**
 *
 * @author scsandra
 */
public class ScoreAlignment implements GuineuModule, TaskListener, ActionListener {

        private Logger logger = Logger.getLogger(this.getClass().getName());
        private Desktop desktop;
        private BasicAlignerGCGCParameters parameters;
        final String helpID = GUIUtils.generateHelpID(this);

        public void initModule() {
                parameters = new BasicAlignerGCGCParameters();
                this.desktop = GuineuCore.getDesktop();
                desktop.addMenuItem(GuineuMenu.MYLLY, "Ransac Alignment..",
                        "TODO write description", KeyEvent.VK_S, this, null, "icons/alignment.png");

        }

        public void taskStarted(Task task) {
                logger.info("Running Ransac Alignment");
        }

        public void taskFinished(Task task) {
                if (task.getStatus() == TaskStatus.FINISHED) {
                        logger.info("Finished Score Alignment on " + ((BasicAlignerGCGCTask) task).getTaskDescription());
                }

                if (task.getStatus() == TaskStatus.ERROR) {

                        String msg = "Error while Score Alignment on .. " + ((BasicAlignerGCGCTask) task).getErrorMessage();
                        logger.severe(msg);
                        desktop.displayErrorMessage(msg);

                }
        }

        public void actionPerformed(ActionEvent e) {
                try {
                        setupParameters(parameters);
                } catch (Exception exception) {
                }
        }

        public void setupParameters(ParameterSet currentParameters) {
                final ParameterSetupDialog dialog = new ParameterSetupDialog(
                        "Please set parameter values for " + toString(),
                        (BasicAlignerGCGCParameters) currentParameters, helpID);
                dialog.setVisible(true);


                if (dialog.getExitCode() == ExitCode.OK) {
                        runModule();
                }
        }

        public ParameterSet getParameterSet() {
                return this.parameters;
        }

        public void setParameters(ParameterSet parameterValues) {
                parameters = (BasicAlignerGCGCParameters) parameters;
        }

        @Override
        public String toString() {
                return "Score Alignment";
        }

        public Task[] runModule() {
                // prepare a new group of tasks
                Task tasks[] = new BasicAlignerGCGCTask[1];
                Dataset[] datasets = desktop.getSelectedDataFiles();

                tasks[0] = new BasicAlignerGCGCTask(datasets, parameters);

                GuineuCore.getTaskController().addTasks(tasks);

                return tasks;



        }
}
