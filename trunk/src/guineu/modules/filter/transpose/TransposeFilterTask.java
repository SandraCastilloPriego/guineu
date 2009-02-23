/*
Copyright 2007-2008 VTT Biotechnology
This file is part of GUINEU.
 */
package guineu.modules.filter.transpose;

import guineu.data.Dataset;
import guineu.data.PeakListRow;
import guineu.data.PeakListRow_concatenate;
import guineu.data.impl.DatasetType;
import guineu.data.impl.SimpleDataset;
import guineu.data.impl.SimpleDataset_concatenate;
import guineu.data.impl.SimplePeakListRowConcatenate;
import guineu.desktop.Desktop;
import guineu.taskcontrol.Task;

/**
 *
 * @author scsandra
 */
public class TransposeFilterTask implements Task {

    private TaskStatus status = TaskStatus.WAITING;
    private String errorMessage;
    private Desktop desktop;
    private double progress = 0.0f;
    private SimpleDataset dataset;

    public TransposeFilterTask(Dataset dataset, Desktop desktop) {
        this.dataset = (SimpleDataset) dataset;
        this.desktop = desktop;
    }

    public String getTaskDescription() {
        return "Transpose Dataset filter... ";
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
            SimpleDataset_concatenate newDataset = new SimpleDataset_concatenate(dataset.getDatasetName() + "- transposed");
            newDataset.AddNameExperiment("Name");
            status = TaskStatus.PROCESSING;
           
            for (PeakListRow row : dataset.getRows()) {
                int cont = 0;
                int l = row.getName().length();
                while(newDataset.containtName(row.getName())) {
                    row.setName(row.getName().substring(0, l));
                    row.setName(row.getName() + cont);
                    cont++;
                }
                newDataset.AddNameExperiment(row.getName()+ " - "+ row.getMZ() + " - " +row.getRT()+ " - " + row.getNumFound());
                row.setName(row.getName()+ " - "+ row.getMZ() + " - " +row.getRT() + " - " + row.getNumFound());
            }
            for (String samples : dataset.getNameExperiments()) {
                SimplePeakListRowConcatenate row = new SimplePeakListRowConcatenate();
                row.setPeak("Name", samples);
                newDataset.AddRow(row);
            }

            for (PeakListRow row2 : dataset.getRows()) {

                for (PeakListRow_concatenate row : newDataset.getRows()) {

                    row.setPeak(row2.getName(), String.valueOf(row2.getPeak((String) row.getPeak("Name"))));
                }
            }
            newDataset.setType(DatasetType.OTHER);
            desktop.AddNewFile(newDataset);
            //creates internal frame with the table
           /* DataTableModel model = new DatasetDataModel_concatenate(newDataset);
            DataTable table = new PushableTable(model);
            DataInternalFrame frame = new DataInternalFrame(newDataset.getDatasetName(), table.getTable(), new Dimension(800, 800));

            desktop.addInternalFrame(frame);*/
           
           // frame.setVisible(true);
            status = TaskStatus.FINISHED;
        } catch (Exception e) {
            status = TaskStatus.ERROR;
            errorMessage = e.toString();
            return;
        }
    }
}

