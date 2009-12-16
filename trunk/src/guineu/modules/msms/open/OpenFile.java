/*
Copyright 2007-2008 VTT Biotechnology

This file is part of GUINEU.

 */
package guineu.modules.msms.open;

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
import java.io.File;
import java.util.logging.Logger;

public class OpenFile implements GuineuModule, TaskListener, ActionListener {

	private Logger logger = Logger.getLogger(this.getClass().getName());
	private Desktop desktop;
	private OpenMSMSFileParameters parameters;

	public void initModule() {

		this.desktop = GuineuCore.getDesktop();
		desktop.addMenuItem(GuineuMenu.MSMS, "Open LCMSMS Local File..",
				"TODO write description", KeyEvent.VK_L, this, null, null);

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

	public void setupParameters(ParameterSet currentParameters) {
		final ParameterSetupDialog dialog = new ParameterSetupDialog(
				"Please set parameter values for " + toString(),
				(OpenMSMSFileParameters) currentParameters);
		dialog.setVisible(true);

		if (dialog.getExitCode() == ExitCode.OK) {
			runModule();
		}
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


		Task tasks[] = new OpenFileTask[1];


		tasks[0] = new OpenFileTask(desktop, parameters);

		GuineuCore.getTaskController().addTasks(tasks);

        return tasks;


	}

	public void actionPerformed(ActionEvent e) {
		parameters = new OpenMSMSFileParameters();
		try {
			setupParameters(parameters);
		} catch (Exception exception) {
		}
	}
}
