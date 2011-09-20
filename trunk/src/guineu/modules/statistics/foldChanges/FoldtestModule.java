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
package guineu.modules.statistics.foldChanges;

import guineu.data.Dataset;
import guineu.main.GuineuCore;
import guineu.modules.GuineuModuleCategory;
import guineu.modules.GuineuProcessingModule;
import guineu.parameters.ParameterSet;
import guineu.taskcontrol.Task;
import guineu.util.dialogs.ExitCode;

/**
 *
 * @author scsandra
 */
public class FoldtestModule implements GuineuProcessingModule {

        public static final String MODULE_NAME = "Fold Test";
        private Dataset dataset;
        private String[] group1, group2;
        private String parameter;

        public ExitCode setupParameters() {
                try {
                        Dataset[] datasets = GuineuCore.getDesktop().getSelectedDataFiles();
                        dataset = datasets[0];
                        FoldtestDataDialog dialog = new FoldtestDataDialog(dataset);
                        dialog.setVisible(true);
                        group1 = dialog.getGroup1();
                        group2 = dialog.getGroup2();
                        parameter = dialog.getParameter();
                        return dialog.getExitCode();
                } catch (Exception exception) {
                        return ExitCode.CANCEL;
                }
        }

        public ParameterSet getParameterSet() {
                return null;
        }

        public String toString() {
                return MODULE_NAME;
        }

        public Task[] runModule(ParameterSet parameters) {
                ExitCode code = setupParameters();
                if (code == ExitCode.OK) {
                        // prepare a new group of tasks
                        Task tasks[] = new FoldTestTask[1];
                        tasks[0] = new FoldTestTask(group1, group2, dataset, parameter);
                        GuineuCore.getTaskController().addTasks(tasks);
                        return tasks;
                } else {
                        return null;
                }
        }

        public GuineuModuleCategory getModuleCategory() {
                return GuineuModuleCategory.DATAANALYSIS;
        }

        public String getIcon() {
                return "icons/fold.png";
        }

        public boolean setSeparator() {
                return false;
        }
}
