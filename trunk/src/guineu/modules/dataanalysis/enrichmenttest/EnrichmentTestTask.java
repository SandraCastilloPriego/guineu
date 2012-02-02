/*
 * Copyright 2007-2012 VTT Biotechnology
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
package guineu.modules.dataanalysis.enrichmenttest;

import guineu.data.Dataset;
import guineu.data.DatasetType;
import guineu.taskcontrol.AbstractTask;
import guineu.taskcontrol.TaskStatus;
import java.util.logging.Level;
import java.util.logging.Logger;


public class EnrichmentTestTask extends AbstractTask {

        private Dataset dataset;
        private int progress = 0;

        public EnrichmentTestTask(Dataset dataset) {
                this.dataset = dataset;
        }

        public String getTaskDescription() {
                return "Performing Enrichment test... ";
        }

        public double getFinishedPercentage() {
                return (float) progress / dataset.getNumberRows();
        }

        public void cancel() {
                setStatus(TaskStatus.CANCELED);
        }

        public void run() {
                setStatus(TaskStatus.PROCESSING);
                try {
                        if (dataset.getType() != DatasetType.GCGCTOF || dataset.getType() != DatasetType.EXPRESSION) {
                                setStatus(TaskStatus.ERROR);
                                errorMessage = "This method can only be applied to GCxGC-MS and Expression data.";
                        }

                        



                        setStatus(TaskStatus.FINISHED);
                } catch (Exception ex) {
                        Logger.getLogger(EnrichmentTestTask.class.getName()).log(Level.SEVERE, null, ex);
                        setStatus(TaskStatus.ERROR);
                }
        }

       
}
