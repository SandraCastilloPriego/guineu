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
package guineu.modules.mylly.alignment.scoreAligner;

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
import guineu.modules.mylly.gcgcaligner.datastruct.GCGCData;
import guineu.modules.mylly.gcgcaligner.datastruct.GCGCDatum;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author scsandra
 */
public class ScoreAlignment implements GuineuModule, TaskListener, ActionListener {

	private Logger logger = Logger.getLogger(this.getClass().getName());
	private Desktop desktop;
	private ScoreAlignmentParameters parameters;

	public void initModule() {
		parameters = new ScoreAlignmentParameters();
		this.desktop = GuineuCore.getDesktop();
		desktop.addMenuSeparator(GuineuMenu.MYLLY);
		desktop.addMenuItem(GuineuMenu.MYLLY, "Score Alignment..",
				"TODO write description", KeyEvent.VK_S, this, null);

	}

	public void taskStarted(Task task) {
		logger.info("Running Score Alignment");
	}

	public void taskFinished(Task task) {
		if (task.getStatus() == Task.TaskStatus.FINISHED) {
			logger.info("Finished Score Alignment on " + ((ScoreAlignmentTask) task).getTaskDescription());
		}

		if (task.getStatus() == Task.TaskStatus.ERROR) {

			String msg = "Error while Score Alignment on .. " + ((ScoreAlignmentTask) task).getErrorMessage();
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
				(ScoreAlignmentParameters) currentParameters);
		dialog.setVisible(true);

		if (dialog.getExitCode() == ExitCode.OK) {
			runModule(null);
		}
	}

	public ParameterSet getParameterSet() {
		return this.parameters;
	}

	public void setParameters(ParameterSet parameterValues) {
		 parameters = (ScoreAlignmentParameters) parameters;
	}

	public String toString() {
		return "Score Alignment";
	}

	public TaskGroup runModule(TaskGroupListener taskGroupListener) {
		// prepare a new group of tasks
		Task tasks[] = new ScoreAlignmentTask[1];
		Dataset[] datasets = desktop.getSelectedDataFiles();
		List<GCGCData> newDatasets = new ArrayList<GCGCData>();
			
		for(int i = 0; i < datasets.length; i++){
			GCGCDatum[][] datum = ((SimpleGCGCDataset)datasets[i]).toArray();
			List<GCGCDatum> datumList = Arrays.asList(datum[0]);			
			newDatasets.add(new GCGCData(datumList, datumList.get(0).getColumnName()));
		}		
		
		tasks[0] = new ScoreAlignmentTask(newDatasets,parameters);

		TaskGroup newGroup = new TaskGroup(tasks, this, taskGroupListener);

		// start the group
		newGroup.start();

		return newGroup;


	}
}
