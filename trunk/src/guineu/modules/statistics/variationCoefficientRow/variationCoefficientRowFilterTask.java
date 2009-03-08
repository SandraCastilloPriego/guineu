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
package guineu.modules.statistics.variationCoefficientRow;

import guineu.data.Dataset;
import guineu.data.PeakListRow;
import guineu.data.parser.impl.Lipidclass;
import guineu.data.impl.SimpleDataset;
import guineu.data.datamodels.DatasetDataModel;
import guineu.desktop.Desktop;
import guineu.taskcontrol.Task;
import guineu.util.Tables.DataTable;
import guineu.util.Tables.impl.PushableTable;
import guineu.util.internalframe.DataInternalFrame;
import java.awt.Dimension;
import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;

/**
 *
 * @author scsandra
 */
public class variationCoefficientRowFilterTask implements Task {

    private Dataset[] datasets;
    private TaskStatus status = TaskStatus.WAITING;
    private String errorMessage;
    private Desktop desktop;
    private double progress;

    public variationCoefficientRowFilterTask(Dataset[] datasets, Desktop desktop) {
        this.datasets = datasets;
        this.desktop = desktop;
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
           /* SimpleDataset newDataset = ((SimpleDataset) dataset).clone();
            newDataset.setDatasetName("Var Coefficient - " + dataset.getDatasetName());
            newDataset.AddNameExperiment("Coefficient of variation");
            for (PeakListRow lipid : newDataset.getRows()) {
                double stdDev = this.getSTDDev(lipid);
                lipid.setPeak("Coefficient of variation", stdDev);
            }
            progress += steps;
            DatasetDataModel model = new DatasetDataModel(newDataset);


            progress = 0.5f;
            DataTable table = new PushableTable(model);
            table.formatNumbers(11);
            DataInternalFrame frame = new DataInternalFrame(dataset.getDatasetName() + " - Coefficient of Variation", table.getTable(), new Dimension(450, 450));
            desktop.addInternalFrame(frame);
            desktop.AddNewFile(newDataset);
            frame.setVisible(true);*/

            dataset.AddNameExperiment("Coefficient of variation", 2);
            for (PeakListRow peakList : dataset.getRows()) {
                double stdDev = this.getSTDDev(peakList);
                peakList.setPeak("Coefficient of variation", stdDev);
            }
        }
        progress = 1f;

    }

    public double getSTDDev(PeakListRow row) {
        DescriptiveStatistics stats = new DescriptiveStatistics();
        for (Object peak : row.getPeaks()) {
            stats.addValue((Double) peak);
        }
        return stats.getStandardDeviation() / stats.getMean();
    }

    public class data {

        String Name;
        Double stdDev;
        int NumMol;
    }
}
