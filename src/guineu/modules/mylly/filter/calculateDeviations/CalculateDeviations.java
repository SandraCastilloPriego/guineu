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
package guineu.modules.mylly.filter.calculateDeviations;

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
import guineu.util.dialogs.ParameterSetupDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.logging.Logger;
import guineu.data.Dataset;

/**
 *
 * @author scsandra
 */
public class CalculateDeviations implements GuineuModule, TaskListener, ActionListener {

	private Logger logger = Logger.getLogger(this.getClass().getName());
	private Desktop desktop;
	private CalculateDeviationsParameters parameters;

	public void initModule() {
		parameters = new CalculateDeviationsParameters();
		this.desktop = GuineuCore.getDesktop();
		desktop.addMenuItem(GuineuMenu.MYLLY, "Calculate Deviations ..",
				"TODO write description", KeyEvent.VK_D, this, null, null);

	}

	public void taskStarted(Task task) {
		logger.info("Running Calculate Deviations");
	}

	public void taskFinished(Task task) {
		if (task.getStatus() == Task.TaskStatus.FINISHED) {
			logger.info("Finished Calculate Deviations ");
		}

		if (task.getStatus() == Task.TaskStatus.ERROR) {

			String msg = "Error while Calculate Deviations .. ";
			logger.severe(msg);
			desktop.displayErrorMessage(msg);

		}
	}

	public void actionPerformed(ActionEvent e) {
		try {
			setupParameters(parameters);
		} catch (Exception exception) {
		}
	}

	public void setupParameters(ParameterSet currentParameters) {
		final ParameterSetupDialog dialog = new ParameterSetupDialog(
				"Please set parameter values for " + toString(),
				(CalculateDeviationsParameters) currentParameters);
		dialog.setVisible(true);

		if (dialog.getExitCode() == ExitCode.OK) {
			runModule(null);
		}
	}

	public ParameterSet getParameterSet() {
		return this.parameters;
	}

	public void setParameters(ParameterSet parameterValues) {
		parameters = (CalculateDeviationsParameters) parameters;
	}

	public String toString() {
		return "Calculate Deviations";
	}

	public TaskGroup runModule(TaskGroupListener taskGroupListener) {

		Dataset[] AlignmentFiles = desktop.getSelectedDataFiles();


		// prepare a new group of tasks
		Task tasks[] = new CalculateDeviationsTask[AlignmentFiles.length];
		for (int i = 0; i < AlignmentFiles.length; i++) {
			tasks[i] = new CalculateDeviationsTask(AlignmentFiles[i], parameters);
		}
		TaskGroup newGroup = new TaskGroup(tasks, this, taskGroupListener);

		// start the group
		newGroup.start();

		return newGroup;

	}
}
