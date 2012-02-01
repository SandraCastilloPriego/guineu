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
package guineu.modules.visualization.Rintensityboxplot;

import guineu.data.Dataset;
import guineu.data.PeakListRow;
import guineu.main.GuineuCore;
import guineu.modules.R.RUtilities;
import guineu.parameters.ParameterSet;
import guineu.taskcontrol.AbstractTask;
import guineu.taskcontrol.TaskStatus;
import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JInternalFrame;
import org.rosuda.JRI.Rengine;
import org.rosuda.javaGD.GDCanvas;

/**
 *
 * @author scsandra
 */
public class BoxPlotTask extends AbstractTask {

        private String selectedFiles[], rowNames[], factors[], colors[];
        private PeakListRow selectedRows[];
        private String xAxisValueSource, colorSource;
        private double finishedPercentage = 0.0f;
        private Dataset dataset;
        private String plotName = "Box plot of concentrations";
        public static GDCanvas _gdc;
        private String fileName, fileType;
        private int height, width;

        public BoxPlotTask(Dataset dataset, ParameterSet parameters) {

                this.fileName = (String) parameters.getParameter(
                        BoxPlotParameters.fileName).getValue().getAbsolutePath();

                this.fileType = (String) parameters.getParameter(
                        BoxPlotParameters.fileTypeSelection).getValue();

                this.xAxisValueSource = (String) parameters.getParameter(
                        BoxPlotParameters.xAxisValueSource).getValue();

                this.colorSource = (String) parameters.getParameter(
                        BoxPlotParameters.colorValueSource).getValue();
               
                this.selectedFiles = parameters.getParameter(
                        BoxPlotParameters.samples).getValue();

                this.selectedRows = parameters.getParameter(
                        BoxPlotParameters.selectedRows).getValue();

                this.plotName = parameters.getParameter(
                        BoxPlotParameters.name).getValue();

                this.height = parameters.getParameter(
                        BoxPlotParameters.height).getValue();

                this.width = parameters.getParameter(
                        BoxPlotParameters.width).getValue();

                this.dataset = dataset;

        }

        public String getTaskDescription() {
                return "Box plot... ";
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
                                        "Box plot requires R but it couldn't be loaded (" + t.getMessage() + ')');
                        }

                        finishedPercentage = 0.3f;

                        synchronized (RUtilities.R_SEMAPHORE) {


                                int numberOfRows = selectedRows.length;
                                // Create two arrays: row and column names
                                rowNames = new String[selectedRows.length];
                                if (this.xAxisValueSource.equals("Sample")) {
                                        this.factors = new String[this.selectedFiles.length];
                                        System.arraycopy(this.selectedFiles, 0, this.factors, 0, this.selectedFiles.length);
                                } else {
                                        this.factors = new String[this.selectedFiles.length];
                                        for (int i = 0; i < this.selectedFiles.length; i++) {
                                                this.factors[i] = dataset.getParametersValue(this.selectedFiles[i], this.xAxisValueSource);
                                        }
                                }


                                if (this.xAxisValueSource.equals("No color")) {
                                        this.colors = this.selectedFiles;
                                } else {
                                        this.colors = new String[this.selectedFiles.length];
                                        for (int i = 0; i < this.selectedFiles.length; i++) {
                                                this.colors[i] = dataset.getParametersValue(this.selectedFiles[i], this.colorSource);
                                        }
                                }

                                rEngine.eval("dataset<- matrix(\"\",nrow =" + numberOfRows + ",ncol=" + this.selectedFiles.length + ")");


                                // assing the values to the matrix                              

                                int realRowIndex = 0;
                                for (int indexRow = 0; indexRow < numberOfRows; indexRow++) {
                                        PeakListRow row = this.selectedRows[indexRow];

                                        this.rowNames[realRowIndex] = row.getID() + " - " + row.getName();
                                        for (int indexColumn = 0; indexColumn < this.selectedFiles.length; indexColumn++) {

                                                int r = realRowIndex + 1;
                                                int c = indexColumn + 1;
                                                double value = 0;

                                                value = (Double) row.getPeak(this.selectedFiles[indexColumn]);


                                                if (!Double.isInfinite(value) && !Double.isNaN(value)) {

                                                        rEngine.eval("dataset[" + r + "," + c + "] = " + value);
                                                } else {

                                                        rEngine.eval("dataset[" + r + "," + c + "] = NA");
                                                }
                                        }
                                        realRowIndex++;

                                }

                                long cols = rEngine.rniPutStringArray(this.selectedFiles);
                                rEngine.rniAssign("columnNames", cols, 0);


                                long factor = rEngine.rniPutStringArray(this.factors);
                                rEngine.rniAssign("factors", factor, 0);

                                long color = rEngine.rniPutStringArray(this.colors);
                                rEngine.rniAssign("colors", color, 0);

                                finishedPercentage = 0.4f;

                                // Assign row names to the data set
                                long rows = rEngine.rniPutStringArray(rowNames);
                                rEngine.rniAssign("rowNames", rows, 0);
                                rEngine.eval("rownames(dataset)<-rowNames");

                                // Assign column names to the data set
                                rEngine.eval("colnames(dataset)<- columnNames");

                                rEngine.eval("source(\"conf/boxPlot.R\")");
                                
                                _gdc = new GDCanvas(this.width, this.height);
                                JInternalFrame f = new JInternalFrame(this.plotName, true, true, true, true);
                                f.getContentPane().setLayout(new BorderLayout());
                                f.getContentPane().add(_gdc, BorderLayout.PAGE_END);
                                f.pack();
                                GuineuCore.getDesktop().addInternalFrame(f);


                                rEngine.eval("library(\"JavaGD\")");
                                rEngine.eval("Sys.putenv('JAVAGD_CLASS_NAME'='guineu/modules/visualization/Rintensityboxplot/BoxFrame')");
                                rEngine.eval("JavaGD()");

                                rEngine.eval("my.boxplot2(as.data.frame(dataset), factor(factors), colors=factor(colors), title=\"" + this.plotName + "\")");

                                finishedPercentage = 0.8f;

                                if (!this.fileType.equals("No export")) {
                                        try {
                                                rEngine.eval("my.boxplot2(as.data.frame(dataset), factor(factors), colors=factor(colors), title=\"" + this.plotName + "\", device =\"" + this.fileType + "\", OutputFileName=\"" + this.fileName + "\"  )");

                                        } catch (Exception e) {
                                        }
                                }                               
                                _gdc.setSize(new Dimension(this.width, this.height));
                                _gdc.initRefresh();                               

                        }
                        rEngine.end();
                        finishedPercentage = 1.0f;


                        setStatus(TaskStatus.FINISHED);

                } catch (Exception e) {
                        setStatus(TaskStatus.ERROR);
                        errorMessage = e.toString();
                        return;
                }
        }       
}
