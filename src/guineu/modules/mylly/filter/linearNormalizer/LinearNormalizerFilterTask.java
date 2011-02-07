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
package guineu.modules.mylly.filter.linearNormalizer;

import guineu.data.Dataset;
import guineu.data.DatasetType;
import guineu.data.PeakListRow;
import guineu.data.impl.peaklists.SimplePeakListRowLCMS;
import guineu.taskcontrol.Task;
import guineu.taskcontrol.TaskStatus;
import guineu.util.GUIUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author scsandra
 */
public class LinearNormalizerFilterTask implements Task {

        private TaskStatus status = TaskStatus.WAITING;
        private String errorMessage;
        private Dataset dataset;

        public LinearNormalizerFilterTask(Dataset dataset) {
                this.dataset = dataset;
        }

        public String getTaskDescription() {
                return "Filtering file with Linear Normalizer... ";
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

                        List<PeakListRow> standards = new ArrayList<PeakListRow>();
                        for (PeakListRow row : dataset.getRows()) {
                                if (row.isSelected() || (dataset.getType() == DatasetType.LCMS &&
                                        ((SimplePeakListRowLCMS) row).getStandard() == 1)) {
                                        standards.add(row);
                                }
                        }
                        LinearNormalizer filter = new LinearNormalizer(standards);
                        Dataset newAlignment = filter.actualMap(dataset);
                        newAlignment.setDatasetName(newAlignment.toString() + "-Normalized");
                        newAlignment.setType(dataset.getType());

                        GUIUtils.showNewTable(newAlignment, true);

                        status = TaskStatus.FINISHED;
                } catch (Exception ex) {
                        Logger.getLogger(LinearNormalizerFilterTask.class.getName()).log(Level.SEVERE, null, ex);
                        status = TaskStatus.ERROR;
                }
        }
}
