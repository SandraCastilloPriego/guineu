/*
 * Copyright 2007-2011 VTT Biotechnology
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
package guineu.modules.filter.Alignment.graphalignment;

import guineu.data.Dataset;
import guineu.desktop.Desktop;
import guineu.desktop.GuineuMenu;
import guineu.main.GuineuCore;
import guineu.main.GuineuModule;
import guineu.parameters.ParameterSet;
import guineu.taskcontrol.Task;
import guineu.taskcontrol.TaskListener;
import guineu.taskcontrol.TaskStatus;
import guineu.util.dialogs.ExitCode;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.logging.Logger;

/**
 * 
 */
public class GraphAligner implements GuineuModule, TaskListener, ActionListener {

	private Logger logger = Logger.getLogger(this.getClass().getName());
	private GraphAlignerParameters parameters;
	private Desktop desktop;

	public GraphAligner() {

		this.desktop = GuineuCore.getDesktop();

		parameters = new GraphAlignerParameters();

		desktop.addMenuItem(GuineuMenu.ALIGNMENT, "Graph alignment..",
				"Alignment of two or more data sets using dynamic programming.", KeyEvent.VK_A, this, null,  "icons/alignment.png");
		desktop.addMenuSeparator(GuineuMenu.ALIGNMENT);
	}

        @Override
	public String toString() {
		return "Graph alignment";
	}

	
	public ParameterSet getParameterSet() {
		return parameters;
	}
	

	/**
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {

		Dataset[] peakLists = desktop.getSelectedDataFiles();

		if (peakLists.length == 0) {
			desktop.displayErrorMessage("Please select peak lists for alignment");
			return;
		}

		// Setup parameters
		ExitCode exitCode = parameters.showSetupDialog();
		if (exitCode != ExitCode.OK) {
			return;
		}

		runModule(peakLists);

	}

	
	public Task[] runModule(Dataset[] peakLists) {

		// check peak lists
		if ((peakLists == null) || (peakLists.length == 0)) {
			desktop.displayErrorMessage("Please select peak lists for alignment");
			return null;
		}

		// prepare a new group with just one task
		Task task = new GraphAlignerTask(peakLists,
				(GraphAlignerParameters) parameters);

		GuineuCore.getTaskController().addTask(task);

		return new Task[]{task};

	}

	public void taskStarted(Task task) {
		logger.info("Running alignment");
	}

	public void taskFinished(Task task) {
		if (task.getStatus() == TaskStatus.FINISHED) {
			logger.info("Finished alignment on " + ((GraphAlignerTask) task).getTaskDescription());
		}

		if (task.getStatus() == TaskStatus.ERROR) {

			String msg = "Error while alignment on .. " + ((GraphAlignerTask) task).getErrorMessage();
			logger.severe(msg);
			desktop.displayErrorMessage(msg);

		}
	}
}
