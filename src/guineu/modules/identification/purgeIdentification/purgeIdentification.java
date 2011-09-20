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
package guineu.modules.identification.purgeIdentification;

import guineu.data.Dataset;
import guineu.desktop.Desktop;
import guineu.main.GuineuCore;
import guineu.modules.GuineuModuleCategory;
import guineu.modules.GuineuProcessingModule;
import guineu.parameters.ParameterSet;
import guineu.taskcontrol.Task;
import guineu.taskcontrol.TaskEvent;
import guineu.taskcontrol.TaskStatus; 
import java.awt.event.ActionEvent;
import java.util.logging.Logger;

/**
 *
 * @author scsandra
 */
public class purgeIdentification implements GuineuProcessingModule {

	private Logger logger = Logger.getLogger(this.getClass().getName());
	private Desktop desktop;

	/*public purgeIdentification() {

		this.desktop = GuineuCore.getDesktop();
		desktop.addMenuItem(GuineuMenu.LCMSIDENTIFICATIONSUBMENU, "Clean Identification.. ",
				"Removes wrong lipid identification based on their RT and MZ", KeyEvent.VK_C, this, null, null);
	}*/

	public void taskStarted(Task task) {
		logger.info("Running Purge Identification..");
	}

	public void taskFinished(Task task) {
		if (task.getStatus() == TaskStatus.FINISHED) {
			logger.info("Finished Purge Identification on " + ((purgeIdentificationTask) task).getTaskDescription());
		}

		if (task.getStatus() == TaskStatus.ERROR) {

			String msg = "Error while Purge Identification on .. " + ((purgeIdentificationTask) task).getErrorMessage();
			logger.severe(msg);
			desktop.displayErrorMessage(msg);

		}
	}

	public void actionPerformed(ActionEvent e) {
		runModule();
	}

	public ParameterSet getParameterSet() {
		return null;
	}
	

	public String toString() {
		return "Purge Identification";
	}

	public Task[] runModule() {

		// prepare a new group of tasks
		Dataset[] datasets = desktop.getSelectedDataFiles();
		Task tasks[] = new purgeIdentificationTask[datasets.length];
		int cont = 0;
		for (Dataset dataset : datasets) {
			tasks[cont++] = new purgeIdentificationTask(dataset, desktop);
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

        public String getIcon() {
                throw new UnsupportedOperationException("Not supported yet.");
        }

        public boolean setSeparator() {
                throw new UnsupportedOperationException("Not supported yet.");
        }
}
