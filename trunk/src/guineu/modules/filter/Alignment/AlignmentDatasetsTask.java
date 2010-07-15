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
package guineu.modules.filter.Alignment;

import guineu.data.Dataset;
import guineu.data.impl.SimpleLCMSDataset;
import guineu.desktop.Desktop;
import guineu.taskcontrol.Task;
import guineu.taskcontrol.TaskStatus;
import guineu.util.GUIUtils;

/**
 *
 * @author SCSANDRA
 */
public class AlignmentDatasetsTask implements Task {

        ;private TaskStatus status = TaskStatus.WAITING;
        private String errorMessage;
        private Alignment comb;

        public AlignmentDatasetsTask(Dataset[] datasets, Desktop desktop, AlignmentParameters parameters) {
                comb = new Alignment(datasets, parameters, desktop);
        }

        public String getTaskDescription() {
                return "Doing the alignment... ";
        }

        public double getFinishedPercentage() {
                return comb.getProgress();
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
                        this.alignment();
                } catch (Exception e) {
                        status = TaskStatus.ERROR;
                        errorMessage = e.toString();
                        return;
                }
        }

        public void alignment() {
                status = TaskStatus.PROCESSING;
                comb.run();

                SimpleLCMSDataset dataset = comb.getDataset();

                //creates internal frame with the table
                GUIUtils.showNewTable(dataset, true);


                status = TaskStatus.FINISHED;
        }
}
