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

import com.csvreader.CsvWriter;
import guineu.data.Dataset;
import guineu.data.PeakListRow;
import guineu.database.intro.InDataBase;
import guineu.database.intro.impl.InOracle;
import guineu.parameters.ParameterSet;
import guineu.taskcontrol.Task;
import guineu.taskcontrol.TaskStatus;
import guineu.util.components.FileUtils;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.math.MathException;
import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math.stat.inference.TTestImpl;
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
        private int height, width, columnMargin, rowMargin, starSize;
        private int maxLength = 0;
        private File outputFile;
        private Rengine re;
        private List<String[]> significance;

        public HeatMapTask(Dataset dataset, ParameterSet parameters, Rengine re) {
                this.re = re;
                outputFile = parameters.getParameter(HeatMapParameters.fileName).getValue();
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
                starSize = parameters.getParameter(HeatMapParameters.star).getInt();

                Dataset newDataset = dataset;
                if (parameterName != null) {
                        if (plegend) {
                                newDataset = this.GroupingDataset(dataset, parameterName);
                        } else {
                                newDataset = this.modifyDataset(dataset, parameterName);
                        }
                }
                db = new InOracle();
                db.WriteCommaSeparatedFile(newDataset, "temp.csv", null);

                if (plegend) {
                        this.saveTemporalSignificaceFile(this.significance);
                }
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

                        re.eval("br<-c(seq(from=min(x,na.rm=T),to=0,length.out=256),seq(from=0,to=max(x,na.rm=T),length.out=256))", false);

                        if (plegend) {
                                re.eval("stars<-read.csv(file=\"stars.csv\",head=FALSE, sep=\",\")", false);
                        }


                        if (this.outputType.contains("pdf")) {
                                re.eval("pdf(\"" + this.outputFile + "\", height=" + this.height + ", width=" + this.width + ")", false);

                        } else if (this.outputType.contains("fig")) {
                                re.eval("xfig(\"" + this.outputFile + "\", height=" + this.height + ", width=" + this.width + ", horizontal = FALSE, pointsize = 12)", false);
                        } else if (this.outputType.contains("svg")) {
                                if (!this.outputFile.getAbsolutePath().contains(".svg")) {
                                        this.outputFile = new File(this.outputFile.getAbsolutePath() + ".svg");
                                }
                                re.eval("library(\"RSvgDevice\")", false);
                                re.eval("devSVG(\"" + this.outputFile + "\", height=" + this.height + ", width=" + this.width + ")", false);
                        } else if (this.outputType.contains("png")) {
                                if (!this.outputFile.getAbsolutePath().contains(".png")) {
                                        this.outputFile = new File(this.outputFile.getAbsolutePath() + ".png");
                                }
                                re.eval("png(\"" + this.outputFile + "\", height=" + this.height + ", width=" + this.width + ")", false);
                        }
                        if (plegend) {
                                re.eval("heatmap.2(x," + marginParameter + ", trace=\"none\", col=bluered(length(br)-1), breaks=br, cellnote=stars, notecol=\"black\", notecex=" + starSize + ")", false);
                        } else {
                                re.eval("heatmap.2(x," + marginParameter + ", trace=\"none\", col=bluered(length(br)-1), breaks=br)", false);
                        }
                        re.eval("dev.off()", false);

                        re.end();
                        File f = new File("temp.csv");
                        f.delete();
                        f = new File("stars.csv");
                        f.delete();

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

        private Dataset GroupingDataset(Dataset dataset, String parameterName) {
                Dataset newDataset = FileUtils.getDataset(dataset, "");
                Vector<String> groups = dataset.getParameterAvailableValues(parameterName);
                DescriptiveStatistics meanControlStats = new DescriptiveStatistics();
                DescriptiveStatistics meanGroupStats = new DescriptiveStatistics();
                significance = new ArrayList<String[]>();



                for (String group : groups) {
                        if (group.equals("Control") || group.equals("control")) {
                                if (rcontrol) {
                                        newDataset.addColumnName(group);
                                }
                        } else {
                                newDataset.addColumnName(group);
                        }
                }

                int rowCount = 0;
                for (PeakListRow row : dataset.getRows()) {
                        if (row.isSelected()) {
                                int size = 0;
                                if (rcontrol) {
                                        size = groups.size();
                                } else {
                                        size = groups.size() - 1;
                                }
                                String[] sig = new String[size];
                                PeakListRow newRow = row.clone();
                                newRow.setID(rowCount++);
                                newRow.removePeaks();
                                meanControlStats.clear();
                                for (String sampleName : dataset.getAllColumnNames()) {
                                        if (dataset.getParametersValue(sampleName, parameterName).equals("control") || dataset.getParametersValue(sampleName, parameterName).equals("Control")) {
                                                meanControlStats.addValue((Double) row.getPeak(sampleName));
                                        }
                                }

                                meanGroupStats.clear();
                                int i = 0;
                                for (String group : groups) {
                                        for (String sampleName : dataset.getAllColumnNames()) {
                                                if (dataset.getParametersValue(sampleName, parameterName).equals(group)) {
                                                        meanGroupStats.addValue((Double) row.getPeak(sampleName));
                                                }
                                        }
                                        if (group.equals("Control") || group.equals("control")) {
                                                if (rcontrol) {
                                                        // get the p-value
                                                        sig[i++] = this.getPvalue(meanGroupStats, meanControlStats);
                                                }
                                        } else {
                                                // get the p-value
                                                sig[i++] = this.getPvalue(meanGroupStats, meanControlStats);

                                        }

                                        // get the fold changes
                                        double peakValue = meanGroupStats.getMean() / meanControlStats.getMean();

                                        if (log) {
                                                peakValue = Math.log(peakValue);
                                        }
                                        newRow.setPeak(group, peakValue);
                                }
                                newDataset.addRow(newRow);
                                this.significance.add(sig);
                        }
                }


                // if the user didn't selecte any concrete row, all the rows are considered to be selected
                if (newDataset.getNumberRows() == 0) {
                        rowCount = 0;
                        for (PeakListRow row : dataset.getRows()) {
                                int size = 0;
                                if (rcontrol) {
                                        size = groups.size();
                                } else {
                                        size = groups.size() - 1;
                                }
                                String[] sig = new String[size];
                                PeakListRow newRow = row.clone();
                                newRow.setID(rowCount++);
                                newRow.removePeaks();
                                meanControlStats.clear();
                                for (String sampleName : dataset.getAllColumnNames()) {
                                        if (dataset.getParametersValue(sampleName, parameterName).equals("control") || dataset.getParametersValue(sampleName, parameterName).equals("Control")) {
                                                meanControlStats.addValue((Double) row.getPeak(sampleName));
                                        }
                                }

                                meanGroupStats.clear();
                                int i = 0;
                                for (String group : groups) {
                                        for (String sampleName : dataset.getAllColumnNames()) {
                                                if (dataset.getParametersValue(sampleName, parameterName).equals(group)) {
                                                        meanGroupStats.addValue((Double) row.getPeak(sampleName));
                                                }
                                        }
                                        if (group.equals("Control") || group.equals("control")) {
                                                if (rcontrol) {
                                                        // get the p-value
                                                        sig[i++] = this.getPvalue(meanGroupStats, meanControlStats);
                                                }
                                        } else {
                                                // get the p-value
                                                sig[i++] = this.getPvalue(meanGroupStats, meanControlStats);

                                        }

                                        // get the fold changes
                                        double peakValue = meanGroupStats.getMean() / meanControlStats.getMean();

                                        if (log) {
                                                peakValue = Math.log(peakValue);
                                        }
                                        newRow.setPeak(group, peakValue);
                                }
                                newDataset.addRow(newRow);
                                this.significance.add(sig);
                        }
                }

                // scale with std dev of each column
                if (scale) {
                        DescriptiveStatistics stdDevStats = new DescriptiveStatistics();
                        for (String name : newDataset.getAllColumnNames()) {
                                for (PeakListRow row : newDataset.getRows()) {
                                        stdDevStats.addValue((Double) row.getPeak(name));
                                }
                        }
                        double stdDev = stdDevStats.getStandardDeviation();
                        for (String name : newDataset.getAllColumnNames()) {
                                for (PeakListRow row : newDataset.getRows()) {
                                        row.setPeak(name, ((Double) row.getPeak(name)) / stdDev);
                                }
                        }
                }

                return newDataset;
        }

        private void saveTemporalSignificaceFile(List<String[]> significance) {
                try {
                        CsvWriter w = new CsvWriter("stars.csv");
                        for (String[] stars : significance) {
                                w.writeRecord(stars);
                        }
                        w.endRecord();
                        w.close();
                } catch (IOException ex) {
                        Logger.getLogger(HeatMapTask.class.getName()).log(Level.SEVERE, null, ex);
                }
        }

        private String getPvalue(DescriptiveStatistics group1, DescriptiveStatistics group2) {
                TTestImpl ttest = new TTestImpl();
                String sig = "";
                try {
                        double pValue = ttest.tTest(group1, group2);
                        if (pValue < 0.05) {
                                sig = "*";
                        } else if (pValue < 0.01) {
                                sig = "**";
                        } else if (pValue < 0.001) {
                                sig = "***";
                        }

                } catch (IllegalArgumentException ex) {
                        sig = "-";
                        Logger.getLogger(HeatMapTask.class.getName()).log(Level.SEVERE, null, ex);
                } catch (MathException ex) {
                        sig = "-";
                        Logger.getLogger(HeatMapTask.class.getName()).log(Level.SEVERE, null, ex);
                }
                return sig;
        }
}
