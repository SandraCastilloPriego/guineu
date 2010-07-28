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
package guineu.modules.mylly.alignment.ransacAligner.functions;

import guineu.data.PeakListRow;
import guineu.data.impl.peaklists.SimplePeakListRowGCGC;

/**
 * This class represents a score between peak list row and aligned peak list row
 */
public class RowVsRowScore implements Comparable<RowVsRowScore> {

        private SimplePeakListRowGCGC peakListRow, alignedRow;
        double score;
        private String errorMessage;

        public RowVsRowScore(PeakListRow peakListRow, PeakListRow alignedRow,
                double RT1MaxDiff, double RT2MaxDiff, double correctedRT1, double correctedRT2) throws Exception {


                this.alignedRow = (SimplePeakListRowGCGC) alignedRow;
                this.peakListRow = (SimplePeakListRowGCGC) peakListRow;

                // Calculate differences between m/z and RT values
                double RT1Diff = Math.abs(correctedRT1 - this.alignedRow.getRT1());
                double rtDiff = Math.abs(correctedRT2 - this.alignedRow.getRT2());

                score = ((1 - RT1Diff / RT1MaxDiff) + (1 - rtDiff / RT2MaxDiff));
        }

        /**
         * This method returns the peak list row which is being aligned
         */
        public PeakListRow getPeakListRow() {
                return peakListRow;
        }

        /**
         * This method returns the row of aligned peak list
         */
        public PeakListRow getAlignedRow() {
                return alignedRow;
        }

        /**
         * This method returns score between the these two peaks (the lower score,
         * the better match)
         */
        double getScore() {
                return score;
        }

        public String getErrorMessage() {
                return errorMessage;
        }

        /**
         * @see java.lang.Comparable#compareTo(java.lang.Object)
         */
        public int compareTo(RowVsRowScore object) {

                // We must never return 0, because the TreeSet in JoinAlignerTask would
                // treat such elements as equal
                if (score < object.getScore()) {
                        return 1;
                } else {
                        return -1;
                }

        }
}
