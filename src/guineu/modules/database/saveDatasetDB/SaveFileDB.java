/*
 * Copyright 2007-2010 VTT Biotechnology
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
package guineu.modules.database.saveDatasetDB;

import guineu.data.Dataset;
import guineu.data.ParameterSet;
import guineu.desktop.Desktop;
import guineu.desktop.GuineuMenu;
import guineu.main.GuineuCore;
import guineu.main.GuineuModule;
import guineu.taskcontrol.Task;
import guineu.taskcontrol.TaskListener;
import guineu.taskcontrol.TaskStatus;
import guineu.util.dialogs.ExitCode;
import guineu.util.dialogs.ParameterSetupDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.logging.Logger;

/**
 *
 * @author scsandra
 */
public class SaveFileDB implements GuineuModule, TaskListener, ActionListener {

    private Logger logger = Logger.getLogger(this.getClass().getName());
    private Desktop desktop;
    private Dataset Dataset;
    private SaveFileParameters parameters;

    public void initModule() {
        this.desktop = GuineuCore.getDesktop();
        desktop.addMenuItem(GuineuMenu.DATABASE, "Save Dataset..",
                "Save Dataset into the internal database", KeyEvent.VK_I, this, null, null);
    }

    public void taskStarted(Task task) {
        logger.info("Running Save Dataset into Database");
    }

    public void taskFinished(Task task) {
        if (task.getStatus() == TaskStatus.FINISHED) {
            logger.info("Finished Save Dataset" + ((SaveFileDBTask) task).getTaskDescription());
        }

        if (task.getStatus() == TaskStatus.ERROR) {

            String msg = "Error while save Dataset on .. " + ((SaveFileDBTask) task).getErrorMessage();
            logger.severe(msg);
            desktop.displayErrorMessage(msg);

        }
    }

    public void setupParameters(ParameterSet currentParameters) {
        final ParameterSetupDialog dialog = new ParameterSetupDialog(
                "Please set parameter values for " + toString(),
                (SaveFileParameters) currentParameters);
        dialog.setVisible(true);

        if (dialog.getExitCode() == ExitCode.OK) {
            runModule();
        }
    }

    public ParameterSet getParameterSet() {
        return parameters;
    }

    public void setParameters(ParameterSet parameterValues) {
        parameters = (SaveFileParameters) parameterValues;
    }

    @Override
    public String toString() {
        return "Save Dataset";
    }

    public Task[] runModule() {

        // prepare a new group of tasks
        Task tasks[] = new SaveFileDBTask[1];

        tasks[0] = new SaveFileDBTask(Dataset, parameters);

        GuineuCore.getTaskController().addTasks(tasks);

        return tasks;

    }

    public void actionPerformed(ActionEvent e) {
        parameters = new SaveFileParameters();
        try {
            Dataset = desktop.getSelectedDataFiles()[0];

            if (Dataset.getDatasetName() != null) {
                parameters.setParameterValue(SaveFileParameters.name, Dataset.getDatasetName());
                setupParameters(parameters);
            }
        } catch (Exception exception) {
        }
    }
}

