/*
 * Copyright 2007-2011 VTT Biotechnology
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
package guineu.modules.database.deleteDataDB;

import guineu.database.retrieve.DataBase;
import guineu.database.retrieve.impl.OracleRetrievement;
import guineu.taskcontrol.Task;
import guineu.taskcontrol.TaskStatus;
import java.util.List;

/**
 *
 * @author scsandra
 */
public class DeleteDatasetDBTask implements Task {

    private TaskStatus status = TaskStatus.WAITING;
    private String errorMessage;
    List<String> datasets;
    DataBase db;
    String DBPassword;

    public DeleteDatasetDBTask(List<String> datasets, String DBPassword) {
        this.datasets = datasets;
        this.DBPassword = DBPassword;
        db = new OracleRetrievement();
    }

    public String getTaskDescription() {
        return "Deleting Dataset... ";
    }

    public double getFinishedPercentage() {
        return 0;
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
            this.deleteFile();
        } catch (Exception e) {
            status = TaskStatus.ERROR;
            errorMessage = e.toString();
            return;
        }
    }

    public void deleteFile() {
        try {
            status = TaskStatus.PROCESSING;

            for(String dataset : datasets){
                //System.out.println(dataset);
                db.deleteDataset(dataset, DBPassword);
            }

            status = TaskStatus.FINISHED;
        } catch (Exception e) {
            status = TaskStatus.ERROR;
        }
    }
}
