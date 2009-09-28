/*
 * Copyright 2006-2009 The MZmine 2 Development Team
 * 
 * This file is part of MZmine 2.
 * 
 * MZmine 2 is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * MZmine 2 is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * MZmine 2; if not, write to the Free Software Foundation, Inc., 51 Franklin St,
 * Fifth Floor, Boston, MA 02110-1301 USA
 */
package guineu.modules.filter.Alignment.RANSAC;

import guineu.data.Dataset;
import guineu.data.ParameterSet;
import guineu.desktop.Desktop;
import guineu.desktop.GuineuMenu;
import guineu.main.GuineuCore;
import guineu.main.GuineuModule;
import guineu.taskcontrol.Task;
import guineu.taskcontrol.TaskListener;
import guineu.util.dialogs.ExitCode;
import guineu.util.dialogs.ParameterSetupDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.logging.Logger;

/**
 * 
 */
public class RansacAligner implements GuineuModule, TaskListener, ActionListener {

	private Logger logger = Logger.getLogger(this.getClass().getName());
	private RansacAlignerParameters parameters;
	private Desktop desktop;

	public void initModule() {

		this.desktop = GuineuCore.getDesktop();

		parameters = new RansacAlignerParameters();

		desktop.addMenuItem(GuineuMenu.ALIGNMENT, "Combine datasets..",
				"TODO write description", KeyEvent.VK_A, this, null, null);
		desktop.addMenuSeparator(GuineuMenu.ALIGNMENT);
	}

	public String toString() {
		return "Ransac aligner";
	}

	/**
	 * @see net.sf.mzmine.main.MZmineModule#getParameterSet()
	 */
	public ParameterSet getParameterSet() {
		return parameters;
	}

	public void setParameters(ParameterSet parameters) {
		this.parameters = (RansacAlignerParameters) parameters;
	}

	/**
	 * @see net.sf.mzmine.modules.BatchStep#setupParameters(net.sf.mzmine.data.ParameterSet)
	 */
	public ExitCode setupParameters(ParameterSet currentParameters) {
		ParameterSetupDialog dialog = new RansacAlignerSetupDialog(
				"Please set parameter values for " + toString(),
				(RansacAlignerParameters) currentParameters, null);
		dialog.setVisible(true);
		return dialog.getExitCode();
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
		ExitCode exitCode = setupParameters(parameters);
		if (exitCode != ExitCode.OK) {
			return;
		}

		runModule(null, peakLists, parameters.clone());

	}

	/**
	 * @see net.sf.mzmine.modules.BatchStep#runModule(net.sf.mzmine.data.RawDataFile[],
	 *      net.sf.mzmine.data.PeakList[], net.sf.mzmine.data.ParameterSet,
	 *      net.sf.mzmine.taskcontrol.Task[]Listener)
	 */
	public Task[] runModule(Dataset[] dataFiles, Dataset[] peakLists,
			ParameterSet parameters) {

		// check peak lists
		if ((peakLists == null) || (peakLists.length == 0)) {
			desktop.displayErrorMessage("Please select peak lists for alignment");
			return null;
		}

		// prepare a new group with just one task
		Task task = new RansacAlignerTask(peakLists,
				(RansacAlignerParameters) parameters);

		GuineuCore.getTaskController().addTask(task);

		return new Task[]{task};

	}

	public void taskStarted(Task task) {
		logger.info("Running alignment");
	}

	public void taskFinished(Task task) {
		if (task.getStatus() == Task.TaskStatus.FINISHED) {
			logger.info("Finished alignment on " + ((RansacAlignerTask) task).getTaskDescription());
		}

		if (task.getStatus() == Task.TaskStatus.ERROR) {

			String msg = "Error while alignment on .. " + ((RansacAlignerTask) task).getErrorMessage();
			logger.severe(msg);
			desktop.displayErrorMessage(msg);

		}
	}
}
