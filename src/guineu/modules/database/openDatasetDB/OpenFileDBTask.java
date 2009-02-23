/*
Copyright 2007-2008 VTT Biotechnology
This file is part of GUINEU.
 */
package guineu.modules.database.openDatasetDB;

import guineu.data.maintable.DatasetDataModel;
import guineu.data.parser.Parser;
import guineu.data.parser.impl.LCMSParserDataBase;
import guineu.data.impl.SimpleDataset;
import guineu.data.parser.impl.GCGCParserDataBase;
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
public class OpenFileDBTask implements Task {

    private int datasetID;
    private TaskStatus status = TaskStatus.WAITING;
    private String errorMessage;
    private Desktop desktop;
    private Parser parser;

    public OpenFileDBTask(int datasetID, String type, Desktop desktop) {
        this.datasetID = datasetID;
        this.desktop = desktop;
        if (type.contains("GCGC")) {
            parser = new GCGCParserDataBase(datasetID);
        } else {
            parser = new LCMSParserDataBase(datasetID);
        }

    }

    public String getTaskDescription() {
        return "Opening Dataset... ";
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
        try {
            status = TaskStatus.PROCESSING;       
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
            status = TaskStatus.FINISHED;
        } catch (Exception e) {
            status = TaskStatus.ERROR;
        }
    }
}
