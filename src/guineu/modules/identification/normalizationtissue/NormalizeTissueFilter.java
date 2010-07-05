/*
 * Copyright 2007-2010 VTT Biotechnology
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
package guineu.modules.identification.normalizationtissue;

import guineu.data.Dataset;
import guineu.data.ParameterSet;
import guineu.data.PeakListRow;
import guineu.desktop.Desktop;
import guineu.desktop.GuineuMenu;
import guineu.desktop.impl.DesktopParameters;
import guineu.main.GuineuCore;
import guineu.main.GuineuModule;
import guineu.taskcontrol.Task;
import guineu.taskcontrol.TaskStatus;

import guineu.taskcontrol.TaskListener;
import guineu.util.Range;
import guineu.util.dialogs.ExitCode;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Logger;

/**
 *
 * @author scsandra
 */
public class NormalizeTissueFilter implements GuineuModule, TaskListener, ActionListener {

    private Logger logger = Logger.getLogger(this.getClass().getName());
    private Desktop desktop;
    private Vector<StandardUmol> standards;
    private Hashtable<String, Double> weights;

    public void initModule() {
        this.standards = new Vector<StandardUmol>();
        this.weights = new Hashtable<String, Double>();
        this.desktop = GuineuCore.getDesktop();
        desktop.addMenuItem(GuineuMenu.NORMALIZATION, "Tissue Normalization Filter..",
                "TODO write description", KeyEvent.VK_S, this, null, null);

    }

    public void taskStarted(Task task) {
        logger.info("Running Tissue Normalization Filter");
    }

    public void taskFinished(Task task) {
        if (task.getStatus() == TaskStatus.FINISHED) {
            logger.info("Finished Serum Normalization Filter on " + ((NormalizeTissueFilterTask) task).getTaskDescription());
        }

        if (task.getStatus() == TaskStatus.ERROR) {

            String msg = "Error while Serum Normalization Filter on .. " + ((NormalizeTissueFilterTask) task).getErrorMessage();
            logger.severe(msg);
            desktop.displayErrorMessage(msg);

        }
    }

    public void actionPerformed(ActionEvent e) {
        ExitCode exitCode = setupParameters();
        if (exitCode != ExitCode.OK) {
            return;
        }

        for (StandardUmol std : this.standards) {
            ((DesktopParameters) GuineuCore.getDesktop().getParameterSet()).setStandard(std.getName(), std.getRange());
        }

        runModule();
    }

    public ExitCode setupParameters() {
        Dataset[] datasets = desktop.getSelectedDataFiles();
        if (datasets.length > 0) {
            Hashtable<String, Range> stdRanges = ((DesktopParameters) GuineuCore.getDesktop().getParameterSet()).getStandards();
            for (PeakListRow row : datasets[0].getRows()) {
                if (row.isSelected() || (Integer) row.getVar("getStandard") == 1) {
                    StandardUmol std = new StandardUmol(row);
                    if (stdRanges != null && stdRanges.containsKey(std.getName())) {
                        std.setRange(stdRanges.get(std.getName()));
                    }
                    if (!this.isThere(std)) {
                        this.standards.add(std);
                    }
                }
            }

            try {

                NormalizationDialog dialog = new NormalizationDialog(standards, datasets[0], weights);
                dialog.setVisible(true);
                return dialog.getExitCode();
            } catch (Exception exception) {
                return ExitCode.CANCEL;
            }
        } else {
            return ExitCode.CANCEL;
        }
    }

    public ParameterSet getParameterSet() {
        return null;
    }

    public void setParameters(ParameterSet parameterValues) {
    }

    public String toString() {
        return "Serum Normalization Filter";
    }

    public Task[] runModule() {
        // prepare a new group of tasks
        Dataset[] datasets = desktop.getSelectedDataFiles();
        Task tasks[] = new NormalizeTissueFilterTask[datasets.length];
        for (int i = 0; i < datasets.length; i++) {
            tasks[i] = new NormalizeTissueFilterTask(datasets[i], desktop, standards, weights);
        }
        GuineuCore.getTaskController().addTasks(tasks);
        return tasks;
    }

    private boolean isThere(StandardUmol std2) {
        for(StandardUmol std : this.standards){
            if(std.getName().equals(std2.getName())){
                return true;
            }
        }
        return false;
    }
}
