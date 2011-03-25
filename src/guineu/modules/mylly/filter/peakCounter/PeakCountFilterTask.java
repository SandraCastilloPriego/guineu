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
package guineu.modules.mylly.filter.peakCounter;

import guineu.data.PeakListRow;
import guineu.data.DatasetType;
import guineu.data.impl.peaklists.SimplePeakListRowGCGC;
import guineu.data.impl.datasets.SimpleGCGCDataset;
import guineu.taskcontrol.Task;
import guineu.taskcontrol.TaskStatus;
import guineu.util.GUIUtils;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author scsandra
 */
public class PeakCountFilterTask implements Task {

        private TaskStatus status = TaskStatus.WAITING;
        private String errorMessage;
        private SimpleGCGCDataset dataset;
        private PeakCountParameters parameters;

        public PeakCountFilterTask(SimpleGCGCDataset dataset, PeakCountParameters parameters) {
                this.dataset = dataset;
                this.parameters = parameters;
        }

        public String getTaskDescription() {
                return "Filtering files with Peak Count Filter... ";
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

                        int peakCount = parameters.getParameter(PeakCountParameters.numFound).getInt();
                        peakCount--;
                        PeakCount filter = new PeakCount(peakCount);
                        SimpleGCGCDataset newAlignment = this.actualMap(dataset, filter);
                        newAlignment.setDatasetName(newAlignment.toString() + parameters.getParameter(PeakCountParameters.suffix).getValue());
                        newAlignment.setType(DatasetType.GCGCTOF);
                        GUIUtils.showNewTable(newAlignment, true);
                        status = TaskStatus.FINISHED;
                } catch (Exception ex) {
                        Logger.getLogger(PeakCountFilterTask.class.getName()).log(Level.SEVERE, null, ex);
                        status = TaskStatus.ERROR;
                }
        }

        private SimpleGCGCDataset actualMap(SimpleGCGCDataset input, PeakCount filter) {
                if (input == null) {
                        return null;
                }

                SimpleGCGCDataset filteredAlignment = new SimpleGCGCDataset(input.getColumnNames(),
                        input.getParameters(),
                        input.getAligner());

                for (PeakListRow row : input.getAlignment()) {
                        if (filter.include(row)) {
                                filteredAlignment.addAlignmentRow((SimplePeakListRowGCGC) row.clone());
                        }
                }

                return filteredAlignment;
        }
}
