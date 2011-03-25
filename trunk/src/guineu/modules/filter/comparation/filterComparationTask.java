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
package guineu.modules.filter.comparation;

import guineu.data.Dataset;
import guineu.desktop.Desktop;
import guineu.parameters.SimpleParameterSet;
import guineu.taskcontrol.Task;
import guineu.taskcontrol.TaskStatus;

public class filterComparationTask implements Task {

    private TaskStatus status = TaskStatus.WAITING;
    private String errorMessage;
    private Desktop desktop;
    private double progress;
    private Dataset[] datasets;
    private double margin;

    public filterComparationTask(Dataset[] datasets, Desktop desktop, SimpleParameterSet parameters) {
        this.desktop = desktop;
        this.datasets = datasets;
        this.margin = parameters.getParameter(filterComparationParameters.difference).getDouble();
    }

    public String getTaskDescription() {
        return "Compare  File... ";
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
            status = TaskStatus.PROCESSING;
            if (datasets.length < 2) {
                status = TaskStatus.ERROR;
                return;
            }
            Dataset dataset1 = datasets[0];
            Dataset dataset2 = datasets[1];
           
            {
                status = TaskStatus.FINISHED;
            }
        } catch (Exception e) {
            status = TaskStatus.ERROR;
            errorMessage = e.toString();
            return;
        }
    }
}
