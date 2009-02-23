/*
Copyright 2007-2008 VTT Biotechnology
This file is part of GUINEU.
 */
package guineu.modules.statistics.variationCoefficient;

import guineu.data.Dataset;
import guineu.data.PeakListRow;
import guineu.data.Tables.variationcoefficienttable.VariationCoefficientDataModel;
import guineu.data.impl.VariationCoefficientData;
import guineu.data.impl.SimpleDataset;
import guineu.desktop.Desktop;
import guineu.taskcontrol.Task;
import guineu.util.Tables.DataTable;
import guineu.util.Tables.DataTableModel;
import guineu.util.Tables.impl.PushableTable;
import guineu.util.internalframe.DataInternalFrame;
import java.awt.Dimension;
import java.util.Vector;
import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;

/**
 *
 * @author scsandra
 */
public class VariationCoefficientTask implements Task {

    private Dataset[] datasets;
    private TaskStatus status = TaskStatus.WAITING;
    private String errorMessage;
    private Desktop desktop;
    private double progress;

    public VariationCoefficientTask(Dataset[] datasets, Desktop desktop) {
        this.datasets = datasets;
        this.desktop = desktop;

    }

    public String getTaskDescription() {
        return "Coefficient of variation... ";
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
            Vector<VariationCoefficientData> data = new Vector<VariationCoefficientData>();
            for (Dataset dataset : datasets) {
                VariationCoefficientData vcdata = new VariationCoefficientData();
                vcdata.variationCoefficient = getvariationCoefficient((SimpleDataset) dataset);
                vcdata.NumberIdentMol = getNumberIdentMol((SimpleDataset) dataset);
                vcdata.datasetName = dataset.getDatasetName();
                vcdata.numberMol = ((SimpleDataset) dataset).getNumberRows();
                vcdata.numberExperiments = ((SimpleDataset) dataset).getNumberCols();
                data.addElement(vcdata);
            }

            DataTableModel model = new VariationCoefficientDataModel(data);
            DataTable table = new PushableTable(model);
            table.formatNumbers(1);
            DataInternalFrame frame = new DataInternalFrame("Coefficient of variation", table.getTable(), new Dimension(450, 450));
            desktop.addInternalFrame(frame);
            frame.setVisible(true);

            progress = 1f;

        } catch (Exception ex) {
        }
        status = TaskStatus.FINISHED;
    }

    private int getNumberIdentMol(SimpleDataset dataset) {
        int cont = 0;
        for (PeakListRow row : dataset.getRows()) {
            if (!row.getName().toLowerCase().matches("unknown")) {
                cont++;
            }
        }
        return cont;
    }

    private double getvariationCoefficient(SimpleDataset dataset) {
        DescriptiveStatistics superStats = DescriptiveStatistics.newInstance();
        DescriptiveStatistics stats = DescriptiveStatistics.newInstance();
        for (PeakListRow row : dataset.getRows()) {
            stats.clear();
            for (String experimentName : dataset.getNameExperiments()) {
                stats.addValue((Double)row.getPeak(experimentName));
            }
            superStats.addValue(stats.getStandardDeviation() / stats.getMean());
        }
        return superStats.getMean();
    }
}
