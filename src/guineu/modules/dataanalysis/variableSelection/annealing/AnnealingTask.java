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
package guineu.modules.dataanalysis.variableSelection.annealing;

import guineu.data.Dataset;
import guineu.data.PeakListRow;
import guineu.modules.R.RUtilities;
import guineu.taskcontrol.AbstractTask;
import guineu.taskcontrol.TaskStatus;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.rosuda.JRI.Rengine;
import org.rosuda.JRI.REXP;

/**
 *
 * @author scsandra
 */
public class AnnealingTask extends AbstractTask {

        private Dataset dataset;
        private String parameterGroup;
        private int nVar = 3;
        private int progress = 0;

        public AnnealingTask(Dataset dataset, AnnealingParameters parameters) {
                this.dataset = dataset;
                parameterGroup = parameters.getParameter(AnnealingParameters.groups).getValue();
                nVar = parameters.getParameter(AnnealingParameters.numberOfParameters).getValue();
        }

        public String getTaskDescription() {
                return "Performing Variable selection: Annealing... ";
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
                                        "Variable selection: Annealing requires R but it couldn't be loaded (" + t.getMessage() + ')');
                        }
                        synchronized (RUtilities.R_SEMAPHORE) {

                                // Load subselect library
                                if (rEngine.eval("require(subselect)").asBool().isFALSE()) {
                                        setStatus(TaskStatus.ERROR);
                                        errorMessage = "The \"subselect\" R package couldn't be loaded - is it installed in R?";
                                }

                                // Creates the data set
                                rEngine.eval("x.train<- matrix(nrow =" + dataset.getNumberRows() + ",ncol=" + dataset.getNumberCols() + ")");

                                // Creates the description of the samples data set
                                rEngine.eval("y.train <- vector(mode=\"numeric\",length=" + dataset.getNumberCols() + ")");


                                Vector<String> columnNames = dataset.getAllColumnNames();
                                String dataColNames = "c(";

                                String dataRowNames = "c(";


                                // assing the values to the matrix
                                for (int row = 0; row < dataset.getNumberRows(); row++) {
                                        PeakListRow peakRow = dataset.getRow(row);
                                        dataRowNames += peakRow.getID() + ", ";
                                        int r = row + 1;
                                        for (int column = 0; column < dataset.getNumberCols(); column++) {
                                                int c = column + 1;

                                                String colName = columnNames.elementAt(column);
                                                double value = 0.0;
                                                try {
                                                        value = (Double) peakRow.getPeak(colName);
                                                } catch (Exception e) {
                                                        System.out.println(peakRow.getPeak(colName));
                                                }

                                                if (!Double.isInfinite(value) && !Double.isNaN(value)) {

                                                        rEngine.eval("x.train[" + r + "," + c + "] = " + value);
                                                } else {

                                                        rEngine.eval("x.train[" + r + "," + c + "] = NA");
                                                }
                                        }
                                }
                                for (int column = 0; column < dataset.getNumberCols(); column++) {
                                        int c = column + 1;
                                        String colName = columnNames.elementAt(column);
                                        dataColNames += "\"" + colName + "\", ";
                                        rEngine.eval("y.train[" + c + "]<- \"" + dataset.getParametersValue(colName, this.parameterGroup) + "\"");
                                }

                                dataColNames = dataColNames.substring(0, dataColNames.length() - 2);
                                dataColNames += ")";

                                dataRowNames = dataRowNames.substring(0, dataRowNames.length() - 2);
                                dataRowNames += ")";

                                rEngine.eval("x.train<-t(x.train)");
                                rEngine.eval("colnames(x.train)<- " + dataRowNames);
                                rEngine.eval("rownames(x.train)<- " + dataColNames);
                                //  rEngine.eval("write.csv(x.train, \"xtrain.csv\")");
                                rEngine.eval("y.train <- factor(y.train)");
                                rEngine.eval("names(y.train) <-" + dataColNames);
                                //  rEngine.eval("write.csv(matrix(y.train), \"ytrain.csv\")");
                                rEngine.eval("fullModel <- glm(y~., family=binomial, data=data.frame(\"y\"=y.train, x.train, check.names=F), control=list(\"maxit\"=50))");

                                rEngine.eval("Hmat <- glmHmat(fullModel)");
                                rEngine.eval("annealing <- anneal(Hmat$mat, 3 , " + this.nVar + ", H=Hmat$H, r=1, criterion=\"Wald\", nsol=3)");
                                // rEngine.eval("write.csv(annealing$bestsets, \"results.csv\")");
                                REXP qexp = rEngine.eval("annealing$bestsets");
                                int[] q = qexp.asIntArray();
                                if (q != null) {
                                        for (int r : q) {
                                                if (r > 0 && r < dataset.getNumberRows()) {
                                                        dataset.getRow(r - 1).setSelectionMode(true);
                                                }
                                        }
                                }
                        }
                        setStatus(TaskStatus.FINISHED);
                } catch (Exception ex) {
                        Logger.getLogger(AnnealingTask.class.getName()).log(Level.SEVERE, null, ex);
                        setStatus(TaskStatus.ERROR);
                }
        }
}
