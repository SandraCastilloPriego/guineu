/*
 * Copyright 2007-2013 VTT Biotechnology
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
package guineu.modules.filter.Alignment.RANSACGCGC;

import guineu.data.Dataset;
import guineu.data.GCGCColumnName;
import guineu.data.PeakListRow;
import guineu.data.impl.datasets.SimpleGCGCDataset;
import guineu.data.impl.peaklists.SimplePeakListRowGCGC;
import guineu.main.GuineuCore;
import guineu.parameters.parametersType.RTTolerance;
import guineu.taskcontrol.AbstractTask;
import guineu.taskcontrol.TaskStatus;
import guineu.util.Range;
import java.util.*;
import org.apache.commons.math.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math.optimization.fitting.PolynomialFitter;
import org.apache.commons.math.optimization.general.GaussNewtonOptimizer;
import org.apache.commons.math.stat.regression.SimpleRegression;

public class RansacGCGCAlignerTask extends AbstractTask {

        private Dataset peakLists[], alignedPeakList;
        // Processed rows counter
        private int processedRows, totalRows;
        // Parameters
        private String peakListName;
        private RTTolerance rtiTolerance;
        private RTTolerance rt1Tolerance;
        private RTTolerance rt2Tolerance;
        private RTTolerance rtToleranceAfterRTcorrection;
        private boolean useOnlyRTI;
        private RansacGCGCAlignerParameters parameters;
        private double progress;
        private RANSACGCGC ransac;

        public RansacGCGCAlignerTask(Dataset[] peakLists, RansacGCGCAlignerParameters parameters) {

                this.peakLists = peakLists;
                this.parameters = parameters;

                // Get parameter values for easier use
                peakListName = parameters.getParameter(RansacGCGCAlignerParameters.peakListName).getValue();

                rtiTolerance = parameters.getParameter(RansacGCGCAlignerParameters.RTITolerance).getValue();

                rt2Tolerance = parameters.getParameter(RansacGCGCAlignerParameters.RT2Tolerance).getValue();

                rt1Tolerance = parameters.getParameter(RansacGCGCAlignerParameters.RTTolerance).getValue();

                rtToleranceAfterRTcorrection = parameters.getParameter(RansacGCGCAlignerParameters.RTToleranceValueAbs).getValue();

                useOnlyRTI = parameters.getParameter(RansacGCGCAlignerParameters.UseRTI).getValue();
        }

        public String getTaskDescription() {
                return "Ransac aligner, " + peakListName + " (" + peakLists.length + " peak lists)";
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

        public void cancel() {
                setStatus(TaskStatus.CANCELED);
        }

        /**
         * @see Runnable#run()
         */
        public void run() {
                setStatus(TaskStatus.PROCESSING);

                // Remember how many rows we need to process.
                for (int i = 0; i < peakLists.length; i++) {
                        totalRows += peakLists[i].getNumberRows() * 2;
                }


                // Create a new aligned peak list                
                this.alignedPeakList = peakLists[0].clone();

                this.alignedPeakList.setDatasetName(this.peakListName);

                this.alignedPeakList.setType(peakLists[0].getType());

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

                for (PeakListRow row : alignedPeakList.getRows()) {
                        row.setVar(GCGCColumnName.NFOUND.getSetFunctionName(), new Double(-1));
                }

                // Ransac alignmnent
                // Iterate source peak lists
                for (Dataset peakList : peakLists) {
                        if (peakList != peakLists[0]) {
                                HashMap<PeakListRow, PeakListRow> alignmentMapping = this.getAlignmentMap(peakList);

                                // Align all rows using mapping
                                for (PeakListRow row : peakList.getRows()) {
                                        PeakListRow targetRow = alignmentMapping.get(row);

                                        // If we have no mapping for this row, add a new one
                                        if (targetRow == null) {
                                                alignedPeakList.addRow(row.clone());
                                        } else {
                                                setColumns(row, targetRow);

                                                // Add all peaks from the original row to the aligned row

                                                for (String file : peakList.getAllColumnNames()) {
                                                        try {
                                                                if (row.getPeak(file).getClass() == Double.class) {
                                                                        targetRow.setPeak(file, (Double) row.getPeak(file));
                                                                } else {
                                                                        targetRow.setPeak(file, (String) row.getPeak(file));
                                                                }
                                                        } catch (Exception ee) {
                                                                targetRow.setPeak(file, (String) row.getPeak(file));
                                                        }
                                                }
                                        }
                                        progress = (double) processedRows++ / (double) totalRows;
                                }
                        }

                }

                for (PeakListRow row : alignedPeakList.getRows()) {
                        double alignmentNumber = (Double) row.getVar(GCGCColumnName.NFOUND.getGetFunctionName());
                        if (alignmentNumber == -1) {
                                row.setVar(GCGCColumnName.NFOUND.getSetFunctionName(), new Double(1));
                        }
                }


                // Add new aligned peak list to the project
                GuineuCore.getDesktop().AddNewFile(alignedPeakList);

                // Add task description to peakList

                setStatus(TaskStatus.FINISHED);


        }

        /**
         * Updates the value of some columns with the values of every data set
         * combined.
         *
         * @param row Source row
         * @param targetRow Combined row
         */
        private void setColumns(PeakListRow row, PeakListRow targetRow) {
                // Aligment column
                double alignmentNumber = (Double) targetRow.getVar(GCGCColumnName.NFOUND.getGetFunctionName());
                if (alignmentNumber == -1) {
                        alignmentNumber = 1;
                }
                targetRow.setVar(GCGCColumnName.NFOUND.getSetFunctionName(), new Double(++alignmentNumber));

                // All Names column
                String name = (String) targetRow.getVar(GCGCColumnName.ALLNAMES.getGetFunctionName());
                String name2 = (String) row.getVar(GCGCColumnName.NAME.getGetFunctionName());
                String allNames = "";
                if (name != null && name.isEmpty()) {
                        allNames = name2;
                } else {
                        allNames = name + " // " + name2;
                }
                targetRow.setVar(GCGCColumnName.ALLNAMES.getSetFunctionName(), allNames);
        }

        /**
         *
         * @param peakList
         * @return
         */
        private HashMap<PeakListRow, PeakListRow> getAlignmentMap(
                Dataset peakList) {

                // Create a table of mappings for best scores
                HashMap<PeakListRow, PeakListRow> alignmentMapping = new HashMap<PeakListRow, PeakListRow>();

                if (alignedPeakList.getNumberRows() < 1) {
                        return alignmentMapping;
                }

                // Create a sorted set of scores matching
                TreeSet<RowVsRowGCGCScore> scoreSet = new TreeSet<RowVsRowGCGCScore>();

                // RANSAC algorithm
                List<AlignGCGCStructMol> list = ransacPeakLists(alignedPeakList, peakList);
                PolynomialFunction function = this.getPolynomialFunction(list, ((SimpleGCGCDataset) alignedPeakList).getRowsRTRange());

                PeakListRow allRows[] = peakList.getRows().toArray(new PeakListRow[0]);

                for (PeakListRow row : allRows) {
                        double rt = 0;
                        if (!this.useOnlyRTI) {
                                rt = function.value(((SimplePeakListRowGCGC) row).getRT1());
                                if (Double.isNaN(rt) || rt == -1) {
                                        rt = ((SimplePeakListRowGCGC) row).getRT1();
                                }
                        } else {
                                try {
                                        rt = function.value(((SimplePeakListRowGCGC) row).getRTI());
                                        if (Double.isNaN(rt) || rt == -1) {
                                                rt = ((SimplePeakListRowGCGC) row).getRTI();
                                        }
                                } catch (Exception ee) {
                                }
                        }
                        PeakListRow candidateRows[] = null;
                        if (!this.useOnlyRTI) {
                                Range RTIRange = this.rtiTolerance.getToleranceRange(((SimplePeakListRowGCGC) row).getRTI());
                                Range RT1Range = this.rtToleranceAfterRTcorrection.getToleranceRange(rt);
                                Range RT2Range = this.rt2Tolerance.getToleranceRange(((SimplePeakListRowGCGC) row).getRT2());
                                // Get all rows of the aligned peaklist within parameter limits
                                candidateRows = ((SimpleGCGCDataset) alignedPeakList).getRowsInsideRT1RT2RTIRange(RT1Range, RT2Range,
                                        RTIRange);
                        } else {
                                Range RTIRange = this.rtiTolerance.getToleranceRange(((SimplePeakListRowGCGC) row).getRTI());
                                candidateRows = ((SimpleGCGCDataset) alignedPeakList).getRowsInsideRT1RT2RTIRange(RTIRange);
                        }
                        for (PeakListRow candidate : candidateRows) {
                                RowVsRowGCGCScore score;
                                try {
                                        score = new RowVsRowGCGCScore(row, candidate, rtiTolerance.getTolerance(),
                                                rtToleranceAfterRTcorrection.getTolerance(), rt);

                                        scoreSet.add(score);
                                        errorMessage = score.getErrorMessage();

                                } catch (Exception e) {
                                        e.printStackTrace();
                                        setStatus(TaskStatus.ERROR);
                                        return null;
                                }
                        }
                        progress = (double) processedRows++ / (double) totalRows;
                }

                // Iterate scores by descending order
                Iterator<RowVsRowGCGCScore> scoreIterator = scoreSet.iterator();
                while (scoreIterator.hasNext()) {

                        RowVsRowGCGCScore score = scoreIterator.next();

                        // Check if the row is already mapped
                        if (alignmentMapping.containsKey(score.getPeakListRow())) {
                                continue;
                        }

                        // Check if the spectra score is unacceptable
                        if (score.score == -10) {
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
        private List<AlignGCGCStructMol> ransacPeakLists(Dataset alignedPeakList,
                Dataset peakList) {
                List<AlignGCGCStructMol> list = this.getVectorAlignment(alignedPeakList,
                        peakList);
                ransac = new RANSACGCGC(parameters);
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
        private PolynomialFunction getPolynomialFunction(List<AlignGCGCStructMol> list, Range RTrange) {
                List<GCGCRTs> data = new ArrayList<GCGCRTs>();
                for (AlignGCGCStructMol m : list) {
                        if (m.Aligned) {
                                data.add(new GCGCRTs(m.RT2, m.RT));
                        }
                }

                data = this.smooth(data, RTrange);
                Collections.sort(data, new GCGCRTs());

                try {
                        PolynomialFitter fitter = new PolynomialFitter(3, new GaussNewtonOptimizer(true));
                        for (GCGCRTs rt : data) {
                                fitter.addObservedPoint(1, rt.RT, rt.RT2);
                        }

                        return fitter.fit();
                } catch (Exception ex) {
                        return null;
                }
        }

        private List<GCGCRTs> smooth(List<GCGCRTs> list, Range RTrange) {

                // Add one point at the begining and another at the end of the list to
                // ampliate the RT limits to cover the RT range completly
                try {
                        Collections.sort(list, new GCGCRTs());

                        GCGCRTs firstPoint = list.get(0);
                        GCGCRTs lastPoint = list.get(list.size() - 1);

                        double min = Math.abs(firstPoint.RT - RTrange.getMin());

                        double RTx = firstPoint.RT - min;
                        double RTy = firstPoint.RT2 - min;

                        GCGCRTs newPoint = new GCGCRTs(RTx, RTy);
                        list.add(newPoint);

                        double max = Math.abs(RTrange.getMin() - lastPoint.RT);
                        RTx = lastPoint.RT + max;
                        RTy = lastPoint.RT2 + max;

                        newPoint = new GCGCRTs(RTx, RTy);
                        list.add(newPoint);
                } catch (Exception exception) {
                }

                // Add points to the model in between of the real points to smooth the regression model
                Collections.sort(list, new GCGCRTs());

                for (int i = 0; i < list.size() - 1; i++) {
                        GCGCRTs point1 = list.get(i);
                        GCGCRTs point2 = list.get(i + 1);
                        if (point1.RT < point2.RT - 2) {
                                SimpleRegression regression = new SimpleRegression();
                                regression.addData(point1.RT, point1.RT2);
                                regression.addData(point2.RT, point2.RT2);
                                double rt = point1.RT + 1;
                                while (rt < point2.RT) {
                                        GCGCRTs newPoint = new GCGCRTs(rt, regression.predict(rt));
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
        private List<AlignGCGCStructMol> getVectorAlignment(Dataset peakListX,
                Dataset peakListY) {

                List<AlignGCGCStructMol> alignMol = new ArrayList<AlignGCGCStructMol>();
                for (PeakListRow row : peakListX.getRows()) {

                        if (getStatus() == TaskStatus.CANCELED) {
                                return null;
                        }
                        // Calculate limits for a row with which the row can be aligned 
                        PeakListRow candidateRows[] = null;
                        if (!this.useOnlyRTI) {
                                Range rtiRange = this.rtiTolerance.getToleranceRange(((SimplePeakListRowGCGC) row).getRTI());
                                Range rtRange = this.rt1Tolerance.getToleranceRange(((SimplePeakListRowGCGC) row).getRT1());
                                Range rt2Range = this.rt2Tolerance.getToleranceRange(((SimplePeakListRowGCGC) row).getRT2());

                                // Get all rows of the aligned peaklist within parameter limits                       
                                candidateRows = ((SimpleGCGCDataset) peakListY).getRowsInsideRT1RT2RTIRange(rtRange, rt2Range, rtiRange);
                        } else {
                                Range rtiRange = this.rtiTolerance.getToleranceRange(((SimplePeakListRowGCGC) row).getRTI());
                                candidateRows = ((SimpleGCGCDataset) peakListY).getRowsInsideRT1RT2RTIRange(rtiRange);
                        }
                        for (PeakListRow candidateRow : candidateRows) {
                                alignMol.add(new AlignGCGCStructMol((SimplePeakListRowGCGC) row, (SimplePeakListRowGCGC) candidateRow, this.useOnlyRTI));
                        }
                }

                return alignMol;
        }
}
