/*
Copyright 2007-2008 VTT Biotechnology
This file is part of GUINEU.
 */
package guineu.modules.identification.normalizationtissue;

import guineu.data.Dataset;
import guineu.data.ParameterSet;
import guineu.data.impl.SimpleDataset;
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
public class NormalizeTissueFilter implements GuineuModule, TaskListener, ActionListener {

    private Logger logger = Logger.getLogger(this.getClass().getName());
    private Desktop desktop;
    private StandardUmol standards;

    public void initModule() {
        this.standards = new StandardUmol();
        this.desktop = GuineuCore.getDesktop();
        desktop.addMenuItem(GuineuMenu.IDENTIFICATION, "Tissue Normalization Filter..",
                "TODO write description", KeyEvent.VK_T, this, null);

    }

    public void taskStarted(Task task) {
        logger.info("Running Tissue Normalization Filter");
    }

    public void taskFinished(Task task) {
        if (task.getStatus() == Task.TaskStatus.FINISHED) {
            logger.info("Finished Tissue Normalization Filter on " + ((NormalizeTissueFilterTask) task).getTaskDescription());
        }

        if (task.getStatus() == Task.TaskStatus.ERROR) {

            String msg = "Error while Tissue Normalization Filter on .. " + ((NormalizeTissueFilterTask) task).getErrorMessage();
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
        try {
            Dataset[] datasets = desktop.getSelectedDataFiles();
            NormalizationTissueDialog dialog = new NormalizationTissueDialog(standards, ((SimpleDataset)datasets[0]).getNameExperiments());
            dialog.setVisible(true);
            return dialog.getExitCode();
        } catch (Exception exception) {
            return ExitCode.CANCEL;
        }
    }

    public ParameterSet getParameterSet() {
        return null;
    }

    public void setParameters(ParameterSet parameterValues) {
    }

    public String toString() {
        return "Sample Normalization Filter";
    }

    public TaskGroup runModule(TaskGroupListener taskGroupListener) {
        // prepare a new group of tasks
        Dataset[] datasets = desktop.getSelectedDataFiles();
        Task tasks[] = new NormalizeTissueFilterTask[1];       
        tasks[0] = new NormalizeTissueFilterTask(datasets[0], desktop, standards);
        
        TaskGroup newGroup = new TaskGroup(tasks, this, taskGroupListener);

        // start the group
        newGroup.start();

        return newGroup;


    }
}
