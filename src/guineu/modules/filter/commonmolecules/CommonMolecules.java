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
package guineu.modules.filter.commonmolecules;

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
import java.awt.event.ActionListener;
import java.util.logging.Logger;

/**
 * 
 */
public class CommonMolecules implements GuineuProcessingModule {

    private Logger logger = Logger.getLogger(this.getClass().getName());
    public static final String MODULE_NAME = "Search Common Peaks";
    private Desktop desktop;

   /* public CommonMolecules() {

        this.desktop = GuineuCore.getDesktop();


        desktop.addMenuItem(GuineuMenu.FILTER, "Search for common peaks between datasets",
                "Search for common peaks between datasets",
                KeyEvent.VK_C, this, null, null);
    }*/

    public ParameterSet getParameterSet() {
        return null;
    }

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {

        Dataset[] selectedPeakLists = desktop.getSelectedDataFiles();

        if (selectedPeakLists.length < 1) {
            desktop.displayErrorMessage("Please select a peak list");
            return;
        }

        runModule(selectedPeakLists, null);

    }

    public Task[] runModule(Dataset[] peakLists,
            ParameterSet parameters) {

        if (peakLists == null) {
            throw new IllegalArgumentException(
                    "Cannot run identification without a peak list");
        }
        // prepare a new sequence of tasks
        Task tasks[] = new CommonMoleculesTask[1];
        tasks[0] = new CommonMoleculesTask(peakLists);

        GuineuCore.getTaskController().addTasks(tasks);

        return tasks;

    }
    

    public String toString() {
        return MODULE_NAME;
    }

    public void taskStarted(Task task) {
        logger.info("Running identification");
    }

    public void taskFinished(Task task) {
        if (task.getStatus() == TaskStatus.FINISHED) {
            logger.info("Finished Transpose Dataset on " + ((CommonMoleculesTask) task).getTaskDescription());
        }

        if (task.getStatus() == TaskStatus.ERROR) {

            String msg = "Error while Transpose Dataset on .. " + ((CommonMoleculesTask) task).getErrorMessage();
            logger.severe(msg);
            desktop.displayErrorMessage(msg);

        }
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
