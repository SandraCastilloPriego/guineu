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
package guineu.modules.mylly.alignment.scoreAligner;

import guineu.main.GuineuCore;
import guineu.modules.GuineuModuleCategory;
import guineu.taskcontrol.Task;
import guineu.data.Dataset;
import guineu.data.DatasetType;
import guineu.data.impl.datasets.SimpleGCGCDataset;
import guineu.modules.GuineuProcessingModule;
import guineu.modules.mylly.datastruct.GCGCData;
import guineu.modules.mylly.datastruct.GCGCDatum;
import guineu.parameters.ParameterSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author scsandra
 */
public class ScoreAlignmentModule implements GuineuProcessingModule {

        private ScoreAlignmentParameters parameters = new ScoreAlignmentParameters();
        public static final String MODULE_NAME = "Score Alignment";

        public ParameterSet getParameterSet() {
                return this.parameters;
        }

        public String toString() {
                return MODULE_NAME;
        }

        public Task[] runModule(ParameterSet parameters) {
                // prepare a new group of tasks
                Task tasks[] = new ScoreAlignmentTask[1];
                Dataset[] datasets = GuineuCore.getDesktop().getSelectedDataFiles();
                List<GCGCData> newDatasets = new ArrayList<GCGCData>();

                for (int i = 0; i < datasets.length; i++) {
                        if (datasets[i].getType() == DatasetType.GCGCTOF) {
                                GCGCDatum[][] datum = ((SimpleGCGCDataset) datasets[i]).toArray();
                                List<GCGCDatum> datumList = Arrays.asList(datum[0]);
                                newDatasets.add(new GCGCData(datumList, datumList.get(0).getColumnName()));
                        }
                }
                if (newDatasets.size() > 1) {
                        tasks[0] = new ScoreAlignmentTask(newDatasets, (ScoreAlignmentParameters) parameters);

                        GuineuCore.getTaskController().addTasks(tasks);

                        return tasks;
                } else {
                        return null;
                }
        }

        public GuineuModuleCategory getModuleCategory() {
                return GuineuModuleCategory.MYLLY;
        }

        public String getIcon() {
                return "icons/alignment.png";
        }

        public boolean setSeparator() {
                return true;
        }
}
