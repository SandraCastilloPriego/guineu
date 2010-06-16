/*
 * Copyright 2007-2010 VTT Biotechnology
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
package guineu.modules.mylly.filter.linearNormalizer;

import guineu.data.ParameterSet;
import guineu.desktop.Desktop;
import guineu.desktop.GuineuMenu;
import guineu.main.GuineuCore;
import guineu.main.GuineuModule;
import guineu.data.impl.SimpleGCGCDataset;
import guineu.taskcontrol.Task;
import guineu.taskcontrol.TaskStatus;
 
import guineu.taskcontrol.TaskListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.logging.Logger;
import guineu.data.Dataset;

/**
 *
 * @author scsandra
 */
public class LinearNormalizerFilter implements GuineuModule, TaskListener, ActionListener {

	private Logger logger = Logger.getLogger(this.getClass().getName());
	private Desktop desktop;

	public void initModule() {		
		this.desktop = GuineuCore.getDesktop();
		desktop.addMenuItem(GuineuMenu.MYLLY, "Linear Normalizer..",
				"Linear Normalization", KeyEvent.VK_L, this, null, null);

	}

	public void taskStarted(Task task) {
		logger.info("Linear Normalizer");
	}

	public void taskFinished(Task task) {
		if (task.getStatus() == TaskStatus.FINISHED) {
			logger.info("Finished Linear Normalizer ");
		}

		if (task.getStatus() == TaskStatus.ERROR) {

			String msg = "Error while Linear Normalizer .. ";
			logger.severe(msg);
			desktop.displayErrorMessage(msg);

		}
	}

	public void actionPerformed(ActionEvent e) {
		try {
			setupParameters(null);
		} catch (Exception exception) {
		}
	}

	public void setupParameters(ParameterSet currentParameters) {
		runModule();		
	}

	public ParameterSet getParameterSet() {
		return null;
	}

	public void setParameters(ParameterSet parameterValues) {
		
	}

	public String toString() {
		return "Linear Normalizer";
	}

	public Task[] runModule() {

		Dataset[] DataFiles = desktop.getSelectedDataFiles();

		// prepare a new group of tasks
		Task tasks[] = new LinearNormalizerFilterTask[DataFiles.length];
		for (int cont = 0; cont < DataFiles.length; cont++) {
			tasks[cont] = new LinearNormalizerFilterTask((SimpleGCGCDataset)DataFiles[cont]);
		}
		GuineuCore.getTaskController().addTasks(tasks);

        return tasks;


	}
}
