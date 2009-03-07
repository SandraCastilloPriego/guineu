/*
Copyright 2007-2008 VTT Biotechnology

This file is part of GUINEU.

 */
package guineu.modules.file.openGCGCDatasetFile;

import guineu.data.parser.impl.GCGCParserXLS;
import guineu.data.datamodels.DatasetDataModel;
import guineu.data.impl.SimpleDataset;
import guineu.data.parser.Parser;
import guineu.data.parser.impl.GCGCParserCSV;
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
public class OpenFileTask implements Task {

    private String fileDir;
    private TaskStatus status = TaskStatus.WAITING;
    private String errorMessage;
    private Desktop desktop;
    private Parser parser;

    public OpenFileTask(String fileDir, Desktop desktop) {
        if (fileDir != null) {
            this.fileDir = fileDir;
        }
        this.desktop = desktop;
        if (fileDir.matches(".*xls")) {
            parser = new GCGCParserXLS(fileDir);
        } else if (fileDir.matches(".*csv.*")) {
            parser = new GCGCParserCSV(fileDir);
        }
    }

    public String getTaskDescription() {
        return "Opening File... ";
    }

    public double getFinishedPercentage() {
        return parser.getProgress();
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
            parser.fillData();
            SimpleDataset dataset = (SimpleDataset) parser.getDataset();
            desktop.AddNewFile(dataset);

            //creates internal frame with the table
            DataTableModel model = new DatasetDataModel(dataset);
            DataTable table = new PushableTable(model);
            table.formatNumbers(dataset.getType());
            DataInternalFrame frame = new DataInternalFrame(dataset.getDatasetName(), table.getTable(), new Dimension(800, 800));

            desktop.addInternalFrame(frame);
            frame.setVisible(true);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        status = TaskStatus.FINISHED;
    }
}
