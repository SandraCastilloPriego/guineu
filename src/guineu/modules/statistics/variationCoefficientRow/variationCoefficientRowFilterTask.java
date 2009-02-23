/*
Copyright 2007-2008 VTT Biotechnology
This file is part of GUINEU.
 */
package guineu.modules.statistics.variationCoefficientRow;

import guineu.data.Dataset;
import guineu.data.PeakListRow;
import guineu.data.parser.impl.Lipidclass;
import guineu.data.impl.SimpleDataset;
import guineu.data.maintable.DatasetDataModel;
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
    private Lipidclass lipidClass;

    public variationCoefficientRowFilterTask(Dataset[] datasets, Desktop desktop) {
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
            this.variationCoefficient();
        } catch (Exception e) {
            status = TaskStatus.ERROR;
            errorMessage = e.toString();
            return;
        }
    }

    public void variationCoefficient() {
        status = TaskStatus.PROCESSING;
        try {
            progress = 0.0f;
            double steps = 1f / datasets.length;
            for (Dataset dataset : datasets) {
                SimpleDataset newDataset = ((SimpleDataset) dataset).clone();
                newDataset.setDatasetName("Var Coefficient - "+dataset.getDatasetName());
                ((SimpleDataset) newDataset).AddNameExperiment("Coefficient of variation");
                for (PeakListRow lipid : ((SimpleDataset) newDataset).getRows()) {
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
                frame.setVisible(true);
            }


            progress = 1f;

        } catch (Exception ex) {
        }
        status = TaskStatus.FINISHED;
    }

    public double getSTDDev(PeakListRow row) {
        DescriptiveStatistics stats = new DescriptiveStatistics();
        for (Object peak : row.getPeaks()) {
            stats.addValue((Double)peak);
        }
        return stats.getStandardDeviation() / stats.getMean();
    }

    public class data {

        String Name;
        Double stdDev;
        int NumMol;
    }
}
