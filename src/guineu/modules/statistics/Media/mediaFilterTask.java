/*
 * Copyright 2007-2008 VTT Biotechnology
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
package guineu.modules.statistics.Media;

import guineu.data.Dataset;
import guineu.data.PeakListRow;
import guineu.data.parser.impl.Lipidclass;
import guineu.desktop.Desktop;
import guineu.taskcontrol.Task;
import guineu.util.internalframe.DataInternalFrame;
import javax.swing.JInternalFrame;
import javax.swing.JTable;
import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;

/**
 *
 * @author scsandra
 */
public class mediaFilterTask implements Task {

    private Dataset[] datasets;
    private TaskStatus status = TaskStatus.WAITING;
    private String errorMessage;
    private Desktop desktop;
    private double progress;
    private Lipidclass lipidClass;

    public mediaFilterTask(Dataset[] datasets, Desktop desktop) {
        this.datasets = datasets;
        this.desktop = desktop;
        this.lipidClass = new Lipidclass();
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
            this.median();
        } catch (Exception e) {
            status = TaskStatus.ERROR;
            errorMessage = e.toString();
            return;
        }
    }

    public void median() {
        status = TaskStatus.PROCESSING;
        try {
            progress = 0.0f;
            for (Dataset dataset : datasets) {
                double[] median = this.getSTDDev(dataset);
                dataset.AddNameExperiment("Median");
                int cont = 0;
                for (PeakListRow row : dataset.getRows()) {
                    row.setPeak("Median", median[cont++]);
                }

                JInternalFrame[] frames = desktop.getInternalFrames();
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
        status = TaskStatus.FINISHED;
    }

    public double[] getSTDDev(Dataset dataset) {
        DescriptiveStatistics stats = new DescriptiveStatistics();
        double[] median = new double[dataset.getNumberRows()];
        int numRows = 0;
        for (PeakListRow peak : dataset.getRows()) {
            stats.clear();
            for (String nameExperiment : dataset.getNameExperiments()) {
                stats.addValue((Double) peak.getPeak(nameExperiment));
            }
            double[] values = stats.getSortedValues();
            median[numRows++] = values[values.length / 2];
        }
        return median;
    }
}
