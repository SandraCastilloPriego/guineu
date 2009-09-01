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
package guineu.modules.mylly.openFiles;

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
import guineu.util.dialogs.ParameterSetupDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.logging.Logger;

/**
 *
 * @author scsandra
 */
public class OpenFiles implements GuineuModule, TaskListener, ActionListener {

	private Logger logger = Logger.getLogger(this.getClass().getName());
	private Desktop desktop;
	private OpenGCGCFileParameters parameters;

	public void initModule() {

		this.desktop = GuineuCore.getDesktop();
		desktop.addMenuItem(GuineuMenu.MYLLY, "Open GCGC Files..",
				"TODO write description", KeyEvent.VK_G, this, null);
		parameters = new OpenGCGCFileParameters();
	}

	public void taskStarted(Task task) {
		logger.info("Running Open GCGC Files");
	}

	public void taskFinished(Task task) {
		if (task.getStatus() == Task.TaskStatus.FINISHED) {
			logger.info("Finished open GCGC files on " + ((OpenFileTask) task).getTaskDescription());
		}

		if (task.getStatus() == Task.TaskStatus.ERROR) {

			String msg = "Error while open GCGC files on .. " + ((OpenFileTask) task).getErrorMessage();
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
		DesktopParameters deskParameters = (DesktopParameters) GuineuCore.getDesktop().getParameterSet();
		String lastPath = deskParameters.getLastMyllyPath();
		if (lastPath != null && !lastPath.isEmpty()) {
			((OpenGCGCFileParameters) currentParameters).setParameterValue(OpenGCGCFileParameters.fileNames, lastPath);
		}
		final ParameterSetupDialog dialog = new ParameterSetupDialog(
				"Please set parameter values for " + toString(),
				(OpenGCGCFileParameters) currentParameters);
		dialog.setVisible(true);

		if (dialog.getExitCode() == ExitCode.OK) {
			runModule(null);
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

	public TaskGroup runModule(TaskGroupListener taskGroupListener) {
		String fileNames = (String) parameters.getParameterValue(OpenGCGCFileParameters.fileNames);
		String separator = (String) parameters.getParameterValue(OpenGCGCFileParameters.separator);
		boolean filterClassified = (Boolean) parameters.getParameterValue(OpenGCGCFileParameters.filterClassified);
		// prepare a new group of tasks
		if (fileNames != null) {
			String[] fileSplit = null;
			if (fileNames.contains("&&")) {
				fileSplit = fileNames.split("&&");
			} else {
				fileSplit = new String[1];
				fileSplit[0] = fileNames;
			}
			DesktopParameters deskParameters = (DesktopParameters) GuineuCore.getDesktop().getParameterSet();
			deskParameters.setLastMyllyPath(fileSplit[0]);

			Task tasks[] = new OpenFileTask[fileSplit.length];
			for (int i = 0; i < fileSplit.length; i++) {				
				tasks[i] = new OpenFileTask(fileSplit[i], separator, filterClassified);
			}
			TaskGroup newGroup = new TaskGroup(tasks, this, taskGroupListener);

			// start the group
			newGroup.start();

			return newGroup;
		} else {
			return null;
		}

	}
}
