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
import guineu.data.LCMSColumnName;
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
        private DynamicAlignerParameters parameters;
        private double progress;

        public DynamicAlignerTask(Dataset[] peakLists, DynamicAlignerParameters parameters) {

                this.peakLists = peakLists;
                this.parameters = parameters;

                // Get parameter values for easier use
                peakListName = (String) parameters.getParameterValue(DynamicAlignerParameters.peakListName);

                mzTolerance = (Double) parameters.getParameterValue(DynamicAlignerParameters.MZTolerance);

                rtTolerance = (Double) parameters.getParameterValue(DynamicAlignerParameters.RTTolerance);

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
                                Matrix matrix = new Matrix(alignedPeakList, peakList);
                                //for each row in the main file which contains all the samples align until that moment.. get the graph of peaks..
                                for (PeakListRow row : alignedPeakList.getRows()) {
                                        Graph rowGraph = this.getGraph(peakList, row);
                                      /*  double minRT = ((SimplePeakListRowLCMS) row).getRT() - this.rtTolerance;
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

                                        PeakListRow bestCandidate = null;
                                        double bestScore = 1000000;*/
                                        for (PeakListRow candidate : peakList.getRows()) {
                                                double score = this.getScore(rowGraph, alignmentMapping.get(candidate));
                                                matrix.setScore(row, candidate, score);
                                               /* if (score < bestScore) {
                                                        bestCandidate = candidate;
                                                        bestScore = score;
                                                }*/
                                        }
                                       /* if (bestCandidate != null) {
                                                for (String sampleNames : peakList.getAllColumnNames()) {
                                                        setColumns(bestCandidate, row);
                                                        row.setPeak(sampleNames, (Double) bestCandidate.getPeak(sampleNames));
                                                }
                                        }*/

                                        progress = (double) processedRows++ / (double) totalRows;
                                }
				matrix.computeAlignmentMatrix();
				matrix.getAlignment();
				/* matrix.masterIndex and matrix.rowIndex give the indices
				 * of the alignment now!
				 */
                        }
                }

                // Add new aligned peak list to the project
                GuineuCore.getDesktop().AddNewFile(alignedPeakList);

                // Add task description to peakList

                logger.info("Finished RANSAC aligner");
                status = TaskStatus.FINISHED;


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

                if (coords1.size() > coords2.size()) {
                        coords1 = graphCandidate.getCoords();
                        coords2 = graphPeak.getCoords();
                }
                double score = 0;

                Double[] bestCoord = null;
                double difference = 1000;
                for (Double[] coord1 : coords1) {
                        for (Double[] coord2 : coords2) {
                                double diff1 = Math.abs(coord1[0] - coord2[0]);
                                double diff2 = Math.abs(coord1[1] - coord2[1]);

                                if (diff1 + diff2 < difference) {
                                        difference = diff1 + diff2;
                                        bestCoord = coord2;
                                }
                        }

                        score += difference;
                        if (bestCoord != null) {
                                coords2.remove(bestCoord);
                        }
                }



                return score;
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

                public List<Double[]> getCoords() {
                        return this.coordinates;
                }
        }

        class Matrix {

                double[][] values;
		double[][] alignmentMatrix;
                List<PeakListRow> masterRows, rows;
		List<Integer> masterIndex, rowIndex;
		double gapPenalty;

                public Matrix(Dataset masterList, Dataset peakList){
                        masterRows = masterList.getRows();
                        rows = peakList.getRows();
                        values = new double[masterList.getNumberRows()][peakList.getNumberRows()];
                }

                public void setScore(PeakListRow masterRow, PeakListRow row, double score){
                      int index1 = masterRows.indexOf(masterRow);
                      int index2 = rows.indexOf(row);
                      values[index1][index2] = 1.0/score;
                }

		public void computeAlignmentMatrix() {
			alignmentMatrix = new double[masterRows.size()+1][rows.size()+1];

			gapPenalty = 5; /* TODO: make it sample dependent e.g. max RT shift */

			for(int i=0; i <= masterRows.size(); i++) {
				alignmentMatrix[i][0] = gapPenalty*i;
			}
			for(int j=0; j <= rows.size(); j++) {
				alignmentMatrix[0][j] = gapPenalty*j;
			}

			for(int i = 1; i <= masterRows.size(); i++) {
				for(int j = 1; j <= rows.size(); j++) {
					double match = alignmentMatrix[i-1][j-1] + values[i-1][j-1];
					double delete = alignmentMatrix[i-1][j] + gapPenalty;
					double insert = alignmentMatrix[i][j-1] + gapPenalty;
					alignmentMatrix[i][j] = Math.max(match, Math.max(delete, insert));
				}
			}
		}

		public void getAlignment() {
			int i = masterRows.size();
			int j = rows.size();

			while((i > 0) && (j > 0)) {

				if(alignmentMatrix[i][j] == alignmentMatrix[i-1][j-1] + values[i-1][j-1]) {
					masterIndex.add(i-1);
					rowIndex.add(j-1);
					i--; j--;
				} else if(alignmentMatrix[i][j] == alignmentMatrix[i-1][j] + gapPenalty) {
					masterIndex.add(i-1);
					rowIndex.add(-1); /* -1 represents a gap */
					i--;
				} else if(alignmentMatrix[i][j] == alignmentMatrix[i][j-1] + gapPenalty) {
					masterIndex.add(-1);
					rowIndex.add(j-1);
					j--;
				}
			}

			while(i > 0) {
				masterIndex.add(i-1);
				rowIndex.add(-1);
				i--;
			}

			while(j > 0) {
				masterIndex.add(-1);
				rowIndex.add(j-1);
				j--;
			}
		}
	}
}
