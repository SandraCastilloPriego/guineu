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
package guineu.modules.identification.normalizationtissue;

import guineu.data.Dataset;
import guineu.taskcontrol.AbstractTask;
import guineu.taskcontrol.TaskStatus;
import guineu.util.GUIUtils;
import java.util.Hashtable;
import java.util.Vector;

/**
 *
 * @author scsandra
 */
public class NormalizeTissueFilterTask extends AbstractTask {

        private Dataset dataset;
        private NormalizeTissue serum;

        public NormalizeTissueFilterTask(Dataset simpleDataset, Vector<StandardUmol> standards, Hashtable<String, Double> weights) {
                this.dataset = simpleDataset.clone();
                this.serum = new NormalizeTissue(dataset, standards, weights);
        }

        public String getTaskDescription() {
                return "Sample Normalization Filter... ";
        }

        public double getFinishedPercentage() {
                return (double) serum.getProgress();
        }

        public void cancel() {
                setStatus(TaskStatus.CANCELED);
        }

        public void run() {
                try {
                        setStatus(TaskStatus.PROCESSING);
                        serum.normalize(getStatus());
                        if (getStatus() == TaskStatus.CANCELED || getStatus() == TaskStatus.ERROR) {
                                return;
                        }
                        dataset = serum.getDataset();
                        GUIUtils.showNewTable(dataset, true);
                        setStatus(TaskStatus.FINISHED);
                } catch (Exception e) {
                        setStatus(TaskStatus.ERROR);
                        errorMessage = e.toString();
                        return;
                }
        }
}
