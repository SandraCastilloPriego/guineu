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
package guineu.modules.mylly.filter.SimilarityFilter;

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
import guineu.data.impl.SimpleGCGCDataset;

/**
 *
 * @author scsandra
 */
public class SimilarityFilter implements GuineuModule, TaskListener, ActionListener {

	private Logger logger = Logger.getLogger(this.getClass().getName());
	private Desktop desktop;
	private SimilarityParameters parameters;

	public void initModule() {
		parameters = new SimilarityParameters();
		this.desktop = GuineuCore.getDesktop();
		desktop.addMenuItem(GuineuMenu.MYLLY, "Similarity Filter..",
				"TODO write description", KeyEvent.VK_S, this, null);

	}

	public void taskStarted(Task task) {
		logger.info("Similarity Filter");
	}

	public void taskFinished(Task task) {
		if (task.getStatus() == Task.TaskStatus.FINISHED) {
			logger.info("Finished Similarity Filter ");
		}

		if (task.getStatus() == Task.TaskStatus.ERROR) {

			String msg = "Error while Similarity Filtering .. ";
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
				(SimilarityParameters) currentParameters);
		dialog.setVisible(true);

		if (dialog.getExitCode() == ExitCode.OK) {
			runModule(null);
		}
	}

	public ParameterSet getParameterSet() {
		return this.parameters;
	}

	public void setParameters(ParameterSet parameterValues) {
		parameters = (SimilarityParameters) parameters;
	}

	public String toString() {
		return "Similarity Filter";
	}

	public TaskGroup runModule(TaskGroupListener taskGroupListener) {

		Dataset[] DataFiles = desktop.getSelectedDataFiles();

		// prepare a new group of tasks
		Task tasks[] = new SimilarityFilterTask[DataFiles.length];
		for (int cont = 0; cont < DataFiles.length; cont++) {
			tasks[cont] = new SimilarityFilterTask((SimpleGCGCDataset)DataFiles[cont], parameters);
		}
		TaskGroup newGroup = new TaskGroup(tasks, this, taskGroupListener);
		// start the group
		newGroup.start();

		return newGroup;


	}
}
