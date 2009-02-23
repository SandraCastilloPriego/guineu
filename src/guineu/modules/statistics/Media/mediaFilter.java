/*
Copyright 2007-2008 VTT Biotechnology
This file is part of GUINEU.
 */
package guineu.modules.statistics.Media;

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
import guineu.util.dialogs.ExitCode;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.logging.Logger;

/**
 *
 * @author scsandra
 */
public class mediaFilter implements GuineuModule, TaskListener, ActionListener {

    private Logger logger = Logger.getLogger(this.getClass().getName());
    private Desktop desktop;

    public void initModule() {

        this.desktop = GuineuCore.getDesktop();
        desktop.addMenuItem(GuineuMenu.STATISTICS, "Median..",
                "TODO write description", KeyEvent.VK_M, this, null);

    }

    public void taskStarted(Task task) {
        logger.info("Running Median....");
    }

    public void taskFinished(Task task) {
        if (task.getStatus() == Task.TaskStatus.FINISHED) {
            logger.info("Finished Median on " + ((mediaFilterTask) task).getTaskDescription());
        }

        if (task.getStatus() == Task.TaskStatus.ERROR) {

            String msg = "Error while Median on .. " + ((mediaFilterTask) task).getErrorMessage();
            logger.severe(msg);
            desktop.displayErrorMessage(msg);

        }
    }

    public void actionPerformed(ActionEvent e) {
        ExitCode exitCode = setupParameters();
        if (exitCode != ExitCode.OK) {
            return;
        }

        runModule(null);
    }

    public ExitCode setupParameters() {
        return ExitCode.OK;
    }

    public ParameterSet getParameterSet() {
        return null;
    }

    public void setParameters(ParameterSet parameterValues) {

    }

    public String toString() {
        return "Median";
    }

    public TaskGroup runModule(TaskGroupListener taskGroupListener) {

        // prepare a new group of tasks
        Dataset[] datasets = desktop.getSelectedDataFiles();
        Task tasks[] = new mediaFilterTask[1];
        tasks[0] = new mediaFilterTask(datasets, desktop);
        TaskGroup newGroup = new TaskGroup(tasks, this, taskGroupListener);
        // start the group
        newGroup.start();

        return newGroup;


    }
}
