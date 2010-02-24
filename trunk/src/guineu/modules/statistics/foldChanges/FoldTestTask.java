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
package guineu.modules.statistics.foldChanges;

import guineu.data.PeakListRow;
import guineu.data.Dataset;
import guineu.desktop.Desktop;
import guineu.taskcontrol.Task;
import guineu.taskcontrol.TaskStatus;
import guineu.util.Tables.DataTable;
import guineu.util.Tables.DataTableModel;
import guineu.util.Tables.impl.PushableTable;
import guineu.util.components.FileUtils;
import guineu.util.internalframe.DataInternalFrame;
import java.awt.Dimension;
import org.apache.commons.math.MathException;
import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;

/**
 *
 * @author scsandra
 */
public class FoldTestTask implements Task {

    private TaskStatus status = TaskStatus.WAITING;
    private String errorMessage;
    private Desktop desktop;
    private double progress = 0.0f;
    private String[] group1,  group2;
    private Dataset dataset;

    public FoldTestTask(String[] group1, String[] group2, Dataset dataset, Desktop desktop) {
        this.group1 = group1;
        this.group2 = group2;
        this.dataset = dataset;
        this.desktop = desktop;

    }

    public String getTaskDescription() {
        return "Fold Changes Test... ";
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
            double[] t = new double[dataset.getNumberRows()];
            for (int i = 0; i < dataset.getNumberRows(); i++) {
                t[i] = this.Foldtest(i);
            }

            Dataset newDataset = FileUtils.getDataset(dataset, "Fold Test - ");
            progress = 0.3f;
            newDataset.AddNameExperiment("Fold test");
            int cont = 0;
            for (PeakListRow row : dataset.getRows()) {
                PeakListRow newRow = row.clone();
                newRow.removePeaks();
                newRow.setPeak("Fold test", t[cont++]);
                newDataset.AddRow(newRow);
            }
            DataTableModel model = FileUtils.getTableModel(newDataset);

            progress = 0.5f;
            DataTable table = new PushableTable(model);
            table.formatNumbers(dataset.getType());
            DataInternalFrame frame = new DataInternalFrame(newDataset.getDatasetName(), table.getTable(), new Dimension(450, 450));
            desktop.addInternalFrame(frame);
            desktop.AddNewFile(newDataset);
            frame.setVisible(true);
            progress = 1f;
            status = TaskStatus.FINISHED;

        } catch (Exception e) {
            status = TaskStatus.ERROR;
            errorMessage = e.toString();
            return;
        }
    }

    public double Foldtest(int mol) throws IllegalArgumentException, MathException {
        DescriptiveStatistics stats1 = new DescriptiveStatistics();
        DescriptiveStatistics stats2 = new DescriptiveStatistics();
        for (int i = 0; i < group1.length; i++) {
            try {
                double value = (Double) this.dataset.getRow(mol).getPeak(group1[i]);
               // if (value > 0) {
                    stats1.addValue(value);
               // }
            } catch (Exception e) {
            }
        }
        for (int i = 0; i < group2.length; i++) {
            try {
                double value = (Double) this.dataset.getRow(mol).getPeak(group2[i]);
                //if (value > 0) {
                    stats2.addValue(value);
               // }
            } catch (Exception e) {
            }
        }
        if (stats1.getN() > 0 && stats2.getN() > 0) {
            /*double[] sortValues1 = stats1.getSortedValues();
            double[] sortValues2 = stats2.getSortedValues();

            return sortValues1[((int) stats1.getN() / 2)] / sortValues2[((int) stats2.getN() / 2)];*/
            return stats1.getMean()/stats2.getMean();
        } else {
            return 0;
        }
    }
}
