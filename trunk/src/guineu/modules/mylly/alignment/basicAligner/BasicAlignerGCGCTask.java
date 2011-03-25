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
package guineu.modules.mylly.alignment.basicAligner;

import guineu.data.Dataset;
import guineu.data.PeakListRow;
import guineu.data.impl.datasets.SimpleGCGCDataset;
import guineu.data.impl.peaklists.SimplePeakListRowGCGC;
import guineu.main.GuineuCore;
import guineu.taskcontrol.Task;
import guineu.taskcontrol.TaskStatus;
import guineu.util.Range;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BasicAlignerGCGCTask implements Task {

        private Logger logger = Logger.getLogger(this.getClass().getName());
        private Dataset peakLists[], alignedPeakList;
        private TaskStatus status = TaskStatus.WAITING;
        private String errorMessage;
        // Processed rows counter
        private int processedRows, totalRows;
        // Parameters
        private String peakListName;
        private double RT1Tolerance;
        private double RT2Tolerance;
        private double progress;

        public BasicAlignerGCGCTask(Dataset[] peakLists, BasicAlignerGCGCParameters parameters) {

                this.peakLists = peakLists;

                // Get parameter values for easier use
                peakListName = parameters.getParameter(BasicAlignerGCGCParameters.peakListName).getValue();

                RT1Tolerance = parameters.getParameter(BasicAlignerGCGCParameters.RT1Tolerance).getValue().getTolerance();

                RT2Tolerance = parameters.getParameter(BasicAlignerGCGCParameters.RT2Tolerance).getValue().getTolerance();
        }

        
        public String getTaskDescription() {
                return "Basic aligner, " + peakListName + " (" + peakLists.length + " peak lists)";
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

       
        public void run() {

                status = TaskStatus.PROCESSING;
                logger.info("Running basic aligner");

                // Remember how many rows we need to process. Each row will be processed
                // twice, first for score calculation, second for actual alignment.
                for (int i = 0; i < peakLists.length; i++) {
                        totalRows += peakLists[i].getNumberRows() * 2;
                }

                // Create a new aligned peak list
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

                // Iterate source peak lists
                for (Dataset peakList : peakLists) {

                        // Create a sorted set of scores matching
                        TreeSet<RowVsRowScore> scoreSet = new TreeSet<RowVsRowScore>();

                        PeakListRow allRows[] = peakList.getRows().toArray(new PeakListRow[0]);

                        // Calculate scores for all possible alignments of this row
                        for (PeakListRow row : allRows) {

                                if (status == TaskStatus.CANCELED) {
                                        return;
                                }

                                // Calculate limits for a row with which the row can be aligned
                                double RT1Min = ((SimplePeakListRowGCGC) row).getRT1() - this.RT1Tolerance;
                                double RT1Max = ((SimplePeakListRowGCGC) row).getRT1() + this.RT1Tolerance;

                                double RT2Min = ((SimplePeakListRowGCGC) row).getRT2() - this.RT2Tolerance;
                                double RT2Max = ((SimplePeakListRowGCGC) row).getRT2() + this.RT2Tolerance;

                                // Get all rows of the aligned peaklist within parameter limits
                                PeakListRow candidateRows[] = ((SimpleGCGCDataset) alignedPeakList).getRowsInsideRT1AndRT2Range(new Range(RT1Min, RT1Max),
                                        new Range(RT2Min, RT2Max));

                                // Calculate scores and store them
                                for (PeakListRow candidate : candidateRows) {
                                        try {
                                                /*  if (sameIDRequired) {
                                                if (!PeakUtils.compareIdentities(row, candidate)) {
                                                continue;
                                                }
                                                }*/
                                                RowVsRowScore score = new RowVsRowScore(row, candidate, this.RT1Tolerance, 10, this.RT2Tolerance, 10);
                                                scoreSet.add(score);
                                        } catch (Exception ex) {
                                                Logger.getLogger(BasicAlignerGCGCTask.class.getName()).log(Level.SEVERE, null, ex);
                                        }

                                }

                                processedRows++;

                        }

                        // Create a table of mappings for best scores
                        Hashtable<PeakListRow, PeakListRow> alignmentMapping = new Hashtable<PeakListRow, PeakListRow>();

                        // Iterate scores by descending order
                        Iterator<RowVsRowScore> scoreIterator = scoreSet.iterator();
                        while (scoreIterator.hasNext()) {

                                RowVsRowScore score = scoreIterator.next();

                                // Check if the row is already mapped
                                if (alignmentMapping.containsKey(score.getPeakListRow())) {
                                        continue;
                                }

                                // Check if the aligned row is already filled
                                if (alignmentMapping.containsValue(score.getAlignedRow())) {
                                        continue;
                                }

                                alignmentMapping.put(score.getPeakListRow(), score.getAlignedRow());

                        }

                        // Align all rows using mapping
                        for (PeakListRow row : peakList.getRows()) {
                                PeakListRow targetRow = alignmentMapping.get(row);

                                // If we have no mapping for this row, add a new one
                                if (targetRow == null) {
                                        alignedPeakList.addRow(row.clone());
                                } else {
                                        //    setColumns(row, targetRow);

                                        // Add all peaks from the original row to the aligned row
                                        for (String file : peakList.getAllColumnNames()) {
                                                targetRow.setPeak(file, (Double) row.getPeak(file));
                                        }
                                }
                                progress = (double) processedRows++ / (double) totalRows;
                        }
                } // Next peak list

                // Add new aligned peak list to the project
                GuineuCore.getDesktop().AddNewFile(alignedPeakList);



                logger.info("Finished Basic aligner");
                status = TaskStatus.FINISHED;

        }

        public Object[] getCreatedObjects() {
                return new Object[]{alignedPeakList};
        }
}
