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
package guineu.modules.filter.Alignment.centering.mean;

import guineu.data.Dataset;
import guineu.data.PeakListRow;
import guineu.taskcontrol.AbstractTask;
import guineu.taskcontrol.TaskStatus;
import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;

class MeanCenteringTask extends AbstractTask {

        private Dataset peakLists[];
        // Processed rows counter
        private int processedRows, totalRows;

        public MeanCenteringTask(Dataset[] peakLists) {

                this.peakLists = peakLists;
        }

        public String getTaskDescription() {
                return "Mean centering";
        }

        public double getFinishedPercentage() {
                if (totalRows == 0) {
                        return 0f;
                }
                return (double) processedRows / (double) totalRows;
        }

        @Override
        public void cancel() {
                setStatus(TaskStatus.CANCELED);
        }

        /**
         * @see Runnable#run()
         */
        public void run() {
                setStatus(TaskStatus.PROCESSING);

                for (Dataset data : this.peakLists) {
                        normalize(data);
                }
                setStatus(TaskStatus.FINISHED);

        }

        private void normalize(Dataset data) {
                DescriptiveStatistics stats = new DescriptiveStatistics();
                for (String nameExperiment : data.getAllColumnNames()) {
                        for (PeakListRow row : data.getRows()) {
                                Object value = row.getPeak(nameExperiment);
                                if (value != null && value instanceof Double) {
                                        stats.addValue((Double) value);
                                }
                        }
                        for (PeakListRow row : data.getRows()) {
                                Object value = row.getPeak(nameExperiment);
                                if (value != null && value instanceof Double) {
                                        row.setPeak(nameExperiment, Math.abs((Double) value - stats.getMean()));
                                }
                        }
                        stats.clear();
                }
        }
}
