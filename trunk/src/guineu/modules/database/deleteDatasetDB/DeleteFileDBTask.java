/*
Copyright 2007-2008 VTT Biotechnology
This file is part of GUINEU.
 */
package guineu.modules.database.deleteDatasetDB;

import guineu.database.intro.InDataBase;
import guineu.database.intro.InOracle;
import guineu.desktop.Desktop;
import guineu.taskcontrol.Task;

/**
 *
 * @author scsandra
 */
public class DeleteFileDBTask implements Task {

    private int datasetID;
    private TaskStatus status = TaskStatus.WAITING;
    private String errorMessage;
    private Desktop desktop;
    private InDataBase db;

    public DeleteFileDBTask(int datasetID, Desktop desktop) {
        this.datasetID = datasetID;
        this.desktop = desktop;
        db = new InOracle();
    }

    public String getTaskDescription() {
        return "Deleting Dataset... ";
    }

    public double getFinishedPercentage() {
        return db.getProgress();
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
            db.deleteDataset(db.connect(), datasetID);
            status = TaskStatus.FINISHED;
        } catch (Exception e) {
            status = TaskStatus.ERROR;
        }
    }
}
