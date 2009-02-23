/*
Copyright 2007-2008 VTT Biotechnology
This file is part of GUINEU.
 */
package guineu.modules.identification.identification;

import guineu.data.Dataset;
import guineu.data.ParameterSet;
import guineu.desktop.Desktop;
import guineu.desktop.GuineuMenu;
import guineu.main.GuineuCore;
import guineu.main.GuineuModule;
import guineu.taskcontrol.Task;
import guineu.taskcontrol.TaskGroup;
import guineu.taskcontrol.TaskGroupListener;
import guineu.taskcontrol.TaskListener;

import guineu.util.internalframe.DataInternalFrame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.logging.Logger;
import javax.swing.JInternalFrame;

/**
 *
 * @author scsandra
 */
public class Identification implements GuineuModule, TaskListener, ActionListener {

    private Logger logger = Logger.getLogger(this.getClass().getName());
    private Desktop desktop;

    public void initModule() {

        this.desktop = GuineuCore.getDesktop();
        desktop.addMenuItem(GuineuMenu.IDENTIFICATION, "Identification Positive Ion Mode.. ",
                "TODO write description", KeyEvent.VK_P, this, null);

    }

    public void taskStarted(Task task) {
        logger.info("Running Identification Positive Ion Mode..");
    }

    public void taskFinished(Task task) {
        if (task.getStatus() == Task.TaskStatus.FINISHED) {
            logger.info("Finished Identification on " + ((IdentificationTask) task).getTaskDescription());
        }

        if (task.getStatus() == Task.TaskStatus.ERROR) {

            String msg = "Error while Identification on .. " + ((IdentificationTask) task).getErrorMessage();
            logger.severe(msg);
            desktop.displayErrorMessage(msg);

        }
    }

    public void actionPerformed(ActionEvent e) {
        runModule(null);
    }

    public ParameterSet getParameterSet() {
        return null;
    }

    public void setParameters(ParameterSet parameterValues) {

    }

    public String toString() {
        return "Identification Positive Ion Mode";
    }

    public TaskGroup runModule(TaskGroupListener taskGroupListener) {

        // prepare a new group of tasks
        Dataset[] datasets = desktop.getSelectedDataFiles();
        JInternalFrame[] IF = desktop.getInternalFrames();
        if (IF != null) {
            for (JInternalFrame IFn : IF) {
                if (IFn != null && datasets != null && IFn.getTitle().contains(datasets[0].getDatasetName())) {
                    Task tasks[] = new IdentificationTask[1];
                    tasks[0] = new IdentificationTask(((DataInternalFrame) IF[0]).getTable(), datasets[0], desktop);

                    TaskGroup newGroup = new TaskGroup(tasks, this, taskGroupListener);

                    // start the group
                    newGroup.start();
                    return newGroup;
                } else {
                    return null;
                }
            }
        }
        return null;

    }
}
