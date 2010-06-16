/*
Copyright 2007-2010 VTT Biotechnology

This file is part of GUINEU.

 */
package guineu.modules.file.openLCMSDatasetFile;

import guineu.data.ParameterSet;
import guineu.desktop.Desktop;
import guineu.desktop.GuineuMenu;
import guineu.desktop.impl.DesktopParameters;
import guineu.main.GuineuCore;
import guineu.main.GuineuModule;
import guineu.taskcontrol.Task;
import guineu.taskcontrol.TaskListener;
import guineu.taskcontrol.TaskStatus;
import guineu.util.dialogs.ExitCode;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.logging.Logger;

public class OpenFile implements GuineuModule, TaskListener, ActionListener {

    private Logger logger = Logger.getLogger(this.getClass().getName());
    private Desktop desktop;
    private File[] FilePath;

    public void initModule() {

        this.desktop = GuineuCore.getDesktop();
        desktop.addMenuItem(GuineuMenu.FILE, "Open LCMS Local File..",
                "TODO write description", KeyEvent.VK_L, this, null, "icons/spectrumicon.png");

    }

    public void taskStarted(Task task) {
        logger.info("Running Open File");
    }

    public void taskFinished(Task task) {
        if (task.getStatus() == TaskStatus.FINISHED) {
            logger.info("Finished open file on " + ((OpenFileTask) task).getTaskDescription());
        }

        if (task.getStatus() == TaskStatus.ERROR) {

            String msg = "Error while open file on .. " + ((OpenFileTask) task).getErrorMessage();
            logger.severe(msg);
            desktop.displayErrorMessage(msg);

        }
    }

    public void actionPerformed(ActionEvent e) {
        ExitCode exitCode = setupParameters();
        if (exitCode != ExitCode.OK) {
            return;
        }

        runModule();
    }

    public ExitCode setupParameters() {
        DesktopParameters deskParameters = (DesktopParameters) GuineuCore.getDesktop().getParameterSet();
        String lastPath = deskParameters.getLastOpenProjectPath();
        if (lastPath == null) {
            lastPath = "";
        }
        File lastFilePath = new File(lastPath);
        DatasetOpenDialog dialog = new DatasetOpenDialog(lastFilePath);
        dialog.setVisible(true);
        try {
            this.FilePath = dialog.getCurrentDirectory();
        } catch (Exception e) {
        }
        return dialog.getExitCode();
    }

    public ParameterSet getParameterSet() {
        return null;
    }

    public void setParameters(ParameterSet parameterValues) {
    }

    public String toString() {
        return "Open File";
    }

    public Task[] runModule() {

        // prepare a new group of tasks
        if (FilePath != null) {
            Task tasks[] = new OpenFileTask[FilePath.length];
            for (int i = 0; i < FilePath.length; i++) {

                tasks[i] = new OpenFileTask(FilePath[i].toString(), desktop);
            }
            GuineuCore.getTaskController().addTasks(tasks);

            return tasks;
        } else {
            return null;
        }

    }
}
