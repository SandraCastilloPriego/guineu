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
package guineu.modules.mylly.filter.alkaneRTCorrector;

import guineu.data.ParameterSet;
import guineu.desktop.Desktop;
import guineu.desktop.GuineuMenu;
import guineu.main.GuineuCore;
import guineu.main.GuineuModule;
import guineu.modules.mylly.datastruct.GCGCData;
import guineu.taskcontrol.Task;
import guineu.taskcontrol.TaskGroup;
import guineu.taskcontrol.TaskGroupListener;
import guineu.taskcontrol.TaskListener;
import guineu.util.dialogs.ExitCode;
import guineu.util.dialogs.ParameterSetupDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.logging.Logger;
import guineu.data.Dataset;
import guineu.data.impl.SimpleGCGCDataset;
import guineu.modules.mylly.datastruct.GCGCDatum;
import java.util.ArrayList;

/**
 *
 * @author scsandra
 */
public class AlkaneRTICorrectorFilter implements GuineuModule, TaskListener, ActionListener {

	private Logger logger = Logger.getLogger(this.getClass().getName());
	private Desktop desktop;
	private AlkaneRTICorrectorParameters parameters;

	public void initModule() {
		parameters = new AlkaneRTICorrectorParameters();
		this.desktop = GuineuCore.getDesktop();
		desktop.addMenuSeparator(GuineuMenu.MYLLY);
		desktop.addMenuItem(GuineuMenu.MYLLY, "Alkane RTI Corrector Filter..",
				"TODO write description", KeyEvent.VK_A, this, null, "icons/help.png");

	}

	public void taskStarted(Task task) {
		logger.info("Running Alkane RTI Corrector Filter");
	}

	public void taskFinished(Task task) {
		if (task.getStatus() == Task.TaskStatus.FINISHED) {
			logger.info("Finished Alkane RTI Corrector Filter ");
		}

		if (task.getStatus() == Task.TaskStatus.ERROR) {

			String msg = "Error while Alkane RTI Corrector Filtering .. ";
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
				(AlkaneRTICorrectorParameters) currentParameters);
		dialog.setVisible(true);

		if (dialog.getExitCode() == ExitCode.OK) {
			runModule(null);
		}
	}

	public ParameterSet getParameterSet() {
		return this.parameters;
	}

	public void setParameters(ParameterSet parameterValues) {
		parameters = (AlkaneRTICorrectorParameters) parameters;
	}

	public String toString() {
		return "Alkane RTI Corrector Filter";
	}

	public TaskGroup runModule(TaskGroupListener taskGroupListener) {

		Dataset[] datasets = desktop.getSelectedDataFiles();
		List<GCGCData> newDatasets = new ArrayList<GCGCData>();

		for(int i = 0; i < datasets.length; i++){
			GCGCDatum[][] datum = ((SimpleGCGCDataset)datasets[i]).toArray();
			List<GCGCDatum> datumList = new ArrayList<GCGCDatum>();
			for(GCGCDatum data: datum[0]){
				datumList.add(data.clone());
			}		
			newDatasets.add(new GCGCData(datumList, datasets[i].getDatasetName()));
		}

		// prepare a new group of tasks
		Task tasks[] = new AlkaneRTICorrectorFilterTask[1];

		tasks[0] = new AlkaneRTICorrectorFilterTask(newDatasets, parameters);

		TaskGroup newGroup = new TaskGroup(tasks, this, taskGroupListener);

		// start the group
		newGroup.start();

		return newGroup;


	}
}
