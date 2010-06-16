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
package guineu.modules.identification.CustomIdentification;

import guineu.data.Dataset;
import guineu.data.ParameterSet;
import guineu.data.impl.SimpleParameterSet;
import guineu.desktop.Desktop;
import guineu.desktop.GuineuMenu;
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
import java.util.logging.Logger;

/**
 * 
 */
public class CustomDBSearch implements ActionListener, GuineuModule, TaskListener {

    private Logger logger = Logger.getLogger(this.getClass().getName());
    public static final String MODULE_NAME = "Custom database search";
    private Desktop desktop;
    private CustomDBSearchParameters parameters;

    public void initModule() {

        this.desktop = GuineuCore.getDesktop();

        parameters = new CustomDBSearchParameters();

        desktop.addMenuItem(GuineuMenu.LCMSIDENTIFICATIONSUBMENU, "Identification by searching in CSV file",
                "TODO write description",
                KeyEvent.VK_C, this, null, null);
    }

    public ParameterSet getParameterSet() {
        return parameters;
    }

    public void setParameters(ParameterSet parameterValues) {
        this.parameters = (CustomDBSearchParameters) parameterValues;
    }

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {

        Dataset[] selectedPeakLists = desktop.getSelectedDataFiles();

        if (selectedPeakLists.length < 1) {
            desktop.displayErrorMessage("Please select a peak list");
            return;
        }

        ExitCode exitCode = setupParameters(parameters);
        if (exitCode != ExitCode.OK) {
            return;
        }

        runModule(selectedPeakLists, parameters.clone());

    }

     public Task[] runModule(Dataset[] peakLists,
            ParameterSet parameters) {

        if (peakLists == null) {
            throw new IllegalArgumentException(
                    "Cannot run identification without a peak list");
        }
        // prepare a new sequence of tasks
        Task tasks[] = new CustomDBSearchTask[peakLists.length];
        for (int i = 0; i < peakLists.length; i++) {
            tasks[i] = new CustomDBSearchTask(peakLists[i],
                    (CustomDBSearchParameters) parameters);
        }
        GuineuCore.getTaskController().addTasks(tasks);

        return tasks;

    }

    public ExitCode setupParameters(ParameterSet parameters) {
        ParameterSetupDialog dialog = new ParameterSetupDialog(
                "Please set parameter values for " + toString(),
                (SimpleParameterSet) parameters);
        dialog.setVisible(true);
        return dialog.getExitCode();
    }

    public String toString() {
        return MODULE_NAME;
    }

    public void taskStarted(Task task) {
        logger.info("Running identification");
    }

    public void taskFinished(Task task) {
        if (task.getStatus() == TaskStatus.FINISHED) {
            logger.info("Finished Transpose Dataset on " + ((CustomDBSearchTask) task).getTaskDescription());
        }

        if (task.getStatus() == TaskStatus.ERROR) {

            String msg = "Error while Transpose Dataset on .. " + ((CustomDBSearchTask) task).getErrorMessage();
            logger.severe(msg);
            desktop.displayErrorMessage(msg);

        }
    }
}
