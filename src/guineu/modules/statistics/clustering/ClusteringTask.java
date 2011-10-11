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
package guineu.modules.statistics.clustering;

import figs.treeVisualization.TreeViewJ;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jmprojection.PCA;
import jmprojection.Preprocess;
import jmprojection.ProjectionStatus;
import jmprojection.Sammons;

import org.jfree.data.xy.AbstractXYDataset;

import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SparseInstance;
import guineu.data.PeakListRow;
import guineu.desktop.Desktop;
import guineu.main.GuineuCore;
import guineu.modules.statistics.PCA.ProjectionPlotDataset;
import guineu.modules.statistics.PCA.ProjectionPlotParameters;
import guineu.modules.statistics.PCA.ProjectionPlotWindow;
import guineu.parameters.ParameterSet;
import guineu.taskcontrol.TaskEvent;
import guineu.taskcontrol.TaskListener;
import guineu.taskcontrol.TaskStatus;

public class ClusteringTask extends AbstractXYDataset implements
        ProjectionPlotDataset {

        private Logger logger = Logger.getLogger(this.getClass().getName());
        private LinkedList<TaskListener> taskListeners = new LinkedList<TaskListener>();
        private double[] component1Coords;
        private double[] component2Coords;
        private ParameterSet parameters;
        private String[] selectedRawDataFiles;
        private PeakListRow[] selectedRows;
        private int[] groupsForSelectedRawDataFiles, groupsForSelectedVariables;
        private Object[] parameterValuesForGroups;
        private int finalNumberOfGroups;
        private String datasetTitle;
        private int xAxisDimension = 1;
        private int yAxisDimension = 2;
        private ProjectionStatus projectionStatus;
        private ClusteringAlgorithm clusteringAlgorithm;
        private ClusteringDataType typeOfData;
        private Instances dataset;
        private int progress;
        private String errorMessage;
        private TaskStatus status = TaskStatus.WAITING;

        public ClusteringTask(ParameterSet parameters) {

                this.parameters = parameters;

                this.selectedRawDataFiles = parameters.getParameter(ProjectionPlotParameters.dataFiles).getValue();
                clusteringAlgorithm = parameters.getParameter(ClusteringParameters.clusteringAlgorithm).getValue();
                typeOfData = parameters.getParameter(ClusteringParameters.typeOfData).getValue();

                this.selectedRows = GuineuCore.getDesktop().getSelectedDataFiles()[0].getSelectedRows().toArray(new PeakListRow[0]);
                if (selectedRows.length == 0) {
                        this.selectedRows = GuineuCore.getDesktop().getSelectedDataFiles()[0].getRows().toArray(new PeakListRow[0]);
                }

                datasetTitle = "Clustering";

                // Determine groups for selected raw data files
                groupsForSelectedRawDataFiles = new int[selectedRawDataFiles.length];
                groupsForSelectedVariables = new int[selectedRows.length];

        }

        @Override
        public String toString() {
                return datasetTitle;
        }

        public String getXLabel() {
                return "1st projected dimension";
        }

        public String getYLabel() {
                return "2nd projected dimension";
        }

        @Override
        public int getSeriesCount() {
                return 1;
        }

        @Override
        public Comparable getSeriesKey(int series) {
                return 1;
        }

        public int getItemCount(int series) {
                return component1Coords.length;
        }

        public Number getX(int series, int item) {
                return component1Coords[item];
        }

        public Number getY(int series, int item) {
                return component2Coords[item];
        }

        public String getRawDataFile(int item) {
                if (typeOfData == ClusteringDataType.VARIABLES) {
                        return selectedRows[item].getName();
                } else {
                        return selectedRawDataFiles[item];
                }
        }

        public int getGroupNumber(int item) {
                if (typeOfData == ClusteringDataType.VARIABLES) {
                        return groupsForSelectedVariables[item];
                } else {
                        return groupsForSelectedRawDataFiles[item];
                }
        }

        public Object getGroupParameterValue(int groupNumber) {
                if (parameterValuesForGroups == null) {
                        return null;
                }
                if ((parameterValuesForGroups.length - 1) < groupNumber) {
                        return null;
                }
                return parameterValuesForGroups[groupNumber];
        }

        public int getNumberOfGroups() {
                return finalNumberOfGroups;
        }

        public void run() {

                setStatus(TaskStatus.PROCESSING);

                logger.info("Clustering");

                double[][] rawData;

                // Creating weka dataset using samples or metabolites (variables)

                if (typeOfData == ClusteringDataType.VARIABLES) {
                        rawData = createMatrix(false);
                        dataset = createVariableWekaDataset(rawData);
                } else {
                        rawData = createMatrix(true);
                        dataset = createSampleWekaDataset(rawData);
                }

                // Running cluster algorithms
                String cluster = "";
                if (clusteringAlgorithm.toString().equals("Hierarchical Clusterer")) {
                        progress = 0;
                        cluster = clusteringAlgorithm.getHierarchicalCluster(dataset);
                        progress = 50;
                        cluster = cluster.replaceAll("Newick:", "");
                        if (typeOfData == ClusteringDataType.SAMPLES) {
                                cluster = addMissingSamples(cluster);
                        }
                        progress = 85;
                        if (cluster != null) {
                                Desktop desktop = GuineuCore.getDesktop();
                                TreeViewJ visualizer = new TreeViewJ(640, 480);
                                cluster += ";";
                                System.out.println(cluster);
                                visualizer.openMenuAction(cluster);
                                desktop.addInternalFrame(visualizer);
                        }
                } else {

                        List<Integer> clusteringResult = clusteringAlgorithm.getClusterGroups(dataset);

                        // Report window
                        Desktop desktop = GuineuCore.getDesktop();
                        if (typeOfData == ClusteringDataType.SAMPLES) {
                                String[] sampleNames = new String[selectedRawDataFiles.length];
                                for (int i = 0; i < selectedRawDataFiles.length; i++) {
                                        sampleNames[i] = selectedRawDataFiles[i];
                                }

                                ClusteringReportWindow reportWindow = new ClusteringReportWindow(sampleNames, (Integer[]) clusteringResult.toArray(new Integer[0]), "Clustering Report");
                                desktop.addInternalFrame(reportWindow);
                        } else {
                                String[] variableNames = new String[selectedRows.length];
                                for (int i = 0; i < selectedRows.length; i++) {
                                        variableNames[i] = selectedRows[i].getID() + " - " + selectedRows[i].getName();
                                }

                                ClusteringReportWindow reportWindow = new ClusteringReportWindow(variableNames, (Integer[]) clusteringResult.toArray(new Integer[0]), "Clustering Report");
                                desktop.addInternalFrame(reportWindow);

                        }

                        // Visualization
                        if (typeOfData == ClusteringDataType.VARIABLES) {
                                for (int ind = 0; ind < selectedRows.length; ind++) {
                                        groupsForSelectedVariables[ind] = clusteringResult.get(ind);
                                }

                        } else {
                                for (int ind = 0; ind < selectedRawDataFiles.length; ind++) {
                                        groupsForSelectedRawDataFiles[ind] = clusteringResult.get(ind);
                                }
                        }


                        this.finalNumberOfGroups = clusteringAlgorithm.getNumberOfGroups();
                        parameterValuesForGroups = new Object[finalNumberOfGroups];
                        for (int i = 0; i < finalNumberOfGroups; i++) {
                                parameterValuesForGroups[i] = "Group " + i;
                        }

                        int numComponents = xAxisDimension;
                        if (yAxisDimension > numComponents) {
                                numComponents = yAxisDimension;
                        }

                        if (clusteringAlgorithm.getVisualizationType() == VisualizationType.PCA) {
                                // Scale data and do PCA
                                Preprocess.scaleToUnityVariance(rawData);
                                PCA pcaProj = new PCA(rawData, numComponents);
                                projectionStatus = pcaProj.getProjectionStatus();

                                double[][] result = pcaProj.getState();

                                if (getStatus() == TaskStatus.CANCELED) {
                                        return;
                                }

                                component1Coords = result[xAxisDimension - 1];
                                component2Coords = result[yAxisDimension - 1];
                        } else if (clusteringAlgorithm.getVisualizationType() == VisualizationType.SAMMONS) {
                                // Scale data and do Sammon's mapping
                                Preprocess.scaleToUnityVariance(rawData);
                                Sammons sammonsProj = new Sammons(rawData);
                                projectionStatus = sammonsProj.getProjectionStatus();

                                sammonsProj.iterate(100);

                                double[][] result = sammonsProj.getState();

                                if (getStatus() == TaskStatus.CANCELED) {
                                        return;
                                }

                                component1Coords = result[xAxisDimension - 1];
                                component2Coords = result[yAxisDimension - 1];
                        }
                        ProjectionPlotWindow newFrame = new ProjectionPlotWindow("Visualization", this, parameters);
                        desktop.addInternalFrame(newFrame);
                }
                progress = 99;
                status = TaskStatus.FINISHED;
                logger.info("Finished computing Clustering visualization.");
        }

        /**
         * Creates a matrix of heights of areas
         * @param isForSamples
         * @return
         */
        private double[][] createMatrix(boolean isForSamples) {
                // Generate matrix of raw data (input to CDA)               
                double[][] rawData;
                if (isForSamples) {
                        rawData = new double[selectedRawDataFiles.length][selectedRows.length];
                        for (int rowIndex = 0; rowIndex < selectedRows.length; rowIndex++) {
                                PeakListRow peakListRow = selectedRows[rowIndex];
                                for (int fileIndex = 0; fileIndex < selectedRawDataFiles.length; fileIndex++) {
                                        String rawDataFile = selectedRawDataFiles[fileIndex];
                                        Double p = (Double) peakListRow.getPeak(rawDataFile);
                                        if (p != null) {
                                                rawData[fileIndex][rowIndex] = p;
                                        }
                                }
                        }
                } else {
                        rawData = new double[selectedRows.length][selectedRawDataFiles.length];
                        for (int rowIndex = 0; rowIndex < selectedRows.length; rowIndex++) {
                                PeakListRow peakListRow = selectedRows[rowIndex];
                                for (int fileIndex = 0; fileIndex < selectedRawDataFiles.length; fileIndex++) {
                                        String rawDataFile = selectedRawDataFiles[fileIndex];
                                        Double p = (Double) peakListRow.getPeak(rawDataFile);
                                        if (p != null) {
                                                rawData[rowIndex][fileIndex] = p;

                                        }
                                }
                        }
                }

                return rawData;
        }

        /**
         * Creates the weka data set for clustering of samples
         * @param rawData Data extracted from selected Raw data files and rows.
         * @return Weka library data set
         */
        private Instances createSampleWekaDataset(double[][] rawData) {
                FastVector attributes = new FastVector();

                for (int i = 0; i < rawData[0].length; i++) {
                        String varName = "Var" + i;
                        Attribute var = new Attribute(varName);
                        attributes.addElement(var);
                }

                if (clusteringAlgorithm.toString().equals("Hierarchical Clusterer")) {
                        Attribute name = new Attribute("name", (FastVector) null);
                        attributes.addElement(name);
                }
                Instances data = new Instances("Dataset", attributes, 0);

                for (int i = 0; i < rawData.length; i++) {
                        double[] values = new double[data.numAttributes()];
                        System.arraycopy(rawData[i], 0, values, 0, rawData[0].length);
                        if (clusteringAlgorithm.toString().equals("Hierarchical Clusterer")) {
                                values[data.numAttributes() - 1] = data.attribute("name").addStringValue(this.selectedRawDataFiles[i]);
                        }
                        Instance inst = new SparseInstance(1.0, values);
                        data.add(inst);
                }
                return data;
        }

        /**
         * Creates the weka data set for clustering of variables (metabolites)
         * @param rawData Data extracted from selected Raw data files and rows.
         * @return Weka library data set
         */
        private Instances createVariableWekaDataset(double[][] rawData) {
                FastVector attributes = new FastVector();

                for (int i = 0; i < this.selectedRawDataFiles.length; i++) {
                        String varName = "Var" + i;
                        Attribute var = new Attribute(varName);
                        attributes.addElement(var);
                }

                if (clusteringAlgorithm.toString().equals("Hierarchical Clusterer")) {
                        Attribute name = new Attribute("name", (FastVector) null);
                        attributes.addElement(name);
                }
                Instances data = new Instances("Dataset", attributes, 0);

                for (int i = 0; i < selectedRows.length; i++) {
                        double[] values = new double[data.numAttributes()];
                        System.arraycopy(rawData[i], 0, values, 0, rawData[0].length);

                        if (clusteringAlgorithm.toString().equals("Hierarchical Clusterer")) {
                                String rowName = selectedRows[i].getName();
                                values[data.numAttributes() - 1] = data.attribute("name").addStringValue(rowName);
                        }
                        Instance inst = new SparseInstance(1.0, values);
                        data.add(inst);
                }
                return data;
        }

        public void cancel() {
                if (projectionStatus != null) {
                        projectionStatus.cancel();
                }

                status = TaskStatus.CANCELED;
        }

        public String getErrorMessage() {
                return errorMessage;
        }

        public Object[] getCreatedObjects() {
                return null;
        }

        /**
         * Adds a TaskListener to this Task
         *
         * @param t The TaskListener to add
         */
        public void addTaskListener(TaskListener t) {
                this.taskListeners.add(t);
        }

        /**
         * Returns all of the TaskListeners which are listening to this task.
         *
         * @return An array containing the TaskListeners
         */
        public TaskListener[] getTaskListeners() {
                return this.taskListeners.toArray(new TaskListener[this.taskListeners.size()]);
        }

        private void fireTaskEvent() {
                TaskEvent event = new TaskEvent(this);
                for (TaskListener t : this.taskListeners) {
                        t.statusChanged(event);
                }

        }

        /**
         * @see net.sf.mzmine.taskcontrol.Task#setStatus()
         */
        public void setStatus(TaskStatus newStatus) {
                this.fireTaskEvent();
        }

        public boolean isCanceled() {
                return getStatus() == TaskStatus.CANCELED;
        }

        public boolean isFinished() {
                return getStatus() == TaskStatus.FINISHED;
        }

        /**
         * Adds the missing samples (these samples have no relation with the rest of the samples)
         * to the cluster using the maximun distance inside the cluster result plus 1.
         * @param cluster String with the cluster result in Newick format.
         * @return String with the cluster result after adding the missing samples in Newick format.
         */
        private String addMissingSamples(String cluster) {
                System.out.println(cluster);
                String[] data = cluster.split(":");
                double max = 0;
                for (String d : data) {
                        double value = -1;
                        Pattern p = Pattern.compile("^\\d+(.\\d+)");
                        Matcher m = p.matcher(d);
                        if (m.find()) {
                                value = Double.parseDouble(d.substring(m.start(), m.end()));
                                System.out.println(value);
                        }
                        if (value > max) {
                                max = value;
                        }
                }
                Pattern p = Pattern.compile("^\\d+(.\\d+)?");
                Matcher m = p.matcher(data[data.length - 1]);
                double lastValue = 0.0;
                if (m.find()) {
                        lastValue = Double.parseDouble(data[data.length - 1].substring(m.start(), m.end()));
                }

                max += lastValue;
                for (int i = 0; i < this.selectedRawDataFiles.length; i++) {
                        if (!cluster.contains(this.selectedRawDataFiles[i])) {
                                max++;
                                cluster = "(" + cluster + ":1.0," + this.selectedRawDataFiles[i] + ":" + max + ")";
                        }
                }

                return cluster;
        }

        @Override
        public String getTaskDescription() {
                return "Clustering visualization " + datasetTitle;
        }

        public TaskStatus getStatus() {
                return status;
        }

        @Override
        public double getFinishedPercentage() {
                if (this.projectionStatus != null && progress < 99) {
                        if (projectionStatus.getFinishedPercentage() > 1.0) {
                                return 1.0;
                        }
                        return projectionStatus.getFinishedPercentage();
                } else {
                        if (progress > 100) {
                                return 1.0;
                        }
                        progress++;
                        return ((double) progress / 100);
                }
        }
}
