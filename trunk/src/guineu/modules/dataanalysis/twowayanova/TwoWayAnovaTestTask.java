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
package guineu.modules.dataanalysis.twowayanova;

import guineu.data.Dataset;
import guineu.data.PeakListRow;
import guineu.modules.R.RUtilities;
import guineu.taskcontrol.AbstractTask;
import guineu.taskcontrol.TaskStatus;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.rosuda.JRI.REXP;
import org.rosuda.JRI.Rengine;

/**
 *
 * @author scsandra
 */
public class TwoWayAnovaTestTask extends AbstractTask {

        private Dataset dataset;
        private String parameterGroup, parameterTime;
        private int progress = 0;

        public TwoWayAnovaTestTask(Dataset dataset, TwoWayAnovaParameters parameters) {
                this.dataset = dataset;
                parameterGroup = parameters.getParameter(TwoWayAnovaParameters.groups).getValue();
                parameterTime = parameters.getParameter(TwoWayAnovaParameters.timePoints).getValue();
        }

        public String getTaskDescription() {
                return "Performing Anova test... ";
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
                                        "2 way-Anova requires R but it couldn't be loaded (" + t.getMessage() + ')');
                        }
                        synchronized (RUtilities.R_SEMAPHORE) {
                                // Creates the data set
                                rEngine.eval("dataset<- matrix(nrow =" + dataset.getNumberRows() + ",ncol=" + dataset.getNumberCols() + ")");

                                // Creates the description of the samples data set
                                rEngine.eval("pheno <- matrix(nrow =" + dataset.getNumberCols() + ",ncol=" + 3 + ")");


                                List<String> columnNames = dataset.getAllColumnNames();
                                String dataColNames = "c(";

                                // assing the values to the matrix
                                for (int row = 0; row < dataset.getNumberRows(); row++) {
                                        PeakListRow peakRow = dataset.getRow(row);
                                        int r = row + 1;
                                        for (int column = 0; column < dataset.getNumberCols(); column++) {
                                                int c = column + 1;

                                                String colName = columnNames.get(column);
                                                double value = 0.0;
                                                try {
                                                        value = (Double) peakRow.getPeak(colName);
                                                } catch (Exception e) {
                                                        System.out.println(peakRow.getPeak(colName));
                                                }

                                                if (!Double.isInfinite(value) && !Double.isNaN(value)) {

                                                        rEngine.eval("dataset[" + r + "," + c + "] = " + value);
                                                } else {

                                                        rEngine.eval("dataset[" + r + "," + c + "] = NA");
                                                }
                                        }
                                }
                                for (int column = 0; column < dataset.getNumberCols(); column++) {
                                        int c = column + 1;
                                        String colName = columnNames.get(column);
                                        dataColNames += "\"" + colName + "\", ";
                                        rEngine.eval("pheno[" + c + ",1]<- \"" + colName + "\"");
                                        rEngine.eval("pheno[" + c + ",2]<- \"" + dataset.getParametersValue(colName, this.parameterGroup) + "\"");
                                        rEngine.eval("pheno[" + c + ",3]<- \"" + dataset.getParametersValue(colName, this.parameterTime) + "\"");

                                }

                                dataColNames = dataColNames.substring(0, dataColNames.length() - 2);
                                dataColNames += ")";

                                rEngine.eval("colnames(dataset)<- " + dataColNames);

                                rEngine.eval("rownames(pheno)<- " + dataColNames);
                                rEngine.eval("colnames(pheno)<- c(\"SampleName\", \"Phenotype\" , \"Time\")");
                                rEngine.eval("pheno <- data.frame(pheno)");

                                rEngine.eval("p <- vector(mode=\"numeric\",length=nrow(dataset))");

                                for (int row = 1; row < dataset.getNumberRows() + 1; row++) {
                                        try {
                                                rEngine.eval("one.2 <- data.frame(\"Profile\"=as.numeric(unlist(dataset[" + row + ",])),\"Phenotype\"=factor(pheno[,\"Phenotype\"]), \"Time\"=factor(pheno[,\"Time\"]))");
                                                rEngine.eval("one.aov <- aov(Profile ~ Phenotype*Time, data=one.2)");
                                                rEngine.eval("p[" + row + "]<- summary(one.aov)[[1]][\"Phenotype:Time\", \"Pr(>F)\"] ");

                                                REXP x = rEngine.eval("p[" + row + "]");
                                                double p = x.asDouble();
                                                this.dataset.getRow(row - 1).setVar("setPValue", p);
                                        } catch (Exception ex) {
                                        }
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
                        Logger.getLogger(TwoWayAnovaTestTask.class.getName()).log(Level.SEVERE, null, ex);
                        setStatus(TaskStatus.ERROR);
                }
        }
}
