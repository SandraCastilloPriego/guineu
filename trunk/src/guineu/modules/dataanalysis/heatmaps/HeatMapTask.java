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
package guineu.modules.dataanalysis.heatmaps;

import guineu.data.Dataset;
import guineu.data.PeakListRow;
import guineu.main.GuineuCore;
import guineu.modules.R.RUtilities;
import guineu.parameters.ParameterSet;
import guineu.taskcontrol.AbstractTask;
import guineu.taskcontrol.TaskStatus;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.File;
import java.util.Vector;
import javax.swing.JInternalFrame;
import org.rosuda.JRI.REXP;
import org.rosuda.JRI.Rengine;
import org.rosuda.javaGD.GDCanvas;

/**
 *
 * @author scsandra
 */
public class HeatMapTask extends AbstractTask {

        private String outputType;
        private boolean log, scale, plegend;
        //private double height, width, heightAvobeHM, heightHM, heightUnderHM, widthDendrogram, widthHM, columnMargin, rowMargin,  starSize, rLabelSize, cLabelSize*/;
        private File outputFile;
        private String[] rowNames, colNames;
        private Dataset dataset;
        private String phenotype, timePoints;
        private double finishedPercentage = 0.0f;
        public static GDCanvas _gdc;

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
             /*   heightAvobeHM = parameters.getParameter(HeatMapParameters.height).getValue();
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

                starSize = parameters.getParameter(HeatMapParameters.star).getValue();*/

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

                                        // Source R script
                                        rEngine.eval("source(\"conf/colonModel.R\")");

                                        // 0 imputation                                        
                                        rEngine.eval("dataset <- zero.impute(dataset)");

                                        finishedPercentage = 0.5f;

                                        // Remove the rows with too many NA's. The distances between rows can't be calculated if the rows don't have
                                        // at least one sample in common.
                                        rEngine.eval("dataset <- remove.na(dataset)");
                                        // rEngine.eval("write.csv(dataset, \"dataset2.csv\")");
                                        // rEngine.eval("write.csv(pheno, \"pheno2.csv\")");

                                        finishedPercentage = 0.8f;
                                        if (this.getStatus() == TaskStatus.PROCESSING) {
                                                if (!this.timePoints.contains("No time Points")) {
                                                        // Time points and T-test. There should be only two phenotypes in each time point.
                                                        rEngine.eval("foldResult <- fold.changes.by.time(data.frame(dataset),data.frame(pheno))");
                                                        rEngine.eval("tResult <- ttest.by.time(data.frame(dataset),data.frame(pheno))");
                                                } else if (!this.plegend) {
                                                        // No t-test and not time points. The raw data set will be shown.
                                                        rEngine.eval("foldResult <- dataset");
                                                } else {
                                                        // No time points but t-test. The first sample group will be consider the reference group.
                                                        rEngine.eval("foldResult <- fold.changes.no.time(data.frame(dataset),data.frame(pheno))");
                                                        rEngine.eval("tResult <- ttest.no.time(data.frame(dataset),data.frame(pheno))");
                                                }

                                                if (this.log && this.scale) {
                                                        rEngine.eval("foldResult <- changes(foldResult, 3)");
                                                } else if (this.log && !this.scale) {
                                                        rEngine.eval("foldResult <- changes(foldResult, 1)");
                                                } else if (!this.log && this.scale) {
                                                        rEngine.eval("foldResult <- changes(foldResult, 2)");
                                                }

                                                //rEngine.eval("write.csv(foldResult, \"dataset4.csv\")");
                                                //  rEngine.eval("write.csv(tResult, \"pvalues.csv\")");
                                                //Sizes
                                                rEngine.eval("hahm <- 1");
                                                rEngine.eval("hhm = 5*log(nrow(foldResult))");
                                                rEngine.eval("huhm = 1.5+0.05*max(nchar(colnames(foldResult)))");
                                                rEngine.eval("rowmargin = 0.55*max(nchar(rownames(foldResult)))");
                                                rEngine.eval("colmargin = 0.55*max(nchar(colnames(foldResult)))");
                                                rEngine.eval("wd=1");
                                                rEngine.eval("whm= 5*log(ncol(foldResult))");

                                                rEngine.eval("width <- 96*(wd+whm+0.1*rowmargin)");
                                                rEngine.eval("height <-96*(hahm+hhm+huhm+0.1*colmargin) ");

                                                long e = rEngine.rniParse("width", 1);
                                                long r = rEngine.rniEval(e, 0);
                                                REXP x = new REXP(rEngine, r);
                                                double width = x.asDouble() * 0.5;

                                                e = rEngine.rniParse("height", 1);
                                                r = rEngine.rniEval(e, 0);
                                                x = new REXP(rEngine, r);
                                                double height = x.asDouble() * 0.5;
                                              

                                                // Window
                                                _gdc = new GDCanvas(width, height);
                                                JInternalFrame f = new JInternalFrame("Heat map", true, true, true, true);
                                                f.getContentPane().setLayout(new BorderLayout());
                                                f.getContentPane().add(_gdc, BorderLayout.PAGE_END);
                                                f.pack();
                                                GuineuCore.getDesktop().addInternalFrame(f);

                                                rEngine.eval("library(JavaGD)");
                                                rEngine.eval("Sys.putenv('JAVAGD_CLASS_NAME'='guineu/modules/dataanalysis/heatmaps/HMFrame')");
                                                rEngine.eval("JavaGD(width=96*(wd+whm+0.1*rowmargin), height=96*(hahm+hhm+huhm+0.1*colmargin),ps=1)");
                                                if (this.plegend) {
                                                        rEngine.eval("final.hm(foldResult, tResult)");
                                                } else {
                                                        rEngine.eval("final.hm(foldResult, NULL)");
                                                }

                                                //    rEngine.eval("write.csv(foldResult, \"results.csv\")");
                                                //    rEngine.eval("write.csv(tResult, \"pvalues.csv\")");

                                                if (!outputType.contains("No export")) {
                                                        if (this.plegend) {
                                                                rEngine.eval("final.hm(foldResult, tResult, xlas=1, device = \"" + this.outputType + "\", OutputFileName=\"" + this.outputFile.getAbsolutePath() + "\")");
                                                        } else {
                                                                rEngine.eval("final.hm(foldResult, NULL, xlas=1, device = \"" + this.outputType + "\", OutputFileName=\"" + this.outputFile.getAbsolutePath() + "\")");
                                                        }
                                                }

                                                _gdc.setSize(new Dimension((int) width, (int) height));
                                                _gdc.initRefresh();

                                                //rEngine.eval("dev.off()", false);

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
