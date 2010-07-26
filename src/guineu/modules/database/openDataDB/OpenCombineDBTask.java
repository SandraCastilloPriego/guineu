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
package guineu.modules.database.openDataDB;

import guineu.data.Dataset;
import guineu.data.DatasetType;
import guineu.data.parser.Parser;
import guineu.data.parser.impl.database.LCMSParserDataBase;
import guineu.data.parser.impl.database.GCGCParserDataBase;
import guineu.modules.filter.Alignment.RANSAC.RansacAlignerParameters;
import guineu.modules.filter.Alignment.RANSAC.RansacAlignerTask;
import guineu.taskcontrol.Task;
import guineu.taskcontrol.TaskStatus;

/**
 *
 * @author scsandra
 */
public class OpenCombineDBTask implements Task {

        private TaskStatus status = TaskStatus.WAITING;
        private String errorMessage;
        private Parser parser;
        private Dataset[] datasets;
        private String taskDescription = "";
        private RansacAlignerTask combineDatasets;

        public OpenCombineDBTask(Dataset[] datasets) {
                this.datasets = datasets;
        }

        public String getTaskDescription() {
                return taskDescription;
        }

        public double getFinishedPercentage() {
                if (taskDescription.contains("Opening")) {
                        return parser.getProgress();
                } else if (taskDescription.contains("Combining")) {
                        return combineDatasets.getFinishedPercentage();
                }
                return 0.0f;
        }

        public TaskStatus getStatus() {
                return status;
        }

        public String getErrorMessage() {
                return errorMessage;
        }

        public void cancel() {
                status = TaskStatus.CANCELED;
        }

        public void run() {
                try {
                        status = TaskStatus.PROCESSING;
                        for (Dataset dataset : datasets) {
                                taskDescription = "Opening " + dataset.getDatasetName();
                                dataset = this.openFile(dataset);
                        }
                        taskDescription = "Combining data sets..";
                        RansacAlignerParameters ransacParameters = new RansacAlignerParameters();
                        combineDatasets = new RansacAlignerTask(datasets, ransacParameters);
                        combineDatasets.run();

                        status = TaskStatus.FINISHED;
                } catch (Exception e) {
                        status = TaskStatus.ERROR;
                        errorMessage = e.toString();
                        return;
                }
        }

        public Dataset openFile(Dataset dataset) {
                try {
                        if (dataset.getType() == DatasetType.GCGCTOF) {
                                parser = new GCGCParserDataBase(dataset);
                        } else {
                                parser = new LCMSParserDataBase(dataset);
                        }
                        parser.fillData();

                        return parser.getDataset();

                } catch (Exception e) {
                        status = TaskStatus.ERROR;
                        return null;
                }
        }
}
