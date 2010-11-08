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
package guineu.modules.mylly.filter.ConcentrationsFromMass;

import guineu.data.ParameterSet;
import guineu.desktop.Desktop;
import guineu.desktop.GuineuMenu;
import guineu.main.GuineuCore;
import guineu.main.GuineuModule;
import guineu.taskcontrol.Task;
import guineu.taskcontrol.TaskStatus;

import guineu.taskcontrol.TaskListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.logging.Logger;
import guineu.data.Dataset;
import guineu.data.impl.datasets.SimpleGCGCDataset;
import guineu.util.dialogs.ExitCode;
import guineu.util.dialogs.ParameterSetupDialog;

/**
 *
 * @author scsandra
 */
public class ConcentrationsFromMassFilter implements GuineuModule, TaskListener, ActionListener {

    private Logger logger = Logger.getLogger(this.getClass().getName());
    private Desktop desktop;
    private ConcentrationsFromMassParameters parameters;

    public void initModule() {
        this.desktop = GuineuCore.getDesktop();
        desktop.addMenuItem(GuineuMenu.MYLLY, "Recalculate intensities..",
                "Recalculation of the intensities of peaks with mass information.", KeyEvent.VK_E, this, null, null);
        this.parameters = new ConcentrationsFromMassParameters();
    }

    public void taskStarted(Task task) {
        logger.info("Recalculating intensities");
    }

    public void taskFinished(Task task) {
        if (task.getStatus() == TaskStatus.FINISHED) {
            logger.info("Finished Recalculating intensities ");
        }

        if (task.getStatus() == TaskStatus.ERROR) {

            String msg = "Error while Recalculating intensities .. ";
            logger.severe(msg);
            desktop.displayErrorMessage(msg);

        }
    }

    public void actionPerformed(ActionEvent e) {
        try {
            setupParameters(parameters);
        } catch (Exception exception) {
        }
    }

    public void setupParameters(ParameterSet currentParameters) {
        final ParameterSetupDialog dialog = new ParameterSetupDialog(
                "Please set parameter values for " + toString(),
                (ConcentrationsFromMassParameters) currentParameters);
        dialog.setVisible(true);

        if (dialog.getExitCode() == ExitCode.OK) {
            runModule();
        }
    }

    public ParameterSet getParameterSet() {
        return this.parameters;
    }

    public void setParameters(ParameterSet parameterValues) {
        this.parameters = (ConcentrationsFromMassParameters) parameterValues;
    }

    @Override
    public String toString() {
        return "Recalculating intensities";
    }

    public Task[] runModule() {

        Dataset[] DataFiles = desktop.getSelectedDataFiles();

        // prepare a new group of tasks
        Task tasks[] = new ConcentrationsFromMassFilterTask[DataFiles.length];
        for (int cont = 0; cont < DataFiles.length; cont++) {
            tasks[cont] = new ConcentrationsFromMassFilterTask((SimpleGCGCDataset) DataFiles[cont], parameters);
        }
        GuineuCore.getTaskController().addTasks(tasks);

        return tasks;



    }
}
