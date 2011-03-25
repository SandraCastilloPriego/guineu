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
package guineu.modules.identification.AdductSearch;

import guineu.data.Dataset;
import guineu.desktop.Desktop;
import guineu.desktop.GuineuMenu;
import guineu.main.GuineuCore;
import guineu.main.GuineuModule;
import guineu.parameters.ParameterSet;
import guineu.taskcontrol.Task;
import guineu.util.dialogs.ExitCode;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

/**
 * 
 */
public class AdductSearch implements GuineuModule, ActionListener {

    public static final String MODULE_NAME = "Adduct search";
    private Desktop desktop;
    private AdductSearchParameters parameters;

    
    public ParameterSet getParameterSet() {
        return parameters;
    }

   
    public AdductSearch() {

        this.desktop = GuineuCore.getDesktop();

        parameters = new AdductSearchParameters();
        desktop.addMenuItem(GuineuMenu.LCMSIDENTIFICATIONSUBMENU, MODULE_NAME,
                "Identification of adduct peaks by mass difference and same retention time", KeyEvent.VK_A, this, null, null);
    }    
   
    
    public void actionPerformed(ActionEvent e) {

        Dataset[] peakLists = desktop.getSelectedDataFiles();

        if (peakLists.length == 0) {
            desktop.displayErrorMessage("Please select a peak lists to process");
            return;
        }

        ExitCode exitCode = parameters.showSetupDialog();
        if (exitCode != ExitCode.OK) {
            return;
        }

        runModule(peakLists, parameters.clone());

    }

    
    public Task[] runModule(Dataset[] peakLists,
            ParameterSet parameters) {
        if (peakLists == null) {
            throw new IllegalArgumentException(
                    "Cannot run identification without a peak list");
        }

        // prepare a new sequence of tasks
        Task tasks[] = new AdductSearchTask[peakLists.length];
        for (int i = 0; i < peakLists.length; i++) {
            tasks[i] = new AdductSearchTask(
                    (AdductSearchParameters) parameters, peakLists[i]);
        }

        GuineuCore.getTaskController().addTasks(tasks);

        return tasks;
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return MODULE_NAME;
    }
}
