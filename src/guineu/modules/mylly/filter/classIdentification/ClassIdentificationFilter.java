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
package guineu.modules.mylly.filter.classIdentification;

import guineu.desktop.Desktop;
import guineu.main.GuineuCore;
import guineu.modules.GuineuModuleCategory;
import guineu.taskcontrol.Task;
import guineu.taskcontrol.TaskEvent;
import guineu.taskcontrol.TaskStatus;

import guineu.util.dialogs.ExitCode;
import java.awt.event.ActionEvent;
import java.util.logging.Logger;
import guineu.data.Dataset;
import guineu.modules.GuineuProcessingModule;
import guineu.parameters.ParameterSet;
import guineu.util.GUIUtils;

/**
 *
 * @author scsandra
 */
public class ClassIdentificationFilter implements GuineuProcessingModule {

        private Logger logger = Logger.getLogger(this.getClass().getName());
        private Desktop desktop;
        private ClassIdentificationParameters parameters;
        final String helpID = GUIUtils.generateHelpID(this);

       /* public ClassIdentificationFilter() {
                parameters = new ClassIdentificationParameters();
                this.desktop = GuineuCore.getDesktop();
                desktop.addMenuItem(GuineuMenu.GCGCIDENTIFICATIONSUBMENU, "Class Identification Filter..",
                        "Identification of the class of the compounds based on a list of rules that the user has to provide.", KeyEvent.VK_C, this, null, null);

        }*/

        public void taskStarted(Task task) {
                logger.info("Class Identification Filter");
        }

        public void taskFinished(Task task) {
                if (task.getStatus() == TaskStatus.FINISHED) {
                        logger.info("Finished Class Identification Filter ");
                }

                if (task.getStatus() == TaskStatus.ERROR) {

                        String msg = "Error while Class Identification Filtering .. ";
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
                return "Class Identification Filter";
        }

        public Task[] runModule() {

                Dataset[] datasets = desktop.getSelectedDataFiles();


                // prepare a new group of tasks
                Task tasks[] = new ClassIdentificationFilterTask[datasets.length];
                for (int i = 0; i < datasets.length; i++) {
                        tasks[i] = new ClassIdentificationFilterTask(datasets[i], parameters);
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
}
