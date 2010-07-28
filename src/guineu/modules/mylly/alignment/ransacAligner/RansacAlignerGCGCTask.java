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
package guineu.modules.mylly.alignment.ransacAligner;

import guineu.modules.mylly.alignment.ransacAligner.functions.*;
import guineu.data.Dataset;
import guineu.data.GCGCColumnName;
import guineu.data.PeakListRow;
import guineu.data.impl.datasets.SimpleGCGCDataset;
import guineu.data.impl.peaklists.SimplePeakListRowGCGC;
import guineu.main.GuineuCore;
import guineu.taskcontrol.Task;
import guineu.taskcontrol.TaskStatus;
import guineu.util.Range;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;
import java.util.Vector;
import java.util.logging.Logger;
import org.apache.commons.math.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math.optimization.fitting.PolynomialFitter;
import org.apache.commons.math.optimization.general.GaussNewtonOptimizer;
import org.apache.commons.math.stat.regression.SimpleRegression;

public class RansacAlignerGCGCTask implements Task {

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
        private double rtToleranceValueAbs;
        private RansacAlignerGCGCParameters parameters;
        private double progress;
        private RANSAC ransac;

        public RansacAlignerGCGCTask(Dataset[] peakLists, RansacAlignerGCGCParameters parameters) {

                this.peakLists = peakLists;
                this.parameters = parameters;

                // Get parameter values for easier use
                peakListName = (String) parameters.getParameterValue(RansacAlignerGCGCParameters.peakListName);

                RT1Tolerance = (Double) parameters.getParameterValue(RansacAlignerGCGCParameters.RT1Tolerance);

                RT2Tolerance = (Double) parameters.getParameterValue(RansacAlignerGCGCParameters.RTTolerance);

                rtToleranceValueAbs = (Double) parameters.getParameterValue(RansacAlignerGCGCParameters.RTToleranceValueAbs);
        }

        /**
         * @see net.sf.mzmine.taskcontrol.Task#getTaskDescription()
         */
        public String getTaskDescription() {
                return "Ransac aligner, " + peakListName + " (" + peakLists.length + " peak lists)";
        }

        /**
         * @see net.sf.mzmine.taskcontrol.Task#getFinishedPercentage()
         */
        public double getFinishedPercentage() {
                if (totalRows == 0) {
                        return 0f;
                }
                if (ransac != null) {
                        return ransac.getProgress();
                }
                return progress; //
        }

        /**
         * @see net.sf.mzmine.taskcontrol.Task#getStatus()
         */
        public TaskStatus getStatus() {
                return status;
        }

        /**
         * @see net.sf.mzmine.taskcontrol.Task#getErrorMessage()
         */
        public String getErrorMessage() {
                return errorMessage;
        }

        /**
         * @see net.sf.mzmine.taskcontrol.Task#cancel()
         */
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

                // Ransac alignmnent
                // Iterate source peak lists
                for (Dataset peakList : peakLists) {
                        if (peakList != peakLists[0]) {

                                Hashtable<PeakListRow, PeakListRow> alignmentMapping = this.getAlignmentMap(peakList);

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

                        }

                }

                // Add new aligned peak list to the project
                GuineuCore.getDesktop().AddNewFile(alignedPeakList);

                // Add task description to peakList

                logger.info("Finished RANSAC aligner");
                status = TaskStatus.FINISHED;


        }
        
        /**
         *
         * @param peakList
         * @return
         */
        private Hashtable<PeakListRow, PeakListRow> getAlignmentMap(
                Dataset peakList) {

                // Create a table of mappings for best scores
                Hashtable<PeakListRow, PeakListRow> alignmentMapping = new Hashtable<PeakListRow, PeakListRow>();

                if (alignedPeakList.getNumberRows() < 1) {
                        return alignmentMapping;
                }

                // Create a sorted set of scores matching
                TreeSet<RowVsRowScore> scoreSet = new TreeSet<RowVsRowScore>();

                // RANSAC algorithm
                Vector<AlignStructMol> list = ransacPeakLists(alignedPeakList, peakList);
                PolynomialFunction function = this.getPolynomialFunction(list, ((SimpleGCGCDataset) alignedPeakList).getRowsRTRange());

                PeakListRow allRows[] = peakList.getRows().toArray(new PeakListRow[0]);

                for (PeakListRow row : allRows) {
                        // Calculate limits for a row with which the row can be aligned
                        double RT1Min, RT1Max;
                        double RT2Min, RT2Max;

                        double RT2Fitted = function.value(((SimplePeakListRowGCGC) row).getRT2());

                        if (Double.isNaN(RT2Fitted) || RT2Fitted == -1) {
                                RT2Fitted = ((SimplePeakListRowGCGC) row).getRT2();
                        }

                        double rtToleranceValue = 0.0f;
                        rtToleranceValue = rtToleranceValueAbs;
                        RT2Min = RT2Fitted - rtToleranceValue;
                        RT2Max = RT2Fitted + rtToleranceValue;

                        double RT1Fitted = function.value(((SimplePeakListRowGCGC) row).getRT1());

                        if (Double.isNaN(RT1Fitted) || RT1Fitted == -1) {
                                RT1Fitted = ((SimplePeakListRowGCGC) row).getRT1();
                        }

                        RT1Min = RT1Fitted - rtToleranceValue;
                        RT1Max = RT1Fitted + rtToleranceValue;


                        // Get all rows of the aligned peaklist within parameter limits
                        PeakListRow candidateRows[] = ((SimpleGCGCDataset) alignedPeakList).getRowsInsideRT1AndRT2Range(new Range(RT1Min, RT1Max),
                                new Range(RT2Min, RT2Max));

                        for (PeakListRow candidate : candidateRows) {
                                RowVsRowScore score;
                                try {
                                        score = new RowVsRowScore(row, candidate, RT1Tolerance,
                                                RT2Tolerance, RT1Fitted, RT2Fitted);

                                        scoreSet.add(score);
                                        errorMessage = score.getErrorMessage();

                                } catch (Exception e) {
                                        e.printStackTrace();
                                        status = TaskStatus.ERROR;
                                        return null;
                                }
                        }
                        progress = (double) processedRows++ / (double) totalRows;
                }

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

                return alignmentMapping;
        }

        /**
         * RANSAC
         *
         * @param alignedPeakList
         * @param peakList
         * @return
         */
        private Vector<AlignStructMol> ransacPeakLists(Dataset alignedPeakList,
                Dataset peakList) {
                Vector<AlignStructMol> list = this.getVectorAlignment(alignedPeakList,
                        peakList);
                ransac = new RANSAC(parameters);
                ransac.alignment(list);
                return list;
        }

        /**
         * Returns the corrected RT of the row
         *
         * @param row
         * @param list
         * @return
         */
        private PolynomialFunction getPolynomialFunction(Vector<AlignStructMol> list, Range RTrange) {
                List<RTs> data = new ArrayList<RTs>();
                for (AlignStructMol m : list) {
                        if (m.Aligned) {
                                data.add(new RTs(m.RT12, m.RT1));
                                data.add(new RTs(m.RT22, m.RT2));
                        }
                }

                data = this.smooth(data, RTrange);
                Collections.sort(data, new RTs());

                try {
                        PolynomialFitter fitter = new PolynomialFitter(3, new GaussNewtonOptimizer(true));
                        for (RTs rt : data) {
                                fitter.addObservedPoint(1, rt.RT, rt.RT2);
                        }

                        return fitter.fit();
                } catch (Exception ex) {
                        return null;
                }
        }

        private List<RTs> smooth(List<RTs> list, Range RTrange) {

                // Add one point at the begining and another at the end of the list to
                // ampliate the RT limits to cover the RT range completly
                try {
                        Collections.sort(list, new RTs());

                        RTs firstPoint = list.get(0);
                        RTs lastPoint = list.get(list.size() - 1);

                        double min = Math.abs(firstPoint.RT - RTrange.getMin());

                        double RTx = firstPoint.RT - min;
                        double RTy = firstPoint.RT2 - min;

                        RTs newPoint = new RTs(RTx, RTy);
                        list.add(newPoint);

                        double max = Math.abs(RTrange.getMin() - lastPoint.RT);
                        RTx = lastPoint.RT + max;
                        RTy = lastPoint.RT2 + max;

                        newPoint = new RTs(RTx, RTy);
                        list.add(newPoint);
                } catch (Exception exception) {
                }

                // Add points to the model in between of the real points to smooth the regression model
                Collections.sort(list, new RTs());

                for (int i = 0; i < list.size() - 1; i++) {
                        RTs point1 = list.get(i);
                        RTs point2 = list.get(i + 1);
                        if (point1.RT < point2.RT - 2) {
                                SimpleRegression regression = new SimpleRegression();
                                regression.addData(point1.RT, point1.RT2);
                                regression.addData(point2.RT, point2.RT2);
                                double rt = point1.RT + 1;
                                while (rt < point2.RT) {
                                        RTs newPoint = new RTs(rt, regression.predict(rt));
                                        list.add(newPoint);
                                        rt++;
                                }

                        }
                }

                return list;
        }

        /**
         * Create the vector which contains all the possible aligned peaks.
         *
         * @param peakListX
         * @param peakListY
         * @return vector which contains all the possible aligned peaks.
         */
        private Vector<AlignStructMol> getVectorAlignment(Dataset peakListX,
                Dataset peakListY) {

                Vector<AlignStructMol> alignMol = new Vector<AlignStructMol>();
                for (PeakListRow row : peakListX.getRows()) {

                        if (status == TaskStatus.CANCELED) {
                                return null;
                        }
                        // Calculate limits for a row with which the row can be aligned
                        double RT1min = ((SimplePeakListRowGCGC) row).getRT1() - RT1Tolerance;
                        double RT1max = ((SimplePeakListRowGCGC) row).getRT1() + RT1Tolerance;
                        double RT2min, RT2max;
                        double rtToleranceValue = RT2Tolerance;
                        RT2min = ((SimplePeakListRowGCGC) row).getRT2() - rtToleranceValue;
                        RT2max = ((SimplePeakListRowGCGC) row).getRT2() + rtToleranceValue;
                        Range RT1Range = new Range(RT1min, RT1max);
                        Range RT2Range = new Range(RT2min, RT2max);

                        // Get all rows of the aligned peaklist within parameter limits
                        PeakListRow candidateRows[] = ((SimpleGCGCDataset) peakListY).getRowsInsideRT1AndRT2Range(RT1Range, RT2Range);

                        for (PeakListRow candidateRow : candidateRows) {
                                alignMol.addElement(new AlignStructMol((SimplePeakListRowGCGC) row, (SimplePeakListRowGCGC) candidateRow));
                        }
                }

                return alignMol;
        }

        public Object[] getCreatedObjects() {
                return new Object[]{alignedPeakList};
        }
}
