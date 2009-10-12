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
package guineu.modules.filter.Alignment.data.scaling.median;

import guineu.data.Dataset;
import guineu.data.ParameterSet;
import guineu.desktop.Desktop;
import guineu.desktop.GuineuMenu;
import guineu.main.GuineuCore;
import guineu.main.GuineuModule;
import guineu.taskcontrol.Task;
import guineu.taskcontrol.TaskListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.logging.Logger;

/**
 * 
 */
public class MedianScaling implements GuineuModule, TaskListener, ActionListener {

	private Logger logger = Logger.getLogger(this.getClass().getName());
	private Desktop desktop;

	public void initModule() {

		this.desktop = GuineuCore.getDesktop();

		desktop.addMenuItem(GuineuMenu.ALIGNMENT, "Median Scaling..",
				"TODO write description", KeyEvent.VK_E, this, null, null);
	}

	public String toString() {
		return "Median Scaling";
	}

	/**
	 * @see net.sf.mzmine.main.MZmineModule#getParameterSet()
	 */
	public ParameterSet getParameterSet() {
		return null;
	}

	public void setParameters(ParameterSet parameters) {
	}

	

	/**
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {

		Dataset[] peakLists = desktop.getSelectedDataFiles();

		if (peakLists.length == 0) {
			desktop.displayErrorMessage("Please select peak lists for Median Scaling");
			return;
		}
		
		runModule(null, peakLists, null);

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
			desktop.displayErrorMessage("Please select peak lists for Median Scaling");
			return null;
		}

		// prepare a new group with just one task
		Task task = new MedianScalingTask(peakLists);

		GuineuCore.getTaskController().addTask(task);

		return new Task[]{task};

	}

	public void taskStarted(Task task) {
		logger.info("Running Median Scaling");
	}

	public void taskFinished(Task task) {
		if (task.getStatus() == Task.TaskStatus.FINISHED) {
			logger.info("Finished Median Scaling on " + ((MedianScalingTask) task).getTaskDescription());
		}

		if (task.getStatus() == Task.TaskStatus.ERROR) {

			String msg = "Error while Median Scaling on .. " + ((MedianScalingTask) task).getErrorMessage();
			logger.severe(msg);
			desktop.displayErrorMessage(msg);

		}
	}
}
