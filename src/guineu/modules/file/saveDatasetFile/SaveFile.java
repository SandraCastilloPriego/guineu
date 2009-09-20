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
package guineu.modules.file.saveDatasetFile;

import guineu.data.Dataset;
import guineu.data.ParameterSet;
import guineu.data.datamodels.GCGCColumnName;
import guineu.desktop.Desktop;
import guineu.main.GuineuModule;
import guineu.taskcontrol.Task;
import guineu.taskcontrol.TaskGroup;
import guineu.taskcontrol.TaskGroupListener;
import guineu.taskcontrol.TaskListener;
import guineu.util.dialogs.ExitCode;
import guineu.util.dialogs.ParameterSetupDialog;
import java.util.logging.Logger;

/**
 *
 * @author scsandra
 */
public class SaveFile implements GuineuModule, TaskListener  {

    private Logger logger = Logger.getLogger(this.getClass().getName());
    private Desktop desktop;
    private Dataset[] Datasets;  
    private SaveDatasetParameters parameters;

    public SaveFile(Dataset[] Datasets) {
        this.Datasets = Datasets;
    }

    public void initModule() {      
        parameters = new SaveDatasetParameters();
        System.out.println(this.Datasets[0].getClass().toString());
        if(this.Datasets[0].getClass().toString().contains("SimpleGCGCDataset")){
            parameters.setMultipleSelection(SaveDatasetParameters.exportItemMultipleSelection, GCGCColumnName.values());
        }else if(!this.Datasets[0].getClass().toString().contains("SimpleLCMSDataset")){
            Object[] object = new Object[1];
            object[0] = "No information";
            parameters.setMultipleSelection(SaveDatasetParameters.exportItemMultipleSelection, object);
        }

         ExitCode exitCode = setupParameters();
        if (exitCode != ExitCode.OK) {
            return;
        }
        runModule(null);
    }

    public void taskStarted(Task task) {
        logger.info("Running Save Dataset into Database");
    }

    public void taskFinished(Task task) {
        if (task.getStatus() == Task.TaskStatus.FINISHED) {
            logger.info("Finished Save Dataset" + ((SaveFileTask) task).getTaskDescription());
        }

        if (task.getStatus() == Task.TaskStatus.ERROR) {

            String msg = "Error while save Dataset on .. " + ((SaveFileTask) task).getErrorMessage();
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

    public ParameterSet getParameterSet() {
        return parameters;
    }

    public void setParameters(ParameterSet parameterValues) {
        parameters = (SaveDatasetParameters) parameterValues;
    }

    @Override
    public String toString() {
        return "Save Dataset";
    }

    public TaskGroup runModule(TaskGroupListener taskGroupListener) {

        // prepare a new group of tasks
        String path = (String) parameters.getParameterValue(SaveDatasetParameters.filename);
        Task tasks[] = new SaveFileTask[Datasets.length];
        for (int i = 0; i < Datasets.length; i++) {
            String newpath = path;
            if (i > 0) {
                newpath = path.substring(0, path.length() - 4) + String.valueOf(i) + path.substring(path.length() - 4);
            }
            tasks[i] = new SaveFileTask(Datasets[i], parameters, newpath);
        }

        TaskGroup newGroup = new TaskGroup(tasks, this, taskGroupListener);

        // start the group
        newGroup.start();

        return newGroup;

    }
}
