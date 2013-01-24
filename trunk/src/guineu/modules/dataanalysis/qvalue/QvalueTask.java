/*
 * Copyright 2007-2013 VTT Biotechnology
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
package guineu.modules.dataanalysis.qvalue;

import guineu.data.Dataset;
import guineu.data.PeakListRow;
import guineu.modules.R.RUtilities;
import guineu.taskcontrol.AbstractTask;
import guineu.taskcontrol.TaskStatus;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.rosuda.JRI.REXP;
import org.rosuda.JRI.Rengine;

/**
 *
 * @author scsandra
 */
public class QvalueTask extends AbstractTask {

        private Dataset dataset;
        private int progress = 0;

        public QvalueTask(Dataset dataset) {
                this.dataset = dataset;
        }

        public String getTaskDescription() {
                return "Calculating q-value... ";
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
                        final Rengine rEngine;
                        try {
                                rEngine = RUtilities.getREngine();
                        } catch (Throwable t) {

                                throw new IllegalStateException(
                                        "q-value test requires R but it couldn't be loaded (" + t.getMessage() + ')');
                        }
                        synchronized (RUtilities.R_SEMAPHORE) {

                                rEngine.eval("p <- vector(mode=\"numeric\",length=" + dataset.getNumberRows() + ")");


                                // assing the values to the matrix
                                for (int row = 0; row < dataset.getNumberRows(); row++) {
                                        int r = row + 1;
                                        rEngine.eval("p[" + r + "] <- " + dataset.getRow(row).getVar("getPValue"));
                                }

                                rEngine.eval("fdr <- p.adjust(p, method=\"BH\")");
                                REXP qexp = rEngine.eval("fdr");
                                double[] q = qexp.asDoubleArray();

                                for (int row = 0; row < dataset.getNumberRows(); row++) {
                                        PeakListRow r = this.dataset.getRow(row);
                                        r.setVar("setQValue", q[row]);
                                        if ((Double) r.getVar("getPValue") < 0.05 && (Double) r.getVar("getQValue") < 0.05) {
                                                this.dataset.getRow(row).setSelectionMode(true);
                                        }
                                }
                        }
                        rEngine.end();
                        setStatus(TaskStatus.FINISHED);
                } catch (Exception ex) {
                        Logger.getLogger(QvalueTask.class.getName()).log(Level.SEVERE, null, ex);
                        setStatus(TaskStatus.ERROR);
                }
        }
}
