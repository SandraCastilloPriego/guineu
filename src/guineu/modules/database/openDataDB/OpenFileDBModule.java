/*
 * Copyright 2007-2012 VTT Biotechnology
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
package guineu.modules.database.openDataDB;

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
public class OpenFileDBModule implements GuineuProcessingModule {

        public static final String MODULE_NAME = "Open database";
        DatasetOpenDialog dialog;
        boolean combine = false;
        Dataset[] datasets;

        public ExitCode setupParameters() {
                dialog = new DatasetOpenDialog();
                dialog.setVisible(true);
                datasets = dialog.getDatasets().toArray(new Dataset[0]);
                combine = dialog.combineDataset();
                return dialog.getExitCode();
        }

        public ParameterSet getParameterSet() {
                return null;
        }

        @Override
        public String toString() {
                return MODULE_NAME;
        }

        public Task[] runModule(ParameterSet parameters) {
                ExitCode code = this.setupParameters();
                if (code == ExitCode.OK) {
                        Task tasks[] = null;
                        if (combine) {
                                // prepare a new group of tasks
                                tasks = new OpenCombineDBTask[1];
                                tasks[0] = new OpenCombineDBTask(datasets);
                        } else {
                                // prepare a new group of tasks
                                tasks = new OpenFileDBTask[datasets.length];
                                for (int i = 0; i < datasets.length; i++) {
                                        tasks[i] = new OpenFileDBTask(datasets[i]);
                                }
                        }
                        GuineuCore.getTaskController().addTasks(tasks);
                        return tasks;
                } else {
                        return null;
                }

        }

        public GuineuModuleCategory getModuleCategory() {
                return GuineuModuleCategory.DATABASE;
        }

        public String getIcon() {
                return "icons/database.png";
        }

        public boolean setSeparator() {
                return false;
        }
}
