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
package guineu.modules.identification.normalizationNOMIS;

import guineu.data.Dataset;
import guineu.data.impl.datasets.SimpleLCMSDataset;
import guineu.taskcontrol.AbstractTask;
import guineu.taskcontrol.TaskStatus;

/**
 *
 * @author scsandra
 */
public class NormalizeNOMISFilterTask extends AbstractTask {

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

        public void cancel() {
                setStatus(TaskStatus.CANCELED);
        }

        public void run() {
                try {
                        setStatus(TaskStatus.PROCESSING);
                        serum.normalize();
                        setStatus(TaskStatus.FINISHED);
                } catch (Exception e) {
                        setStatus(TaskStatus.ERROR);
                        errorMessage = e.toString();
                        return;
                }
        }
}
