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
package guineu.modules.identification.normalizationserum;

import guineu.data.Dataset;
import guineu.data.PeakListRow;
import guineu.main.GuineuCore;
import guineu.modules.GuineuModuleCategory;
import guineu.modules.GuineuProcessingModule;
import guineu.parameters.ParameterSet;
import guineu.taskcontrol.Task;
import guineu.util.GUIUtils;
import guineu.util.Range;
import guineu.util.dialogs.ExitCode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author scsandra
 */
public class NormalizeSerumModule implements GuineuProcessingModule {

        public static final String MODULE_NAME = "Serum Normalization Filter";
        private List<StandardUmol> standards;
        final String helpID = GUIUtils.generateHelpID(this);

        public ExitCode setupParameters() {
                this.standards = new ArrayList<StandardUmol>();
                Dataset[] datasets = GuineuCore.getDesktop().getSelectedDataFiles();
                if (datasets.length > 0) {
                        HashMap<String, Range> stdRanges = GuineuCore.getStandards();
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

                        purge();

                        try {

                                NormalizationDialog dialog = new NormalizationDialog(standards, helpID);
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

        public String toString() {
                return MODULE_NAME;
        }

        private boolean isThere(StandardUmol std2) {
                for (StandardUmol std : this.standards) {
                        if (std.getName().equals(std2.getName())) {
                                return true;
                        }
                }
                return false;
        }

        private void purge() {
                List<StandardUmol> remove = new ArrayList<StandardUmol>();
                for (StandardUmol std : this.standards) {
                        if (!std.isSelect()) {
                                remove.add(std);
                        }
                }

                for (StandardUmol std : remove) {
                        this.standards.remove(std);
                }

        }

        public Task[] runModule(ParameterSet parameters) {
                ExitCode exitCode = setupParameters();
                if (exitCode != ExitCode.OK) {
                        return null;
                }

                for (StandardUmol std : this.standards) {
                        GuineuCore.setStandard(std.getName(), std.getRange());
                }

                // prepare a new group of tasks
                Dataset[] datasets = GuineuCore.getDesktop().getSelectedDataFiles();
                Task tasks[] = new NormalizeSerumTask[datasets.length];
                for (int i = 0; i < datasets.length; i++) {
                        tasks[i] = new NormalizeSerumTask(datasets[i], standards);
                }
                GuineuCore.getTaskController().addTasks(tasks);
                return tasks;
        }

        public GuineuModuleCategory getModuleCategory() {
                return GuineuModuleCategory.NORMALIZATION;
        }

        public String getIcon() {
                return "icons/serumnorm.png";
        }

        public boolean setSeparator() {
                return false;
        }
}
