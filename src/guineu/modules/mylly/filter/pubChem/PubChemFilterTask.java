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
package guineu.modules.mylly.filter.pubChem;

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
public class PubChemFilterTask extends AbstractTask {

        private Dataset dataset;
        private PubChemParameters parameters;

        public PubChemFilterTask(Dataset dataset, PubChemParameters parameters) {
                this.dataset = dataset;
                this.parameters = parameters;
        }

        public String getTaskDescription() {
                return "Filtering files with PubChem ID Filter... ";
        }

        public double getFinishedPercentage() {
                return 1f;
        }

        public void cancel() {
                setStatus(TaskStatus.CANCELED);
        }

        public void run() {
                setStatus(TaskStatus.PROCESSING);
                try {
                        String name = parameters.getParameter(PubChemParameters.fileNames).getValue().getAbsolutePath();
                        PubChem filter = new PubChem();
                        filter.createCorrector(new File(name));
                        SimpleGCGCDataset alignment = filter.actualMap((SimpleGCGCDataset) dataset);
                        alignment.setDatasetName(alignment.getDatasetName() + parameters.getParameter(PubChemParameters.suffix).getValue());
                        alignment.setType(DatasetType.GCGCTOF);
                        GUIUtils.showNewTable(alignment, true);
                        setStatus(TaskStatus.FINISHED);
                } catch (Exception ex) {
                        Logger.getLogger(PubChemFilterTask.class.getName()).log(Level.SEVERE, null, ex);
                        setStatus(TaskStatus.ERROR);
                }
        }
}
