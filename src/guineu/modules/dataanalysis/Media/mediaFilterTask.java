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
package guineu.modules.dataanalysis.Media;

import guineu.data.Dataset;
import guineu.data.PeakListRow;
import guineu.main.GuineuCore;
import guineu.taskcontrol.AbstractTask;
import guineu.taskcontrol.TaskStatus;
import guineu.util.internalframe.DataInternalFrame;
import javax.swing.JInternalFrame;
import javax.swing.JTable;
import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;

/**
 *
 * @author scsandra
 */
public class mediaFilterTask extends AbstractTask {

        private Dataset[] datasets;
        private double progress;

        public mediaFilterTask(Dataset[] datasets) {
                this.datasets = datasets;
        }

        public String getTaskDescription() {
                return "std Dev scores... ";
        }

        public double getFinishedPercentage() {
                return progress;
        }

        public void cancel() {
                setStatus(TaskStatus.CANCELED);
        }

        public void run() {
                try {
                        this.median();
                } catch (Exception e) {
                        setStatus(TaskStatus.ERROR);
                        errorMessage = e.toString();
                        return;
                }
        }

        public void median() {
                setStatus(TaskStatus.PROCESSING);
                try {
                        progress = 0.0f;
                        for (Dataset dataset : datasets) {
                                double[] median = this.getSTDDev(dataset);
                                dataset.addColumnName("Median");
                                int cont = 0;
                                for (PeakListRow row : dataset.getRows()) {
                                        row.setPeak("Median", median[cont++]);
                                }

                                JInternalFrame[] frames = GuineuCore.getDesktop().getInternalFrames();
                                for (int i = 0; i < frames.length; i++) {
                                        try {
                                                JTable table = ((DataInternalFrame) frames[i]).getTable();
                                                table.createDefaultColumnsFromModel();
                                                table.revalidate();
                                        } catch (Exception e) {
                                        }
                                }

                        }
                        progress = 1f;

                } catch (Exception ex) {
                }
                setStatus(TaskStatus.FINISHED);
        }

        public double[] getSTDDev(Dataset dataset) {
                DescriptiveStatistics stats = new DescriptiveStatistics();
                double[] median = new double[dataset.getNumberRows()];
                int numRows = 0;
                for (PeakListRow peak : dataset.getRows()) {
                        stats.clear();
                        for (String nameExperiment : dataset.getAllColumnNames()) {
                                try {
                                        stats.addValue((Double) peak.getPeak(nameExperiment));
                                } catch (Exception e) {
                                }
                        }
                        double[] values = stats.getSortedValues();
                        median[numRows++] = values[values.length / 2];
                }
                return median;
        }
}
