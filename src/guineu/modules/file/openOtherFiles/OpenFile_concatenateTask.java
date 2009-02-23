/*
Copyright 2007-2008 VTT Biotechnology
This file is part of GUINEU.
 */
package guineu.modules.file.openOtherFiles;

import guineu.data.Dataset;
import guineu.data.impl.SimpleDataset_concatenate;
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
public class OpenFile_concatenateTask implements Task {

    private String fileDir;
    private TaskStatus status = TaskStatus.WAITING;
    private String errorMessage;
    private Desktop desktop;
    private double progress;

    public OpenFile_concatenateTask(String fileDir, Desktop desktop) {
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
                    LCMSParserCSV_concatenate parser = new LCMSParserCSV_concatenate(fileDir);
                    progress = parser.getProgress();
                    Dataset dataset = (SimpleDataset_concatenate) parser.getDataset();
                    progress = parser.getProgress();
                    desktop.AddNewFile(dataset);
                    //creates internal frame with the table
                    DataTableModel model = new DatasetDataModel_concatenate(dataset);
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
