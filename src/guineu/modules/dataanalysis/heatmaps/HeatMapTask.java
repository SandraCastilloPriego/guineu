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
package guineu.modules.dataanalysis.heatmaps;

import guineu.data.Dataset;
import guineu.data.PeakListRow;
import guineu.modules.R.RUtilities;
import guineu.parameters.ParameterSet;
import guineu.taskcontrol.AbstractTask;
import guineu.taskcontrol.TaskStatus;
import java.io.File;
import java.util.Vector;
import org.rosuda.JRI.Rengine;

/**
 *
 * @author scsandra
 */
public class HeatMapTask extends AbstractTask {

        private String outputType;
        private boolean log, scale, plegend;
        private double height, width, heightAvobeHM, heightHM, heightUnderHM, widthDendrogram, widthHM, columnMargin, rowMargin, starSize, rLabelSize, cLabelSize;
        private File outputFile;
        private String[] rowNames, colNames;
        private Dataset dataset;
        private String phenotype, timePoints;
        private double finishedPercentage = 0.0f;

        public HeatMapTask(Dataset dataset, ParameterSet parameters) {
                this.dataset = dataset;

                outputFile = parameters.getParameter(HeatMapParameters.fileName).getValue();
                outputType = parameters.getParameter(HeatMapParameters.fileTypeSelection).getValue();
                timePoints = parameters.getParameter(HeatMapParameters.timePoints).getValue();
                phenotype = parameters.getParameter(HeatMapParameters.phenotype).getValue();

                log = parameters.getParameter(HeatMapParameters.log).getValue();
                scale = parameters.getParameter(HeatMapParameters.scale).getValue();
                plegend = parameters.getParameter(HeatMapParameters.plegend).getValue();

                // Measures
                heightAvobeHM = parameters.getParameter(HeatMapParameters.height).getValue();
                heightHM = parameters.getParameter(HeatMapParameters.heighthm).getValue();
                heightUnderHM = parameters.getParameter(HeatMapParameters.heightuhm).getValue();
                this.height = heightAvobeHM + heightHM + heightUnderHM;

                widthDendrogram = parameters.getParameter(HeatMapParameters.widthdendrogram).getValue();
                widthHM = parameters.getParameter(HeatMapParameters.widthhm).getValue();
                this.width = widthDendrogram + widthHM;

                columnMargin = parameters.getParameter(HeatMapParameters.columnMargin).getValue();
                rowMargin = parameters.getParameter(HeatMapParameters.rowMargin).getValue();

                rLabelSize = parameters.getParameter(HeatMapParameters.rlabelSize).getValue();
                cLabelSize = parameters.getParameter(HeatMapParameters.clabelSize).getValue();

                starSize = parameters.getParameter(HeatMapParameters.star).getValue();

        }

        public String getTaskDescription() {
                return "Heat Map... ";
        }

        public double getFinishedPercentage() {
                return finishedPercentage;
        }

        public void run() {
                try {
                        setStatus(TaskStatus.PROCESSING);

                        final Rengine rEngine;
                        try {
                                rEngine = RUtilities.getREngine();
                        } catch (Throwable t) {

                                throw new IllegalStateException(
                                        "Heat map requires R but it couldn't be loaded (" + t.getMessage() + ')');
                        }

                        finishedPercentage = 0.3f;

                        synchronized (RUtilities.R_SEMAPHORE) {
                                // Load gplots library
                                if (rEngine.eval("require(gplots)").asBool().isFALSE()) {
                                        setStatus(TaskStatus.ERROR);
                                        errorMessage = "The \"gplots\" R package couldn't be loaded - is it installed in R?";
                                }


                                try {
                                        rEngine.eval("library(gplots)");

                                        if (outputType.contains("png")) {
                                                if (this.height < 500 || this.width < 500) {
                                                        this.height = 500;
                                                        this.width = 500;
                                                }
                                        }
                                        int numberOfRows = this.isAnySelected(dataset);
                                        boolean isAnySelected = true;
                                        if (numberOfRows == 0) {
                                                numberOfRows = dataset.getNumberRows();
                                                isAnySelected = false;
                                        }

                                        // Create two arrays: row and column names
                                        rowNames = new String[numberOfRows];
                                        colNames = new String[this.dataset.getNumberCols()];

                                        rEngine.eval("dataset<- matrix(\"\",nrow =" + numberOfRows + ",ncol=" + dataset.getNumberCols() + ")");

                                        rEngine.eval("pheno <- matrix(nrow =" + dataset.getNumberCols() + ",ncol=" + 3 + ")");

                                        Vector<String> columnNames = dataset.getAllColumnNames();

                                        // assing the values to the matrix
                                        int realRowIndex = 0;
                                        for (int indexRow = 0; indexRow < dataset.getNumberRows(); indexRow++) {
                                                PeakListRow row = dataset.getRow(indexRow);
                                                if ((isAnySelected && row.isSelected()) || !isAnySelected) {
                                                        this.rowNames[realRowIndex] = row.getID() + " - " + row.getName();
                                                        for (int indexColumn = 0; indexColumn < dataset.getNumberCols(); indexColumn++) {

                                                                int r = realRowIndex + 1;
                                                                int c = indexColumn + 1;

                                                                double value = (Double) row.getPeak(columnNames.elementAt(indexColumn));

                                                                if (!Double.isInfinite(value) && !Double.isNaN(value)) {

                                                                        rEngine.eval("dataset[" + r + "," + c + "] = " + value);
                                                                } else {

                                                                        rEngine.eval("dataset[" + r + "," + c + "] = NA");
                                                                }
                                                        }
                                                        realRowIndex++;
                                                }
                                        }


                                        for (int column = 0; column < dataset.getNumberCols(); column++) {
                                                int c = column + 1;
                                                String colName = columnNames.elementAt(column);
                                                this.colNames[column] = colName;
                                                rEngine.eval("pheno[" + c + ",1]<- \"" + colName + "\"");
                                                rEngine.eval("pheno[" + c + ",2]<- \"" + dataset.getParametersValue(colName, this.phenotype) + "\"");
                                                if (!this.timePoints.contains("No time Points")) {
                                                        rEngine.eval("pheno[" + c + ",3]<- \"" + dataset.getParametersValue(colName, this.timePoints) + "\"");
                                                } else {
                                                        rEngine.eval("pheno[" + c + ",3]<- \"1\"");
                                                }

                                        }

                                        long cols = rEngine.rniPutStringArray(colNames);
                                        rEngine.rniAssign("columnNames", cols, 0);
                                        rEngine.eval("rownames(pheno)<- columnNames");
                                        rEngine.eval("colnames(pheno)<- c(\"SampleName\", \"Phenotype\" , \"Time\")");
                                        rEngine.eval("pheno <- data.frame(pheno)");

                                        finishedPercentage = 0.4f;

                                        rEngine.eval("dataset <- apply(dataset, 2, as.numeric)");

                                        // Assign row names to the data set
                                        long rows = rEngine.rniPutStringArray(rowNames);
                                        rEngine.rniAssign("rowNames", rows, 0);
                                        rEngine.eval("rownames(dataset)<-rowNames");

                                        // Assign column names to the data set
                                        rEngine.eval("colnames(dataset)<- columnNames");

                                        // 0 imputation
                                        rEngine.eval("source(\"conf/colonModel.R\")");
                                        rEngine.eval("dataset <- zero.impute(dataset)");

                                        finishedPercentage = 0.5f;

                                        // Remove the rows with too many NA's. The distances between rows can't be calculated if the rows don't have
                                        // at least one sample in common.
                                        rEngine.eval("dataset <- remove.na(dataset)");

                                        finishedPercentage = 0.8f;
                                        if (this.getStatus() == TaskStatus.PROCESSING) {
                                                if (!this.timePoints.contains("No time Points")) {
                                                        // Time points and T-test. There should be only two phenotypes in each time point.
                                                        rEngine.eval("foldResult <- fold.changes.by.time(dataset,pheno)");
                                                        rEngine.eval("tResult <- ttest.by.time(dataset,pheno)");
                                                } else if (!this.plegend) {
                                                        // No t-test and not time points. The raw data set will be shown.
                                                        rEngine.eval("foldResult <- dataset");
                                                } else {
                                                        // No time points but t-test. The first sample group will be consider the reference group.
                                                        rEngine.eval("foldResult <- fold.changes.no.time(dataset,pheno)");
                                                        rEngine.eval("tResult <- ttest.no.time(dataset,pheno)");
                                                }

                                                if (this.log && this.scale) {
                                                        rEngine.eval("foldResult <- changes(foldResult, 3)");
                                                } else if (this.log && !this.scale) {
                                                        rEngine.eval("foldResult <- changes(foldResult, 1)");
                                                } else if (!this.log && this.scale) {
                                                        rEngine.eval("foldResult <- changes(foldResult, 2)");
                                                }

                                                rEngine.eval("library(JavaGD)");
                                                rEngine.eval("Sys.putenv('JAVAGD_CLASS_NAME'='guineu/modules/dataanalysis/heatmaps/HMFrame')");
                                                rEngine.eval("JavaGD()");
                                                if (this.plegend) {
                                                        rEngine.eval("final.hm(foldResult, tResult," + this.heightAvobeHM + "," + this.heightHM + "," + this.heightUnderHM + "," + this.rowMargin + "," + this.columnMargin + "," + this.widthDendrogram + "," + this.widthHM + "," + this.rLabelSize + "," + this.cLabelSize + ", notecex=" + starSize + ")");
                                                } else {
                                                        rEngine.eval("final.hm(foldResult, NULL," + this.heightAvobeHM + "," + this.heightHM + "," + this.heightUnderHM + "," + this.rowMargin + "," + this.columnMargin + "," + this.widthDendrogram + "," + this.widthHM + "," + this.rLabelSize + "," + this.cLabelSize + ")");
                                                }

                                                if (!outputType.contains("No export")) {
                                                        // Possible output file types
                                                        if (outputType.contains("pdf")) {

                                                                rEngine.eval("pdf(\"" + outputFile + "\", height=" + height + ", width=" + width + ")");
                                                        } else if (outputType.contains("fig")) {

                                                                rEngine.eval("xfig(\"" + outputFile + "\", height=" + height + ", width=" + width + ", horizontal = FALSE, pointsize = 12)");
                                                        } else if (outputType.contains("svg")) {

                                                                // Load RSvgDevice library
                                                                if (rEngine.eval("require(RSvgDevice)").asBool().isFALSE()) {

                                                                        throw new IllegalStateException("The \"RSvgDevice\" R package couldn't be loaded - is it installed in R?");
                                                                }

                                                                rEngine.eval("devSVG(\"" + outputFile + "\", height=" + height + ", width=" + width + ")");
                                                        } else if (outputType.contains("png")) {

                                                                rEngine.eval("png(\"" + outputFile + "\", height=" + height + ", width=" + width + ")");
                                                        }

                                                        if (this.plegend) {
                                                                rEngine.eval("final.hm(foldResult, tResult," + this.heightAvobeHM + "," + this.heightHM + "," + this.heightUnderHM + "," + this.rowMargin + "," + this.columnMargin + "," + this.widthDendrogram + "," + this.widthHM + "," + this.rLabelSize + "," + this.cLabelSize + ", notecex=" + starSize + ")");
                                                        } else {
                                                                rEngine.eval("final.hm(foldResult, NULL," + this.heightAvobeHM + "," + this.heightHM + "," + this.heightUnderHM + "," + this.rowMargin + "," + this.columnMargin + "," + this.widthDendrogram + "," + this.widthHM + "," + this.rLabelSize + "," + this.cLabelSize + ")");
                                                        }
                                                }

                                                rEngine.eval("dev.off()", false);

                                        }
                                        rEngine.end();
                                        finishedPercentage = 1.0f;

                                } catch (Throwable t) {

                                        throw new IllegalStateException("R error during the heat map creation", t);
                                }
                        }

                        setStatus(TaskStatus.FINISHED);

                } catch (Exception e) {
                        setStatus(TaskStatus.ERROR);
                        errorMessage = e.toString();
                        return;
                }
        }

        private int isAnySelected(Dataset dataset) {
                int cont = 0;
                for (PeakListRow row : dataset.getRows()) {
                        if (row.isSelected()) {
                                cont++;
                        }
                }
                return cont;
        }
}
