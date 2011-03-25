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
package guineu.modules.mylly.filter.NameFilter;

import guineu.desktop.Desktop;
import guineu.desktop.GuineuMenu;
import guineu.main.GuineuCore;
import guineu.main.GuineuModule;
import guineu.taskcontrol.Task;
import guineu.taskcontrol.TaskStatus;
import guineu.taskcontrol.TaskListener;
import guineu.util.dialogs.ExitCode;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.logging.Logger;
import guineu.data.Dataset;
import guineu.parameters.ParameterSet;
import guineu.util.GUIUtils;

/**
 *
 * @author scsandra
 */
public class NameFilter implements GuineuModule, TaskListener, ActionListener {

        private Logger logger = Logger.getLogger(this.getClass().getName());
        private Desktop desktop;
        private NameFilterParameters parameters;
        final String helpID = GUIUtils.generateHelpID(this);

        public NameFilter() {
                parameters = new NameFilterParameters();
                this.desktop = GuineuCore.getDesktop();
                desktop.addMenuSeparator(GuineuMenu.MYLLY);
                desktop.addMenuItem(GuineuMenu.MYLLY, "Name Filter..",
                        "Filter basen on compound names", KeyEvent.VK_O, this, null, null);

        }

        public void taskStarted(Task task) {
                logger.info("Running Name Filter");
        }

        public void taskFinished(Task task) {
                if (task.getStatus() == TaskStatus.FINISHED) {
                        logger.info("Finished Name Filter ");
                }

                if (task.getStatus() == TaskStatus.ERROR) {

                        String msg = "Error while Name Filtering .. ";
                        logger.severe(msg);
                        desktop.displayErrorMessage(msg);

                }
        }

        public void actionPerformed(ActionEvent e) {
                try {
                        ExitCode exitCode = parameters.showSetupDialog();
                        if (exitCode == ExitCode.OK) {
                                runModule();
                        }
                } catch (Exception exception) {
                }
        }

        public ParameterSet getParameterSet() {
                return this.parameters;
        }

        public String toString() {
                return "Name Filter";
        }

        public Task[] runModule() {

                Dataset[] AlignmentFiles = desktop.getSelectedDataFiles();


                // prepare a new group of tasks
                Task tasks[] = new NameFilterTask[1];

                tasks[0] = new NameFilterTask(AlignmentFiles, parameters);

                GuineuCore.getTaskController().addTasks(tasks);

                return tasks;


        }
}
