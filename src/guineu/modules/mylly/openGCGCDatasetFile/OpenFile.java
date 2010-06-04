/*
 * Copyright 2007-2008 VTT Biotechnology
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

import guineu.data.ParameterSet;
import guineu.data.impl.SimpleParameterSet;
import guineu.desktop.Desktop;
import guineu.desktop.GuineuMenu;
import guineu.desktop.impl.DesktopParameters;
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
import java.io.File;
import java.util.logging.Logger;

/**
 *
 * @author scsandra
 */
public class OpenFile implements GuineuModule, TaskListener, ActionListener {

    private Logger logger = Logger.getLogger(this.getClass().getName());
    private Desktop desktop;   
    private SimpleParameterSet parameters;

    public void initModule() {
        parameters = new OpenFileParameters();
        this.desktop = GuineuCore.getDesktop();
        desktop.addMenuItem(GuineuMenu.MYLLY, "Open GCGC Aligned Dataset..",
                "TODO write description", KeyEvent.VK_D, this, null, "icons/spectrumicon.png");
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


    public ExitCode setupParameters() {
        try {
            ParameterSetupDialog dialog = new ParameterSetupDialog("LCMS Table View parameters", parameters);
            dialog.setVisible(true);
            return dialog.getExitCode();
        } catch (Exception exception) {
            return ExitCode.CANCEL;
        }
    }

    public void actionPerformed(ActionEvent e) {
        ExitCode exitCode = setupParameters();
        if (exitCode != ExitCode.OK) {
            return;
        }

        runModule();
    }

  /*  public ExitCode setupParameters() {
        DesktopParameters deskParameters = (DesktopParameters) GuineuCore.getDesktop().getParameterSet();
        String lastPath = deskParameters.getLastOpenProjectPath();
        if (lastPath == null) {
            lastPath = "";
        }
        File lastFilePath = new File(lastPath);
        DatasetOpenDialog dialog = new DatasetOpenDialog(lastFilePath);
        dialog.setVisible(true);
        try {
            this.FilePath = dialog.getCurrentDirectory();
        } catch (Exception e) {
        }
        return ExitCode.OK;
    }*/

    public ParameterSet getParameterSet() {
         return parameters;
    }

    public void setParameters(ParameterSet parameterValues) {
        parameters = (SimpleParameterSet) parameterValues;
    }

    public String toString() {
        return "Open File";
    }

    public Task[] runModule() {
        String path = (String) parameters.getParameterValue(OpenFileParameters.fileName);
        int numColumns = (Integer) parameters.getParameterValue(OpenFileParameters.numColumns);
        // prepare a new group of tasks
        if (path != null) {
            Task tasks[] = new OpenFileTask[1];
            tasks[0] = new OpenFileTask(path, numColumns, desktop);

            GuineuCore.getTaskController().addTasks(tasks);

            return tasks;

        } else {
            return null;
        }

    }
}
