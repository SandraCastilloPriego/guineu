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
package guineu.modules.database.openDataDB;

import guineu.data.Dataset;
import guineu.data.DatasetType;
import guineu.data.parser.Parser;
import guineu.data.parser.impl.database.LCMSParserDataBase;
import guineu.data.parser.impl.database.GCGCParserDataBase;
import guineu.main.GuineuCore;
import guineu.modules.GuineuModule;
import guineu.modules.filter.Alignment.RANSAC.RansacAlignerParameters;
import guineu.modules.filter.Alignment.RANSAC.RansacAlignerTask;
import guineu.modules.mylly.alignment.basicAligner.BasicAlignerGCGCParameters;
import guineu.modules.mylly.alignment.basicAligner.BasicAlignerGCGCTask;
import guineu.parameters.ParameterSet;
import guineu.taskcontrol.AbstractTask;
import guineu.taskcontrol.TaskStatus;

/**
 *
 * @author scsandra
 */
public class OpenCombineDBTask extends AbstractTask {

        private Parser parser;
        private Dataset[] datasets;
        private String taskDescription = "";
        private RansacAlignerTask combineLCMSDatasets;
        private BasicAlignerGCGCTask combineGCGCDatasets2;

        public OpenCombineDBTask(Dataset[] datasets) {
                this.datasets = datasets;
        }

        public String getTaskDescription() {
                return taskDescription;
        }

        public double getFinishedPercentage() {
                if (taskDescription.contains("Opening") && parser != null) {
                        return parser.getProgress();
                } else if (taskDescription.contains("Combining") && combineLCMSDatasets != null) {
                        return combineLCMSDatasets.getFinishedPercentage();
                } else if (taskDescription.contains("Combining") && combineGCGCDatasets2 != null) {
                        return combineGCGCDatasets2.getFinishedPercentage();
                }
                return 0.0f;
        }

        public void cancel() {
                setStatus(TaskStatus.CANCELED);
        }

        public void run() {
                try {
                        setStatus(TaskStatus.PROCESSING);
                        DatasetType type = null;
                        for (Dataset dataset : datasets) {
                                taskDescription = "Opening " + dataset.getDatasetName();
                                dataset = this.openFile(dataset);
                                DatasetType datasetType = dataset.getType();
                                if (type == null) {
                                        type = datasetType;
                                } else if (type != datasetType) {
                                        type = DatasetType.BASIC;
                                }
                        }
                        taskDescription = "Combining data sets..";
                        switch (type) {
                                case LCMS:

                                        ParameterSet ransacParameters = null;
                                        for (GuineuModule module : GuineuCore.getAllModules()) {
                                                if (module.toString().matches("Ransac aligner")) {
                                                        ransacParameters = module.getParameterSet();
                                                        break;
                                                }
                                        }
                                        if(ransacParameters == null){
                                                ransacParameters = new RansacAlignerParameters();                                                
                                        }
                                        combineLCMSDatasets = new RansacAlignerTask(datasets, (RansacAlignerParameters) ransacParameters);
                                        combineLCMSDatasets.run();
                                        break;

                                case GCGCTOF:
                                        /* RansacAlignerGCGCParameters ransacGCGCParameters = new RansacAlignerGCGCParameters();
                                        combineGCGCDatasets = new RansacAlignerGCGCTask(datasets, ransacGCGCParameters);
                                        combineGCGCDatasets.run();*/

                                        BasicAlignerGCGCParameters parameters = new BasicAlignerGCGCParameters();
                                        this.combineGCGCDatasets2 = new BasicAlignerGCGCTask(datasets, parameters);
                                        this.combineGCGCDatasets2.run();
                                        break;

                        }


                        setStatus(TaskStatus.FINISHED);
                } catch (Exception e) {
                        setStatus(TaskStatus.ERROR);
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
                        setStatus(TaskStatus.ERROR);
                        return null;
                }
        }
}
