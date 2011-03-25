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
package guineu.modules.mylly.filter.pubChem;

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
public class PubChemFilter implements GuineuModule, TaskListener, ActionListener {

        private Logger logger = Logger.getLogger(this.getClass().getName());
        private Desktop desktop;
        private PubChemParameters parameters;
        final String helpID = GUIUtils.generateHelpID(this);

        public PubChemFilter() {
                parameters = new PubChemParameters();
                this.desktop = GuineuCore.getDesktop();
                desktop.addMenuItem(GuineuMenu.GCGCIDENTIFICATIONSUBMENU, "PubChem ID Filter..",
                        "Addition of the PubChem ID to the compounds present in a file created by the user. ", KeyEvent.VK_P, this, null, null);

        }

        public void taskStarted(Task task) {
                logger.info("PubChem ID Filter");
        }

        public void taskFinished(Task task) {
                if (task.getStatus() == TaskStatus.FINISHED) {
                        logger.info("Finished PubChem ID Filter ");
                }

                if (task.getStatus() == TaskStatus.ERROR) {

                        String msg = "Error while PubChem ID Filtering .. ";
                        logger.severe(msg);
                        desktop.displayErrorMessage(msg);

                }
        }

        public void actionPerformed(ActionEvent e) {
                try {
                        ExitCode exitcode = parameters.showSetupDialog();
                        if (exitcode == ExitCode.OK) {
                                runModule();
                        }
                } catch (Exception exception) {
                }
        }


        public ParameterSet getParameterSet() {
                return this.parameters;
        }

        public String toString() {
                return "PubChem ID Filter";
        }

        public Task[] runModule() {

                Dataset[] datasets = desktop.getSelectedDataFiles();


                // prepare a new group of tasks
                Task tasks[] = new PubChemFilterTask[datasets.length];
                for (int i = 0; i < datasets.length; i++) {
                        tasks[i] = new PubChemFilterTask(datasets[i], parameters);
                }
                GuineuCore.getTaskController().addTasks(tasks);

                return tasks;


        }
}
