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
package guineu.modules.filter.relatedpeaks;

import guineu.data.Dataset;
import guineu.data.PeakListRow;
import guineu.taskcontrol.AbstractTask;
import guineu.taskcontrol.TaskStatus;

/**
 *
 * @author scsandra
 */
public class RelatedPeaksTask extends AbstractTask {

        private double progress = 0.0f;
        private Dataset dataset;

        public RelatedPeaksTask(Dataset dataset) {
                this.dataset = dataset;
        }

        public String getTaskDescription() {
                return "Related Peaks filter... ";
        }

        public double getFinishedPercentage() {
                return progress;
        }

        public void cancel() {
                setStatus(TaskStatus.CANCELED);
        }

        public void run() {
                try {
                        int cont = 0;
                        setStatus(TaskStatus.PROCESSING);

                        for (PeakListRow row : dataset.getRows()) {
                                for (PeakListRow row2 : dataset.getRows()) {
                                        if (row.getVar("getAllNames") != null || row2.getVar("getAllNames") != null) {
                                                continue;
                                        }
                                        if (!((String) row.getVar("getName")).matches(".*TG.*|.*ChoE.*|.*unknown.*") || !((String) row2.getVar("getName")).matches(".*TG.*|.*ChoE.*|.*unknown.*")) {
                                                continue;
                                        }
                                        double mzDiff = (Double) row2.getVar("getMZ") - (Double) row.getVar("getMZ");
                                        if (mzDiff < 5.1 && mzDiff > 4.90) {
                                                double rtDiff = (Double) row.getVar("getRT") - (Double) row2.getVar("getRT");
                                                if (Math.abs(rtDiff) < 2) {
                                                        //if (this.getMean(row) < this.getMean(row2)) {
                                                        if (this.isAdduct(row2, row)) {
                                                                row2.setVar("setAllNames", "Group" + cont + "Adduct of: " + row.getVar("getName"));
                                                                row.setVar("setAllNames", "Group" + cont++);
                                                        }
                                                }
                                        }
                                }
                                /*if (!this.isGoodCandidate(row.getName()) || !row.getAllNames().matches(".*Deuterium.*") || !this.isRepeated(row)) {
                                ((SimplePeakListRowLCMS) row).setAllNames("");
                                }*/
                        }

                        setStatus(TaskStatus.FINISHED);
                } catch (Exception e) {
                        setStatus(TaskStatus.ERROR);
                        errorMessage = e.toString();
                        return;
                }
        }

        private boolean isAdduct(PeakListRow row, PeakListRow row2) {
                Double[] peaksRow1 = (Double[]) row.getPeaks();
                Double[] peaksRow2 = (Double[]) row2.getPeaks();
                for (int i = 0; i < row.getNumberPeaks(); i++) {
                        if (peaksRow1[i] > peaksRow2[i]) {
                                return false;
                        }
                }
                return true;
        }

        /*  private boolean isGoodCandidate(String name) {
        if (name.matches("unknown") || name.matches(".*TG.*") || name.matches(".*TAG.*") || name.matches(".*ChoE.*")) {
        return true;
        } else {
        return false;
        }
        }

        private boolean isRepeated(PeakListRow row) {
        for (PeakListRow rowCompared : dataset.getRows()) {
        int rowComparedGroup = this.getGroup(rowCompared.getAllNames());
        int rowGroup = this.getGroup(row.getAllNames());
        if (row != rowCompared && (rowComparedGroup != -1) && (rowGroup != -1) && rowComparedGroup == rowGroup) {
        if(row.getMZ() > rowCompared.getMZ()){
        if(this.getMean(row) < this.getMean(rowCompared)){
        return true;
        }
        }else{
        if(this.getMean(row) > this.getMean(rowCompared)){
        return true;
        }
        }
        }
        }
        return false;
        }

        private int getGroup(String identity) {
        try {
        String str = identity.substring(identity.indexOf("Group")+5, identity.indexOf(" [(Deuterium"));
        return Integer.valueOf(str);
        } catch (Exception e) {
        return -1;
        }
        }
         */
        private double getMean(PeakListRow row) {
                double mean = 0;
                Double[] peaks = (Double[]) row.getPeaks();
                for (Double concentration : peaks) {
                        mean += concentration;
                }
                return mean / peaks.length;
        }
}
