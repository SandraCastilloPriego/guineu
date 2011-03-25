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
package guineu.modules.mylly.filter.GroupIdentification;

import guineu.desktop.Desktop;
import guineu.desktop.GuineuMenu;
import guineu.main.GuineuCore;
import guineu.main.GuineuModule;
import guineu.taskcontrol.Task;
import guineu.taskcontrol.TaskStatus;
 
import guineu.taskcontrol.TaskListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.logging.Logger;
import guineu.data.Dataset;
import guineu.data.impl.datasets.SimpleGCGCDataset;
import guineu.parameters.ParameterSet;

/**
 *
 * @author scsandra
 */
public class GroupIdentificationFilter implements GuineuModule, TaskListener, ActionListener {

	private Logger logger = Logger.getLogger(this.getClass().getName());
	private Desktop desktop;

	public GroupIdentificationFilter() {
		this.desktop = GuineuCore.getDesktop();
		desktop.addMenuItem(GuineuMenu.GCGCIDENTIFICATIONSUBMENU, "Group Identification Filter..",
				"Connection with the Golm database to get the substructure identification based on the spectra.", KeyEvent.VK_S, this, null, null);

	}

	public void taskStarted(Task task) {
		logger.info("Group Identification Filter");
	}

	public void taskFinished(Task task) {
		if (task.getStatus() == TaskStatus.FINISHED) {
			logger.info("Finished Group Identification Filter ");
		}

		if (task.getStatus() == TaskStatus.ERROR) {

			String msg = "Error while Group Identification Filtering .. ";
			logger.severe(msg);
			desktop.displayErrorMessage(msg);

		}
	}

	public void actionPerformed(ActionEvent e) {
		try {
			runModule();
		} catch (Exception exception) {
		}
	}	

	public ParameterSet getParameterSet() {
		return null;
	}	

	public String toString() {
		return "Group Identification Filter";
	}

	public Task[] runModule() {

		Dataset[] DataFiles = desktop.getSelectedDataFiles();

		// prepare a new group of tasks
		Task tasks[] = new GroupIdentificationFilterTask[DataFiles.length];
		for (int cont = 0; cont < DataFiles.length; cont++) {                        
                        tasks[cont] = new GroupIdentificationFilterTask((SimpleGCGCDataset)DataFiles[cont]);
		}
		GuineuCore.getTaskController().addTasks(tasks);

        return tasks;



	}
}
