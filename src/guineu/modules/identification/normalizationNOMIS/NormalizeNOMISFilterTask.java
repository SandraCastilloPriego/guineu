/*
 * Copyright 2007-2010 VTT Biotechnology
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
package guineu.modules.identification.normalizationNOMIS;

import guineu.data.Dataset;
import guineu.data.impl.datasets.SimpleLCMSDataset;
import guineu.taskcontrol.Task;
import guineu.taskcontrol.TaskStatus;

/**
 *
 * @author scsandra
 */
public class NormalizeNOMISFilterTask implements Task {

    private TaskStatus status = TaskStatus.WAITING;
    private String errorMessage;
    private SimpleLCMSDataset dataset;
    private StandardUmol standards;
    private NormalizeNOMIS serum;

    public NormalizeNOMISFilterTask(Dataset simpleDataset) {
        this.dataset = ((SimpleLCMSDataset) simpleDataset).clone();      
        this.standards = new StandardUmol();
        this.serum = new NormalizeNOMIS(dataset, standards);
    }

    public String getTaskDescription() {
        return "NOMIS Normalization Filter... ";
    }

    public double getFinishedPercentage() {
        return (double) /*serum.getProgress()*/ 0.0f;
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
            serum.normalize();            
            status = TaskStatus.FINISHED;
        } catch (Exception e) {
            status = TaskStatus.ERROR;
            errorMessage = e.toString();
            return;
        }
    }
}
