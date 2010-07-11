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
package guineu.modules.statistics.anova;

import guineu.data.ParameterSet;
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
import guineu.data.Dataset;
import guineu.util.GUIUtils;
import java.util.logging.Logger;

/**
 *
 * @author scsandra
 */
public class AnovaTest implements GuineuModule, TaskListener, ActionListener {

    private Logger logger = Logger.getLogger(this.getClass().getName());
    private Desktop desktop;
    private AnovaParameters parameters;
    final String helpID = GUIUtils.generateHelpID(this);

    public void initModule() {
        this.desktop = GuineuCore.getDesktop();
        desktop.addMenuItem(GuineuMenu.STATISTICS, "Anova Test..",
                "Anova statistical test", KeyEvent.VK_U, this, null, null);

    }

    public void taskStarted(Task task) {
        logger.info("Anova Test");
    }

    public void taskFinished(Task task) {
        if (task.getStatus() == TaskStatus.FINISHED) {
            logger.info("Anova Test ");
        }

        if (task.getStatus() == TaskStatus.ERROR) {

            String msg = "Error while Anova Test .. ";
            logger.severe(msg);
            desktop.displayErrorMessage(msg);

        }
    }

    public void actionPerformed(ActionEvent e) {
        try {
            Dataset[] DataFiles = desktop.getSelectedDataFiles();
            if (DataFiles != null && DataFiles[0].getParametersName().size() > 0) {
                parameters = new AnovaParameters();
                setupParameters(parameters);
            }else{
                
            }
        } catch (Exception exception) {
        }
    }

    public void setupParameters(ParameterSet currentParameters) {
        final ParameterSetupDialog dialog = new ParameterSetupDialog(
                "Please set parameter values for " + toString(),
                (AnovaParameters) currentParameters, helpID);
        dialog.setVisible(true);

        if (dialog.getExitCode() == ExitCode.OK) {
            runModule();
        }
    }

    public ParameterSet getParameterSet() {
        return this.parameters;
    }

    public void setParameters(ParameterSet parameterValues) {
        parameters = (AnovaParameters) parameters;
    }

    public String toString() {
        return "Anova Test";
    }

    public Task[] runModule() {

        Dataset[] DataFiles = desktop.getSelectedDataFiles();
        // prepare a new group of tasks
        Task tasks[] = new AnovaTestTask[1];
        tasks[0] = new AnovaTestTask(DataFiles[0], parameters);
        GuineuCore.getTaskController().addTasks(tasks);

        return tasks;


    }
}