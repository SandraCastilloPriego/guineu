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
package guineu.modules.file.saveDatasetDB;

import guineu.data.Dataset;
import guineu.data.DatasetType;
import guineu.data.impl.datasets.SimpleBasicDataset;
import guineu.data.impl.datasets.SimpleGCGCDataset;
import guineu.data.impl.datasets.SimpleLCMSDataset;
import guineu.database.intro.InDataBase;
import guineu.database.intro.impl.InOracle;
import guineu.parameters.ParameterSet;
import guineu.taskcontrol.AbstractTask;
import guineu.taskcontrol.TaskStatus;
import java.sql.Connection;

/**
 *
 * @author scsandra
 */
public class SaveFileDBTask extends AbstractTask {

        private Dataset dataset;
        private String author, datasetName, parameterFileName, study;
        private InDataBase db;

        public SaveFileDBTask(Dataset dataset, ParameterSet parameters) {
                this.dataset = dataset;
                this.author = parameters.getParameter(SaveFileParameters.author).getValue();
                this.datasetName = parameters.getParameter(SaveFileParameters.name).getValue();
                this.parameterFileName = parameters.getParameter(SaveFileParameters.parameters).getValue().getAbsolutePath();
                this.study = parameters.getParameter(SaveFileParameters.studyId).getValue().toString();
                db = new InOracle();
        }

        public String getTaskDescription() {
                if (db != null) {
                        return db.getTaskDescription();
                } else {
                        return "Saving Dataset into the database... ";
                }
        }

        public double getFinishedPercentage() {
                return db.getProgress();
        }

        public void cancel() {
                setStatus(TaskStatus.CANCELED);
        }

        public void run() {
                try {
                        saveFile();
                } catch (Exception e) {
                        setStatus(TaskStatus.ERROR);
                        errorMessage = e.toString();
                        return;
                }
        }

        public synchronized void saveFile() {
                try {
                        setStatus(TaskStatus.PROCESSING);
                        Connection connection = db.connect();
                        String type;
                        if (dataset.getType() == DatasetType.LCMS) {
                                type = "LC-MS";
                                db.lcms(connection, (SimpleLCMSDataset) dataset, type, author, datasetName, parameterFileName, study);
                        } else if (dataset.getType() == DatasetType.GCGCTOF) {
                                type = "GCxGC-MS";
                                db.gcgctof(connection, (SimpleGCGCDataset) dataset, type, author, datasetName, study);
                        } else if (dataset.getType() == DatasetType.QUALITYCONTROL) {
                                db.qualityControlFiles(connection, (SimpleBasicDataset) dataset);
                        }
                        setStatus(TaskStatus.FINISHED);
                } catch (Exception e) {
                        setStatus(TaskStatus.ERROR);
                }
        }
}
