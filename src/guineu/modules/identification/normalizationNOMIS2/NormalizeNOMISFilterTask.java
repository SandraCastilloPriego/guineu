/*
Copyright 2007-2008 VTT Biotechnology
This file is part of GUINEU.
 */
package guineu.modules.identification.normalizationNOMIS2;

import guineu.data.Dataset;
import guineu.data.maintable.DatasetDataModel;
import guineu.data.impl.SimpleDataset;
import guineu.desktop.Desktop;
import guineu.taskcontrol.Task;
import guineu.util.Tables.DataTable;
import guineu.util.Tables.DataTableModel;
import guineu.util.Tables.impl.PushableTable;
import guineu.util.internalframe.DataInternalFrame;
import java.awt.Dimension;

/**
 *
 * @author scsandra
 */
public class NormalizeNOMISFilterTask implements Task {

    private TaskStatus status = TaskStatus.WAITING;
    private String errorMessage;
    private Desktop desktop;
    private SimpleDataset dataset;
    private StandardUmol standards;
    private NormalizeNOMIS serum;

    public NormalizeNOMISFilterTask(Dataset simpleDataset, Desktop desktop) {
        this.dataset = ((SimpleDataset) simpleDataset).clone();
        this.desktop = desktop;
        this.standards = new StandardUmol();
        this.serum = new NormalizeNOMIS(dataset, standards);
    }

    public String getTaskDescription() {
        return "NOMIS Normalization Filter... ";
    }

    public double getFinishedPercentage() {
        return (double) serum.getProgress();
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
            serum.normalize();
            dataset = serum.getDataset();
            desktop.AddNewFile(dataset);
            DataTableModel model = new DatasetDataModel(dataset);
            DataTable table = new PushableTable(model);
            table.formatNumbers(dataset.getType());
            DataInternalFrame frame = new DataInternalFrame(dataset.getDatasetName(), table.getTable(), new Dimension(800, 800));

            desktop.addInternalFrame(frame);
            status = TaskStatus.FINISHED;
        } catch (Exception e) {
            status = TaskStatus.ERROR;
            errorMessage = e.toString();
            return;
        }
    }
}
