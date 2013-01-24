/*
 * Copyright 2007-2013 VTT Biotechnology
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
package guineu.modules.identification.simplelipidname;

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
public class SimpleLipidNameFilter implements GuineuProcessingModule {

    private Logger logger = Logger.getLogger(this.getClass().getName());
    private Desktop desktop;

  /*  public SimpleLipidNameFilter() {

        this.desktop = GuineuCore.getDesktop();
        desktop.addMenuItem(GuineuMenu.IDENTIFICATIONFILTERS, "Simplify Lipid Name..",
                "Simplify the name of the lipids (not needed)", KeyEvent.VK_I, this, null, null);

    }*/

    public void taskStarted(Task task) {
        logger.info("Running Simplify Lipid Name");
    }

    public void taskFinished(Task task) {
        if (task.getStatus() == TaskStatus.FINISHED) {
            logger.info("Finished Simplify Lipid Name on " + ((SimpleLipidNameFilterTask) task).getTaskDescription());
        }

        if (task.getStatus() == TaskStatus.ERROR) {

            String msg = "Error while Simplify Lipid Name on .. " + ((SimpleLipidNameFilterTask) task).getErrorMessage();
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
        return "Simplify Lipid Name Filter";
    }

    public Task[] runModule() {

        // prepare a new group of tasks
        Dataset[] datasets = desktop.getSelectedDataFiles();
        Task tasks[] = new SimpleLipidNameFilterTask[datasets.length];
        for (int i = 0; i < datasets.length; i++) {
            tasks[i] = new SimpleLipidNameFilterTask(datasets[i], desktop);
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
