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
import guineu.modules.R.RUtilities;
import guineu.parameters.ParameterSet;
import guineu.taskcontrol.AbstractTask;
import guineu.taskcontrol.TaskStatus;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.math.MathException;
import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math.stat.inference.TTestImpl;
import org.rosuda.JRI.Rengine;

/**
 *
 * @author scsandra
 */
public class HeatMapTask extends AbstractTask {
        private String outputType;
        private boolean log, rcontrol, scale, plegend;
        private int height, width, columnMargin, rowMargin, starSize;
        private File outputFile;
        private String[] rowNames, colNames;
        private String[][] pValueMatrix;
        private Dataset dataset;
        private String parameterName, referenceGroup;
        private double finishedPercentage = 0.0f;

        public HeatMapTask(Dataset dataset, ParameterSet parameters) {
                this.dataset = dataset;

                outputFile = parameters.getParameter(HeatMapParameters.fileName).getValue();
                outputType = parameters.getParameter(HeatMapParameters.fileTypeSelection).getValue();
                parameterName = parameters.getParameter(HeatMapParameters.selectionData).getValue();
                referenceGroup = parameters.getParameter(HeatMapParameters.referenceGroup).getValue();

                log = parameters.getParameter(HeatMapParameters.log).getValue();
                scale = parameters.getParameter(HeatMapParameters.scale).getValue();
                rcontrol = parameters.getParameter(HeatMapParameters.showControlSamples).getValue();
                plegend = parameters.getParameter(HeatMapParameters.plegend).getValue();

                height = parameters.getParameter(HeatMapParameters.height).getValue();
                width = parameters.getParameter(HeatMapParameters.width).getValue();
                columnMargin = parameters.getParameter(HeatMapParameters.columnMargin).getValue();
                rowMargin = parameters.getParameter(HeatMapParameters.rowMargin).getValue();
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
                        double[][] newDataset;
                       
                        if (!parameterName.isEmpty() && !referenceGroup.isEmpty()) {
                                if (plegend) {
                                        newDataset = this.GroupingDataset(dataset);
                                } else {
                                        newDataset = this.modifyDataset(dataset);
                                }
                        } else {
                                setStatus(TaskStatus.ERROR);
                                if (parameterName.isEmpty()) {
                                        errorMessage = "Sample parameters have to be defined.";
                                } else if (referenceGroup.isEmpty()) {
                                        errorMessage = "Reference group has to be defined.";
                                }
                                return;
                        }

                       
                        if (newDataset == null || newDataset.length == 0 || newDataset[0].length == 0) {
                                setStatus(TaskStatus.ERROR);
                                errorMessage = "The data for heat map is empty.";
                                return;
                        }
                       
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

                                        if (outputType.contains("png")) {
                                                if (height < 500 || width < 500) {

                                                        setStatus(TaskStatus.ERROR);
                                                        errorMessage = "Figure height or width is too small. Minimun height and width is 500.";
                                                        return;
                                                }
                                        }

                                        rEngine.eval("dataset<- matrix(\"\",nrow =" + newDataset[0].length + ",ncol=" + newDataset.length + ")");

                                        if (plegend) {
                                                rEngine.eval("stars<- matrix(\"\",nrow =" + newDataset[0].length + ",ncol=" + newDataset.length + ")");
                                        }

                                        // assing the values to the matrix
                                        for (int row = 0; row < newDataset[0].length; row++) {

                                                for (int column = 0; column < newDataset.length; column++) {

                                                        int r = row + 1;
                                                        int c = column + 1;

                                                        double value = newDataset[column][row];

                                                        if (plegend) {
                                                                String pValue = pValueMatrix[column][row];
                                                                rEngine.eval("stars[" + r + "," + c + "] = \"" + pValue + "\"");
                                                        }

                                                        if (!Double.isInfinite(value) && !Double.isNaN(value)) {

                                                                rEngine.eval("dataset[" + r + "," + c + "] = " + value);
                                                        } else {

                                                                rEngine.eval("dataset[" + r + "," + c + "] = NA");
                                                        }
                                                }
                                        }
                                        finishedPercentage = 0.4f;
                                        
                                        rEngine.eval("dataset <- apply(dataset, 2, as.numeric)");

                                        // Assign row names to the data set
                                        long rows = rEngine.rniPutStringArray(rowNames);
                                        rEngine.rniAssign("rowNames", rows, 0);
                                        rEngine.eval("rownames(dataset)<-rowNames");

                                        // Assign column names to the data set
                                        long columns = rEngine.rniPutStringArray(colNames);
                                        rEngine.rniAssign("colNames", columns, 0);
                                        rEngine.eval("colnames(dataset)<-colNames");

                                        finishedPercentage = 0.5f;

                                        // Remove the rows with too many NA's. The distances between rows can't be calculated if the rows don't have
                                        // at least one sample in common.
                                        rEngine.eval(" d <- as.matrix(dist(dataset))");
                                        rEngine.eval("d[upper.tri(d)] <- 0");
                                        rEngine.eval("dataset <- dataset[-na.action(na.omit(d)),]");

                                        finishedPercentage = 0.8f;

                                        String marginParameter = "margins = c(" + columnMargin + "," + rowMargin + ")";
                                        rEngine.eval("br<-c(seq(from=min(dataset,na.rm=T),to=0,length.out=256),seq(from=0,to=max(dataset,na.rm=T),length.out=256))", false);

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

                                        if (plegend) {

                                                rEngine.eval("heatmap.2(dataset," + marginParameter + ", trace=\"none\", col=bluered(length(br)-1), breaks=br, cellnote=stars, notecol=\"black\", notecex=" + starSize + ", na.color=\"grey\")", false);
                                        } else {

                                                rEngine.eval("heatmap.2(dataset," + marginParameter + ", trace=\"none\", col=bluered(length(br)-1), breaks=br, na.color=\"grey\")", false);
                                        }

                                        rEngine.eval("dev.off()", false);
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

        private double[][] modifyDataset(Dataset dataset) {
                // Determine the reference group and non reference group (the rest of
                // the samples) for raw data files
                List<String> referenceDataFiles = new ArrayList<String>();
                List<String> nonReferenceDataFiles = new ArrayList<String>();

                for (String rawDataFile : dataset.getAllColumnNames()) {

                        String paramValue = dataset.getParametersValue(rawDataFile, parameterName);

                        if (paramValue.equals(referenceGroup)) {

                                referenceDataFiles.add(rawDataFile);
                        } else {

                                nonReferenceDataFiles.add(rawDataFile);
                        }
                }

                int numRows = 0;
                for (int row = 0; row < dataset.getNumberRows(); row++) {

                        if (dataset.getRow(row).isSelected()) {

                                numRows++;
                        }
                }
                boolean useCompleteDataset = false;
                if (numRows == 0) {
                        numRows = dataset.getNumberRows();
                        useCompleteDataset = true;
                }

                // Create a new aligned peak list with all the samples if the reference
                // group has to be shown or with only
                // the non reference group if not.
                double[][] dataMatrix;
                if (rcontrol) {
                        dataMatrix = new double[dataset.getNumberCols()][numRows];
                } else {
                        dataMatrix = new double[nonReferenceDataFiles.size()][numRows];
                }

                // Data files that should be in the heat map
                List<String> shownDataFiles = null;
                if (rcontrol) {
                        shownDataFiles = dataset.getAllColumnNames();
                } else {
                        shownDataFiles = nonReferenceDataFiles;
                }

                for (int row = 0, rowIndex = 0; row < dataset.getNumberRows(); row++) {
                        PeakListRow rowPeak = dataset.getRow(row);
                        if (rowPeak.isSelected() || useCompleteDataset) {

                                // Average area or height of the reference group
                                double referenceAverage = 0;
                                int referencePeakCount = 0;
                                for (int column = 0; column < referenceDataFiles.size(); column++) {

                                        if (rowPeak.getPeak(referenceDataFiles.get(column)) != null) {

                                                referenceAverage += (Double) rowPeak.getPeak(
                                                        referenceDataFiles.get(column));
                                                referencePeakCount++;
                                        }
                                }
                                if (referencePeakCount > 0) {

                                        referenceAverage /= referencePeakCount;
                                }

                                // Divide the area or height of each peak by the average of the
                                // area or height of the reference peaks in each row
                                for (int column = 0; column < shownDataFiles.size(); column++) {
                                        double value = Double.NaN;
                                        if (rowPeak.getPeak(shownDataFiles.get(column)) != null) {

                                                value = (Double) rowPeak.getPeak(shownDataFiles.get(column)) / referenceAverage;

                                                if (log) {

                                                        value = Math.log(value);
                                                }
                                        }

                                        dataMatrix[column][rowIndex] = value;
                                }
                                rowIndex++;
                        }
                }

                // Scale the data dividing the peak area/height by the standard
                // deviation of each column
                if (scale) {
                        scale(dataMatrix);
                }

                // Create two arrays: row and column names
                rowNames = new String[dataMatrix[0].length];
                colNames = new String[shownDataFiles.size()];

                for (int column = 0; column < shownDataFiles.size(); column++) {

                        colNames[column] = shownDataFiles.get(column);
                }
                for (int row = 0, rowIndex = 0; row < dataset.getNumberRows(); row++) {
                        if (dataset.getRow(row).isSelected() || useCompleteDataset) {

                                rowNames[rowIndex++] = dataset.getRow(row).getName();
                        }
                }

                return dataMatrix;
        }

        private double[][] GroupingDataset(Dataset dataset) {
                DescriptiveStatistics meanControlStats = new DescriptiveStatistics();
                DescriptiveStatistics meanGroupStats = new DescriptiveStatistics();

                // Determine the reference group and non reference group (the rest of
                // the samples) for raw data files
                List<String> referenceDataFiles = new ArrayList<String>();
                List<String> nonReferenceDataFiles = new ArrayList<String>();

                List<String> groups = new ArrayList<String>();
                for (String rawDataFile : dataset.getAllColumnNames()) {

                        String paramValue = dataset.getParametersValue(rawDataFile, parameterName);
                       
                        if (!groups.contains(paramValue)) {
                                groups.add(paramValue);
                        }
                        if (paramValue.equals(referenceGroup)) {

                                referenceDataFiles.add(rawDataFile);
                        } else {

                                nonReferenceDataFiles.add(rawDataFile);
                        }
                }
                int numRows = 0;
                for (int row = 0; row < dataset.getNumberRows(); row++) {

                        if (dataset.getRow(row).isSelected()) {
                                numRows++;
                        }
                }
               
                boolean useCompleteDataset = false;
                if (numRows == 0) {
                        numRows = dataset.getNumberRows();
                        useCompleteDataset = true;
                }

                // Create a new aligned peak list with all the samples if the reference
                // group has to be shown or with only
                // the non reference group if not.
                double[][] dataMatrix;
                if (groups.size() > 1) {
                        dataMatrix = new double[groups.size() - 1][numRows];
                        pValueMatrix = new String[groups.size() - 1][numRows];
                } else {
                        return null;
                }                                

                for (int row = 0, rowIndex = 0; row < dataset.getNumberRows(); row++) {
                        PeakListRow rowPeak = dataset.getRow(row);
                        if (rowPeak.isSelected() || useCompleteDataset) {
                                // Average height of the reference group
                                meanControlStats.clear();
                                for (int column = 0; column < referenceDataFiles.size(); column++) {

                                        if (rowPeak.getPeak(referenceDataFiles.get(column)) != null) {

                                                meanControlStats.addValue((Double) rowPeak.getPeak(referenceDataFiles.get(column)));
                                        }
                                }

                                // Divide the height of each peak by the average of the
                                // area or height of the reference peaks in each row
                                int columnIndex = 0;
                                for (int column = 0; column < groups.size(); column++) {
                                        String group = groups.get(column);
                                        meanGroupStats.clear();
                                        if (!group.equals(referenceGroup)) {

                                                for (int dataColumn = 0; dataColumn < nonReferenceDataFiles.size(); dataColumn++) {

                                                        String paramValue = dataset.getParametersValue(nonReferenceDataFiles.get(dataColumn), parameterName);
                                                        if (rowPeak.getPeak(nonReferenceDataFiles.get(dataColumn)) != null
                                                                && paramValue.equals(group)) {

                                                                meanGroupStats.addValue((Double) rowPeak.getPeak(nonReferenceDataFiles.get(dataColumn)));
                                                        }
                                                }

                                                double value = meanGroupStats.getMean()
                                                        / meanControlStats.getMean();
                                                if (meanGroupStats.getN() > 1
                                                        && meanControlStats.getN() > 1) {
                                                        pValueMatrix[columnIndex][rowIndex] = this.getPvalue(meanGroupStats, meanControlStats);
                                                } else {
                                                        pValueMatrix[columnIndex][rowIndex] = "";
                                                }

                                                if (log) {

                                                        value = Math.log(value);
                                                }
                                                dataMatrix[columnIndex++][rowIndex] = value;
                                        }
                                }
                                rowIndex++;
                        }
                }
                
                // Scale the data dividing the peak area/height by the standard
                // deviation of each column
                if (scale) {
                        scale(dataMatrix);
                }
              
                // Create two arrays: row and column names
                rowNames = new String[dataMatrix[0].length];
                colNames = new String[groups.size() - 1];

                int columnIndex = 0;
                for (String group : groups) {

                        if (!group.equals(referenceGroup)) {

                                colNames[columnIndex++] = group;
                        }
                }
                for (int row = 0, rowIndex = 0; row < dataset.getNumberRows(); row++) {
                        if (dataset.getRow(row).isSelected() || useCompleteDataset) {
                                rowNames[rowIndex++] = dataset.getRow(row).getName();
                        }
                }
                
                return dataMatrix;
        }

        private String getPvalue(DescriptiveStatistics group1, DescriptiveStatistics group2) {
                TTestImpl ttest = new TTestImpl();
                String sig = "";
                try {
                        double pValue = ttest.tTest(group1, group2);
                        if (pValue < 0.05) {
                                sig = "*";
                        }
                        if (pValue < 0.01) {
                                sig = "**";
                        }
                        if (pValue < 0.001) {
                                sig = "***";
                        }

                } catch (IllegalArgumentException ex) {
                        sig = "-";

                } catch (MathException ex) {
                        sig = "-";
                }
                return sig;
        }

        private void scale(double[][] peakList) {
                DescriptiveStatistics stdDevStats = new DescriptiveStatistics();

                for (int columns = 0; columns < peakList.length; columns++) {
                        stdDevStats.clear();
                        for (int row = 0; row < peakList[columns].length; row++) {
                                if (!Double.isInfinite(peakList[columns][row])
                                        && !Double.isNaN(peakList[columns][row])) {
                                        stdDevStats.addValue(peakList[columns][row]);
                                }
                        }

                        double stdDev = stdDevStats.getStandardDeviation();

                        for (int row = 0; row < peakList[columns].length; row++) {
                                if (stdDev != 0) {
                                        peakList[columns][row] = peakList[columns][row] / stdDev;
                                }
                        }
                }
        }

}
