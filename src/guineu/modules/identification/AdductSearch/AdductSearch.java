/*
 * Copyright 2006-2010 The MZmine 2 Development Team
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
package guineu.modules.identification.AdductSearch;

import guineu.data.Dataset;
import guineu.data.ParameterSet;
import guineu.data.impl.SimpleParameterSet;
import guineu.desktop.Desktop;
import guineu.desktop.GuineuMenu;
import guineu.main.GuineuCore;
import guineu.main.GuineuModule;
import guineu.taskcontrol.Task;
import guineu.util.dialogs.ExitCode;
import guineu.util.dialogs.ParameterSetupDialog;
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

    /**
     * @see net.sf.mzmine.main.MZmineModule#getParameterSet()
     */
    public ParameterSet getParameterSet() {
        return parameters;
    }

    /**
     * @see net.sf.mzmine.main.MZmineModule#initModule(net.sf.mzmine.main.MZmineCore)
     */
    public void initModule() {

        this.desktop = GuineuCore.getDesktop();

        parameters = new AdductSearchParameters();
        desktop.addMenuItem(GuineuMenu.IDENTIFICATIONSUBMENU, MODULE_NAME,
                "Identification of adduct peaks by mass difference and same retention time", KeyEvent.VK_A, this, null, null);
    }

    /**
     * @see net.sf.mzmine.main.MZmineModule#setParameters(net.sf.mzmine.data.ParameterSet)
     */
    public void setParameters(ParameterSet parameterValues) {
        this.parameters = (AdductSearchParameters) parameterValues;
    }


    /**
     * @see net.sf.mzmine.modules.batchmode.BatchStep#setupParameters(net.sf.mzmine.data.ParameterSet)
     */
    public ExitCode setupParameters(ParameterSet parameters) {
        ParameterSetupDialog dialog = new ParameterSetupDialog(
                "Please set parameter values for " + toString(),
                (SimpleParameterSet) parameters);
        dialog.setVisible(true);
        return dialog.getExitCode();
    }

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {

        Dataset[] peakLists = desktop.getSelectedDataFiles();

        if (peakLists.length == 0) {
            desktop.displayErrorMessage("Please select a peak lists to process");
            return;
        }

        ExitCode exitCode = setupParameters(parameters);
        if (exitCode != ExitCode.OK) {
            return;
        }

        runModule(peakLists, parameters.clone());

    }

    /**
     * @see net.sf.mzmine.modules.batchmode.BatchStep#runModule(net.sf.mzmine.data.RawDataFile[],
     *      net.sf.mzmine.data.PeakList[], net.sf.mzmine.data.ParameterSet,
     *      net.sf.mzmine.taskcontrol.Task[]Listener)
     */
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
