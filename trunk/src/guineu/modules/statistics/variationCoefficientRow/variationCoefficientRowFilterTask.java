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
package guineu.modules.statistics.variationCoefficientRow;

import guineu.data.Dataset;
import guineu.data.PeakListRow;
import guineu.taskcontrol.Task;
import guineu.taskcontrol.TaskStatus;
import guineu.util.GUIUtils;
import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;

/**
 *
 * @author scsandra
 */
public class variationCoefficientRowFilterTask implements Task {

        private Dataset[] datasets;
        private TaskStatus status = TaskStatus.WAITING;
        private String errorMessage;
        private double progress;

        public variationCoefficientRowFilterTask(Dataset[] datasets) {
                this.datasets = datasets;

        }

        public String getTaskDescription() {
                return "std Dev scores... ";
        }

        public double getFinishedPercentage() {
                return progress;
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
                        this.variationCoefficient();
                        status = TaskStatus.FINISHED;
                } catch (Exception e) {
                        status = TaskStatus.ERROR;
                        errorMessage = e.toString();
                        return;
                }
        }

        public void variationCoefficient() {
                progress = 0.0f;
                double steps = 1f / datasets.length;
                for (Dataset dataset : datasets) {
                        Dataset newDataset = dataset.clone();
                        newDataset.setDatasetName("Var Coefficient - " + dataset.getDatasetName());
                        newDataset.addColumnName("Coefficient of variation");
                        for (PeakListRow lipid : newDataset.getRows()) {
                                double stdDev = this.CoefficientOfVariation(lipid);
                                lipid.setPeak("Coefficient of variation", stdDev);
                        }
                        progress += steps;
                        progress = 0.5f;
                        GUIUtils.showNewTable(newDataset, true);
                }
                progress = 1f;

        }

        public double CoefficientOfVariation(PeakListRow row) {
                DescriptiveStatistics stats = new DescriptiveStatistics();
                for (Object peak : row.getPeaks()) {
                        if (peak != null) {
                                stats.addValue((Double) peak);
                        }
                }
                return stats.getStandardDeviation() / stats.getMean();
        }
}
