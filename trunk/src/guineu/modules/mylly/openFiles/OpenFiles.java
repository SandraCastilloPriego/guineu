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
package guineu.modules.mylly.openFiles;

import guineu.desktop.Desktop;
import guineu.desktop.GuineuMenu;
import guineu.main.GuineuCore;
import guineu.main.GuineuModule;
import guineu.parameters.ParameterSet;
import guineu.taskcontrol.Task;
import guineu.taskcontrol.TaskStatus;

import guineu.taskcontrol.TaskListener;
import guineu.util.GUIUtils;
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
public class OpenFiles implements GuineuModule, TaskListener, ActionListener {

        private Logger logger = Logger.getLogger(this.getClass().getName());
        private Desktop desktop;
        private OpenGCGCFileParameters parameters;
        final String helpID = GUIUtils.generateHelpID(this);

        public OpenFiles() {

                this.desktop = GuineuCore.getDesktop();
                desktop.addMenuItem(GuineuMenu.FILE, "Open GCGC Files..",
                        "Open single peak list in CVS format", KeyEvent.VK_G, this, null, "icons/pickedpeakicon.png");
                desktop.addMenuSeparator(GuineuMenu.FILE);
                parameters = new OpenGCGCFileParameters();
        }

        public void taskStarted(Task task) {
                logger.info("Running Open GCGC Files");
        }

        public void taskFinished(Task task) {
                if (task.getStatus() == TaskStatus.FINISHED) {
                        logger.info("Finished open GCGC files on " + ((OpenFileTask) task).getTaskDescription());
                }

                if (task.getStatus() == TaskStatus.ERROR) {

                        String msg = "Error while open GCGC files on .. " + ((OpenFileTask) task).getErrorMessage();
                        logger.severe(msg);
                        desktop.displayErrorMessage(msg);

                }
        }

        public void actionPerformed(ActionEvent e) {
                try {
                        ExitCode exitCode = parameters.showSetupDialog();
                        if (exitCode != ExitCode.OK) {
                                return;
                        }

                        runModule();
                } catch (Exception exception) {
                }
        }

        public ParameterSet getParameterSet() {
                return parameters;
        }

        public String toString() {
                return "Open File";
        }

        public Task[] runModule() {
                File[] fileNames = parameters.getParameter(OpenGCGCFileParameters.fileNames).getValue();
                String separator = parameters.getParameter(OpenGCGCFileParameters.separator).getValue();
                boolean filterClassified = parameters.getParameter(OpenGCGCFileParameters.filterClassified).getValue();
                // prepare a new group of tasks
                if (fileNames != null) {

                        Task tasks[] = new OpenFileTask[fileNames.length];
                        for (int i = 0; i < fileNames.length; i++) {
                                tasks[i] = new OpenFileTask(fileNames[i], separator, filterClassified);
                        }
                        GuineuCore.getTaskController().addTasks(tasks);

                        return tasks;

                } else {
                        return null;
                }

        }
}
