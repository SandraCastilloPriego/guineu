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
package guineu.modules.R.heatmaps;

import guineu.data.Dataset;
import guineu.data.PeakListRow;
import guineu.database.intro.InDataBase;
import guineu.database.intro.impl.InOracle;
import guineu.parameters.ParameterSet;
import guineu.taskcontrol.Task;
import guineu.taskcontrol.TaskStatus;
import guineu.util.components.FileUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;
import org.rosuda.JRI.Rengine;

/**
 *
 * @author scsandra
 */
public class HeatMapTask implements Task {

        private TaskStatus status = TaskStatus.WAITING;
        private String errorMessage;
        private InDataBase db;
        private String outputType;
        private boolean log, rcontrol, scale, plegend;
        private int height, width, columnMargin, rowMargin;
        private int maxLength = 0;
        private Rengine re;

        public HeatMapTask(Dataset dataset, ParameterSet parameters, Rengine re) {
                this.re = re;
                outputType = parameters.getParameter(HeatMapParameters.fileTypeSelection).getValue();
                String parameterName = parameters.getParameter(HeatMapParameters.selectionData).getValue();

                log = parameters.getParameter(HeatMapParameters.log).getValue();
                scale = parameters.getParameter(HeatMapParameters.scale).getValue();
                rcontrol = parameters.getParameter(HeatMapParameters.rcontrol).getValue();
                plegend = parameters.getParameter(HeatMapParameters.plegend).getValue();

                height = parameters.getParameter(HeatMapParameters.height).getInt();
                width = parameters.getParameter(HeatMapParameters.width).getInt();
                columnMargin = parameters.getParameter(HeatMapParameters.columnMargin).getInt();
                rowMargin = parameters.getParameter(HeatMapParameters.rowMargin).getInt();

                Dataset newDataset = dataset;
                if (parameterName != null) {
                        newDataset = this.modifyDataset(dataset, parameterName);
                }
                db = new InOracle();
                db.WriteCommaSeparatedFile(newDataset, "temp.csv", null);
        }

        public String getTaskDescription() {
                return "Heat Map... ";
        }

        public double getFinishedPercentage() {

                return 0.0f;

        }

        public TaskStatus getStatus() {
                return status;
        }

        public String getErrorMessage() {
                return errorMessage;
        }

        public void cancel() {
                status = TaskStatus.CANCELED;
        }

        public void run() {
                try {
                        status = TaskStatus.PROCESSING;


                        String marginParameter = "margins = c(" + this.columnMargin + "," + this.rowMargin + ")";

                        re.eval("dataset1<-read.csv(file=\"temp.csv\",head=TRUE, sep=\",\")", false);
                        re.eval("dataset<-dataset1[,-c(1:15)]", false);
                        re.eval("rownames(dataset)<-paste(dataset1[,2], dataset1[,5], sep=\"_\")", false);
                        re.eval("library(gplots)", false);
                        re.eval("x<-data.matrix(dataset, rownames.force=NA)", false);
                        if (this.outputType.contains("pdf")) {
                                re.eval("pdf(\"temp.pdf\", height=" + this.height + ", width=" + this.width + ")", false);
                                re.eval("heatmap.2(x," + marginParameter + ", trace=\"none\", col=bluered(256))", false);
                                re.eval("dev.off()", false);
                                /* re.eval("png(\"preview.png\", height=1000, width=1000)", false);
                                re.eval("heatmap.2(x," + marginParameter + ", trace=\"none\", col=bluered(256))", false);
                                re.eval("dev.off()", false);
                                ImageFrame preview = new ImageFrame("preview.png", 1000, 1000);*/

                        } else if (this.outputType.contains("fig")) {
                                re.eval("xfig(\"temp.fig\", height=" + this.height + ", width=" + this.width + ", horizontal = FALSE, pointsize = 12)", false);
                                re.eval("heatmap.2(x," + marginParameter + ", trace=\"none\", col=bluered(256))", false);
                                re.eval("dev.off()", false);
                                re.eval("system(\"fig2dev -L emf temp.fig > temp.emf\"", false);
                                /*re.eval("png(\"preview.png\", height=1000, width=1000)", false);
                                re.eval("heatmap.2(x," + marginParameter + ", trace=\"none\", col=bluered(256))", false);
                                re.eval("dev.off()", false);
                                ImageFrame preview = new ImageFrame("preview.png", 1000, 1000);*/

                        } else if (this.outputType.contains("svg")) {
                                re.eval("library(\"RSvgDevice\")", false);
                                re.eval("devSVG(\"temp.svg\", height=" + this.height + ", width=" + this.width + ")", false);
                                re.eval("heatmap.2(x," + marginParameter + ",trace=\"none\", col=bluered(256))", false);
                                re.eval("dev.off()", false);
                                /*re.eval("png(\"preview.png\", height=1000, width=1000)", false);
                                re.eval("heatmap.2(x," + marginParameter + ", trace=\"none\", col=bluered(256))", false);
                                re.eval("dev.off()", false);
                                ImageFrame preview = new ImageFrame("preview.png", 1000, 1000);*/

                        } else if (this.outputType.contains("png")) {
                                re.eval("png(\"temp.png\", height=" + this.height + ", width=" + this.width + ")", false);
                                re.eval("heatmap.2(x," + marginParameter + ", trace=\"none\", col=bluered(256))", false);
                                re.eval("dev.off()", false);
                                //   ImageFrame preview = new ImageFrame("temp.png", this.height, this.width);
                        }


                        re.end();
                        File f = new File("temp.csv");
                        f.delete();
                        /* try {
                        f = new File("preview.png");
                        f.deleteOnExit();
                        } catch (Exception e) {
                        }*/
                        status = TaskStatus.FINISHED;

                } catch (Exception e) {
                        status = TaskStatus.ERROR;

                        errorMessage = e.toString();
                        return;
                }
        }

        private Dataset modifyDataset(Dataset dataset, String parameterName) {
                Vector<String> columnNames = dataset.getAllColumnNames();
                Dataset newDataset = FileUtils.getDataset(dataset, "");
                List<String> controlNames = new ArrayList<String>();

                for (String name : columnNames) {
                        try {
                                if (dataset.getParametersValue(name, parameterName).equals("control") || dataset.getParametersValue(name, parameterName).equals("Control")) {
                                        controlNames.add(name);
                                        if (rcontrol) {
                                                newDataset.addColumnName(name);
                                        }
                                } else {
                                        newDataset.addColumnName(name);
                                }
                                // gets the maximum lenght of the sample names
                                if (name.length() > maxLength) {
                                        maxLength = name.length();
                                }
                        } catch (NullPointerException e) {
                        }
                }
                if (controlNames.isEmpty()) {
                        newDataset = dataset.clone();
                }

                double average = 0;
                for (PeakListRow row : dataset.getRows()) {
                        if (row.isSelected()) {
                                PeakListRow newRow = row.clone();
                               /* if (!rcontrol) {
                                        newRow.removeNoSamplePeaks(controlNames.toArray(new String[0]));
                                }*/
                                newDataset.addRow(newRow);

                                for (String controlColumnName : controlNames) {
                                        average += (Double) row.getPeak(controlColumnName);
                                }
                                average /= controlNames.size();

                                for (String columnName : columnNames) {
                                        double peakValue = ((Double) newRow.getPeak(columnName)) / average;
                                        if (log) {
                                                peakValue = Math.log(peakValue);
                                        }

                                        newRow.setPeak(columnName, peakValue);
                                }
                        }
                }

                // if the user didn't selecte any concrete row, all the rows are considered to be selected
                if (newDataset.getNumberRows() == 0) {
                        for (PeakListRow row : dataset.getRows()) {

                                PeakListRow newRow = row.clone();
                                newDataset.addRow(newRow);
                                for (String controlColumnName : controlNames) {
                                        average += (Double) newRow.getPeak(controlColumnName);
                                }
                                average /= controlNames.size();

                                for (String columnName : columnNames) {
                                        double peakValue = ((Double) newRow.getPeak(columnName)) / average;
                                        if (log) {
                                                peakValue = Math.log(peakValue);
                                        }

                                        newRow.setPeak(columnName, peakValue);
                                }

                        }
                }

                // scale with std dev of each column
                if (scale) {
                        DescriptiveStatistics stdDevStats = new DescriptiveStatistics();
                        for (String name : columnNames) {
                                for (PeakListRow row : newDataset.getRows()) {
                                        stdDevStats.addValue((Double) row.getPeak(name));
                                }
                        }
                        double stdDev = stdDevStats.getStandardDeviation();
                        for (String name : columnNames) {
                                for (PeakListRow row : newDataset.getRows()) {
                                        row.setPeak(name, ((Double) row.getPeak(name)) / stdDev);
                                }
                        }
                }
                return newDataset;
        }
}
