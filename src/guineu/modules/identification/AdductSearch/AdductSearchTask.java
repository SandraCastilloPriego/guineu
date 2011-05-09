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
package guineu.modules.identification.AdductSearch;

import guineu.data.Dataset;
import guineu.data.DatasetType;
import guineu.data.PeakListRow;
import guineu.data.impl.peaklists.SimplePeakListRowLCMS;
import guineu.main.GuineuCore;
import guineu.taskcontrol.Task;
import guineu.taskcontrol.TaskStatus;
import guineu.util.PeakListRowSorter;
import guineu.util.SortingDirection;
import guineu.util.SortingProperty;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

public class AdductSearchTask implements Task {

        private Logger logger = Logger.getLogger(this.getClass().getName());
        private TaskStatus status = TaskStatus.WAITING;
        private String errorMessage;
        private int finishedRows, totalRows;
        private Dataset peakList;
        private double rtTolerance, mzTolerance, maxAdductHeight,
                customMassDifference;
        private AdductType[] selectedAdducts;

        /**
         * @param parameters
         * @param peakList
         */
        public AdductSearchTask(AdductSearchParameters parameters, Dataset peakList) {

                this.peakList = peakList;
                rtTolerance = parameters.getParameter(AdductSearchParameters.rtTolerance).getValue().getTolerance();
                mzTolerance = parameters.getParameter(AdductSearchParameters.mzTolerance).getValue().getTolerance();
                selectedAdducts = parameters.getParameter(AdductSearchParameters.adducts).getValue();
                customMassDifference = parameters.getParameter(AdductSearchParameters.customAdductValue).getDouble();
                maxAdductHeight = parameters.getParameter(AdductSearchParameters.maxAdductHeight).getDouble();

        }

        public void cancel() {
                status = TaskStatus.CANCELED;
        }

        public String getErrorMessage() {
                return errorMessage;
        }

        public double getFinishedPercentage() {
                if (totalRows == 0) {
                        return 0;
                }
                return ((double) finishedRows) / totalRows;
        }

        public TaskStatus getStatus() {
                return status;
        }

        public String getTaskDescription() {
                return "Identification of adducts in " + peakList;
        }

        /**
         * @see java.lang.Runnable#run()
         */
        public void run() {

                status = TaskStatus.PROCESSING;

                if(peakList.getType() != DatasetType.LCMS){
                        status = TaskStatus.ERROR;
                        errorMessage = "Wrong data set type. This module is for the adduct search in LC-MS data";
                        return;
                }

                logger.info("Starting adducts search in " + peakList);

                List<PeakListRow> rows = peakList.getRows();
                totalRows = rows.size();

                PeakListRow[] arrayRows = rows.toArray(new PeakListRow[0]);

                // Start with the highest peaks
                Arrays.sort(arrayRows, new PeakListRowSorter(SortingProperty.Height,
                        SortingDirection.Descending));

                // Compare each two rows against each other
                for (int i = 0; i < totalRows; i++) {

                        SimplePeakListRowLCMS peak1 = (SimplePeakListRowLCMS) arrayRows[i];

                        for (int j = i + 1; j < arrayRows.length; j++) {

                                // Task canceled?
                                if (status == TaskStatus.CANCELED) {
                                        return;
                                }
                                SimplePeakListRowLCMS peak2 = (SimplePeakListRowLCMS) arrayRows[j];


                                // Treat the smaller m/z peak as main peak and check if the
                                // bigger one may be an adduct
                                if (peak1.getMZ() > peak2.getMZ()) {
                                        checkAllAdducts(peak2, peak1);
                                } else {
                                        checkAllAdducts(peak1, peak2);
                                }

                        }

                        finishedRows++;

                }


                status = TaskStatus.FINISHED;

                logger.info("Finished adducts search in " + peakList);

        }

        /**
         * Check if candidate peak may be a possible adduct of a given main peak
         *
         * @param mainPeak
         * @param possibleFragment
         */
        private void checkAllAdducts(SimplePeakListRowLCMS mainRow, SimplePeakListRowLCMS possibleAdduct) {

                for (AdductType adduct : selectedAdducts) {

                        if (checkAdduct(mainRow, possibleAdduct, adduct)) {
                                addAdductInfo(mainRow, possibleAdduct, adduct);
                        }
                }
        }

        /**
         * Check if candidate peak is a given type of adduct of given main peak
         *
         * @param mainPeak
         * @param possibleFragment
         * @param adduct
         * @return
         */
        private boolean checkAdduct(SimplePeakListRowLCMS mainPeak,
                SimplePeakListRowLCMS possibleAdduct, AdductType adduct) {

                // Calculate expected mass difference of this adduct
                double expectedMzDifference;
                if (adduct == AdductType.CUSTOM) {
                        expectedMzDifference = customMassDifference;
                } else {
                        expectedMzDifference = adduct.getMassDifference();
                }

                // Check mass difference condition
                double mzDifference = Math.abs(mainPeak.getMZ() + expectedMzDifference - possibleAdduct.getMZ());
                if (mzDifference > mzTolerance) {
                        return false;
                }

                // Check retention time condition
                double rtDifference = Math.abs(mainPeak.getRT() - possibleAdduct.getRT());
                if (rtDifference > rtTolerance) {
                        return false;
                }

                // Check height condition
                if (getHeight(possibleAdduct) > getHeight(mainPeak) * maxAdductHeight) {
                        return false;
                }

                return true;

        }

        private double getHeight(SimplePeakListRowLCMS row) {
                Double[] peaks = row.getPeaks();
                double value = 0;
                for (Double peak : peaks) {
                        value += peak;
                }
                if (value > 0) {
                        return value / peaks.length;
                } else {
                        return -1;
                }
        }

        /**
         * Add new identity to the adduct row
         *
         * @param mainRow
         * @param fragmentRow
         */
        private void addAdductInfo(SimplePeakListRowLCMS mainRow, SimplePeakListRowLCMS adductRow,
                AdductType adduct) {
                NumberFormat mzFormat = GuineuCore.getMZFormat();
                String adductName = adduct.getName() + " adduct of " + mzFormat.format(mainRow.getMZ()) + " m/z" + " - " + mainRow.getName();

                adductRow.setName(adductName);
        }

        public Object[] getCreatedObjects() {
                return null;
        }
}
