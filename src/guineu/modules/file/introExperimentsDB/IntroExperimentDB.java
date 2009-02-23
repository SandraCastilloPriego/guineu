/*
Copyright 2007-2008 VTT Biotechnology
This file is part of GUINEU.
 */
package guineu.modules.file.introExperimentsDB;

import guineu.data.Dataset;
import guineu.data.ParameterSet;
import guineu.desktop.Desktop;
import guineu.desktop.GuineuMenu;
import guineu.desktop.impl.DesktopParameters;
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
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.logging.Logger;

/**
 *
 * @author scsandra
 */
public class IntroExperimentDB implements GuineuModule, TaskListener, ActionListener {

    private Logger logger = Logger.getLogger(this.getClass().getName());
    private Desktop desktop;
    private IntroExperimentParameters parameters;
    private Dataset[] datasets;
    

    public IntroExperimentDB (Dataset[] datasets){
        this.datasets = datasets;
    }
    public void initModule() {
        this.desktop = GuineuCore.getDesktop();
        desktop.addMenuItem(GuineuMenu.FILE, "Intro Experiment into database..",
                "TODO write description", KeyEvent.VK_I, this, null);
        parameters = new IntroExperimentParameters();
        setupParameters(parameters);
    }

    public void taskStarted(Task task) {
        logger.info("Running Save Experiments into Database");
    }

    public void taskFinished(Task task) {
        if (task.getStatus() == Task.TaskStatus.FINISHED) {
            logger.info("Finished Save Experiments" + ((IntroExperimentDBTask) task).getTaskDescription());
        }

        if (task.getStatus() == Task.TaskStatus.ERROR) {

            String msg = "Error while save Experiments on .. " + ((IntroExperimentDBTask) task).getErrorMessage();
            logger.severe(msg);
            desktop.displayErrorMessage(msg);

        }
    }

    public void setupParameters(ParameterSet currentParameters) {
        DesktopParameters deskParameters = (DesktopParameters) GuineuCore.getDesktop().getParameterSet();
        String lastPath = deskParameters.getLastOpenProjectPath();

        final IntroExperimentDialog dialog = new IntroExperimentDialog(
                (IntroExperimentParameters) currentParameters, lastPath);
        dialog.setVisible(true);
        dialog.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosed(WindowEvent e) {
                if (dialog.getExitCode() == ExitCode.OK) {
                    runModule(null);
                }
            }
        });
    }

    public ParameterSet getParameterSet() {
        return parameters;
    }

    public void setParameters(ParameterSet parameterValues) {
        parameters = (IntroExperimentParameters) parameterValues;
    }

    @Override
    public String toString() {
        return "Save Dataset";
    }

    public TaskGroup runModule(TaskGroupListener taskGroupListener) {

        // prepare a new group of tasks
        Task tasks[] = new IntroExperimentDBTask[1];

        tasks[0] = new IntroExperimentDBTask(parameters, datasets);


        TaskGroup newGroup = new TaskGroup(tasks, this, taskGroupListener);

        // start the group
        newGroup.start();

        return newGroup;

    }

    public void actionPerformed(ActionEvent e) {
        
    }
}
