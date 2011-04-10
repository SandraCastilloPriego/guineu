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
package guineu.modules.filter.Alignment.dynamicProgramming;

import guineu.data.Dataset;
import guineu.data.LCMSColumnName;
import guineu.data.PeakListRow;
import guineu.data.impl.datasets.SimpleLCMSDataset;
import guineu.data.impl.peaklists.SimplePeakListRowLCMS;
import guineu.main.GuineuCore;
import guineu.taskcontrol.Task;
import guineu.taskcontrol.TaskStatus;
import guineu.util.Range;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
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
        private double progress;
        private double increaseWindows = 0;

        public DynamicAlignerTask(Dataset[] peakLists, DynamicAlignerParameters parameters) {

                this.peakLists = peakLists;

                // Get parameter values for easier use
                peakListName = parameters.getParameter(DynamicAlignerParameters.peakListName).getValue();

                mzTolerance = parameters.getParameter(DynamicAlignerParameters.MZTolerance).getValue().getTolerance();

                rtTolerance = parameters.getParameter(DynamicAlignerParameters.RTTolerance).getValue().getTolerance();


        }

        public String getTaskDescription() {
                return "Dynamic aligner, " + peakListName + " (" + peakLists.length + " peak lists)";
        }

        public double getFinishedPercentage() {
                if (totalRows == 0) {
                        return 0f;
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
        @Override
        public void run() {

                Comparator<PeakListRow> c = new Comparator<PeakListRow>() {

                        @Override
                        public int compare(PeakListRow o1, PeakListRow o2) {
                                if (((SimplePeakListRowLCMS) o1).getRT() > ((SimplePeakListRowLCMS) o2).getRT()) {
                                        return 1;
                                } else {
                                        return -1;
                                }
                        }
                };

                status = TaskStatus.PROCESSING;
                logger.info("Running RANSAC aligner");

                // Remember how many rows we need to process.
                for (int i = 0; i < peakLists.length; i++) {
                        totalRows += peakLists[i].getNumberRows() * 2;
                }


                // Create a new aligned peak list
                this.alignedPeakList = peakLists[0].clone();

                this.alignedPeakList.setDatasetName(this.peakListName);

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


                for (Dataset peakList : peakLists) {
                        if (peakList != peakLists[0]) {
                                List<PeakListRow> masterRows = alignedPeakList.getRows();
                                List<PeakListRow> rows = peakList.getRows();
                                Collections.sort(masterRows, c);
                                Collections.sort(rows, c);


                                HashMap<Integer, Integer> alignedIndices = new HashMap<Integer, Integer>();


                                //for each row in the main file which contains all the samples align until that moment.. get the graph of peaks..

                                for (PeakListRow row : peakList.getRows()) {
                                        Graph rowGraph = null;
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
                                        PeakListRow candidateRows[] = ((SimpleLCMSDataset) alignedPeakList).getRowsInsideRTAndMZRange(rtRange, mzRange);

                                        if (candidateRows.length > 0) {
                                                this.increaseWindows = 0;
                                                rowGraph = this.getGraph(peakList, row, this.increaseWindows);
                                        }

                                        double maxScore = 0;
                                        double[] scores = new double[candidateRows.length];
                                        int index = 0;
                                        for (PeakListRow candidate : candidateRows) {
                                                double diffMZ = 0;//Math.abs(((SimplePeakListRowLCMS) row).getMZ() - ((SimplePeakListRowLCMS) candidate).getMZ()) * 100;
                                                this.increaseWindows = 0;
                                                double score = this.getScore(rowGraph, this.getGraph(alignedPeakList, candidate, this.increaseWindows)) + diffMZ;
                                                scores[index] = score;

                                                if (score > maxScore) {
                                                        maxScore = score;
                                                }


                                        }

                                        if (candidateRows.length > 0) {
                                                maxScore = 1000;
                                        }

                                        double bestScore = 1050;
                                        if (candidateRows.length == 0) {
                                                alignedIndices.put(row.getID(), -1);
                                        } else {
                                                for (int i = 0; i < candidateRows.length; i++) {
                                                        if (scores[i] < maxScore + 50) {
                                                                if (alignedIndices.containsKey(row.getID())) {
                                                                        if (scores[i] < bestScore) {
                                                                                alignedIndices.put(row.getID(), i);
                                                                                bestScore = scores[i];
                                                                        }
                                                                } else {
                                                                        alignedIndices.put(row.getID(), i);
                                                                }
                                                        }
                                                }
                                        }

                                        int id = alignedIndices.get(row.getID());
                                        if (id != -1) {
                                                PeakListRow masterRow = candidateRows[id];

                                                setColumns(row, masterRow);
                                                for (String name : peakList.getAllColumnNames()) {
                                                        masterRow.setPeak(name, (Double) row.getPeak(name));
                                                }
                                        } else {
                                                alignedPeakList.addRow(row.clone());
                                        }

                                        progress = (double) processedRows++ / (double) totalRows;

                                }

                        }
                }

                // Add new aligned peak list to the project
                GuineuCore.getDesktop().AddNewFile(alignedPeakList);

                // Add task description to peakList

                logger.info("Finished RANSAC aligner");
                status = TaskStatus.FINISHED;


        }

        public boolean Contains(PeakListRow[] candidateRows, PeakListRow candidate) {
                for (PeakListRow row : candidateRows) {
                        if (row == candidate) {
                                return true;
                        }
                }
                return false;
        }

        /**
         * Updates the value of some columns with the values of every data set combined.
         *
         * @param row Source row
         * @param targetRow Combined row
         */
        private void setColumns(PeakListRow row, PeakListRow targetRow) {
                // Aligment column
                int alignmentNumber = (Integer) targetRow.getVar(LCMSColumnName.ALIGNMENT.getGetFunctionName());
                if (alignmentNumber == -1) {
                        alignmentNumber = 1;
                }
                targetRow.setVar(LCMSColumnName.ALIGNMENT.getSetFunctionName(), ++alignmentNumber);
                // Num Found column
                double numberFound = (Double) targetRow.getVar(LCMSColumnName.NFOUND.getGetFunctionName());
                double numberFound2 = (Double) row.getVar(LCMSColumnName.NFOUND.getGetFunctionName());

                targetRow.setVar(LCMSColumnName.NFOUND.getSetFunctionName(), numberFound + numberFound2);

                // All Names column
                String name = (String) targetRow.getVar(LCMSColumnName.ALLNAMES.getGetFunctionName());
                String name2 = (String) row.getVar(LCMSColumnName.NAME.getGetFunctionName());
                String allNames = "";
                if (name != null && name.isEmpty()) {
                        allNames = name2;
                } else {
                        allNames = name + " // " + name2;
                }
                targetRow.setVar(LCMSColumnName.ALLNAMES.getSetFunctionName(), allNames);
        }

        private double getScore(Graph graphPeak, Graph graphCandidate) {
                List<Double[]> coords1 = graphPeak.getCoords();
                List<Double[]> coords2 = graphCandidate.getCoords();

                //System.out.println("sizes: " + coords1.size() + " - " + coords2.size());

                if (coords1.size() > coords2.size()) {
                        coords1 = graphCandidate.getCoords();
                        coords2 = graphPeak.getCoords();
                }

                double score = 0;

                for (Double[] coord1 : coords1) {
                        //System.out.println(coord1[0] + " - " + coord1[1]);
                        Double[] bestCoord = null;
                        double difference = 100000000.0;
                        for (Double[] coord2 : coords2) {
                                double diff1 = Math.abs(coord1[0] - coord2[0]);
                                double diff2 = Math.abs(coord1[1] - coord2[1]);

                                if ((diff1 + diff2) < difference) {
                                        difference = diff1 + diff2;
                                        bestCoord = coord2;
                                }
                        }

                        score += difference;
                        if (bestCoord != null) {
                                //System.out.println("Best: " + bestCoord[0] + " - " + bestCoord[1] + " diff: " + difference);
                                coords2.remove(bestCoord);
                        }
                }



                //System.out.println(score);

                return score;
        }

        private Graph getGraph(Dataset peakListY, PeakListRow row, double increaseWindow) {
                double rt = this.rtTolerance + increaseWindow;
                double mz = this.mzTolerance + (increaseWindow / 10);
                double minRT = ((SimplePeakListRowLCMS) row).getRT() - rt;
                if (minRT < 0) {
                        minRT = 0;
                }
                Range rtRange = new Range(minRT, ((SimplePeakListRowLCMS) row).getRT() + rt);
                double minMZ = ((SimplePeakListRowLCMS) row).getMZ() - mz;
                if (minMZ < 0) {
                        minMZ = 0;
                }
                Range mzRange = new Range(minMZ, ((SimplePeakListRowLCMS) row).getMZ() + mz);

                // Get all rows of the aligned peaklist within parameter limits
                PeakListRow candidateRows[] = ((SimpleLCMSDataset) peakListY).getRowsInsideRTAndMZRange(rtRange, mzRange);
                if (candidateRows.length < 3) {
                        this.increaseWindows += 5;
                        return getGraph(peakListY, row, this.increaseWindows);
                }

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
                                if (peak != row) {
                                        Double[] coord = new Double[2];
                                        coord[0] = peak.getRT() - ((SimplePeakListRowLCMS) row).getRT();
                                        coord[1] = peak.getMZ() - ((SimplePeakListRowLCMS) row).getMZ();
                                        this.coordinates.add(coord);
                                }
                        }
                }

                public List<Double[]> getCoords() {
                        return this.coordinates;
                }
        }
}
