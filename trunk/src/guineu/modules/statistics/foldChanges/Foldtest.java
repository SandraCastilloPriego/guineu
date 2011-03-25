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
package guineu.modules.statistics.foldChanges;

import guineu.data.Dataset;
import guineu.desktop.Desktop;
import guineu.desktop.GuineuMenu;
import guineu.main.GuineuCore;
import guineu.main.GuineuModule;
import guineu.parameters.ParameterSet;
import guineu.taskcontrol.Task;
import guineu.taskcontrol.TaskStatus;

import guineu.taskcontrol.TaskListener;
import guineu.util.dialogs.ExitCode;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.logging.Logger;

/**
 *
 * @author scsandra
 */
public class Foldtest implements GuineuModule, TaskListener, ActionListener {

        private Logger logger = Logger.getLogger(this.getClass().getName());
        private Desktop desktop;
        private Dataset dataset;
        private String[] group1, group2;
        private String parameter;

        public Foldtest() {

                this.desktop = GuineuCore.getDesktop();
                desktop.addMenuItem(GuineuMenu.STATISTICS, "Fold Test..",
                        "Fold test using the means", KeyEvent.VK_F, this, null, "icons/fold.png");

        }

        public void taskStarted(Task task) {
                logger.info("Running Fold Test");
        }

        public void taskFinished(Task task) {
                if (task.getStatus() == TaskStatus.FINISHED) {
                        logger.info("Finished fold Test on " + ((FoldTestTask) task).getTaskDescription());
                }

                if (task.getStatus() == TaskStatus.ERROR) {

                        String msg = "Error while Fold Test on .. " + ((FoldTestTask) task).getErrorMessage();
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
                        FoldtestDataDialog dialog = new FoldtestDataDialog(dataset);
                        dialog.setVisible(true);
                        group1 = dialog.getGroup1();
                        group2 = dialog.getGroup2();
                        parameter = dialog.getParameter();
                        return dialog.getExitCode();
                } catch (Exception exception) {
                        return ExitCode.CANCEL;
                }
        }

        public ParameterSet getParameterSet() {
                return null;
        }

        public String toString() {
                return "Fold Test";
        }

        public Task[] runModule() {

                // prepare a new group of tasks

                Task tasks[] = new FoldTestTask[1];
                tasks[0] = new FoldTestTask(group1, group2, dataset, desktop, parameter);

                GuineuCore.getTaskController().addTasks(tasks);

                return tasks;



        }
}
