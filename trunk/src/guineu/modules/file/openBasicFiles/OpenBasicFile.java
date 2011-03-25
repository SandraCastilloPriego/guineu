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
package guineu.modules.file.openBasicFiles;


import guineu.desktop.Desktop;
import guineu.desktop.GuineuMenu;
import guineu.main.GuineuCore;
import guineu.main.GuineuModule;
import guineu.parameters.ParameterSet;
import guineu.taskcontrol.Task;
import guineu.taskcontrol.TaskListener;
import guineu.taskcontrol.TaskStatus;
import guineu.util.dialogs.ExitCode;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.logging.Logger;

/**
 *
 * @author scsandra
 */
public class OpenBasicFile implements GuineuModule, TaskListener, ActionListener {

        private Logger logger = Logger.getLogger(this.getClass().getName());
        private Desktop desktop;
        private OpenBasicFileParameters parameters;

        public OpenBasicFile() {
                this.parameters = new OpenBasicFileParameters();
                this.desktop = GuineuCore.getDesktop();
                desktop.addMenuItem(GuineuMenu.FILE, "Open other Files..",
                        "Opens files with any structure considering all the columns as a sample column", KeyEvent.VK_O, this, null, "icons/others.png");

        }

        public void taskStarted(Task task) {
                logger.info("Running other Files");
        }

        public void taskFinished(Task task) {
                if (task.getStatus() == TaskStatus.FINISHED) {
                        logger.info("Finished other Files on " + ((OpenBasicFileTask) task).getTaskDescription());
                }

                if (task.getStatus() == TaskStatus.ERROR) {

                        String msg = "Error while other Files on .. " + ((OpenBasicFileTask) task).getErrorMessage();
                        logger.severe(msg);
                        desktop.displayErrorMessage(msg);

                }
        }

        public void actionPerformed(ActionEvent e) {
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
                return "Other Files";
        }

        public Task[] runModule() {
                File[] files = this.parameters.getParameter(OpenBasicFileParameters.fileNames).getValue();

                // prepare a new group of tasks
                Task tasks[] = new OpenBasicFileTask[files.length];
                for (int i = 0; i < files.length; i++) {
                        tasks[i] = new OpenBasicFileTask(files[i].getAbsolutePath(), desktop);
                }
                GuineuCore.getTaskController().addTasks(tasks);

                return tasks;
        }
}
