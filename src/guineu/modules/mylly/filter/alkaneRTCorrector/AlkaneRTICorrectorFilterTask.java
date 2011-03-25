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
package guineu.modules.mylly.filter.alkaneRTCorrector;

import guineu.data.DatasetType;
import guineu.data.impl.datasets.SimpleGCGCDataset;
import guineu.data.impl.peaklists.SimplePeakListRowGCGC;
import guineu.main.GuineuCore;
import guineu.modules.mylly.datastruct.GCGCData;
import guineu.modules.mylly.datastruct.GCGCDatum;
import guineu.taskcontrol.Task;
import guineu.taskcontrol.TaskStatus;
import java.io.File;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author scsandra
 */
public class AlkaneRTICorrectorFilterTask implements Task {

        private TaskStatus status = TaskStatus.WAITING;
        private String errorMessage;
        private List<GCGCData> datasets;
        private AlkaneRTICorrectorParameters parameters;

        public AlkaneRTICorrectorFilterTask(List<GCGCData> datasets, AlkaneRTICorrectorParameters parameters) {
                this.datasets = datasets;
                this.parameters = parameters;
        }

        public String getTaskDescription() {
                return "Filtering files with Alkane RTI Corrector Filter... ";
        }

        public double getFinishedPercentage() {
                return 1f;
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
                status = TaskStatus.PROCESSING;
                try {

                        String name = parameters.getParameter(AlkaneRTICorrectorParameters.fileNames).getValue().getAbsolutePath();
                        AlkaneRTICorrector filter = AlkaneRTICorrector.createCorrector(new File(name));
                        List<GCGCData> newDatasets = filter.actualMap(datasets);

                        for (GCGCData dates : newDatasets) {
                                dates.setName(dates.getName() + parameters.getParameter(AlkaneRTICorrectorParameters.suffix).getValue());
                                SimpleGCGCDataset newTableOther = this.writeDataset(dates.toList(), dates.getName());
                                GuineuCore.getDesktop().AddNewFile(newTableOther);
                        }

                        status = TaskStatus.FINISHED;
                } catch (Exception ex) {
                        Logger.getLogger(AlkaneRTICorrectorFilterTask.class.getName()).log(Level.SEVERE, null, ex);
                        status = TaskStatus.ERROR;
                }
        }

        private SimpleGCGCDataset writeDataset(List<GCGCDatum> data, String name) {

                SimpleGCGCDataset dataset = new SimpleGCGCDataset(name);
                dataset.addColumnName(name);
                dataset.setType(DatasetType.GCGCTOF);

                for (GCGCDatum mol : data) {
                        SimplePeakListRowGCGC row = new SimplePeakListRowGCGC((int) mol.getId(), mol.getRT1(), mol.getRT2(), mol.getRTI(),
                                mol.getSimilarity(), 0, 0, 0, mol.getQuantMass(), null, mol.getName(),
                                null, mol.getSpectrum().toString(), null, mol.getCAS(), mol.getNewCAS(), mol.getKeggID(), mol.getChebiID(), mol.getSynonyms(), 0.0);

                        mol.setColumnName(name);
                        GCGCDatum[] peaks = new GCGCDatum[1];
                        peaks[0] = mol;
                        row.setDatum(peaks);
                        dataset.addAlignmentRow(row);
                }

                return dataset;
        }
}
