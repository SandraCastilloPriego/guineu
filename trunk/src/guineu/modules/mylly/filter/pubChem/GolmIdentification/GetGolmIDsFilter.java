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
package guineu.modules.mylly.filter.pubChem.GolmIdentification;

import guineu.desktop.Desktop;
import guineu.main.GuineuCore;
import guineu.modules.GuineuModuleCategory;
import guineu.taskcontrol.Task;
import guineu.taskcontrol.TaskEvent;
import guineu.taskcontrol.TaskStatus;
 
import java.awt.event.ActionEvent;
import java.util.logging.Logger;
import guineu.data.Dataset;
import guineu.data.impl.datasets.SimpleGCGCDataset;
import guineu.modules.GuineuProcessingModule;
import guineu.parameters.ParameterSet;

/**
 *
 * @author scsandra
 */
public class GetGolmIDsFilter implements GuineuProcessingModule {

	private Logger logger = Logger.getLogger(this.getClass().getName());
	private Desktop desktop;

	/*public GetGolmIDsFilter() {
		this.desktop = GuineuCore.getDesktop();
		desktop.addMenuItem(GuineuMenu.GCGCIDENTIFICATIONSUBMENU, "IDs Identification Filter..",
				"Connection with the Golm database to get the possible PubChem IDs, Kegg IDs, etc...", KeyEvent.VK_S, this, null, null);

	}*/

	public void taskStarted(Task task) {
		logger.info("Group Identification Filter");
	}

	public void taskFinished(Task task) {
		if (task.getStatus() == TaskStatus.FINISHED) {
			logger.info("Finished IDs Identification Filter ");
		}

		if (task.getStatus() == TaskStatus.ERROR) {

			String msg = "Error while IDs Identification Filtering .. ";
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
		return "IDs Identification Filter";
	}

	public Task[] runModule() {

		Dataset[] DataFiles = desktop.getSelectedDataFiles();

		// prepare a new group of tasks
		Task tasks[] = new GetGolmIDsFilterTask[DataFiles.length];
		for (int cont = 0; cont < DataFiles.length; cont++) {                        
                        tasks[cont] = new GetGolmIDsFilterTask((SimpleGCGCDataset)DataFiles[cont]);
		}
		GuineuCore.getTaskController().addTasks(tasks);

        return tasks;



	}

        public void statusChanged(TaskEvent e) {
                throw new UnsupportedOperationException("Not supported yet.");
        }

        public Task[] runModule(ParameterSet parameters) {
                throw new UnsupportedOperationException("Not supported yet.");
        }

        public GuineuModuleCategory getModuleCategory() {
                throw new UnsupportedOperationException("Not supported yet.");
        }
}
