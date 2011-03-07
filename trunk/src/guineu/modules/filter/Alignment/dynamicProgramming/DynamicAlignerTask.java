/*
 * Copyright 2007-2010 VTT Biotechnology
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
package guineu.modules.filter.Alignment.dynamicProgramming;

import guineu.modules.filter.Alignment.RANSAC.*;
import guineu.data.Dataset;
import guineu.data.PeakListRow;
import guineu.data.impl.datasets.SimpleLCMSDataset;
import guineu.data.impl.peaklists.SimplePeakListRowLCMS;
import guineu.main.GuineuCore;
import guineu.taskcontrol.Task;
import guineu.taskcontrol.TaskStatus;
import guineu.util.Range;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.logging.Logger;

public class DynamicAlignerTask implements Task {

    private Logger logger = Logger.getLogger(this.getClass().getName());
    private Dataset peakLists[], alignedPeakList;
    private TaskStatus status = TaskStatus.WAITING;
    private String errorMessage;
    // Processed rows counter
    private int processedRows, totalRows;
    // Parameters
    private String peakListName;
    private double mzTolerance;
    private double rtTolerance;
    private RansacAlignerParameters parameters;
    private double progress;
    private RANSAC ransac;

    public DynamicAlignerTask(Dataset[] peakLists, RansacAlignerParameters parameters) {

        this.peakLists = peakLists;
        this.parameters = parameters;

        // Get parameter values for easier use
        peakListName = (String) parameters.getParameterValue(RansacAlignerParameters.peakListName);

        mzTolerance = (Double) parameters.getParameterValue(RansacAlignerParameters.MZTolerance);

        rtTolerance = (Double) parameters.getParameterValue(RansacAlignerParameters.RTTolerance);

    }

    public String getTaskDescription() {
        return "Dynamic aligner, " + peakListName + " (" + peakLists.length + " peak lists)";
    }

    public double getFinishedPercentage() {
        if (totalRows == 0) {
            return 0f;
        }
        if (ransac != null) {
            return ransac.getProgress();
        }
        return progress; //
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

    /**
     * @see Runnable#run()
     */
    public void run() {
        status = TaskStatus.PROCESSING;
        logger.info("Running RANSAC aligner");

        // Remember how many rows we need to process.
        for (int i = 0; i < peakLists.length; i++) {
            totalRows += peakLists[i].getNumberRows() * 2;
        }


        // Create a new aligned peak list
        this.alignedPeakList = peakLists[0].clone();

        this.alignedPeakList.setDatasetName("Aligned Dataset");

        for (Dataset dataset : this.peakLists) {
            if (dataset != peakLists[0]) {
                for (String experimentName : dataset.getAllColumnNames()) {
                    this.alignedPeakList.addColumnName(experimentName);

                    // Adding parameters to the new data set from the rests of data sets
                    for (String parameterName : dataset.getParametersName()) {
                        alignedPeakList.addParameterValue(experimentName, parameterName, dataset.getParametersValue(experimentName, parameterName));
                    }
                }
            }
        }

        // Dynamic alignmnent
        // Iterate source peak lists to create the representative "graph" for each peak
        Hashtable<PeakListRow, Graph> alignmentMapping = new Hashtable<PeakListRow, Graph>();
        for (Dataset peakList : peakLists) {
            if (peakList != peakLists[0]) {
                for (PeakListRow row : peakList.getRows()) {
                    Graph graph = this.getGraph(peakList, row);
                    alignmentMapping.put(row, graph);
                    //progress = (double) processedRows++ / (double) totalRows;
                }
            }
        }

        for (Dataset peakList : peakLists) {
            if (peakList != peakLists[0]) {
                //for each row in the main file which contains all the samples align until that moment.. get the graph of peaks..
                for (PeakListRow row : alignedPeakList.getRows()) {
                    Graph rowGraph = this.getGraph(peakList, row);
                    double minRT = ((SimplePeakListRowLCMS) row).getRT() - this.rtTolerance;
                    if (minRT < 0) {
                        minRT = 0;
                    }
                    Range rtRange = new Range(minRT, ((SimplePeakListRowLCMS) row).getRT() + this.rtTolerance);
                    double minMZ = ((SimplePeakListRowLCMS) row).getMZ() - this.mzTolerance;
                    if (minMZ < 0) {
                        minMZ = 0;
                    }
                    Range mzRange = new Range(minMZ, ((SimplePeakListRowLCMS) row).getMZ() + this.mzTolerance);
                    PeakListRow candidateRows[] = ((SimpleLCMSDataset) peakList).getRowsInsideRTAndMZRange(rtRange, mzRange);
                    for (PeakListRow candidate : candidateRows) {
                        double score = this.getScore(rowGraph, alignmentMapping.get(candidateRows));
                    }
                    //progress = (double) processedRows++ / (double) totalRows;
                }
            }
        }

        // Add new aligned peak list to the project
        GuineuCore.getDesktop().AddNewFile(alignedPeakList);

        // Add task description to peakList

        logger.info("Finished RANSAC aligner");
        status = TaskStatus.FINISHED;


    }

    private double getScore(Graph graphPeak, Graph grahpCandidate) {
        return 0.0;
    }

    private Graph getGraph(Dataset peakListY, PeakListRow row) {
        double minRT = ((SimplePeakListRowLCMS) row).getRT() - this.rtTolerance;
        if (minRT < 0) {
            minRT = 0;
        }
        Range rtRange = new Range(minRT, ((SimplePeakListRowLCMS) row).getRT() + this.rtTolerance);
        double minMZ = ((SimplePeakListRowLCMS) row).getMZ() - this.mzTolerance;
        if (minMZ < 0) {
            minMZ = 0;
        }
        Range mzRange = new Range(minMZ, ((SimplePeakListRowLCMS) row).getMZ() + this.mzTolerance);

        // Get all rows of the aligned peaklist within parameter limits
        PeakListRow candidateRows[] = ((SimpleLCMSDataset) peakListY).getRowsInsideRTAndMZRange(rtRange, mzRange);
        Graph graph = new Graph((SimplePeakListRowLCMS) row, candidateRows);
        return graph;
    }

    public Object[] getCreatedObjects() {
        return new Object[]{alignedPeakList};
    }

    class Graph {

        List<Double[]> coordinates = new ArrayList<Double[]>();

        public Graph(SimplePeakListRowLCMS peak, PeakListRow[] candidates) {
            for (PeakListRow row : candidates) {
                Double[] coord = new Double[2];
                coord[0] = peak.getRT() - ((SimplePeakListRowLCMS) row).getRT();
                coord[1] = peak.getMZ() - ((SimplePeakListRowLCMS) row).getMZ();
                this.coordinates.add(coord);
            }
        }
    }
}
