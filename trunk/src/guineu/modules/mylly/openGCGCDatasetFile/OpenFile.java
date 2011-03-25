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
package guineu.modules.mylly.openGCGCDatasetFile;

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
import java.util.logging.Logger;

/**
 *
 * @author scsandra
 */
public class OpenFile implements GuineuModule, TaskListener, ActionListener {

    private Logger logger = Logger.getLogger(this.getClass().getName());
    private Desktop desktop;
    private OpenFileParameters parameters;
    final String helpID = GUIUtils.generateHelpID(this);

    public OpenFile() {
        parameters = new OpenFileParameters();
        this.desktop = GuineuCore.getDesktop();
        desktop.addMenuSeparator(GuineuMenu.FILE);
        desktop.addMenuItem(GuineuMenu.FILE, "Open GCGC Multiple Samples File..",
                "Open multiple aligned peak list in CVS or Excel format", KeyEvent.VK_D, this, null, "icons/spectrumicon.png");
    }

    public void taskStarted(Task task) {
        logger.info("Running Open File");
    }

    public void taskFinished(Task task) {
        if (task.getStatus() == TaskStatus.FINISHED) {
            logger.info("Finished open file on " + ((OpenFileTask) task).getTaskDescription());
        }

        if (task.getStatus() == TaskStatus.ERROR) {

            String msg = "Error while open file on .. " + ((OpenFileTask) task).getErrorMessage();
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
        return "Open File";
    }

    public Task[] runModule() {
        String path = (String) parameters.getParameter(OpenFileParameters.fileName).getValue().getAbsolutePath();
        int numColumns = (Integer) parameters.getParameter(OpenFileParameters.numColumns).getValue().intValue();
        // prepare a new group of tasks
        if (path != null) {
            Task tasks[] = new OpenFileTask[1];
            tasks[0] = new OpenFileTask(path, numColumns);

            GuineuCore.getTaskController().addTasks(tasks);

            return tasks;

        } else {
            return null;
        }

    }
}
