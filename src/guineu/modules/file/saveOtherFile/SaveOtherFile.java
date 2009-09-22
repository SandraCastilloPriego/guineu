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
package guineu.modules.file.saveOtherFile;

import guineu.data.Dataset;
import guineu.data.ParameterSet;
import guineu.data.impl.SimpleParameterSet;
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
public class SaveOtherFile implements GuineuModule, TaskListener {

	private Logger logger = Logger.getLogger(this.getClass().getName());
	private Desktop desktop;
	private Dataset[] Datasets;
	private SimpleParameterSet parameters;

	public SaveOtherFile(Dataset[] Datasets) {
		this.Datasets = Datasets;
	}

	public void initModule() {		
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
			logger.info("Finished Save Dataset" + ((SaveOtherFileTask) task).getTaskDescription());
		}

		if (task.getStatus() == Task.TaskStatus.ERROR) {

			String msg = "Error while save Dataset on .. " + ((SaveOtherFileTask) task).getErrorMessage();
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
		parameters = (SimpleParameterSet)parameterValues;
	}

	@Override
	public String toString() {
		return "Save Dataset";
	}

	public TaskGroup runModule(TaskGroupListener taskGroupListener) {

		// prepare a new group of tasks
		String path = (String) parameters.getParameterValue(SaveOtherParameters.Otherfilename);
		Task tasks[] = new SaveOtherFileTask[Datasets.length];
		for (int i = 0; i < Datasets.length; i++) {
			String newpath = path;
			if (i > 0) {
				newpath = path.substring(0, path.length() - 4) + String.valueOf(i) + path.substring(path.length() - 4);
			}
			tasks[i] = new SaveOtherFileTask(Datasets[i], parameters, newpath);
		}

		TaskGroup newGroup = new TaskGroup(tasks, this, taskGroupListener);

		// start the group
		newGroup.start();

		return newGroup;

	}
}
