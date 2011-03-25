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
package guineu.modules.mylly.filter.singlingFilter;

import guineu.data.DatasetType;
import guineu.data.impl.datasets.SimpleGCGCDataset;
import guineu.taskcontrol.Task;
import guineu.taskcontrol.TaskStatus;
import guineu.util.GUIUtils;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author scsandra
 */
public class SinglingFilterTask implements Task {

        private TaskStatus status = TaskStatus.WAITING;
        private String errorMessage;
        private SimpleGCGCDataset dataset;
        private double minSimilarity;
        private boolean unknownPeaks;
        private String suffix;

        public SinglingFilterTask(SimpleGCGCDataset dataset, SinglingParameters parameters) {
                this.dataset = dataset;
                minSimilarity = parameters.getParameter(SinglingParameters.similarity).getDouble();
                unknownPeaks = parameters.getParameter(SinglingParameters.unknownPeaks).getValue();
                suffix = parameters.getParameter(SinglingParameters.suffix).getValue();
        }

        public String getTaskDescription() {
                return "Filtering files with Leave Only Uniques Filter... ";
        }

        public double getFinishedPercentage() {
                return 1f;
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

                        Singling filter = new Singling(minSimilarity, unknownPeaks);
                        SimpleGCGCDataset newAlignment = filter.actualMap(dataset);
                        if (newAlignment != null) {
                                newAlignment.setDatasetName(newAlignment.toString() + suffix);
                                newAlignment.setType(DatasetType.GCGCTOF);
                                GUIUtils.showNewTable(newAlignment, true);
                        } else {
                                System.out.println("The result is null");
                        }

                        status = TaskStatus.FINISHED;
                } catch (Exception ex) {
                        Logger.getLogger(SinglingFilterTask.class.getName()).log(Level.SEVERE, null, ex);
                        status = TaskStatus.ERROR;
                }
        }
}
