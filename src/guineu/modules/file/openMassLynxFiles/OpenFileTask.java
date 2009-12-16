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
package guineu.modules.file.openMassLynxFiles;

import guineu.data.Dataset;
import guineu.data.impl.SimpleOtherDataset;
import guineu.desktop.Desktop;
import guineu.data.datamodels.OtherDataModel;
import guineu.taskcontrol.Task;
import guineu.taskcontrol.TaskStatus;
import guineu.util.Tables.DataTable;
import guineu.util.Tables.DataTableModel;
import guineu.util.Tables.impl.PushableTable;
import guineu.util.internalframe.DataInternalFrame;
import java.awt.Dimension;

/**
 *
 * @author scsandra
 */
public class OpenFileTask implements Task {

    private String fileDir;
    private TaskStatus status = TaskStatus.WAITING;
    private String errorMessage;
    private Desktop desktop;
    private double progress;

    public OpenFileTask(String fileDir, Desktop desktop) {
        if (fileDir != null) {
            this.fileDir = fileDir;
        }
        this.desktop = desktop;
    }

    public String getTaskDescription() {
        return "Opening File... ";
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
            this.openFile();
        } catch (Exception e) {
            status = TaskStatus.ERROR;
            errorMessage = e.toString();
            return;
        }
    }

    public void openFile() {
        status = TaskStatus.PROCESSING;        
            try {
                if (status == TaskStatus.PROCESSING) {
                    LCMSParserMassLynx parser = new LCMSParserMassLynx(fileDir);
                    progress = parser.getProgress();
                    Dataset dataset = (SimpleOtherDataset) parser.getDataset();
                    progress = parser.getProgress();
                    desktop.AddNewFile(dataset);
                    //creates internal frame with the table
                    DataTableModel model = new OtherDataModel(dataset);
                    DataTable table = new PushableTable(model);
                    table.formatNumbers(dataset.getType());
                    DataInternalFrame frame = new DataInternalFrame(dataset.getDatasetName(), table.getTable(), new Dimension(800, 800));

                    desktop.addInternalFrame(frame);
                    frame.setVisible(true);
                }
            } catch (Exception ex) {
            }
       
        progress = 1f;
        status = TaskStatus.FINISHED;
    }
}
