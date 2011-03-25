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
package guineu.modules.filter.report.intensitiesReport;

import guineu.data.Dataset;
import guineu.desktop.Desktop;
import guineu.desktop.GuineuMenu;
import guineu.main.GuineuCore;
import guineu.main.GuineuModule;
import guineu.parameters.ParameterSet;
import guineu.parameters.SimpleParameterSet;
import guineu.taskcontrol.Task;
import guineu.taskcontrol.TaskStatus;
import guineu.taskcontrol.TaskListener;
import guineu.util.dialogs.ExitCode;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.logging.Logger;

/**
 *
 * @author scsandra
 */
public class Report implements GuineuModule, TaskListener, ActionListener {

    private Logger logger = Logger.getLogger(this.getClass().getName());
    private Desktop desktop;
    private SimpleParameterSet parameters;

    public Report() {
        this.parameters = new ReportParameters();
        this.desktop = GuineuCore.getDesktop();
        desktop.addMenuItem(GuineuMenu.REPORT, "Report intesities..",
                "Report intesities", KeyEvent.VK_I, this, null, null);

    }

    public void taskStarted(Task task) {
        logger.info("Running Report");
    }

    public void taskFinished(Task task) {
        if (task.getStatus() == TaskStatus.FINISHED) {
            logger.info("Finished Report on " + ((ReportTask) task).getTaskDescription());
        }

        if (task.getStatus() == TaskStatus.ERROR) {

            String msg = "Error while Report on .. " + ((ReportTask) task).getErrorMessage();
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
        return "Report";
    }

    public Task[] runModule() {

        // prepare a new group of tasks
        Dataset[] datasets = desktop.getSelectedDataFiles();
        Task tasks[] = new ReportTask[datasets.length];
        for (int i = 0; i < datasets.length; i++) {
            tasks[i] = new ReportTask(datasets[i], parameters);
        }
        GuineuCore.getTaskController().addTasks(tasks);

        return tasks;

    }
}
