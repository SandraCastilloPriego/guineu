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
package guineu.modules.identification.linearnormalization;

import guineu.data.Dataset;
import guineu.data.DatasetType;
import guineu.data.PeakListRow;
import guineu.data.impl.peaklists.SimplePeakListRowGCGC;
import guineu.data.impl.peaklists.SimplePeakListRowLCMS;
import guineu.taskcontrol.Task;
import guineu.taskcontrol.TaskStatus;
import guineu.util.GUIUtils;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author Taken from MZmine2
 * http://mzmine.sourceforge.net/
 */
class LinearNormalizerTask implements Task {

        private Logger logger = Logger.getLogger(this.getClass().getName());
        static final double maximumOverallPeakHeightAfterNormalization = 100000.0;
        private Dataset originalPeakList, normalizedPeakList;
        private TaskStatus status = TaskStatus.WAITING;
        private String errorMessage;
        private int processedDataFiles, totalDataFiles;
        private String normalizationType;
        private List<PeakListRow> standards;

        public LinearNormalizerTask(Dataset peakList,
                LinearNormalizerParameters parameters) {

                normalizationType = (String) parameters.getParameterValue(LinearNormalizerParameters.normalizationType);
                this.originalPeakList = peakList;

                this.normalizedPeakList = this.originalPeakList.clone();
                this.normalizedPeakList.getRows().clear();
                this.normalizedPeakList.setDatasetName("Linear Normalization - " + this.originalPeakList.getDatasetName());

                totalDataFiles = originalPeakList.getNumberCols();

        }

        public void cancel() {
                status = TaskStatus.CANCELED;

        }

        public String getErrorMessage() {
                return errorMessage;
        }

        public double getFinishedPercentage() {
                return (double) processedDataFiles / (double) totalDataFiles;
        }

        public TaskStatus getStatus() {
                return status;
        }

        public String getTaskDescription() {
                return "Linear normalization of " + originalPeakList + " by " + normalizationType;
        }

        public void run() {

                status = TaskStatus.PROCESSING;
                logger.info("Running linear normalizer");

                if (normalizationType.equals(LinearNormalizerParameters.NormalizationTypeStandards)) {
                        standards = new ArrayList<PeakListRow>();
                        for (PeakListRow row : originalPeakList.getRows()) {
                                if (row.isSelected() || (originalPeakList.getType() == DatasetType.LCMS &&
                                        ((SimplePeakListRowLCMS) row).getStandard() == 1)) {
                                        standards.add(row);
                                }
                        }
                }


                // This hashtable maps rows from original alignment result to rows of
                // the normalized alignment
                Hashtable<PeakListRow, PeakListRow> rowMap = new Hashtable<PeakListRow, PeakListRow>();

                // Create new peak list
                //	normalizedPeakList = new Dataset(
                //			originalPeakList + " " + suffix)

                // Loop through all raw data files, and find the peak with biggest
                // height
                double maxOriginalHeight = 0.0;
                for (String file : originalPeakList.getAllColumnNames()) {
                        for (PeakListRow originalpeakListRow : originalPeakList.getRows()) {
                                Double p = (Double) originalpeakListRow.getPeak(file);
                                if (p != null) {
                                        if (maxOriginalHeight <= p) {
                                                maxOriginalHeight = p;
                                        }
                                }
                        }
                }

                // Loop through all raw data files, and normalize peak values
                for (String file : originalPeakList.getAllColumnNames()) {

                        // Cancel?
                        if (status == TaskStatus.CANCELED) {
                                return;
                        }

                        // Determine normalization type and calculate normalization factor
                        double normalizationFactor = 1.0;

                        // - normalization by average peak intensity
                        if (normalizationType == LinearNormalizerParameters.NormalizationTypeAverageIntensity) {
                                double intensitySum = 0;
                                int intensityCount = 0;
                                for (PeakListRow peakListRow : originalPeakList.getRows()) {
                                        Double p = (Double) peakListRow.getPeak(file);
                                        if (p != null) {
                                                intensitySum += p;

                                                intensityCount++;
                                        }
                                }
                                normalizationFactor = intensitySum / (double) intensityCount;
                        }

                        // - normalization by average squared peak intensity
                        if (normalizationType == LinearNormalizerParameters.NormalizationTypeAverageSquaredIntensity) {
                                double intensitySum = 0.0;
                                int intensityCount = 0;
                                for (PeakListRow peakListRow : originalPeakList.getRows()) {
                                        Double p = (Double) peakListRow.getPeak(file);
                                        if (p != null) {
                                                intensitySum += (p * p);

                                                intensityCount++;
                                        }
                                }
                                normalizationFactor = intensitySum / (double) intensityCount;
                        }

                        // - normalization by maximum peak intensity
                        if (normalizationType == LinearNormalizerParameters.NormalizationTypeMaximumPeakHeight) {
                                double maximumIntensity = 0.0;
                                for (PeakListRow peakListRow : originalPeakList.getRows()) {
                                        Double p = (Double) peakListRow.getPeak(file);
                                        if (p != null) {
                                                if (maximumIntensity < p) {
                                                        maximumIntensity = p;
                                                }


                                        }
                                }
                                normalizationFactor = maximumIntensity;
                        }

                        // - normalization by maximum peak intensity
                        if (normalizationType == LinearNormalizerParameters.NormalizationTypeStandards) {
                                // Normalize all peak intenisities using the normalization factor
                                for (PeakListRow originalpeakListRow : originalPeakList.getRows()) {

                                        // Cancel?
                                        if (status == TaskStatus.CANCELED) {
                                                return;
                                        }

                                        PeakListRow nearestStandard = this.getNearestStandard(originalpeakListRow);

                                        Double originalPeak = (Double) originalpeakListRow.getPeak(file);
                                        if (originalPeak != null) {
                                                Double normalizedPeak;
                                                double normalizedHeight = originalPeak / (Double) nearestStandard.getPeak(file);
                                                normalizedPeak = normalizedHeight;

                                                PeakListRow normalizedRow = rowMap.get(originalpeakListRow);

                                                if (normalizedRow == null) {
                                                        normalizedRow = originalpeakListRow.clone();
                                                        rowMap.put(originalpeakListRow, normalizedRow);
                                                }

                                                normalizedRow.setPeak(file, normalizedPeak);

                                        }

                                }

                        }



                        if (normalizationType != LinearNormalizerParameters.NormalizationTypeStandards) {
                                // Readjust normalization factor so that maximum height will be
                                // equal to maximumOverallPeakHeightAfterNormalization after
                                // normalization
                                double maxNormalizedHeight = maxOriginalHeight / normalizationFactor;
                                normalizationFactor = normalizationFactor * maxNormalizedHeight / maximumOverallPeakHeightAfterNormalization;

                                // Normalize all peak intenisities using the normalization factor
                                for (PeakListRow originalpeakListRow : originalPeakList.getRows()) {

                                        // Cancel?
                                        if (status == TaskStatus.CANCELED) {
                                                return;
                                        }

                                        Double originalPeak = (Double) originalpeakListRow.getPeak(file);
                                        if (originalPeak != null) {
                                                Double normalizedPeak;
                                                double normalizedHeight = originalPeak / normalizationFactor;
                                                normalizedPeak = normalizedHeight;

                                                PeakListRow normalizedRow = rowMap.get(originalpeakListRow);

                                                if (normalizedRow == null) {
                                                        normalizedRow = originalpeakListRow.clone();
                                                        rowMap.put(originalpeakListRow, normalizedRow);
                                                }

                                                normalizedRow.setPeak(file, normalizedPeak);

                                        }

                                }
                        }

                        // Progress
                        processedDataFiles++;

                }

                // Finally add all normalized rows to normalized alignment result
                for (PeakListRow originalpeakListRow : originalPeakList.getRows()) {
                        PeakListRow normalizedRow = rowMap.get(originalpeakListRow);
                        normalizedPeakList.addRow(normalizedRow);
                }

                GUIUtils.showNewTable(normalizedPeakList, true);

                logger.info("Finished linear normalizer");
                status = TaskStatus.FINISHED;

        }

        private PeakListRow getNearestStandard(PeakListRow row) {
                // Search for nearest standard
                PeakListRow nearestStandardRow = null;
                double nearestStandardRowDistance = Double.MAX_VALUE;

                for (int standardRowIndex = 0; standardRowIndex < standards.size(); standardRowIndex++) {
                        PeakListRow standardRow = standards.get(standardRowIndex);
                        if (originalPeakList.getType() == DatasetType.LCMS) {
                                double stdMZ = ((SimplePeakListRowLCMS) standardRow).getMZ();
                                double stdRT = ((SimplePeakListRowLCMS) standardRow).getRT();
                                double distance = 10 * Math.abs(((SimplePeakListRowLCMS) row).getMZ() - stdMZ) + Math.abs(((SimplePeakListRowLCMS) row).getRT() - stdRT);
                                if (distance <= nearestStandardRowDistance) {
                                        nearestStandardRow = standardRow;
                                        nearestStandardRowDistance = distance;
                                }
                        } else if (originalPeakList.getType() == DatasetType.GCGCTOF) {
                                double stdRT1 = ((SimplePeakListRowGCGC) standardRow).getRT1();
                                double stdRT2 = ((SimplePeakListRowGCGC) standardRow).getRT2();
                                double distance = Math.abs(((SimplePeakListRowGCGC) row).getRT1() - stdRT1) + Math.abs(((SimplePeakListRowGCGC) row).getRT2() - stdRT2);
                                if (distance <= nearestStandardRowDistance) {
                                        nearestStandardRow = standardRow;
                                        nearestStandardRowDistance = distance;
                                }
                        }

                }
                return nearestStandardRow;

        }

        public Object[] getCreatedObjects() {
                return new Object[]{normalizedPeakList};
        }
}
