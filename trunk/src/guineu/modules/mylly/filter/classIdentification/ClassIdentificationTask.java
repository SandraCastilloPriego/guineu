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
package guineu.modules.mylly.filter.classIdentification;

import guineu.data.DatasetType;
import guineu.data.impl.datasets.SimpleGCGCDataset;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import guineu.data.Dataset;
import guineu.taskcontrol.AbstractTask;
import guineu.taskcontrol.TaskStatus;
import guineu.util.GUIUtils;

/**
 *
 * @author scsandra
 */
public class ClassIdentificationTask extends AbstractTask {

        private Dataset dataset;
        private ClassIdentificationParameters parameters;
        private ClassIdentification filter;

        public ClassIdentificationTask(Dataset dataset, ClassIdentificationParameters parameters) {
                this.dataset = dataset;
                this.parameters = parameters;
                filter = new ClassIdentification();
        }

        public String getTaskDescription() {
                return "Filtering files with Class Identification Filter... ";
        }

        public double getFinishedPercentage() {
                return filter.getProgress();
        }

        public void cancel() {
                setStatus(TaskStatus.CANCELED);
        }

        public void run() {
                setStatus(TaskStatus.PROCESSING);
                try {

                        String name = parameters.getParameter(ClassIdentificationParameters.fileNames).getValue().getAbsolutePath();
                        filter.createCorrector(new File(name));
                        SimpleGCGCDataset alignment = filter.actualMap((SimpleGCGCDataset) dataset);
                        alignment.setDatasetName(alignment.getDatasetName() + parameters.getParameter(ClassIdentificationParameters.suffix).getValue());
                        alignment.setType(DatasetType.GCGCTOF);
                        GUIUtils.showNewTable(alignment, true);
                        setStatus(TaskStatus.FINISHED);
                } catch (Exception ex) {
                        Logger.getLogger(ClassIdentificationTask.class.getName()).log(Level.SEVERE, null, ex);
                        setStatus(TaskStatus.ERROR);
                }
        }
}
