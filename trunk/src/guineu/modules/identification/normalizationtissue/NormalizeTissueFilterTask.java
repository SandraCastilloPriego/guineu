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
package guineu.modules.identification.normalizationtissue;

import guineu.data.Dataset;
import guineu.data.datamodels.DatasetLCMSDataModel;
import guineu.data.impl.SimpleLCMSDataset;
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
public class NormalizeTissueFilterTask implements Task {

    private TaskStatus status = TaskStatus.WAITING;
    private String errorMessage;
    private Desktop desktop;
    private SimpleLCMSDataset dataset;
    private StandardUmol standards;
    private NormalizeTissue serum;

    public NormalizeTissueFilterTask(Dataset simpleDataset, Desktop desktop, StandardUmol standards) {
        this.dataset = ((SimpleLCMSDataset) simpleDataset).clone();
        this.desktop = desktop;
        this.standards = standards;
        this.serum = new NormalizeTissue(dataset, standards);
    }

    public String getTaskDescription() {
        return "Sample Normalization Filter... ";
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
            serum.normalize(status);
            dataset = serum.getDataset();
            desktop.AddNewFile(dataset);
            DataTableModel model = new DatasetLCMSDataModel(dataset);
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
