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
package guineu.modules.database.deleteDataDB;

import guineu.main.GuineuCore;
import guineu.modules.GuineuModuleCategory;
import guineu.modules.GuineuProcessingModule;
import guineu.parameters.ParameterSet;
import guineu.taskcontrol.Task;
import guineu.util.dialogs.ExitCode;
import java.util.List;

/**
 *
 * @author scsandra
 */
public class DeleteDatasetDBModule implements GuineuProcessingModule {

        public static final String MODULE_NAME = "Delete data set";
        DatasetDeleteDialog dialog;
        private String DBPassword;
        List<String> datasets;

        public ExitCode setupParameters() {
                dialog = new DatasetDeleteDialog();
                dialog.setVisible(true);
                datasets = dialog.getDatasets();
                DBPassword = dialog.getDBPassword();
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
                ExitCode code = setupParameters();
                if (code == ExitCode.OK) {
                        Task tasks[] = null;
                        // prepare a new group of tasks
                        tasks = new DeleteDatasetDBTask[1];
                        tasks[0] = new DeleteDatasetDBTask(datasets, DBPassword);

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
                return null;
        }

        public boolean setSeparator() {
                return false;
        }
}
