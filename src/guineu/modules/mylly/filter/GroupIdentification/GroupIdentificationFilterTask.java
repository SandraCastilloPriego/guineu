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
package guineu.modules.mylly.filter.GroupIdentification;

import guineu.data.impl.SimpleGCGCDataset;
import guineu.taskcontrol.Task;
import guineu.taskcontrol.TaskStatus;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author scsandra
 */
public class GroupIdentificationFilterTask implements Task {

    private TaskStatus status = TaskStatus.WAITING;
    private String errorMessage;
    private SimpleGCGCDataset dataset;
    private GroupIdentification filter;

    public GroupIdentificationFilterTask(SimpleGCGCDataset dataset) {
        this.dataset = dataset;
    }

    public String getTaskDescription() {
        return "Filtering files with Group Identifiacion Filter... ";
    }

    public double getFinishedPercentage() {
        if (filter != null) {
            return filter.getProgress();
        } else {
            return 0.0;
        }
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
        status = TaskStatus.PROCESSING;
        try {
            filter = new GroupIdentification();
            filter.actualMap(dataset);
            status = TaskStatus.FINISHED;
        } catch (Exception ex) {
            Logger.getLogger(GroupIdentificationFilterTask.class.getName()).log(Level.SEVERE, null, ex);
            status = TaskStatus.ERROR;
        }
    }
}
