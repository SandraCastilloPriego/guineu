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
package guineu.modules.file.saveGCGCFile;

import guineu.data.Dataset;
import guineu.data.ParameterSet;
import guineu.data.impl.SimpleParameterSet;
import guineu.desktop.Desktop;
import guineu.main.GuineuCore;
import guineu.main.GuineuModule;
import guineu.taskcontrol.Task;
import guineu.taskcontrol.TaskListener;
import guineu.taskcontrol.TaskStatus;
import guineu.util.dialogs.ExitCode;
import guineu.util.dialogs.ParameterSetupDialog;
import java.util.logging.Logger;

/**
 *
 * @author scsandra
 */
public class SaveGCGCFile implements GuineuModule, TaskListener {

    private Logger logger = Logger.getLogger(this.getClass().getName());
    private Desktop desktop;
    private Dataset[] Datasets;
    private SimpleParameterSet parameters;

    public SaveGCGCFile(Dataset[] Datasets) {
        this.Datasets = Datasets;
    }

    public void initModule() {
        ExitCode exitCode = setupParameters();
        if (exitCode != ExitCode.OK) {
            return;
        }
        runModule();
    }

    public void taskStarted(Task task) {
        logger.info("Running Save Dataset into Database");
    }

    public void taskFinished(Task task) {
        if (task.getStatus() == TaskStatus.FINISHED) {
            logger.info("Finished Save Dataset" + ((SaveGCGCFileTask) task).getTaskDescription());
        }

        if (task.getStatus() == TaskStatus.ERROR) {

            String msg = "Error while save Dataset on .. " + ((SaveGCGCFileTask) task).getErrorMessage();
            logger.severe(msg);
            desktop.displayErrorMessage(msg);

        }
    }

    public ExitCode setupParameters() {
        try {
            ParameterSetupDialog dialog = new ParameterSetupDialog("GCGC Table View parameters", parameters);
            dialog.setVisible(true);
            return dialog.getExitCode();
        } catch (Exception exception) {
            return ExitCode.CANCEL;
        }
    }

    public ParameterSet getParameterSet() {
        return parameters;
    }

    public void setParameters(ParameterSet parameterValues) {
        parameters = (SimpleParameterSet) parameterValues;
    }

    @Override
    public String toString() {
        return "Save Dataset";
    }

    public Task[] runModule() {

        // prepare a new group of tasks
        String path = (String) parameters.getParameterValue(SaveGCGCParameters.GCGCfilename);
        Task tasks[] = new SaveGCGCFileTask[Datasets.length];
        for (int i = 0; i < Datasets.length; i++) {
            String newpath = path;
            if (i > 0) {
                newpath = path.substring(0, path.length() - 4) + String.valueOf(i) + path.substring(path.length() - 4);
            }
            tasks[i] = new SaveGCGCFileTask(Datasets[i], parameters, newpath);
        }

        GuineuCore.getTaskController().addTasks(tasks);

        return tasks;

    }
}
