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
package guineu.modules.dataanalysis.kstest;

import guineu.data.Dataset;
import guineu.data.PeakListRow;
import guineu.modules.R.RUtilities;
import guineu.taskcontrol.AbstractTask;
import guineu.taskcontrol.TaskStatus;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;
import org.rosuda.JRI.REXP;
import org.rosuda.JRI.Rengine;

/**
 *
 * @author scsandra
 */
public class KSTestTask extends AbstractTask {

        private Dataset dataset;
        private double progress;

        public KSTestTask(Dataset dataset) {
                this.dataset = dataset;
        }

        public String getTaskDescription() {
                return "Kolmogorov-Smirnov Test... ";
        }

        public double getFinishedPercentage() {
                return progress;
        }

        public void cancel() {
                setStatus(TaskStatus.CANCELED);
        }

        public void run() {
                try {
                        final Rengine rEngine;
                        try {
                                rEngine = RUtilities.getREngine();
                        } catch (Throwable t) {

                                throw new IllegalStateException(
                                        "Kolmogorov-Smirnov test requires R but it couldn't be loaded (" + t.getMessage() + ')');
                        }
                        synchronized (RUtilities.R_SEMAPHORE) {

                                DescriptiveStatistics stats = new DescriptiveStatistics();
                                // assing the values to the matrix
                                for (int row = 0; row < dataset.getNumberRows(); row++) {
                                        rEngine.eval("x <- vector(mode=\"numeric\",length=" + dataset.getNumberCols() + ")");
                                        stats.clear();
                                        PeakListRow peakListRow = dataset.getRow(row);
                                        for (int c = 0; c < dataset.getNumberCols(); c++) {
                                                int r = c + 1;
                                                double value = (Double) peakListRow.getPeak(dataset.getAllColumnNames().elementAt(c));
                                                rEngine.eval("x[" + r + "] <- " + value);
                                                stats.addValue(value);
                                        }

                                        rEngine.eval("y <- rnorm(" + dataset.getNumberCols() + ", mean= " + stats.getMean() + ", sd = " + stats.getStandardDeviation() + ")");

                                        rEngine.eval("result <- ks.test(x,y)");
                                        long e = rEngine.rniParse("result$p.value", 1);
                                        long r = rEngine.rniEval(e, 0);
                                        REXP x = new REXP(rEngine, r);
                                        double pValue = x.asDouble();
                                        dataset.getRow(row).setVar("setPValue", pValue);
                                        if (peakListRow.getID() == 68) {
                                                rEngine.eval("write.csv(x, \"x.csv\"");
                                        }
                                }

                        }
                        rEngine.end();
                        setStatus(TaskStatus.FINISHED);
                } catch (Exception ex) {
                        Logger.getLogger(KSTestTask.class.getName()).log(Level.SEVERE, null, ex);
                        setStatus(TaskStatus.ERROR);
                }
        }
}
