/*
 * Copyright 2007-2013 VTT Biotechnology
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
package guineu.modules.mylly.filter.singlingFilter;

import guineu.desktop.Desktop;
import guineu.main.GuineuCore;
import guineu.data.impl.datasets.SimpleGCGCDataset;
import guineu.modules.GuineuModuleCategory;
import guineu.taskcontrol.Task;
import guineu.taskcontrol.TaskEvent;
import guineu.taskcontrol.TaskStatus;
import guineu.util.dialogs.ExitCode;
import java.awt.event.ActionEvent;
import guineu.data.Dataset;
import guineu.modules.GuineuProcessingModule;
import guineu.parameters.ParameterSet;
import guineu.util.GUIUtils;
import java.util.logging.Logger;

/**
 *
 * @author scsandra
 */
public class SinglingFilter implements GuineuProcessingModule {

        private Logger logger = Logger.getLogger(this.getClass().getName());
        private Desktop desktop;
        private SinglingParameters parameters;
        final String helpID = GUIUtils.generateHelpID(this);

       /* public SinglingFilter() {
                parameters = new SinglingParameters();
                this.desktop = GuineuCore.getDesktop();
                desktop.addMenuItem(GuineuMenu.MYLLY, "Leave Only Uniques Filter..",
                        "Certain compounds are removed from the peak list depending its similarity or if they are identified as unknown.", KeyEvent.VK_U, this, null, null);

        }*/

        public void taskStarted(Task task) {
                logger.info("Leave Only Uniques Filter");
        }

        public void taskFinished(Task task) {
                if (task.getStatus() == TaskStatus.FINISHED) {
                        logger.info("Finished Leave Only Uniques Filter ");
                }

                if (task.getStatus() == TaskStatus.ERROR) {

                        String msg = "Error while Leave Only Uniques Filtering .. ";
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
                return "Leave Only Uniques Filter";
        }

        public Task[] runModule() {

                Dataset[] DataFiles = desktop.getSelectedDataFiles();

                // prepare a new group of tasks
                Task tasks[] = new SinglingFilterTask[DataFiles.length];
                for (int cont = 0; cont < DataFiles.length; cont++) {
                        tasks[cont] = new SinglingFilterTask((SimpleGCGCDataset) DataFiles[cont], parameters);
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

        public String getIcon() {
                throw new UnsupportedOperationException("Not supported yet.");
        }

        public boolean setSeparator() {
                throw new UnsupportedOperationException("Not supported yet.");
        }
}
