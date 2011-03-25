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
package guineu.modules.identification.linearnormalization;

import guineu.data.Dataset;
import guineu.desktop.Desktop;
import guineu.desktop.GuineuMenu;
import guineu.main.GuineuCore;
import guineu.main.GuineuModule;
import guineu.parameters.ParameterSet;
import guineu.taskcontrol.Task;
import guineu.taskcontrol.TaskListener;
import guineu.taskcontrol.TaskStatus;
import guineu.util.GUIUtils;
import guineu.util.dialogs.ExitCode;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.logging.Logger;

/**
 * @author Taken from MZmine2
 * http://mzmine.sourceforge.net/
 */
public class LinearNormalizer implements GuineuModule, TaskListener,
        ActionListener {

        private Logger logger = Logger.getLogger(this.getClass().getName());
        final String helpID = GUIUtils.generateHelpID(this);
        public static final String MODULE_NAME = "Linear normalizer";
        private LinearNormalizerParameters parameters;
        private Desktop desktop;

        public LinearNormalizer() {

                this.desktop = GuineuCore.getDesktop();

                parameters = new LinearNormalizerParameters();

                desktop.addMenuItem(GuineuMenu.NORMALIZATION, "Linear Normalization Filter..",
                        "Peak list normalization using linear coefficients", KeyEvent.VK_S, this, null, null);


        }

        public String toString() {
                return MODULE_NAME;
        }

        public ParameterSet getParameterSet() {
                return parameters;
        }
       
        public void actionPerformed(ActionEvent e) {

                Dataset[] selectedPeakLists = desktop.getSelectedDataFiles();

                // check peak lists
                if ((selectedPeakLists == null) || (selectedPeakLists.length == 0)) {
                        desktop.displayErrorMessage("Please select peak lists for normalization");
                        return;
                }

                ExitCode exitCode = parameters.showSetupDialog();
                if (exitCode != ExitCode.OK) {
                        return;
                }

                runModule(selectedPeakLists, parameters.clone());

        }

        public Task[] runModule(Dataset[] peakLists,
                ParameterSet parameters) {

                // check peak lists
                if ((peakLists == null) || (peakLists.length == 0)) {
                        desktop.displayErrorMessage("Please select peak lists for normalization");
                        return null;
                }

                // prepare a new group of tasks
                Task tasks[] = new LinearNormalizerTask[peakLists.length];
                for (int i = 0; i < peakLists.length; i++) {
                        tasks[i] = new LinearNormalizerTask(peakLists[i],
                                (LinearNormalizerParameters) parameters);
                }

                GuineuCore.getTaskController().addTasks(tasks);

                return tasks;

        }

        public void taskStarted(Task task) {
                logger.info("Running Serum Normalization Filter");
        }

        public void taskFinished(Task task) {
                if (task.getStatus() == TaskStatus.FINISHED) {
                        logger.info("Finished Linear Normalization Filter on " + ((LinearNormalizerTask) task).getTaskDescription());
                }

                if (task.getStatus() == TaskStatus.ERROR) {

                        String msg = "Error while Serum Normalization Filter on .. " + ((LinearNormalizerTask) task).getErrorMessage();
                        logger.severe(msg);
                        desktop.displayErrorMessage(msg);

                }
        }
}
