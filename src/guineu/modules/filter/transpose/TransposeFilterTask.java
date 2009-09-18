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
package guineu.modules.filter.transpose;

import guineu.data.Dataset;
import guineu.data.PeakListRow;
import guineu.data.impl.DatasetType;
import guineu.data.impl.SimpleLCMSDataset;
import guineu.data.impl.SimpleOtherDataset;
import guineu.data.impl.SimplePeakListRowLCMS;
import guineu.data.impl.SimplePeakListRowOther;
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
    private SimpleLCMSDataset dataset;

    public TransposeFilterTask(Dataset dataset, Desktop desktop) {
        this.dataset = (SimpleLCMSDataset) dataset;
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
            SimpleOtherDataset newDataset = new SimpleOtherDataset(dataset.getDatasetName() + "- transposed");
            newDataset.AddNameExperiment("Name");
            status = TaskStatus.PROCESSING;
           
            for (PeakListRow Row : dataset.getRows()) {
				SimplePeakListRowLCMS row = (SimplePeakListRowLCMS)Row;
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
                SimplePeakListRowOther row = new SimplePeakListRowOther();
                row.setPeak("Name", samples);
                newDataset.AddRow(row);
            }

            for (PeakListRow row2 : dataset.getRows()) {

                for (PeakListRow row : newDataset.getRows()) {

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

